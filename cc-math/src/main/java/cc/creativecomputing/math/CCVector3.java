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
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.io.format.CCDataHolder;
import cc.creativecomputing.core.io.format.CCDataSerializable;


/**
 * Vector3 represents a point or vector in a three dimensional system. This implementation stores its data in
 * double-precision.
 */
public class CCVector3 implements Cloneable, Externalizable, CCDataSerializable {

    private static final long serialVersionUID = 1L;
    public static final double ALLOWED_DEVIANCE = 0.000001f;
    /**
     * 0, 0, 0
     */
    public final static CCVector3 ZERO = new CCVector3(0, 0, 0);

    /**
     * 1, 1, 1
     */
    public final static CCVector3 ONE = new CCVector3(1, 1, 1);

    /**
     * -1, -1, -1
     */
    public final static CCVector3 NEG_ONE = new CCVector3(-1, -1, -1);

    /**
     * 1, 0, 0
     */
    public final static CCVector3 UNIT_X = new CCVector3(1, 0, 0);

    /**
     * -1, 0, 0
     */
    public final static CCVector3 NEG_UNIT_X = new CCVector3(-1, 0, 0);

    /**
     * 0, 1, 0
     */
    public final static CCVector3 UNIT_Y = new CCVector3(0, 1, 0);

    /**
     * 0, -1, 0
     */
    public final static CCVector3 NEG_UNIT_Y = new CCVector3(0, -1, 0);

    /**
     * 0, 0, 1
     */
    public final static CCVector3 UNIT_Z = new CCVector3(0, 0, 1);

    /**
     * 0, 0, -1
     */
    public final static CCVector3 NEG_UNIT_Z = new CCVector3(0, 0, -1);
    
    public static CCVector3 add(final CCVector3 ...theVector3fs){
    	CCVector3 myResult = new CCVector3();
		for(CCVector3 myVector:theVector3fs){
			myResult.addLocal(myVector);
		}
		return myResult;
	}
    
    /**
     *  If the input vectors are v0, v1, and v2, then the Gram-Schmidt
     * orthonormalization produces vectors u0, u1, and u2 as follows,
     *<pre>
     *   u0 = v0/|v0|
     *   u1 = (v1-(u0*v1)u0)/|v1-(u0*v1)u0|
     *   u2 = (v2-(u0*v2)u0-(u1*v2)u1)/|v2-(u0*v2)u0-(u1*v2)u1|
     *</pre>
     * where |A| indicates length of vector A and A*B indicates dot
     * product of vectors A and B.
     * @param vec0
     * @param vec1
     * @param vec2
     */
    public final static void orthonormalize (CCVector3 vec0, CCVector3 vec1, CCVector3 vec2){
        
        // Compute u0.
        vec0.normalize();

        // Compute u1.
        double dot0 = vec0.dot(vec1); 
        vec1.set(
        	-dot0 * vec0.x, 
        	-dot0 * vec0.y, 
        	-dot0 * vec0.z
        );
        vec1.normalize();

        // Compute u2.
        double dot1 = vec1.dot(vec2);
        dot0 = vec0.dot(vec2);
        vec1.set(
        	-(dot0 * vec0.x + dot1 * vec1.x), 
        	-(dot0 * vec0.y + dot1 * vec1.y), 
        	-(dot0 * vec0.z + dot1 * vec1.z)
        );
        vec2.normalize();
    }

	public final static void orthonormalize(CCVector3... theVectors) {
		orthonormalize(theVectors[0], theVectors[1], theVectors[2]);
	}

	public final static void generateOrthonormalBasis(CCVector3 vec0, CCVector3 vec1, CCVector3 vec2) {
		vec2.normalize();
		generateComplementBasis(vec0, vec1, vec2);
	}
   
	public final static void generateComplementBasis(CCVector3 vec0, CCVector3 vec1, final CCVector3 vec2) {
		double invLength;

		if (CCMath.abs(vec2.x) >= CCMath.abs(vec2.y)) {
			// vec2.x or vec2.z is the largest magnitude component, swap them
			invLength = 1.0f / CCMath.sqrt(vec2.x * vec2.x + vec2.z * vec2.z);
			vec0.x = -vec2.z * invLength;
			vec0.y = 0.0f;
			vec0.z = +vec2.x * invLength;
			vec1.x = vec2.y * vec0.z;
			vec1.y = vec2.z * vec0.x - vec2.x * vec0.z;
			vec1.z = -vec2.y * vec0.x;
		} else {
			// vec2.y or vec2.z is the largest magnitude component, swap them
			invLength = 1.0f / CCMath.sqrt(vec2.y * vec2.y + vec2.z * vec2.z);
			vec0.x = 0.0f;
			vec0.y = +vec2.z * invLength;
			vec0.z = -vec2.y * invLength;
			vec1.x = vec2.y * vec0.z - vec2.z * vec0.y;
			vec1.y = -vec2.x * vec0.z;
			vec1.z = vec2.x * vec0.y;
		}
	}
	
