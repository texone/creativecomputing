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
 * Implement this interface to add a listener to a coolie hat.
 * @author info
 *
 */
public interface CCInputCoolieHatListener {
	
	/**
	 * Called when a coolie hat is pressed
	 * @param theX
	 * @param theY
	 */
	public void onPress(final float theX, final float theY);

	/**
	 * Called when a coolie hat is  released
	 * @param theX
	 * @param theY
	 */
	public void onRelease(final float theX, final float theY);
}
