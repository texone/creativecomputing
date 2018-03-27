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

import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCHandleType;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCLinearControlPoint;
import cc.creativecomputing.control.timeline.point.CCStepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.actions.CCAddControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.actions.CCControlUndoHistory;
import cc.creativecomputing.controlui.timeline.controller.actions.CCMoveControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.actions.CCRemoveControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackView;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCreateTool extends CCTimelineTool{

	protected boolean _myAddedNewPoint = false;

	protected List<CCControlPoint> _myDraggedPoints;
	protected List<CCControlPoint> _myStartPoints;

	protected boolean _myIsInDrag = false;

	protected boolean _myHasAdd = false;

	protected CCTimelineTools _myTool;

	public CCCreateTool(CCTimedContentView theController) {
		super(true, theController);

		_myTool = CCTimelineTools.LINEAR_POINT;
	}

	public void setTool(CCTimelineTools theTool) {
		_myTool = theTool;
	}

	public boolean isInDrag() {
		return _myIsInDrag;
	}

	public CCControlPoint draggedPoint() {
		if (_myDraggedPoints == null || _myDraggedPoints.size() == 0)
			return null;
		return _myDraggedPoints.get(0);
	}

	public CCControlPoint createPoint(CCVector2 theViewCoords) {
		return createPoint(_myDataView.viewToCurveSpace(theViewCoords, true));
	}

	public CCControlPoint createPoint(CCControlPoint myControlPoint) {
		switch (_myTool) {
		case TRIGGER_POINT:
			myControlPoint = new CCStepControlPoint(new CCControlPoint(myControlPoint.time(), 0.5));
			break;
		case STEP_POINT:
			myControlPoint = new CCStepControlPoint(myControlPoint);
			break;
		case LINEAR_POINT:
			myControlPoint = new CCLinearControlPoint(myControlPoint);
			break;
		case BEZIER_POINT:
			CCBezierControlPoint myBezierPoint = new CCBezierControlPoint(myControlPoint);

			CCControlPoint myLower =_myTrackData.lower(myControlPoint);
			double myTime;
			if (myLower == null) {
				myTime = myControlPoint.time() - 1;
			} else {
				myTime = myLower.time() + myControlPoint.time();
				myTime /= 2;
			}
			CCHandleControlPoint myInHandle = new CCHandleControlPoint(myBezierPoint, CCHandleType.BEZIER_IN_HANDLE, myTime, myControlPoint.value());
			myBezierPoint.inHandle(myInHandle);

			CCControlPoint myHigher =_myTrackData.higher(myControlPoint);
			if (myHigher == null) {
				myTime = myControlPoint.time() + myControlPoint.time() - myTime;
			} else {
				myTime = myHigher.time() + myControlPoint.time();
				myTime /= 2;
			}

			CCHandleControlPoint myOutHandle = new CCHandleControlPoint(myBezierPoint, CCHandleType.BEZIER_OUT_HANDLE, myTime, myControlPoint.value());
			myBezierPoint.outHandle(myOutHandle);

			myControlPoint = myBezierPoint;
			break;
		default:
			throw new RuntimeException("invalid control point type: " + _myTool + " for double track");
		}

		_myTrackData.add(myControlPoint);
		return myControlPoint;
	}

	private CCVector2 _mySelectionStart;
	private CCVector2 _mySelectionEnd;

	public CCVector2 selectionStart() {
		return _mySelectionStart;
	}

	public CCVector2 selectionEnd() {
		return _mySelectionEnd;
	}

	@Override
	public void mousePressed(CCGLMouseEvent theEvent) {
		super.mousePressed(theEvent);

		_mySnap = true;
		_myIsInDrag = false;

		CCControlPoint myControlPoint = pickNearestPoint(_myPressViewCoords);
		CCControlPoint myHandle = _myTool == CCTimelineTools.BEZIER_POINT ? pickHandle(_myPressViewCoords) : null;
		if(myHandle != null)myControlPoint = null;
		
		if (myHandle != null) {
			_myDraggedPoints = new ArrayList<CCControlPoint>();
			_myDraggedPoints.add(myHandle);
		} else if (myControlPoint != null && isInRange(myControlPoint,_myPressViewCoords)) {
			_myDraggedPoints = new ArrayList<>();
			_myDraggedPoints.add(myControlPoint);
		} else {
			// ControlPoint myFloor =
			// theController.trackData().floor(_myPressCurveCoords);
			// ControlPoint myCeil =
			// theController.trackData().ceiling(_myPressCurveCoords);

			if (theEvent.clickCount == 2) {
				_myAddedNewPoint = true;
				_myDraggedPoints = new ArrayList<CCControlPoint>();
				_myDraggedPoints.add(createPoint(_myPressViewCoords));
				_myHasAdd = true;
			} else {
				// if(myFloor != null && myCeil != null){
				// _myDraggedPoints = new ArrayList<ControlPoint>();
				// _myDraggedPoints.add(myFloor);
				// _myDraggedPoints.add(myCeil);
				// }
			}
		}

		if (!_myHasAdd && _myDraggedPoints != null) {
			_myStartPoints = new ArrayList<CCControlPoint>();
			for (CCControlPoint myDraggedPoint : _myDraggedPoints) {
				_myStartPoints.add(myDraggedPoint.clone());
			}
		}

		if (_myDraggedPoints == null) {
			_mySelectionStart = _myPressViewCoords;
			_mySelectionEnd = _myPressViewCoords;
		}
	}

	private void moveOppositeHandle(CCHandleControlPoint theMovedHandle, CCHandleControlPoint theHandleToMove) {
		CCControlPoint myCenter = theMovedHandle.parent();
		CCVector2 myPoint = new CCVector2(theMovedHandle.time() - myCenter.time(), theMovedHandle.value() - myCenter.value());
		theHandleToMove.time(myCenter.time() - myPoint.x);
		theHandleToMove.value(Math.max(0, Math.min(myCenter.value() - myPoint.y, 1)));
	}

	private void dragPoint(CCControlPoint theDraggedPoint, CCControlPoint myTargetPosition, boolean theIsPressedShift) {
		if (theDraggedPoint.type().equals(CCControlPointType.HANDLE)) {
			// first get next point:
			CCHandleControlPoint myHandle = (CCHandleControlPoint) theDraggedPoint;
			CCControlPoint myParent = ((CCHandleControlPoint) theDraggedPoint).parent();
			// ControlPoint myCurveCoords = myTargetPosition;
			CCControlPoint myPreviousPoint = myParent.previous();

			switch (myHandle.handleType()) {
			case BEZIER_IN_HANDLE:
				if (myPreviousPoint == null)
					return;
				
				CCControlPoint myPoint = _myDataView.quantize(myTargetPosition);

				double time = CCMath.min(myParent.time(), myPoint.time());

				// if(myPreviousPoint.getType() == ControlPointType.BEZIER) {
				// HandleControlPoint myOutHandle =
				// ((BezierControlPoint)myPreviousPoint).outHandle();
				// time = CCMath.max(time, myOutHandle.getTime());
				// }else {
				time = CCMath.max(myPoint.time(), myPreviousPoint.time());
				// }

				theDraggedPoint.time(CCMath.constrain(myPoint.time(), myPreviousPoint.time(), myParent.time()));
				theDraggedPoint.value(myPoint.value());

				if (theIsPressedShift) {
					CCHandleControlPoint myOutHandle = ((CCBezierControlPoint) myParent).outHandle();
					moveOppositeHandle(myHandle, myOutHandle);
				}

				break;
			case BEZIER_OUT_HANDLE:
				myPoint = _myDataView.quantize(myTargetPosition);

				time = CCMath.max(myParent.time(), myPoint.time());

				CCControlPoint myNextPoint = myParent.next();

				if (myNextPoint != null) {
					// if(myNextPoint.getType() == ControlPointType.BEZIER) {
					// HandleControlPoint myInHandle =
					// ((BezierControlPoint)myNextPoint).inHandle();
					// time = CCMath.min(time, myInHandle.getTime());
					// }else {
					time = CCMath.min(time, myNextPoint.time());
					// }
				}

				theDraggedPoint.time(time);
				theDraggedPoint.value(myPoint.value());

				if (theIsPressedShift) {
					CCHandleControlPoint myInHandle = ((CCBezierControlPoint) myParent).inHandle();
					moveOppositeHandle(myHandle, myInHandle);
				}
				break;
			default:
				break;
			}
			_myTrackData.move(myParent, myParent);
		} else {
			CCControlPoint myPoint = _myDataView.quantize(myTargetPosition);

			double myValueChange = myPoint.value() - theDraggedPoint.value();

			_myTrackData.move(theDraggedPoint, _myDataView.quantize(myPoint));

			switch (theDraggedPoint.type()) {
			case BEZIER:
				CCBezierControlPoint myBezierPoint = (CCBezierControlPoint) theDraggedPoint;
				myBezierPoint.inHandle().value(myBezierPoint.inHandle().value() + myValueChange);
				myBezierPoint.outHandle().value(myBezierPoint.outHandle().value() + myValueChange);
				break;
			default:
				break;
			}
			if (_myDataView.property() == null)
				return;

			updatePropertyValue(myPoint);
			_myDataView.viewValue(_myDataView.property().valueString());
		}
	}

	@Override
	public void mouseDragged(CCVector2 theEvent) {
		super.mouseDragged(theEvent);

		if (_myDraggedPoints == null) {
			_mySelectionEnd = _myViewCoords;
			return;
		}

		if (_myIsInDrag == false) {
			boolean myAddSelection = true;
			if (_myDraggedPoints != null && _myDraggedPoints.size() > 0) {
				myAddSelection = _myDraggedPoints.get(0).type() != CCControlPointType.HANDLE;
			}
			if(_myDraggedPoints.size() == 1 && !_myDraggedPoints.get(0).isSelected()) {
				clearSelection();
			}
			if (myAddSelection && _mySelectedPoints.size() > 0) {
				_myDraggedPoints.addAll(_mySelectedPoints);
				_myStartPoints.clear();
				for (CCControlPoint myDraggedPoint : _myDraggedPoints) {
					_myStartPoints.add(myDraggedPoint.clone());
				}
			}
		}

		_myIsInDrag = true;

		if (_myStartPoints == null)
			return;

		for (int i = 0; i < _myDraggedPoints.size(); i++) {
			CCControlPoint myStartPoint = _myStartPoints.get(i).clone();
			CCControlPoint myTarget = new CCControlPoint(myStartPoint.time() + _myCurveMovement.time(), CCMath.saturate(myStartPoint.value() + _myCurveMovement.value()));
			CCControlPoint myDraggedPoint = _myDraggedPoints.get(i);

			dragPoint(myDraggedPoint, myTarget, _myIsShiftDown);
		}
	}

	@Override
	public void mouseReleased(CCGLMouseEvent theEvent) {
		super.mouseReleased(theEvent);

		if (_mySelectionStart != null) {
			CCControlPoint mySelectionStartCurve = _myDataView.viewToCurveSpace(_mySelectionStart, true);
			CCControlPoint mySelectionEndCurve = _myDataView.viewToCurveSpace(_mySelectionEnd, true);
			double myStartTime = CCMath.min(mySelectionStartCurve.time(), mySelectionEndCurve.time());
			double myEndTime = CCMath.max(mySelectionStartCurve.time(), mySelectionEndCurve.time());
			double myMinValue = CCMath.min(mySelectionStartCurve.value(), mySelectionEndCurve.value());
			double myMaxValue = CCMath.max(mySelectionStartCurve.value(), mySelectionEndCurve.value());
			List<CCControlPoint> mySelectedPoints =_myTrackData.rangeList(myStartTime, myEndTime);

			if (!theEvent.isShiftDown())
				clearSelection();

			for (CCControlPoint mySelectedPoint : mySelectedPoints) {
				if (mySelectedPoint.value() < myMinValue || mySelectedPoint.value() > myMaxValue)
					continue;
				if (!_mySelectedPoints.contains(mySelectedPoint)) {
					mySelectedPoint.setSelected(true);
					_mySelectedPoints.add(mySelectedPoint);
				}
			}

			_mySelectionStart = null;
			_mySelectionEnd = null;
			return;
		}

		if (theEvent.clickCount == 2 && theEvent.x == _myPressX && theEvent.y == _myPressY && !_myAddedNewPoint) {
			CCVector2 myViewCoords = new CCVector2(theEvent.x, theEvent.y);
			CCControlPoint myNearestPoint = pickNearestPoint(myViewCoords);
			if (myNearestPoint != null)
				CCControlUndoHistory.instance().apply(new CCRemoveControlPointCommand(_myTrackData, myNearestPoint));
		} else {
			if (_myHasAdd) {
				_myHasAdd = false;
				CCControlUndoHistory.instance().apply(new CCAddControlPointCommand(_myTrackData, _myDraggedPoints.get(0)));
				_myDraggedPoints = null;
				return;
			}

			if (_myDraggedPoints == null) {
				clearSelection();
				return;
			}

			CCControlUndoHistory.instance().apply(new CCMoveControlPointCommand(_myTrackData, _myDraggedPoints, _myStartPoints, _myDraggedPoints));

			if (_myIsInDrag || _myDraggedPoints.size() != 1) {
				_myDraggedPoints = null;
				return;
			}

			if (!theEvent.isShiftDown()) {
				clearSelection();
			}

			CCControlPoint myPoint = _myDraggedPoints.get(0);
			myPoint.toggleSelection();
			if (myPoint.isSelected()) {
				if(_mySelectedPoints.size() <= 0)onSelection(myPoint);
				_mySelectedPoints.add(myPoint);
			} else {
				_mySelectedPoints.remove(myPoint);
			}

			_myDraggedPoints = null;
		}
		_myIsInDrag = false;
		_myAddedNewPoint = false;
	}
	
	@Override
	public void keyPressed(CCGLKeyEvent e) {
		super.keyPressed(e);
		double _myTimeChange = 0;
		double _myValueChange = 0;
		switch(e.key) {
		case KEY_UP:
			_myValueChange = 0.1;
			break;
		case KEY_DOWN:
			_myValueChange = -0.1;
			break;
		case KEY_LEFT:
			_myTimeChange = -_myDataView.viewTime() / 100;
			break;
		case KEY_RIGHT:
			_myTimeChange = _myDataView.viewTime() / 100;
			break;
		}
		
		if(_myTimeChange == 0 && _myValueChange == 0) return;
		
		_myIsInDrag = true;
		
		_myTimeChange *= e.isShiftDown() ? 0.1 : 1;
		_myValueChange *= e.isShiftDown() ? 0.1 : 1;
		for(CCControlPoint myPoint:_mySelectedPoints) {
			dragPoint(myPoint,new CCControlPoint(myPoint.time() + _myTimeChange, CCMath.saturate(myPoint.value() + _myValueChange)), false);
		}
	}
	
	@Override
	public void keyReleased(CCGLKeyEvent e) {
		super.keyReleased(e);
		_myIsInDrag = false;
	}
	
	private void updatePropertyValue(CCControlPoint thePoint) {
		if(!( _myDataView.property() instanceof CCNumberPropertyHandle)) {
			return;
		}
		CCNumberPropertyHandle<?> myProperty = (CCNumberPropertyHandle<?>)_myDataView.property();
		double myValue = CCMath.blend(myProperty.min().doubleValue(), myProperty.max().doubleValue(), thePoint.value());
		_myDataView.property().fromDoubleValue(myValue, false);
	}
	
	public void onSelection(CCControlPoint thePoint) {
		updatePropertyValue(thePoint);
	}
}
