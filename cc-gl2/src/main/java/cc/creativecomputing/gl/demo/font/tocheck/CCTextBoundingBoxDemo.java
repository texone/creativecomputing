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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCTextBoundingBoxDemo extends CCGL2Adapter {
	
	private CCFont<?> _myFont;
    private CCText _myText;

    @Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myFont = CCFontIO.createVectorFont("Helvetica", 50);
		_myText = new CCText(_myFont);
		_myText.text("TEXONEggg");
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255,0,0);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(_myText.boundingBox().min().x, _myText.boundingBox().min().y);
		g.vertex(_myText.boundingBox().max().x, _myText.boundingBox().min().y);
		g.vertex(_myText.boundingBox().max().x, _myText.boundingBox().max().y);
		g.vertex(_myText.boundingBox().min().x, _myText.boundingBox().max().y);
		g.endShape();
		g.color(255);
		_myText.draw(g);
		
	}

	public static void main(String[] args) {
		CCTextBoundingBoxDemo demo = new CCTextBoundingBoxDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

