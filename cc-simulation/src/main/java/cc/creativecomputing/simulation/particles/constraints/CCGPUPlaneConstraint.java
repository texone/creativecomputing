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

import cc.creativecomputing.math.CCPlane3f;

import com.jogamp.opengl.cg.CGparameter;

public class CCGPUPlaneConstraint extends CCGPUConstraint{
	private CGparameter _myConstantParameter;
	private CGparameter _myNormalParameter;
	
	protected CCPlane3f _myPlane;
	
	public CCGPUPlaneConstraint(CCPlane3f thePlane, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("PlaneConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myPlane = thePlane;
	}

	public void setupParameter(final int theWidth, final int theHeight){
		_myConstantParameter = parameter("constant");
		_myNormalParameter = parameter("normal");
	}
	
	public CCPlane3f plane() {
		return _myPlane;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint#update(float)
	 */
	@Override
	public void update(float theDeltaTime) {
		_myVelocityShader.parameter(_myConstantParameter, _myPlane.constant());
		_myVelocityShader.parameter(_myNormalParameter, _myPlane.normal());
	}
	
}
