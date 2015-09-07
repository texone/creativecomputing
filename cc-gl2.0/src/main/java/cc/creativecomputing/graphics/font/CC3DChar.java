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

import java.util.ArrayList;
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
public class CC3DChar extends CCVectorChar{

	private final List<List<CCVector2>>_myContour = new ArrayList<List<CCVector2>>();
	private List<CCVector2>_myPath;
	
	private double _myDepth;
	
	private CCDisplayList _myDisplayList;
	
	/**
	 * @param theChar
	 */
	CC3DChar(char theChar, final int theGlyphCode, final double theWidth, final double theHeight, final double theSize, final double theDepth) {
		super(theChar, theGlyphCode, theWidth, theHeight,theSize);
		_myDepth = theDepth;
	}
	
	public void setDisplayList(CCGraphics g) {
		_myDisplayList = new CCDisplayList();
		_myDisplayList.beginRecord();
		
		g.beginShape(CCDrawMode.TRIANGLES);
		
		for(int i = 0; i < _myVertexCounter;i++){
			g.normal(0, 0, -1);
			g.vertex(
				_myVertices[i * 2], 
				-_myVertices[i * 2 + 1],
				-0.5f
			);
		}
		
		for(int i = _myVertexCounter - 1; i >= 0;i--){
			g.normal(0, 0, 1);
			g.vertex(
				_myVertices[i * 2], 
				-_myVertices[i * 2 + 1],
				0.5f
			);
		}
		
		for(List<CCVector2> myPath:_myContour){
			for(int i = 0; i < myPath.size();i++) {
				CCVector2 myVertex1 = myPath.get(i);
				CCVector2 myVertex2 = myPath.get((i+1) % myPath.size());
				
				CCVector3 _myNormal = CCVector3.normal(
					new CCVector3(myVertex1.x, myVertex1.y, -0.5f),
					new CCVector3(myVertex1.x, myVertex1.y, +0.5f),
					new CCVector3(myVertex2.x, myVertex2.y, +0.5f)
				);
				g.normal(_myNormal);
				g.vertex(
					myVertex1.x, 
					-myVertex1.y,
					-0.5f
				);
				g.vertex(
					myVertex1.x, 
					-myVertex1.y,
					0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					0.5f
				);
				
				g.vertex(
					0 + myVertex1.x, 
					-myVertex1.y,
					-0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					-0.5f
				);
			}
			
		}
		
		g.endShape();
		
		_myDisplayList.endRecord();
	}
	
	public void beginPath(){
		_myPath = new ArrayList<CCVector2>();
	}
	
	public void addOutlineVertex(final double theX, final double theY){
		_myPath.add(new CCVector2(theX, theY));
	}
	
	public void endPath(){
		_myContour.add(_myPath);
	}
	
	public List<List<CCVector2>> contour(){
		return _myContour;
	}
	
	public void depth(final double theDepth) {
		_myDepth = theDepth;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCVectorChar#draw(cc.creativecomputing.graphics.CCGraphics, double, double, double, double)
	 */
	@Override
	public double draw(CCGraphics g, double theX, double theY, double theZ, double theSize) {
		if(_myDisplayList == null)setDisplayList(g);
		final double myScale = theSize / _mySize;
		
		g.pushMatrix();
		g.translate(theX, theY, theZ);
		g.scale(myScale,myScale,_myDepth);
		_myDisplayList.draw();
		g.popMatrix();

//		for(int i = 0; i < _myVertexCounter;i++){
//			g.normal(0, 0, -1);
//			g.vertex(
//				theX + _myVertices[i * 2] * myScale, 
//				theY - _myVertices[i * 2 + 1] * myScale,
//				theZ - _myDepth / 2
//			);
//		}
//		
//		for(int i = _myVertexCounter - 1; i >= 0;i--){
//			g.normal(0, 0, 1);
//			g.vertex(
//				theX + _myVertices[i * 2] * myScale, 
//				theY - _myVertices[i * 2 + 1] * myScale,
//				theZ + _myDepth / 2
//			);
//		}
//		
//		for(List<CCVector2> myPath:_myContour){
//			for(int i = 0; i < myPath.size();i++) {
//				CCVector2 myVertex1 = myPath.get(i);
//				CCVector2 myVertex2 = myPath.get((i+1) % myPath.size());
//				
//				CCVector3 _myNormal = CCVecMath.normal(
//					new CCVector3(myVertex1.x(), myVertex1.y, -_myDepth / 2),
//					new CCVector3(myVertex1.x(), myVertex1.y, +_myDepth / 2),
//					new CCVector3(myVertex2.x(), myVertex2.y, +_myDepth / 2)
//				);
//				g.normal(_myNormal);
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ - _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ + _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ + _myDepth / 2
//				);
//				
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ - _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ + _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ - _myDepth / 2
//				);
//			}
//		}
		return _myWidth * theSize;
	}
	
	
}
