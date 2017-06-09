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
public class CCSlopedTriSignal extends CCSignal{
	
	
	
	public CCSlopedTriSignal() {
		super();
	}

	public CCSlopedTriSignal(CCSignalSettings theSettings) {
		super(theSettings);
	}

	private double triValue(double theInput){
		theInput += 0.5;
		if(theInput < 0){
			theInput = -theInput + 0.25f;
		}
		theInput = (theInput * 4) % 4;
		double myResult = 0;
		if(theInput < 1){
			myResult = 0;
		}else if(theInput < 2){
			myResult = (theInput - 1);
		}else if(theInput < 3){
			myResult = 1;
		}else{
			myResult = 1 - (theInput - 3);
		}
		
		if(!_mySettings.isNormed()){
			myResult = myResult * 2 - 1;
		}
		return myResult;
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
