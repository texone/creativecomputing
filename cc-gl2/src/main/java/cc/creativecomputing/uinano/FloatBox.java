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

public class FloatBox extends NumberBox<Float> {
	public FloatBox(CCWidget parent) {
		this(parent, 0);
	}

	public FloatBox(CCWidget parent, float value) {
		super(parent, value, 0f, Float.MIN_VALUE, Float.MAX_VALUE, 0.1f, "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
	}

	@Override
	public String createFormat() {
		return null;
	}

	@Override
	public Float clamp(Float theValue, Float theMin, Float theMax) {
		return CCMath.clamp(theValue, theMin, theMax);
	}


	@Override
	public Float deltaChange(Float theVal, int theDelta, Float theIncrement) {
		return theVal + theDelta * theIncrement;
	}

	@Override
	public Float stringToValue(String theString) {
		return Float.parseFloat(theString);
	}

	@Override
	public Float increment(Float theValue, Float theIncrement) {
		return theValue + theIncrement;
	}

	@Override
	public Float decrement(Float theValue, Float theIncrement) {
		return theValue - theIncrement;
	}
}