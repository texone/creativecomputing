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
package cc.creativecomputing.graphics.shader.postprocess.deferred;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;

/**
 * @author christianriekoff
 *
 */
public class CCDirectionalLight extends CCDeferredLight{
	//direction of the light
	@CCProperty(name = "direction", min = -1, max = 1)
	private CCVector3 _myLightDirection;
	
	public CCDirectionalLight(CCColor theColor, CCVector3 theDirection) {
		super(theColor);
		_myLightDirection = theDirection;
	}
	
	public CCDirectionalLight() {
		this(new CCColor(), new CCVector3());
	}
	
	public CCVector3 lightDirection() {
		return _myLightDirection;
	}
	
	public void lightDirection(float theX, float theY, float theZ) {
		_myLightDirection.set(theX, theY, theZ);
	}
	
	public void lightDirection(CCVector3 theDirection) {
		_myLightDirection.set(theDirection);
	}
}
