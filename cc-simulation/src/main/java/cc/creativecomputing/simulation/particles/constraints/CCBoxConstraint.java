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

public class CCBoxConstraint extends CCConstraint{
	private String _myMinCornerParameter;
	private String _myMaxCornerParameter;
	
	@CCProperty(name = "min")
	private CCVector3 _myMinCorner;
	@CCProperty(name = "max")
	private CCVector3 _myMaxCorner;
	
	public CCBoxConstraint(
		final CCVector3 theMinCorner, final CCVector3 theMaxCorner, 
		final float theResilience, final float theFriction, final float theMinimalVelocity
	) {
		super("boxConstraint", theResilience, theFriction, theMinimalVelocity);
		
		_myMinCorner = new CCVector3(theMinCorner);
		_myMaxCorner = new CCVector3(theMaxCorner);

		_myMinCornerParameter  = parameter("minCorner");
		_myMaxCornerParameter  = parameter("maxCorner");
	}

	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myMinCornerParameter, _myMinCorner);
		_myShader.uniform3f(_myMaxCornerParameter, _myMaxCorner);
	}
	
	public void minCorner(final CCVector3 theMinCorner) {
		_myMinCorner.set(theMinCorner);
	}
	
	public void maxCorner(final CCVector3 theMaxCorner) {
		_myMaxCorner.set(theMaxCorner);
	}
	
}
