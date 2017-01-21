package cc.creativecomputing.math.filter;

import java.util.Arrays;

import cc.creativecomputing.math.CCMath;

public class CCFilterHistoryBuffer {

	private int[] _myUseableBuffer;
	private int[] _myIndex;
	
	private double[][] _myBuffer;
	
	private int _mySize;
	private int _myChannels;
	
	public CCFilterHistoryBuffer(int theChannels, int theSize){
		_mySize = theSize;
		_myChannels = theChannels;
		_myBuffer = new double[theChannels][theSize];
		reset();
	}
	
	public void reset() {
		_myUseableBuffer = new int[_myChannels];
		_myIndex = new int[_myChannels];
	}
	
	public void append(int theChannel, double theData){
		_myUseableBuffer[theChannel] = CCMath.min(_myUseableBuffer[theChannel], _mySize);
		if(_myUseableBuffer[theChannel] < _mySize) {
			_myUseableBuffer[theChannel]++;
		}
		
		_myBuffer[theChannel][_myIndex[theChannel]] = theData;

		_myIndex[theChannel]++;
		_myIndex[theChannel] %= _mySize;
	}
	
	public double get(int theChannel, int theID){
		return _myBuffer[theChannel][(_myIndex[theChannel] + theID) % _mySize];
	}
	
	public double average(int theChannel){
		double myAverage = 0;
		for(int i = 0; i < _myUseableBuffer[theChannel];i++) {
			myAverage += _myBuffer[theChannel][i];
		}
		myAverage /= _myUseableBuffer[theChannel];
		return myAverage;
	}
	
	public int useable(int theChannel){
		return _myUseableBuffer[theChannel];
	}
	
	public double[] sort(int theChannel){
		double[] _mySortedValues = new double[_mySize];
		System.arraycopy(_myBuffer[theChannel], 0, _mySortedValues, 0, _myUseableBuffer[theChannel]);
		Arrays.sort(_mySortedValues, 0, _myUseableBuffer[theChannel]);
		return _mySortedValues;
	}
}
