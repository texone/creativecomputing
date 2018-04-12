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
package cc.creativecomputing.controlui.timeline.controller.track;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.actions.CCAddControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.actions.CCControlUndoHistory;
import cc.creativecomputing.controlui.timeline.tools.CCEventTrackTool;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;
import cc.creativecomputing.controlui.timeline.tools.CCEventTrackTool.EventAction;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 * 
 */
public class CCEventTrackController extends CCTrackController {
	
	private CCListenerManager<CCEventTrackListener> _myEventTrackListener = CCListenerManager.create(CCEventTrackListener.class);
	
	public CCEventTrackTool _myEventTrackTool;

	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	@SuppressWarnings({ "rawtypes" })
	public CCEventTrackController(
		CCTimelineController theTimelineController,
		CCTrack theTrack, 
		CCGroupTrackController theParent
	) {
		super(theTimelineController, theTrack, theParent);
		if(theTrack.property() == null)return;
		
		theTrack.property().changeEvents.add(theValue ->{
			_myEventTrackTool.editValue(theValue);
		});
		
		_myActiveTool = _myEventTrackTool = new CCEventTrackTool(this);
	}
	
	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.CREATE_EVENT};
	}
	
	@Override
	public void setTool(CCTimelineTools theTool) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public CCTimelineTools activeTool() {
		return CCTimelineTools.CREATE_EVENT;
	}
	
	public void splitDrag(boolean theSplitDrag){
		_myEventTrackTool.splitDrag(theSplitDrag);
	}
	
	public EventAction dragAction(){
		return null;
	}
	
	public CCListenerManager<CCEventTrackListener> events(){
		return _myEventTrackListener;
	}
	
	public static final String EVENT_TYPES = "eventTypes";
	
	public List<String> eventTypes(){
		List<String> myResult = new ArrayList<String>();
		String[] myEventTypesArray = _myTrack.extras().get(EVENT_TYPES).split(",");
		for(String myEventType:myEventTypesArray) {
			myResult.add(myEventType);
		}
		return myResult;
	}
	
	public void delete(CCTimedEventPoint theEvent) {
		trackData().remove(theEvent);
		_myEventTrackListener.proxy().onDelete(this, theEvent);
	}
	
	public void properties(CCTimedEventPoint theEvent) {
		_myEventTrackListener.proxy().onProperties(this, theEvent);
	}
	
	public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double upperTime, CCGraphics theG2d){
		_myEventTrackListener.proxy().renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, upperTime, theG2d);
	}
	
	public void createPoint(CCGLMouseEvent theEvent, String theEventType) {
		CCVector2 myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		CCTimedEventPoint myPoint = _myEventTrackTool.createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		_myEventTrackListener.proxy().onCreate(this, myPoint);
		CCControlUndoHistory.instance().apply(new CCAddControlPointCommand(this, myPoint));
	}
	
	
	public void writeValue(double theTime){
		CCControlPoint myControlPoint = new CCControlPoint(theTime, _myProperty.normalizedValue());
		_myEventTrackTool.createPoint(myControlPoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.CCVector2, boolean)
	 */
	
	
	private CCTimedEventPoint pointAt(double theTime) {
		CCControlPoint myCurveCoords = new CCControlPoint(theTime, 0);
		CCTimedEventPoint myLower = (CCTimedEventPoint)trackData().lower(myCurveCoords);
		if(myLower == null) return null;
		CCControlPoint myUpper = myLower.endPoint();
		if(myUpper == null) return null;
		if(myCurveCoords.time() > myLower.time() && myCurveCoords.time() < myUpper.time()) {
			return myLower;
		}
		return null;
	}
	
	public CCTimedEventPoint clickedPoint(CCGLMouseEvent e) {
		CCVector2 myViewCoords = new CCVector2(e.x, e.y);
		CCControlPoint myCurveCoords = viewToCurveSpace(myViewCoords, true);
		return pointAt(myCurveCoords.time());
	}
	
	public CCTimedEventPoint editedEvent(){
		return _myEventTrackTool.editedEvent();
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		CCTimedEventPoint myEventPoint = pointAt(theTime);
		
	    	if(myEventPoint == null || myEventPoint.content() == null || myEventPoint.content().value() == null){
	    		_myTrack.property().restorePreset();
	    		_myEventTrackListener.proxy().onOut();
	    		return;
	    	}
	
	    	_myTrack.property().valueCasted(myEventPoint.content().value(), false);
	    	_myEventTrackListener.proxy().onTime(theTime, this, myEventPoint);

//    	for (TimelineListener myListener : _myTimelineListener) {
//			TimedEvent myEvent = new TimedEvent(
//				myEventPoint,
//				theTime, 
//				myController.track().property().path().toString(), 
//				TrackType.TIMED_DATA
//			);
//			myListener.onTimedEvent(myEvent);
//		}
	}
	
	
}
