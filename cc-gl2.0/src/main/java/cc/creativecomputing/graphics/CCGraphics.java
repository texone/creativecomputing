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
package cc.creativecomputing.graphics;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCEndianess;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.gl.app.CCGLGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCGlutFont.CCGlutFontType;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.TraceGL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.glu.GLUtessellator;


/**
 * This class represents the render module for creative computing
 * it contains all methods for drawing and is completely based on
 * OPENGL. OpenGL is strictly defined as "a software interface to 
 * graphics hardware." It is a 3D graphics and modeling library that 
 * is highly portable and very fast. Using OpenGL, you can create 
 * elegant and beautiful 3D graphics with nearly the visual quality 
 * of a ray tracer. Creative computing uses jogl as interface to 
 * OpenGL. It is aimed to simplify the access to OpengGL.
 * 
 * Every instance of CCApp has an instance of CCGraphics that
 * can be used for drawing.
 * @see CCApp 
 */
public class CCGraphics extends CCGLGraphics<GL2>{
	
	public static CCGraphics instance;
	
	private static boolean debug = false;
	
	/**
	 * Changes the plain gl implementation with a composable pipeline which wraps 
	 * an underlying GL implementation, providing error checking after each OpenGL 
	 * method call. If an error occurs, causes a GLException to be thrown at exactly 
	 * the point of failure. 
	 */
	public static void debug() {
		debug = true;
	}
	
	/**
	 * Ends debugging.
	 * @see #debug()
	 */
	public void noDebug() {
		debug = false;
	}
	
