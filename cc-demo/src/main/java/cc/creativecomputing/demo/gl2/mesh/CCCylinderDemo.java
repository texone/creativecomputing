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
import cc.creativecomputing.demo.gl2.camera.CC2CameraControllerDemo;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCCylinderDemo extends CCGL2Adapter {

	public static class CCCylinderMesh extends CCVBOMesh {

		public CCCylinderMesh(double theRadius, double theLength, int theRadialResolution, int theLengthResolution) {
			super(CCDrawMode.TRIANGLES, (theRadialResolution + 1) * (theLengthResolution + 1) * 6);

			for (int r = 0; r < theRadialResolution; r++) {
				double myAngle0 = CCMath.map(r, 0, theRadialResolution, 0, CCMath.TWO_PI);
				double myAngle1 = CCMath.map(r + 1, 0, theRadialResolution, 0, CCMath.TWO_PI);
				CCVector2 myPoint0 = CCVector2.circlePoint(myAngle0, 1, 0, 0);
				CCVector2 myPoint1 = CCVector2.circlePoint(myAngle1, 1, 0, 0);

				double myV0 = CCMath.norm(r, 0, theRadialResolution - 1);
				double myV1 = CCMath.norm(r + 1, 0, theRadialResolution - 1);

				for (int l = 0; l < theLengthResolution; l++) {
					double myX0 = CCMath.map(l, 0, theLengthResolution, -theLength / 2, theLength / 2);
					double myX1 = CCMath.map(l + 1, 0, theLengthResolution, -theLength / 2, theLength / 2);
					
					double myU0 = CCMath.norm(l, 0, theLengthResolution - 1);
					double myU1 = CCMath.norm(l, 0, theLengthResolution - 1);

					addNormal(0, myPoint0.x, myPoint0.y);
					addNormal(0, myPoint1.x, myPoint1.y);
					addNormal(0, myPoint1.x, myPoint1.y);
					
					addNormal(0, myPoint0.x, myPoint0.y);
					addNormal(0, myPoint1.x, myPoint1.y);
					addNormal(0, myPoint0.x, myPoint0.y);

					addTextureCoords(myU0, myV0);
					addTextureCoords(myU0, myV1);
					addTextureCoords(myU1, myV1);

					addTextureCoords(myU0, myV0);
					addTextureCoords(myU1, myV1);
					addTextureCoords(myU1, myV0);
					
					addVertex(myX0, myPoint0.x * theRadius, myPoint0.y * theRadius);
					addVertex(myX0, myPoint1.x * theRadius, myPoint1.y * theRadius);
					addVertex(myX1, myPoint1.x * theRadius, myPoint1.y * theRadius);
					
					addVertex(myX0, myPoint0.x * theRadius, myPoint0.y * theRadius);
					addVertex(myX1, myPoint1.x * theRadius, myPoint1.y * theRadius);
					addVertex(myX1, myPoint0.x * theRadius, myPoint0.y * theRadius);
				}
				
				
			}
		}
	}

	private CCCylinderMesh _myMesh;
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;
	@CCProperty(name = "shader")
	private CCGLProgram _myCylinderShader;
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _myDrawttributes = new CCDrawAttributes();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myMesh = new CCCylinderMesh(1,1,360,1000);

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
