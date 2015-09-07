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

public class CCLine3 {

	protected CCVector3 _myStart;
	protected CCVector3 _myEnd;
	

	public CCLine3(final CCVector3 theStart, final CCVector3 theEnd) {
		_myStart = theStart;
		_myEnd = theEnd;
	}

	public CCLine3(
		final double theStartX, final double theStartY, final double theStartZ,
		final double theEndX, final double theEndY, final double theEndZ
	) {
		this(new CCVector3(theStartX, theStartY, theStartZ), new CCVector3(theEndX, theEndY, theEndZ));
	}

	public CCLine3(final CCLine3 theSegment) {
		this(theSegment._myStart, theSegment._myEnd);
	}

	public CCLine3() {
		this(0, 0, 0, 0, 0, 0);
	}

	/**
	 * @return the start
	 */
	public CCVector3 start() {
		return _myStart;
	}

	/**
	 * @return the end
	 */
	public CCVector3 end() {
		return _myEnd;
	}

	public double length() {
		return _myStart.distance(_myEnd);
	}
	
	@Override
	public boolean equals(final Object theSegment) {
		if(!(theSegment instanceof CCLine3))return false;
		
		CCLine3 mySegment = (CCLine3)theSegment;
		return 
			mySegment.start().equals(start()) && mySegment.end().equals(end()) ||
			mySegment.start().equals(end()) && mySegment.end().equals(start());
	}
	
	public double closestPointBlend(CCVector3 thePoint){
		return closestPointBlend(thePoint.x, thePoint.y, thePoint.z);
	}
	
	public double closestPointBlend(final double theX, final double theY, final double theZ){
		return CCMath.saturate(( 
	    	(theX - _myStart.x) * ( _myEnd.x - _myStart.x) +
	        (theY - _myStart.y) * ( _myEnd.y - _myStart.y)  +
	        (theZ - _myStart.z) * ( _myEnd.z - _myStart.z)
	    ) / _myStart.distanceSquared(_myEnd));
	}
	
	/**
	 * Returns the point on the line that is closest to the given point
	 * @param theX x coord of the point
	 * @param theY y coord of the point
	 * @param theZ z coord of the point
	 * @return the closest point
	 */
	public CCVector3 closestPoint(final double theX, final double theY, final double theZ){ 
	 
	    double myBlend = closestPointBlend(theX, theY, theZ);
	    
	    return CCVector3.lerp(_myStart, _myEnd, myBlend);
	 
//	    if( _myU < 0.0f) {
//	    	return _myStart.clone();
//	    }
//	    
//	    if(_myU > 1.0f ) {
//	    	return _myEnd.clone();
//	    }
//	 
//	    return new CCVector3(
//	    	_myStart.x + _myU * (_myEnd.x - _myStart.x),
//	    	_myStart.y + _myU * (_myEnd.y - _myStart.y),
//	    	_myStart.z + _myU * (_myEnd.z - _myStart.z)
//	    );
	}
	
	/**
	 * Returns the point on the line that is closest to the given point
	 * @param thePoint the point to use for searching
	 * @return the closest point on the line to the given point
	 */
	public CCVector3 closestPoint(CCVector3 thePoint){
		return closestPoint(thePoint.x, thePoint.y, thePoint.z);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public double distance(double theX, double theY, double theZ){
		CCVector3 myClosestPoint = closestPoint(theX, theY, theZ);
		return myClosestPoint.distance(theX, theY, theZ);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param thePoint
	 * @param theEndPoint1
	 * @param theEndPoint2
	 * @return
	 */
	public double distance(final CCVector3 theVector){ 
	    return distance(theVector.x, theVector.y, theVector.z);
	}

	

	public CCLine3 closestLineBetween(final CCLine3 theOtherLine) {

		CCVector3 p13 = _myStart.subtract(theOtherLine._myStart);
		CCVector3 p43 = theOtherLine._myEnd.subtract(theOtherLine._myStart);
		
		if (
			Math.abs(p43.x) <= Float.MIN_NORMAL && 
			Math.abs(p43.y) <= Float.MIN_NORMAL && 
			Math.abs(p43.z) <= Float.MIN_NORMAL
		) {
			return null;
		}

		CCVector3 p21 = _myEnd.subtract(_myStart);
		
		if (
			Math.abs(p21.x) <= Float.MIN_NORMAL && 
			Math.abs(p21.y) <= Float.MIN_NORMAL && 
			Math.abs(p21.z) <= Float.MIN_NORMAL
		) {
			return null;
		}
		
		double d4321 = p43.dot(p21);
		double d4343 = p43.dot(p43);
		double d2121 = p21.dot(p21);

		double denom = d2121 * d4343 - d4321 * d4321;
		if (Math.abs(denom) < Float.MIN_NORMAL) {
			return(null);
		}
		
		double d1343 = p13.dot(p43);
		double d1321 = p13.dot(p21);
		
		double numer = d1343 * d4321 - d1321 * d4343;

		double mua = numer / denom;
		double mub = (d1343 + d4321 * (mua)) / d4343;

		return new CCLine3(
			_myStart.x + mua * p21.x,
			_myStart.y + mua * p21.y,
			_myStart.z + mua * p21.z,
			theOtherLine._myStart.x + mub * p43.x,
			theOtherLine._myStart.y + mub * p43.y,
			theOtherLine._myStart.z + mub * p43.z
		);
	}

	/**
 	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCLine3f _myStart:[ "+_myStart+" ] end:[ " + _myEnd + " ]";
	}
}
