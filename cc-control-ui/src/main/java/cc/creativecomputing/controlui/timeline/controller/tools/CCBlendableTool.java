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

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCLinearControlPoint;
import cc.creativecomputing.control.timeline.point.CCStepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCBlendableTrackController;
import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.core.logging.CCLog;

public class CCBlendableTool<Type extends CCBlendable<Type>> extends CCCreateTool{
	
    public CCBlendableTool(CCBlendableTrackController<Type> theController) {
		super(theController);
		_myTool = CCTimelineTools.LINEAR_POINT;
	}
    
	@SuppressWarnings("unchecked")
	public CCControlPoint createPoint(CCControlPoint myControlPoint) {
		switch(_myTool) {
		case LINEAR_POINT:
    			myControlPoint = new CCLinearControlPoint(myControlPoint);
    			break;
		case STEP_POINT:
    			myControlPoint = new CCStepControlPoint(myControlPoint);
    			break;
		default:
			break;
		}
		CCLog.info((Type)_myDataView.property().value());
		myControlPoint.blendable((Type)_myDataView.property().value());
		CCLog.info(myControlPoint.blendable());
	    _myDataView.trackData().add(myControlPoint);
	    _myDataView.view().render();
	    return myControlPoint;
	}
	
	@Override
	public void onSelection(CCControlPoint thePoint) {
		if(thePoint.blendable() == null)return;
		_myDataView.property().valueCasted(thePoint.blendable() , false);
	}
}
