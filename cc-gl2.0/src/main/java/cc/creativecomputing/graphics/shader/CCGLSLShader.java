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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

public class CCGLSLShader extends CCShader{
	
	/**
	 * Tries to create a shader based on the given class and the following name scheme. You need to put 
	 * shader files with the names vertex.glsl, geometry.glsl, fragment.glsl into the package folder of
	 * the given class, at least a vertex or fragment shader has to be existent.
	 * <p>
	 * If the shader can not be loaded shader exception is thrown
	 * @param theClass class to look for the resources
	 * @return shader based on the given resources
	 */
	public static CCGLSLShader createFromResource(Class<?> theClass) {
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
		
		return new CCGLSLShader(myVertexShader, myGeometryShader, myFragmentShader);
	}
	
	/**
	 * Shortcut to {@linkplain #createFromResource(Class)} by taking the class from the object.
	 * @param theObject
	 * @return shader based on the given resources
	 */
	public static CCGLSLShader createFromResource(Object theObject) {
		return createFromResource(theObject.getClass());
	}
	
	public static enum CCGeometryInputType{
		
		POINTS(GL.GL_POINTS),
		LINES(GL.GL_LINES),
		//LINES_ADJACENCY(GL2.GL_LINES_ADJACENCY_EXT),
		LINES_ADJACENCY(GL2.GL_LINES_ADJACENCY_EXT),
		TRIANGLES(GL.GL_TRIANGLES),
		//TRIANGLES_ADJACENCY(GL2.GL_TRIANGLES_ADJACENCY_EXT);
		TRIANGLES_ADJACENCY(GL2.GL_TRIANGLES_ADJACENCY_EXT);
		
		int glID;
		
		private CCGeometryInputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	public static enum CCGeometryOutputType{
		
		POINTS(GL.GL_POINTS),
		LINE_STRIP(GL.GL_LINE_STRIP),
		TRIANGLE_STRIP(GL.GL_TRIANGLE_STRIP);
		
		int glID;
		
		private CCGeometryOutputType(final int theGLID) {
			glID = theGLID;
		}
	}
	
	protected int _myProgramObject;
	
	protected int _myVertexShaderID;
	protected int _myGeometryShaderID;
	protected int _myFragmentShaderID;
	
	protected Path[] _myVertexFiles;
	protected Path[] _myFragmentFiles;
	protected Path[] _myGeometryFiles;

	public CCGLSLShader(final Path theVertexShaderFile, final Path theFragmentShaderFile) {
		super(theVertexShaderFile,theFragmentShaderFile);
	}
	
	public CCGLSLShader(final Path[] theVertexShaderFile, final Path[] theFragmentShaderFile) {
		super(theVertexShaderFile,theFragmentShaderFile);
	}
	
	public CCGLSLShader(
		final Path theVertexShaderFile, 
		final Path theGeometryShaderFile, 
		final Path theFragmentShaderFile
	) {
		super(theVertexShaderFile,theFragmentShaderFile);

		if(theGeometryShaderFile != null)loadShader(GL3.GL_GEOMETRY_SHADER, theGeometryShaderFile);
	}
	
	/**
	 * Returns the underlying gl program id
	 * @return the underlying gl program id
	 */
	public long glProgram() {
		return _myProgramObject;
	}

	@Override
	public void initShader() {
		GL2 gl = CCGraphics.currentGL();
		_myProgramObject = (int)gl.glCreateProgramObjectARB();
	}
	
	private void loadShader(int theShaderID, final Path...theFiles) {
		if(theFiles == null || theFiles.length <= 0)return;
		String shaderSource = buildSource(theFiles);

		GL2 gl = CCGraphics.currentGL();
		//create an object that act as vertex shader container
		
		// add the source code of the hader to the object
		gl.glShaderSourceARB(theShaderID, 1, new String[] { shaderSource },(int[]) null, 0);
		
		// finally compile the shader
		gl.glCompileShader(theShaderID);
		
		checkLogInfo(gl, theShaderID, theFiles);
		
		// attach vertex shader to this program object
		gl.glAttachObjectARB(_myProgramObject, theShaderID);

		// delete shader objects this will mark them for deletion
		// shader object will be deleted when the program object is deleted
		gl.glDeleteObjectARB(theShaderID);
	}

	@Override
	public void loadVertexShader(final Path...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myVertexShaderID = (int)gl.glCreateShaderObjectARB(GL2.GL_VERTEX_SHADER);
		_myVertexFiles = theFiles;
		loadShader(_myVertexShaderID, _myVertexFiles);
	}

