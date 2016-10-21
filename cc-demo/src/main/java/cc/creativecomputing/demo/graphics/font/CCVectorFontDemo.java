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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCVectorFontDemo extends CCGL2Adapter {
	
	@CCProperty(name = "font size", min = 0, max = 200)
	private double _cFontSize = 0;

	private double _myTextWidth;
	private CCText _myText;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCVectorFont myFont = CCFontIO.createVectorFont("arial", 24, CCCharSet.EXTENDED_CHARSET);
		
		String myText = "CCCatch";
		_myTextWidth = myFont.width(myText);
		
		System.out.println(_myTextWidth);
		
		_myText = new CCText(myFont);
		_myText.text(myText);
	}


	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myText.size(_cFontSize);
		_myText.draw(g);
		
	}

	public static void main(String[] args) {
		CCVectorFontDemo demo = new CCVectorFontDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
