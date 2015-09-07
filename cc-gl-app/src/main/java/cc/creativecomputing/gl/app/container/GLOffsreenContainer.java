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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.gl.app.CCAbstractGLContext;
import cc.creativecomputing.gl.app.CCCursor;

import com.jogamp.nativewindow.util.InsetsImmutable;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLAutoDrawable;

/**
 * @author texone
 */
public class GLOffsreenContainer extends GLContainer{
	
	private GLWindow _myWindow;

	public GLOffsreenContainer(final CCAbstractGLContext<?> theContext) {
		
		theContext.glCapabilities().setOnscreen(false);
		theContext.glCapabilities().setAlphaBits(1); 
		theContext.glCapabilities().setPBuffer(true);
		theContext.glCapabilities().setDoubleBuffered(true);
		
		// Create the OpenGL rendering canvas
		_myWindow = GLWindow.create(theContext.glCapabilities());
		_myWindow.setTitle(theContext.title);
//		_myWindow.setResizable(theExtension.isResizable());
		_myWindow.setUndecorated(theContext.undecorated);
		_myWindow.setAlwaysOnTop(theContext.alwaysOnTop);
		
		//get insets to adjust frame size
		final InsetsImmutable myInsets = _myWindow.getInsets();
		
		_myWindow.setSize(
			theContext.width + myInsets.getLeftWidth() + myInsets.getRightWidth(), 
			theContext.height + myInsets.getTopHeight() + myInsets.getBottomHeight()
		);
				
		_myWindow.setPosition(0,0);
		_myWindow.setVisible(true);
		
		_myWindow.addWindowListener(new WindowListener() {
			
			@Override
			public void windowResized(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowRepaint(WindowUpdateEvent arg0) {}
			
			@Override
			public void windowMoved(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowLostFocus(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowGainedFocus(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowDestroyed(com.jogamp.newt.event.WindowEvent arg0) {}
			
			@Override
			public void windowDestroyNotify(com.jogamp.newt.event.WindowEvent arg0) {}
		});
	}
	
	@Override
	public void setVisible(boolean visible) {
		_myWindow.setVisible(visible);

		//attempt to get the focus of the canvas
		if(visible)_myWindow.requestFocus();
	}
	
	@Override
	public void close() {
//		processWindowEvent(new WindowEvent(this, _myApplication.settings.closeOperation().id()));
	}

	@Override
	public int x() {
		return _myWindow.getX();
	}

	@Override
	public int y() {
		return _myWindow.getY();
	}

	@Override
	public int width() {
		return _myWindow.getWidth();
	}

	@Override
	public int height() {
		return _myWindow.getHeight();
	}

	@Override
	public void size(int theWidth, int theHeight) {
		_myWindow.setSize(theWidth, theHeight);
	}
	
	@Override
	public void position(int theWidth, int theHeight) {
		_myWindow.setPosition(theWidth, theHeight);
	}
	
	@Override
	public void fullScreen(boolean theIsFullScreen) {
		
	}
	
	@Override
	public String title() {
		return _myWindow.getTitle();
	}
	
	@Override
	public void title(String theTitle) {
		_myWindow.setTitle(theTitle);
	}

	@Override
	public boolean isVisible() {
		return _myWindow.isVisible();
	}
	
	@Override
	public void noCursor(){
		_myWindow.setPointerVisible(false);
	}
	
	
	@Override
	public void cursor(final CCCursor theCursor){
		_myWindow.setPointerVisible(true);
	}
	
	@Override
	public GLAutoDrawable glAutoDrawable() {
		return _myWindow;
	}
	
	private CCAnimatorAdapter _myDisplayUpdate = null;

	@Override
	public void handleAddUpdates(CCAnimator theAnimatorModule) {
		_myDisplayUpdate = new CCAnimatorAdapter() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				if(updateDisplay())_myWindow.display();
			}
		};
		
		theAnimatorModule.listener().add(_myDisplayUpdate);
	}
	
	@Override
	public void handleRemoveUpdates(CCAnimator theAnimatorModule) {
		theAnimatorModule.listener().remove(_myDisplayUpdate);
	}
}
