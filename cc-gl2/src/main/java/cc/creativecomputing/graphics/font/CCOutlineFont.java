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

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3;


public class CCOutlineFont extends CCFont<CCOutlineChar> {

	public CCOutlineFont(CCCharSet theCharSet, Path thePath, int theSize) {
		super(theCharSet, thePath);
		_mySize = theSize;
		createChars();
	}

	@Override
	protected void createChars() {

    	_myChars = new CCOutlineChar[_myCharCodes.length];
		
		for (int i = 0; i < _myCharCount; i++) {

			char myChar = _myCharSet.chars()[i];
			int myGlyphIndex = index(myChar);
			_myChars[myGlyphIndex] = outline(myChar);
		}
	}
	
	@Override
	public double scaleForSize(double theSize) {
		return scaleForPixelHeight(theSize);
	}
	
	@Override
	public void beginText(CCGraphics g){
		g.beginShape(CCDrawMode.LINES);
	}

    @Override
	public void endText(CCGraphics g){
		g.endShape();
	}
	
	
	
//	public CCMesh createMesh(
//		final String theText, final CCTextAlign theTextAlign, final double theTextSize,
//		double theX, final double theY, final double theZ
//	){
//		
//		final char[] myTextBuffer = theText.toCharArray();
//		if (theTextAlign == CCTextAlign.CENTER){
//			theX -= width(myTextBuffer, 0, myTextBuffer.length) / 2f * theTextSize;
//		}else if (theTextAlign == CCTextAlign.RIGHT){
//			theX -= width(myTextBuffer, 0, myTextBuffer.length) * theTextSize;
//		}
//
//		final CCTriangulator myTriangulator = new CCTriangulator();
//			
//		for (int index = 0; index < myTextBuffer.length; index++){
//			final char myChar = myTextBuffer[index];
//			switch(myChar){
//				case ' ':
//					theX += spaceWidth() * theTextSize;
//					break;
//				default:
//					final int myIndex = index(myChar);
//					if(myIndex < 0)continue;
//					
//					final CCOutlineChar myOutlineChar = _myChars[myIndex];
//					final double myWidth = width(myIndex) * theTextSize;
//					
//					final double myScale = theTextSize/_mySize;
//					
//					final List<List<CCVector2>> _myContour = myOutlineChar.contour();
//					myTriangulator.beginPolygon();
//					for(List<CCVector2> myPath:_myContour){
//						myTriangulator.beginContour();
//						for(CCVector2 myVertex:myPath){
//							myTriangulator.vertex(new CCVector3(
//								theX + myVertex.x * myScale, 
//								theY - myVertex.y * myScale,
//								theZ
//							));
//						}
//						myTriangulator.endContour();
//					}
//					myTriangulator.endPolygon();
//							
//					theX +=  myWidth;
//			}
//		}
//		
//		CCMesh myMesh = new CCVBOMesh();
//		myMesh.vertices(myTriangulator.vertices());
//		return myMesh;
//	}
}
