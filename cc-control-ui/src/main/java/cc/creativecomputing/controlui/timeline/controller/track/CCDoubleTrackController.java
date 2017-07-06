package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;

public class CCDoubleTrackController extends CCCurveTrackController{
	

	public CCDoubleTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
	}

	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.CREATE_STEP_POINT, CCTimelineTools.CREATE_LINEAR_POINT, CCTimelineTools.CREATE_BEZIER_POINT, CCTimelineTools.CURVE};
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
//		_myHandle.value(theValue);
	}

}
