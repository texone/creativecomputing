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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCVectorChar;
import cc.creativecomputing.graphics.font.CCVectorFont;

public class CCVectorMeshText extends CCText {
	
	private CCVBOMesh _myMesh;

	public CCVectorMeshText(CCVectorFont theFont) {
		super(theFont);
	}
	
	public CCVectorMeshText(final String theFont, final float theSize) {
		this(CCFontIO.createVectorFont(theFont, theSize));
	}
	
	// TODO fix this
	@Override
	public void breakText() {
		super.breakText();
		int myVertices = 0;
		for(CCTextGridLinePart myPart:_myTextGrid.gridLines()) {
			for (int i = 0; i < myPart.charIndices().length; i++) {
				int myCharIndex = myPart.charIndices()[i];
				
				final CCVectorChar glyph = (CCVectorChar)myPart.font().chars()[myCharIndex];
				myVertices += glyph.numberOfVertices();
			}
		}

		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES,myVertices);
		for(CCTextGridLinePart myPart:_myTextGrid.gridLines()) {
			for (int i = 0; i < myPart.charIndices().length; i++) {
				int myCharIndex = myPart.charIndices()[i];
				
				final CCVectorChar glyph = (CCVectorChar)myPart.font().chars()[myCharIndex];

				for(int v = 0; v < glyph.numberOfVertices();v++){
					_myMesh.addVertex(
						_myPosition.x + myPart.x(i) + glyph.vertices()[v * 2],
						_myPosition.y + myPart.y() + myPart.font().ascent() - glyph.vertices()[v * 2 + 1],
						_myPosition.z
					);
				}
			}
		}
	}

	@Override
	public void draw(CCGraphics g) {
		_myMesh.draw(g);
	}
}
