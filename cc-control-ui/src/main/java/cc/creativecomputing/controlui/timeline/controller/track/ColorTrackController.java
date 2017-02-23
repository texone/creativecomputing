package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.math.CCColor;

public class ColorTrackController extends CCBlendableTrackController<CCColor>{

	public ColorTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
	}

	@Override
	public CCColor createDefault() {
		return new CCColor();
	}

}
