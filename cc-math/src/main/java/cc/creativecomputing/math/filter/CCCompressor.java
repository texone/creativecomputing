package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCCompressor extends CCFilter {

	

	private class CCEnvelopeDetector {

		protected double _myEnvelopeSample = 0;
		
		public double envelope(double theData){
			double myAttackGain = CCMath.exp(-1 / (attackTime * _mySampleRate));
			double myReleaseGain = CCMath.exp(-1 / (releaseTime * _mySampleRate));
			
			if (_myEnvelopeSample < theData) {
				_myEnvelopeSample = theData + myAttackGain * (_myEnvelopeSample - theData);
			} else {
				_myEnvelopeSample = theData + myReleaseGain * (_myEnvelopeSample - theData);
			}

			if(Double.isNaN(_myEnvelopeSample))_myEnvelopeSample = 0;
			return _myEnvelopeSample;
		}
		
	}

	public static enum ProcessType {
		Compressor, Limiter
	}

	@CCProperty(name = "Threshold", min = 0, max = 1f)
	public double threshold = 0f; // in dB
	@CCProperty(name = "Ratio (x:1)", min = 1f, max = 20f)
	public double ratio = 1f;
	@CCProperty(name = "Knee", min = 0f, max = 2f)
	public double knee = 0.2f;
	@CCProperty(name = "Pre-gain", min = 0f, max = 2f)
	public double preGain = 1f; // amplifies the audio signal prior to
								// envelope detection.
	@CCProperty(name = "Post-gain", min = 0f, max = 2f)
	public double postGain = 1f; // amplifies the audio signal after
									// compression.
	@CCProperty(name = "Attack time (ms)", min = 0f, max = 3.00f)
	public double attackTime = 10f; // in ms
	@CCProperty(name = "Release time ", min = 0.010f, max = 3.000f)
	public double releaseTime = 50f; // in ms
	@CCProperty(name = "Lookahead time ", min = 0, max = 0.200f)
	public double lookaheadTime = 0f; // in ms

	@CCProperty(name = "process type")
	public ProcessType processType = ProcessType.Compressor;

	private CCEnvelopeDetector[] _myEnvelopeDetectors;

	private static double amp2db(double level) {
		return 20. * CCMath.log10(level);
	}

	private static double db2amp(double db) {
		return CCMath.pow(10.0f, db / 20.0f);
	}

	public CCCompressor(int theChannels) {
		channels(theChannels);
		
	}
	
	public CCCompressor() {
		this(1);
	}
	
	@Override
	public void channels(int theChannels) {
		super.channels(theChannels);
		_myEnvelopeDetectors = new CCEnvelopeDetector[theChannels];
		for(int i = 0; i < theChannels;i++){
			_myEnvelopeDetectors[i] = new CCEnvelopeDetector();
		}
	}
	
	@CCProperty(name = "reset")
	public void resetEnvelope(){
		for(CCEnvelopeDetector myDetector:_myEnvelopeDetectors){
			myDetector._myEnvelopeSample = 0;
		}
	}
	
	@Override
	public double process(int theChannel, double theData, double theTime) {
		if(_myBypass)return theData;

		if(Double.isNaN(theData))return theData;
		if (preGain != 0f) {
			theData *= preGain;
		}

		double threshDB = amp2db(threshold);
		double envelopeData = _myEnvelopeDetectors[theChannel].envelope(theData);
		// Threshold is in dB and will always be either 0 or negative, so * by -1 to make positive.
		double kneeWidth = threshDB * knee * -1f; 
		double lowerKneeBound = threshDB - (kneeWidth / 2f);
		double upperKneeBound = threshDB + (kneeWidth / 2f);

		double envValue = amp2db(envelopeData);
		double mySlope = 1;
		if (processType == ProcessType.Compressor) {
			mySlope = 1 - (1 / ratio);
		}
		double myGain;

		if (kneeWidth > 0f && envValue > lowerKneeBound && envValue < upperKneeBound) { // Soft
																							// knee
			// Lerp the compressor slope value.
			// Slope is multiplied by 0.5 since the gain is calculated in
			// relation to the lower knee bound for soft knee.
			// Otherwise, the interpolation's peak will be reached at the
			// threshold instead of at the upper knee bound.
			mySlope *= (((envValue - lowerKneeBound) / kneeWidth) * 0.5f);
			myGain = mySlope * (lowerKneeBound - envValue);
		} else { // Hard knee
			myGain = mySlope * (threshDB - envValue);
			myGain = CCMath.min(0f, myGain);
		}

		myGain = db2amp(myGain);

		return theData * (myGain * postGain);
		
	}
}
