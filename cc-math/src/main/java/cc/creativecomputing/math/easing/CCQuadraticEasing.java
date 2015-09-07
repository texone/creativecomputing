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

public class CCQuadraticEasing extends CCEasing{
	
	public double easeIn(final double theBlend) {
		return CCMath.sq(theBlend);
	}
	
	public double easeOut(final double theBlend) {
		return -(theBlend) * (theBlend - 2);
	}
	
	public double  easeInOut(final double theBlend) {
		if (theBlend < 0.5) return 2 * theBlend * theBlend;
		return 1 - 2 * CCMath.sq(1 - theBlend);
	}
	
	public CCQuadraticEasing clone() {
		return new CCQuadraticEasing();
	}
}
