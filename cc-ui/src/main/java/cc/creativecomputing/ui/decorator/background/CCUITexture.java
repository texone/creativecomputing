/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.decorator.background;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.CCUISpacing;

/**
 * @author christianriekoff
 *
 */
@CCXMLPropertyObject(name = "uitexture")
public class CCUITexture {
	
	@CCXMLPropertyObject(name="texture_splice")
	public static class CCUITextureSplice{
		@CCXMLProperty(name="texture_start", node=false)
		private double _myStart;
		@CCXMLProperty(name="texture_end", node=false)
		private double _myEnd;
		
		@CCXMLProperty(name="stretch", node=false)
		private boolean _myIsStretch;
		
		private CCUITextureSplice() {
			
		}
	}
	
	@CCXMLProperty(name = "spacing", optional = true)
	private CCUISpacing _mySpacing = new CCUISpacing();

	@CCXMLProperty(name="file")
	private String _myTextureFile;
	
	private CCTexture2D _myTexture;
	
	@CCXMLProperty(name="vertical_splices", optional = true)
	private CCUITextureSplice[] _myVerticalSplices;
	
	@CCXMLProperty(name="horizontal_splices", optional = true)
	private CCUITextureSplice[] _myHorizontalSplices;
	
//	private double _myLeftOver = 0;
	
	private double[][] _myXpositions;
	private double[][] _myYpositions;
	
	public CCUITexture() {
		
	}
	
	public void setup(CCUI theUI) {
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath(_myTextureFile)));
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
