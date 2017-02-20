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
package cc.creativecomputing.demo.gl2.texture.video;

import java.nio.file.Paths;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCVideoTexture;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.video.CCFFMPGMovie;
import cc.creativecomputing.video.CCVideoAsset;

public class CCVideoAssetDemo extends CCGL2Adapter {
	
	@CCProperty(name = "video")
	private CCVideoAsset _myData;
	private CCVideoTexture _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myData = new CCVideoAsset(theAnimator);
		_myData = new CCFFMPGMovie(theAnimator, CCNIOUtil.dataPath("videos/kaki.mov"));
//		_myData = new CCFFMPGMovie(theAnimator, Paths.get("/Users/christianr/dev/artcom/unity/media-wall/media-wall/AssetCache/170214_SAO_PAULO-FullRes.mp4"));
		_myData.loop();
		_myData.printInfo();
		_myTexture = new CCVideoTexture(this,_myData);
	}

	@Override
	public void update(final CCAnimator theDeltaTime) {
	}
	
	private boolean _myLoop = false;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(1f);
		g.clear();
		
//		g.texture(_myTexture);
//		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
//		g.beginShape(CCDrawMode.QUADS);
//		g.vertex(-width/2, -height/2, -0.5f * _myNoise, -0.5f *_myNoise);
//		g.vertex( width/2, -height/2, 1.5f * _myNoise, -0.5f *_myNoise);
//		g.vertex( width/2,  height/2, 1.5f * _myNoise, 1.5f *_myNoise);
//		g.vertex(-width/2,  height/2, -0.5f * _myNoise, 1.5f *_myNoise);
//		g.endShape();
//		g.noTexture();
		
		_myTexture.display(g);
		
		g.color(255);
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height()/2);
		
		g.color(255,0,0);
		g.line(-g.width() / 2, 0, g.width()/2, 0);
		double myPos = CCMath.map(_myData.time(), 0, _myData.duration(), -g.width() / 2, g.width() / 2);
		
		g.line(myPos, -5, myPos, 5);
	}

	public static void main(String[] args) {
		CCVideoAssetDemo demo = new CCVideoAssetDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
