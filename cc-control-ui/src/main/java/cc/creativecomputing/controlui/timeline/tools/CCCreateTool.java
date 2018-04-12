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

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCHandleType;
import cc.creativecomputing.control.timeline.point.CCHandleControlPoint;
import cc.creativecomputing.control.timeline.point.CCLinearControlPoint;
import cc.creativecomputing.control.timeline.point.CCStepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.actions.CCAddControlPointCommand;
import cc.creativecomputing.controlui.timeline.controller.actions.CCControlUndoHistory;
import cc.creativecomputing.controlui.timeline.controller.actions.CCRemoveControlPointCommand;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCVector2;

public class CCCreateTool extends CCTimelineTool{

	protected CCTimelineTools _myTool;
	protected CCSelectTool _mySelectTool;

	public CCCreateTool(CCTimedContentView theController, CCSelectTool theSelectTool) {
		super(true, theController);

		_myTool = CCTimelineTools.LINEAR_POINT;
		_mySelectTool = theSelectTool;
	}

	public void setTool(CCTimelineTools theTool) {
		_myTool = theTool;
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

	@Override
	public void mouseReleased(CCGLMouseEvent theEvent) {
		super.mouseReleased(theEvent);
		
		if(theEvent.clickCount != 2)return;
		
		CCControlPoint myControlPoint = pickNearestPoint(_myPressViewCoords);
		if (myControlPoint != null &&  isInRange(myControlPoint,_myPressViewCoords)) {
			CCControlUndoHistory.instance().apply(new CCRemoveControlPointCommand(_myTrackData, myControlPoint));
			_mySelectTool.selection().remove(myControlPoint);
			return;
		}
		
		CCControlUndoHistory.instance().apply(new CCAddControlPointCommand(_myTrackData, createPoint(_myPressViewCoords)));
	}
	
	private void updatePropertyValue(CCControlPoint thePoint) {
//		if(!( _myDataView.property() instanceof CCNumberPropertyHandle)) {
//			return;
//		}
//		CCNumberPropertyHandle<?> myProperty = (CCNumberPropertyHandle<?>)_myDataView.property();
//		double myValue = CCMath.blend(myProperty.min().doubleValue(), myProperty.max().doubleValue(), thePoint.value());
//		_myDataView.property().fromDoubleValue(myValue, false);
	}
	
	public void onSelection(CCControlPoint thePoint) {
		updatePropertyValue(thePoint);
	}
}
