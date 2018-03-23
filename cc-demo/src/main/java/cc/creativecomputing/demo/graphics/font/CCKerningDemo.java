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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCKerningDemo extends CCGL2Adapter {

	CCVectorFont _myFont;
	CCVectorFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		double mySize = 70;
		
		_myFont = CCFontIO.createVectorFont("arial", mySize, CCCharSet.EXTENDED_CHARSET);
		

		_myText = new CCText(_myFont);
		_myText.text("L L ATAW.F. without kerning");
		_myText.position(-300,0);
		
		_myFont2 = CCFontIO.createVectorFont("font/Arial.ttf", mySize, CCCharSet.EXTENDED_CHARSET);
		
		for(char myChar1:CCCharSet.EXTENDED_CHARSET.chars()) {
			for(char myChar2:CCCharSet.EXTENDED_CHARSET.chars()) {
				double myKerning = _myFont2.kerning(myChar1, myChar2) * mySize;
				if(myKerning < 0)CCLog.info(myChar1 + ":" + myChar2 + ":" +myKerning);
			}
		}

		_myText2 = new CCText(_myFont2);
		_myText2.text("L L ATAW.F. with kerning");
		_myText2.position(-300,70);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		
		_myText.draw(g);
		_myText2.draw(g);
	}
	
	public static void main(String[] args) {
		CCKerningDemo demo = new CCKerningDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
