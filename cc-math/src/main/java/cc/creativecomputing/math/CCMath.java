/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.math;

import cc.creativecomputing.math.interpolate.CCInterpolator;
import cc.creativecomputing.math.interpolate.CCInterpolators;
import cc.creativecomputing.math.random.CCFastRandom;
import cc.creativecomputing.math.random.CCRandom;

/**
 * <p>
 * This class contains methods for performing basic numeric operations such as the elementary exponential, 
 * logarithm, square root, and trigonometric functions.
 * </p> 
 * @author christianriekoff
 *
 */
public class CCMath {
	/**
     * Square root of 2
     */
    public static final double SQRT2 = (double) Math.sqrt(2);

    /**
     * Square root of 3
     */
    public static final double SQRT3 = (double) Math.sqrt(3);
    
    public static final double ALLOWED_DEVIANCE = 0.000001f;

    
	/** A "close to zero" double epsilon value for use*/
    public static final double DBL_EPSILON = 2.220446049250313E-16d;

    /** A "close to zero" double epsilon value for use*/
    public static final double FLT_EPSILON = 1.1920928955078125E-6f;

    /** A "close to zero" double epsilon value for use*/
    public static final double ZERO_TOLERANCE = 0.0001f;
    
	public static final double PI = (double) Math.PI;
	public static final double HALF_PI = PI / 2.0f;
	public static final double THIRD_PI = PI / 3.0f;
	public static final double QUARTER_PI = PI / 4.0f;
	public static final double TWO_PI = PI * 2.0f;
	
	public static final double ONE_THIRD = 1f / 3;

	public static final double DEG_TO_RAD = PI / 180.0f;
	public static final double RAD_TO_DEG = 180.0f / PI;

	public static final CCRandom RANDOM = new CCRandom();
	public static final CCFastRandom FAST_RANDOM = new CCFastRandom();
	
	 /**
     * Returns true if the number is a power of 2 (2,4,8,16...)
     * 
     * A good implementation found on the Java boards. note: a number is a power of two if and only if it is the
     * smallest number with that number of significant bits. Therefore, if you subtract 1, you know that the new number
     * will have fewer bits, so ANDing the original number with anything less than it will give 0.
     * 
     * @param number
     *            The number to test.
     * @return True if it is a power of two.
     */
    public static boolean isPowerOfTwo(final int number) {
        return number > 0 && (number & number - 1) == 0;
    }

    /**
     * @param number
     * @return the closest power of two to the given number.
     */
    public static int nearestPowerOfTwo(final int number) {
        return (int) Math.pow(2, Math.ceil(Math.log(number) / Math.log(2)));
    }
	
	public static double random(){
		return FAST_RANDOM.random();
	}

	public static float random(final float theMax) {
		return FAST_RANDOM.random(theMax);
	}

	public static double random(final double theMax) {
		return RANDOM.random(theMax);
	}

	public static float random(final float theMin, final float theMax) {
		return FAST_RANDOM.random(theMin, theMax);
	}

	public static double random(final double theMin, final double theMax) {
		return RANDOM.random(theMin, theMax);
	}

	public static int random(final int theMin, final int theMax) {
		return CCMath.round(RANDOM.random(theMin, theMax));
	}
	
	/**
	 * Returns random values using the given interpolator to change the distribution of random values
	 * @param theInterpolator the interpolator to change distribution
	 * @return random value 
	 */
	public static double random(CCInterpolator theInterpolator) {
		return RANDOM.random(theInterpolator);
	}
	
	public static double random(CCInterpolators theInterpolator) {
		return RANDOM.random(theInterpolator);
	}
	
	/**
	 * Returns random values using the given interpolator to change the distribution of random values
	 * @param theMin the min random value
	 * @param theMax the max random value
	 * @param theInterpolator the interpolator to change distribution
	 * @return random value 
	 */
	public static double random(double theMin, double theMax, CCInterpolator theInterpolator) {
		return RANDOM.random(theMin, theMax, theInterpolator);
	}
	
	public static double random(double theMin, double theMax, CCInterpolators theInterpolator) {
		return RANDOM.random(theMin, theMax, theInterpolator);
	}

	public static double gaussianRandom() {
		return FAST_RANDOM.gaussianRandom();
	}
	
	public static float gaussianRandom(final float theMax) {
		return FAST_RANDOM.gaussianRandom(theMax);
	}
	
