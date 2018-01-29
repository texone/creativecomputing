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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;


public class CCVectorFont extends CCFont <CCVectorChar>{

	public CCVectorFont(CCCharSet theCharSet, Path theFontPath, int theSize){
		super(theCharSet, theFontPath);
		_mySize = theSize;
		createChars();
	}
	
	@Override
	public double scaleForSize(double theSize) {
		return scaleForPixelHeight(theSize);
	}
	
	@Override
	public void beginText(CCGraphics g){
		g.beginShape(CCDrawMode.TRIANGLES);
	}

    @Override
	public void endText(CCGraphics g){
		g.endShape();
	}

    @Override
	protected void createChars() {
    	_myChars = new CCVectorChar[_myCharCodes.length];
		
		for (int i = 0; i < _myCharCount; i++) {
			char myChar = _myCharSet.chars()[i];
			int myGlyphIndex = index(myChar);
			_myChars[myGlyphIndex] = fill(myChar);
		}
	}
}
