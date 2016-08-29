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
package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCTriSignal extends CCSignal{
	@CCProperty(name = "ratio", min = 0, max = 1)
	private double _cRatio = 0.5;
	
	private double triValue(double theInput){
		theInput %= 1;
		if(theInput < 0){
			theInput = 1+theInput;
		}
		
		if(theInput <= _cRatio){
			return theInput / _cRatio;
		}
		return 1 - (theInput - _cRatio) / (1 - _cRatio);
	}

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{CCMath.average(triValue(theX),triValue(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{CCMath.average(triValue(theX),triValue(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{triValue(theX)};
	}

}
