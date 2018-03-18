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
 * The two points are the endpoints of the axis of the cylinder. radius1 is the 
 * outer radius, and radius2 is the inner radius for a cylindrical shell. 
 * radius2 = 0 for a solid cylinder with no empty space in the middle.
 * <br>
 * Generate returns a random point in the cylindrical shell. 
 * Within returns true if the point is within the cylindrical shell.
 * @author christianr
 *
 */
public class Cylinder extends CCDomain{
	/**
	 * One end of the cylinder
	 */
	public CCVector3 apex;
	
	/**
	 * Vector from one end to the other
	 */
	public CCVector3 axis;
	
	public CCVector3 u;
	public CCVector3 v; // Apex is one end. Axis is vector from one end to the other.
	
	public double len, radOut, radIn, radOutSqr, radInSqr, radDif, axisLenInvSqr;
	public boolean thinShell;

	public Cylinder(
		final CCVector3 e0, final CCVector3 e1, 
		final double radOut0, final double radIn0
	){
		apex = e0;
		axis = e1;
		axis.subtractLocal(e0);

		if(radOut0 < radIn0) {
			radOut = radIn0;
			radIn = radOut0;
		} else {
			radOut = radOut0;
			radIn = radIn0;
		}
			
		radOutSqr = radOut * radOut;
		radInSqr = radIn * radIn;

		thinShell = (radIn == radOut);
		radDif = radOut - radIn;

		// Given an arbitrary nonzero vector n, make two orthonormal
		// vectors u and v forming a frame [u,v,n.normalizeLocal()].
		CCVector3 n = axis.clone();
		double axisLenSqr = axis.lengthSquared();
		
		if(axisLenSqr == 0){
			axisLenInvSqr = 1f / axisLenSqr;
		}else{
			axisLenInvSqr = 0f;
		}
		
		n.multiplyLocal(Math.sqrt(axisLenInvSqr));

		// Find a vector orthogonal to n.
		CCVector3 basis = new CCVector3(1.0f, 0.0f, 0.0f);
		if (Math.abs(basis.dot(n)) > 0.999f){
			basis = new CCVector3(0.0f, 1.0f, 0.0f);
		}

		// Project away N component, normalizeLocal and cross to get
		// second orthonormal vector.
		u = basis.clone();
		CCVector3 temp = n.clone();
		temp.multiplyLocal(basis.dot(n));
		u.subtractLocal(temp);
		u.normalizeLocal();
		
		v = n.cross(u);
	}
	
	boolean isWithin(
		final double i_rSqr, final double i_dist
	){
		return (i_rSqr <= CCMath.sq(radIn) && i_rSqr >= CCMath.sq(radOut));
	}

	public boolean isWithin(final CCVector3 pos){
			// This is painful and slow. Might be better to do quick accept/reject tests.
			// Axis is vector from base to tip of the cylinder.
			// x is vector from base to pos.
			//         x . axis
			// dist = ---------- = projected distance of x along the axis
			//        axis. axis   ranging from 0 (base) to 1 (tip)
			//
			// rad = x - dist * axis = projected vector of x along the base

			CCVector3 x = pos.clone();
			x.subtractLocal(apex);

			// Check axial distance
			double dist = axis.dot(x) * axisLenInvSqr;
			if(dist < 0.0f || dist > 1.0f){
				return false;
			}

			// Check radial distance
			CCVector3 xrad = x.clone();
			CCVector3 temp = axis.clone();
			temp.multiplyLocal(dist);
			xrad.subtractLocal(temp);
			double rSqr = xrad.lengthSquared();
			return isWithin(rSqr,dist);
	}
	
	double scaleCoord(final double i_coord, final double i_dist){
		return i_coord;
	}

	public CCVector3 generate(){
		double dist = CCMath.random(); // Distance between base and tip
		double theta = CCMath.random() * 2.0f * Math.PI; // Angle around axis
		
		// Distance from axis
		double r = radIn + CCMath.random() * radDif;

		// Another way to do this is to choose a random point in a square and keep it if it's in the circle.
		double x = scaleCoord(r * Math.cos(theta),dist);
		double y = scaleCoord(r * Math.sin(theta),dist);
		
		CCVector3 axis = this.axis.clone();
		axis.multiplyLocal(dist);
		CCVector3 u = this.u.clone();
		u.multiplyLocal(x);
		CCVector3 v = this.v.clone();
		v.multiplyLocal(y);
			
		CCVector3 pos = apex.clone();
		pos.addLocal(axis);
		pos.addLocal(u);
		pos.addLocal(v);
		return pos;
	}
}
