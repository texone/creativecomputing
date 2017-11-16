package cc.creativecomputing.control.code;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderObject {
	
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param thePaths
	 * @return
	 */
	public static String[] buildSource(final Path...thePaths) {
		String[] myBuffer = new String[thePaths.length];
		int i = 0;
		for(Path myPath:thePaths) {
			myBuffer[i] = CCNIOUtil.loadString(myPath);
		}
		
		return myBuffer;
	}
	
	public static abstract class CCShaderObjectInterface{
		
		public abstract void update();
		
		public abstract String errorLog();
		
		public String[] keywords(){
			return new String[]{};
		}
		public Path[] templates(){
			return new Path[]{};
		}

		public String templateSource(Path thePath) {
			return "";
		}
	}
	
	private CCListenerManager<CCShaderCompileListener> _myEvents = CCListenerManager.create(CCShaderCompileListener.class);
	
	private final CCShaderObjectInterface _myInterface;
	
	public static interface CCShaderCompileListener{
		public void onRecompile(CCShaderObject theCompiler);
	}
	
	public CCListenerManager<CCShaderCompileListener> events(){
		return _myEvents;
	}
	
	private Path[] _myPaths;
	
	@CCProperty(name = "shaders", hide = true)
	private Map<String, CCShaderFile> _myFiles = new LinkedHashMap<>();
	@CCProperty(name = "save in file")
	private boolean _cSaveInFile = true;
	@CCProperty(name = "auto save")
	private boolean _cAutoSave = true;
	
	public CCShaderObject(CCShaderObjectInterface theInterface, Path[] theFiles){
		_myPaths = theFiles;
		_myInterface = theInterface;
		String[] mySources = buildSource(theFiles);
		for(int i = 0; i < _myPaths.length;i++){
			_myFiles.put(_myPaths[i].getFileName().toString(), new CCShaderFile(this, _myPaths[i], mySources[i]));
		}
	}
	
	public boolean saveInFile(){
		return _cSaveInFile;
	}
	
	public boolean autoSave(){
		return _cAutoSave;
	}
	
	public void update(){
		_myInterface.update();
	}
	
	public String[] keywords(){
		return _myInterface.keywords();
	}
	
	public Path[] templates(){
		return _myInterface.templates();
	}
	
	public Path[] paths(){
		return _myPaths;
	}
	
	public Map<String, CCShaderFile> sourceCode(){
		return _myFiles;
	}
	
	public String errorLog(){
		return _myInterface.errorLog();
	}

	public String templateSource(Path thePath) {
		return _myInterface.templateSource(thePath);
	}
}
