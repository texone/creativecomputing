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

public class CCCircularEasing extends CCEasing{
	
	public double easeIn(final double theBlend) {
		return 1 - CCMath.sqrt(1 - theBlend * theBlend);
	}
	
	public double easeOut(double theBlend) {
		theBlend = 1 - theBlend;
		return CCMath.sqrt(1 - theBlend * theBlend);
	}
	
	public double easeInOut(double theBlend) {
		theBlend *= 2;
		if (theBlend < 1) return -0.5 * (CCMath.sqrt(1 - theBlend * theBlend) - 1);
		return 0.5f * (CCMath.sqrt(1 - CCMath.sq(2  - theBlend)) + 1);
	}

	public CCCircularEasing clone() {
		return new CCCircularEasing();
	}
}
