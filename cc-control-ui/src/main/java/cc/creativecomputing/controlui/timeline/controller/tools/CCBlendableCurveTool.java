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

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCHandleType;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;

public class CCBlendableCurveTool extends CCCurveTool{
    
    public CCBlendableCurveTool(CCCurveTrackController theController) {
		super(theController);
	}
    
    @Override
    protected CCBezierControlPoint makeBezier(CCControlPoint thePoint, CCTrackData theData){
		if(thePoint.type() == CCControlPointType.BEZIER)return (CCBezierControlPoint)thePoint;
	
	    	theData.remove(thePoint);
	    	
	    	CCBezierControlPoint myResult = new CCBezierControlPoint(thePoint.time(), thePoint.value());
	    	myResult.blendable(thePoint.blendable());
	    	CCHandleControlPoint myInHandle = new CCHandleControlPoint(myResult,CCHandleType.BEZIER_IN_HANDLE, myResult.time(), 1.0);
	    	myResult.inHandle(myInHandle);
	    	CCHandleControlPoint myOutHandle = new CCHandleControlPoint(myResult,CCHandleType.BEZIER_OUT_HANDLE, myResult.time(), 0.0);
	    	myResult.outHandle(myOutHandle);
	    	theData.add(myResult);
	    	return myResult;
    }
    
    @Override
	public void mouseDragged(CCGLMouseEvent theEvent) {
    		super.mouseDragged(theEvent);
    	
		if(_myFloorBezier == null)return;
		if(_myCeilBezier == null)return;
    	
		if(CCMath.abs(_myMovX) > CCMath.abs(_myMovY)){
	    		double myXBlend = CCMath.saturate(CCMath.norm(CCMath.abs(_myMovX), 0, MAX_DRAG_X));
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
