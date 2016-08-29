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
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.math.CCVector3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUSphereConstraint extends CCGPUConstraint{
	private CGparameter _myRadiusParameter;
	private CGparameter _myCenterParameter;
	
	private CCVector3f _myCenter;
	private float _myRadius;
	
	public CCGPUSphereConstraint(
		final CCVector3f theCenter, final float theRadius, 
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		this(theCenter, theRadius, theResilience, theFriction, theMinimalVelocity,false);
	}
	
	public CCGPUSphereConstraint(
		final CCVector3f theCenter, final float theRadius, 
		final float theResilience, final float theFriction, final float theMinimalVelocity,
		final boolean theStayInside
	) {
		super(theStayInside ? "SphereInConstraint":"SphereConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myCenter = theCenter;
		_myRadius = theRadius;
	}

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
		_myVelocityShader.parameter(_myCenterParameter, theCenter);
	}
	
}
