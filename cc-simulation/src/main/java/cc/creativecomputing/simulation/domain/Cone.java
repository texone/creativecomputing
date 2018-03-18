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
 * The first point is the apex of the cone and the second is the other endpoint
 *  of the axis of the cone. radius1 is the radius of the base of the cone. 
 *  radius2 is the radius of the base of a cone to subtract from the first 
 *  cone to create a conical shell. This is similar to the cylindrical shell, 
 *  which can be thought of as a large cylinder with a smaller cylinder subtracted 
 *  from the middle. 
 *  <br>
 *  Both cones share the same apex and axis, which implies that the thickness of 
 *  the conical shell tapers to 0 at the apex. radius2 = 0 for a solid cone 
 *  with no empty space in the middle.
 *  <br>
 *  Generate returns a random point in the conical shell. 
 *  Within returns true if the point is within the conical shell.
 * @author christianr
 *
 */
public class Cone extends Cylinder{

	public Cone(
		final CCVector3 i_e0, final CCVector3 i_e1, 
		final double i_radOut, final double i_radIn
	){
		super(i_e0,i_e1,i_radOut,i_radIn);
	}

	boolean isWithin(
		final double i_rSqr, final double i_dist
	){
		return (i_rSqr <= CCMath.sq(i_dist * radIn)&& i_rSqr >= CCMath.sq(i_dist * radOut));
	}
	
	double scaleCoord(final double i_coord, final double i_dist){
		return i_coord * i_dist;
	}
}
