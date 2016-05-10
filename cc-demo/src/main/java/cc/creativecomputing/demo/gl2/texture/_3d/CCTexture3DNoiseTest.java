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
package cc.creativecomputing.demo.gl2.texture._3d;

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCTexture3DNoiseTest extends CCGL2Adapter{
	
	
	private float _myOffset = 0;
	

	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	@CCProperty (name = "noise amount", min = 0, max = 5)
	private float _cNoiseAmount = 1;
	
	@CCProperty (name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 1;
	
	@CCProperty (name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.LINEAR;

	private Path _myFolder = CCNIOUtil.dataPath("3d/");
	
	private CCTexture3D _myTexture;

	// 01 - 24
	// 25 - 39
	// 41 - 50
	// 52 - 63
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myTexture = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture.updateData(CCImageIO.newImage(myPath), i++);
			CCLog.info(i);
//			if(i >= 11)break;
		}

		_myCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myOffset += theAnimator.deltaTime() * 0.1f;
	}
	
	@CCProperty(name = "noise")
	private CCMixSignal _myNoise = new CCMixSignal();

	@Override
	public void display(CCGraphics g) {
		_myTexture.textureFilter(_cFilter);
		g.clear();
		_myCameraController.camera().draw(g);
		g.scale(2);
		g.translate(-320,-180);
		g.texture(_myTexture);
		
		float xres = 64;
		float yres = 32;
		
		float scale = 5;
		
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < xres;x++) {
			for(int y = 0; y < yres;y++) {
				float texX1 = x/xres;
				float texY1 = y/yres;
				
				float texX2 = (x + 1)/xres;
				float texY2 = (y + 1)/yres;
				
				g.textureCoords3D(texX1, texY1, _myNoise.value(texX1 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount); //texY1 * _cNoiseScale, 
				g.vertex(x * scale, y * scale);
				g.textureCoords3D(texX2, texY1, _myNoise.value(texX2 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount); //texY1 * _cNoiseScale, 
				g.vertex((x + 1) * scale, y * scale);
				g.textureCoords3D(texX2, texY2, _myNoise.value(texX2 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount); //texY2 * _cNoiseScale, 
				g.vertex((x + 1) * scale, (y + 1) * scale);
				g.textureCoords3D(texX1, texY2, _myNoise.value(texX1 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount); //texY2 * _cNoiseScale, 
				g.vertex(x * scale, (y + 1) * scale);
			}
		}
		g.endShape();
		g.noTexture();
		//g.image(_myTexture, -320,-180);
	}

	public static void main(String[] args) {
		CCTexture3DNoiseTest demo = new CCTexture3DNoiseTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

