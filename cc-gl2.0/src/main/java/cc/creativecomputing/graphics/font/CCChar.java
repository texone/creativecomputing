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
	
	CCChar(final char theChar, final int theGlyphCode, final double theWidth, final double theHeight){
		_myChar = theChar;
		_myGlyphCode = theGlyphCode;
		_myWidth = theWidth;
		_myHeight = theHeight;
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
	
	public abstract double draw(CCGraphics g, double theX, double theY, double theZ, double theSize);
}
