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

import java.nio.file.Path;

import org.lwjgl.stb.STBTTPackedchar;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFontImage.CCFontQuad;
import cc.creativecomputing.graphics.texture.CCTexture;


public class CCTextureMapFont extends CCFont<CCTextureMapChar>{
	
	protected CCFontImage _myFontImage;
	
	private STBTTPackedchar.Buffer _myChardata;
	
	public CCTextureMapFont(CCFont<CCChar> theFont, CCFontImage theImage, int theSize, int theHOversample, int theVOversample){
		super(theFont);
		_myFontImage = theImage;
		theImage.oversampling(theHOversample, theVOversample);
		_myChardata = theImage.packFont(theFont, theSize);
		_mySize = theSize;
		createChars();
	}
	
	public CCTextureMapFont(CCCharSet theCharSet, Path thePath, int theSize, int theHOversample, int theVOversample) {
		super(theCharSet, thePath);
		_myFontImage = new CCFontImage(1024, 1024);
		_myFontImage.packBegin();
		_myFontImage.oversampling(theHOversample, theVOversample);
		_myChardata = _myFontImage.packFont(this, theCharSet.chars()[0], theSize);
		_myFontImage.packEnd();
		_mySize = theSize;
		createChars();
	}
	
	public CCTextureMapFont(CCCharSet theCharSet, Path thePath, int theSize){
		this(theCharSet, thePath, theSize, 1, 1);
	}
	
	public CCTextureMapFont(Path thePath, int theSize){
		this(CCCharSet.REDUCED, thePath, theSize, 1, 1);
	}
	
	public CCFontQuad quad(char theChar){
		return _myFontImage.quad(_myChardata, theChar);
	}
	
	@Override
	protected void createChars() {
		_myChars = new CCTextureMapChar[_myCharCodes.length];
		
		int myFirstChar = _myCharSet.chars()[0];
		for (int i = 0; i < _myCharCount; i++) {
			char myChar = _myCharSet.chars()[i];
			int myGlyphIndex = index(myChar);
			CCLog.info(i, (int)myChar, Integer.toHexString(myChar),_myChardata);
			CCFontQuad myQuad = null;
			CCLog.info((int)myChar, _myChardata.limit() ,myFirstChar);
			if((int)myChar - myFirstChar < _myChardata.limit())myQuad = _myFontImage.quad(_myChardata, myChar - myFirstChar);
			_myChars[myGlyphIndex] = new CCTextureMapChar(
				myChar, 
				myGlyphIndex, 
				boundingBox(myGlyphIndex),
				hMetrics(myGlyphIndex),
				myQuad
			);
			
		}
	}
	
