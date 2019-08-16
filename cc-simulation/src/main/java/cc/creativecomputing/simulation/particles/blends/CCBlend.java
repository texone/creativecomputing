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
package cc.creativecomputing.simulation.particles.blends;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCGLProgramInterface;
import cc.creativecomputing.simulation.particles.CCParticles;

public abstract class CCBlend extends CCGLProgramInterface{
	@CCProperty(name = "amount", min = 0, max = 1)
	protected double _cAmount = 1;

	protected String _myAmountParameter;
	
	protected CCBlend(final String theShaderTypeName){
		super(theShaderTypeName);
		_myAmountParameter = parameter("amount");
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myAmountParameter, _cAmount);
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
	
	public void reset(CCGraphics g) {}
	
	/**
	 * Set the strength of the force. The default value is one.
	 * @param theStrength strength value to scale the force
	 */
	public void amount(final double theAmount) {
		_cAmount = theAmount;
	}
}
