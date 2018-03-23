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


import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCVector3;

public class CCOutlineTextPathDemo extends CCGL2Adapter{

	private List<CCVector3> _myTextPath;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCOutlineFont font = CCFontIO.createOutlineFont("Arial",48, 30);
		g.bezierDetail(31);
		_myTextPath = font.getPath("TEXONE", CCTextAlign.CENTER, 192,0, 0, 0);
		g.clearColor(0.3f);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		for(CCVector3 myPoint:_myTextPath){
			g.vertex(myPoint);
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		CCOutlineTextPathDemo demo = new CCOutlineTextPathDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
