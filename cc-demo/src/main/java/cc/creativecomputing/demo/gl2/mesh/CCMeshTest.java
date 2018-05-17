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
package cc.creativecomputing.demo.gl2.mesh;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.gl2.camera.CC2CameraControllerDemo;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCVector3;


public class CCMeshTest extends CCGLApp {
	
	private CCMesh _myMesh;
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;

	@Override
	public void setup() {
		_myMesh = new CCMesh(CCDrawMode.TRIANGLES, 12000);
		
		for(int i = 0; i < 12000;i++){
			_myMesh.addVertex(new CCVector3().randomize(100));
//			_myMesh.addColor(CCMath.random(), CCMath.random(), CCMath.random());
		}

		_myCameraController1 = new CCCameraController(this, g, 100);
	}

	public void display(CCGraphics g) {
		_myCameraController1.camera().draw(g);
		g.clear();
		g.noDepthTest();
		g.blendMode(CCBlendMode.ADD);
		g.color(1f,0.05f);
		_myMesh.draw(g);
	}
	
	public static void main(String[] args) {
		CCMeshTest demo = new CCMeshTest();
		
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
//		myAppManager.glcontext().size(800, 300);
//		myAppManager.animator().framerate = 30;
//		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.run();
	}
}
