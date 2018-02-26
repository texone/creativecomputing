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

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackDataController;
import cc.creativecomputing.controlui.util.Action;

/**
 * @author christianriekoff
 *
 */
public class MoveEventAction implements Action{
	
	private CCTrackDataController _myEventTrackController;
	private CCTimedEventPoint _myEventPoint;
	private CCControlPoint _myEndControlPoint;
	private CCControlPoint _myStartControlPoint;
	private double _myEventStartEnd;
	private double _myEventEndEnd;
	
	public MoveEventAction(
		CCEventTrackController theEventTrackController, 
		CCTimedEventPoint theEventPoint, 
		CCControlPoint theStartControlPoint, 
		CCControlPoint theEndControlPoint,
		double theEventStartEnd,
		double theEventEndEnd
	) {
		_myEventTrackController = theEventTrackController;
		_myEventPoint = theEventPoint;
		_myStartControlPoint = theStartControlPoint.clone();
		_myEndControlPoint = theEndControlPoint.clone();
		_myEventStartEnd = theEventStartEnd;
		_myEventEndEnd = theEventEndEnd;
	}

	@Override
	public void apply() {
		_myEventTrackController.trackData().move(_myEventPoint, _myEndControlPoint);
		_myEventPoint.endTime(_myEventEndEnd);
		_myEventTrackController.view().render();
	}

	@Override
	public void undo() {
		_myEventTrackController.trackData().move(_myEventPoint, _myStartControlPoint);
		_myEventPoint.endTime(_myEventStartEnd);
		_myEventTrackController.view().render();
	}

}
