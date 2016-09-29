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

public class CCGPUYConstraint extends CCGPUPlaneConstraint{
	
	public CCGPUYConstraint(final float theY, final float theOrientation, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super(new CCPlane3f(new CCVector3f(0,theY,0), new CCVector3f(0,theOrientation,0)), theResilience, theFriction, theMinimalVelocity);
	}
	
	public CCGPUYConstraint(final float theY, final float theResilience, final float theFriction, final float theMinimalVelocity) {
		super(new CCPlane3f(new CCVector3f(0,theY,0), new CCVector3f(0,1,0)), theResilience, theFriction, theMinimalVelocity);
	}
	
	public void y(final float theY) {
		_myPlane.setOriginNormal(new CCVector3f(0,theY,0), _myPlane.normal());
	}
}
