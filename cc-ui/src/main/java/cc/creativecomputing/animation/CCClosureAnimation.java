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

import cc.creativecomputing.math.easing.CCEasing;

public class CCClosureAnimation extends CCAnimation {
	private CCCallBack _myClosureCB = null;

	public CCClosureAnimation(
		float theDuration, 
		CCCallBack theClosureCB,
		CCEasing.CCEaseFormular theEasingFormular, 
		CCEasing.CCEaseMode theEasingMode
	) {
		super(theDuration);
		super.setEasingMode(theEasingFormular, theEasingMode);
		_myClosureCB = theClosureCB;
	}

	public void update(float theTime) {
		if (_myClosureCB != null) {
			try {
				_myClosureCB.invoke(_myProgress);
			} catch (Exception theE) {
			}
		}
		super.update(theTime);
	}
}
