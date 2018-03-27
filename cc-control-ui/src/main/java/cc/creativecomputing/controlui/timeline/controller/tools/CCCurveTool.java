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
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCHandleType;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCurveTool extends CCTimelineTool{

	protected static float MAX_DRAG_X = 100;
	
    protected CCBezierControlPoint _myFloorBezier;
    protected CCBezierControlPoint _myCeilBezier;
    
    public CCCurveTool(CCTimedContentView theController) {
		super(false, theController);
	}
    
	protected CCBezierControlPoint makeBezier(CCControlPoint thePoint, CCTrackData theData){
		if(thePoint.type() == CCControlPointType.BEZIER)return (CCBezierControlPoint)thePoint;
    		
		theData.remove(thePoint);
	    	
		CCBezierControlPoint myResult = new CCBezierControlPoint(thePoint.time(), thePoint.value());
		myResult.blendable(thePoint.blendable());
		CCHandleControlPoint myInHandle = new CCHandleControlPoint(myResult,CCHandleType.BEZIER_IN_HANDLE, myResult.time(), myResult.value());
		myResult.inHandle(myInHandle);
		CCHandleControlPoint myOutHandle = new CCHandleControlPoint(myResult,CCHandleType.BEZIER_OUT_HANDLE, myResult.time(), myResult.value());
		myResult.outHandle(myOutHandle);
		theData.add(myResult);
		return myResult;
    }

    @Override
    public void mousePressed(CCGLMouseEvent theEvent){
		super.mousePressed(theEvent);
    	
		CCControlPoint myFloor = _myTrackData.floor(_myPressCurveCoords);
		CCControlPoint myCeil = _myTrackData.ceiling(_myPressCurveCoords);
		
		if(myFloor != null && myCeil != null){
			_myFloorBezier = makeBezier(myFloor, _myTrackData);
			_myCeilBezier = makeBezier(myCeil, _myTrackData);
		}else{
			_myFloorBezier = null;
			_myCeilBezier = null;
		}
	}
    
    @Override
	public void mouseDragged(CCVector2 theEvent) {
    	super.mouseDragged(theEvent);
    	
		if(_myFloorBezier == null)return;
		if(_myCeilBezier == null)return;
    	
		if(CCMath.abs(_myMovX) > CCMath.abs(_myMovY)){
			double myXBlend = CCMath.saturate(CCMath.norm(CCMath.abs(_myMovX), 0, MAX_DRAG_X));
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
