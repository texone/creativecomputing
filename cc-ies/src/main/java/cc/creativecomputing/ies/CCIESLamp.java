package cc.creativecomputing.ies;

public class CCIESLamp {
	private final int _myNumberOfLamps;
	
	private final float _myLumensPerLamp;

	private final float _myMultiplier;

	private final CCIETiltData _myTilt;

	CCIESLamp(int theNumberOfLamps, float theLumensPerLamp, float theMultiplier, CCIETiltData theTiltData) {
		super();
		_myNumberOfLamps = theNumberOfLamps;
		_myLumensPerLamp = theLumensPerLamp;
		_myMultiplier = theMultiplier;
		_myTilt = theTiltData;
	}

	/**
	 * Returns the number of lamps in the luminaire
	 * 
	 * @return number of lamps in the luminaire
	 */
	public int numberOfLamps() {
		return _myNumberOfLamps;
	}

	/**
	 * Returns the rated lumens per lamp on which the photometric test was
	 * based. (This value is obtained from the lamp manufacturer's published
	 * technical data for the lamp, and does not represent the actual lumens
	 * emitted by the test lamp.)
	 * <p>
	 * If the luminaire has two or more lamps with different rated lumens per
	 * lamp, this value represents the average lumens per lamp for the
	 * luminaire.
	 * <p>
	 * In the (very rare) cases of absolute photometry, this value is -1.
	 * 
	 * @return lumens per lamp
	 */
	public float lumensPerLamp() {
		return _myLumensPerLamp;
	}

	/**
	 * Returns a multiplying factor that is to be applied to all candela values
	 * in the photometric data file.
	 * 
	 * @return candela multiplier
	 */
	public float multiplier() {
		return _myMultiplier;
	}

	/**
	 * If tilt is null the lamp output (presumably) does not vary as a function
	 * of the luminaire tilt angle otherwise the lamp output varies as a
	 * function of the luminaire tilt angle.
	 * 
	 * @return tilt data of the lamp
	 */
	public CCIETiltData tilt() {
		return _myTilt;
	}

}