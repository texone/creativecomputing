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
package cc.creativecomputing.graphics;

import cc.creativecomputing.math.CCVector2;

public class CCViewport {

	private int _myX = 0;
	private int _myY = 0;

	private int _myWidth = 640;
	private int _myHeight = 480;

	public CCViewport() {
	}

	public CCViewport(final CCViewport theViewport) {
		set(theViewport);
	}

	public CCViewport(final int theX, final int theY, final int theWidth, final int theHeight) {
		set(theX, theY, theWidth, theHeight);
	}

	public void set(final CCViewport theViewport) {
		_myX = theViewport._myX;
		_myY = theViewport._myY;
		_myWidth = theViewport._myWidth;
		_myHeight = theViewport._myHeight;
	}

	public void set(final int theX, final int theY, final int theWidth, final int theHeight) {
		_myX = theX;
		_myY = theY;
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	public void position(final int theX, final int theY){
		_myX = theX;
		_myY = theY;
	}
	
	public CCVector2 position(){
		return new CCVector2(_myX, _myY);
	}	
	
	public void dimension(final int theWidth, final int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}

	public boolean pointInside(CCVector2 thePoint) {	
		return thePoint.x > _myX && thePoint.x < _myX + _myWidth && thePoint.y > _myY && thePoint.y < _myY + _myHeight;
	}
		
	public int x(){
		return _myX;
	}
	
	public int y(){
		return _myY;
	}
	
	public int width(){
		return _myWidth;
	}
	
	public int height(){
		return _myHeight;
	}
	
	public float aspectRatio(){
		return (float)_myWidth/_myHeight;
	}

	public void draw(CCGraphics g) {
		g.gl.glViewport(_myX, _myY, _myWidth, _myHeight);
	}
	
	@Override
	public String toString() {
		return "[x:" + _myX + ", y:" + _myY + ", w:" + _myWidth + ", h:" + _myHeight + "]";
	}
}
