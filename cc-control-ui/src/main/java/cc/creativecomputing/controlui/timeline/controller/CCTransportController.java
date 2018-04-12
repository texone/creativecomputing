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


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.CCTrackType;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCMarkerPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackDataController;
import cc.creativecomputing.controlui.timeline.view.transport.CCRulerView;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


/**
 * @author christianriekoff
 *
 */
public class CCTransportController extends CCTrackDataController{
	
	public static class CCRulerInterval{
		private double _myMin;
		private double _myMax;
		
		private double _myInterval;
		
		public CCRulerInterval(double theMax, double theMin, double theInterval) {
			_myMin = theMin;
			_myMax = theMax;
			_myInterval = theInterval;
		}
		
		public boolean isInInterval(double theValue) {
			return theValue < _myMax && theValue >= _myMin;
		}
		
		public double quantize(double theValue) {
			return quantize(theValue, 1);
		}
		
		public double quantize(double theValue, int theRaster) {
			if(theRaster == 0)return theValue;
			double myFactor = _myInterval / theRaster;
			return myFactor * Math.round(theValue / myFactor);
		}
		
		public double interval() {
			return _myInterval;
		}
	}
	
	private List<CCRulerInterval> _myIntervals = new ArrayList<CCTransportController.CCRulerInterval>();
	
	private CCTrackData _myMarkerList;
	
	public CCRulerView _myRulerView;
	
	public CCTimeRangeController _myTimeRangeController;

	private boolean _myDefineLoop = false;
	private int _myStartClickX = 0;
	
	private double _myLowerBound = 0;
	private double _myUpperBound = 0;
	
	private double _myCurrentTime;
	private double _mySpeedFactor;
	
	public final CCEventManager<Double> timeEvents = new CCEventManager<>();
	public final CCEventManager<Double> playEvents = new CCEventManager<>();
	public final CCEventManager<Double> stopEvents = new CCEventManager<>();
	
	public final CCEventManager<CCMarkerPoint> markerEvents = new CCEventManager<>();
	
	private boolean _myIsPlaying;
	
	private boolean _myIsInLoop = false;
	
	private CCTimeRange _myLoop = new CCTimeRange();
	
	private CCTimelineController _myTimelineController;
	
	private CCRulerInterval _myInterval;
	
	private boolean _myUseBeats = false;
	
	private double _myBPM = 120;
	
	public CCTransportController(CCTimelineController theTimelineController) {
		super(theTimelineController, null);
		_myTimelineController = theTimelineController;
		_myMarkerList = new CCTrackData();
		
		_myIsPlaying = false;
		_myCurrentTime = 0;
		_mySpeedFactor = 1;
		
		_myInterval = new CCRulerInterval(Float.MAX_VALUE, 0f, 1f);
		createIntervals();
	}
	
	public void useBeats(boolean theUseBeats){
		_myUseBeats = theUseBeats;
		createIntervals();
		_myInterval = currentInterval();
	}
	
	private CCRulerInterval currentInterval() {
		if(_myRulerView == null)return _myInterval;
		int myDevides = (int)(_myRulerView.width() / MIN_SPACE);
		double myInterval = (_myUpperBound - _myLowerBound) / myDevides;
		
		for(CCRulerInterval myRulerInterval:_myIntervals) {
			if(myRulerInterval.isInInterval(myInterval)) {
				return myRulerInterval;
			}
		}
		return new CCRulerInterval(Float.MAX_VALUE, 0f, 1f);
	}
	
