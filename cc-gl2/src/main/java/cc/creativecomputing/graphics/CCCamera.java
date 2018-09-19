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

import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW_MATRIX;
import static org.lwjgl.opengl.GL11.GL_PROJECTION_MATRIX;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glGetFloatv;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.glu.GLU;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;


/**
 * Use this class to control the virtual camera of your application. 
 * 
 * Defaults: 
 * <ul>
 * <li><b>Camera position</b> - sits on the positive z-axis</li>
 * <li><b>Target position</b> - located at the world origin</li>
 * <li><b>Up direction</b> - point in the negative y</li>
 * <li><b>Field-of-view</b> - PI/3 radians (60 degrees)</li>
 * <li><b>Aspect ratio</b> - application width to application height</li>
 * <li><b>Near clipping plane</b> - 0.1x shot length</li>
 * <li><b>Far clipping plane</b> - 10x the shot length</li>
 * </ul>
 * 
 * @author Christian Riekoff
 */
public class CCCamera{
	// --- Class Attributes ----
	private static final double TOL = 0.00001f;
	public static double DEFAULT_FOV = 60;

//	private CCGraphics _myGraphics;

	// Camera Orientation Information
	private double _myXrotation;
	private double _myYRotation;
	private double _myZRotation;
	
	private CCViewport _myViewport;
	
	private CCVector2 _myFrustumOffset = new CCVector2();

	/**
	 * Camera Position
	 */
	private CCVector3 _myPosition;

	/**
	 * Target Position
	 */
	private CCVector3 _myTarget;

	/**
	 * Up Vector
	 */
	private CCVector3 _myUp;

	/**
	 * Field of View
	 */
	private double _myFoV;

	/**
	 * Aspect Ratio
	 */
	private double _myAspect;

	// Clip Planes
	private double _myNearClip;
	private double _myFarClip;

	/**
	 * The length of the view vector
	 */
	private double _myShotLength;

	/**
	 * Distance differences between camera and target
	 */
	private CCVector3 _myDelta;
	
	private CCFrustum _myFrustum;

	/**
	 * Create a camera that sits on the z axis
	 */
	public CCCamera(final CCGraphics g) {
		this(g.width(),g.height());
	}
	
	public CCCamera(final int theWidth, final int theHeight){
		this(theWidth, theHeight, DEFAULT_FOV * CCMath.DEG_TO_RAD);
	}
	
	public CCCamera(final int theWidth, final int theHeight, final double theFov){
		set(theWidth, theHeight, theFov);
		_myFrustum = new CCFrustum(this);
	}

	/**
	 * Create a camera that sits on the z axis with a specified shot length
	 * @param theParent
	 * @param theShotLength
	 */
	public CCCamera(final CCGraphics g, final double theShotLength) {
		this(g.width(), g.height(), 0, 0, theShotLength,0,0,0);
	}
	
	public CCCamera(final int theWidth, final int theHeight, final CCVector3 thePosition){
		this(theWidth, theHeight, thePosition.x, thePosition.y, thePosition.z,0,0,0);
	}
	
	public CCCamera(final int theWidth, final int theHeight, final CCVector3 thePosition, final CCVector3 theTarget){
		this(
			theWidth, theHeight, 
			thePosition.x, thePosition.y, thePosition.z,
			theTarget.x, theTarget.y, theTarget.z
		);
	}

	/**
	 * Create a camera at the specified location with the specified target
	 * @param theParent
	 * @param theCameraX
	 * @param theCameraY
	 * @param theCameraZ
	 * @param theTargetX
	 * @param theTargetY
	 * @param theTargetZ
	 */
	public CCCamera(
		final int theWidth, final int theHeight, 
		final double theCameraX, final double theCameraY, final double theCameraZ, 
		final double theTargetX, final double theTargetY, final double theTargetZ
	) {
		this(
			theWidth, theHeight, 
			theCameraX, theCameraY, theCameraZ, 
			theTargetX, theTargetY, theTargetZ, 
			0, 1, 0, 
			DEFAULT_FOV * CCMath.DEG_TO_RAD, (double) (1f * theWidth / theHeight), 
			0, 0
		);

		_myNearClip = _myShotLength * 0.1f;
		_myFarClip = _myShotLength * 100f;
	}

