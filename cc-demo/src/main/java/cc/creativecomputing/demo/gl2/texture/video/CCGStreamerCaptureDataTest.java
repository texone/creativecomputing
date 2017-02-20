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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCVideoTexture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.video.CCGStreamerCapture;
import cc.creativecomputing.video.CCGStreamerCapture.CCGStreamerCaptureResolution;

public class CCGStreamerCaptureDataTest extends CCGL2Adapter {
	
	private CCGStreamerCapture _myData;
	private CCVideoTexture _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		for(String myDevice : CCGStreamerCapture.list()) {
			CCLog.info(myDevice);
		}
		
		_myData = new CCGStreamerCapture(this, 640, 480, 30);
		_myData.start();
		
		for(CCGStreamerCaptureResolution myResolution:_myData.resolutions()) {
			CCLog.info("RES");
			CCLog.info(myResolution.width+":"+myResolution.height);
			CCLog.info(myResolution.fps);
		}
		
		_myTexture = new CCVideoTexture<CCGStreamerCapture>(_myData);
	}
	
	float _myTime = 0;
	float _myNoise = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime; 
		_myNoise = CCMath.noise(_myTime * 0.1f);
	}

	@Override
	public void draw() {
		g.clearColor(1f);
		g.clear();
//		g.blend(CCBlendMode.ADD);
		g.texture(_myTexture);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(-width/2, -height/2, -0.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2, -height/2, 1.5f * _myNoise, -0.5f *_myNoise);
		g.vertex( width/2,  height/2, 1.5f * _myNoise, 1.5f *_myNoise);
		g.vertex(-width/2,  height/2, -0.5f * _myNoise, 1.5f *_myNoise);
		g.endShape();
		g.noTexture();
		
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height()/2);
	}

	public static void main(String[] args) {
		CCGStreamerCaptureDataTest demo = new CCGStreamerCaptureDataTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

