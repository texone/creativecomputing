package cc.creativecomputing.ies;

public class CCIEPhotometricData {
	/**
	 * Number of Vertical Angles
	 * <p>
	 * This integer value indicates the total number of vertical angles in
	 * the photometric data
	 */
	int num_vert_angles;
	/**
	 * Number of Horizontal Angles
	 * 
	 * This integer value indicates the total number of horizontal angles in
	 * the photometric data
	 */
	int num_horz_angles;
	/**
	 * Vertical Angles
	 * <p>
	 * This line enumerates the (floating point) vertical angles.
	 * <p>
	 * For Type C photometry, the first vertical angle will be either 0 or
	 * 90 degrees, and the last vertical angle will be either 90 or 180
	 * degrees.
	 * <p>
	 * For Type A or B photometry, the first vertical angle will be either
	 * -90 or 0 degrees, and the last vertical angle will be 90 degrees.
	 */
	float[] vert_angles;
	/**
	 * Horizontal Angles (Identifier Line 13)
	 * <p>
	 * This line enumerates the (floating point) horizontal angles.
	 * <p>
	 * For Type C photometry, the first value is (almost) always 0 degrees,
	 * and the last value is one of the following:
	 * <p>
	 * <ul>
	 * <li>0 There is only one horizontal angle, implying that the luminaire
	 * is laterally symmetric in all photometric planes.</li>
	 * <li>90 The luminaire is assumed to be symmetric in each quadrant.
	 * </li>
	 * <li>180 The luminaire is assumed to be bilaterally symmetric about
	 * the 0-180 degree photometric plane.</li>
	 * <li>360 The luminaire is assumed to exhibit no lateral symmetry.
	 * (NOTE: this is an error in the draft IES LM-63-1995 standard, because
	 * the 360-degree plane is coincident with the 0-degree plane. It should
	 * read "greater than 180 degrees and less than 360 degrees").</li>
	 * </ul>
	 * <p>
	 * (A luminaire that is bilaterally symmetric about the 90-270 degree
	 * photometric plane will have a first value of 90 degrees and a last
	 * value of 270 degrees.)
	 * <p>
	 * For Type A or B photometry where the luminaire is laterally symmetric
	 * about a vertical reference plane, the first horizontal angle will be
	 * 0 degrees, and the last horizontal angle will be 90 degrees.
	 * <p>
	 * For Type A or B photometry where the luminaire is not laterally
	 * symmetric about a vertical reference plane, the first horizontal
	 * angle will be -90 degrees, and the last horizontal angle will be 90
	 * degrees.
	 */
	float[] horz_angles;
	/**
	 * Candela Values (Identifier Lines 14 Through 17)
	 * <p>
	 * These lines enumerate the (floating point) candela values. There is
	 * one line for each corresponding horizontal angle, and one candela
	 * value for each corresponding vertical angle.
	 */
	float[][] pcandela;
	/**
	 * the type of photometric web used for the photometric measurements
	 */
	CCIEGonimeterType gonio_type;
}