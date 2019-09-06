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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCSphereConstraint extends CCConstraint{
	private String _myRadiusParameter;
	private String _myCenterParameter;
	
	@CCProperty(name = "center")
	private CCVector3 _myCenter;
	@CCProperty(name = "radius")
	private float _myRadius;
	
	@CCProperty(name = "inside")
	private boolean _cInside = false;
	
	public CCSphereConstraint(
		final CCVector3 theCenter, final float theRadius, 
		final boolean theStayInside
	) {
		super("sphereConstraint");
		
		_myCenter = new CCVector3(theCenter);
		_myRadius = theRadius;
		
		_myRadiusParameter  = parameter("radius");
		_myCenterParameter  = parameter("center");
	}
	
	public CCSphereConstraint(
		final CCVector3 theCenter, final float theRadius
	) {
		this(theCenter, theRadius, false);
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myCenterParameter, _myCenter);
		_myShader.uniform1f(_myRadiusParameter, _myRadius);
	}
	
	public void radius(final float theRadius) {
		_myRadius = theRadius;
	}
	
	public void center(final CCVector3 theCenter) {
		_myCenter.set(theCenter);
	}
	
}
