package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;

public class CCGradientTrackController extends CCBlendableTrackController<CCGradient>{

	public CCGradientTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
	}
	
	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.LINEAR_POINT, CCTimelineTools.STEP_POINT, CCTimelineTools.CURVE};
	}

	@Override
	public CCGradient createDefault() {
		return new CCGradient();
	}

}
