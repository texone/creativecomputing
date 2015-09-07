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
 
public class CCExponentialEasing extends CCEasing{
	
	public double easeIn(final double theBlend) {
		return (theBlend==0) ? 0 : theBlend * CCMath.pow(2, 10 * (theBlend - 1));
	}
	
	public double easeOut(final double theBlend) {
		return (theBlend == 1) ? 1 : (-CCMath.pow(2, -10 * theBlend) + 1);	
	}
	
	public double  easeInOut(double theBlend) {
		if (theBlend == 0) return 0;
		if (theBlend == 1) return 1;
		if ((theBlend) < 0.5) return 0.5f * CCMath.pow(2, 10 * (theBlend - 1));
		return 0.5f * (-CCMath.pow(2, -10 * (theBlend-1)) + 2);
	}

	public CCExponentialEasing clone() {
		return new CCExponentialEasing();
	}
}
