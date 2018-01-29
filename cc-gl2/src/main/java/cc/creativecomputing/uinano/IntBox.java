package cc.creativecomputing.uinano;

import java.util.*;

import cc.creativecomputing.math.CCMath;

/**
 * \class IntBox textbox.h nanogui/textbox.h
 *
 * \brief A specialization of TextBox for representing integral values.
 *
 * Template parameters should be integral types, e.g. ``int``, ``long``,
 * ``uint32_t``, etc.
 */

public class IntBox extends NumberBox<Integer> {
	public IntBox(CCWidget parent) {
		this(parent, 0);
	}

	public IntBox(CCWidget parent, int value) {
		super(parent, value, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, "[-]?[0-9]*");
	}

	@Override
	public String createFormat() {
		return null;
	}

	@Override
	public Integer clamp(Integer theValue, Integer theMin, Integer theMax) {
		return CCMath.clamp(theValue, theMin, theMax);
	}


	@Override
	public Integer deltaChange(Integer theVal, int theDelta, Integer theIncrement) {
		return theVal + theDelta * theIncrement;
	}

	@Override
	public Integer stringToValue(String theString) {
		return Integer.parseInt(theString);
	}

	@Override
	public Integer increment(Integer theValue, Integer theIncrement) {
		return theValue + theIncrement;
	}

	@Override
	public Integer decrement(Integer theValue, Integer theIncrement) {
		return theValue - theIncrement;
	}
}