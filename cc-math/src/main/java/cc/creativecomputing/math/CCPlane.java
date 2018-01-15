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
 * A representation of a mathematical plane using a normal vector and a plane
 * constant (d) whose absolute value represents the distance from the origin to
 * the plane. It is generally calculated by taking a point (X) on the plane and
 * finding its dot-product with the plane's normal vector. iow: d = N dot X
 */
public class CCPlane implements Cloneable, Externalizable {

	public enum Side {
		/**
		 * On the side of the plane opposite of the plane's normal vector.
		 */
		Inside,

		/**
		 * On the same side of the plane as the plane's normal vector.
		 */
		Outside,

		/**
		 * Not on either side - in other words, on the plane itself.
		 */
		Neither
    }

	private static final long serialVersionUID = 1L;

	public static final CCPlane XZ = new CCPlane(CCVector3.UNIT_Y, 0);
	public static final CCPlane XY = new CCPlane(CCVector3.UNIT_Z, 0);
	public static final CCPlane YZ = new CCPlane(CCVector3.UNIT_X, 0);

	@CCProperty(name = "normal")
	protected final CCVector3 _myNormal = new CCVector3();
	@CCProperty(name = "constant")
	protected double _myConstant = 0;

	/**
	 * Constructs a new plane with a normal of (0, 1, 0) and a constant value of
	 * 0.
	 */
	public CCPlane() {
		this(CCVector3.UNIT_Y, 0);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param theSource
	 *            the plane to copy from.
	 */
	public CCPlane(final CCPlane theSource) {
		this(theSource.normal(), theSource.constant());
	}

	/**
	 * Constructs a new plane using the supplied normal vector and plane
	 * constant
	 * 
	 * @param normal
	 * @param theConstant
	 */
	public CCPlane(final CCVector3 theNormal, final double theConstant) {
		_myNormal.set(theNormal);
		_myConstant = theConstant;
	}
	
	/**
	 * Creates a plane based on a normal and an origin point lying on the plane
	 * @param theOrigin point on the plane
	 * @param theNormal normal of the plane
	 */
	public CCPlane(CCVector3 theOrigin, CCVector3 theNormal) {
		setOriginNormal(theOrigin, theNormal);
	}
	
	public CCPlane(final CCVector3 theV1, final CCVector3 theV2, final CCVector3 theV3){
		this();
		setPlanePoints(theV1, theV2, theV3);
	}

	public double constant() {
		return _myConstant;
	}

	/**
	 * 
	 * @return normal as a readable vector
	 */
	public CCVector3 normal() {
		return _myNormal;
	}

	/**
	 * Sets the value of this plane to the constant and normal values of the
	 * provided source plane.
	 * 
	 * @param theSource
	 * @return this plane for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCPlane set(final CCPlane theSource) {
		setConstant(theSource.constant());
		setNormal(theSource.normal());
		return this;
	}

	/**
	 * Sets the constant value of this plane to the given double value.
	 * 
	 * @param theConstant
	 */
	public void setConstant(final double theConstant) {
		_myConstant = theConstant;
	}

	/**
	 * Sets the plane normal to the values of the given vector.
	 * 
	 * @param theNormal
	 * @throws NullPointerException
	 *             if normal is null.
	 */
	public void setNormal(final CCVector3 theNormal) {
		_myNormal.set(theNormal);
	}

	/**
	 * @param thePoint
	 * @return the distance from this plane to a provided point. If the point is
	 *         on the negative side of the plane the distance returned is
	 *         negative, otherwise it is positive. If the point is on the plane,
	 *         it is zero.
	 * @throws NullPointerException
	 *             if point is null.
	 */
	public double pseudoDistance(final CCVector3 thePoint) {
		return _myNormal.dot(thePoint) - _myConstant;
	}
	
	public double distance(final CCVector3 thePoint){
		return CCMath.abs(pseudoDistance(thePoint));
	}

	/**
	 * @param thePoint
	 * @return the side of this plane on which the given point lies.
	 * @see Side
	 * @throws NullPointerException
	 *             if point is null.
	 */
	public Side whichSide(final CCVector3 thePoint) {
		final double dis = pseudoDistance(thePoint);
		if (dis < 0) {
			return Side.Inside;
		} else if (dis > 0) {
			return Side.Outside;
		} else {
			return Side.Neither;
		}
	}
	
	/**
     * Initialize this plane using a point of origin and a normal.
     *
     * @param theOrigin
     * @param theNormal
     */
    public void setOriginNormal(CCVector3 theOrigin, CCVector3 theNormal){
        _myNormal.set(theNormal);
        _myConstant = theNormal.x * theOrigin.x + theNormal.y * theOrigin.y + theNormal.z * theOrigin.z;
    }

	/**
	 * Sets this plane to the plane defined by the given three points.
	 * 
	 * @param theA
	 * @param theB
	 * @param theC
	 * @return this plane for chaining
	 * @throws NullPointerException
	 *             if one or more of the points are null.
	 */
	public CCPlane setPlanePoints(final CCVector3 theA, final CCVector3 theB, final CCVector3 theC) {
		_myNormal.set(theB).subtractLocal(theA);
		_myNormal.crossLocal(theC.x - theA.x, theC.y - theA.y, theC.z - theA.z).normalizeLocal();
		_myConstant = _myNormal.dot(theA);
		return this;
	}

	/**
	 * Reflects an incoming vector across the normal of this Plane.
	 * 
	 * @param theUnitVector
	 *            the incoming vector. Must be a unit vector.
	 * @param theStore
	 *            optional Vector to store the result in. May be the same as the
	 *            unitVector.
	 * @return the reflected vector.
	 */
	public CCVector3 reflectVector(final CCVector3 theUnitVector, final CCVector3 theStore) {
		CCVector3 result = theStore;
		if (result == null) {
			result = new CCVector3();
		}

		final double dotProd = _myNormal.dot(theUnitVector) * 2;
		result.set(theUnitVector).subtractLocal(_myNormal.x * dotProd, _myNormal.y * dotProd, _myNormal.z * dotProd);
		return result;
	}

	/**
	 * Check a plane... if it is null or its constant, or the doubles of its
	 * normal are NaN or infinite, return false. Else return true.
	 * 
	 * @param thePlane
	 *            the plane to check
	 * @return true or false as stated above.
	 */
	public static boolean isValid(final CCPlane thePlane) {
		if (thePlane == null) {
			return false;
		}
		if (Double.isNaN(thePlane.constant()) || Double.isInfinite(thePlane.constant())) {
			return false;
		}

		return CCVector3.isValid(thePlane.normal());
	}

	/**
	 * @return the string representation of this plane.
	 */
	@Override
	public String toString() {
		return "com.ardor3d.math.Plane [Normal: " + _myNormal + " - Constant: " + _myConstant + "]";
	}

	/**
	 * @return returns a unique code for this plane object based on its values.
	 *         If two planes are numerically equal, they will return the same
	 *         hash code value.
	 */
	@Override
	public int hashCode() {
		int result = 33;

		result += 63 * result + _myNormal.hashCode();

		final long c = Double.doubleToLongBits(constant());
		result += 63 * result + (int) (c ^ c >>> 64);

		return result;
	}

	/**
	 * @param theObject
	 *            the object to compare for equality
	 * @return true if this plane and the provided plane have the same constant
	 *         and normal values.
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}
		if (!(theObject instanceof CCPlane)) {
			return false;
		}
		final CCPlane comp = (CCPlane) theObject;
		return constant() == comp.constant() && _myNormal.equals(comp.normal());
	}

	// /////////////////
	// Method for Cloneable
	// /////////////////

	@Override
	public CCPlane clone() {
		return new CCPlane(this);
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
		setNormal((CCVector3) in.readObject());
		setConstant(in.readDouble());
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
		out.writeObject(_myNormal);
		out.writeDouble(constant());
	}
}
