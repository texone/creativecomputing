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
package cc.creativecomputing.video;

import cc.creativecomputing.app.modules.CCAnimatorModule;
import cc.creativecomputing.core.events.CCListenerManager;

/**
 * This class is representing video data so the content of the object
 * is updated permanently on playback. It also implements the movie
 * interface for control of the play back.
 * @author christian riekoff
 *
 */
public abstract class CCMovieData extends CCVideo implements CCMovie{
	
	/**
	 * set this true for looping
	 */
	protected boolean _myDoRepeat = false;
	
	/**
	 * indicates if the movie is running
	 */
	protected boolean _myIsRunning = false;
	
	protected boolean _myIsPaused = false;
	
	protected CCListenerManager<CCMovieListener> _myMovieEvents = CCListenerManager.create(CCMovieListener.class);

	/**
	 * Creates a new instance, without setting any parameters.
	 * @param theAnimator
	 */
	public CCMovieData(final CCAnimatorModule theAnimator) {
		super(theAnimator);
	}
	
	public CCListenerManager<CCMovieListener> events(){
		return _myMovieEvents;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#isRunning()
	 */
	public boolean isRunning() {
		return _myIsRunning;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#loop()
	 */
	public void loop() {
		_myDoRepeat = true;
		try {
			play();
			_myIsRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			_myIsRunning = false;
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#loop(boolean)
	 */
	public void loop(boolean theDoLoop) {
		_myDoRepeat = theDoLoop;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#progress()
	 */
	public float progress() {
		return time() / duration();
	}

	public void play() {
		play(false);
	}
	
	public void play(boolean theDoRestart) {
		_myIsRunning = true;
		_myIsPaused = false;

		if (theDoRestart)
			goToBeginning();
		_myMovieEvents.proxy().onPlay();
	}

	public void stop() {
		if (_myIsRunning) {
			goToBeginning();
			_myIsRunning = false;
		}
		_myIsPaused = false;
		_myMovieEvents.proxy().onStop();
	}
	
	public void pause() {
		_myIsRunning = false;
		_myIsPaused = true;
		_myMovieEvents.proxy().onPause();
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#volume()
	 */
	public float volume() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.texture_new.video.CCMovie#volume(float)
	 */
	public void volume(float theVolume) {
		
	}
	
	/**
	 * Set the time of the movie in seconds
	 * @param theTime
	 */
	public void time(float theTime) {
		
	}
	
	/**
	 * 
	 */
	public float time() {
		return 0;
	}
}
