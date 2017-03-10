/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.timeline.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.TimeRange;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.TrackType;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.MarkerPoint;
import cc.creativecomputing.controlui.timeline.controller.track.TrackDataController;
import cc.creativecomputing.controlui.timeline.view.SwingRulerView;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;


/**
 * @author christianriekoff
 *
 */
public class CCTransportController extends TrackDataController implements CCZoomable{
	
	public static class RulerInterval{
		private double _myMin;
		private double _myMax;
		
		private double _myInterval;
		
		public RulerInterval(double theMax, double theMin, double theInterval) {
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
	
	private List<RulerInterval> _myIntervals = new ArrayList<CCTransportController.RulerInterval>();
	
	public static enum TransportAction{
		PLAY, STOP, LOOP
	}
	
	private TrackData _myMarkerList;
	
	public SwingRulerView _myRulerView;
	
	public TimeRangeController _myTimeRangeController;

	private boolean _myDefineLoop = false;
	private int _myStartClickX = 0;
	
	private double _myLowerBound = 0;
	private double _myUpperBound = 0;
	
	private double _myCurrentTime;
	private double _mySpeedFactor;
	
	private CCListenerManager<CCTransportable> _myTransportEvents = CCListenerManager.create(CCTransportable.class);
	private CCListenerManager<TransportStateListener> _myStateListener = CCListenerManager.create(TransportStateListener.class);
	
	private CCListenerManager<MarkerListener> _myMarkerListener = CCListenerManager.create(MarkerListener.class);
	
	public static enum PlayMode {
		PLAYING, STOPPED
	}
	
	private PlayMode _myPlayMode;
	
	private boolean _myIsInLoop = false;
	
	private TimeRange _myLoop = new TimeRange();
	
	private TimelineController _myTimelineController;
	
	private RulerInterval _myInterval;
	
	private boolean _myUseBeats = false;
	
	private double _myBPM = 120;
	
	public CCTransportController(TimelineController theTimelineController) {
		super(theTimelineController, null);
		_myTimelineController = theTimelineController;
		_myMarkerList = new TrackData(null);
		
		_myPlayMode = PlayMode.STOPPED;
		_myCurrentTime = 0;
		_mySpeedFactor = 1;
		
		_myInterval = new RulerInterval(Float.MAX_VALUE, 0f, 1f);
		createIntervals();
	}
	
	public void useBeats(boolean theUseBeats){
		_myUseBeats = theUseBeats;
		createIntervals();
		_myInterval = currentInterval();
	}
	
	private RulerInterval currentInterval() {
		int myDevides = _myRulerView.width() / MIN_SPACE;
		double myInterval = (_myUpperBound - _myLowerBound) / myDevides;
		
		for(RulerInterval myRulerInterval:_myIntervals) {
			if(myRulerInterval.isInInterval(myInterval)) {
				return myRulerInterval;
			}
		}
		return new RulerInterval(Float.MAX_VALUE, 0f, 1f);
	}
	
	private void createIntervals() {
		_myIntervals.clear();
		if(_myUseBeats){
			double myBarTime = (60 / _myBPM) * 4;
			
			for(int i = 1; i < 16;i++){
				double myUpperInterval = myBarTime * CCMath.pow(2, i);
				double mylowerInterval = myBarTime * CCMath.pow(2, i - 1);
				_myIntervals.add(new RulerInterval(myUpperInterval, mylowerInterval, myUpperInterval));
			}
			for(int i = 1; i < 16;i++){
				double myUpperInterval = myBarTime * CCMath.pow(0.5, i - 1);
				double mylowerInterval = myBarTime * CCMath.pow(0.5, i);
				_myIntervals.add(new RulerInterval(myUpperInterval, mylowerInterval, myUpperInterval));
			}
		}else{
			_myIntervals.add(new RulerInterval(18000, 9000, 18000));
			_myIntervals.add(new RulerInterval(9000, 3600, 7200));
			_myIntervals.add(new RulerInterval(3600, 1800, 3600));
			
			_myIntervals.add(new RulerInterval(1800, 1500, 1800));
			_myIntervals.add(new RulerInterval(1500, 600, 1200));
			_myIntervals.add(new RulerInterval(600, 300, 600));
			
			_myIntervals.add(new RulerInterval(300, 150, 300));
			_myIntervals.add(new RulerInterval(150, 60, 120));
			_myIntervals.add(new RulerInterval(60, 30, 60));
			
			_myIntervals.add(new RulerInterval(30, 20, 30));
			_myIntervals.add(new RulerInterval(20, 10, 20));
			
			_myIntervals.add(new RulerInterval(10, 5, 10));
			_myIntervals.add(new RulerInterval(5, 2, 5));
			_myIntervals.add(new RulerInterval(2, 1, 2));
			
			_myIntervals.add(new RulerInterval(1.0000, 0.5000f, 1));
			_myIntervals.add(new RulerInterval(0.5000, 0.2500f, 0.5f));
			_myIntervals.add(new RulerInterval(0.2500, 0.1000f, 0.2f));
	
			_myIntervals.add(new RulerInterval(0.1000, 0.0500f, 0.1f));
			_myIntervals.add(new RulerInterval(0.0500, 0.0250f, 0.05f));
			_myIntervals.add(new RulerInterval(0.0250, 0.0100f, 0.02f));
	
			_myIntervals.add(new RulerInterval(0.0100, 0.0050f, 0.01f));
			_myIntervals.add(new RulerInterval(0.0050, 0.0025f, 0.005f));
			_myIntervals.add(new RulerInterval(0.0025, 0.0010f, 0.002f));
			
			_myIntervals.add(new RulerInterval(0.0010, 0.0000f, 0.001f));
		}
	}
	
	public RulerInterval rulerInterval() {
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
		_myMarkerList.add(new MarkerPoint(theTime, theName));
		if(_myRulerView != null)_myRulerView.render();
	}
	
	/**
	 * Adds a listener to react on marker events
	 * @param theMarkerListener
	 */
	public void addMarkerListener(MarkerListener theMarkerListener){
		_myMarkerListener.add(theMarkerListener);
	}
	
	/**
	 * Removes the given listener
	 * @param theMarkerListener
	 */
	public void removeMarkerListener(MarkerListener theMarkerListener){
		_myMarkerListener.add(theMarkerListener);
	}
	
	public TrackData marker() {
		return _myMarkerList;
	}
	
	public TimeRange loopRange() {
		return _myLoop;
	}
	
	public void rulerView(SwingRulerView theRulerView) {
		view(theRulerView);
		_myRulerView = theRulerView;
		_myTimeRangeController = new TimeRangeController(_myTimelineController, loopRange(), this);
		_myInterval = currentInterval();
	}
	
	private MarkerPoint _myLastMarker = null;
	
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
		return _myLoop.start();
	}
	
