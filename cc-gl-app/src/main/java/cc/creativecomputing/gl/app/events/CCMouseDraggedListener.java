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
 * Mouse dragg events are generated for mouse draggs
 * inside the application window. Classes that are interested 
 * in processing these events implement this interface.
 * <P>
 * The listener object created from that class is then registered using 
 * the application's <code>addMouseDraggedListener</code> 
 * method. A mouse motion event is generated when the mouse is dragged. 
 * (Many such events will be generated). When a mouse dragg event
 * occurs, the relevant method in the listener object is invoked, and 
 * the <code>CCMouseEvent</code> is passed to it.
 * @author texone
 */
public interface CCMouseDraggedListener{
	/**
     * Invoked when a mouse button is pressed on the application window and then 
     * dragged. Mouse dragg events will continue to be delivered to the application
     * until the mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the application window).
	 * @param theMouseEvent the related mouse event
	 */
    void mouseDragged(final CCMouseEvent theMouseEvent);
	
	
}
