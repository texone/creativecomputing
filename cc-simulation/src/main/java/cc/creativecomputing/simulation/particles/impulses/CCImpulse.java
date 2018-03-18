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
package cc.creativecomputing.simulation.particles.impulses;


import cc.creativecomputing.app.modules.CCAnimator;
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
