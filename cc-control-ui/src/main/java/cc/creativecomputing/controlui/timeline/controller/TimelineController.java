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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPathHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.control.timeline.AbstractTrack;
import cc.creativecomputing.control.timeline.GroupTrack;
import cc.creativecomputing.control.timeline.TimeRange;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.CCColorMap;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCClipTrackObject;
import cc.creativecomputing.controlui.timeline.controller.arrange.CCPresetTrackObject;
import cc.creativecomputing.controlui.timeline.controller.track.BooleanTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.DoubleTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.GroupTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.IntegerTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineView;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingGroupTrackView;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;


/**
 * @author christianriekoff
 * 
 */
public class TimelineController extends TrackContext implements TransportTimeListener{
	
	private TransportController _myTransportController;
	
	private GroupTrackController _myRootController;
	private Map<Path, GroupTrackController> _myGrouptrackControllerMap = new HashMap<>();
	private List<Path> _myGroupOrder = new ArrayList<>();
	private SwingTimelineView _myView;
	
	private Map<Path, TrackController> _myTrackControllerMap;
	private List<TrackController> _myTrackController;
	
	private final CCPropertyMap _myPropertyMap;
	
	private final TimelineContainer _myTimelineContainer;
	
	public TimelineController(TimelineContainer theTimelineContainer, CCPropertyMap thePropertyMap) {
		super();
		_myTimelineContainer = theTimelineContainer;
		_myTransportController = new TransportController(this);
		_myTransportController.addTimeListener(this);
		
		_myTrackControllerMap = new HashMap<>();
		_myTrackController = new ArrayList<>();
		
		_myPropertyMap = thePropertyMap;
	}
	
	public void view(SwingTimelineView theView){
		_myView = theView;
		_myView.controller(this);
		_myTransportController.rulerView(_myView.rulerView());
		_myZoomController.addZoomable(_myTransportController);
	}
	
	public CCPropertyMap propertyMap(){
		return _myPropertyMap;
	}
	
	public SwingTimelineView view() {
		return _myView;
	}
	
	public CCZoomController zoomController() {
		return _myZoomController;
	}
	
	public TransportController transportController() {
		return _myTransportController;
	}
	
	public ToolController toolController() {
		return _myToolController;
	}
	
	public List<TrackController> trackController(){
		return _myTrackController;
	}
	
	public int drawRaster(){
		return _myQuantizer.drawRaster();
	}
	
