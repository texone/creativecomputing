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
package cc.creativecomputing.graphics.shape;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

public class CCEllipse extends CCAbstractShape{
	
	protected CCVector2 _myCenter;
	protected double _myRadius;
	
	public CCEllipse(final CCVector2 theCenter, final CCColor theColor, final double theRadius){
		_myCenter = theCenter;
		_myRadius = theRadius;
		_myColor = theColor;
	}
	
	public CCEllipse(final double theCenterX, final double theCenterY, final CCColor theColor, final double theRadius){
		_myCenter = new CCVector2(theCenterX, theCenterY);
		_myColor = theColor;
		_myRadius = theRadius;
	}

	@Override
	public void draw(CCGraphics g) {
		g.color(_myColor);
		g.pushMatrix();
		g.translate(_myCenter);
		CCShapeMode myShapeMode = g.ellipseMode();
		g.ellipseMode(CCShapeMode.CENTER);
		g.ellipse(_myCenter, _myRadius);
		g.ellipseMode(myShapeMode);
		g.popMatrix();
	}

	@Override
	public void translate(double theX, double theY) {
		_myCenter.add(theX, theY);
	}

	@Override
	public void position(double theX, double theY) {
		_myCenter.set(theX, theY);
	}

}
