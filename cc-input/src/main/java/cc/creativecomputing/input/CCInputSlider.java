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
package cc.creativecomputing.input;

import net.java.games.input.Component;

/**
 * The slider class is for analog input elements having a value range. Normally this range goes from -1 to 1. You can
 * set a multiplier to increase this range, this is useful so that you do not have to change the values in your
 * application. You can get the actual value and the total value of a slider. The actual value gives you the current
 * state of the controller. For the total value the actual values for each frame are add. If you not want a slider to
 * react up to a certain value you can set a tolerance value.
 * 
 * @see CCInputDevice
 * @see CCInputButton
 */
public class CCInputSlider extends CCInput {

	/**
	 * The total Value of the slider
	 */
	protected float _myTotalValue = 0f;

	/**
	 * Tolerance is minimum under which the input is set to zero.
	 */
	protected float _myTolerance = 0f;

	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the multiplier you can increase and
	 * decrease this range.
	 */
	protected float _myMultiplier = 1.f;
	
	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the power you can change the way
	 * the value increases and decreases.
	 */
	protected float _myPower = 1f;

	/**
	 * Initializes a new slider.
	 * 
	 * @param theComponent
	 */
	CCInputSlider(final Component theComponent) {
		super(theComponent);
	}

	/**
	 * For the total value the values for each frame are add. Use this method to get the total value of a slider.
	 * 
	 * @return float, the total value of a slider
	 * @see #value()
	 * @see #reset()
	 */
	public float totalValue() {
		return _myTotalValue;
	}

	/**
	 * For the total value the actual values for each frame are add. Use this method to set the total value to 0.
	 * 
	 * @see #totalValue()
	 */
	public void reset() {
		_myTotalValue = 0;
	}

	/**
	 * If you not want a slider to react up to a certain value you can set a tolerance value. Use this method to
	 * retrieve the set tolerance. By default this value is set to 0.
	 * 
	 * @see #tolerance(float)
	 */
	public float tolerance() {
		return _myTolerance;
	}

	/**
	 * If you not want a slider to react up to a certain value you can set a tolerance value. Use this method to set the
	 * tolerance. By default this value is set to 0.
	 * 
	 * @param theTolerance float, the new tolerance for the slider
	 * @see #tolerance()
	 */
	public void tolerance(final float theTolerance) {
		_myTolerance = theTolerance;
	}

	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the multiplier you can increase and
	 * decrease this range. Use this method to get the actual multiplier. By default this value is 1.0.
	 * 
	 * @return the actual multiplier for the slider
	 * @see #multiplier(float)
	 */
	public float multiplier() {
		return _myMultiplier;
	}

	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the multiplier you can increase and
	 * decrease this range. Use this method to set the actual multiplier. By default this value is 1.0.
	 * 
	 * @param theMultiplier the new multiplier for a Slider
	 * @see CCInputSlider
	 * @see #multiplier()
	 */
	public void multiplier(final float theMultiplier) {
		_myMultiplier = theMultiplier;
	}
	
	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the power you can change the way
	 * the value increases and decreases. Power values higher than 1 will result in low increase at the beginning
	 * values lower than 1 will result in fast increase at the beginning and slow increase in the end
	 * @param thePower
	 */
	public void power(final float thePower) {
		_myPower = thePower;
	}

	/**
	 * Use this method to see if a slider is relative. A relative sliders value represents always the change between the
	 * current state and the last state.
	 * 
	 * @return boolean, true if the slider is relative
	 */
	public boolean isRelative() {
		return _myComponent.isRelative();
	}
	
	static private final int sign(final float theValue) {
		if (theValue < 0)
			return -1;
		return 1;
	}

	/**
	 * This method is called before each frame to update the slider values.
	 */
	void update(final float theDeltaTime) {
		_myActualValue = _myComponent.getPollData();
		if (Math.abs(_myActualValue) < _myComponent.getDeadZone() + _myTolerance) {
			_myActualValue = 0f;
		} else {
			float myValue = _myComponent.getPollData();
			int mySign = sign(myValue);
			_myActualValue = (float)Math.pow(_myComponent.getPollData(),_myPower) * _myMultiplier;
			_myActualValue *= sign(_myActualValue) == mySign ? 1 : -1;
		}
		_myTotalValue += _myActualValue * theDeltaTime;
	}
}