	public double scaleForSize(double theSize){
		return theSize / _mySize;
	}
//	public CCTextureMapChar textureChar(char theChar){
//		xb.put(0, 0);
//	    yb.put(0, 0);
//	} 
//
//	@Override
//	protected void createChars() {
//		double myBlurRadius = _mySettings == null ? 0 : _mySettings.blurRadius();
//		
//		int myCharsPerRow = (int)(CCMath.sqrt(_myCharCount)*1.5f);
//		int index = 0;
//
//		// calculate width and height for the texture
//		int myTextureWidth = 0;
//		int myTextureHeight = 0;
//		int myCurrentLineWidth = 0;
//		int mySpaceAdd = (int)(0.2f * _mySize);
//		int counter = 0;
//
//		for (int i = 0; i < _myCharCount; i++){
//			if(counter >= myCharsPerRow){
//				myTextureWidth = CCMath.max(myTextureWidth, myCurrentLineWidth);
//				myCurrentLineWidth = 0;
//				myTextureHeight += _myHeight * 1.25f + myBlurRadius * 2;
//				counter = 0;
//			}
//			char c = _myCharSet.chars()[i];
//			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
//				continue;
//			}
//			
//			myCurrentLineWidth += (double)_myFontMetrics.charWidth(c)+mySpaceAdd+ myBlurRadius * 2;
//			counter++;
//		}
//		myTextureHeight += _myHeight;
//		
//		BufferedImage myCharImage = new BufferedImage(myTextureWidth, myTextureHeight, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D myGraphics = (Graphics2D) myCharImage.getGraphics();
//		
//		myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, _myIsAntialiase ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//		myGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//		myGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
//		myGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//		myGraphics.setFont(_myFont);
////		myGraphics.setBackground(Color.cyan);
////		myGraphics.clearRect(0, 0, (int)myTextureWidth, (int)myTextureHeight);
//		myGraphics.setColor(Color.white);
//		
//		int myX = (int)myBlurRadius;
//		int myY = (int)myBlurRadius;
//		
//		// array passed to createGylphVector
//		char textArray[] = new char[1];
//		final FontRenderContext frc = myGraphics.getFontRenderContext();
//		
//
//		counter = 0;
//		for (int i = 0; i < _myCharCount; i++){
//			if(counter >= myCharsPerRow){
//				myX = (int)myBlurRadius;
//				counter = 0;
//				myY += _myHeight * 1.25f + myBlurRadius * 2;
//			}
//			char c = _myCharSet.chars()[i];
//			
//			if(!_myFont.canDisplay(c) || _myFontMetrics.charWidth(c) <= 0){
//				continue;
//			}
//
//			if (c < 128){
//				_myAsciiLookUpTable[c] = index;
//			}
//			
//			_myCharCodes[index] = c;
//			
//			textArray[0] = c;
//			final GlyphVector myGlyphVector = _myFont.createGlyphVector(frc,textArray);
//			final Rectangle myPixelBounds = myGlyphVector.getPixelBounds(frc, myX, myY + _myFontMetrics.getAscent());
////			CCLog.info(c);
////			CCLog.info(
////				myPixelBounds.getMinX() + " " + 
////				myPixelBounds.getMinY() + " " + 
////				myPixelBounds.getMaxX() + " " + 
////				myPixelBounds.getMaxY() + " " + 
////				myPixelBounds.getWidth() + " " + 
////				myPixelBounds.getHeight()
////			);
////			CCLog.info(
////				(myX - myBlurRadius) + " " + 
////				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight - myBlurRadius) + " " + 
////				(myX + myBlurRadius + (double)_myFontMetrics.charWidth(c)) + " " + 
////				(myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f + myBlurRadius)
////			);
//			
//			double myXOffset = myX - myPixelBounds.getMinX();
//			double myYOffset = (myY + _myFontMetrics.getAscent() + _myFontMetrics.getDescent() * 1.25f - _myHeight) - myPixelBounds.getMinY();
//			
//			_myChars[index] = new CCTextureMapChar(
//				c, 
//				myGlyphVector.getGlyphCode(0), 
//				-myXOffset,
//				myYOffset,
//				myPixelBounds.getWidth() / _mySize,
//				myPixelBounds.getHeight() / _mySize,
//				charWidth(c),
//				_myHeight,
//				new CCVector2(
//					(myPixelBounds.getMinX() - myBlurRadius) / (double)myTextureWidth,
//					1 - (myPixelBounds.getMinY() - myBlurRadius) / (double)myTextureHeight),
//				new CCVector2(
//					(myPixelBounds.getMaxX() + myBlurRadius) / (double)myTextureWidth,
//					1 - (myPixelBounds.getMaxY() + myBlurRadius) / (double)myTextureHeight
//				),
//				myBlurRadius / _mySize
//			);
//			myGraphics.drawString(String.valueOf(c), myX, myY + _myFontMetrics.getAscent());
////			myGraphics.drawRect((int)myX, (int)myY, _myFontMetrics.charWidth(c) - 1, _myFontMetrics.getAscent() + _myFontMetrics.getDescent()-1);
//			myX += _myFontMetrics.charWidth(c) + mySpaceAdd + myBlurRadius * 2;
//
//			index++;
//			counter++;
//		}
//		
//		if(myBlurRadius > 0) {
//			CCGaussianBlur myBlur = new CCGaussianBlur(myBlurRadius);
//			myCharImage = myBlur.filter(myCharImage);
//		}
//		
//		_myCharCount = index;
//		
//		_myAscent = _myFontMetrics.getAscent();
//		_myDescent = _myFontMetrics.getDescent();
//		
//		_myLeading = _myFontMetrics.getLeading();
//		
//		_myNormalizedAscent = (double)_myFontMetrics.getAscent() / _mySize;
//		_myNormalizedDescent = _myDescent / _mySize;
//		
//		try{
//			_myFontTexture = new CCTexture2D(CCImageIO.newImage(myCharImage));
//			_myFontTexture.textureFilter(CCTextureFilter.LINEAR);
//		}catch (Exception theException){
//			// TODO Auto-generated catch block
//			theException.printStackTrace();
//			throw new RuntimeException(theException);
//		}
//
//	}
	
	@Override
	public void beginText(CCGraphics g){
		g.texture(_myFontImage.texture());
		g.beginShape(CCDrawMode.QUADS);
	}

    @Override
	public void endText(CCGraphics g){
		g.endShape();
		g.noTexture();
	}
	
	public CCTexture texture(){
		return _myFontImage.texture();
	}
}
