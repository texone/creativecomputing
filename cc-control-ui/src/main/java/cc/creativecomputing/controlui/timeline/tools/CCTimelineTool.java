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

import java.util.ArrayList;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCTimelineTool {
	
    protected double _myPressX;
    protected double _myPressY;
    
    protected double _myMovX;
    protected double _myMovY;
    
    protected CCVector2 _myPressViewCoords;
    protected CCControlPoint _myPressCurveCoords;

    protected CCVector2 _myViewCoords;
    protected CCControlPoint _myCurveCoords;
    protected CCControlPoint _myCurveMovement;

    protected boolean _mySnap = false;
    
    protected CCTimedContentView _myDataView;
    
    protected CCTrackData _myTrackData;
	
	public CCEventManager<CCControlPoint> hoverEvents = new CCEventManager<>();
	public CCEventManager<CCControlPoint> endHoverEvents = new CCEventManager<>();

	public CCTimelineTool(boolean theSnap, CCTimedContentView theController){
		_mySnap = theSnap;
		_myDataView = theController;
	}
	
	
	
	/// PICK FUNCTIONS ////
	
	protected double pickRange() {
		return _myDataView.viewWidthToTime(CCUIConstants.PICK_RADIUS);
	}
	
	protected boolean isInRange(CCControlPoint theControlPoint, CCVector2 theViewCoords){
		return _myDataView.curveToViewSpace(theControlPoint).distance(theViewCoords) < CCUIConstants.PICK_RADIUS;
	}
	
	protected boolean isInRangeX(CCControlPoint theControlPoint, CCVector2 theViewCoords){
		return CCMath.abs(_myDataView.curveToViewSpace(theControlPoint).x - theViewCoords.x) < CCUIConstants.PICK_RADIUS;
	}
	
	// picks the nearest point (could be null) and returns it in view space
	public CCControlPoint pickNearestPoint(CCVector2 theViewCoords) {
		if (_myTrackData == null)
			return null;
		
		CCControlPoint myPickCoords = _myDataView.viewToCurveSpace(theViewCoords, true);
		double myPickRange = pickRange();

		ArrayList<CCControlPoint> myPoints = _myTrackData.rangeList(myPickCoords.time() - myPickRange, myPickCoords.time() + myPickRange);

		if (myPoints.size() == 0) {
			return null;
		}

		CCVector2 myCurrentPoint = _myDataView.curveToViewSpace(myPoints.get(0));
		CCControlPoint myNearest = myPoints.get(0);
		double myMinDistance = myCurrentPoint.distance(theViewCoords);
		for (CCControlPoint myPoint : myPoints) {
			myCurrentPoint = _myDataView.curveToViewSpace(myPoint);
			double myDistance = myCurrentPoint.distance(theViewCoords);
			if (myDistance < myMinDistance) {
				myNearest = myPoint;
				myMinDistance = myDistance;
			}
		}
		return myNearest;
	}
	
	public CCHandleControlPoint pickHandle(CCVector2 theViewCoords) {
		if (_myTrackData == null)
			return null;
		
		CCControlPoint myCurveCoords = _myDataView.viewToCurveSpace(theViewCoords, true);

		CCControlPoint myNextPoint = _myTrackData.ceiling(myCurveCoords);
		CCControlPoint myPreviousPoint = _myTrackData.lower(myCurveCoords);

		if (myNextPoint != null) {
			switch (myNextPoint.type()) {
			case BEZIER:
				CCHandleControlPoint myInputHandle = ((CCBezierControlPoint) myNextPoint).inHandle();

				if (isInRange(myInputHandle, theViewCoords)) {
					return myInputHandle;
				}
				break;
			default:
				break;
			}

			myNextPoint = _myTrackData.higher(myNextPoint);
			if (myNextPoint != null && myNextPoint.type() == CCControlPointType.BEZIER) {
				CCHandleControlPoint myInputHandle = ((CCBezierControlPoint) myNextPoint).inHandle();
				if (isInRange(myInputHandle, theViewCoords)) {
					return myInputHandle;
				}
			}
		}

		if (myPreviousPoint != null) {
			switch (myPreviousPoint.type()) {
			case BEZIER:
				CCHandleControlPoint myOutputHandle = ((CCBezierControlPoint) myPreviousPoint).outHandle();

				if (isInRange(myOutputHandle, theViewCoords)) {
					return myOutputHandle;
				}
				break;
			case TIMED_EVENT:
				CCHandleControlPoint myTimedEnd = ((CCEventPoint) myPreviousPoint).endPoint();

				if (isInRangeX(myTimedEnd, theViewCoords)) {
					return myTimedEnd;
				}
				break;
			default:
				break;
			}

			myPreviousPoint = _myTrackData.lower(myPreviousPoint);
			if (myPreviousPoint == null || myPreviousPoint.type() != CCControlPointType.BEZIER) {
				return null;
			}

			CCHandleControlPoint myOutputHandle = ((CCBezierControlPoint) myPreviousPoint).outHandle();
			if (isInRange(myOutputHandle, theViewCoords)) {
				return myOutputHandle;
			}
		}

		return null;
	}
	
	public void trackData(CCTrackData theTrackData){
		_myTrackData = theTrackData;
	}

	public CCTrackData trackData(){
		return _myTrackData;
	}
	
	protected CCGLKey _myKeyCode;
	protected char _myKeyChar;
	
	protected boolean _myIsShiftDown = false;
	protected boolean _myIsAltDown = false;
	protected boolean _myIsCTRLDown = false;
	protected boolean _myIsSuperDown = false;
	
	protected boolean _myIsMousePressed = false;
	
	public CCEvent<Character> keyChar = this::keyChar;
	public void keyChar(char theChar){
		_myKeyChar = theChar;
	}
	
	public CCEvent<CCGLKeyEvent> keyPressed = this::keyPressed;
	public CCEvent<CCGLKeyEvent> keyReleased = this::keyReleased;

	public CCEvent<CCGLMouseEvent> mousePressed = this::mousePressed;
	public CCEvent<CCGLMouseEvent> mouseReleased = this::mouseReleased;
	public CCEvent<CCVector2> mouseMoved = this::mouseMoved;
	public CCEvent<CCVector2> mouseDragged = this::mouseDragged;
	
	public void keyPressed(CCGLKeyEvent e) {
		_myKeyCode = e.key;
		switch (e.key) {
		case KEY_LEFT_SHIFT:
		case KEY_RIGHT_SHIFT:
			_myIsShiftDown = true;
			break;
		case KEY_LEFT_ALT:
		case KEY_RIGHT_ALT:
			_myIsAltDown = true;
			break;
		case KEY_LEFT_CONTROL:
		case KEY_RIGHT_CONTROL:
			_myIsCTRLDown = true;
			break;
		case KEY_LEFT_SUPER:
		case KEY_RIGHT_SUPER:
			_myIsSuperDown = true;
			break;

		default:
			break;
		}
	}
	
	public void keyReleased(CCGLKeyEvent e) {
		_myKeyCode = null;
		_myKeyChar = 0;
		
		switch (e.key) {
		case KEY_LEFT_SHIFT:
		case KEY_RIGHT_SHIFT:
			_myIsShiftDown = false;
			break;
		case KEY_LEFT_ALT:
		case KEY_RIGHT_ALT:
			_myIsAltDown = false;
			break;
		case KEY_LEFT_CONTROL:
		case KEY_RIGHT_CONTROL:
			_myIsCTRLDown = false;
			break;
		case KEY_LEFT_SUPER:
		case KEY_RIGHT_SUPER:
			_myIsSuperDown = false;
			break;
		default:
			break;
		}
	}
	
	public CCVector2 mousePressViewCoords(){
		return _myPressViewCoords;
	}
	
	public CCVector2 mouseViewCoords(){
		return _myViewCoords;
	}
	
	public boolean mousePressed(){
		return _myIsMousePressed;
	}
	
	public void mousePressed(CCGLMouseEvent theEvent){
		_myPressX = theEvent.x;
		_myPressY = theEvent.y;
		_myMovX = 0;
		_myMovY = 0;
		
		_myPressViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myPressCurveCoords = _myDataView.viewToCurveSpace(_myPressViewCoords, true);
		
		_myIsMousePressed = true;
		
		updateMotion(new CCVector2(theEvent.x, theEvent.y));
	}
	
	private void updateMotion(CCVector2 theEvent){
		_myMovX = _myPressX - theEvent.x;
		_myMovY = _myPressY - theEvent.y;
		
		if(_myKeyChar == 'x') {
			_myMovY = 0;
		}
		if(_myKeyChar == 'y') {
			_myMovX = 0;
		}

		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myDataView.viewToCurveSpace(_myViewCoords, true);
		
		if(_myMovY > 0){
			_myCurveMovement = _myDataView.viewToCurveSpace(new CCVector2(-_myMovX, _myMovY), false);
			_myCurveMovement.value(-_myCurveMovement.value());
		}else{
			_myCurveMovement = _myDataView.viewToCurveSpace(new CCVector2(-_myMovX, -_myMovY), false);
			_myCurveMovement.value( _myCurveMovement.value());
		}
	}
	
	private CCControlPoint _myHoverPoint = null;
	
	public void mouseMoved(CCVector2 theEvent){
		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myDataView.viewToCurveSpace(_myViewCoords, true);
		
		CCControlPoint myControlPoint = pickNearestPoint(_myViewCoords);
		if(myControlPoint == null)return;
		if(isInRange(myControlPoint,_myViewCoords)){
			if(_myHoverPoint == null){
				hoverEvents.event(myControlPoint);
				_myHoverPoint = myControlPoint;
			}
		}else{
			if(_myHoverPoint != null){
				endHoverEvents.event(myControlPoint);
				_myHoverPoint = null;
			}
		}
	}
	
	public void mouseDragged(CCVector2 theEvent){
		updateMotion(theEvent);
	}
	
	public void mouseReleased(CCGLMouseEvent theEvent){
		_myKeyCode = null;
		
		_myIsMousePressed = false;
	}
	
	public void drawViewSpace(CCGraphics g) {
		
	}
}
