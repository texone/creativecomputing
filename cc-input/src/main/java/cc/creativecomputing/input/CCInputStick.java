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

/**
 * A Stick combines two Sliders to a joy stick with x and y values. This class is for simpler handling of sliders
 * belonging together. For detailed control like different multipliers for the different axes of the stick work with the
 * Sliders instead. <br>
 * Note that this class is quiet experimental as it is only tested with my joypad and correct function extremely depends
 * on the order of the sliders for a device. If the getStick ( ) function of ControllDevice gives you wrong sticks, you
 * can initialize your own by giving it your own sliders.
 * 
 * @demo cc.creativecomputing.input.test.CCInputStickDemo
 * @see CCInputDevice
 * @see CCInputSlider
 */
public class CCInputStick {

	/**
	 * The Slider for the x movement of the stick
	 */
	private final CCInputSlider _myXSlider;

	/**
	 * The Slider for the y movement of the stick
	 */
	private final CCInputSlider _myYSlider;

	/**
	 * Initializes a new Stick by two given Sliders
	 * 
	 * @param theXSlider the slider for the x axis
	 * @param theYSlider the slider for the y axis
	 */
	public CCInputStick(final CCInputSlider theXSlider, final CCInputSlider theYSlider) {
		_myXSlider = theXSlider;
		_myYSlider = theYSlider;
	}

	/**
	 * Returns the name of the stick. The sticks name is the combination of the names of its sliders.
	 * 
	 * @return the name of the stick
	 */
	public String name() {
		return _myXSlider.name() + " " + _myYSlider.name();
	}

	/**
	 * The current x value of the stick.
	 * 
	 * @return the x value of the stick
	 * @see #y()
	 * @see #value()
	 */
	public float x() {
		return _myXSlider.value();
	}

	/**
	 * The current y value of the stick.
	 * 
	 * @return the y value of the stick
	 * @see #x()
	 * @see #value()
	 */
	public float y() {
		return _myYSlider.value();
	}

	/**
	 * For the total value the actual values for each frame are add. Use this method to get the total x value of a
	 * stick.
	 * 
	 * @return the total x value of stick
	 * @see #totalY()
	 * @see #reset()
	 */
	public float totalX() {
		return _myXSlider.totalValue();
	}

	/**
	 * For the total value the actual values for each frame are add. Use this method to get the total y value of a
	 * stick.
	 * 
	 * @return the total y value of stick
	 * @see #totalX()
	 * @see #reset()
	 */
	public float totalY() {
		return _myYSlider.totalValue();
	}

	/**
	 * For the total value the values for each frame are add. Use this method to set the total value to 0.
	 * 
	 * @see #totalX()
	 * @see #totalY()
	 */
	public void reset() {
		_myXSlider.reset();
		_myYSlider.reset();
	}

	/**
	 * If you not want a stick to react up to a certain value you can set a tolerance value. Use this method to retrieve
	 * the set tolerance. By default this value is set to 0.
	 * 
	 * @return the tolerance value for a stick
	 * @see #tolerance(float)
	 */
	public float tolerance() {
		return _myXSlider.tolerance();
	}

	/**
	 * If you not want a stick to react up to a certain value you can set a tolerance value. Use this method to set the
	 * tolerance. By default this value is set to 0.
	 * 
	 * @param theTolerance float
	 * @see #tolerance()
	 */
	public void tolerance(final float theTolerance) {
		_myXSlider.tolerance(theTolerance);
		_myYSlider.tolerance(theTolerance);
	}

	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the multiplier you can increase and decrease
	 * this range. Use this method to get the actual multiplier. By default this value is 1.0.
	 * 
	 * @return the multiplier for the stick
	 * @see #multiplier(float)
	 */
	public float multiplier() {
		return _myXSlider.multiplier();
	}

	/**
	 * The value of a stick is a relative value between -1.0f and 1.0f with the multiplier you can increase and decrease
	 * this range. Use this method to set the actual multiplier. By default this value is 1.0.
	 * @see #multiplier()
	 */
	public void multiplier(final float theMultiplier) {
		_myXSlider.multiplier(theMultiplier);
		_myYSlider.multiplier(theMultiplier);
	}
	
	/**
	 * The value of a slider is a relative value between -1.0f and 1.0f with the power you can change the way
	 * the value increases and decreases. Power values higher than 1 will result in low increase at the beginning
	 * values lower than 1 will result in fast increase at the beginning and slow increase in the end
	 * @param thePower
	 */
	public void power(final float thePower) {
		_myXSlider.power(thePower);
		_myYSlider.power(thePower);
	}
}
