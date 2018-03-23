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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCUnicodeBlock;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCUnicodeBlockDemo extends CCGL2Adapter {
	
	private CCText _myText;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCFont<?> myFont = CCFontIO.createTextureMapFont("Helvetica", 20, true, CCCharSet.EXTENDED_CHARSET);
		
		StringBuffer myBuffer = new StringBuffer("test");
		
		int myCounter = 1;
		
		for (char myChar : CCUnicodeBlock.LATIN_1_SUPPLEMENT.chars()) {
			if (!myFont.canDisplay(myChar)) {
				CCLog.info("cannot display:" + (int) myChar + " " + myChar);
			} else {
				CCLog.info("can display:" + (int) myChar + " " + myChar);
				myBuffer.append(myChar);
			}
			if(myCounter % 30 == 0) {
				myBuffer.append("\n");
			}
			myCounter++;
		}
		
		_myText = new CCText(myFont);
		_myText.text(myBuffer.toString());
		_myText.position(-g.width()/2 + 80, g.height()/2 - 80);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myText.draw(g);
	}

	public static void main(String[] args) {
		CCUnicodeBlockDemo demo = new CCUnicodeBlockDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
