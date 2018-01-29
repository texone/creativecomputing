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
package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCColor;

/**
 * Shape representing a 2D rectangle
 * @author christian riekoff
 *
 */
public class CCRectangle extends CCAbstractShape{
	
	protected double _myX;
	protected double _myY;
	
	protected double _myWidth;
	protected double _myHeight;
	
	/**
	 * Creates a new rectangle using the given color and coordinates.
	 * @param theColor
	 * @param theX1
	 * @param theY1
	 * @param theX2
	 * @param theY2
	 */
	public CCRectangle(
		final double theX, final double theY, 
		final double theWidth, final double theHeight,
		final CCColor theColor
	){
		super();
		_myX = theX;
		_myY = theY;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myColor = theColor;
		
		_myBoundingRectangle = new CCAABoundingRectangle(_myX, _myY, _myX + _myWidth, _myY + _myHeight);
	}
	
	public CCRectangle() {
		this(0,0,0,0,new CCColor());
	}

	/**
	 * Draws the rectangle
	 * @param theG
	 */
	public void draw(CCGraphics g) {
		if(_myColor != null)g.color(_myColor);
		
		double myX1 = _myX;
		double myY1 = _myY;
		
		double myX2 = _myX + _myWidth * _myScale;
		double myY2 = _myY + _myHeight * _myScale;
		
		switch(_myShapeMode){
		case CENTER:
			myX1 -= _myWidth * _myScale / 2;
			myX2 -= _myWidth * _myScale / 2;

			myY1 -= _myHeight * _myScale / 2;
			myY2 -= _myHeight * _myScale / 2;
			break;
		default:
			break;
		}
		
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(myX1, myY1);
		g.vertex(myX2, myY1);
		g.vertex(myX2, myY2);
		g.vertex(myX1, myY2);
		g.endShape();
	}



	@Override
	public void position(double theX, double theY) {
		_myX = theX;
		_myY = theY;
	}
	
	public void size(final double theWidth, final double theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
}