	// Specify all parameters for camera creation
	public CCCamera(
		final int theWidth, final int theHeight, 
		final CCVector3 thePosition, final CCVector3 theTarget, final CCVector3 theUp, 
		final double theFov, final double theAspect,
		final double theNearClip, final double theFarClip
	){
		this(
			theWidth, theHeight,
			thePosition.x, thePosition.y, thePosition.z,
			theTarget.x, theTarget.y, theTarget.z,
			theUp.x, theUp.y, theUp.z,
			theFov, theAspect,
			theNearClip, theFarClip
		);
	}
	
	public CCCamera(
		final double theFov, final double theAspect,
		final double theNearClip, final double theFarClip
	){
		this(
			-1, -1, 
			0, 0, 0,
			0, 0, -1,
			0, 1, 0,
			theFov, theAspect, 
			theNearClip, theFarClip
		);
	}
	
	public CCCamera(
		final int theWidth, final int theHeight, 
		final double theCameraX, final double theCameraY, final double theCameraZ, 
		final double theTargetX, final double theTargetY, final double theTargetZ,
		final double theUpX, final double theUpY, final double theUpZ, 
		final double theFov, final double theAspect,
		final double theNearClip, final double theFarClip
	){
		_myPosition = new CCVector3(theCameraX, theCameraY, theCameraZ);
		_myTarget = new CCVector3(theTargetX, theTargetY, theTargetZ);
		_myUp = new CCVector3(theUpX, theUpY, theUpZ);
		
		_myFoV = theFov;
		_myAspect = theAspect;
		
		_myNearClip = theNearClip;
		_myFarClip = theFarClip;
		
		_myDelta = _myPosition.subtract(_myTarget);

		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}
		
		if(theWidth > 0 && theHeight > 0)_myViewport = new CCViewport(0,0,theWidth,theHeight);

		updateUp();
		updateProjectionInfos();
		
