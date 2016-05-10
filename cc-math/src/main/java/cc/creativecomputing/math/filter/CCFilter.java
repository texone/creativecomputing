package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;

public class CCFilter {

	protected double _mySampleRate;
	
	@CCProperty(name = "bypass")
	protected boolean _myBypass = false;
	
	@CCProperty(name = "sample rate", min = 0, max = 60, defaultValue = 0.1)
	public void sampleRate(double theSampleRate){
		_mySampleRate = theSampleRate;
	}
	
	protected int _myChannels = 0;
	
	public void channels(int theChannels){
		_myChannels = theChannels;
	}
	
	public int channels(){
		return _myChannels;
	}

	public void process(int theChannel, double[] theData, double theTime){
		
	}
	
	public double process(int theChannel, double theData, double theTime){
		double[] myData = new double[]{theData};
		process(theChannel, myData, theTime);
		return myData[0];
	}
}
