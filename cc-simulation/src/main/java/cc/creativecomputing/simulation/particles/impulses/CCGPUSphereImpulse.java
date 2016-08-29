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
package cc.creativecomputing.simulation.particles.impulses;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCGPUSphereImpulse extends CCGPUImpulse{
	
	private CGparameter _myRadiusParameter;
	private CGparameter _myCenterParameter;
	
	private CCVector3f _myCenter;
	private float _myRadius;

	/**
	 * @param theShaderTypeName
	 * @param theStrength
	 */
	public CCGPUSphereImpulse(CCVector3f theCenter, float theRadius, float theStrength) {
		super("SphereImpulse", theStrength);
		_myCenter = theCenter;
		_myRadius = theRadius;
	}

	@Override
	public void setupParameter(final int theWidth, final int theHeight){
		_myRadiusParameter  = parameter("radius");
		_myCenterParameter  = parameter("center");
		
		radius(_myRadius);
		center(_myCenter);
	}
	
	public void radius(final float theRadius) {
		_myVelocityShader.parameter(_myRadiusParameter, theRadius);
	}
	
	public void center(final CCVector3f theCenter) {
		_myCenter = theCenter;
	}
	
	public CCVector3f center() {
		return _myCenter;
	}
	
	@Override
	public void update(float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myCenterParameter, _myCenter);
	}

}
