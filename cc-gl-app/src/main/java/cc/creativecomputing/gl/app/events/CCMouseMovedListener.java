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
 * Mouse moved events are generated for mouse moves 
 * inside the application window. Classes that are interested 
 * in processing these events implement this interface.
 * <P>
 * The listener object created from that class is then registered using 
 * the application's <code>addMouseMotionListener</code> 
 * method. A mouse move event is generated when the mouse is moved. 
 * (Many such events will be generated). When a mouse move event
 * occurs, the relevant method in the listener object is invoked, and 
 * the <code>CCMouseEvent</code> is passed to it.
 * @author texone
 */
public interface CCMouseMovedListener{
	
	/**
     * Invoked when the mouse cursor has been moved.
	 * @param theMouseEvent the related mouse event
     */
    void mouseMoved(final CCMouseEvent theMouseEvent);
}
