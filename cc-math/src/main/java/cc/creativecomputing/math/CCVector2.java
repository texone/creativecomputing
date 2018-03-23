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

import cc.creativecomputing.core.CCProperty;

/**
 * Vector2 represents a point or vector in a two dimensional system. This implementation stores its data in
 * double-precision.
 */
public class CCVector2 implements Cloneable, Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * 0, 0
     */
    public final static CCVector2 ZERO = new CCVector2(0, 0);

    /**
     * 1, 1
     */
    public final static CCVector2 ONE = new CCVector2(1, 1);

    /**
     * -1, -1
     */
    public final static CCVector2 NEG_ONE = new CCVector2(-1, -1);

    /**
     * 1, 0
     */
    public final static CCVector2 UNIT_X = new CCVector2(1, 0);

    /**
     * -1, 0
     */
    public final static CCVector2 NEG_UNIT_X = new CCVector2(-1, 0);

    /**
     * 0, 1
     */
    public final static CCVector2 UNIT_Y = new CCVector2(0, 1);

    /**
     * 0, -1
     */
    public final static CCVector2 NEG_UNIT_Y = new CCVector2(0, -1);
    
    /**
	 * Returns the angle between the two given vectors
	 * @param theX1
	 * @param theY1
	 * @param theX2
	 * @param theY2
	 * @return angle between the two given vectors
	 */
	public static double angle(final double theX1, final double theY1, final double theX2, final double theY2){
		return CCMath.atan2(theX2, theY2) - CCMath.atan2(theX1, theY1);
	}
	
    /**
	 * Returns the angle between the two given vectors
	 * @param theV1
	 * @param theV2
	 */
	public static double angle(final CCVector2 theV1, CCVector2 theV2) {
		return angle(theV1.y, theV1.x, theV2.y, theV2.x);
	}
	
	public static double angle(CCVector2 theVec){
		return CCMath.atan2(-theVec.y, theVec.x);
	}

	@CCProperty(name = "x")
    public double x = 0;
	@CCProperty(name = "y")
	public double y = 0;
    
    /**
     * Constructs a new vector set to (x, y).
     * 
     * @param theX
     * @param theY
     */
    public CCVector2(final double theX, final double theY) {
        x = theX;
        y = theY;
    }

    /**
     * Constructs a new vector set to (0, 0).
     */
    public CCVector2() {
        this(0, 0);
    }

    /**
     * Constructs a new vector set to the (x, y) values of the given source vector.
     * 
     * @param theSource
     */
    public CCVector2(final CCVector2 theSource) {
        this(theSource.x, theSource.y);
    }
    
    public CCVector2(final CCVector2i theSource) {
        this(theSource.x, theSource.y);
    }
    
    /**
     * Checks if one of the components of the vector is nan
     * @return true if one the vectors components is NaN
     */
    public boolean isNaN(){
    	return Double.isNaN(x) || Double.isNaN(y);
    }

    /**
     * @param theIndex
     * @return x value if index == 0 or y value if index == 1
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1.
     */
    public double getValue(final int theIndex) {
        switch (theIndex) {
            case 0:
                return x;
            case 1:
                return y;
        }
        throw new IllegalArgumentException("index must be either 0 or 1");
    }

    /**
     * @param theIndex
     *            which field index in this vector to set.
     * @param theValue
     *            to set to one of x or y.
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1.
     */
    public void setValue(final int theIndex, final double theValue) {
        switch (theIndex) {
            case 0:
                x = theValue;
                return;
            case 1:
                y = theValue;
                return;
        }
        throw new IllegalArgumentException("index must be either 0 or 1");
    }

    /**
     * Stores the double values of this vector in the given double array.
     * 
     * @param theStore
     *            if null, a new double[2] array is created.
     * @return the double array
     * @throws ArrayIndexOutOfBoundsException
     *             if store is not at least length 2.
     */
    public double[] toArray(double[] theStore) {
        if (theStore == null) {
            theStore = new double[2];
        }
        // do last first to ensure size is correct before any edits occur.
        theStore[1] = y;
        theStore[0] = x;
        return theStore;
    }
    
    public double[] toArray(){
    	return toArray(null);
    }

    

    /**
     * Sets the value of this vector to (x, y)
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector2 set(final double theX, final double theY) {
        x = theX;
        y = theY;
        return this;
    }

    /**
     * Sets the value of this vector to the (x, y) values of the provided source vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector2 set(final CCVector2 theSource) {
        x = theSource.x;
        y = theSource.y;
        return this;
    }

    /**
     * Sets the value of this vector to (0, 0)
     * 
     * @return this vector for chaining
     */
    public CCVector2 zero() {
        return set(0, 0);
    }

    /**
     * Adds the given values to those of this vector and returns them in store * @param store the vector to store the
     * result in for return. If null, a new vector object is created and returned. .
     * 
     * @param theX
     * @param theY
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return (this.x + x, this.y + y)
     */
    public CCVector2 add(final double theX, final double theY, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }

        return result.set(x + theX, y + theY);
    }
    
    public CCVector2 add(final double theX, final double theY){
    	return add(theX, theY, null);
    }

    /**
     * Increments the values of this vector with the given x and y values.
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector2 addLocal(final double theX, final double theY){
    	return add(theX, theY, this);
    }

    /**
     * Adds the values of the given source vector to those of this vector and returns them in store.
     * 
     * @param theSource
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return (this.x + source.x, this.y + source.y)
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector2 add(final CCVector2 theSource, final CCVector2 theStore) {
        return add(theSource.x, theSource.y, theStore);
    }
    
    public CCVector2 add(final CCVector2 theSource) {
        return add(theSource, null);
    }

    /**
     * Increments the values of this vector with the x and y values of the given vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector2 addLocal(final CCVector2 theSource) {
        return add(theSource, this);
    }

    /**
     * Subtracts the given values from those of this vector and returns them in store.
     * 
     * @param theX
     * @param theY
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return (this.x - x, this.y - y)
     */
    public CCVector2 subtract(final double theX, final double theY, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }

        return result.set(
        	x - theX, 
        	y - theY
        );
    }
    
    public CCVector2 subtract(final double theX, final double theY){
    	return subtract(theX, theY, null);
    }

    /**
     * Decrements the values of this vector by the given x and y values.
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector2 subtractLocal(final double theX, final double theY) {
        return subtract(theX, theY, this);
    }

    /**
     * Subtracts the values of the given source vector from those of this vector and returns them in store.
     * 
     * @param theSource
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return (this.x - source.x, this.y - source.y)
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector2 subtract(final CCVector2 theSource, final CCVector2 theStore) {
        return subtract(theSource.x, theSource.y, theStore);
    }
    
    public CCVector2 subtract(final CCVector2 theSource) {
        return subtract(theSource, null);
    }

    /**
     * Decrements the values of this vector by the x and y values from the given source vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector2 subtractLocal(final CCVector2 theSource) {
        return subtract(theSource, this);
    }

    /**
     * Multiplies the values of this vector by the given scalar value and returns the result in store.
     * 
     * @param theScalar
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x * scalar, this.y * scalar)
     */
    public CCVector2 multiply(final double theScalar, final CCVector2 theStore) {
    	return multiply(theScalar, theScalar, theStore);
    }
    
    public CCVector2 multiply(final double theScalar) {
    	return multiply(theScalar, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the given scalar value.
     * 
     * @param theScalar
     * @return this vector for chaining
     */
    public CCVector2 multiplyLocal(final double theScalar) {
    	return multiply(theScalar, theScalar, this);
    }

    /**
     * Multiplies the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theScale
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x * scale.x, this.y * scale.y)
     */
    public CCVector2 multiply(final CCVector2 theScale, final CCVector2 theStore) {
    	return multiply(theScale.x, theScale.y, theStore);
    }
    
    public CCVector2 multiply(final CCVector2 theScale) {
    	return multiply(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the values of the given scale.
     * 
     * @param scale
     * @return this vector for chaining
     */
    public CCVector2 multiplyLocal(final CCVector2 theScale) {
    	return multiply(theScale, this);
    }

    /**
     * Multiplies the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theX
     * @param theY
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x * scale.x, this.y * scale.y)
     */
    public CCVector2 multiply(final double theX, final double theY, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }

        return result.set(x * theX, y * theY);
    }
    
    public CCVector2 multiply(final double theX, final double theY){
    	return multiply(theX, theY, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the values of the given scale.
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector2 multiplyLocal(final double theX, final double theY){
    	return multiply(theX, theY, this);
    }

    /**
     * Divides the values of this vector by the given scalar value and returns the result in store.
     * 
     * @param theScalar
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x / scalar, this.y / scalar)
     */
    public CCVector2 divide(final double theScalar, final CCVector2 theStore) {
        return divide(theScalar, theScalar, theStore);
    }
    
    public CCVector2 divide(final double theScalar) {
        return divide(theScalar, null);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the given scalar value.
     * 
     * @param theScalar
     * @return this vector for chaining
     * @throws ArithmeticException
     *             if scalar is 0
     */
    public CCVector2 divideLocal(final double theScalar) {
    	return divide(theScalar, this);
    }

    /**
     * Divides the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theScale
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x / scale.x, this.y / scale.y)
     */
    public CCVector2 divide(final CCVector2 theScale, final CCVector2 theStore) {
    	return divide(theScale.x, theScale.y, theStore);
    }
    
    public CCVector2 divide(final CCVector2 theScale) {
    	return divide(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the values of the given scale.
     * 
     * @param scale
     * @return this vector for chaining
     */
    public CCVector2 divideLocal(final CCVector2 theScale) {
    	return divide(theScale, this);
    }

    /**
     * Divides the values of this vector by the given scale values and returns the result in store.
     * 
     * @param x
     * @param y
     * @param store
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x / scale.x, this.y / scale.y)
     */
    public CCVector2 divide(final double theX, final double theY, final CCVector2 store) {
        CCVector2 result = store;
        if (result == null) {
            result = new CCVector2();
        }

        return result.set(x / theX, y / theY);
    }
    
    public CCVector2 divide(final double theX, final double theY){
    	return divide(theX, theY, null);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the values of the given scale.
     * 
     * @param x
     * @param y
     * @return this vector for chaining
     */
    public CCVector2 divideLocal(final double theX, final double theY){
    	return divide(theX, theY, this);
    }

    /**
     * Scales this vector by multiplying its values with a given scale value, then adding a given "add" value. The
     * result is store in the given store parameter.
     * 
     * @param theScale
     *            the value to multiply by.
     * @param theAdd
     *            the value to add
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return the store variable
     */
    public CCVector2 scaleAdd(final double theScale, final CCVector2 theAdd, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }
        
        result.x = x * theScale + theAdd.x;
        result.y = y * theScale + theAdd.y;
        return result;
    }
    
    public CCVector2 scaleAdd(final double theScale, final CCVector2 theAdd){
        return scaleAdd(theScale, theAdd, null);
    }

    /**
     * 
     * Internally modifies this vector by multiplying its values with a given scale value, then adding a given "add"
     * value.
     * 
     * @param theScale
     *            the value to multiply this vector by.
     * @param theAdd
     *            the value to add to the result
     * @return this vector for chaining
     */
    public CCVector2 scaleAddLocal(final double theScale, final CCVector2 theAdd) {
        return scaleAdd(theScale, theAdd, this);
    }

    /**
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return same as multiply(-1, store)
     */
    public CCVector2 negate(final CCVector2 theStore) {
        return multiply(-1, theStore);
    }
    
    public CCVector2 negate(){
    	return negate(null);
    }

    /**
     * @return same as multiplyLocal(-1)
     */
    public CCVector2 negateLocal() {
        return negate(this);
    }

    /**
     * Creates a new unit length vector from this one by dividing by length. If the length is 0, (ie, if the vector is
     * 0, 0) then a new vector (0, 0) is returned.
     * 
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new unit vector (or 0, 0 if this unit is 0 length)
     */
    public CCVector2 normalize(final CCVector2 theStore) {
        final double lengthSq = lengthSquared();
        if (CCMath.abs(lengthSq) > CCMath.FLT_EPSILON) {
            return multiply(1f / CCMath.sqrt(lengthSq), theStore);
        }

        return theStore != null ? theStore.set(CCVector2.ZERO) : new CCVector2(CCVector2.ZERO);
    }
    
    public CCVector2 normalize(){
    	return normalize(null);
    }

    /**
     * Converts this vector into a unit vector by dividing it internally by its length. If the length is 0, (ie, if the
     * vector is 0, 0) then no action is taken.
     * 
     * @return this vector for chaining
     */
    public CCVector2 normalizeLocal() {
    	return normalize(this);
    }
    
    /**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector2 randomize(){
		do{
			x = CCMath.random() * 2.0F - 1.0F;
			y = CCMath.random() * 2.0F - 1.0F;
		}while (lengthSquared() > 1.0F);
		return this;
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector2 randomize(double radius){
		do{
			x = radius * (CCMath.random() * 2.0F - 1.0F);
			y = radius * (CCMath.random() * 2.0F - 1.0F);
		}while (lengthSquared() > radius * radius);
		return this;
	}
    
    /**
	 * clamps the length of a given vector to maxLength.  If the vector is 
	 * shorter its value is returned unaltered, if the vector is longer
	 * the value returned has length of maxLength and is parallel to the
	 * original input.
	 * @param theTreshhold double, maximum length to vector is set to.
	 */
	public CCVector2 truncate(final double theTreshhold, final CCVector2 theStore){
		double length = length();
		if (length > theTreshhold)
			return multiply(theTreshhold / length, theStore);

        return theStore != null ? theStore.set(this) : new CCVector2(this);
	}
	
	public CCVector2 truncate(final double theTreshhold){
		return truncate(theTreshhold, null);
	}
	
	public CCVector2 truncateLocal(final double theTreshhold){
		return truncate(theTreshhold, this);
	}

    /**
     * Creates a new vector representing this vector rotated around 0,0 by a specified angle in a given direction.
     * 
     * @param theAngle
     *            in radians
     * @param theClockwise
     *            true to rotate in a clockwise direction
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return the new rotated vector
     */
    public CCVector2 rotateAroundOrigin(double theAngle, final boolean theClockwise, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }

        if (theClockwise) {
            theAngle = -theAngle;
        }
        final double newX = CCMath.cos(theAngle) * x - CCMath.sin(theAngle) * y;
        final double newY = CCMath.sin(theAngle) * x + CCMath.cos(theAngle) * y;
        return result.set(newX, newY);
    }
    
    public CCVector2 rotateAroundOrigin(double theAngle, final boolean theClockwise){
    	return rotateAroundOrigin(theAngle, theClockwise, null);
    }

    /**
     * Internally rotates this vector around 0,0 by a specified angle in a given direction.
     * 
     * @param theAngle
     *            in radians
     * @param theClockwise
     *            true to rotate in a clockwise direction
     * @return this vector for chaining
     */
    public CCVector2 rotateAroundOriginLocal(double theAngle, final boolean theClockwise) {
    	return rotateAroundOrigin(theAngle, theClockwise, this);
    }

    /**
     * Performs a linear interpolation between this vector and the given end vector, using the given scalar as a
     * percent. iow, if changeAmnt is closer to 0, the result will be closer to the current value of this vector and if
     * it is closer to 1, the result will be closer to the end value. The result is returned as a new vector object.
     * 
     * @param theEndVector
     * @param theScalar
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector as described above.
     * @throws NullPointerException
     *             if endVec is null.
     */
    public CCVector2 lerp(final CCVector2 theEndVector, final double theScalar, final CCVector2 theStore) {
        return lerp(this, theEndVector, theScalar, theStore);
    }
    
    public CCVector2 lerp(final CCVector2 theEndVector, final double theScalar) {
        return lerp(theEndVector, theScalar, null);
    }

    /**
     * Performs a linear interpolation between this vector and the given end vector, using the given scalar as a
     * percent. iow, if changeAmnt is closer to 0, the result will be closer to the current value of this vector and if
     * it is closer to 1, the result will be closer to the end value. The result is stored back in this vector.
     * 
     * @param theEndVector
     * @param theScalar
     * @return this vector for chaining
     * @throws NullPointerException
     *             if endVec is null.
     */
    public CCVector2 lerpLocal(final CCVector2 theEndVector, final double theScalar) {
        return lerp(theEndVector, theScalar, this);
    }

    /**
     * Performs a linear interpolation between the given begin and end vectors, using the given scalar as a percent.
     * iow, if changeAmnt is closer to 0, the result will be closer to the begin value and if it is closer to 1, the
     * result will be closer to the end value. The result is returned as a new vector object.
     * 
     * @param theBeginVector
     * @param theEndVector
     * @param theScalar
     *            the scalar as a percent.
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector as described above.
     * @throws NullPointerException
     *             if beginVec or endVec are null.
     */
    public static CCVector2 lerp(final CCVector2 theBeginVector, final CCVector2 theEndVector, final double theScalar, final CCVector2 theStore) {
        CCVector2 result = theStore;
        if (result == null) {
            result = new CCVector2();
        }

        final double x = (1.0f - theScalar) * theBeginVector.x + theScalar * theEndVector.x;
        final double y = (1.0f - theScalar) * theBeginVector.y + theScalar * theEndVector.y;
        return result.set(x, y);
    }
    
    public static CCVector2 lerp(final CCVector2 theBeginVector, final CCVector2 theEndVector, final double theScalar) {
        return lerp(theBeginVector, theEndVector, theScalar, null);
    }

    /**
     * Performs a linear interpolation between the given begin and end vectors, using the given scalar as a percent.
     * iow, if changeAmnt is closer to 0, the result will be closer to the begin value and if it is closer to 1, the
     * result will be closer to the end value. The result is stored back in this vector.
     * 
     * @param theBeginVector
     * @param theEndVector
     * @param changeAmnt
     *            the scalar as a percent.
     * @return this vector for chaining
     * @throws NullPointerException
     *             if beginVec or endVec are null.
     */
    public CCVector2 lerpLocal(final CCVector2 theBeginVector, final CCVector2 theEndVector, final double theScalar) {
        return lerp(theBeginVector, theEndVector, theScalar, this);
    }
    
	static public CCVector2 bezierPoint(CCVector2 a, CCVector2 b, CCVector2 c, CCVector2 d, double t) {
		double t1 = 1.0f - t;
		return new CCVector2(
			a.x * t1 * t1 * t1 + 3 * b.x * t * t1 * t1 + 3 * c.x * t * t * t1 + d.x * t * t * t,
			a.y * t1 * t1 * t1 + 3 * b.y * t * t1 * t1 + 3 * c.y * t * t * t1 + d.y * t * t * t
		);
	}
	
	public static CCVector2 circlePoint(double theAngle, double theRadius, double theMX, double theMY){
		return new CCVector2(
			CCMath.cos(theAngle) * theRadius + theMX,
			CCMath.sin(theAngle) * theRadius + theMY
		);
	}

    /**
     * @return the magnitude of this vector, or the distance between the origin (0, 0) and the point described by (x,
     *         y). Basically sqrt(x^2 + y^2)
     */
    public double length() {
        return CCMath.sqrt(lengthSquared());
    }

    /**
     * @return the squared magnitude of this vector. (x^2 + y^2)
     */
    public double lengthSquared() {
        return x * x + y * y;
    }

    /**
     * @param theX
     * @param theY
     * @return the squared distance between the point described by this vector and the given x, y point. When comparing
     *         the relative distance between two points it is usually sufficient to compare the squared distances, thus
     *         avoiding an expensive square root operation.
     */
    public double distanceSquared(final double theX, final double theY) {
        final double dx = x - theX;
        final double dy = y - theY;
        return dx * dx + dy * dy;
    }

    /**
     * @param theDestination
     * @return the squared distance between the point described by this vector and the given destination point. When
     *         comparing the relative distance between two points it is usually sufficient to compare the squared
     *         distances, thus avoiding an expensive square root operation.
     * @throws NullPointerException
     *             if destination is null.
     */
    public double distanceSquared(final CCVector2 theDestination) {
        return distanceSquared(theDestination.x, theDestination.y);
    }

    /**
     * @param theX
     * @param theY
     * @return the distance between the point described by this vector and the given x, y point.
     */
    public double distance(final double theX, final double theY) {
        return CCMath.sqrt(distanceSquared(theX, theY));
    }

    /**
     * @param theDestination
     * @return the distance between the point described by this vector and the given destination point.
     * @throws NullPointerException
     *             if destination is null.
     */
    public double distance(final CCVector2 theDestination) {
        return CCMath.sqrt(distanceSquared(theDestination));
    }

    /**
     * @param theX
     * @param theY
     * @return the dot product of this vector with the given x, y values.
     */
    public double dot(final double theX, final double theY) {
        return x * theX + y * theY;
    }

    /**
     * @param theVector
     * @return the dot product of this vector with the x, y values of the given vector.
     * @throws NullPointerException
     *             if vec is null.
     */
    public double dot(final CCVector2 theVector) {
        return dot(theVector.x, theVector.y);
    }

    /**
     * @return the angle - in radians [-pi, pi) - represented by this Vector2 as expressed by a conversion from
     *         rectangular coordinates (<code>x</code>,&nbsp;<code>y</code>) to polar coordinates
     *         (r,&nbsp;<i>theta</i>).
     */
    public double getPolarAngle() {
        return -CCMath.atan2(y, x);
    }

    /**
     * @param theOtherVector
     *            the "destination" unit vector
     * @return the angle (in radians) required to rotate a ray represented by this vector to lie co-linear to a ray
     *         described by the given vector. It is assumed that both this vector and the given vector are unit vectors
     *         (normalized).
     * @throws NullPointerException
     *             if otherVector is null.
     */
    public double angleBetween(final CCVector2 theOtherVector) {
        return CCMath.atan2(theOtherVector.y, theOtherVector.x) - CCMath.atan2(y, x);
    }

    /**
     * @param theOtherVector
     *            a unit vector to find the angle against
     * @return the minimum angle (in radians) between two vectors. It is assumed that both this vector and the given
     *         vector are unit vectors (normalized).
     * @throws NullPointerException
     *             if otherVector is null.
     */
    public double smallestAngleBetween(final CCVector2 theOtherVector) {
        final double dotProduct = dot(theOtherVector);
        return CCMath.acos(dotProduct);
    }
    
    public CCVector2 cross(){
    	return new CCVector2(y, -x);
    }
    
    public CCVector2 crossLocal(){
    	return set(y, -x);
    }

    /**
     * Check a vector... if it is null or its doubles are NaN or infinite, return false. Else return true.
     * 
     * @param theVector
     *            the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValid(final CCVector2 theVector) {
        if (theVector == null) {
            return false;
        }
        if (Double.isNaN(theVector.x) || Double.isNaN(theVector.y)) {
            return false;
        }
        return !Double.isInfinite(theVector.x) && !Double.isInfinite(theVector.y);
    }

    /**
     * @return the string representation of this vector.
     */
    @Override
    public String toString() {
        return getClass().getName() + " [X=" + x + ", Y=" + y + "]";
    }

    /**
     * @return returns a unique code for this vector object based on its values. If two vectors are numerically equal,
     *         they will return the same hash code value.
     */
    @Override
    public int hashCode() {
        int result = 33;

        final long myX = Double.doubleToLongBits(x);
        result += 63 * result + (myX ^ myX >>> 63);

        final long myY = Double.doubleToLongBits(y);
        result += 63 * result + (myY ^ myY >>> 63);

        return result;
    }

    /**
     * @param theObject
     *            the object to compare for equality
     * @return true if this vector and the provided vector have the same x and y values.
     */
    @Override
    public boolean equals(final Object theObject) {
        if (this == theObject) {
            return true;
        }
        if (!(theObject instanceof CCVector2)) {
            return false;
        }
        final CCVector2 comp = (CCVector2) theObject;
        return 
        	CCMath.abs(x - comp.x) < CCVector3.ALLOWED_DEVIANCE && 
        	CCMath.abs(y - comp.y) < CCVector3.ALLOWED_DEVIANCE;
    }

    // /////////////////
    // Method for Cloneable
    // /////////////////

    @Override
    public CCVector2 clone() {
        return new CCVector2(this);
    }

    // /////////////////
    // Methods for Externalizable
    // /////////////////

    /**
     * Used with serialization. Not to be called manually.
     * 
     * @param in
     *            ObjectInput
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    public void readExternal(final ObjectInput in) throws IOException {
        x = in.readFloat();
        y = in.readFloat();
    }

    /**
     * Used with serialization. Not to be called manually.
     * 
     * @param out
     *            ObjectOutput
     * @throws IOException
     */
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
    }

	public CCVector2 rotate(double r, CCVector2 theStorage){
		if(theStorage == null){
			theStorage = new CCVector2();
		}
		double xprev = x;
		double yprev = y;
		
		final double sinR = CCMath.sin(r);
		final double cosR = CCMath.cos(r);

		theStorage.x = cosR * xprev + sinR * yprev;
		theStorage.y = -sinR * xprev + cosR * yprev;
		
		return theStorage;
	}
	
	public CCVector2 rotate(double r){
		return rotate(r, new CCVector2());
	}

	public CCVector2 rotateLocal(double r){
		return rotate(r, this);
	}

	public boolean isZero() {
		return x == 0 && y == 0;
	}
}