	/**
	 * Calculates the normal to the plane defined by the three given vectors
	 * @param theV1 vector1 of the plane
	 * @param theV2 vector2 of the plane
	 * @param theV3 vector3 of the plane
	 * @return normal of the plane
	 */
	public static CCVector3 normal(final CCVector3 theV1, final CCVector3 theV2, final CCVector3 theV3) {
		CCVector3 v21 = theV2.subtract(theV1);
		CCVector3 v31 = theV3.subtract(theV1);
		
		return v21.cross(v31).normalizeLocal();
	}
	
	/**
	 * Reflects the Vector according to the given normal
	 * @param theVector the vector to reflect
	 * @param theNormal the normal to use for reflection
	 * @return the Reflected input vector
	 */
	public static CCVector3 reflect(CCVector3 theVector, CCVector3 theNormal){
		double myDot = theVector.dot(theNormal);
		CCVector3 myResult = theNormal.clone();
		myResult.multiplyLocal(-2 * myDot);
		myResult.addLocal(theVector);
		return myResult;
	}
	
	/**
	 * Compute the angular separation between two vectors.
	 * <p>
	 * This method computes the angular separation between two vectors using the
	 * dot product for well separated vectors and the cross product for almost
	 * aligned vectors. This allow to have a good accuracy in all cases, even
	 * for vectors very close to each other.
	 * </p>
	 * 
	 * @param v1
	 *            first vector
	 * @param v2
	 *            second vector
	 * @return angular separation between v1 and v2
	 * @exception ArithmeticException
	 *                if either vector has a null norm
	 */
	public static double angle(final CCVector3 v1, final CCVector3 v2) {

		final double normProduct = v1.length() * v2.length();
		if (normProduct == 0) {
			throw new ArithmeticException("null norm");
		}

		final double dot = v1.dot(v2);
		final double threshold = normProduct * 0.9999f;
		if ((dot < -threshold) || (dot > threshold)) {
			// the vectors are almost aligned, compute using the sine
			final CCVector3 v3 = v1.cross(v2);
			if (dot >= 0) {
				return CCMath.asin(v3.length() / normProduct);
			}
			return CCMath.PI - CCMath.asin(v3.length() / normProduct);
		}

		// the vectors are sufficiently separated to use the cosine
		return CCMath.acos(dot / normProduct);
	}

	@CCProperty(name = "x")
    public double x = 0f;
	@CCProperty(name = "y")
	public double y = 0f;
	@CCProperty(name = "z")
	public double z = 0f;

    /**
     * Constructs a new vector set to (0, 0, 0).
     */
    public CCVector3() {
        this(0, 0, 0);
    }

    /**
     * Constructs a new vector set to the (x, y, z) values of the given source vector.
     * 
     * @param theSource
     */
    public CCVector3(final CCVector2 theSource) {
        this(theSource.x, theSource.y, 0);
    }

    /**
     * Constructs a new vector set to the (x, y, z) values of the given source vector.
     * 
     * @param theSource
     */
    public CCVector3(final CCVector3 theSource) {
        this(theSource.x, theSource.y, theSource.z);
    }
    
    /**
     * Constructs a new vector set to the (x, y, z) values of the given source vector.
     * 
     * @param theSource
     */
    public CCVector3(final CCVector4 theSource) {
        this(theSource.x, theSource.y, theSource.z);
    }
    
    /**
     * Constructs a new vector set to (x, y, z).
     * 
     * @param theX
     * @param theY
     * @param theZ
     */
    public CCVector3(final double theX, final double theY, final double theZ) {
        x = theX;
        y = theY;
        z = theZ;
    }

    /**
     * Constructs a new vector set to (x, y).
     * 
     * @param theX
     * @param theY
     */
    public CCVector3(final double theX, final double theY) {
       this(theX, theY, 0.0f);
    }

    

