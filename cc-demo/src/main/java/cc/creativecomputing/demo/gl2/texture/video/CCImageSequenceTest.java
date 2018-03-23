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

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVideoTexture;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.video.CCImageSequence;

public class CCImageSequenceTest extends CCGL2Adapter {
	
	private CCImageSequence _myData;
	private CCVideoTexture _myVideoTexture;
	
	@CCProperty(name = "position", min = 0, max = 1)
	private double _cPosition = 0;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		
		_myData = new CCImageSequence(theAnimator, CCNIOUtil.dataPath("videos/crash01"));
//		_myData.start(true);
		_myVideoTexture = new CCVideoTexture(this,_myData, CCTextureTarget.TEXTURE_2D, myAttributes);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myData.time(_cPosition * _myData.duration());
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.image(_myVideoTexture,-g.width()/2, -g.height()/2);
		CCLog.info(_myData.time());
	}

	public static void main(String[] args) {

		CCImageSequenceTest demo = new CCImageSequenceTest();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

