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
		final float theResilience, final float theFriction, final float theMinimalVelocity,
		final boolean theStayInside
	) {
		super("sphereConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myCenter = new CCVector3(theCenter);
		_myRadius = theRadius;
		
		_myRadiusParameter  = parameter("radius");
		_myCenterParameter  = parameter("center");
	}
	
	public CCSphereConstraint(
		final CCVector3 theCenter, final float theRadius, 
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		this(theCenter, theRadius, theResilience, theFriction, theMinimalVelocity,false);
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
