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
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGRectangle extends CCSVGElement{
	
	private CCVector2 _myCenter;
	
	private CCVector2 _myDimension;
	
	public CCSVGRectangle(CCSVGGroup theParent) {
		super(theParent, CCShapeKind.RECT, CCShapeFamily.PRIMITIVE);
		_myCenter = new CCVector2();
		_myDimension = new CCVector2();
	}
	
	public CCVector2 center(){
		return _myCenter;
	}
	
	public CCVector2 dimension(){
		return _myDimension;
	}
	
	@Override
	public List<CCLinearSpline> contours(double theFlatness) {
		CCLinearSpline myContour = new CCLinearSpline(true);
		myContour.beginEditSpline();
		myContour.addPoint(new CCVector3(_myCenter.x, _myCenter.y));
		myContour.addPoint(new CCVector3(_myCenter.x + _myDimension.x, _myCenter.y));
		myContour.addPoint(new CCVector3(_myCenter.x + _myDimension.x, _myCenter.y + _myDimension.y));
		myContour.addPoint(new CCVector3(_myCenter.x, _myCenter.y + _myDimension.y));
		myContour.endEditSpline();
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(myContour);
		return myResult;
	}
	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {
		CCShapeMode myRectMode = g.rectMode();
		g.rect(_myCenter, _myDimension, theFill);
		g.rectMode(myRectMode);
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		super.read(theSVG);
		
		center().set(
			CCSVGIO.getDoubleWithUnit(theSVG, "x"),
			CCSVGIO.getDoubleWithUnit(theSVG, "y")
		);
		dimension().set(
			CCSVGIO.getDoubleWithUnit(theSVG, "width"),
			CCSVGIO.getDoubleWithUnit(theSVG, "height")
		);
	}
	
	@Override
	public String svgTag() {
		return "rect";
	}
}
