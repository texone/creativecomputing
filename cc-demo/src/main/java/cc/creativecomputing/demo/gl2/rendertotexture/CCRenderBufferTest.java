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
package cc.creativecomputing.demo.gl2.rendertotexture;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.math.CCMath;

public class CCRenderBufferTest extends CCGL2Adapter {
	
	private CCRenderBuffer _myRenderBuffer;

	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCFrameBufferObjectAttributes myAttributes = new CCFrameBufferObjectAttributes();
//		myAttributes.samples(8);
		
		_myRenderBuffer = new CCRenderBuffer(g, CCTextureTarget.TEXTURE_RECT, myAttributes, 400, 400);
		_myRenderBuffer.attachment(0).generateMipmaps();
		_myRenderBuffer.attachment(0).textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myRenderBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		g.pointSize(2);
		g.smooth();
		
//		g.debug();
	}

	public void display(CCGraphics g) {

		g.clearColor(0);
		g.clear();
		_myRenderBuffer.beginDraw();

		g.clearColor(255,0,0);
		g.clear();
		g.color(255);
		CCMath.randomSeed(0);
		for(int i = 0; i < 200;i++) {
			g.color(CCMath.random(),CCMath.random(),CCMath.random());
			g.ellipse(CCMath.random(-g.width()/2,g.width()/2),CCMath.random(-g.height()/2,g.height()/2),CCMath.random(200),20,20);
		}
		g.rect(-200,-200, 50,50);
		_myRenderBuffer.endDraw();
		
		g.color(255);
		g.image(_myRenderBuffer.attachment(0), 0,0,200,200);
//		g.texture(_myRenderBuffer);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(-200, -200, 0, 0f);
//		g.vertex( 200, -200, 1, 0f);
//		g.vertex( 200,  200, 1, 1);
//		g.vertex(-200,  200, 0, 1);
//		g.endShape();
//		g.noTexture();

	}

	public static void main(String[] args) {
		CCRenderBufferTest demo = new CCRenderBufferTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
