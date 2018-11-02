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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCCylinderMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.io.CCNIOUtil;

public class CCCylinderDemo extends CCGL2Adapter {

	private CCCylinderMesh _myMesh;
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;
	@CCProperty(name = "shader")
	private CCGLProgram _myCylinderShader;
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _myDrawttributes = new CCDrawAttributes();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myMesh = new CCCylinderMesh(1,1,720,2000);

		_myCameraController1 = new CCCameraController(this, g, 100);
		_myCylinderShader = new CCGLProgram(
			CCNIOUtil.classPath(this,"cylinder_vert.glsl"), 
			CCNIOUtil.classPath(this,"cylinder_frag.glsl")
		);
	}

	public void display(CCGraphics g) {
		_myCameraController1.camera().draw(g);
		g.clear();
		g.noDepthTest();
		g.blendMode(CCBlendMode.ADD);
		g.color(1f, 1f);
		_myDrawttributes.start(g);
		g.texture(0, CCGLShaderUtil.randomTexture());
		_myCylinderShader.start();
		_myCylinderShader.uniform1f("iTime", animator().time());
		_myMesh.draw(g);
		_myCylinderShader.end();
		g.noTexture();
		_myDrawttributes.end(g);
	}

	public static void main(String[] args) {
		CCCylinderDemo demo = new CCCylinderDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1800, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start(); 
	}
}
