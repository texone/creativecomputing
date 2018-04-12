/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control.timeline.point;

import java.util.SortedSet;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCCubicSolver;


/**
 * @author christianriekoff
 * 
 */
public class CCBezierControlPoint extends CCControlPoint {

	private CCHandleControlPoint _myInHandle;
	private CCHandleControlPoint _myOutHandle;

	public CCBezierControlPoint() {
		super(CCControlPointType.BEZIER);
	}

	public CCBezierControlPoint(double theTime, double theValue) {
		super(theTime, theValue, CCControlPointType.BEZIER);
		_myInHandle = new CCHandleControlPoint(this,CCHandleType.BEZIER_IN_HANDLE, theTime, theValue);
		_myOutHandle = new CCHandleControlPoint(this,CCHandleType.BEZIER_OUT_HANDLE, theTime, theValue);
	}
	
	public CCBezierControlPoint(CCControlPoint theControlPoint) {
		this(theControlPoint.time(), theControlPoint.value());
	}

	@Override
	public boolean hasHandles() {
		return true;
	}

	public CCHandleControlPoint inHandle() {
		return _myInHandle;
	}

	public void inHandle(CCHandleControlPoint theHandle) {
		_myInHandle = theHandle;
	}

	public CCHandleControlPoint outHandle() {
		return _myOutHandle;
	}

	public void outHandle(CCHandleControlPoint theOutHandle) {
		_myOutHandle = theOutHandle;
	}
	
	/**
	 * Returns the bezier blend between 0 and 1 that would result in the given x
	 * @param theTime0 time of the first key
	 * @param theTime1 time of the first control point
	 * @param theTime2 time of the second control point
	 * @param theTime3 time of the second key
	 * @param theTime 
	 * @return bezier blend for the given time
	 */
	private double bezierBlend(double theTime0, double theTime1, double theTime2, double theTime3, double theTime) {
		double a = -theTime0 + 3 * theTime1 - 3 * theTime2 + theTime3;
		double b = 3 * theTime0 - 6 * theTime1 + 3 * theTime2;
		double c = -3 * theTime0 + 3 * theTime1;
		double d = theTime0 - theTime;

		double[] myResult = CCCubicSolver.solveCubic(a, b, c, d);
		int i = 0;
		while(i < myResult.length - 1 && (myResult[i] < 0 || myResult[i] > 1)) {
			i++;
		}
		return myResult[i];
	}

	public double sampleBezierSegment(CCControlPoint p0, CCControlPoint p1, CCControlPoint p2, CCControlPoint p3, double theTime) {
		double myBezierBlend = bezierBlend(p0.time(), p1.time(), p2.time(), p3.time(), theTime);
		return CCMath.bezierPoint(p0.value(), p1.value(), p2.value(), p3.value(), myBezierBlend);
	}
	
	@Override
	public void fix() {
		if(_myPrevious != null){
			_myInHandle.time(CCMath.max(_myInHandle.time(), _myPrevious.time()));
		}
		if(_myNext != null){
			_myOutHandle.time(CCMath.min(_myOutHandle.time(), _myNext.time()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.model.ControlPoint#interpolateValue(double, de.artcom.timeline.model.TrackData)
	 */
	@Override
	public double interpolateValue(double theTime, CCTrackData theData) {
		try{
			CCControlPoint mySample = new CCControlPoint(theTime, 0);
			SortedSet<CCControlPoint> myHeadSet = theData.headSet(mySample, false);
	
			CCControlPoint p1 = null;
			CCControlPoint p2 = null;
	
			if (myHeadSet.size() != 0) {
				p1 = theData.getLastOnSamePosition(myHeadSet.last());
			}
			
			if(p1 instanceof CCBezierControlPoint) {
				p2 = ((CCBezierControlPoint)p1)._myOutHandle;
			}else {
				p2 = p1;
			}
	
			return sampleBezierSegment(p1, p2, _myInHandle, this, theTime);
		}catch(Exception e){
			return 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#clone()
	 */
	@Override
	public CCBezierControlPoint clone() {
		CCBezierControlPoint myResult = new CCBezierControlPoint(_myTime, _myValue);
		myResult.inHandle(_myInHandle.clone());
		myResult.inHandle().parent(myResult);
		
		myResult.outHandle(_myOutHandle.clone());
		myResult.outHandle().parent(myResult);
		
		return myResult;
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.points.ControlPoint#setTime(double)
	 */
	@Override
	public void time(double theTime) {
		double myDifference = theTime - _myTime;
		super.time(theTime);
		
		_myInHandle.time(_myInHandle.time() + myDifference);
		_myOutHandle.time(_myOutHandle.time() + myDifference);
	}
	
	@Override
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myResult = super.data(theStartTime, theEndTime);
		myResult.put("in", _myInHandle.data(theStartTime, theEndTime));
		myResult.put("out", _myOutHandle.data(theStartTime, theEndTime));
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		super.data(theData);
		CCDataObject myInHandleData = theData.getObject("in");
		_myInHandle = new CCHandleControlPoint(
			this, 
			CCHandleType.BEZIER_IN_HANDLE, 
			myInHandleData.getDouble(TIME_ATTRIBUTE), 
			myInHandleData.getDouble(VALUE_ATTRIBUTE)
		);
		
		CCDataObject myOutHandleData = theData.getObject("out");
		_myOutHandle = new CCHandleControlPoint(
			this, 
			CCHandleType.BEZIER_OUT_HANDLE, 
			myOutHandleData.getDouble(TIME_ATTRIBUTE), 
			myOutHandleData.getDouble(VALUE_ATTRIBUTE)
		);
	}
}
