package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.math.CCColor;

public class CCColorTrackController extends CCBlendableTrackController<CCColor>{

	public CCColorTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
	}

	@Override
	public CCColor createDefault() {
		return new CCColor();
	}

}
