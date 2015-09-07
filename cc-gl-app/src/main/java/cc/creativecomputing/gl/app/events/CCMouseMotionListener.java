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
 * Mouse motion events are generated for mouse moves and draggs
 * inside the application window. Classes that are interested 
 * in processing these events implement this interface (and all the
 * methods it contains).
 * <P>
 * The listener object created from that class is then registered using 
 * the application's <code>addMouseMotionListener</code> 
 * method. A mouse motion event is generated when the mouse is moved
 * or dragged. (Many such events will be generated). When a mouse motion event
 * occurs, the relevant method in the listener object is invoked, and 
 * the <code>CCMouseEvent</code> is passed to it.
 * @author texone
 * @see CCAbstractWindowApp#addMouseMotionListener(CCMouseMotionListener)
 */
public interface CCMouseMotionListener{
	/**
     * Invoked when a mouse button is pressed on the application window and then 
     * dragged. Mouse dragg events will continue to be delivered to the application
     * until the mouse button is released (regardless of whether the mouse position 
     * is within the bounds of the application window).
     * @shortdesc invoked when the mouse is dragged over the application window
	 * @param theMouseEvent the related mouse event
	 * @see #mouseDragged(CCMouseEvent)
	 */
	public void mouseDragged(final CCMouseEvent theMouseEvent);
	
	/**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     * @shortdesc invoked when the mouse has moved over the application window
	 * @param theMouseEvent the related mouse event
     * @see #mouseMoved(CCMouseEvent)
     */
	public void mouseMoved(final CCMouseEvent theMouseEvent);
}
