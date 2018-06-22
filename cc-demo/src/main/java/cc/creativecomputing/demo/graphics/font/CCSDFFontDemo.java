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
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSDFFontDemo extends CCGL2Adapter {

	CCTextureMapFont _myFont;
	
	private CCText _myText;
	

	CCTextureMapFont _myFont2;
	
	private CCText _myText2;
	
	
	@CCProperty(name = "font shader")
	private CCGLProgram _cFontShader;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCFontIO.printFontList();
		_cFontShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "sdf_vertex.glsl"),
			CCNIOUtil.classPath(this, "sdf_fragment.glsl")
		);
		
		
		String myFont = "Times";
		float mySize = 32;
		
		String myLorem = CCLoremIpsumGenerator.generate(40);
		
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize, true, CCCharSet.REDUCED_CHARSET);
		mySettings.doSDF(true);
		mySettings.sdfSpread(8);
		_myFont = CCFontIO.createTextureMapFont(mySettings);
		
		_myText = new CCText(_myFont);
		_myText.size(32);
		_myText.width(300);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.text(myLorem);
		_myText.position(-300,0);
		
		CCFontSettings mySettings2 = new CCFontSettings(myFont, mySize, true, CCCharSet.REDUCED_CHARSET);
		_myFont2 = CCFontIO.createTextureMapFont(mySettings2);
		_myText2 = new CCText(_myFont2);
		_myText2.size(32);
		_myText2.width(300);
//		_myText2.lineBreak(CCLineBreakMode.BLOCK);
		_myText2.text(myLorem);
		_myText2.position(-300,30);
		
		g.clearColor(255,0,0);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.blend();
		g.color(255);
		g.scale(10);
		g.image(_myFont.texture(), -400,-400);
		_cFontShader.start();
		_cFontShader.uniform1i("fontTexture", 0);
		_myText.draw(g);
		_cFontShader.end();
		
		_myText2.draw(g);
	}
	
	public static void main(String[] args) {
		CCSDFFontDemo demo = new CCSDFFontDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
