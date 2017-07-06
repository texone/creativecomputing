package cc.creativecomputing.controlui.timeline.controller.tools;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.actions.AddControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.actions.MoveControlPointAction;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.math.CCMath;

public class CCCreateTool extends CCTimelineTool<CCCurveTrackController>{

	protected boolean _myAddedNewPoint = false;

    protected List<ControlPoint> _myDraggedPoints;
    protected List<ControlPoint> _myStartPoints;
    
	protected boolean _myIsInDrag = false;
    
    protected boolean _myHasAdd = false;
    
    protected CCTimelineTools _myTool;
	
    public CCCreateTool(CCCurveTrackController theController) {
		super(true, theController);
		
		_myTool = CCTimelineTools.CREATE_LINEAR_POINT;
	}
    
    public void setTool(CCTimelineTools theTool){
    	_myTool = theTool;
    }
    
    public boolean isInDrag(){
    	return _myIsInDrag;
    }

	public ControlPoint draggedPoint(){
		if(_myDraggedPoints == null || _myDraggedPoints.size() == 0)return null;
		return _myDraggedPoints.get(0);
	}
	
	public ControlPoint createPoint(Point2D theViewCoords) {
		return createPoint(_myController.viewToCurveSpace(theViewCoords, true));
	}
	
	public ControlPoint createPoint(ControlPoint myControlPoint) {
		switch(_myTool) {
    	case CREATE_TRIGGER_POINT:
    		myControlPoint = new StepControlPoint(new ControlPoint(myControlPoint.time(), 0.5));
    		break;
    	case CREATE_STEP_POINT:
    		myControlPoint = new StepControlPoint(myControlPoint);
    		break;
    	case CREATE_LINEAR_POINT:
    		myControlPoint = new LinearControlPoint(myControlPoint);
    		break;
    	case CREATE_BEZIER_POINT:
    		BezierControlPoint myBezierPoint = new BezierControlPoint(myControlPoint);
    		
    		ControlPoint myLower = _myController.trackData().lower(myControlPoint);
    		double myTime;
    		if(myLower == null) {
    			myTime = myControlPoint.time() - 1;
    		}else {
    			myTime = myLower.time() + myControlPoint.time();
    			myTime /= 2;
    		}
    		HandleControlPoint myInHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_IN_HANDLE, myTime, myControlPoint.value());
    		myBezierPoint.inHandle(myInHandle);
    		
    		ControlPoint myHigher = _myController.trackData().higher(myControlPoint);
    		if(myHigher == null) {
    			myTime = myControlPoint.time() + myControlPoint.time() - myTime;
    		}else {
    			myTime = myHigher.time() + myControlPoint.time();
    			myTime /= 2;
    		}
    		
    		HandleControlPoint myOutHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_OUT_HANDLE, myTime, myControlPoint.value());
    		myBezierPoint.outHandle(myOutHandle);
    		
