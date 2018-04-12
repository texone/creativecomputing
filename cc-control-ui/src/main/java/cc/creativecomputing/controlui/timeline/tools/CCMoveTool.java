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
import java.util.List;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.gl.app.CCGLKeyEvent;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCMoveTool extends CCTimelineTool{

	protected List<CCControlPoint> _myDraggedPoints;
	protected List<CCControlPoint> _myStartPoints;

	protected boolean _myIsInDrag = false;
	protected CCSelectTool _mySelectTool;

	public CCMoveTool(CCTimedContentView theController, CCSelectTool theSelectTool) {
		super(true, theController);
		_mySelectTool = theSelectTool;
	}

	public boolean isInDrag() {
		return _myIsInDrag;
	}

	public CCControlPoint draggedPoint() {
		if (_myDraggedPoints == null || _myDraggedPoints.size() == 0)
			return null;
		return _myDraggedPoints.get(0);
	}

	@Override
	public void mousePressed(CCGLMouseEvent theEvent) {
		super.mousePressed(theEvent);
		_mySnap = true;
		_myIsInDrag = false;

		CCControlPoint myControlPoint = pickNearestPoint(_myPressViewCoords);

		if (myControlPoint == null || !isInRange(myControlPoint,_myPressViewCoords)) return;
		
		_myDraggedPoints = new ArrayList<>();
		_myDraggedPoints.add(myControlPoint);
	
		_myStartPoints = new ArrayList<>();
		for (CCControlPoint myDraggedPoint : _myDraggedPoints) {
			_myStartPoints.add(myDraggedPoint.clone());
		}
	}

	private void dragPoint(CCControlPoint theDraggedPoint, CCControlPoint myTargetPosition, boolean theIsPressedShift) {
		
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
//			if (_myDataView.property() == null)
//				return;
//
//			updatePropertyValue(myPoint);
//			_myDataView.viewValue(_myDataView.property().valueString());
	}

	@Override
	public void mouseDragged(CCVector2 theEvent) {
		super.mouseDragged(theEvent);
		
		if(_myDraggedPoints == null)return;

		if (_myIsInDrag == false) {
			boolean myAddSelection = true;
			if (_myDraggedPoints != null && _myDraggedPoints.size() > 0) {
				myAddSelection = _myDraggedPoints.get(0).type() != CCControlPointType.HANDLE;
			}
			if(_myDraggedPoints.size() == 1 && !_myDraggedPoints.get(0).isSelected()) {
				_mySelectTool.clearSelection();
			}
			if (myAddSelection && _mySelectTool.selection().size() > 0) {
				_myDraggedPoints.addAll(_mySelectTool.selection());
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

		if(_myStartPoints == null)return;
		if(_myDraggedPoints == null)return;
//		TODO FIX THIS!!
//		CCControlUndoHistory.instance().apply(new CCMoveControlPointCommand(_myTrackData, _myDraggedPoints, _myStartPoints, _myDraggedPoints));

		for(CCControlPoint myPoint:_myTrackData){
			myPoint.fix();
		}
		if (_myIsInDrag || _myDraggedPoints.size() != 1) {
			_myDraggedPoints = null;
			return;
		}

		_myDraggedPoints = null;
		_myIsInDrag = false;
	}
	
	@Override
	public void keyPressed(CCGLKeyEvent e) {
		super.keyPressed(e);
		double _myTimeChange = 0;
		double _myValueChange = 0;
		switch(e.key) {
		case KEY_UP:
			_myValueChange = -0.01;
			break;
		case KEY_DOWN:
			_myValueChange = 0.01;
			break;
		case KEY_LEFT:
			_myTimeChange = -_myDataView.viewTime() / 100;
			break;
		case KEY_RIGHT:
			_myTimeChange = _myDataView.viewTime() / 100;
			break;
		default:
			break;
		}
		
		if(_myTimeChange == 0 && _myValueChange == 0) return;
		
		_myIsInDrag = true;
		
		_myTimeChange *= e.isShiftDown() ? 0.1 : 1;
		_myValueChange *= e.isShiftDown() ? 0.1 : 1;
		for(CCControlPoint myPoint:_mySelectTool.selection()) {
			dragPoint(myPoint,new CCControlPoint(myPoint.time() + _myTimeChange, CCMath.saturate(myPoint.value() + _myValueChange)), false);
		}
	}
	
	@Override
	public void keyReleased(CCGLKeyEvent e) {
		super.keyReleased(e);
		_myIsInDrag = false;
	}
	
//	private void updatePropertyValue(CCControlPoint thePoint) {
//		if(!( _myDataView.property() instanceof CCNumberPropertyHandle)) {
//			return;
//		}
//		CCNumberPropertyHandle<?> myProperty = (CCNumberPropertyHandle<?>)_myDataView.property();
//		double myValue = CCMath.blend(myProperty.min().doubleValue(), myProperty.max().doubleValue(), thePoint.value());
//		_myDataView.property().fromDoubleValue(myValue, false);
//	}
	
	public void onSelection(CCControlPoint thePoint) {
//		updatePropertyValue(thePoint);
	}
}
