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
package cc.creativecomputing.video;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.video.CCVideoTexture;
import cc.creativecomputing.video.Movie;

public class CCGStreamerDemo extends CCGL2Adapter {
	
	private Movie _myData;
	private CCVideoTexture _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120116_spline2_fine2_1356x136_jpg.mov"));
		_myData = new Movie(theAnimator, CCNIOUtil.dataPath("sintel_trailer-1080p.mp4"));//
//		_myData = new CCGStreamerMovie(this, "http://cabspotting.org/movies/lines-sf4hr.mpg");
		_myData.play();

		
		_myTexture = new CCVideoTexture(this,_myData);
	}
	
	@CCProperty(name = "play")
	public void play() {
		_myData.play();
	}
	
	@CCProperty(name = "stop")
	public void stop() {
		_myData.stop();
	}
	
	@CCProperty(name = "jump")
	public void jump() {
		_myData.time(40);
	}
	
	@CCProperty(name = "pause")
	public void pause() {
		_myData.pause();
	}
	
	float _myTime = 0;

	@Override
	public void update(final CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime(); 
	}

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
		
//		for(int x = 0; x < _myData.width();x+=10){
//			for(int y = 0; y < _myData.height();y+=10){
//				g.color(_myData.getPixel(x, y));
//				g.rect(x-_myTexture.width()/2, y-_myTexture.height()/2, 10,10);
//			}
//		}
		g.image(_myTexture, -_myTexture.width()/2, -_myTexture.height()/2);
	}

	public static void main(String[] args) {
		CCGStreamerDemo demo = new CCGStreamerDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1920, 1080);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

