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
package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class CCOneEuroFilter extends CCFilter {
	private class SingleExponentialFilter {

		private double _myLastX;
		private double _myAlpha;
		private double _myLastEstimate;

		private SingleExponentialFilter(double theAlpha) {
			_myAlpha = theAlpha;
			_myLastEstimate = -1;
			_myLastX = -1;
		}

		public double filter(double x) {
			if (_myLastX == -1) {
				_myLastEstimate = x;
			} else {
				_myLastEstimate = lowpass(x);
			}
			_myLastX = x;

			return _myLastEstimate;
		}

		public double lowpass(double x) {
			return _myAlpha * x + (1.0f - _myAlpha) * _myLastEstimate;
		}
	}
	
	@CCProperty(name = "minimum cutoff", min = 0, max = 1)
	private double _cMinimumCutoff = 1f;
	@CCProperty(name = "cutoff slope", min = 0, max = 1)
	private double _cBeta = 0.007f;
	@CCProperty(name = "derivative cutoff", min = 0, max = 1)
	private double _cDerivativeCutoff = 1f;
	
	private SingleExponentialFilter[] _myFilter;
	private SingleExponentialFilter[] _myDerivativeFilter;
	
	
	public CCOneEuroFilter(int theChannels) {
		_myChannels = theChannels;
		reset();
	}
	
	public CCOneEuroFilter(){
		this(1);
	}
	
	private void reset(){
		_myFilter = new SingleExponentialFilter[_myChannels];
		_myDerivativeFilter = new SingleExponentialFilter[_myChannels];
		for(int i = 0; i < _myChannels;i++){
			_myFilter[i] = new SingleExponentialFilter(alpha(_cMinimumCutoff, 1 / 25d));
			_myDerivativeFilter[i] = new SingleExponentialFilter(alpha(_cDerivativeCutoff, 1 / 25d));
		}
	}

	private double alpha(double cutoff, double frequency) {
		double te = 1.0f / frequency;
		double tau = 1.0f / (2 * CCMath.PI * cutoff);
		return 1.0f / (1.0f + tau / te);
	}
	
	@Override
	public double process(int theChannel, double theData, double theDeltaTime) {
		if(theChannel >= _myChannels){
			_myChannels = theChannel;
			reset();
		}
		double myFrequency = 1.0f / (theDeltaTime);
		
		double previousX = _myFilter[theChannel]._myLastX;
		double dx = (previousX == -1) ? 0 : (theData - previousX) * myFrequency;
		
		_myDerivativeFilter[theChannel]._myAlpha = alpha(_cDerivativeCutoff, myFrequency);
		double edx = _myDerivativeFilter[theChannel].filter(dx);
		double cutoff = _cMinimumCutoff + _cBeta * Math.abs(edx);
		
		_myFilter[theChannel]._myAlpha = alpha(cutoff, myFrequency);
		
		if(_myBypass)return theData;
		
		return _myFilter[theChannel].filter(theData);
	}
	
	
}
