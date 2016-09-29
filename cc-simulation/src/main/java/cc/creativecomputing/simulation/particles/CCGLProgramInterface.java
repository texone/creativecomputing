package cc.creativecomputing.simulation.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;

@SuppressWarnings("unused")
public class CCGLProgramInterface {
	
	private String _myFunctionName;
	private String _mySource;
	private List<String> _myUniforms;
	
	protected CCGLProgram _myShader;
	
	static int id = 0;
	
	private final String _myInterfaceAppend;
	
	public CCGLProgramInterface(String theFunctionName){
		_myFunctionName = theFunctionName;
		_myInterfaceAppend = theFunctionName + id++;
		
		List<String> mySource = CCNIOUtil.loadStrings(CCNIOUtil.classPath(this, getClass().getSimpleName() + ".glsl"));
		List<String> myUniforms = new ArrayList<>();
		StringBuffer mySourceBuffer = new StringBuffer();
		for(String myLine:mySource){
			if(myLine.startsWith("uniform")){
				String[] myParts = myLine.split(Pattern.quote(" "));
				mySourceBuffer.append("uniform ");
				mySourceBuffer.append(myParts[1]);
				mySourceBuffer.append(" ");
				mySourceBuffer.append(parameter(myParts[2]));
				myUniforms.add(myParts[2].replace(";", ""));
				mySourceBuffer.append("\n");
			}else if(myLine.contains("function")){
				myLine = myLine.replace("function", parameter("function"));
				mySourceBuffer.append(myLine);
				mySourceBuffer.append("\n");
			}else{
				for(String myUniform:myUniforms){
					myLine = myLine.replace(myUniform, parameter(myUniform));
				}
				mySourceBuffer.append(myLine);
				mySourceBuffer.append("\n");
			}
		}
		mySourceBuffer.append("\n");
		_mySource = mySourceBuffer.toString();
	}
	
	public void setShader(CCGLProgram theProgram){
		_myShader = theProgram;
	}
	
	public void preDisplay(CCGraphics g){
		
	}
	
	public void setUniforms(){
		
	}
	
	public String parameter(String theUniform){
		return _myInterfaceAppend + "_" + theUniform;
	}
	
	public String shaderSource(){
		return _mySource;
	}
}
