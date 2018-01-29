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

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.util.CCStopWatch;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCMultiFontText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCMultiFontTextDemo extends CCGLApp {

	CCVectorFont _myVectorFont;
	
	private CCMultiFontText _myText = new CCMultiFontText();
	
	private List<Path> _myFontList;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theAnimator) {
		_myText.position().set(-300,300,0);
		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.position(-300, 200);
		_myText.dimension(300, 400);

		_myFontList = CCNIOUtil.list(CCNIOUtil.dataPath("fonts"), true, "ttf");
		_myMainWindow.keyPressEvents.add(theEvent -> {
			switch(theEvent.key){
			case KEY_C:
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
			Path myFont = _myFontList.get((int)CCMath.random(_myFontList.size()));
			int mySize = (int)CCMath.random(10,30);
			String myText = CCLoremIpsumGenerator.generate((int)CCMath.random(5,20));
			CCLog.info(myFont + " " + mySize + " " + myText);
			CCStopWatch.instance().startWatch("create font");
			CCFont<?> myFontObject = new CCTextureMapFont(CCCharSet.EXTENDED,myFont, mySize);
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
//		_myText.boundingBox().draw(g);
		_myText.textGrid().drawGrid(g);
		
//		int myIndex = _myText.textGrid().gridIndex(new CCVector2(mouse().position.x - g.width()/2, g.height()/2 - mouse().position.y));
//		CCVector2 myPos = _myText.textGrid().gridPosition(myIndex);
//		
//		g.color(255,0,0);
//		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
//		g.line(-g.width()/2, 300, g.width()/2, 300);
	}
	

	public static void main(String[] args) {
		CCMultiFontTextDemo demo = new CCMultiFontTextDemo();
		
		demo.run();
		
//		for(Path myFont:CCNIOUtil.list(CCNIOUtil.dataPath("fonts"), true, "ttf")){
//			CCLog.info(myFont);
//		}
	}
}
