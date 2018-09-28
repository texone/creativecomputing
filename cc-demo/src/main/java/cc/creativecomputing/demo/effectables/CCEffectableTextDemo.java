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
package cc.creativecomputing.demo.effectables;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.modulation.CCColumnRowRingSource;
import cc.creativecomputing.effects.modulation.CCColumnRowSpiralSource;
import cc.creativecomputing.effects.modulation.CCPositionSource;
import cc.creativecomputing.effects.modulation.CCXYEuclidianDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYManhattanDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYRadialSource;
import cc.creativecomputing.effects.modulation.CCXYSignalSource;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapChar;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCMultiFontText.CCTextGridLinePart;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCVector2;

/**
 * Demonstrated how to extend the text class to create animated text
 * @author christianriekoff
 *
 */
public class CCEffectableTextDemo extends CCGL2Adapter {

	@CCProperty(name = "scale", min = 1, max = 10)
	private double _cScale = 1;
	@CCProperty(name = "rotate", min = -90, max = 90)
	private double _cRotate = 1;
	@CCProperty(name = "translate X", min = -1000, max = 1000)
	private double _cTranslateX = 1;
	@CCProperty(name = "translate Y", min = -2000, max = 2000)
	private double _cTranslateY = 1;
	
	private class CCCharEffectable extends CCEffectable {

		private CCTextGridLinePart _myGridLine;
		private int _myCharID;
		
		private double _myBlend;
		
		private CCTextureMapChar myChar;
		
		public CCCharEffectable(int theId, CCTextGridLinePart theGridLine, int theColumn, int theRow) {
			super(theId);
			
			CCTextureMapFont myFont = (CCTextureMapFont)theGridLine.font();
			myChar = (CCTextureMapChar)theGridLine.charByIndex(theColumn);
			
			column(theColumn);
			row(theRow);
			_myGridLine = theGridLine;
			_myCharID = theColumn;
		}
		
		
		@Override
		public void apply(double... theValues) {
			_myBlend = theValues[0];
		}
		
		public void draw(CCGraphics g) {
			g.color(_myBlend);
			_myGridLine.drawChar(g, _myCharID);
		}
	}
	
	public class CCEffectableText extends CCText{
		
		private CCFont<?> _myFont;
		
		private List<CCCharEffectable> _myChars = new ArrayList<>();

		/**
		 * @param theFont
		 */
		public CCEffectableText(CCFont<?> theFont) {
			super(theFont);
			_myFont = theFont;
		}
		
		public void update(double theDeltaTime) {
	
		}
		
		@Override
		public void breakText() {
			super.breakText();
			_myChars.clear();
			int myLine = 0;
			int myID = 0;
			for(CCTextGridLinePart myGridLines:_myTextGrid.gridLines()) {
				double myY = myGridLines.y();
				for(int i = 0; i < myGridLines.myNumberOfChars();i++) {
					double myX = myGridLines.x(i);	
					if(myGridLines.charByIndex(i).getChar() == ' ')continue;
					if(myGridLines.charByIndex(i).getChar() == '\n')continue;
					CCCharEffectable myEffectable = new CCCharEffectable(myID++, myGridLines, i, myLine);
					myEffectable.position().x = myX + position().x;
					myEffectable.position().y = myY + position().y;
					_myChars.add(myEffectable);
				}
				myLine++;
			}
		}
		
		@Override
		public void draw(CCGraphics g) {
				_myFont.beginText(g);
				for(CCCharEffectable myEffectable:_myChars) {
					myEffectable.draw(g);
				}
				_myFont.endText(g);
		}
	}
	
	CCTextureMapFont _myFont;
	
	private CCEffectableText _myText;
	
	@CCProperty(name = "effects")
	private CCEffectManager<CCCharEffectable> _myEffectManager;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		String myFont = "Theinhardt-Medium";
		double mySize = 48;
		
		_myFont = CCFontIO.createTextureMapFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET);
		_myText = new CCEffectableText(_myFont);
		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.dimension(600, 600);
		_myText.position(-300,300);
		_myText.text(CCLoremIpsumGenerator.generate(50));
//		_myText.lineBreak(CCLineBreakMode.BLOCK);	
		
		_myEffectManager = new CCEffectManager<CCCharEffectable>(_myText._myChars, "a");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.addRelativeSources(
			new CCColumnRowRingSource(),
			new CCColumnRowSpiralSource(),
			new CCPositionSource("position"),
			new CCXYEuclidianDistanceSource("euclidian", 200, new CCVector2()),
			new CCXYManhattanDistanceSource("manhattan", 200, 200, new CCVector2()),
			new CCXYRadialSource("radial", new CCVector2()),
			new CCXYSignalSource("signal")
		);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("offset2", new CCOffsetEffect());
		_myEffectManager.put("offset3", new CCOffsetEffect());
		
	}
	
	@Override
	public void update(final CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
		_myText.update(theAnimator.deltaTime());
	}

	@Override
	public void display(CCGraphics g) {
		
		g.pushMatrix();
		g.rotate(_cRotate);
		g.translate(_cTranslateX, _cTranslateY);
		g.scale(_cScale);
		g.clear();
		g.color(255);
		
		g.blend();
		_myText.draw(g);
		g.popMatrix();
		
//		g.color(255);
//		g.image(_myParticles.groupTexture(), 0,0, 400,400);
//		g.blend();
	}
	
	

	public static void main(String[] args) {
		CCEffectableTextDemo demo = new CCEffectableTextDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

