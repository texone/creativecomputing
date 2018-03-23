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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCSelectionPropertyHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.timeline.CCAbstractTrack;
import cc.creativecomputing.control.timeline.CCGroupTrack;
import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCPresetTrackObject;
import cc.creativecomputing.controlui.timeline.controller.quantize.CCQuantizeMode;
import cc.creativecomputing.controlui.timeline.controller.track.CCBooleanTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCDoubleTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGradientTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCIntegerTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTriggerTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineView;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


/**
 * @author christianriekoff
 * 
 */
public class CCTimelineController extends CCTrackContext {
	
	private CCTransportController _myTransportController;
	
	private CCGroupTrackController _myRootController;
	private CCGroupTrackController _myClipController;
	private Map<Path, CCGroupTrackController> _myGrouptrackControllerMap = new HashMap<>();
	private List<Path> _myGroupOrder = new ArrayList<>();
	private SwingTimelineView _myView;
	
	private Map<Path, CCTrackController> _myTrackControllerMap;
	private List<CCTrackController> _myTrackController;
	
	private final CCObjectPropertyHandle _myRootHandle;
	
	private final CCTimelineContainer _myTimelineContainer;

	protected CCQuantizeMode _myQuantizeMode;
	
	public CCTimelineController(CCTimelineContainer theTimelineContainer, CCObjectPropertyHandle theRootHandle) {
		super();
		_myTimelineContainer = theTimelineContainer;
		_myTransportController = new CCTransportController(this);
		_myTransportController.timeEvents.add(this::time);
		
		_myTrackControllerMap = new HashMap<>();
		_myTrackController = new ArrayList<>();
		
		_myRootHandle = theRootHandle;

		_myQuantizeMode = CCQuantizeMode.OFF;
	}
	
	public void view(SwingTimelineView theView){
		_myView = theView;
		_myView.controller(this);
		_myTransportController.rulerView(_myView.rulerView());
		_myZoomController.events.add(_myTransportController::setRange);
	}
	
	public CCObjectPropertyHandle rootHandle(){
		return _myRootHandle;
	}
	
	public SwingTimelineView view() {
		return _myView;
	}
	
	public CCZoomController zoomController() {
		return _myZoomController;
	}
	
	public CCTransportController transportController() {
		return _myTransportController;
	}
	
	public List<CCTrackController> trackController(){
		return _myTrackController;
	}
	
	/**
	 * Raster resolution to align the track data
	 * @param theRaster Raster resolution to align the track data
	 */
	public void quantizer(CCQuantizeMode theQuantizeMode){
		_myQuantizeMode = theQuantizeMode;
	}
	
	/**
	 * Raster resolution to align the track data
	 * @return Raster resolution to align the track data
	 */
	public CCQuantizeMode quantizer(){
		return _myQuantizeMode;
	}
	
	@Override
	/**
	 * Snaps the time of the given point to the raster of this context. This is called quantization.
	 * @param thePoint
	 * @return
	 */
	public CCControlPoint quantize(CCControlPoint thePoint) {
    	double myTime = quantize(thePoint.time());
        thePoint.time(myTime);
        return thePoint;
	}

	@Override
	public double quantize(double theTime) {
		return _myQuantizeMode.quantizer().quantize(_myTransportController,theTime);
	}
	
	public int drawRaster(){
		return _myQuantizeMode.quantizer().drawRaster(_myTransportController);
	}
	