	private void createIntervals() {
		_myIntervals.clear();
		if(_myUseBeats){
			double myBarTime = (60 / _myBPM) * 4;
			
			for(int i = 1; i < 16;i++){
				double myUpperInterval = myBarTime * CCMath.pow(2, i);
				double mylowerInterval = myBarTime * CCMath.pow(2, i - 1);
				_myIntervals.add(new CCRulerInterval(myUpperInterval, mylowerInterval, myUpperInterval));
			}
			for(int i = 1; i < 16;i++){
				double myUpperInterval = myBarTime * CCMath.pow(0.5, i - 1);
				double mylowerInterval = myBarTime * CCMath.pow(0.5, i);
				_myIntervals.add(new CCRulerInterval(myUpperInterval, mylowerInterval, myUpperInterval));
			}
		}else{
			_myIntervals.add(new CCRulerInterval(18000, 9000, 18000));
			_myIntervals.add(new CCRulerInterval(9000, 3600, 7200));
			_myIntervals.add(new CCRulerInterval(3600, 1800, 3600));
			
			_myIntervals.add(new CCRulerInterval(1800, 1500, 1800));
			_myIntervals.add(new CCRulerInterval(1500, 600, 1200));
			_myIntervals.add(new CCRulerInterval(600, 300, 600));
			
			_myIntervals.add(new CCRulerInterval(300, 150, 300));
			_myIntervals.add(new CCRulerInterval(150, 60, 120));
			_myIntervals.add(new CCRulerInterval(60, 30, 60));
			
			_myIntervals.add(new CCRulerInterval(30, 20, 30));
			_myIntervals.add(new CCRulerInterval(20, 10, 20));
			
			_myIntervals.add(new CCRulerInterval(10, 5, 10));
			_myIntervals.add(new CCRulerInterval(5, 2, 5));
			_myIntervals.add(new CCRulerInterval(2, 1, 2));
			
			_myIntervals.add(new CCRulerInterval(1.0000, 0.5000f, 1));
			_myIntervals.add(new CCRulerInterval(0.5000, 0.2500f, 0.5f));
			_myIntervals.add(new CCRulerInterval(0.2500, 0.1000f, 0.2f));
	
			_myIntervals.add(new CCRulerInterval(0.1000, 0.0500f, 0.1f));
			_myIntervals.add(new CCRulerInterval(0.0500, 0.0250f, 0.05f));
			_myIntervals.add(new CCRulerInterval(0.0250, 0.0100f, 0.02f));
	
			_myIntervals.add(new CCRulerInterval(0.0100, 0.0050f, 0.01f));
			_myIntervals.add(new CCRulerInterval(0.0050, 0.0025f, 0.005f));
			_myIntervals.add(new CCRulerInterval(0.0025, 0.0010f, 0.002f));
			
			_myIntervals.add(new CCRulerInterval(0.0010, 0.0000f, 0.001f));
		}
	}
	
	public CCRulerInterval rulerInterval() {
		return _myInterval;
	}
	
	public String timeToString(double theTime) {
		if(_myUseBeats){
			double barsDeci = theTime / ((60 / _myBPM) * 4);
			int bar = (int)barsDeci;
			double beatsDeci = (barsDeci - bar) * 4;
			int beat = (int)beatsDeci;
			double divDeci = (beatsDeci - beat) * 4;
			int div = (int)divDeci;
			StringBuffer myResult = new StringBuffer();
			myResult.append(bar);
			if(beat != 0){
				myResult.append("." + (beat + 1));
			}
			if(div != 0){
				myResult.append("." + (beat + 1));
				myResult.append("." + (div + 1));
			}
			return myResult.toString();
		}else{
			long myTime = (long)(theTime * 1000);
			long myMillis = myTime % 1000;
			myTime /= 1000;
			long mySeconds = myTime % 60;
			myTime /= 60;
			long myMinutes = myTime % 60;
			myTime /= 60;
			long myHours = myTime;
			
			StringBuffer myResult = new StringBuffer();
			if(myHours != 0) {
				myResult.append(myHours);
				myResult.append("h ");
			}
			if(myMinutes != 0) {
				myResult.append(myMinutes);
				myResult.append("min ");
			}
			if(mySeconds != 0) {
				myResult.append(mySeconds);
				myResult.append("s ");
			}
			if(myMillis != 0) {
				myResult.append(myMillis);
				myResult.append("ms ");
			}
			return myResult.toString();
		}
	}
	
	public void addMarkerFromMouse(final String theName, final int theMouseX){
		if(_myRulerView == null)return;
		double myClickedTime = viewXToTime(theMouseX, true);
		addMarker(theName, myClickedTime);
	}
	
	/**
	 * Adds a marker with the given name at the given time
	 * @param theName name of the marker
	 * @param theTime time of the marker
	 */
	public void addMarker(final String theName, final double theTime) {
		_myMarkerList.add(new CCMarkerPoint(theTime, theName));
		if(_myRulerView != null)_myRulerView.render();
	}
	
