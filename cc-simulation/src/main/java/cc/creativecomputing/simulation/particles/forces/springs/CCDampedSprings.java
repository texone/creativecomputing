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
package cc.creativecomputing.simulation.particles.forces.springs;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.forces.CCForce;

/**
 * Adds support of spring forces to the particle system.
 * 
 * @author info
 * 
 */
public class CCDampedSprings extends CCForce {
	private String _mySpringDampingParameter;
	private float _mySpringDamping;
	
	public CCDampedSprings(final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		this(4,theSpringConstant, theSpringDamping, theRestLength);
	}

	public CCDampedSprings(final int theNumberOfSprings, final float theSpringConstant, final float theSpringDamping, final float theRestLength) {
		super("DampedSprings");//, theNumberOfSprings,theSpringConstant, theRestLength
		_mySpringDamping = theSpringDamping;
	}

	@Override
	public void setSize(final CCGraphics g, int theWidth, int theHeight) {
		super.setSize(g, theWidth, theHeight);
		_mySpringDampingParameter = parameter("springDamping");
		springDamping(_mySpringDamping);
	}

	public void springDamping(final float theSpringDamping) {
//		_myVelocityShader.parameter(_mySpringDampingParameter, theSpringDamping);
	}
}
