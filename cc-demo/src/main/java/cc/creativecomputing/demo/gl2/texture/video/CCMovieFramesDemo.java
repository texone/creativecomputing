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
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.io.CCNIOUtil;
//import cc.creativecomputing.video.CCGStreamerMovie;
import cc.creativecomputing.video.CCVideoTexture;

public class CCMovieFramesDemo extends CCGL2Adapter {

//	private CCGStreamerMovie _myData;
	private CCVideoTexture _myTexture;
	
	private int _myNewFrame;
	
	private CCText _myText;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myText = new CCText(CCFontIO.createVectorFont("arial", 24));
		
//		_myData = new CCGStreamerMovie(theAnimator, CCNIOUtil.dataPath("videos/120123_counter_640x64_30fps_anim.mov"));
//		_myTexture = new CCVideoTexture(_myData);
	}

	@Override
	public void update(final CCAnimator theAnimator) {
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		g.image(_myTexture, -g.width() / 2, -g.height() / 2, g.width(), g.height());
		
		g.color(240, 20, 30);
		_myText.position(-g.width() / 2 + 10, -g.height() / 2 + 30);
//		_myText.text(_myData.frame() + " / " + (_myData.numberOfFrames() - 1) + " / " + _myNewFrame);
		_myText.draw(g);
	}

	public void keyPressed(CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_LEFT:
			if (_myNewFrame > 0)
				_myNewFrame--;
			break;
		case VK_RIGHT:
//			if (_myNewFrame < _myData.numberOfFrames() * 2 - 1)
//				_myNewFrame++;
			break;
		default:
		}

//		_myData.frame(_myNewFrame / 2);
	}

	public static void main(String[] args) {
		CCMovieFramesDemo demo = new CCMovieFramesDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

