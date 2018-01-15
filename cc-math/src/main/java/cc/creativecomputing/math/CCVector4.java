/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Vector4 represents a point or vector in a four dimensional system. This
 * implementation stores its data in double-precision.
 */
public class CCVector4 implements Cloneable, Externalizable {

	private static final long serialVersionUID = 1L;

	/**
	 * 0, 0, 0, 0
	 */
	public final static CCVector4 ZERO = new CCVector4(0, 0, 0, 0);

	/**
	 * 1, 1, 1, 1
	 */
	public final static CCVector4 ONE = new CCVector4(1, 1, 1, 1);

	/**
	 * -1, -1, -1, -1
	 */
	public final static CCVector4 NEG_ONE = new CCVector4(-1, -1, -1, -1);

	/**
	 * 1, 0, 0, 0
	 */
	public final static CCVector4 UNIT_X = new CCVector4(1, 0, 0, 0);

	/**
	 * -1, 0, 0, 0
	 */
	public final static CCVector4 NEG_UNIT_X = new CCVector4(-1, 0, 0, 0);

	/**
	 * 0, 1, 0, 0
	 */
	public final static CCVector4 UNIT_Y = new CCVector4(0, 1, 0, 0);

	/**
	 * 0, -1, 0, 0
	 */
	public final static CCVector4 NEG_UNIT_Y = new CCVector4(0, -1, 0, 0);

	/**
	 * 0, 0, 1, 0
	 */
	public final static CCVector4 UNIT_Z = new CCVector4(0, 0, 1, 0);

	/**
	 * 0, 0, -1, 0
	 */
	public final static CCVector4 NEG_UNIT_Z = new CCVector4(0, 0, -1, 0);

	/**
	 * 0, 0, 0, 1
	 */
	public final static CCVector4 UNIT_W = new CCVector4(0, 0, 0, 1);

	/**
	 * 0, 0, 0, -1
	 */
	public final static CCVector4 NEG_UNIT_W = new CCVector4(0, 0, 0, -1);

	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double w = 0;

	/**
	 * Constructs a new vector set to (x, y, z, w).
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 */
	public CCVector4(final double theX, final double theY, final double theZ, final double theW) {
		x = theX;
		y = theY;
		z = theZ;
		w = theW;
	}

	/**
	 * Constructs a new vector set to the (x, y, z, w) values of the given
	 * source vector.
	 * 
	 * @param theSource
	 */
	public CCVector4(final CCVector4 theSource) {
		this(theSource.x, theSource.y, theSource.z, theSource.w);
	}

	/**
	 * Constructs a new vector set to the (x, y, z, w) values of the given
	 * source vector.
	 * 
	 * @param theSource
	 */
	public CCVector4(final CCVector3 theSource, double theW) {
		this(theSource.x, theSource.y, theSource.z, theW);
	}
	
	public CCVector4(final CCVector2 theSource) {
		this(theSource.x, theSource.y, 0,0);
	}

	/**
	 * Constructs a new vector set to (0, 0, 0, 0).
	 */
	public CCVector4() {
		this(0, 0, 0, 0);
	}

