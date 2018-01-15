package cc.creativecomputing.app.modules;

import cc.creativecomputing.core.CCProperty;

public class CCTimer {
	@CCProperty(name = "fix update time")
	public boolean fixUpdateTime = false;
	
	@CCProperty(name = "fixed update time", min = 0, max = 1)
	public double fixedUpdateTime = 1;
	
	private double _myFrameRate = 0;
	private int _myFrameCount = 0;

	private long _myFrameRateLastNanos = 0;
	private long _myDeltaTimeNanos = 0;
	
	private double _myDeltatime = 0;
	private double _myDeltaVariation = 0;
	private double _myTimeSinceStart = 0;
	
	public void calculateDeltaTime(){
		if(fixUpdateTime){
			_myFrameRateLastNanos = System.nanoTime();
			_myDeltatime =  fixedUpdateTime;
			_myTimeSinceStart += _myDeltatime;
			return;
		}
		
		if (_myFrameRateLastNanos == 0){
			_myFrameRateLastNanos = System.nanoTime();
			_myDeltatime = 0;
			return;
		}
		
		long myNanos = System.nanoTime();
		_myDeltaTimeNanos = myNanos - _myFrameRateLastNanos;
		_myFrameRateLastNanos = myNanos;
		
		if (_myDeltaTimeNanos == 0)return;
		
		_myDeltaVariation = _myDeltatime;
		_myDeltatime = _myDeltaTimeNanos / 1e9;
		_myDeltaVariation -= _myDeltatime;
		_myTimeSinceStart += _myDeltatime;
		_myFrameRate = 1f / _myDeltatime;
	}
	
	/**
	 * Returns the current frame rate of the animator
	 * @return current frame rate of the animator
	 */
	@CCProperty(name = "show frame rate", readBack = true)
	public double frameRate(){
		return _myFrameRate;
	}
	
	/**
	 * Returns the time since the last frame of the animator in seconds
	 * @return time since the last frame of the animator 
	 */
	public double deltaTime(){
		return _myDeltatime;
	}
	
	public double deltaTimeVariation(){
		return _myDeltaVariation;
	}
	
	/**
	 * Returns the time since the start of the animator in seconds
	 * @return
	 */
	public double time(){
		return _myTimeSinceStart;
	}
	
	/**
	 * Returns the number of frames since application start.
	 * @return number of frames since application start
	 */
	public int frames(){
		return _myFrameCount;
	}
}
