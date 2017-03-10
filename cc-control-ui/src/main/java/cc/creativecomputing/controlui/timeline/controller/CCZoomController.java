package cc.creativecomputing.controlui.timeline.controller;

import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.TimeRange;
import cc.creativecomputing.controlui.timeline.view.SwingConstants;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;


public class CCZoomController  {
	
	private double _myLowerBound;
	private double _myUpperBound;
	private Point2D _myDragStart;
	private double _myLastV;
	private double _myLastH;
	
	private CCListenerManager<CCZoomable> _myZoomables = CCListenerManager.create(CCZoomable.class);
	
	private double _myMinRange = 0;
	private double _myMaxRange = Double.MAX_VALUE;
	
	public CCZoomController() {
		_myLowerBound = 0;
		_myUpperBound = SwingConstants.DEFAULT_RANGE;
	}
	
	public void addZoomable(CCZoomable theZoomable) {
		_myZoomables.add(theZoomable);
		theZoomable.setRange(_myLowerBound, _myUpperBound);
	}
	
	public void removeZoomable(CCZoomable theZoomable) {
		_myZoomables.remove(theZoomable);
	}
	
	public void startDrag( Point2D theViewCoords ) {
		_myDragStart = theViewCoords;
	}
	
	public void endDrag() {
		_myDragStart = null;
		_myLastV = 0;
		_myLastH = 0;
	}
	
	public void performDrag( Point2D theViewCoords, int theViewWidth ) {
		if (_myDragStart != null) {
			double myVMovement = theViewCoords.getY() - _myDragStart.getY();
			double myHMovement = theViewCoords.getX() - _myDragStart.getX();
			
			if(CCMath.abs(myVMovement) > CCMath.abs(myHMovement)){
				myHMovement = 0;
			}else{
				myVMovement = 0;
			}
			
			double myVDelta = myVMovement - _myLastV;
			double myHDelta = myHMovement - _myLastH;
			
			myVDelta *= 0.01 * (_myUpperBound - _myLowerBound);
			myHDelta = myHDelta / theViewWidth * (_myUpperBound - _myLowerBound);
			
			// zooming should occur around the point where you grab the time line
			double myFixPoint = theViewCoords.getX() / theViewWidth;
			
			_myLowerBound -= myVDelta * myFixPoint;
			if (_myLowerBound < 0) {
				_myLowerBound = 0;
			}
			_myUpperBound += myVDelta * (1-myFixPoint);
			_myLowerBound -= myHDelta;
			if (_myLowerBound < 0) {
				_myLowerBound = 0;
			} else {
				_myUpperBound -= myHDelta;
			}
			
			if(_myLowerBound >= _myUpperBound) {
				_myLowerBound = _myUpperBound - 1;
			}
			
			double myRange = _myUpperBound - _myLowerBound;
			if(myRange < _myMinRange) {
				double myRangeDif = _myMinRange - myRange;
				_myLowerBound -= myRangeDif/2;
				if(_myLowerBound < 0)_myLowerBound = 0;
				_myUpperBound = _myLowerBound + _myMinRange;
			} 
			
			if(myRange > _myMaxRange) {
				double myRangeDif = myRange - _myMaxRange;
				_myLowerBound += myRangeDif / 2;
				_myUpperBound = _myLowerBound + _myMaxRange;
			} 
			
			_myLastV = myVMovement;
			_myLastH = myHMovement;
			
			updateZoomables();
		}
	}
	
	public void reset() {
		setRange(new TimeRange(0, SwingConstants.DEFAULT_RANGE));
	}
	
	public void minRange(double theMinRange) {
		_myMinRange = theMinRange;
	}
	
	public void maxRange(double theMaxRange) {
		_myMaxRange = theMaxRange;
	}
	
	public void setRange(TimeRange theRange) {
		if(theRange.end() <= theRange.start())return;
		
		_myLowerBound = theRange.start();
		_myUpperBound = theRange.end();
		updateZoomables();
	}
	
	public void setRange(double theStart, double theEnd) {
		if(theEnd <= theStart)return;
		
		_myLowerBound = theStart;
		_myUpperBound = theEnd;
		updateZoomables();
	}
	
	public void updateZoomables() {
		if (_myLowerBound < _myUpperBound) {
			_myZoomables.proxy().setRange(_myLowerBound, _myUpperBound);
		}		
	}
	
	public void setLowerBound(double theLowerBound) {
		_myLowerBound = theLowerBound;
		updateZoomables();
	}
	
	public void setUpperBound(double theUpperBound) {
		_myUpperBound = theUpperBound;
		updateZoomables();
	}
	
	public double lowerBound() {
		return _myLowerBound;
	}
	
	public double upperBound() {
		return _myUpperBound;
	}
}
