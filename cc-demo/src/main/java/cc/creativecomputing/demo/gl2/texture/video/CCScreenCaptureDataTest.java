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
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.video.CCScreenCaptureData;
import cc.creativecomputing.video.CCVideoTexture;

public class CCScreenCaptureDataTest extends CCGL2Adapter {
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureData _myData;
	private CCVideoTexture _myVideoTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myData = new CCScreenCaptureData(theAnimator, 0,0,1200, 800, 60);
		_myData.grabArea().isActive(true);
		_myVideoTexture = new CCVideoTexture(this,_myData, CCTextureTarget.TEXTURE_2D, myAttributes);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.image(_myVideoTexture,-g.width()/2, -g.height()/2);
		CCLog.info(_myData.captureRate());
	}

	public static void main(String[] args) {

		CCScreenCaptureDataTest demo = new CCScreenCaptureDataTest();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

