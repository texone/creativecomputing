package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCLimiter extends CCFilter {

	@CCProperty(name = "gain", min = 0, max = 100)
	private double limiterInputGain = 0.0D;
	
	@CCProperty(name = "threshold", min = -100, max = 0)
	private double limiterThreshold = -1D;
	@CCProperty(name = "release", min = 0, max = 100)
	private double limiterRelease = 10D;
	
	private double waveLimiter = 0.0D;

	@Override
	public double process(int theChannel, double theData, double theTime) {
		if(_myBypass)return theData;
		
		double releaseSamples = limiterRelease * _mySampleRate;
		
		double inputGain = CCMath.pow(10.0D, limiterInputGain / 20.0D);
		double threshold = CCMath.pow(10.0D, limiterThreshold / 20.0D);

		if (threshold == 1.0D)
			threshold = 0.9999999000000001D;

		if (releaseSamples == 0.0D)
			releaseSamples = 1.0D;

		theData *= inputGain;

		double waveActual = Math.abs(theData);
		double deltaWave = waveLimiter - waveActual;
			
		if (deltaWave > 0.0D) {
			double deltaReleaseWave = Math.abs(deltaWave / releaseSamples);
			waveLimiter -= deltaReleaseWave;
			if (waveLimiter < 0.0D)
				waveLimiter = 0.0D;
		} else if (deltaWave < 0.0D) {
			waveLimiter = waveActual;
		} else {
			waveLimiter = waveActual;
		}

		double at = waveLimiter - threshold;
		if (at < 0.0D)
			at = 0.0D;
		double bt = waveLimiter - at;
			
		double CurrentAttenuation = 0.0D;
		if (at > 0.0D)
			CurrentAttenuation = bt / waveLimiter;
		else if (at == 0.0D) {
			CurrentAttenuation = 1.0D;
		}

		theData *= CurrentAttenuation;
		
		return theData;

	}
}