	public void loadGeometryShader(final Path...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myGeometryShaderID = (int)gl.glCreateShaderObjectARB(GL3.GL_GEOMETRY_SHADER);
		_myGeometryFiles = theFiles;
		loadShader(_myGeometryShaderID, _myGeometryFiles);
	}

	@Override
	public void loadFragmentShader(final Path...theFiles) {
		GL2 gl = CCGraphics.currentGL();
		_myFragmentShaderID = (int)gl.glCreateShaderObjectARB(GL2.GL_FRAGMENT_SHADER);
		_myFragmentFiles = theFiles;
		
		try {
			loadShader(_myFragmentShaderID, _myFragmentFiles);
		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void load() {
		GL2 gl = CCGraphics.currentGL();
		gl.glLinkProgram(_myProgramObject);
//		gl.glValidateProgram(_myProgramObject);
		checkLogInfo(gl, _myProgramObject, null);
	}
	
	public void reload() {
//		finalize();
//		initShader();
		loadShader(_myVertexShaderID, _myVertexFiles);
		loadShader(_myGeometryShaderID, _myGeometryFiles);
		loadShader(_myFragmentShaderID, _myFragmentFiles);
		load();
	}
	
	private boolean _myIsShaderInUse = false;

	@Override
	public void start() {
		GL2 gl = CCGraphics.currentGL();
		gl.glUseProgram(_myProgramObject);
		_myIsShaderInUse = true;
	}

	@Override
	public void end() {
		_myIsShaderInUse = false;
		GL2 gl = CCGraphics.currentGL();
		gl.glUseProgram(0);
	}

	void checkLogInfo(GL2 gl, int theObject, Path[] theFiles) {
		IntBuffer iVal = CCBufferUtil.newIntBuffer(1);
		gl.glGetObjectParameterivARB(theObject, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB,iVal);

		int length = iVal.get();
		if (length <= 1) {
			return;
		}
		ByteBuffer infoLog = CCBufferUtil.newByteBuffer(length);
		iVal.flip();
		gl.glGetInfoLogARB(theObject, length, iVal, infoLog);
		byte[] infoBytes = new byte[length];
		infoLog.get(infoBytes);
		String myReply = new String(infoBytes);
		if(myReply.startsWith("WARNING:"))return;
		
		if(theFiles != null) {
			StringBuffer myReplyBuffer = new StringBuffer("Problem inside the following shader:");
			for(Path myFile:theFiles) {
				myReplyBuffer.append("\n");
				myReplyBuffer.append(myFile);
			}
			myReplyBuffer.append("\n");
			myReplyBuffer.append("The following Problem occured:\n");
			myReplyBuffer.append(myReply);
			myReplyBuffer.append("\n");
			myReply = myReplyBuffer.toString();
		}
		
		throw new CCShaderException(myReply);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void finalize(){
		GL2 gl = CCGraphics.currentGL();
		gl.glDeleteObjectARB(_myProgramObject);
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
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2GL3.GL_GEOMETRY_INPUT_TYPE_ARB, theInputType.glID);
		gl.glProgramParameteri(_myProgramObject, GL3.GL_GEOMETRY_INPUT_TYPE, theInputType.glID);
	}
	
	public void geometryVerticesOut(final int theVerticesOut) {
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2.GL_GEOMETRY_VERTICES_OUT_EXT, theVerticesOut);
		gl.glProgramParameteri(_myProgramObject, GL3.GL_GEOMETRY_VERTICES_OUT, theVerticesOut);
	}
	
	public int maximumGeometryOutputVertices() {
		IntBuffer temp = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL3.GL_MAX_GEOMETRY_OUTPUT_VERTICES,temp);
		return temp.get();
	}
	
	public void geometryOutputType(final CCGeometryOutputType theOutputType) {
		GL2 gl = CCGraphics.currentGL();
		//gl.glProgramParameteri(_myProgramObject, GL2.GL_GEOMETRY_OUTPUT_TYPE_EXT, theOutputType.glID);
		gl.glProgramParameteri(_myProgramObject, GL3.GL_GEOMETRY_OUTPUT_TYPE, theOutputType.glID);
	}

	int getAttribLocation(String name) {
		GL2 gl = CCGraphics.currentGL();
		return (gl.glGetAttribLocation(_myProgramObject, name));
	}

	public int uniformLocation(final String theName) {
		GL2 gl = CCGraphics.currentGL();
		return gl.glGetUniformLocation(_myProgramObject, theName);
	}
	
	public void uniform1i(final int theLocation, final int theValue){
		GL2 gl = CCGraphics.currentGL();
		gl.glUniform1i(theLocation, theValue);
	}
	
