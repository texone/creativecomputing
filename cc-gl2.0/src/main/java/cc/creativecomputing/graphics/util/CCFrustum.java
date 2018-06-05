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
package cc.creativecomputing.graphics.util;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCVector3;

public class CCFrustum {
	private enum CCFrustumPlane {
		TOP, BOTTOM, LEFT, RIGHT, NEAR, FAR
	}

	/**
	 * Describes the relations of an object to a frustum.
	 * Can either be inside, outside or intersecting
	 * @author christian riekoff
	 *
	 */
    public enum CCFrustumRelation {
    	/**
    	 * enum for an object outside of the frustum
    	 */
		OUTSIDE, 
		/**
    	 * enum for an object intersecting the frustum
    	 */
		INTERSECT, 
		/**
    	 * enum for an object inside the frustum
    	 */
		INSIDE
	}

    private CCPlane[] _myFrustumPlanes = new CCPlane[6];

	private CCVector3 _myNearTopLeft;
	private CCVector3 _myNearTopRight;
	private CCVector3 _myNearBottomLeft;
	private CCVector3 _myNearBottomRight;

	private CCVector3 _myFarTopLeft;
	private CCVector3 _myFarTopRight;
	private CCVector3 _myFarBottomLeft;
	private CCVector3 _myFarBottomRight;

	private double _myNear, _myFar, _myAspect, _myFov, _myTang;
	
	private double _myNearWidth;
	private double _myNearHeight;
	private double _myFarWidth;
	private double _myFarHeight;
	
	private CCCamera _myCamera;

	public CCFrustum(final CCCamera theCamera) {
		_myCamera = theCamera;
		
		updateFromCamera();
		setCamDef(_myCamera.position(), _myCamera.target(), _myCamera.up());
	}
	
	public CCPlane[] frustumPlanes() {
		return _myFrustumPlanes;
	}

	public void updateFromCamera() {

		_myFov = _myCamera.fov();
		_myAspect = _myCamera.aspect();
		_myNear = _myCamera.near();
		_myFar = _myCamera.far();

		_myTang = CCMath.tan(_myFov * 0.5f);
		_myNearHeight = _myNear * _myTang;
		_myNearWidth = _myNearHeight * _myAspect;
		_myFarHeight = _myFar * _myTang;
		_myFarWidth = _myFarHeight * _myAspect;
		
		CCVector3 myZ = _myCamera.position().subtract(_myCamera.target());
		myZ.normalizeLocal();

		CCVector3 myX = _myCamera.up().cross(myZ);
		myX.normalizeLocal();

		CCVector3 myY = myZ.cross(myX);

		CCVector3 myNearCenter = _myCamera.position().subtract(myZ.multiply(_myNear));
		CCVector3 myFarCenter = _myCamera.position().subtract(myZ.multiply(_myFar));
		
		double myNearFrustumOffsetX = _myCamera.frustumOffset().x;
		double myNearFrustumOffsetY = _myCamera.frustumOffset().y;
		
		double myFarFrustumOffsetX = myNearFrustumOffsetX * _myFar / _myNear;
		double myFarFrustumOffsetY = myNearFrustumOffsetY * _myFar / _myNear;

		_myNearTopLeft     = myNearCenter.add(     myY.multiply(_myNearHeight + myNearFrustumOffsetY)).subtract(myX.multiply(_myNearWidth - myNearFrustumOffsetX));
		_myNearTopRight    = myNearCenter.add(     myY.multiply(_myNearHeight + myNearFrustumOffsetY)).add(     myX.multiply(_myNearWidth + myNearFrustumOffsetX));
		_myNearBottomLeft  = myNearCenter.subtract(myY.multiply(_myNearHeight - myNearFrustumOffsetY)).subtract(myX.multiply(_myNearWidth - myNearFrustumOffsetX));
		_myNearBottomRight = myNearCenter.subtract(myY.multiply(_myNearHeight - myNearFrustumOffsetY)).add(     myX.multiply(_myNearWidth + myNearFrustumOffsetX));

		_myFarTopLeft      = myFarCenter.add(      myY.multiply(_myFarHeight  + myFarFrustumOffsetY)).subtract( myX.multiply(_myFarWidth  - myFarFrustumOffsetX));
		_myFarTopRight     = myFarCenter.add(      myY.multiply(_myFarHeight  + myFarFrustumOffsetY)).add(      myX.multiply(_myFarWidth  + myFarFrustumOffsetX));
		_myFarBottomLeft   = myFarCenter.subtract( myY.multiply(_myFarHeight  - myFarFrustumOffsetY)).subtract( myX.multiply(_myFarWidth  - myFarFrustumOffsetX));
		_myFarBottomRight  = myFarCenter.subtract( myY.multiply(_myFarHeight  - myFarFrustumOffsetY)).add(      myX.multiply(_myFarWidth  + myFarFrustumOffsetX));

		_myFrustumPlanes[CCFrustumPlane.TOP.ordinal()] = new CCPlane(_myNearTopRight, _myNearTopLeft, _myFarTopLeft);
		_myFrustumPlanes[CCFrustumPlane.BOTTOM.ordinal()] = new CCPlane(_myNearBottomLeft, _myNearBottomRight, _myFarBottomRight);
		_myFrustumPlanes[CCFrustumPlane.LEFT.ordinal()] = new CCPlane(_myNearTopLeft, _myNearBottomLeft, _myFarBottomLeft);
		_myFrustumPlanes[CCFrustumPlane.RIGHT.ordinal()] = new CCPlane(_myNearBottomRight, _myNearTopRight, _myFarBottomRight);
		_myFrustumPlanes[CCFrustumPlane.NEAR.ordinal()] = new CCPlane(_myNearTopLeft, _myNearTopRight, _myNearBottomRight);
		_myFrustumPlanes[CCFrustumPlane.FAR.ordinal()] = new CCPlane(_myFarTopRight, _myFarTopLeft, _myFarBottomLeft);
	}
	
