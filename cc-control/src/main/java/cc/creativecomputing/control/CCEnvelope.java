package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.timeline.TrackData;

public class CCEnvelope {

	private Map<String, TrackData> _myCurves = new LinkedHashMap<String, TrackData>();
	
	private TrackData _myCurrentCurve = null;
	
	private String _myCurrentKey = null;
	
	public CCEnvelope(){
	}
	
	public void curveIndex(int theCurveIndex){
		List<String> myList = new ArrayList<>(_myCurves.keySet());
		if(theCurveIndex >= myList.size()){
			_myCurrentCurve = null;
			_myCurrentKey = null;
			return;
		}
		curve(myList.get(theCurveIndex));
	}
	
	public String currentCurve(){
		return _myCurrentKey;
	}
	
	public void currentCurve(TrackData theCurrentCurve){
		_myCurrentCurve = theCurrentCurve;
	}
	
	public void curve(String theCurve){
		_myCurrentKey = theCurve;
		if(theCurve == null){
			_myCurrentCurve = null;
		}
		_myCurrentCurve = _myCurves.get(theCurve);
	}
	
	public float value(float theTime){
		if(_myCurrentCurve == null)return 0;
		return (float)_myCurrentCurve.value(theTime);
	}
	
	public Map<String, TrackData> curves(){
		return _myCurves;
	}
	
	public void curves(Map<String, TrackData> theCurves){
		_myCurves = theCurves;
	}
}
