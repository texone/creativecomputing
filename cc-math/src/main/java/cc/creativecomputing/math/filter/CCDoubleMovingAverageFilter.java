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
 * @author christianriekoff
 *
 */
public class CCDoubleMovingAverageFilter extends CCFilter{
	
	@CCProperty(name = "buffer size", min = 1, max = 100)
	private int _cBufferSize = 1;
		
	@CCProperty(name = "forecast", min = 0, max = 10)
	private double _cForeCast = 0;

	protected CCFilterHistoryBuffer _myBuffer;
	
	private CCFilterHistoryBuffer _myMovingAverages;
	
	public CCDoubleMovingAverageFilter(int theChannels) {
		_myChannels = theChannels;
		reset();
	}
	
	public CCDoubleMovingAverageFilter() {
		this(1);
	}
	
	
	public void reset() {
		_myBuffer = new CCFilterHistoryBuffer(_myChannels, 100);
		_myMovingAverages = new CCFilterHistoryBuffer(_myChannels, 100);
	}
	
	@Override
	public double process(int theChannel, double theData, double theTime) {
		if(theChannel >= _myChannels){
			_myChannels = theChannel;
			reset();
		}
		
		_myBuffer.append(theChannel, theData);
		double myAverage = _myBuffer.average(theChannel);
		_myMovingAverages.append(theChannel, myAverage);
		
		if(_myBypass)return theData;
		
		double myAverageAverage = _myMovingAverages.average(theChannel);
		
		
		double myValue = 2 * myAverage - myAverageAverage;
		return myValue + (2 * _cForeCast) / (_myBuffer.useable(theChannel) + 1) * (myAverage - myAverageAverage);
	}
}
