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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector2;

public class CCRingEmitter extends CCEmitter{
	@CCProperty(name = "center", min = -1000, max = 1000)
	private CCVector2 _cCenter = new CCVector2();
	@CCProperty(name = "radius", min = 0, max = 1000)
	private double _cRadius = 200;
	
	private String _myCenterParameter;
	private String _myRadiusParameter;
	
	public CCRingEmitter() {
		super("ring");
		_myCenterParameter = parameter("center");
		_myRadiusParameter = parameter("radius");
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();

		_myShader.uniform2f(_myCenterParameter, _cCenter);
		_myShader.uniform1f(_myRadiusParameter, _cRadius);
	}

}
