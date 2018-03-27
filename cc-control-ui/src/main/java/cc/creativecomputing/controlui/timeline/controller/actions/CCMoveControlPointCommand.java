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

package cc.creativecomputing.controlui.timeline.controller.actions;

import java.util.List;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;

/**
 * @author christianriekoff
 *
 */
public class CCMoveControlPointCommand extends CCTrackEditCommand{
	
	private List<CCControlPoint> _myEndControlPoints;
	private List<CCControlPoint> _myStartControlPoints;
	
	public CCMoveControlPointCommand(
		CCTrackData theTrackData, 
		List<CCControlPoint> theControlPoints, 
		List<CCControlPoint> theStartControlPoints, 
		List<CCControlPoint> theEndControlPoints
	) {
		super(theTrackData, theControlPoints);
		_myStartControlPoints = copy(theStartControlPoints);
		_myEndControlPoints = copy(theEndControlPoints);
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#apply()
	 */
	@Override
	public void apply() {
		for(int i = 0; i < _myControlPoints.size();i++){
			_myTrackData.move(_myControlPoints.get(i), _myEndControlPoints.get(i));
		}
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#undo()
	 */
	@Override
	public void undo() {
		for(int i = 0; i < _myControlPoints.size();i++){
			_myTrackData.move(_myControlPoints.get(i), _myStartControlPoints.get(i));
		}
	}

}
