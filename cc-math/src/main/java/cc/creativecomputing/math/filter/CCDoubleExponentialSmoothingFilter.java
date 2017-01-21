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

/**
 * The double exponential smoothing filter is a popular smoothing filter used in many applications. 
 * Similar to a double moving averaging filter, the double exponential smoothing filter smoothes the 
 * smoothed output by applying a second exponential filter (hence the name double exponential), and it 
 * uses this to account for trends in input data. There are various formulations of double exponential 
 * smoothing filters, with minor differences between them this implementation is based on the most popular one.
 * <p>
 * @author christianriekoff
 *
 */
public class CCDoubleExponentialSmoothingFilter extends CCFilter{
	
	@CCProperty(name = "weight", min = 0, max = 1)
	protected double _cWeight;

	@CCProperty(name = "trend weight", min = 0, max = 1)
	protected double _cTrendWeight;

	@CCProperty(name = "forecast", min = 0, max = 10)
	protected double _cForeCast;


	private double[] _myTrend;
	private double[] _myLastTrend;
	private double[] _myValue;
	
	public CCDoubleExponentialSmoothingFilter(int theChannels) {
		_myChannels = theChannels;
		reset();
	}
	
	public CCDoubleExponentialSmoothingFilter() {
		this(1);
	}
	
	private void reset() {
		_myTrend = new double[_myChannels];
		_myLastTrend = new double[_myChannels];
		_myValue = new double[_myChannels];
	}
	
	@Override
	public double process(int theChannel, double theData, double theDeltaTime) {
		if(theChannel >= _myChannels){
			_myChannels = theChannel;
			reset();
		}
		_myLastTrend[theChannel] = _myTrend[theChannel];
		_myTrend[theChannel] = (theData - _myValue[theChannel]) * _cTrendWeight + (1 - _cTrendWeight) * _myLastTrend[theChannel];
		_myValue[theChannel] = _cWeight * theData + (1 - _cWeight) * (_myValue[theChannel] + _myLastTrend[theChannel]);
		if(_myBypass)return theData;
		return _myValue[theChannel] + _cForeCast * _myTrend[theChannel];
	}
	
}
