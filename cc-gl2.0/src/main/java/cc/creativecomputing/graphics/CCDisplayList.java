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
package cc.creativecomputing.graphics;

import com.jogamp.opengl.GL2;

/**
 * CCDisplayList uses the OPENGL display list for recording drawing calls. Because cc
 * is completely based on OPENGL all drawing calls in your application can be directly recorded
 * to the graphic cards memory. This makes drawing of the recorded shape much faster.
 * @author christianr
 */
public class CCDisplayList{
	
	private final int _myID;
	
	/**
	 * Initializes a new ProGLShape
	 * @example shape
	 */
	public CCDisplayList(){
		_myID = CCGraphics.currentGL().glGenLists(1);;
	}

	/**
	 * Use this method to start the recording of your shape, all previous recordings
	 * will be overwritten.
	 * Render lists are groups of GL commands that have been stored for subsequent execution. 
	 * The render lists are created with beginRenderList. All subsequent commands are placed 
	 * in the render list, in the order issued, until endList is called.
	 * When endRenderList is encountered, the display-list definition is completed by associating 
	 * the list with the unique name list (specified in the beginRenderList command). If a render 
	 * list with name list already exists, it is replaced only when endRenderList is called. 
	 */
	public void beginRecord(){
		CCGraphics.currentGL().glNewList(_myID,GL2.GL_COMPILE);
	}
	
	/**
	 * Use this method to end the recording of your shape.
	 * Render lists are groups of GL commands that have been stored for subsequent execution. 
	 * The render lists are created with beginRenderList. All subsequent commands are placed 
	 * in the render list, in the order issued, until endList is called.
	 * When endRenderList is encountered, the display-list definition is completed by associating 
	 * the list with the unique name list (specified in the beginRenderList command). If a render 
	 * list with name list already exists, it is replaced only when endRenderList is called. 
	 */
	public void endRecord(){
		CCGraphics.currentGL().glEndList();
	}
	
	/**
	 * Draws the shape on to the screen.
	 * After you've created a render list, you can execute it by calling draw(). 
	 * Naturally, you can execute the same display list many times, and you can mix calls to 
	 * execute display lists with calls to perform immediate-mode graphics, as you've already seen.
	 * This routine executes the display list specified by list. The commands in the display list 
	 * are executed in the order they were saved, just as if they were issued without using a display 
	 * list. If list hasn't been defined, nothing happens.
	 * Since a render list can contain calls that change the value of OpenGL state variables, 
	 * these values change as the display list is executed, just as if the commands were called in 
	 * immediate mode. The changes to OpenGL state persist after execution of the render list is completed. 
	 */
	public void draw(){
		CCGraphics.currentGL().glCallList(_myID);
	}
	
	@Override
	public void finalize() {
		CCGraphics.currentGL().glDeleteLists(_myID,1);
	}
}
