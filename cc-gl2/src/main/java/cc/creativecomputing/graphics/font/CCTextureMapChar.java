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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFontImage.CCFontQuad;

public class CCTextureMapChar extends CCChar{
	
	private CCFontQuad _myALignedQuad;
	
	protected CCTextureMapChar(
		final char theChar, 
		final int theGlyphCode, 
		final int[] theBounds,
		final int[] theHMetrics,
		final CCFontQuad theALignedQuad
	){
		super(theChar, theGlyphCode, theBounds, theHMetrics);
		_myALignedQuad = theALignedQuad;
	}
	
	public CCFontQuad quad(){
		return _myALignedQuad;
	}
	
	private void drawBoxTC(CCGraphics g, double x0, double y0, double x1, double y1, double s0, double t0, double s1, double t1) {
        g.textureCoords2D(s0, t0);
        g.vertex(x0, y0);
        g.textureCoords2D(s1, t0);
        g.vertex(x1, y0);
        g.textureCoords2D(s1, t1);
        g.vertex(x1, y1);
        g.textureCoords2D(s0, t1);
        g.vertex(x0, y1);
    }

	@Override
	public double drawVertices(CCGraphics g, double theX, double theY, double theZ, double theScale) {
		if(_myALignedQuad == null)return (_myAdvanceWidth) * theScale;
		drawBoxTC(
			g,
			_myALignedQuad.x0 * theScale + theX, theY - _myALignedQuad.y0 * theScale, _myALignedQuad.x1 * theScale + theX, theY - _myALignedQuad.y1 * theScale,
			_myALignedQuad.s0, _myALignedQuad.t0, _myALignedQuad.s1, _myALignedQuad.t1
		);
		
		return (_myALignedQuad.width) * theScale;
	}
	
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theScale) {
		g.beginShape(CCDrawMode.QUADS);
		double myResult = drawVertices(g, theX, theY, theZ, theScale);
		g.endShape();
		return myResult;
	}
	
	@Override
	public double width() {
		return _myALignedQuad.width;
	}
	
}
