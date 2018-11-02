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
package cc.creativecomputing.demo.topic.simulation.ripple;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.gl2.shader.imaging.CCVoronoiseLighted;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCGPUWaterRippleDemo extends CCGL2Adapter {

	@CCProperty(name = "splash radius", min = 0, max = 20)
	public float _cSplashRadius;

	@CCProperty(name = "splash amplitude", min = 0, max = 1f)
	public float _cSplashAmplitude;

	@CCProperty(name = "blend", min = 0, max = 0.05f)
	public float _cBlend;

	@CCProperty(name = "blend2", min = 0, max = 0.05f)
	public float _cBlend2;

	@CCProperty(name = "ripple")
	private CCGPUWaterRipple _myWaterRipple;

	// private CCTexture _myRippleTexone;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

		// _myRippleTexone = CCTextureIO.newTexture("effects/ripple_tex.png");
		_myWaterRipple = new CCGPUWaterRipple(g, null, g.width(), g.height());
		_myWaterRipple.backgroundTexture(new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/waltz.jpg")), CCTextureTarget.TEXTURE_RECT));
		g.imageMode(CCShapeMode.CENTER);
		
	}

	float _myAngle = 0;

	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void display(CCGraphics g) {
		if (mouse().isPressed)
			_myWaterRipple.addSplash(mouse().position.x, mouse().position.y, _cSplashRadius, _cSplashAmplitude);

		// define areas where water animation will be masked
		_myWaterRipple.beginDrawMask();
		g.color(0);
		float x = g.width() / 2;
		float y = g.height() / 2;
		float r = 300;

		// g.line(x-r, y-r, x+r, y-r);
		// g.line(x+r, y-r, x+r, y+r);
		// g.line(x+r, y+r, x-r, y+r);
		// g.line(x-r, y+r, x-r, y-r);

		_myWaterRipple.endDrawMask();

		// define areas that cause water ripple
		_myWaterRipple.beginDrawActiveArea();
		g.clearColor(0);
		g.clear();
		g.pushMatrix();
		g.translate(g.width() / 2 + 50, g.height() / 2 + 50);
		g.rotate(_myAngle);
		_myAngle += animator().deltaTime() * 30;
		g.color(_cBlend);
		x = 0;// _myWidth/2 + 50;
		y = 0;// _myHeight/2 + 50;
		r = 100;

		g.line(x - r, y - r, x + r, y - r);
		g.line(x + r, y - r, x + r, y + r);
		g.line(x + r, y + r, x - r, y + r);
		g.line(x - r, y + r, x - r, y - r);
		g.triangle(200, 200, 250, 300, 300, 150);

		g.popMatrix();
		// g.clearColor(0);
		// g.clear();
		g.color(_cBlend2);
		g.rect(200, 200, 300, 300);
		_myWaterRipple.endDrawActiveArea();
		g.clearColor(0);
		_myWaterRipple.update(animator().deltaTime());
		
		g.clear();
		_myWaterRipple.draw(g);
	}

	

	public static void main(String[] args) {

		CCGPUWaterRippleDemo demo = new CCGPUWaterRippleDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