	public CCVector3 closestIntersection(CCVector3 thePosition, CCVector3 theDirection) {
		CCRay3 myRay = new CCRay3(thePosition, theDirection);
		double myClosestDistance = Double.MAX_VALUE;
		CCVector3 myResult = null;
		for(CCPlane myFrustumPlane:frustumPlanes()) {
			CCVector3 myIntersection = myRay.intersectsPlane(myFrustumPlane);
			if(myIntersection == null)continue;
			double myDistance = thePosition.distance(myIntersection);
			if(myDistance < myClosestDistance) {
				myResult = myIntersection;
				myClosestDistance = myDistance;
			}
		}
		return myResult;
	}

	public void setCamDef(final CCVector3 thePo, CCVector3 theTa, CCVector3 theU) {

		
	}

	private boolean isInFrustum(final CCVector3 thePoint, int thePlanes) {
		for (int i = 0; i < thePlanes; i++) {
			if (_myFrustumPlanes[i].pseudoDistance(thePoint) < 0)
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if the given Point is in the frustum.
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 * 
	 * @param thePoint
	 *            the point to check
	 * @return <code>true</code> if the point is in the frustum <code>false</code>
	 *         otherwise
	 */
	public boolean isInFrustum(final CCVector3 thePoint) {
		return isInFrustum(thePoint, 6);
	}
	
	/**
	 * Checks if the given Point is in the frustum, but only checks for the top,
	 * bottom, left and right plane of the frustum.
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 * 
	 * @param thePoint
	 *            the point to check
	 * @return <code>true</code> if the point is in the frustum <code>false</code>
	 *         otherwise
	 */
	public boolean isInFrustumTBLR(final CCVector3 thePoint) {
		return isInFrustum(thePoint, 4);
	}

	private CCFrustumRelation isInFrustum(CCVector3 thePosition, double theRadius, int thePlanes) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		double distance;

		for (int i = 0; i < thePlanes; i++) {
			distance = _myFrustumPlanes[i].pseudoDistance(thePosition);
			if (distance < -theRadius)
				return CCFrustumRelation.OUTSIDE;
			else if (distance < theRadius)
				result = CCFrustumRelation.INTERSECT;
		}
		return (result);
	}
	
	/**
	 * Checks if the given bounding sphere is in the frustum. This method does not
	 * only return true or false but the relation to the frustum which might be
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 * 
	 * @param thePoint
	 *            the center of the bounding sphere to check
	 * @param theRadius
	 *            the radius of the bounding sphere to check
	 * @return the relation of the point to the frustum
	 */
	public CCFrustumRelation isInFrustum(CCVector3 thePoint, double theRadius) {
		return isInFrustum(thePoint, theRadius, 6);
	}
	
	/**
	 * Checks if the given bounding sphere is in the frustum, but only checks for
	 * the top, bottom, left and right plane of the frustum. This method does not
	 * only return true or false but the relation to the frustum which might be
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 * 
	 * @param thePoint
	 *            the center of the bounding sphere to check
	 * @param theRadius
	 *            the radius of the bounding sphere to check
	 * @return the relation of the point to the frustum
	 */
	public CCFrustumRelation isInFrustumTBLR(CCVector3 thePoint, double theRadius) {
		return isInFrustum(thePoint, theRadius, 4);
	}

	private CCFrustumRelation isInFrustum(final CCAABB theBoundingBox, int thePlanes) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		for(int i=0; i < thePlanes; i++) {
			if (_myFrustumPlanes[i].pseudoDistance(theBoundingBox.max(_myFrustumPlanes[i].normal())) < 0)
				return CCFrustumRelation.OUTSIDE;
			else if (_myFrustumPlanes[i].pseudoDistance(theBoundingBox.min(_myFrustumPlanes[i].normal())) < 0)
				result = CCFrustumRelation.INTERSECT;
		}
		return(result);
	}
	
