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
package cc.creativecomputing.animation;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manager to handle animation instances. 
 * @author christianr
 * @author valentin schunack
 *
 */
public class CCAnimationManager {
	
	private ArrayList<CCAnimation> _myAnimations = new ArrayList<CCAnimation>();
	private ArrayList<CCAnimation> _myOnFrameCollectedAnimations = new ArrayList<CCAnimation>();;
	boolean _myIsOnFrame = false;
	double _myAnimationTime;

	/**
	 * Returns the number of playing animations.
	 * @return number of playing animations
	 */
	public int animationCount() {
		return _myAnimations.size();
	}

	/**
	 * Plays back the given Animation.
	 * @param theAnimation
	 */
	public void play(CCAnimation theAnimation) {
		if(theAnimation == null)return;
		if (_myIsOnFrame) {
			_myOnFrameCollectedAnimations.add(theAnimation);
		} else {
			_myAnimations.add(theAnimation);
			theAnimation.play(_myAnimationTime + theAnimation.delay());
		}
	}

	/**
	 * Use this method to check if there are currently animations in playback.
	 * @return <code>true</code> if there are animations in playback otherwise false
	 */
	public boolean isPlaying() {
		return _myAnimations.size() > 0;
	}

	/**
	 * Updates the animation manager and the containing animations
	 * @param theDeltaTime time since the last frame
	 */
	public void update(double theDeltaTime) {
		_myIsOnFrame = true;
		_myAnimationTime += theDeltaTime;
		for (CCAnimation myAnimation:_myAnimations) {
			if (!myAnimation.isRunning()) continue;
			
			myAnimation.update(_myAnimationTime);
		}
		removeFinished();
		_myIsOnFrame = false;
		
		for (CCAnimation myAnimation:_myOnFrameCollectedAnimations) {
			_myAnimations.add(myAnimation);
			myAnimation.play(_myAnimationTime);
		}
		_myOnFrameCollectedAnimations.clear();
	}

	/**
	 * Set the time of the animation manager in seconds
	 * @param theTime animation time in seconds
	 */
	public void init(double theTime) {
		_myAnimationTime = theTime;
	}

	/**
	 * Returns the time of the animation manager in seconds
	 * @return the time of the animation in seconds
	 */
	public double time() {
		return _myAnimationTime;
	}

	public CCAnimationManager() {
	}

	private void removeFinished() {
		Iterator<CCAnimation> myIt = _myAnimations.iterator();
		while (myIt.hasNext()) {
			CCAnimation myNextAnimation = myIt.next();
			if (!(myNextAnimation.isRunning())) {
				myIt.remove();
			}
		}

	}

}
