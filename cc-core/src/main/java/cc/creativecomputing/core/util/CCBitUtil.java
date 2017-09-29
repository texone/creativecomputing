package cc.creativecomputing.core.util;

/**
 * Utility class for bit conversions
 * @author christianr
 *
 */
public class CCBitUtil {
	
	/**
	 * Returns the bit at the given position as integer
	 * @param theValue the value to read the bit from
	 * @param theBit the position
	 * @return
	 */
	public static int bit(int theValue, int theBit){
		return ((theValue >>  theBit * 8) & 0xff);
	}
	
	public static int combine(int the0, int the1, int the2, int the3) {
		return (the3 << 24) | (the2 << 16) | (the1 << 8) | the0;
	}
}
