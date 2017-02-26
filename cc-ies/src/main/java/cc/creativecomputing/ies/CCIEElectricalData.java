package cc.creativecomputing.ies;

public class CCIEElectricalData {
	/**
	 * Ballast Factor
	 * <p>
	 * This floating point value indicates the ratio of the lamp lumens when
	 * operated on a commercially-available ballast, to the rated lamp
	 * lumens as measured by the lamp manufacturer using a standard
	 * (reference) ballast.
	 * <p>
	 * All candela values in the photometric data file (identifier lines 14
	 * through 17) must be multiplied by the ballast factor before the
	 * candela values are used in an application program.
	 */
	float ball_factor;
	/**
	 * Ballast-Lamp Photometric Factor / Future Use
	 * <p>
	 * In LM-63-1986 and LM-63-1991, this floating point value indicates the
	 * ratio of the lamp lumen output using the given ballast and lamp type
	 * used to generate a photometric report, to the lumen output of the
	 * same luminaire with the ballast and lamp type used for photometric
	 * testing.
	 * <p>
	 * In LM-63-1995, it was recognized that most lighting manufacturers
	 * incorporate the ballast-lamp photometric factor in the preceding
	 * ballast factor and set the ballast-lamp photometric factor to unity.
	 * Consequently, the ballast-lamp photometric factor was designated as
	 * being for future use and the value set to unity to be compatible with
	 * previous releases of LM-63.
	 * <p>
	 * All candela values in the photometric data file (identifier lines 14
	 * through 17) must be multiplied by the ballast-lamp photometric factor
	 * before the candela values are used in an application program.
	 */
	float blp_factor;
	/**
	 * Input Watts
	 * <p>
	 * This floating point value indicates the total power (measured in
	 * watts) consumed by the luminaire, as measured during the photometric
	 * test.
	 * <p>
	 * (The input watts value is *not* adjusted by the ballast factor or
	 * ballast-lamp photometric factor, even though the power consumption of
	 * a luminaire may change if the measured candela values are modified.)
	 */
	float input_watts;
}