	public void uniform1i(final String theName, final int theValue){
		uniform1i(uniformLocation(theName), theValue);
	}
	
	public void uniform(final int theLocation, final boolean theValue){
		GL2 gl = CCGraphics.currentGL();
		if(theValue)gl.glUniform1i(theLocation, 1);
		else gl.glUniform1i(theLocation, 0);
	}
	
	public void uniform(final String theName, final boolean theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final double theValue){
		CCGraphics.currentGL().glUniform1f(theLocation, (float)theValue);
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
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector1){
				CCVector1 myVector = (CCVector1)myObject;
				myData.put(myVector.x);
			}else if(myObject instanceof Float){
				Float myVector = (Float)myObject;
				myData.put(myVector);
			}
		}
		myData.rewind();
		CCGraphics.currentGL().glUniform1fv(theLocation, theVectors.size(), myData);
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
		CCGraphics.currentGL().glUniform1fv(theLocation, theValues.length, myData);
	}
	
	public void uniform1fv(final String theName, double...theValues){
		uniform1fv(uniformLocation(theName), theValues);
	}
	
	
	public void uniform2f(final int theLocation, final double theX, final double theY){
		CCGraphics.currentGL().glUniform2f(theLocation, (float)theX, (float)theY);
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
	
	public void uniform3f(final int theLocation, final double theX, final double theY, final double theZ){
		CCGraphics.currentGL().glUniform3f(theLocation, (float)theX, (float)theY, (float)theZ);
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
		CCGraphics.currentGL().glUniform4f(theLocation, (float)theX, (float)theY, (float)theZ, (float)theW);
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
		CCGraphics.currentGL().glUniform3fv(theLocation, theValues.length, myData);
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
		CCGraphics.currentGL().glUniform3fv(theLocation, theValues.length, myData);
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
		CCGraphics.currentGL().glUniform4fv(theLocation, theVectors.size(), myData);
	}
	
	public void uniform4fv(final String theName, final List<?> theVectors){
		uniform4fv(uniformLocation(theName), theVectors);
	}
	
	
	public void uniform(final int theLocation, final CCColor theColor){
		if(!_myIsShaderInUse)throw new CCShaderException("You can only change values if a shader is in use. See start() and end() method of CCShader.");
		GL2 gl = CCGraphics.currentGL();
		gl.glUniform4f(theLocation, (float)theColor.red(), (float)theColor.green(), (float)theColor.blue(), (float)theColor.alpha());
	}
	
	public void uniform(final String theName, final CCColor theColor){
		uniform(uniformLocation(theName), theColor);
	}
	
	public void uniformMatrix4f(final int theLocation, CCMatrix4x4 theMatrix) { 
		GL2 gl = CCGraphics.currentGL();
		FloatBuffer myData =  theMatrix.toFloatBuffer();
		myData.rewind();
		gl.glUniformMatrix4fv(theLocation, 1, false, myData);
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
		CCGraphics.currentGL().glUniformMatrix4fv(theLocation, theMatrices.size(), false, myData);
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
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_ATTRIBS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., doubleing-point values) that 
	 * are available for vertex shader uniform variables. The minimum legal value is 512.
	 * @return
	 */
	public int maximumVertexUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of doubleing-point variables available 
	 * for varying variables. The minimum legal value is 32.
	 * @return
	 */
	public int maximumVariyingFloats(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VARYING_FLOATS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of hardware units that can be used to 
	 * access texture maps from the vertex processor. The minimum legal value is 0.
	 * @return
	 */
	public int maximumVertexTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the vertex processor and the fragment processor combined. The minimum legal value is 2.
	 * @return
	 */
	public int maximumCombinedTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the total number of hardware units that can be used to access texture 
	 * maps from the fragment processor. The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureImageUnits(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_IMAGE_UNITS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of texture coordinate sets that are available. 
	 * The minimum legal value is 2.
	 * @return
	 */
	public int maximumTextureCoords(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_TEXTURE_COORDS_ARB, myResult);
		return myResult.get();
	}
	
	/**
	 * Defines the number of components (i.e., doubleing-point values) that 
	 * are available for fragment shader uniform variables. The minimum legal value is 64.
	 * @return
	 */
	public int maximumFragmentUniformComponents(){
		IntBuffer myResult = IntBuffer.allocate(1);
		GL2 gl = CCGraphics.currentGL();
		gl.glGetIntegerv(GL2.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS, myResult);
		return myResult.get();
	}
	
	public void printSpecs(){
		GL2 gl = CCGraphics.currentGL();
		System.out.println(gl.glGetString(GL.GL_EXTENSIONS));
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
