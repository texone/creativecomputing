/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.math.CCPlane;

public class CCPlaneConstraint extends CCConstraint{
	private String _myConstantParameter;
	private String _myNormalParameter;
	
	protected CCPlane _myPlane;
	
	public CCPlaneConstraint(CCPlane thePlane, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super("PlaneConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myPlane = thePlane;
		_myConstantParameter = parameter("planeConstant");
		_myNormalParameter = parameter("planeNormal");
	}
	
	public CCPlaneConstraint(CCPlane thePlane) {
		this(thePlane,0,0,0);
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
