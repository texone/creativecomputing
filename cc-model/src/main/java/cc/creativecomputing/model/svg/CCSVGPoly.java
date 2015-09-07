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

import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCSVGPoly extends CCSVGElement{
	
	private CCLinearSpline _mySpline;

	public CCSVGPoly(CCSVGGroup theParent, boolean theIsClosed) {
		super(theParent);
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
}
