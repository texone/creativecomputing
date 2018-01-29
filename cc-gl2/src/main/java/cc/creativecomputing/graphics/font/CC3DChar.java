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

import java.util.List;

import cc.creativecomputing.graphics.CCDisplayList;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * @author info
 *
 */
public class CC3DChar extends CCChar{
	
	private double _myDepth;
	
	private CCDisplayList _myDisplayList;
	
	private CCVectorChar _myVectorChar;
	private CCOutlineChar _myOutlineChar;
	
	/**
	 * @param theChar
	 */
	CC3DChar(CCVectorChar theVectorChar, CCOutlineChar theOutlineChar, double theDepth) {
		super(theVectorChar);
		_myVectorChar = theVectorChar;
		_myOutlineChar = theOutlineChar;
		_myDepth = theDepth;
	}
	
	public void setDisplayList(CCGraphics g) {
		_myDisplayList = new CCDisplayList();
		_myDisplayList.beginRecord();
		
		g.beginShape(CCDrawMode.TRIANGLES);
		
		for(int i = 0; i < _myVectorChar._myVertexCounter;i++){
			g.normal(0, 0, -1);
			g.vertex(_myVectorChar._myVertices[i * 2], _myVectorChar._myVertices[i * 2 + 1], -0.5f);
		}
		
		for(int i = _myVectorChar._myVertexCounter - 1; i >= 0;i--){
			g.normal(0, 0, 1);
			g.vertex(_myVectorChar._myVertices[i * 2], _myVectorChar._myVertices[i * 2 + 1],0.5f);
		}
		
		for(List<CCVector2> myPath:_myOutlineChar.contour()){
			for(int i = 0; i < myPath.size();i++) {
				CCVector2 myVertex1 = myPath.get(i);
				CCVector2 myVertex2 = myPath.get((i+1) % myPath.size());
				
				CCVector3 _myNormal = CCVector3.normal(
					new CCVector3(myVertex1.x, myVertex1.y, -0.5f),
					new CCVector3(myVertex1.x, myVertex1.y, +0.5f),
					new CCVector3(myVertex2.x, myVertex2.y, +0.5f)
				);
				g.normal(_myNormal);
				g.vertex(myVertex1.x, myVertex1.y, -0.5f);
				g.vertex(myVertex1.x, myVertex1.y, 0.5f);
				g.vertex(myVertex2.x, myVertex2.y, 0.5f);
				
				g.vertex(myVertex1.x, myVertex1.y, -0.5f);
				g.vertex(myVertex2.x, myVertex2.y, 0.5f);
				g.vertex(myVertex2.x, myVertex2.y, -0.5f);
			}
		}
		
		g.endShape();
		
		_myDisplayList.endRecord();
	}
	
	public List<List<CCVector2>> contour(){
		return _myOutlineChar.contour();
	}
	
	public void depth(final double theDepth) {
		_myDepth = theDepth;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCVectorChar#draw(cc.creativecomputing.graphics.CCGraphics, double, double, double, double)
	 */
	@Override
	public double drawVertices(CCGraphics g, double theX, double theY, double theZ, double theScale) {
		if(_myDisplayList == null)setDisplayList(g);
		
		g.pushMatrix();
		g.translate(theX, theY, theZ);
		g.scale(theScale,theScale,_myDepth);
		_myDisplayList.draw();
		g.popMatrix();

		return _myWidth * theScale;
	}
	
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theScale) {
		return drawVertices(g, theX, theY, theZ, theScale);
	}
}
