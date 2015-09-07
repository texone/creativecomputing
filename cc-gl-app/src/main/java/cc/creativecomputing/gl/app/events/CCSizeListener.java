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

/**
 * <p>
 * Size events are generated every time the application window. Changes
 * its size. Classes that are interested 
 * in processing this event implement this interface (and all the size
 * method it contains). The listener object created from that class is 
 * then registered using the application's <code>addSizeListener</code> 
 * method. When a size event occurs, the relevant method in the listener 
 * object is invoked, and the new width and height of the application 
 * window is passed to it.
 * </p>
 * @author texone
 */
public interface CCSizeListener{

	/**
	 * Called when the application window is resized. You can implement this function 
	 * to change settings that are dependent on the window size.
	 * @param theWidth the new width of the application window
	 * @param theHeight the new height of the application window
	 * @shortdesc Called when the application window is resized.
	 */
	public void size(final int theWidth, final int theHeight);
}
