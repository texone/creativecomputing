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

import java.nio.DoubleBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jogamp.opengl.cg.CGcontext;
import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CGprogram;
import com.jogamp.opengl.cg.CgGL;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * This class encapsulates the functionalities for CG shaders in JOGL.
 * It automatically loads the shader files checks for errors and simplifies
 * the process of setting parameters. One thing most shaders need in the transformation
 * of the vertices regarding the modelview and projection matrix. This class 
 * assumes that you pass this as uniform parameter with the name modelViewProj to the main entry function
 * of your shader. If you have a different name you can also pass it as optional argument to the constructor.
 * See the following example as starting point for your entry function.
 * <blockquote>
 * <pre>
 * void C4E1v_transform(double4 position  : POSITION,
 * 						out double4 oPosition : POSITION,
 * 
 * 						uniform double4x4 modelViewProj)
 * {
 * 		// Transform position from object space to clip space
 * 		oPosition = mul(modelViewProj, position);
 * }
 * </pre>
 * </blockquote>
 * 
 * Check the cg tutorial of nvidia which is an excellent source to begin with writing cg
 * shaders. Most of the examples are ported to creative computing. Check the demos trunk in the svn.
 * 
 * @author texone
 *
 */
public class CCCGShader extends CCShader{
	
	public static enum CCCGMatrixType{
		MODELVIEW(CgGL.CG_GL_MODELVIEW_MATRIX),
		PROJECTION(CgGL.CG_GL_PROJECTION_MATRIX),
		TEXTURE(CgGL.CG_GL_TEXTURE_MATRIX),
		MODELVIEW_PROJECTION(CgGL.CG_GL_MODELVIEW_PROJECTION_MATRIX);
		
		private int cgID;
		
		private CCCGMatrixType(final int theCgID) {
			cgID = theCgID;
		}
	}
	
	public static enum CCCGMatrixTransform{
		IDENTITY(CgGL.CG_GL_MATRIX_IDENTITY),
		TRANSPOSE(CgGL.CG_GL_MATRIX_TRANSPOSE),
		INVERSE(CgGL.CG_GL_MATRIX_INVERSE),
		INVERSE_TRANSPOSE(CgGL.CG_GL_MATRIX_INVERSE_TRANSPOSE);
		
		private int cgID;
		
		private CCCGMatrixTransform(final int theCgID) {
			cgID = theCgID;
		}
	}
	
	public static interface CCCGShaderListener{
		public void onStart();
		
		public void onEnd();
	}
	
	private class CCCGMatrix{
		private CGparameter _myParameter;
		private CCCGMatrixType _myMatrixType;
		private CCCGMatrixTransform _myMatrixTransform;
		
		private CCCGMatrix(CGparameter theParameter, CCCGMatrixType theMatrixType, CCCGMatrixTransform theMatrixTransform) {
			_myParameter = theParameter;
			_myMatrixType = theMatrixType;
			_myMatrixTransform = theMatrixTransform;
		}
		
		private void update() {
			CgGL.cgGLSetStateMatrixParameter(_myParameter, _myMatrixType.cgID, _myMatrixTransform.cgID);
		}
	}
	
	/**
	 * context for managing the cg programs
	 */
	protected static CGcontext cg_context;

	/**
	 * Profile of the vertex programs
	 */
	private static int cg_vertex_profile;

	/**
	 * Profile of the fragment programs
	 */
	private static int cg_fragment_profile;

	/**
	 * CG vertex program to use
	 */
	private CGprogram _myVertexProgram;

	/**
	 * CG fragment program to use
	 */
	private CGprogram _myFragmentProgram;
	
	private List<CCCGMatrix> _myMatrices = new ArrayList<CCCGMatrix>();
	
	private List<CCCGShaderListener> _myListener = new ArrayList<CCCGShaderListener>();
	
	private Set<CGparameter> _myUsedTextureParameters = new HashSet<CGparameter>();

