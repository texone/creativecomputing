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
package cc.creativecomputing.controlui.timeline.tools;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
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
		theData.add(myResult);
		return myResult;
    }
	
	private CCHandleControlPoint _myHandle;
	private CCControlPoint _myStartPoint;
	
    @Override
    public void mousePressed(CCGLMouseEvent theEvent){
		super.mousePressed(theEvent);
		
		_myHandle = pickHandle(_myPressViewCoords);
		if(_myHandle != null){
			_myStartPoint = _myHandle.clone();
			return;
		}
    	
		CCControlPoint myFloor = _myTrackData.getLastPointBefore(_myPressCurveCoords.time());
		CCControlPoint myCeil = _myTrackData.getFirstPointAfter(_myPressCurveCoords.time());
		
		if(myFloor != null && myCeil != null){
			_myFloorBezier = makeBezier(myFloor, _myTrackData);
			_myCeilBezier = makeBezier(myCeil, _myTrackData);
		}else{
			_myFloorBezier = null;
			_myCeilBezier = null;
		}
	}
    
	private void moveOppositeHandle(CCHandleControlPoint theMovedHandle, CCHandleControlPoint theHandleToMove) {
		CCControlPoint myCenter = theMovedHandle.parent();
		CCVector2 myPoint = new CCVector2(theMovedHandle.time() - myCenter.time(), theMovedHandle.value() - myCenter.value());
		theHandleToMove.time(myCenter.time() - myPoint.x);
		theHandleToMove.value(Math.max(0, Math.min(myCenter.value() - myPoint.y, 1)));
	}
	
	private void dragHandle(CCHandleControlPoint theHandle, CCControlPoint myTargetPosition, boolean theIsPressedShift) {
		// first get next point:
		CCControlPoint myParent = theHandle.parent();
		// ControlPoint myCurveCoords = myTargetPosition;
		CCControlPoint myPreviousPoint = myParent.previous();

		switch (theHandle.handleType()) {
		case BEZIER_IN_HANDLE:
			if (myPreviousPoint == null)
				return;
				
			CCControlPoint myPoint = _myDataView.quantize(myTargetPosition);

			double time = CCMath.min(myParent.time(), myPoint.time());
			time = CCMath.max(myPoint.time(), myPreviousPoint.time());

			theHandle.time(CCMath.constrain(myPoint.time(), myPreviousPoint.time(), myParent.time()));
			theHandle.value(myPoint.value());

			if (theIsPressedShift) {
				CCHandleControlPoint myOutHandle = ((CCBezierControlPoint) myParent).outHandle();
				moveOppositeHandle(theHandle, myOutHandle);
			}

			break;
		case BEZIER_OUT_HANDLE:
			myPoint = _myDataView.quantize(myTargetPosition);

			time = CCMath.max(myParent.time(), myPoint.time());

			CCControlPoint myNextPoint = myParent.next();

			if (myNextPoint != null) {
				time = CCMath.min(time, myNextPoint.time());
			}

			theHandle.time(time);
			theHandle.value(myPoint.value());

			if (theIsPressedShift) {
				CCHandleControlPoint myInHandle = ((CCBezierControlPoint) myParent).inHandle();
				moveOppositeHandle(theHandle, myInHandle);
			}
			break;
		default:
			break;
		}
		_myTrackData.move(myParent, myParent);
	}
	
	public boolean doMoveHandle(){
		return _myHandle != null;
	}
    
    @Override
	public void mouseDragged(CCVector2 theEvent) {
    	super.mouseDragged(theEvent);
    	
    	if(_myHandle != null){
			CCControlPoint myTarget = new CCControlPoint(_myStartPoint.time() + _myCurveMovement.time(), CCMath.saturate(_myStartPoint.value() + _myCurveMovement.value()));
			dragHandle(_myHandle, myTarget, _myIsShiftDown);
			return;
		}
    	
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
    
    @Override
    public void drawViewSpace(CCGraphics g) {
    	if (!mousePressed() || doMoveHandle()) return;
		
    	g.color(255);

    	double myRadius = mousePressViewCoords().distance(mouseViewCoords());

    	g.beginShape(CCDrawMode.LINE_STRIP);
    	for (int i = 0; i <= 100; i++) {
    		double myAngle = i / 100d * CCMath.TWO_PI;
    		double myX = CCMath.cos(myAngle) * myRadius + mousePressViewCoords().x;
    		double myY = CCMath.sin(myAngle) * myRadius + mousePressViewCoords().y;
    		
    		g.color(CCMath.abs(CCMath.sin(myAngle)), 0, CCMath.abs(CCMath.cos(myAngle)));
    		g.vertex(myX, myY);
    	}
    	g.endShape();

    	g.beginShape(CCDrawMode.LINES);
    	g.color(1d, 0d);
    	g.vertex(mousePressViewCoords());
    	g.color(1d);
    	g.vertex(mouseViewCoords());
    	g.endShape();
    }
}