	/**
	 * Sets the zoom range based on the selected range
	 */
	public void zoomToSelection() {
		// TODO check zoom to selection
		_myZoomController.setRange(_myToolController.selectionController().range());
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public double maximumTime() {
		double myMaxValue = 0;
		for (TrackController myTrackController : _myTrackControllerMap.values()) {
			myMaxValue = CCMath.max(myTrackController.maxTime(), myMaxValue);
		}
		for (TrackController myTrackController : _myGrouptrackControllerMap.values()) {
			myMaxValue = CCMath.max(myTrackController.maxTime(), myMaxValue);
		}
		return myMaxValue;
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public void zoomToMaximum() {
		_myZoomController.setRange(new TimeRange(0, maximumTime()));
	}
	
	public GroupTrackController rootController(){
		return _myRootController;
	}
	
	public void insertTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;
		_myTransportController.trackData().insertTime(myLowerBound, myRange);
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void insertTime(double theInsertTime, double theTime){
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(theInsertTime, theTime);
			myController.view().render();
		}
	}
	
	public void removeTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;

		_myTransportController.trackData().cutRangeAndTime(myLowerBound, myRange);
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().cutRangeAndTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void colorTrack(final Color theColor, final Path thePath) {
		TrackController myTrackController = _myTrackControllerMap.get(thePath);
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
	
	public GroupTrackController group(Path theGroupPath) {
		return _myGrouptrackControllerMap.get(theGroupPath);
	}
	
	public TrackController track(Path thePath) {
		return _myTrackControllerMap.get(thePath);
	}
	

	
	public GroupTrackController createGroupController(CCObjectPropertyHandle theProperty) {
		if(theProperty.parent() != null){
			createGroupController(theProperty.parent());
		}else{
			
		}
		GroupTrackController myResult = group(theProperty.path());
		
		if(myResult != null) return myResult;
		
		GroupTrack myGroupTrack = new GroupTrack(theProperty);
		Map<String, String> myExtraMap = new HashMap<>();
		myExtraMap.put(EventTrackController.EVENT_TYPES,"new");
		myGroupTrack.extras(myExtraMap);
		myGroupTrack.color(CCColorMap.getColor(theProperty.path()));
		myGroupTrack.isOpen(true);
		
		myResult = new GroupTrackController(this, _myToolController, myGroupTrack);
		myResult.events().add(new EventTrackAdapter() {
			@Override
			public void onProperties(EventTrackController theController, TimedEventPoint thePoint) {
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
			GroupTrackController myGroupTrackController = _myGrouptrackControllerMap.get(theProperty.parent().path());
			myGroupTrackController.addTrack(myResult);
		}
		
		_myTrackCount++;
		
		if(theProperty.parent() == null){
			_myRootController = myResult;
		}

		_myZoomController.addZoomable(myResult);
		return myResult;
	}
	
	public GroupTrackController createGroupController(Path thePath){
		return createGroupController((CCObjectPropertyHandle)_myPropertyMap.property(thePath));
	}
	
	private static class TrackRenderAction implements CCPropertyListener<Object>{
		
		private TrackController _myTrackController;
		
		public TrackRenderAction(TrackController theCurveTrackController){
			_myTrackController = theCurveTrackController;
		}

		@Override
		public void onChange(Object theValue) {
			if(_myTrackController.trackData().size() == 0)_myTrackController.view().render();
		}
		
	}
	
	private int _myArrangeCounter = 0;
	
	private List<CCPropertyHandle<?>> _myClipTrackHandles = new ArrayList<CCPropertyHandle<?>>();
	
	public TrackController createClipTrack(Path thePath){
		CCClipTrackObject myClipTrackObject = new CCClipTrackObject(_myTimelineContainer);
		CCObjectPropertyHandle myParent = new CCObjectPropertyHandle(myClipTrackObject);
		myParent.path(Paths.get("clip arrange"));
		createGroupController(myParent);
		
		CCPropertyHandle<?> myProperty = myParent.property("trackID");
		_myClipTrackHandles.add(myProperty);
		myProperty.path(thePath);
		EventTrackController myEventController = (EventTrackController)createController(myProperty, myClipTrackObject);
		myEventController.events().add(myClipTrackObject);
		myEventController.events().add(new EventTrackAdapter() {
			@Override
			public void onProperties(EventTrackController theController, TimedEventPoint thePoint) {
				_myView.openClipTrackDialog(theController, thePoint);
			}
		});
		return myEventController;
	}
	
	public void resetClipTracks(){
		for(CCPropertyHandle<?> _myClipTrackProperty:new ArrayList<>(_myClipTrackHandles)){
			removeTrack(_myClipTrackProperty.path());
		}
	}
	
	public TrackController createClipTrack(){
		Path myPath = Paths.get("clip arrange","track " + _myArrangeCounter++);
		while(_myTrackControllerMap.containsKey(myPath)){
			myPath = Paths.get("clip arrange","track " + _myArrangeCounter++);
		}
		return createClipTrack(myPath);
	}
	
	public TrackController createController(CCPropertyHandle<?> theProperty, CCClipTrackObject theObject){

		Path myPath = theProperty.path();
		if(_myTrackControllerMap.containsKey(myPath))return _myTrackControllerMap.get(myPath);
		
		GroupTrackController myGroup = _myGrouptrackControllerMap.get(theProperty.parent().path());
		Track myTrack = new Track(theProperty);
		myTrack.color(CCColorMap.getColor(theProperty.path()));
		
		TrackController myTrackController = null;
		if(theProperty instanceof CCBooleanPropertyHandle){
			myTrackController = new BooleanTrackController(this, _myCurveToolController, myTrack, myGroup);
		}else if(theProperty instanceof CCNumberPropertyHandle<?>){
			CCNumberPropertyHandle<?> myNumberProperty = (CCNumberPropertyHandle<?>)theProperty;
			if(myNumberProperty.max() instanceof Integer){
				myTrackController = new IntegerTrackController(this, _myCurveToolController, myTrack, myGroup);
			}else if(myNumberProperty.max() instanceof Float){
				myTrackController = new DoubleTrackController(this, _myCurveToolController, myTrack, myGroup);
			}else if(myNumberProperty.max() instanceof Double){
				myTrackController = new DoubleTrackController(this, _myCurveToolController, myTrack, myGroup);
			}
		}else if(theProperty instanceof CCStringPropertyHandle){
			myTrackController = new EventTrackController(this, _myToolController, myTrack, myGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(EventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}else if(theProperty instanceof CCEnumPropertyHandle){
			myTrackController = new EventTrackController(this, _myToolController, myTrack, myGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(EventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}else if(theProperty instanceof CCPathHandle){
			EventTrackController myEventController = new EventTrackController(this, _myToolController, myTrack, myGroup);
			myEventController.events().add(new EventTrackAdapter() {
				
				@Override
				public void onTime(double theTime, EventTrackController theController, TimedEventPoint thePoint) {
					((CCPathHandle)theProperty).time(theTime, theTime - thePoint.time());
				}
				
				@Override
				public void onOut() {
					((CCPathHandle)theProperty).out();
				}
			});
			_myTransportController.addStateListener(new TransportStateListener() {
				
				@Override
				public void stop(double theTime) {
					((CCPathHandle)theProperty).stop();
				}
				
				@Override
				public void play(double theTime) {
					((CCPathHandle)theProperty).play();
				}
			});
			myTrackController = myEventController;
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(EventTrackController.EVENT_TYPES,"new");
			myTrack.extras(myExtraMap);
		}
		if(myTrackController == null)return null;
		
		_myTrackControllerMap.put(myPath, myTrackController);
		_myTrackController.add(myTrackController);
		
		if(_myView != null){
			SwingAbstractTrackView myTrackView = _myView.addTrack(_myTrackCount, myTrackController,  theObject);
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
	
	public TrackController createController(Path thePath){
		if(thePath.startsWith("clip arrange")){
			return createClipTrack();
		}
		return createController(_myPropertyMap.property(thePath), null);
	}
	
	public void removeTrack(Path thePath){
		if(_myGrouptrackControllerMap.containsKey(thePath)){
			GroupTrackController myController = _myGrouptrackControllerMap.remove(thePath);
			if(myController == _myRootController)_myRootController = null;
			_myGroupOrder.remove(thePath);
			_myTrackController.remove(myController);
			_myTrackCount--;
			for(TrackController myTrackController:myController.trackController()) {
				removeTrack(myTrackController.property().path());
			}
		}else if(_myTrackControllerMap.containsKey(thePath)){
			TrackController myController = _myTrackControllerMap.remove(thePath);
			_myZoomController.removeZoomable(myController);
			_myTrackController.remove(myController);
			_myClipTrackHandles.remove(myController.property());
			_myTrackCount--;
		}
		
		if(_myView != null)_myView.removeTrack(thePath);
	}
	
	public void removeAll(){
		if(_myRootController == null)return;
		_myTrackCount = 0;
		removeTrack(_myRootController.property().path());
	}
	
	@Override
	public void render(){
		for (TrackController myController : _myTrackControllerMap.values()) {
			if(myController.view() != null)myController.view().render();
		}
	}
	
	@SuppressWarnings("unused")
	private void insertDataTrack(Track theTrack, double theRange) {
		TrackController myTrackController = _myTrackControllerMap.get(theTrack.property().path());
		if(myTrackController != null) {
			myTrackController.trackData().insertAll(_myTransportController.time(), theRange, theTrack.trackData().rangeList(0, theRange));
		}
	}
	
	private double checkMaxTime(double theMaxTime, TrackData theTrackData){
		double myLastTime = theTrackData.getLastTime();
		if (myLastTime > theMaxTime) {
			return myLastTime;
		}
		return theMaxTime;
	}
	
	private double checkMaxTime(double theMaxTime, Track theTrack){
		return checkMaxTime(theMaxTime, theTrack.trackData());
	}
	
	private void insertTrackData(double theInsertTime, double theMaxTime, Track theTrack){
		TrackController myController = _myTrackControllerMap.get(theTrack.property().path());
		if(myController == null){
			CCLog.info("INSERT TIME:" + theTrack.property().path().toString());
			return;
		}
		myController.trackData().insertAll(theInsertTime, theMaxTime, theTrack.trackData().rangeList(0));
	}
	
	public void insertTracks(List<AbstractTrack> theTracks, TrackData theMarkerTrack){
		if(theTracks == null)return;
		
		double myMaxTime = 0;
		for (AbstractTrack myAbstractTrack : theTracks) {
			if(myAbstractTrack instanceof Track) {
				myMaxTime = checkMaxTime(myMaxTime, (Track)myAbstractTrack);
			}else if(myAbstractTrack instanceof GroupTrack) {
				for(Track myTrack:((GroupTrack)myAbstractTrack).tracks()) {
					myMaxTime = checkMaxTime(myMaxTime, myTrack);
				}
			}
		}
		myMaxTime = checkMaxTime(myMaxTime, theMarkerTrack);
		
		double myInsertTime = _myTransportController.time();
		
		
		for (AbstractTrack myAbstractTrack : theTracks) {
			CCLog.info(myAbstractTrack);
			if(myAbstractTrack instanceof GroupTrack) {
				for(Track myTrack:((GroupTrack)myAbstractTrack).tracks()) {
					CCLog.info(myTrack.property().path().toString());
					insertTrackData(myInsertTime, myMaxTime, myTrack);
				}
			}else if(myAbstractTrack instanceof Track) {
				Track myTrack = (Track)myAbstractTrack;
				insertTrackData(myInsertTime, myMaxTime, myTrack);
			}
		}
		
		_myTransportController.trackData().insertAll(myInsertTime, myMaxTime, theMarkerTrack.rangeList(0));
	}
	
	public void resetTracks() {
		for(TrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.reset();
		}
	}
	
	public void closeGroups(){
		for(Path myPath:_myGroupOrder){
			GroupTrackController myGroupTrackController = _myGrouptrackControllerMap.get(myPath);
			myGroupTrackController.closeGroup(false);
		}
	}
	
	public void openGroups(){
		for(GroupTrackController myGroupTrackController:_myGrouptrackControllerMap.values()){
			myGroupTrackController.openGroup(false);
		}
	}
	
	public void reverseTracks(){
		double myMaximumTime = maximumTime();
		for(TrackController myTrackController:_myTrackControllerMap.values()){
			myTrackController.track().trackData().reverse(0, myMaximumTime);
		}
		render();
	}
	
	public void hideUnusedTracks(boolean theHideUnusedTracks){
		if(theHideUnusedTracks)_myView.hideUnusedTracks();
		else _myView.showUnusedTracks();
	}
	
	private int _myTrackCount = 0;
	
	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * @param theIsMuted
	 */
	public void muteAll(boolean theIsMuted) {
		for(TrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.mute(theIsMuted);
		}
	}
	
	@Override
	public void update(double theDeltaTime) {
		
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TransportTimeListener#time(double)
	 */
	@Override
	public void time(double theTime) {
		for(CCPropertyHandle<?> myHandle:new ArrayList<>(_myClipTrackHandles)){
			myHandle.update(0);
		}
		if(_myRootController != null)_myRootController.time(theTime);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TransportTimeListener#onChangeLoop(cc.creativecomputing.timeline.model.TimeRange, boolean)
	 */
	@Override
	public void onChangeLoop(TimeRange theRange, boolean theLoopIsActive) {
	}
}
