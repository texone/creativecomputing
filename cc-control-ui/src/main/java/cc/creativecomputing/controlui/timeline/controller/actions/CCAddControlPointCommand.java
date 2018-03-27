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
public class CCAddControlPointCommand extends CCTrackEditCommand{
	
	public CCAddControlPointCommand(CCTrackData theTrackData, List<CCControlPoint> theControlPoints) {
		super(theTrackData, theControlPoints);
	}
	
	public CCAddControlPointCommand(CCTrackData theTrackData, CCControlPoint...theControlPoints) {
		super(theTrackData, theControlPoints);
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#apply()
	 */
	@Override
	public void apply() {
		for(CCControlPoint myPoint:_myControlPoints){
			_myTrackData.add(myPoint);
		}
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#undo()
	 */
	@Override
	public void undo() {
		for(CCControlPoint myPoint:_myControlPoints){
			_myTrackData.remove(myPoint);
		}
	}

}
