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
package cc.creativecomputing.demo.gl2.shader.imaging;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;

public class CCBlur3DTest extends CCGL2Adapter {
	
	public final static float MAXIMUM_BLUR_RADIUS = 150;
	
	@CCProperty(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	private CCGPUSeperateGaussianBlur _myBlur;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;

	public void init(CCGraphics g, CCAnimator theAnimator) {

		_myBlur = new CCGPUSeperateGaussianBlur(20, g.width(), g.height(), 1);
		
		_myCameraController = new CCCameraController(this, g, 100);
	}
	float _myTime = 0;
	public void update(final float theTime){
		_myTime += theTime;
		_myBlur.radius(_cBlurRadius);
	}

	public void display(CCGraphics g) {
		g.color(255);
		g.clear();
		
		_myBlur.beginDraw(g);
		g.clear();
		_myCameraController.camera().beginDraw(g);
		g.box(300);
		_myCameraController.camera().endDraw(g);
		_myBlur.endDraw(g);
		
		g.color(0);
	}

	public static void main(String[] args) {
		CCBlur3DTest demo = new CCBlur3DTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
