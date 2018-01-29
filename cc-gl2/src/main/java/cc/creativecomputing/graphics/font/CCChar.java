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

import cc.creativecomputing.graphics.CCGraphics;

/**
 * @author info
 *
 */
public abstract class CCChar {

	/**
	 * glyph code for kerning information
	 */
	protected final int _myGlyphCode;
	protected final char _myChar;
	protected final double _myWidth;
	protected final double _myHeight;
	
	protected final double _myBoundX0;
	protected final double _myBoundY0;
	protected final double _myBoundX1;
	protected final double _myBoundY1;
	
	protected final int _myAdvanceWidth;
	protected final int _myLeftSideBearing;
	
	CCChar(final char theChar, final int theGlyphCode, final int[] theBounds, final int[] theHMetrics){
		_myChar = theChar;
		_myGlyphCode = theGlyphCode;
		
		_myBoundX0 = theBounds[0];
		_myBoundY0 = theBounds[1];
		_myBoundX1 = theBounds[2];
		_myBoundY1 = theBounds[3];
		
		_myAdvanceWidth = theHMetrics[0];
		_myLeftSideBearing = theHMetrics[1];
		
		_myWidth = _myBoundX1 - _myBoundX0;
		_myHeight = _myBoundY1 - _myBoundY0;
	}
	
	CCChar(final CCChar theChar){
		_myChar = theChar.getChar();
		_myGlyphCode = theChar.glyphCode();
		
		_myBoundX0 = theChar.boundx0();
		_myBoundY0 = theChar.boundy0();
		_myBoundX1 = theChar.boundx1();
		_myBoundY1 = theChar.boundy1();
		
		_myAdvanceWidth = theChar.advanceWidth();
		_myLeftSideBearing = theChar.leftSideBearing();
		
		_myWidth = _myBoundX1 - _myBoundX0;
		_myHeight = _myBoundY1 - _myBoundY0;
	}
	
	/**
	 * the offset from the current horizontal position to the next horizontal position
	 * @return
	 */
	public int advanceWidth(){
		return _myAdvanceWidth;
	}
	
	/**
	 * the offset from the current horizontal position to the left edge of the character
	 * @return
	 */
	public int leftSideBearing(){
		return _myLeftSideBearing;
	}
	
	public double boundx0(){
		return _myBoundX0;
	}
	
	public double boundy0(){
		return _myBoundY0;
	}
	
	public double boundx1(){
		return _myBoundX1;
	}
	
	public double boundy1(){
		return _myBoundY1;
	}
	
	public int glyphCode() {
		return _myGlyphCode;
	}
	
	public char getChar() {
		return _myChar;
	}
	
	public double width() {
		return _myWidth;
	}
	
	public abstract double drawVertices(CCGraphics g, double theX, double theY, double theZ, double theSize);
	
	public abstract double draw(CCGraphics g, double theX, double theY, double theZ, double theSize);
}
