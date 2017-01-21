package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCFIRFilter extends CCFilter{
	// window size
	private int N = 40;
	
		
	// transfer functions
	private double[] h_calc  = new double[N];
	
	private CCFilterHistoryBuffer _myBuffer;
	
	public CCFIRFilter(int theChannels){
		channels(theChannels);
		prepareValues();
	}
	
	public CCFIRFilter(){
		this(1);
	}
	
	@Override
	public void channels(int theChannels) {
		super.channels(theChannels);
		_myBuffer = new CCFilterHistoryBuffer(theChannels,N);
	}
	
	protected double _myCutOff;
	
	@CCProperty(name = "cut off", min = 0, max = 1, defaultValue = 1)
	public void cutoff(double theCutOff){
		_myCutOff = theCutOff;
		prepareValues();
	}
	
	protected double _myWeight;
	
	@CCProperty(name = "weight", min = 0, max = 4, defaultValue = 0.46)
	public void weight(double theWeight){
		_myWeight = theWeight;
		prepareValues();
	}
	
	@CCProperty(name = "size", min = 10, max = 100, defaultValue = 40)
	public void windowSize(int theSize){
		N = theSize;
		prepareValues();
	}
	
	@Override
	public void prepareValues(){
		h_calc  = new double[N];
		double wc = CCMath.TWO_PI * _myCutOff / _mySampleRate;
		double[] h_ideal = new double[N];
		for (int n = 0; n < N; n++) {
			if (n!=N/2) {
				h_ideal[n] = CCMath.sin(wc*(n-N/2)) / (CCMath.PI*(n-N/2));
			}else {
				h_ideal[n] = wc/CCMath.PI;
			}
		}

		// window array
		double[] w = new double[N];
				
		// hamming window
		for (int n = 0; n < N; n++) {
			w[n] = (1 - _myWeight) - _myWeight*(1-CCMath.cos ((CCMath.TWO_PI * n)/(N-1)));
		}
				
		for (int n = 0; n < N; n++) {
			h_calc[n] = w[n] * h_ideal[n];
		}
	}

	public double process(int theChannel, double theData, double theTime){
		// make sure we have enough filter buffers
		if(theChannel > _myChannels)channels(theChannel);
		
		_myBuffer.append(theChannel, theData);
		
		if(_myBypass) return theData;
		
		double myResult = 0;
		for (int i = 0; i < N;i++) {
			myResult -= _myBuffer.get(theChannel, i) * h_calc[i];
		}
		return myResult;
	}
}
