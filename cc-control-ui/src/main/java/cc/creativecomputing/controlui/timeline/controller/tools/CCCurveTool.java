package cc.creativecomputing.controlui.timeline.controller.tools;

import java.awt.event.MouseEvent;

import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.math.CCMath;

public class CCCurveTool extends CCTimelineTool<CCCurveTrackController>{

	private static float MAX_DRAG_X = 100;
	
    private BezierControlPoint _myFloorBezier;
    private BezierControlPoint _myCeilBezier;
    
    public CCCurveTool(CCCurveTrackController theController) {
		super(false, theController);
	}
    
    private BezierControlPoint makeBezier(ControlPoint thePoint, TrackData theData){
    	if(thePoint.getType() == ControlPointType.BEZIER)return (BezierControlPoint)thePoint;
    	
    	theData.remove(thePoint);
    	BezierControlPoint myResult = new BezierControlPoint(thePoint.time(), thePoint.value());
    	HandleControlPoint myInHandle = new HandleControlPoint(myResult,HandleType.BEZIER_IN_HANDLE, myResult.time(), myResult.value());
    	myResult.inHandle(myInHandle);
		HandleControlPoint myOutHandle = new HandleControlPoint(myResult,HandleType.BEZIER_OUT_HANDLE, myResult.time(), myResult.value());
		myResult.outHandle(myOutHandle);
		theData.add(myResult);
    	return myResult;
    }

    @Override
    public void mousePressed(MouseEvent theEvent){
    	super.mousePressed(theEvent);
    	
		ControlPoint myFloor = _myController.trackData().floor(_myPressCurveCoords);
		ControlPoint myCeil = _myController.trackData().ceiling(_myPressCurveCoords);
		
		if(myFloor != null && myCeil != null){
			_myFloorBezier = makeBezier(myFloor, _myController.trackData());
			_myCeilBezier = makeBezier(myCeil, _myController.trackData());
		}else{
			_myFloorBezier = null;
			_myCeilBezier = null;
		}
	}
    
    @Override
    public void mouseDragged(MouseEvent theEvent) {
    	super.mouseDragged(theEvent);
    	
    	if(_myFloorBezier == null)return;
    	if(_myCeilBezier == null)return;
    	
    	if(CCMath.abs(_myMovX) > CCMath.abs(_myMovY)){
    		float myXBlend = CCMath.saturate(CCMath.norm(CCMath.abs(_myMovX), 0, MAX_DRAG_X));
    		if(_myMovX < 0){
    			_myFloorBezier.outHandle().value(_myFloorBezier.value());
    			_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myXBlend));

    			_myCeilBezier.inHandle().value(CCMath.blend(_myCeilBezier.value(), _myFloorBezier.value(), myXBlend));
    			_myCeilBezier.inHandle().time(_myCeilBezier.time());
    		}else{
    			_myFloorBezier.outHandle().value(CCMath.blend(_myFloorBezier.value(), _myCeilBezier.value(), myXBlend));
    			_myFloorBezier.outHandle().time(_myFloorBezier.time());

    			_myCeilBezier.inHandle().value(_myCeilBezier.value());
    			_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myXBlend));
				}
		}else{
			float myYBlend = (float)CCMath.saturate(CCMath.norm(CCMath.abs(_myCurveMovement.value()), 0, 0.25));
			if(_myCurveMovement.value() < 0){
				_myFloorBezier.outHandle().value(_myFloorBezier.value());
				_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myYBlend));

				_myCeilBezier.inHandle().value(_myCeilBezier.value());
				_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myYBlend));
			}else{
				_myFloorBezier.outHandle().value(CCMath.blend(_myFloorBezier.value(), _myCeilBezier.value(), myYBlend));
				_myFloorBezier.outHandle().time(_myFloorBezier.time());
					
				_myCeilBezier.inHandle().value(CCMath.blend(_myCeilBezier.value(), _myFloorBezier.value(), myYBlend));
				_myCeilBezier.inHandle().time(_myCeilBezier.time());
			}
		}
    }
}
