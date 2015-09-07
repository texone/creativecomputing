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
 * @author info
 *
 */
public abstract class CCMouseAdapter implements CCMouseListener, CCMouseMotionListener, CCMouseWheelListener{

	public void mouseClicked(CCMouseEvent theEvent) {}

	public void mouseEntered(CCMouseEvent theEvent) {}

	public void mouseExited(CCMouseEvent theEvent) {}

	public void mousePressed(CCMouseEvent theEvent) {}

	public void mouseReleased(CCMouseEvent theEvent) {}

	public void mouseDragged(CCMouseEvent theMouseEvent) {}

	public void mouseMoved(CCMouseEvent theMouseEvent) {}
	
	public void mouseWheelMoved(CCMouseWheelEvent theThe) {}
	
}
