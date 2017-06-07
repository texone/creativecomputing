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

import java.nio.FloatBuffer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCMath;

public class CCShaderTextureTest extends CCGL2Adapter {
	
	private CCShaderBuffer _myShaderTexture;

	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myShaderTexture = new CCShaderBuffer(400, 400);
		g.pointSize(2);
		g.smooth();
		
//		g.debug();
	}

	public void display(CCGraphics g) {

		g.clearColor(0);
		g.clear();
		_myShaderTexture.beginDraw(g);

		g.clearColor(255,0,0);
		g.clear();
		g.color(255);
		CCMath.randomSeed(0);
		for(int i = 0; i < 200;i++) {
			g.color(CCMath.random(),CCMath.random(),CCMath.random());
			g.ellipse(CCMath.random(400),CCMath.random(400),0,20,20);
		}
		g.rect(-200,-200, 50,50);
		_myShaderTexture.endDraw(g);
		
		FloatBuffer outputData0 = _myShaderTexture.getData(0);
		System.err.printf("toutput0\toutput1\toutput2\toutput3\n");
	    for (int i = 0; i < _myShaderTexture.width() * _myShaderTexture.height() * 3; i++)
	    	System.err.printf("t%.2f\t%.2f\t%.2f\n", outputData0.get(), outputData0.get(), outputData0.get());
	
		g.color(255);
		g.image(_myShaderTexture.attachment(0), 0,0,200,200);
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
		CCShaderTextureTest demo = new CCShaderTextureTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
