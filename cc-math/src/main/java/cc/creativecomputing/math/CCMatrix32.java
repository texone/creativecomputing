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
package cc.creativecomputing.math;

import java.nio.FloatBuffer;

/**
 * 3x2 affine matrix implementation.
 */
public class CCMatrix32 {

	public double m00, m01, m02;
	public double m10, m11, m12;

	/**
	 * Creates a new identy matrix
	 */
	public CCMatrix32() {
		reset();
	}

	/**
	 * Creates a new matrix
	 * @param m00
	 * @param m01
	 * @param m02
	 * @param m10
	 * @param m11
	 * @param m12
	 */
	public CCMatrix32(
		double m00, double m01, double m02, 
		double m10, double m11, double m12
	) {
		set(m00, m01, m02, m10, m11, m12);
	}
	
	public CCMatrix32(
		FloatBuffer theBuffer
	) {
		m00 = theBuffer.get();
		m01 = theBuffer.get();
		m02 = theBuffer.get();

		m10 = theBuffer.get();
		m11 = theBuffer.get();
		m12 = theBuffer.get();
	}

	/**
	 * Creates a matrix by copying the given matrix
	 * @param theMatrix
	 */
	public CCMatrix32(final CCMatrix32 theMatrix) {
		set(theMatrix);
	}

	/**
	 * Sets this matrix to the identity matrix
	 */
	public void reset() {
		set(
			1, 0, 0, 
			0, 1, 0
		);
	}

	/**
	 * Returns a copy of this matrix.
	 */
	public CCMatrix32 clone() {
		CCMatrix32 outgoing = new CCMatrix32();
		outgoing.set(this);
		return outgoing;
	}

	/**
	 * Copies the matrix contents into a 6 entry double array. 
	 * If target is null (or not the correct size), a new array will be created.
	 *
	 * @param theTarget the array to fill with the data
	 * @return 
	 */
	public double[] get(double[] theTarget) {
		if ((theTarget == null) || (theTarget.length != 6)) {
			theTarget = new double[6];
		}
		theTarget[0] = m00;
		theTarget[1] = m01;
		theTarget[2] = m02;

		theTarget[3] = m10;
		theTarget[4] = m11;
		theTarget[5] = m12;

		return theTarget;
	}

	/**
	 * Sets this matrix to the given matrix
	 * @param theMatrix matrix with new values
	 */
	public void set(final CCMatrix32 theMatrix) {
		set(theMatrix.m00, theMatrix.m01, theMatrix.m02, theMatrix.m10, theMatrix.m11, theMatrix.m12);
	}

	/**
	 * Sets this matrix from the values of the given array.
	 * The given array needs to contain six values. The
	 * first 3 values are taken as first row of the matrix, 
	 * the second 3 values as second row.
	 * @param theSource new values
	 */
	public void set(double[] theSource) {
		m00 = theSource[0];
		m01 = theSource[1];
		m02 = theSource[2];

		m10 = theSource[3];
		m11 = theSource[4];
		m12 = theSource[5];
	}

