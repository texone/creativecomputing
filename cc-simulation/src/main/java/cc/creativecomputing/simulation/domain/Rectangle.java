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
 * The point o is a point on the plane. u and v are (non-parallel) 
 * basis vectors in the plane. They don't need to be normal or orthogonal.
 * <br>
 * Generate returns a random point in the diamond-shaped patch whose 
 * corners are o, o+u, o+u+v, and o+v. 
 * Within returns true if the point is in the positive half-space of 
 * the plane (in the plane or on the side that the normal (u cross v) points to).
 * @author christianr
 *
 */
public class Rectangle extends CCTriangle{

	public Rectangle(final CCVector3 i_p0, final CCVector3 i_p1, final CCVector3 i_p2){
		super(i_p0, i_p1, i_p2);
	}

	CCVector3 calculateNonBasicEdge(){
		final CCVector3 f = v.clone();
	   f.addLocal(u);
	   return f;
	}

	public CCVector3 generate(){
		final CCVector3 p = this._myPoint.clone();
		
		final CCVector3 u = this.u.clone();
		u.multiplyLocal(CCMath.random());
		p.addLocal(u);
		
		final CCVector3 v = this.v.clone();
		v.multiplyLocal(CCMath.random());
		p.addLocal(v);
		
		return p;
	}

	public CCVector3 nearestEdge(final CCVector3 i_vector){
		final CCVector3 uofs = uNorm.clone();
      uofs.multiplyLocal(uNorm.dot(i_vector));
      uofs.subtractLocal(i_vector);
      final double udistSqr = uofs.lengthSquared();
      
      final CCVector3 vofs = vNorm.clone();
      vofs.multiplyLocal(vNorm.dot(i_vector));
      vofs.subtractLocal(i_vector);
      final double vdistSqr = vofs.lengthSquared();

      final CCVector3 foffset = u.clone();
      foffset.addLocal(v);
      foffset.subtractLocal(i_vector);
      
      final CCVector3 fofs = uNorm.clone();
      fofs.multiplyLocal(uNorm.dot(foffset));
      fofs.subtractLocal(foffset);
      double fdistSqr = fofs.lengthSquared();
      
      final CCVector3 gofs = vNorm.clone();
      gofs.multiplyLocal(vNorm.dot(foffset));
      gofs.subtractLocal(foffset);
      double gdistSqr = gofs.lengthSquared();

      // S is the safety vector toward the closest point on boundary.
      final CCVector3 result;
      if(udistSqr <= vdistSqr && udistSqr <= fdistSqr && udistSqr <= gdistSqr) result = uofs;
      else if(vdistSqr <= fdistSqr && vdistSqr <= gdistSqr) result = vofs;
      else if(fdistSqr <= gdistSqr) result = fofs;
      else result = gofs;
      return result;
	}
}
