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
	@CCProperty(name = "position", min = -2000, max = 2000, readBack = true)
	private CCVector3 _myPosition;
	@CCProperty(name = "radius", min = 0, max = 3000, readBack = true)
	private double _myRadius;
	@CCProperty(name = "attract")
	private boolean _cAttract = true;
	
	@CCProperty(name = "attractionSpeed", min = 0, max = 10)
	private double _cAttractionSpeed = 5.0f;
	@CCProperty(name = "attractionForce", min = 0, max = 100)
	private double _cAttractionForce = 20.0f;
	@CCProperty(name = "stickDistance", min = 0, max = 30)
	private double _cStickDistance = 0.1f;
	@CCProperty(name = "stickForce", min = 0, max = 100)
	private double _cStickForce = 50.0f;
	
	private String _myPositionParameter;
	private String _myRadiusParameter;
	private String _myDirectionParameter;
	
	private String _myAttractionSpeedParameter;
	private String _myAttractionForceParameter;
	private String _myStickDistanceParameter;
	private String _myStickForceParameter;
	
	public CCAttractor(final CCVector3 thePosition, final double theRadius){
		super("attractor");
		
		_myPositionParameter = parameter("position");
		_myRadiusParameter = parameter("radius");
		_myDirectionParameter = parameter("direction");
		_myAttractionSpeedParameter = parameter("attractionSpeed");
		_myAttractionForceParameter = parameter("attractionForce");
		_myStickDistanceParameter = parameter("stickDistance");
		_myStickForceParameter = parameter("stickForce");
		
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

		_myShader.uniform1f(_myAttractionSpeedParameter, _cAttractionSpeed);
		_myShader.uniform1f(_myAttractionForceParameter, _cAttractionForce);
		_myShader.uniform1f(_myStickDistanceParameter, _cStickDistance);
		_myShader.uniform1f(_myStickForceParameter, _cStickForce);
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
