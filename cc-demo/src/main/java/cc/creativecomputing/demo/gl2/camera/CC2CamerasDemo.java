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
import cc.creativecomputing.gl.app.events.CCKeyAdapter;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.util.CCFrustum;
import cc.creativecomputing.math.CCMath;

public class CC2CamerasDemo extends CCGL2Adapter {
	
	private class CCDemoCamera{
		@CCProperty(name = "camera near", min = 0, max = 10000)
		private double _cCameraNear = 10;
		
		@CCProperty(name = "camera far", min = 0, max = 10000)
		private double _cCameraFar = 1000;
		
		@CCProperty(name = "frustum offset x", min = -50, max = 50)
		private double _cFrustumOffsetX = 0;
		
		@CCProperty(name = "frustum offset y", min = -50, max = 50)
		private double _cFrustumOffsetY = 0;
		
		@CCProperty(name = "x rotation", min = -CCMath.HALF_PI, max = CCMath.HALF_PI)
		private double _cCameraXrotation = 0;
		
		@CCProperty(name = "y rotation", min = 0, max = CCMath.TWO_PI)
		private double _cCameraYrotation = 0;
		
		@CCProperty(name = "z rotation", min = 0, max = CCMath.TWO_PI)
		private double _cCameraZrotation = 0;

		@CCProperty(name = "fov", min = 0, max = CCMath.PI)
		private double _cCameraFov = CCMath.radians(60);
		
		private CCCamera _myCamera;
		private CCFrustum _myFrustum;
		
		public CCDemoCamera(final CCViewport theViewport, CCGraphics g) {
			_myCamera = new CCCamera(g);
			_myCamera.viewport(theViewport);
			_myFrustum = new CCFrustum(_myCamera);
		}
		
		public void update(final double theDeltaTime) {
			_myCamera.near(_cCameraNear);
			_myCamera.far(_cCameraFar);
			
			_myCamera.frustumOffset().x = _cFrustumOffsetX;
			_myCamera.frustumOffset().y = _cFrustumOffsetY;
			
			_myCamera.xRotation(_cCameraXrotation);
			_myCamera.yRotation(_cCameraYrotation);
			_myCamera.zRotation(_cCameraZrotation);
			
			_myCamera.fov(_cCameraFov);
			
			_myFrustum.updateFromCamera();
		}
		
		public void draw(CCGraphics g) {
			_myCamera.draw(g);
		}
		
		public void drawFrustum(CCGraphics g) {
			_myFrustum.drawLines(g);
			_myFrustum.drawNormals(g);
			_myFrustum.drawPoints(g);
		}
	}
	
	@CCProperty(name = "camera1")
	private CCDemoCamera _myCamera1;
	@CCProperty(name = "camera2")
	private CCDemoCamera _myCamera2;
	
//	private CCArcball _myArcball;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		_myArcball = new CCArcball(this);
		
		_myCamera1 = new CCDemoCamera(new CCViewport(0, 0, g.width()/2, g.height()), g);
		_myCamera2 = new CCDemoCamera(new CCViewport(g.width()/2, 0, g.width()/2, g.height()), g);
		
		keyListener().add(new CCKeyAdapter() {
			public void keyPressed(CCKeyEvent theKeyEvent) {
				switch(theKeyEvent.keyCode()) {
				case VK_C:
					_myDrawCamera = !_myDrawCamera;
					break;
				default:
				}
			}
		});
		
		glListener().add(new CCGL2Adapter(){
			@Override
			public void reshape(CCGraphics g) {
				_myCamera1._myCamera.viewport(new CCViewport(0, 0, g.width()/2, g.height()));
				_myCamera2._myCamera.viewport(new CCViewport(g.width()/2, 0, g.width()/2, g.height()));
			}
		});
		
	}

	@Override
	public void update(final CCAnimator theAnimator) {
		_myCamera1.update(theAnimator.deltaTime());
		_myCamera2.update(theAnimator.deltaTime());
	}
	
	private boolean _myDrawCamera = false;

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.polygonMode(CCPolygonMode.LINE);
		if(_myDrawCamera) {
			_myCamera1.draw(g);
			g.box(140);
			_myCamera2.draw(g);
			g.box(140);
		}else {
//			_myArcball.draw(g);
			g.box(140);
			
			_myCamera1.drawFrustum(g);
			_myCamera2.drawFrustum(g);
		}
		
		g.polygonMode(CCPolygonMode.FILL);
	}
	
	public static void main(String[] args) {
		CC2CamerasDemo demo = new CC2CamerasDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 300);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
