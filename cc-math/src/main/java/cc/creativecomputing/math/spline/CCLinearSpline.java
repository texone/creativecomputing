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

import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCVector3;

public class CCLinearSpline extends CCSpline{
	
	private List<CCLine3> _myLines = new ArrayList<CCLine3>();
	
	public CCLinearSpline(boolean theIsClosed) {
		super(CCSplineType.LINEAR, theIsClosed);
	}

	public CCLinearSpline(CCVector3[] theControlPoints, boolean theIsClosed) {
		super(CCSplineType.LINEAR, theControlPoints, theIsClosed);
	}

	public CCLinearSpline(List<CCVector3> theControlPoints, boolean theIsClosed) {
		super(CCSplineType.LINEAR, theControlPoints, theIsClosed);
	}

	@Override
	public void computeTotalLengthImpl() {
		_myLines.clear();
		if (_myPoints.size() > 1) {
			for (int i = 0; i < _myPoints.size() - 1; i++) {
				CCLine3 myLine = new CCLine3(_myPoints.get(i), _myPoints.get(i + 1));
				double myLength = myLine.length();
				_mySegmentsLength.add(myLength);
				_myTotalLength += myLength;
				_myLines.add(myLine);
			}
		}
	}

	@Override
	public CCVector3 interpolate(double value, int currentControlPoint) {
		endEditSpline();
		return CCVector3.lerp( 
			_myPoints.get(currentControlPoint), 
			_myPoints.get(currentControlPoint + 1),
			value
		);
	}
	
	
	
//	public Tuple<Integer, Float>closestInterpolation(CCVector3 thePoint,  int theStart, int theEnd){
//		if(theEnd < theStart)theEnd += _myLines.size();
//		
//		int myIndex = theStart;
//		double myBlend = 0;
//		
//		double myMinDistSq = Float.MAX_VALUE;
//		
//		for(int i = theStart; i < theEnd;i++){
//			CCLine3 myLine = _myLines.get(i % _myLines.size());
//			CCVector3 myPoint = myLine.closestPoint(thePoint);
//			double myDistSq = myPoint.distanceSquared(thePoint);
//			if(myDistSq < myMinDistSq){
//				myIndex = i % _myLines.size();
//				myBlend = myLine.closestPointBlend(thePoint);
//				myMinDistSq = myDistSq;
//			}
//		}
//		
//		return new CCTuple<Integer, Float>(myIndex, myBlend);
//	}
	
	@Override
	public CCVector3 closestPoint (CCVector3 thePoint) {
		return closestPoint(thePoint, 0, _myLines.size());
	}
	
	public CCVector3 closestPoint (CCVector3 thePoint, int theStart, int theEnd) {
		if(theEnd < theStart)theEnd += _myLines.size();
		CCVector3 myClosestPoint = null;
		double myMinDistSq = Float.MAX_VALUE;
		
		for(int i = theStart; i < theEnd;i++){
			CCLine3 myLine = _myLines.get(i % _myLines.size());
			CCVector3 myPoint = myLine.closestPoint(thePoint);
			double myDistSq = myPoint.distanceSquared(thePoint);
			if(myDistSq < myMinDistSq){
				myClosestPoint = myPoint;
				myMinDistSq = myDistSq;
			}
		}
		
		return myClosestPoint;
	}
	@Override
	public void clear () {
		super.clear();
		_myLines.clear();
	}
	
	
}
