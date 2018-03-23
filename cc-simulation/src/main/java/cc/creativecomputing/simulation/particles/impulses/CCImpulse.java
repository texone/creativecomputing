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
package cc.creativecomputing.simulation.particles.impulses;


import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.simulation.particles.CCGLProgramInterface;
import cc.creativecomputing.simulation.particles.CCParticlesUpdateShader;

public abstract class CCImpulse extends CCGLProgramInterface{
	
	protected String _myParameterIndex;
	protected String _myShaderTypeName;
	protected CCParticlesUpdateShader _myVelocityShader;
	
	@CCProperty(name = "strength", min = 0, max = 10)
	protected double _cStrength = 1;
	
	private String _myStrengthParameter;
	
	public CCImpulse(final String theShaderTypeName, final double theStrength){
		super(theShaderTypeName);

		_myStrengthParameter  = parameter("strength");
		
		_cStrength = theStrength;
	}
	
	public void strength(final double theStrength) {
		_cStrength = theStrength;
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		if(_myTrigger) {
			_myTrigger = false;
			_myShader.uniform1f(_myStrengthParameter, _cStrength);
		}else {
			_myShader.uniform1f(_myStrengthParameter, 0);
		}
	}
	
	private boolean _myTrigger = false;
	
	public void trigger() {
		_myTrigger = true;
	}
	
	public boolean isTriggering() {
		return _myTrigger;
	}
	
	public void update(final CCAnimator theAnimator) {
		
	}
}
