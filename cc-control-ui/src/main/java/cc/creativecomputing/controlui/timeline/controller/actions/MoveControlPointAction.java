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
import java.util.List;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.TrackDataController;
import cc.creativecomputing.controlui.util.Action;

/**
 * @author christianriekoff
 *
 */
public class MoveControlPointAction implements Action{
	
	private TrackDataController _myTrackDataController;
	private List<ControlPoint> _myControlPoints;
	private List<ControlPoint> _myEndControlPoints;
	private List<ControlPoint> _myStartControlPoints;
	
	public MoveControlPointAction(
		TrackDataController theTrackDataController, 
		List<ControlPoint> theControlPoints, 
		List<ControlPoint> theStartControlPoints, 
		List<ControlPoint> theEndControlPoints
	) {
		_myTrackDataController = theTrackDataController;
		_myControlPoints = theControlPoints;
		_myStartControlPoints = copy(theStartControlPoints);
		_myEndControlPoints = copy(theEndControlPoints);
	}
	
	private List<ControlPoint> copy(List<ControlPoint> theInput){
		List<ControlPoint> myResult = new ArrayList<>();
		for(ControlPoint myPoint:theInput){
			myResult.add(myPoint.clone());
		}
		return myResult;
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#apply()
	 */
	@Override
	public void apply() {
		for(int i = 0; i < _myControlPoints.size();i++){
			_myTrackDataController.trackData().move(_myControlPoints.get(i), _myEndControlPoints.get(i));
		}
		_myTrackDataController.view().render();
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#undo()
	 */
	@Override
	public void undo() {
		for(int i = 0; i < _myControlPoints.size();i++){
			_myTrackDataController.trackData().move(_myControlPoints.get(i), _myStartControlPoints.get(i));
		}
		_myTrackDataController.view().render();
	}

}
