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
public class CCSpotLight extends CCPointLight{
	
	@CCProperty(name = "direction", min = -1, max = 1)
	private CCVector3 _myDirection;
	
	@CCProperty(name = "cone angle", min = 0, max = 90)
	private float _myConeAngle;
	
	@CCProperty(name = "spot decay exponent", min = 0, max = 10)
	private float _mySpotDecayExponent;
	
	public CCSpotLight(
		cc.creativecomputing.math.CCColor theColor, 
		CCVector3 thePosition,
		CCVector3 theDirection,
		float theRadius,
		float theIntensity
	) {
		super(theColor, thePosition, theRadius, theIntensity);
		_myDirection = theDirection;
	}
	
	public CCSpotLight() {
		this(new CCColor(), new CCVector3(), new CCVector3(0, 0,1), 10, 1.0f);
	}
	
	public CCVector3 direction() {
		return _myDirection;
	}
	
	public void direction(float theX, float theY, float theZ) {
		_myDirection.set(theX, theY, theZ);
	}
	
	public void direction(CCVector3 theDirection) {
		_myDirection.set(theDirection);
	}
	
	
	
	public void coneAngle(float theConeAngle) {
		_myConeAngle = theConeAngle;
	}
	
	public float coneAngle() {
		return _myConeAngle;
	}
	
	public void spotDecayExponent(float theRadius) {
		_mySpotDecayExponent = theRadius;
	}
	
	public float spotDecayExponent() {
		return _mySpotDecayExponent;
	}
}
