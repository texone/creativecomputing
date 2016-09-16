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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCGLProgramInterface;
import cc.creativecomputing.simulation.particles.CCParticles;

public abstract class CCForce extends CCGLProgramInterface{
	
	@CCProperty(name = "strength", min = 0, max = 10)
	protected double _cStrength = 1;
	
	protected String _myStrengthParameter;
	
	protected CCForce(final String theShaderTypeName){
		super(theShaderTypeName);
		_myStrengthParameter = parameter("strength");
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myStrengthParameter, _cStrength);
	}
	
	public void setParticles(CCParticles theParticles){
		
	}

	/**
	 * @param theG
	 * @param theWidth
	 * @param theHeight
	 */
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {}
	
	public void update(final CCAnimator theAnimator) {}
	
	public void reset() {}
	
	/**
	 * Set the strength of the force. The default value is one.
	 * @param theStrength strength value to scale the force
	 */
	public void strength(final float theStrength) {
		_cStrength = theStrength;
	}
}
