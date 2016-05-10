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


import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.imaging.CCGPUGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCBlurTest extends CCGL2Adapter {
	
	private CCTexture2D _myTexture;
	private CCGPUGaussianBlur _myConvolutionFilter;

	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/waltz.jpg")));
		_myConvolutionFilter = new CCGPUGaussianBlur(g,5);
		_myConvolutionFilter.texture(_myTexture);
	}

	public void display(CCGraphics g) {
		g.clear();
		_myConvolutionFilter.start();
		g.image(_myTexture, -g.width()/2, -g.height()/2);
		_myConvolutionFilter.end();
		g.noTexture();
		
		g.image(_myTexture, 0, -g.height()/2);
	}

	public static void main(String[] args) {
		CCBlurTest demo = new CCBlurTest();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
