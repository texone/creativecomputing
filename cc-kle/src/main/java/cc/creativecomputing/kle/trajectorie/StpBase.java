package cc.creativecomputing.kle.trajectorie;

import cc.creativecomputing.math.CCMath;

public abstract class StpBase {

	public static double MIN_VALUE_EQ_ZERO  = (1.e-8);
	

	/**
	 * \brief Returns true if x is close to zero.
	 * If the absolute value of the passed argument is less or equal to
	 * MIN_VALUE_EQ_ZERO, true is returned.
	 */
	public static  boolean isZero(double x) {return CCMath.abs(x) < MIN_VALUE_EQ_ZERO; };

	/**
	 * \brief Returns true if x is close to or greater than zero.
	 * If the absolute value of the passed argument is at least 
	 *  -(MIN_VALUE_EQ_ZERO), true is returned.
	 */
	public static  boolean isPositive(double x) { return x > -MIN_VALUE_EQ_ZERO; };

	/**
	 * \brief Returns true if x is close to or smaller than zero.
	 * If the absolute value of the passed argument is at smaller than 
	 *  +(MIN_VALUE_EQ_ZERO), true is returned.
	 */
	public static  boolean isNegative(double x) { return x < MIN_VALUE_EQ_ZERO; };
	
	public abstract double getDuration();

	public abstract double getEndOfCruisingTime();

	public abstract double pos(double t);

	public abstract double vel(double t);

	public double acc(double t){
		return 0;
	}

	public double jerk(double t){
		return 0;
	}

	/// scale a planned profile to a longer duration
	public abstract double scaleToDuration(double newDuration);
	
	public abstract void move(double t, StpData theData);
};