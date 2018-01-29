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
import cc.creativecomputing.graphics.font.CCTextureMapChar;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.texture.CCTexture;

public class CCTextureMapMeshText extends CCText {
	
	private CCVBOMesh _myMesh;
	
	public CCTextureMapMeshText(final CCTextureMapFont theFont, int theSize) {
		super(theFont, theSize);
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS);
	}

	//TODO fix this
	@Override
	public void breakText() {
		super.breakText();
		int myVertices = 0;
		
		for(CCTextGridLinePart myGridLine:_myTextGrid.gridLines()) {
			for (int i = 0; i < myGridLine.charIndices().length; i++) {
				myVertices+=4;
			}
		}
		
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS,myVertices);
		for(CCTextGridLinePart myPart:_myTextGrid.gridLines()) {
			for (int i = 0; i < myPart.charIndices().length; i++) {
				int myCharIndex = myPart.charIndices()[i];
				
				final CCTextureMapChar glyph = (CCTextureMapChar)myPart.font().fontChar(myCharIndex);
				final double myWidth = myPart.font().width(myCharIndex) * size();
				final double myHeight = myPart.font().height() * size();

				double myX = _myPosition.x + myPart.x(i);
				double myY = _myPosition.y + myPart.y() + myPart.font().ascent();
				double myZ = _myPosition.z;
				_myMesh.addVertex(myX, myY, myZ);
				_myMesh.addVertex(myX + myWidth, myY, myZ);
				_myMesh.addVertex(myX + myWidth, myY - myHeight, myZ);
				_myMesh.addVertex(myX, myY - myHeight, myZ);

				_myMesh.addTextureCoords(glyph.quad().x0, glyph.quad().y0);
				_myMesh.addTextureCoords(glyph.quad().x1, glyph.quad().y0);
				_myMesh.addTextureCoords(glyph.quad().x1, glyph.quad().y1);
				_myMesh.addTextureCoords(glyph.quad().x0, glyph.quad().y1);
			}
		}
	}

	

	public CCVBOMesh mesh() {
		return _myMesh;
	}

	public CCTexture texture() {
		return ((CCTextureMapFont)font()).texture();
	}

	public void draw(CCGraphics g) {
		g.texture(texture());
		_myMesh.draw(g);
		g.noTexture();
	}
}
