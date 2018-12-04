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
import cc.creativecomputing.math.CCMath;


public class CCSequenceAnimation extends CCCompositeAnimation {
	private int _myCurrent;

	public CCSequenceAnimation() {
		super();
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if (_myCurrent >= _myChildren.size()) {
			finishAnimation(theAnimator.time());
			return;
		}
		_myChildren.get(_myCurrent).update(theAnimator);
		if (!_myChildren.get(_myCurrent).isRunning() && isRunning()) {
			_myCurrent++;
			if (_myCurrent < _myChildren.size()) {
				_myChildren.get(_myCurrent).play(theAnimator.time());
			} else {
				finishAnimation(theAnimator.time());
			}
		}
		_myProgressTime = CCMath.max(0, theAnimator.time() - _myStartTime);
		if (_myDuration == 0.0f) {
			_myProgress = 1.0f;
		} else {
			_myProgress = _myProgressTime / _myDuration;
		}
		progressEvents.proxy().event(this);
	}

	protected void childDurationChanged() {
		float myDurationSum = 0;
		for (int i = 0; i < _myChildren.size(); i++) {
			myDurationSum += _myChildren.get(i).duration();
		}
		_myDuration = myDurationSum;
	}

	public void play(float theStartTime) {
		super.play(theStartTime);
		if (isRunning()) {
			_myCurrent = 0;
			if (_myChildren.size() > 0) {
				_myChildren.get(0).play(theStartTime);
			}
		}
	}
	
	protected void finishAnimation(float theTime) {
		_myCurrent = 0;
		super.finishAnimation(theTime);
	}
}
