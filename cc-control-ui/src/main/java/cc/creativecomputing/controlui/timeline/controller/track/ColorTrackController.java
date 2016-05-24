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
import cc.creativecomputing.control.timeline.point.ColorPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.ToolController;
import cc.creativecomputing.controlui.timeline.controller.actions.AddControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveColorPointAction;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class ColorTrackController extends TrackController {
	
	public final static float MIN_EVENT_TIME = 0.0001f;
	
	private static enum EventAction{
		DRAG_START, 
		DRAG_END, 
		DRAG_BLOCK
	}
	
	private EventAction _myDragAction = EventAction.DRAG_START;
	
	private CCListenerManager<ColorTrackListener> _myColorTrackListener = CCListenerManager.create(ColorTrackListener.class);

	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	@SuppressWarnings({ "rawtypes" })
	public ColorTrackController(
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

				if(_myEditedColor != null && _myEditedColor.isSelected()){
					_myEditedColor.color((CCColor)theValue);
			        _myTrackView.render();
				}
			}
		});
	}
	
	public void splitDrag(boolean theSplitDrag){
	}
	
	public CCListenerManager<ColorTrackListener> events(){
		return _myColorTrackListener;
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
	
	public void delete(ColorPoint theEvent) {
		_myColorTrackListener.proxy().onDelete(this, theEvent);
	}
	
	public void properties(ColorPoint theEvent) {
		_myColorTrackListener.proxy().onProperties(this, theEvent);
	}
	
	public void renderTimedEvent(ColorPoint theTimedEvent, Point2D theLower, Point2D theUpper, double lowerTime, double upperTime, Graphics2D theG2d){
		_myColorTrackListener.proxy().renderTimedEvent(theTimedEvent, theLower, theUpper, lowerTime, upperTime, theG2d);
	}
	
	public void createPoint(MouseEvent theEvent, String theEventType) {
		Point2D myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		ColorPoint myPoint = (ColorPoint)createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		_myColorTrackListener.proxy().onCreate(this, myPoint);
		UndoHistory.instance().apply(new AddControlPointAction(this, myPoint));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public ColorPoint createPointImpl(ControlPoint theCurveCoords) {
		double myBlend = theCurveCoords.value();
		myBlend = CCMath.round(myBlend);

		ColorPoint myStartPoint = new ColorPoint(theCurveCoords.time(), myBlend);
		return myStartPoint;
	}
	
	@Override
	protected ColorPoint createPoint(Point2D theViewCoords) {
        ControlPoint myControlPoint = viewToCurveSpace(theViewCoords, true);
        ColorPoint myEventPoint = createPointImpl(myControlPoint);
        	
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
		ColorPoint myEventPoint = createPointImpl(myControlPoint);
    	
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
		myEventPoint.color((CCColor)_myProperty.value());
		
        
        _myHasAdd = true;
        _myTrackView.render();
	}
	
	private void dragStart(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		ColorPoint myTimedEvent = (ColorPoint) theDraggedPoint;
		
		double myTime = _myTrackContext.quantize(theMousePoint).time();

		ColorPoint myPrev = (ColorPoint)theDraggedPoint.getPrevious();
		if(myPrev != null) {
			myTime = Math.max(myPrev.time(), myTime);
			myPrev.endTime(myPrev.time() + (theDraggedPoint.time() - myPrev.time()) * _myPrevRelation);
		}
		
		ControlPoint myPost = theDraggedPoint.getNext();
		if(myPost != null) {
			myTime = Math.min(myPost.time() - MIN_EVENT_TIME, myTime);
			myTimedEvent.endTime(myTimedEvent.time() + (myPost.time() - myTimedEvent.time()) * _myPostRelation);
		}
			
		theMousePoint.time(myTime);
		trackData().move(theDraggedPoint, theMousePoint);
	}
	
	private void dragEndHandle(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
		ControlPoint myStart = myControlPoint.parent();
		
		double myTime = Math.max(myStart.time() + MIN_EVENT_TIME, theMousePoint.time());

		ControlPoint myHigherPoint = myStart.getNext();
		if(myHigherPoint != null) {
			myTime = Math.min(myHigherPoint.time(), myTime);
		}
		
        theDraggedPoint.time(myTime);
		theDraggedPoint.value(theMousePoint.value());
	}
	
	private boolean dragBlock(ControlPoint theDraggedPoint, ControlPoint theMovement){
		if(_myStartPoints == null)return false;
		if(_myCurveCoords == null)return false;
		
		ColorPoint myTimedEvent = (ColorPoint) theDraggedPoint;
		
		double myMove = theMovement.time();
		ControlPoint myMovedTarget = new ControlPoint(_myStartPoints.get(0).time() + myMove, 1.0);
		double myEndOffset = myTimedEvent.endPoint().time() - myTimedEvent.time();

		double myTime = _myTrackContext.quantize(myMovedTarget).time();
		ColorPoint myLowerPoint = (ColorPoint)theDraggedPoint.getPrevious();
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
		}
		_myColorTrackListener.proxy().onChange(this, _myEditedColor);
	}
	
	public ColorPoint pointAt(double theTime) {
		ControlPoint myCurveCoords = new ControlPoint(theTime, 0);
		ColorPoint myLower = (ColorPoint)trackData().lower(myCurveCoords);
		ColorPoint myCeiling = (ColorPoint)trackData().ceiling(myCurveCoords);
		if(myLower == null && myCeiling == null) {
			return null;
		}
		
		if(myLower == null) return myCeiling;
		return myCeiling;
	}
	
	public ColorPoint clickedPoint(MouseEvent e) {
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		ControlPoint myCurveCoords = viewToCurveSpace(myViewCoords, true);
		return pointAt(myCurveCoords.time());
	}
	
	private ColorPoint _myEditedColor = null;
	private double _myStartEnd;
	private ControlPoint _myCurveCoords;
	
	private double _myPrevRelation = 0.5;
	private double _myPostRelation = 0.5;
	
	@Override
	public void mousePressed(MouseEvent e) {
		_myMouseStartX = e.getX();
		_myMouseStartY = e.getY();
				
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		if (e.isAltDown()) {
			_myTrackContext.zoomController().startDrag(myViewCoords);
			return;
		}
		
		_myCurveCoords = viewToCurveSpace(myViewCoords, true);
			 
		ControlPoint myControlPoint = pickNearestPoint(myViewCoords);
		HandleControlPoint myHandle = pickHandle(myViewCoords);
		
		_myEditedColor = null;
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myHandle);
			_myDragAction = EventAction.DRAG_END;
		} else if (myControlPoint != null  && distance(myControlPoint, myViewCoords) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			_myEditedColor = (ColorPoint)myControlPoint;
			if(myControlPoint.hasPrevious()){
				ColorPoint myPrev = (ColorPoint)myControlPoint.getPrevious();
				_myPrevRelation = (myPrev.endTime() - myPrev.time()) / (myControlPoint.time() - myPrev.time());
			}
			if(myControlPoint.hasNext()){
				ColorPoint myPost = (ColorPoint)myControlPoint.getNext();
				_myPostRelation = (_myEditedColor.endTime() - _myEditedColor.time()) / (myPost.time() - _myEditedColor.time());
			}
			_myDragAction = EventAction.DRAG_START;
		} else {
			
			

//			if(myLower != null) {
//				ControlPoint myUpper = myLower.endPoint();
//				ControlPoint myCoords = viewToCurveSpace(myViewCoords, true);
//				
//				if(myCoords.time() > myLower.time() && myCoords.time() < myUpper.time()) {
//					_myDraggedPoints = new ArrayList<ControlPoint>();
//					_myDraggedPoints.add(myLower);
//					_myEditedColor = (ColorPoint)myLower;
//					_myDragAction = EventAction.DRAG_BLOCK;
//				}
//			} else {
				ControlPoint myClick = viewToCurveSpace(myViewCoords, true);
				ControlPoint myFloor = trackData().floor(myClick);
				ControlPoint myCeil = trackData().ceiling(myClick);
				
				if(e.getClickCount() == 2){
					_myAddedNewPoint = true;
					_myDraggedPoints = new ArrayList<ControlPoint>();
					_myDraggedPoints.add(createPoint(myViewCoords));
				}else{
					if(myFloor != null && myCeil != null){	
						_myDraggedPoints = new ArrayList<ControlPoint>();
						_myDraggedPoints.add(myFloor);
						_myDraggedPoints.add(myCeil);
					}
				}
//			}
			
		}
		
		if(_myEditedColor != null) {
			_myStartEnd = _myEditedColor.endTime();
		}
		if(_myDraggedPoints != null) {
			 _myStartPoints = new ArrayList<ControlPoint>();
			 for(ControlPoint myDraggedPoint:_myDraggedPoints){
				 _myStartPoints.add(myDraggedPoint.clone());
			 }
       }
		
	}
	
	public void mouseReleased(MouseEvent e) {
		if(_myEditedColor != null) {
			_myColorTrackListener.proxy().onChange(this, _myEditedColor);
			
		}else{
			for(ControlPoint myEvent:trackData()){
				((ColorPoint)myEvent).isSelected(false);
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
			_myColorTrackListener.proxy().onClick(this, _myEditedColor);
			if(!e.isMetaDown()){
				for(ControlPoint myEvent:trackData()){
					((ColorPoint)myEvent).isSelected(false);
				}
			}
			if(_myEditedColor != null){
				_myTrack.property().valueCasted(_myEditedColor.color() == null ? null : _myEditedColor.color().clone(), false);
				_myEditedColor.isSelected(true);
				_myTrack.property().beginEdit();
		        _myTrackView.render();
			}
		}
		
		if(_myDraggedPoints != null && _myEditedColor != null) {
			UndoHistory.instance().apply(new MoveColorPointAction(
				this, _myEditedColor, 
				_myStartPoints.get(0),  
				_myEditedColor,
				_myStartEnd,
				_myEditedColor.endTime()));
		}
        _myDraggedPoints = null;
		_myAddedNewPoint = false;
	}
	

	public CCColor color(double theTime){
		if (trackData().size() == 0) {
			return new CCColor();
		}
		
		ControlPoint mySample = trackData().createSamplePoint(theTime);
		ColorPoint myLower = (ColorPoint)trackData().lower(mySample);
		ColorPoint myCeiling = (ColorPoint)trackData().ceiling(mySample);

		if (myLower == null && myCeiling == null) {
			return new CCColor();
		}
		
		if (myLower == null && myCeiling != null) {
			return myCeiling.color().clone();
		}
		
		if(myCeiling == null && myLower != null){
			return myLower.color().clone();
		}

		myLower = (ColorPoint)trackData().getLastOnSamePosition(myLower);

		double myBlend = (theTime - myLower.time()) / (myCeiling.time() - myLower.time());
		double myPow = (myLower.endTime() - myLower.time()) / (myCeiling.time() - myLower.time());
		if(myPow > 0.5)myPow = 1 / CCMath.blend(1, 0, (myPow - 0.5) * 2);
		else myPow = CCMath.blend(0, 1, (myPow) * 2);
		return CCColor.blend(myLower.color(), myCeiling.color(),CCMath.pow(myBlend, myPow) );
		
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		ColorPoint myEventPoint = pointAt(theTime);
		
    	if(myEventPoint == null){
    		_myTrack.property().restorePreset();
    		_myColorTrackListener.proxy().onOut();
    		return;
    	}

    	_myTrack.property().valueCasted(color(theTime), false);
    	_myColorTrackListener.proxy().onTime(theTime, this, myEventPoint);

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
