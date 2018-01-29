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
import java.util.List;

public class CCCompositeAnimation extends CCAnimation {
	protected List<CCAnimation> _myChildren = new ArrayList<>();

	public CCCompositeAnimation() {
		super();
	}

	public void add(CCAnimation theAnimation) {
		_myChildren.add(theAnimation);
		childDurationChanged();
	}

	protected void childDurationChanged() {
	}

	public void finish(float theTime) {
		for (CCAnimation myAnimation:_myChildren) {
			myAnimation.finish(theTime);
		}
		super.finish(theTime);
	}

	public void cancel() {
		for (CCAnimation myAnimation:_myChildren) {
			if (myAnimation.isRunning()) {
				myAnimation.cancel();
			}
		}
		super.cancel();
	}
}