	/**
	 * Sets this matrix to the given values
	 * @param m00 1. row 1. column
	 * @param m01 1. row 2. column
	 * @param m02 1. row 3. column
	 * @param m10 2. row 1. column
	 * @param m11 2. row 2. column
	 * @param m12 2. row 3. column
	 */
	public void set(
		double m00, double m01, double m02, 
		double m10, double m11, double m12
	) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
	}

	/**
	 * Applies a translation to this matrix
	 * @param theX translation in x direction
	 * @param theY translation in y direction
	 */
	public void translate(final double theX, final double theY) {
		m02 = theX * m00 + theY * m01 + m02;
		m12 = theX * m10 + theY * m11 + m12;
	}
	
	/**
	 * Applies a translation to this matrix
	 * @param theVector translation vector
	 */
	public void translate(final CCVector2 theVector) {
		translate(theVector.x, theVector.y);
	}
	
	/**
	 * Applies a rotation to this matrix
	 * @param theAngle angle of the applied rotation
	 */
	public void rotate(final double theAngle) {
		double s = CCMath.sin(theAngle);
		double c = CCMath.cos(theAngle);

		double temp1 = m00;
		double temp2 = m01;
		m00 = c * temp1 + s * temp2;
		m01 = -s * temp1 + c * temp2;
		temp1 = m10;
		temp2 = m11;
		m10 = c * temp1 + s * temp2;
		m11 = -s * temp1 + c * temp2;
	}

	/**
	 * Applies a scale to this matrix
	 * @param theScale applied scale
	 */
	public void scale(final double theScale) {
		scale(theScale, theScale);
	}

	/**
	 * Applies a scale in x and y direction to this matrix
	 * @param theScaleX
	 * @param theScaleY
	 */
	public void scale(final double theScaleX, final double theScaleY) {
		m00 *= theScaleX;
		m01 *= theScaleY;
		m10 *= theScaleX;
		m11 *= theScaleY;
	}

	public void skewX(double angle) {
		apply(1, 0, 1, angle, 0, 0);
	}

	public void skewY(double angle) {
		apply(1, 0, 1, 0, angle, 0);
	}

	/**
	 * Applies the transformation of the given matrix
	 * @param source
	 */
	public void apply(CCMatrix32 source) {
		apply(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12);
	}

	public void apply(
		double n00, double n01, double n02, 
		double n10, double n11, double n12
	) {
		double t0 = m00;
		double t1 = m01;
		m00 = n00 * t0 + n10 * t1;
		m01 = n01 * t0 + n11 * t1;
		m02 += n02 * t0 + n12 * t1;

		t0 = m10;
		t1 = m11;
		m10 = n00 * t0 + n10 * t1;
		m11 = n01 * t0 + n11 * t1;
		m12 += n02 * t0 + n12 * t1;
	}

	/**
	 * Apply another matrix to the left of this one.
	 */
	public void preApply(CCMatrix32 left) {
		preApply(left.m00, left.m01, left.m02, left.m10, left.m11, left.m12);
	}

	public void preApply(double n00, double n01, double n02, double n10, double n11, double n12) {
		double t0 = m02;
		double t1 = m12;
		n02 += t0 * n00 + t1 * n01;
		n12 += t0 * n10 + t1 * n11;

		m02 = n02;
		m12 = n12;

		t0 = m00;
		t1 = m10;
		m00 = t0 * n00 + t1 * n01;
		m10 = t0 * n10 + t1 * n11;

		t0 = m01;
		t1 = m11;
		m01 = t0 * n00 + t1 * n01;
		m11 = t0 * n10 + t1 * n11;
	}
	
	/**
	 * Multiplies the given source vector against this matrix
	 * @param theSource the vector to multiply with this matrix
	 * @param theTarget if this vector is not null the result will be stored in this vector
	 * @return the result of the multiplication
	 */
	public CCVector2 transform(final CCVector2 theSource, CCVector2 theTarget) {
		if (theTarget == null) {
			theTarget = new CCVector2();
		}

		theTarget.x = m00 * theSource.x + m01 * theSource.y + m02;
		theTarget.y = m10 * theSource.x + m11 * theSource.y + m12;
		return theTarget;
	}
	
	/**
	 * Multiplies the given source vector against this matrix
	 * @param theSource the vector to multiply with this matrix
	 * @return the result of the multiplication
	 */
	public CCVector2 transform(CCVector2 theSource) {
		return transform(theSource, null);
	}

	/**
	 * Multiply a two element vector against this matrix. If out is null or not length four, a new double array will be
	 * returned. The values for vec and out can be the same (though that's less efficient).
	 */
	public double[] mult(double vec[], double out[]) {
		if (out == null || out.length != 2) {
			out = new double[2];
		}

		if (vec == out) {
			double tx = m00 * vec[0] + m01 * vec[1] + m02;
			double ty = m10 * vec[0] + m11 * vec[1] + m12;

			out[0] = tx;
			out[1] = ty;

		} else {
			out[0] = m00 * vec[0] + m01 * vec[1] + m02;
			out[1] = m10 * vec[0] + m11 * vec[1] + m12;
		}

		return out;
	}

	public double multX(double x, double y) {
		return m00 * x + m01 * y + m02;
	}

	public double multY(double x, double y) {
		return m10 * x + m11 * y + m12;
	}

	/**
	 * Transpose this matrix.
	 */
	public void transpose() {}

	/**
	 * Invert this matrix. Implementation stolen from OpenJDK.
	 * 
	 * @return true if successful
	 */
	public boolean invert() {
		double determinant = determinant();
		if (Math.abs(determinant) <= Float.MIN_VALUE) {
			return false;
		}

		double t00 = m00;
		double t01 = m01;
		double t02 = m02;
		double t10 = m10;
		double t11 = m11;
		double t12 = m12;

		m00 = t11 / determinant;
		m10 = -t10 / determinant;
		m01 = -t01 / determinant;
		m11 = t00 / determinant;
		m02 = (t01 * t12 - t11 * t02) / determinant;
		m12 = (t10 * t02 - t00 * t12) / determinant;

		return true;
	}
	
	public CCMatrix32 inverse() {
		CCMatrix32 myResult = clone();
		myResult.invert();
		return myResult;
	}

	/**
	 * @return the determinant of the matrix
	 */
	public double determinant() {
		return m00 * m11 - m01 * m10;
	}

	// ////////////////////////////////////////////////////////////

	// TODO these need to be added as regular API, but the naming and
	// implementation needs to be improved first. (e.g. actually keeping track
	// of whether the matrix is in fact identity internally.)

	protected boolean isIdentity() {
		return ((m00 == 1) && (m01 == 0) && (m02 == 0) && (m10 == 0) && (m11 == 1) && (m12 == 0));
	}

	// TODO make this more efficient, or move into PMatrix2D
	protected boolean isWarped() {
		return ((m00 != 1) || (m01 != 0) && (m10 != 0) || (m11 != 1));
	}
}
