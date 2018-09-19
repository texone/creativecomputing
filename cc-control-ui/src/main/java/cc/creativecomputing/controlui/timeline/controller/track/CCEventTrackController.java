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

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.actions.CCAddControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.actions.CCControlUndoHistory;
import cc.creativecomputing.controlui.timeline.tools.CCEventTrackTool;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;
import cc.creativecomputing.controlui.timeline.tools.CCEventTrackTool.EventAction;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 * 
 */
public class CCEventTrackController extends CCTrackController {
	
	public CCEventManager<CCEventPoint> createEvents = new CCEventManager<>();
	
	public CCEventManager<CCEventPoint> changeEvents = new CCEventManager<>();
	
	public CCEventManager<CCEventPoint> deleteEvents = new CCEventManager<>();
	
	public CCEventManager<CCEventPoint> propertyEvents = new CCEventManager<>();
	
	public CCEventManager<CCEventPoint> clickEvents = new CCEventManager<>();
	
	public static class CCEventTrackTimeEvent{
		public final double time;
		public final CCEventPoint point;
		
		private CCEventTrackTimeEvent(double theTime, CCEventPoint thePoint) {
			time = theTime;
			point = thePoint;
		}
	}
	public CCEventManager<CCEventTrackTimeEvent> timeEvents = new CCEventManager<>();
//	void onTime(double theTime, CCEventTrackController theController, CCEventPoint thePoint);
//	
//	void onTimeChange(double theTime, double theOffset, CCEventTrackController theController, CCEventPoint thePoint);
	
	public CCEventManager<Object> outEvents = new CCEventManager<>();
	
//	void renderTimedEvent(CCEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double UpperTime, CCGraphics theG2d);
	
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
	
	public static final String EVENT_TYPES = "eventTypes";
	
	public List<String> eventTypes(){
		List<String> myResult = new ArrayList<String>();
		String[] myEventTypesArray = _myTrack.extras().get(EVENT_TYPES).split(",");
		for(String myEventType:myEventTypesArray) {
			myResult.add(myEventType);
		}
		return myResult;
	}
	
	public void delete(CCEventPoint theEvent) {
		trackData().remove(theEvent);
		deleteEvents.event(theEvent);
	}
	
	public void properties(CCEventPoint theEvent) {
		propertyEvents.event(theEvent);
	}
	
	public void renderTimedEvent(CCEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double upperTime, CCGraphics theG2d){
		_myEventTrackListener.proxy().renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, upperTime, theG2d);
	}
	
	public void createPoint(CCGLMouseEvent theEvent, String theEventType) {
		CCVector2 myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		CCEventPoint myPoint = _myEventTrackTool.createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		createEvents.event(myPoint);
		CCControlUndoHistory.instance().apply(new CCAddControlPointCommand(_myTrack.trackData(), myPoint));
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
	
	
	private CCEventPoint pointAt(double theTime) {
		CCControlPoint myCurveCoords = new CCControlPoint(theTime, 0);
		CCEventPoint myLower = (CCEventPoint)trackData().lower(myCurveCoords);
		if(myLower == null) return null;
		CCControlPoint myUpper = myLower.endPoint();
		if(myUpper == null) return null;
		if(myCurveCoords.time() > myLower.time() && myCurveCoords.time() < myUpper.time()) {
			return myLower;
		}
		return null;
	}
	
	public CCEventPoint clickedPoint(CCGLMouseEvent e) {
		CCVector2 myViewCoords = new CCVector2(e.x, e.y);
		CCControlPoint myCurveCoords = viewToCurveSpace(myViewCoords, true);
		return pointAt(myCurveCoords.time());
	}
	
	public CCEventPoint editedEvent(){
		return _myEventTrackTool.editedEvent();
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		CCEventPoint myEventPoint = pointAt(theTime);
		
		if(myEventPoint == null || myEventPoint.content() == null || myEventPoint.content().value() == null){
			_myTrack.property().restorePreset();
			outEvents.event();
			return;
		}
	
		_myTrack.property().valueCasted(myEventPoint.content().value(), false);
		timeEvents.event(new CCEventTrackTimeEvent(theTime, myEventPoint));
	}
	
	
}
