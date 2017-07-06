package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

@SuppressWarnings({"unchecked"})
public abstract class CCBlendableTrackController<Type extends CCBlendable<Type>> extends CCDoubleTrackController{
	

	public CCBlendableTrackController(TrackContext theTrackContext, Track theTrack, CCGroupTrackController theParent) {
		super(theTrackContext, theTrack, theParent);
	}
	
	@Override
	public void applyValue(ControlPoint thePoint, Object theValue) {
		thePoint.blendable((Type)_myProperty.value());
	}

//	@Override
//	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
//		ControlPoint myResult = super.createPointImpl(theCurveCoords);
//		myResult.blendable((Type)_myProperty.value());
//		return myResult;
//	}
	
	public abstract Type createDefault();
	
	public Type blend(double theTime){
		if (trackData().size() == 0) {
			return createDefault();
		}
		
		ControlPoint mySample = trackData().createSamplePoint(theTime);
		ControlPoint myLower = trackData().lower(mySample);
		ControlPoint myCeiling = trackData().ceiling(mySample);
		
		if(myLower != null)myLower.value(0);
		if(myCeiling != null)myCeiling.value(1);

		if (myLower == null && myCeiling == null) {
			return createDefault();
		}
		
		if (myLower == null && myCeiling != null) {
			return ((Type)myCeiling.blendable()).clone();
		}
		
		if(myCeiling == null && myLower != null){
			return ((Type)myLower.blendable()).clone();
		}

		myLower = trackData().getLastOnSamePosition(myLower);
		
		double myBlend = myCeiling.interpolateValue(theTime, trackData());
		Type blend0 = (Type)myLower.blendable();
		Type blend1 = (Type)myCeiling.blendable();
		return blend0.blend(blend1, myBlend);
		
	}

	@Override
	public void timeImplementation(double theTime, double theValue) {
		_myProperty.valueCasted(blend(theTime), false);
	}

}
