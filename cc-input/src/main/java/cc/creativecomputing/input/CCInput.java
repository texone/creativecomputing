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
 * Base class for input elements of a controller.
 * 
 * @invisible
 */
abstract class CCInput {
	/**
	 * The current state of the input
	 */
	protected float _myActualValue = 0f;

	/**
	 * JInput Component representing this Slider
	 */
	final Component _myComponent;

	/**
	 * The name of the Slider
	 */
	private final String _myName;

	/**
	 * Initializes a new Slider.
	 * 
	 * @param theComponent
	 */
	CCInput(final Component theComponent) {
		_myComponent = theComponent;
		_myName = _myComponent.getName();
	}

	/**
	 * Returns the name of the input.
	 * 
	 * @return the name of the input element
	 */
	public String name() {
		return _myName;
	}

	/**
	 * Gives you the current value of an input.
	 * 
	 * @return the actual value of the slider
	 * @see CCInputSlider
	 */
	public float value() {
		return _myActualValue;
	}

	/**
	 * This method is called before each frame to update the slider values.
	 */
	abstract void update(final float theDeltaTime);

}
