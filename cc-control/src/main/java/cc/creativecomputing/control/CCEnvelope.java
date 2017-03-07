package cc.creativecomputing.control;

import cc.creativecomputing.control.timeline.TrackData;

public class CCEnvelope {
	
	private TrackData _myCurrentCurve = null;
	
	public CCEnvelope(){
		_myCurrentCurve = new TrackData(null);
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
