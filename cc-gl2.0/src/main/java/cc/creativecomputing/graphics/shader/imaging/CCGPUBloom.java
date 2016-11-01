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
package cc.creativecomputing.graphics.shader.imaging;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCGPUBloom {
	private CCRenderBuffer _myRenderTexture;
	private CCGLProgram _myBloomShader;
	
	@CCProperty(name = "highlightRange", min = 0, max = 1)
	private float _cHighlightColor;
	
	@CCProperty(name = "highlightScale", min = 0, max = 10)
	private float _cHighlightScale;
	
	@CCProperty(name = "highlightPow", min = 0, max = 10)
	private float _cHighlightPow;
	
	@CCProperty(name = "debug bloom")
	private boolean _cDebugBloom = false;
	
	@CCProperty(name = "apply bloom")
	private boolean _cApplyBloom = false;
	
	public final static float MAXIMUM_BLUR_RADIUS = 50;
	
	@CCProperty(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	@CCProperty(name = "blur radius 2", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius2 = MAXIMUM_BLUR_RADIUS;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	
	private int _myWidth;
	private int _myHeight;
	
	private CCGraphics g;
	
	public CCGPUBloom(CCGraphics theG, int theWidth, int theHeight) {
		g = theG;
		_myWidth = theWidth;
		_myHeight = theHeight;
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
		myAttributes.samples(8);
		_myRenderTexture = new CCRenderBuffer(myAttributes, _myWidth, _myHeight);
		
		_myBlur = new CCGPUSeperateGaussianBlur(5, _myWidth, _myHeight, 1);
		
		_myBloomShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/bloom_vert.glsl"), 
			CCNIOUtil.classPath(this, "shader/bloom_frag.glsl")
		);
	}
	
	public CCGPUBloom(CCGraphics g) {
		this(g, g.width(), g.height());
	}
	
	public void start() {
		if(!_cApplyBloom)return;
		_myRenderTexture.beginDraw(g);
	}
	
	public void endCapture(){
		if(!_cApplyBloom)return;
		_myRenderTexture.endDraw(g);
	}
	
	public void blur(){
		_myBlur.radius(_cBlurRadius);
		g.clearColor(0);
		g.clear();
		g.color(255);
		_myBlur.beginDraw(g);
		g.clear();
		g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
		_myBlur.endDraw(g);
	}
	
	public CCTexture2D blurredTexture() {
		return _myBlur.blurredTexture();
	}
	
	public void drawBlurredTexture() {
		g.image(_myBlur.blurredTexture(), -_myWidth/2, -_myHeight/2, _myWidth, _myHeight);
	}
	
	public void bloom(){
		g.clear();
		if(!_cDebugBloom) {
			g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
			
		}
		g.blend(CCBlendMode.ADD);
		_myBloomShader.start();
		_myBloomShader.uniform1i("texture", 0);
		_myBloomShader.uniform1f("highlightRange", _cHighlightColor);
		_myBloomShader.uniform1f("highlightScale", _cHighlightScale);
		_myBloomShader.uniform1f("highlightPow", _cHighlightPow);

		g.image(_myBlur.blurredTexture(), -_myWidth/2, -_myHeight/2, _myWidth, _myHeight);
		_myBloomShader.end();
		g.blend();
	}
	
	public void apply(){
		if(!_cApplyBloom)return;
		blur();
		
		
		
		bloom();
	}
	
	public void end() {
		endCapture();
		apply();
	}
	
	public void startBlur(CCGraphics g) {
		_myRenderTexture.beginDraw(g);
	}
	
	public void endBlur(CCGraphics g) {
		_myRenderTexture.endDraw(g);

		_myBlur.radius(_cBlurRadius2);
		g.color(255);
		_myBlur.beginDraw(g);
		g.clear();
		g.image(_myRenderTexture.attachment(0), -_myWidth/2, -_myHeight/2);
		_myBlur.endDraw(g);
		
	}
}
