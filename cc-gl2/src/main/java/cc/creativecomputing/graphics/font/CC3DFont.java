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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;


public class CC3DFont extends CCFont<CC3DChar> {
	
	private double _myDepth;

	public CC3DFont(CCCharSet theCharset, Path thePath, double theDepth){
		super(theCharset, thePath);
		_myDepth = theDepth;
		createChars();
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
	protected void createChars() {
		_myChars = new CC3DChar[_myCharCodes.length];
		
		for (int i = 0; i < _myCharCount; i++) {
			char myChar = _myCharSet.chars()[i];
			int myGlyphIndex = index(myChar);
			CCVectorChar myVectorChar = fill(myChar);
			CCOutlineChar myOutlineChar = outline(myChar);
			_myChars[myGlyphIndex] = new CC3DChar(myVectorChar, myOutlineChar, _myDepth);
		}

	}
	
	
}
