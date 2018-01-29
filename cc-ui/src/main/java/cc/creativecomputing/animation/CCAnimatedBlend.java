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

import cc.creativecomputing.animation.CCAnimation.CCAnimationListener;

public class CCAnimatedBlend<Type extends CCBlendable<Type>>  implements CCAnimationListener, CCBlendModifier{
	
	private Type _myObject;
	private Type _myStart;
	private Type _myTarget;
	
	private CCAnimation _myAnimation;
	
	public CCAnimatedBlend(Type theProperty, Type theStart, Type theTarget){
		_myObject = theProperty;
		_myObject.modifier(this);
		_myStart = theStart.clone();
		_myTarget = theTarget.clone();
	}

	@Override
	public void onPlay(CCAnimation theAnimation) {
		_myAnimation = theAnimation; 
	}

	@Override
	public void onFinish(CCAnimation theAnimation) {
	}

	@Override
	public void onCancel(CCAnimation theAnimation) {
	}

	@Override
	public void onProgress(CCAnimation theAnimation, double theProgress) {
		_myObject.blend(theProgress, _myStart, _myTarget);
	}

	@Override
	public void onReplace() {
		if(_myAnimation != null)_myAnimation.events().remove(this);
	}
}
