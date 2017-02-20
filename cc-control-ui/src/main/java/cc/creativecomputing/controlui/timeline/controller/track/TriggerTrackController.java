package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;

public class TriggerTrackController extends CurveTrackController{

	private CCEventTriggerHandle _myTriggerHandle;
	
	public TriggerTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
		_myTriggerHandle = (CCEventTriggerHandle)theTrack.property();
	}

	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		return new StepControlPoint(theCurveCoords.time(), 0.5);
	}
	
	public ControlPoint pointAt(double theTime) {
		ControlPoint myCurveCoords = new ControlPoint(theTime, 0);
		return trackData().lower(myCurveCoords);
	}
	
	private ControlPoint _myLastControlPoint = null;

	@Override
	public void timeImplementation(double theTime, double theValue) {
		ControlPoint myEventPoint = pointAt(theTime);
		if(myEventPoint == _myLastControlPoint)return;
		
		if(myEventPoint != null)_myTriggerHandle.trigger();
		_myLastControlPoint = myEventPoint;
	}

	@Override
	public void applyValue(ControlPoint thePoint, Object theValue) {
		
	}
}
