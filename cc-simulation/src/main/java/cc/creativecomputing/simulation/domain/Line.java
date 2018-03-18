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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * Domain representing a line given the two end points
 * @author christianr
 *
 */
public class Line extends CCDomain{
	public CCVector3 p0;
	public CCVector3 p1;

	public Line(final CCVector3 i_p0, final CCVector3 i_p1){
		p0 = i_p0;
		p1 = i_p1;
		p1.subtract(p0);
	}

	public CCVector3 generate(){
		final CCVector3 result = p1.clone();
		result.multiplyLocal(CCMath.random());
		result.addLocal(p0);
		return result;
	}
}
