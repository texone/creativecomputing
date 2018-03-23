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
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCMath;

/**
 * Demonstrated how to extend the text class to create animated text
 * @author christianriekoff
 *
 */
public class CCBlinkingTextDemo extends CCGL2Adapter {
	
	private class CCBlinkingText extends CCText{
		

		private double[][] _myAngles;
		private double[][] _myFreqs;

		/**
		 * @param theFont
		 */
		public CCBlinkingText(CCFont<?> theFont) {
			super(theFont);
		}
		
		public void update(double theDeltaTime) {
			for(int i = 0; i < _myAngles.length;i++) {
				for(int j = 0; j < _myAngles[i].length;j++) {
					_myAngles[i][j] += theDeltaTime * _myFreqs[i][j];
				}
			}
		}
		
		@Override
		public void breakText() {
			super.breakText();
			_myAngles = new double[textGrid().gridLines().size()][];
			_myFreqs = new double[textGrid().gridLines().size()][];
			for(int i = 0; i < textGrid().gridLines().size();i++) {
				int myNumberOfChars = textGrid().gridLines().get(i).myNumberOfChars();
				_myAngles[i] = new double[myNumberOfChars];
				_myFreqs[i] = new double[myNumberOfChars];
				for(int j = 0; j < myNumberOfChars;j++) {
					_myAngles[i][j] = CCMath.random(CCMath.TWO_PI);
					_myFreqs[i][j] = CCMath.random(2, 5);
				}
			}
		}
		
		@Override
		public void draw(CCGraphics g) {
			_myFont.beginText(g);
			int myLine = 0;
			for(CCTextGridLinePart myGridLines:_myTextGrid.gridLines()) {
				for(int i = 0; i < myGridLines.myNumberOfChars();i++) {
					double myAlpha = CCMath.sin(_myAngles[myLine][i]) / 2f + 0.5f;
					g.color(1f,CCMath.blend(0.3f, 1f, myAlpha));
					myGridLines.drawChar(g, i);	
				}
				myLine++;
			}
			_myFont.endText(g);
		}
	}
	
	CCTextureMapFont _myFont;
	
	private CCBlinkingText _myText;
	
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		String myFont = "arial";
		double mySize = 24;
		
		_myFont = CCFontIO.createTextureMapFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET);
		_myText = new CCBlinkingText(_myFont);
		_myText.dimension(300, 400);
		_myText.text(CCLoremIpsumGenerator.generate(400));
		_myText.position(-300,300);
		_myText.lineBreak(CCLineBreakMode.BLOCK);	
	}
	
	@Override
	public void update(final CCAnimator theAnimator) {
		_myText.update(theAnimator.deltaTime());
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);
		
		g.blend();
		_myText.draw(g);
	}
	
	

	public static void main(String[] args) {
		CCBlinkingTextDemo demo = new CCBlinkingTextDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

