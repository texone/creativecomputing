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

import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
public class CCBitFont extends CCTextureMapFont{
	
	private CCImage _myImage;

	public CCBitFont(CCImage theImage, int theDescent) {
		super(null);
		_myImage = theImage;
		_myCharCount = 255;
		_myChars = createCharArray(_myCharCount); 
		_myCharCodes = new int[_myCharCount];
		
		_myHeight = _mySize = theImage.height();
		_myNormalizedHeight = 1;
		
		_myAscent = _myHeight - theDescent;
		_myDescent = theDescent;
//		
		_myNormalizedAscent = (float)_myAscent / _mySize;
		_myNormalizedDescent = (float)_myDescent / _mySize;
		
		_myLeading = _myHeight + 2;
		
		createChars();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#index(char)
	 */
	@Override
	public int index(char theChar) {
		int c = (int) theChar;
		return c - 32;
	}

	protected void createChars() {
		
		_mySize = _myImage.height();
			      
		int index = 0;

		int myX = 0;
		
		// array passed to createGylphVector
		
		int myLastX = 0;
		
		for (int x = 0; x < _myImage.width(); x++) {

        	myX++;
        	
        	boolean myApplyCut = !_myImage.getPixel(x, _myImage.height() - 1).equals(CCColor.RED);

        	for(int y = 0; y < _myImage.height(); y++) {
        		if(_myImage.getPixel(x, y).equals(CCColor.BLACK)) {
        			_myImage.setPixel(x, y, CCColor.WHITE);
        		}else {
        			_myImage.setPixel(x, y, CCColor.TRANSPARENT);
        		}
        	}
        	
	        if (myApplyCut) {
	        	continue;
	        }
	        
	        
	        
	        float myCharWidth = myX - myLastX;
	        
	        if(index == 0) {
	    		_mySpaceWidth = myCharWidth / _mySize;
	        }
	        
	        char c = (char)(index + 32);
			
			_myCharCodes[index] = c;
			
			
//			_myChars[index] = new CCTextureMapChar(c, -1, myCharWidth / _mySize, height(),
//				new CCVector2f(
//					myLastX / (float)_myData.width(),
//					1f
//				),
//				new CCVector2f(
//					myX / (float)_myData.width(),
//					0
//				),
//				0
//			);
			myLastX = myX;

			index++;
		}
		
		_myCharCount = index;
		
//		_myAscent = _myFontMetrics.getAscent();
//		_myDescent = _myFontMetrics.getDescent();
//		
//		_myLeading = _myFontMetrics.getLeading();
//		_mySpacing = _myFontMetrics.getHeight();
//		
//		_myNormalizedAscent = (float)_myFontMetrics.getAscent() / _mySize;
//		_myNormalizedDescent = (float)_myDescent / _mySize;
		_myFontTexture = new CCTexture2D(_myImage);
		_myFontTexture.textureFilter(CCTextureFilter.NEAREST);
	}
	
	
}