	/**
	 * Creates a new CG shader. Note that a shader does not necessarily a vertex and fragment program, though normally
	 * you work with combinations of both. If you only want to load a vertex program set the parameters for the fragment
	 * program null. You can also pass the name of the parameter for the modelview projection matrix.
	 * @param theVertexShaderFile
	 * @param theVertexEntry
	 * @param theFragmentShaderFile
	 * @param theFragmentEntry
	 */
	public CCCGShader(
		final Path[] theVertexShaderFile, final String theVertexEntry, 
		final Path[] theFragmentShaderFile, final String theFragmentEntry, 
		final String theMatrixParameter
	) {
		super(theVertexShaderFile, theVertexEntry, theFragmentShaderFile, theFragmentEntry);
		
		if(_myVertexProgram != null) {
			CGparameter myModelViewProjParameter = CgGL.cgGetNamedParameter(_myVertexProgram, theMatrixParameter);
			if(myModelViewProjParameter != null) {
				_myMatrices.add(new CCCGMatrix(myModelViewProjParameter, CCCGMatrixType.PROJECTION, CCCGMatrixTransform.IDENTITY));
			}
		}
	}
	
	public CCCGShader(
		final Path theVertexShaderFile, final String theVertexEntry, 
		final Path theFragmentShaderFile, final String theFragmentEntry, 
		final String theMatrixParameter
	) {
		this(new Path[] {theVertexShaderFile}, theVertexEntry, new Path[] {theFragmentShaderFile}, theFragmentEntry, theMatrixParameter);
	}
	
	public CCCGShader(
		final Path theVertexShaderFile,
		final Path theFragmentShaderFile,
		final String theMatrixParameter
	){
		this(theVertexShaderFile, null, theFragmentShaderFile,null, theMatrixParameter);
	}
	
	public CCCGShader(
		final Path theVertexShaderFile,final String theVertexEntry, 
		final Path theFragmentShaderFile, final String theFragmentEntry
	) {
		this(theVertexShaderFile, theVertexEntry, theFragmentShaderFile, theFragmentEntry, "ModelViewProj");
	}
	
	public CCCGShader(
		final Path[] theVertexShaderFile,
		final Path[] theFragmentShaderFile
	) {
		this(theVertexShaderFile, null, theFragmentShaderFile, null, "ModelViewProj");
	}
	
	public CCCGShader(
		final Path theVertexShaderFile,
		final Path theFragmentShaderFile
	) {
		this(theVertexShaderFile, null, theFragmentShaderFile, null, "ModelViewProj");
	}

	@Override
	public void initShader() {
		if(cg_context == null){
			cg_context = CgGL.cgCreateContext();
			
			checkError("Could not create CG Shader. Failed To Create Cg Context");
			
			// get the latest available vertex profile
			cg_vertex_profile = CgGL.cgGLGetLatestProfile(CgGL.CG_GL_VERTEX); 
			
			
			// check if we got a valid profile 
			checkError("Could not create CG shader. Invalid vertex profile type");

			// get the latest available vertex profile
			cg_fragment_profile = CgGL.cgGLGetLatestProfile(CgGL.CG_GL_FRAGMENT); 
			
			
			// check if we got a valid profile 
			checkError("Could not create CG shader. Invalid fragment profile type");
		}
	}

	@Override
	public void loadFragmentShader(final Path...theFiles) {
		if(theFiles == null || theFiles.length <= 0)return;
		_myFragmentProgram = loadShader(_myFragmentEntry, cg_fragment_profile, theFiles);
	}

	@Override
	public void loadVertexShader(final Path...theFiles) {
		if(theFiles == null || theFiles.length <= 0)return;
		_myVertexProgram = loadShader(_myVertexEntry, cg_vertex_profile, theFiles);
	}
	
	private CGprogram loadShader(final String theEntry, final int theProfile,final Path...theFiles){
		// set the current Profile
		CgGL.cgGLSetOptimalOptions(theProfile);
		CgGL.cgSetAutoCompile(cg_context, CgGL.CG_COMPILE_MANUAL);

		final String myShaderSource = buildSource(theFiles);
		
		final CGprogram myProgram = CgGL.cgCreateProgram(
			cg_context, 
			CgGL.CG_SOURCE,
			myShaderSource, 
			theProfile, 
			theEntry, 
			null
		);
		checkError("Cg error(s) in " + theFiles[0]);
		
		return myProgram;
	}

	public void checkError(final String theMessage) {
		final int myError = CgGL.cgGetError();
		
		if(myError != CgGL.CG_NO_ERROR){
			StringBuilder myStringBuilder = new StringBuilder(theMessage);
			myStringBuilder.append(CgGL.cgGetErrorString(myError));
			
			if(myError == CgGL.CG_COMPILER_ERROR){
				myStringBuilder.append(CgGL.cgGetLastListing(cg_context));
			}
			throw new CCShaderException(myStringBuilder.toString());
		}		
	}
	