	public static GL2 currentGL() {
		try{
			if(!debug)return GLU.getCurrentGL().getGL2();
			else return new DebugGL2(GLU.getCurrentGL().getGL2());
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static GLU currentGLU() {
		return GLU.createGLU();
	}

	private CCCamera _myCamera;
	
	private GL2 _myPlainGL;
	
	private DebugGL2 _myDebugGL;
	
	@SuppressWarnings("unused")
	private TraceGL2 _myTraceGL2;

	GLUtessellator tobj;

	

	/**
	 * Stores the number of lights supported by the OPENGL device
	 */
	private int MAX_LIGHTS;

	/**
	 * Quadratic object for drawing primitives and to define how 
	 * they have to be drawn
	 */
	private GLUquadric quadratic;
	
	/**
	 * Gives you the possibility to directly access OPENGLs utility functions
	 */
	public GLU glu;

	
	protected CCTexture[] _myTextures;
	
	

	public CCGraphics(final GL2 theGL, int theWidth, int theHeight){
		super(theGL, theWidth, theHeight);
		gl = theGL;
		glu = new GLU();
		
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing (Less Or Equal)
		gl.glEnable(GL.GL_DEPTH_TEST); // Enable Depth Testing

		// these are necessary for alpha (i.e. fonts) to work
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		instance = this;
		_myPlainGL = theGL;
		quadratic = glu.gluNewQuadric();
		MAX_LIGHTS = 8;
		
	    gl.glShadeModel(GL2.GL_SMOOTH);									// Select Smooth Shading
	    gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);			// Set Perspective Calculations To Most Accurate
		

		// 
		frontFace(CCFace.COUNTER_CLOCK_WISE);

		//setup up default lighting
		lighting = false;
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
		for (int i = 0; i < MAX_LIGHTS; i++){
			gl.glDisable(GLLightingFunc.GL_LIGHT0 + i);
		}
		gl.glEnable(GL2.GL_NORMALIZE);


		colorMaterial(CCColorMaterialMode.AMBIENT_AND_DIFFUSE);
		
		lightModelTwoSide(true);
		
		int myTextureUnits = textureUnits();
		_myTextures = new CCTexture[myTextureUnits];
	}
	
	// /////////////////////////////////////////////////
		//
		// OPENGL INFORMATIONS
		//
		// ////////////////////////////////////////////////

		private int[] _myIntegerGet = new int[1];

		public int getInteger(int theGLIID) {
			gl.glGetIntegerv(theGLIID, _myIntegerGet, 0);
			return _myIntegerGet[0];
		}

		public IntBuffer getIntBuffer(int theGLID, int theNumberOfValues) {
			final IntBuffer myResult = IntBuffer.allocate(theNumberOfValues);
			gl.glGetIntegerv(theGLID, myResult);
			myResult.rewind();
			return myResult;
		}

		public int[] getIntArray(int theGLID, int theNumberOfValues) {
			int[] result = new int[theNumberOfValues];
			gl.glGetIntegerv(theGLID, result, 0);
			return result;
		}

		private float[] _myFloatGet = new float[1];

		public float getFloat(int theGLIID) {
			gl.glGetFloatv(theGLIID, _myFloatGet, 0);
			return _myFloatGet[0];
		}

		public FloatBuffer getFloatBuffer(int theGLID, int theNumberOfValues) {
			final FloatBuffer myResult = FloatBuffer.allocate(theNumberOfValues);
			gl.glGetFloatv(theGLID, myResult);
			myResult.rewind();
			return myResult;
		}
		
		public DoubleBuffer getDoubleBuffer(int theGLID, int theNumberOfValues) {
			final DoubleBuffer myResult = DoubleBuffer.allocate(theNumberOfValues);
			gl.glGetDoublev(theGLID, myResult);
			myResult.rewind();
			return myResult;
		}

		public float[] getFloatArray(int theGLID, int theNumberOfValues) {
			float[] result = new float[theNumberOfValues];
			gl.glGetFloatv(theGLID, result, 0);
			return result;
		}

		public String getString(int theGLID) {
			return gl.glGetString(theGLID);
		}

		/**
		 * Returns the name of the hardware vendor.
		 * 
		 * @return the name of the hardware vendor
		 */
		public String vendor() {
			return getString(GL.GL_VENDOR);
		}

		/**
		 * Returns a brand name or the name of the vendor dependent on the OPENGL implementation.
		 * 
		 * @return brand name or name of the vendor
		 */
		public String renderer() {
			return getString(GL.GL_RENDERER);
		}

		/**
		 * returns the version number followed by a space and any vendor-specific information.
		 * 
		 * @return the version number
		 */
		public String version() {
			return getString(GL.GL_VERSION);
		}

		/**
		 * Returns an array with all the extensions that are available on the current hardware setup.
		 * 
		 * @return the available extensions
		 */
		public String[] extensions() {
			return getString(GL.GL_EXTENSIONS).split(" ");
		}

		/**
		 * Returns true if the given extension is available at the current hardware setup.
		 * 
		 * @param theExtension extension to check
		 * @return true if the extension is available otherwise false
		 */
		public boolean isExtensionSupported(final String theExtension) {
			for (String myExtension : extensions()) {
				if (myExtension.equals(theExtension))
					return true;
			}
			return false;
		}

		/**
		 * true if you want to report that no error occurred
		 */
		private boolean _myReportNoError = false;
		protected boolean _myReportErrors = true;

		/**
		 * Call this method to check for drawing errors. cc checks for drawing errors at the end of each frame
		 * automatically. However only the last error will be reported. You can call this method for debugging to find where
		 * errors occur. Error codes are cleared when checked, and multiple error flags may be currently active. To retrieve
		 * all errors, call this function repeatedly until you get no error.
		 * 
		 * @shortdesc Use this method to check for drawing errors.
		 */
		public void checkError(final String theString) {
			switch (gl.glGetError()) {
			case GL.GL_NO_ERROR:
				if (_myReportNoError)
					CCLog.error(theString + " # NO ERROR REPORTED");
				return;
			case GL.GL_INVALID_ENUM:
				CCLog.error(theString + " # INVALID ENUMERATION REPORTED. check for errors in OPENGL calls with constants.");
				return;
			case GL.GL_INVALID_VALUE:
				CCLog.error(theString + "# INVALID VALUE REPORTED. check for errors with passed values that are out of a defined range.");
				return;
			case GL.GL_INVALID_OPERATION:
				CCLog.error(theString + "# INVALID OPERATION REPORTED. check for function calls that are invalid in the current graphics state.");
				return;
			
			case GL2ES1.GL_STACK_OVERFLOW:
				CCLog.error(theString + "# STACK OVERFLOW REPORTED. check for errors in matrix operations");
				return;
			case GL2ES1.GL_STACK_UNDERFLOW:
				CCLog.error(theString + "# STACK UNDERFLOW REPORTED. check for errors  in matrix operations");
				return;
			
			case GL.GL_OUT_OF_MEMORY:
				CCLog.error(theString + "# OUT OF MEMORY. not enough memory to execute the commands");
				return;
			case GL2.GL_TABLE_TOO_LARGE:
				CCLog.error(theString + "# TABLE TOO LARGE.");
				return;
			}
		}

		public void checkError() {
			checkError("");
		}

		/**
		 * Use this method to tell cc if it should report no error
		 * 
		 * @param theReportNoError
		 */
		public void reportNoError(final boolean theReportNoError) {
			_myReportNoError = theReportNoError;
		}

		public void reportError(final boolean theReportError) {
			_myReportErrors = theReportError;
		}
	
	

	protected boolean displayed = false;
	
	///////////////////////////////////////////////////
	//
	// GRAPHICS SETUP
	//
	//////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.CCAbstractGraphics#reshape()
	 */
	@Override
	public void reshape(int theX, int theY, int theWidth, int theHeight) {
		super.reshape(theX, theY, theWidth, theHeight);
		
		_myCamera = new CCCamera(this);
		camera().viewport(new CCViewport(theX, theY, width(), height()));
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.CCAbstractGraphics#updateGL(javax.media.opengl.GLAutoDrawable)
	 */
	public void updateGL(GLAutoDrawable theDrawable) {
		gl = theDrawable.getGL().getGL2();
	}

	boolean firstFrame = true;

	/**
	 * @invisible
	 */
	public void beginDraw(){
		if(debug) {
			if(_myDebugGL == null)_myDebugGL = new DebugGL2(_myPlainGL);
			gl = _myDebugGL;
		}else {
			gl = _myPlainGL;
		}
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

		//gl.glLoadIdentity();
		pushMatrix();
		if(_myCamera != null)_myCamera.draw(this);
	}

	/**
	 * @invisible
	 */
	public void endDraw(){
		popMatrix();
		
		if(_myReportErrors)checkError();
	}
	
	/**
	 * colorMask specifies whether the individual color components in the frame buffer 
	 * can or cannot be written. If theMaskRed is false, for example, no change is made to the red
	 * component of any pixel in any of the color buffers, regardless of the drawing operation attempted.
	 * @param theMaskRed
	 * @param theMaskGreen
	 * @param theMaskBlue
	 * @param theMaskAlpha
	 */
	public void colorMask(final boolean theMaskRed, final boolean theMaskGreen, final boolean theMaskBlue, final boolean theMaskAlpha) {
		gl.glColorMask(theMaskRed, theMaskGreen, theMaskBlue, theMaskAlpha);
	}
	
	/**
	 * Disables a previous color mask.
	 */
	public void noColorMask() {
		colorMask(true, true, true, true);
	}

	/**
	 * Use this method to define a mask. You can use all available draw methods
	 * after it. After calling endMask everything drawn will be masked by the
	 * defined mask.
	 */
	public void beginMask(){
//		colorMask(false, false, false, false);
//		gl.glClearStencil(0x1);
//		gl.glEnable(GL.GL_STENCIL_TEST);
//        gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
//        gl.glStencilFunc (GL.GL_ALWAYS, 0x1, 0x1);
//        gl.glStencilOp (GL.GL_REPLACE, GL.GL_REPLACE, GL.GL_REPLACE);
        
        gl.glEnable(GL.GL_STENCIL_TEST);
        gl.glColorMask(false, false, false, false);
        gl.glStencilFunc(GL.GL_NEVER, 1, 0xFF);
        gl.glStencilOp(GL.GL_REPLACE, GL.GL_KEEP, GL.GL_KEEP);  // draw 1s on test fail (always)

        // draw stencil pattern
        gl.glStencilMask(0xFF);
        gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
        
	}

	/**
	 * Ends the mask
	 */
	public void endMask(){
//		colorMask(true, true, true, true);
//		gl.glStencilFunc (GL.GL_NOTEQUAL, 0x1, 0x1);
//		gl.glStencilOp (GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		
		gl.glColorMask(true, true, true, true);
		gl.glStencilMask(0x00);
		  // draw where stencil's value is 0
		gl.glStencilFunc(GL.GL_EQUAL, 0, 0xFF);
		  /* (nothing to draw) */
		  // draw only where stencil's value is 1
		gl.glStencilFunc(GL.GL_EQUAL, 1, 0xFF);
	}

	/**
	 * Disables a mask once you have defined one using beginMask and endMask
	 */
	public void noMask(){
		gl.glDisable(GL.GL_STENCIL_TEST);
	}
	
	public void scissor(final int theX, final int theY, final int theWidth, final int theHeight) {
		gl.glEnable(GL.GL_SCISSOR_TEST);
		gl.glScissor(theX, theY, theWidth, theHeight);
	}
	
	public void noScissor() {
		gl.glDisable(GL.GL_SCISSOR_TEST);
	}
	
	/**
	 * Specifies the depth comparison function.
	 * @author Riekoff
	 *
	 */
	public static enum CCDepthFunc{
		/**
		 * Never passes.
		 */
		NEVER(GL.GL_NEVER),
		/**
		 * Passes if the incoming depth value is less than the stored depth value.
		 */
		ALWAYS(GL.GL_ALWAYS),
		/**
		 * Passes if the incoming depth value is equal to the stored depth value.
		 */
		LESS(GL.GL_LESS),
		/**
		 * Passes if the incoming depth value is less than or equal to the stored depth value.
		 */
		LESS_EQUAL(GL.GL_LEQUAL),
		/**
		 * Passes if the incoming depth value is greater than the stored depth value.
		 */
		GREATER(GL.GL_GREATER),
		/**
		 * Passes if the incoming depth value is greater than or equal to the stored depth value.
		 */
		GREATER_EQUAL(GL.GL_GEQUAL),
		/**
		 * Passes if the incoming depth value is equal to the stored depth value.
		 */
		EQUAL(GL.GL_EQUAL),
		/**
		 * Passes if the incoming depth value is not equal to the stored depth value.
		 */
		NOT_EQUAL(GL.GL_NOTEQUAL);
		
		private final int glID;
		
		CCDepthFunc(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * Specifies the function used to compare each incoming pixel depth 
	 * value with the depth value present in the depth buffer. The comparison 
	 * is performed only if depth testing is enabled.
	 * </p>
	 * <p>
	 * The initial value of func is LESS_EQUAL. Initially, depth testing is disabled. 
	 * If depth testing is disabled or if no depth buffer exists, it is as if the depth test always passes.
	 * @param theCompare Specifies the depth comparison function.
	 */
	public void depthFunc(final CCDepthFunc theCompare){
		gl.glDepthFunc(theCompare.glID);
	}
	
	public void depthTest(){
		gl.glEnable(GL.GL_DEPTH_TEST);
	}
	
	public void noDepthTest(){
		gl.glDisable(GL.GL_DEPTH_TEST);
	}
	
	public void depthMask(){
		gl.glDepthMask(true);
	}

	public void noDepthMask(){
		gl.glDepthMask(false);
	}
	
	/**
	 * Clears the depth buffer
	 */
	public void clearDepthBuffer(){
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearDepth(final double theDefaultDepth){
		gl.glClearDepth(theDefaultDepth);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  STENCIL OPERATIONS
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * specifies the index used by clearStencil() to clear the stencil buffer. 
	 * s is masked with 2 m - 1 , where m is the number of bits in the stencil buffer.
	 * @param theS Specifies the index used when the stencil buffer is cleared. The initial value is 0.
	 */
	public void clearStencil(int theS){
		gl.glClearStencil(theS);
	}
	
	/**
	 * Clears the stencil buffer.
	 */
	public void clearStencilBuffer(){
		gl.glClear(GL.GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void stencilTest(){
		gl.glEnable(GL.GL_STENCIL_TEST);
	}

	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void noStencilTest(){
		gl.glDisable(GL.GL_STENCIL_TEST);
	}
	
	/**
	 * Specifies thestencil  test function.
	 * @author christianriekoff
	 *
	 */
	public static enum CCStencilFunction{
		/**
		 * Always fails.
		 */
		NEVER(GL.GL_NEVER),
		/**
		 * Passes if ( ref & mask ) < ( stencil & mask ).
		 */
		LESS(GL.GL_LESS),
		
		/**
		 * Passes if ( ref & mask ) <= ( stencil & mask ).
		 */
		LESS_EQUAL(GL.GL_LEQUAL),
		/**
		 * Passes if ( ref & mask ) > ( stencil & mask ).
		 */
		GREATER(GL.GL_GREATER),
		/**
		 * Passes if ( ref & mask ) >= ( stencil & mask ).
		 */
		GREATER_EQUAL(GL.GL_GEQUAL),
		/**
		 * Passes if ( ref & mask ) = ( stencil & mask ).
		 */
		EQUAL(GL.GL_EQUAL),
		/**
		 * Passes if ( ref & mask ) != ( stencil & mask ).
		 */
		NOT_EQUAL(GL.GL_NOTEQUAL),
		/**
		 * Always passes.
		 */
		ALWAYS(GL.GL_ALWAYS);
		
		
		private final int glID;
		
		CCStencilFunction(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * Stenciling, like depth-buffering, enables and disables drawing on a per-pixel basis. 
	 * Stencil planes are first drawn into using GL drawing primitives, then geometry and 
	 * images are rendered using the stencil planes to mask out portions of the screen. 
	 * Stenciling is typically used in multipass rendering algorithms to achieve special 
	 * effects, such as decals, outlining, and constructive solid geometry rendering.
	 * </p>
	 * <p>
	 * The stencil test conditionally eliminates a pixel based on the outcome of a comparison 
	 * between the reference value and the value in the stencil buffer. To enable and disable 
	 * the test, call {@linkplain #stencilTest()} and {@linkplain #noStencilTest()}. To specify 
	 * actions based on the outcome of the stencil test, call glStencilOp or glStencilOpSeparate.
	 * </p>
	 * <p>
	 * There can be two separate sets of func, ref, and mask parameters; one affects back-facing 
	 * polygons, and the other affects front-facing polygons as well as other non-polygon primitives. 
	 * glStencilFunc sets both front and back stencil state to the same values. Use glStencilFuncSeparate 
	 * to set front and back stencil state to different values.
	 * </p>
	 * <p>
	 * func is a symbolic constant that determines the stencil comparison function. It accepts 
	 * one of eight values, shown in the following list. ref is an integer reference value that 
	 * is used in the stencil comparison. It is clamped to the range 0 2 n - 1 , where n is the 
	 * number of bitplanes in the stencil buffer. mask is bitwise ANDed with both the reference 
	 * value and the stored stencil value, with the ANDed values participating in the comparison.
	 * </p>
	 * <p>
	 * If stencil represents the value stored in the corresponding stencil buffer location, the 
	 * following list shows the effect of each comparison function that can be specified by func. 
	 * Only if the comparison succeeds is the pixel passed through to the next stage in the 
	 * rasterization process (see glStencilOp). All tests treat stencil values as unsigned 
	 * integers in the range 0 2 n - 1 , where n is the number of bitplanes in the stencil buffer.
	 * </p>
	 * @param theFunc Specifies the test function. Eight symbolic constants are valid: 
	 * @param theRef 
	 * 		Specifies the reference value for the stencil test. ref is clamped to the range 
	 * 		0 2 n - 1 , where n is the number of bitplanes in the stencil buffer. The initial value is 0.
	 * @param theMask
	 * 		Specifies a mask that is ANDed with both the reference value and the stored stencil value when 
	 * 		the test is done. The initial value is all 1's.
	 */
	public void stencilFunc(CCStencilFunction theFunc, int theRef, int theMask){
		gl.glStencilFunc(theFunc.glID, theRef, theMask);
	}
	
	/**
	 * Specifies thestencil  test function.
	 * @author christianriekoff
	 *
	 */
	public static enum CCStencilOperation{
		/**
		 * Keeps the current value.
		 */
		KEEP(GL.GL_KEEP),
		/**
		 * Sets the stencil buffer value to 0.
		 */
		ZERO(GL.GL_ZERO),
		
		/**
		 * Sets the stencil buffer value to ref, as specified by stencilFunction.
		 */
		REPLACE(GL.GL_REPLACE),
		/**
		 * Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.
		 */
		INCREMENT(GL.GL_INCR),
		/**
		 * Increments the current stencil buffer value. Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.
		 */
		INCREMENT_WRAP(GL.GL_INCR_WRAP),
		/**
		 * Decrements the current stencil buffer value. Clamps to 0.
		 */
		DECREMENT(GL.GL_DECR),
		/**
		 * Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.
		 */
		DECREMENT_WRAP(GL.GL_DECR_WRAP),
		/**
		 * Bitwise inverts the current stencil buffer value.
		 */
		INVERT(GL.GL_INVERT);
		
		
		private final int glID;
		
		CCStencilOperation(final int theGlID){
			glID = theGlID;
		}
	}
	
	public void stencilOperation(
		CCStencilOperation theStencilTestFailOp,
		CCStencilOperation theDepthTestFailOp,
		CCStencilOperation ThePassOp
	){
		gl.glStencilOp(theStencilTestFailOp.glID, theDepthTestFailOp.glID, ThePassOp.glID);
	}
	
	public void stencilOperation(CCStencilOperation theOperation){
		stencilOperation(theOperation,theOperation,theOperation);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////	
	//
	//  BLENDING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * All possible blend factors
	 * @author info
	 *
	 */
	static public enum CCBlendFactor{
		/**
		 * (0,0,0,0)
		 */
		ZERO(GL.GL_ZERO),
		/**
		 * (1,1,1,1)
		 */
        ONE	(GL.GL_ONE),
        /**
         * (Rs,Gs,Bs,As)
         */
        SRC_COLOR(GL.GL_SRC_COLOR),
        /**
         * (1 - Rs, 1 - Gs, 1 - Bs, 1 - As)
         */
        ONE_MINUS_SRC_COLOR(GL.GL_ONE_MINUS_SRC_COLOR),
        /**
         * (Rd, Gd, Bd, Ad)
         */
        DST_COLOR(GL.GL_DST_COLOR),
        /**
         * (1 - Rd, 1 - Gd, 1 - Bd, 1 - Ad)
         */
        ONE_MINUS_DST_COLOR(GL.GL_ONE_MINUS_DST_COLOR),
        /**
         * (As, As, As, As)
         */
        SRC_ALPHA(GL.GL_SRC_ALPHA),
        /**
         * (1 - As, 1 - As, 1 - As, 1 - As)
         */
        ONE_MINUS_SRC_ALPHA(GL.GL_ONE_MINUS_SRC_ALPHA),
        /**
         * (Ad, Ad, Ad, Ad)
         */
        DST_ALPHA(GL.GL_DST_ALPHA),
        /**
         * (1 - Ad, 1 - Ad, 1 - Ad, 1 - Ad)
         */
        ONE_MINUS_DST_ALPHA(GL.GL_ONE_MINUS_DST_ALPHA),
        /**
         * Constant color is set by blendColor()
         * (Rbc, Gbc, Bbc, Abc)
         */
        CONSTANT_COLOR(GL2.GL_CONSTANT_COLOR),
        /**
         * Constant color is set by blendColor()
         * (1 - Rbc, 1 - Gbc, 1 - Bbc, 1 - Abc)
         */
        ONE_MINUS_CONSTANT_COLOR(GL2.GL_ONE_MINUS_CONSTANT_COLOR),
        /**
         * Constant color is set by blendColor()
         * (Abc, Abc, Abc, Abc)
         */
        CONSTANT_ALPHA(GL2.GL_CONSTANT_ALPHA),
        /**
         * Constant color is set by blendColor()
         * (1 - Abc, 1 - Abc, 1 - Abc, 1 - Abc)
         */
        ONE_MINUS_CONSTANT_ALPHA(GL2.GL_ONE_MINUS_CONSTANT_ALPHA),
        SRC_ALPHA_SATURATE(GL.GL_SRC_ALPHA_SATURATE);
        
		private final int glId;
		  
		private CCBlendFactor(final int theglID){
			glId = theglID;
		}
	}
	
	public static enum CCBlendEquation{
		ADD(GL.GL_FUNC_ADD), 
		SUBTRACT(GL.GL_FUNC_SUBTRACT),
        REVERSE_SUBTRACT(GL.GL_FUNC_REVERSE_SUBTRACT), 
        DARKEST(GL2.GL_MIN), 
        LIGHTEST(GL2.GL_MAX);
    
		private final int glId;
		  
		private CCBlendEquation(final int theglID){
			glId = theglID;
		}
	}
	
	/**
	 * blendColor may be used to calculate the source and destination
	 * blending factors. The color components are clamped to the range {0,1}
	 * before being stored. See beginBlend for a complete description of the
	 * blending operations. Initially the blend color is set to (0, 0, 0, 0).
	 * @param theColor
	 * @see #beginBlend()
	 */
	public void blendColor(final CCColor theColor) {
		gl.glBlendColor((float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a);
	}
	
	/**
	 * <p>
	 * In RGBA mode, pixels can be drawn using a function that blends
	 * the incoming (source) RGBA values with the RGBA values that are 
	 * already in the frame buffer (the destination values). Blending is 
	 * initially disabled. Use <code>blend()</code> and <code>noBlend()</code>
	 * to enable and disable blending.
	 * </p>
	 * <p>
	 * <code>blendMode</code> defines the operation of blending when it is enabled.
	 * sfactor specifies which method is used to scale the source color 
	 * components. dfactor specifies which method is used to scale the
	 * destination color components. The possible methods are described 
	 * in <code>CCBlendFactor</code> enumeration. Each method defines 
	 * four scale factors, one each for red, green, blue, and alpha.
	 * You can set different factors for the rgb and the alpha component.
	 * </p>
	 * <p>
	 * The blend equations determines how a new pixel (the ''source'' color)
	 * is combined with a pixel already in the framebuffer (the ''destination'' color).  
	 * The default equation is add, you can set this value to work equally on RGBA or
	 * set two different equations for RGB and alpha. The blend equations use the 
	 * specified source and destination blend factors
	 * </p>
	 * 
	 * @param theSrcFactor Specifies how the red, green, blue and alpha source blending factors are computed.
	 * @param theDstFactor Specifies how the red, green, blue and alpha destination blending factors are computed.
	 */
	public void blendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		
		gl.glBlendFunc(theSrcFactor.glId, theDstFactor.glId);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
	/**
	 * @param theSrcFactor Specifies how the red, green, blue and alpha source blending factors are computed.
	 * @param theDstFactor Specifies how the red, green, blue and alpha destination blending factors are computed.
	 * @param theEquation specifies how source and destination colors are combined
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, 
		final CCBlendFactor theDstFactor,
		final CCBlendEquation theEquation
	) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		
		gl.glBlendFunc(theSrcFactor.glId, theDstFactor.glId);
		gl.glBlendEquation(theEquation.glId);
	}
	
	/**
	 * @param theSrcFactor Specifies how the red, green, and blue blending factors are computed
	 * @param theDstFactor Specifies how the red, green, and blue destination blending factors are computed
	 * @param theSrcAlphaFactor Specified how the alpha source blending factor is computed
	 * @param theDstAlphaFactor Specified how the alpha destination blending factor is computed
	 * @param theColorEquation specifies the RGB blend equation, how the red, green and blue 
	 * 						  	components of the source and destination colors are combined
	 * @param theAlphaEquation specifies the alpha blend equation, how the alpha component of
	 * 							 the source and destination colors are combined
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, 
		final CCBlendFactor theSrcAlphaFactor, final CCBlendFactor theDstAlphaFactor,
		final CCBlendEquation theColorEquation, final CCBlendEquation theAlphaEquation
	) {
		if(theDstFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination factor");
		if(theDstAlphaFactor == CCBlendFactor.SRC_ALPHA_SATURATE)
			throw new CCGraphicsException("SRC_ALPHA_SATURATE is not available as destination alpha factor");
		
		gl.glBlendFuncSeparate(theSrcFactor.glId, theDstFactor.glId, theSrcAlphaFactor.glId, theDstAlphaFactor.glId);
		gl.glBlendEquationSeparate(theColorEquation.glId, theAlphaEquation.glId);
	}
	
	/**
	 * @param theSrcFactor factor for source rgb
	 * @param theDstFactor factor for destination rgb
	 * @param theSrcAlphaFactor factor for source alpha
	 * @param theDstAlphaFactor factor for destination alpha
	 */
	public void blendMode(
		final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, 
		final CCBlendFactor theSrcAlphaFactor, final CCBlendFactor theDstAlphaFactor
	) {
		gl.glBlendFuncSeparate(theSrcFactor.glId, theDstFactor.glId, theSrcAlphaFactor.glId, theDstAlphaFactor.glId);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
// blend mode keyword definitions

	  public final static CCBlendMode ADD        = CCBlendMode.ADD;
	  public final static CCBlendMode SUBTRACT   = CCBlendMode.SUBTRACT;
	  public final static CCBlendMode LIGHTEST   = CCBlendMode.LIGHTEST;
	  public final static CCBlendMode DARKEST    = CCBlendMode.DARKEST;
	  
	  /**
	   * Enumeration to collect useful blend settings
	   * @author info
	   *
	   */
	public static enum CCBlendMode {
		ALPHA(CCBlendFactor.ONE, CCBlendFactor.ONE_MINUS_SRC_ALPHA, CCBlendEquation.ADD), 
		BLEND(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE_MINUS_SRC_ALPHA, CCBlendEquation.ADD), 
		REPLACE(CCBlendFactor.ONE, CCBlendFactor.ZERO, CCBlendEquation.ADD), 
		ADD(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.ADD), 
		REVERSE_SUBTRACT(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.REVERSE_SUBTRACT), 
		SUBTRACT(CCBlendFactor.SRC_ALPHA, CCBlendFactor.ONE, CCBlendEquation.SUBTRACT), 
		LIGHTEST(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.LIGHTEST), 
		DARKEST(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.DARKEST), 
		DARKEST_ALPHA(CCBlendFactor.SRC_COLOR, CCBlendFactor.DST_COLOR, CCBlendEquation.DARKEST);

		private final CCBlendFactor _mySrcFactor;
		private final CCBlendFactor _myDstFactor;
		private final CCBlendFactor _mySrcAlphaFactor;
		private final CCBlendFactor _myDstAlphaFactor;
		private final CCBlendEquation _myEquation;
		private final CCBlendEquation _myAlphaEquation;

		private CCBlendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, final CCBlendEquation theEquation) {
			_mySrcFactor = theSrcFactor;
			_myDstFactor = theDstFactor;
			_mySrcAlphaFactor = theSrcFactor;
			_myDstAlphaFactor = theDstFactor;
			_myEquation = theEquation;
			_myAlphaEquation = theEquation;
		}

		private CCBlendMode(final CCBlendFactor theSrcFactor, final CCBlendFactor theDstFactor, final CCBlendEquation theEquation, final CCBlendFactor theSrcAlphaFactor,
				final CCBlendFactor theDstAlphaFactor, final CCBlendEquation theAlphaEquation) {
			_mySrcFactor = theSrcFactor;
			_myDstFactor = theDstFactor;
			_mySrcAlphaFactor = theSrcAlphaFactor;
			_myDstAlphaFactor = theDstAlphaFactor;
			_myEquation = theEquation;
			_myAlphaEquation = theAlphaEquation;
		}
	}

	public void blendMode(final CCBlendMode theBlendMode){
		blendMode(
			theBlendMode._mySrcFactor, theBlendMode._myDstFactor, 
			theBlendMode._mySrcAlphaFactor, theBlendMode._myDstAlphaFactor,
			theBlendMode._myEquation, theBlendMode._myAlphaEquation
		);
	}

	public void endBlend(){
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBlendEquation(GL.GL_FUNC_ADD);
	}
	
	public void blend(){
		blend(CCBlendMode.BLEND);
	}
	
	public void blend(final CCBlendMode theBlendMode) {
		gl.glEnable(GL.GL_BLEND);
		blendMode(theBlendMode);
	}
	
	public void noBlend(){
		endBlend();
		gl.glDisable(GL.GL_BLEND);
	}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  PIXEL OPERATION HANDLING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	IntBuffer getsetBuffer = CCBufferUtil.newIntBuffer(1);

	//int getset[] = new int[1];

	public int get(int x, int y){
		gl.glReadPixels(x, y, 1, 1, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, getsetBuffer);
		int getset = getsetBuffer.get(0);

		if (CCSystem.endianess == CCEndianess.BIG_ENDIAN){
			return 0xff000000 | ((getset >> 8) & 0x00ffffff);

		}else{
			return 0xff000000 | ((getset << 16) & 0xff0000) | (getset & 0xff00) | ((getset >> 16) & 0xff);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  COLOR HANDLING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the clear color for OPENGL the clear color is the color the
	 * background is filled with after the call of clear. As long as you haven't
	 * defined a clear color it will be set to black. Normally you once define a
	 * clear color and than use clear to clear the screen
	 */
	public void clearColor(final double theRed, final double theGreen, final double theBlue, final double theAlpha) {
		gl.glClearColor((float)theRed, (float)theGreen, (float)theBlue, (float)theAlpha);
	}
	
	public void clearColor(final double theRed, final double theGreen, final double theBlue) {
		clearColor(theRed, theGreen, theBlue, 1);
	}
	
	public void clearColor(final double theGray, final double theAlpha){
		clearColor(theGray,theGray,theGray,theAlpha);
	}
	
	public void clearColor(final double theGray){
		clearColor(theGray,theGray,theGray,1);
	}
	
	public void clearColor(final CCColor theColor){
		clearColor((double)theColor.r,(double)theColor.g,(double)theColor.b,(double)theColor.a);
	}
	
	public void clearColor(final int theRGB) {
		if (((theRGB & 0xff000000) == 0) && (theRGB <= 255)) {
			clearColor(theRGB, theRGB, theRGB);
		} else {
			clearColor(
				(theRGB >> 16) & 0xff,
				(theRGB >> 8)  & 0xff,
				(theRGB)	   & 0xff,
				(theRGB >> 24) & 0xff
			);
		}
	}
	
	public void clearColor(final int theGray, final int theAlpha){
		clearColor(theGray,theGray,theGray,theAlpha);
	}
	
	public void clearColor(final int theRed, final int theGreen, final int theBlue){
		clearColor((double)theRed/255, (double)theGreen/255, (double)theBlue/255);
	}
	
	public void clearColor(final int theRed, final int theGreen, final int theBlue, final int theAlpha){
		clearColor((double)theRed/255, (double)theGreen/255, (double)theBlue/255, (double)theAlpha/255);
	}
	
	public void clearColor(){
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
	}
	
	/**
	 * Fills the background with the actual clear color, so that the screen is cleared.
	 * As long as you haven't defined clear color it will be set to black. 
	 * Normally you once define a clear color and than use clear to clear the screen
	 */
	public void clear() {
		gl.glClearStencil(0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
	}

	// ////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////
	//
	// TEXTURE / IMAGE HANDLING
	//
	// ////////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////

	/**
	 * Returns the maximum texture size supported by the hardware
	 */
	public int maxTextureSize() {
		return getInteger(GL.GL_MAX_TEXTURE_SIZE);
	}

	/**
	 * Returns the number of texture units that are supported by the graphics card. This allows using multi textures, to
	 * combine different textures.
	 */
	public int textureUnits() {
		return getInteger(GL2ES2.GL_MAX_TEXTURE_IMAGE_UNITS);
	}

	protected boolean _myDrawTexture = false;

	/**
	 * Specifies which texture unit to make active. The number of texture units is implementation dependent, but must be
	 * at least two.
	 * 
	 * @param theTextureUnit
	 */
	public void activeTexture(int theTextureUnit) {
		gl.glActiveTexture(GL.GL_TEXTURE0 + theTextureUnit);
	}

	public void texture(final CCTexture theTexture) {
		_myTextures[0] = theTexture;
		gl.glEnable(_myTextures[0].target().glID);
		_myTextures[0].bind();
		_myDrawTexture = true;

		applyTextureTransformation(0, theTexture);
	}

	public void texture(final int theTextureUnit, final CCTexture theTexture) {
		// GL_TEXTURE_RECTANGLE_ARB
		_myTextures[theTextureUnit] = theTexture;
		activeTexture(theTextureUnit);
		gl.glEnable(_myTextures[theTextureUnit].target().glID);

		_myTextures[theTextureUnit].bind();
		applyTextureTransformation(theTextureUnit, theTexture);
		activeTexture(0);

		_myDrawTexture = true;
	}

	public void texture(final int theTextureUnit, final CCTexture theTexture, final int theID) {
		_myTextures[theTextureUnit] = theTexture;
		gl.glActiveTexture(GL.GL_TEXTURE0 + theTextureUnit);
		gl.glEnable(_myTextures[theTextureUnit].target().glID);

		theTexture.bind(theID);
		applyTextureTransformation(theTextureUnit, theTexture);
		gl.glActiveTexture(GL.GL_TEXTURE0);

		_myDrawTexture = true;
	}

	public void noTexture() {
		for (int i = 0; i < _myTextures.length; i++) {
			if (_myTextures[i] != null) {
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glDisable(_myTextures[i].target().glID);
				_myTextures[i] = null;
				removeTextureTransformation();
			}
		}
		gl.glActiveTexture(GL.GL_TEXTURE0);
		gl.glDisable(GL.GL_TEXTURE_2D);
		_myDrawTexture = false;
	}

	/**
	 * <p>
	 * This enables texturing after you have disabled it. This method is not to confuse with the
	 * {@link #texture(CCAbstractTexture)} method which has to be used first to define which texture to use. So to use
	 * textures for drawing you call one of the texture methods and pass it the texture to use. If you do not need the
	 * texture anymore you call {@link #noTexture()}.
	 * </p>
	 * <p>
	 * Sometimes you might want to draw objects with one texture but inbetween you draw objects that are not textured.
	 * Lets say you have a number of cubes and you only want to texture one side of the cube. You can use enableTextures
	 * and disableTextures. To say whether to use the texture or not. This avoid calling texture and noTexture that are
	 * more expensive performance wise.
	 * </p>
	 */
	public void enableTextures() {
		for (int i = 0; i < _myTextures.length; i++) {
			if (_myTextures[i] != null) {
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glEnable(_myTextures[i].target().glID);
			}
		}
	}

	public void disableTextures() {
		for (int i = 0; i < _myTextures.length; i++) {
			if (_myTextures[i] != null) {
				gl.glActiveTexture(GL.GL_TEXTURE0 + i);
				gl.glDisable(_myTextures[i].target().glID);
			}
		}
	}

	public static enum CCTextureMode {
		IMAGE, NORMALIZED, TARGET_BASED;
	}

	protected CCTextureMode _myTextureMode = CCTextureMode.TARGET_BASED;

	/**
	 * Sets the coordinate space for texture mapping. There are three options:
	 * <ul>
	 * <li><code>IMAGE</code> which refers to the actual coordinates of the image</li>
	 * <li><code>NORMALIZED</code> which refers to a normalized space of values ranging from 0 to 1</li>
	 * <li><code>TARGET BASED</code> dependend on the texture target</li>
	 * </ul>
	 * The default mode is <code>TARGET BASED</code>. In IMAGE, if an image is 100 x 200 pixels, mapping the image onto
	 * the entire size of a quad would require the points (0,0) (0,100) (100,200) (0,200). The same mapping in
	 * NORMAL_SPACE is (0,0) (0,1) (1,1) (0,1).
	 * 
	 * @param theTextureMode
	 */
	public void textureMode(final CCTextureMode theTextureMode) {
		_myTextureMode = theTextureMode;
	}

	public CCTextureMode textureMode() {
		return _myTextureMode;
	}
	
	////////////////////////////////////////////////
	//
	// SHADER 
	//
	////////////////////////////////////////////////

	/**
	 * Load the shader from the source text
	 * 
	 * @param theShaderSource
	 * @param theShaderID
	 */
	public void loadShaderSrc(String theShaderSource, int theShaderID) {
		gl.glShaderSource(theShaderID, 1, new String[] {theShaderSource}, null);
	}
	
	/**
	 * Use this method to set the drawing color, everything you draw
	 * after a call of color, will have the defined color, there are three
	 * ways to define a color, first is to use double values between 0 and 1,
	 * the second is to use integer values between 0 and 255 and the third way
	 * is to use the CCColor class.
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 * @param theAlpha
	 */
	public void color(final double theRed, final double theGreen, final double theBlue, final double theAlpha){
		gl.glColor4d(theRed, theGreen, theBlue, theAlpha);
	}
	
	public void color(final double theRed, final double theGreen,final double theBlue){
		gl.glColor3d(theRed, theGreen, theBlue);
	}
	
	public void color(double theGray, final double theAlpha) {
		gl.glColor4d(theGray,theGray,theGray,theAlpha);
	}
	
	public void color(double theGray) {
		gl.glColor4d(theGray,theGray,theGray,1);
	}
	
	public void color(final CCColor color){
		color(color.r,color.g,color.b,color.a);
	}
	
	public void color(final CCColor color, final double theAlpha){
		color(color.r,color.g,color.b,theAlpha);
	}
	
	public void color(final int theRGB) {
		if (((theRGB & 0xff000000) == 0) && (theRGB <= 255)) {
			color(theRGB, theRGB, theRGB);
		} else {
			color(
				(theRGB >> 16) & 0xff,
				(theRGB >> 8)  & 0xff,
				(theRGB)	   & 0xff,
				(theRGB >> 24) & 0xff
			);
		}
	}
	
	public void color(final int theGray, final int theAlpha){
		color(theGray,theGray,theGray,theAlpha);
	}
	
	public void color(final int theRed, final int theGreen, final int theBlue){
		gl.glColor3ub((byte)theRed, (byte)theGreen, (byte)theBlue);
	}
	
	public void color(final int theRed, final int theGreen, final int theBlue, final int theAlpha){
		gl.glColor4ub((byte)theRed, (byte)theGreen, (byte)theBlue, (byte)theAlpha);
	}
	
	public CCColor color(){
		double[] myColor = new double[4];
		gl.glGetDoublev(GL2ES1.GL_CURRENT_COLOR,myColor,0);
		return new CCColor(myColor);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  MATRIX OPERATIONS
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	public static enum CCMatrixMode{
		/**
		 * Applies subsequent matrix operations to the modelview matrix stack.
		 */
		MODELVIEW(GLMatrixFunc.GL_MODELVIEW,GLMatrixFunc.GL_MODELVIEW_MATRIX),
		/**
		 * Applies subsequent matrix operations to the projection matrix stack.
		 */
		PROJECTION(GLMatrixFunc.GL_PROJECTION,GLMatrixFunc.GL_PROJECTION_MATRIX),
		/**
		 * Applies subsequent matrix operations to the texture matrix stack.
		 */
		TEXTURE(GL.GL_TEXTURE,GLMatrixFunc.GL_TEXTURE_MATRIX);
		
		int glID;
		int glMatrixID;
		
		private CCMatrixMode(final int theGlID, final int theGlMatrixID){
			glID = theGlID;
			glMatrixID = theGlMatrixID;
		}
	}
	
	/**
	 * Specifies whether the modelview, projection, or texture matrix will be modified, 
	 * using the argument MODELVIEW, PROJECTION, or TEXTURE for mode. Subsequent 
	 * transformation commands affect the specified matrix. Note that only one matrix 
	 * can be modified at a time. By default, the modelview matrix is the one that's 
	 * modifiable, and all three matrices contain the identity matrix.
	 * @param theMode int, Specifies which matrix stack is the target for subsequent matrix operations. 
	 * Three values are accepted: MODELVIEW, PROJECTION, and TEXTURE.
	 */
	public void matrixMode(final CCMatrixMode theMode){
		gl.glMatrixMode(theMode.glID);
	}
	
	public CCMatrixMode matrixMode() {
		switch(getInteger(GLMatrixFunc.GL_MATRIX_MODE)) {
		case GL.GL_TEXTURE:
			return CCMatrixMode.TEXTURE;
		case GLMatrixFunc.GL_PROJECTION:
			return CCMatrixMode.PROJECTION;
		default:
			return CCMatrixMode.MODELVIEW;
		}
		
		
	}
	
	/**
	 * Replaces the current matrix with the identity matrix. It is semantically 
	 * equivalent to calling glLoadMatrix with the identity matrix.
	 * Use the loadIdentity() command to clear the currently modifiable matrix 
	 * for future transformation commands, since these commands modify the current 
	 * matrix. Typically, you always call this command before specifying projection 
	 * or viewing transformations, but you might also call it before specifying 
	 * a modeling transformation.
	 */
	public void loadIdentity(){
		gl.glLoadIdentity();
		
		//gl.glTranslatef(0,0,-400.00001f);
	}
	
	private DoubleBuffer _myDoubleBuffer = DoubleBuffer.allocate(16);
	
	/**
	 * Replaces the current matrix with the one specified in m. The current matrix 
	 * is the projection matrix, modelview matrix, or texture matrix, determined 
	 * by the current matrix mode.
	 * @param theMatrix Matrix4f, matrix the current matrix is set to
	 * @related matrixMode ( )
	 */
	public void loadMatrix(final CCMatrix4x4 theMatrix){
		gl.glLoadMatrixd(theMatrix.toDoubleBuffer(_myDoubleBuffer));
	}
	
	/**
	 * Applies the matrix specified by the sixteen values pointed to by m by the 
	 * current matrix and stores the result as the current matrix.
	 * @param theMatrix
	 */
	public void applyMatrix(final CCMatrix4x4 theMatrix){
		gl.glMultMatrixd(theMatrix.toDoubleBuffer(_myDoubleBuffer));
	}
	
	public void applyTransform(final CCTransform theTransform){
		gl.glMultMatrixd(theTransform.getGLApplyMatrix(_myDoubleBuffer));
	}
	
	public void applyMatrix(final CCMatrix32 theMatrix) {
		applyMatrix(
			theMatrix.m00, theMatrix.m01, theMatrix.m02, 
			theMatrix.m10, theMatrix.m11, theMatrix.m12
		);
	}
	
	public void applyMatrix(final double[] theMatrix) {
		gl.glMultMatrixd(theMatrix, 0);
	}
	
	public void applyMatrix(
		final double n00, final double n01, final double n02, final double n03,
		final double n10, final double n11, final double n12, final double n13,
		final double n20, final double n21, final double n22, final double n23,
		final double n30, final double n31, final double n32, final double n33
	){
		final DoubleBuffer myMatrixBuffer = DoubleBuffer.allocate(16);
		
		myMatrixBuffer.put(n00); myMatrixBuffer.put(n10); myMatrixBuffer.put(n20); myMatrixBuffer.put(n30);
		myMatrixBuffer.put(n01); myMatrixBuffer.put(n11); myMatrixBuffer.put(n21); myMatrixBuffer.put(n31);
		myMatrixBuffer.put(n02); myMatrixBuffer.put(n12); myMatrixBuffer.put(n22); myMatrixBuffer.put(n32);
		myMatrixBuffer.put(n03); myMatrixBuffer.put(n13); myMatrixBuffer.put(n23); myMatrixBuffer.put(n33);

		myMatrixBuffer.rewind();
		gl.glMultMatrixd(myMatrixBuffer);
	}
	

	  /**
		 * Apply a 3x2 affine transformation matrix.
		 */
	public void applyMatrix(
		final double n00, final double n01, final double n02, 
		final double n10, final double n11, final double n12
	){
		final DoubleBuffer myMatrixBuffer = DoubleBuffer.allocate(16);
		
		myMatrixBuffer.put(n00); myMatrixBuffer.put(n10); myMatrixBuffer.put(0); myMatrixBuffer.put(0);
		myMatrixBuffer.put(n01); myMatrixBuffer.put(n11); myMatrixBuffer.put(0); myMatrixBuffer.put(0);
		myMatrixBuffer.put(0); myMatrixBuffer.put(0); myMatrixBuffer.put(1); myMatrixBuffer.put(0);
		myMatrixBuffer.put(n02); myMatrixBuffer.put(n12); myMatrixBuffer.put(0); myMatrixBuffer.put(1);

		myMatrixBuffer.rewind();
		gl.glMultMatrixd(myMatrixBuffer);
	}

	public void resetMatrix(){
		gl.glLoadIdentity();
	}
	
	/**
	 * Moves the coordinate system origin to the specified point.
	 * The point can be passed as vector or separate values and can be 2d or 3d.
	 * If the matrix mode is either MODELVIEW or PROJECTION, all objects drawn 
	 * after translate is called are translated. Use pushMatrix and popMatrix to 
	 * save and restore the untranslated coordinate system.
	 * @shortdesc Moves the coordinate system origin to the point defined point.
	 * @param theX double, x coord of the translation vector
	 * @param theY double, y coord of the translation vector
	 * @param theZ double, z coord of the translation vector
	 */
	public void translate(final double theX, final double theY, final double theZ){
		gl.glTranslated(theX,theY,theZ);
	}
	
	public void translate(final double theX,final double theY){
		translate(theX,theY,0);
	}
	
	/**
	 * @param theVector Vector3f, the translation vector
	 */
	public void translate(final CCVector3 theVector){
		translate(theVector.x,theVector.y,theVector.z);
	}
	
	/**
	 * @param theVector Vector2f, the translation vector
	 */
	public void translate(final CCVector2 theVector){
		translate(theVector.x,theVector.y);
	}
	
	/**
	 * Multiplies the current matrix by a matrix that rotates an object 
	 * (or the local coordinate system) in a counterclockwise direction about 
	 * the ray from the origin through the point (x, y, z). The angle parameter 
	 * specifies the angle of rotation in degrees.<br>
	 * If the matrix mode is either MODELVIEW or PROJECTION, all objects drawn 
	 * after rotate is called are rotated. Use pushMatrix and popMatrix to save 
	 * and restore the unrotated coordinate system.
	 * @param theAngle double, the angle of rotation, in degrees.
	 * @param theX double, x coord of the vector
	 * @param theY double, y coord of the vector
	 * @param theZ double, z coord of the vector
	 */
	public void rotate(final double theAngle, final double theX, final double theY, final double theZ) {
		gl.glRotated(theAngle, theX, theY, theZ);
	}
	
	/**
	 * @param theVector Vector3f, vector 
	 */
	public void rotate(final double theAngle, final CCVector3 theVector) {
		rotate(theAngle,theVector.x,theVector.y,theVector.z);
	}
	
	public void rotate(final CCQuaternion theQuaternion){
		final CCVector3 myAxis = new CCVector3();
		final double myAngle = theQuaternion.toAngleAxis(myAxis);
		rotate(CCMath.degrees(myAngle),myAxis.x,myAxis.y,myAxis.z);
	}
	
	public static enum CCRotationOrder{
		XYZ,
		XZY,
		YXZ,
		YZX,
		ZXY,
		ZYX
	}
	
	public void rotate(final CCRotationOrder theOrder, final double theX, final double theY, final double theZ){
		switch(theOrder){
		case XYZ:
			rotateX(theX);
			rotateY(theY);
			rotateZ(theZ);
			break;
		case XZY:
			rotateX(theX);
			rotateZ(theZ);
			rotateY(theY);
			break;
		case YXZ:
			rotateY(theY);
			rotateX(theX);
			rotateZ(theZ);
			break;
		case YZX:
			rotateY(theY);
			rotateZ(theZ);
			rotateX(theX);
			break;
		case ZXY:
			rotateZ(theZ);
			rotateX(theX);
			rotateY(theY);
			break;
		case ZYX:
			rotateZ(theZ);
			rotateY(theY);
			rotateX(theX);
			break;
		}
	}
	
	/**
	 * Rotates an object around the X axis the amount specified by the angle parameter.
	 * Objects are always rotated around their relative position to the origin and positive 
	 * numbers rotate objects in a counterclockwise direction. Transformations apply to 
	 * everything that happens after and subsequent calls to the function accumulates the effect.
	 * @param theAngle double, the angle of rotation, in degrees.
	 */
	public void rotateX(final double theAngle){
		rotate(theAngle,1.0f,0.0f,0.0f);
	}
	
	/**
	 * Rotates an object around the Y axis the amount specified by the angle parameter.
	 * Objects are always rotated around their relative position to the origin and positive 
	 * numbers rotate objects in a counterclockwise direction. Transformations apply to 
	 * everything that happens after and subsequent calls to the function accumulates the effect.
	 * @param theAngle double, the angle of rotation, in degrees.
	 */
	public void rotateY(final double theAngle){
		rotate(theAngle,0.0f,1.0f,0.0f);
	}
	
	/**
	 * Rotates an object around the Z axis the amount specified by the angle parameter.
	 * Objects are always rotated around their relative position to the origin and positive 
	 * numbers rotate objects in a counterclockwise direction. Transformations apply to 
	 * everything that happens after and subsequent calls to the function accumulates the effect.
	 * @param theAngle double, the angle of rotation, in degrees.
	 */
	public void rotateZ(final double theAngle){
		rotate(theAngle,0.0f,0.0f,1.0f);
	}
	
	/**
	 * Rotates an object around the Z axis the amount specified by the angle parameter.
	 * Objects are always rotated around their relative position to the origin and positive 
	 * numbers rotate objects in a counterclockwise direction. Transformations apply to 
	 * everything that happens after and subsequent calls to the function accumulates the effect.
	 * @param theAngle double, the angle of rotation, in degrees.
	 */
	public void rotate(final double theAngle){
		rotate(theAngle,0.0f,0.0f,1.0f);
	}
	
	/**
	 * Produces a general scaling along the x, y, and z axes. The three arguments indicate 
	 * the desired scale factors along each of the three axes. If the matrix mode is either 
	 * MODELVIEW or PROJECTION, all objects drawn after scale is called are scaled. Use 
	 * pushMatrix and popMatrix to save and restore the unscaled coordinate system.<br>
	 * Scale() is the only one of the three modeling transformations that changes the apparent 
	 * size of an object: Scaling with values greater than 1.0 stretches an object, and using 
	 * values less than 1.0 shrinks it. Scaling with a -1.0 value reflects an object across an 
	 * axis. The identity values for scaling are (1.0, 1.0, 1.0). In general, you should limit 
	 * your use of scale() to those cases where it is necessary. Using scale() decreases the 
	 * performance of lighting calculations, because the normal vectors have to be renormalized 
	 * after transformation.<br>
	 * A scale value of zero collapses all object coordinates along that axis to zero. It's 
	 * usually not a good idea to do this, because such an operation cannot be undone. 
	 * Mathematically speaking, the matrix cannot be inverted, and inverse matrices are required 
	 * for certain lighting operations. Sometimes collapsing coordinates does make sense, however; 
	 * the calculation of shadows on a planar surface is a typical application. In general, if a 
	 * coordinate system is to be collapsed, the projection matrix should be used rather than 
	 * the modelview matrix. 
	 * @param theX double, scale factor along the x axis
	 * @param theY double, scale factor along the y axis
	 * @param theZ double, scale factor along the z axis
	 */
	public void scale(final double theX, final double theY, final double theZ) {
		gl.glScaled(theX, theY, theZ);
	}

	public void scale(final double theX, final double theY) {
		gl.glScaled(theX, theY, 1);
	}

	public void scale(final double theSize) {
		gl.glScaled(theSize, theSize, theSize);
	}
	
	/**
	 * There is a stack of matrices for each of the matrix modes. In MODELVIEW mode, the stack depth 
	 * is at least 32. In the other two modes, PROJECTION and TEXTURE, the depth is at least 2. 
	 * The current matrix in any mode is the matrix on the top of the stack for that mode.<br>
	 * pushMatrix pushes the current matrix stack down by one, duplicating the current matrix. 
	 * That is, after a pushMatrix call, the matrix on the top of the stack is identical to the one below it.<br>
	 * popMatrix pops the current matrix stack, replacing the current matrix with the one below it on the stack.
	 * Initially, each of the stacks contains one matrix, an identity matrix. 
	 *
	 */
	public void pushMatrix(){
		gl.glPushMatrix();
	}
	
	/**
	 * There is a stack of matrices for each of the matrix modes. In MODELVIEW mode, the stack depth 
	 * is at least 32. In the other two modes, PROJECTION and TEXTURE, the depth is at least 2. 
	 * The current matrix in any mode is the matrix on the top of the stack for that mode.<br>
	 * pushMatrix pushes the current matrix stack down by one, duplicating the current matrix. 
	 * That is, after a pushMatrix call, the matrix on the top of the stack is identical to the one below it.<br>
	 * popMatrix pops the current matrix stack, replacing the current matrix with the one below it on the stack.
	 * Initially, each of the stacks contains one matrix, an identity matrix. 
	 */
	public void popMatrix(){
		gl.glPopMatrix();
	}
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////
	//
	// PRINTING OF MATRIZES
	//
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////

	private void printMatrixBuffer(final FloatBuffer theMatrix){
		int big = 0;

		for (int i = 0; i < 16; i++){
			big = Math.max(big, (int) Math.abs(theMatrix.get(i)));
		}

		// avoid infinite loop
		if (Float.isNaN(big) || Float.isInfinite(big)){
			big = 1000000; // set to something arbitrary
		}

		int d = 1;
		while ((big /= 10) != 0)
			d++; // cheap log()

		for (int i = 0; i < 16; i += 4){
			System.out.println(
				CCFormatUtil.nfs(theMatrix.get(i), d, 4) + " " + 
				CCFormatUtil.nfs(theMatrix.get(i + 1), d, 4) + " " + 
				CCFormatUtil.nfs(theMatrix.get(i + 2), d, 4) + " " + 
				CCFormatUtil.nfs(theMatrix.get(i + 3), d, 4));
		}
		System.out.println();
	}

	/**
	 * Prints out the given matrix in a nice format
	 */
	public void printGLMatrix(final CCMatrixMode theMatrixMode){
		printMatrixBuffer(getFloatBuffer(theMatrixMode.glMatrixID, 16));
	}

	/**
	 * Prints the current modelview matrix.
	 */
	public void printMatrix(){
		printGLMatrix(CCMatrixMode.MODELVIEW);
	}

	/**
	 * Prints the current projection matrix.
	 */
	public void printProjectionMatrix(){
		printGLMatrix(CCMatrixMode.PROJECTION);
	}
	
	/**
	 * Prints the current projection matrix.
	 */
	public void printTextureMatrix(){
		printGLMatrix(CCMatrixMode.PROJECTION);
	}
	
	public CCMatrix4x4 projectionMatrix(){
		return new CCMatrix4x4().fromDoubleBuffer(getDoubleBuffer(CCMatrixMode.PROJECTION.glMatrixID, 16));
	}
	
	public CCMatrix4x4 textureMatrix(){
		return new CCMatrix4x4().fromDoubleBuffer(getDoubleBuffer(CCMatrixMode.TEXTURE.glMatrixID, 16));
	}
	
	public CCMatrix4x4 modelviewMatrix(){
		return new CCMatrix4x4().fromDoubleBuffer(getDoubleBuffer(CCMatrixMode.MODELVIEW.glMatrixID, 16));
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// DRAWING
	//
	/////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static enum CCAttributeMask{
		/**
		 * Accumulation buffer clear value
		 */
		ACCUM_BUFFER(GL2.GL_ACCUM_BUFFER_BIT),
		/**
		 * GL_ALPHA_TEST enable bit
		 * Alpha test function and reference value
		 * GL_BLEND enable bit
		 * Blending source and destination functions
		 * Constant blend color
		 * Blending equation
		 * GL_DITHER enable bit
		 * GL_DRAW_BUFFER setting
		 * GL_COLOR_LOGIC_OP enable bit
		 * GL_INDEX_LOGIC_OP enable bit
		 * Logic op function
		 * Color mode and index mode clear values
		 * Color mode and index mode writemasks
		 */
		COLOR_BUFFER(GL.GL_COLOR_BUFFER_BIT);
		
		int glID;
		
		private CCAttributeMask(final int theGlID){
			glID = theGlID;
		}
	}
	
	public static enum CCAttributeBit{
		
	}
	
	/**
	 * Saves the current attributes on the attribute stack so that they
	 * can be restored using popAttribute after made changes.
	 * @shortdesc push and pop the server attribute stack 
	 * @see #popAttribute()
	 */
	public void pushAttribute() {
		gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);
	}
	
	/**
	 * popAttribute restores the values of the state variables saved with the last
	 * pushAttribute command. Those not saved are left unchanged. It is an error 
	 * to push attributes onto a full stack or to pop attributes off an empty stack.
	 * In either case, the error flag is set and no other change is made. Initially, 
	 * the attribute stack is empty.
	 * @shortdesc push and pop the server attribute stack 
	 * @see #pushMatrix()
	 */
	public void popAttribute() {
		gl.glPopAttrib();
	}
	
	/**
	 * Specifies whether front- or back-facing facets are candidates for culling.
	 */
	public static enum CCCullFace{
		FRONT(GL.GL_FRONT),
		BACK(GL.GL_BACK),
		FRONT_AND_BACK(GL.GL_FRONT_AND_BACK);
		
		int glID;
		
		private CCCullFace(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * specify whether front- or back-facing facets can be culled. cullFace enables culling and 
	 * specifies whether front- or back-facing facets are culled (as specified by mode). 
	 * Facet culling is initially disabled. Facets include triangles, quadrilaterals,
	 * polygons, and rectangles.
	 * </p>
	 * <p>
	 * {@link #frontFace(CCFace)} specifies which of the clockwise and counterclockwise facets
	 * are front-facing and back-facing.
	 * </p>
	 * @param theFace 
	 * 		Specifies whether front- or back-facing facets are candidates for culling.
	 * 		CCCullFace.FRONT, CCCullFace.BACK, and CCCullFace.FRONT_AND_BACK are accepted.
	 * 		The initial value is CCCullFace.BACK.
	 * @see #frontFace(CCFace)
	 * @see #noCullFace()
	 */
	public void cullFace(CCCullFace theFace){
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(theFace.glID);
	}
	
	public void noCullFace(){
		gl.glDisable(GL.GL_CULL_FACE);
	}
	
	public static enum CCFace{
		CLOCK_WISE(GL.GL_CW),
		COUNTER_CLOCK_WISE(GL.GL_CCW);
		
		int glID;
		
		private CCFace(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * <p>
	 * define front- and back-facing polygons. In a scene composed entirely of opaque closed surfaces,
	 * back-facing polygons are never visible. Eliminating these invisible polygons has the obvious benefit
	 * of speeding up the rendering of the image. To enable and disable elimination of back-facing polygons, 
	 * call cullFace with the desired mode.
	 * </p>
	 * <p>
	 * The projection of a polygon to window coordinates is said to have clockwise winding if an imaginary 
	 * object following the path from its first vertex, its second vertex, and so on, to its last vertex,
	 * and finally back to its first vertex, moves in a clockwise direction about the interior of the polygon.
	 * The polygon's winding is said to be counterclockwise if the imaginary object following the same path moves 
	 * in a counterclockwise direction about the interior of the polygon.
	 * </p>
	 * <p>
	 * frontFace specifies whether polygons with clockwise winding in window coordinates, or counterclockwise 
	 * winding in window coordinates, are taken to be front-facing. Passing CCFace.COUNTER_CLOCK_WISE to mode selects 
	 * counterclockwise polygons as front-facing; CCFace.CLOCK_WISE selects clockwise polygons as front-facing.
	 * By default, counterclockwise polygons are taken to be front-facing.
	 * </p>
	 * @param theFace 
	 * 		specifies the orientation of front-facing polygons.
	 * 		CCFace.CLOCK_WISE and CCFace.COUNTER_CLOCK_WISE are accepted.
	 * 		The initial value is CCFace.COUNTER_CLOCK_WISE.
	 * 
	 * @see #cullFace(CCCullFace)
	 */
	public void frontFace(final CCFace theFace) {
		gl.glFrontFace(theFace.glID);
	}
	
	public static enum CCPolygonMode{
		POINT(GL2.GL_POINT),
		LINE(GL2.GL_LINE),
		FILL(GL2.GL_FILL);
		
		int glID;
		
		private CCPolygonMode(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * This function allows you to change how polygons are rendered. By default, polygons are 
	 * filled or shaded with the current color or material properties. However, you may also 
	 * specify that only the outlines or only the vertices are drawn.
	 * @param thePolygonMode 
	 * Specifies the new drawing mode. 
	 * <ul>
	 * <li>FILL is the default, producing filled polygons. </li>
	 * <li>LINE produces polygon outlines, and </li>
	 * <li>POINT plots only the points of the vertices.</li>
	 * </ul>
	 */
	public void polygonMode(final CCPolygonMode thePolygonMode){
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, thePolygonMode.glID);
	}
	
	/**
	 * beginShape and endShape delimit the vertices that define a primitive or a group of like primitives. 
	 * beginShape accepts a single argument that specifies which of ten ways the vertices are interpreted. 
	 * @param theDrawMode
	 */
	public void beginShape(final CCDrawMode theDrawMode){
		gl.glBegin(theDrawMode.glID);
	}
	
	/**
	 * beginShape and endShape delimit the vertices that define a primitive or a group of like primitives. 
	 * beginShape accepts a single argument that specifies which of ten ways the vertices are interpreted.
	 */
	public void endShape(){
		gl.glEnd();
	}

	/**
	 * beginShape and endShape delimit the vertices that define a primitive or a group of like primitives. 
	 * beginShape accepts a single argument that specifies which of ten ways the vertices are interpreted.
	 */
	public void beginShape(){
		beginShape(CCDrawMode.POLYGON);
	}

	/**
	 * Each vertex of a polygon, separate triangle, or separate quadrilateral specified between a 
	 * beginShape/endShape pair is marked as the start of either a boundary or nonboundary edge. 
	 * If edge is activated when the vertex is specified, the vertex is marked as the start of a 
	 * boundary edge. Otherwise, the vertex is marked as the start of a nonboundary edge.
	 * <br>
	 * The vertices of connected triangles and connected quadrilaterals are always marked as boundary, 
	 * regardless if edges is activated or not. Boundary and nonboundary edges on vertices are significant 
	 * only if polygonMode is set to POINT or LINE. Initially, the edges is activated. 
	 *
	 */
	public void edges(){
		gl.glEdgeFlag(true);
	}
	
	public void noEdges(){
		gl.glEdgeFlag(false);
	}
	
	/**
	 * With OpenGL, all geometric objects are ultimately described as an ordered set of vertices.
	 * Vertex commands are used within beginShape/endShape pairs to specify point, line, and polygon vertices. 
	 * The current color, normal, and texture coordinates are associated with the vertex when vertex is called.
	 * @param theX
	 * @param theY
	 */
	public void vertex(final double theX, final double theY) {
		gl.glVertex2d(theX, theY);
	}
	
	/**
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void vertex(final double theX, final double theY, final double theZ){
		gl.glVertex3d(theX,theY,theZ);
	}
	
	/**
	 * 
	 * @param theVector
	 */
	public void vertex(final CCVector2 theVector){
		gl.glVertex2d(theVector.x, theVector.y);
	}
	
	
	
	/**
	 * 
	 * @param theVector
	 */
	public void vertex(final CCVector3 theVector){
		gl.glVertex3d(theVector.x, theVector.y, theVector.z);
	}
	
	public void vertex(final double theX, final double theY, final double theU, final double theV){
		textureCoords2D(theU,theV);
		gl.glVertex2d(theX,theY);
	}
	
	/**
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void vertex(final double theX, final double theY, final double theZ, final double theU, final double theV){
		textureCoords2D(theU,theV);
		gl.glVertex3d(theX,theY,theZ);
	}
	
	/**
	 * 
	 * @param theVertex
	 */
	public void vertex(final CCVector2 theVertex,final CCVector2 theTextureCoords){
		textureCoords2D(theTextureCoords);
		gl.glVertex2d(theVertex.x, theVertex.y);
	}
	
	
	
	/**
	 * 
	 * @param i_v
	 */
	public void vertex(final CCVector3 theVertex, final CCVector2 theTextureCoords){
		textureCoords2D(theTextureCoords);
		gl.glVertex3d(theVertex.x, theVertex.y, theVertex.z);
	}
	
	/**
	 * Sets the current normal vector as specified by the arguments. You use
	 * normal() to set the current normal to the value of the argument passed in.
	 * Subsequent calls to vertex() cause the specified vertices to be assigned
	 * the current normal. Often, each vertex has a different normal, which
	 * necessitates a series of alternating calls.<br>
	 * A normal vector (or normal, for short) is a vector that points in a
	 * direction that's perpendicular to a surface. For a flat surface, one
	 * perpendicular direction suffices for every point on the surface, but for a
	 * general curved surface, the normal direction might be different at each
	 * point. With OpenGL, you can specify a normal for each vertex. Vertices
	 * might share the same normal, but you can't assign normals anywhere other
	 * than at the vertices.<br>
	 * An object's normal vectors define the orientation of its surface in space -
	 * in particular, its orientation relative to light sources. These vectors
	 * are used by OpenGL to determine how much light the object receives at its
	 * vertices.
	 * 
	 * @param theX double, x part of the normal
	 * @param theY double, y part of the normal
	 * @param theZ double, z part of the normal
	 */
	public void normal(final double theX, final double theY, final double theZ){
		gl.glNormal3d(theX,theY,theZ);
	}
	
	/**
	 * 
	 * @param theNormal Vector3D, vector with the normal
	 */
	public void normal(final CCVector3 theNormal){
		gl.glNormal3d(theNormal.x, theNormal.y, theNormal.z);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//  DRAWING BEZIER CURVES
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private int bezierDetail = 31;
	
	/**
	 * Sets the number of divisions for a beziercurve
	 * @param bezierDetail
	 */
	public void bezierDetail(final int bezierDetail){
		this.bezierDetail = bezierDetail;
	}
	
	/**
	 * Returns the bezier detail used to draw bezier curves
	 * @return
	 */
	public int bezierDetail(){
		return bezierDetail;
	}
	
	/**
	 * Draws a bezier curve with the given points.
	 * @param thePointBuffer
	 */
	private void bezier(final DoubleBuffer thePointBuffer, final int theNumberOfPoints){
		gl.glMap1d(GL2.GL_MAP1_VERTEX_3,0.0f,bezierDetail,3,theNumberOfPoints,thePointBuffer);
		gl.glEnable(GL2.GL_MAP1_VERTEX_3);
		gl.glMapGrid1d(bezierDetail,0,bezierDetail);
		gl.glEvalMesh1(GL2.GL_LINE,0,bezierDetail);
	}
	
	/**
	 * Draws a Bezier curve on the screen. These curves are defined by a series of anchor 
	 * and control points. The first two parameters specify the first anchor point and the 
	 * last two parameters specify the other anchor point. The middle parameters specify 
	 * the control points which define the shape of the curve. Bezier curves were developed 
	 * by French engineer Pierre Bezier. 
	 * @param x1,y1,z1 coordinates for the first anchor point
	 * @param x2,y2,z2 coordinates for the first control point
	 * @param x3,y3,z3 coordinates for the second control point
	 * @param x4,y4,z4 coordinates for the second anchor point
	 */
	public void bezier(
		final double x1, final double y1, final double z1,
		final double x2, final double y2, final double z2,
		final double x3, final double y3, final double z3,
		final double x4, final double y4, final double z4
	){
		final DoubleBuffer myPoints = DoubleBuffer.allocate(12);
		myPoints.put(x1); myPoints.put(y1); myPoints.put(z1);
		myPoints.put(x2); myPoints.put(y2); myPoints.put(z2);
		myPoints.put(x3); myPoints.put(y3); myPoints.put(z3);
		myPoints.put(x4); myPoints.put(y4); myPoints.put(z4);
		myPoints.rewind();
		bezier(myPoints,4);
	}
	
	public void bezier(
		final double x1, final double y1,
		final double x2, final double y2,
		final double x3, final double y3,
		final double x4, final double y4
	){
		bezier(
			x1,y1,0,
			x2,y2,0,
			x3,y3,0,
			x4,y4,0
		);
	}
	
	/**
	 * @param v1 CCVector2: vector with the x, y coordinates of the first anchor point
	 * @param v2 CCVector2: vector with the x, y coordinates of the first control point
	 * @param v3 CCVector2: vector with the x, y coordinates of the second control point
	 * @param v4 CCVector2: vector with the x, y coordinates of the second anchor point
	 */
	public void bezier(
		final CCVector2 v1,
		final CCVector2 v2,
		final CCVector2 v3,
		final CCVector2 v4
	){
		final DoubleBuffer floatbuffer = DoubleBuffer.allocate(12);
		floatbuffer.put(v1.x);
		floatbuffer.put(v1.y);
		floatbuffer.put(0);
		floatbuffer.put(v2.x);
		floatbuffer.put(v2.y);
		floatbuffer.put(0);
		floatbuffer.put(v3.x);
		floatbuffer.put(v3.y);
		floatbuffer.put(0);
		floatbuffer.put(v4.x);
		floatbuffer.put(v4.y);
		floatbuffer.put(0);
		floatbuffer.rewind();
		bezier(floatbuffer,4);
	}
	
	/**
	 * @param v1 CCVector3: vector with the x, y coords of the first anchor point
	 * @param v2 CCVector3: vector with the x, y coords of the first controll point
	 * @param v3 CCVector3: vector with the x, y coords of the second controll point
	 * @param v4 CCVector3: vector with the x, y coords of the second anchor point
	 */
	public void bezier(
		final CCVector3 v1,
		final CCVector3 v2,
		final CCVector3 v3,
		final CCVector3 v4
	){
		final DoubleBuffer floatbuffer = DoubleBuffer.allocate(12);
		floatbuffer.put(v1.x);
		floatbuffer.put(v1.y);
		floatbuffer.put(v1.z);
		
		floatbuffer.put(v2.x);
		floatbuffer.put(v2.y);
		floatbuffer.put(v2.z);
		
		floatbuffer.put(v3.x);
		floatbuffer.put(v3.y);
		floatbuffer.put(v3.z);
		
		floatbuffer.put(v4.x);
		floatbuffer.put(v4.y);
		floatbuffer.put(v4.z);
		floatbuffer.rewind();
		bezier(floatbuffer,4);
	}
	
	private double[] _myBezierCoords;
	private int _myNumberOfBezierCoords;
	
	private double _myLastBezierAnchorX;
	private double _myLastBezierAnchorY;
	private double _myLastBezierAnchorZ;
	
	private boolean _myIsAnchorDefined;
	
	public void beginBezier(){
		_myBezierCoords = new double[300];
		_myNumberOfBezierCoords = 0;
		_myIsAnchorDefined = false;
	}
	 
	public void endBezier(){
		gl.glMap1d(GL2.GL_MAP1_VERTEX_3,0.0f,bezierDetail,3,31,DoubleBuffer.wrap(_myBezierCoords));
		gl.glEnable(GL2.GL_MAP1_VERTEX_3);
		gl.glMapGrid1d(bezierDetail,0,bezierDetail);
		gl.glEvalMesh1(GL2.GL_LINE,0,bezierDetail);
	}

	public void bezierVertex(
		final double x1, final double y1, final double z1, 
		final double x2, final double y2, final double z2, 
		final double x3, final double y3, final double z3, 
		final double x4, final double y4, final double z4
	){
		_myBezierCoords[_myNumberOfBezierCoords++] = x1;
		_myBezierCoords[_myNumberOfBezierCoords++] = y1;
		_myBezierCoords[_myNumberOfBezierCoords++] = z1;
		
		_myBezierCoords[_myNumberOfBezierCoords++] = x2;
		_myBezierCoords[_myNumberOfBezierCoords++] = y2;
		_myBezierCoords[_myNumberOfBezierCoords++] = z2;
		
		_myBezierCoords[_myNumberOfBezierCoords++] = x3;
		_myBezierCoords[_myNumberOfBezierCoords++] = y3;
		_myBezierCoords[_myNumberOfBezierCoords++] = z3;
		
		_myBezierCoords[_myNumberOfBezierCoords++] = x4;
		_myBezierCoords[_myNumberOfBezierCoords++] = y4;
		_myBezierCoords[_myNumberOfBezierCoords++] = z4;
		
		_myLastBezierAnchorX = x4;
		_myLastBezierAnchorY = y4;
		_myLastBezierAnchorZ = z4;
		
		_myIsAnchorDefined = true;
	}
	
	public void bezierVertex(
		final double x2, final double y2, final double z2, 
		final double x3, final double y3, final double z3, 
		final double x4, final double y4, final double z4
	){
		if(!_myIsAnchorDefined){
			throw new RuntimeException("You have to define a bezierVertex with two anchorpoints first!");
		}
		bezierVertex(
			_myLastBezierAnchorX,
			_myLastBezierAnchorY,
			_myLastBezierAnchorZ,
			x2, y2, z2, 
			x3, y3, z3, 
			x4, y4, z4
		);
	}
	
	public void bezierVertex(
		final double x1, final double y1, 
		final double x2, final double y2, 
		final double x3, final double y3, 
		final double x4, final double y4
	){
		bezierVertex(
			x1, y1, 0,
			x2, y2, 0, 
			x3, y3, 0, 
			x4, y4, 0
		);
	}
	
	public void bezierVertex(
		final double x2, final double y2, 
		final double x3, final double y3, 
		final double x4, final double y4
	){
		bezierVertex(
			x2, y2, 0, 
			x3, y3, 0, 
			x4, y4, 0
		);
	}


	public void bezierVertex(
		final CCVector3 v1, final CCVector3 v2, final CCVector3 v3, final CCVector3 v4
	){
		bezierVertex(
			v1.x, v1.y, v1.z, 
			v2.x, v2.y, v2.z, 
			v3.x, v3.y, v3.z,
			v4.x, v4.y, v4.z
		);
	}
	
	public void bezierVertex(
		final CCVector3 v1, final CCVector3 v2, final CCVector3 v3
	){
		bezierVertex(
			v1.x, v1.y, v1.z, 
			v2.x, v2.y, v2.z, 
			v3.x, v3.y, v3.z
		);
	}


	public void bezierVertex(
		final CCVector2 v1, final CCVector2 v2, final CCVector2 v3, final CCVector2 v4
	){
		bezierVertex(
			v1.x, v1.y, 
			v2.x, v2.y, 
			v3.x, v3.y,
			v4.x, v4.y
		);
	}
	
	public void bezierVertex(
		final CCVector2 v1, final CCVector2 v2, final CCVector2 v3
	){
		bezierVertex(
			v1.x, v1.y, 
			v2.x, v2.y, 
			v3.x, v3.y
		);
	}
	

	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//
	// 2D PRIMITIVES
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	/**
	 * Draws a line (a direct path between two points) to the screen. The version of line() 
	 * with four parameters draws the line in 2D. To color a line, use the color() function. 
	 * 2D lines are drawn with a width of one pixel by default, but this can be changed with 
	 * the lineWidth() function. The version with six parameters allows the line to be placed 
	 * anywhere within XYZ space.
	 * @param x1 x coordinate of the lines starting point
	 * @param y1 y coordinate of the lines starting point
	 * @param x2 x coordinate of the lines end point
	 * @param y2 y coordinate of the lines end point
	 */
	public void line(final double x1, final double y1, final double x2, final double y2) {
		beginShape(CCDrawMode.LINES);
		vertex(x1, y1);
		vertex(x2, y2);
		endShape();
	}
	
	/**
	 * @param z1 z coordinate of the lines starting point
	 * @param z2 z coordinate of the lines end point
	 */
	public void line(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
		beginShape(CCDrawMode.LINES);
		vertex(x1, y1, z1);
		vertex(x2, y2, z2);
		endShape();
	}
	
	/**
	 * @param v1 vector with the x,y coordinates of the lines start point
	 * @param v2 vector with the x,y coordinates of the lines end point
	 */
	public void line(final CCVector2 v1, final CCVector2 v2) {
		beginShape(CCDrawMode.LINES);
		vertex(v1);
		vertex(v2);
		endShape();
	}
	
	/**
	 * @param v1 CCVector3: vector with the x,y,z coordinates of the lines start point
	 * @param v2 CCVector3: vector with the x,y,z coordinates of the lines end point
	 */
	public void line(final CCVector3 v1, final CCVector3 v2) {
		beginShape(CCDrawMode.LINES);
		vertex(v1);
		vertex(v2);
		endShape();
	}
	
	
	
	/**
	 * By default, a line is drawn solid and one pixel wide.
	 * Use this method to set the width in pixels for rendered lines,
	 * the width size must be greater than 0.0 and by default is 1.0.
	 * <br>
	 * The actual rendering of lines is affected by the antialising mode. 
	 * Without antialiasing, widths of 1, 2, and 3 draw lines one, two, 
	 * and three pixels wide. With antialiasing enabled, nonintegral line 
	 * widths are possible, and pixels on the boundaries are typically partially filled.
	 * <br>
	 * Keep in mind that by default lines are one pixel wide, so they 
	 * appear wider on lower-resolution screens. For computer displays, 
	 * this isn't typically an issue, but if you're using OpenGL to render 
	 * to a high-resolution plotter, one-pixel lines might be nearly invisible. 
	 * To obtain resolution-independent line widths, you need to take into 
	 * account the physical dimensions of pixels. 
	 * @param lineWidth
	 */
	public void lineWidth(final double lineWidth){
		gl.glLineWidth((float)lineWidth);
	}
	
	/**
	 * A particular OpenGL implementation might limit the width of nonantialiased 
	 * lines to its maximum antialiased line width, rounded to the nearest 
	 * integer value. Use this method to obtain this floating-point value.
	 * @return largest valid line width
	 */
	public double lineWidthMaximum(){
		return getFloatArray(GL2.GL_LINE_WIDTH_RANGE, 2)[1];
	}
	
	/**
	 * A particular OpenGL implementation might limit the width of nonantialiased 
	 * lines to its minimum antialiased line width, rounded to the nearest 
	 * integer value. Use this method to obtain this floating-point value.
	 * @return smallest valid line width
	 */
	public double lineWidthMinimum(){
		return getFloatArray(GL2.GL_LINE_WIDTH_RANGE, 2)[0];
	}
	
	/**
	 * Returns the current line width
	 * @return
	 */
	public double lineWidth(){
		return getFloat(GL.GL_LINE_WIDTH);
	}
	
	/**
	 * Use this to get the minimal supported line width difference with smooth enabled
	 * @return step size allowable between the line widths
	 */
	public double lineWidthGranularity(){
		return getFloat(GL2.GL_LINE_WIDTH_GRANULARITY);
	}
	
	/**
	 * Disables linestippling for dotted or dashed lines
	 * @see #lineStipple(String)
	 */
	public void noLineStipple(){
		gl.glDisable(GL2.GL_LINE_STIPPLE);
	}
	
	/**
	 * the pattern
	 */
	private int lineStipplePattern = 0;
	
	/**
	 * Bitmasks for masking the value to enable in the pattern
	 * 1000 0000 0000 0000
	 * 0100 0000 0000 0000
	 * :
	 * 0000 0000 0000 0001
	 */
	static private final int[] enableBitMasks = {
		32768,16384,8192,4096,
		2048,1024,512,256,
		128,64,32,16,
		8,4,2,1
	}; 
	
	/**
	 * Bitmasks for masking the value to disable in the pattern
	 * 0111 1111 1111 1111
	 * 1011 1111 1111 1111
	 * :
	 * 1111 1111 1111 1110
	 */
	static private int[] disableBitMasks = {
		32767,49151,57343,61439,
		63487,64511,65023,65279,
		65407,65471,65503,65519,
		65527,65531,65533,65534
	};
	
	/**
	 * Parses the line pattern from a given String sequence.
	 * The sequence must have between two and 16 signs, and consist
	 * of the following signs.<br>
	 * - for line<br>
	 * x for break<br>
	 * for example "-x" would paint a simple dotted line while
	 * "----xxxx" would paint a dashed line.Note while a pattern
	 * consists of 16 signs. Strings with lengths that are not a 
	 * factor of 16 have to be truncated in the end so the pattern
	 * "--xxx" would result in the pattern "--xxx--xxx--xxx-".
	 * If the given sequence has more than 16 characters only the first
	 * 16 are parsed.
	 * @param thePattern
	 */
	@SuppressWarnings("unused")
	private void parseLineStipplePattern(final String thePattern){
		int counter = 0;
		while (counter < 15){
			for (int i = 0; i < thePattern.length(); i++){
				if (counter > 15){
					return;
				}
				char c = thePattern.charAt(i);
				
				if (c == '-'){
					lineStipplePattern = lineStipplePattern | enableBitMasks[counter];
				}else if (c == 'x' || c == 'X'){
					lineStipplePattern = lineStipplePattern & disableBitMasks[counter];
				}else{
					throw new RuntimeException("You tried to parse a line pattern with an invalid character");
				}
				counter++;
			}
		}
	}
	
	/**
	 * Use this method to make stippled (dotted or dashed) lines.
	 * Define the stipple pattern, and enable stippling using
	 * <pre> lineStippling ( )</pre>.
	 * One way to think of the stippling is that as the line is being drawn, 
	 * the pattern is shifted by one bit each time a pixel is drawn 
	 * (or factor pixels are drawn, if factor isn't 1). 
	 * When a series of connected line segments is drawn between a single 
	 * <pre>beginShape()</pre> and <pre>endShape()</pre>, the pattern continues 
	 * to shift as one segment turns into the next. This way, a stippling pattern 
	 * continues across a series of connected line segments. 
	 * When <pre>endShape()</pre> is executed, the pattern is reset, and - if more 
	 * lines are drawn before stippling is disabled - the stippling restarts at the 
	 * beginning of the pattern. If you're drawing lines with <pre>LINES</pre>, the pattern 
	 * resets for each independent line.
	 * @param thePattern
	 * @related CCLinePattern
	 * @related lineStippling ( )
	 * @related noLineStippling ( )
	 */
	public void lineStipple(final String thePattern){
		lineStipple(thePattern,1);
	}
	
	/**
	 * 
	 * @param theFactor
	 */
	public void lineStipple(
		final String thePattern,
		final int theFactor
	){
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		gl.glLineStipple(theFactor,Short.parseShort(thePattern, 2));
	}
	
	////////////////////////////////////////////////////////
	//
	// CALLS FOR DRAWING POINTS
	//
	////////////////////////////////////////////////////////
	
	/**
	 * Draws a point, a coordinate in space at the dimension of one pixel. 
	 * Note that you can change the size of a point using the pointSize method.
	 * The first parameter is the horizontal value for the point, the second 
	 * value is the vertical value for the point, and the optional third value 
	 * is the depth value. To draw a lot of points better use beginShape(), 
	 * vertex() and endShape() with the POINT primitive
	 * @param theX the x coordinate of the point to draw
	 * @param theY the y coordinate of the point to draw
	 * @shortdesc Draws a point at the given coordinates.
	 * @example shape/E_01_Point
	 * @related pointSize( )
	 */
	public void point(final double theX, final double theY){
		beginShape(CCDrawMode.POINTS);
		vertex(theX,theY);
		endShape();
	}
	
	/**
	 * @param theVector CCVector2: vector with the x and y coord of the point to  draw
	 */
	public void point(final CCVector2 theVector){
		beginShape(CCDrawMode.POINTS);
		vertex(theVector);
		endShape();
	}
	
	/**
	 * @param theZ z coordinate of the point to draw
	 */
	public void point(final double theX, final double theY, final double theZ){
		beginShape(CCDrawMode.POINTS);
		vertex(theX,theY,theZ);
		endShape();
	}
	
	/**
	 * @param theVector CCVector3: vector containing the x,y and z coordinates of the point to draw
	 */
	public void point(final CCVector3 theVector){
		beginShape(CCDrawMode.POINTS);
		vertex(theVector);
		endShape();
	}
	
	////////////////////////////////////////////////////////
	//
	// SET AND GET POINT PARAMETERS
	//
	////////////////////////////////////////////////////////
	
	/**
	 * By default, a drawn point appears as a single pixel on the screen.
	 * You can use this method to define the size of drawn points. Note
	 * that the pointSize needs to be greater than 0.0 the default value 
	 * is 1.0.<br>
	 * If <b>smooth()</b> is enabled a point is drawn as circle with the
	 * given pointSize as perimeter. Without smooth a point appears as
	 * square with the given pointSize as side length. With disabled smooth
	 * the pointSize is rounded to its nearest integer value.
	 * @param thePointSize the size for points to draw
	 * @example shape/E_02_PointSize
	 * @related point( )
	 * @shortdesc Sets the size for drawn points.
	 */
	public void pointSize(final double thePointSize){
		gl.glPointSize((float)thePointSize);
	}
	
	/**
	 * Returns  the current point size.
	 * @return double: the current pointSize
	 */
	public double pointSize(){
		return getFloat(GL.GL_POINT_SIZE);
	}
	
	/**
	 * Most OpenGL implementations support very large point sizes. 
	 * A particular implementation, however, might limit the size of 
	 * nonantialiased points to its maximum antialiased point size, 
	 * rounded to the nearest integer value. 
	 * Use this method to obtain this floating-point value. 
	 * @return
	 */
	public double pointSizeMaximum(){
		return getFloat(GL2.GL_POINT_SIZE_MAX);
	}

	public void pointSizeMaximum(final double thePointSize){
		gl.glPointParameterf(GL2.GL_POINT_SIZE_MAX, (float)thePointSize);
	}
	
	/**
	 * Returns the smallest possible point size.
	 * @return
	 */
	public double pointSizeMinimum(){
		return getFloat(GL2.GL_POINT_SIZE_MIN);
	}

	public void pointSizeMinimum(final double thePointSize){
		gl.glPointParameterf(GL2.GL_POINT_SIZE_MIN, (float)thePointSize);
	}
	
	public float[] pointSizeRange(){
		return getFloatArray(GL2.GL_POINT_SIZE_RANGE, 2);
	}
	
	/**
	 * Use this to get the minimal supported point size difference with smooth enabled
	 * @return the minimal supported point size difference with smooth
	 */
	public double pointSizeGranularity(){
		return getFloat(GL2.GL_POINT_SIZE_GRANULARITY);
	}
	
	public void pointDistanceAttenuation(final double theConstant, final double theLinear, final double theQuadratic){
		float quadratic[] =  { (float)theConstant, (float)theLinear, (float)theQuadratic };
		gl.glPointParameterfv( GL2.GL_POINT_DISTANCE_ATTENUATION, quadratic ,0);
	}
	
	private boolean _myResetTexture = false;
	
	public void pointSprite(CCTexture thePointSprite) {
		texture(thePointSprite);
		gl.glEnable(GL2.GL_POINT_SPRITE);
		gl.glTexEnvi(GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL.GL_TRUE); 
		_myResetTexture = true;
	}
	
	public void pointSprite(){
		gl.glEnable(GL2.GL_POINT_SPRITE);
		gl.glTexEnvi(GL2.GL_POINT_SPRITE, GL2.GL_COORD_REPLACE, GL.GL_TRUE); 
		_myResetTexture = false;
	}
	
	public void noPointSprite() {
		gl.glDisable(GL2.GL_POINT_SPRITE);
		if(_myResetTexture)noTexture();
	}
	
	public void programPointSize(){
		gl.glEnable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
	}
	
	public void noProgramPointSize(){
		gl.glDisable(GL2.GL_VERTEX_PROGRAM_POINT_SIZE);
	}
	
	public void quad(
		final double x1, final double y1, 
		final double x2, final double y2, 
		final double x3, final double y3, 
		final double x4, final double y4, 
		final boolean theDrawOutline
	){
		if(theDrawOutline) beginShape(CCDrawMode.LINE_LOOP);
		else beginShape(CCDrawMode.QUADS);
		vertex(x1, y1);
		vertex(x2, y2);
		vertex(x3, y3);
		vertex(x4, y4);
		endShape();
	}

	/**
	 * A quad is a quadrilateral, a four sided polygon. It is similar to a rectangle, 
	 * but the angles between its edges are not constrained to ninety degrees.
	 */
	public void quad(
		final double x1, final double y1, 
		final double x2, final double y2, 
		final double x3, final double y3, 
		final double x4, final double y4
	){
		quad(x1, y1, x2, y2, x3, y3, x4, y4, false);
	}

	/**
	 * A triangle is a plane created by connecting three points. 
	 * The first two arguments specify the first point, the middle 
	 * two arguments specify the second point, and the last two 
	 * arguments specify the third point.
	 * 
	 * @param theX1 x-coordinate of the first point
	 * @param theY1 y-coordinate of the first point
	 * @param theZ1 y-coordinate of the first point
	 * 
	 * @param theX2 x-coordinate of the second point
	 * @param theY2 y-coordinate of the second point
	 * @param theZ2 y-coordinate of the second point
	 * 
	 * @param theX3 x-coordinate of the third point
	 * @param theY3 y-coordinate of the third point
	 * @param theZ3 y-coordinate of the third point
	 */
	public void triangle(
		final double theX1, final double theY1, final double theZ1,
		final double theX2, final double theY2, final double theZ2,
		final double theX3, final double theY3, final double theZ3
	){
		beginShape(CCDrawMode.TRIANGLES);
		vertex(theX1, theY1, theZ1);
		vertex(theX2, theY2, theZ2);
		vertex(theX3, theY3, theZ3);
		endShape();
	}
	
	/**
	 * 
	 * @param thePoint1 first point
	 * @param thePoint2 second point
	 * @param thePoint3 third point
	 */
	public void triangle(final CCVector3 thePoint1, final CCVector3 thePoint2, final CCVector3 thePoint3){
		beginShape(CCDrawMode.TRIANGLES);
		vertex(thePoint1);
		vertex(thePoint2);
		vertex(thePoint3);
		endShape();
	}
	
	public void triangle(
		final double theX1, final double theY1,
		final double theX2, final double theY2,
		final double theX3, final double theY3
	){
		beginShape(CCDrawMode.TRIANGLES);
		vertex(theX1, theY1);
		vertex(theX2, theY2);
		vertex(theX3, theY3);
		endShape();
	}
	
	/**
	 * 
	 * @param thePoint1 first point
	 * @param thePoint2 second point
	 * @param thePoint3 third point
	 */
	public void triangle(final CCVector2 thePoint1, final CCVector2 thePoint2, final CCVector2 thePoint3){
		beginShape(CCDrawMode.TRIANGLES);
		vertex(thePoint1);
		vertex(thePoint2);
		vertex(thePoint3);
		endShape();
	}
	
	public void rectMode(final CCShapeMode theRectMode){
		_myRectMode = theRectMode;
	}
	
	public CCShapeMode rectMode(){
		return _myRectMode;
	}
	
	public void rect(final CCVector2 thePosition, final CCVector2 theDimension){
		rect(thePosition.x, thePosition.y, theDimension.x, theDimension.y);
	}
	
	public void rect(final CCVector2 thePosition, final CCVector2 theDimension, boolean theFill){
		rect(thePosition.x, thePosition.y, theDimension.x, theDimension.y, theFill);
	}
	
	public void rect(
		double theX1, double theY1, 
		double theX2, double theY2
	){
		rect(theX1, theY1, theX2, theY2, false);
	}

	public void rect(
		double theX1, double theY1, 
		double theX2, double theY2,
		boolean theDrawOutline
	){
		final double hradius, vradius;
		
		switch (_myRectMode){
			case CORNERS:
				break;
			case CORNER:
				theX2 += theX1;
				theY2 += theY1;
				break;
			case RADIUS:
				hradius = theX2;
				vradius = theY2;
				theX2 = theX1 + hradius;
				theY2 = theY1 + vradius;
				theX1 -= hradius;
				theY1 -= vradius;
				break;
			case CENTER:
				hradius = theX2 / 2.0f;
				vradius = theY2 / 2.0f;
				theX2 = theX1 + hradius;
				theY2 = theY1 + vradius;
				theX1 -= hradius;
				theY1 -= vradius;
		}

		if (theX1 > theX2){
			double temp = theX1;
			theX1 = theX2;
			theX2 = temp;
		}

		if (theY1 > theY2){
			double temp = theY1;
			theY1 = theY2;
			theY2 = temp;
		}

		quad(theX1, theY2,theX2, theY2, theX2, theY1, theX1, theY1, theDrawOutline);
	}

	//////////////////////////////////////////////////////////////
	//
	// ELLIPSE AND ARC
	//
	/////////////////////////////////////////////////////////////

	/**
	 * The origin of the ellipse is modified by the ellipseMode() function.
	 * The possible Modes are:
	 * <ul>
	 * <li>CENTER: the default configuration, specifies the location of the ellipse as the center of the shape.</li>
	 * <li>RADIUS: the same like CENTER but width and height define radius of the ellipse rather than the diameter</li>
	 * <li>CORNER: draws the shape from the upper-left corner of its bounding box.</li>
	 * <li>CORNERS: uses the four parameters to ellipse() to set two opposing corners of the ellipse's bounding box</li>
	 * </ul>
	 * @param theMode, SHAPEMODE: Either CENTER, RADIUS, CORNER, or CORNERS.
	 * @related ellipse ( )
	 */
	public void ellipseMode(final CCShapeMode theMode) {
		_myEllipseMode = theMode;
	}
	
	public CCShapeMode ellipseMode(){
		return _myEllipseMode;
	}
	
	// precalculate sin/cos lookup tables
	// circle resolution is determined from the actual used radii
	// passed to ellipse() method. this will automatically take any
	// scale transformations into account too
	static final private double sinLUT[];
	static final private double cosLUT[];
	static final private double SINCOS_PRECISION = 0.5f;
	static final private int SINCOS_LENGTH = (int) (360f / SINCOS_PRECISION);
	
	static {
		sinLUT = new double[SINCOS_LENGTH];
		cosLUT = new double[SINCOS_LENGTH];
		
		for (int i = 0; i < SINCOS_LENGTH; i++) {
			sinLUT[i] = CCMath.sin(i * CCMath.DEG_TO_RAD * SINCOS_PRECISION);
			cosLUT[i] = CCMath.cos(i * CCMath.DEG_TO_RAD * SINCOS_PRECISION);
		}
	}
	
	public void ellipse(final double theX, double theY, final double theZ, final double theWidth, final double theHeight, boolean theDrawOutline) {
	    double x = theX;
	    double y = theY;
	    double w = theWidth;
	    double h = theHeight;
	    
	    switch(_myEllipseMode){
	    	case CORNERS:
	    		w = theWidth - theX;
	  	      	h = theHeight - theY;
	    		break;
	    	case RADIUS:
	    		x = theX - theWidth;
	    		y = theY - theHeight;
	    		w = theWidth * 2;
	    		h = theHeight * 2;
	    		break;
	    	default:
	    		x = theX - theWidth/2f;
	    		y = theY - theHeight/2f;
	    }
	    // undo negative width
		if (w < 0) { 
			x += w;
			w = -w;
		}

		// undo negative height
		if (h < 0) { 
			y += h;
			h = -h;
		}

		double hradius = w / 2f;
	    double vradius = h / 2f;

	    double centerX = x + hradius;
	    double centerY = y + vradius;

	    int accuracy = (int)(4+Math.sqrt(hradius+vradius)*3);
	    
	    double inc = (double)SINCOS_LENGTH / accuracy;

	    double val = 0;

	    if(theDrawOutline){
	    	beginShape(CCDrawMode.LINE_LOOP);
	    } else {
	    	beginShape(CCDrawMode.TRIANGLE_FAN);
	    	normal(0, 0, 1);
	    	vertex(centerX, centerY,theZ);
	    }
		for (int i = 0; i < accuracy; i++) {
			vertex(
				centerX + cosLUT[(int) val] * hradius, 
				centerY + sinLUT[(int) val] * vradius,
				theZ
			);
			val += inc;
		}
		// back to the beginning
		vertex(centerX + cosLUT[0] * hradius, centerY + sinLUT[0] * vradius,theZ);
		endShape();
	}
	
	/**
	 * Draws an ellipse (oval) in the display window. An ellipse with an equal
	 * width and height is a circle. The first two parameters set the location,
	 * the third sets the width, and the fourth sets the height. The origin may
	 * be changed with the ellipseMode() function.
	 * 
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 */
	public void ellipse(final double theX, double theY, final double theZ, final double theWidth, final double theHeight) {
	    ellipse(theX, theY, theZ, theWidth, theHeight, false);
	}
	
	public void ellipse(final double theX, final double theY, final double theWidth, final double theHeight){
		ellipse(theX, theY, 0, theWidth, theHeight);
	}
	
	public void ellipse(final double theX, final double theY, final double theDiameter){
		ellipse(theX,theY,0,theDiameter,theDiameter);
	}
	
	public void ellipse(final CCVector2 thePosition, final double theDiameter){
		ellipse(thePosition.x, thePosition.y, theDiameter, theDiameter);
	}
	
	public void ellipse(final CCVector2 thePosition, final double theWidth, final double theHeight){
		ellipse(thePosition.x, thePosition.y, theWidth, theHeight);
	}
	
	public void ellipse(final CCVector2 thePosition, final double theWidth, final double theHeight, boolean theFill){
		ellipse(thePosition.x, thePosition.y, 0, theWidth, theHeight, theFill);
	}
	
	public void ellipse(final CCVector3 thePosition, final double theDiameter){
		ellipse(thePosition.x, thePosition.y, thePosition.z, theDiameter, theDiameter);
	}
	
	public void ellipse(final CCVector3 thePosition, final double r0, final double r1){
		ellipse(thePosition.x, thePosition.y, thePosition.z, r0, r1);
	}

	/**
	 * Draws an arc in the display window. Arcs are drawn along the outer edge of 
	 * an ellipse defined by the x, y, width and height parameters. 
	 * The origin or the arc's ellipse may be changed with the ellipseMode() function. 
	 * The start and stop parameters specify the angles at which to draw the arc.
	 * @param theX
	 * @param theY
	 * @param theWidth
	 * @param theHeight
	 * @param theStart
	 * @param theStop
	 * @related ellipse()
	 * @related ellipseMode()
	 */
	public void arc(
		final double theX, final double theY, 
		final double theWidth, final double theHeight,
		final double theStart, double theStop
	) {
		double x = theX;
		double y = theY;
		double w = theWidth;
		double h = theHeight;

		switch(_myEllipseMode){
    	case CORNERS:
    		w = theWidth - theX;
  	      	h = theHeight - theY;
    		break;
    	case RADIUS:
    		x = theX - theWidth;
    		y = theY - theHeight;
    		w = theWidth * 2;
    		h = theHeight * 2;
    		break;
    	case CENTER:
    		x = theX - theWidth/2f;
    		y = theY - theHeight/2f;
    		break;
    	default:
		}

		// if (angleMode == DEGREES) {
		// start = start * DEG_TO_RAD;
		// stop = stop * DEG_TO_RAD;
		// }
		// before running a while loop like this,
		// make sure it will exit at some point.
		if (Double.isInfinite(theStart) || Double.isInfinite(theStop)){
			return;
		}
		
		while (theStop < theStart){
			theStop += CCMath.TWO_PI;
		}
		
		 // undo negative width
		if (w < 0) { 
			x += w;
			w = -w;
		}

		// undo negative height
		if (h < 0) { 
			y += h;
			h = -h;
		}

		double hr = w / 2f;
		double vr = h / 2f;

		double centerX = x + hr;
		double centerY = y + vr;

		int startLUT = (int) (0.5f + (theStart / CCMath.TWO_PI) * SINCOS_LENGTH);
		int stopLUT = (int) (0.5f + (theStop / CCMath.TWO_PI) * SINCOS_LENGTH);

		beginShape(CCDrawMode.TRIANGLE_FAN);
		vertex(centerX, centerY);
		int increment = 1; // what's a good algorithm? stopLUT - startLUT;
		
		for (int i = startLUT; i < stopLUT; i += increment) {
			int ii = i % SINCOS_LENGTH;
			vertex(centerX + cosLUT[ii] * hr, centerY + sinLUT[ii] * vr);
		}
		// draw last point explicitly for accuracy
		vertex(
			centerX + cosLUT[stopLUT % SINCOS_LENGTH] * hr, 
			centerY+ sinLUT[stopLUT % SINCOS_LENGTH] * vr
		);
		endShape();
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	// 
	// 3D PRIMITIVS
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	public void box(final double size){
		box(size, size, size);
	}
	
	public void box(final CCVector3 theDimension){
		box(theDimension.x, theDimension.y, theDimension.z);
	}

	public void box(double theXSize, double theYSize, double theZSize){
		final double x1 = -theXSize / 2f;
		final double x2 = theXSize / 2f;
		final double y1 = -theYSize / 2f;
		final double y2 = theYSize / 2f;
		final double z1 = -theZSize / 2f;
		final double z2 = theZSize / 2f;

		beginShape(CCDrawMode.QUADS);

		// front
		normal(0,0,-1);
		gl.glTexCoord2f(0,1);
		vertex(x1, y2, z1);
		gl.glTexCoord2f(1,1);
		vertex(x2, y2, z1);
		gl.glTexCoord2f(1,0);
		vertex(x2, y1, z1);
		gl.glTexCoord2f(0,0);
		vertex(x1, y1, z1);

		// right
		normal(1,0,0);
		gl.glTexCoord2f(0,1);
		vertex(x2, y2, z1);
		gl.glTexCoord2f(1,1);
		vertex(x2, y2, z2);
		gl.glTexCoord2f(1,0);
		vertex(x2, y1, z2);
		gl.glTexCoord2f(0,0);
		vertex(x2, y1, z1);

		// back
		normal(0,0,1);
		gl.glTexCoord2f(0,1);
		vertex(x2, y2, z2);
		gl.glTexCoord2f(1,1);
		vertex(x1, y2, z2);
		gl.glTexCoord2f(1,0);
		vertex(x1, y1, z2);
		gl.glTexCoord2f(0,0);
		vertex(x2, y1, z2);

		// left
		normal(-1,0,0);
		gl.glTexCoord2f(0,1);
		vertex(x1, y2, z2);
		gl.glTexCoord2f(1,1);
		vertex(x1, y2, z1);
		gl.glTexCoord2f(1,0);
		vertex(x1, y1, z1);
		gl.glTexCoord2f(0,0);
		vertex(x1, y1, z2);

		// top
		normal(0,-1,0);
		gl.glTexCoord2f(0,1);
		vertex(x1, y1, z1);
		gl.glTexCoord2f(1,1);
		vertex(x2, y1, z1);
		gl.glTexCoord2f(1,0);
		vertex(x2, y1, z2);
		gl.glTexCoord2f(0,0);
		vertex(x1, y1, z2);

		// bottom
		normal(0,1,0);
		gl.glTexCoord2f(0,1);
		vertex(x1, y2, z2);
		gl.glTexCoord2f(1,1);
		vertex(x2, y2, z2);
		gl.glTexCoord2f(1,0);
		vertex(x2, y2, z1);
		gl.glTexCoord2f(0,0);
		vertex(x1, y2, z1);

		endShape();
	}
	
	public void boxGrid(final double size){
		boxGrid(size, size, size);
	}
	
	public void boxGrid(double theXSize, double theYSize, double theZSize){
		final double x1 = -theXSize / 2f;
		final double x2 = theXSize / 2f;
		final double y1 = -theYSize / 2f;
		final double y2 = theYSize / 2f;
		final double z1 = -theZSize / 2f;
		final double z2 = theZSize / 2f;

		// top
		beginShape(CCDrawMode.LINE_LOOP);
		vertex(x1, y1, z2);
		vertex(x2, y1, z2);
		vertex(x2, y1, z1);
		vertex(x1, y1, z1);
		endShape();
		
		// bottom
		beginShape(CCDrawMode.LINE_LOOP);
		vertex(x1, y2, z1);
		vertex(x2, y2, z1);
		vertex(x2, y2, z2);
		vertex(x1, y2, z2);
		endShape();
		
		beginShape(CCDrawMode.LINES);
		vertex(x1, y1, z1);
		vertex(x1, y2, z1);
		vertex(x2, y1, z1);
		vertex(x2, y2, z1);
		vertex(x2, y1, z2);
		vertex(x2, y2, z2);
		vertex(x1, y1, z2);
		vertex(x1, y2, z2);
		endShape();
	}

	/**
	 * keeps the current sphere detail
	 */
	private int sphereDetail = 30;

	/**
	 * Sets the detail of the sphere
	 * @param theDetail
	 */
	public void sphereDetail(final int theDetail){
		sphereDetail = theDetail;
	}

	/**
	 * Draws a sphere with the given radius.
	 * @param radius
	 */
	public void sphere(final double radius){
		if(_myDrawTexture)glu.gluQuadricTexture(quadratic, _myDrawTexture);
		glu.gluSphere(quadratic, radius, sphereDetail, sphereDetail);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// TEXTURE / IMAGE HANDLING
	//
	//////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	
	/**
	 * @invisible
	 * @author texone
	 *
	 */
	public static enum CCTextureEnvironmentMode{
		/**
		 * Texel values are applied to geometry fragment values. If blending 
		 * is enabled and the texture contains an alpha channel, the geometry 
		 * blends through the texture according to the current blend function.
		 */
		DECAL(GL2.GL_DECAL),
		/**
		 * Texel values replace geometry fragment values. If blending is enabled 
		 * and the texture contains an alpha channel, the texture's alpha values 
		 * are used to replace the geometry fragment colors in the color buffer.
		 */
		REPLACE(GL2.GL_REPLACE), 
		/**
		 * Texel color values are multiplied by the geometry fragment color values.
		 */
		MODULATE(GL2.GL_MODULATE), 
		/**
		 * Texel color values are added to the geometry color values.
		 */
		ADD(GL2.GL_ADD), 
		/**
		 * Texel color values are multiplied by the texture environment color.
		 */
		BLEND(GL2.GL_BLEND), 
		/**
		 * Texel color values are combined with a second texture unit according 
		 * to the texture combine function.
		 */
		COMBINE(GL2.GL_COMBINE);
		
		private final int glID;
		
		private CCTextureEnvironmentMode(final int theGlID){
			glID = theGlID;
		}
	}
	
	/**
	 * Defines how OpenGL combines the colors from texels with the color of the underlying geometry.
	 */
	public void textureEnvironment(final CCTextureEnvironmentMode theMode){
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, theMode.glID);
	}
	
	public void applyTextureTransformation(final int theTextureUnit, final CCTexture theTexture) {

		matrixMode(CCMatrixMode.TEXTURE);
		pushMatrix();

		double myTextureXscale = 1;
		double myTextureYscale = 1;
		switch (_myTextureMode) {
		case IMAGE:
			if (theTexture.target() == CCTextureTarget.TEXTURE_RECT) {
				myTextureXscale = 1;
				myTextureYscale = 1;
			} else {
				myTextureXscale = 1f / theTexture.width();
				myTextureYscale = 1f / theTexture.height();
			}
			break;
		case NORMALIZED:
			if (theTexture.target() == CCTextureTarget.TEXTURE_RECT) {
				myTextureXscale = theTexture.width();
				myTextureYscale = theTexture.height();
			} else {
				myTextureXscale = 1;
				myTextureYscale = 1;
			}
			break;
		case TARGET_BASED:
			myTextureXscale = 1;
			myTextureYscale = 1;
			break;
		}

		if (theTexture.mustFlipVertically()) {
			if (theTexture.target() == CCTextureTarget.TEXTURE_RECT) {
				scale(myTextureXscale, -myTextureYscale);
				translate(0, -theTexture.height() / myTextureYscale);
			} else {
				scale(myTextureXscale, -myTextureYscale);
				translate(0, -1 / myTextureYscale);
			}
		} else {
			if (theTexture.target() == CCTextureTarget.TEXTURE_RECT) {
				scale(myTextureXscale, myTextureYscale);
			} else {
				scale(myTextureXscale, myTextureYscale);
			}
		}
		matrixMode(CCMatrixMode.MODELVIEW);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.CCAbstractGraphics#removeTextureTransformation()
	 */

	public void removeTextureTransformation() {
		matrixMode(CCMatrixMode.TEXTURE);
		popMatrix();
		matrixMode(CCMatrixMode.MODELVIEW);
	}
	
	/**
	 * Control the generation of texture coordinates
	 * @author christianriekoff
	 *
	 */
	public static enum CCTextureGenMode{
		/**
		 * When the texture generation mode is set to <code>OBJECT_LINEAR</code>, 
		 * texture coordinates are generated using the following function:
		 * <pre>coord = p1 * x + p2 * y + p3 * z + p4 * w</pre>
		 * The x, y, z, and w values are the vertex coordinates from the object 
		 * being textured, and the p1 ? p4 values are the coefficients for a plane 
		 * equation. The texture coordinates are then projected onto the geometry 
		 * from the perspective of this plane.
		 * <p>For example, to project texture coordinates for S and T from the plane 
		 * Z = 0, one would use the following code:
		 * <pre>
		 * g.texGen(CCTextureGenMode.OBJECT_LINEAR, 0, 0, 1, 0);
		 * </pre>
		 * Note that the texture coordinate generation function can be based on a 
		 * different plane equation for each coordinate. Here, we simply use the 
		 * same one for both the S and the T coordinates. This technique maps the 
		 * texture to the object in object coordinates, regardless of any modelview 
		 * transformation in effect.
		 */
		OBJECT_LINEAR(GL2.GL_OBJECT_LINEAR, GL2.GL_OBJECT_PLANE),
		
		/**
		 * When the texture generation mode is set to <code>EYE_LINEAR</code>, texture coordinates 
		 * are gener- ated in a similar manner to <code>OBJECT_LINEAR</code>. The coordinate 
		 * generation looks the same, except that now the X, Y, Z, and W coordinates indicate 
		 * the location of the point of view (where the camera or eye is located). 
		 * The plane equation coefficients are also inverted before being applied 
		 * to the equation to account for the fact that now everything is in eye 
		 * coordinates. The texture, therefore, is basically projected from the plane 
		 * onto the geometry. As the geometry is transformed by the modelview matrix, 
		 * the texture will appear to slide across the surface. We set up this capability 
		 * with the following code from the TEXGEN sample program:
		 * <pre>
		 * g.texGen(CCTextureGenMode.EYE_LINEAR, 0, 0, 1, 0);
		 * </pre>
		 */
		EYE_LINEAR(GL2.GL_EYE_LINEAR, GL2.GL_EYE_PLANE),
		
		/**
		 * When the texture generation mode is set to <code>SPHERE_MAP</code>, OpenGL calculates
		 * texture coordinates in such a way that the object appears to be reflecting the current 
		 * texture map. This is the easiest mode to set up:
		 * <pre>
		 * g.texGen(CCTextureGenMode.SPHERE_MAP);
		 * You usually can make a well-constructed texture by taking a photograph through a 
		 * fish-eye lens. This texture then lends a convincing reflective quality to the geometry. 
		 * For more realistic results, sphere mapping has largely been replaced by cube mapping 
		 * (discussed next). However, sphere mapping still has some uses because it has significantly 
		 * less overhead.
		 */
		SPHERE_MAP(GL2.GL_SPHERE_MAP,-1),
		NORMAL_MAP(GL2.GL_NORMAL_MAP,-1),
		REFLECTION_MAP(GL2.GL_REFLECTION_MAP,-1);

		private final int glID;
		private final int glPlaneId;
		
		private CCTextureGenMode(final int theGlID, final int thePlaneID){
			glID = theGlID;
			glPlaneId = thePlaneID;
		}
	}
	
	public static enum CCTextureGenCoord{
		S(GL2.GL_S, GL2.GL_TEXTURE_GEN_S),
		T(GL2.GL_T, GL2.GL_TEXTURE_GEN_T),
		R(GL2.GL_R, GL2.GL_TEXTURE_GEN_R),
		Q(GL2.GL_Q, GL2.GL_TEXTURE_GEN_Q);

		private final int glID;
		private final int glGenID;
		
		private CCTextureGenCoord(final int theGlID, final int theGenID){
			glID = theGlID;
			glGenID = theGenID;
		}
	}
	
	/**
	 * Use this method to let OpenGL generate texture coordinates. When texture coordinate 
	 * generation is enabled, any calls to textureCoords are ignored, and OpenGL calculates 
	 * the texture coordinates for each vertex for you. In the same manner that texture 
	 * coordinate generation is turned on, you turn it off by using noTexGen().
	 * @param theMode the texture coord generation mode to apply
	 */
	public void texGen(final CCTextureGenMode theMode, double...theData) {
		texGen(CCTextureGenCoord.S, theMode, theData);
		texGen(CCTextureGenCoord.T, theMode, theData);
		
		if(theMode == CCTextureGenMode.REFLECTION_MAP || theMode == CCTextureGenMode.NORMAL_MAP)
		texGen(CCTextureGenCoord.R, theMode);
//		texGen(CCTextureGenCoord.Q, theMode);
	}
	
	/**
	 * Use this method to let OpenGL generate texture coordinates for different 
	 * texture coordinates. When texture coordinate 
	 * generation is enabled, any calls to textureCoords are ignored, and OpenGL calculates 
	 * the texture coordinates for each vertex for you. In the same manner that texture 
	 * coordinate generation is turned on, you turn it off by using noTexGen().
	 * @param theCoord specifies which texture coordinate this function sets
	 * @param theMode the texture coord generation mode to apply
	 */
	public void texGen(final CCTextureGenCoord theCoord, final CCTextureGenMode theMode, double...theData) {
		gl.glEnable(theCoord.glGenID);
		gl.glTexGeni(theCoord.glID, GL2.GL_TEXTURE_GEN_MODE, theMode.glID);
		
		if(theMode.glPlaneId < 0 || theData == null || theData.length < 4)return;
		
		gl.glTexGendv(theCoord.glID, theMode.glPlaneId, theData,0);
	}
	
	public void noTexGen() {
		gl.glDisable(CCTextureGenCoord.S.glGenID);
		gl.glDisable(CCTextureGenCoord.T.glGenID);
		gl.glDisable(CCTextureGenCoord.R.glGenID);
		gl.glDisable(CCTextureGenCoord.Q.glGenID);
	}
	
	public void textureCoords1D(double theX){
		gl.glTexCoord1d(theX);
	}
	
	public void textureCoords2D(final CCVector2 theTextureCoords){
		gl.glTexCoord2d(theTextureCoords.x, theTextureCoords.y);
	}
	
	public void textureCoords2D(double theX, double theY){
		gl.glTexCoord2d(theX, theY);
	}
	
	public void textureCoords3D(final CCVector3 theTextureCoords){
		gl.glTexCoord3d(theTextureCoords.x, theTextureCoords.y, theTextureCoords.z);
	}
	
	public void textureCoords3D(double theX, double theY, double theZ){
		gl.glTexCoord3d(theX, theY, theZ);
	}
	
	public void textureCoords1D(final int theTextureUnit, final double theX) {
		gl.glMultiTexCoord1d(theTextureUnit, theX);
	}
	
	public void textureCoords2D(final int theTextureUnit, final CCVector2 theTextureCoords){
		gl.glMultiTexCoord2d(theTextureUnit,theTextureCoords.x, theTextureCoords.y);
	}
	
	public void textureCoords2D(final int theTextureUnit, double theX, double theY){
		gl.glMultiTexCoord2d(theTextureUnit, theX, theY);
	}
	
	public void textureCoords3D(final int theUnit, CCVector3 theVector){
		gl.glMultiTexCoord3d(theUnit, theVector.x, theVector.y, theVector.z);
	}
	
	public void textureCoords3D(final int theUnit, final double theX, final double theY, final double theZ){
		gl.glMultiTexCoord3d(theUnit, theX, theY, theZ);
	}
	
	public void textureCoords4D(final int theUnit, final double theX, final double theY, final double theZ, final double theW){
		gl.glMultiTexCoord4d(theUnit, theX, theY, theZ, theW);
	}
	
	//////////////////////////////////////////////////////////////
    //
	//   IMAGE
	//
	//////////////////////////////////////////////////////////////
	
	
	/**
	 * Expects x1, y1, x2, y2 coordinates where (x2 >= x1) and (y2 >= y1).
	 * If tint() has been called, the image will be colored.
	 * <p/>
	 * The default implementation draws an image as a textured quad.
	 * The (u, v) coordinates are in image space (they're ints, after all..)
	 */
	
	protected void imageImplementation(
		final CCTexture theImage,
		double x1, double y1, 
		double x2, double y2,
		double u1, double v1, 
		double u2, double v2
	){
		switch(_myImageMode){
		case CENTER:
			// reset a negative width
			if (x2 < 0){ x1 += x2; x2 = -x2;}
			// reset a negative height
			if (y2 < 0){ y1 += y2; y2 = -y2;}
			x1 -= x2/2;
			y1 -= y2/2;
			x2 += x1;
			y2 += y1;
			break;
		case CORNER:
			// reset a negative width
			if (x2 < 0){ x1 += x2; x2 = -x2;}
			// reset a negative height
			if (y2 < 0){ y1 += y2; y2 = -y2;}
	
			x2 += x1;
			y2 += y1;
			break;
		case CORNERS:
			// reverse because x2 < x1
//			if (x2 < x1){
//				double temp = x1; 
//				x1 = x2; 
//				x2 = temp;
//			}
//				
//			// reverse because y2 < y1
//			if (y2 < y1){
//				double temp = y1; 
//				y1 = y2; 
//				y2 = temp;
//			}
		default:
			break;
		}
		
		texture(theImage);
		
		beginShape(CCDrawMode.QUADS);
		vertex(x1, y1, u1, v1);
		vertex(x1, y2, u1, v2);
		vertex(x2, y2, u2, v2);
		vertex(x2, y1, u2, v1);
		endShape();
		noTexture();
	}
	
	/**
	 * Displays images to the screen. The images must be in the sketch's "data" 
	 * directory to load correctly. Select "Add file..." from the "Sketch" menu 
	 * to add the image. Creative computing currently works with PNG, GIF, JPEG
	 * and Targa images. The color of an image may be modified with the color() 
	 * function and if a GIF or PNG has transparency, it will maintain its 
	 * transparency. The img parameter specifies the image to display and 
	 * the x and y parameters define the location of the image from its upper-left corner. 
	 * The image is displayed at its original size unless the width and height 
	 * parameters specify a different size. The imageMode() function changes 
	 * the way the parameters work. A call to imageMode(CORNERS) will change 
	 * the width and height parameters to define the x and y values of the opposite 
	 * corner of the image. 
	 * @param theImage
	 * @param theX
	 * @param theY
	 * @see #color()
	 * @see #imageMode(CCShapeMode)
	 */
	public void image(final CCTexture theImage, final double theX, final double theY) {
		image(theImage, theX, theY, theImage.width(), theImage.height());
	}
	
	public void image(final CCTexture theImage, final CCVector2 theVector){
		image(theImage, theVector.x, theVector.y);
	}

	public void image(
		final CCTexture theImage, 
		final double theX, final double theY, 
		final double theWidth, final double theHeight
	){
		CCTextureMode myStoredTextureMode = _myTextureMode;
		_myTextureMode = CCTextureMode.IMAGE;
		imageImplementation(
			theImage, 
			theX, theY, theWidth, theHeight, 
			0, 0, theImage.width(), theImage.height()
		);
		_myTextureMode = myStoredTextureMode;
	}

	public void image(final CCTexture theImage, final CCVector2 thePosition, final CCVector2 theDimension){
		image(theImage, thePosition.x,thePosition.y,theDimension.x,theDimension.y);
	}
	
	public void image(
		final CCTexture theImage, 
		final double theX1, final double theY1, final double theX2, final double theY2,
		final double theIX1, final double theIY1, final double theIX2, final double theIY2
	){
		CCTextureMode myStoredTextureMode = _myTextureMode;
		_myTextureMode = CCTextureMode.IMAGE;
		imageImplementation(
			theImage, 
			theX1, theY1, theX2, theY2, 
			theIX1, theIY1, theIX2, theIY2
		);
		_myTextureMode = myStoredTextureMode;
	}

	/**
	 * Modifies the location from which images draw. The default mode is CORNER, 
	 * which specifies the location to be the upper left corner and uses the fourth
	 * and fifth parameters of image() to set the image's width and height. 
	 * The syntax imageMode(CORNERS) uses the second and third parameters of image() 
	 * to set the location of one corner of the image and uses the fourth and fifth 
	 * parameters to set the opposite corner. Use imageMode(CENTER) to draw images
	 * centered at the given x and y position.
	 * @shortdesc Modifies the location from which images draw.
	 * @param theShapeMode 
	 */
	public void imageMode(final CCShapeMode theShapeMode){
		_myImageMode = theShapeMode;
	}
	
	public CCShapeMode imageMode(){
		return _myImageMode;
	}
	
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// CAMERA PROJECTION HANDLING
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	public void beginCamera(){
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
	}

	public void endCamera(){
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}
	
	public void resetCamera(){
		_myCamera.reset(this);
	}

	public void updateCamera(){
		_myCamera.draw(this);
	}
	
	public CCCamera camera(){
		return _myCamera;
	}
	
	public void camera(CCCamera theCamera){
		_myCamera = theCamera;
		updateCamera();
	}
	
	public void camera(final CCVector3 thePosition, final CCVector3 theTarget, final CCVector3 theUp){
		_myCamera.position(thePosition);
		_myCamera.target(theTarget);
		_myCamera.draw(this);
	}

	public void camera(
		final double thePositionX, final double thePositionY, double thePositionZ,
		final double theTargetX, final double theTargetY, final double theTargetZ,
		final double theUpX, final double theUpY, final double theUpZ
	){
		_myCamera.position(thePositionX, thePositionY, thePositionZ);
		_myCamera.target(theTargetX, theTargetY, theTargetZ);
		_myCamera.draw(this);
	}
	
	public void cameraPosition(final double thePositionX, final double thePositionY, double thePositionZ){
		_myCamera.position(thePositionX, thePositionY, thePositionZ);
		_myCamera.draw(this);
	}
	
	public void cameraPosition(final CCVector3 thePosition){
		cameraPosition(thePosition.x, thePosition.y, thePosition.z);
	}
	
	public void cameraTarget(final double theCenterX, final double theCenterY, final double theCenterZ){
		_myCamera.target(theCenterX, theCenterY, theCenterZ);
		_myCamera.draw(this);
	}
	
	public void cameraTarget(final CCVector3 theCenter){
		cameraTarget(theCenter.x, theCenter.y, theCenter.z);
	}


	/**
	 * Sets an orthographic projection and defines a parallel clipping volume. 
	 * All objects with the same dimension appear the same size, regardless of 
	 * whether they are near or far from the camera. The parameters to this 
	 * function specify the clipping volume where left and right are the minimum 
	 * and maximum x values, top and bottom are the minimum and maximum y values, 
	 * and near and far are the minimum and maximum z values. If no parameters are 
	 * given, the default is used: ortho(0, width, 0, height, -10, 10).
	 * @param theLeft left plane of the clipping volume
	 * @param theRight right plane of the clipping volume
	 * @param theBottom bottom plane of the clipping volume
	 * @param theTop top plane of the clipping volume
	 * @param theNear maximum distance from the origin to the viewer
	 * @param theFar maximum distance from the origin away from the viewer
	 */
	public void ortho(
		final double theLeft, final double theRight, 
		final double theBottom, final double theTop, 
		final double theNear, final double theFar
	){
		// Select the Projection Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		// Reset The Projection Matrix
		gl.glLoadIdentity();
		gl.glOrtho(theLeft - theLeft / 2, theRight - theLeft / 2, -theBottom + theTop, -theTop + theTop, theNear, theFar);

		// Select The Modelview Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/**
	 * 
	 */
	public void ortho(){
		ortho(0, width(), 0, height(), -1000, 1000);
	}
	
	public void ortho2D(final int theWidth, final int theHeight){
		gl.glViewport(0, 0, theWidth, theHeight);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, theWidth, 0, theHeight);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void ortho2D(){
		ortho2D(width(),height());
	}
	
	/**
	 * Switch to orthographic projection<BR>
	 * The current projection and modelview matrix are saved (push).<BR>
	 * You can loads projection and modelview matrices with endOrtho
	 * @see #endOrtho(glRenderer)
	 */
	public void beginOrtho(int theLeft, int theRight, int theBottom, int theTop, int theNear, int theFar){
		/*
		 * We save the current projection matrix and we define a viewing volume
		 * in the orthographic mode.
		 * Projection matrix stack defines how the scene is projected to the screen.
		 */
		matrixMode(CCMatrixMode.PROJECTION);				//select the Projection matrix
		pushMatrix();								//save the current projection matrix
		loadIdentity();							//reset the current projection matrix to creates a new Orthographic projection
		//Creates a new orthographic viewing volume
		gl.glOrtho(theLeft, theRight, theBottom, theTop, theNear, theFar);

		/*
		 * Select, save and reset the modelview matrix.
		 * Modelview matrix stack store transformation like translation, rotation ...
		 */
		matrixMode(CCMatrixMode.MODELVIEW);
		pushMatrix();
		loadIdentity();
	}
	
	public void beginOrtho(final int theWidth, final int theHeight){
		beginOrtho(0, theWidth, theHeight, 0, -1000, 1000);
	}
	
	public void beginOrtho() {
		beginOrtho(width(), height());
	}

	/**
	 * Load projection and modelview matrices previously saved by the method beginOrtho
	 * @see #beginOrtho(glRenderer)
	 */
	public void endOrtho(){
		//Select the Projection matrix stack
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		//Load the previous Projection matrix (Generally, it is a Perspective projection)
		gl.glPopMatrix();

		//Select the Modelview matrix stack
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		//Load the previous Modelview matrix
		gl.glPopMatrix();
	}
	
	public void beginOrtho2D(){
		beginOrtho2D(width(),height());
	}
	
	public void beginOrtho2D(final int theWidth, final int theHeight){
		gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, theWidth, theHeight);

		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, theWidth, 0, theHeight);

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}
	
	public void endOrtho2D(){
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glPopMatrix();

		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glPopAttrib();
	}

	/**
	 * Calls perspective() with Processing's standard coordinate projection.
	 * <P>
	 * Projection functions:
	 * <UL>
	 * <LI>frustrum()
	 * <LI>ortho()
	 * <LI>perspective()
	 * </UL>
	 * Each of these three functions completely replaces the projection matrix
	 * with a new one. They can be called inside setup(), and their effects will
	 * be felt inside draw(). At the top of draw(), the projection matrix is not
	 * reset. Therefore the last projection function to be called always
	 * dominates. On resize, the default projection is always established, which
	 * has perspective.
	 * <P>
	 * This behavior is pretty much familiar from OpenGL, except where functions
	 * replace matrices, rather than multiplying against the previous.
	 * <P>
	 */
	public void perspective(){
		perspective(_myCamera.fov(), _myCamera.aspect(), _myCamera.near(), _myCamera.far());
	}

	/**
	 * Similar to gluPerspective(). Implementation based on Mesa's glu.c
	 */
	public void perspective(double fov, double aspect, double zNear, double zFar){
		//double ymax = zNear * tan(fovy * PI / 360.0f);
		double ymax = zNear * (double) Math.tan(fov / 2.0f);
		double ymin = -ymax;

		double xmin = ymin * aspect;
		double xmax = ymax * aspect;

		frustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	/**
	 * Same as glFrustum(), except that it wipes out (rather than
	 * multiplies against) the current perspective matrix.
	 * <P>
	 * Implementation based on the explanation in the OpenGL blue book.
	 */
	public void frustum(double left, double right, double bottom, double top, double znear, double zfar){
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(left, right, bottom, top, znear, zfar);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
	}
	
	/**
	 * Viewport specifies the affine transformation of x and y from normalized device coordinates to window coordinates.
	 * 
	 * Let <code>xnd</code> and <code>ynd</code> be normalized device coordinates. Then the window coordinates 
	 * <code>xw</code> and <code>yw</code> are computed as follows:
	 * 
	 * <pre>
	 * xw = (xnd + 1)(width / 2) + x
	 * yw = (xnd + 1)(height / 2) + y
	 * </pre>
	 * Viewport width and height are silently clamped to a range that depends on the implementation.
	 * When creative computing is started width and height are set to the dimensions of the application window.
	 * @param theX Specify the left corner of the viewport rectangle, in pixels. The initial value is 0.
	 * @param theY Specify the lower corner of the viewport rectangle, in pixels. The initial value is 0.
	 * @param theWidth Specify the width of the viewport. 
	 * @param theHeight Specify the height of the viewport. 
	 */
	public void viewport(final int theX, final int theY, final int theWidth, final int theHeight) {
		gl.glViewport(theX, theY, theWidth, theHeight);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// LIGHTNING
	//
	/////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////
	
	
	/**
	 * Different Color Material modes
	 * @author texone
	 *
	 */
	public enum CCColorMaterialMode{
		OFF, EMISSION, AMBIENT, DIFFUSE, AMBIENT_AND_DIFFUSE, SPECULAR;
	}
	
	private CCColorMaterialMode _myColorMaterialMode;
	private CCCullFace _myColorMaterialFace;
	
	/**
	 * This function allows material properties to be set without having to call <b>material()</b> 
	 * directly. By using this function, you can set certain material properties to follow the 
	 * current color as specified by <b>color()</b>. By default, color tracking is enabled.
	 * <ul>
	 * <li>OFF</li>
	 * <li>EMISSION</li>
	 * <li>AMBIENT</li>
	 * <li>DIFFUSE</li>
	 * <li>AMBIENT_AND_DIFFUSE</li>
	 * <li>SPECULAR</li>
	 * </ul>
	 * @param theMode
	 */
	public void colorMaterial(final CCColorMaterialMode theMode){
		colorMaterial(CCCullFace.FRONT_AND_BACK, theMode);
	}
	
	public void colorMaterial(final CCCullFace theFace, final CCColorMaterialMode theMode){
		_myColorMaterialMode = theMode;
		_myColorMaterialFace = theFace;
		switch(theMode){
		case OFF:
			gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
			break;
		case EMISSION:
			gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
			gl.glColorMaterial(_myColorMaterialFace.glID, GLLightingFunc.GL_EMISSION);
			break;
		case AMBIENT:
			gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
			gl.glColorMaterial(_myColorMaterialFace.glID, GLLightingFunc.GL_AMBIENT);
			break;
		case DIFFUSE:
			gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
			gl.glColorMaterial(_myColorMaterialFace.glID, GLLightingFunc.GL_DIFFUSE);
			break;
		case AMBIENT_AND_DIFFUSE:
			gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
			gl.glColorMaterial(_myColorMaterialFace.glID, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
			break;
		case SPECULAR:
			gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
			gl.glColorMaterial(_myColorMaterialFace.glID, GLLightingFunc.GL_SPECULAR);
			break;
		}
	}
	
	public CCColorMaterialMode colorMaterial(){
		return _myColorMaterialMode;
	}
	
	/**
	 * Use this method to set the default ambient illumination for a scene. 
	 * By default, this light has an RGBA value of (0.2, 0.2, 0.2, 1.0). 
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 * @see #material(CCMaterial)
	 * @see #light(CCLight)
	 * @see #lightModelColorControl(boolean)
	 * @see #lightModelLocalViewer(boolean)
	 * @see #lightModelTwoSide(boolean)
	 */
	public void lightModelAmbient(final double theRed, final double theGreen, final double theBlue){
		float[] theColor = new float[4];
		theColor[0] = (float)theRed;
		theColor[1] = (float)theGreen;
		theColor[2] = (float)theBlue;
		theColor[3] = 1.0f;
		gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, theColor, 0);
	}
	
	public void lightModelAmbient(final int theRed, final int theGreen, final int theBlue){
		int[] theColor = new int[4];
		theColor[0] = theRed;
		theColor[1] = theGreen;
		theColor[2] = theBlue;
		theColor[3] = 255;
		gl.glLightModeliv(GL2.GL_LIGHT_MODEL_AMBIENT, theColor, 0);
	}

	public void lightModelAmbient(final CCColor theColor){
		lightModelAmbient(theColor.r, theColor.g, theColor.b);
	}
	
	/**
	 * <b>lightModelTwoSide()</b> specifies if both sides of polygons are illuminated. 
	 * By default, the front and back(defined by winding) of polygons is illuminated, 
	 * using the front material properties as specified by <b>material()</b>. 
	 * @param theIsTwoSide <code>false</code> indicates that only the fronts of polygons are 
	 * to be included in illumination calculations. <code>true</code> indicates that both the 
	 * front and back are included.
	 * @see #material(CCMaterial)
	 * @see #light(CCLight)
	 * @see #lightModelColorControl(boolean)
	 * @see #lightModelLocalViewer(boolean)
	 * @see #lightModelAmbient(CCColor)
	 */
	public void lightModelTwoSide(final boolean theIsTwoSide){
		if(theIsTwoSide)gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 1);
		else gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, 0);
	}
	
	/**
	 * Specifying lightModelLocalViewer() modifies the calculation of specular reflection 
	 * angles, whether the view is down along the negative z-axis or from the origin of the 
	 * eye coordinate system. As default the negative z-axis is taken.
	 * @param theIsLocalViewer <code>false</code> indicates that specular lighting angles 
	 * take the view direction to be parallel to and in the direction of the negative z-axis. 
	 * <code>true</code> indicates that the view is from the origin of eye coordinate system.
	 * @see #material(CCMaterial)
	 * @see #light(CCLight)
	 * @see #lightModelColorControl(boolean)
	 * @see #lightModelAmbient(double, double, double)
	 * @see #lightModelTwoSide(boolean)
	 */
	public void lightModelLocalViewer(final boolean theIsLocalViewer){
		if(theIsLocalViewer)gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		else gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 0);
	}
	
	/**
	 * Use this method to specify whether specular lighting produces a second color 
	 * (textures get specular light) or whether all three lighting components are combined.
	 * As default a single color is used
	 * @param theIsSingleColor if <code>true</code> all lighting components are combined, for <code>
	 * false</code> a second color is produced
	 * @see #material(CCMaterial)
	 * @see #light(CCLight)
	 * @see #lightModelTwoSide(boolean)
	 * @see #lightModelLocalViewer(boolean)
	 * @see #lightModelAmbient(CCColor)
	 */
	public void lightModelColorControl(final boolean theIsSingleColor){
		if(theIsSingleColor)gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SINGLE_COLOR);
		else gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
	}

	

	protected void handle_lighting(){
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
	}
	
	public boolean lighting = false;

	/**
	 * Activates lighting and sets default values
	 */
	public void lights(){
		lighting = true;
		gl.glEnable(GLLightingFunc.GL_LIGHTING);
	}
	
	public void noLights(){
		lighting = false;
		gl.glDisable(GLLightingFunc.GL_LIGHTING);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// TEXTHANDLING
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	
	private CCText _myText = new CCText(CCFontIO.createGlutFont(CCGlutFontType.BITMAP_HELVETICA_10));
	
	public void textAlign(final CCTextAlign theTextAlign){
		_myText.align(theTextAlign);
	}
	
	public CCTextAlign textAlign(){
		return _myText.textAlign();
	}

	/**
	 * Returns the ascent of the current font at the current size.
	 * This is a method, rather than a variable inside the PGraphics object
	 * because it requires calculation.
	 */

	public double textAscent(){
		return _myText.ascent();
	}

	public double textDescent(){
		return _myText.descent();
	}
	
	/**
	 * Sets the current font. The font's size will be the "natural" size of
	 * this font (the size that was set when using "Create Font"). The leading
	 * will also be reset.
	 * @param theFont
	 */
	public void textFont(final CCFont<?> theFont){
		_myText.font(theFont);
	}

	/**
	 * Useful function to set the font and size at the same time.
	 */
	public void textFont(final CCFont<?> theFont, final double theSize) {
		_myText.font(theFont, theSize);
	}
	
	public CCFont<?> textFont(){
		return _myText.font();
	}

	/**
	 * Set the text leading to a specific value. If using a custom value for
	 * the text leading, you'll have to call textLeading() again after any
	 * calls to textSize().
	 */
	public void textLeading(final double theTextLeading) {
		_myText.leading(theTextLeading);
	}

	/**
	 * Same as parent, but override for native version of the font. <p/> Also
	 * gets called by textFont, so the metrics will get recorded properly.
	 */
	public void textSize(final double theSize){
		_myText.size(theSize);
	}
	
	public double textSize(){
		return _myText.size();
	}

	public void text(final char theChar, final double theX, final double theY){
		text(theChar,theX,theY);
	}

	public void text(final char theChar, final double theX, final double theY, final double theZ){
		_myText.text(theChar);
		_myText.position(theX, theY, theZ);
		_myText.draw(this);
	}

	public void text(final String theString, final double theX, double theY, final double theZ){
		_myText.text(theString);
		_myText.position(theX, theY, theZ);
		_myText.draw(this);
	}
	
	public void text(String theText, CCVector3 theVector) {
		text(theText, theVector.x, theVector.y, theVector.z);
	}

	public void text(final String theString, final double theX, final double theY){
		text(theString, theX, theY, 0);
	}
	
	public void text(final String theString, final CCVector2 theVector){
		text(theString, theVector.x, theVector.y);
	}

	public void text(final int theNumber, final double theX, final double theY){
		text(String.valueOf(theNumber), theX, theY);
	}
	public void text(final double theNumber, final double theX, final double theY){
		text(String.valueOf(theNumber), theX, theY);
	}

	public void text(final int theNumber, final CCVector2 theVector){
		text(String.valueOf(theNumber), theVector.x, theVector.y);
	}

	public void text(final int theNumber, final double theX, final double theY, final double theZ){
		text(String.valueOf(theNumber), theX, theY, theZ);
	}

	public void text(final int theNumber, final CCVector3 theVector){
		text(String.valueOf(theNumber), theVector.x, theVector.y, theVector.z);
	}



	// ////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////

	public void strokeWeight(final double weight){
		gl.glLineWidth((float)weight);
	}
	
	// ////////////////////////////////////////////////////////////

	// ////////////////////////////////////////////////////////////

	public void smooth(){
		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);
	}

	public void noSmooth(){
		gl.glDisable(GL2.GL_POINT_SMOOTH);
		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glDisable(GL2.GL_POLYGON_SMOOTH);
		gl.glDisable(GL.GL_BLEND);
	}

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	// TEXTURE HANDLING
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	private CCShapeMode _myImageMode = CCShapeMode.CORNER;
	private CCShapeMode _myRectMode = CCShapeMode.CORNER;
	private CCShapeMode _myEllipseMode = CCShapeMode.CENTER;

	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
	//
	//  COPYING OF SCREEN AND IMAGE PARTS
	//
	//////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////

	/**
	 * The raster position can be compared to vertex in that the coordinates 
	 * are transformed by the current modelview and projection matrices. 
	 * The resulting window position becomes the current raster position. 
	 * All rasterizing operations (bitmaps and pixmaps) occur with the current 
	 * raster position specifying the image's lower-left corner. If the current 
	 * raster position falls outside the window's viewport, it is invalid, and 
	 * any operations that require the raster position will fail.
	 * @param theX x coordinate of the raster position
	 * @param theY y coordinate of the raster position
	 */
	public void rasterPos(final int theX, final int theY) {
		gl.glRasterPos2i(theX, theY);
	}
	
	public void rasterPos(final double theX, final double theY) {
		gl.glRasterPos2d(theX, theY);
	}
	
	/**
	 * Works like rasterPos without applying the modelViewProjection matrix.
	 * @see CCGraphics#rasterPos(int, int)
	 * @param theX window position x
	 * @param theY window position y
	 */
	public void windowPos(final int theX, final int theY) {
		gl.glWindowPos2i(theX, theY);
	}
	
	public void windowPos(final double theX, final double theY) {
		gl.glWindowPos2d(theX, theY);
	}

	/**
	 * Copies a region of pixels from the display window to another area of 
	 * the display window and copies a region of pixels from an image used 
	 * as the srcImg parameter into the display window. 
	 * If the source and destination regions aren't the same size, it will 
	 * automatically resize the source pixels to fit the specified target 
	 * region. No alpha information is used in the process, however if the 
	 * source image has an alpha channel set, it will be copied as well.
	 * 
	 * The imageMode() function changes the way the parameters work. 
	 * For example, a call to imageMode(CORNERS) will change the width and 
	 * height parameters to define the x and y values of the opposite corner 
	 * of the image.
	 * @param sx1
	 * @param sy1
	 * @param sx2
	 * @param sy2
	 * @param dx1
	 * @param dy1
	 * @param dx2
	 * @param dy2
	 */
	public void copy(
		final int sx1, final int sy1, int sx2, int sy2, 
		final int dx1, final int dy1, int dx2, int dy2
	){

		if (_myImageMode == CCShapeMode.CORNERS){
			sx2 = sx2 - sx1;
			sy2 = sy2 - sy1;
			dx2 = dx2 - dx1;
			dy2 = dy2 - dy1;
		}

		gl.glWindowPos2i(width()/2+dx1, height()/2 + dy1);
		gl.glPixelZoom((float) dx2 / sx2, (float) dy2 / sy2);
		gl.glCopyPixels(width()/2+sx1, height()/2 + sy1, sx2, sy2, GL2.GL_COLOR);
		gl.glPixelZoom(1, 1);
	}
	
	public void copy(
		final CCTexture src, 
		final int sx1, final int sy1, int sx2, int sy2, 
		final int dx1, final int dy1, int dx2, int dy2
	){
		if (_myImageMode == CCShapeMode.CORNER){
			sx2 = sx2 + sx1;
			sy2 = sy2 + sy1;
			dx2 = dx2 + dx1;
			dy2 = dy2 + dy1;
		}
		
		double u1 = (double)sx1/src.width();
		double u2 = (double)sx2/src.width();
		double v1 = (double)sy1/src.height();
		double v2 = (double)sy2/src.height();

		texture(src);
		beginShape(CCDrawMode.QUADS);
		vertex(dx1, dy1, u1, v1);
		vertex(dx2, dy1, u2, v1);
		vertex(dx2, dy2, u2, v2);
		vertex(dx1, dy2, u1, v2);
		endShape();
	}
	
	public void copy(
		final int sx1, final int sy1, int sx2, int sy2, 
		final CCTexture des, 
		final int dx1, final int dy1, int dx2, int dy2
	){
		if (_myImageMode == CCShapeMode.CORNERS){
			sx2 = sx2 - sx1;
			sy2 = sy2 - sy1;
			dx2 = dx2 - dx1;
			dy2 = dy2 - dy1;
		}
		
		// Bind The Texture
		des.bind();

		gl.glCopyTexSubImage2D(des.target().glID, 0, dx1, dy1, sx1, sy1, sx2, sy2);
	}
	
	// / IntBuffer to go with the pixels[] array
	protected IntBuffer pixelBuffer;
	public int pixels[];

	public void loadPixels() {
		if ((pixels == null) || (pixels.length != width() * height())) {
			pixels = new int[width() * height()];
			pixelBuffer = CCBufferUtil.newIntBuffer(pixels.length);
		}

		gl.glReadPixels(0, 0, width(), height(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
		pixelBuffer.get(pixels);
		pixelBuffer.rewind();

		// flip vertically (opengl stores images upside down),
		// and swap RGBA components to ARGB (big endian)
		int index = 0;
		int yindex = (height() - 1) * width();
		for (int y = 0; y < height() / 2; y++) {
			if (CCSystem.endianess == CCEndianess.BIG_ENDIAN) {
				for (int x = 0; x < width(); x++) {
					int temp = pixels[index];
					// ignores alpha component, just sets it opaque
					pixels[index] = 0xff000000 | ((pixels[yindex] >> 8) & 0x00ffffff);
					pixels[yindex] = 0xff000000 | ((temp >> 8) & 0x00ffffff);

					index++;
					yindex++;
				}
			} else { // LITTLE_ENDIAN, convert ABGR to ARGB
				for (int x = 0; x < width(); x++) {
					int temp = pixels[index];

					// identical to updatePixels because only two
					// components are being swapped
					pixels[index] = 0xff000000 | ((pixels[yindex] << 16) & 0xff0000) | (pixels[yindex] & 0xff00) | ((pixels[yindex] >> 16) & 0xff);

					pixels[yindex] = 0xff000000 | ((temp << 16) & 0xff0000) | (temp & 0xff00) | ((temp >> 16) & 0xff);

					index++;
					yindex++;
				}
			}
			yindex -= width() * 2;
		}
	}

	static final double EPSILON = 0.0001f;

	public void updatePixels() {
		// flip vertically (opengl stores images upside down),

		int index = 0;
		int yindex = (height() - 1) * width();
		for (int y = 0; y < height() / 2; y++) {
			if (CCSystem.endianess == CCEndianess.BIG_ENDIAN) {
				// and convert ARGB back to opengl RGBA components (big endian)
				for (int x = 0; x < width(); x++) {
					int temp = pixels[index];
					pixels[index] = ((pixels[yindex] << 8) & 0xffffff00) | 0xff;
					pixels[yindex] = ((temp << 8) & 0xffffff00) | 0xff;

					index++;
					yindex++;
				}

			} else {
				// convert ARGB back to native little endian ABGR
				for (int x = 0; x < width(); x++) {
					int temp = pixels[index];

					pixels[index] = 0xff000000 | ((pixels[yindex] << 16) & 0xff0000) | (pixels[yindex] & 0xff00) | ((pixels[yindex] >> 16) & 0xff);

					pixels[yindex] = 0xff000000 | ((temp << 16) & 0xff0000) | (temp & 0xff00) | ((temp >> 16) & 0xff);

					index++;
					yindex++;
				}
			}
			yindex -= width() * 2;
		}

		gl.glRasterPos2d(EPSILON, height() - EPSILON);
		// gl.glRasterPos2f(width/2, height/2);

		pixelBuffer.put(pixels);
		pixelBuffer.rewind();
		gl.glDrawPixels(width(), height(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pixelBuffer);
	}

	public void updatePixels(int x, int y, int c, int d) {
		// throw new RuntimeException("updatePixels() not available with OpenGL");
		// TODO make this actually work for a smaller region
		// problem is, it gets pretty messy with the y reflection, etc
		updatePixels();
	}

	public void set(int x, int y, int argb){
		int getset = 0;

		if (CCSystem.endianess == CCEndianess.BIG_ENDIAN){
			// convert ARGB to RGBA
			getset = (argb << 8) | 0xff;

		}else{
			// convert ARGB to ABGR
			getset = (argb & 0xff00ff00) | ((argb << 16) & 0xff0000) | ((argb >> 16) & 0xff);
		}
		getsetBuffer.put(0, getset);
		gl.glRasterPos2d(x + EPSILON, y + EPSILON);
		gl.glDrawPixels(1, 1, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, getsetBuffer);
	}
	


	public void blend(final int sx, final int sy, final int dx, final int dy, final CCBlendMode mode){
		blendMode(mode);
		set(dx, dy, get(sx, sy));
		endBlend();
	}

	/**
	 * Now OPENGL based and much faster
	 */
	public void blend(final int sx1, final int sy1, final int sx2, final int sy2, final int dx1, final int dy1, final int dx2, final int dy2, final CCBlendMode mode){
		blendMode(mode);
		copy(sx1, sy1, sx2, sy2, dx1, dy1, dx2, dy2);
		endBlend();
	}
}
