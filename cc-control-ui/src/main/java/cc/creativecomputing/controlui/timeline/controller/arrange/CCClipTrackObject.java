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
package cc.creativecomputing.controlui.timeline.controller.arrange;

import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.core.CCProperty;

public class CCClipTrackObject extends CCEventTrackAdapter{
	
	private final CCTimelineContainer _myTimelineContainer;
	
	private CCTimelineController _myTimelineController;
	
	public CCClipTrackObject(CCTimelineContainer theTimelineContainer){
		_myTimelineContainer = theTimelineContainer;
	}
	
	private String _myLastTimeline = "";
	
	@CCProperty(name = "trackID")
	public void trackID(String theTrackID){
		if(theTrackID == null){
			_myLastTimeline = null;
			_myTimelineController = null;
			return;
		}
		if(_myLastTimeline == null || !theTrackID.equals(_myLastTimeline)){
			_myTimelineController = _myTimelineContainer.timeline(theTrackID);
			_myLastTimeline = theTrackID;
		}
	}
	
	public CCTimelineController timelineController(String theTrackID){
		return _myTimelineContainer.timeline(theTrackID);
	}
	
	@Override
	public void onTime(double theTime, CCEventTrackController theController, CCTimedEventPoint thePoint) {
		if(_myTimelineController == null)return;
		_myTimelineController.time(theTime - thePoint.time() - thePoint.contentOffset());
	}
}
