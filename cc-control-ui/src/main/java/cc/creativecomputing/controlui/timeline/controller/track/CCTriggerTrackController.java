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

import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;

public class CCTriggerTrackController extends CCCurveTrackController{

	private CCEventTriggerHandle _myTriggerHandle;
	
	public CCTriggerTrackController(CCTrackContext theTrackContext, CCTrack theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
		_myTriggerHandle = (CCEventTriggerHandle)theTrack.property();
		
		_myCreateTool.setTool(CCTimelineTools.TRIGGER_POINT);
		_myActiveTool = _myCreateTool;
	}
	
	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.TRIGGER_POINT};
	}
	
	
	private CCControlPoint _myLastControlPoint = null;

	private CCControlPoint pointAt(double theTime) {
		CCControlPoint myCurveCoords = new CCControlPoint(theTime, 0);
		return trackData().lower(myCurveCoords);
	}
	
	@Override
	public void timeImplementation(double theTime, double theValue) {
		CCControlPoint myEventPoint = pointAt(theTime);
		if(myEventPoint == _myLastControlPoint)return;
		
		if(myEventPoint != null)_myTriggerHandle.trigger();
		_myLastControlPoint = myEventPoint;
	}

	@Override
	public void applyValue(CCControlPoint thePoint, Object theValue) {
		
	}
}
