package cc.creativecomputing.control;

import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;

public class CCEnvelope {
	
	private TrackData _myCurrentCurve = null;
	
	public CCEnvelope(){
		_myCurrentCurve = new TrackData(null);
		_myCurrentCurve.add(new LinearControlPoint(0, 0));
		_myCurrentCurve.add(new LinearControlPoint(1, 1));
	}
	
	
	
	public TrackData curve(){
		return _myCurrentCurve;
	}
	
	
	
	public double value(double theTime){
		if(_myCurrentCurve == null)return 0;
		try{
			return _myCurrentCurve.value(theTime);
		}catch(Exception e){
			return 0;
		}
	}
}
