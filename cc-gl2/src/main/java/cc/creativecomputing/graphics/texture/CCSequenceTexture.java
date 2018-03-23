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
package cc.creativecomputing.graphics.texture;

import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.image.CCImage;


/**
 * Use this class to play texture sequences. You can create a sequence texture
 * by passing an array of texture data objects, or files.
 * @author Christian Riekoff
 *
 */
public class CCSequenceTexture extends CCTexture2D {
	
	/**
	 * frame rate of the sequence
	 */
	private double _myFrameRate = 15;
	
	/**
	 * play back rate of the sequence
	 */
	private double _myRate = 1;
	
	/**
	 * Duration of the movie in seconds
	 */
	private double _myDuration = 0;
	
	/**
	 * Current time of the sequence in seconds
	 */
	private double _myTime = 0;
	
	/**
	 * true if the movie is looping otherwise false
	 */
	protected boolean _myIsLooping = false;
	
	/**
	 * true if the movie is running otherwise false
	 */
	private boolean _myIsRunning;

	/**
	 * Creates a new sequence texture from the given texture data objects.
	 * @param theTarget texture target can be <code>TEXTURE_RECT</code> or <code>TEXTURE_2D</code>
	 * @param theAttributes  attributes of the texture to be generated
	 * @param theTextureData array containing the texture data
	 */
	public CCSequenceTexture(CCTextureTarget theTarget, CCTextureAttributes theAttributes, List<CCImage> theTextureData) {
		super(theTarget, theAttributes, theTextureData.size());
		
		for(CCImage myData:theTextureData) {
			data(myData);
			_myTextureID++;
		}
		_myTextureID = 0;
		_myIsRunning = false;
		
		_myDuration = _myTextureIDs.length / (_myFrameRate);
	}
	
	/**
	 * Updates the texture for playback
	 */
	public void update(CCAnimator theAnimator) {
		if (_myIsRunning) {
			_myTime += theAnimator.deltaTime() * _myRate;
			_myTextureID = (int)(_myTime / _myDuration * (_myTextureIDs.length - 1));
			
			if(_myTextureID >= _myTextureIDs.length){
				if(_myIsLooping){
					_myTextureID = 0;
					_myTime = 0;
				}else{
					_myIsRunning = false;
					_myTime = _myDuration;
				}
			}
			
			if(_myTextureID < 0){
				if(_myIsLooping){
					_myTextureID = (_myTextureIDs.length - 1);
					_myTime = _myDuration;
				}else{
					_myIsRunning = false;
					_myTime = 0;
				}
			}
		}
	}
	
	/**
	 * Use this to define the frame rate of you texture sequence
	 * @param theFrameRate frame rate of the texture sequence
	 */
	public void frameRate(final double theFrameRate) {
		_myFrameRate = theFrameRate;
	}
	
	public double frameRate() {
		return _myFrameRate;
	}

	public double duration() {
		return _myDuration;
	}

	public void goToBeginning() {
		time(0);
	}

	public boolean isRunning() {
		return _myIsRunning;
	}

	public void loop() {
		_myIsLooping = true;
		try {
			play();
			_myIsRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			_myIsRunning = false;
		}
	}

	public void loop(boolean theDoLoop) {
		_myIsLooping = theDoLoop;
	}

	public double progress() {
		return time() / duration();
	}

	public void rate(double theRate) {
		_myRate = theRate;
	}

	public double rate() {
		return _myRate;
	}

	public void play() {
		play(false);
	}

	public void play(boolean theRestart) {
		if(theRestart) {
			_myTextureID = 0;
			_myTime = 0;
		}
		_myIsRunning = true;
	}

	public void stop() {
		pause();
		goToBeginning();
	}
	
	public void pause() {
		_myIsRunning = false;
	}

	public double time() {
		return _myTime;
	}

	public void time(double theNewtime) {
		_myTime = 0;
		_myTextureID = (int)(_myTime/_myDuration);
	}

	public double volume() {
		return 0;
	}

	public void volume(double theVolume) {
	}
}
