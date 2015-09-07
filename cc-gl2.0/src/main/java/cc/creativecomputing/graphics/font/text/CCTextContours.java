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
package cc.creativecomputing.graphics.font.text;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.math.CCVector2;

/**
 * @author info
 * 
 */
public class CCTextContours extends CCText{

	private List<List<CCVector2>> _myContours = new ArrayList<List<CCVector2>>();

	/**
	 * Create a new Text object with the given Font
	 * 
	 * @param theFont
	 */
	public CCTextContours(final CCOutlineFont theFont) {
		super(theFont);
		text("");
	}
	
	//TODO fix this
	@Override
	public void breakText() {
		super.breakText();

//		for(CCTextGridLinePart myGridLine:_myTextGrid.gridLines()) {
//			for (int i = 0; i < myGridLine.charIndices().length; i++) {
//				int myCharIndex = myGridLine.charIndices()[i];
//				
//				final CCOutlineChar glyph = (CCOutlineChar)_myFont.chars()[myCharIndex];
//				
//				for(List<CCVector2> myPath:glyph.contour()){
//					List<CCVector2> myContour = new ArrayList<CCVector2>();
//					for(CCVector2 myVertex:myPath){
//						myContour.add(
//							new CCVector2(
//								_myPosition.x + myGridLine.x(i) + myVertex.x, 
//								_myPosition.y + myGridLine.y() + ascent() - myVertex.y
//							)
//						);
//					}
//					_myContours.add(myContour);
//				}
//			}
//			
//			
//		}
	}
	
	
	// TODO fix this
//	@Override
//	public CCTextContours clone() {
//		final CCTextContours _myResult = new CCTextContours((CCOutlineFont)_myFont);
//		_myResult._myPosition = _myPosition.clone();
//		_myResult.text(_myText);
//		_myResult.align(_myTextAlign);
//		return _myResult;
//	}
	
	public List<List<CCVector2>> contours(){
		return _myContours;
	}
	
	
}