	public CCTrackData marker() {
		return _myMarkerList;
	}
	
	public CCTimeRange loopRange() {
		return _myLoop;
	}
	
	public void rulerView(CCRulerView theRulerView) {
		view(theRulerView);
		_myRulerView = theRulerView;
		_myTimeRangeController = new CCTimeRangeController(_myTimelineController, loopRange(), this);
		_myInterval = currentInterval();
	}
	
	private CCMarkerPoint _myLastMarker = null;
	
	public double time() {
		return _myCurrentTime;
	}
	
	public void speed(double theSpeed){
		_mySpeedFactor = theSpeed;
	}
	
	public double speed() {
		return _mySpeedFactor;
	}
	
	public double bpm(){
		return _myBPM;
	}
	
	public void bpm(double theBPM){
		_myBPM = theBPM;
		if(_myUseBeats){
			createIntervals();
			_myInterval = currentInterval();
		}
	}
	
	public double loopStart() {
		return _myLoop.start;
	}
	
	public double loopEnd() {
		return _myLoop.end;
	}
	
	public void loop(final double theLoopStart, final double theLoopEnd) {
		_myLoop.range(theLoopStart, theLoopEnd);
	}
	
	public void doLoop(final boolean theIsInLoop) {
		_myIsInLoop = theIsInLoop;
	}
	
	public boolean doLoop() {
		return _myIsInLoop;
	}
	
	private int MIN_SPACE = 150;
	
	public void setRange(CCTimeRange theRange) {
		_myLowerBound = theRange.start;
		_myUpperBound = theRange.end;
		_myInterval = currentInterval();
		if(_myRulerView != null)_myRulerView.render();
	}

	public double lowerBound() {
		return _myLowerBound;
	}

	public double upperBound() {
		return _myUpperBound;
	}
	
	private void moveTransport(final double theMouseX, boolean theQuantize) {
		if(_myRulerView == null)return;
		double myClickedTime = viewXToTime(theMouseX, true);
		if(theQuantize)myClickedTime = _myTimelineController.quantize(myClickedTime);
		time(myClickedTime);
	}
	
	@Override
	public CCTrackData trackData() {
		return _myMarkerList;
	}
	
	public void trackData(CCTrackData theTrackData) {
		_myMarkerList = theTrackData;
	}
	
	public CCTrackType trackType() {
		return CCTrackType.MARKER;
	}
    
//    public double viewXToTime(int theViewX) {
//        return _myRulerView.viewXToTime(theViewX);
//    }
//
//    public int timeToViewX(double theTime) {
//        return _myRulerView.timeToViewX(theTime);
//    }
	
	public CCVector2 curveToViewSpace(CCControlPoint thePoint) {
        return new CCVector2(timeToViewX(thePoint.time()),0);
    }
    
	@Override
    public CCControlPoint viewToCurveSpace(CCVector2 thePoint, boolean theGetPos) {
        CCControlPoint myResult = new CCControlPoint();
        myResult.time(viewXToTime((int) thePoint.x, theGetPos));
        myResult.value(0);
        
        return myResult;
    }
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
//	@Override
//	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
//		MarkerPoint myMarkerPoint = new MarkerPoint(theCurveCoords.time(), "");
//		
//		if(_myRulerView != null)_myRulerView.showMarkerDialog(myMarkerPoint);
//		
//		return myMarkerPoint;
//	}
	
	/* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.CCVector2, boolean)
     */
    public void dragPointImp(CCControlPoint theDraggedPoint, CCControlPoint myTargetPosition, CCControlPoint theMovement, boolean theIsPressedShift) {
    	CCControlPoint myPoint = _myTrackContext.quantize(myTargetPosition);
    	trackData().move(theDraggedPoint, myPoint);
    }
	
	private double _myLoopStart = 0;
	private double _myLoopEnd = 0;
	
	public void mousePressed(CCGLMouseEvent e) {
		if(_myRulerView == null)return;
		
		_myDefineLoop = e.y < _myRulerView.height() / 2;
		
		if(!_myDefineLoop) {
			moveTransport(e.x, e.isShiftDown());
		}else {
			_myTimeRangeController.mousePressed(e);
			
			_myLoopStart = _myTimeRangeController._myLoopStart;
			_myLoopEnd = _myTimeRangeController._myLoopEnd;
		}
		_myRulerView.render();
		_myTimelineController.renderInfo();
	}
	