	/**
	 * @param theIndex
	 * @return x value if index == 0, y value if index == 1, z value if index ==
	 *         2 or w value if index == 3
	 * @throws IllegalArgumentException
	 *             if index is not one of 0, 1, 2, 3.
	 */
	public double getValue(final int theIndex) {
		switch (theIndex) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		case 3:
			return w;
		}
		throw new IllegalArgumentException("index must be either 0, 1, 2 or 3");
	}

	/**
	 * @param theIndex
	 *            which field index in this vector to set.
	 * @param theValue
	 *            to set to one of x, y, z or w.
	 * @throws IllegalArgumentException
	 *             if index is not one of 0, 1, 2, 3.
	 * 
	 *             if this vector is read only
	 */
	public void setValue(final int theIndex, final double theValue) {
		switch (theIndex) {
		case 0:
			x = theValue;
			return;
		case 1:
			y = theValue;
			return;
		case 2:
			z = theValue;
			return;
		case 3:
			w = theValue;
			return;
		}
		throw new IllegalArgumentException("index must be either 0, 1, 2 or 3");
	}

	/**
	 * Stores the double values of this vector in the given double array.
	 * 
	 * @param theStore
	 *            if null, a new double[4] array is created.
	 * @return the double array
	 * @throws ArrayIndexOutOfBoundsException
	 *             if store is not at least length 4.
	 */
	public double[] toArray(double[] theStore) {
		if (theStore == null) {
			theStore = new double[4];
		}
		// do last first to ensure size is correct before any edits occur.
		theStore[3] = w;
		theStore[2] = z;
		theStore[1] = y;
		theStore[0] = x;
		return theStore;
	}

	public double[] toArray() {
		return toArray(null);
	}

	/**
	 * Sets the value of this vector to (x, y, z, w)
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return this vector for chaining
	 */
	public CCVector4 set(final double theX, final double theY, final double theZ, final double theW) {
		x = theX;
		y = theY;
		z = theZ;
		w = theW;
		return this;
	}

	/**
	 * Sets the value of this vector to the (x, y, z, w) values of the provided
	 * source vector.
	 * 
	 * @param theSource
	 * @return this vector for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCVector4 set(final CCVector4 theSource) {
		x = theSource.x;
		y = theSource.y;
		z = theSource.z;
		w = theSource.w;
		return this;
	}

	/**
	 * Sets the value of this vector to (0, 0, 0, 0)
	 * 
	 * @return this vector for chaining
	 */
	public CCVector4 zero() {
		return set(0, 0, 0, 0);
	}

	/**
	 * Adds the given values to those of this vector and returns them in store.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return (this.x + x, this.y + y, this.z + z, this.w + w)
	 */
	public CCVector4 add(final double theX, final double theY, final double theZ, final double theW, CCVector4 theStore) {
		if (theStore == null)
			theStore = new CCVector4();
		
		return theStore.set(
			x + theX, 
			y + theY, 
			z + theZ, 
			w + theW
		);
	}

	public CCVector4 add(final double theX, final double theY, final double theZ, final double theW) {
		return add(theX, theY, theZ, theW, null);
	}

	/**
	 * Increments the values of this vector with the given x, y, z and w values.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return this vector for chaining
	 */
	public CCVector4 addLocal(final double theX, final double theY, final double theZ, final double theW) {
		return add(theX, theY, theZ, theW, this);
	}

	/**
	 * Adds the values of the given source vector to those of this vector and
	 * returns them in store.
	 * 
	 * @param theSource
	 * @return (this.x + source.x, this.y + source.y, this.z + source.z, this.w
	 *         + source.w)
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCVector4 add(final CCVector4 theSource, CCVector4 theStore) {
		return add(theSource.x, theSource.y, theSource.z, theSource.w, theStore);
	}

	public CCVector4 add(final CCVector4 theSource) {
		return add(theSource, null);
	}

	/**
	 * Increments the values of this vector with the x, y, z and w values of the
	 * given vector.
	 * 
	 * @param theSource
	 * @return this vector for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCVector4 addLocal(final CCVector4 theSource) {
		return add(theSource, this);
	}

	/**
	 * Subtracts the given values from those of this vector and returns them in
	 * store.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return (this.x - x, this.y - y, this.z - z, this.w - w)
	 */
	public CCVector4 subtract(final double theX, final double theY, final double theZ, final double theW, CCVector4 theStore) {
		if (theStore == null)
			theStore = new CCVector4();
		
		return theStore.set(
			x - theX, 
			y - theY, 
			z - theZ, 
			w - theW
		);
	}
	
	public CCVector4 subtract(final double theX, final double theY, final double theZ, final double theW){
		return subtract(theX, theY, theZ, theW, null);
	}

	/**
	 * Decrements the values of this vector by the given x, y, z and w values.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return this vector for chaining
	 */
	public CCVector4 subtractLocal(final double theX, final double theY, final double theZ, final double theW){
		return subtract(theX, theY, theZ, theW, this);
	}

	/**
	 * Subtracts the values of the given source vector from those of this vector
	 * and returns them in store.
	 * 
	 * @param theSource
	 * @return (this.x - source.x, this.y - source.y, this.z - source.z, this.w
	 *         - source.w)
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCVector4 subtract(final CCVector4 theSource, CCVector4 theStore) {
		return subtract(theSource.x, theSource.y, theSource.z, theSource.w, theStore);
	}
	

	public CCVector4 subtract(final CCVector4 theSource) {
		return subtract(theSource, null);
	}

	/**
	 * Decrements the values of this vector by the x, y, z and w values from the
	 * given source vector.
	 * 
	 * @param theSource
	 * @return this vector for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCVector4 subtractLocal(final CCVector4 theSource) {
		return subtract(theSource, this);
	}

	/**
	 * Multiplies the values of this vector by the given scalar value and
	 * returns the result in store.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return a new vector (this.x * scale.x, this.y * scale.y, this.z *
	 *         scale.z, this.w * scale.w)
	 */
	public CCVector4 multiply(final double theX, final double theY, final double theZ, final double theW, CCVector4 theStore) {
		if(theStore == null)theStore = new CCVector4();
		return theStore.set(
			x * theX, 
			y * theY, 
			z * theZ, 
			w * theW
		);
	}
	
	public CCVector4 multiply(final double theX, final double theY, final double theZ, final double theW){
		return multiply(theX, theY, theZ, theW, null);
	}

	/**
	 * Internally modifies the values of this vector by multiplying them each by
	 * the given scale values.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return this vector for chaining
	 */
	public CCVector4 multiplyLocal(final double theX, final double theY, final double theZ, final double theW){
		return multiply(theX, theY, theZ, theW, this);
	}

	/**
	 * Multiplies the values of this vector by the given scalar value and
	 * returns the result in store.
	 * 
	 * @param scalar
	 * @return a new vector (this.x * scalar, this.y * scalar, this.z * scalar,
	 *         this.w * scalar)
	 */
	public CCVector4 multiply(final double theScalar, CCVector4 theStore) {
		return multiply(theScalar, theScalar, theScalar, theScalar, theStore);
	}
	
	public CCVector4 multiply(final double theScalar) {
		return multiply(theScalar, theScalar, theScalar, theScalar, null);
	}

	/**
	 * Internally modifies the values of this vector by multiplying them each by
	 * the given scalar value.
	 * 
	 * @param theScalar
	 * @return this vector for chaining
	 */
	public CCVector4 multiplyLocal(final double theScalar) {
		return multiply(theScalar, theScalar, theScalar, theScalar, this);
	}

	/**
	 * Multiplies the values of this vector by the given scalar value and
	 * returns the result in store.
	 * 
	 * @param theScale
	 * @return a new vector (this.x * scale.x, this.y * scale.y, this.z *
	 *         scale.z, this.w * scale.w)
	 */
	public CCVector4 multiply(final CCVector4 theScale, CCVector4 theStore) {
		return multiply(theScale.x, theScale.y, theScale.z, theScale.w, theStore);
	}
	
	public CCVector4 multiply(final CCVector4 theScale) {
		return multiply(theScale.x, theScale.y, theScale.z, theScale.w, null);
	}

	/**
	 * Internally modifies the values of this vector by multiplying them each by
	 * the given scale values.
	 * 
	 * @param theScale
	 * @return this vector for chaining
	 */
	public CCVector4 multiplyLocal(final CCVector4 theScale) {
		return multiply(theScale.x, theScale.y, theScale.z, theScale.w, this);
	}
	
	/**
	 * Divides the values of this vector by the given scale values and returns
	 * the result in store.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return a new vector (this.x / scale.x, this.y / scale.y, this.z /
	 *         scale.z, this.w / scale.w)
	 */
	public CCVector4 divide(final double theX, final double theY, final double theZ, final double theW, CCVector4 theStore) {
		if(theStore == null)theStore = new CCVector4();
		return theStore.set(
			x / theX, 
			y / theY, 
			z / theZ, 
			w / theW
		);
	}
	
	public CCVector4 divide(final double theX, final double theY, final double theZ, final double theW){
		return divide(theX, theY, theZ, theW, null);
	}

	/**
	 * Internally modifies the values of this vector by dividing them each by
	 * the given scale values.
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return this vector for chaining
	 */
	public CCVector4 divideLocal(final double theX, final double theY, final double theZ, final double theW) {
		return divide(theX, theY, theZ, theW, this);
	}

	/**
	 * Divides the values of this vector by the given scalar value and returns
	 * the result in store.
	 * 
	 * @param theScalar
	 * @return a new vector (this.x / scalar, this.y / scalar, this.z / scalar,
	 *         this.w / scalar)
	 */
	public CCVector4 divide(final double theScalar, CCVector4 theStore) {
		return divide(theScalar, theScalar, theScalar, theScalar, theStore);
	}
	
	public CCVector4 divide(final double theScalar) {
		return divide(theScalar, theScalar, theScalar, theScalar, null);
	}

	/**
	 * Internally modifies the values of this vector by dividing them each by
	 * the given scalar value.
	 * 
	 * @param scalar
	 * @return this vector for chaining
	 * 
	 * 
	 * @throws ArithmeticException
	 *             if scalar is 0
	 */
	public CCVector4 divideLocal(final double theScalar) {
		return divide(theScalar, theScalar, theScalar, theScalar, this);
	}

	/**
	 * Divides the values of this vector by the given scale values and returns
	 * the result in store.
	 * 
	 * @param theScale
	 * @return a new vector (this.x / scale.x, this.y / scale.y, this.z /
	 *         scale.z, this.w / scale.w)
	 */
	public CCVector4 divide(final CCVector4 theScale, CCVector4 theStore) {
		return divide(theScale.x, theScale.y, theScale.z, theScale.w, theStore);
	}
	
	public CCVector4 divide(final CCVector4 theScale) {
		return divide(theScale.x, theScale.y, theScale.z, theScale.w, null);
	}

	/**
	 * Internally modifies the values of this vector by dividing them each by
	 * the given scale values.
	 * 
	 * @param scale
	 * @return this vector for chaining
	 */
	public CCVector4 divideLocal(final CCVector4 theScale) {
		return divide(theScale.x, theScale.y, theScale.z, theScale.w, this);
	}

	/**
	 * 
	 * Internally modifies this vector by multiplying its values with a given
	 * scale value, then adding a given "add" value.
	 * 
	 * @param theScale
	 *            the value to multiply this vector by.
	 * @param theAdd
	 *            the value to add to the result
	 * @return this vector for chaining
	 */
	public CCVector4 scaleAdd(final double theScale, final CCVector4 theAdd, CCVector4 theStore) {
		if(theStore == null)theStore = new CCVector4();
		return theStore.set(
			x * theScale + theAdd.x,
			y * theScale + theAdd.y,
			z * theScale + theAdd.z,
			w * theScale + theAdd.w
		);
	}
	
	public CCVector4 scaleAdd(final double theScale, final CCVector4 theAdd){
		return scaleAdd(theScale, theAdd, null);
	}

	/**
	 * Scales this vector by multiplying its values with a given scale value,
	 * then adding a given "add" value. The result is store in the given store
	 * parameter.
	 * 
	 * @param theScale
	 *            the value to multiply by.
	 * @param theAdd
	 *            the value to add
	 * @return the store variable
	 */
	public CCVector4 scaleAddLocal(final double theScale, final CCVector4 theAdd){
		return scaleAdd(theScale, theAdd, this);
	}

	/**
	 * @return same as multiply(-1, store)
	 */
	public CCVector4 negate(CCVector4 theStore) {
		return multiply(-1, theStore);
	}
	
	public CCVector4 negate() {
		return negate(null);
	}

	/**
	 * @return same as multiplyLocal(-1)
	 */
	public CCVector4 negateLocal() {
		return negate(this);
	}

	/**
	 * Creates a new unit length vector from this one by dividing by length. If
	 * the length is 0, (ie, if the vector is 0, 0, 0, 0) then a new vector (0,
	 * 0, 0, 0) is returned.
	 * 
	 * @return a new unit vector (or 0, 0, 0, 0 if this unit is 0 length)
	 */
	public CCVector4 normalize(CCVector4 theStore) {
		final double lengthSq = lengthSquared();
		if (CCMath.abs(lengthSq) > CCMath.FLT_EPSILON) {
			return multiply(1f / CCMath.sqrt(lengthSq), theStore);
		}

		if(theStore == null)theStore = new CCVector4();
		return theStore.set(CCVector4.ZERO);
	}
	
	public CCVector4 normalize(){
		return normalize(null);
	}

	/**
	 * Converts this vector into a unit vector by dividing it internally by its
	 * length. If the length is 0, (ie, if the vector is 0, 0, 0, 0) then no
	 * action is taken.
	 * 
	 * @return this vector for chaining
	 */
	public CCVector4 normalizeLocal(){
		return normalize(this);
	}
	
	/**
	 * Performs a linear interpolation between the given begin and end vectors,
	 * using the given scalar as a percent. iow, if changeAmnt is closer to 0,
	 * the result will be closer to the begin value and if it is closer to 1,
	 * the result will be closer to the end value. The result is returned as a
	 * new vector object.
	 * 
	 * @param theBeginVector
	 * @param theEndVector
	 * @param theScalar
	 *            the scalar as a percent.
	 * @return a new vector as described above.
	 * @throws NullPointerException
	 *             if beginVec or endVec are null.
	 */
	public static CCVector4 lerp(
		final CCVector4 theBeginVector, 
		final CCVector4 theEndVector, 
		final double theScalar, 
		CCVector4 theStore
	) {
		if(theStore == null)theStore = new CCVector4();
		
		return theStore.set(
			(1.0f - theScalar) * theBeginVector.x + theScalar * theEndVector.x,
			(1.0f - theScalar) * theBeginVector.y + theScalar * theEndVector.y,
			(1.0f - theScalar) * theBeginVector.z + theScalar * theEndVector.z,
			(1.0f - theScalar) * theBeginVector.w + theScalar * theEndVector.w
		);
	}
	public static CCVector4 lerp(
		final CCVector4 theBeginVector, 
		final CCVector4 theEndVector, 
		final double theScalar
	) {
		return lerp(theBeginVector, theEndVector, theScalar, null);
	}

	/**
	 * Performs a linear interpolation between this vector and the given end
	 * vector, using the given scalar as a percent. iow, if changeAmnt is closer
	 * to 0, the result will be closer to the current value of this vector and
	 * if it is closer to 1, the result will be closer to the end value. The
	 * result is returned as a new vector object.
	 * 
	 * @param theEndVector
	 * @param theScalar
	 * @param theStore
	 *            the vector to store the result in for return. If null, a new
	 *            vector object is created and returned.
	 * @return a new vector as described above.
	 * @throws NullPointerException
	 *             if endVec is null.
	 */
	public CCVector4 lerp(final CCVector4 theEndVector, final double theScalar, CCVector4 theStore) {
		return lerp(this, theEndVector, theScalar, theStore);
	}
	
	public CCVector4 lerp(final CCVector4 theEndVector, final double theScalar){
		return lerp(theEndVector, theScalar, null);
	}

	/**
	 * Performs a linear interpolation between this vector and the given end
	 * vector, using the given scalar as a percent. iow, if changeAmnt is closer
	 * to 0, the result will be closer to the current value of this vector and
	 * if it is closer to 1, the result will be closer to the end value. The
	 * result is stored back in this vector.
	 * 
	 * @param theEndVector
	 * @param theScalar
	 * @return this vector for chaining
	 * 
	 * 
	 * @throws NullPointerException
	 *             if endVec is null.
	 */
	public CCVector4 lerpLocal(final CCVector4 theEndVector, final double theScalar){
		return lerp(theEndVector, theScalar, this);
	}

	

	/**
	 * Performs a linear interpolation between the given begin and end vectors,
	 * using the given scalar as a percent. iow, if changeAmnt is closer to 0,
	 * the result will be closer to the begin value and if it is closer to 1,
	 * the result will be closer to the end value. The result is stored back in
	 * this vector.
	 * 
	 * @param theBeginVector
	 * @param theEndVector
	 * @param changeAmnt
	 *            the scalar as a percent.
	 * @return this vector for chaining
	 * 
	 * 
	 * @throws NullPointerException
	 *             if beginVec or endVec are null.
	 */
	public CCVector4 lerpLocal(final CCVector4 theBeginVector, final CCVector4 theEndVector, final double theScalar) {
		return lerp(theBeginVector, theEndVector, theScalar, this);
	}

	/**
	 * @return the magnitude or distance between the origin (0, 0, 0, 0) and the
	 *         point described by this vector (x, y, z, w). Effectively the
	 *         square root of the value returned by {@link #lengthSquared()}.
	 */
	public double length() {
		return CCMath.sqrt(lengthSquared());
	}

	/**
	 * @return the squared magnitude or squared distance between the origin (0,
	 *         0, 0, 0) and the point described by this vector (x, y, z, w)
	 */
	public double lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return the squared distance between the point described by this vector
	 *         and the given x, y, z, w point. When comparing the relative
	 *         distance between two points it is usually sufficient to compare
	 *         the squared distances, thus avoiding an expensive square root
	 *         operation.
	 */
	public double distanceSquared(final double theX, final double theY, final double theZ, final double theW) {
		final double dx = x - theX;
		final double dy = y - theY;
		final double dz = z - theZ;
		final double dw = w - theW;
		return dx * dx + dy * dy + dz * dz + dw * dw;
	}

	/**
	 * @param theDestination
	 * @return the squared distance between the point described by this vector
	 *         and the given destination point. When comparing the relative
	 *         distance between two points it is usually sufficient to compare
	 *         the squared distances, thus avoiding an expensive square root
	 *         operation.
	 * @throws NullPointerException
	 *             if destination is null.
	 */
	public double distanceSquared(final CCVector4 theDestination) {
		return distanceSquared(theDestination.x, theDestination.y, theDestination.z, theDestination.w);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return the distance between the point described by this vector and the
	 *         given x, y, z, w point.
	 */
	public double distance(final double theX, final double theY, final double theZ, final double theW) {
		return CCMath.sqrt(distanceSquared(theX, theY, theZ, theW));
	}

	/**
	 * @param theDestination
	 * @return the distance between the point described by this vector and the
	 *         given destination point.
	 * @throws NullPointerException
	 *             if destination is null.
	 */
	public double distance(final CCVector4 theDestination) {
		return CCMath.sqrt(distanceSquared(theDestination));
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @param theW
	 * @return the dot product of this vector with the given x, y, z, w values.
	 */
	public double dot(final double theX, final double theY, final double theZ, final double theW) {
		return x * theX + y * theY + z * theZ + w * theW;
	}

	/**
	 * @param theVector
	 * @return the dot product of this vector with the x, y, z, w values of the
	 *         given vector.
	 * @throws NullPointerException
	 *             if vec is null.
	 */
	public double dot(final CCVector4 theVector) {
		return dot(theVector.x, theVector.y, theVector.z, theVector.w);
	}

	/**
	 * Check a vector... if it is null or its doubles are NaN or infinite,
	 * return false. Else return true.
	 * 
	 * @param theVector
	 *            the vector to check
	 * @return true or false as stated above.
	 */
	public static boolean isValid(final CCVector4 theVector) {
		if (theVector == null) {
			return false;
		}
		if (Double.isNaN(theVector.x) || Double.isNaN(theVector.y) || Double.isNaN(theVector.z) || Double.isNaN(theVector.w)) {
			return false;
		}
        return !Double.isInfinite(theVector.x) && !Double.isInfinite(theVector.y) && !Double.isInfinite(theVector.z) && !Double.isInfinite(theVector.w);
    }

	/**
	 * @return the string representation of this vector.
	 */
	@Override
	public String toString() {
		return "com.ardor3d.math.Vector4 [X=" + x + ", Y=" + y + ", Z=" + z + ", W=" + w + "]";
	}

	/**
	 * @return returns a unique code for this vector object based on its values.
	 *         If two vectors are numerically equal, they will return the same
	 *         hash code value.
	 */
	@Override
	public int hashCode() {
		int result = 33;

		final long myX = Double.doubleToLongBits(x);
		result += 31 * result + (int) (myX ^ myX >>> 32);

		final long myY = Double.doubleToLongBits(y);
		result += 31 * result + (int) (myY ^ myY >>> 32);

		final long myZ = Double.doubleToLongBits(z);
		result += 31 * result + (int) (myZ ^ myZ >>> 32);

		final long myW = Double.doubleToLongBits(w);
		result += 31 * result + (int) (myW ^ myW >>> 32);

		return result;
	}

	/**
	 * @param theObject
	 *            the object to compare for equality
	 * @return true if this vector and the provided vector have the same x, y, z
	 *         and w values.
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}
		if (!(theObject instanceof CCVector4)) {
			return false;
		}
		final CCVector4 comp = (CCVector4) theObject;
		return x == comp.x && y == comp.y && z == comp.z && w == comp.w;
	}

	// /////////////////
	// Method for Cloneable
	// /////////////////

	@Override
	public CCVector4 clone() {
		return new CCVector4(this);
	}

	// /////////////////
	// Methods for Externalizable
	// /////////////////

	/**
	 * Used with serialization. Not to be called manually.
	 * 
	 * @param theInput
	 *            ObjectInput
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	public void readExternal(final ObjectInput theInput) throws IOException {
		x = theInput.readDouble();
		y = theInput.readDouble();
		z = theInput.readDouble();
		w = theInput.readDouble();
	}

	/**
	 * Used with serialization. Not to be called manually.
	 * 
	 * @param theOutput
	 *            ObjectOutput
	 * @throws IOException
	 */
	@Override
	public void writeExternal(final ObjectOutput theOutput) throws IOException {
		theOutput.writeDouble(x);
		theOutput.writeDouble(y);
		theOutput.writeDouble(z);
		theOutput.writeDouble(w);
	}
}
