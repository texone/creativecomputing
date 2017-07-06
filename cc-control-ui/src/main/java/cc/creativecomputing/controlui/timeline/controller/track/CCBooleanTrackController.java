package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;

public class CCBooleanTrackController extends CCCurveTrackController{

	private CCBooleanPropertyHandle _myBooleanHandle;
	
	public CCBooleanTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
		_myBooleanHandle = (CCBooleanPropertyHandle)theTrack.property();
	}
	
	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.CREATE_STEP_POINT};
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		_myBooleanHandle.value(theValue >= 0.5, false);
	}

}
