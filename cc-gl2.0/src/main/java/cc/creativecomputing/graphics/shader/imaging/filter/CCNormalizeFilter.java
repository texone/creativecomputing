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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

/* 
 * Scale each pixel by a factor. This factor can be set with a value from outside (e.g. calculated during the value-to-texture process).
 * Another possibility is to calculate the mean / max of the texture and apply this value.
 */
public class CCNormalizeFilter extends CCImageFilter {
	
	
	private CCGLProgram   _myScalingShader;
	private int _myDownscale = 64;
	
	private CCRenderBuffer _myInputRenderBuffer;
	private CCRenderBuffer _myOutputTmp;
	private CCRenderBuffer _myOutput;   

	
	public CCNormalizeFilter (CCGraphics theGraphics, CCTexture2D theInput) {
		super(theGraphics, theInput);
		_myScalingShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/normalize_vertex.glsl"), CCNIOUtil.classPath(this, "shader/normalize_fragment.glsl"));
		
		_myInputRenderBuffer = new CCRenderBuffer (_myGraphics, _myInput.width(), _myInput.height());
		_myOutput            = new CCRenderBuffer (_myGraphics, _myInput.width(), _myInput.height());	
		_myOutputTmp         = new CCRenderBuffer (_myGraphics, theInput.width()/_myDownscale, theInput.height()/_myDownscale);
		
		
		_myOutputTmp.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		//_myOutputTmp.attachment(0).generateMipmaps(true);


		//_myInputRenderBuffer.attachment(0).generateMipmaps(true);
		_myInputRenderBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
	
	@Override
	public void update(float theDeltaTime) {
		
		// swap to input render buffer to have mipmaps
		_myInputRenderBuffer.beginDraw();
		_myGraphics.image(_myInput,-_myInput.width()/2, -_myInput.height()/2, _myInput.width(), _myInput.height());
		_myInputRenderBuffer.endDraw();
		
		// draw scaled down to outbuffer
		_myOutputTmp.beginDraw();
		_myGraphics.pushAttribute();
		_myGraphics.clearColor(1f);
		_myGraphics.clear();
		_myGraphics.popAttribute();
		//_myGraphics.scale(_myDownscale);
		_myGraphics.image(_myInputRenderBuffer.attachment(0),-_myInput.width()/_myDownscale/2, -_myInput.height()/_myDownscale/2, _myInput.width()/_myDownscale, _myInput.height()/_myDownscale);
		_myOutputTmp.endDraw();
		
		_myOutput.beginDraw();
		_myGraphics.clear();
		_myGraphics.image(_myOutputTmp.attachment(0), -_myOutput.width()/2,- _myOutput.height()/2, _myOutput.width(), _myOutput.height());
		_myOutput.endDraw();
	}
}
