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
 * The point x, y, z is the center of a disc in the plane with normal nx, ny, nz. 
 * The disc has outer radius radius1 and inner radius radius2. The normal will 
 * get normalized, so it need not already be normalized.
 * Generate returns a point inside the disc. 
 * Within returns false.
 * @author christianr
 *
 */
public class Disc extends CCDomain{
	
	public CCVector3 center;
	public CCVector3 normal;
	public CCVector3 u;
	public CCVector3 v;
	
	public double radIn;
	public double radOut;
	
	public double radInSqr; 
	public double radOutSqr;
	public double dif;
	public double d;

	public Disc(
		final CCVector3 i_center, final CCVector3 i_normal, 
		final double i_outerRadius, final double i_innerRadius
	){
		center = i_center;
		normal = i_normal;
		normal.normalize();

		if(i_outerRadius > i_innerRadius) {
			radOut = i_outerRadius; radIn = i_innerRadius;
		} else {
			radOut = i_innerRadius; radIn = i_outerRadius;
		}
		dif = radOut - radIn;
		radInSqr = CCMath.sq(radIn);
		radOutSqr = CCMath.sq(radOut);

		//Find a vector orthogonal to n.
		CCVector3 basis = new CCVector3(1.0f, 0.0f, 0.0f);
		if (Math.abs(basis.dot(normal)) > 0.999f){
			basis = new CCVector3(0.0f, 1.0f, 0.0f);
		}
			
		// Project away N component, normalize and cross to get
		// second orthonormal vector.
		u = basis.clone();
		CCVector3 temp = normal.clone();
		temp.multiplyLocal(basis.dot(normal));
		u.subtractLocal(temp);
		u.normalizeLocal();
		
		v = normal.cross(u);
			
		d = -(center.dot(normal));
	}
	
	public Disc(
		final CCVector3 i_center, final CCVector3 i_normal, 
		final double i_radius
	){
		this(i_center, i_normal,i_radius,0);
	}

	public CCVector3 generate(){
		// Might be faster to generate a point in a square and reject if outside the circle
		double theta = CCMath.random() * 2.0f * Math.PI; // Angle around normal
		// Distance from center
		double r = radIn + CCMath.random() * dif;

		double x = r * Math.cos(theta); // Weighting of each frame vector
		double y = r * Math.sin(theta);

		CCVector3 u = this.u.clone();
		u.multiplyLocal(x);
		CCVector3 v = this.v.clone();
		v.multiplyLocal(y);

		CCVector3 result = center.clone();
		result.addLocal(u);
		result.addLocal(v);
		return result;
	}

}
