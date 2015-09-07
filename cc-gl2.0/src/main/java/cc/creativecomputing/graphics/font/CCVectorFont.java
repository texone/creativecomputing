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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;


public class CCVectorFont extends CCFont<CCVectorChar> {

	private CCVectorFontTesselator _myTesselator;
	private float _myBezierDetail;

	public CCVectorFont(CCFontSettings theSettings){
		super(theSettings);

		_myBezierDetail = theSettings.detail();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CCVectorChar[] createCharArray(int theSize) {
		return new CCVectorChar[theSize];
	}
	
	@Override
	public void beginText(CCGraphics g){
		g.beginShape(CCDrawMode.TRIANGLES);
	};
	
	@Override
	public void endText(CCGraphics g){
		g.endShape();
	};

	@Override
	protected void createChars(final BufferedImage theCharImage, final Graphics2D theGraphics) {
		_myTesselator = new CCVectorFontTesselator();
		
		// six element array received from the Java2D path iterator
		float textPoints[] = new float[6];

		// array passed to createGylphVector
		char textArray[] = new char[1];

		final Graphics2D myGraphics = theGraphics;
		final FontRenderContext frc = myGraphics.getFontRenderContext();

		int index = 0;
		
		for (int i = 0; i < _myCharCount; i++) {
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
			final Shape myShape = myGlyphVector.getOutline();
			final PathIterator myPathIterator = myShape.getPathIterator(null,0.05);
			
			final CCVectorChar myVectorChar = new CCVectorChar(c, myGlyphVector.getGlyphCode(0), charWidth(c), height(),_mySize);
			_myTesselator.beginPolygon(myVectorChar);

			float lastX = 0;
			float lastY = 0;
			
			while (!myPathIterator.isDone()) {
				int type = myPathIterator.currentSegment(textPoints);
				switch (type) {
					case PathIterator.SEG_MOVETO: // 1 point (2 vars) in textPoints
						_myTesselator.beginContour();
					case PathIterator.SEG_LINETO: // 1 point
						_myTesselator.vertex(textPoints[0], textPoints[1] + _myFontMetrics.getAscent(), 0);
						lastX = textPoints[0];
						lastY = textPoints[1];
						break;
	
					case PathIterator.SEG_QUADTO: // 2 points
	
						for (int j = 1; j < _myBezierDetail; j++) {
							float t = (float) j / _myBezierDetail;
							_myTesselator.vertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[2], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[3], t) + _myFontMetrics.getAscent(), 
								0
							);
						}
	
						lastX = textPoints[2];
						lastY = textPoints[3];
						break;
	
					case PathIterator.SEG_CUBICTO: // 3 points
						for (int j = 1; j < _myBezierDetail; j++) {
							float t = (float) j / _myBezierDetail;
							_myTesselator.vertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[4], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[5], t) + _myFontMetrics.getAscent(), 
								0
							);
						}
	
						lastX = textPoints[4];
						lastY = textPoints[5];
						break;
	
					case PathIterator.SEG_CLOSE:
						_myTesselator.endContour();
						break;
				}

				myPathIterator.next();
			}
			
			_myTesselator.endPolygon();
			_myChars[index] = myVectorChar;

			index++;
		}
	}
}
