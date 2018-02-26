/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package cc.creativecomputing.ui.draw;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.ui.CCUISpacing;

/**
 * @author christianriekoff
 *
 */
public class CCUITexture {
	
	public static class CCUITextureSplice{
		@CCProperty(name="texture_start")
		private double _myStart;
		@CCProperty(name="texture_end")
		private double _myEnd;
		
		@CCProperty(name="stretch")
		private boolean _myIsStretch;
		
		private CCUITextureSplice() {
			
		}
	}
	
	@CCProperty(name = "spacing")
	private CCUISpacing _mySpacing = new CCUISpacing();

	@CCProperty(name="file")
	private String _myTextureFile;
	
	private CCTexture2D _myTexture;
	
	@CCProperty(name="vertical_splices")
	private CCUITextureSplice[] _myVerticalSplices;
	
	@CCProperty(name="horizontal_splices")
	private CCUITextureSplice[] _myHorizontalSplices;
	
//	private double _myLeftOver = 0;
	
	private double[][] _myXpositions;
	private double[][] _myYpositions;
	
	public CCUITexture() {
		
	}
	
	private double[][] splicesToPositions(CCUITextureSplice[] theSplices, double theSize, double theTextureSize, double theStart) {
		
		
		double[][] myPositions;
		
		if(theSplices == null) {
			myPositions = new double[2][2];
			myPositions[0][0] = 0;
			myPositions[0][1] = 1;
			myPositions[1][0] = theStart;
			myPositions[1][1] = theStart + theSize;
			return myPositions;
		}else {
			myPositions = new double[2][theSplices.length + 1];
		}
		
		double myFixedSize = 0;
		int myNumberOfVariableSplices = 0;
		
		for(CCUITextureSplice mySplice : theSplices) {
			if(!mySplice._myIsStretch)myFixedSize += mySplice._myEnd - mySplice._myStart;
			else myNumberOfVariableSplices++;
		}
		
		double myVariableSize = (theSize - myFixedSize) / myNumberOfVariableSplices;
		
		int i = 1;
		double myPosition = 0;
		myPositions[0][0] = theSplices[0]._myStart / theTextureSize;
		myPositions[1][0] = theStart;
		
		for(CCUITextureSplice mySplice : theSplices) {
			if(mySplice._myIsStretch) {
				myPosition += myVariableSize;
			}else {
				myPosition += mySplice._myEnd - mySplice._myStart;
			}
			myPositions[0][i] = mySplice._myEnd / theTextureSize;
			myPositions[1][i] = theStart + myPosition;
			i++;
		}
		return myPositions;
	}
	
	public void dimension(double theWidth, double theHeight) {
		theWidth += _mySpacing.left();
		theWidth += _mySpacing.right();
		_myXpositions = splicesToPositions(_myHorizontalSplices, theWidth, _myTexture.width(), -_mySpacing.left());
		
		theHeight += _mySpacing.top();
		theHeight += _mySpacing.bottom();
		_myYpositions = splicesToPositions(_myVerticalSplices, theHeight, _myTexture.height(), -_mySpacing.top());
	}
	
	public void draw(CCGraphics g) {

		g.texture(_myTexture);
		g.beginShape(CCDrawMode.QUADS);
		
		for(int y = 0; y < _myYpositions[0].length - 1; y++) {
			for(int x = 0; x < _myXpositions[0].length - 1; x++) {
				double myTexX1 = _myXpositions[0][x];
				double myTexX2 = _myXpositions[0][x + 1];

				double myTexY1 = _myYpositions[0][y];
				double myTexY2 = _myYpositions[0][y + 1];
				
				double myX1 = _myXpositions[1][x];
				double myX2 = _myXpositions[1][x + 1];

				double myY1 = _myYpositions[1][y];
				double myY2 = _myYpositions[1][y + 1];
				
				g.textureCoords2D(myTexX1, myTexY1);
				g.vertex(myX1, myY1);

				g.textureCoords2D(myTexX2, myTexY1);
				g.vertex(myX2, myY1);

				g.textureCoords2D(myTexX2, myTexY2);
				g.vertex(myX2, myY2);

				g.textureCoords2D(myTexX1, myTexY2);
				g.vertex(myX1, myY2);
			}
		}
		
		g.endShape();
		g.noTexture();
	}
}
