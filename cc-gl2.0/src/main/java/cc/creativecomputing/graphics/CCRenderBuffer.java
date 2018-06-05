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
import cc.creativecomputing.math.CCMath;


public class CCRenderBuffer extends CCFrameBufferObject{
	
	private CCCamera _myCamera;
	
	public CCRenderBuffer(final CCTextureTarget theTarget, final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		super(theTarget, theAttributes, theWidth, theHeight);
		_myCamera = new CCCamera(theWidth,theHeight);
	}
	
	public CCRenderBuffer( final CCFrameBufferObjectAttributes theAttributes, final int theWidth, final int theHeight){
		this(CCTextureTarget.TEXTURE_2D, theAttributes, theWidth, theHeight);
	}
	
	public CCRenderBuffer(final CCTextureTarget theTarget, final int theWidth, final int theHeight){
		this(theTarget,new CCFrameBufferObjectAttributes(),theWidth, theHeight);
	}
	
	public CCRenderBuffer(final int theNumberOfAttachments, final int theWidth, final int theHeight){
		this(CCTextureTarget.TEXTURE_2D, new CCFrameBufferObjectAttributes(theNumberOfAttachments), theWidth, theHeight);
	}
	
	public CCRenderBuffer(final int theWidth, final int theHeight){
		this(CCTextureTarget.TEXTURE_2D, theWidth, theHeight);
	}
	
	@Override
	public void beginDraw(CCGraphics g) {
		bindFBO();
		g.matrixMode(CCMatrixMode.PROJECTION);
		g.pushMatrix();
		g.matrixMode(CCMatrixMode.MODELVIEW);
		g.pushMatrix();
		_myCamera.draw(g);
	}

	@Override
	public void endDraw(CCGraphics g) {
		g.camera().draw(g);
		g.matrixMode(CCMatrixMode.PROJECTION);
		g.popMatrix();
		g.matrixMode(CCMatrixMode.MODELVIEW);
		g.popMatrix();
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
