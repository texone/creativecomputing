package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;

public class BooleanTrackController extends CurveTrackController{

	private CCBooleanPropertyHandle _myBooleanHandle;
	
	public BooleanTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
		_myBooleanHandle = (CCBooleanPropertyHandle)theTrack.property();
	}

	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		return new StepControlPoint(theCurveCoords.time(), theCurveCoords.value());
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		_myBooleanHandle.value(theValue >= 0.5, false);
	}

}
