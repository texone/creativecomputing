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
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextureMapMeshText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCVector2;

public class CCTextureMapMeshTextDemo extends CCGL2Adapter {
	
	private CCTextureMapMeshText _myMeshMeshText;
	private CCText _myNormalText;
	
	private String _myText = "";
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		String myFont = "arial";
		float mySize = 24;
		
		_myText = CCLoremIpsumGenerator.generate(40);
		_myMeshMeshText = new CCTextureMapMeshText(CCFontIO.createTextureMapFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET));
		_myMeshMeshText.position(-400,300);
		_myMeshMeshText.dimension(300, 400);
		_myMeshMeshText.text(_myText);
		_myMeshMeshText.lineBreak(CCLineBreakMode.BLOCK);
		
		_myNormalText = new CCText(CCFontIO.createTextureMapFont(myFont, mySize, CCCharSet.EXTENDED_CHARSET));
		_myNormalText.position(0,300);
		_myNormalText.dimension(300, 400);
		_myNormalText.text(_myText);
		_myNormalText.lineBreak(CCLineBreakMode.BLOCK);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		_myMeshMeshText.draw(g);
		_myNormalText.draw(g);
		
		g.color(255,50);
//		_myMeshMeshText.boundingBox().draw(g);
		_myMeshMeshText.textGrid().drawGrid(g);
		
		int myIndex = _myMeshMeshText.textGrid().gridIndex(new CCVector2(mouse().position.x - g.width()/2, g.height()/2 - mouse().position.y));
		CCVector2 myPos = _myMeshMeshText.textGrid().gridPosition(myIndex);
		
		g.color(255,0,0);
		g.line(myPos.x, myPos.y, myPos.x, myPos.y - 20);
		g.line(-g.width()/2, 300, g.width()/2, 300);
	}
	
	

	public static void main(String[] args) {
		CCTextureMapMeshTextDemo demo = new CCTextureMapMeshTextDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
