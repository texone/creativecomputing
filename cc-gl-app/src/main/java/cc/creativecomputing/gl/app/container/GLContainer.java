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
package cc.creativecomputing.gl.app.container;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCPropertyFeedbackObject;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.gl.app.CCCursor;

import com.jogamp.opengl.GLAutoDrawable;

/**
 * Context the application is running in that might be a frame or a dialog so far
 * @author christianriekoff
 *
 */
public abstract class GLContainer implements CCPropertyFeedbackObject{
	
	private boolean _myUpdateDisplay = true;
	

	protected Map<String, CCPropertyListener<?>> _myListenerMap = new HashMap<>();
	
	@Override
	public Map<String, CCPropertyListener<?>> propertyListener() {
		return _myListenerMap;
	}
	
	protected GLContainer(){
	}
	
	public boolean updateDisplay(){
		return _myUpdateDisplay;
	}
	
	public void updateDisplay(boolean theUpdateDisplay){
		_myUpdateDisplay = theUpdateDisplay;
	}
	
	public abstract void handleAddUpdates(CCAnimator theAnimatorModule);
	
	public abstract void handleRemoveUpdates(CCAnimator theAnimatorModule);

	public abstract void close();
	
	public abstract int x();
	
	public abstract int y();
	
	public abstract int width();
	
	public abstract int height();
	
	public abstract void size(int theWidth, int theHeight);
	
	public abstract void position(int theX, int theY);
	
	public abstract boolean isVisible();
	
	public abstract void setVisible(boolean theIsVisible);
	
	public abstract void fullScreen(boolean theIsFullScreen);
	
	public abstract String title();
	
	public abstract void title(String theTitle);
	
	/**
	 * Use this method to hide the mouse cursor.
	 */
	public abstract void noCursor();
	
	public abstract void cursor(final CCCursor theCursor);
	
	public abstract GLAutoDrawable glAutoDrawable();
}
