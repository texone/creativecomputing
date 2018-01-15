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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCVector3;

public class CCFrustum {
	private enum CCFrustumPlane {
		TOP, BOTTOM, LEFT, RIGHT, NEARP, FARP
	}

    public enum CCFrustumRelation {
		OUTSIDE, INTERSECT, INSIDE
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
		myZ.normalize();

		CCVector3 myX = _myCamera.up().cross(myZ);
		myX.normalize();

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
		_myFrustumPlanes[CCFrustumPlane.NEARP.ordinal()] = new CCPlane(_myNearTopLeft, _myNearTopRight, _myNearBottomRight);
		_myFrustumPlanes[CCFrustumPlane.FARP.ordinal()] = new CCPlane(_myFarTopRight, _myFarTopLeft, _myFarBottomLeft);
	}

	public void setCamDef(final CCVector3 thePo, CCVector3 theTa, CCVector3 theU) {

		
	}

	public CCFrustumRelation isInFrustum(final CCVector3 thePoint) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		for (int i = 0; i < 6; i++) {
			if (_myFrustumPlanes[i].distance(thePoint) < 0)
				return CCFrustumRelation.OUTSIDE;
		}
		return (result);
	}

	public CCFrustumRelation isInFrustum(CCVector3 p, double raio) {
		CCFrustumRelation result = CCFrustumRelation.INSIDE;
		double distance;

		for (int i = 0; i < 6; i++) {
			distance = _myFrustumPlanes[i].distance(p);
			if (distance < -raio)
				return CCFrustumRelation.OUTSIDE;
			else if (distance < raio)
				result = CCFrustumRelation.INTERSECT;
		}
		return (result);
	}

	// public CCFrustumRelation boxInFrustum(final CCAABB theBoundingBox) {
	// CCFrustumRelation result = CCFrustumRelation.INSIDE;
	// for(int i=0; i < 6; i++) {
	// if (pl[i].distance(b.getVertexP(pl[i].normal())) < 0)
	// return CCFrustumRelation.OUTSIDE;
	// else if (pl[i].distance(b.getVertexN(pl[i].normal())) < 0)
	// result = CCFrustumRelation.INTERSECT;
	// }
	// return(result);
	// }

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
		b = a.add(_myFrustumPlanes[CCFrustumPlane.NEARP.ordinal()].normal().multiply(100));
		g.vertex(a);
		g.vertex(b);

		// far
		a = CCVector3.add(_myFarTopRight, _myFarTopLeft, _myFarBottomRight, _myFarBottomLeft).multiplyLocal(0.25f);
		b = a.add(_myFrustumPlanes[CCFrustumPlane.FARP.ordinal()].normal().multiply(100));
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
