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
package cc.creativecomputing.demo.topic.geometry;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCFilteredLinesDemo extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _myLineShader;
	
	private double _myRadius = 1;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCTexture2D _myFilterTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myFilterTexture = createFilterTexture(100);
		
		_myLineShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "filtered_lines_vertex.glsl"),
			CCNIOUtil.classPath(this, "filtered_lines_fragment.glsl")
		);
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	private CCTexture2D createFilterTexture(final int theTextureSize) {
		CCImage myData = new CCImage(theTextureSize,theTextureSize);
		
		// Fill in the filter texture
		for (int x = 0; x < theTextureSize; x++) {
			for (int y = 0; y < theTextureSize; y++) {
				double t = CCMath.sqrt(x * x + y * y) / theTextureSize;
				t = CCMath.min(t, 1.0f);
				t = CCMath.max(t, 0.0f);
				t = 1 - t;
				t = CCMath.smoothStep(0.0, 1.0, t);
//				t = CCMath.pow(t, 1f);
				myData.setPixel(x,y, new CCColor(t));
			}
		}
		CCTexture2D myFilterTexture = new CCTexture2D(myData);
		myFilterTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	    return myFilterTexture;
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	public void drawLine(CCGraphics g, CCVector3 p0, CCVector3 p1) {
		g.textureCoords3D(0,p1);
		g.textureCoords3D(1, -1, -1, 0);
		g.vertex(p0);
		g.textureCoords3D(0,p1);
		g.textureCoords3D(1, 1, 1, 0);
		g.vertex(p0);
		
		g.textureCoords3D(0,p0);
		g.textureCoords3D(1, -1,1, 1);
		g.vertex(p1);
		g.textureCoords3D(0,p0);
		g.textureCoords3D(1, 1,-1,1);
		g.vertex(p1);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		_cCameraController.camera().draw(g);
		g.polygonMode(CCPolygonMode.LINE);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		
		_myLineShader.start();
		_myLineShader.uniform1f("radius", _myRadius / g.width());
		_myLineShader.uniform1f("aspect", g.aspectRatio());
		g.beginShape(CCDrawMode.QUADS);
		for(int i = 0; i < 360;i+=30) {
			double x1 = CCMath.sin(CCMath.radians(i)) * 40;
			double y1 = CCMath.cos(CCMath.radians(i)) * 40;
			double x2 = CCMath.sin(CCMath.radians(i)) * 200;
			double y2 = CCMath.cos(CCMath.radians(i)) * 200;
			drawLine(g, new CCVector3(x1,y1,0), new CCVector3(x2,y2,0));
			drawLine(g, new CCVector3(x2,y2,40), new CCVector3(x2,y2,200));
		}

		g.endShape();
		_myLineShader.end();
	}

	public static void main(String[] args) {

		CCFilteredLinesDemo demo = new CCFilteredLinesDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
