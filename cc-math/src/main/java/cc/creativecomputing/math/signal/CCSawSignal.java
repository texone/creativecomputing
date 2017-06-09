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
public class CCSawSignal extends CCSignal{
	
	
	
	public CCSawSignal() {
		super();
	}

	public CCSawSignal(CCSignalSettings theSettings) {
		super(theSettings);
	}

	private double saw(double theValue){
		double myResult = 0;
		if(theValue < 0){
			myResult = -theValue % 1;
		}else{
			myResult = 1- theValue % 1;
		}
		if(!_mySettings.isNormed()){
			myResult = myResult * 2 - 1;
		}
		return myResult;
	}

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{CCMath.average(saw(theX), saw(theY), saw(theZ))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{CCMath.average(saw(theX), saw(theY))};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{saw(theX)};
	}

}
