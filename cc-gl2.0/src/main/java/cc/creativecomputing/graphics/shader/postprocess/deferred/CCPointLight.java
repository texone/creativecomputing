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
public class CCPointLight extends CCDeferredLight{
	
	//this is the position of the light
	@CCProperty(name = "position", min = -1000, max = 1000)
	private CCVector3 _myPosition;
	
	//how far does this light reach
	@CCProperty(name = "radius", min = 0, max = 1000)
	private float _myRadius;
	
	//control the brightness of the light
	@CCProperty(name = "intensity", min = 0, max = 1)
	private float _myIntensity = 1.0f;
	
	public CCPointLight(
		CCColor theColor, 
		CCVector3 theLightPosition,
		float theLightRadius,
		float theLightIntensity
	) {
		super(theColor);
		_myPosition = theLightPosition;
		_myRadius = theLightRadius;
		_myIntensity = theLightIntensity;
	}
	
	public CCPointLight() {
		this(new CCColor(255), new CCVector3(), 10, 1.0f);
	}
	
	public CCVector3 position() {
		return _myPosition;
	}
	
	public void position(float theX, float theY, float theZ) {
		_myPosition.set(theX, theY, theZ);
	}
	
	public void position(CCVector3 theDirection) {
		_myPosition.set(theDirection);
	}
	
	public void intensity(float theIntensity) {
		_myIntensity = theIntensity;
	}
	
	public float intensity() {
		return _myIntensity;
	}
	
	public void radius(float theRadius) {
		_myRadius = theRadius;
	}
	
	public float radius() {
		return _myRadius;
	}
}
