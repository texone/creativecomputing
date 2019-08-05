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
package cc.creativecomputing.model.svg;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGEllipse extends CCSVGElement{
	
	private CCVector2 _myCenter;
	private CCVector2 _myRadius;
	
	private boolean _myIsCircle;

	public CCSVGEllipse(CCSVGGroup theParent, boolean theIsCircle) {
		super(theParent, CCShapeKind.ELLIPSE, CCShapeFamily.PRIMITIVE);
		_myIsCircle = theIsCircle;
		_myCenter = new CCVector2();
		_myRadius = new CCVector2();
	}
	
	public CCVector2 center(){
		return _myCenter;
	}
	
	public CCVector2 radius(){
		return _myRadius;
	}
	
	@Override
	public List<CCLinearSpline> contours(double theFlatness) {
		CCLinearSpline myContour = new CCLinearSpline(true);
		myContour.beginEditSpline();
		for(int i = 0; i <= 360;i++){
			double myX = CCMath.cos(CCMath.radians(i)) * _myRadius.x + _myCenter.x;
			double myY = CCMath.sin(CCMath.radians(i)) * _myRadius.y + _myCenter.y;
			myContour.addPoint(new CCVector3(myX, myY,0));
		}
		myContour.endEditSpline();
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(myContour);
		myContour.endEditSpline();
		return myResult;
	}
	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {
		CCShapeMode myEllipseMode = g.ellipseMode();
		g.ellipse(_myCenter, _myRadius.x, _myRadius.y, theFill);
		g.ellipseMode(myEllipseMode);
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		super.read(theSVG);
		
		center().set(
			CCSVGIO.getDoubleWithUnit(theSVG, "cx"),
			CCSVGIO.getDoubleWithUnit(theSVG, "cy")
		);

		double rx, ry;
		if (_myIsCircle) {
			rx = ry = CCSVGIO.getDoubleWithUnit(theSVG, "r");
		} else {
			rx = CCSVGIO.getDoubleWithUnit(theSVG, "rx");
			ry = CCSVGIO.getDoubleWithUnit(theSVG, "ry");
		}
		radius().set(rx, ry);
	}
	
	@Override
	public String svgTag() {
		if(radius().x == radius().y)return "circle";
		return "ellipse";
	}
}
