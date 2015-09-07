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

import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCSquareSignal extends CCSignal{
	
	private double square(double theValue){
		if(theValue < 0) theValue = -theValue + 0.5f;
		return 1 - (int)(theValue * 2) % 2;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#noiseImpl(double, double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{CCMath.average(square(theX), square(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{CCMath.average(square(theX), square(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{square(theX)};
	}

}
