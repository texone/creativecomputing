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
 * Vector2 represents a point or vector in a two dimensional system. This implementation stores its data in
 * float-precision.
 */
public class CCVector1 implements Cloneable, Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * 0, 0
     */
    public final static CCVector1 ZERO = new CCVector1(0);

    /**
     * 1, 1
     */
    public final static CCVector1 ONE = new CCVector1(1);

    /**
     * -1, -1
     */
    public final static CCVector1 NEG_ONE = new CCVector1(-1);

    /**
     * 1, 0
     */
    public final static CCVector1 UNIT_X = new CCVector1(1);

    /**
     * -1, 0
     */
    public final static CCVector1 NEG_UNIT_X = new CCVector1(-1);

    public float x = 0;

    /**
     * Constructs a new vector set to (0).
     */
    public CCVector1() {
        this(0);
    }

    /**
     * Constructs a new vector set to the (x) values of the given source vector.
     * 
     * @param theSource
     */
    public CCVector1(final CCVector1 theSource) {
        this(theSource.x);
    }

    /**
     * Constructs a new vector set to (x, y).
     * 
     * @param theX
     * @param theY
     */
    public CCVector1(final float theX) {
        x = theX;
    }

    /**
     * @param theIndex
     * @return x value if index == 0 or y value if index == 1
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1.
     */
    public float getValue(final int theIndex) {
        switch (theIndex) {
            case 0:
                return x;
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
    public void setValue(final int theIndex, final float theValue) {
        switch (theIndex) {
            case 0:
                x = theValue;
                return;
        }
        throw new IllegalArgumentException("index must be either 0 or 1");
    }

    /**
     * Stores the float values of this vector in the given float array.
     * 
     * @param theStore
     *            if null, a new float[2] array is created.
     * @return the float array
     * @throws ArrayIndexOutOfBoundsException
     *             if store is not at least length 2.
     */
    public float[] toArray(float[] theStore) {
        if (theStore == null) {
            theStore = new float[1];
        }
        // do last first to ensure size is correct before any edits occur.
        theStore[0] = x;
        return theStore;
    }
    
    public float[] toArray(){
    	return toArray(null);
    }

    

    /**
     * Sets the value of this vector to (x, y)
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector1 set(final float theX) {
        x = theX;
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
    public CCVector1 set(final CCVector1 theSource) {
        x = theSource.x;
        return this;
    }

    /**
     * Sets the value of this vector to (0, 0)
     * 
     * @return this vector for chaining
     */
    public CCVector1 zero() {
        return set(0);
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
    public CCVector1 add(final float theX, final CCVector1 theStore) {
        CCVector1 result = theStore;
        if (result == null) {
            result = new CCVector1();
        }

        return result.set(x + theX);
    }
    
    public CCVector1 add(final float theX){
    	return add(theX, null);
    }

    /**
     * Increments the values of this vector with the given x and y values.
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector1 addLocal(final float theX){
    	return add(theX, this);
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
    public CCVector1 add(final CCVector1 theSource, final CCVector1 theStore) {
        return add(theSource.x, theStore);
    }
    
    public CCVector1 add(final CCVector1 theSource) {
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
    public CCVector1 addLocal(final CCVector1 theSource) {
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
    public CCVector1 subtract(final float theX, final CCVector1 theStore) {
        CCVector1 result = theStore;
        if (result == null) {
            result = new CCVector1();
        }

        return result.set(
        	x - theX
        );
    }
    
    public CCVector1 subtract(final float theX){
    	return subtract(theX, null);
    }

    /**
     * Decrements the values of this vector by the given x and y values.
     * 
     * @param theX
     * @param theY
     * @return this vector for chaining
     */
    public CCVector1 subtractLocal(final float theX) {
        return subtract(theX, this);
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
    public CCVector1 subtract(final CCVector1 theSource, final CCVector1 theStore) {
        return subtract(theSource.x, theStore);
    }
    
    public CCVector1 subtract(final CCVector1 theSource) {
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
    public CCVector1 subtractLocal(final CCVector1 theSource) {
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
    public CCVector1 multiply(final float theScalar, final CCVector1 theStore) {
    	return multiply(theScalar, theStore);
    }
    
    public CCVector1 multiply(final float theScalar) {
    	return multiply(theScalar, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the given scalar value.
     * 
     * @param theScalar
     * @return this vector for chaining
     */
    public CCVector1 multiplyLocal(final float theScalar) {
    	return multiply(theScalar, this);
    }

    /**
     * Multiplies the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theScale
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x * scale.x, this.y * scale.y)
     */
    public CCVector1 multiply(final CCVector1 theScale, final CCVector1 theStore) {
    	return multiply(theScale.x, theStore);
    }
    
    public CCVector1 multiply(final CCVector1 theScale) {
    	return multiply(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the values of the given scale.
     * 
     * @param scale
     * @return this vector for chaining
     */
    public CCVector1 multiplyLocal(final CCVector1 theScale) {
    	return multiply(theScale, this);
    }

    /**
     * Divides the values of this vector by the given scalar value and returns the result in store.
     * 
     * @param theScalar
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x / scalar, this.y / scalar)
     */
    public CCVector1 divide(final float theScalar, final CCVector1 theStore) {
        return divide(theScalar, theStore);
    }
    
    public CCVector1 divide(final float theScalar) {
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
    public CCVector1 divideLocal(final float theScalar) {
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
    public CCVector1 divide(final CCVector1 theScale, final CCVector1 theStore) {
    	return divide(theScale.x, theStore);
    }
    
    public CCVector1 divide(final CCVector1 theScale) {
    	return divide(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the values of the given scale.
     * 
     * @param scale
     * @return this vector for chaining
     */
    public CCVector1 divideLocal(final CCVector1 theScale) {
    	return divide(theScale, this);
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
    public CCVector1 scaleAdd(final float theScale, final CCVector1 theAdd, final CCVector1 theStore) {
        CCVector1 result = theStore;
        if (result == null) {
            result = new CCVector1();
        }
        
        result.x = x * theScale + theAdd.x;
        return result;
    }
    
    public CCVector1 scaleAdd(final float theScale, final CCVector1 theAdd){
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
    public CCVector1 scaleAddLocal(final float theScale, final CCVector1 theAdd) {
        return scaleAdd(theScale, theAdd, this);
    }

    /**
     * @param theStore
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return same as multiply(-1, store)
     */
    public CCVector1 negate(final CCVector1 theStore) {
        return multiply(-1, theStore);
    }
    
    public CCVector1 negate(){
    	return negate(null);
    }

    /**
     * @return same as multiplyLocal(-1)
     */
    public CCVector1 negateLocal() {
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
    public CCVector1 normalize(final CCVector1 theStore) {
        final float lengthSq = lengthSquared();
        if (CCMath.abs(lengthSq) > CCMath.FLT_EPSILON) {
            return multiply(1f / CCMath.sqrt(lengthSq), theStore);
        }

        return theStore != null ? theStore.set(CCVector1.ZERO) : new CCVector1(CCVector1.ZERO);
    }
    
    public CCVector1 normalize(){
    	return normalize(null);
    }

    /**
     * Converts this vector into a unit vector by dividing it internally by its length. If the length is 0, (ie, if the
     * vector is 0, 0) then no action is taken.
     * 
     * @return this vector for chaining
     */
    public CCVector1 normalizeLocal() {
    	return normalize(this);
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
    public CCVector1 lerp(final CCVector1 theEndVector, final float theScalar, final CCVector1 theStore) {
        return lerp(this, theEndVector, theScalar, theStore);
    }
    
    public CCVector1 lerp(final CCVector1 theEndVector, final float theScalar) {
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
    public CCVector1 lerpLocal(final CCVector1 theEndVector, final float theScalar) {
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
    public static CCVector1 lerp(final CCVector1 theBeginVector, final CCVector1 theEndVector, final float theScalar, final CCVector1 theStore) {
        CCVector1 result = theStore;
        if (result == null) {
            result = new CCVector1();
        }

        final float x = (1.0f - theScalar) * theBeginVector.x + theScalar * theEndVector.x;
        return result.set(x);
    }
    
    public static CCVector1 lerp(final CCVector1 theBeginVector, final CCVector1 theEndVector, final float theScalar) {
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
    public CCVector1 lerpLocal(final CCVector1 theBeginVector, final CCVector1 theEndVector, final float theScalar) {
        return lerp(theBeginVector, theEndVector, theScalar, this);
    }

    /**
     * @return the magnitude of this vector, or the distance between the origin (0, 0) and the point described by (x,
     *         y). Basically sqrt(x^2 + y^2)
     */
    public float length() {
        return CCMath.sqrt(lengthSquared());
    }

    /**
     * @return the squared magnitude of this vector. (x^2 + y^2)
     */
    public float lengthSquared() {
        return x * x;
    }

    /**
     * @param theX
     * @param theY
     * @return the squared distance between the point described by this vector and the given x, y point. When comparing
     *         the relative distance between two points it is usually sufficient to compare the squared distances, thus
     *         avoiding an expensive square root operation.
     */
    public float distanceSquared(final float theX) {
        final float dx = x - theX;
        return dx * dx;
    }

    /**
     * @param theDestination
     * @return the squared distance between the point described by this vector and the given destination point. When
     *         comparing the relative distance between two points it is usually sufficient to compare the squared
     *         distances, thus avoiding an expensive square root operation.
     * @throws NullPointerException
     *             if destination is null.
     */
    public float distanceSquared(final CCVector1 theDestination) {
        return distanceSquared(theDestination.x);
    }

    /**
     * @param theX
     * @param theY
     * @return the distance between the point described by this vector and the given x, y point.
     */
    public float distance(final float theX) {
        return CCMath.sqrt(distanceSquared(theX));
    }

    /**
     * @param theDestination
     * @return the distance between the point described by this vector and the given destination point.
     * @throws NullPointerException
     *             if destination is null.
     */
    public float distance(final CCVector1 theDestination) {
        return CCMath.sqrt(distanceSquared(theDestination));
    }

    /**
     * @param theX
     * @param theY
     * @return the dot product of this vector with the given x, y values.
     */
    public float dot(final float theX) {
        return x * theX;
    }

    /**
     * @param theVector
     * @return the dot product of this vector with the x, y values of the given vector.
     * @throws NullPointerException
     *             if vec is null.
     */
    public float dot(final CCVector1 theVector) {
        return dot(theVector.x);
    }

    /**
     * @param theOtherVector
     *            a unit vector to find the angle against
     * @return the minimum angle (in radians) between two vectors. It is assumed that both this vector and the given
     *         vector are unit vectors (normalized).
     * @throws NullPointerException
     *             if otherVector is null.
     */
    public float smallestAngleBetween(final CCVector1 theOtherVector) {
        final float dotProduct = dot(theOtherVector);
        return CCMath.acos(dotProduct);
    }

    /**
     * Check a vector... if it is null or its doubles are NaN or infinite, return false. Else return true.
     * 
     * @param theVector
     *            the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValid(final CCVector1 theVector) {
        if (theVector == null) {
            return false;
        }
        if (Float.isNaN(theVector.x)) {
            return false;
        }
        if (Float.isInfinite(theVector.x)) {
            return false;
        }
        return true;
    }

    /**
     * @return the string representation of this vector.
     */
    @Override
    public String toString() {
        return "com.ardor3d.math.Vector2 [X=" + x + "]";
    }

    /**
     * @return returns a unique code for this vector object based on its values. If two vectors are numerically equal,
     *         they will return the same hash code value.
     */
    @Override
    public int hashCode() {
        int result = 17;

        final int myX = Float.floatToIntBits(x);
        result += 15 * result + (int) (myX ^ myX >>> 31);

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
        if (!(theObject instanceof CCVector1)) {
            return false;
        }
        final CCVector1 comp = (CCVector1) theObject;
        return 
        	CCMath.abs(x - comp.x) < CCVector3.ALLOWED_DEVIANCE;
    }

    // /////////////////
    // Method for Cloneable
    // /////////////////

    @Override
    public CCVector1 clone() {
        return new CCVector1(this);
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
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readFloat();
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
        out.writeFloat(x);
    }

}
