package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;

public class IntegerTrackController extends CurveTrackController{
	

	public IntegerTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
	}
	
	@Override
	public void applyValue(ControlPoint thePoint, Object theValue) {
		thePoint.value(_myProperty.normalizedValue());
	}

	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		return new StepControlPoint(theCurveCoords.time(), theCurveCoords.value());
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
	}

}