    /**
     * @param theIndex
     * @return x value if index == 0, y value if index == 1 or z value if index == 2
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1, 2.
     */
    public double getValue(final int theIndex) {
        switch (theIndex) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }

    /**
     * @param theIndex
     *            which field index in this vector to set.
     * @param theValue
     *            to set to one of x, y or z.
     * @throws IllegalArgumentException
     *             if index is not one of 0, 1, 2.
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
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }

    /**
     * Stores the double values of this vector in the given double array.
     * 
     * @param theStore
     *            if null, a new double[3] array is created.
     * @return the double array
     * @throws ArrayIndexOutOfBoundsException
     *             if store is not at least length 3.
     */
    public double[] toArray(double[] theStore) {
        if (theStore == null) {
            theStore = new double[3];
        }

        // do last first to ensure size is correct before any edits occur.
        theStore[2] = z;
        theStore[1] = y;
        theStore[0] = x;
        return theStore;
    }
    
    public double[] toArray(){
    	return toArray(null);
    }

    /**
     * Sets the value of this vector to (x, y, z)
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector for chaining
     */
    public CCVector3 set(final double theX, final double theY, final double theZ) {
        x = theX;
        y = theY;
        z = theZ;
        return this;
    }

    /**
     * Sets the value of this vector to the (x, y, z) values of the provided source vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector3 set(final CCVector3 theSource) {
        x = theSource.x;
        y = theSource.y;
        z = theSource.z;
        return this;
    }

    /**
     * Sets the value of this vector to (0, 0, 0)
     * 
     * @return this vector for chaining
     */
    public CCVector3 zero() {
        return set(0, 0, 0);
    }

    /**
     * Adds the given values to those of this vector and returns them in store * @param store the vector to store the
     * result in for return. If null, a new vector object is created and returned. .
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return (this.theX + x, this.y + y, this.z + z)
     */
    public CCVector3 add(final double theX, final double theY, final double theZ, CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
        theStore.set(
        	x + theX, 
        	y + theY, 
        	z + theZ
        );
        return theStore;
    }
    
    public CCVector3 add(final double theX, final double theY, final double theZ){
    	return add(theX, theY, theZ, null);
    }

    /**
     * Increments the values of this vector with the given x, y and z values.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector for chaining
     */
    public CCVector3 addLocal(final double theX, final double theY, final double theZ) {
        return add(theX, theY, theZ, this);
    }

    /**
     * Adds the values of the given source vector to those of this vector and returns them in store.
     * 
     * @param theSource
     * @return (this.x + source.x, this.y + source.y, this.z + source.z)
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector3 add(final CCVector3 theSource, CCVector3 theStore) {
        return add(theSource.x, theSource.y, theSource.z, theStore);
    }
    
    public CCVector3 add(final CCVector3 theSource){
    	return add(theSource, null);
    }

    /**
     * Increments the values of this vector with the x, y and z values of the given vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector3 addLocal(final CCVector3 theSource) {
        return add(theSource, this);
    }

    /**
     * Subtracts the given values from those of this vector and returns them in store.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @param store
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return (this.x - x, this.y - y, this.z - z)
     */
	public CCVector3 subtract(final double theX, final double theY, final double theZ, CCVector3 theStore) {
		if (theStore == null)
			theStore = new CCVector3();
		
		theStore.set(
			x - theX, 
			y - theY, 
			z - theZ
		);
		return theStore;
	}
	
	public CCVector3 subtract(final double theX, final double theY, final double theZ){
		return subtract(theX, theY, theZ, null);
	}

    /**
     * Decrements the values of this vector by the given x, y and z values.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector for chaining
     */
    public CCVector3 subtractLocal(final double theX, final double theY, final double theZ) {
        return subtract(theX, theY, theZ, this);
    }

    /**
     * Subtracts the values of the given source vector from those of this vector and returns them in store.
     * 
     * @param theSource
     * @return (this.x - source.x, this.y - source.y, this.z - source.z)
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector3 subtract(final CCVector3 theSource, CCVector3 theStore) {
        return subtract(theSource.x, theSource.y, theSource.z, theStore);
    }
    
    public CCVector3 subtract(final CCVector3 theSource){
    	return subtract(theSource, null);
    }

    /**
     * Decrements the values of this vector by the x, y and z values from the given source vector.
     * 
     * @param theSource
     * @return this vector for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCVector3 subtractLocal(final CCVector3 theSource) {
        return subtract(theSource, this);
    }

    /**
     * Multiplies the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return a new vector (this.x * scale.x, this.y * scale.y, this.z * scale.z)
     */
    public CCVector3 multiply(final double theX, final double theY, final double theZ, CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
    	theStore.set(
    		x * theX, 
    		y * theY, 
    		z * theZ
    	);
    	return theStore;
    }
    
