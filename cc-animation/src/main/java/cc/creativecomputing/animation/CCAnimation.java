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

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;

public class CCAnimation {
	private static int ourIdCounter;

	public static interface CCAnimationEvent {
		public void event(CCAnimation theAnimation);
	}

	public CCListenerManager<CCAnimationEvent> playEvents = CCListenerManager.create(CCAnimationEvent.class);
	public CCListenerManager<CCAnimationEvent> finishEvents = CCListenerManager.create(CCAnimationEvent.class);
	public CCListenerManager<CCAnimationEvent> cancelEvents = CCListenerManager.create(CCAnimationEvent.class);
	public CCListenerManager<CCAnimationEvent> progressEvents = CCListenerManager.create(CCAnimationEvent.class);

	protected double _myProgress;
	private boolean _myRunning;
	private boolean _myFinished;
	private boolean _myLoop;
	protected double _myStartTime;
	protected double _myDuration;
	protected double _myDelay;
	protected double _myProgressTime;
	protected final int _myId;
	
	public CCAnimation(double theDuration){
		_myDuration = theDuration;
		_myDelay = 0;
		_myId = ourIdCounter++;
		_myRunning = false;
		_myFinished = false;
		_myLoop = false;
	}

	public CCAnimation() {
		this(0);
	}

	public void update(CCAnimator theAnimator) {
//		CCLog.info("Anim #: " + _myId + " " + theTime+":" + _myStartTime);
		_myProgressTime = CCMath.max(0, theAnimator.time() - _myStartTime);
		if (_myDuration == 0.0f) {
			_myProgress = 1.0f;
		} else {
			_myProgress =  _myProgressTime / _myDuration;
		}
		progressEvents.proxy().event(this);
		if (_myProgressTime >= _myDuration) {
			finish(theAnimator.time());
		}
	}

	public void play(double theStartTime) {
//		CCLog.info("Anim #" + _myId + " set to play");

		_myStartTime = theStartTime + _myDelay;
		_myProgressTime = 0;
		_myProgress = _myProgressTime;
		_myRunning = true;
		_myFinished = false;

		playEvents.proxy().event(this);
	}

	public void finish(double theTime) {
		if (_myFinished) {
			return;
		}
		if (!_myRunning) {
			play(theTime);
		}
		finishAnimation(theTime);
	}

	public void cancel() {
		_myRunning = false;
		_myFinished = true;
		cancelEvents.proxy().event(this);
	}

	public boolean isRunning() {
		return _myRunning;
	}

	public boolean isFinished() {
		return _myFinished;
	}

	public void loop(boolean theLoop) {
		_myLoop = theLoop;
	}

	public double duration() {
		return _myDuration;
	}

	public void duration(double theDuration) {
		 _myDuration = theDuration;
	}
	
	public double progress(){
		return _myProgress;
	}

	public double delay() {
		return _myDelay;
	}

	public void delay(double theDelay) {
		 _myDelay = theDelay;
	}
	
	public int id() {
		return _myId;
	}

	protected void finishAnimation(double theTime) {
		_myProgressTime = _myDuration;
		_myProgress = 1;
		finishEvents.proxy().event(this);
		if (_myLoop) {
			play(theTime);
		} else {
			_myRunning = false;
			_myFinished = true;
		}
	}
}
