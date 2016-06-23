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

public abstract class CCEasing {
	
	public static enum CCEaseFormular {
		LINEAR(new CCLinearEasing()),
		EXPONENTIAL(new CCExponentialEasing()),
		PENDUALR(new CCPendularEasing()),
		CIRCULAR(new CCCircularEasing()),
		SINE(new CCSineEasing()),
		QUADRATIC(new CCQuadraticEasing()),
		CUBIC(new CCPowerEasing(5));
		
		private CCEasing _myEasing;
		
		private CCEaseFormular(CCEasing theEasing){
			_myEasing = theEasing;
		}
		
		public CCEasing easing(){
			return _myEasing;
		}
	}
	
	public enum CCEaseMode {
		IN, OUT, IN_OUT
	}
	
	public abstract double easeIn(final double theBlend);
	
	public abstract double easeOut(final double theBlend);
	
	public abstract double easeInOut(final double theBlend);
	
	public double easeIn(final double theStart, final double theStop, final double theBlend){
		return CCMath.blend(theStart, theStop, easeIn(theBlend));
	}
	
	public double easeOut(final double theStart, final double theStop, final double theBlend){
		return CCMath.blend(theStart, theStop, easeOut(theBlend));
	}
	
	public double easeInOut(final double theStart, final double theStop, final double theBlend){
		return CCMath.blend(theStart, theStop, easeInOut(theBlend));
	}
	
	public double easeIn(final double theStart, final double theStop, final double theTime, final double theDuration){
		return CCMath.blend(theStart, theStop, easeIn(theTime/theDuration));
	}
	
	public double easeOut(final double theStart, final double theStop, final double theTime, final double theDuration){
		return CCMath.blend(theStart, theStop, easeOut(theTime/theDuration));
	}
	
	public double easeInOut(final double theStart, final double theStop, final double theTime, final double theDuration){
		return CCMath.blend(theStart, theStop, easeInOut(theTime/theDuration));
	}
	
	public double ease(final CCEaseMode theMode, final double theBlend){
		switch(theMode){
		case IN:
			return easeIn(theBlend);
		case OUT:
			return easeOut(theBlend);
        default:
		}
		return easeInOut(theBlend);
	}
	
	public double ease(final CCEaseMode theMode, final double theStart, final double theStop, final double theBlend){
		switch(theMode){
		case IN:
			return easeIn(theStart, theStop, theBlend);
		case OUT:
			return easeOut(theStart, theStop, theBlend);
        default:
		}
		return easeInOut(theStart, theStop, theBlend);
	}
	
	public double ease(final CCEaseMode theMode, final double theStart, final double theStop, final double theTime, final double theDuration){
		switch(theMode){
		case IN:
			return easeIn(theStart, theStop, theTime, theDuration);
		case OUT:
			return easeOut(theStart, theStop, theTime, theDuration);
        default:
		}
		return easeInOut(theStart, theStop, theTime, theDuration);
	}
}
