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
public class CCRandomPulseSignal extends CCSimplexNoise{
	
	public CCRandomPulseSignal() {
		super();
	}
	@CCProperty(name = "ratio", min = 0, max = 1)
	private double _cRatio = 0.5;

	public CCRandomPulseSignal(CCSignalSettings theSettings) {
		super(theSettings);
	}
	
	private double[] step(double[] theValues) {
		double[] myResult = new double[theValues.length];
		
		for(int i = 0; i < theValues.length;i++) {
			myResult[i] = CCMath.step(_cRatio, theValues[i]);
		}
		
		return myResult;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#noiseImpl(double, double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return step(super.signalImpl(theX, theY, theZ));
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		return step(super.signalImpl(theX, theY));
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		return step(super.signalImpl(theX));
	}

}
