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
package cc.creativecomputing.demo.graphics.font;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCTextWidthDemo extends CCGL2Adapter {

	private double _myTextWidth;
	private CCText _myText;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCTextureMapFont myFont = CCFontIO.createTextureMapFont("arial", 24, true, CCCharSet.EXTENDED_CHARSET);
		
		String myText = "CCCatch";
		
		_myText = new CCText(myFont);
		_myText.text(myText);
		_myTextWidth = _myText.width();
		
		System.out.println(_myTextWidth);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myText.draw(g);
		g.line(_myTextWidth,0,_myTextWidth,100);
		
	}

	public static void main(String[] args) {
		CCTextWidthDemo demo = new CCTextWidthDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

