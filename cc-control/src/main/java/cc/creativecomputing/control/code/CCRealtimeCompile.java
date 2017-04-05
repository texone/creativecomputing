package cc.creativecomputing.control.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * check http://stackoverflow.com/questions/12173294/compile-code-fully-in-memory-with-javax-tools-javacompiler
 * @author maxg, christian riekoff
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class CCRealtimeCompile<CompileType> {

	private static class CCJavaFileObject extends SimpleJavaFileObject {

		private OutputStream _myOutputStream;
		private String _mySource = "";

		protected CCJavaFileObject(Path theTarget, Kind kind) {

			// target
			super(theTarget.toUri(), kind);
			
			_mySource = CCNIOUtil.loadString(theTarget);
			CCLog.info(_mySource);
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

	@CCProperty(name = "instance")
	private CompileType _myInstance;// = new ArrayList<>();
	private CCCompileParameters _myParameters;

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
		
		_mySourcePath = Paths.get("src/main/java", _myPackage.split("\\.")).resolve(_myClassName + ".java");
		_myTargetPath = Paths.get("compile");
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
	
	
	
	public CompileType instance(){
		return _myInstance;
	}
	
	public CompileType createObject(Object...theParameters){
		_myInstance = createObjectOnCompiledClass(theParameters);
		_myParameters = new CCCompileParameters(_myInstance, theParameters);
		return _myInstance;
	}
	
	private CompileType createObjectOnCompiledClass(Object...theParameters){
		if(_myCompiledClass == null){
			return null;
		}
		
		for(Constructor<?> myConstructor : _myCompiledClass.getConstructors()){
			try{
				CompileType myResult = (CompileType)myConstructor.newInstance(theParameters);
				return myResult;
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		try{
			CompileType myInstance = (CompileType)_myCompiledClass.newInstance();
			return myInstance;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private CompileType createObjectOnCompiledClass(CCCompileParameters theParameters){
		if(theParameters._myType == null){
			return createObjectOnCompiledClass();
		}
		return createObjectOnCompiledClass(theParameters._myParameters);
	}

	public Class<?> recompile() {
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
			CCLog.info(_myTargetPath);
			URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { _myTargetPath.toUri().toURL() });
			_myCompiledClass = classLoader.loadClass(_myClassPath);
			CCLog.info(_myCompiledClass.getResource(""));
			return _myCompiledClass;
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
			_myParameters._myType = createObjectOnCompiledClass(_myParameters);
			_myInstance = _myParameters._myType;
		
//		_myInstances = myNewInstances;
		recompile();
		
	}
	
	public static interface CCTest extends CCCompileObject{
		public float bla();
		public boolean blaBool();
		public void blaGen(List<String> theList);
	}
	
	public static void main(String[] args) {
		CCRealtimeCompile<CCTest> myCompile = new CCRealtimeCompile<CCRealtimeCompile.CCTest>("de.test.TestImp", CCTest.class);
	}

	public String codeTemplate() {
		// TODO Auto-generated method stub
		return new CCTemplateGenerator().codeTemplate(_myBaseClass, _myClassPath);
	}
}
