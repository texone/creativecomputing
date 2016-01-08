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
package cc.creativecomputing.math;


public class CCQuad2f {
	private CCVector2 _myLeftTop;
	private CCVector2 _myLeftBottom;
	private CCVector2 _myRightBottom;
	private CCVector2 _myRightTop;

	public CCQuad2f(final CCVector2 theLeftUpper, final CCVector2 theLeftBottom, final CCVector2 theRightBottom, final CCVector2 theRightUpper) {
		_myLeftTop = theLeftUpper;
		_myLeftBottom = theLeftBottom;
		_myRightBottom = theRightBottom;
		_myRightTop = theRightUpper;
	}
	
	public CCQuad2f(){
		this(new CCVector2(), new CCVector2(), new CCVector2(), new CCVector2());
	}

	public CCVector2 gridVector(final double theX, final double theY, CCVector2 theStore) {
		double topX = _myLeftTop.x * (1 - theX) + _myRightTop.x * theX;
		double topY = _myLeftTop.y * (1 - theX) + _myRightTop.y * theX;

		double bottomX = _myLeftBottom.x * (1 - theX) + _myRightBottom.x * theX;
		double bottomY = _myLeftBottom.y * (1 - theX) + _myRightBottom.y * theX;
		
		theStore.x = topX * (1 - theY) + bottomX * theY;
		theStore.y = topY * (1 - theY) + bottomY * theY;
		
		return theStore;
	}
	
	public CCVector2 gridVector(final double theX, final double theY){
		return gridVector(theX, theY, new CCVector2());
	}
	
	public CCVector2 leftTop(){
		return _myLeftTop;
	}
	
	public void leftTop(CCVector2 theLeftTop){
		_myLeftTop = theLeftTop;
	}

	public CCVector2 leftBottom() {
		return _myLeftBottom;
	}

	public void leftBottom(CCVector2 theLeftBottom) {
		_myLeftBottom = theLeftBottom;
	}

	public CCVector2 rightBottom() {
		return _myRightBottom;
	}

	public void rightBottom(CCVector2 theRightBottom) {
		_myRightBottom = theRightBottom;
	}

	public CCVector2 rightTop() {
		return _myRightTop;
	}

	public void rightTop(CCVector2 theRightUpper) {
		_myRightTop = theRightUpper;
	}
}
