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
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;

public class CCAlignmentDemo extends CCGL2Adapter {

	private CCFont<?> _myFont;
	private CCText _myBlockText;
	private double _myTextBlockHeight;
	
	@CCProperty(name = "Align")
	private CCTextAlign _myAlign = CCTextAlign.JUSTIFY;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCFontIO.printFontList();
		_myFont = CCFontIO.createTextureMapFont( "data/font/SolexBol.ttf", 20, CCCharSet.EXTENDED_CHARSET);
		
		_myBlockText = new CCText(_myFont);
		_myBlockText.lineBreak(CCLineBreakMode.BLOCK);
		_myBlockText.position(0,0);
		_myBlockText.dimension(150, 100);
		_myBlockText.align(CCTextAlign.JUSTIFY);
		_myBlockText.text("Use CCBlockText to display text in a defined block €.");
		
		_myTextBlockHeight = _myBlockText.height();
	}
	
	@Override
	public void update(final CCAnimator theAnimator){
		_myBlockText.align(_myAlign);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		g.color(255);
		_myBlockText.draw(g);
		g.line(-g.width()/2, -_myTextBlockHeight, g.width()/2, -_myTextBlockHeight);
	}

	public static void main(String[] args) {
		CCAlignmentDemo demo = new CCAlignmentDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
