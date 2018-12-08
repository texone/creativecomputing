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
 * In a median filter (also known as moving median filter), the filter's output is the 
 * median of the last N inputs. Median filters are useful in removing impulsive spike noises, 
 * as shown in Figure 9. Ideally, the filter size N should be selected to be larger than the 
 * duration of the spike noise peaks. However, the filter's latency directly depends on N, and 
 * hence, a larger N adds more latency.
 * @author christianriekoff
 *
 */
public class CCMedianFilter extends CCFilter{
	
	
	@CCProperty(name = "buffer size", min = 1, max = 100)
	private int _cBufferSize = 1;
	
	private CCFilterHistoryBuffer _myBuffer;
	
	public CCMedianFilter(int theChannels) {
		_myChannels = theChannels;
		reset();
	}
	
	public CCMedianFilter() {
		this(1);
	}
	
	public void reset() {
		_myBuffer = new CCFilterHistoryBuffer(_myChannels,_cBufferSize);
	}
	
	@Override
	public double process(int theChannel, double theData, double theTime) {
		
		if(theChannel >= _myChannels){
			_myChannels = theChannel;
			reset();
		}
		if(_myBuffer.size() != _cBufferSize) {
			reset();
		}
		_myBuffer.append(theChannel, theData);
		
		if(_myBypass)return theData;
		
		return _myBuffer.sort(theChannel)[_myBuffer.useable(theChannel) / 2];
	}
}
