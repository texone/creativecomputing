package cc.creativecomputing.gl.app;

import cc.creativecomputing.core.CCProperty;


import static org.lwjgl.glfw.GLFW.*;

public class CCGLTimer {
	@CCProperty(name = "fix update time")
	public boolean fixUpdateTime = false;
	
	@CCProperty(name = "fixed update time", min = 0, max = 1)
	public double fixedUpdateTime = 1;
	
	private double _myFrameRate = 0;
	private int _myFrameCount = 0;
	
	private double _myLastTime = 0;
	private double _myDeltatime = 0;
	private double _myDeltaVariation = 0;
	private double _myTimeSinceStart = 0;
	
	public void calculateDeltaTime(){
		if(fixUpdateTime){
			_myLastTime = glfwGetTime();
			_myDeltatime =  fixedUpdateTime;
			_myTimeSinceStart += _myDeltatime;
			return;
		}
		
		if (_myLastTime == 0){
			_myLastTime = glfwGetTime();
			_myDeltatime = 0;
			return;
		}
		
		double myTime = glfwGetTime();
		_myDeltaVariation = _myDeltatime;
		_myDeltatime = myTime - _myLastTime;
		_myLastTime = myTime;
		
		if (_myDeltatime == 0)return;
		
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