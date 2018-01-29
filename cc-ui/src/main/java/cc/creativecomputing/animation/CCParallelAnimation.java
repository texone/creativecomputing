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

public class CCParallelAnimation  extends CCCompositeAnimation{
	//private int _myCurrent;
	public CCParallelAnimation() {
		super();	
	}
	
	public void update(double theTime) {
		if(!isRunning())return;
		
		boolean myNotFinishedFlag = false;
		
		for(CCAnimation myAnimation:_myChildren) {
			if (!myAnimation.isRunning()) continue;
				
			myAnimation.update(theTime);
			myNotFinishedFlag |= myAnimation.isRunning(); 
		}		
		if (myNotFinishedFlag) return;
		
		finishAnimation(theTime);
	}
	
	protected void childDurationChanged() {
		double myMaxDuration = 0;
		for(CCAnimation myAnimation:_myChildren) {
			myMaxDuration = CCMath.max(myAnimation.duration(), myMaxDuration);
		}		
		_myDuration = myMaxDuration;
	}
	
	public void play(double theStartTime) {
		super.play(theStartTime);
		
		if(!isRunning()) return;
		
		for(CCAnimation myAnimation:_myChildren) {
			myAnimation.play(theStartTime);
		}
	}
}
