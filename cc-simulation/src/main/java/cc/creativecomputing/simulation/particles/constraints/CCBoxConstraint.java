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
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCBoxConstraint extends CCConstraint{
	private String _myMinCornerParameter;
	private String _myMaxCornerParameter;
	
	@CCProperty(name = "min")
	private CCVector3 _myMinCorner;
	@CCProperty(name = "max")
	private CCVector3 _myMaxCorner;
	
	public CCBoxConstraint(final CCVector3 theMinCorner, final CCVector3 theMaxCorner) {
		super("boxConstraint");
		
		_myMinCorner = new CCVector3(theMinCorner);
		_myMaxCorner = new CCVector3(theMaxCorner);

		_myMinCornerParameter  = parameter("minCorner");
		_myMaxCornerParameter  = parameter("maxCorner");
	}

	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myMinCornerParameter, _myMinCorner);
		_myShader.uniform3f(_myMaxCornerParameter, _myMaxCorner);
	}
	
	public void minCorner(final CCVector3 theMinCorner) {
		_myMinCorner.set(theMinCorner);
	}
	
	public void maxCorner(final CCVector3 theMaxCorner) {
		_myMaxCorner.set(theMaxCorner);
	}
	
}
