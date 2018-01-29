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

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCGradientEllipse extends CCEllipse{
	
	private double _myInnerRadius;
	private CCColor _myGradientColor;

	public CCGradientEllipse(CCVector2 theCenter, CCColor theColor, CCColor theGradientColor, double theInnerRadius, double theRadius) {
		super(theCenter, theColor, theRadius);
		_myShapeMode = CCShapeMode.CENTER;
		_myGradientColor = theGradientColor;
		_myInnerRadius = theInnerRadius;
	}

	public CCGradientEllipse(double theCenterX, double theCenterY, CCColor theColor, CCColor theGradientColor, double theInnerRadius, double theRadius) {
		this(new CCVector2(theCenterX, theCenterY), theColor, theGradientColor,theInnerRadius,theRadius);
	}
	
	@Override
	public void draw(CCGraphics g) {
		if(!_myIsVisible)return;
		if(_myScale == 0)return;
		

	    int accuracy = (int)(4+Math.sqrt((_myRadius +_myRadius) * _myScale)*3);
	    
	    double inc = CCMath.TWO_PI / accuracy;

	    double val = 0;

		g.color(_myColor);
	    g.beginShape(CCDrawMode.TRIANGLE_FAN);
		g.normal(0, 0, 1);
		g.vertex(_myCenter);
		for (int i = 0; i < accuracy; i++) {
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myInnerRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myInnerRadius * _myScale
			);
			val += inc;
		}
		// back to the beginning
		g.vertex(_myCenter.x + _myInnerRadius * _myScale, _myCenter.y);
		g.endShape();
		
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		g.normal(0, 0, 1);
		for (int i = 0; i < accuracy; i++) {
			g.color(_myColor);
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myInnerRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myInnerRadius * _myScale
			);
			g.color(_myGradientColor);
			g.vertex(
				_myCenter.x + CCMath.cos(val) * _myRadius * _myScale, 
				_myCenter.y + CCMath.sin(val) * _myRadius * _myScale
			);
			val += inc;
		}
		// back to the beginning
		g.color(_myColor);
		g.vertex(_myCenter.x + _myInnerRadius * _myScale, _myCenter.y);
		g.color(_myGradientColor);
		g.vertex(_myCenter.x + _myRadius * _myScale, _myCenter.y);
		g.endShape();
	}
}
