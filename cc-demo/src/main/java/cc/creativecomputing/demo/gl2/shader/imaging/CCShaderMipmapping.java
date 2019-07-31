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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderMipmapping extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	private CCTexture2D _myHeightMap;
	
	@CCProperty(name = "lod", min = 1, max = 10)
	private float _cLod = 1;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/mipmap_vp.glsl"),
			CCNIOUtil.classPath(this, "shader/mipmap_fp.glsl")
		);
		
		_myMesh = new CCMesh(CCDrawMode.POINTS, 200*200);
		for(int x = 0; x < 200;x++) {
			for(int y = 0; y < 200;y++) {
				_myMesh.addVertex(x * 2 - 200, y * 2 - 200);
				_myMesh.addTextureCoords(0, x / 200f, y / 200f);
			}
		}
		
		_myHeightMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/texone2.png")));
		_myHeightMap.generateMipmaps(true);
		_myHeightMap.textureFilter(CCTextureFilter.LINEAR);
		_myHeightMap.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);

		_myCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(100);
		g.clear();

		_myCameraController.camera().draw(g);
		g.noDepthTest();
		_myShader.start();
		g.texture(0, _myHeightMap);
		_myShader.uniform1i("texture", 0);
		_myShader.uniform1f("lod", _cLod);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
	}

	public static void main(String[] args) {
		CCShaderMipmapping demo = new CCShaderMipmapping();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