	public void addMatrix(final String theParameterName, final CCCGMatrixType theMatrixType, final CCCGMatrixTransform theMatrixTransform) {
		_myMatrices.add(new CCCGMatrix(vertexParameter(theParameterName),theMatrixType, theMatrixTransform));
	}
	
	public void addFragmentMatrix(final String theParameterName, final CCCGMatrixType theMatrixType, final CCCGMatrixTransform theMatrixTransform) {
		_myMatrices.add(new CCCGMatrix(fragmentParameter(theParameterName),theMatrixType, theMatrixTransform));
	}
	
	public void addListener(final CCCGShaderListener theListener) {
		_myListener.add(theListener);
	}

	@Override
	public void load() {
		if(_myFragmentProgram != null)CgGL.cgGLLoadProgram(_myFragmentProgram); 
		checkError("loading fragment program");
		if(_myVertexProgram != null)CgGL.cgGLLoadProgram(_myVertexProgram);
		checkError("loading vertex program");
		
	}

	@Override
	public void start() {
		if(_myVertexProgram != null){
			//activate vertex shader profile
			CgGL.cgGLEnableProfile(cg_vertex_profile);
			checkError("enabling vertex profile");
			
			// bind the shader program 
			CgGL.cgGLBindProgram(_myVertexProgram); 
			checkError("binding vertex program");
		}
		

		if(_myFragmentProgram != null){
			//activate fragment shader profile
			CgGL.cgGLEnableProfile(cg_fragment_profile);
			checkError("enabling fragment profile");
			
			// bind the shader program 
			CgGL.cgGLBindProgram(_myFragmentProgram); 
			checkError("binding fragment program");
		}
		
		for(CGparameter myTextureParameter:_myUsedTextureParameters){
			CgGL.cgGLEnableTextureParameter(myTextureParameter);
		}
		
		
		
		for(CCCGShaderListener myListener:_myListener) {
			myListener.onStart();
		}
	}
	
	public void updateMatrices(){
		for(CCCGMatrix myMatrix:_myMatrices) {
			myMatrix.update();
		}
	}

	@Override
	public void end() {
		for(CGparameter myTextureParameter:_myUsedTextureParameters){
			CgGL.cgGLDisableTextureParameter(myTextureParameter);
		}
		
		if(_myVertexProgram != null){
			CgGL.cgGLDisableProfile(cg_vertex_profile);
			checkError("disabling vertex profile");
		}
		if(_myFragmentProgram != null){
			CgGL.cgGLDisableProfile(cg_fragment_profile);
			checkError("disabling fragment profile");
		}
		
		for(CCCGShaderListener myListener:_myListener) {
			myListener.onEnd();
		}
		
		
	}
	
	public CGprogram vertexProgram(){
		return _myVertexProgram;
	}
	
	public CGprogram fragmentProgram(){
		return _myFragmentProgram;
	}
	
	public CGparameter vertexParameter(String name) {
		CGparameter myResult = CgGL.cgGetNamedParameter(_myVertexProgram, name);
		checkError("could not get vertex parameter");
		return myResult;
	}

	public CGparameter fragmentParameter(String name) {
		CGparameter myResult = CgGL.cgGetNamedParameter(_myFragmentProgram, name);
		checkError("could not get fragment parameter: " + name + " : ");
		return myResult;
	}
	
	public CGparameter createFragmentParameter(final String theType){
		int type = CgGL.cgGetNamedUserType(fragmentProgram().getBuffer(), theType);
		return CgGL.cgCreateParameter(cg_context, type);
	}
	
	public CGparameter createVertexParameter(final String theType){
		int type = CgGL.cgGetNamedUserType(vertexProgram().getBuffer(), theType);
		return CgGL.cgCreateParameter(cg_context, type);
	}
	
	public void parameter(final CGparameter theParameter, final int theValue){
		CgGL.cgSetParameter1i(theParameter, theValue);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final double theValue){
		CgGL.cgSetParameter1d(theParameter, theValue);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final double theV1, final double theV2){
		CgGL.cgSetParameter2d(theParameter, theV1, theV2);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final double theV1, final double theV2, final double theV3){
		CgGL.cgSetParameter3d(theParameter, theV1, theV2, theV3);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final double theV1, final double theV2, final double theV3, final double theV4){
		CgGL.cgSetParameter4d(theParameter, theV1, theV2, theV3, theV4);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final CCVector3 theVector){
		CgGL.cgSetParameter3d(theParameter, theVector.x, theVector.y, theVector.z);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final boolean theValue){
		if(theValue)CgGL.cgSetParameter1i(theParameter, 1);
		else CgGL.cgSetParameter1i(theParameter, 0);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final CCVector2 theVector){
		CgGL.cgSetParameter2d(theParameter, theVector.x, theVector.y);
		checkError("Problem setting parameters ");
	}
	
