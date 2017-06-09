package cc.creativecomputing.math.util;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCHistogram {
	
	private int[] _myCounts = new int[10];
	
	private int _myBands = 10;
	
	private int _myMax = 0;
	
	private double _myStep;
	
	@CCProperty(name = "histogram bands", defaultValue = 10)
	public void bands(int theBands){
		_myBands = theBands;
		_myStep = 1d / _myBands;
		_myCounts = new int[_myBands];
	}

	public void reset(){
		_myCounts = new int[_myBands];
		_myMax = 0;
	}
	
	public int max(){
		return _myMax;
	}
	
	public int bands(){
		return _myBands;
	}
	
	public int count(int theBand){
		return _myCounts[theBand];
	}
	
	public void add(double theValue){
		for(int i = 0; i < _myBands;i++){
			if(theValue < (i + 1) * _myStep){
				_myCounts[i]++;
				_myMax = CCMath.max(_myMax, _myCounts[i]);
				break;
			}
		}
	}
	
	
}
