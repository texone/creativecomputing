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

public class CCLinearEasing extends CCEasing{
	
//	public static float easeNone (float t,float b , float c, float d) {
//		return c*t/d + b;
//	}
	
	public double easeIn (final double theBlend) {
		return theBlend;
	}
	
	public double easeOut (final double theBlend) {
		return theBlend;
	}
	
	public double easeInOut (final double theBlend) {
		return theBlend;
	}
	
	public CCLinearEasing clone() {
		return new CCLinearEasing();
	}
}
