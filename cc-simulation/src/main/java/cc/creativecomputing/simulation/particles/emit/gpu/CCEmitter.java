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
package cc.creativecomputing.simulation.particles.emit.gpu;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCGLProgramInterface;
import cc.creativecomputing.simulation.particles.CCParticles;

public class CCEmitter extends CCGLProgramInterface{
	@CCProperty(name = "propability", min = 0, max = 1)
	protected double _cPropability = 0.001;
	
	protected String _myPropabilityParameter;
	
	protected CCEmitter(final String theShaderTypeName){
		super(theShaderTypeName);
		_myPropabilityParameter = parameter("propability");
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myPropabilityParameter, _cPropability);
	}
	
	public void setParticles(CCParticles theParticles){
		
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
	}

	/**
	 * @param theG
	 * @param theWidth
	 * @param theHeight
	 */
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {}
	
	public void update(final CCAnimator theAnimator) {}
	
	public void reset(CCGraphics g) {}
	
	/**
	 * Set the emit propability . The default value is one.
	 * @param thePropability thePropability 
	 */
	public void propability(final double thePropability) {
		_cPropability = thePropability;
	}
}
