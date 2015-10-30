package cc.creativecomputing.control.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * 
 * @author maxg, christian riekoff
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CCRealtimeCompile<CompileType extends CCCompileObject> {

	private static class CCJavaFileObject extends SimpleJavaFileObject {

		private OutputStream _myOutputStream;
		private String _mySource = "";

		protected CCJavaFileObject(Path theTarget, Kind kind) {

			// target
			super(theTarget.toUri(), kind);
			
			_mySource = CCNIOUtil.loadString(theTarget);
			_myOutputStream = new ByteArrayOutputStream();
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return _mySource;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return _myOutputStream;
		}
	}
	
	public static interface CCRealtimeCompileListener<CompileType extends CCCompileObject>{
		public void onRecompile(CCRealtimeCompile<CompileType> theCompiler);
	}
	
	public static String classPath(Class<?> theBaseClass){
		return theBaseClass.getPackage().getName() + "." + theBaseClass.getSimpleName() + "Imp";
	}
	
	private class CCCompileParameters{
		private CompileType _myType;
		private final Object[] _myParameters;
		
		private CCCompileParameters(CompileType theType, Object[] theParameters){
			_myType = theType;
			_myParameters = theParameters;
		}
	}

	private List<CompileType> _myInstances = new ArrayList<>();
	private List<CCCompileParameters> _myParameters = new ArrayList<>();

	private String _mySource = null;

	private Path _mySourcePath;
	private Path _myTargetPath;
	private String _myClassPath;
	
	private String _myPackage;
	private String _myClassName;
	
	private Class<?> _myCompiledClass;
	private Class<CompileType> _myBaseClass;
	
	private JavaCompiler compiler;
	private StandardJavaFileManager fileManager;
	private final List<Diagnostic> _myDiagnostics = new ArrayList<>();
	
	private CCListenerManager<CCRealtimeCompileListener> _myEvents = CCListenerManager.create(CCRealtimeCompileListener.class);

	public CCRealtimeCompile(String theClassPath, Class<CompileType> theBaseClass) {
		_myClassPath = theClassPath;
		_myPackage = theClassPath.substring(0, theClassPath.lastIndexOf("."));
		_myClassName = theClassPath.substring(theClassPath.lastIndexOf(".") + 1);
		_myBaseClass = theBaseClass;
		
		_mySourcePath = Paths.get("realtimeCode", _myPackage.split("\\.")).resolve(_myClassName + ".java");
		_myTargetPath = _mySourcePath.getParent();
		CCNIOUtil.createDirectories(_myTargetPath);
		
		try {
		// init toolchain
			compiler = ToolProvider.getSystemJavaCompiler();
			fileManager = compiler.getStandardFileManager(null, null, null);
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(_myTargetPath.toFile()));
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CCRealtimeCompile(Class<CompileType> theBaseClass){
		this(classPath(theBaseClass), theBaseClass);
	}
	
	public List<Diagnostic> diagnostics(){
		return _myDiagnostics;
	}
	
	public String packageName(){
		return _myPackage;
	}
	
	public String className(){
		return _myClassName;
	}
	
	private void appendImport(StringBuffer theBuffer, Class<?> theClass){
		if(theClass == null)return;
		if(theClass.isPrimitive())return;
		
		if(theClass.isArray()){
			theBuffer.append("import " + theClass.getComponentType().getName().replaceAll("\\$", ".") + ";\n");
		}else{
			theBuffer.append("import " + theClass.getName().replaceAll("\\$", ".") + ";\n");
		}
	}
	
	private void appendReturn(StringBuffer theBuffer, Class<?> theClass){
		if(theClass != null){
			if(!theClass.isPrimitive()){
				theBuffer.append("\t\treturn null;\n");
			}else if(theClass == Float.TYPE){
				theBuffer.append("\t\treturn 0f;\n");
			}else if(theClass == Double.TYPE){
				theBuffer.append("\t\treturn 0.0;\n");
			}else if(theClass == Integer.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Long.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Short.TYPE){
				theBuffer.append("\t\treturn 0;\n");
			}else if(theClass == Boolean.TYPE){
				theBuffer.append("\t\treturn false;\n");
			}
		}else{
			theBuffer.append("\t\t\n");
		}
	}
	
	public String codeTemplate(){
		StringBuffer myTemplateBuffer = new StringBuffer();
		myTemplateBuffer.append("package " + _myPackage + ";\n");
		myTemplateBuffer.append("\n");
		
		StringBuffer myImportBuffer = new StringBuffer();
		myImportBuffer.append("import " + _myBaseClass.getName().replaceAll("\\$", ".") + ";\n");
		
		StringBuffer myCodeBuffer = new StringBuffer();
		if(_myBaseClass.isInterface()){
			myCodeBuffer.append("public class " + _myClassName + " implements " + _myBaseClass.getSimpleName() +"{");
			myCodeBuffer.append("\n");
			myCodeBuffer.append("\n");
			
			for(Method myMethod:_myBaseClass.getMethods()){
				
				myCodeBuffer.append("\tpublic ");
				if(myMethod.getReturnType() != null){
					myCodeBuffer.append(myMethod.getReturnType().getSimpleName());
				}else{
					myCodeBuffer.append("void");
				}
				myCodeBuffer.append(" " + myMethod.getName()+"(");
				for(Class<?> theClass:myMethod.getParameterTypes()){
					myCodeBuffer.append(theClass.getSimpleName() + " the" + theClass.getSimpleName()+", ");
					appendImport(myImportBuffer, theClass);
				}
				appendImport(myImportBuffer, myMethod.getReturnType());
				
				if(myMethod.getParameterTypes().length > 0)myCodeBuffer.delete(myCodeBuffer.length() - 2, myCodeBuffer.length());
				myCodeBuffer.append("){\n");
				
				appendReturn(myCodeBuffer, myMethod.getReturnType());
				
				myCodeBuffer.append("\t}\n");
				myCodeBuffer.append("\n");
			}
			myCodeBuffer.append("\n");
		}else{
			myCodeBuffer.append("public class " + _myClassName + " extends " + _myBaseClass.getSimpleName() +"{");
		}
		myCodeBuffer.append("");
		myCodeBuffer.append("}");
		
		myTemplateBuffer.append(myImportBuffer);

		myTemplateBuffer.append("\n");
		
		myTemplateBuffer.append(myCodeBuffer);
		
		return myTemplateBuffer.toString();
	}

	// check file change in seperate thread, recompile in main thread
	public boolean hasCodeUpdate() {
		try{
			String mySource = CCNIOUtil.loadString(_mySourcePath);
	
			boolean myResult = !mySource.equals(_mySource) || _mySource == null;
			
			_mySource = mySource;
	
			return myResult;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public List<CompileType> instances(){
		return _myInstances;
	}
	
	public CompileType instance(){
		if(_myInstances.size() <= 0)return null;
		return _myInstances.get(0);
	}
	
	public CompileType createObject(Object...theParameters){
		CompileType myResult = createObjectOnCompiledClass(theParameters);
		_myInstances.add(myResult);
		_myParameters.add(new CCCompileParameters(myResult, theParameters));
		return myResult;
	}
	
	private CompileType createObjectOnCompiledClass(Object...theParameters){
		if(_myCompiledClass == null){
			return null;
		}
		
		for(Constructor<?> myConstructor : _myCompiledClass.getConstructors()){
			try{
				CompileType myResult = (CompileType)myConstructor.newInstance(theParameters);
				myResult.onRecompile();
				return myResult;
			}catch(Exception e){
			}
		}
		try{
			CompileType myInstance = (CompileType)_myCompiledClass.newInstance();
			myInstance.onRecompile();
			return myInstance;
		}catch(Exception e){
		}
		return null;
	}
	
	private CompileType createObjectOnCompiledClass(CCCompileParameters theParameters){
		if(theParameters._myType == null){
			return createObjectOnCompiledClass();
		}
		return createObjectOnCompiledClass(theParameters._myParameters);
	}
	
	public void removeObject(Object theObject){
		_myInstances.remove(theObject);
	}

	private Class<?> recompile() {
		try {
			// prepare compiler

			// load file as custom file object

			CCJavaFileObject fileObject = new CCJavaFileObject(_mySourcePath, Kind.SOURCE);
			List<CCJavaFileObject> compilationObjects = new ArrayList<>();
			compilationObjects.add(fileObject);
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			
			// compile todo: check if error
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, new ArrayList<>(), null, compilationObjects);
			task.call();
			
			_myDiagnostics.clear();
			_myDiagnostics.addAll(diagnostics.getDiagnostics());
			
			_myEvents.proxy().onRecompile(this);

			URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { _myTargetPath.toUri().toURL() });
			return Class.forName(_myClassPath, false, classLoader);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public CCListenerManager<CCRealtimeCompileListener> events(){
		return _myEvents;
	}

	public String sourceCode() {
		return _mySource;
	}

	public void sourceCode(String theSource) {
		_mySource = theSource;
		CCNIOUtil.saveString(_mySourcePath, _mySource);
		_myCompiledClass = recompile();
		List<CompileType> myNewInstances = new ArrayList<>();
		for (CCCompileParameters myOldInstance : _myParameters) {
			myOldInstance._myType = createObjectOnCompiledClass(myOldInstance);
			myNewInstances.add(myOldInstance._myType);
		}
		_myInstances = myNewInstances;
		recompile();
		
	}
	
	public static interface CCTest extends CCCompileObject{
		public float bla();
		public boolean blaBool();
		public void blaGen(List<String> theList);
	}
	
	public static void main(String[] args) {
		CCRealtimeCompile<CCTest> myCompile = new CCRealtimeCompile<CCRealtimeCompile.CCTest>("de.test.TestImp", CCTest.class);
		
		System.out.println(myCompile.codeTemplate());
	}
}
