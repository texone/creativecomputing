package cc.creativecomputing.graphics.shader;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.jogamp.opengl.GL2;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.control.code.CCShaderObject.CCShaderObjectInterface;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCPropertyObject;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCDirectMember;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram.CCShaderObjectType;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCNIOUtil;

public class CCGLShader extends CCShaderObjectInterface{
	
	/**
	 * Takes the given files and merges them to one String. 
	 * This method is used to combine the different shader sources and get rid of the includes
	 * inside the shader files.
	 * @param thePaths
	 * @return
	 */
	public static String buildSource(final Path...thePaths) {
		StringBuffer myBuffer = new StringBuffer();
		for(Path myPath:thePaths) {
			myBuffer.append(CCNIOUtil.loadString(myPath));
			myBuffer.append("\n");
		}
		
		return myBuffer.toString();
	}
	
	public static CCShaderSource buildSourceObject(final Path...thePaths) {
		CCShaderSource mySource = new CCShaderSource();
		
		for(Path myPath:thePaths) {
			for(String myLine:CCNIOUtil.loadStrings(myPath)){
				mySource.addLine(myLine);
			}
		}
		
		return mySource;
	}
	
	protected CCShaderObjectType _myType;
	protected Path[] _myFiles;
	
	private String _mySource;
	
	private String _myReloadSourceCode = null;
	
	private boolean _myReloadSource = false;
	
	@CCProperty(name = "code")
	private CCShaderObject _myCode;
	@CCProperty(name = "uniforms")
	private CCObjectPropertyHandle _myUniformHandles = new CCObjectPropertyHandle(new CCDirectMember( new CCPropertyObject("uniforms", 0, 0)));
	
	CCGLShader(CCShaderObjectType theType, Path...theFiles){
		_myType = theType;
		_myFiles = theFiles;
		
		GL2 gl = CCGraphics.currentGL();
		_myShaderID = (int)gl.glCreateShader(_myType.glID);
		_myCode = new CCShaderObject(this);
		loadShader(_myFiles);
	}
	
	CCGLShader(CCShaderObjectType theType, String theSource){
		_myType = theType;
		GL2 gl = CCGraphics.currentGL();
		_myShaderID = (int)gl.glCreateShader(_myType.glID);
		
		loadShader(theSource, true, "");
	}

	@Override
	public String errorLog() {
		return _myInfoLog;
	}
	
	/**
	 * returns the value of a parameter.
	 * <p>
	 * The following parameters are defined:
	 * <ul>
	 * <li><code>GL_SHADER_TYPE</code> returns <code>GL_VERTEX_SHADER</code> if shader is a vertex shader object, and <code>GL_FRAGMENT_SHADER</code> if shader is a fragment shader object.</li>
	 * <li><code>GL_DELETE_STATUS</code> returns <code>GL_TRUE</code> if shader is currently flagged for deletion, and <code>GL_FALSE</code> otherwise.</li>
	 * <li><code>GL_COMPILE_STATUS</code> returns <code>GL_TRUE</code> if the last compile operation on shader was successful, and <code>GL_FALSE</code> otherwise.</li>
	 * <li><code>GL_INFO_LOG_LENGTH</code> returns the number of characters in the information log for shader including the null termination character (i.e., the size of the character buffer required to store the information log). If shader has no information log, a value of 0 is returned.</li>
	 * <li><code>GL_SHADER_SOURCE_LENGTH</code> returns the length of the concatenation of the source strings that make up the shader source for the shader, including the null termination character. (i.e., the size of the character buffer required to store the shader source). If no source code exists, 0 is returned.</li>
	 * </ul>
	 * @param theParameter
	 * @return the value of a parameter.
	 */
	public int get(int theParameter){
		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetShaderiv(_myShaderID, theParameter,iVal);
		return  iVal.get();
	}
	
	/**
	 * Returns the number of characters in the information log for shader including the 
	 * null termination character (i.e., the size of the character buffer required to 
	 * store the information log). If shader has no information log, a value of 0 is returned.
	 * @return the number of characters in the information log
	 */
	public int infoLogLength(){
		return get(GL2.GL_INFO_LOG_LENGTH);
	}
	
	/**
	 * Returns <code>true</code> if the last compile operation on shader was successful, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean compileStatus(){
		return get(GL2.GL_COMPILE_STATUS) == GL2.GL_TRUE;
	}
	
	/**
	 * Returns <code>true</code> if shader is currently flagged for deletion, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean deleteStatus(){
		return get(GL2.GL_DELETE_STATUS) == GL2.GL_TRUE;
	}
	
	/**
	 * Returns the information log for the specified shader object. The information log 
	 * for a shader object is modified when the shader is compiled. The string that is 
	 * returned will be null terminated.
	 * @param gl
	 * @param theObject
	 * @param theFiles
	 * @return
	 */
	public String getInfoLog() {

		int length = infoLogLength();
		if (infoLogLength() <= 0) {
			return null;
		}

		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);
		iVal.put(length);
		iVal.flip();
		
		ByteBuffer infoLog = CCBufferUtil.newByteBuffer(length);
		
