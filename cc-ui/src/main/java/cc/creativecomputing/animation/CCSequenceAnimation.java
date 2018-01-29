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

import cc.creativecomputing.math.CCMath;


public class CCSequenceAnimation extends CCCompositeAnimation {
	private int _myCurrent;

	public CCSequenceAnimation() {
		super();
	}

	public void update(float theTime) {
		if (_myCurrent >= _myChildren.size()) {
			finishAnimation(theTime);
			return;
		}
		_myChildren.get(_myCurrent).update(theTime);
		if (!_myChildren.get(_myCurrent).isRunning() && isRunning()) {
			_myCurrent++;
			if (_myCurrent < _myChildren.size()) {
				_myChildren.get(_myCurrent).play(theTime);
			} else {
				finishAnimation(theTime);
			}
		}
		_myProgressTime = CCMath.max(0, theTime - _myStartTime);
		if (_myDuration == 0.0f) {
			_myProgress = 1.0f;
		} else {
			_myProgress = _myProgressTime / _myDuration;
		}
		_myEvents.proxy().onProgress(this, Math.min(_myProgress, 1.0f));
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
