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

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.actions.CCControlUndoHistory;
import cc.creativecomputing.controlui.timeline.controller.actions.CCRemoveControlPointCommand;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
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
    
	protected List<CCControlPoint> _mySelectedPoints = new ArrayList<>();

	public CCTimelineTool(boolean theSnap, CCTimedContentView theController){
		_mySnap = theSnap;
		_myDataView = theController;
	}
	
	/// SELCTION FUNCTIONS ////
	
	public void clearSelection() {
		for (CCControlPoint myPoint : _mySelectedPoints) {
			myPoint.setSelected(false);
		}
		_mySelectedPoints.clear();
	}

	public void deleteSelection() {
		for (CCControlPoint myPoint : _mySelectedPoints) {
			_myTrackData.remove(myPoint);
		}
		clearSelection();
	}

	public List<CCControlPoint> copySelection() {
		List<CCControlPoint> clipBoard = new ArrayList<>();
		for (CCControlPoint myPoint : _mySelectedPoints) {
			clipBoard.add(myPoint.clone());
		}
		return clipBoard;
	}

	public List<CCControlPoint> cutSelection() {
		List<CCControlPoint> clipBoard = new ArrayList<>();
		for (CCControlPoint myPoint : _mySelectedPoints) {
			_myTrackData.remove(myPoint);
			clipBoard.add(myPoint.clone());
		}
		clearSelection();
		return clipBoard;
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
				CCHandleControlPoint myTimedEnd = ((CCTimedEventPoint) myPreviousPoint).endPoint();

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
	

	
	protected CCGLKey _myKeyCode;
	
	protected boolean _myIsShiftDown = false;
	protected boolean _myIsAltDown = false;
	protected boolean _myIsCTRLDown = false;
	protected boolean _myIsSuperDown = false;
	
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
	}
	
	public void mousePressed(CCGLMouseEvent theEvent){
		_myPressX = theEvent.x;
		_myPressY = theEvent.y;
		
		_myPressViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myPressCurveCoords = _myDataView.viewToCurveSpace(_myPressViewCoords, true);
	}
	
	private void updateMotion(CCVector2 theEvent){
		_myMovX = _myPressX - theEvent.x;
		_myMovY = _myPressY - theEvent.y;
		
		if(_myKeyCode == CCGLKey.KEY_X) {
			_myMovY = 0;
		}
		if(_myKeyCode == CCGLKey.KEY_Y) {
			_myMovX = 0;
		}

		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myDataView.viewToCurveSpace(_myViewCoords, true);
		
		if(_myMovY > 0){
			_myCurveMovement = _myDataView.viewToCurveSpace(new CCVector2(-_myMovX, _myMovY), false);
			_myCurveMovement.value(1 - _myCurveMovement.value());
		}else{
			_myCurveMovement = _myDataView.viewToCurveSpace(new CCVector2(-_myMovX, -_myMovY), false);
			_myCurveMovement.value( _myCurveMovement.value() - 1);
		}
	} 
	
	public void mouseMoved(CCVector2 theEvent){
		_myViewCoords = new CCVector2(theEvent.x, theEvent.y);
		_myCurveCoords = _myDataView.viewToCurveSpace(_myViewCoords, true);
	}
	
	public void mouseDragged(CCVector2 theEvent){
		updateMotion(theEvent);
	}
	
	public void mouseReleased(CCGLMouseEvent theEvent){
		_myKeyCode = null;
	}
}
