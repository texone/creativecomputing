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
package cc.creativecomputing.math.easing;

import cc.creativecomputing.math.CCMath;

/**
 * @author info
 *
 */
public class CCPendularEasing extends CCEasing{
	
	private double _myFrequency = 3;
	private double _myPendularAmount = 1;
	private double _myFrequencyIncrease = 0;
	
	public CCPendularEasing(final double theFrequency, final double thePendularAmount) {
		_myFrequency = theFrequency;
		_myPendularAmount = thePendularAmount;
	}
	
	public CCPendularEasing(final double theFrequency) {
		_myFrequency = theFrequency;
	}
	
	public CCPendularEasing() {
		this(3);
	}
	
	public void frequency(final double theFrequency) {
		_myFrequency = theFrequency;
	}
	
	public void frequencyIncrease(final double theFrequencyIncrease) {
		_myFrequencyIncrease = theFrequencyIncrease;
	}
	
	public void pendularAmount(final double theAmount) {
		_myPendularAmount = theAmount;
	}

	public double easeIn(final double theBlend) {
		return 1 - CCMath.cos(theBlend * CCMath.HALF_PI);
	}

	public double easeOut(final double theBlend) {
		return 
			(CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2 + 
			(CCMath.cos(CCMath.TWO_PI * theBlend * CCMath.blend(1, _myFrequencyIncrease,theBlend) * _myFrequency + CCMath.PI) + 1) * (1 - theBlend);
	}

	public double easeInOut(final double theBlend) {
		return 
			(CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2 + 
			(CCMath.cos(CCMath.TWO_PI * theBlend * CCMath.blend(1, theBlend, _myFrequencyIncrease) * _myFrequency + CCMath.PI) + 1) * (1 - theBlend) * _myPendularAmount;
	}

	public CCPendularEasing clone() {
		return new CCPendularEasing(_myFrequency);
	}
}
