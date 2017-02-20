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
 * Key events are send every time your application is focused and you press or
 * release a key. Classes that are interested in processing a keyboard event
 * implement this interface (and all the methods it contains).
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
public interface CCKeyTypedListener {

	/**
	 * The keyTyped() function is called once every time a key is pressed, but
	 * action keys such as CTRL, SHIFT, and ALT are ignored. Because of how
	 * operating systems handle key repeats, holding down a key will cause
	 * multiple calls to keyTyped(), the rate is set by the operating system and
	 * how each computer is configured.
	 * 
	 * @param theKeyEvent
	 * @see CCKeyEvent
	 * @see CCGLAdapter#keyTyped()
	 */
	public void keyTyped(final CCKeyEvent theKeyEvent);
}
