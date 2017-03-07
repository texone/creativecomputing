package cc.creativecomputing.ies;

public class CCIELamp {
	/**
	 * number of lamps in the luminaire
	 */
	int num_lamps;
	/**
	 * Lumens Per Lamp
	 * <p>
	 * This floating point value indicates the rated lumens per lamp on
	 * which the photometric test was based. (This value is obtained from
	 * the lamp manufacturer's published technical data for the lamp, and
	 * does not represent the actual lumens emitted by the test lamp.)
	 * <p>
	 * If the luminaire has two or more lamps with different rated lumens
	 * per lamp, this value represents the average lumens per lamp for the
	 * luminaire.
	 * <p>
	 * In the (very rare) cases of absolute photometry, this value is -1.
	 */
	float lumens_lamp;
	/**
	 * Candela Multiplier
	 * <p>
	 * This floating point value indicates a multiplying factor that is to
	 * be applied to all candela values in the photometric data file
	 * (identifier lines 14 through 17).
	 */
	float multiplier;

	/** TILT file name pointer (optional) */
	String tilt_fname;

	/**
	 * If tilt is null the lamp output (presumably) does not vary as a
	 * function of the luminaire tilt angle otherwise the lamp output varies
	 * as a function of the luminaire tilt angle.
	 */
	CCIETiltData tilt;
}