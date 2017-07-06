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
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGLine extends CCSVGElement{
	
	private CCVector2 _myA;
	private CCVector2 _myB;

	public CCSVGLine(CCSVGGroup theParent) {
		super(theParent, CCShapeKind.LINE, CCShapeFamily.PRIMITIVE);
		_myA = new CCVector2();
		_myB = new CCVector2();
	}
	
	public CCVector2 a(){
		return _myA;
	}
	
	public CCVector2 b(){
		return _myB;
	}
	
	@Override
	public List<CCLinearSpline> contours() {
		CCLinearSpline myContour = new CCLinearSpline(false);
		myContour.beginEditSpline();
		myContour.addPoint(new CCVector3(_myA.x, _myA.y));
		myContour.addPoint(new CCVector3(_myB.x, _myB.y));
		myContour.endEditSpline();
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(myContour);
		return myResult;
	}
	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {
		g.line(_myA, _myB);
	}
	
	@Override
	public CCDataElement write() {
		CCDataElement myResult = super.write();
		myResult.addAttribute("x1", _myA.x);
		myResult.addAttribute("y1", _myA.x);
		myResult.addAttribute("x2", _myB.y);
		myResult.addAttribute("y2", _myB.y);
		return myResult;
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		super.read(theSVG);
		
		_myA.set(
			CCSVGIO.getDoubleWithUnit(theSVG, "x1"),
			CCSVGIO.getDoubleWithUnit(theSVG, "y1")
		);
		_myB.set(
			CCSVGIO.getDoubleWithUnit(theSVG, "x2"),
			CCSVGIO.getDoubleWithUnit(theSVG, "y2")
		);
	}
	
	@Override
	public String svgTag() {
		return "line";
	}
}
