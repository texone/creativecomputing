package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;

public class CCTriggerTrackController extends CCCurveTrackController{

	private CCEventTriggerHandle _myTriggerHandle;
	
	public CCTriggerTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
		_myTriggerHandle = (CCEventTriggerHandle)theTrack.property();
		
		_myCreateTool.setTool(CCTimelineTools.TRIGGER_POINT);
		_myActiveTool = _myCreateTool;
	}
	
	@Override
	public CCTimelineTools[] tools() {
		return new CCTimelineTools[]{CCTimelineTools.TRIGGER_POINT};
	}
	
	
	private ControlPoint _myLastControlPoint = null;

	private ControlPoint pointAt(double theTime) {
		ControlPoint myCurveCoords = new ControlPoint(theTime, 0);
		return trackData().lower(myCurveCoords);
	}
	
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
