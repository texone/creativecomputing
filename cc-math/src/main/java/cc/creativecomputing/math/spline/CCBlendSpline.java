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
package cc.creativecomputing.math.spline;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCBlendSpline extends CCSpline{

	private double _myBlend = 0;
	
	private CCSpline _mySpline1;
	private CCSpline _mySpline2;
	
	public CCBlendSpline(CCSpline theSpline1, CCSpline theSpline2) {
		super(CCSplineType.BLEND, false);
		
		_mySpline1 = theSpline1;
		_mySpline2 = theSpline2;
	}

	public void blend(double theBlend){
		_myBlend = theBlend;
	}
	
	@Override
	protected void computeTotalLengthImpl() {
		
	}
	
	@Override
	public double totalLength() {
		return CCMath.blend(_mySpline1.totalLength(), _mySpline2.totalLength(), _myBlend);
	}
	
	@Override
	public int numberOfSegments() {
		return CCMath.max(_mySpline1.numberOfSegments(), _mySpline2.numberOfSegments());
	}

	@Override
	public CCVector3 interpolate(double theBlend, int theControlPointIndex) {
		return null;
	}
	
	@Override
	public CCVector3 interpolate(double theBlend) {
		return CCVector3.blend(
			_mySpline1.interpolate(theBlend), 
			_mySpline2.interpolate(theBlend),
			_myBlend
		);
	}
	
	public CCVector3 interpolate(double theBlendSpline, double theBlendPoint) {
		CCVector3 myVector0 = _mySpline1.interpolate(theBlendPoint);
		CCVector3 myVector1 = _mySpline2.interpolate(theBlendPoint);
		if(myVector0 == null || myVector1 == null){
			return null;
		}
		return CCVector3.blend(
			myVector0, 
			myVector1,
			theBlendSpline
		);
		
	}
}
