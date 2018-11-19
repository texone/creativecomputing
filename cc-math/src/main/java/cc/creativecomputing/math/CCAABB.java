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
package cc.creativecomputing.math;




/**
 * Axis-aligned bounding box with basic intersection features for Ray, AABB and Sphere classes.
 */
public class CCAABB {

	/**
	 * Creates a new instance from two vectors specifying opposite corners of the box
	 * 
	 * @param theMin first corner point
	 * @param theMax second corner point
	 * @return new AABB with center at the half point between the 2 input vectors
	 */
	public static final CCAABB fromMinMax(final CCVector3 theMin, final CCVector3 theMax) {
		CCVector3 a = new CCVector3(
			CCMath.min(theMin.x, theMax.x), 
			CCMath.min(theMin.y, theMax.y), 
			CCMath.min(theMin.z, theMax.z)
		);
		CCVector3 b = new CCVector3(
			CCMath.max(theMin.x, theMax.x), 
			CCMath.max(theMin.y, theMax.y), 
			CCMath.max(theMin.z, theMax.z)
		);
		return new CCAABB(a.add(b).multiplyLocal(0.5f), b.subtract(a).multiplyLocal(0.5f));
	}

	private CCVector3 _myCenter;
	private CCVector3 _myExtent;

	private CCVector3 _myMin;
	private CCVector3 _myMax;

	public CCAABB() {
		_myCenter = new CCVector3();
		_myMin = new CCVector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		_myMax = new CCVector3(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		_myExtent = new CCVector3();
	}

	/**
	 * Creates an independent copy of the passed in box
	 * 
	 * @param theAABB
	 */
	public CCAABB(final CCAABB theAABB) {
		this(theAABB._myCenter.clone(), theAABB._myExtent.clone());
	}

	/**
	 * Creates a new instance from center point and extent
	 * 
	 * @param thePosition
	 * @param theExtent box dimensions (the box will be double the size in each direction)
	 */
	public CCAABB(final CCVector3 thePosition, final CCVector3 theExtent) {
		_myCenter = new CCVector3(thePosition);
		extent(new CCVector3(theExtent));
	}
	
	/**
	 * Creates a new instance from center point and extent
	 * 
	 * @param thePosition
	 * @param theExtent box dimensions (the box will be double the size in each direction)
	 */
	public CCAABB(final CCVector3 thePosition) {
		this(thePosition, new CCVector3());
	}

	public CCAABB clone() {
		return new CCAABB(this);
	}

	/**
	 * Returns the current box size
	 * 
	 * @return box size
	 */
	public final CCVector3 extent() {
		return _myExtent;
	}

	public CCVector3 center() {
		return _myCenter;
	}

	/**
	 * Updates the position of the box in space and calls {@link #updateBounds()} immediately
	 */
	public void center(final float theX, final float theY, final float theZ) {
		_myCenter.set(theX, theY, theZ);
		updateBounds();
	}
	
	public void center(final CCVector3 theCenter) {
		_myCenter.set(theCenter);
		updateBounds();
	}

	public CCVector3 max() {
		return _myMax;
	}

	public CCVector3 min() {
		return _myMin;
	}
	
	public CCVector3 max(CCVector3 theNormal) {
		CCVector3 myResult = min().clone();
		if (theNormal.x > 0)myResult.x += _myExtent.x;
		if (theNormal.y > 0)myResult.y += _myExtent.y;
		if (theNormal.z > 0)myResult.z += _myExtent.z;
		return myResult;
	}



	public CCVector3 min(CCVector3 theNormal) {
		CCVector3 myResult = min().clone();
		if (theNormal.x < 0)myResult.x += _myExtent.x;
		if (theNormal.y < 0)myResult.y += _myExtent.y;
		if (theNormal.z < 0)myResult.z += _myExtent.z;
		return(myResult);
	}
	
	/**
	 * Checks if the point is inside the given AABB.
	 * 
	 * @param box bounding box to check
	 * 
	 * @return true, if point is inside
	 */
	public boolean isInside(final CCVector3 theVector) {
		if (theVector.x < _myMin.x || theVector.x > _myMax.x) {
			return false;
		}
		if (theVector.y < _myMin.y || theVector.y > _myMax.y) {
			return false;
		}
        return !(theVector.z < _myMin.z) && !(theVector.z > _myMax.z);
    }

	/**
	 * Checks if the box intersects the passed in one.
	 * 
	 * @param theAABB box to check
	 * @return true, if boxes overlap
	 */
	public boolean intersectsBox(final CCAABB theAABB) {
		CCVector3 t = theAABB._myCenter.subtract(_myCenter);
		return 
			CCMath.abs(t.x) <= (_myExtent.x + theAABB._myExtent.x) && 
			CCMath.abs(t.y) <= (_myExtent.y + theAABB._myExtent.y) && 
			CCMath.abs(t.z) <= (_myExtent.z + theAABB._myExtent.z);
	}

	

	public void set(final CCAABB theAABB) {
		_myExtent.set(theAABB._myExtent);
		center(theAABB._myCenter);
	}

	/**
	 * Updates the size of the box and calls {@link #updateBounds()} immediately
	 * 
	 * @param theExtent new box size
	 * @return itself, for method chaining
	 */
	public CCAABB extent(final CCVector3 theExtent) {
		_myExtent = theExtent;
		return updateBounds();
	}

	/**
	 * Increases the size of the box so that the given vector is inside the box
	 * 
	 * @param thePoint the point to check
	 * @return itself, for method chaining
	 */
	public void checkSize(final double theX, final double theY, final double theZ) {
		if(theX > _myMax.x)_myMax.x = theX;
		if(theY > _myMax.y)_myMax.y = theY;
		if(theZ > _myMax.z)_myMax.z = theZ;
		
		if(theX < _myMin.x)_myMin.x = theX;
		if(theY < _myMin.y)_myMin.y = theY;
		if(theZ < _myMin.z)_myMin.z = theZ;
		
		_myExtent.x = (_myMax.x - _myMin.x) / 2;
		_myExtent.y = (_myMax.y - _myMin.y) / 2;
		_myExtent.z = (_myMax.z - _myMin.z) / 2;
		
		_myCenter.x = _myMax.x - _myExtent.x;
		_myCenter.y = _myMax.y - _myExtent.y;
		_myCenter.z = _myMax.z - _myExtent.z;
	}
	
	public void checkSize(final CCVector3 thePoint) {
		checkSize(thePoint.x, thePoint.y, thePoint.z);
	}
	
	public void checkSize(final CCAABB theBound) {
		checkSize(theBound.min());
		checkSize(theBound.max());
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<aabb> min: ").append(_myMin).append(" max: ").append(_myMax);
		return sb.toString();
	}

	/**
	 * Updates the min/max corner points of the box. MUST be called after moving the box in space by manipulating the
	 * public x,y,z coordinates directly.
	 * 
	 * @return itself
	 */
	public final CCAABB updateBounds() {
		// this is check is necessary for the constructor
		if (_myExtent != null) {
			_myMin = _myCenter.subtract(_myExtent);
			_myMax = _myCenter.add(_myExtent);
		}
		return this;
	}
	
	
//	public void draw(CCGraphics g) {
//		g.pushMatrix();
//		g.translate(_myCenter);
//		g.boxGrid(_myExtent.x * 2, _myExtent.y * 2, _myExtent.z * 2);
//		g.popMatrix();
//	}
}
