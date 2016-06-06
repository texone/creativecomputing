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

import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGPoly extends CCSVGElement{
	
	private CCLinearSpline _mySpline;

	public CCSVGPoly(CCSVGGroup theParent, boolean theIsClosed) {
		super(theParent, CCShapeKind.POLYGON, CCShapeFamily.PATH);
		_mySpline = new CCLinearSpline(theIsClosed);
	}
	
	public CCLinearSpline spline(){
		return _mySpline;
	}
	
	@Override
	public List<CCLinearSpline> contours() {
		List<CCLinearSpline> myResult = new ArrayList<>();
		myResult.add(_mySpline);
		return myResult;
	}
	
	@Override
	public void drawImplementation(CCGraphics g, boolean theFill) {
		draw(g,_mySpline, theFill);
	}
	
//	@Override
//	public CCXMLElement write() {
//		CCXMLElement myResult = super.write();
//	}
	
	@Override
	public void read(CCXMLElement theSVG) {
		super.read(theSVG);

		String pointsAttr = theSVG.attribute("points");
		if (pointsAttr != null) {
			String[] pointsBuffer = CCStringUtil.splitTokens(pointsAttr);
			_mySpline.beginEditSpline();
			for (int i = 0; i < pointsBuffer.length; i++) {
				String pb[] = CCStringUtil.split(pointsBuffer[i], ',');
				_mySpline.addPoint(new CCVector3(
					Double.valueOf(pb[0]),
					Double.valueOf(pb[1])
				));
			}
			_mySpline.endEditSpline();
		}
	}
	
	@Override
	public String svgTag() {
		if(_mySpline.isClosed())return "polygon";
		return "polyline";
	}
}