	public static float gaussianRandom(final float theMin, final float theMax) {
		return FAST_RANDOM.gaussianRandom(theMin, theMax);
	}
	
	public static void randomSeed(final long theSeed) {
		FAST_RANDOM.randomSeed((int)theSeed);
		RANDOM.randomSeed(theSeed);
	}
	
	public static boolean chance(double c){
		return random()<=c;
	}

	/**
	 * returns the bitwise representation of a floating point number
	 */
	static public int floatToBits(float theValue) {
		return Float.floatToIntBits(theValue);
	}

	/**
	 * returns the absolute value of a floating point number in bitwise form
	 */
	static public int floatToAbsluteBits(float theValue) {
		return (floatToBits(theValue) & 0x7FFFFFFF);
	}

	/**
	 * returns the signal bit of a floating point number
	 */
	static public int signalBit(float theValue) {
		return floatToBits(theValue) & 0x80000000;
	}

    /**
	 * returns the value of 1.0f in bitwise form
	 */
	static public int oneInBits() {
		return 0x3F800000;
	}

	/**
	 *  Convert's an angle in radians to one in degrees.
	 *
	 *  @param  rad  The angle in radians to be converted.
	 *  @return The angle in degrees.
	 **/
	public static final double radiansToDegrees(double rad) {
		return rad * 180 / PI;
	}

	/**
	 *  Convert's an angle in degrees to one in radians.
	 *
	 *  @param  deg  The angle in degrees to be converted.
	 *  @return The angle in radians.
	 **/
	public static final double degreesToRadians(double deg) {
		return deg * PI / 180;
	}

	public static final double mag(double abc[]) {
		return Math.sqrt(abc[0] * abc[0] + abc[1] * abc[1] + abc[2]
				* abc[2]);
	}

	//////////////////////////////////////////////////////////////

	// MATH

	// lots of convenience methods for math with floats.
	
	public static final int abs(int theValue) {
		int y = theValue >> 31;
		return (theValue ^ y) - y;
	}

	public static final float abs(float theValue) {
		return theValue < 0 ? -theValue : theValue;
	}

	public static final double abs(double theValue) {
		return theValue < 0 ? -theValue : theValue;
	}

	static public final float sq(float a) {
		return a * a;
	}

	static public final double sq(double a) {
		return a * a;
	}

	static public final float sqrt(float a) {
		return (float) Math.sqrt(a);
	}

	static public final double sqrt(double a) {
		return Math.sqrt(a);
	}
	
	/**
	 * Returns 1/sqrt(fValue)
	 * 
	 * @param theValue The value to process.
	 * @return 1/sqrt(fValue)
	 * @see java.lang.Math#sqrt(double)
	 */
	public static double invSqrt(final double theValue) {
		return 1.0f / sqrt(theValue);
	}

	static public final double log(double a) {
		return Math.log(a);
	}
	
	static public final double log2(double a){
		return Math.log(a) / log(2);
	}
	
	static public final double log10(double a){
		return Math.log10(a);
	}

	static public final double exp(double a) {
		return Math.exp(a);
	}

