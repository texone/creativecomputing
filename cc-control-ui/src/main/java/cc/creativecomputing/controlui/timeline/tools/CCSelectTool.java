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

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCSelectTool extends CCTimelineTool{

	protected List<CCControlPoint> _mySelectedPoints = new ArrayList<>();

	public CCSelectTool(CCTimedContentView theController) {
		super(true, theController);
	}
	
	public List<CCControlPoint> selection(){
		return _mySelectedPoints;
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

	private CCVector2 _mySelectionStart;
	private CCVector2 _mySelectionEnd;

	public CCVector2 selectionStart() {
		return _mySelectionStart;
	}

	public CCVector2 selectionEnd() {
		return _mySelectionEnd;
	}
	
	private CCControlPoint _myClickedPoint;

	@Override
	public void mousePressed(CCGLMouseEvent theEvent) {
		super.mousePressed(theEvent);

		CCControlPoint myControlPoint = pickNearestPoint(_myPressViewCoords);
		if (myControlPoint != null && isInRange(myControlPoint,_myPressViewCoords)) {
			_myClickedPoint = myControlPoint;
		}else{
			_myClickedPoint = null;
		}

		if (_myClickedPoint == null) {
			_mySelectionStart = _myPressViewCoords;
			_mySelectionEnd = _myPressViewCoords;
		}
	}

	

	@Override
	public void mouseDragged(CCVector2 theEvent) {
		super.mouseDragged(theEvent);

		if (_myClickedPoint == null) {
			_mySelectionEnd = _myViewCoords;
			return;
		}

	}

	@Override
	public void mouseReleased(CCGLMouseEvent theEvent) {
		super.mouseReleased(theEvent);

		if (_mySelectionStart != null && _mySelectionStart.distance(_mySelectionEnd) > 5) {
			CCControlPoint mySelectionStartCurve = _myDataView.viewToCurveSpace(_mySelectionStart, true);
			CCControlPoint mySelectionEndCurve = _myDataView.viewToCurveSpace(_mySelectionEnd, true);
			double myStartTime = CCMath.min(mySelectionStartCurve.time(), mySelectionEndCurve.time());
			double myEndTime = CCMath.max(mySelectionStartCurve.time(), mySelectionEndCurve.time());
			double myMinValue = CCMath.min(mySelectionStartCurve.value(), mySelectionEndCurve.value());
			double myMaxValue = CCMath.max(mySelectionStartCurve.value(), mySelectionEndCurve.value());
			List<CCControlPoint> mySelectedPoints =_myTrackData.rangeList(myStartTime, myEndTime);

			if (!theEvent.isShiftDown())
				clearSelection();

			boolean myContainsAll = true;
			for (CCControlPoint mySelectedPoint : mySelectedPoints) {
				if (mySelectedPoint.value() < myMinValue || mySelectedPoint.value() > myMaxValue)
					continue;
				if (!_mySelectedPoints.contains(mySelectedPoint)) {
					mySelectedPoint.setSelected(true);
					_mySelectedPoints.add(mySelectedPoint);
					myContainsAll = false;
				}
			}
			if(myContainsAll){
				for (CCControlPoint mySelectedPoint : mySelectedPoints) {
					if (mySelectedPoint.value() < myMinValue || mySelectedPoint.value() > myMaxValue)
						continue;
					_mySelectedPoints.remove(mySelectedPoint);
					mySelectedPoint.setSelected(false);
				}
			}

			_mySelectionStart = null;
			_mySelectionEnd = null;
			return;
		}

		if (_myClickedPoint == null) {
			if(theEvent.clickCount == 2)clearSelection();
			return;
		}
		
		_myClickedPoint.toggleSelection();
		if (_myClickedPoint.isSelected()) {
			if(_mySelectedPoints.size() <= 0)onSelection(_myClickedPoint);
			_mySelectedPoints.add(_myClickedPoint);
		} else {
			_mySelectedPoints.remove(_myClickedPoint);
		}
	}
	
	private void updatePropertyValue(CCControlPoint thePoint) {
		
	}
	
	public void onSelection(CCControlPoint thePoint) {
		updatePropertyValue(thePoint);
	}
	
	@Override
	public void drawViewSpace(CCGraphics g) {
		if (selectionStart() == null)
			return;

		g.color(255, 0, 0, 100);
		g.rectMode(CCShapeMode.CORNERS);
		g.rect(selectionStart(), selectionEnd(), false);
		g.color(255, 0, 0);
		g.rect(selectionStart(), selectionEnd(), true);
	}
}
