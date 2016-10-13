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
package cc.creativecomputing.demo.topic.simulation.dla;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCDLADemo extends CCGL2Adapter {
	
	@CCProperty(name = "dla")
	private CCDLA _myDLA;

	private CCText _myText;

	@CCProperty(name = "swp width", min = 0, max = 1)
	private double _cSwapWidth = 0.3;
	@CCProperty(name = "swp height", min = 0, max = 1)
	private double _cSwapHeight = 0.3;
	
	@CCProperty(name = "screencapture")
	private CCScreenCaptureController _myScreenCapture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myDLA = new CCDLA(g, 250, 250, g.width(), g.height());
		_myText = new CCText(CCFontIO.createVectorFont("Helvetica-Bold", 80));
		_myText.text(
				"We can modify CHO cell lines\nto provide viral resistance to \nMVM. Learn more at booth\n608 at #BPSMT");
		_myText.position(30, 500);
		
		_myScreenCapture = new CCScreenCaptureController(this);
		

		_myDLA.beginCrystal();
		_myText.draw(g);
		_myDLA.endCrystal();
	}
	
	private boolean _myReset = false;

	@CCProperty(name = "reset")
	public void reset() {
		_myReset = true;
	}
	
	public void update(final float theDeltaTime) {
	}
	
	

	public void display(CCGraphics g) {
		if (_myReset) {
			_myReset = false;
			_myDLA.reset();
			_myDLA.beginCrystal();
			
//			g.color(255);
//			_myText.draw(g);
//			for (int i = 0; i < g.width(); i += 100) {
//				g.color(CCColor.createFromHSB(CCMath.norm(i, 0, g.width()), 1., 1.));
//				g.ellipse(i, g.height() / 2, 5);
//				// g.line(i, g.height() / 2 -100, i, g.height() / 2 + 100);
//			}
			g.color(255);

			_myText.draw(g);
			_myDLA.endCrystal();
		}

		_myDLA.beginCrystal();
		g.color(0);
		
		g.rect(0, 0, _cSwapWidth * g.width(), _cSwapHeight * g.height());
		g.color(255);
		_myDLA.endCrystal();
		_myDLA.update(g, animator());
		
		g.clear();
		g.pushMatrix();
		g.translate(-g.width()/2, -g.height()/2);
		g.blend(CCBlendMode.LIGHTEST);
		_myDLA.draw(g);
		g.popMatrix();
		g.blend();
	}

	public static void main(String[] args) {
		CCDLADemo demo = new CCDLADemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

