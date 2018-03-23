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
package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


public class CCZoomController  {
	
	private CCTimeRange _myRange;
	private CCVector2 _myDragStart;
	private double _myLastV;
	private double _myLastH;
	
	public final CCEventManager<CCTimeRange> events = new CCEventManager<>();
	
	private double _myMinRange = 0;
	private double _myMaxRange = Double.MAX_VALUE;
	
	public CCZoomController() {
		_myRange = new CCTimeRange(0,CCUIConstants.DEFAULT_RANGE);
	}
	
	public CCTimeRange range(){
		return _myRange;
	}
	
	public void startDrag( CCVector2 theViewCoords ) {
		_myDragStart = theViewCoords;
	}
	
	public void endDrag() {
		_myDragStart = null;
		_myLastV = 0;
		_myLastH = 0;
	}
	
	public void performDrag( CCVector2 theViewCoords, double theViewWidth ) {
		if (_myDragStart != null) {
			double myVMovement = theViewCoords.y - _myDragStart.y;
			double myHMovement = theViewCoords.x - _myDragStart.x;
			
			if(CCMath.abs(myVMovement) > CCMath.abs(myHMovement)){
				myHMovement = 0;
			}else{
				myVMovement = 0;
			}
			
			double myVDelta = myVMovement - _myLastV;
			double myHDelta = myHMovement - _myLastH;
			
			myVDelta *= 0.01 * _myRange.length();
			myHDelta = myHDelta / theViewWidth * _myRange.length();
			
			// zooming should occur around the point where you grab the time line
			double myFixPoint = theViewCoords.x / theViewWidth;
			
			_myRange.start -= myVDelta * myFixPoint;
			if (_myRange.start < 0) {
				_myRange.start = 0;
			}
			_myRange.end += myVDelta * (1-myFixPoint);
			_myRange.start -= myHDelta;
			if (_myRange.start < 0) {
				_myRange.start = 0;
			} else {
				_myRange.end -= myHDelta;
			}
			
			if(_myRange.start >= _myRange.end) {
				_myRange.start = _myRange.end - 1;
			}
			
			double myRange = _myRange.end - _myRange.start;
			if(myRange < _myMinRange) {
				double myRangeDif = _myMinRange - myRange;
				_myRange.start -= myRangeDif/2;
				if(_myRange.start < 0)_myRange.start = 0;
				_myRange.end = _myRange.start + _myMinRange;
			} 
			
			if(myRange > _myMaxRange) {
				double myRangeDif = myRange - _myMaxRange;
				_myRange.start += myRangeDif / 2;
				_myRange.end = _myRange.start + _myMaxRange;
			} 
			
			_myLastV = myVMovement;
			_myLastH = myHMovement;
			
			updateZoomables();
		}
	}
	
	public void reset() {
		setRange(new CCTimeRange(0, CCUIConstants.DEFAULT_RANGE));
	}
	
	public void minRange(double theMinRange) {
		_myMinRange = theMinRange;
	}
	
	public void maxRange(double theMaxRange) {
		_myMaxRange = theMaxRange;
	}
	
	public void setRange(CCTimeRange theRange) {
		if(theRange.end <= theRange.start)return;
		
		_myRange.start = theRange.start;
		_myRange.end = theRange.end;
		updateZoomables();
	}
	
	public void setRange(double theStart, double theEnd) {
		if(theEnd <= theStart)return;
		
		_myRange.start = theStart;
		_myRange.end = theEnd;
		updateZoomables();
	}
	
	public void updateZoomables() {
		if (_myRange.start < _myRange.end) {
			events.event(_myRange);
		}		
	}
	
	public void setLowerBound(double theLowerBound) {
		_myRange.start = theLowerBound;
		updateZoomables();
	}
	
	public void setUpperBound(double theUpperBound) {
		_myRange.end = theUpperBound;
		updateZoomables();
	}
	
	public double lowerBound() {
		return _myRange.start;
	}
	
	public double upperBound() {
		return _myRange.end;
	}
}
