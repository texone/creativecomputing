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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCImageFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCMipMapSobelFilter extends CCImageFilter {


	private CCGLProgram _myShader;
	private CCShaderBuffer _myOutput;
	private CCRenderBuffer _myInputBuffer;
	
	@CCProperty(name = "lod", min = 1, max = 10)
	private int _cLod = 1;
	
	@CCProperty(name = "shift_value")
	private boolean _cShift = true;
	
	
	public CCMipMapSobelFilter (CCGraphics g, CCTexture2D theInput) {
		super(theInput);
	
		_myInput = theInput;
		_myInputBuffer = new CCRenderBuffer (theInput.width(), theInput.height());
		_myInputBuffer.attachment(0).generateMipmaps(true);
		_myInputBuffer.attachment(0).textureFilter(CCTextureFilter.NEAREST);
		_myInputBuffer.attachment(0).textureMipmapFilter(CCTextureMipmapFilter.NEAREST);
		//_myInputBuffer.attachment(0).wrap(CCTextureWrap.CLAMP_TO_BORDER);
		//_myInputBuffer.attachment(0).textureBorderColor(CCColor.BLACK);
	
		_myOutput = new CCShaderBuffer(32,3,2,_myInput.width(), _myInput.height());
		_myOutput.clear(g);
		
		_myShader = new CCGLProgram(CCNIOUtil.classPath(this, "shader/sobel_mipmap_vp.glsl"), CCNIOUtil.classPath(this, "shader/sobel_mipmap_fp.glsl"));
	}


	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}
	
	public CCTexture2D outputShifted() {
		return _myOutput.attachment(1);
	}
	
	public CCTexture2D input() {
		return _myInputBuffer.attachment(0);
	}
	
	private void swapTexture(CCGraphics g) {
		_myInputBuffer.beginDraw(g);
		g.image(_myInput, -_myInput.width()/2, -_myInput.height()/2, _myInput.width(), _myInput.height());
		_myInputBuffer.endDraw(g);
	}

	@Override
	public void display(CCGraphics g) {
		
		swapTexture(g);
		
		_myOutput.beginDraw(g);
		g.clear();
		_myShader.start();
		g.texture(0, _myInputBuffer.attachment(0));
		
		_myShader.uniform1i("texture", 0);
		_myShader.uniform1i("lod", _cLod);
		_myShader.uniform1f("alpha", 1f);
		_myShader.uniform1f("textureWidth", _myInput.width());
		_myShader.uniform1f("textureHeight", _myInput.height());
		_myShader.uniform  ("shift",_cShift);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0f,0f);
		g.vertex (0,0);
		g.textureCoords2D(0, 1f,0f);
		g.vertex (_myOutput.width(), 0);
		g.textureCoords2D(0, 1f,1f);
		g.vertex(_myOutput.width(), _myOutput.height());
		g.textureCoords2D(0, 0f,1f);
		g.vertex(0, _myOutput.height());
		g.endShape();
		
		g.noTexture();
		
		_myShader.end();
		_myOutput.endDraw(g);
	}
}
