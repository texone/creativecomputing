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

import cc.creativecomputing.animation.CCAnimation.CCAnimationEvent;
import cc.creativecomputing.app.modules.CCAnimator;

/**
 * Manager to handle animation instances. 
 * @author christianr
 * @author valentin schunack
 *
 */
public class CCAnimationManager extends ArrayList<CCAnimation>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8969318592897276003L;
	
	private double _myTime;

	/**
	 * Plays back the given Animation.
	 * @param theAnimation
	 */
	public void play(CCAnimation theAnimation) {
		if(theAnimation == null)return;
		
		add(theAnimation);
		theAnimation.play(_myTime);
	}
	
	public void progress(double theDelay, double theDuration, CCAnimationEvent theProgressEvent) {
		CCAnimation myAnimation = new CCAnimation(theDuration);
		myAnimation.delay(theDelay);
		myAnimation.progressEvents.add(theProgressEvent);
		play(myAnimation);
	}
	
	public void finish(double theDuration, CCAnimationEvent theFinishEvent) {
		CCAnimation myAnimation = new CCAnimation(theDuration);
		myAnimation.finishEvents.add(theFinishEvent);
		play(myAnimation);
	}

	/**
	 * Use this method to check if there are currently animations in playback.
	 * @return <code>true</code> if there are animations in playback otherwise false
	 */
	public boolean isPlaying() {
		return size() > 0;
	}

	/**
	 * Updates the animation manager and the containing animations
	 * @param theDeltaTime time since the last frame
	 */
	public void update(CCAnimator theAnimator) {
		_myTime = theAnimator.time();
		new ArrayList<>(this).forEach(a ->{
			if(!a.isRunning())return;
			a.update(theAnimator);
		});
	
		removeFinished();
	}

	private void removeFinished() {
		new ArrayList<>(this).forEach(a ->{
			if(!a.isRunning())remove(a);
		});
	}

}
