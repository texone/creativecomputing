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
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
//import cc.creativecomputing.video.CCGStreamerMovie;

/**
 * @author christianriekoff
 *
 */
public class CCMoviePixelateDemo  extends CCGL2Adapter {
	
//	private CCGStreamerMovie _myData;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
//		_myData = new CCGStreamerMovie(this, CCIOUtil.dataPath("videos/120116_spline2_fine2_1356x136_jpg.mov"));
//		_myData = new CCGStreamerMovie(theAnimator, CCNIOUtil.dataPath("videos/station.mov"));
//		_myData.loop();
//		_myData.time(20);
		
		g.clearColor(1f);
		g.clear();
	}

	@Override
	public void update(final CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.color(255,30);
	}

	public static void main(String[] args) {
		CCMoviePixelateDemo demo = new CCMoviePixelateDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
