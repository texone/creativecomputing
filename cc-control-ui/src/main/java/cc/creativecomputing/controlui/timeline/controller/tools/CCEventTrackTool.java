package cc.creativecomputing.controlui.timeline.controller.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint.TimedData;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveEventAction;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.math.CCMath;

public class CCEventTrackTool extends CCTimelineTool<CCEventTrackController>{

	private final static float MIN_EVENT_TIME = 0.0001f;
	
	public static enum EventAction{
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

	private TimedEventPoint _myEditedEvent = null;
    protected List<ControlPoint> _myDraggedPoints;
    protected List<ControlPoint> _myStartPoints;

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
	
	public TimedEventPoint createPoint(Point2D theViewCoords){
		return createPoint(_myController.viewToCurveSpace(theViewCoords, true));
	}
	
	public void setEndPoint(TimedEventPoint thePoint){
		double myViewTime = _myController.context().zoomController().upperBound() - _myController.context().zoomController().lowerBound();
		myViewTime /= 10;
		
		double myEventEndTime = thePoint.time() + myViewTime;
		
		ControlPoint myHigherPoint = thePoint.getNext();
		if(myHigherPoint != null) {
			myEventEndTime = Math.min(myHigherPoint.time(), myEventEndTime);
		}
		
		HandleControlPoint myEndPoint = new HandleControlPoint(
				thePoint, 
			HandleType.TIME_END, 
			myEventEndTime, 
			1.0
		);
		thePoint.endPoint(myEndPoint);
	}
	
	public TimedEventPoint createPoint(ControlPoint theCurveCoords) {
		double myBlend = theCurveCoords.value();
		myBlend = CCMath.round(myBlend);

		TimedEventPoint myEventPoint = new TimedEventPoint(theCurveCoords.time(), myBlend);
		myEventPoint.content(new TimedData(_myController.property().value()));
        _myController.trackData().add(myEventPoint);
        
        setEndPoint(myEventPoint);
        
        _myController.view().render();
        return myEventPoint;
    }

	public TimedEventPoint editedEvent(){
		return _myEditedEvent;
	}

