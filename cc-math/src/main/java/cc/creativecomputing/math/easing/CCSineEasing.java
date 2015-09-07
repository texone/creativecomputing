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

public class CCSineEasing extends CCEasing {

	public double easeIn(final double theBlend) {
		return 1 - CCMath.cos(theBlend * CCMath.HALF_PI);
	}

	public double easeOut(final double theBlend) {
		return CCMath.sin(theBlend * CCMath.HALF_PI);
	}

	public double easeInOut(final double theBlend) {
		return (CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2;
	}

	public CCSineEasing clone() {
		return new CCSineEasing();
	}
}
