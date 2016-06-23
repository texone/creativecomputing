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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.random.CCRandom;

public class CCShaderHeightMap extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	private CCMesh _myMesh;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	private CCTexture2D _myHeightMap;
	
	@CCProperty(name = "lod", min = 0, max = 10)
	private float _cLod = 1;
	
	private CCTexture2D _myRandomTexture;
	
	private CCTexture2D _myInputMask;
	
	private CCRandom _myRandom = new CCRandom();
	
	private class CCNoiseControl{
		@CCProperty(name = "octaves", min = 1, max = 10)
		private int octaves = 4; 
		@CCProperty(name = "gain", min = 0, max = 1)
		private float gain = 0.5f; 
		@CCProperty(name = "lacunarity", min = 0, max = 10)
		private float lacunarity = 2f; 
		
		@CCProperty(name = "speed x", min = -10, max = 10)
		private float speedX = 0f; 
		@CCProperty(name = "speed y", min = -10, max = 10)
		private float speedY = 1.0f; 
		@CCProperty(name = "speed z", min = -10, max = 10)
		private float speedZ = 0f; 
		@CCProperty(name = "speed gain", min = 0, max = 2)
		private float speedGain = 0.5f; 
	}
	
	@CCProperty(name = "noise")
	private CCNoiseControl _cNoiseControl = new CCNoiseControl();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/mipmap_vp.glsl"),
			CCNIOUtil.classPath(this, "shader/mipmap_fp.glsl")
		);
		int res = 1000;
		_myMesh = new CCMesh(CCDrawMode.TRIANGLES, res*res);
		List<Integer> _myIndices = new ArrayList<Integer>();
		for(int x = 0; x < res;x++) {
			for(int y = 0; y < res;y++) {
				_myMesh.addVertex(x * 2 - res, y * 2 - res);
				_myMesh.addTextureCoords(0, x / (double)res, y / (double)res);
				if(x < res - 1 && y < res - 1){
					_myIndices.add(x * res + y);
					_myIndices.add((x + 1) * res + y);
					_myIndices.add(x * res + y + 1);

					_myIndices.add((x + 1) * res + y);
					_myIndices.add((x + 1) * res + y + 1);
					_myIndices.add(x * res + y + 1);
				}
			}
		}
		_myMesh.indices(_myIndices);
		
		_myHeightMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/height_alps.png")));
		_myHeightMap.generateMipmaps(true);
		_myHeightMap.textureFilter(CCTextureFilter.LINEAR);
		_myHeightMap.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		CCColor[][] myBaseColorMap = new CCColor[256][256];
		
		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				myBaseColorMap[x][y] = new CCColor(_myRandom.random(),0,0,0);
			}
		}

		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				int x2 = (x + 37) % 256;
				int y2 = (y + 17) % 256;
				myBaseColorMap[x2][y2].g = myBaseColorMap[x][y].r;
			}
		}
		
		CCImage myData = new CCImage(256,256);
		for(int x = 0; x < myData.width(); x++){
			for(int y = 0; y < myData.height(); y++){
				myData.setPixel(x, y, myBaseColorMap[x][y]);
			}
		}
		
		_myRandomTexture = new CCTexture2D(myData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);

		_myCameraController = new CCCameraController(this, g, 100);
	}
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();

	@Override
	public void display(CCGraphics g) {
		g.clearColor(100);
		g.clear();

		_myCameraController.camera().draw(g);
		_cAttributes.start(g);
		g.pushMatrix();
		g.scale(10);
		_myShader.start();
		g.texture(0, _myHeightMap);
		g.texture(1, _myRandomTexture);
		_myShader.uniform1i("texture", 0);
		_myShader.uniform1f("lod", _cLod);
		_myShader.uniform1i("randomTexture",1);
		_myShader.uniform2f("randomTextureResolution",_myRandomTexture.width(), _myRandomTexture.height());

		_myShader.uniform1i("inputMask", 1);
		
		_myShader.uniform1i("octaves", _cNoiseControl.octaves);
		_myShader.uniform1f("gain", _cNoiseControl.gain);
		_myShader.uniform1f("lacunarity", _cNoiseControl.lacunarity);
		
		_myShader.uniform3f("noiseMovement", _cNoiseControl.speedX, _cNoiseControl.speedY, _cNoiseControl.speedZ);
		_myShader.uniform1f("speedGain", _cNoiseControl.speedGain);
		_myMesh.draw(g);
		g.noTexture();
		_myShader.end();
		g.popMatrix();
		_cAttributes.end(g);
	}

	public static void main(String[] args) {
		CCShaderHeightMap demo = new CCShaderHeightMap();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