	public void mouseMoved(CCVector2  e) {
		_myDefineLoop = e.y < _myRulerView.height() / 2;
		try {
		if(_myDefineLoop) {
			switch(_myTimeRangeController.action(e)) {
			case MOVE_BOTH:
//				_myRulerView.setCursor(new Cursor(Cursor.MOVE_CURSOR));
				break;
			case MOVE_END:
			case MOVE_START:
//				_myRulerView.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
				break;
			}
		}
		}catch(Exception x) {
			x.printStackTrace();
		}
	}
	
	public void mouseDragged(CCVector2 e) {
		if(!_myDefineLoop) {
			moveTransport(e.x, false);
			_myTimelineController.renderInfo();
			
			_myTimelineController.renderInfo();
		}else {
			_myTimeRangeController.mouseDragged(e);
			doLoop(true);
		}
	}
	
	public void mouseReleased(CCGLMouseEvent e) {
		if(_myDefineLoop) {
			if(e.x == _myStartClickX ) {
				doLoop(false);
			}else {
				if(e.isShiftDown()) {
					_myTimelineController.scaleTracks(
						_myLoopStart, _myLoopEnd, 
						_myTimeRangeController._myLoopStart, _myTimeRangeController._myLoopEnd
					);
				}
			}
		}else {
			if(!e.isShiftDown()) {
//				moveTransport(e.x);
			}else {
//				super.mouseReleased(e, null);
			}
		}
	}
	
	public boolean isPlaying() {
		return _myIsPlaying;
	}
	
	public void play() {
		if (isPlaying()) {
			rewind();
		} else {
			_myIsPlaying = true;
		}
		playEvents.event(_myCurrentTime);
	}
	
	public void pause() {
		_myIsPlaying = false;
		stopEvents.event(_myCurrentTime);
	}
	
	public void stop() {
		if (!isPlaying()) {
			rewind();
		}
		_myIsPlaying = false;
		stopEvents.event(_myCurrentTime);
	}
	
	public void rewind() {
		_myCurrentTime = _myLoopStart;
		timeEvents.event(_myCurrentTime);
	}
	
	public void loop() {
		doLoop(!doLoop());
	}
	
	public void update(double theDeltaT) {
		if(_myRulerView != null)_myRulerView.render();
		if (!_myIsPlaying) return;
		
		_myCurrentTime += theDeltaT * _mySpeedFactor;
			
		if(_myIsInLoop && _myCurrentTime > _myLoop.end) {
			_myCurrentTime = _myLoop.start + _myCurrentTime - _myLoop.end;
		}
		timeEvents.event(_myCurrentTime);
			
		CCMarkerPoint myCurrentMarker = (CCMarkerPoint)_myMarkerList.getFirstPointAfter(_myCurrentTime);
		if(myCurrentMarker != _myLastMarker && _myLastMarker != null){
			markerEvents.event(_myLastMarker);
		}
		_myLastMarker = myCurrentMarker;
	}
	
	double _myLastTime = -1;
	
	public void time(double theTime) {
		theTime = CCMath.max(0,theTime);
		_myCurrentTime = theTime;
		timeEvents.event(_myCurrentTime);
//		if(theTime < _myLastTime || theTime - _myLastTime > 0.1){
//			_myLastTime = theTime;
//			_myLastMarker = null;
//			return;
//		}
		_myLastTime = theTime;
		
		// TODO make marker events work (this should be replaced by timed event tracks)
//		MarkerPoint myCurrentMarker = (MarkerPoint)_myMarkerList.getFirstPointAt(_myCurrentTime);
//		if(myCurrentMarker != _myLastMarker && _myLastMarker != null){
//			for(MarkerListener myListener:_myMarkerListener){
//				myListener.onMarker(_myLastMarker);
//			}
//		}
//		_myLastMarker = myCurrentMarker;
	}

	@Override
	public double timeToViewX(double theTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double viewXToTime(double theViewX) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double viewWidthToTime(double theViewWidth) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CCControlPoint quantize(CCControlPoint myTargetPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double viewTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
