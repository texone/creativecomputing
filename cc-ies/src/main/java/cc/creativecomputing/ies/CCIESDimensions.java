package cc.creativecomputing.ies;

/**
 * Luminous Opening Dimensions
 * <p>
 * While the term "luminous opening" is somewhat ambiguous for many
 * architectural luminaires and other light sources, it is useful in
 * calculating average luminaire luminances and modeling the luminaires as
 * homogeneous area light sources.
 * <p>
 * Nonrectangular Luminous Openings
 * <p>
 * The luminous opening is normally considered to be rectangular. However,
 * other predefined shapes can be modeled by specifying one or more of the
 * above dimensions as zero or negative floating point numbers as follows:
 * <p>
 * 
 * <pre>
 * Width  Length  Height  Description
 *
 *    0      0       0    Point
 *    w      l       h    Rectangular (default)
 *   -d      0       0    Circular (where d = diameter of circle)
 *   -d      0      -d    Sphere (where d = diameter of circle)
 *   -d      0       h    Vertical cylinder (where d = diameter of
 *                        cylinder)
 *    0      l      -d    Horizontal cylinder oriented along luminaire
 *                        length.
 *    w      0      -d    Horizontal cylinder oriented along luminaire
 *                        width.
 *   -w      l       h    Ellipse oriented along luminaire length.
 *    w     -l       h    Ellipse oriented along luminaire width.
 *   -w      l      -h    Ellipsoid oriented along luminaire length.
 *    w     -l      -h    Ellipsoid oriented along luminaire width.
 * </pre>
 * 
 * See IES LM-63-1995 for detailed descriptions and diagrams.
 * 
 * @author christianr
 *
 */
public class CCIEDimensions {
	/**
	 * Luminaire Opening Width
	 * <p>
	 * This floating point value indicates the distance across the luminous
	 * opening of the luminaire as measured along the 90-270 degree
	 * photometric plane.
	 */
	float width;
	/**
	 * Luminaire Opening Length
	 * <p>
	 * This floating point value indicates the distance across the luminous
	 * opening of the luminaire as measured along the 0-180 degree
	 * photometric plane.
	 */
	float length;
	/**
	 * Luminaire Height
	 * <p>
	 * This floating point value indicates the average height of the
	 * luminous opening of the luminaire as measured along the vertical
	 * axis.
	 */
	float height;
}