package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;

public abstract class CCFilter {

	@CCProperty(name = "bypass")
	protected boolean _myBypass = true;
	
	protected double _mySampleRate;
	
	@CCProperty(name = "sample rate", min = 0, max = 60, defaultValue = 0.1)
	public void sampleRate(double theSampleRate){
		_mySampleRate = theSampleRate;
		prepareValues();
	}
	
	public void prepareValues(){
		
	}
	
	protected int _myChannels = 0;
	
	public void channels(int theChannels){
		_myChannels = theChannels;
	}
	
	public int channels(){
		return _myChannels;
	}
	
	public abstract double process(int theChannel, double theData, double theDeltaTime);
}
