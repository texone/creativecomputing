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
	
	/**
	 * @param theChar
	 * @param theWidth
	 */
	CCOutlineChar(char theChar, int theGlyphCode, int[] theBoundingBox, int[] theHMetrics) {
		super(theChar, theGlyphCode, theBoundingBox, theHMetrics);
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
	
	@Override
	public double drawVertices(CCGraphics g, double theX, double theY, double theZ, double theScale) {
		for(List<CCVector2> myPath:_myContour){
			CCVector2 myLast = null;
			for(CCVector2 myVertex:myPath){
				if(myLast != null){
					g.vertex(
						theX + myLast.x * theScale, 
						theY + myLast.y * theScale,
						theZ
					);
					g.vertex(
						theX + myVertex.x * theScale, 
						theY + myVertex.y * theScale,
						theZ
					);
				}
				
				myLast = myVertex;
			}
		}
		return _myWidth * theScale;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theScale) {

		g.beginShape(CCDrawMode.LINES);
		drawVertices(g, theX, theY, theZ, theScale);
		g.endShape();
		
		return _myWidth * theScale;
	}
}
