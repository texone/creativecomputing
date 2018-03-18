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


public class CCAttractor extends CCForce{
	@CCProperty(name = "position", min = -1000, max = 1000, readBack = true)
	private CCVector3 _myPosition;
	@CCProperty(name = "radius", min = 0, max = 1000, readBack = true)
	private double _myRadius;
	@CCProperty(name = "attract")
	private boolean _cAttract = true;
	
	private String _myPositionParameter;
	private String _myRadiusParameter;
	private String _myDirectionParameter;
	
	public CCAttractor(final CCVector3 thePosition, final double theRadius){
		super("attractor");
		
		_myPositionParameter = parameter("position");
		_myRadiusParameter = parameter("radius");
		_myDirectionParameter = parameter("direction");
		
		_myPosition = new CCVector3(thePosition);
		_myRadius = theRadius;
	}
	
	public CCAttractor(){
		this(new CCVector3(), 100);
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myPositionParameter, _myPosition);
		_myShader.uniform1f(_myRadiusParameter, _myRadius);
		_myShader.uniform1f(_myDirectionParameter, _cAttract ? 1 : -1);
	}
	
	public void position(final CCVector3 thePosition){
		_myPosition.set(thePosition);
	}
	
	public CCVector3 position(){
		return _myPosition;
	}
	
	public void radius(final double theRadius){
		_myRadius = theRadius;
	}
	
	public double radius(){
		return _myRadius;
	}

}
