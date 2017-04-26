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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class CCExponentialSmoothingFilter extends CCFilter{
	
	@CCProperty(name = "weight", min = 0, max = 1)
	protected double _cWeight;
		
	@CCProperty(name = "skip range", min = 0, max = 50)
	protected double _cSkipRange = 0;
	
	public CCExponentialSmoothingFilter(int theChannels) {
		_myChannels = theChannels;
		_myValues = new double[_myChannels];
	}
	
	public CCExponentialSmoothingFilter() {
		this(1);
	}
	
	private double[] _myValues;
	
	
	@Override
	public double process(int theChannel, double theData, double theTime) {
		if(theChannel >= _myChannels || _myValues.length < _myChannels){
			_myChannels = theChannel + 1;
			_myValues = new double[_myChannels];
		}
		if(_myValues[theChannel] == 0) {
			_myValues[theChannel] = theData;
			return _myValues[theChannel];
		}
		if(CCMath.abs(_myValues[theChannel] - theData) > _cSkipRange && _cSkipRange > 0) {
			_myValues[theChannel] = theData;
			return _myValues[theChannel];
		}
		
		_myValues[theChannel] = _myValues[theChannel] * _cWeight + theData * (1 - _cWeight);
		return _myValues[theChannel];
	}
}