	@Override
	public void mousePressed(MouseEvent theEvent) {
		super.mousePressed(theEvent);
		
		boolean mySwitchAction = _mySplitDrag && _myPressCurveCoords.value() > 0.5;
			 
		ControlPoint myControlPoint = _myController.pickNearestPoint(_myPressViewCoords);
		HandleControlPoint myHandle = _myController.pickHandle(_myPressViewCoords);
		
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
		} else if (myControlPoint != null  && Math.abs(_myController.curveToViewSpace(myControlPoint).getX() - _myPressViewCoords.getX()) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			_myEditedEvent = (TimedEventPoint)myControlPoint;
			if(!mySwitchAction){
				_myDragAction = EventAction.DRAG_START;
			}else{
				_myDragAction = EventAction.DRAG_START_OFFSET;
			}
		} else {
			
			TimedEventPoint myLower = (TimedEventPoint)_myController.trackData().lower(_myCurveCoords);

			if(myLower != null) {
				ControlPoint myUpper = myLower.endPoint();
				if(_myPressCurveCoords.time() > myLower.time() && _myPressCurveCoords.time() < myUpper.time()) {
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
	
	@Override
	public void mouseMoved(MouseEvent theEvent) {
		super.mouseMoved(theEvent);
		_myDragAction = null;
				
		
		boolean mySwitchAction = _mySplitDrag && _myCurveCoords.value() > 0.5;
		
		ControlPoint myControlPoint = _myController.pickNearestPoint(_myViewCoords);
		HandleControlPoint myHandle = _myController.pickHandle(_myViewCoords);
		
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myHandle);
			if(!(_mySplitDrag && _myCurveCoords.value() < 0.5)){
				_myDragAction = EventAction.DRAG_END;
			}else{
				_myDragAction = EventAction.DRAG_END_OFFSET;
			}
		} else if (myControlPoint != null  && Math.abs(_myController.curveToViewSpace(myControlPoint).getX() - _myPressViewCoords.getX()) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
			if(!mySwitchAction){
				_myDragAction = EventAction.DRAG_START;
			}else{
				_myDragAction = EventAction.DRAG_START_OFFSET;
			}
		} else {
			
			TimedEventPoint myLower = (TimedEventPoint)_myController.trackData().lower(_myCurveCoords);

			if(myLower != null) {
				ControlPoint myUpper = myLower.endPoint();
				ControlPoint myCoords = _myController.viewToCurveSpace(_myViewCoords, true);
				if(myCoords.time() > myLower.time() && myCoords.time() < myUpper.time()) {
					_myDraggedPoints = new ArrayList<ControlPoint>();
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
	
	private boolean dragBlock(ControlPoint theDraggedPoint, ControlPoint theMovement){
		if(_myStartPoints == null)return false;
		if(_myCurveCoords == null)return false;
		
		TimedEventPoint myTimedEvent = (TimedEventPoint) theDraggedPoint;
		
		double myMove = theMovement.time();
		ControlPoint myMovedTarget = new ControlPoint(_myStartPoints.get(0).time() + myMove, 1.0);
		double myEndOffset = myTimedEvent.endPoint().time() - myTimedEvent.time();

		double myTime = _myController.context().quantize(myMovedTarget).time();
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
		
		_myController.trackData().move(theDraggedPoint, myMovedTarget);
		
		myTimedEvent.endPoint().time(myTimedEvent.time() + myEndOffset);
		
		return true;
	}
	
	private void dragStart(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		TimedEventPoint myTimedEvent = (TimedEventPoint) theDraggedPoint;
		
		double myTime = _myController.context().quantize(theMousePoint).time();
		HandleControlPoint myEnd = myTimedEvent.endPoint();
		myTime = Math.min(myEnd.time() - MIN_EVENT_TIME, myTime);

		TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint.getPrevious();
		if(myLowerPoint != null) {
			if(myTime < myLowerPoint.endTime())myLowerPoint.endTime(myTime);
			if(myTime < myLowerPoint.time())_myController.trackData().remove(myLowerPoint);
		}
		theMousePoint.time(myTime);
		_myController.trackData().move(theDraggedPoint, theMousePoint);
	}
	
	private void dragEndHandle(ControlPoint theDraggedPoint, ControlPoint theMousePoint){
		HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
		ControlPoint myStart = myControlPoint.parent();
		
		double myTime = Math.max(myStart.time() + MIN_EVENT_TIME, theMousePoint.time());

		TimedEventPoint myHigherPoint = (TimedEventPoint)myStart.getNext();
		if(myHigherPoint != null) {
			if(myTime > myHigherPoint.time())myHigherPoint.time(myTime);
			if(myTime > myHigherPoint.endTime())_myController.trackData().remove(myHigherPoint);
		}
		
        theDraggedPoint.time(myTime);
		theDraggedPoint.value(theMousePoint.value());
	}
	
	private void dragPoint(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift) {
		ControlPoint myMousePoint = _myController.context().quantize(myTargetPosition);
			
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
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset - myTime);
			}
			break;
		case DRAG_END_OFFSET:
			dragEndHandle(theDraggedPoint, myMousePoint);
			if(theDraggedPoint != null && theDraggedPoint instanceof HandleControlPoint){

				HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
				TimedEventPoint myLowerPoint = (TimedEventPoint)(myControlPoint.parent());
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		case DRAG_CONTENT:
			if(theDraggedPoint != null && theDraggedPoint instanceof TimedEventPoint){
				TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint;
				double myTime = _myController.context().quantize(theMovement.time());
				myLowerPoint.contentOffset(_myLastOffset + myTime);
			}
			break;
		}
		_myController.events().proxy().onChange(_myController, _myEditedEvent);
	}
	
	@Override
	public void mouseDragged(MouseEvent theEvent) {
		super.mouseDragged(theEvent);
		
		if(_myIsInDrag == false){
			if(_myController.selectedPoints().size() > 0){
				_myDraggedPoints.addAll(_myController.selectedPoints());
				_myStartPoints.clear();
				 for(ControlPoint myDraggedPoint:_myDraggedPoints){
					 _myStartPoints.add(myDraggedPoint.clone());
				 }
			}
		}
		
		_myIsInDrag = true;
		
		if(_myDraggedPoints == null)return;
		if(_myStartPoints == null)return;
			
		for(int i = 0; i < _myDraggedPoints.size();i++){
			ControlPoint myStartPoint = _myStartPoints.get(i).clone();
			ControlPoint myTarget = new ControlPoint(
				myStartPoint.time() + _myCurveMovement.time(), 
				CCMath.saturate(myStartPoint.value() + _myCurveMovement.value())
			);
			ControlPoint myDraggedPoint = _myDraggedPoints.get(i);
				
			boolean myPressedShift = (theEvent.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
	
			dragPoint(myDraggedPoint, myTarget, _myCurveMovement, myPressedShift);        
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent theEvent) {
		_myDragAction = null;
		if(_myEditedEvent != null) {
			_myController.events().proxy().onChange(_myController, _myEditedEvent);
			
		}else{
			for(ControlPoint myEvent:_myController.trackData()){
				((TimedEventPoint)myEvent).isSelected(false);
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
		
		if (theEvent.getX() == _myPressX && theEvent.getY() == _myPressY && !_myAddedNewPoint) {
			_myController.events().proxy().onClick(_myController, _myEditedEvent);
			
			if(_myEditedEvent != null){
				_myController.property().valueCasted(_myEditedEvent.content() == null ? null : _myEditedEvent.content().value(), false);
				_myEditedEvent.isSelected(!_myEditedEvent.isSelected());
				_myController.property().beginEdit();
		        _myController.view().render();
			}else{
				if(!theEvent.isMetaDown()){
					for(ControlPoint myEvent:_myController.trackData()){
						((TimedEventPoint)myEvent).isSelected(false);
					}
				}
			}
		}
		
		if(_myDraggedPoints != null && _myEditedEvent != null) {
			UndoHistory.instance().apply(new MoveEventAction(
				(CCEventTrackController)_myController, 
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
