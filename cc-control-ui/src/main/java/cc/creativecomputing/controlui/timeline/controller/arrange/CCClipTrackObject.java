package cc.creativecomputing.controlui.timeline.controller.arrange;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackAdapter;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.core.CCProperty;

public class CCClipTrackObject extends EventTrackAdapter{
	
	private final TimelineContainer _myTimelineContainer;
	
	private TimelineController _myTimelineController;
	
	public CCClipTrackObject(TimelineContainer theTimelineContainer){
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
	
	public TimelineController timelineController(String theTrackID){
		return _myTimelineContainer.timeline(theTrackID);
	}
	
	@Override
	public void onTime(double theTime, EventTrackController theController, TimedEventPoint thePoint) {
		if(_myTimelineController == null)return;
		_myTimelineController.time(theTime - thePoint.time());
	}
}