package cc.creativecomputing.uinano.layout;

/**
 * The different kinds of alignments a layout can perform.
 * 
 * @author christianr
 *
 */
public enum Alignment {
	/**
	 * Take only as much space as is required.
	 */
	Minimum,
	/**
	 * Center align.
	 */
	Middle,
	/**
	 * Take as much space as is allowed.
	 */
	Maximum,
	/**
	 * Fill according to preferred sizes.
	 */
	Fill;

}