		_myFrustum = new CCFrustum(this);
	}
	
	public void set(
		final double theCameraX, final double theCameraY, final double theCameraZ, 
		final double theTargetX, final double theTargetY, final double theTargetZ,
		final double theUpX, final double theUpY, final double theUpZ, 
		final double theFov, final double theAspect,
		final double theNearClip, final double theFarClip
	){
		_myPosition = new CCVector3(theCameraX, theCameraY, theCameraZ);
		_myTarget = new CCVector3(theTargetX, theTargetY, theTargetZ);
		_myUp = new CCVector3(theUpX, theUpY, theUpZ);
			
		_myFoV = theFov;
		_myAspect = theAspect;
			
		_myNearClip = theNearClip;
		_myFarClip = theFarClip;
			
		_myDelta = _myPosition.subtract(_myTarget);

		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}

		updateUp();
	}
	
	public void set(final int theWidth, final int theHeight, final double theFov) {
		_myViewport = new CCViewport(0,0,theWidth,theHeight);
		// init perspective projection based on new dimensions
		double cameraFOV = theFov; // at least for now
		double cameraAspect = (double) theWidth / (double) theHeight;
		
		double cameraX = 0;
		double cameraY = 0;
		double cameraZ = theHeight / 2.0f / CCMath.tan(cameraFOV / 2.0f);
		
		
		double cameraNear = cameraZ / 10.0f;
		double cameraFar = cameraZ * 100.0f;
		
		set(
			cameraX, cameraY, cameraZ, 
			cameraX, cameraY, 0, 
			0, 1, 0,
			cameraFOV, cameraAspect,
			cameraNear, cameraFar
		);
	}
	
	public void set(final int theWidth, final int theHeight){
		set(theWidth, theHeight, DEFAULT_FOV * CCMath.DEG_TO_RAD);
	}
	
	public void reset(CCGraphics g){
		_myFoV = 60 * CCMath.DEG_TO_RAD; // at least for now
		_myAspect = (double) g.width() / (double) g.height();
		
		_myPosition = new CCVector3(0, 0, g.height() / 2.0f / Math.tan(_myFoV / 2.0f));
		_myTarget = new CCVector3(0,0,0);
		_myUp = new CCVector3(0,1,0);
		
		_myDelta = _myPosition.subtract(_myTarget);
		
		_myShotLength = _myDelta.length();
		
		_myNearClip = _myPosition.z / 10.0f;
		_myFarClip = _myPosition.z * 10.0f;

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x));

		if (_myXrotation > CCMath.HALF_PI - TOL) {
			_myUp.y = 0;
			_myUp.z = -1;
		}

		if (_myXrotation < TOL - CCMath.HALF_PI) {
			_myUp.y = 0;
			_myUp.z = 1;
		}
		
		_myViewport = new CCViewport(0,0,g.width(),g.height());
		_myFrustumOffset = new CCVector2();

		updateUp();
	}

	/**
	 * @invisible
	 * @param g
	 */
	public void drawFrustum(CCGraphics g){
		if(_myViewport != null)_myAspect = _myViewport.aspectRatio();
		
		double ymax = _myNearClip * Math.tan(_myFoV / 2.0f);
		double ymin = -ymax;

		double xmin = ymin * _myAspect;
		double xmax = ymax * _myAspect;

		g.frustum(
			xmin + _myFrustumOffset.x, xmax + _myFrustumOffset.x, 
			ymax + _myFrustumOffset.y, ymin + _myFrustumOffset.y, 
			_myNearClip, _myFarClip
		);
	}
	
	public void drawPerspective(CCGraphics g){
		GLU.gluPerspective((float)_myFoV, (float)_myAspect, (float)_myNearClip, (float)_myFarClip);
	}
	
	public void beginDraw(CCGraphics g){
		CCMatrixMode myMatrixMode = g.matrixMode();
		g.matrixMode(CCMatrixMode.PROJECTION);
		g.pushMatrix();
		g.matrixMode(myMatrixMode);
		draw(g);
	}
	
	public void endDraw(CCGraphics g){
		CCMatrixMode myMatrixMode = g.matrixMode();
		g.matrixMode(CCMatrixMode.PROJECTION);
		g.popMatrix();
		g.matrixMode(myMatrixMode);
	}

	/**
	 * Send what this camera sees to the view port
	 */
	public void draw(CCGraphics g) {
		if(_myViewport != null)_myViewport.draw(g);
		
		updateProjectionInfos();

		glLoadIdentity();
		g.applyMatrix(CCMatrix4x4.createLookAt(_myPosition, _myTarget, _myUp));
		drawFrustum(g);
		
	}

	/**
	 * Aim the camera at the specified target
	 * @param theTargetX
	 * @param theTargetY
	 * @param theTargetZ
	 */
	public void target(final double theTargetX, final double theTargetY, final double theTargetZ) {
		_myTarget.set(theTargetX, theTargetY, theTargetZ);
		updateDeltas();
	}
	
	/**
	 * 
	 * @param theTarget
	 */
	public void target(final CCVector3 theTarget){
		_myTarget.set(theTarget);
		updateDeltas();
	}

	/**
	 * Returns the target position
	 * @return
	 */
	public CCVector3 target() {
		return _myTarget;
	}

	/**
	 * Jump the camera to the specified position
	 * @param positionX
	 * @param positionY
	 * @param positionZ
	 */
	public void position(final double thePositionX, final double thePositionY, final double thePositionZ) {
		// Move the camera
		_myPosition.set(thePositionX,thePositionY,thePositionZ);
		updateDeltas();
	}
	
	public void position(final CCVector3 thePosition) {
		// Move the camera
		_myPosition.set(thePosition);
		updateDeltas();
	}

	public void up(final CCVector3 theUp) {
		_myUp.set(theUp);
	}

	public void up(final double theUpX, final double theUpY, final double theUpZ) {
		_myUp.set(theUpX, theUpY, theUpZ);
	}
	
	/**
	 * Returns the camera position
	 */
	public CCVector3 position() {
		return _myPosition;
	}

	/**
	 * Change the field of view between "fish-eye" and "close-up"
	 * @param theAmount
	 */
	public void zoom(final double theAmount) {
		_myFoV = CCMath.constrain(_myFoV + theAmount, TOL, CCMath.PI - TOL);
	}
	
	public CCViewport viewport(){
		return _myViewport;
	}
	
	public void viewport(final CCViewport theViewport){
		_myViewport = theViewport;
		// init perspective projection based on new dimensions
		double cameraFOV = _myFoV; // at least for now
		double cameraAspect = (double) theViewport.width() / (double) theViewport.height();
				
		double cameraX = 0;
		double cameraY = 0;
		double cameraZ = theViewport.height() / 2.0f / CCMath.tan(cameraFOV / 2.0f);
				
				
		double cameraNear = cameraZ / 10.0f;
		double cameraFar = cameraZ * 100.0f;

		set(
			cameraX, cameraY, cameraZ, 
			cameraX, cameraY, 0, 
			0, 1, 0,
			cameraFOV, cameraAspect,
			cameraNear, cameraFar
		);
	}
	
	public CCVector2 frustumOffset(){
		return _myFrustumOffset;
	}

	public void frustumOffset(CCVector2 theOffset){
		_myFrustumOffset.set(theOffset);
	}
	
	public CCFrustum frustum(){
		return _myFrustum;
	}

	//////////////////////////////////////////////////
	//
	// CAMERA AXIS
	//
	//////////////////////////////////////////////////
	
	/**
	 * Calculates the x axis of the camera
	 * @return x axis of the camera
	 */
	public CCVector3 xAxis() {
		// calculate the camera's X axis in world space
		final CCVector3 myDelta = _myTarget.subtract(_myPosition);
		myDelta.normalizeLocal();
		
		return myDelta.cross(_myUp);
	}
	
	/**
	 * Calculates the y axis of the camera
	 * @return y axis of the camera
	 */
	public CCVector3 yAxis() {
		return _myUp.clone();
	}
	
	/**
	 * Calculates the z axis of the camera
	 * @return z axis of the camera
	 */
	public CCVector3 zAxis() {
		return _myDelta.clone();
	}
	
	//////////////////////////////////////////////////
	//
	// CAMERA MOVEMENT
	//
	//////////////////////////////////////////////////

	/**
	 * Move the camera and target simultaneously along the camera's X axis
	 * @param theAmount
	 */
	public void moveX(final double theAmount) {
		// calculate the camera's X axis in world space
		final CCVector3 myDirection = xAxis();

		// normalize and scale translation vector
		myDirection.normalizeLocal();
		myDirection.multiplyLocal(theAmount);

		// translate both camera position and target
		_myPosition.addLocal(myDirection);
		_myTarget.addLocal(myDirection);
	}

	/**
	 * Move the camera and target simultaneously along the camera's Y axis
	 * @param theAmount
	 */
	public void moveY(final double theAmount) {
		// Perform the boom, if any
		_myPosition.addLocal(
			_myUp.x * theAmount,
			_myUp.y * theAmount,
			_myUp.z * theAmount
		);
		_myTarget.addLocal(
			_myUp.x * theAmount,
			_myUp.y * theAmount,
			_myUp.z * theAmount
		);
	}

	/**
	 * Move the camera and target along the view vector
	 * @param theAmount
	 */
	public void moveZ(final double theAmount) {
		// Normalize the view vector
		final CCVector3 myDirection = _myDelta.clone();
		myDirection.multiplyLocal(theAmount/ _myShotLength);

		// Perform the dolly, if any
		_myPosition.addLocal(myDirection);
		_myTarget.addLocal(myDirection);
	}

	/**
	 * Moves the camera and target simultaneously in the camera's X-Y plane
	 * @param theXOffset
	 * @param theYOffset
	 */
	public void moveXY(final double theXOffset, final double theYOffset) {
		// Perform the truck, if any
		moveX(theXOffset);

		// Perform the boom, if any
		moveY(theYOffset);
	}
	
	/**
	 * Moves the camera by the defined vector
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void move(final double theX, final double theY, final double theZ){
		_myPosition.addLocal(theX, theY, theZ);
		_myTarget.addLocal(theX, theY, theZ);
	}
	
	public void move(final CCVector3 theVector){
		_myPosition.addLocal(theVector);
		_myTarget.addLocal(theVector);
	}
	
	//////////////////////////////////////////////////
	//
	// CAMERA ROTATION 
	//
	//////////////////////////////////////////////////

	/**
	 * Rotate the camera about its X axis
	 * @param theXrotation
	 */
	public void rotateX(final double theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation - theXrotation, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		// Update the target
		updateTarget();
	}

	/**
	 * Rotate the camera about its Y axis
	 * @param theYrotation
	 */
	public void rotateY(final double theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = (_myYRotation - theYrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the target
		updateTarget();
	}


	/**
	 * Rotate the camera about its Z axis
	 * @param theZrotation
	 */
	public void rotateZ(final double theZrotation) {
		// Change the roll amount
		_myZRotation = (_myZRotation + theZrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the up vector
		updateUp();
	}
	
	/**
	 * Sets the rotation of the camera about its X axis. Note that this method different
	 * from {@link #rotateX(double)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between -HALF_PI and HALF_PI
	 * @param theXrotation new x rotation of the camera 
	 */
	public void xRotation(final double theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = CCMath.constrain(
			theXrotation, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		// Update the target
		updateTarget();
	}
	
	/**
	 * Sets the rotation of the camera about its Y axis. Note that this method different
	 * from {@link #rotateY(double)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between 0 and TWO_PI
	 * @param theYrotation new y rotation of the camera 
	 */
	public void yRotation(final double theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = CCMath.constrain(
			theYrotation, 
			0, 
			CCMath.TWO_PI
		);

		// Update the target
		updateTarget();
	}
	
	/**
	 * Sets the rotation of the camera about its Z axis. Note that this method different
	 * from {@link #rotateZ(double)} that rotates the camera by a given amount, while this
	 * is setting the rotation directly. The given value will be constrained to a value
	 * between 0 and TWO_PI
	 * @param theZrotation new z rotation of the camera 
	 */
	public void zRotation(final double theZrotation) {
		// Calculate the new azimuth for the camera
		_myZRotation = CCMath.constrain(
			theZrotation, 
			0, 
			CCMath.TWO_PI
		);

		// Update the up vector
		updateUp();
	}

	/**
	 * Rotate the camera about its X axis around the target of the camera
	 * @param theXrotation
	 */
	public void rotateXaroundTarget(final double theXrotation) {
		// Calculate the new elevation for the camera
		_myXrotation = //CCMath.constrain(
			_myXrotation + theXrotation;
//			TOL - CCMath.HALF_PI, 
//			CCMath.HALF_PI - TOL
//		);

		// Update the camera
		updateCamera();
	}

	/**
	 * Circle the camera around a center of interest at a set elevation
	 * @param theYrotation
	 */
	public void rotateYaroundTarget(final double theYrotation) {
		// Calculate the new azimuth for the camera
		_myYRotation = (_myYRotation + theYrotation + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the camera
		updateCamera();
	}
	
	/**
	 * Look about the camera's position
	 * @param theAzimuthOffset
	 * @param theElevationOffset
	 */
	public void look(final double theAzimuthOffset, final double theElevationOffset) {
		// Calculate the new azimuth and elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation - theElevationOffset, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		_myYRotation = (_myYRotation - theAzimuthOffset + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the target
		updateTarget();
	}

	/**
	 * Tumble the camera about its target
	 * @param theAzimuthOffset
	 * @param theElevationOffset
	 */
	public void tumble(final double theAzimuthOffset, final double theElevationOffset) {
		// Calculate the new azimuth and elevation for the camera
		_myXrotation = CCMath.constrain(
			_myXrotation + theElevationOffset, 
			TOL - CCMath.HALF_PI, 
			CCMath.HALF_PI - TOL
		);

		_myYRotation = (_myYRotation + theAzimuthOffset + CCMath.TWO_PI) % CCMath.TWO_PI;

		// Update the camera
		updateCamera();
	}

	// Returns the camera orientation
	public double[] attitude() {
		return new double[] { _myYRotation, _myXrotation, _myZRotation };
	}

	// Returns the "up" vector
	public CCVector3 up() {
		return _myUp;
	}

	/**
	 * Returns the field of view
	 * @return
	 */
	public double fov() {
		return _myFoV;
	}
	
	public void fov(final double theFoV){
		_myFoV = theFoV;
	}
	
	public double aspect(){
		return _myAspect;
	}
	
	public double near(){
		return _myNearClip;
	}
	
	public void near(final double theNearClip) {
		_myNearClip = theNearClip;
	}
	
	public double far(){
		return _myFarClip;
	}
	
	public void far(final double theFarClip) {
		_myFarClip = theFarClip;
	}
	
	public double depthRange() {
		return _myFarClip - _myNearClip;
	}

	/**
	 * Update deltas and related information
	 */
	private void updateDeltas() {
		// Describe the new vector between the camera and the target
		_myDelta = position().subtract(_myTarget);

		// Describe the new azimuth and elevation for the camera
		_myShotLength = _myDelta.length();

		_myYRotation = CCMath.atan2(_myDelta.x, _myDelta.z);
		_myXrotation = CCMath.atan2(_myDelta.y, CCMath.sqrt(_myDelta.z* _myDelta.z + _myDelta.x * _myDelta.x));

		// update the up vector
		updateUp();
		
		_myFrustum.updateFromCamera();
	}

	/**
	 * Update target and related information
	 */
	private void updateTarget() {
		// Rotate to the new orientation while maintaining the shot distance.
		_myTarget.x = _myPosition.x - (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.sin(_myYRotation));
		_myTarget.y = _myPosition.y  - (-_myShotLength * CCMath.cos(CCMath.HALF_PI + _myXrotation));
		_myTarget.z = _myPosition.z  - (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.cos(_myYRotation));

		// update the up vector
		updateUp();
	}

	/**
	 * Update target and related information
	 */
	private void updateCamera() {
		// Orbit to the new orientation while maintaining the shot distance.
		_myPosition.x = _myTarget.x	+ (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.sin(_myYRotation));
		_myPosition.y = _myTarget.y + (-_myShotLength * CCMath.cos(CCMath.HALF_PI + _myXrotation));
		_myPosition.z = _myTarget.z + (_myShotLength * CCMath.sin(CCMath.HALF_PI + _myXrotation) * CCMath.cos(_myYRotation));

		// update the up vector
		updateUp();
	}

	/**
	 * Update the up direction and related information
	 */
	private void updateUp() {
		// Describe the new vector between the camera and the target
		_myDelta = _myPosition.clone();
		_myDelta.subtract(_myTarget);

		// Calculate the new "up" vector for the camera
		_myUp.x = -_myDelta.x * _myDelta.y;
		_myUp.y = _myDelta.z * _myDelta.z + _myDelta.x * _myDelta.x;
		_myUp.z = -_myDelta.z * _myDelta.y;

		// Normalize the "up" vector
		_myUp.normalize();

		// Calculate the roll if there is one
		if (_myZRotation != 0) {
			// Calculate the camera's X axis in world space
			final CCVector3 myDirection = new CCVector3(
				_myDelta.y * _myUp.z - _myDelta.z * _myUp.y,
				_myDelta.x * _myUp.z - _myDelta.z * _myUp.y,
				_myDelta.x * _myUp.y - _myDelta.y * _myUp.x	
			);

			// Normalize this vector so that it can be scaled
			myDirection.normalize();

			// Perform the roll
			final double myCosRoll = CCMath.cos(_myZRotation);
			final double mySinRoll = CCMath.sin(_myZRotation);
			_myUp.x = _myUp.x * myCosRoll + myDirection.x * mySinRoll;
			_myUp.y = _myUp.y * myCosRoll + myDirection.y * mySinRoll;
			_myUp.z = _myUp.z * myCosRoll + myDirection.z * mySinRoll;
		}
	}
	
	//////////////////////////////////////////////////////////
	//
	// Calculations
	//
	//////////////////////////////////////////////////////////
	private final int viewport[] = new int[4];
	private final float[] _myProjectionMatrix = new float[16];
	private final float[] _myViewMatrix = new float[16];
	private final FloatBuffer myResultArray = FloatBuffer.allocate(4);
	
	public void updateProjectionInfos(){
		// get viewport
		glGetIntegerv(GL_VIEWPORT, viewport);

		// get projection matrix
		glGetFloatv(GL_PROJECTION_MATRIX, _myProjectionMatrix);

		// get modelview matrix
		glGetFloatv(GL_MODELVIEW_MATRIX, _myViewMatrix);
	}
	
	public CCMatrix4x4 viewMatrix(){
		CCMatrix4x4 myResult = new CCMatrix4x4();
		myResult.fromArray(_myViewMatrix, true);
		return myResult;
	}
	
	public CCMatrix4x4 projectionMatrix(){
		CCMatrix4x4 myResult = new CCMatrix4x4();
		myResult.fromArray(_myProjectionMatrix, true);
		return myResult;
	}
	
	/**
	 * project transforms the specified object coordinates into
	 * window coordinates using model, proj and view. The result
	 * is stored in the returned vector
	 */
	public CCVector3 modelToScreen(final CCVector3 theObjectVector) {
		return modelToScreen(theObjectVector.x,theObjectVector.y,theObjectVector.z);
	}
	
	public CCVector3 modelToScreen(final double theX, final double theY, final double theZ){
		GLU.gluProject(
			(float)theX, 
			(float)theY, 
			(float)theZ, 
			FloatBuffer.wrap(_myViewMatrix), 
			FloatBuffer.wrap(_myProjectionMatrix), 
			IntBuffer.wrap(viewport), 
			myResultArray
		);
		myResultArray.rewind();
		return new CCVector3(myResultArray.get(), myResultArray.get(), myResultArray.get());
	}
	
	/**
	 * Screen to model will map the given window coordinates to model coordinates. The depth of
	 * the model coordinates is read from the depth buffer. Optionally you can pass a depth value
	 * to this function that must be in the range 0 to 1.
	 * @shortdesc Calculates the model coordinates corresponding to the given screen coordinates.
	 * @param theX
	 * @param theY
	 * @param theDepth
	 * @return
	 */
	public CCVector3 screenToModel(final double theX, final double theY, final double theDepth) {
		//For the viewport matrix... not sure what all the values are, I think
		// the first two are width and height, and all Matrices in GL seem to
		// be 4 or 16...
		myResultArray.rewind();
		GLU.gluUnProject(
			(float)theX, 
			(float)theY,
			(float)theDepth, 
			FloatBuffer.wrap(_myViewMatrix), 
			FloatBuffer.wrap(_myProjectionMatrix), 
			IntBuffer.wrap(viewport),
			myResultArray
		);
		myResultArray.rewind();
		return new CCVector3(myResultArray.get(), myResultArray.get(), myResultArray.get());
	}

	/**
	 * @param theWindowVector
	 * @return
	 */
	public CCVector3 screenToModel(final CCVector3 theWindowVector) {
		return screenToModel(theWindowVector.x,theWindowVector.y,theWindowVector.z);
	}

	/**
	 * @param theWindowVector
	 * @return
	 */
	public CCVector3 screenToModel(final CCVector2 theWindowVector) {
//		CCVector3 myPos0 = screenToModel(theWindowVector.x, theWindowVector.y, 0);
//		CCVector3 myPos1 = screenToModel(theWindowVector.x, theWindowVector.y, 1);
//		CCLog.info(myPos0, myPos1, myPos1.subtract(myPos0));
//		CCRay3 myRay = new CCRay3(myPos0, myPos0.subtract(myPos1));
//		CCPlane myViewPlane = new CCPlane(new CCVector3(), new CCVector3(0,0,1));
//		CCVector3 myIntersection = myRay.intersectsPlane(myViewPlane);
//		CCLog.info(screenToModel(theWindowVector.x, theWindowVector.y, 0.9));
		return screenToModel(theWindowVector.x, theWindowVector.y, 0.9);
		//return screenToModel(theWindowVector.x, theWindowVector.y, 0.9);
	}
	
	/**
	 * @param theMouseX
	 * @param theMouseY
	 * @return the model coordinates for the given screen position
	 */
	public CCVector3 screenToModel(final int theMouseX, final int theMouseY) {
		// set up a floatbuffer to get the depth buffer value of the mouse
		double myDepth = 0;
		try(MemoryStack myStack = MemoryStack.stackPush()){
			final FloatBuffer myFloatBuffer = myStack.mallocFloat(1);
			
			// Get the depth buffer value at the mouse position. have to do
			// height-mouseY, as GL puts 0,0 in the bottom left, not top left.
			glReadPixels(theMouseX, theMouseY, 1, 1, GL_DEPTH_COMPONENT, GL_FLOAT, myFloatBuffer);
			
			myDepth = myFloatBuffer.get(0);
		}
		// the result x,y,z will be put in this.. 4th value will be 1, but I
		// think it's "scale" in GL terms, but I think it'll always be 1.

		return  screenToModel(theMouseX,  theMouseY,myDepth);
	}
	
	/**
	 * Use this method to calculate the vector perpendicular to the screen. At the
	 * given position. The resulting vector is normalized and facing forward away
	 * from the screen.
	 * @param thePosition
	 * @return
	 */
	public CCVector3 screenOrthogonal(CCVector3 thePosition){
		CCVector3 myScreenCoords = modelToScreen(thePosition);
		CCVector3 myModelCoords = screenToModel(myScreenCoords.x,myScreenCoords.y,0);
		
		CCVector3 myResult = myModelCoords.subtract(thePosition);
		myResult.normalizeLocal();
		return myResult;
	}
	
	public CCVector3 screenOrthogonal(final double theX, final double theY, final double theZ){
		return screenOrthogonal(new CCVector3(theX,theY,theZ));
	}
	
	public CCVector3 screenOrthogonal(final double theX, final double theY) {
		CCVector3 myVec1 = screenToModel(theX, theY,0);
		CCVector3 myVec2 = screenToModel(theX, theY,1);
		
		return myVec1.subtract(myVec2).normalizeLocal();
	}
	
	public CCVector3 screenOrthogonal(final CCVector2 theVector) {
		return screenOrthogonal(theVector.x, theVector.y);
	}
	
	/**
	 * Returns the dimension of the screen at the given z value
	 * @param theZ
	 * @return
	 */
	public CCVector2 screenDimension(final double theZ){
		double tang = CCMath.tan(_myFoV * 0.5f) ;
		double myHeight = (_myPosition.z - theZ) * tang * 2;
		double myWidth = myHeight * _myAspect;
		return new CCVector2(myWidth,myHeight);
	}
	
	public String toString(){
		StringBuilder _myStringBuilder = new StringBuilder();
		_myStringBuilder.append("Camera Settings:\n");
		_myStringBuilder.append("position:\n");
		_myStringBuilder.append("target:\n");
		_myStringBuilder.append("up:\n");
		return _myStringBuilder.toString();
	}
}
