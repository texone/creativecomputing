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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.math.CCMath;

public class CCWriteDataShaderDemo extends CCGL2Adapter {
	
	private CCShaderBuffer _myShaderTexture;
	
	private CCVBOMesh _myMesh;
	protected CCGLWriteDataShader _mySetDataShader;

	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myShaderTexture = new CCShaderBuffer(32,4,4,400, 400);
		g.pointSize(2);
		g.smooth();
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS, _myShaderTexture.width() * _myShaderTexture.height());
		for (int i = 0; i < _myShaderTexture.width() * _myShaderTexture.height(); i++){
//			g.textureCoords3D(1, 1, 1, 1);
//			g.textureCoords3D(2, 0, 0, 0);
//			g.textureCoords3D(3, 1, 1, 1);
			_myMesh.addVertex(i % _myShaderTexture.width(),i / _myShaderTexture.width());
			_myMesh.addTextureCoords(0,CCMath.random(),CCMath.random(),CCMath.random(),1);
			_myMesh.addTextureCoords(1,CCMath.random(),0,0,1);
			_myMesh.addTextureCoords(2,0,CCMath.random(),0,1);
			_myMesh.addTextureCoords(3,0,0,CCMath.random(),1);
		}
		
		_mySetDataShader = new CCGLWriteDataShader();
//		g.debug();
	}

	public void display(CCGraphics g) {

		g.clearColor(0);
		g.clear();
		_myShaderTexture.beginDraw(g);
		_mySetDataShader.start();
		_myMesh.draw(g);
		_mySetDataShader.end();
		_myShaderTexture.endDraw(g);
		
		g.ortho2D();
		
		g.color(255);
		g.image(_myShaderTexture.attachment(0), 0,0,200,200);
		g.image(_myShaderTexture.attachment(1), 200,0,200,200);
		g.image(_myShaderTexture.attachment(2), 0,200,200,200);
		g.image(_myShaderTexture.attachment(3), 200,200,200,200);


	}

	public static void main(String[] args) {
		CCWriteDataShaderDemo demo = new CCWriteDataShaderDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
