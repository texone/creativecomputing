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
package cc.creativecomputing.controlui.timeline.controller.tools;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCHandleType;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint.TimedData;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveEventAction;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.CCControlUndoHistory;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCEventTrackTool extends CCTimelineTool<CCEventTrackController>{

	private final static float MIN_EVENT_TIME = 0.0001f;
	
	public enum EventAction{
		DRAG_START, 
		DRAG_START_OFFSET,
		DRAG_END, 
		DRAG_END_OFFSET,
		DRAG_BLOCK, 
		DRAG_CONTENT
	}

	protected boolean _myAddedNewPoint = false;
    
	protected boolean _myIsInDrag = false;
	
	private EventAction _myDragAction = EventAction.DRAG_START_OFFSET;
	
	private boolean _mySplitDrag = false;

	private CCTimedEventPoint _myEditedEvent = null;
    protected List<CCControlPoint> _myDraggedPoints;
    protected List<CCControlPoint> _myStartPoints;

	private double _myStartEnd;
	
	private double _myLastOffset = 0;
	
	public CCEventTrackTool(CCEventTrackController theController) {
		super(false, theController);
	}
	
	public void splitDrag(boolean theSplitDrag){
		_mySplitDrag = theSplitDrag;
	}
	
	public void editValue(Object theValue){
		if(_myEditedEvent == null)return;
		if(!_myEditedEvent.isSelected())return;
		
		_myEditedEvent.content(new TimedData(theValue));
		_myController.view().render();
	}
	
	public CCTimedEventPoint createPoint(CCVector2 theViewCoords){
		return createPoint(_myController.viewToCurveSpace(theViewCoords, true));
	}
	
	public void setEndPoint(CCTimedEventPoint thePoint){
		double myViewTime = _myController.context().zoomController().upperBound() - _myController.context().zoomController().lowerBound();
		myViewTime /= 10;
		
		double myEventEndTime = thePoint.time() + myViewTime;
		
		CCControlPoint myHigherPoint = thePoint.next();
		if(myHigherPoint != null) {
			myEventEndTime = Math.min(myHigherPoint.time(), myEventEndTime);
		}
		
		CCHandleControlPoint myEndPoint = new CCHandleControlPoint(
				thePoint, 
			CCHandleType.TIME_END, 
			myEventEndTime, 
			1.0
		);
		thePoint.endPoint(myEndPoint);
	}
	
	public CCTimedEventPoint createPoint(CCControlPoint theCurveCoords) {
		double myBlend = theCurveCoords.value();
		myBlend = CCMath.round(myBlend);

		CCTimedEventPoint myEventPoint = new CCTimedEventPoint(theCurveCoords.time(), myBlend);
		myEventPoint.content(new TimedData(_myController.property().value()));
        _myController.trackData().add(myEventPoint);
        
        setEndPoint(myEventPoint);
        
        _myController.view().render();
        return myEventPoint;
    }

	public CCTimedEventPoint editedEvent(){
		return _myEditedEvent;
	}

	@Override
	public void mousePressed(CCGLMouseEvent theEvent) {
		super.mousePressed(theEvent);
		
		boolean mySwitchAction = _mySplitDrag && _myPressCurveCoords.value() > 0.5;
			 
		CCControlPoint myControlPoint = _myController.pickNearestPoint(_myPressViewCoords);
		CCHandleControlPoint myHandle = _myController.pickHandle(_myPressViewCoords);
		
		_myEditedEvent = null;
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<CCControlPoint>();
			_myDraggedPoints.add(myHandle);
			_myEditedEvent = (CCTimedEventPoint)myHandle.parent();
			if(!(_mySplitDrag && _myCurveCoords.value() < 0.5)){
				_myDragAction = EventAction.DRAG_END;
			}else{
				_myDragAction = EventAction.DRAG_END_OFFSET;
			}
		} else if (myControlPoint != null  && Math.abs(_myController.curveToViewSpace(myControlPoint).x - _myPressViewCoords.x) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<CCControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			_myEditedEvent = (CCTimedEventPoint)myControlPoint;
			if(!mySwitchAction){
				_myDragAction = EventAction.DRAG_START;
			}else{
				_myDragAction = EventAction.DRAG_START_OFFSET;
			}
		} else {
			
			CCTimedEventPoint myLower = (CCTimedEventPoint)_myController.trackData().lower(_myCurveCoords);

			if(myLower != null) {
				CCControlPoint myUpper = myLower.endPoint();
				if(_myPressCurveCoords.time() > myLower.time() && _myPressCurveCoords.time() < myUpper.time()) {
					_myDraggedPoints = new ArrayList<CCControlPoint>();
					_myDraggedPoints.add(myLower);
					_myEditedEvent = myLower;
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
			 _myStartPoints = new ArrayList<CCControlPoint>();
			 for(CCControlPoint myDraggedPoint:_myDraggedPoints){
				 _myStartPoints.add(myDraggedPoint.clone());
			 }
		}
	}
	
	@Override
	public void mouseMoved(CCGLMouseEvent theEvent) {
		super.mouseMoved(theEvent);
		_myDragAction = null;
				
		
		boolean mySwitchAction = _mySplitDrag && _myCurveCoords.value() > 0.5;
		
		CCControlPoint myControlPoint = _myController.pickNearestPoint(_myViewCoords);
		CCHandleControlPoint myHandle = _myController.pickHandle(_myViewCoords);
		
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<CCControlPoint>();
			_myDraggedPoints.add(myHandle);
			if(!(_mySplitDrag && _myCurveCoords.value() < 0.5)){
				_myDragAction = EventAction.DRAG_END;
			}else{
				_myDragAction = EventAction.DRAG_END_OFFSET;
			}
		} else if (myControlPoint != null  && Math.abs(_myController.curveToViewSpace(myControlPoint).x - _myPressViewCoords.x) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<CCControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			if(!mySwitchAction){
				_myDragAction = EventAction.DRAG_START;
			}else{
				_myDragAction = EventAction.DRAG_START_OFFSET;
			}
		} else {
			
			CCTimedEventPoint myLower = (CCTimedEventPoint)_myController.trackData().lower(_myCurveCoords);

			if(myLower != null) {
				CCControlPoint myUpper = myLower.endPoint();
				CCControlPoint myCoords = _myController.viewToCurveSpace(_myViewCoords, true);
				if(myCoords.time() > myLower.time() && myCoords.time() < myUpper.time()) {
					_myDraggedPoints = new ArrayList<CCControlPoint>();
					_myDraggedPoints.add(myLower);
					if(!mySwitchAction){
						_myDragAction = EventAction.DRAG_BLOCK;
					}else{
						_myDragAction = EventAction.DRAG_CONTENT;
					}
					
				}
			}
			
		}
	}
	
	private boolean dragBlock(CCControlPoint theDraggedPoint, CCControlPoint theMovement){
		if(_myStartPoints == null)return false;
		if(_myCurveCoords == null)return false;
		
		CCTimedEventPoint myTimedEvent = (CCTimedEventPoint) theDraggedPoint;
		
		double myMove = theMovement.time();
		CCControlPoint myMovedTarget = new CCControlPoint(_myStartPoints.get(0).time() + myMove, 1.0);
		double myEndOffset = myTimedEvent.endPoint().time() - myTimedEvent.time();

		double myTime = _myController.context().quantize(myMovedTarget).time();
		CCTimedEventPoint myLowerPoint = (CCTimedEventPoint)theDraggedPoint.previous();
		if(myLowerPoint != null) {
			myTime = Math.max(myLowerPoint.endTime(), myTime);
		}
		CCControlPoint myHigherPoint = theDraggedPoint.next();
		if(myHigherPoint != null) {
			myTime = Math.min(myHigherPoint.time(), myTime + myEndOffset) - myEndOffset;
		}
		myTime = Math.max(0, myTime);
		
		myMovedTarget.time(myTime);
		
		_myController.trackData().move(theDraggedPoint, myMovedTarget);
		
		myTimedEvent.endPoint().time(myTimedEvent.time() + myEndOffset);
		
		return true;
	}
	
	private void dragStart(CCControlPoint theDraggedPoint, CCControlPoint theMousePoint){
		CCTimedEventPoint myTimedEvent = (CCTimedEventPoint) theDraggedPoint;
		
		double myTime = _myController.context().quantize(theMousePoint).time();
		CCHandleControlPoint myEnd = myTimedEvent.endPoint();
		myTime = Math.min(myEnd.time() - MIN_EVENT_TIME, myTime);

		CCTimedEventPoint myLowerPoint = (CCTimedEventPoint)theDraggedPoint.previous();
		if(myLowerPoint != null) {
			if(myTime < myLowerPoint.endTime())myLowerPoint.endTime(myTime);
			if(myTime < myLowerPoint.time())_myController.trackData().remove(myLowerPoint);
		}
		theMousePoint.time(myTime);
		_myController.trackData().move(theDraggedPoint, theMousePoint);
	}
	
	private void dragEndHandle(CCControlPoint theDraggedPoint, CCControlPoint theMousePoint){
		CCHandleControlPoint myControlPoint = (CCHandleControlPoint)theDraggedPoint;
		CCControlPoint myStart = myControlPoint.parent();
		
		double myTime = Math.max(myStart.time() + MIN_EVENT_TIME, theMousePoint.time());

		CCTimedEventPoint myHigherPoint = (CCTimedEventPoint)myStart.next();
		if(myHigherPoint != null) {
			if(myTime > myHigherPoint.time())myHigherPoint.time(myTime);
			if(myTime > myHigherPoint.endTime())_myController.trackData().remove(myHigherPoint);
		}
		
        theDraggedPoint.time(myTime);
		theDraggedPoint.value(theMousePoint.value());
	}
	
	private void dragPoint(CCControlPoint theDraggedPoint, CCControlPoint myTargetPosition, CCControlPoint theMovement, boolean theIsPressedShift) {
		CCControlPoint myMousePoint = _myController.context().quantize(myTargetPosition);
			
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
			if(theDraggedPoint != null && theDraggedPoint instanceof CCTimedEventPoint){
				CCTimedEventPoint myLowerPoint = (CCTimedEventPoint)theDraggedPoint;
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset - myTime);
			}
			break;
		case DRAG_END_OFFSET:
			dragEndHandle(theDraggedPoint, myMousePoint);
			if(theDraggedPoint != null && theDraggedPoint instanceof CCHandleControlPoint){

				CCHandleControlPoint myControlPoint = (CCHandleControlPoint)theDraggedPoint;
				CCTimedEventPoint myLowerPoint = (CCTimedEventPoint)(myControlPoint.parent());
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		case DRAG_CONTENT:
			if(theDraggedPoint != null && theDraggedPoint instanceof CCTimedEventPoint){
				CCTimedEventPoint myLowerPoint = (CCTimedEventPoint)theDraggedPoint;
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		}
		_myController.events().proxy().onChange(_myController, _myEditedEvent);
	}
	
	@Override
	public void mouseDragged(CCGLMouseEvent theEvent) {
		super.mouseDragged(theEvent);
		
		if(_myIsInDrag == false){
			if(_myController.selectedPoints().size() > 0){
				_myDraggedPoints.addAll(_myController.selectedPoints());
				_myStartPoints.clear();
				 for(CCControlPoint myDraggedPoint:_myDraggedPoints){
					 _myStartPoints.add(myDraggedPoint.clone());
				 }
			}
		}
		
		_myIsInDrag = true;
		
		if(_myDraggedPoints == null)return;
		if(_myStartPoints == null)return;
			
		for(int i = 0; i < _myDraggedPoints.size();i++){
			CCControlPoint myStartPoint = _myStartPoints.get(i).clone();
			CCControlPoint myTarget = new CCControlPoint(
				myStartPoint.time() + _myCurveMovement.time(), 
				CCMath.saturate(myStartPoint.value() + _myCurveMovement.value())
			);
			CCControlPoint myDraggedPoint = _myDraggedPoints.get(i);
	
			dragPoint(myDraggedPoint, myTarget, _myCurveMovement, theEvent.isShiftDown());        
		}
	}
	
	@Override
	public void mouseReleased(CCGLMouseEvent theEvent) {
		_myDragAction = null;
		if(_myEditedEvent != null) {
			_myController.events().proxy().onChange(_myController, _myEditedEvent);
			
		}else{
			for(CCControlPoint myEvent:_myController.trackData()){
				((CCTimedEventPoint)myEvent).isSelected(false);
			}
			_myController.property().endEdit();
			_myController.property().restorePreset();
			_myController.view().render();
		}
		
		super.mouseReleased(theEvent);
		if (theEvent.isAltDown()) {
			_myController.context().zoomController().endDrag();
			return;
		}
		
		if (theEvent.x == _myPressX && theEvent.y == _myPressY && !_myAddedNewPoint) {
			_myController.events().proxy().onClick(_myController, _myEditedEvent);
			
			if(_myEditedEvent != null){
				_myController.property().valueCasted(_myEditedEvent.content() == null ? null : _myEditedEvent.content().value(), false);
				_myEditedEvent.isSelected(!_myEditedEvent.isSelected());
				_myController.property().beginEdit();
		        _myController.view().render();
			}else{
				if(!theEvent.isSuperDown()){
					for(CCControlPoint myEvent:_myController.trackData()){
						((CCTimedEventPoint)myEvent).isSelected(false);
					}
				}
			}
		}
		
		if(_myDraggedPoints != null && _myEditedEvent != null) {
			CCControlUndoHistory.instance().apply(new MoveEventAction(
                    _myController,
				_myEditedEvent, 
				_myStartPoints.get(0),  
				_myEditedEvent,
				_myStartEnd,
				_myEditedEvent.endTime()));
		}
        _myDraggedPoints = null;
		_myAddedNewPoint = false;
	}

}
