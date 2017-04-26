/*
 * Copyright 2014 Higher Frequency Trading
 *
 * http://www.higherfrequencytrading.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.creativecomputing.control.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class CCRuntimeCompiler implements Closeable {

	/*
	 * A file object used to represent source coming from a string.
	 */
	private static class JavaSourceFromString extends SimpleJavaFileObject {
		/**
		 * The source code of this "file".
		 */
		private String _myCode;

		/**
		 * Constructs a new JavaSourceFromString.
		 *
		 * @param theName the name of the compilation unit represented by this file
		 *            object
		 */
		JavaSourceFromString(String theName) {
			super(URI.create("string:///" + theName.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			_myCode = "";
		}

		@Override
		public CharSequence getCharContent(boolean theIgnoreEncodingErrors) {
			return _myCode;
		}
	}

	private static class CCJavaFileManager implements JavaFileManager {
		private final StandardJavaFileManager _myfileManager;
		private final Map<String, ByteArrayOutputStream> _myBuffers = new LinkedHashMap<String, ByteArrayOutputStream>();

		CCJavaFileManager(StandardJavaFileManager fileManager) {
			_myfileManager = fileManager;
		}

		@Override
		public ClassLoader getClassLoader(Location theLocation) {
			return _myfileManager.getClassLoader(theLocation);
		}

		@Override
		public Iterable<JavaFileObject> list(Location theLocation, String thePackageName, Set<Kind> theKinds,
				boolean theRecurse) throws IOException {
			return _myfileManager.list(theLocation, thePackageName, theKinds, theRecurse);
		}

		@Override
		public String inferBinaryName(Location theLocation, JavaFileObject theFile) {
			return _myfileManager.inferBinaryName(theLocation, theFile);
		}

		@Override
		public boolean isSameFile(FileObject a, FileObject b) {
			return _myfileManager.isSameFile(a, b);
		}

		@Override
		public boolean handleOption(String theCurrent, Iterator<String> theRemaining) {
			return _myfileManager.handleOption(theCurrent, theRemaining);
		}

		@Override
		public boolean hasLocation(Location theLocation) {
			return _myfileManager.hasLocation(theLocation);
		}

		@Override
		public JavaFileObject getJavaFileForInput(Location theLocation, String theClassName, Kind theKind) throws IOException {
			if (theLocation == StandardLocation.CLASS_OUTPUT && _myBuffers.containsKey(theClassName) && theKind == Kind.CLASS) {
				final byte[] bytes = _myBuffers.get(theClassName).toByteArray();
				return new SimpleJavaFileObject(URI.create(theClassName), theKind) {
					public InputStream openInputStream() {
						return new ByteArrayInputStream(bytes);
					}
				};
			}
			return _myfileManager.getJavaFileForInput(theLocation, theClassName, theKind);
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location theLocation, final String theClassName, Kind theKind, FileObject theSibling) throws IOException {
			return new SimpleJavaFileObject(URI.create(theClassName), theKind) {

				public OutputStream openOutputStream() {
					CCLog.info(theClassName);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					_myBuffers.put(theClassName, baos);
					return baos;
				}
			};
		}

		@Override
		public FileObject getFileForInput(Location theLocation, String thePackageName, String theRelativeName)throws IOException {
			return _myfileManager.getFileForInput(theLocation, thePackageName, theRelativeName);
		}

		@Override
		public FileObject getFileForOutput(Location theLocation, String thePackageName, String theRelativeName, FileObject theSibling) throws IOException {
			return _myfileManager.getFileForOutput(theLocation, thePackageName, theRelativeName, theSibling);
		}

		@Override
		public void flush() throws IOException {
			// Do nothing
		}

		@Override
		public void close() throws IOException {
			_myfileManager.close();
		}

		@Override
		public int isSupportedOption(String theOption) {
			return _myfileManager.isSupportedOption(theOption);
		}

		@SuppressWarnings("unused")
		public void clearBuffers() {
			_myBuffers.clear();
		}

		public Map<String, byte[]> getAllBuffers() {
			Map<String, byte[]> ret = new LinkedHashMap<String, byte[]>(_myBuffers.size() * 2);
			for (Map.Entry<String, ByteArrayOutputStream> entry : _myBuffers.entrySet()) {
				ret.put(entry.getKey(), entry.getValue().toByteArray());
			}
			return ret;
		}
	}

	private static final Method DEFINE_CLASS_METHOD;

	static {
		try {
			DEFINE_CLASS_METHOD = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			DEFINE_CLASS_METHOD.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}
	
	public class CCClassLoader{
		private final CCJavaFileManager _myFileManager;
		private final String _myClassPath;
		private final String _myPackage;
		private final String _myClassName;
		private final Path _mySourcePath;
		
		private Class<?> _myClass;
		private long _myLastModified = 0;
		private JavaSourceFromString _mySource;
		
		private CCClassLoader(String theClassName){
			_myClassPath = theClassName;
			_mySource = new JavaSourceFromString(theClassName);
			_myFileManager = new CCJavaFileManager(_myCompiler.getStandardFileManager(null, null, null));
			

			_myPackage = _myClassPath.substring(0, _myClassPath.lastIndexOf("."));
			_myClassName = _myClassPath.substring(_myClassPath.lastIndexOf(".") + 1);
			_mySourcePath = Paths.get("src/main/java", _myPackage.split("\\.")).resolve(_myClassName + ".java");
			
			if(CCNIOUtil.exists(_mySourcePath)){
				_myLastModified = CCNIOUtil.lastModified(_mySourcePath);
			}
		}
		
		public void close(){
			try {
				_myFileManager.close();
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		}
		
		public Class<?> currentClass(){
			return _myClass;
		}

		/**
		 * Define a class for byte code.
		 *
		 * @param theClassLoader to load the class into.
		 * @param theClassName expected to load.
		 * @param bytes of the byte code.
		 */
		private Class<?> defineClass(ClassLoader theLoader, byte[] bytes) {
			try {
				return (Class<?>) DEFINE_CLASS_METHOD.invoke(theLoader, _myClassPath, bytes, 0, bytes.length);
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			} catch (InvocationTargetException e) {
				throw new AssertionError(e.getCause());
			}
		}

		public Class<?> compileFromSource(String theSourceCode) {
			if(_mySource._myCode.equals(theSourceCode)){
				return _myClass;
			}
			CCLog.info("recompile");
			_mySource._myCode = theSourceCode;
			_myFileObjectsMap.put(_myClassPath, _mySource);

			// reuse the same file manager to allow caching of jar files
			boolean myHasCompiled = _myCompiler.getTask(
				_myWriter, 
				_myFileManager, 
				_myDiagnosticListener, 
				null, 
				null, 
				_myFileObjectsMap.values()
			).call();
			
			Map<String, byte[]> myClassByteMap = _myFileManager.getAllBuffers();
			
			if (!myHasCompiled) {
				// compilation error, so we want to exclude this file from future
				// compilation passes
				_myFileObjectsMap.remove(_myClassPath);

				// nothing to return due to compiler error
				myClassByteMap =  Collections.emptyMap();
			}
			
			ClassLoader myClassLoader = new ClassLoader(){};
			
			for (Map.Entry<String, byte[]> myEntry : myClassByteMap.entrySet()) {
				String myClassName = myEntry.getKey();
				
				byte[] myBytes = myEntry.getValue();
				CCLog.info(myClassName);
				if(!myClassName.equals(_myClassName)){
					
				}else{
					defineClass(myClassLoader, myBytes);
				}
				
			}
			try{
				_myClass = myClassLoader.loadClass(_myClassPath);
				return _myClass;
			}catch(ClassNotFoundException e){
				throw new RuntimeException(e);
			}
		}
		
		public boolean isUpdated(){
			return CCNIOUtil.lastModified(_mySourcePath) > _myLastModified;
		}
		
		public Class<?> recompile(){
	        String mySource = CCNIOUtil.loadString(_mySourcePath);
	        _myLastModified = CCNIOUtil.lastModified(_mySourcePath);
	        return compileFromSource(mySource);
		}
	}
	
	private final Map<String, CCClassLoader> _myLoaderMap = Collections.synchronizedMap(new WeakHashMap<>());
	private final Map<String, JavaFileObject> _myFileObjectsMap = new HashMap<>();
	

	private JavaCompiler _myCompiler;
	private final PrintWriter _myWriter;
	private DiagnosticListener<JavaFileObject> _myDiagnosticListener;
	
	private CCRuntimeCompiler() {
		_myWriter = new PrintWriter(System.err);
		_myDiagnosticListener = new DiagnosticListener<JavaFileObject>() {
			@Override
			public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
				if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
					_myWriter.println(diagnostic);
				}
			}
		};
		
		_myCompiler = ToolProvider.getSystemJavaCompiler();
		if (_myCompiler != null)
			return;

		try {
			Class<?> javacTool = Class.forName("com.sun.tools.javac.api.JavacTool");
			Method create = javacTool.getMethod("create");
			_myCompiler = (JavaCompiler) create.invoke(null);
		} catch (Exception e) {
			throw new AssertionError(e);
		}

	}

	private static CCRuntimeCompiler instance;
	
	private static CCRuntimeCompiler instance(){
		if(instance == null){
			instance = new CCRuntimeCompiler();
		}
		return instance;
	}

	public void close() {
		for (CCClassLoader myLoader : _myLoaderMap.values()) {
			myLoader.close();
		}
	}
	
	private CCClassLoader loader(String theClassName){
		synchronized (_myLoaderMap) {
			CCClassLoader myResult = _myLoaderMap.get(theClassName);
			if (myResult != null) return myResult;
			
			myResult = new CCClassLoader(theClassName);
			_myLoaderMap.put(theClassName, myResult);
			return myResult;
		}
	}


	/**
	 * Compile a java class from text.
	 *
	 * @param theClassName expected class name of the outer class.
	 * @param theSourceCode to compile and load.
	 * @return the outer class loaded.
	 * @throws RuntimeException the class name didn't match or failed to
	 *             initialize.
	 */
	public static Class<?> compileFromSource(String theClassName, String theSourceCode) {
		
		CCClassLoader myLoader = instance().loader(theClassName);
//		Class<?> myClass = myLoader.getLoadedClass(theClassName);
//		if (myClass != null)
//			return myClass;
		
		return myLoader.compileFromSource(theSourceCode);
	}

	/**
	 * Load a java class file from source.
	 *
	 * @param theClassName expected class name of the outer class.
	 * @param theSourceCode to compile and load
	 * @return the outer class loaded.
	 * @throws IOException the resource could not be loaded.
	 * @throws RuntimeException the class name didn't match or failed to
	 *             initialize.
	 */
	public static Class<?> loadFromJava(String theClassName, String theSourceCode) {
		return compileFromSource(theClassName, theSourceCode);
	}
	
	public static CCClassLoader loader(Class<?> theClass){
		return instance().loader(theClass.getName());
	}

	public static Class<?> compileFromSource(Class<?> theClass){
		CCClassLoader myLoader = instance().loader(theClass.getName());
        return myLoader.recompile();
	}
	
	public static <Type> Type recompile(Type theObject){
		Class mClass = compileFromSource(theObject.getClass());
		try {
			return (Type)mClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
