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
 * These are the vertices of a triangle. The triangle can be used to define 
 * an arbitrary geometric model for particles to bounce off, or generate 
 * particles on its surface (and explode them), etc.
 * <br>
 * Generate returns a random point in the triangle. Within always returns 
 * false. [This must eventually change so we can sink particles that
 *  enter/exit a model. Suggestions?]
 * @author christianr
 *
 */
public class CCTriangle extends CCPlaneDomain {

	public CCVector3 u;
	public CCVector3 v;
	// f is the third (non-basis) triangle edge.
	final CCVector3 f;

	public CCVector3 uNorm;
	public CCVector3 vNorm;
	final CCVector3 fNorm;

	//needed for avoid and bounce
	public double uLen;
	public double vLen;

	public CCTriangle(
		final CCVector3 i_p0, 
		final CCVector3 i_p1,
		final CCVector3 i_p2
	){
		super(i_p0);

		u = i_p1;
		u.subtractLocal(_myPoint);

		v = i_p2;
		v.subtractLocal(_myPoint);

		f = calculateNonBasicEdge();

		uLen = u.length();
		uNorm = u.clone();
		uNorm.multiplyLocal(1 / uLen);

		fNorm = f.clone();
		fNorm.normalizeLocal();

		vLen = v.length();
		vNorm = v.clone();
		vNorm.multiplyLocal(1 / vLen);

		_myNormal = uNorm.cross(vNorm);
		_myNormal.normalizeLocal();

		d = -_myPoint.dot(_myNormal);
	}

	CCVector3 calculateNonBasicEdge() {
		final CCVector3 f = v.clone();
		f.subtractLocal(u);
		return f;
	}

	public CCVector3 generate() {
		final double r1 = CCMath.random();
		final double r2 = CCMath.random();

		final CCVector3 p = this._myPoint.clone();
		final CCVector3 u = this.u.clone();
		final CCVector3 v = this.v.clone();

		if (r1 + r2 < 1.0f) {
			u.multiplyLocal(r1);
			v.multiplyLocal(r2);
		} else {
			u.multiplyLocal(1f - r1);
			v.multiplyLocal(1f - r2);
		}

		p.addLocal(u);
		p.addLocal(v);

		return p;
	}

	public CCVector3 nearestEdge(final CCVector3 i_vector) {
		final CCVector3 uofs = uNorm.clone();
		uofs.multiplyLocal(uNorm.dot(i_vector));
		uofs.subtractLocal(i_vector);
		final double udistSqr = uofs.lengthSquared();

		final CCVector3 vofs = vNorm.clone();
		vofs.multiplyLocal(vNorm.dot(i_vector));
		vofs.subtractLocal(i_vector);
		final double vdistSqr = vofs.lengthSquared();

		final CCVector3 foffset = i_vector.clone();
		foffset.subtractLocal(u);

		final CCVector3 fofs = fNorm.clone();
		fofs.multiplyLocal(fNorm.dot(foffset));
		fofs.subtractLocal(foffset);
		double fdistSqr = fofs.lengthSquared();

		// S is the safety vector toward the closest point on boundary.
		final CCVector3 result;
		if (udistSqr <= vdistSqr & udistSqr <= fdistSqr)
			result = uofs;
		else if (vdistSqr <= fdistSqr)
			result = vofs;
		else
			result = fofs;
		return result;
	}
}
