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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCIcoSphere;
import cc.creativecomputing.math.CCVector3;

public class CCIcoSphereDemo extends CCGL2Adapter {
	
	private CCIcoSphere _mySphere;
	private CCVBOMesh _myMesh;
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _myDrawttributes = new CCDrawAttributes();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySphere = new CCIcoSphere(new CCVector3(), 200, 2);
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES);
		_myMesh.vertices(_mySphere.vertices());
		_myMesh.indices(_mySphere.indices());

		_myCameraController1 = new CCCameraController(this, g, 100);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myCameraController1.camera().draw(g);
		g.polygonMode(CCPolygonMode.LINE);
		_myMesh.draw(g);
	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCIcoSphereDemo());
		myAppManager.glcontext().size(1800, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start(); 
	}
}

