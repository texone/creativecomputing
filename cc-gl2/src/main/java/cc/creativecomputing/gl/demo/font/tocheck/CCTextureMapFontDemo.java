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
package cc.creativecomputing.gl.demo.font.tocheck;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;

public class CCTextureMapFontDemo extends CCGL2Adapter {

	CCTextureMapFont _myFont;
	CCTextureMapFont _myFont2;
	
	private CCText _myText;
	private CCText _myText2;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		String myFont = "DeuBaUnivers-Regular";
		float mySize = 30;
		
		String myLorem = CCLoremIpsumGenerator.generate(40);
		
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize);
		mySettings.blurRadius(10);
		_myFont = CCFontIO.createTextureMapFont(mySettings);
		CCFontSettings mySettings2 = new CCFontSettings(myFont, mySize);
		_myFont2 = CCFontIO.createTextureMapFont(mySettings2);
		_myText = new CCText(_myFont);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.text(myLorem);
		_myText.position(-300,300);

		_myText2 = new CCText(_myFont2);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText2.text(myLorem);
		_myText2.position(-300,300);
		
		g.clearColor(255);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.blend();
		g.color(0);
//		g.image(_myFont.texture(), -400,-400);

		
		_myText.draw(g);
		g.color(255);
		_myText2.draw(g);
	}
	
	public static void main(String[] args) {
		CCTextureMapFontDemo demo = new CCTextureMapFontDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