	static public final float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}

	static public final double pow(double a, double b) {
		return Math.pow(a, b);
	}

	static public final int pow(int a, int b) {
		return (int)Math.pow(a, b);
	}

	static public final float max(float a, float b) {
		return Math.max(a, b);
//		return (a > b) ? a : b;
	}
	
	static public final long max(long a, long b) {
		//return Math.max(a, b);
		return (a > b) ? a : b;
	}

	static public final double max(double a, double b) {
		//return Math.max(a, b);
		return (a > b) ? a : b;
	}

	static public final float max(float a, float b, float c) {
		//return Math.max(a, Math.max(b, c));
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	static public final double max(double a, double b, double c) {
		//return Math.max(a, Math.max(b, c));
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}
	
	static public final float max(float...theValues) {
		float result = Float.MIN_VALUE;
		for(float myValue:theValues) {
			result = max(result,myValue);
		}
		return result;
	}
	
	static public final double max(double...theValues) {
		double result = Double.MIN_VALUE;
		for(double myValue:theValues) {
			result = max(result,myValue);
		}
		return result;
	}

	static public final float min(float a, float b) {
//		return Math.min(a, b);
		return (a < b) ? a : b;
	}

	static public final long min(long a, long b) {
//		return Math.min(a, b);
		return (a < b) ? a : b;
	}

	static public final double min(double a, double b) {
		//return Math.min(a, b);
		return (a < b) ? a : b;
	}

	static public final double min(double a, double b, double c) {
		//return Math.min(a, Math.min(b, c));
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}
	/**
	 * constrains a value to a range between two floats.
	 * @param value
	 * @param theMin minimum output value
	 * @param theMax maximum output value
	 * @return
	 */
	static public float clamp(float value, float theMin, float theMax){
		return CCMath.max(theMin,CCMath.min(value,theMax));
	}

	static public double clamp(double value, double theMin, double theMax){
		return CCMath.max(theMin,CCMath.min(value,theMax));
	}

	static public int clamp(int value, int theMin, int theMax){
		return CCMath.max(theMin,CCMath.min(value,theMax));
	}
	
	static public final double min(double...theValues) {
		double result = Float.MAX_VALUE;
		for(double myValue:theValues) {
			result = min(result,myValue);
		}
		return result;
	}

	/**
	 * Blends between a start and an end value according to the given blend.
	 * The blend parameter is the amount to interpolate between the two values 
	 * where 0.0 equal to the first point, 0.1 is very near the first point, 
	 * 0.5 is half-way in between, etc. The blend function is convenient for 
	 * creating motion along a straight path and for drawing dotted lines.
	 * 
	 * @param theStart first value
	 * @param theStop second value
	 * @param theBlend between 0.0 and 1.0
	 * @return
	 */
	static public final float blend(final float theStart, final float theStop, final float theBlend) {
		return theStart + (theStop - theStart) * theBlend;
	}
	/**
	 * Blends between a start and an end value according to the given blend.
	 * The blend parameter is the amount to interpolate between the two values 
	 * where 0.0 equal to the first point, 0.1 is very near the first point, 
	 * 0.5 is half-way in between, etc. The blend function is convenient for 
	 * creating motion along a straight path and for drawing dotted lines.
	 * 
	 * @param theStart first value
	 * @param theStop second value
	 * @param theBlend between 0.0 and 1.0
	 * @return
	 */
	static public final double blend(final double theStart, final double theStop, final double theBlend) {
		return theStart + (theStop - theStart) * theBlend;
	}
	/**
	 * Blends between a start and an end value according to the given blend.
	 * The blend parameter is the amount to interpolate between the two values 
	 * where 0.0 equal to the first point, 0.1 is very near the first point, 
	 * 0.5 is half-way in between, etc. The blend function is convenient for 
	 * creating motion along a straight path and for drawing dotted lines.
	 * 
	 * @param theStart first value
	 * @param theStop second value
	 * @param theBlend between 0.0 and 1.0
	 * @return
	 */
	static public final long blend(final long theStart, final long theStop, final double theBlend) {
		return (long)(theStart + (theStop - theStart) * theBlend);
	}
	/**
	 * Blends between a start and an end value according to the given blend.
	 * The blend parameter is the amount to interpolate between the two values 
	 * where 0.0 equal to the first point, 0.1 is very near the first point, 
	 * 0.5 is half-way in between, etc. The blend function is convenient for 
	 * creating motion along a straight path and for drawing dotted lines.
	 * 
	 * @param theStart first value
	 * @param theStop second value
	 * @param theBlend between 0.0 and 1.0
	 * @return
	 */
	static public final double[] blend(final double[] theStart, final double[] theStop, final double theBlend) {
		double[] result = new double[min(theStart.length, theStop.length)];
		for(int i = 0; i < result.length; i++){
			result[i] = blend(theStart[i], theStop[i], theBlend);
		}
		return result;
	}
	
	static public double blend(double theBlendU, double theBlendV, double theA, double theB, double theC) {
		// Compute vectors        
		double v0 = theC - theA;
		double v1 = theB - theA;
		
		double myResult = theA;
		myResult += v0 * theBlendU;
		myResult += v1 * theBlendV;
		return myResult;
} 

	/**
	 * <p>Normalizes a number from another range into a value between 0 and 1.</p>
	 * <p>Identical to map(value, low, high, 0, 1);</p>
	 * <p>Numbers outside the range are not clamped to 0 and 1, because out-of-range 
	 * values are often intentional and useful.</p>
	 * 
	 * @param theValue The incoming value to be converted
	 * @param theMin Lower bound of the value's current range
	 * @param theMax Upper bound of the value's current range
	 * @return
	 */
	static public final float norm(final float theValue, final float theMin, final float theMax) {
		return (theValue - theMin) / (theMax - theMin);
	}
	/**
	 * <p>Normalizes a number from another range into a value between 0 and 1.</p>
	 * <p>Identical to map(value, low, high, 0, 1);</p>
	 * <p>Numbers outside the range are not clamped to 0 and 1, because out-of-range 
	 * values are often intentional and useful.</p>
	 * 
	 * @param theValue The incoming value to be converted
	 * @param theMin Lower bound of the value's current range
	 * @param theMax Upper bound of the value's current range
	 * @return
	 */
	static public final double norm(final double theValue, final double theMin, final double theMax) {
		return (theValue - theMin) / (theMax - theMin);
	}

	/**
	 * <p>
	 * Re-maps a number from one range to another. In the example above, the number '25' 
	 * is converted from a value in the range 0..100 into a value that ranges from the 
	 * left edge (0) to the right edge (width) of the screen.</p>
	 * <p>
	 * Numbers outside the range are not clamped to 0 and 1, because out-of-range values 
	 * are often intentional and useful.</p>
	 * @param theValue The incoming value to be converted
	 * @param theMinSrc Lower bound of the value's current range
	 * @param theMaxSrc Upper bound of the value's current range
	 * @param theMinDst Lower bound of the value's target range
	 * @param theMaxDst Upper bound of the value's target range
	 * @return
	 */
	static public float map(final float theValue, final float theMinSrc, final float theMaxSrc, final float theMinDst, final float theMaxDst) {
		return blend(theMinDst, theMaxDst, norm(theValue, theMinSrc, theMaxSrc));
	}

	static public double map(final double theValue, final double theMinSrc, final double theMaxSrc, final double theMinDst, final double theMaxDst) {
		return blend(theMinDst, theMaxDst, norm(theValue, theMinSrc, theMaxSrc));
	}
	
	/**
	 * Performs smooth Hermite interpolation between 0 and 1 when edge0 < x < edge1. 
	 * This is useful in cases where a threshold function with a smooth transition is desired.
	 * <p>
	 * For values of x between min and max, returns a smoothly varying value 
	 * that ranges from 0 at x = min to 1 at x = max. x is clamped to the 
	 * range [min, max] and then the interpolation formula is evaluated:
	 * -2*((x-min)/(max-min))3 + 3*((x-min)/(max-min))2
	 * @param theEdge0 Specifies the value of the lower edge of the Hermite function.
	 * @param theEdge1 Specifies the value of the upper edge of the Hermite function.
	 * @param theValue Specifies the source value for interpolation.
	 * @return
	 */
	static public final double smoothStep(final double theEdge0, final double theEdge1, final double theValue) {
		if (theValue <= theEdge0)
			return 0;
		if (theValue >= theEdge1)
			return 1;
		
		return 3 * pow((theValue-theEdge0)/(theEdge1-theEdge0), 2) - 2 * pow((theValue-theEdge0)/(theEdge1-theEdge0), 3);
	}
	
	/**
	 * Performs smooth linear interpolation between 0 and 1 when edge0 < x < edge1. 
	 * This is useful in cases where a threshold function with a smooth transition is desired.
	 * @param theEdge0 Specifies the value of the lower edge of the linear function.
	 * @param theEdge1 Specifies the value of the upper edge of the linear function.
	 * @param theValue Specifies the source value for interpolation.
	 * @return
	 */
	static public final double linearStep(final double theEdge0, final double theEdge1, final double theValue) {
		if (theValue <= theEdge0)
			return 0;
		if (theValue >= theEdge1)
			return 1;
		
		return map(theValue, theEdge0, theEdge1, 0, 1);
	}
	
	/**
	 * Generates a step function by comparing x to edge.
	 * 0.0 is returned if x < edge, and 1.0 is returned otherwise.
	 * @param theEdge specifies the location of the edge of the step function.
	 * @param theValue specify the value to be used to generate the step function.
	 * @return
	 */
	static public final double step(double theEdge, double theValue) {
		return theValue < theEdge ? 0 : 1;
	}

	/**
	 * Constrains a value to not exceed a maximum and minimum value.
	 * @param theValue the value to constrain
	 * @param theMin minimum limit
	 * @param theMax maximum limit
	 * @return the constrained value
	 */
	static public final float constrain(final float theValue, final float theMin, final float theMax) {
		return (theValue < theMin) ? theMin : ((theValue > theMax) ? theMax : theValue);
	}

	/**
	 * Constrains a value to not exceed a maximum and minimum value.
	 * @param theValue the value to constrain
	 * @param theMin minimum limit
	 * @param theMax maximum limit
	 * @return the constrained value
	 */
	static public final double constrain(final double theValue, final double theMin, final double theMax) {
		return (theValue < theMin) ? theMin : ((theValue > theMax) ? theMax : theValue);
	}

	/**
	 * Constrains a value to not exceed a maximum and minimum value.
	 * @param theValue the value to constrain
	 * @param theMin minimum limit
	 * @param theMax maximum limit
	 * @return the constrained value
	 */
	static public final int constrain(final int theValue, final int theMinimum, final int theMaximum) {
		return (theValue < theMinimum) ? theMinimum : ((theValue > theMaximum) ? theMaximum : theValue);
	}
	
	/**
	 * Constrains the given value to be between 0 and 1
	 * @param theValue the value to constrain
	 * @return the saturated value
	 */
	public static float saturate(float theValue) {
		return constrain(theValue, 0, 1);
	}
	
	/**
	 * Constrains the given value to be between 0 and 1
	 * @param theValue the value to constrain
	 * @return the saturated value
	 */
	public static double saturate(double theValue) {
		return constrain(theValue, 0, 1);
	}

	static public final int max(int a, int b) {
		return (a > b) ? a : b;
	}
	
	static public final int max(int...theValues) {
		int result = Integer.MIN_VALUE;
		for(int myValue:theValues) {
			result = max(result,myValue);
		}
		return result;
	}
	
	private static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

	static public final byte max(byte a, byte b) {
		return (unsignedByteToInt(a) > unsignedByteToInt(b)) ? a : b;
	}

	static public final int max(int a, int b, int c) {
		return (a > b) ? ((a > c) ? a : c) : ((b > c) ? b : c);
	}

	static public final int min(int a, int b) {
		return (a < b) ? a : b;
	}

	static public final byte min(byte a, byte b) {
		return (unsignedByteToInt(a) < unsignedByteToInt(b)) ? a : b;
	}

	static public final int min(int...theValues) {
		int result = Integer.MAX_VALUE;
		for(int myValue:theValues) {
			result = min(result,myValue);
		}
		return result;
	}

	static public final int min(int a, int b, int c) {
		return (a < b) ? ((a < c) ? a : c) : ((b < c) ? b : c);
	}

	public final static float sin(float angle) {
		return (float) sin((double)angle);
	}

	public static double sin(double angle) {
		return Math.sin(angle);
	}

	public final static float cos(float angle) {
		return (float) cos((double)angle);
	}

	public static double cos(double angle) {
		return Math.cos(angle);
	}

	public static final double tan(double angle) {
		return Math.tan(angle);
	}

	public static final float asin(float value) {
		return (float) Math.asin(value);
	}

	public static final double asin(double value) {
		return Math.asin(value);
	}

	public static final float acos(float value) {
		return (float) Math.acos(value);
	}

	public static final double acos(double value) {
		return Math.acos(value);
	}

	public static final float atan(float value) {
		return (float) Math.atan(value);
	}

	public static final double atan(double value) {
		return Math.atan(value);
	}

	public static final float atan2(float a, float b) {
		return (float) Math.atan2(a, b);
	}

	public static final double atan2(double a, double b) {
		return Math.atan2(a, b);
	}

	static public final float degrees(float radians) {
		return radians * (float)RAD_TO_DEG;
	}

	static public final double degrees(double radians) {
		return radians * RAD_TO_DEG;
	}

	static public final float radians(float degrees) {
		return degrees * (float)DEG_TO_RAD;
	}

	static public final double radians(double degrees) {
		return degrees * CCMath.DEG_TO_RAD;
	}

	static public final int ceil(float theValue) {
		return (int)Math.ceil(theValue);
	}

	static public final int ceil(double theValue) {
		return (int)Math.ceil(theValue);
	}

	static public final int floor(float theValue) {
		return (int)Math.floor(theValue);
	}

	static public final int floor(double theValue) {
		return (int)Math.floor(theValue);
	}

	static public final int round(float theValue) {
		return Math.round(theValue);
	}

	static public final int round(double theValue) {
		return (int) Math.round(theValue);
	}
	
	public static double floorMod(double theA, double theB){
		return (theA % theB + theB) % theB;
	}
	
	public static double frac(double theValue) {
		return (theValue - CCMath.floor(theValue));
	}

	/**
	 * Round a double value to a specified number of decimal places. 
	 * @param val the value to be rounded.
	 * @param places the number of decimal places to round to.
	 * @return val rounded to places decimal places.
	 */
	public static double round(double val, int places) {
		long factor = (long) Math.pow(10, places);

		// Shift the decimal the correct number of places
		// to the right.
		val = val * factor;

		// Round to the nearest integer.
		long tmp = Math.round(val);

		// Shift the decimal the correct number of places
		// back to the left.
		return (double) tmp / factor;
	}

	/**
	 * Round a double value to a specified number of decimal places.
	 * @param val the value to be rounded.
	 * @param places the number of decimal places to round to.
	 * @return val rounded to places decimal places.
	 */
	public static double round(float val, int places) {
		return (float) round((double) val, places);
	}

	static public final double mag(double a, double b) {
		return Math.sqrt(a * a + b * b);
	}

	static public final double mag(double a, double b, double c) {
		return Math.sqrt(a * a + b * b + c * c);
	}

	static public final double dist(double x1, double y1, double x2, double y2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	static public final double dist(double x1, double y1, double z1, double x2,
			double y2, double z2) {
		return sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
	}

	/**
	 * Returns the sign of the given number so 1 if the value
	 * is bigger or equal than zero otherwise -1;
	 * @param theValue value to check for the sign
	 * @return sign of the given value
	 */
	static public final int sign(final float theValue) {
		if (theValue < 0)
			return -1;
		return 1;
	}

	/**
	 * Returns the sign of the given number so 1 if the value
	 * is bigger or equal than zero otherwise -1;
	 * @param theValue value to check for the sign
	 * @return sign of the given value
	 */
	static public final byte sign(final double theValue) {
		if (theValue < 0)
			return -1;
		return 1;
	}
	
	static public boolean sameSign(final double theValue1, final double theValue2){
		return sign(theValue1) == sign(theValue2);
	}

	public static int leastCommonMultiple(int theA, int theB) {
		int ggtZahl1 = theA;
		int ggtZahl2 = theB;
		
		while (ggtZahl2 != 0) { // Berechnung des ggT
			int temp = ggtZahl1;
			ggtZahl1 = ggtZahl2;
			ggtZahl2 = temp % ggtZahl1;
		}
		// Berechnung und Rueckgabe des kgVs
		return Math.abs(theA / ggtZahl1 * theB);
	}
	
	public static int leasCommonMultiple(int...theValues) {
		int result = 1;
		for(int myValue:theValues) {
			result = leasCommonMultiple(result,myValue);
		}
		return result;
	}

	/**
	 * @param theLastX
	 * @param theF
	 * @param theF2
	 * @param theF3
	 * @param theT
	 * @return
	 */
	static public double bezierPoint(double a, double b, double c, double d, double t) {
		double t1 = 1.0f - t;
		return 
			a * t1 * t1 * t1 + 
			3 * b * t * t1 * t1 + 
			3 * c * t * t * t1 + 
			d * t * t * t;
	}
	
	/**
	 * Interpolate a spline between at least 4 control points following the Catmull-Rom equation.
     * here is the interpolation matrix
     * m = [ 0.0  1.0  0.0   0.0 ]
     *     [-T    0.0  T     0.0 ]
     *     [ 2T   T-3  3-2T  -T  ]
     *     [-T    2-T  T-2   T   ]
     * where T is the curve tension
     * the result is a value between p1 and p2, t=0 for p1, t=1 for p2
     * @param theU value from 0 to 1
     * @param theT The tension of the curve
     * @param theP0 control point 0
     * @param theP1 control point 1
     * @param theP2 control point 2
     * @param theP3 control point 3
     * @return catmull-Rom interpolation
     */
    public static double catmullRomBlend(double theP0, double theP1, double theP2, double theP3, double theU, double theT) {
        double c1, c2, c3, c4;
        c1 = theP1;
        c2 = -1.0 * theT * theP0 + theT * theP2;
        c3 = 2 * theT * theP0 + (theT - 3) * theP1 + (3 - 2 * theT) * theP2 + -theT * theP3;
        c4 = -theT * theP0 + (2 - theT) * theP1 + (theT - 2) * theP2 + theT * theP3;

        return ((c4 * theU + c3) * theU + c2) * theU + c1;
    }
    
    public static double cubicBlend(
    	double theV0, double theV1,
    	double theV2, double theV3,
    	double theBlend
    ){
    	double mu2 = theBlend * theBlend;
    	double a0 = theV3 - theV2 - theV0 + theV1;
    	double a1 = theV0 - theV1 - a0;
    	double a2 = theV2 - theV0;
    	double a3 = theV1;
    	
    	return(
    		a0 * theBlend * mu2 + 
    		a1 * mu2 + 
    		a2 * theBlend + 
    		a3
    	);
    }
    
    public static double smoothCubicBlend(
    	double theV0, double theV1,
    	double theV2, double theV3,
    	double theBlend
    ){
		double mu2 = theBlend * theBlend;
		double a0 = -0.5 * theV0 + 1.5 * theV1 - 1.5 * theV2 + 0.5 * theV3;
		double a1 = theV0 - 2.5 * theV1 + 2 * theV2 - 0.5 * theV3;
		double a2 = -0.5 * theV0 + 0.5 * theV2;
		double a3 = theV1;
    	
    	return(
    		a0 * theBlend * mu2 + 
    		a1 * mu2 + 
    		a2 * theBlend + 
    		a3
    	);
    }
    
    /**
     * Hermite interpolation like cubic requires 4 points so that it can achieve a higher degree of continuity. 
     * In addition it has nice tension and biasing controls. Tension can be used to tighten up the curvature at 
     * the known points. The bias is used to twist the curve about the known points.
     * @param theV0
     * @param theV1
     * @param theV2
     * @param theV3
     * @param theBlend
     * @param tension 1 is high, 0 normal, -1 is low
     * @param bias 0 is even, positive is towards first segment, negative towards the other
     * @return
     */
	public static double hermiteBlend(
		double theV0, double theV1, double theV2, double theV3, 
		double theBlend, 
		double tension, 
		double bias
	) {

		double mu2 = theBlend * theBlend;
		double mu3 = mu2 * theBlend;
		double m0 = (theV1 - theV0) * (1 + bias) * (1 - tension) / 2;
		m0 += (theV2 - theV1) * (1 - bias) * (1 - tension) / 2;
		double m1 = (theV2 - theV1) * (1 + bias) * (1 - tension) / 2;
		m1 += (theV3 - theV2) * (1 - bias) * (1 - tension) / 2;
		
		double a0 = 2 * mu3 - 3 * mu2 + 1;
		double a1 = mu3 - 2 * mu2 + theBlend;
		double a2 = mu3 - mu2;
		double a3 = -2 * mu3 + 3 * mu2;

		return (a0 * theV1 + a1 * m0 + a2 * m1 + a3 * theV2);
	}
	
	public static double hermiteBlend(
		double theV0, double theV1, double theV2, double theV3, 
		double theBlend
	){
		return hermiteBlend(theV0, theV1, theV2, theV3, theBlend, 0, 0);
	}
	
	//////////////////////////////////////////////////////////////////
	//
	// function to shape value ranges between 0 and 1 or do easing
	//
	//////////////////////////////////////////////////////////////////
	
	/**
	 * Use this function to shape a linear increase from 0 to 1 to a curved one.
	 * Higher exponents result in steeper curves.
	 * @param theValue the value to shape
	 * @param theExponent the exponent for shaping the output
	 * @return the shaped value
	 */
	public static double shapeExponential(final double theValue, final double theExponent) {
		return shapeExponential(theValue, 0.5, theExponent);
	}
	
	/**
	 * Use this function to shape a linear increase from 0 to 1 to a curved one.
	 * Higher exponents result in steeper curves.
	 * @param theValue the value to shape
	 * @param theExponent the exponent for shaping the output
	 * @return the shaped value
	 */
	public static double shapeExponential(final double theValue, final double theBreakPoint, final double theExponent) {
		if(theValue < 0) return 0;
		if(theValue > 1) return 1;
		
		if(theValue < 0.5) {
			return theBreakPoint * CCMath.pow(2 * theValue,theExponent);
		}
		
		return (1 - (1 - theBreakPoint) * CCMath.pow(2 * (1 - theValue),theExponent)) ;
	}
	
	/**
	 * Use this function to shape a linear increase from 0 to 1 to a curved one.
	 * Higher exponents result in steeper curves.
	 * @param theValue the value to shape
	 * @param theExponent the exponent for shaping the output
	 * @return the shaped value
	 */
	public static float shapeExponential(final float theValue, final float theExponent) {
		return shapeExponential(theValue, 0.5f, theExponent);
	}
	
	/**
	 * Use this function to shape a linear increase from 0 to 1 to a curved one.
	 * Higher exponents result in steeper curves.
	 * @param theValue the value to shape
	 * @param theExponent the exponent for shaping the output
	 * @return the shaped value
	 */
	public static float shapeExponential(final float theValue, final float theBreakPoint, final float theExponent) {
		if(theValue < 0) return 0;
		if(theValue > 1) return 1;
		
		if(theValue < 0.5f) {
			return theBreakPoint * CCMath.pow(2 * theValue,theExponent);
		}
		
		return (1 - (1 - theBreakPoint) * CCMath.pow(2 * (1 - theValue),theExponent)) ;
	}

	/**
	 * Use this method to average a value. This is useful if you want to buffer
	 * value changes. The smaller the factor is the slower the value reacts to 
	 * changes. 
	 * @param theOldValue value you had so far
	 * @param theNewValue new value
	 * @param theFactor influence of the new value to the average value
	 * @return averaged value based on the two given values and the factor
	 */
	public static float bufferedAverage(float theOldValue, float theNewValue, float theFactor) {
		return theOldValue * (1f - theFactor) + theNewValue * theFactor;
	}

	/**
	 * Use this method to average a value. This is useful if you want to buffer
	 * value changes. The smaller the factor is the slower the value reacts to 
	 * changes. 
	 * @param theOldValue value you had so far
	 * @param theNewValue new value
	 * @param theFactor influence of the new value to the average value
	 * @return averaged value based on the two given values and the factor
	 */
	public static double bufferedAverage(double theOldValue, double theNewValue, double theFactor) {
		return theOldValue * (1f - theFactor) + theNewValue * theFactor;
	}
	
	/**
	 * Use this function to get the average of all given values.
	 * @param theValues values to average
	 * @return average of the given values
	 */
	public static float average(float...theValues) {
		float theSum = 0;
		for(float myValue:theValues) {
			theSum += myValue;
		}
		return theSum / theValues.length;
	}
	
	/**
	 * Use this function to get the average of all given values.
	 * @param theValues values to average
	 * @return average of the given values
	 */
	public static double average(double...theValues) {
		double theSum = 0;
		for(double myValue:theValues) {
			theSum += myValue;
		}
		return theSum / theValues.length;
	}

	/**
	 * compute the maximum number of digits
	 */
	public static int countDigits(double theValue) {
		int myDigits = 1;
		int myTemp = 10;

		while (true) {
			if (theValue >= myTemp) {
				myDigits++;
				myTemp *= 10;
			} else
				break;
		}
		return myDigits;
	}

	/**
	 * Checks if the given value is between the two given borders
	 * @param theValue the value to check
	 * @param theBorder1 the first border
	 * @param theBorder2 the second border
	 * @return <code>true</code> if the value is between the two given border values other wise <code>false</code>
	 */
	public static boolean isInBetween(double theValue, double theBorder1, double theBorder2) {
		return theValue > theBorder1 && theValue < theBorder2 || theValue < theBorder1 && theValue > theBorder2;
	}
	
	public static boolean isNaN(double...theValues){
		for(double myValue:theValues){
			if(Double.isNaN(myValue))return true;
		}
		return false;
	}

	/** sqrt(a^2 + b^2) without under/overflow. **/

   public static double hypot(double a, double b) {
      double r;
      if (Math.abs(a) > Math.abs(b)) {
         r = b/a;
         r = Math.abs(a)*Math.sqrt(1+r*r);
      } else if (b != 0) {
         r = a/b;
         r = Math.abs(b)*Math.sqrt(1+r*r);
      } else {
         r = 0.0;
      }
      return r;
   }
}