	public double loopEnd() {
		return _myLoop.end();
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
	
	public void setRange( double theLowerBound, double theUpperBound ) {
		_myLowerBound = theLowerBound;
		_myUpperBound = theUpperBound;
		_myInterval = currentInterval();
		if(_myRulerView != null)_myRulerView.render();
	}

	public double lowerBound() {
		return _myLowerBound;
	}

	public double upperBound() {
		return _myUpperBound;
	}
	
	private void moveTransport(final int theMouseX) {
		if(_myRulerView == null)return;
		double myClickedTime = viewXToTime(theMouseX, true);
		myClickedTime = _myTimelineController.quantize(myClickedTime);
		time(myClickedTime);
	}
	
	@Override
	public TrackData trackData() {
		return _myMarkerList;
	}
	
	public void trackData(TrackData theTrackData) {
		_myMarkerList = theTrackData;
	}
	
	public TrackType trackType() {
		return TrackType.MARKER;
	}
    
//    public double viewXToTime(int theViewX) {
//        return _myRulerView.viewXToTime(theViewX);
//    }
//
//    public int timeToViewX(double theTime) {
//        return _myRulerView.timeToViewX(theTime);
//    }
	
	public Point2D curveToViewSpace(ControlPoint thePoint) {
        Point2D myResult = new Point2D.Double();
        int myX = timeToViewX(thePoint.time());
        int myY = 0; // reverse y axis
        myResult.setLocation(myX, myY);
        return myResult;
    }
    
	@Override
    public ControlPoint viewToCurveSpace(Point2D thePoint, boolean theGetPos) {
        ControlPoint myResult = new ControlPoint();
        
        myResult.time(viewXToTime((int) thePoint.getX(), theGetPos));
        myResult.value(0);
        
        return myResult;
    }
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		MarkerPoint myMarkerPoint = new MarkerPoint(theCurveCoords.time(), "");
		
		if(_myRulerView != null)_myRulerView.showMarkerDialog(myMarkerPoint);
		
		return myMarkerPoint;
	}
	
