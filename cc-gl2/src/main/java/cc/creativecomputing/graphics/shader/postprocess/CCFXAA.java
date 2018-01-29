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
package cc.creativecomputing.graphics.shader.postprocess;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * @author christianriekoff
 *
 */
public class CCFXAA {

	private CCGLProgram _myFXAAShader;
	private CCTexture2D _myContentTexture;
	
	@CCProperty(name = "subpixel shift", min = 0, max = 1)
	private float _cSubPixelShift = 0.25f;
	
	@CCProperty(name = "span max", min = 0, max = 20)
	private float _cSpanMax = 8f;
	
	public CCFXAA(CCTexture2D theContentTexture) {
		_myFXAAShader = new CCGLProgram(
			CCNIOUtil.classPath(CCPostProcess.class, "shader/fxaa_vertex.glsl"), 
			CCNIOUtil.classPath(CCPostProcess.class, "shader/fxaa_fragment.glsl")
		);
		_myContentTexture = theContentTexture;
	}
	
	public void draw(CCGraphics g) {
		_myFXAAShader.start();
		_myFXAAShader.uniform1i("tex0", 0);
		_myFXAAShader.uniform2f("invTextureSize", 1f / _myContentTexture.width(), 1f / _myContentTexture.height());
		_myFXAAShader.uniform1f("subpixelShift", _cSubPixelShift);
		_myFXAAShader.uniform1f("FXAA_SPAN_MAX", _cSpanMax);
		_myFXAAShader.uniform1f("FXAA_REDUCE_MUL", 1.0f/_cSpanMax);
		g.texture(0, _myContentTexture);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0f, 0f);
		g.vertex(-_myContentTexture.width() / 2,   _myContentTexture.height() / 2);
		g.textureCoords2D(0, 1f, 0f);
		g.vertex( _myContentTexture.width() / 2,   _myContentTexture.height() / 2);
		g.textureCoords2D(0, 1f, 1f);
		g.vertex( _myContentTexture.width() / 2,  -_myContentTexture.height() / 2);
		g.textureCoords2D(0, 0f, 1f);
		g.vertex(-_myContentTexture.width() / 2,  -_myContentTexture.height() / 2);
		g.endShape();
		g.noTexture();
//		g.image(_myRenderTexture, -width/2, -height/2);
		_myFXAAShader.end();
	}
}
