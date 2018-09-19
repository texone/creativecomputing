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

package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.tools.CCCreateTool;
import cc.creativecomputing.controlui.timeline.tools.CCCurveTool;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 *
 */
public abstract class CCCurveTrackController extends CCTrackController{
	
	protected CCCreateTool _myCreateTool;
	protected CCCurveTool _myCurveTool;
	
	protected CCTimelineTools _myActiveToolEnum;
	
	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	public CCCurveTrackController(
		CCTrackContext theTrackContext, 
		CCTrack theTrack, 
		CCGroupTrackController theParent
	) {
		super(theTrackContext, theTrack, theParent);
		
		
		_myCreateTool = new CCCreateTool(this);
		_myCurveTool = new CCCurveTool(this);
		
		_myCreateTool.setTool(CCTimelineTools.LINEAR_POINT);
		_myActiveToolEnum = CCTimelineTools.LINEAR_POINT;
		_myActiveTool = _myCreateTool;
		if(theTrack.property() == null)return;
		
		theTrack.property().changeEvents.add(theValue -> {
			if(_mySelectedPoints == null || _mySelectedPoints.size() == 0)return;
			if(_myCreateTool.isInDrag())return;
			if(_myTimelineController != null && _myTimelineController.transportController().isPlaying())return;
			if(_myChangedValue){
				_myChangedValue = false;
				return;
			}
			for(CCControlPoint myPoint:_mySelectedPoints){
				applyValue(myPoint, null);
			}
		});
	}
	
	@Override
	public void setTool(CCTimelineTools theTool) {
		_myActiveToolEnum = theTool;
		switch(theTool){
		case BEZIER_POINT:
		case LINEAR_POINT:
		case STEP_POINT:
			CCLog.info(this+":"+_myCreateTool);
			CCLog.info(_myCreateTool + " : " + theTool);
			if(_myCreateTool == null)return;
			_myCreateTool.setTool(theTool);
			_myActiveTool = _myCreateTool;
			break;
		case CURVE:
			clearSelection();
			_myActiveTool = _myCurveTool;
			break;
		default:
			break;
		}
	}
	
	@Override
	public CCTimelineTools activeTool() {
		return _myActiveToolEnum;
	}
	
	@Override
	public CCControlPoint draggedPoint() {
		return _myCreateTool.draggedPoint();
	}
	
	public CCVector2 selectionStart(){
		return _myCreateTool.selectionStart();
	}
	
	public CCVector2 selectionEnd(){
		return _myCreateTool.selectionEnd();
	}
	
	@Override
	public void writeValue(double theTime) {
		_myCreateTool.createPoint(new CCControlPoint(theTime, _myProperty.normalizedValue()));
	}
	
	private boolean _myApplyValue = true;
	
	public void setApplyValue(boolean theApplyValue) {
		_myApplyValue = theApplyValue;
	}
	
	public void applyValue(CCControlPoint thePoint, Object theValue) {
		thePoint.value(_myProperty.normalizedValue());
	}
}
