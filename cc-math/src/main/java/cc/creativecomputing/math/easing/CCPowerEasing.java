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

public class CCPowerEasing extends CCEasing{
	
	private final double _myPow;
	
	public CCPowerEasing(final double thePow){
		_myPow = thePow;
	}
	
	public double easeIn (final double theBlend) {
		return CCMath.pow(theBlend, _myPow);
	}
	
	public double easeOut (final double theBlend) {
		return 1 - CCMath.pow(1 - theBlend, _myPow);
	}
	
	public double easeInOut (final double theBlend) {
		if (theBlend < 0.5f) return CCMath.pow(theBlend * 2, _myPow) / 2;
		return 1 - CCMath.pow((1 - theBlend) * 2, _myPow) / 2;
	}

	public CCPowerEasing clone() {
		return new CCPowerEasing(_myPow);
	}
}
