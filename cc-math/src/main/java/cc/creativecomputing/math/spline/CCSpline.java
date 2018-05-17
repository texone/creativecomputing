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
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * <p>
 * In computer graphics splines are popular curves because of the simplicity of their construction, their ease and accuracy of evaluation, and their capacity to approximate complex
 * shapes through curve fitting and interactive curve design.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Spline_(mathematics)">spline at wikipedia</a>
 */
public abstract class CCSpline extends ArrayList<CCVector3>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3640611551655330160L;
	
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
	 * if you directly add points.
	 */
	public void beginEditSpline () {
		if (_myIsModified) return;

		_myIsModified = true;

		if (size() > 2 && _myIsClosed) {
			remove(size() - 1);
		}

	}

	public void endEditSpline () {
		if (!_myIsModified){
			return;
		}

		_myIsModified = false;

		if (size() >= 2 && _myIsClosed) {
			add(get(0));
		}

		if (size() > 1) {
			computeTotalLentgh();
		}
	}

	/**
	 * remove the controlPoint from the spline
	 * @param controlPoint the controlPoint to remove
	 */
	public void removePoint (CCVector3 controlPoint) {
		beginEditSpline();
		remove(controlPoint);
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
		add(theControlPoint);
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
	
	/**
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
	 * @return the position
	 */
	public CCVector3 interpolate (double theBlend){
		double myLength = _myTotalLength * CCMath.saturate(theBlend);
		double myReachedLength = 0;
		int myIndex = 0;
		
		if(size() == 0)return null;
		if(_mySegmentsLength == null || _mySegmentsLength.size() == 0){
			return get(0).clone();
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
		for(CCVector3 myControlPoint:this){
			double myDistSq = thePoint.distanceSquared(myPoint);
			if(myDistSq < myMinDistanceSq){
				myMinDistanceSq = myDistSq;
				myPoint = myControlPoint;
			}
		}
		return myPoint;
	}
	
	/**
	 * Removes all points from the spline
	 */
	public void clear(){
		super.clear();
		if(_mySegmentsLength != null)_mySegmentsLength.clear();
		_myTotalLength = 0;
	}

}
