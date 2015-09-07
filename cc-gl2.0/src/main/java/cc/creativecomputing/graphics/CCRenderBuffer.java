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

import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.texture.CCFrameBufferObject;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;


public class CCRenderBuffer extends CCFrameBufferObject{
	
	private CCCamera _myCamera;
	private CCGraphics _myGraphics;
	
	public CCRenderBuffer(final CCGraphics theGraphics, final CCTextureTarget theTarget, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theWidth, theHeight);
		
		_myGraphics = theGraphics;
		_myCamera = new CCCamera(theWidth,theHeight);
	}
	
	public CCRenderBuffer(final CCGraphics theGraphics, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, theAttributes, theWidth, theHeight);
	}
	
	public CCRenderBuffer(CCGraphics theGraphics, final CCTextureTarget theTarget, final int theWidth, final int theHeight){
		this(theGraphics, theTarget,new CCFrameBufferObjectAttributes(),theWidth, theHeight);
	}
	
	public CCRenderBuffer(CCGraphics theGraphics, final int theNumberOfAttachments, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, new CCFrameBufferObjectAttributes(theNumberOfAttachments), theWidth, theHeight);
	}
	
	public CCRenderBuffer(CCGraphics theGraphics, final int theWidth, final int theHeight){
		this(theGraphics, CCTextureTarget.TEXTURE_2D, theWidth, theHeight);
	}
	
	public void beginDraw() {
		bindFBO();
		_myGraphics.matrixMode(CCMatrixMode.PROJECTION);
		_myGraphics.pushMatrix();
		_myGraphics.matrixMode(CCMatrixMode.MODELVIEW);
		_myGraphics.pushMatrix();
		_myCamera.draw(_myGraphics);
	}

	public void endDraw() {
		_myGraphics.camera().draw(_myGraphics);
		_myGraphics.matrixMode(CCMatrixMode.PROJECTION);
		_myGraphics.popMatrix();
		_myGraphics.matrixMode(CCMatrixMode.MODELVIEW);
		_myGraphics.popMatrix();
		releaseFBO();
	}
	
	public CCCamera camera(){
		return _myCamera;
	}
	
	@Override
	public void finalize(){
		super.finalize();
	}
}
