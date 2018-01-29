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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.io.CCNIOUtil;

public class CCAlignmentDemo extends CCGLApp {

	private CCFont<?> _myFont;
	private CCText _myBlockText;
	private double _myTextBlockHeight;
	
	@CCProperty(name = "Align")
	private CCTextAlign _myAlign = CCTextAlign.JUSTIFY;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theAnimator) {
//		_myFont = new CCTextureMapFont(CCCharSet.REDUCED_CHARSET, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"));
//		
//		_myBlockText = new CCText(_myFont, 10);
//		_myBlockText.lineBreak(CCLineBreakMode.BLOCK);
//		_myBlockText.position(0,0);
//		_myBlockText.dimension(150, 100);
//		_myBlockText.align(CCTextAlign.JUSTIFY);
//		_myBlockText.text("Use CCBlockText to display text in a defined block.");
//		
//		_myTextBlockHeight = _myBlockText.height();
	}
	
	@Override
	public void update(final CCGLTimer theAnimator){
//		_myBlockText.align(_myAlign);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		g.color(255);
//		_myBlockText.draw(g);
		g.line(-g.width()/2, -_myTextBlockHeight, g.width()/2, -_myTextBlockHeight);
	}

	public static void main(String[] args) {
		CCAlignmentDemo demo = new CCAlignmentDemo();
		demo.width = 1280;
		demo.height = 720;
		demo.run();
	}
}
