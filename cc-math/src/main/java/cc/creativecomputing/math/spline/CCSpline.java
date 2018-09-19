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
package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

/**
 * <p>
 * In computer graphics splines are popular curves because of the simplicity of their construction, their ease and accuracy of evaluation, and their capacity to approximate complex
 * shapes through curve fitting and interactive curve design.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Spline_(mathematics)">spline at wikipedia</a>
 */
public abstract class CCSpline implements Iterable<CCVector3>, CCInterpolatable<CCVector3>{
	
	protected List<CCVector3> _myPoints = new ArrayList<>();

	protected boolean _myIsClosed;
	protected List<Double> _mySegmentsLength;

	public enum CCSplineType {
		LINEAR, CATMULL_ROM, BEZIER, NURB, BLEND
	}

	protected double	_myTotalLength;
	protected CCSplineType _myType;

	protected boolean _myIsModified	= true;
	
	protected int _myInterpolationIncrease = 1;

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (CCSplineType theSplineType, boolean theIsClosed) {
		_myIsClosed = theIsClosed;
		_myType = theSplineType;
	}

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theControlPoints an array of vector to use as control points of the spline
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (
		CCSplineType theSplineType, CCVector3[] theControlPoints,
		boolean theIsClosed) {
		this(theSplineType, theIsClosed);
		addControlPoints(theControlPoints);
	}

	/**
	 * Create a spline
	 * @param theSplineType the type of the spline @see {CCSplineType}
	 * @param theControlPoints a list of vector to use as control points of the spline
	 * @param theIsClosed true if the spline cycle.
	 */
	public CCSpline (
		CCSplineType theSplineType, List<CCVector3> theControlPoints,
		boolean theIsClosed) {
		this(theSplineType, theIsClosed);
		addControlPoints(theControlPoints);
	}

	/**
	 * Use this method to mark the spline as modified, this is only necessary
	 * if you directly add points using the reference passed by the {@linkplain #points()} method.
	 */
	public void beginEditSpline () {
		if (_myIsModified) return;

		_myIsModified = true;

		if (_myPoints.size() > 2 && _myIsClosed) {
			_myPoints.remove(_myPoints.size() - 1);
		}

	}

	public void endEditSpline () {
		if (!_myIsModified){
			return;
		}

		_myIsModified = false;

		if (_myPoints.size() >= 2 && _myIsClosed) {
			_myPoints.add(_myPoints.get(0));
		}

		if (_myPoints.size() > 1) {
			computeTotalLentgh();
		}
	}

	/**
	 * remove the controlPoint from the spline
	 * @param controlPoint the controlPoint to remove
	 */
	public void removePoint (CCVector3 controlPoint) {
		beginEditSpline();
		_myPoints.remove(controlPoint);
	}

	/**
	 * Adds a controlPoint to the spline.
	 * <p>
	 * If you add one control point to a bezier spline and the added point is 
	 * not the first point of the spline, there are two more
	 * points added as control points these points will be the previous point
	 * and the added point, resulting in a straight line.
	 * </p>
	 * @param theControlPoint a position in world space
	 */
	public void addPoint (CCVector3 theControlPoint) {
		beginEditSpline();
		_myPoints.add(theControlPoint);
	}

	/**
	 * Adds the given control points to the spline
	 * @param theControlPoints
	 */
	public void addControlPoints (CCVector3... theControlPoints) {
		for (CCVector3 myPoint : theControlPoints) {
			addPoint(myPoint);
		}
	}

	/**
	 * Adds the given control points to the spline
	 * @param theControlPoints
	 */
	public void addControlPoints (List<CCVector3> theControlPoints) {
		for (CCVector3 myPoint : theControlPoints) {
			addPoint(myPoint);
		}
	}
	
	/**
	 * returns this spline control points
	 * @return
	 */
	public List<CCVector3> points () {
		return _myPoints;
	}
	
