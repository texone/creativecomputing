package cc.creativecomputing.ies;

/**
 * The lamp output may vary as a function of the luminaire tilt angle. If
 * so, then the photometric data file may provide photometric data
 * multipliers for various tilt angles.
 * 
 * @author christianr
 *
 */
public class CCIETiltData {
	/**
	 * indicates the orientation of the lamp within the luminaire
	 */
	CCIELampOrientation orientation;
	/**
	 * indicates the total number of lamp tilt angles and their
	 * corresponding candela multiplying factors
	 */
	int num_pairs;
	/**
	 * lamp tilt angles
	 */
	double[] angles;
	/**
	 * candela multiplying factors for the corresponding lamp tilt angles
	 */
	double[] mult_factors;
}