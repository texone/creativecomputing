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

import cc.creativecomputing.graphics.CCGraphics;

public class CCVectorChar extends CCChar{

	protected double _mySize;

	protected int _myVertexCounter = 0;
	
	protected double[] _myVertices = new double[50];
	
	/**
	 * @param theChar
	 */
	CCVectorChar(char theChar, final int theGlyphCode, final double theWidth, final double theHeight, final double theSize) {
		super(theChar, theGlyphCode, theWidth, theHeight);
		_mySize = theSize;
	}
	
	public void addVertex(final double theX, final double theY){
		double[] myNewVertices = new double[_myVertices.length + 50];
		if(_myVertexCounter >= _myVertices.length/2){
			System.arraycopy(_myVertices, 0, myNewVertices, 0, _myVertices.length);
			_myVertices = myNewVertices;
		}
		_myVertices[_myVertexCounter * 2] = theX;
		_myVertices[_myVertexCounter * 2 + 1] = theY;
		_myVertexCounter++;
	}
	
	public int numberOfVertices(){
		return _myVertexCounter;
	}
	
	public double[] vertices(){
		return _myVertices;
	}
	
	public void end(){
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCChar#draw(cc.creativecomputing.graphics.CCGraphics)
	 */
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theSize) {
		final double myScale = theSize / _mySize;
		
		for(int i = 0; i < _myVertexCounter;i++){
			g.vertex(
				theX + _myVertices[i * 2] * myScale, 
				theY - _myVertices[i * 2 + 1] * myScale,
				theZ
			);
		}
		
		return _myWidth * theSize;
	}
}
