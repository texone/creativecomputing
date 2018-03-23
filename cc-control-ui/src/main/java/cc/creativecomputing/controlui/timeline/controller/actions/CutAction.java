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
/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.controlui.timeline.controller.actions;

import java.util.ArrayList;

import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.util.Action;


/**
 * @author christianriekoff
 *
 */
public class CutAction implements CCUndoAction{
	
	private CCTrackController _myTrackDataController;
	private ArrayList<CCControlPoint> _myControlPoints;
	private CCTimeRange _myTimeRange;
	
	public CutAction(CCTrackController theTrackDataController, ArrayList<CCControlPoint> theControlPoints, CCTimeRange theTimeRange) {
		_myTrackDataController = theTrackDataController;
		_myControlPoints = theControlPoints;
		_myTimeRange = theTimeRange;
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#apply()
	 */
	@Override
	public void apply() {
		_myTrackDataController.trackData().removeAll(_myTimeRange.start(), _myTimeRange.end() );
		_myTrackDataController.view().render();
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#undo()
	 */
	@Override
	public void undo() {
		_myTrackDataController.trackData().insertAll(_myTimeRange.start(), _myTimeRange.length(), _myControlPoints);
		_myTrackDataController.view().render();
	}

}
