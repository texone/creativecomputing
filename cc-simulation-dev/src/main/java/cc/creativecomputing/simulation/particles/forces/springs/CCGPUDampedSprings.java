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
package cc.creativecomputing.simulation.particles.forces.springs;

import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.cg.CGparameter;

/**
 * Adds support of spring forces to the particle system.
 * 
 * @author info
 * 
 */
public class CCGPUDampedSprings extends CCGPUSprings {
	private CGparameter _mySpringDampingParameter;
	private float _mySpringDamping;
	
	public CCGPUDampedSprings(final CCGraphics g, final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		this(g, 4,theSpringConstant, theSpringDamping, theRestLength);
	}

	public CCGPUDampedSprings(final CCGraphics g, final int theNumberOfSprings, final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		super("DampedSprings", g, theNumberOfSprings,theSpringConstant, theRestLength);
		_mySpringDamping = theSpringDamping;
	}

	public void setupParameter(int theWidth, int theHeight) {
		super.setupParameter(theWidth, theHeight);
		_mySpringDampingParameter = parameter("springDamping");
		springDamping(_mySpringDamping);
	}

	public void springDamping(final float theSpringDamping) {
		_myVelocityShader.parameter(_mySpringDampingParameter, theSpringDamping);
	}
}
