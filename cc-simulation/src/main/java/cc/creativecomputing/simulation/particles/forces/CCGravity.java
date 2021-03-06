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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCGravity extends CCForce{
	private String _myDirectionParameter;
	private String _myRandomAmountParameter;
	
	@CCProperty(name = "direction", min = -1, max = 1)
	private CCVector3 _cDirection;
	@CCProperty(name = "random amount", min = 0, max = 1)
	private CCVector3 _cRandomAmount;
	
	public CCGravity(final CCVector3 theGravity) {
		super("gravity");
		_cDirection = new CCVector3(theGravity);
		_cRandomAmount = new CCVector3();
		_myDirectionParameter  = parameter("direction");
		_myRandomAmountParameter  = parameter("randomAmount");
	}
	
	public CCGravity() {
		this(new CCVector3());
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myDirectionParameter, _cDirection);
		_myShader.uniform3f(_myRandomAmountParameter, _cRandomAmount);
	}
	
	public CCVector3 direction() {
		return _cDirection;
	}
}
