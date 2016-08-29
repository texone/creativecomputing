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
import cc.creativecomputing.math.CCVector3f;

public class CCGPUZConstraint extends CCGPUPlaneConstraint{
	
	public CCGPUZConstraint(final float theZ, final float theOrientation, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super(new CCPlane3f(new CCVector3f(0,0,theZ), new CCVector3f(0,0,theOrientation)), theResilience, theFriction, theMinimalVelocity);
	}
	
	public CCGPUZConstraint(final float theZ, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super(new CCPlane3f(new CCVector3f(0,0,theZ), new CCVector3f(0,0,1)), theResilience, theFriction, theMinimalVelocity);
	}
	
	public void negate() {
		_myPlane.normal().negate();
	}
	
	public void z(final float theZ) {
		_myPlane.setOriginNormal(new CCVector3f(0,0,theZ), _myPlane.normal());
	}
}
