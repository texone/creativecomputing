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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;


public class CCOutlineChar extends CCChar{

	private final List<List<CCVector2>>_myContour = new ArrayList<List<CCVector2>>();
	private List<CCVector2>_myPath;
	private double _mySize;
	
	/**
	 * @param theChar
	 * @param theWidth
	 */
	CCOutlineChar(char theChar, int theGlyphCode, double theWidth, double theHeight, double theSize) {
		super(theChar, theGlyphCode, theWidth, theHeight);
		_mySize = theSize;
	}
	
	public void beginPath(){
		_myPath = new ArrayList<CCVector2>();
	}
	
	public void addVertex(final CCVector2 theVector){
		_myPath.add(theVector);
	}
	
	public void endPath(){
		_myContour.add(_myPath);
	}
	
	public List<List<CCVector2>> contour(){
		return _myContour;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theSize) {
		final double myWidth = _myWidth * theSize;
		
		final double myScale = theSize / _mySize;
		
		for(List<CCVector2> myPath:_myContour){
			g.beginShape(CCDrawMode.LINE_LOOP);
			for(CCVector2 myVertex:myPath){
				g.vertex(
					theX + myVertex.x * myScale, 
					theY - myVertex.y * myScale,
					theZ
				);
			}
			g.endShape();
		}
		
		return myWidth;
	}
}
