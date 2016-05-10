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
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCTexture3DTest extends CCGL2Adapter{
	
	private CCTexture3D _myTexture;
	private float _myOffset = 0;
	

	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	private Path _myFolder = CCNIOUtil.dataPath("videos/crash/");

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myTexture = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture.updateData(CCImageIO.newImage(myPath), i++);
		}

		_myCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myOffset += theAnimator.deltaTime() * 0.1f;
	}
	
	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myCameraController.camera().draw(g);
		g.translate(-320,-180);
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < 64;x++) {
			for(int y = 0; y < 32;y++) {
				float texX1 = x/64f;
				float texY1 = y/32f;
				
				float texX2 = (x + 1)/64f;
				float texY2 = (y + 1)/32f;
				
				g.textureCoords3D(texX1, texY1, _myNoise.value(texX1, texY1, _myOffset));
				g.vertex(x * 10, y * 10);
				g.textureCoords3D(texX2, texY1, _myNoise.value(texX2, texY1, _myOffset));
				g.vertex((x + 1) * 10, y * 10);
				g.textureCoords3D(texX2, texY2, _myNoise.value(texX2, texY2, _myOffset));
				g.vertex((x + 1) * 10, (y + 1) * 10);
				g.textureCoords3D(texX1, texY2, _myNoise.value(texX1, texY2, _myOffset));
				g.vertex(x * 10, (y + 1) * 10);
			}
		}
		g.endShape();
		g.noTexture();
		//g.image(_myTexture, -320,-180);
	}
	
	public static void main(String[] args) {
		CCTexture3DTest demo = new CCTexture3DTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

