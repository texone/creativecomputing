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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;


public class CC3DFont extends CCFont<CC3DChar> {

	private CCVectorFontTesselator _myTesselator;
	private double _myBezierDetail;
	private double _myDepth;

	CC3DFont(CCFontSettings theSettings){
		super(theSettings);

		_myBezierDetail = theSettings.detail();
		_myDepth = theSettings.depth();
	}
	
	
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCFont#createCharArray(int)
	 */
	@Override
	protected CC3DChar[] createCharArray(int theSize) {
		return new CC3DChar[theSize];
	}

	@Override
	public void beginText(CCGraphics g){
//		g.beginShape(CCGraphics.TRIANGLES);
	}
	
	@Override
	public void endText(CCGraphics g){
//		g.endShape();
	}

	@Override
	protected void createChars(final BufferedImage theCharImage, final Graphics2D theGraphics) {
		_myTesselator = new CCVectorFontTesselator();
		
		// six element array received from the Java2D path iterator
		double textPoints[] = new double[6];

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
			
			final CC3DChar my3DChar = new CC3DChar(c, myGlyphVector.getGlyphCode(0), charWidth(c), height(), _mySize, _myDepth);
			_myTesselator.beginPolygon(my3DChar);

			double lastX = 0;
			double lastY = 0;

			while (!myPathIterator.isDone()) {
				int type = myPathIterator.currentSegment(textPoints);
				switch (type) {
					case PathIterator.SEG_MOVETO: // 1 point (2 vars) in textPoints
						_myTesselator.beginContour();
						my3DChar.beginPath();
					case PathIterator.SEG_LINETO: // 1 point
						_myTesselator.vertex(textPoints[0], textPoints[1] + _myFontMetrics.getAscent(), 0);
						my3DChar.addOutlineVertex(textPoints[0], textPoints[1] + _myFontMetrics.getAscent());
						lastX = textPoints[0];
						lastY = textPoints[1];
						break;
	
					case PathIterator.SEG_QUADTO: // 2 points
	
						for (int j = 1; j < _myBezierDetail; j++) {
							double t = (double) j / _myBezierDetail;
							_myTesselator.vertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[2], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[3], t) + _myFontMetrics.getAscent(), 
								0
							);
							my3DChar.addOutlineVertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[2], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[3], t) + _myFontMetrics.getAscent()
							);
						}
	
						lastX = textPoints[2];
						lastY = textPoints[3];
						break;
	
					case PathIterator.SEG_CUBICTO: // 3 points
						for (int j = 1; j < _myBezierDetail; j++) {
							double t = (double) j / _myBezierDetail;
							_myTesselator.vertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[4], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[5], t) + _myFontMetrics.getAscent(), 
								0
							);
							my3DChar.addOutlineVertex(
								CCMath.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[4], t), 
								CCMath.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[5], t) + _myFontMetrics.getAscent()
							);
						}
	
						lastX = textPoints[4];
						lastY = textPoints[5];
						break;
	
					case PathIterator.SEG_CLOSE:
						_myTesselator.endContour();
						my3DChar.endPath();
						break;
				}

				myPathIterator.next();
			}
			_myTesselator.endPolygon();
			_myChars[index] = my3DChar;

			index++;
		}

	}
	
	@Override
	public double drawChar(CCGraphics g, int theIndex, double theSize, double theX, double theY, double theZ) {
		if(theIndex < 0 || theIndex > _myChars.length || _myChars[theIndex] == null)return 0;
		_myChars[theIndex].depth(_myDepth);
		return _myChars[theIndex].draw(g, theX, theY, theZ, theSize);
	}
}