    public CCVector3 multiply(final double theX, final double theY, final double theZ){
    	return multiply(theX, theY, theZ, null);
    }

    /**
     * Multiplies the values of this vector by the given scalar value and returns the result in store.
     * 
     * @param theScalar
     * @return a new vector (this.x * scalar, this.y * scalar, this.z * scalar)
     */
    public CCVector3 multiply(final double theScalar, CCVector3 theStore) {
        return multiply(theScalar, theScalar, theScalar, theStore);
    }
    
    public CCVector3 multiply(final double theScalar) {
        return multiply(theScalar, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the given scalar value.
     * 
     * @param theScalar
     * @return this vector for chaining
     */
    public CCVector3 multiplyLocal(final double theScalar) {
        return multiply(theScalar, this);
    }

    /**
     * Multiplies the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theScale
     * @return a new vector (this.x * scale.x, this.y * scale.y, this.z * scale.z)
     */
    public CCVector3 multiply(final CCVector3 theScale, CCVector3 theStore) {
        return multiply(theScale.x, theScale.y, theScale.z, theStore);
    }
    
    public CCVector3 multiply(final CCVector3 theScale) {
        return multiply(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the given scale values.
     * 
     * @param scalar
     * @return this vector for chaining
     */
    public CCVector3 multiplyLocal(final CCVector3 theScale) {
        return multiply(theScale, this);
    }

    /**
     * Internally modifies the values of this vector by multiplying them each by the given scale values.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector for chaining
     */
    public CCVector3 multiplyLocal(final double theX, final double theY, final double theZ) {
        return multiply(theX, theY, theZ, this);
    }
    
    /**
     * Divides the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @param store
     *            the vector to store the result in for return. If null, a new vector object is created and returned.
     * @return a new vector (this.x / scale.x, this.y / scale.y, this.z / scale.z)
     */
    public CCVector3 divide(final double theX, final double theY, final double theZ, CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
    	theStore.set(
    		x / theX, 
    		y / theY, 
    		z / theZ
    	);
    	return theStore;
    }
    
    public CCVector3 divide(final double theX, final double theY, final double theZ){
    	return divide(theX, theY, theZ, null);
    }

    /**
     * Divides the values of this vector by the given scalar value and returns the result in store.
     * 
     * @param theScalar
     * @return a new vector (this.x / scalar, this.y / scalar, this.z / scalar)
     */
    public CCVector3 divide(final double theScalar, CCVector3 theStore) {
        return divide(theScalar, theScalar, theScalar, theStore);
    }
    
    public CCVector3 divide(final double theScalar) {
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
    public CCVector3 divideLocal(final double theScalar) {
        return divide(theScalar, this);
    }

    /**
     * Divides the values of this vector by the given scale values and returns the result in store.
     * 
     * @param theScale
     * @return a new vector (this.x / scale.x, this.y / scale.y, this.z / scale.z)
     */
    public CCVector3 divide(final CCVector3 theScale, CCVector3 theStore) {
        return divide(theScale.x, theScale.y, theScale.z, theStore);
    }
    
    public CCVector3 divide(final CCVector3 theScale) {
        return divide(theScale, null);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the given scale values.
     * 
     * @param theScale
     * @return this vector for chaining
     */
    public CCVector3 divideLocal(final CCVector3 theScale) {
        return divide(theScale, this);
    }

    /**
     * Internally modifies the values of this vector by dividing them each by the given scale values.
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector for chaining
     */
    public CCVector3 divideLocal(final double theX, final double theY, final double theZ) {
        return divide(theX, theY, theZ, this);
    }
    
    /**
     * Scales this vector by multiplying its values with a given scale value, then adding a given "add" value. The
     * result is store in the given store parameter.
     * 
     * @param theScale
     *            the value to multiply by.
     * @param theAdd
     *            the value to add
     * @return the store variable
     */
    public CCVector3 scaleAdd(final double theScale, final CCVector3 theAdd, CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
    	theStore.set(
    		x * theScale + theAdd.x,
    		y * theScale + theAdd.y,
    		z * theScale + theAdd.z
        );
        return theStore;
    }
    
    public CCVector3 scaleAdd(final double theScale, final CCVector3 theAdd) {
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
    public CCVector3 scaleAddLocal(final double theScale, final CCVector3 theAdd) {
        return scaleAdd(theScale, theAdd, this);
    }

    /**
     * @param theStore
     * @return same as multiply(-1)
     */
    public CCVector3 negate(CCVector3 theStore) {
        return multiply(-1, theStore);
    }
    
    public CCVector3 negate(){
    	return negate(null);
    }

    /**
     * @return same as multiplyLocal(-1)
     */
    public CCVector3 negateLocal() {
        return negate(this);
    }

    /**
     * Creates a new unit length vector from this one by dividing by length. If the length is 0, (ie, if the vector is
     * 0, 0, 0) then a new vector (0, 0, 0) is returned.
     * 
     * @return a new unit vector (or 0, 0, 0 if this unit is 0 length)
     */
    public CCVector3 normalize(CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
        final double lengthSq = lengthSquared();
        if (CCMath.abs(lengthSq) > CCMath.FLT_EPSILON) {
            return multiply(1 / CCMath.sqrt(lengthSq), theStore);
        }

        return theStore.set(CCVector3.ZERO);
    }
    
    public CCVector3 normalize(){
    	return normalize(null);
    }

    /**
     * Converts this vector into a unit vector by dividing it internally by its length. If the length is 0, (ie, if the
     * vector is 0, 0, 0) then no action is taken.
     * 
     * @return this vector for chaining
     */
    public CCVector3 normalizeLocal() {
    	return normalize(this);
    }
    
    /**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector3 randomize(){
		do{
			x = CCMath.random() * 2.0F - 1.0F;
			y = CCMath.random() * 2.0F - 1.0F;
			z = CCMath.random() * 2.0F - 1.0F;
		}while (lengthSquared() > 1.0F);
		return this;
	}

	/**
	 * Sets a position randomly distributed inside a sphere of unit radius
	 * centered at the origin.  Orientation will be random and length will range
	 * between 0 and 1
	 */
	public CCVector3 randomize(double radius){
		do{
			x = radius * (CCMath.random() * 2.0F - 1.0F);
			y = radius * (CCMath.random() * 2.0F - 1.0F);
			z = radius * (CCMath.random() * 2.0F - 1.0F);
		}while (lengthSquared() > radius * radius);
		return this;
	}
	
	/**
	 * given a vector, return a vector perpendicular to it. arbitrarily selects
	 * one of the infinitely many perpendicular vectors. a zero vector maps to
	 * itself, otherwise length is irrelevant (empirically, output length seems
	 * to remain within 20% of input length).
	 */
	public CCVector3 perp(){
		// to be filled in:
		CCVector3 quasiPerp; // a direction which is "almost perpendicular"

		// three mutually perpendicular basis vectors

		// measure the projection of "direction" onto each of the axes
		final double id = UNIT_X.dot(this);
		final double jd = UNIT_Y.dot(this);
		final double kd = UNIT_Z.dot(this);

		// set quasiPerp to the basis which is least parallel to "direction"
		if ((id <= jd) && (id <= kd)){
			//	projection onto i was the smallest
			quasiPerp = UNIT_X; 
		}else{
			if ((jd <= id) && (jd <= kd)){
				//projection onto j was the smallest
				quasiPerp = UNIT_Y; 
			}else{
				//projection onto k was the smallest
				quasiPerp = UNIT_Z; 
			}
		}

		// return the cross product (direction x quasiPerp)
		// which is guaranteed to be perpendicular to both of them
		return cross(quasiPerp);
	}


    /**
     * Performs a linear interpolation between this vector and the given end vector, using the given scalar as a
     * percent. iow, if changeAmnt is closer to 0, the result will be closer to the current value of this vector and if
     * it is closer to 1, the result will be closer to the end value. The result is returned as a new vector object.
     * 
     * @param theEndVector
     * @param theScalar
     * @return a new vector as described above.
     * @throws NullPointerException
     *             if endVec is null.
     */
   public CCVector3 lerp(final CCVector3 theEndVector, final double theScalar, CCVector3 theStore) {
	   return lerp(this, theEndVector, theScalar, theStore);
    }
   
   	public CCVector3 lerp(final CCVector3 theEndVector, final double theScalar){
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
    public CCVector3 lerpLocal(final CCVector3 theEndVector, final double theScalar) {
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
     * @return a new vector as described above.
     * @throws NullPointerException
     *             if beginVec or endVec are null.
     */
    public static CCVector3 lerp(final CCVector3 theBeginVector, final CCVector3 theEndVector, final double theScalar, CCVector3 theStore) {
    	if(theStore == null) theStore = new CCVector3();
    	
    	if(theBeginVector.equals(theEndVector))return theStore.set(theBeginVector);

    	return theStore.set(
    		(1.0f - theScalar) * theBeginVector.x + theScalar * theEndVector.x,
            (1.0f - theScalar) * theBeginVector.y + theScalar * theEndVector.y,
            (1.0f - theScalar) * theBeginVector.z + theScalar * theEndVector.z
        );
    }
    
    public static CCVector3 lerp(final CCVector3 theBeginVector, final CCVector3 theEndVector, final double theScalar){
    	return lerp(theBeginVector, theEndVector, theScalar, null);
    }

    /**
     * Performs a linear interpolation between the given begin and end vectors, using the given scalar as a percent.
     * iow, if changeAmnt is closer to 0, the result will be closer to the begin value and if it is closer to 1, the
     * result will be closer to the end value. The result is stored back in this vector.
     * 
     * @param theBeginVector
     * @param theEndVector
     * @param theScalar
     * @return this vector for chaining
     * @throws NullPointerException
     *             if beginVec or endVec are null.
     */
    public CCVector3 lerpLocal(final CCVector3 theBeginVector, final CCVector3 theEndVector, final double theScalar) {
        return lerp(theBeginVector, theEndVector, theScalar, this);
    }
    
    /**
	 * Evaluates quadratic bezier at point t for points a, b, c, d.
	 * t varies between 0 and 1, and a and d are the on curve points,
	 * b and c are the control points. this can be done once with the
	 * x coordinates and a second time with the y coordinates to get
	 * the location of a bezier curve at t.
	 * <P>
	 * For instance, to convert the following example:<PRE>
	 * stroke(255, 102, 0);
	 * line(85, 20, 10, 10);
	 * line(90, 90, 15, 80);
	 * stroke(0, 0, 0);
	 * bezier(85, 20, 10, 10, 90, 90, 15, 80);
	 *
	 * // draw it in gray, using 10 steps instead of the default 20
	 * // this is a slower way to do it, but useful if you need
	 * // to do things with the coordinates at each step
	 * stroke(128);
	 * beginShape(LINE_STRIP);
	 * for (int i = 0; i <= 10; i++) {
	 *   double t = i / 10.0f;
	 *   double x = bezierPoint(85, 10, 90, 15, t);
	 *   double y = bezierPoint(20, 10, 90, 80, t);
	 *   vertex(x, y);
	 * }
	 * endShape();</PRE>
	 */
	 public static CCVector3 bezierPoint(
		final CCVector3 theStartPoint, final CCVector3 theStartAnchor, 
		final CCVector3 theEndAnchor, final CCVector3 theEndPoint, 
		final double t
	) {
		double t1 = 1.0f - t;
		return new CCVector3(
			theStartPoint.x * t1 * t1 * t1 + 3 * theStartAnchor.x * t * t1 * t1 + 3 * theEndAnchor.x * t * t * t1 + theEndPoint.x * t * t * t,
			theStartPoint.y * t1 * t1 * t1 + 3 * theStartAnchor.y * t * t1 * t1 + 3 * theEndAnchor.y * t * t * t1 + theEndPoint.y * t * t * t,
			theStartPoint.z * t1 * t1 * t1 + 3 * theStartAnchor.z * t * t1 * t1 + 3 * theEndAnchor.z * t * t * t1 + theEndPoint.z * t * t * t
		);
	}
	 
	/**
	 * Interpolate a spline between at least 4 control points following the
	 * Catmull-Rom equation. here is the interpolation matrix m = [ 0.0 1.0 0.0
	 * 0.0 ] [-T 0.0 T 0.0 ] [ 2T T-3 3-2T -T ] [-T 2-T T-2 T ] where T is the
	 * tension of the curve the result is a value between p1 and p2, t=0 for p1,
	 * t=1 for p2
	 * 
	 * @param theU
	 *            value from 0 to 1
	 * @param theT
	 *            The tension of the curve
	 * @param theP0
	 *            control point 0
	 * @param theP1
	 *            control point 1
	 * @param theP2
	 *            control point 2
	 * @param theP3
	 *            control point 3
	 * @param store
	 *            a Vector3f to store the result
	 * @return catmull-Rom interpolation
	 */
	public static CCVector3 catmulRomPoint(CCVector3 theP0, CCVector3 theP1, CCVector3 theP2, CCVector3 theP3, double theU, double theT) {
        return new CCVector3(
        	CCMath.catmullRomBlend(theP0.x, theP1.x, theP2.x, theP3.x, theU, theT),
        	CCMath.catmullRomBlend(theP0.y, theP1.y, theP2.y, theP3.y, theU, theT),
        	CCMath.catmullRomBlend(theP0.z, theP1.z, theP2.z, theP3.z, theU, theT)
        );
	}

    /**
     * @return the magnitude or distance between the origin (0, 0, 0) and the point described by this vector (x, y, z).
     *         Effectively the square root of the value returned by {@link #lengthSquared()}.
     */
    public double length() {
        return CCMath.sqrt(lengthSquared());
    }

    /**
     * @return the squared magnitude or squared distance between the origin (0, 0, 0) and the point described by this
     *         vector (x, y, z)
     */
    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @return the squared distance between the point described by this vector and the given x, y, z point. When
     *         comparing the relative distance between two points it is usually sufficient to compare the squared
     *         distances, thus avoiding an expensive square root operation.
     */
    public double distanceSquared(final double theX, final double theY, final double theZ) {
        final double dx = x - theX;
        final double dy = y - theY;
        final double dz = z - theZ;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * @param destination
     * @return the squared distance between the point described by this vector and the given destination point. When
     *         comparing the relative distance between two points it is usually sufficient to compare the squared
     *         distances, thus avoiding an expensive square root operation.
     * @throws NullPointerException
     *             if destination is null.
     */
    public double distanceSquared(final CCVector3 destination) {
        return distanceSquared(destination.x, destination.y, destination.z);
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @return the distance between the point described by this vector and the given x, y, z point.
     */
    public double distance(final double theX, final double theY, final double theZ) {
        return CCMath.sqrt(distanceSquared(theX, theY, theZ));
    }

    /**
     * @param theDestination
     * @return the distance between the point described by this vector and the given destination point.
     * @throws NullPointerException
     *             if destination is null.
     */
    public double distance(final CCVector3 theDestination) {
        return CCMath.sqrt(distanceSquared(theDestination));
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @return the dot product of this vector with the given x, y, z values.
     */
    public double dot(final double theX, final double theY, final double theZ) {
        return x * theX + y * theY + z * theZ;
    }

    /**
     * @param theVector
     * @return the dot product of this vector with the x, y, z values of the given vector.
     * @throws NullPointerException
     *             if vec is null.
     */
    public double dot(final CCVector3 theVector) {
        return dot(theVector.x, theVector.y, theVector.z);
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @return the cross product of this vector with the given x, y, z values.
     */
    public CCVector3 cross(final double theX, final double theY, final double theZ, CCVector3 theStore) {
    	if(theStore == null)theStore = new CCVector3();
        
    	return theStore.set(
    		y * theZ - z * theY,
    		z * theX - x * theZ,
    		x * theY - y * theX
        );
    }
    
    public CCVector3 cross(final double theX, final double theY, final double theZ){
    	return cross(theX, theY, theZ, null);
    }
    
    public CCVector3 cross(final CCVector3 theVector, CCVector3 theStore) {
        return cross(theVector.x, theVector.y, theVector.z, theStore);
    }

    /**
     * @param theVector
     * @return the cross product of this vector with the given vector's x, y, z values
     * @throws NullPointerException
     *             if vec is null.
     */
    public CCVector3 cross(final CCVector3 theVector) {
        return cross(theVector, null);
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @return this vector, set to the cross product of this vector with the given x, y, z values.
     */
    public CCVector3 crossLocal(final double theX, final double theY, final double theZ) {
        final double newX = y * theZ - z * theY;
        final double newY = z * theX - x * theZ;
        final double newZ = x * theY - y * theX;
        set(newX, newY, newZ);
        return this;
    }

    /**
     * @param vec
     * @return this vector, set to the cross product of this vector with the given vector's x, y, z values
     * @throws NullPointerException
     *             if vec is null.
     */
    public CCVector3 crossLocal(final CCVector3 vec) {
        return crossLocal(vec.x, vec.y, vec.z);
    }

    /**
     * @param theOtherVector
     *            a unit vector to find the angle against
     * @return the minimum angle (in radians) between two vectors. It is assumed that both this vector and the given
     *         vector are unit vectors (normalized).
     * @throws NullPointerException
     *             if otherVector is null.
     */
    public double smallestAngleBetween(final CCVector3 theOtherVector) {
        return CCMath.acos(dot(theOtherVector));
    }

    /**
     * Check a vector... if it is null or its doubles are NaN or infinite, return false. Else return true.
     * 
     * @param theVector
     *            the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValid(final CCVector3 theVector) {
        if (theVector == null)return false;
        if (Double.isNaN(theVector.x) || Double.isNaN(theVector.y) || Double.isNaN(theVector.z)) {
            return false;
        }
        if (isInfinite(theVector)) {
            return false;
        }
        return true;
    }
    
    /**
     * Return rotation of this vector interpreted as point around the vector (u,v,w)
     * @param theX
     * @param theY
     * @param theZ
     * @param theAngle
     * 
     * @return the rotated vector
     */
    public CCVector3 rotate(final double theX, final double theY, final double theZ, final double theAngle){
	    double ux = theX * x; double uy = theX * y; double uz = theX * z;
	    double vx = theY * x; double vy = theY * y; double vz = theY * z;
	    double wx = theZ * x; double wy = theZ * y; double wz = theZ * z;
	    
	    double sa = CCMath.sin(theAngle);
	    double ca = CCMath.cos(theAngle);
	    
	    CCVector3 ret = new CCVector3();
	    ret.x = theX * (ux + vy + wz) + (x * (theY * theY + theZ * theZ) - theX * (vy + wz)) * ca + (-wy + vz) * sa;
	    ret.y = theY * (ux + vy + wz) + (y * (theX * theX + theZ * theZ) - theY * (ux + wz)) * ca + ( wx - uz) * sa;
	    ret.z = theZ * (ux + vy + wz) + (z * (theX * theX + theY * theY) - theZ * (ux + vy)) * ca + (-vx + uy) * sa;
	    
	    return ret;
    }

    /**
     * Check if a vector is non-null and has infinite values.
     * 
     * @param theVector
     *            the vector to check
     * @return true or false as stated above.
     */
    public static boolean isInfinite(final CCVector3 theVector) {
        if (theVector == null) {
            return false;
        }
        if (Double.isInfinite(theVector.x) || Double.isInfinite(theVector.y) || Double.isInfinite(theVector.z)) {
            return true;
        }
        return false;
    }

    /**
     * @return the string representation of this vector.
     */
    @Override
    public String toString() {
        return getClass().getName() + " [X=" + x + ", Y=" + y + ", Z=" + z + "]";
    }

    /**
     * @return returns a unique code for this vector object based on its values. If two vectors are numerically equal,
     *         they will return the same hash code value.
     */
    @Override
    public int hashCode() {
        int result = 65;

        final long myX = Double.doubleToLongBits(x);
        result += 63 * result + (int) (myX ^ myX >>> 64);

        final long myY = Double.doubleToLongBits(y);
        result += 63 * result + (int) (myY ^ myY >>> 64);

        final long myZ = Double.doubleToLongBits(z);
        result += 63 * result + (int) (myZ ^ myZ >>> 64);

        return result;
    }

    /**
     * @param theObject
     *            the object to compare for equality
     * @return true if this vector and the provided vector have the same x, y and z values.
     */
    @Override
    public boolean equals(final Object theObject) {
        if (this == theObject) {
            return true;
        }
        if (!(theObject instanceof CCVector3)) {
            return false;
        }
        final CCVector3 comp = (CCVector3) theObject;
        return 
        	CCMath.abs(x - comp.x) < ALLOWED_DEVIANCE && 
        	CCMath.abs(y - comp.y) < ALLOWED_DEVIANCE && 
        	CCMath.abs(z - comp.z) < ALLOWED_DEVIANCE;
    }

    // /////////////////
    // Method for Cloneable
    // /////////////////

    @Override
    public CCVector3 clone() {
        return new CCVector3(this);
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
    public void readExternal(final ObjectInput theInput) throws IOException, ClassNotFoundException {
        x = theInput.readFloat();
        y = theInput.readFloat();
        z = theInput.readFloat();
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
    }

	@Override
	public Map<String, Object> toDataObject(CCDataHolder<?, ?> theHolder) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("x", x);
		ret.put("y", y);
		ret.put("z", z);
		
		return ret;
	}

	public CCVector2 xy() {
		return new CCVector2(x, y);
	}    
}