		GL2 gl = CCGraphics.currentGL();
		gl.glGetShaderInfoLog(_myShaderID, length, iVal, infoLog);
		byte[] infoBytes = new byte[length];
		infoLog.get(infoBytes);
		String myReply = new String(infoBytes);
		if(myReply.startsWith("WARNING:"))return null;
		
		StringBuffer myReplyBuffer = new StringBuffer();
		myReplyBuffer.append("\n");
		myReplyBuffer.append("The following Problem occured:\n");
		myReplyBuffer.append(myReply);
		myReplyBuffer.append("\n");
		return myReplyBuffer.toString();
	}
	
	/**
	 * Sets the source code in shader to the source code in the array of strings specified by string. 
	 * Any source code previously stored in the shader object is completely replaced.
	 * The source code strings are not scanned or parsed at this time; they are simply copied into 
	 * the specified shader object.
	 * @param theSource string containing the source code to be loaded into the shader
	 */
	public void source(String theSource){
		GL2 gl = CCGraphics.currentGL();
		gl.glShaderSource(_myShaderID, 1, new String[] { theSource },(int[]) null, 0);
	}
	
	/**
	 * compiles the source code strings that have been stored in the shader object specified by shader.
	 * The compilation status will be stored as part of the shader object's state. This value will be 
	 * set to <code>true</code> if the shader was compiled without errors and is ready for use, and 
	 * <code>false</code> otherwise. It can be queried by calling {@linkplain #compileStatus()}.
	 * <p>
	 * Compilation of a shader can fail for a number of reasons as specified by the OpenGL ES 
	 * Shading Language Specification. Whether or not the compilation was successful, 
	 * information about the compilation can be obtained from the shader object's information log 
	 * by calling {@linkplain #getInfoLog()}.
	 */
	public void compile(){
		GL2 gl = CCGraphics.currentGL();
		gl.glCompileShader(_myShaderID);
	}
	
	/**
	 * Frees the memory and invalidates the name associated with the shader object specified by shader.
	 * If a shader object to be deleted is attached to a program, it will be flagged for deletion, but 
	 * it will not be deleted until it is no longer attached to any program object, for any rendering 
	 * context (i.e., it must be detached from wherever it was attached before it will be deleted). 
	 * A value of 0 for shader will be silently ignored.
	 * <p>
	 * To determine whether an object has been flagged for deletion, call {@linkplain #deleteStatus()}
	 */
	public void delete(){
		GL2 gl = CCGraphics.currentGL();
		gl.glDeleteShader(_myShaderID);
	}
	
	private boolean loadShader(final Path...theFiles) {
		if(theFiles == null || theFiles.length <= 0)return false;
		String shaderSource = buildSource(theFiles);
		StringBuffer myReplyBuffer = new StringBuffer();
		if(theFiles != null){
			myReplyBuffer.append("Problem inside the following " + _myType + " shader:");
			for(Path myFile:theFiles) {
				myReplyBuffer.append("\n");
				myReplyBuffer.append(myFile);
			}
		}
		return loadShader(shaderSource, true, myReplyBuffer.toString());
	}
	
	
	private String _myInfoLog;
	
	int _myShaderID = -1;
	
	private CCNumberPropertyHandle<Double> createHandle(String theName, double theMin, double theMax){
		CCNumberPropertyHandle<Double> myResult = new CCNumberPropertyHandle<Double>(
			_myUniformHandles, 
			new CCDirectMember<CCProperty>(new Double(0), new CCPropertyObject(theName, theMin, theMax)),
			CCPropertyMap.doubleConverter
		);
		_myUniformHandles.children().put(theName, myResult);
		return myResult;
	}
	
	public class CCShaderUniform{
		String _myUniformName;
		CCNumberPropertyHandle<Double>[] _myProperties;
		
		private CCShaderUniform(String theUniformName, CCNumberPropertyHandle<Double>...theProperties){
			_myUniformName = theUniformName;
			_myProperties = theProperties;
		}
		
		public void apply(CCGLProgram theProgram){
			switch(_myProperties.length){
			case 1:
				theProgram.uniform1f(
					_myUniformName, 
					_myProperties[0].value().doubleValue()
				);
				break;
			case 2:
				theProgram.uniform2f(
					_myUniformName, 
					_myProperties[0].value().doubleValue(), 
					_myProperties[1].value().doubleValue()
				);
				break;
			case 3:
				theProgram.uniform3f(
					_myUniformName, 
					_myProperties[0].value().doubleValue(), 
					_myProperties[1].value().doubleValue(), 
					_myProperties[2].value().doubleValue()
				);
				break;
			case 4:
				theProgram.uniform4f(
					_myUniformName, 
					_myProperties[0].value().doubleValue(), 
					_myProperties[1].value().doubleValue(), 
					_myProperties[2].value().doubleValue(), 
					_myProperties[3].value().doubleValue()
				);
				break;
			}
		}
	}
	
	private Map<String,CCShaderUniform> _myUniforms = new HashMap<>();
	
	void applyUniforms(CCGLProgram theProgram){
		for(CCShaderUniform myUniform:_myUniforms.values()){
			myUniform.apply(theProgram);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readProperty(String thePropertyLine, String theUniformLine, Map<String,CCShaderUniform> theUniforms){
		CCLog.info(_myUniforms.size());
		
		for(String myKey:_myUniforms.keySet()){
			CCLog.info(myKey);
		}
		try{
			String myName = null;
			double myMin = -1;
			double myMax = -1;
			int myStartIndex = thePropertyLine.indexOf("(");
			int myEndIndex =  thePropertyLine.indexOf(")");
			if(myStartIndex == -1 || myEndIndex == -1)return;
			String[] myParameters = thePropertyLine.substring(myStartIndex + 1, myEndIndex).split(",");
			for(String myParameter:myParameters){
				if(!myParameter.contains("="))continue;
				String[] myParamPair = myParameter.split("=");
				if(myParamPair.length < 2)continue;
				String myKey = myParamPair[0].trim();
				String myValue = myParamPair[1].trim();
				
				switch(myKey){
				case "name":
					myName = myValue.replace("\"", "");
					break;
				case "min":
					try{
						myMin = Double.parseDouble(myValue);
						break;
					}catch(Exception e){
						
					}
				case "max":
					try{
						myMax = Double.parseDouble(myValue);
						break;
					}catch(Exception e){
						
					}
					break;
				}
			}
			
			if(myName == null || myName.equals(""))return;
			
			String[] myUniformParts = theUniformLine.split(Pattern.quote(" "));
	
			if(myUniformParts.length < 3)return;
			
			String myType = myUniformParts[1];
			String myUniformName = myUniformParts[2].replace(";", "");
			String myKey = myType + ":" + myUniformName + ":" + myName;
			CCLog.info("checks:" + myKey);
			if(_myUniforms.containsKey(myKey)){
				CCLog.info("contains:" + myKey);
				theUniforms.put(myKey, _myUniforms.get(myKey));
				for(CCNumberPropertyHandle<Double> myUniform:_myUniforms.get(myKey)._myProperties){
					_myUniformHandles.children().put(myUniform.name(), myUniform);
				}
				return;
			}
			switch(myType){
			case "float":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName, myMin, myMax)
					)
				);
				break;
			case "vec2":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax)
					)
				);
				break;
			case "vec3":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax), 
						createHandle(myName + ".z", myMin, myMax)
					)
				);
				break;
			case "vec4":
				theUniforms.put(
					myKey,
					new CCShaderUniform(
						myUniformName, 
						createHandle(myName + ".x", myMin, myMax), 
						createHandle(myName + ".y", myMin, myMax), 
						createHandle(myName + ".z", myMin, myMax), 
						createHandle(myName + ".w", myMin, myMax)
					)
				);
				break;
			}
			CCLog.info("puts:" + myKey);
		}catch(NumberFormatException nf){
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	private boolean loadShader(String theSource, boolean theThrowException, final String theErrorPrepend){
		Map<String,CCShaderUniform> myUniforms = new HashMap<>();
		_myUniformHandles.children().clear();
		
		String[] myLines = theSource.split(Pattern.quote("\n"));
		String myPropertyLine = null;
		
		StringBuffer mySource = new StringBuffer();
		
		for(int i = 0; i < myLines.length; i++){
			String myLine = myLines[i];
			myLine = myLine.trim();
			if(myLine.length() == 0){
				mySource.append("\n");
				continue;
			}else if(myLine.startsWith("@CCProperty")){
				mySource.append("\n");
				myPropertyLine = myLine;
			}else if(myLine.startsWith("@")){
				mySource.append("\n");
				continue;
			}else if(myLine.startsWith("uniform")){
				mySource.append(myLine + "\n");
				if(myPropertyLine != null)readProperty(myPropertyLine, myLine,myUniforms);
				myPropertyLine = null;
			}else{
				myPropertyLine = null;
				mySource.append(myLine + "\n");
			}
		}
		
		_myUniforms = myUniforms;
		_myUniformHandles.forceChange();
		
		theSource = mySource.toString();
		
		source(theSource);
		compile();
		if(!compileStatus()){
			StringBuffer myReplyBuffer = new StringBuffer();
			if(theErrorPrepend != null)myReplyBuffer.append(theErrorPrepend);
			myReplyBuffer.append(getInfoLog());
			_myInfoLog = myReplyBuffer.toString();
			if(theThrowException)throw new CCShaderException(_myInfoLog);
			return false;
		}else{
			_myInfoLog = "";
		}
		
		_mySource = theSource;
		
		return true;
	}
	
	@Override
	public String sourceCode() {
		return _mySource;
	}
	
	@Override
	public void sourceCode(String theSource) {
		if(!theSource.equals(_mySource)){
			_myReloadSource = true;
			_myReloadSourceCode = theSource;
		}
	}
	
	boolean checkReloadSource(){
		if(!_myReloadSource)return false;
		_myReloadSource = false;
		if(!loadShader(_myReloadSourceCode, false, null))return false;
		return true;
	}
	
	public void reloadFromSource(){
		loadShader(_mySource, false, null);
	}
	
	public void reload(){
		CCLog.info(loadShader(_myFiles));
	}
}