package cc.creativecomputing.control;

import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

public class CCEnvelope implements CCInterpolatable<Double>{
	
	private TrackData _myCurrentCurve = null;
	
	public CCEnvelope(){
		_myCurrentCurve = new TrackData(null);
		_myCurrentCurve.add(new LinearControlPoint(0, 0));
		_myCurrentCurve.add(new LinearControlPoint(1, 1));
	}
	
	public TrackData curve(){
		return _myCurrentCurve;
	}
	
	@Deprecated
	/**
	 * Note is deprecated use interpolate instead
	 * @param theTime
	 * @return
	 */
	public double value(double theTime){
		return interpolate(theTime);
	}

	@Override
	public Double interpolate(double theValue) {
		if(_myCurrentCurve == null)return 0d;
		try{
			return _myCurrentCurve.value(theValue);
		}catch(Exception e){
			return 0d;
		}
	}

	public void set(CCEnvelope theValue) {
		_myCurrentCurve = theValue.curve();
	}
	
	public CCEnvelope clone() {
		CCEnvelope myResult = new CCEnvelope();
		myResult.set(this);
		return myResult;
	}
}
