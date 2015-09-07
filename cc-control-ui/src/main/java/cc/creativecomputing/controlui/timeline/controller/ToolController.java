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


/**
 * @author christianriekoff
 *
 */
public class ToolController {
	

	protected SelectionController _mySelectionController;
	protected TimelineTool _myToolMode = TimelineTool.NONE;

	public ToolController(TrackContext theTrackContext) {
		_mySelectionController = new SelectionController(theTrackContext);
	}
	
	public SelectionController selectionController() {
		return _mySelectionController;
	}
	
	public TimelineTool toolMode() {
		return _myToolMode;
	}
	
	public void toolMode(TimelineTool theMode) {
		_myToolMode = theMode;
	}
	
	public TimelineTool[] tools() {
		return new TimelineTool[] {TimelineTool.MOVE};
	}
}
