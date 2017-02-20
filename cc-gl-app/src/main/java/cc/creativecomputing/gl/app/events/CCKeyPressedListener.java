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
package cc.creativecomputing.gl.app.events;

import cc.creativecomputing.gl.app.CCGLAdapter;

/**
 * <p>
 * The keyPressed() function is called once every time a key is pressed. The key
 * that was pressed is passed as key event. Because of how operating systems
 * handle key repeats, holding down a key may cause multiple calls to
 * keyPressed() (and keyReleased() as well). The rate of repeat is set by the
 * operating system and how each computer is configured.
 * </p>
 * <p>
 * The listener object created from that class is then registered using the
 * applications's <code>addKeyListener</code> method. A keyboard event is
 * generated when a key is pressed, released, or typed. The relevant method in
 * the listener object is then invoked, and the <code>CCKeyEvent</code> is
 * passed to it.
 * </p>
 * 
 * @author texone
 *
 */
public interface CCKeyPressedListener {
	/**
	 * The keyPressed() function is called once every time a key is pressed. The
	 * key that was pressed is passed as key event. Because of how operating
	 * systems handle key repeats, holding down a key may cause multiple calls
	 * to keyPressed() (and keyReleased() as well). The rate of repeat is set by
	 * the operating system and how each computer is configured.
	 * 
	 * @param theKeyEvent
	 * @see CCKeyEvent
	 * @see CCGLAdapter#keyPressed()
	 */
	public void keyPressed(final CCKeyEvent theKeyEvent);

}