	public void parameter(final CGparameter theParameter, final CCColor theColor){
		CgGL.cgSetParameter4d(theParameter, theColor.r, theColor.g, theColor.b, theColor.a);
		checkError("Problem setting parameters ");
	}
	
	public void parameter1(final CGparameter theParameter, final List<Double> theParameterValues) {
		DoubleBuffer myValueBuffer = DoubleBuffer.allocate(theParameterValues.size());
		for(Double myValue:theParameterValues) {
			myValueBuffer.put(myValue);
		}
		myValueBuffer.flip();
		
		CgGL.cgGLSetParameterArray1d(theParameter, 0, theParameterValues.size(), myValueBuffer);
	}
	
	public void parameter1(final CGparameter theParameter, final double...theParameterValues) {
		DoubleBuffer myValueBuffer = DoubleBuffer.wrap(theParameterValues);
		myValueBuffer.flip();
		
		CgGL.cgGLSetParameterArray1d(theParameter, 0, theParameterValues.length, myValueBuffer);
	}
	
	public void parameter2(final CGparameter theParameter, final List<CCVector2> theParameterValues) {
		DoubleBuffer myValueBuffer = DoubleBuffer.allocate(theParameterValues.size() * 2);
		for(CCVector2 myVector:theParameterValues) {
			myValueBuffer.put(myVector.x);
			myValueBuffer.put(myVector.y);
		}
		myValueBuffer.flip();
		
		CgGL.cgGLSetParameterArray2d(theParameter, 0, theParameterValues.size(), myValueBuffer);
	}
	
	public void parameter2(final CGparameter theParameter, final CCVector2[] theParameterValues) {
		DoubleBuffer myValueBuffer = DoubleBuffer.allocate(theParameterValues.length * 2);
		for(CCVector2 myVector:theParameterValues) {
			myValueBuffer.put(myVector.x);
			myValueBuffer.put(myVector.y);
		}
		myValueBuffer.flip();
		
		CgGL.cgGLSetParameterArray2d(theParameter, 0, theParameterValues.length, myValueBuffer);
	}
	
	public void parameter3(final CGparameter theParameter, final List<CCVector3> theParameterValues) {
		DoubleBuffer myValueBuffer = DoubleBuffer.allocate(theParameterValues.size() * 3);
		for(CCVector3 myVector:theParameterValues) {
			myValueBuffer.put(myVector.x);
			myValueBuffer.put(myVector.y);
			myValueBuffer.put(myVector.z);
		}
		myValueBuffer.flip();
		
		CgGL.cgGLSetParameterArray3d(theParameter, 0, theParameterValues.size(), myValueBuffer);
	}
	
	public void matrix(final CGparameter theParameter, final CCMatrix4x4 theMatrix) {
		CgGL.cgSetMatrixParameterdc(theParameter, theMatrix.toDoubleBuffer());
	}
	
	public void matrix(final CGparameter theParameter, final CCCGMatrixType theMatrixType, final CCCGMatrixTransform theMatrixTransform) {
		CgGL.cgGLSetStateMatrixParameter(theParameter, theMatrixType.cgID, theMatrixTransform.cgID);
	}
	
	/**
	 * Sets the texture for a given cgparameter. Be aware that you have to call
	 * this before you call the start method of the shader. As all textures get
	 * automatically enabled in the start method. 
	 * @param theParameter cg parameter
	 * @param theTextureID id of the texture
	 */
	public void texture(final CGparameter theParameter, int theTextureID){
		CgGL.cgGLSetTextureParameter(theParameter, theTextureID);
		checkError("Problem setting texture ");
//	    CgGL.cgGLEnableTextureParameter(theParameter);
	    _myUsedTextureParameters.add(theParameter);
	}
	
	public void destroy(){
		CgGL.cgDestroyContext(cg_context);
	}
	
	@Override
	public void finalize(){
		if(_myVertexProgram != null)CgGL.cgDestroyProgram(_myVertexProgram);
		if(_myFragmentProgram != null)CgGL.cgDestroyProgram(_myFragmentProgram);
	}
}
