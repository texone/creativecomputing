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
package cc.creativecomputing.demo.gl2.camera;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;

public class CC2CameraControllerDemo extends CCGL2Adapter {
	
	
	
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;
	@CCProperty(name = "camera2")
	private CCCameraController _myCameraController2;
	@CCProperty(name = "camera3")
	private CCCameraController _myCameraController3;
	
//	private CCArcball _myArcball;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		_myArcball = new CCArcball(this);
		
		_myCameraController1 = new CCCameraController(this, g, 0, 0, 0.5, 0.75, 0, 0, 0, 100);
		_myCameraController2 = new CCCameraController(this, g, 0.5, 0, 0.5, 0.75, 0, 0, 0, 100);
		_myCameraController3 = new CCCameraController(this, g, 0, 0.75, 1, 0.25, 0, 0, 0, 100);	
	}

	@Override
	public void update(final CCAnimator theAnimator) {
	}
	

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.polygonMode(CCPolygonMode.LINE);
		_myCameraController1.camera().draw(g);
		g.box(140);
		_myCameraController2.camera().draw(g);
		g.box(140);
		_myCameraController3.camera().draw(g);
		g.box(140);
		
		
		g.polygonMode(CCPolygonMode.FILL);
	}
	
	public static void main(String[] args) {
		CC2CameraControllerDemo demo = new CC2CameraControllerDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 300);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
