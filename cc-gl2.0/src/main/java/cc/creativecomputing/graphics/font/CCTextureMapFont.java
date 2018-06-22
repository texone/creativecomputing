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
package cc.creativecomputing.graphics.font;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.util.CCDistanceFieldGenerator;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.graphics.texture.CCTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.image.filter.CCGaussianBlur;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;


public class CCTextureMapFont extends CCFont<CCTextureMapChar>{
	
	protected CCTexture2D _myFontTexture;
	protected CCImage _myFontImage;
	
	
	CCTextureMapFont(CCFontSettings theFontSettings) {
		super(theFontSettings);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CCTextureMapChar[] createCharArray(int theSize) {
		return new CCTextureMapChar[theSize];
	}

	@Override
	protected void createChars(BufferedImage theCharImage, Graphics2D theGraphics) {
		double myBlurRadius = 0;
		if(_mySettings != null ) {
			myBlurRadius = CCMath.max(_mySettings.blurRadius(), _mySettings.sdfSpread());
		}
		
		int myCharsPerRow = (int)(CCMath.sqrt(_myCharCount)*1.5f);
		int index = 0;

		// calculate width and height for the texture
		int myTextureWidth = 0;
		int myTextureHeight = 0;
		int myCurrentLineWidth = 0;
		int mySpaceAdd = (int)(0.2f * _mySize);
		int counter = 0;

		for (int i = 0; i < _myCharCount; i++){
			if(counter >= myCharsPerRow){
				myTextureWidth = CCMath.max(myTextureWidth, myCurrentLineWidth);
				myCurrentLineWidth = 0;
				myTextureHeight += _myHeight * 1.25f + myBlurRadius * 2;
				counter = 0;
			}
			char c = _myCharSet.chars()[i];
			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
				continue;
			}
			
			myCurrentLineWidth += (double)_myFontMetrics.charWidth(c)+mySpaceAdd+ myBlurRadius * 2;
			counter++;
		}
		myTextureHeight += _myHeight;
		
		BufferedImage myCharImage = new BufferedImage(myTextureWidth, myTextureHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D myGraphics = (Graphics2D) myCharImage.getGraphics();
		
		myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, _myIsAntialiase ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		myGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		myGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		myGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		myGraphics.setFont(_myFont);
//		myGraphics.setBackground(Color.cyan);
//		myGraphics.clearRect(0, 0, (int)myTextureWidth, (int)myTextureHeight);
		myGraphics.setColor(Color.white);
		
		int myX = (int)myBlurRadius;
		int myY = (int)myBlurRadius;
		
		// array passed to createGylphVector
		char textArray[] = new char[1];
		final FontRenderContext frc = myGraphics.getFontRenderContext();
		

		counter = 0;
		for (int i = 0; i < _myCharCount; i++){
			if(counter >= myCharsPerRow){
				myX = (int)myBlurRadius;
				counter = 0;
				myY += _myHeight * 1.25f + myBlurRadius * 2;
			}
			char c = _myCharSet.chars()[i];
			
			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
				continue;
			}

			if (c < 128){
				_myAsciiLookUpTable[c] = index;
			}
			
			_myCharCodes[index] = c;
			
			textArray[0] = c;
			final GlyphVector myGlyphVector = _myFont.createGlyphVector(frc,textArray);
			final Rectangle myPixelBounds = myGlyphVector.getPixelBounds(frc, myX, myY + _myFontMetrics.getAscent());
//			CCLog.info(c);
//			CCLog.info(
//				myPixelBounds.getMinX() + " " + 
//				myPixelBounds.getMinY() + " " + 
//				myPixelBounds.getMaxX() + " " + 
//				myPixelBounds.getMaxY() + " " + 
//				myPixelBounds.getWidth() + " " + 
//				myPixelBounds.getHeight()
//			);
//			CCLog.info(
//				(myX - myBlurRadius) + " " + 
//				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight - myBlurRadius) + " " + 
//				(myX + myBlurRadius + (double)_myFontMetrics.charWidth(c)) + " " + 
//				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f + myBlurRadius)
//			);
			
			double myXOffset = myX - myPixelBounds.getMinX();
			double myYOffset = (myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight) - myPixelBounds.getMinY();
			
			_myChars[index] = new CCTextureMapChar(
				c, 
				myGlyphVector.getGlyphCode(0), 
				-myXOffset,
				myYOffset,
				myPixelBounds.getWidth() / _mySize,
				myPixelBounds.getHeight() / _mySize,
				charWidth(c),
				_myHeight,
				new CCVector2(
					(myPixelBounds.getMinX() - myBlurRadius) / (double)myTextureWidth,
					1 - (myPixelBounds.getMinY() - myBlurRadius) / (double)myTextureHeight),
				new CCVector2(
					(myPixelBounds.getMaxX() + myBlurRadius) / (double)myTextureWidth,
					1 - (myPixelBounds.getMaxY() + myBlurRadius) / (double)myTextureHeight
				),
				myBlurRadius / _mySize
			);
			myGraphics.drawString(String.valueOf(c), myX, myY + _myFontMetrics.getAscent());
//			myGraphics.drawRect((int)myX, (int)myY, _myFontMetrics.charWidth(c) - 1, _myFontMetrics.getAscent() + _myFontMetrics.getDescent()-1);
			myX += _myFontMetrics.charWidth(c) + mySpaceAdd + myBlurRadius * 2;

			index++;
			counter++;
		}
		
		if(myBlurRadius > 0 && (_mySettings != null && !_mySettings.doSDF())) {
			CCGaussianBlur myBlur = new CCGaussianBlur(myBlurRadius);
			myCharImage = myBlur.filter(myCharImage);
		}
		
		
		
		_myCharCount = index;
		
		_myAscent = _myFontMetrics.getAscent();
		_myDescent = _myFontMetrics.getDescent();
		
		_myLeading = _myFontMetrics.getLeading();
		
		_myNormalizedAscent = (double)_myFontMetrics.getAscent() / _mySize;
		_myNormalizedDescent = _myDescent / _mySize;
		
		try{
			CCImage myFontImage = CCImageIO.newImage(myCharImage);
			if(_mySettings != null && _mySettings.doSDF()) {
//				CCDistanceFieldGenerator myGenerator = new CCDistanceFieldGenerator();
//				myGenerator.spread(_mySettings.sdfSpread());
//				
//				
//				
//				myFontImage = myGenerator.generateDistanceField(myFontImage);
				
				double[][] myImage = new double[myFontImage.width()][myFontImage.height()];
				for(int x = 0; x < myFontImage.width();x++) {
					for(int y = 0; y < myFontImage.height();y++) {
//						if(x % 40 > 20 && y % 40 > 20)_myImage.setPixel(x, y, new CCColor(1d,1d));
//						else _myImage.setPixel(x, y, new CCColor(1d,0d));
						myImage[x][y] = myFontImage.getPixel(x, y).r;
//		;				_myImage.setPixel(x, y, new CCColor(_myFontImage.getPixel(x, y).r));
					}
				}
				CCSignedDistanceField mySDF = new CCSignedDistanceField();
				double[][] myOut = new double[myFontImage.width()][myFontImage.height()];
				mySDF.buildDistanceField(myOut, _mySettings.sdfSpread(), myImage, myFontImage.width(), myFontImage.height());
				CCImage myNewFontImage = new CCImage(myFontImage.width(), myFontImage.height());
				for(int x = 0; x < myFontImage.width();x++) {
					for(int y = 0; y < myFontImage.height();y++) {
//						if(x % 40 > 20 && y % 40 > 20)_myImage.setPixel(x, y, new CCColor(1d,1d));
//						else _myImage.setPixel(x, y, new CCColor(1d,0d));
						
						myNewFontImage.setPixel(x, y, new CCColor(1d, myOut[x][y]));
		//;				_myImage.setPixel(x, y, );
					}
				}
				_myFontImage = myNewFontImage;
				_myFontTexture = new CCTexture2D(myNewFontImage);
			}else {
				_myFontImage = myFontImage;
				_myFontTexture = new CCTexture2D(myFontImage);
				
			}
			_myFontTexture.textureFilter(CCTextureFilter.LINEAR);
		}catch (Exception theException){
			// TODO Auto-generated catch block
			theException.printStackTrace();
			throw new RuntimeException(theException);
		}

	}
	
	public static void main(String[] args) {
		String myFont = "DeuBaUnivers-Regular";
		float mySize = 30;
		
		String myLorem = CCLoremIpsumGenerator.generate(40);
		
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize);
		CCFontIO.createTextureMapFont(mySettings);
	}
	
	@Override
	public void beginText(CCGraphics g){
		g.texture(_myFontTexture);
		g.beginShape(CCDrawMode.QUADS);
	}

    @Override
	public void endText(CCGraphics g){
		g.endShape();
		g.noTexture();
	}

    public CCTextureMapChar[] chars(){
		return _myChars;
	}
	
	public CCTexture2D texture(){
		return _myFontTexture;
	}
	
	public CCImage image(){
		return _myFontImage;
	}
}