    		myControlPoint =  myBezierPoint;
    		break;
        default:
        	throw new RuntimeException("invalid control point type: " + _myTool + " for double track");
    	}

	    _myController.trackData().add(myControlPoint);
	    _myController.view().render();
	    return myControlPoint;
	}
	
	private Point2D _mySelectionStart;
	private Point2D _mySelectionEnd;
	
	public Point2D selectionStart(){
		return _mySelectionStart;
	}
	
	public Point2D selectionEnd(){
		return _mySelectionEnd;
	}

	@Override
	public void mousePressed(MouseEvent theEvent) {
		super.mousePressed(theEvent);

		_mySnap = true;
		_myIsInDrag = false;
		
		ControlPoint myControlPoint = _myController.pickNearestPoint(_myPressViewCoords);
		ControlPoint myHandle = _myController.pickHandle(_myPressViewCoords);
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myHandle);
		} else if (myControlPoint != null  && _myController.curveToViewSpace(myControlPoint).distance(_myPressViewCoords) < SwingTrackView.PICK_RADIUS){
			_myDraggedPoints = new ArrayList<ControlPoint>();
			_myDraggedPoints.add(myControlPoint);
		} else {
//			ControlPoint myFloor = theController.trackData().floor(_myPressCurveCoords);
//			ControlPoint myCeil = theController.trackData().ceiling(_myPressCurveCoords);
			
			if(theEvent.getClickCount() == 2){
				_myAddedNewPoint = true;
				_myDraggedPoints = new ArrayList<ControlPoint>();
				_myDraggedPoints.add(createPoint(_myPressViewCoords));
		        _myHasAdd = true;
			}else{
//				if(myFloor != null && myCeil != null){	
//					_myDraggedPoints = new ArrayList<ControlPoint>();
//					_myDraggedPoints.add(myFloor);
//					_myDraggedPoints.add(myCeil);
//				}
			}
		}
		
		if(!_myHasAdd && _myDraggedPoints != null) {
			 _myStartPoints = new ArrayList<ControlPoint>();
			 for(ControlPoint myDraggedPoint:_myDraggedPoints){
				 _myStartPoints.add(myDraggedPoint.clone());
			 }
        }
		
		if(_myDraggedPoints == null){
			_mySelectionStart = _myPressViewCoords;
			_mySelectionEnd = _myPressViewCoords;
		}
	}

    private void moveOppositeHandle(HandleControlPoint theMovedHandle, HandleControlPoint theHandleToMove) {
    	ControlPoint myCenter = theMovedHandle.parent();
    	Point2D myPoint = new Point2D.Double(
    		theMovedHandle.time() - myCenter.time(), 
    		theMovedHandle.value() - myCenter.value()
    	);
    	theHandleToMove.time(myCenter.time() -  myPoint.getX());
    	theHandleToMove.value(Math.max(0, Math.min(myCenter.value() -  myPoint.getY(),1)));
    }
	
	private void dragPoint(ControlPoint theDraggedPoint, ControlPoint myTargetPosition, ControlPoint theMovement, boolean theIsPressedShift) {
    	if (theDraggedPoint.getType().equals(ControlPointType.HANDLE)) {
            // first get next point:
        	HandleControlPoint myHandle = (HandleControlPoint)theDraggedPoint;
            ControlPoint myParent = ((HandleControlPoint)theDraggedPoint).parent();
//            ControlPoint myCurveCoords = myTargetPosition;
            ControlPoint myPreviousPoint = myParent.getPrevious();
            
            switch (myHandle.handleType()) {
			case BEZIER_IN_HANDLE:
				if (myPreviousPoint == null)return;
				ControlPoint myPoint = _myController.context().quantize(myTargetPosition);
				
				double time = CCMath.min(myParent.time(), myPoint.time());
				
//				if(myPreviousPoint.getType() == ControlPointType.BEZIER) {
//					HandleControlPoint myOutHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
//					time = CCMath.max(time, myOutHandle.getTime());
//				}else {
					time = CCMath.max(myPoint.time(), myPreviousPoint.time());
//				}
				
				theDraggedPoint.time(CCMath.constrain(myPoint.time(), myPreviousPoint.time(), myParent.time()));
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myOutHandle = ((BezierControlPoint)myParent).outHandle();
					moveOppositeHandle(myHandle,myOutHandle);
				}
				
				break;
			case BEZIER_OUT_HANDLE:
				myPoint = _myController.context().quantize(myTargetPosition);
				
				time = CCMath.max(myParent.time(), myPoint.time());
				
				ControlPoint myNextPoint = myParent.getNext();
				
				if(myNextPoint != null) {
//					if(myNextPoint.getType() == ControlPointType.BEZIER) {
//						HandleControlPoint myInHandle = ((BezierControlPoint)myNextPoint).inHandle();
//						time = CCMath.min(time, myInHandle.getTime());
//					}else {
						time = CCMath.min(time, myNextPoint.time());
//					}
				}
				
				theDraggedPoint.time(time);
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myInHandle = ((BezierControlPoint)myParent).inHandle();
					moveOppositeHandle(myHandle,myInHandle);
				}
				break;
			default:
				break;
			}
            _myController.trackData().move(myParent,myParent);
        } else {
            ControlPoint myPoint = _myController.context().quantize(myTargetPosition);
            
            double myValueChange = myPoint.value() - theDraggedPoint.value();
            
            _myController.trackData().move(theDraggedPoint, _myController.context().quantize(myPoint));

            switch(theDraggedPoint.getType()) {
            case BEZIER:
            	BezierControlPoint myBezierPoint = (BezierControlPoint)theDraggedPoint;
            	myBezierPoint.inHandle().value(myBezierPoint.inHandle().value() + myValueChange);
            	myBezierPoint.outHandle().value(myBezierPoint.outHandle().value() + myValueChange);
            	break;
			default:
				break;
            }
           if(_myController.property() == null)return;
           
            _myController.property().fromNormalizedValue(myPoint.value(), false);
            _myController.viewValue(_myController.property().valueString());
        }
    }

	@Override
	public void mouseDragged(MouseEvent theEvent) {
		super.mouseDragged(theEvent);
		
		if(_myDraggedPoints == null){
			_mySelectionEnd = _myViewCoords;
			return;
		}
		
		if(_myIsInDrag == false){
			boolean myAddSelection = true;
			if(_myDraggedPoints != null && _myDraggedPoints.size() > 0){
				myAddSelection = _myDraggedPoints.get(0).getType() != ControlPointType.HANDLE;
			}
			if(myAddSelection && _myController.selectedPoints().size() > 0){
				_myDraggedPoints.addAll(_myController.selectedPoints());
				_myStartPoints.clear();
				 for(ControlPoint myDraggedPoint:_myDraggedPoints){
					 _myStartPoints.add(myDraggedPoint.clone());
				 }
			}
		}
		
		_myIsInDrag = true;
		
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
		super.mouseReleased(theEvent);
		
		if(_mySelectionStart != null){
			ControlPoint mySelectionStartCurve = _myController.viewToCurveSpace(_mySelectionStart, true);
			ControlPoint mySelectionEndCurve = _myController.viewToCurveSpace(_mySelectionEnd, true);
			double myStartTime = CCMath.min(mySelectionStartCurve.time(),mySelectionEndCurve.time());
			double myEndTime = CCMath.max(mySelectionStartCurve.time(),mySelectionEndCurve.time());
			double myMinValue = CCMath.min(mySelectionStartCurve.value(),mySelectionEndCurve.value());
			double myMaxValue = CCMath.max(mySelectionStartCurve.value(),mySelectionEndCurve.value());
			List<ControlPoint> mySelectedPoints = _myController.trackData().rangeList(myStartTime, myEndTime);
			
			if(!theEvent.isShiftDown())_myController.clearSelection();
			
			for(ControlPoint mySelectedPoint:mySelectedPoints){
				if(mySelectedPoint.value() < myMinValue || mySelectedPoint.value() > myMaxValue)continue;
				if(!_myController.selectedPoints().contains(mySelectedPoint)){
					mySelectedPoint.setSelected(true);
					_myController.selectedPoints().add(mySelectedPoint);
				}
			}

			_mySelectionStart = null;
			_mySelectionEnd = null;
			return;
		}
		
		
		if (theEvent.getClickCount() == 2 && theEvent.getX() == _myPressX && theEvent.getY() == _myPressY && !_myAddedNewPoint) {
			Point2D myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
			_myController.removePoint(myViewCoords);
		} else {
			if(_myHasAdd) {
	            _myHasAdd = false;
	            UndoHistory.instance().apply(new AddControlPointAction(_myController, _myDraggedPoints.get(0)));
	            _myDraggedPoints = null;
	            return;
	        }
	        
	        if(_myDraggedPoints == null){
	        	_myController.clearSelection();
	        	return;
	        }
	         
	        UndoHistory.instance().apply(new MoveControlPointAction(_myController, _myDraggedPoints, _myStartPoints,  _myDraggedPoints));
	        
	        if(_myIsInDrag || _myDraggedPoints.size() != 1){
	        	_myDraggedPoints = null;
	            return;
	        }
	        
	        if(!theEvent.isShiftDown()){
	        	_myController.clearSelection();
	        }
	       
	        ControlPoint myPoint = _myDraggedPoints.get(0);
	        myPoint.toggleSelection();
	        if(myPoint.isSelected()){
	        	_myController.selectedPoints().add(myPoint);
	        }else{
	        	_myController.selectedPoints().remove(myPoint);
	        }
	        
	        
	        _myDraggedPoints = null;
		}
		_myIsInDrag = false;
		_myAddedNewPoint = false;
	}
}
