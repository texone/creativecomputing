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
import cc.creativecomputing.math.CCVector2;

public class CCTextureMapChar extends CCChar{
	private final CCVector2 _myMin;
	private final CCVector2 _myMax;
	private final double _myBlurRadius;
	private final double _myDrawWidth;
	private final double _myDrawHeight;
	private final double _myXOffset;
	private final double _myYOffset;
	
	protected CCTextureMapChar(
		final char theChar, 
		final int theGlyphCode, 
		final double theX,
		final double theY,
		final double theDrawWidth,
		final double theDrawHeight,
		final double theWidth, 
		final double theHeight, 
		final CCVector2 theMin, 
		final CCVector2 theMax, 
		double theBlurRadius
	){
		super(theChar, theGlyphCode, theWidth, theHeight);
		_myXOffset = theX;
		_myYOffset = theY;
		_myDrawWidth = theDrawWidth;
		_myDrawHeight = theDrawHeight;
		_myMin = theMin;
		_myMax = theMax;
		_myBlurRadius = theBlurRadius;
	}

	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theSize) {
		final double myBlurRadius = _myBlurRadius * theSize;
		final double myWidth = _myDrawWidth * theSize;
		final double myHeight = _myDrawHeight * theSize;
		g.vertex(theX - myBlurRadius + _myXOffset,			 theY + myBlurRadius + _myYOffset,			   _myMin.x, _myMin.y);
		g.vertex(theX + myBlurRadius + _myXOffset + myWidth, theY + myBlurRadius + _myYOffset,			   _myMax.x, _myMin.y);
		g.vertex(theX + myBlurRadius + _myXOffset + myWidth, theY - myBlurRadius + _myYOffset - myHeight,  _myMax.x, _myMax.y);
		g.vertex(theX - myBlurRadius + _myXOffset,			 theY - myBlurRadius + _myYOffset - myHeight,  _myMin.x, _myMax.y);
		
		return myWidth;
	}
	
	public CCVector2 min(){
		return _myMin;
	}
	
	public CCVector2 max(){
		return _myMax;
	}
}
