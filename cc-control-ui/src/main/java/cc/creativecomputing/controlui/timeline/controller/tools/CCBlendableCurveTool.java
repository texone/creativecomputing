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

public class CCBlendableCurveTool extends CCCurveTool{
    
    public CCBlendableCurveTool(CCCurveTrackController theController) {
		super(theController);
	}
    
    @Override
    protected BezierControlPoint makeBezier(ControlPoint thePoint, TrackData theData){
		if(thePoint.getType() == ControlPointType.BEZIER)return (BezierControlPoint)thePoint;
	
	    	theData.remove(thePoint);
	    	
	    	BezierControlPoint myResult = new BezierControlPoint(thePoint.time(), thePoint.value());
	    	myResult.blendable(thePoint.blendable());
	    	HandleControlPoint myInHandle = new HandleControlPoint(myResult,HandleType.BEZIER_IN_HANDLE, myResult.time(), 1.0);
	    	myResult.inHandle(myInHandle);
	    	HandleControlPoint myOutHandle = new HandleControlPoint(myResult,HandleType.BEZIER_OUT_HANDLE, myResult.time(), 0.0);
	    	myResult.outHandle(myOutHandle);
	    	theData.add(myResult);
	    	return myResult;
    }
    
    @Override
	public void mouseDragged(MouseEvent theEvent) {
    		super.mouseDragged(theEvent);
    	
		if(_myFloorBezier == null)return;
		if(_myCeilBezier == null)return;
    	
		if(CCMath.abs(_myMovX) > CCMath.abs(_myMovY)){
	    		float myXBlend = CCMath.saturate(CCMath.norm(CCMath.abs(_myMovX), 0, MAX_DRAG_X));
	    		if(_myMovX < 0){
	    			_myFloorBezier.outHandle().value(0.0);
	    			_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myXBlend));
	
	    			_myCeilBezier.inHandle().value(CCMath.blend(1.0, 0.0, myXBlend));
	    			_myCeilBezier.inHandle().time(_myCeilBezier.time());
	    		}else{
	    			_myFloorBezier.outHandle().value(CCMath.blend(0.0, 1.0, myXBlend));
	    			_myFloorBezier.outHandle().time(_myFloorBezier.time());
	
	    			_myCeilBezier.inHandle().value(1.0);
	    			_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myXBlend));
			}
		}else{
			float myYBlend = (float)CCMath.saturate(CCMath.norm(CCMath.abs(_myCurveMovement.value()), 0, 0.25));
			if(_myCurveMovement.value() < 0){
				_myFloorBezier.outHandle().value(0.0);
				_myFloorBezier.outHandle().time(CCMath.blend(_myFloorBezier.time(), _myCeilBezier.time(), myYBlend));
	
				_myCeilBezier.inHandle().value(1.0);
				_myCeilBezier.inHandle().time(CCMath.blend(_myCeilBezier.time(), _myFloorBezier.time(), myYBlend));
			}else{
				_myFloorBezier.outHandle().value(CCMath.blend(0.0, 1.0, myYBlend));
				_myFloorBezier.outHandle().time(_myFloorBezier.time());
						
				_myCeilBezier.inHandle().value(CCMath.blend(1.0, 0.0, myYBlend));
				_myCeilBezier.inHandle().time(_myCeilBezier.time());
			}
		}
    }
}
