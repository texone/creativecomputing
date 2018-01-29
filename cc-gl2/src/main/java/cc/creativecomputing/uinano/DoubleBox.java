package cc.creativecomputing.uinano;

import cc.creativecomputing.math.CCMath;

/**
 * \class IntBox textbox.h nanogui/textbox.h
 *
 * \brief A specialization of TextBox for representing integral values.
 *
 * Template parameters should be integral types, e.g. ``int``, ``long``,
 * ``uint32_t``, etc.
 */

public class DoubleBox extends NumberBox<Double> {
	public DoubleBox(CCWidget parent) {
		this(parent, 0);
	}

	public DoubleBox(CCWidget parent, double value) {
		super(parent, value, 0d, -(Double.MAX_VALUE - 1d), Double.MAX_VALUE, 0.1, "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
	}

	@Override
	public String createFormat() {
		return null;
	}

	@Override
	public Double clamp(Double theValue, Double theMin, Double theMax) {
		return CCMath.clamp(theValue, theMin, theMax);
	}


	@Override
	public Double deltaChange(Double theVal, int theDelta, Double theIncrement) {
		return theVal + theDelta * theIncrement;
	}

	@Override
	public Double stringToValue(String theString) {
		return Double.parseDouble(theString);
	}

	@Override
	public Double increment(Double theValue, Double theIncrement) {
		return theValue + theIncrement;
	}

	@Override
	public Double decrement(Double theValue, Double theIncrement) {
		return theValue - theIncrement;
	}
}