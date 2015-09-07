/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;

/**
 * @author christianriekoff
 *
 */
public class CurveToolController extends ToolController{
	
	private ControlPointType _myControlPointType = ControlPointType.LINEAR;
	
	public CurveToolController(TrackContext theTrackContext) {
		super(theTrackContext);
		_myToolMode = TimelineTool.LINEAR;
	}
	
	@Override
	public void toolMode(TimelineTool theMode) {
		super.toolMode(theMode);
		
		switch(_myToolMode) {
		case BEZIER:
			_myControlPointType = ControlPointType.BEZIER;
			break;
		case LINEAR:
			_myControlPointType = ControlPointType.LINEAR;
			break;
		case STEP:
			_myControlPointType = ControlPointType.STEP;
			break;
		default:
			break;
		}
		_mySelectionController.clear();
	}

	public ControlPointType controlPointType() {
		return _myControlPointType;
	}
	
	public TimelineTool[] tools() {
		return SwingGuiConstants.CURVE_TOOLS;
	}
}
