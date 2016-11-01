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

import cc.creativecomputing.math.CCPlane;

public class CCPlaneConstraint extends CCConstraint{
	private String _myConstantParameter;
	private String _myNormalParameter;
	
	protected CCPlane _myPlane;
	
	public CCPlaneConstraint(CCPlane thePlane, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("PlaneConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myPlane = thePlane;
		_myConstantParameter = parameter("constant");
		_myNormalParameter = parameter("normal");
	}

	public void setupParameter(final int theWidth, final int theHeight){
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myNormalParameter, _myPlane.normal());
		_myShader.uniform1f(_myConstantParameter, _myPlane.constant());
	}
	
	public CCPlane plane() {
		return _myPlane;
	}
	
}
