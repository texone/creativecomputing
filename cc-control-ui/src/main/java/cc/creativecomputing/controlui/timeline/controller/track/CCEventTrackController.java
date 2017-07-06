/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.controlui.timeline.controller.track;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.actions.AddControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.tools.CCEventTrackTool;
import cc.creativecomputing.controlui.timeline.controller.tools.CCEventTrackTool.EventAction;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.events.CCListenerManager;

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
		TimelineController theTimelineController,
		Track theTrack, 
		CCGroupTrackController theParent
	) {
		super(theTimelineController, theTrack, theParent);
		if(theTrack.property() == null)return;
		
		theTrack.property().events().add(new CCPropertyListener() {

			@Override
			public void onChange(Object theValue) {
				_myEventTrackTool.editValue(theValue);
			}
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
	
	public void delete(TimedEventPoint theEvent) {
		trackData().remove(theEvent);
		_myEventTrackListener.proxy().onDelete(this, theEvent);
	}
	
	public void properties(TimedEventPoint theEvent) {
		_myEventTrackListener.proxy().onProperties(this, theEvent);
	}
	
	public void renderTimedEvent(TimedEventPoint theTimedEvent, Point2D theLower, Point2D theUpper, double lowerTime, double upperTime, Graphics2D theG2d){
		_myEventTrackListener.proxy().renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, upperTime, theG2d);
	}
	
	public void createPoint(MouseEvent theEvent, String theEventType) {
		Point2D myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		TimedEventPoint myPoint = _myEventTrackTool.createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		_myEventTrackListener.proxy().onCreate(this, myPoint);
		UndoHistory.instance().apply(new AddControlPointAction(this, myPoint));
	}
	
	
	public void writeValue(double theTime){
		ControlPoint myControlPoint = new ControlPoint(theTime, _myProperty.normalizedValue());
		_myEventTrackTool.createPoint(myControlPoint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
	 */
	
	
	private TimedEventPoint pointAt(double theTime) {
		ControlPoint myCurveCoords = new ControlPoint(theTime, 0);
		TimedEventPoint myLower = (TimedEventPoint)trackData().lower(myCurveCoords);
		if(myLower == null) return null;
		ControlPoint myUpper = myLower.endPoint();
		if(myUpper == null) return null;
		if(myCurveCoords.time() > myLower.time() && myCurveCoords.time() < myUpper.time()) {
			return myLower;
		}
		return null;
	}
	
	public TimedEventPoint clickedPoint(MouseEvent e) {
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		ControlPoint myCurveCoords = viewToCurveSpace(myViewCoords, true);
		return pointAt(myCurveCoords.time());
	}
	
	public TimedEventPoint editedEvent(){
		return _myEventTrackTool.editedEvent();
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		TimedEventPoint myEventPoint = pointAt(theTime);
		
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
