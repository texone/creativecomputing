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

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.io.xml.CCDataElement;


public class CCSVGDocument extends CCSVGGroup{
	/**
	 * The width of the CCSVGDocument document.
	 */
	public double width;
	
	/**
	 * The height of the CCSVGDocument document.
	 */
	public double height;
	
	public CCSVGDocument(){
		super(null, CCShapeKind.GROUP);
	}
	
	@Override
	public CCDataElement write() {
		CCDataElement myResult = super.write();
		myResult.addAttribute("width", width);
		myResult.addAttribute("height", height);
		return myResult;
	}
	
	@Override
	public void read(CCDataElement theSVG) {
		// not proper parsing of the viewBox, but will cover us for cases where
		// the width and height of the object is not specified
		String viewBoxStr = theSVG.attribute("viewBox");
		if (viewBoxStr != null) {
			String[] viewBox = CCStringUtil.splitTokens(viewBoxStr);
			width = Double.parseDouble(viewBox[2]);
			height = Double.parseDouble(viewBox[3]);
		}

		// TODO if viewbox is not same as width/height, then use it to scale
		// the original objects. for now, viewbox only used when width/height
		// are empty values (which by the spec means w/h of "100%"
		String unitWidth = theSVG.attribute("width");
		String unitHeight = theSVG.attribute("height");
		if (unitWidth != null) {
			width = CCSVGIO.parseUnitSize(unitWidth);
			height = CCSVGIO.parseUnitSize(unitHeight);
		} else {
			if ((width == 0) || (height == 0)) {
				// throw new RuntimeException("width/height not specified");
				CCLog.warn("The width and/or height is not readable in the <svg> tag of this file.");
				// For the spec, the default is 100% and 100%. For purposes
				// here, insert a dummy value because this is prolly just a
				// font or something for which the w/h doesn't matter.
				width = 1;
				height = 1;
			}
		}
		
		super.read(theSVG);
		
	}
	
	@Override
	public String svgTag() {
		return "svg";
	}
}
