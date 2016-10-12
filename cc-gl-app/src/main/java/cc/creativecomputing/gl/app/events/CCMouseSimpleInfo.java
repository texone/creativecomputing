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

import cc.creativecomputing.math.CCVector2;

/**
 * @author info
 *
 */
public class CCMouseSimpleInfo extends CCMouseAdapter{
	
	public CCVector2 lastPosition = new CCVector2();
	public CCVector2 position = new CCVector2();
	public CCVector2 motion = new CCVector2();
	
	public boolean isPressed = false;
	
	private void updatePositions(CCMouseEvent theEvent){
		lastPosition.set(position);
		position.set(theEvent.x(), theEvent.y());
		motion.set(position.subtract(lastPosition));
	}

	public void mouseClicked(CCMouseEvent theEvent) {
		updatePositions(theEvent);
	}

	public void mouseEntered(CCMouseEvent theEvent) {
		updatePositions(theEvent);}

	public void mouseExited(CCMouseEvent theEvent) {
		updatePositions(theEvent);}

	public void mousePressed(CCMouseEvent theEvent) {
		updatePositions(theEvent);
		isPressed = true;
	}

	public void mouseReleased(CCMouseEvent theEvent) {
		updatePositions(theEvent);
		isPressed = false;
	}

	public void mouseDragged(CCMouseEvent theEvent) {
		updatePositions(theEvent);}

	public void mouseMoved(CCMouseEvent theEvent) {
		updatePositions(theEvent);}
	
	public void mouseWheelMoved(CCMouseWheelEvent theThe) {}
	
	
	
}
