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
import cc.creativecomputing.core.util.CCStopWatch;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCMultiFontText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCMultiFontTextDemo extends CCGL2Adapter {

	CCVectorFont _myVectorFont;
	
	private CCMultiFontText _myText = new CCMultiFontText();
	
	private String[] myFontList = CCFontIO.list();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myText.position().set(-300,300,0);
		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.position(-300, 200);
		_myText.dimension(300, 400);

		keyPressed().add(theEvent -> {
			switch(theEvent.keyCode()){
			case VK_C:
				createText();
				break;
			default:
			}
		});
		
		createText();
	}
	
	int i = 0;
	
	private void createText(){
		// 4 sehr grosses leading checken
		// 6,8 leading komisch
		
		CCMath.randomSeed(i);
		_myText.reset();
		for(int i = 0; i < 5;i++){
			String myFont = myFontList[(int)CCMath.random(myFontList.length)];
			int mySize = (int)CCMath.random(10,30);
			String myText = CCLoremIpsumGenerator.generate((int)CCMath.random(5,20));
			CCLog.info(myFont + " " + mySize + " " + myText);
			CCStopWatch.instance().startWatch("create font");
			CCFont<?> myFontObject = CCFontIO.createTextureMapFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET);
			CCLog.info(CCStopWatch.instance().endWatch("create font"));
			_myText.addText(
				myText + " ", 
				myFontObject,
				mySize,
				CCColor.random()
			);
		}
		_myText.breakText();
		CCLog.info(i);
		i++;
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		
		_myText.draw(g);
		
		g.color(255,50);
//		_myText.boundingBox().display(g);
		_myText.textGrid().drawGrid(g);
		
		int myIndex = _myText.textGrid().gridIndex(new CCVector2(mouse().position.x - g.width()/2, g.height()/2 - mouse().position.y));
		CCVector2 myPos = _myText.textGrid().gridPosition(myIndex);
		
		g.color(255,0,0);
		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
		g.line(-g.width()/2, 300, g.width()/2, 300);
	}
	

	public static void main(String[] args) {
		CCMultiFontTextDemo demo = new CCMultiFontTextDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
