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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;

/**
 * @author christianriekoff
 *
 */
public abstract class CCTrackEditCommand implements CCUndoCommand{
	
	protected CCTrackData _myTrackData;
	protected List<CCControlPoint> _myControlPoints;
	
	protected static List<CCControlPoint> toList(CCControlPoint...thePoints){
		List<CCControlPoint> myResult = new ArrayList<>();
		for(CCControlPoint myPoint:thePoints){
			myResult.add(myPoint);
		}
		return myResult;
	}
	
	public CCTrackEditCommand(
		CCTrackData theTrackData, 
		List<CCControlPoint> theControlPoints
	) {
		_myTrackData = theTrackData;
		_myControlPoints = theControlPoints;
	}
	
	public CCTrackEditCommand(
		CCTrackData theTrackData, 
		CCControlPoint...theControlPoints
	) {
		_myTrackData = theTrackData;
		_myControlPoints = toList(theControlPoints);
	}
	
	protected List<CCControlPoint> copy(List<CCControlPoint> theInput){
		List<CCControlPoint> myResult = new ArrayList<>();
		for(CCControlPoint myPoint:theInput){
			myResult.add(myPoint.clone());
		}
		return myResult;
	}

}
