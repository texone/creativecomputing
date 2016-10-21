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
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint.TimedEventPointContent;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.ToolController;
import cc.creativecomputing.controlui.timeline.controller.actions.AddControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveEventAction;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class EventTrackController extends TrackController {
	
	public final static float MIN_EVENT_TIME = 0.0001f;
	
	private static enum EventAction{
		DRAG_START, 
		DRAG_START_OFFSET,
		DRAG_END, 
		DRAG_END_OFFSET,
		DRAG_BLOCK, 
		DRAG_CONTENT
	}
	
	private EventAction _myDragAction = EventAction.DRAG_START_OFFSET;
	
	private boolean _mySplitDrag = false;
	
	private CCListenerManager<EventTrackListener> _myEventTrackListener = CCListenerManager.create(EventTrackListener.class);

	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	@SuppressWarnings({ "rawtypes" })
	public EventTrackController(
		TimelineController theTimelineController,
		ToolController theToolController,
		Track theTrack, 
		GroupTrackController theParent
	) {
		super(theTimelineController, theToolController, theTrack, theParent);
		if(theTrack.property() == null)return;
		
		theTrack.property().events().add(new CCPropertyListener() {

			@Override
			public void onChange(Object theValue) {
				if(_myEditedEvent == null)return;
				if(!_myEditedEvent.isSelected())return;
				
				_myEditedEvent.content(new TimedEventPointContent(theValue));
				_myTrackView.render();
			}
		});
	}
	
	public void splitDrag(boolean theSplitDrag){
		_mySplitDrag = theSplitDrag;
	}
	
	public CCListenerManager<EventTrackListener> events(){
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
		TimedEventPoint myPoint = (TimedEventPoint)createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		_myEventTrackListener.proxy().onCreate(this, myPoint);
		UndoHistory.instance().apply(new AddControlPointAction(this, myPoint));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public TimedEventPoint createPointImpl(ControlPoint theCurveCoords) {
		double myBlend = theCurveCoords.value();
		myBlend = CCMath.round(myBlend);

		TimedEventPoint myStartPoint = new TimedEventPoint(theCurveCoords.time(), myBlend);
		return myStartPoint;
	}
	
	@Override
	protected TimedEventPoint createPoint(Point2D theViewCoords) {
        ControlPoint myControlPoint = viewToCurveSpace(theViewCoords, true);
        TimedEventPoint myEventPoint = createPointImpl(myControlPoint);
        	
        trackData().add(myEventPoint);
        
        double myViewTime = _myTrackContext.zoomController().upperBound() - _myTrackContext.zoomController().lowerBound();
		myViewTime /= 10;
		
		double myEventEndTime = myEventPoint.time() + myViewTime;
		
		ControlPoint myHigherPoint = myEventPoint.getNext();
		if(myHigherPoint != null) {
			myEventEndTime = Math.min(myHigherPoint.time(), myEventEndTime);
		}
		
		HandleControlPoint myEndPoint = new HandleControlPoint(
			myEventPoint, 
			HandleType.TIME_END, 
			myEventEndTime, 
			1.0
		);
		myEventPoint.endPoint(myEndPoint);
        
        _myHasAdd = true;
        _myTrackView.render();
        return myEventPoint;
    }
	
	public void writeValue(double theTime){
		ControlPoint myControlPoint = new ControlPoint(theTime, _myProperty.normalizedValue());
		TimedEventPoint myEventPoint = createPointImpl(myControlPoint);
    	
        trackData().add(myEventPoint);
        
        double myViewTime = _myTrackContext.zoomController().upperBound() - _myTrackContext.zoomController().lowerBound();
		myViewTime /= 10;
		
		double myEventEndTime = myEventPoint.time() + myViewTime;
		
		ControlPoint myHigherPoint = myEventPoint.getNext();
		if(myHigherPoint != null) {
			myEventEndTime = Math.min(myHigherPoint.time(), myEventEndTime);
		}
		
		HandleControlPoint myEndPoint = new HandleControlPoint(
			myEventPoint, 
			HandleType.TIME_END, 
			myEventEndTime, 
			1.0
		);
		myEventPoint.endPoint(myEndPoint);
		myEventPoint.content(new TimedEventPointContent(_myProperty.value()));
		
        
        _myHasAdd = true;
        _myTrackView.render();
	}
	
	private void dragStart(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		TimedEventPoint myTimedEvent = (TimedEventPoint) theDraggedPoint;
		
		double myTime = _myTrackContext.quantize(theMousePoint).time();
		HandleControlPoint myEnd = myTimedEvent.endPoint();
		myTime = Math.min(myEnd.time() - MIN_EVENT_TIME, myTime);

		TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint.getPrevious();
		if(myLowerPoint != null) {
			if(myTime < myLowerPoint.endTime())myLowerPoint.endTime(myTime);
			if(myTime < myLowerPoint.time())trackData().remove(myLowerPoint);
		}
		theMousePoint.time(myTime);
		trackData().move(theDraggedPoint, theMousePoint);
	}
	
	private void dragEndHandle(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
		ControlPoint myStart = myControlPoint.parent();
		
		double myTime = Math.max(myStart.time() + MIN_EVENT_TIME, theMousePoint.time());

		TimedEventPoint myHigherPoint = (TimedEventPoint)myStart.getNext();
		if(myHigherPoint != null) {
			if(myTime > myHigherPoint.time())myHigherPoint.time(myTime);
			if(myTime > myHigherPoint.endTime())trackData().remove(myHigherPoint);
		}
		
        theDraggedPoint.time(myTime);
		theDraggedPoint.value(theMousePoint.value());
	}
	
	private boolean dragBlock(ControlPoint theDraggedPoint, ControlPoint theMovement){
		if(_myStartPoints == null)return false;
		if(_myCurveCoords == null)return false;
		
		TimedEventPoint myTimedEvent = (TimedEventPoint) theDraggedPoint;
		
		double myMove = theMovement.time();
		ControlPoint myMovedTarget = new ControlPoint(_myStartPoints.get(0).time() + myMove, 1.0);
		double myEndOffset = myTimedEvent.endPoint().time() - myTimedEvent.time();

		double myTime = _myTrackContext.quantize(myMovedTarget).time();
		TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint.getPrevious();
		if(myLowerPoint != null) {
			myTime = Math.max(myLowerPoint.endTime(), myTime);
		}
		ControlPoint myHigherPoint = theDraggedPoint.getNext();
		if(myHigherPoint != null) {
			myTime = Math.min(myHigherPoint.time(), myTime + myEndOffset) - myEndOffset;
		}
		myTime = Math.max(0, myTime);
		
		myMovedTarget.time(myTime);
		
		trackData().move(theDraggedPoint, myMovedTarget);
		
		myTimedEvent.endPoint().time(myTimedEvent.time() + myEndOffset);
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
	 */
	@Override
	public void dragPointImp(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift) {
		ControlPoint myMousePoint = _myTrackContext.quantize(myTargetPosition);
			
		switch(_myDragAction){
		case DRAG_BLOCK:
			if(!dragBlock(theDraggedPoint, theMovement))return;
			break;
		case DRAG_START:
			dragStart(theDraggedPoint, myMousePoint);
			break;
		case DRAG_END:
			dragEndHandle(theDraggedPoint, myMousePoint);
			break;
		case DRAG_START_OFFSET:
			dragStart(theDraggedPoint, myMousePoint);
			if(theDraggedPoint != null && theDraggedPoint instanceof TimedEventPoint){
				TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint;
				double myTime = _myTrackContext.quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset - myTime);
			}
			break;
		case DRAG_END_OFFSET:
			dragEndHandle(theDraggedPoint, myMousePoint);
			if(theDraggedPoint != null && theDraggedPoint instanceof HandleControlPoint){

				HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
				TimedEventPoint myLowerPoint = (TimedEventPoint)(myControlPoint.parent());
				double myTime = _myTrackContext.quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		case DRAG_CONTENT:
			if(theDraggedPoint != null && theDraggedPoint instanceof TimedEventPoint){
				TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint;
				double myTime = _myTrackContext.quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		}
		_myEventTrackListener.proxy().onChange(this, _myEditedEvent);
	}
	
	public TimedEventPoint pointAt(double theTime) {
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
	
	private TimedEventPoint _myEditedEvent = null;
	private double _myStartEnd;
	private ControlPoint _myCurveCoords;
	
	private double _myLastOffset = 0;
	
	@Override
	public void mousePressed(MouseEvent e) {
		_myMouseStartX = e.getX();
		_myMouseStartY = e.getY();
				
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		_myCurveCoords = viewToCurveSpace(myViewCoords, true);
		
		boolean mySwitchAction = _mySplitDrag && _myCurveCoords.value() > 0.5;
			 
		if (e.isAltDown()) {
			_myTrackContext.zoomController().startDrag(myViewCoords);
			return;
		}
		
		ControlPoint myControlPoint = pickNearestPoint(myViewCoords);
		HandleControlPoint myHandle = pickHandle(myViewCoords);
		
		_myEditedEvent = null;
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myHandle);
			_myEditedEvent = (TimedEventPoint)myHandle.parent();
			if(!(_mySplitDrag && _myCurveCoords.value() < 0.5)){
				_myDragAction = EventAction.DRAG_END;
			}else{
				_myDragAction = EventAction.DRAG_END_OFFSET;
			}
		} else if (myControlPoint != null  && distance(myControlPoint, myViewCoords) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			_myEditedEvent = (TimedEventPoint)myControlPoint;
			if(!mySwitchAction){
				_myDragAction = EventAction.DRAG_START;
			}else{
				_myDragAction = EventAction.DRAG_START_OFFSET;
			}
		} else {
			
			TimedEventPoint myLower = (TimedEventPoint)trackData().lower(_myCurveCoords);

			if(myLower != null) {
				ControlPoint myUpper = myLower.endPoint();
				ControlPoint myCoords = viewToCurveSpace(myViewCoords, true);
				if(myCoords.time() > myLower.time() && myCoords.time() < myUpper.time()) {
					_myDraggedPoints = new ArrayList<ControlPoint>();
					_myDraggedPoints.add(myLower);
					_myEditedEvent = (TimedEventPoint)myLower;
					if(!mySwitchAction){
						_myDragAction = EventAction.DRAG_BLOCK;
					}else{
						_myLastOffset = myLower.contentOffset();
						_myDragAction = EventAction.DRAG_CONTENT;
					}
					
				}
			}
//			if(!_myDragBlock) {
//				_myAddedNewPoint = true;
//				_myDraggedPoint = createPoint(myViewCoords);
//			}
			
		}
		
		if(_myEditedEvent != null) {
			_myStartEnd = _myEditedEvent.endTime();
		}
		if(_myDraggedPoints != null) {
			 _myStartPoints = new ArrayList<ControlPoint>();
			 for(ControlPoint myDraggedPoint:_myDraggedPoints){
				 _myStartPoints.add(myDraggedPoint.clone());
			 }
       }
		
	}
	
	public TimedEventPoint editedEvent(){
		return _myEditedEvent;
	}
	
	public void mouseReleased(MouseEvent e) {
		if(_myEditedEvent != null) {
			_myEventTrackListener.proxy().onChange(this, _myEditedEvent);
			
		}else{
			for(ControlPoint myEvent:trackData()){
				((TimedEventPoint)myEvent).isSelected(false);
			}
			_myTrack.property().endEdit();
			_myTrack.property().restorePreset();
	        _myTrackView.render();
		}
		if (e.isAltDown()) {
			_myTrackContext.zoomController().endDrag();
			return;
		}
		
		if (e.getX() == _myMouseStartX && e.getY() == _myMouseStartY && !_myAddedNewPoint) {
			_myEventTrackListener.proxy().onClick(this, _myEditedEvent);
			
			if(_myEditedEvent != null){
				_myTrack.property().valueCasted(_myEditedEvent.content() == null ? null : _myEditedEvent.content().value(), false);
				_myEditedEvent.isSelected(!_myEditedEvent.isSelected());
				_myTrack.property().beginEdit();
		        _myTrackView.render();
			}else{
				if(!e.isMetaDown()){
					for(ControlPoint myEvent:trackData()){
						((TimedEventPoint)myEvent).isSelected(false);
					}
				}
			}
		}
		
		if(_myDraggedPoints != null && _myEditedEvent != null) {
			UndoHistory.instance().apply(new MoveEventAction(
				this, _myEditedEvent, 
				_myStartPoints.get(0),  
				_myEditedEvent,
				_myStartEnd,
				_myEditedEvent.endTime()));
		}
        _myDraggedPoints = null;
		_myAddedNewPoint = false;
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