	public void reverse() {
		Collections.reverse(_myPoints);
		if (_myPoints.size() > 1) {
			computeTotalLentgh();
		}
	}
	
	@Override
	public Iterator<CCVector3> iterator() {
		return _myPoints.iterator();
	}

	protected abstract void computeTotalLengthImpl ();

	/**
	 * This method computes the total length of the curve.
	 */
	protected void computeTotalLentgh () {
		_myTotalLength = 0;

		if (_mySegmentsLength == null) {
			_mySegmentsLength = new ArrayList<Double>();
		} else {
			_mySegmentsLength.clear();
		}
		computeTotalLengthImpl();
	}

	/**
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the current control point and the next one
	 * @param theControlPointIndex the current control point
	 * @return the position
	 */
	public abstract CCVector3 interpolate (double theBlend, int theControlPointIndex);
	
	@Override
	/**
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
	 * @return the position
	 */
	public CCVector3 interpolate (double theBlend){
		double myLength = _myTotalLength * CCMath.saturate(theBlend);
		double myReachedLength = 0;
		int myIndex = 0;
		
		if(_myPoints.size() == 0)return null;
		if(_mySegmentsLength == null || _mySegmentsLength.size() == 0){
			return _myPoints.get(0).clone();
		}
		
		while(myReachedLength + _mySegmentsLength.get(myIndex) < myLength){
			myReachedLength += _mySegmentsLength.get(myIndex);
			myIndex ++;
		}
		
		double myLocalLength = myLength - myReachedLength;
		double myLocalBlend = myLocalLength / _mySegmentsLength.get(myIndex);
		return interpolate(myLocalBlend, myIndex * _myInterpolationIncrease);
	}

	/**
	 * returns true if the spline cycle
	 * @return
	 */
	public boolean isClosed () {
		return _myIsClosed;
	}

	/**
	 * set to true to make the spline cycle
	 * @param theIsClosed
	 */
	public void isClosed (boolean theIsClosed) {
		if (theIsClosed == _myIsClosed) return;
		beginEditSpline();
		_myIsClosed = theIsClosed;
		endEditSpline();
	}

	/**
	 * return the total length of the spline
	 * @return
	 */
	public double totalLength () {
		return _myTotalLength;
	}

	/**
	 * return the type of the spline
	 * @return
	 */
	public CCSplineType type () {
		return _myType;
	}
	
	/**
	 * Returns the number of segments in this spline
	 * @return
	 */
	public int numberOfSegments(){
		return _mySegmentsLength.size();
	}

	/**
	 * returns a list of double representing the segments length
	 * @return
	 */
	public List<Double> segmentsLengths () {
		return _mySegmentsLength;
	}
	
	public CCVector3 closestPoint(CCVector3 thePoint){
		double myMinDistanceSq = Double.MAX_VALUE;
		CCVector3 myPoint = null;
		for(CCVector3 myControlPoint:_myPoints){
			double myDistSq = thePoint.distanceSquared(myPoint);
			if(myDistSq < myMinDistanceSq){
				myMinDistanceSq = myDistSq;
				myPoint = myControlPoint;
			}
		}
		return myPoint;
	}
	
	public void closestPointAndDirection(CCVector3 thePoint, CCVector3 theClosestPoint, CCVector3 theDirection) {
		double myMinDistanceSq = Double.MAX_VALUE;
		CCVector3 myPoint = null;
		for(CCVector3 myControlPoint:_myPoints){
			double myDistSq = thePoint.distanceSquared(myPoint);
			if(myDistSq < myMinDistanceSq){
				myMinDistanceSq = myDistSq;
				myPoint = myControlPoint;
			}
		}
		if(myPoint != null)theClosestPoint.set(myPoint);
	}
	
	/**
	 * Removes all points from the spline
	 */
	public void clear(){
		_myPoints.clear();
		if(_mySegmentsLength != null)_mySegmentsLength.clear();
		_myTotalLength = 0;
	}

}
