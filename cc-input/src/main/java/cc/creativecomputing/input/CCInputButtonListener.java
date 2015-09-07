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
 * Implement this interface to add a button listener to a button.
 * @author info
 *
 */
public interface CCInputButtonListener {
	/**
	 * Is called when the button is pressed
	 */
	public void onPress();
	
	/**
	 * is called when the button is released
	 */
	public void onRelease();
}