	/**
	 * Sets the zoom range based on the selected range
	 */
	public void zoomToLoop() {
		// TODO check zoom to selection
		_myZoomController.setRange(_myTransportController.loopRange());
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public double maximumTime() {
		double myMaxValue = 0;
		for (CCTrackController myTrackController : _myTrackControllerMap.values()) {
			myMaxValue = CCMath.max(myTrackController.maxTime(), myMaxValue);
		}
		for (CCTrackController myTrackController : _myGrouptrackControllerMap.values()) {
			myMaxValue = CCMath.max(myTrackController.maxTime(), myMaxValue);
		}
		return myMaxValue;
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public void zoomToMaximum() {
		_myZoomController.setRange(new CCTimeRange(0, maximumTime()));
	}
	
	public CCGroupTrackController rootController(){
		return _myRootController;
	}
	
	public CCGroupTrackController clipController(){
		return _myClipController;
	}
	
	public void insertTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;
		_myTransportController.trackData().insertTime(myLowerBound, myRange);
		for (CCTrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void insertTime(double theInsertTime, double theTime){
		for (CCTrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(theInsertTime, theTime);
			myController.view().render();
		}
	}
	
	public void removeTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;

		_myTransportController.trackData().cutRangeAndTime(myLowerBound, myRange);
		for (CCTrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().cutRangeAndTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void colorTrack(final CCColor theColor, final Path thePath) {
		CCTrackController myTrackController = _myTrackControllerMap.get(thePath);
		if(myTrackController != null) {
			myTrackController.track().color(theColor);
			myTrackController.view().color(theColor);
		}
	}
	
	public void scaleTracks(double theOldLoopStart, double theOldLoopEnd, double theNewLoopStart, double theNewLoopEnd) {
		//TODO CHECK THIS
//		for(TrackController myController : _myTrackControllerMap.values()) {
//			myController.trackData().scaleRange(theOldLoopStart, theOldLoopEnd, theNewLoopStart, theNewLoopEnd);
//			myController.view().render();
//		}
	}
	
	public CCGroupTrackController group(Path theGroupPath) {
		return _myGrouptrackControllerMap.get(theGroupPath);
	}
	
	public CCTrackController track(Path thePath) {
		return _myTrackControllerMap.get(thePath);
	}
	
	public CCGroupTrackController createGroupController(CCObjectPropertyHandle theProperty) {
		if(theProperty.parent() != null){
			createGroupController(theProperty.parent());
		}
		CCGroupTrackController myResult = group(theProperty.path());
		
		if(myResult != null) return myResult;
		
		CCGroupTrack myGroupTrack = new CCGroupTrack(theProperty);
		Map<String, String> myExtraMap = new HashMap<>();
		myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
		myGroupTrack.extras(myExtraMap);
		myGroupTrack.color(CCColorMap.getColor(theProperty.path()));
		myGroupTrack.isOpen(true);
		
		myResult = new CCGroupTrackController(this, myGroupTrack);
		myResult.events().add(new CCEventTrackAdapter() {
			@Override
			public void onProperties(CCEventTrackController theController, CCTimedEventPoint thePoint) {
				_myView.openGroupPresetDialog(theProperty, theController, thePoint);
			}
		});
		myResult.events().add(new CCPresetTrackObject(theProperty));
		_myGrouptrackControllerMap.put(theProperty.path(), myResult);
		_myGroupOrder.add(theProperty.path());
		_myTrackController.add(myResult);
		
		openGroups();
		
		if(_myView != null){
			SwingGroupTrackView myGroupView = _myView.addGroupTrack(_myTrackCount, myResult);
			myGroupView.color(myGroupTrack.color());
			
			myResult.view(myGroupView);
		}
		
		if(myGroupTrack.property().parent() != null){
			CCGroupTrackController myGroupTrackController = _myGrouptrackControllerMap.get(theProperty.parent().path());
			myGroupTrackController.addTrack(myResult);
		}
		
		_myTrackCount++;
		
		
		if(theProperty.parent() == null){
			_myRootController = myResult;
		}

		_myZoomController.addZoomable(myResult);
		return myResult;
	}
	
	public CCGroupTrackController createGroupController(Path thePath){
		return createGroupController((CCObjectPropertyHandle)_myRootHandle.property(thePath));
	}
	
	private static class TrackRenderAction implements CCPropertyListener<Object>{
		
		private CCTrackController _myTrackController;
		
		public TrackRenderAction(CCTrackController theCurveTrackController){
			_myTrackController = theCurveTrackController;
		}

		@Override
		public void onChange(Object theValue) {
			if(_myTrackController.trackData().size() == 0)_myTrackController.view().render();
		}
		
	}
	
	private int _myArrangeCounter = 0;
	
	private List<CCPropertyHandle<?>> _myClipTrackHandles = new ArrayList<CCPropertyHandle<?>>();
	
	public CCGroupTrackController createClipGroup(Path thePath){
		CCGroupTrack myGroupTrack = new CCGroupTrack(thePath);
		Map<String, String> myExtraMap = new HashMap<>();
		myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
		myGroupTrack.extras(myExtraMap);
//		myGroupTrack.color(CCColorMap.getColor(theProperty.path()));
		myGroupTrack.isOpen(true);
		
		CCGroupTrackController myResult = new CCGroupTrackController(this, myGroupTrack);
		_myGrouptrackControllerMap.put(thePath, myResult);
		_myGroupOrder.add(thePath);
		_myTrackController.add(myResult);
		
		openGroups();
		
		if(_myView != null){
			SwingGroupTrackView myGroupView = _myView.addGroupTrack(_myTrackCount, myResult);
			myGroupView.color(myGroupTrack.color());
			
			myResult.view(myGroupView);
		}
		
		_myTrackCount++;

		_myZoomController.addZoomable(myResult);
		return myResult;
	}
	
	public CCTrackController createClipTrack(Path thePath){
		if(_myClipController == null)_myClipController = createClipGroup(Paths.get("clip arrange"));
		
		CCClipTrackObject myClipTrackObject = new CCClipTrackObject(_myTimelineContainer);
		CCObjectPropertyHandle myParent = new CCObjectPropertyHandle(myClipTrackObject, "cliptrack");
		myParent.path(Paths.get("clip arrange"));
		
		CCPropertyHandle<?> myProperty = myParent.property("trackID");
		_myClipTrackHandles.add(myProperty);
		myProperty.path(thePath);
		CCEventTrackController myEventController = (CCEventTrackController)createController(myProperty, myClipTrackObject);
		myEventController.splitDrag(true);
		myEventController.events().add(myClipTrackObject);
		myEventController.events().add(new CCEventTrackAdapter() {
			@Override
			public void onProperties(CCEventTrackController theController, CCTimedEventPoint thePoint) {
				_myView.openClipTrackDialog(theController, thePoint);
			}
			
			@Override
			public void onTime(double theTime, CCEventTrackController theController, CCTimedEventPoint thePoint) {
				// TODO Auto-generated method stub
				super.onTime(theTime, theController, thePoint);
			}
		});
		_myClipController.addTrack(myEventController);
		return myEventController;
	}
	
	public void resetClipTracks(){
		for(CCPropertyHandle<?> _myClipTrackProperty:new ArrayList<>(_myClipTrackHandles)){
			removeTrack(_myClipTrackProperty.path());
		}
	}
	
	public CCTrackController createClipTrack(){
		Path myPath = Paths.get("clip arrange","track " + _myArrangeCounter++);
		while(_myTrackControllerMap.containsKey(myPath)){
			myPath = Paths.get("clip arrange","track " + _myArrangeCounter++);
		}
		return createClipTrack(myPath);
	}
	
	public CCTrackController createController(CCPropertyHandle<?> theProperty, CCClipTrackObject theObject){

		Path myPath = theProperty.path();
		if(_myTrackControllerMap.containsKey(myPath))return _myTrackControllerMap.get(myPath);
		
		CCGroupTrackController myGroup = _myGrouptrackControllerMap.get(theProperty.parent().path());
		CCTrack myTrack = new CCTrack(theProperty);
		myTrack.color(CCColorMap.getColor(theProperty.path()));
		
		CCTrackController myTrackController = null;
		if(theProperty instanceof CCBooleanPropertyHandle){
			myTrackController = new CCBooleanTrackController(this, myTrack, myGroup);
		}else if(theProperty instanceof CCEventTriggerHandle){
			myTrackController = new CCTriggerTrackController(this, myTrack, myGroup);
		}else if(theProperty instanceof CCNumberPropertyHandle<?>){
			CCNumberPropertyHandle<?> myNumberProperty = (CCNumberPropertyHandle<?>)theProperty;
			if(myNumberProperty.max() instanceof Integer){
				myTrackController = new CCIntegerTrackController(this, myTrack, myGroup);
			}else if(myNumberProperty.max() instanceof Float){
				myTrackController = new CCDoubleTrackController(this, myTrack, myGroup);
			}else if(myNumberProperty.max() instanceof Double){
				myTrackController = new CCDoubleTrackController(this, myTrack, myGroup);
			}
		}else if(theProperty instanceof CCColorPropertyHandle){
			myTrackController = new CCColorTrackController(this, myTrack, myGroup);
		}else if(theProperty instanceof CCGradientPropertyHandle){
			myTrackController = new CCGradientTrackController(this, myTrack, myGroup);
		}else if(theProperty instanceof CCStringPropertyHandle){
			myTrackController = new CCEventTrackController(this, myTrack, myGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}else if(theProperty instanceof CCEnumPropertyHandle){
			myTrackController = new CCEventTrackController(this, myTrack, myGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}else if(theProperty instanceof CCSelectionPropertyHandle){
			myTrackController = new CCEventTrackController(this, myTrack, myGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}else if(theProperty instanceof CCPathHandle){
			CCEventTrackController myEventController = new CCEventTrackController(this, myTrack, myGroup);
			myEventController.events().add(new CCEventTrackAdapter() {
				
				@Override
				public void onTime(double theTime, CCEventTrackController theController, CCTimedEventPoint thePoint) {
					((CCPathHandle)theProperty).time(theTime, theTime - thePoint.time(), thePoint.contentOffset());
				}
				
				@Override
				public void onOut() {
					((CCPathHandle)theProperty).out();
				}
				
				@Override
				public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double UpperTime, CCGraphics theG2d) {
//					((CCPathHandle)theProperty).renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, UpperTime, theG2d);
				}
			});
			myEventController.splitDrag(true);
			_myTransportController.playEvents.add(t -> {((CCPathHandle)theProperty).play();});
			_myTransportController.stopEvents.add(t -> {((CCPathHandle)theProperty).stop();});
				
			myTrackController = myEventController;
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(CCEventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}
		if(myTrackController == null)return null;
		
		_myTrackControllerMap.put(myPath, myTrackController);
		_myTrackController.add(myTrackController);
		
		if(_myView != null){
			CCAbstractTrackView myTrackView = _myView.addTrack(_myTrackCount, myTrackController,  theObject);
			myTrackController.view(myTrackView);
			myTrackController.view().color(myTrack.color());
		}
		myTrackController.mute(myTrack.mute());
		
		_myZoomController.addZoomable(myTrackController);
		
		
		if(myGroup != null) {
			myGroup.addTrack(myTrackController);
		}
		
		theProperty.events().add(new TrackRenderAction(myTrackController));
		
		_myTrackCount++;
		
		return myTrackController;
	}
	
	public CCTrackController createController(Path thePath){
		if(thePath.startsWith("clip arrange")){
			return createClipTrack();
		}
		return createController(_myRootHandle.property(thePath), null);
	}
	
	public void removeTrack(Path thePath){
		CCTrackController myRemoveController = null;
		if(_myGrouptrackControllerMap.containsKey(thePath)){
			CCGroupTrackController myController = _myGrouptrackControllerMap.remove(thePath);
			myRemoveController = myController;
			if(myController == _myRootController)_myRootController = null;
			_myGroupOrder.remove(thePath);
			_myTrackController.remove(myController);
			_myTrackCount--;
			for(CCTrackController myTrackController:new ArrayList<>(myController.trackController())) {
				removeTrack(myTrackController.property().path());
			}
		}else if(_myTrackControllerMap.containsKey(thePath)){
			CCTrackController myController = _myTrackControllerMap.remove(thePath);
			myRemoveController = myController;
			_myZoomController.removeZoomable(myController);
			_myTrackController.remove(myController);
			_myClipTrackHandles.remove(myController.property());
			_myTrackCount--;
		}
		try{
			CCGroupTrackController myParentController = _myGrouptrackControllerMap.get(thePath.getParent());
			myParentController.removeTrack(myRemoveController);
		}catch(Exception e){
			
		}
		
		if(_myView != null)_myView.removeTrack(thePath);
	}
	
	public void removeAll(){
		if(_myRootController == null)return;
		_myTrackCount = 0;
		removeTrack(_myRootController.property().path());
	}
	
	public void render(){
		for (CCTrackController myController : _myTrackControllerMap.values()) {
			if(myController.view() != null)myController.view().render();
		}
		for (CCTrackController myController : _myGrouptrackControllerMap.values()) {
			if(myController.view() != null)myController.view().render();
		}
	}

	@Override
	public void renderInfo() {
		for (CCTrackController myController : _myTrackControllerMap.values()) {
			if(myController.view() != null)myController.view().renderInfo();
		}
		for (CCTrackController myController : _myGrouptrackControllerMap.values()) {
			if(myController.view() != null)myController.view().renderInfo();
		}
	}
	
	@SuppressWarnings("unused")
	private void insertDataTrack(CCTrack theTrack, double theRange) {
		CCTrackController myTrackController = _myTrackControllerMap.get(theTrack.property().path());
		if(myTrackController != null) {
			myTrackController.trackData().insertAll(_myTransportController.time(), theRange, theTrack.trackData().rangeList(0, theRange));
		}
	}
	
	private double checkMaxTime(double theMaxTime, CCTrackData theTrackData){
		double myLastTime = theTrackData.getLastTime();
		if (myLastTime > theMaxTime) {
			return myLastTime;
		}
		return theMaxTime;
	}
	
	private double checkMaxTime(double theMaxTime, CCTrack theTrack){
		return checkMaxTime(theMaxTime, theTrack.trackData());
	}
	
	private void insertTrackData(double theInsertTime, double theMaxTime, CCTrack theTrack){
		CCTrackController myController = _myTrackControllerMap.get(theTrack.property().path());
		if(myController == null){
			return;
		}
		myController.trackData().insertAll(theInsertTime, theMaxTime, theTrack.trackData().rangeList(0));
	}
	
	public void insertTracks(List<CCAbstractTrack> theTracks, CCTrackData theMarkerTrack){
		if(theTracks == null)return;
		
		double myMaxTime = 0;
		for (CCAbstractTrack myAbstractTrack : theTracks) {
			if(myAbstractTrack instanceof CCTrack) {
				myMaxTime = checkMaxTime(myMaxTime, (CCTrack)myAbstractTrack);
			}else if(myAbstractTrack instanceof CCGroupTrack) {
				for(CCTrack myTrack:((CCGroupTrack)myAbstractTrack).tracks()) {
					myMaxTime = checkMaxTime(myMaxTime, myTrack);
				}
			}
		}
		myMaxTime = checkMaxTime(myMaxTime, theMarkerTrack);
		
		double myInsertTime = _myTransportController.time();
		
		
		for (CCAbstractTrack myAbstractTrack : theTracks) {
			if(myAbstractTrack instanceof CCGroupTrack) {
				for(CCTrack myTrack:((CCGroupTrack)myAbstractTrack).tracks()) {
					insertTrackData(myInsertTime, myMaxTime, myTrack);
				}
			}else if(myAbstractTrack instanceof CCTrack) {
				CCTrack myTrack = (CCTrack)myAbstractTrack;
				insertTrackData(myInsertTime, myMaxTime, myTrack);
			}
		}
		
		_myTransportController.trackData().insertAll(myInsertTime, myMaxTime, theMarkerTrack.rangeList(0));
	}
	
	public void resetTracks() {
		for(CCTrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.reset();
		}
	}
	
	public void closeGroups(){
		for(Path myPath:_myGroupOrder){
			CCGroupTrackController myGroupTrackController = _myGrouptrackControllerMap.get(myPath);
			myGroupTrackController.closeGroup(false);
		}
	}
	
	public void openGroups(){
		for(CCGroupTrackController myGroupTrackController:_myGrouptrackControllerMap.values()){
			myGroupTrackController.openGroup(false);
		}
	}
	
	public void reverseTracks(){
		double myMaximumTime = maximumTime();
		for(CCTrackController myTrackController:_myTrackControllerMap.values()){
			myTrackController.track().trackData().reverse(0, myMaximumTime);
		}
		render();
	}
	
	public void hideUnusedTracks(boolean theHideUnusedTracks){
		if(theHideUnusedTracks)_myView.hideUnusedTracks();
		else _myView.showUnusedTracks();
	}
	
	private int _myTrackCount = 0;
	
	public void mousePressed(CCGLMouseEvent e) {
	}

	public void mouseReleased(CCGLMouseEvent e) {
	}

	public void mouseDragged(CCGLMouseEvent e) {
	}

	public void mouseMoved(CCGLMouseEvent e) {
	}

	/**
	 * @param theIsMuted
	 */
	public void muteAll(boolean theIsMuted) {
		for(CCTrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.mute(theIsMuted);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TransportTimeListener#time(double)
	 */
	public void time(double theTime) {
		for(CCPropertyHandle<?> myHandle:new ArrayList<>(_myClipTrackHandles)){
			myHandle.update(0);
		}
		if(_myClipController != null){
			_myClipController.time(theTime);
//			for(TrackController myYO:_myClipController.trackController()){
//				CCLog.info(myYO.trackData().size());
//				myYO.time(theTime);
//			}
		}
		if(_myRootController != null)_myRootController.time(theTime);
		renderInfo();
	}
}
