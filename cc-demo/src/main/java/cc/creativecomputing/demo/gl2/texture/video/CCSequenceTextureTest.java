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
package cc.creativecomputing.demo.gl2.texture.video;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCSequenceTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSequenceTextureTest extends CCGL2Adapter {
	
	private CCSequenceTexture _mySequenceTexture;
	
	@CCProperty(name = "rate", min = -2, max = 2)
	private static float _cRate = 1;
	
	private String _myFolder = "videos/crash/";

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(false);
		
		_mySequenceTexture = new CCSequenceTexture(CCTextureTarget.TEXTURE_2D, myAttributes, CCImageIO.newImages(CCNIOUtil.dataPath(_myFolder)));
		_mySequenceTexture.loop();
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_mySequenceTexture.rate(_cRate);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0f,1f,0f);
		g.clear();
		g.texture(_mySequenceTexture);
		_mySequenceTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-200, -100, -0.5f, -0.5f);
		g.vertex( 0, -100, 1.5f, -0.5f);
		g.vertex( 0,  100, 1.5f, 1.5f);
		g.vertex(-200,  100, -0.5f, 1.5f);
		g.endShape();
		g.noTexture();

	}

	public static void main(String[] args) {
		CCSequenceTextureTest demo = new CCSequenceTextureTest();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

