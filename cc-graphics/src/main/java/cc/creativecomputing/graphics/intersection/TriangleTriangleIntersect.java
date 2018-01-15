/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.intersection;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class TriangleTriangleIntersect {

	/**
	 * EPSILON represents the error buffer used to denote a hit.
	 */
	public static final double EPSILON = 1e-12f;

	/**
	 * This method tests for the intersection between two triangles defined by
	 * their vertices. Converted to java from C code found at
	 * http://jgt.akpeters.com/papers/Moller97/
	 * 
	 * @param v0
	 *            First triangle's first vertex.
	 * @param v1
	 *            First triangle's second vertex.
	 * @param v2
	 *            First triangle's third vertex.
	 * @param u0
	 *            Second triangle's first vertex.
	 * @param u1
	 *            Second triangle's second vertex.
	 * @param u2
	 *            Second triangle's third vertex.
	 * @return True if the two triangles intersect, false otherwise.
	 */
	public static boolean intersectTriTri(final CCVector3 v0, final CCVector3 v1, final CCVector3 v2, final CCVector3 u0,
			final CCVector3 u1, final CCVector3 u2) {
		final CCVector3 e1 = new CCVector3();
		final CCVector3 e2 = new CCVector3();
		final CCVector3 n1 = new CCVector3();
		final CCVector3 n2 = new CCVector3();
		final CCVector3 d = new CCVector3();
		try {

			double d1, d2;
			double du0, du1, du2, dv0, dv1, dv2;
			final double[] isect1 = new double[2];
			final double[] isect2 = new double[2];
			double du0du1, du0du2, dv0dv1, dv0dv2;
			short index;
			double vp0, vp1, vp2;
			double up0, up1, up2;
			double bb, cc, max;
			double xx, yy, xxyy, tmp;

			/* compute plane equation of triangle(v0,v1,v2) */
			v1.subtract(v0, e1);
			v2.subtract(v0, e2);
			e1.cross(e2, n1);
			d1 = -n1.dot(v0);
			/* plane equation 1: n1.X+d1=0 */

			/*
			 * put u0,u1,u2 into plane equation 1 to compute signed distances to
			 * the plane
			 */
			du0 = n1.dot(u0) + d1;
			du1 = n1.dot(u1) + d1;
			du2 = n1.dot(u2) + d1;

			/* coplanarity robustness check */
			if (CCMath.abs(du0) < EPSILON) {
				du0 = 0.0f;
			}
			if (CCMath.abs(du1) < EPSILON) {
				du1 = 0.0f;
			}
			if (CCMath.abs(du2) < EPSILON) {
				du2 = 0.0f;
			}
			du0du1 = du0 * du1;
			du0du2 = du0 * du2;

			if (du0du1 > 0.0f && du0du2 > 0.0f) {
				return false;
			}

			/* compute plane of triangle (u0,u1,u2) */
			u1.subtract(u0, e1);
			u2.subtract(u0, e2);
			e1.cross(e2, n2);
			d2 = -n2.dot(u0);
			/* plane equation 2: n2.X+d2=0 */

			/* put v0,v1,v2 into plane equation 2 */
			dv0 = n2.dot(v0) + d2;
			dv1 = n2.dot(v1) + d2;
			dv2 = n2.dot(v2) + d2;

			if (CCMath.abs(dv0) < EPSILON) {
				dv0 = 0.0f;
			}
			if (CCMath.abs(dv1) < EPSILON) {
				dv1 = 0.0f;
			}
			if (CCMath.abs(dv2) < EPSILON) {
				dv2 = 0.0f;
			}

			dv0dv1 = dv0 * dv1;
			dv0dv2 = dv0 * dv2;

			if (dv0dv1 > 0.0f && dv0dv2 > 0.0f) { /*
												 * same sign on all of them +
												 * not equal 0 ?
												 */
				return false; /* no intersection occurs */
			}

			/* compute direction of intersection line */
			n1.cross(n2, d);

			/* compute and index to the largest component of d */
			max = CCMath.abs(d.x);
			index = 0;
			bb = CCMath.abs(d.y);
			cc = CCMath.abs(d.z);
			if (bb > max) {
				max = bb;
				index = 1;
			}
			if (cc > max) {
				vp0 = v0.z;
				vp1 = v1.z;
				vp2 = v2.z;

				up0 = u0.z;
				up1 = u1.z;
				up2 = u2.z;

			} else if (index == 1) {
				vp0 = v0.y;
				vp1 = v1.y;
				vp2 = v2.y;

				up0 = u0.y;
				up1 = u1.y;
				up2 = u2.y;
			} else {
				vp0 = v0.x;
				vp1 = v1.x;
				vp2 = v2.x;

				up0 = u0.x;
				up1 = u1.x;
				up2 = u2.x;
			}

			/* compute interval for triangle 1 */
			{
				final CCVector3 abc = new CCVector3();
				final CCVector2 x0x1 = new CCVector2();
				if (newComputeIntervals(vp0, vp1, vp2, dv0, dv1, dv2, dv0dv1, dv0dv2, abc, x0x1)) {
					return coplanarTriTri(n1, v0, v1, v2, u0, u1, u2);
				}

				/* compute interval for triangle 2 */
				final CCVector3 def = new CCVector3();
				final CCVector2 y0y1 = new CCVector2();
				if (newComputeIntervals(up0, up1, up2, du0, du1, du2, du0du1, du0du2, def, y0y1)) {
					return coplanarTriTri(n1, v0, v1, v2, u0, u1, u2);
				}

				xx = x0x1.x * x0x1.y;
				yy = y0y1.x * y0y1.y;
				xxyy = xx * yy;

				tmp = abc.x * xxyy;
				isect1[0] = tmp + abc.y * x0x1.y * yy;
				isect1[1] = tmp + abc.z * x0x1.x * yy;

				tmp = def.x * xxyy;
				isect2[0] = tmp + def.y * xx * y0y1.y;
				isect2[1] = tmp + def.z * xx * y0y1.x;

				sort(isect1);
				sort(isect2);
			}
            return !(isect1[1] < isect2[0]) && !(isect2[1] < isect1[0]);
        } finally {
		}
	}

	private static void sort(final double[] f) {
		if (f[0] > f[1]) {
			final double c = f[0];
			f[0] = f[1];
			f[1] = c;
		}
	}

	private static boolean newComputeIntervals(final double vv0, final double vv1, final double vv2, final double d0, final double d1,
			final double d2, final double d0d1, final double d0d2, final CCVector3 abc, final CCVector2 x0x1) {
		if (d0d1 > 0.0f) {
			/* here we know that d0d2 <=0.0 */
			/*
			 * that is d0, d1 are on the same side, d2 on the other or on the
			 * plane
			 */
			abc.x = vv2;
			abc.y = (vv0 - vv2) * d2;
			abc.z = (vv1 - vv2) * d2;
			x0x1.x = d2 - d0;
			x0x1.y = d2 - d1;
		} else if (d0d2 > 0.0f) {
			/* here we know that d0d1 <=0.0 */
			abc.x = vv1;
			abc.y = (vv0 - vv1) * d1;
			abc.z = (vv2 - vv1) * d1;
			x0x1.x = d1 - d0;
			x0x1.y = d1 - d2;
		} else if (d1 * d2 > 0.0f || d0 != 0.0f) {
			/* here we know that d0d1 <=0.0 or that d0!=0.0 */
			abc.x = vv0;
			abc.y = (vv1 - vv0) * d0;
			abc.z = (vv2 - vv0) * d0;
			x0x1.x = d0 - d1;
			x0x1.y = d0 - d2;
		} else if (d1 != 0.0f) {
			abc.x = vv1;
			abc.y = (vv0 - vv1) * d1;
			abc.z = (vv2 - vv1) * d1;
			x0x1.x = d1 - d0;
			x0x1.y = d1 - d2;
		} else if (d2 != 0.0f) {
			abc.x = vv2;
			abc.y = (vv0 - vv2) * d2;
			abc.z = (vv1 - vv2) * d2;
			x0x1.x = d2 - d0;
			x0x1.y = d2 - d1;
		} else {
			/* triangles are coplanar */
			return true;
		}
		return false;
	}

	private static boolean coplanarTriTri(final CCVector3 n, final CCVector3 v0, final CCVector3 v1, final CCVector3 v2,
			final CCVector3 u0, final CCVector3 u1, final CCVector3 u2) {
		final CCVector3 a = new CCVector3();
		short i0, i1;
		a.x = CCMath.abs(n.x);
		a.y = CCMath.abs(n.y);
		a.z = CCMath.abs(n.z);

		if (a.x > a.y) {
			if (a.x > a.z) {
				i0 = 1; /* a[0] is greatest */
				i1 = 2;
			} else {
				i0 = 0; /* a[2] is greatest */
				i1 = 1;
			}
		} else /* a[0] <=a[1] */{
			if (a.z > a.y) {
				i0 = 0; /* a[2] is greatest */
				i1 = 1;
			} else {
				i0 = 0; /* a[1] is greatest */
				i1 = 2;
			}
		}

		/* test all edges of triangle 1 against the edges of triangle 2 */
		final double[] v0f = new double[3];
		v0.toArray(v0f);
		final double[] v1f = new double[3];
		v1.toArray(v1f);
		final double[] v2f = new double[3];
		v2.toArray(v2f);
		final double[] u0f = new double[3];
		u0.toArray(u0f);
		final double[] u1f = new double[3];
		u1.toArray(u1f);
		final double[] u2f = new double[3];
		u2.toArray(u2f);
		if (edgeAgainstTriEdges(v0f, v1f, u0f, u1f, u2f, i0, i1)) {
			return true;
		}

		if (edgeAgainstTriEdges(v1f, v2f, u0f, u1f, u2f, i0, i1)) {
			return true;
		}

		if (edgeAgainstTriEdges(v2f, v0f, u0f, u1f, u2f, i0, i1)) {
			return true;
		}

		/* finally, test if tri1 is totally contained in tri2 or vice versa */
		pointInTri(v0f, u0f, u1f, u2f, i0, i1);
		pointInTri(u0f, v0f, v1f, v2f, i0, i1);

		return false;
	}

	private static boolean pointInTri(final double[] V0, final double[] U0, final double[] U1, final double[] U2, final int i0, final int i1) {
		double a, b, c, d0, d1, d2;
		/* is T1 completly inside T2? */
		/* check if V0 is inside tri(U0,U1,U2) */
		a = U1[i1] - U0[i1];
		b = -(U1[i0] - U0[i0]);
		c = -a * U0[i0] - b * U0[i1];
		d0 = a * V0[i0] + b * V0[i1] + c;

		a = U2[i1] - U1[i1];
		b = -(U2[i0] - U1[i0]);
		c = -a * U1[i0] - b * U1[i1];
		d1 = a * V0[i0] + b * V0[i1] + c;

		a = U0[i1] - U2[i1];
		b = -(U0[i0] - U2[i0]);
		c = -a * U2[i0] - b * U2[i1];
		d2 = a * V0[i0] + b * V0[i1] + c;
        return d0 * d1 > 0.0 && d0 * d2 > 0.0;

    }

	private static boolean edgeAgainstTriEdges(final double[] v0, final double[] v1, final double[] u0, final double[] u1, final double[] u2,
			final int i0, final int i1) {
		double aX, aY;
		aX = v1[i0] - v0[i0];
		aY = v1[i1] - v0[i1];
		/* test edge u0,u1 against v0,v1 */
		if (edgeEdgeTest(v0, u0, u1, i0, i1, aX, aY)) {
			return true;
		}
		/* test edge u1,u2 against v0,v1 */
		if (edgeEdgeTest(v0, u1, u2, i0, i1, aX, aY)) {
			return true;
		}
		/* test edge u2,u1 against v0,v1 */
        return edgeEdgeTest(v0, u2, u0, i0, i1, aX, aY);
    }

	private static boolean edgeEdgeTest(final double[] v0, final double[] u0, final double[] u1, final int i0, final int i1, final double aX,
			final double Ay) {
		final double Bx = u0[i0] - u1[i0];
		final double By = u0[i1] - u1[i1];
		final double Cx = v0[i0] - u0[i0];
		final double Cy = v0[i1] - u0[i1];
		final double f = Ay * Bx - aX * By;
		final double d = By * Cx - Bx * Cy;
		if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
			final double e = aX * Cy - Ay * Cx;
			if (f > 0) {
                return e >= 0 && e <= f;
			} else {
                return e <= 0 && e >= f;
			}
		}
		return false;
	}

}
