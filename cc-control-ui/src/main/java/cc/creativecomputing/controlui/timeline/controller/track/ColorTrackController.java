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
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class ColorTrackController extends DoubleTrackController{
	

	public ColorTrackController(TrackContext theTrackContext, CurveToolController theCurveTool, Track theTrack, GroupTrackController theParent) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
	}
	
	@Override
	public void applyValue(ControlPoint thePoint, Object theValue) {
		thePoint.blendable((CCColor)theValue);
		thePoint.value((Double)_myProperty.normalizedValue());
	}

	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		ControlPoint myResult = super.createPointImpl(theCurveCoords);
		myResult.blendable(new CCColor());
		return myResult;
	}
	
	public CCColor color(double theTime){
		if (trackData().size() == 0) {
			return new CCColor();
		}
		
		ControlPoint mySample = trackData().createSamplePoint(theTime);
		ControlPoint myLower = trackData().lower(mySample);
		ControlPoint myCeiling = trackData().ceiling(mySample);

		if (myLower == null && myCeiling == null) {
			return new CCColor();
		}
		
		if (myLower == null && myCeiling != null) {
			return ((CCColor)myCeiling.blendable()).clone();
		}
		
		if(myCeiling == null && myLower != null){
			return ((CCColor)myLower.blendable()).clone();
		}

		myLower = trackData().getLastOnSamePosition(myLower);
		
		double myBlend = myCeiling.interpolateValue(theTime, trackData());
		
		return CCColor.blend((CCColor)myLower.blendable(), (CCColor)myCeiling.blendable(),CCMath.pow(myBlend, myBlend) );
		
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
//		_myHandle.value(theValue);
	}

}