	/* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
     */
    @Override
    public void dragPointImp(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift) {
    	ControlPoint myPoint = _myTrackContext.quantize(myTargetPosition);
    	trackData().move(theDraggedPoint, myPoint);
    }
	
	private double _myLoopStart = 0;
	private double _myLoopEnd = 0;
	
	public void mousePressed(MouseEvent e) {
		if(_myRulerView == null)return;
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		
		_myDefineLoop = e.getY() < _myRulerView.height() / 2;
		
		if(!_myDefineLoop) {
			if(!myPressedShift) {
				moveTransport(e.getX());
			}else {
				super.mousePressed(e, null);
			}
		}else {
			_myTimeRangeController.mousePressed(e);
			_myLoopStart = _myTimeRangeController._myLoopStart;
			_myLoopEnd = _myTimeRangeController._myLoopEnd;
		}
		_myRulerView.render();
		_myTimelineController.renderInfo();
	}
	
	public void mouseDragged(MouseEvent e) {
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		
		if(!_myDefineLoop) {
			if(!myPressedShift) {
				moveTransport(e.getX());
				_myTimelineController.renderInfo();
			}else {
				super.mouseDragged(e, null);
			}
			_myTimelineController.renderInfo();
		}else {
			_myTimeRangeController.mouseDragged(e);
			doLoop(true);
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		if(_myDefineLoop) {
			if(e.getX() == _myStartClickX ) {
				doLoop(false);
			}else {

				if(myPressedShift) {
				
					_myTimelineController.scaleTracks(
						_myLoopStart, _myLoopEnd, 
						_myTimeRangeController._myLoopStart, _myTimeRangeController._myLoopEnd
					);
				}
			}
		}else {
			if(!myPressedShift) {
//				moveTransport(e.getX());
			}else {
				super.mouseReleased(e, null);
			}
		}
	}
	
	public boolean isPlaying() {
		return _myPlayMode == PlayMode.PLAYING;
	}
	
	public void play() {
		if (isPlaying()) {
			rewind();
		} else {
			_myPlayMode = PlayMode.PLAYING;
		}
		for(TransportStateListener myStateListener:_myStateListener){
			myStateListener.play(_myCurrentTime);
		}
	}
	
	public void stop() {
		if (!isPlaying()) {
			rewind();
		}
		_myPlayMode = PlayMode.STOPPED;
		for(TransportStateListener myStateListener:_myStateListener){
			myStateListener.stop(_myCurrentTime);
		}
	}
	
	public void rewind() {
		_myCurrentTime = _myLoopStart;
		_myTransportEvents.proxy().time(_myCurrentTime);
	}
	
	public void loop() {
		doLoop(!doLoop());
	}
	
	public void onTransportAction(final TransportAction theAction) {
		switch (theAction) {
		case PLAY:
			play();
			break;
		case STOP:
			stop();
			break;
		case LOOP:
			loop();
			break;

		default:
			break;
		}
	}
	
	public void update(double theDeltaT) {
		if(_myRulerView != null)_myRulerView.render();
		if (_myPlayMode == PlayMode.PLAYING) {
			_myCurrentTime += theDeltaT * _mySpeedFactor;
			
			if(_myIsInLoop && _myCurrentTime > _myLoop.end()) {
				_myCurrentTime = _myLoop.start() + _myCurrentTime - _myLoop.end();
			}
			_myTransportEvents.proxy().time(_myCurrentTime);
			
			MarkerPoint myCurrentMarker = (MarkerPoint)_myMarkerList.getFirstPointAt(_myCurrentTime);
			if(myCurrentMarker != _myLastMarker && _myLastMarker != null){
				for(MarkerListener myListener:_myMarkerListener){
					myListener.onMarker(_myLastMarker);
				}
			}
			_myLastMarker = myCurrentMarker;
		}
	}
	
	double _myLastTime = -1;
	
	public void time(double theTime) {
		theTime = CCMath.max(0,theTime);
		_myCurrentTime = theTime;
		_myTransportEvents.proxy().time(_myCurrentTime);
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
	
	public CCListenerManager<CCTransportable> transportEvents(){
		return _myTransportEvents;
	}
	
	public void addStateListener(TransportStateListener theTransportable) {
		_myStateListener.add(theTransportable);
	}
	
	public void removeStateListener(TransportStateListener theTransportable) {
		_myStateListener.remove(theTransportable);
	}
}
