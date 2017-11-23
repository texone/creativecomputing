package cc.creativecomputing.control.code;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderFile extends CCShaderSource{

	private Path _myFile;
	
	public CCShaderFile(CCShaderObject theShaderObject, Path theFile, String theSource){
		super(theShaderObject, theSource);
		_myFile = theFile;
	}
	public void save(){
		Path myFixedPath = Paths.get("/");
		
		for(Path myName:_myFile){
			if(myName.toString().equals("target"))continue;
			if(myName.toString().equals("classes")){
				myFixedPath = myFixedPath.resolve("src/main/java");
				continue;
			}
			myFixedPath = myFixedPath.resolve(myName);
			
		}
		CCNIOUtil.saveString(myFixedPath, _mySourceCode);
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
