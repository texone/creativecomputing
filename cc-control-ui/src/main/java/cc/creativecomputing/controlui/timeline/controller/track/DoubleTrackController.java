package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.HandleType;
import cc.creativecomputing.control.timeline.point.HandleControlPoint;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;

public class DoubleTrackController extends CurveTrackController{
	

	public DoubleTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
	}

	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		switch(_myCurveTool.controlPointType()) {
    	case STEP:
    		return new StepControlPoint(theCurveCoords.time(), theCurveCoords.value());
    	case LINEAR:
    		return new LinearControlPoint(theCurveCoords.time(), theCurveCoords.value());
    	case BEZIER:
    		BezierControlPoint myBezierPoint = new BezierControlPoint(theCurveCoords.time(), theCurveCoords.value());
    		
    		ControlPoint myLower = trackData().lower(theCurveCoords);
    		double myTime;
    		if(myLower == null) {
    			myTime = theCurveCoords.time() - 1;
    		}else {
    			myTime = myLower.time() + theCurveCoords.time();
    			myTime /= 2;
    		}
    		HandleControlPoint myInHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_IN_HANDLE, myTime, theCurveCoords.value());
    		myBezierPoint.inHandle(myInHandle);
    		
    		ControlPoint myHigher = trackData().higher(theCurveCoords);
    		if(myHigher == null) {
    			myTime = theCurveCoords.time() + theCurveCoords.time() - myTime;
    		}else {
    			myTime = myHigher.time() + theCurveCoords.time();
    			myTime /= 2;
    		}
    		
    		HandleControlPoint myOutHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_OUT_HANDLE, myTime, theCurveCoords.value());
    		myBezierPoint.outHandle(myOutHandle);
    		
            return myBezierPoint;
        default:
        	throw new RuntimeException("invalid control point type: " + _myCurveTool.controlPointType() + " for double track");
    	}
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
//		_myHandle.value(theValue);
	}

}