	/**
	 * Checks if the given bounding box is in the frustum. This method does not
	 * only return true or false but the relation to the frustum which might be
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 * 
	 * @param theBoundingBox
	 *            the bounding box to check
	 * @return the relation of the point to the frustum
	 */
	public CCFrustumRelation isInFrustum(CCAABB theBoundingBox) {
		return isInFrustum(theBoundingBox, 6);
	}
	
	/**
	 * Checks if the given bounding box is in the frustum, but only checks for
	 * the top, bottom, left and right plane of the frustum. This method does not
	 * only return true or false but the relation to the frustum which might be
	 * {@linkplain CCFrustumRelation#INSIDE},{@linkplain CCFrustumRelation#INTERSECT}
	 * or {@linkplain CCFrustumRelation#OUTSIDE}
	 *
	 * @param theBoundingBox
	 *            the bounding box to check
	 * @return the relation of the point to the frustum
	 */
	public CCFrustumRelation isInFrustumTBLR(CCAABB theBoundingBox) {
		return isInFrustum(theBoundingBox, 4);
	}

	public void drawPoints(CCGraphics g) {
		g.beginShape(CCDrawMode.POINTS);

		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myNearBottomRight);

		g.vertex(_myFarTopLeft);
		g.vertex(_myFarTopRight);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		g.endShape();
	}

	public void drawLines(CCGraphics g) {
		g.beginShape(CCDrawMode.LINE_LOOP);

		// near plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearBottomLeft);

		g.endShape();

		g.beginShape(CCDrawMode.LINE_LOOP);
		
		// far plane
		g.vertex(_myFarTopRight);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		g.endShape();
		

		g.beginShape(CCDrawMode.LINES);

		// bottom plane
		g.vertex(_myNearBottomLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myNearBottomRight);
		g.vertex(_myFarBottomRight);

		// top plane
		g.vertex(_myNearTopRight);
		g.vertex(_myFarTopRight);
		g.vertex(_myNearTopLeft);
		g.vertex(_myFarTopLeft);

		// left plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myFarTopLeft);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myFarBottomLeft);

		// right plane
		g.vertex(_myNearBottomRight);
		g.vertex(_myFarBottomRight);
		g.vertex(_myNearTopRight);
		g.vertex(_myFarTopRight);

		g.endShape();
	}

	public void drawPlanes(CCGraphics g) {
		g.beginShape(CCDrawMode.QUADS);

		// near plane
		g.vertex(_myNearTopLeft);
		g.vertex(_myNearTopRight);
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearBottomLeft);

		// far plane
		g.vertex(_myFarTopRight);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarBottomRight);

		// bottom plane
		g.vertex(_myNearBottomLeft);
		g.vertex(_myNearBottomRight);
		g.vertex(_myFarBottomRight);
		g.vertex(_myFarBottomLeft);

		// top plane
		g.vertex(_myNearTopRight);
		g.vertex(_myNearTopLeft);
		g.vertex(_myFarTopLeft);
		g.vertex(_myFarTopRight);

		// left plane

		g.vertex(_myNearTopLeft);
		g.vertex(_myNearBottomLeft);
		g.vertex(_myFarBottomLeft);
		g.vertex(_myFarTopLeft);

		// right plane
		g.vertex(_myNearBottomRight);
		g.vertex(_myNearTopRight);
		g.vertex(_myFarTopRight);
		g.vertex(_myFarBottomRight);

		g.endShape();
	}

	public void drawNormals(CCGraphics g) {
		CCVector3 a, b;

		g.beginShape(CCDrawMode.LINES);

		// near
		a = CCVector3.add(_myNearTopRight, _myNearTopLeft, _myNearBottomRight, _myNearBottomLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.NEAR.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// far
		a = CCVector3.add(_myFarTopRight, _myFarTopLeft, _myFarBottomRight, _myFarBottomLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.FAR.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// left
		a = CCVector3.add(_myFarTopLeft, _myFarBottomLeft, _myNearBottomLeft, _myNearTopLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.LEFT.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// right
		a = CCVector3.add(_myFarTopRight, _myNearBottomRight, _myFarBottomRight, _myNearTopRight).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.RIGHT.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// top
		a = CCVector3.add(_myFarTopRight, _myFarTopLeft, _myNearTopRight, _myNearTopLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.TOP.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// bottom
		a = CCVector3.add(_myFarBottomRight, _myFarBottomLeft, _myNearBottomRight, _myNearBottomLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.BOTTOM.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		g.endShape();
	}
}
