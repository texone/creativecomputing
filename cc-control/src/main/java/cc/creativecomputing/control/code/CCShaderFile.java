package cc.creativecomputing.control.code;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderFile {

	private String _mySource;
	private Path _myFile;
	
	private CCShaderObject _myShaderObject;
	
	public CCShaderFile(CCShaderObject theShaderObject, Path theFile, String theSource){
		_myShaderObject = theShaderObject;
		_mySource = theSource;
		_myFile = theFile;
	}
	
	public CCShaderObject object(){
		return _myShaderObject;
	}
	
	public String source(){
		return _mySource;
	}
	
	public void source(String theSource){
		_mySource = theSource;
		_myShaderObject.update();
	}

	public String errorLog() {
		return _myShaderObject.errorLog();
	}
	
	public void save(){
		Path myFixedPath = Paths.get("/");
		
		boolean myDoResolve = false;
		for(Path myName:_myFile){
			if(myName.toString().equals("target"))continue;
			if(myName.toString().equals("classes")){
				myFixedPath = myFixedPath.resolve("src/main/java");
				continue;
			}
			myFixedPath = myFixedPath.resolve(myName);
			
		}
		CCLog.info(myFixedPath);
		CCNIOUtil.saveString(myFixedPath, _mySource);
	}
	
	public static void main(String[] args) {
		Path myPath = CCNIOUtil.classPath(CCShaderFile.class, "");
		Path myFixedPath = Paths.get("/");
		for(Path myName:myPath){
			if(myName.toString().equals("target"))continue;
			if(myName.toString().contains("classes")){
				myFixedPath = myFixedPath.resolve("src/main/java");
				continue;
			}
			myFixedPath = myFixedPath.resolve(myName);
			
		}
		
		CCLog.info(myPath);
		CCLog.info(myFixedPath);

		CCNIOUtil.saveString(myFixedPath.resolve("bla.txt"), "bla");
	}
}
