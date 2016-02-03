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

public class CCLine2 {

	protected CCVector2 _myStart;
	protected CCVector2 _myEnd;
	

	public CCLine2(final CCVector2 theStart, final CCVector2 theEnd) {
		_myStart = theStart;
		_myEnd = theEnd;
	}

	public CCLine2(
		final double theStartX, final double theStartY,
		final double theEndX, final double theEndY
	) {
		this(new CCVector2(theStartX, theStartY), new CCVector2(theEndX, theEndY));
	}

	public CCLine2(final CCLine2 theSegment) {
		this(theSegment._myStart, theSegment._myEnd);
	}

	public CCLine2() {
		this(0, 0, 0, 0);
	}

	/**
	 * @return the start
	 */
	public CCVector2 start() {
		return _myStart;
	}

	/**
	 * @return the end
	 */
	public CCVector2 end() {
		return _myEnd;
	}

	public double length() {
		return _myStart.distance(_myEnd);
	}
	
	@Override
	public boolean equals(final Object theSegment) {
		if(!(theSegment instanceof CCLine2))return false;
		
		CCLine2 mySegment = (CCLine2)theSegment;
		return 
			mySegment.start().equals(start()) && mySegment.end().equals(end()) ||
			mySegment.start().equals(end()) && mySegment.end().equals(start());
	}
	
	public double closestPointBlend(CCVector2 thePoint){
		return closestPointBlend(thePoint.x, thePoint.y);
	}
	
	public double closestPointBlend(final double theX, final double theY){
		return CCMath.saturate(( 
	    	(theX - _myStart.x) * ( _myEnd.x - _myStart.x) +
	        (theY - _myStart.y) * ( _myEnd.y - _myStart.y)
	    ) / _myStart.distanceSquared(_myEnd));
	}
	
	/**
	 * Returns the point on the line that is closest to the given point
	 * @param theX x coord of the point
	 * @param theY y coord of the point
	 * @param theZ z coord of the point
	 * @return the closest point
	 */
	public CCVector2 closestPoint(final double theX, final double theY){ 
	 
	    double myBlend = closestPointBlend(theX, theY);
	    
	    return CCVector2.lerp(_myStart, _myEnd, myBlend);
	 
//	    if( _myU < 0.0f) {
//	    	return _myStart.clone();
//	    }
//	    
//	    if(_myU > 1.0f ) {
//	    	return _myEnd.clone();
//	    }
//	 
//	    return new CCVector2(
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
	public CCVector2 closestPoint(CCVector2 thePoint){
		return closestPoint(thePoint.x, thePoint.y);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param theX
	 * @param theY
	 * @return
	 */
	public double distance(double theX, double theY){
		CCVector2 myClosestPoint = closestPoint(theX, theY);
		return myClosestPoint.distance(theX, theY);
	}
	
	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param thePoint
	 * @param theEndPoint1
	 * @param theEndPoint2
	 * @return
	 */
	public double distance(final CCVector2 theVector){ 
	    return distance(theVector.x, theVector.y);
	}

	

	public CCLine2 closestLineBetween(final CCLine2 theOtherLine) {

		CCVector2 p13 = _myStart.subtract(theOtherLine._myStart);
		CCVector2 p43 = theOtherLine._myEnd.subtract(theOtherLine._myStart);
		
		if (
			Math.abs(p43.x) <= Float.MIN_NORMAL && 
			Math.abs(p43.y) <= Float.MIN_NORMAL
		) {
			return null;
		}

		CCVector2 p21 = _myEnd.subtract(_myStart);
		
		if (
			Math.abs(p21.x) <= Float.MIN_NORMAL && 
			Math.abs(p21.y) <= Float.MIN_NORMAL
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

		return new CCLine2(
			_myStart.x + mua * p21.x,
			_myStart.y + mua * p21.y,
			theOtherLine._myStart.x + mub * p43.x,
			theOtherLine._myStart.y + mub * p43.y
		);
	}

	/**
 	 * Returns a string representation of the vector
	 */
	public String toString(){
		return "CCLine3f _myStart:[ "+_myStart+" ] end:[ " + _myEnd + " ]";
	}
}
