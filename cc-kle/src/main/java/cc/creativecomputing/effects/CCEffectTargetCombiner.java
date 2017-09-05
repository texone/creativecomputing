package cc.creativecomputing.effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCEffectTargetCombiner implements CCEffectCombiner{
	
	@CCProperty(name = "targets a", min = 0, max = 1)
	private Map<String, Double> _cTargetsA = new HashMap<>();
	@CCProperty(name = "targets b", min = 0, max = 1)
	private Map<String, Double> _cTargetsB = new HashMap<>();
	@CCProperty(name = "bypass")
	private boolean _cBypass = true;
	
	private String[] _myValueNames;
	
	private CCEffectBlendCombiner _myBlendCombiner;
	
	public CCEffectTargetCombiner(String...theValueNames) {
		_myValueNames = theValueNames;
		for(String myValueName:theValueNames) {
			_cTargetsA.put(myValueName, 0d);
			_cTargetsB.put(myValueName, 0d);
		}
		_myBlendCombiner = new CCEffectBlendCombiner();
	}
	
	private class CCValueInfo implements Comparable<CCValueInfo>{
		int id;
		int channel;
		
		double[][] _myValues;
		
		public CCValueInfo(int theID, int theChannel, double[][] theValues){
			id = theID;
			channel = theChannel;
			_myValues = theValues;
		}
		
		public double value(){
			return _myValues[id][channel];
		}

		@Override
		public int compareTo(CCValueInfo o) {
			return Double.compare(o.value(), value());
		}
	}
	
	public void threshold(double[][] theIn, double[][] theOut, int theChannel, double theTarget){
		double sum = 0;
		
		for(int i = 0; i < theIn.length;i++){
			double myValue = theIn[i][theChannel];
			sum += myValue;
		}
		
		double myNeededChange = theTarget * theIn.length - sum;
		if(myNeededChange == 0) {
			for(int i = 0; i < theIn.length;i++){
				theOut[i][theChannel] = theIn[i][theChannel];
			}
			return;
		}
		
		List<CCValueInfo> myInfos = new ArrayList<>();
		for(int i = 0; i < theIn.length; i++){
			myInfos.add(new CCValueInfo(i, theChannel, theIn));
		}
		
		Collections.sort(myInfos);
		
		if(myNeededChange > 0){
			double myChange = myNeededChange;
			int mySegments = theIn.length;
			for(CCValueInfo myInfo:myInfos){
				double myDif = CCMath.min(1d - myInfo.value(), myChange / mySegments);
				
				theOut[myInfo.id][theChannel] = myInfo.value() + myDif;
				myChange -= myDif;
				mySegments --;
			}
		}else{
			double myChange = -myNeededChange;
			int mySegments = theIn.length;
			
			for(int i = theIn.length - 1; i >= 0;i--){
				CCValueInfo myInfo = myInfos.get(i);
				double myDif = CCMath.min(myInfo.value(), myChange / mySegments);
				theOut[myInfo.id][theChannel] = myInfo.value() - myDif;
				myChange -= myDif;
				mySegments --;
			}
		}
	}
	
	@Override
	public double[][] combine(double[] theBlends, double[][] theValuesA, double[][] theValuesB) {
		if(_cBypass)return _myBlendCombiner.combine(theBlends, theValuesA, theValuesB);
		
		double[][] myThreshA = new double[theBlends.length][_myValueNames.length];
		double[][] myThreshB = new double[theBlends.length][_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++) {
			double myTargetA = _cTargetsA.get(_myValueNames[i]);
			double myTargetB = _cTargetsB.get(_myValueNames[i]);
			threshold(theValuesA, myThreshA, i, myTargetA);
			threshold(theValuesB, myThreshB, i, myTargetB);
		}
		return _myBlendCombiner.combine(theBlends, myThreshA, myThreshB);
	}
}
