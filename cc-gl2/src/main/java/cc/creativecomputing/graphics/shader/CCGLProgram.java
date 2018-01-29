/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.graphics.shader;

import static org.lwjgl.opengl.GL11.GL_EXTENSIONS;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_DELETE_STATUS;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_COORDS;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_VARYING_FLOATS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_ATTRIBS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1fv;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform2fv;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_INPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_OUTPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_VERTICES_OUT;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_MAX_GEOMETRY_OUTPUT_VERTICES;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import static org.lwjgl.opengl.GL41.glProgramParameteri;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCGLProgram{
	
	/**
	 * Tries to create a shader based on the given class and the following name scheme. You need to put 
	 * shader files with the names vertex.glsl, geometry.glsl, fragment.glsl into the package folder of
	 * the given class, at least a vertex or fragment shader has to be existent.
	 * <p>
	 * If the shader can not be loaded shader exception is thrown
	 * @param theClass class to look for the resources
	 * @return shader based on the given resources
	 */
	public static CCGLProgram createFromResource(Class<?> theClass) {
		Path myVertexShader = CCNIOUtil.classPath(theClass,"vertex.glsl");
		Path myGeometryShader = CCNIOUtil.classPath(theClass,"geometry.glsl");
		Path myFragmentShader = CCNIOUtil.classPath(theClass,"fragment.glsl");
		
		if(myVertexShader == null && myGeometryShader == null && myFragmentShader == null) {
			throw new CCShaderException(
				"Could not load Shader based on the given resource.\b" +
				"Note to make this work you need put shader source files with the name scheme:\n" +
				" vertex.glsl\n geometry.glsl\n fragment.glsl\n" +
				"inside the package folder of the given class. " +
				"At least a vertex or fragment shader need to be found."
			);
		}
		
		return new CCGLProgram(myVertexShader, myGeometryShader, myFragmentShader);
	}
	
	/**
	 * Shortcut to {@linkplain #createFromResource(Class)} by taking the class from the object.
	 * @param theObject
	 * @return shader based on the given resources
	 */
	public static CCGLProgram createFromResource(Object theObject) {
		return createFromResource(theObject.getClass());
	}
	
	public static CCGLProgram createEmptyFrament() {
		CCGLProgram myResult = new CCGLProgram();
		myResult._myFragmentShader = myResult.attachShader(
			"void main(){\n" + 
			"	gl_FragColor = vec4(1.0,1.0,0.0,1.0);\n" + 
			"}", 
			CCShaderObjectType.FRAGMENT
		);
		CCLog.info(myResult._myFragmentShader);
		myResult.link();
		return myResult;
	}
	
	public enum CCGeometryInputType{
		
		POINTS(GL_POINTS),
		LINES(GL_LINES),
		LINES_ADJACENCY(GL_LINES_ADJACENCY),
		TRIANGLES(GL_TRIANGLES),
		TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY);
		
		int glID;
		
		CCGeometryInputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public enum CCGeometryOutputType{
		
		POINTS(GL_POINTS),
		LINE_STRIP(GL_LINE_STRIP),
		TRIANGLE_STRIP(GL_TRIANGLE_STRIP);
		
		int glID;
		
		CCGeometryOutputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public static class CCGLTextureUniform{
		public CCTexture texture;
		public int unit;
		public String parameter;
		
		public CCGLTextureUniform(CCTexture theTexture, String theParameter){
			texture = theTexture;
			parameter = theParameter;
			unit = -1;
		}
	}
	
	protected Map<String, CCGLTextureUniform> _myTextures = new HashMap<>();
	
	public void setTextureUniform(String theParameter, CCTexture theTexture){
		if(_myTextures.containsKey(theParameter)){
			_myTextures.get(theParameter).texture = theTexture;
		}
		_myTextures.put(theParameter, new CCGLTextureUniform(theTexture, theParameter));
	}
	
	public Collection<CCGLTextureUniform> textures(){
		return _myTextures.values();
	}
	
	protected int _myProgram;
	@CCProperty(name = "vertex")
	protected CCGLShader _myVertexShader;
	@CCProperty(name = "fragment")
	protected CCGLShader _myFragmentShader;
	@CCProperty(name = "geometry")
	protected CCGLShader _myGeometryShader;
	
	private List<CCGLShader> _myShaderList = new ArrayList<>();

	public CCGLProgram(final Path theVertexShaderPath, final Path theFragmentShaderPath) {
		this(
			new Path[] {theVertexShaderPath}, 
			new Path[] {theFragmentShaderPath}
		);
	}
	
	public CCGLProgram(final Path theVertexShaderPath, final Path theGeometryShaderPath, final Path theFragmentShaderPath) {
		this(
			new Path[] {theVertexShaderPath}, 
			new Path[] {theGeometryShaderPath}, 
			new Path[] {theFragmentShaderPath}
		);
	}
	
	public CCGLProgram(
		final Path[] theVertexShaderPaths, 
		final Path[] theFragmentShaderFile
	) {
		this(
			theVertexShaderPaths,
			null, 
			theFragmentShaderFile
		);
	}
	
	protected CCGLProgram(){
		
		_myProgram = glCreateProgram();
	}
	
	public CCGLProgram(
		final Path[] theVertexShaderPaths, 
		final Path[] theGeometryShaderPaths, 
		final Path[] theFragmentShaderPaths
	) {
		this();
		
		_myVertexShader = attachShader(theVertexShaderPaths, CCShaderObjectType.VERTEX);
		_myGeometryShader = attachShader(theGeometryShaderPaths, CCShaderObjectType.GEOMETRY);
		_myFragmentShader = attachShader(theFragmentShaderPaths, CCShaderObjectType.FRAGMENT);

		link();
	}
	
	private CCGLShader attachShader(final Path[] theShaderPaths, CCShaderObjectType theType){
		if(theShaderPaths == null || theShaderPaths[0] == null)return null;
		
		CCGLShader myShader = new CCGLShader(theType, theShaderPaths);
		_myShaderList.add(myShader);
		attach(myShader);
		
		return myShader;
	}
	
	protected void init(String theVertexSource, String theGeometrySource, String theFragmentSource){
		
		_myVertexShader = attachShader(theVertexSource, CCShaderObjectType.VERTEX);
		_myGeometryShader = attachShader(theGeometrySource, CCShaderObjectType.GEOMETRY);
		_myFragmentShader = attachShader(theFragmentSource, CCShaderObjectType.FRAGMENT);

		link();
	}
	
	protected CCGLShader attachShader(final String theSource, CCShaderObjectType theType){
		if(theSource == null)return null;
		
		CCGLShader myShader = new CCGLShader(theType, theSource);
		_myShaderList.add(myShader);
		attach(myShader);
		
		return myShader;
	}
	
	public void reload() {
		if(_myVertexShader != null)_myVertexShader.reload();
		if(_myFragmentShader != null)_myFragmentShader.reload();
		if(_myGeometryShader != null)_myGeometryShader.reload();
	}
	
	private boolean _myIsShaderInUse = false;
	
	/**
	 * In order to create a complete shader program, there must be a way to specify the list 
	 * of things that will be linked together. CCGLProgram provide this mechanism. Shaders 
	 * that are to be linked together in a program must first be attached to that program. 
	 * This attaches the shader object specified by shader to the program. 
	 * This indicates that shader will be included in link operations that will be performed on program.
	 * <p>
	 * All operations that can be performed on a shader object are valid whether or not the shader object 
	 * is attached to a program. It is permissible to attach a shader object to a program object before 
	 * source code has been loaded into the shader object or before the shader object has been compiled. 
	 * It is permissible to attach multiple shader objects of the same type because each may contain a 
	 * portion of the complete shader. It is also permissible to attach a shader object to more than 
	 * one program. If a shader object is deleted while it is attached to a program, it will be flagged 
	 * for deletion, and deletion will not occur until glDetachShader is called to detach it from all 
	 * programs to which it is attached.
	 * @param theShader the shader object that is to be attached.
	 */
	public void attach(CCGLShader theShader){
		
		glAttachShader(_myProgram, theShader._myShaderID);
	}
	
	/**
	 * Detaches the shader object specified by shader from the program object specified by program. 
	 * This command can be used to undo the effect of the command {@linkplain #attach(CCGLShader)}.
	 * <p>
	 * If shader has already been flagged for deletion by a call to {@linkplain CCGLShader#delete()} 
	 * and it is not attached to any other program, it will be deleted after it has been detached.
	 * @param theShader the shader object to be detached.
	 */
	public void detach(CCGLShader theShader){
		
		glDetachShader(_myProgram, theShader._myShaderID);
	}
	
	/**
	 * Links the program object specified by program. A shader object of type {@link CCShaderObjectType#VERTEX} 
	 * attached to program is used to create an executable that will run on the programmable vertex processor. 
	 * A shader object of type {@link CCShaderObjectType#FRAGMENT} attached to program is used to create an 
	 * executable that will run on the programmable fragment processor.
	 * <p>
	 * The status of the link operation will be stored as part of the program object's state. This value will 
	 * be set to true if the program object was linked without errors and is ready for use, and false otherwise. 
	 * It can be queried by calling {@linkplain #linkStatus()}
	 * <p>
	 * As a result of a successful link operation, all active user-defined uniform variables belonging to program 
	 * will be initialized to 0, and each of the program object's active uniform variables will be assigned a 
	 * location that can be queried by calling glGetUniformLocation. Also, any active user-defined attribute v
	 * ariables that have not been bound to a generic vertex attribute index will be bound to one at this time.
	 * <p>
	 * Linking of a program object can fail for a number of reasons as specified in the OpenGL ES Shading Language 
	 * Specification. The following lists some of the conditions that will cause a link error.
	 * <ul>
	 * <li>A vertex shader and a fragment shader are not both present in the program object.
	 * <li>The number of active attribute variables supported by the implementation has been exceeded.
	 * <li>The storage limit for uniform variables has been exceeded.
	 * <li>The number of active uniform variables supported by the implementation has been exceeded.
	 * <li>The main function is missing for the vertex shader or the fragment shader.
	 * <li>A varying variable actually used in the fragment shader is not declared in the same way (or is not declared at all) in the vertex shader.
	 * <li>A reference to a function or variable name is unresolved.
	 * <li>A shared global is declared with two different types or two different initial values.
	 * <li>One or more of the attached shader objects has not been successfully compiled (via glCompileShader) or loaded with a pre-compiled shader binary (via glShaderBinary).
	 * <li>Binding a generic attribute matrix caused some rows of the matrix to fall outside the allowed maximum of GL_MAX_VERTEX_ATTRIBS.
	 * <li>Not enough contiguous vertex attribute slots could be found to bind attribute matrices.
	 * </ul>
	 * When a program object has been successfully linked, the program object can be made part of current state by 
	 * calling glUseProgram. Whether or not the link operation was successful, the program object's information 
	 * log will be overwritten. The information log can be retrieved by calling getInfoLog.
	 * {@linkplain #link()} will also install the generated executables as part of the current rendering state 
	 * if the link operation was successful and the specified program object is already currently in use as a 
	 * result of a previous call to glUseProgram. If the program object currently in use is relinked unsuccessfully, 
	 * its link status will be set to false , but the executables and associated state will remain part of the current 
	 * state until a subsequent call to glUseProgram removes it from use. After it is removed from use, it cannot 
	 * be made part of current state until it has been successfully relinked.
	 * <p>
	 * The program object's information log is updated and the program is generated at the time of the link operation. 
	 * After the link operation, applications are free to modify attached shader objects, compile attached shader objects, 
	 * detach shader objects, delete shader objects, and attach additional shader objects. None of these operations affects 
	 * the information log or the program that is part of the program object.
	 */
	public void link(){
		
		glLinkProgram(_myProgram);
	}
	
	public void validate(){
		
		glValidateProgram(_myProgram);
	}
	
	/**
	 * returns the value of a parameter.
	 * <p>
	 * The following parameters are defined:
	 * <ul>
	 * <li><code>GL_DELETE_STATUS</code> returns <code>GL_TRUE</code> if program is currently flagged for deletion, and <code>GL_FALSE</code> otherwise.
	 * <li><code>GL_LINK_STATUS</code> returns <code>GL_TRUE</code> if the last link operation on program was successful, and <code>GL_FALSE</code> otherwise.
	 * <li><code>GL_VALIDATE_STATUS</code> returns <code>GL_TRUE</code> if the last validation operation on program was successful, and <code>GL_FALSE</code> otherwise.
	 * <li><code>GL_INFO_LOG_LENGTH</code> returns the number of characters in the information log for program including the null termination character (i.e., the size of the character buffer required to store the information log). If program has no information log, a value of 0 is returned.
	 * <li><code>GL_ATTACHED_SHADERS</code> returns the number of shader objects attached to program.
	 * <li><code>GL_ACTIVE_ATTRIBUTES</code> returns the number of active attribute variables for program.
	 * <li><code>GL_ACTIVE_ATTRIBUTE_MAX_LENGTH</code> returns the length of the longest active attribute name for program, including the null termination character (i.e., the size of the character buffer required to store the longest attribute name). If no active attributes exist, 0 is returned.
	 * <li><code>GL_ACTIVE_UNIFORMS</code> returns the number of active uniform variables for program.
	 * <li><code>GL_ACTIVE_UNIFORM_MAX_LENGTH</code> returns the length of the longest active uniform variable name for program, including the null termination character (i.e., the size of the character buffer required to store the longest uniform variable name). If no active uniform variables exist, 0 is returned.
	 * </ul>
	 * @param theParameter
	 * @return the value of a parameter.
	 */
	public int get(int theParameter){
		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);
		
		glGetProgramiv(_myProgram, theParameter,iVal);
		return  iVal.get();
	}
	
	/**
	 * returns <code>true</code> if the last link operation on program was successful, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean linkStatus(){
		return get(GL_LINK_STATUS) == GL_TRUE;
	}
	
	/**
	 * Returns the number of characters in the information log for shader including the 
	 * null termination character (i.e., the size of the character buffer required to 
	 * store the information log). If shader has no information log, a value of 0 is returned.
	 * @return the number of characters in the information log
	 */
	public int infoLogLength(){
		return get(GL_INFO_LOG_LENGTH);
	}
	
	/**
	 * Returns <code>true</code> if the last validation operation on program was successful, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean validateStatus(){
		return get(GL_VALIDATE_STATUS) == GL_TRUE;
	}
	
	/**
	 * Returns <code>true</code> if shader is currently flagged for deletion, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean deleteStatus(){
		return get(GL_DELETE_STATUS) == GL_TRUE;
	}
	
	public int activeUniforms(){
		return get(GL_ACTIVE_UNIFORMS);
	}
	

	public void start() {
		
		boolean myRelink = false;
		for(CCGLShader myShader:_myShaderList){
			myRelink = myRelink || myShader.checkReloadSource();
		}
		if(myRelink)link();
		
		glUseProgram(_myProgram);
		for(CCGLShader myShader:_myShaderList){
			myShader.applyUniforms(this);
		}
		_myIsShaderInUse = true;
	}

	public void end() {
		_myIsShaderInUse = false;
		
		glUseProgram(0);
	}

	String getInfoLog(int theObject, Path[] theFiles) {
		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);

		int length = iVal.get();
		if (length <= 1) {
			return null;
		}
		ByteBuffer infoLog = CCBufferUtil.newByteBuffer(length);
		iVal.flip();
		glGetShaderInfoLog(theObject, iVal, infoLog);
		byte[] infoBytes = new byte[length];
		infoLog.get(infoBytes);
		String myReply = new String(infoBytes);
		if(myReply.startsWith("WARNING:"))return null;
		
		StringBuffer myReplyBuffer = new StringBuffer();
		if(theFiles != null){
			myReplyBuffer.append("Problem inside the following shader:");
			for(Path myFile:theFiles) {
				myReplyBuffer.append("\n");
				myReplyBuffer.append(myFile);
			}
		}
		myReplyBuffer.append("\n");
		myReplyBuffer.append("The following Problem occured:\n");
		myReplyBuffer.append(myReply);
		myReplyBuffer.append("\n");
		return myReplyBuffer.toString();
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void finalize(){
		
		
	}
	
// SETTINGS FOR GEOMETRY SHADER
	
	/**
	 * The input primitive type is a parameter of the program object, and must be
	 * set before loading the shader with the {@link #load()} function. by calling ProgramParameteriARB with <pname> set to
    GEOMETRY_INPUT_TYPE_ARB and <value> set to one of POINTS, LINES,
    LINES_ADJACENCY_ARB, TRIANGLES or TRIANGLES_ADJACENCY_ARB. This setting
    will not be in effect until the next time LinkProgram has been called
	 */
	public void geometryInputType(final CCGeometryInputType theInputType) {
		
		//glProgramParameteri(_myProgramObject, GL2GL_GEOMETRY_INPUT_TYPE_ARB, theInputType.glID);
		glProgramParameteri(_myProgram, GL_GEOMETRY_INPUT_TYPE, theInputType.glID);
	}
	
	public void geometryVerticesOut(final int theVerticesOut) {
		
		//glProgramParameteri(_myProgramObject, GL_GEOMETRY_VERTICES_OUT_EXT, theVerticesOut);
		glProgramParameteri(_myProgram, GL_GEOMETRY_VERTICES_OUT, theVerticesOut);
	}
	
	public int maximumGeometryOutputVertices() {
		IntBuffer temp = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_GEOMETRY_OUTPUT_VERTICES,temp);
		return temp.get();
	}
	
	public void geometryOutputType(final CCGeometryOutputType theOutputType) {
		
		//glProgramParameteri(_myProgramObject, GL_GEOMETRY_OUTPUT_TYPE_EXT, theOutputType.glID);
		glProgramParameteri(_myProgram, GL_GEOMETRY_OUTPUT_TYPE, theOutputType.glID);
	}

	int getAttribLocation(String name) {
		
		return (glGetAttribLocation(_myProgram, name));
	}

	public int uniformLocation(final String theName) {
		
		return glGetUniformLocation(_myProgram, theName);
	}
	
	public void uniform1i(final int theLocation, final int theValue){
		
		glUniform1i(theLocation, theValue);
	}
	
	public void uniform1i(final String theName, final int theValue){
		uniform1i(uniformLocation(theName), theValue);
	}
	
	public void uniform(final int theLocation, final boolean theValue){
		
		if(theValue)glUniform1i(theLocation, 1);
		else glUniform1i(theLocation, 0);
	}
	
	public void uniform(final String theName, final boolean theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final double theValue){
		glUniform1f(theLocation, (float)theValue);
	}
	
	public void uniform1f(final String theName, final double theValue){
		uniform1f(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final CCVector1 theValue){
		uniform1f(theLocation, theValue.x);
	}
	
	public void uniform1f(final String theName, final CCVector1 theValue){
		uniform1f(theName, theValue.x);
	}
	
	public void uniform1fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size());
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector1){
				CCVector1 myVector = (CCVector1)myObject;
				myData.put(myVector.x);
			}else if(myObject instanceof Float){
				Float myVector = (Float)myObject;
				myData.put(myVector);
			}else if(myObject instanceof Double){
				Double myVector = (Double)myObject;
				myData.put(myVector.floatValue());
			}
		}
		myData.rewind();
		glUniform1fv(theLocation, myData);
	}
	
	public void uniform1fv(final String theName, final List<?> theVectors){
		uniform1fv(uniformLocation(theName), theVectors);
	}
	
	public void uniform1fv(final int theLocation, double...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length);
		for(double myVal:theValues){
			myData.put((float)myVal);
		}
		myData.rewind();
		glUniform1fv(theLocation, myData);
	}
	
	public void uniform1fv(final String theName, double...theValues){
		uniform1fv(uniformLocation(theName), theValues);
	}
	
	
	public void uniform2f(final int theLocation, final double theX, final double theY){
		glUniform2f(theLocation, (float)theX, (float)theY);
	}
	
	public void uniform2f(final int theLocation, final CCVector2 theVector){
		uniform2f(theLocation, theVector.x, theVector.y);
	}
	
	public void uniform2f(final String theName, final double theX, final double theY){
		uniform2f(uniformLocation(theName), theX, theY);
	}
	
	public void uniform2f(final String theName, final CCVector2 theValue){
		uniform2f(uniformLocation(theName), theValue);
	}
	
	public void uniform2fv(final int theLocation, final List<CCVector2> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 2);
		for(CCVector2 myValue:theVectors){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
			}else{
				myData.put((float)myValue.x);
				myData.put((float)myValue.y);
			}
		}
		myData.rewind();
		glUniform2fv(theLocation, myData);
	}
	
	public void uniform2fv(final int theLocation, CCVector2...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 2);
		for(CCVector2 myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
			}else{
				myData.put((float)myValue.x);
				myData.put((float)myValue.y);
			}
		}
		myData.rewind();
		glUniform2fv(theLocation, myData);
	}
	
	public void uniform2fv(final String theName, CCVector2...theValues){
		uniform2fv(uniformLocation(theName), theValues);
	}
	
	public void uniform2fv(final String theName, List<CCVector2>theValues){
		uniform2fv(uniformLocation(theName), theValues);
	}
	
	public void uniform3f(final int theLocation, final double theX, final double theY, final double theZ){
		glUniform3f(theLocation, (float)theX, (float)theY, (float)theZ);
	}
	
	public void uniform(final int theLocation, final CCVector3 theVector){
		uniform3f(theLocation, theVector.x, theVector.y, theVector.z);
	}
	
	public void uniform3f(final String theName, final double theX, final double theY, final double theZ){
		uniform3f(uniformLocation(theName), theX, theY, theZ);
	}
	
	public void uniform3f(final String theName, final CCVector3 theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform3f(final int theLocation, final CCColor theColor){
		uniform3f(theLocation, theColor.r, theColor.g, theColor.b);
	}
	
	public void uniform3f(final String theName, final CCColor theColor){
		uniform3f(uniformLocation(theName), theColor);
	}
	
	public void uniform4f(int theLocation, final double theX, final double theY, final double theZ, double theW){
		glUniform4f(theLocation, (float)theX, (float)theY, (float)theZ, (float)theW);
	}
	
	public void uniform4f(final String theName, final double theX, final double theY, final double theZ, double theW){
		uniform4f(uniformLocation(theName), theX, theY, theZ, theW);
	}
	
	public void uniform4f(final int theLocation, final CCVector4 theVector){
		uniform4f(theLocation, theVector.x, theVector.y, theVector.z, theVector.w);
	}
	
	public void uniform4f(final int theLocation, final CCColor theColor){
		uniform4f(theLocation, theColor.r, theColor.g, theColor.b, theColor.a);
	}
	
	public void uniform4f(final String theName, final CCVector4 theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform4f(final String theName, final CCColor theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform3fv(final int theLocation, CCVector3...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 3);
		for(CCVector3 myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put((float)myValue.x);
				myData.put((float)myValue.y);
				myData.put((float)myValue.z);
			}
		}
		myData.rewind();
		glUniform3fv(theLocation, myData);
	}
	
	public void uniform3fv(final String theName, CCColor...theValues){
		uniform3fv(uniformLocation(theName), theValues);
	}
	
	public void uniform3fv(final int theLocation, CCColor...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theValues.length * 3);
		for(CCColor myValue:theValues){
			if(myValue == null){
				myData.put(0);
				myData.put(0);
				myData.put(0);
			}else{
				myData.put((float)myValue.r);
				myData.put((float)myValue.g);
				myData.put((float)myValue.b);
			}
		}
		myData.rewind();
		glUniform3fv(theLocation, myData);
	}
	
	public void uniform3fv(final String theName, CCVector3...theValues){
		uniform3fv(uniformLocation(theName), theValues);
	}

	
	public void uniform4fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector4){
				CCVector4 myVector = (CCVector4)myObject;
				myData.put((float)myVector.x);
				myData.put((float)myVector.y);
				myData.put((float)myVector.z);
				myData.put((float)myVector.w);
			}else if(myObject instanceof CCVector3){
				CCVector3 myVector = (CCVector3)myObject;
				myData.put((float)myVector.x);
				myData.put((float)myVector.y);
				myData.put((float)myVector.z);
				myData.put(0);
			}
		}
		myData.rewind();
		glUniform4fv(theLocation, myData);
	}
	
	public void uniform4fv(final String theName, final List<?> theVectors){
		uniform4fv(uniformLocation(theName), theVectors);
	}
	
	
	public void uniform(final int theLocation, final CCColor theColor){
		if(!_myIsShaderInUse)throw new CCShaderException("You can only change values if a shader is in use. See start() and end() method of CCShader.");
		
		glUniform4f(theLocation, (float)theColor.red(), (float)theColor.green(), (float)theColor.blue(), (float)theColor.alpha());
	}
	
	public void uniform(final String theName, final CCColor theColor){
		uniform(uniformLocation(theName), theColor);
	}
	
	public void uniformMatrix4f(final int theLocation, CCMatrix4x4 theMatrix) { 
		
		FloatBuffer myData =  theMatrix.toFloatBuffer();
		myData.rewind();
		glUniformMatrix4fv(theLocation, false, myData);
	}
	
	public void uniformMatrix4f(final String theName, CCMatrix4x4 theMatrix) {
		uniformMatrix4f(uniformLocation(theName), theMatrix);
	}
	
	public void uniformMatrix4fv(final int theLocation, final List<CCMatrix4x4> theMatrices){
		if(theMatrices.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theMatrices.size() * 16);
		for(CCMatrix4x4 myMatrix:theMatrices){
			if(myMatrix == null)continue;
			myData.put(myMatrix.toFloatBuffer());
		}
		myData.rewind();
		glUniformMatrix4fv(theLocation, false, myData);
	}
	
	public void uniformMatrix4fv(final String theName, final List<CCMatrix4x4> theMatrices){
		uniformMatrix4fv(uniformLocation(theName), theMatrices);
	}
	
	//////////////////////////////////////////////////
	//
	// GET INFORMATION ON SHADER SUPPORT
	//
	//////////////////////////////////////////////////
	
	/**
	 * Defines the number of active vertex attributes that are available. 
	 * The minimum legal value is 16.
	 */
	public int maximumVertexAttributes(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., doubleing-point values) that 
	 * are available for vertex shader uniform variables. The minimum legal value is 512.
	 * @return
	 */
	public int maximumVertexUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_VERTEX_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of doubleing-point variables available 
	 * for varying variables. The minimum legal value is 32.
	 * @return
	 */
	public int maximumVariyingFloats(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_VARYING_FLOATS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of hardware units that can be used to 
	 * access texture maps from the vertex processor. The minimum legal value is 0.
	 * @return
	 */
	public int maximumVertexTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the vertex processor and the fragment processor combined. The minimum legal value is 2.
	 * @return
	 */
	public int maximumCombinedTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the fragment processor. The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of texture coordinate sets that are available. 
	 * The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureCoords(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_TEXTURE_COORDS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., doubleing-point values) that 
	 * are available for fragment shader uniform variables. The minimum legal value is 64.
	 * @return
	 */
	public int maximumFragmentUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		
		glGetIntegerv(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	public void printSpecs(){
		System.out.println(glGetString(GL_EXTENSIONS));
		System.out.println("GLSL SHADER SUPPORT");
		System.out.println("########################");
		System.out.println("maximumVertexAttributes:         "+maximumVertexAttributes());
		System.out.println("maximumVertexUniformComponents:  "+maximumVertexUniformComponents());
		System.out.println("maximumVariyingFloats:           "+maximumVariyingFloats());
		System.out.println("maximumVertexTextureImageUnits:  "+maximumVertexTextureImageUnits());
		System.out.println("maximumCombinedTextureImageUnits:"+maximumCombinedTextureImageUnits());
		System.out.println("maximumTextureImageUnits:        "+maximumTextureImageUnits());
		System.out.println("maximumTextureCoords:            "+maximumTextureCoords());
		System.out.println("maximumFragmentUniformComponents:"+maximumFragmentUniformComponents());
	}
}
