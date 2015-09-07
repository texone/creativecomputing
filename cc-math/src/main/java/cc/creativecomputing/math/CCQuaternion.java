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
 * Quaternion represents a 4 value math object used in Ardor3D to describe rotations. It has the advantage of being able
 * to avoid lock by adding a 4th dimension to rotation.
 * 
 * Note: some algorithms in this class were ported from Eberly, Wolfram, Game Gems and others to Java.
 */
public class CCQuaternion implements Cloneable, Externalizable {
	
	public static CCQuaternion createFromAngleAxis(double theAngle, CCVector3 theAxis){
		return new CCQuaternion().fromAngleAxis(theAngle, theAxis);
	}

    /** Used with equals method to determine if two Quaternions are close enough to be considered equal. */
    public static final double ALLOWED_DEVIANCE = 0.000001f;

    private static final long serialVersionUID = 1L;

    /**
     * x=0, y=0, z=0, w=1
     */
    public final static CCQuaternion IDENTITY = new CCQuaternion(0, 0, 0, 1);

    @CCProperty(name = "x")
    public double x = 0;
    @CCProperty(name = "y")
    public double y = 0;
    @CCProperty(name = "z")
    public double z = 0;
    @CCProperty(name = "w")
    public double w = 1;

    /**
     * Constructs a new quaternion set to (0, 0, 0, 1).
     */
    public CCQuaternion() {
        this(CCQuaternion.IDENTITY);
    }

    /**
     * Constructs a new quaternion set to the (x, y, z, w) values of the given source quaternion.
     * 
     * @param source
     */
    public CCQuaternion(final CCQuaternion source) {
        this(source.x, source.y, source.z, source.w);
    }

    /**
     * Constructs a new quaternion set to (x, y, z, w).
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @param theW
     */
    public CCQuaternion(final double theX, final double theY, final double theZ, final double theW) {
        x = theX;
        y = theY;
        z = theZ;
        w = theW;
    }

    /**
     * Stores the double values of this quaternion in the given double array as (x,y,z,w).
     * 
     * @param store
     *            The array in which to store the values of this quaternion. If null, a new double[4] array is created.
     * @return the double array
     * @throws ArrayIndexOutOfBoundsException
     *             if store is not null and is not at least length 4
     */
    public double[] toArray(final double[] store) {
        double[] result = store;
        if (result == null) {
            result = new double[4];
        }
        result[3] = w;
        result[2] = z;
        result[1] = y;
        result[0] = x;
        return result;
    }

    /**
     * Sets the value of this quaternion to (x, y, z, w)
     * 
     * @param theX
     * @param theY
     * @param theZ
     * @param theW
     * @return this quaternion for chaining
     */
    public CCQuaternion set(final double theX, final double theY, final double theZ, final double theW) {
        x = theX;
        y = theY;
        z = theZ;
        w = theW;
        return this;
    }

    /**
     * Sets the value of this quaternion to the (x, y, z, w) values of the provided source quaternion.
     * 
     * @param source
     * @return this quaternion for chaining
     * @throws NullPointerException
     *             if source is null.
     */
    public CCQuaternion set(final CCQuaternion source) {
        x = source.x;
        y = source.y;
        z = source.z;
        w = source.w;
        return this;
    }

    /**
     * Updates this quaternion from the given Euler rotation angles, applied in the given order: heading, attitude,
     * bank.
     * 
     * @param angles
     *            the Euler angles of rotation (in radians) stored as heading, attitude, and bank.
     * @return this quaternion for chaining
     * @throws ArrayIndexOutOfBoundsException
     *             if angles is less than length 3
     * @throws NullPointerException
     *             if angles is null.
     */
    public CCQuaternion fromEulerAngles(final double[] angles) {
        return fromEulerAngles(angles[0], angles[1], angles[2]);
    }

    /**
     * Updates this quaternion from the given Euler rotation angles, applied in the given order: heading, attitude,
     * bank.
     * 
     * @param heading
     *            the Euler heading angle in radians. (rotation about the y axis)
     * @param attitude
     *            the Euler attitude angle in radians. (rotation about the z axis)
     * @param bank
     *            the Euler bank angle in radians. (rotation about the x axis)
     * @return this quaternion for chaining
     * @see <a
     *      href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm">euclideanspace.com-eulerToQuaternion</a>
     */
    public CCQuaternion fromEulerAngles(final double heading, final double attitude, final double bank) {
        double angle = heading * 0.5f;
        final double sinHeading = CCMath.sin(angle);
        final double cosHeading = CCMath.cos(angle);
        angle = attitude * 0.5f;
        final double sinAttitude = CCMath.sin(angle);
        final double cosAttitude = CCMath.cos(angle);
        angle = bank * 0.5f;
        final double sinBank = CCMath.sin(angle);
        final double cosBank = CCMath.cos(angle);

        // variables used to reduce multiplication calls.
        final double cosHeadingXcosAttitude = cosHeading * cosAttitude;
        final double sinHeadingXsinAttitude = sinHeading * sinAttitude;
        final double cosHeadingXsinAttitude = cosHeading * sinAttitude;
        final double sinHeadingXcosAttitude = sinHeading * cosAttitude;

        final double myW = cosHeadingXcosAttitude * cosBank - sinHeadingXsinAttitude * sinBank;
        final double myX = cosHeadingXcosAttitude * sinBank + sinHeadingXsinAttitude * cosBank;
        final double myY = sinHeadingXcosAttitude * cosBank + cosHeadingXsinAttitude * sinBank;
        final double myZ = cosHeadingXsinAttitude * cosBank - sinHeadingXcosAttitude * sinBank;

        set(myX, myY, myZ, myW);

        return normalizeLocal();
    }

    /**
     * Converts this quaternion to Euler rotation angles in radians (heading, attitude, bank).
     * 
     * @param store
     *            the double[] array to store the computed angles in. If null, a new double[] will be created
     * @return the double[] array, filled with heading, attitude and bank in that order..
     * @throws ArrayIndexOutOfBoundsException
     *             if non-null store is not at least length 3
     * @see <a
     *      href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm">euclideanspace.com-quaternionToEuler</a>
     * @see #fromEulerAngles(double, double, double)
     */
    public double[] toEulerAngles(final double[] store) {
        double[] result = store;
        if (result == null) {
            result = new double[3];
        } else if (result.length < 3) {
            throw new ArrayIndexOutOfBoundsException("store array must have at least three elements");
        }

        final double sqw = w * w;
        final double sqx = x * x;
        final double sqy = y * y;
        final double sqz = z * z;
        final double unit = sqx + sqy + sqz + sqw; // if normalized is one, otherwise
        // is correction factor
        final double test = x * y + z * w;
        if (test > 0.499 * unit) { // singularity at north pole
            result[0] = 2 * CCMath.atan2(x, w);
            result[1] = CCMath.HALF_PI;
            result[2] = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            result[0] = -2 * CCMath.atan2(x, w);
            result[1] = -CCMath.HALF_PI;
            result[2] = 0;
        } else {
            result[0] = CCMath.atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw);
            result[1] = CCMath.asin(2 * test / unit);
            result[2] = CCMath.atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw);
        }
        return result;
    }

    /**
     * Sets the value of this quaternion to the rotation described by the given matrix.
     * 
     * @param matrix
     * @return this quaternion for chaining
     * @throws NullPointerException
     *             if matrix is null.
     */
    public CCQuaternion fromRotationMatrix(final CCMatrix3x3 matrix) {
        return fromRotationMatrix(
        	matrix._m00, matrix._m01, matrix._m02, 
        	matrix._m10, matrix._m11, matrix._m12, 
        	matrix._m20, matrix._m21, matrix._m22
        );
    }

    /**
     * Sets the value of this quaternion to the rotation described by the given matrix values.
     * 
     * @param m00
     * @param m01
     * @param m02
     * @param m10
     * @param m11
     * @param m12
     * @param m20
     * @param m21
     * @param m22
     * @return this quaternion for chaining
     */
    public CCQuaternion fromRotationMatrix(
    	final double m00, final double m01, final double m02, 
    	final double m10, final double m11, final double m12, 
    	final double m20, final double m21, final double m22
    ) {
        // Uses the Graphics Gems code, from
        // ftp://ftp.cis.upenn.edu/pub/graphics/shoemake/quatut.ps.Z
        // *NOT* the "Matrix and Quaternions FAQ", which has errors!

        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        final double t = m00 + m11 + m22;

        // we protect the division by s by ensuring that s>=1
        double myX, myY, myZ, myW;
        if (t >= 0) { // |w| >= .5
            double s = CCMath.sqrt(t + 1); // |s|>=1 ...
            myW = 0.5f * s;
            s = 0.5f / s; // so this division isn't bad
            myX = (m21 - m12) * s;
            myY = (m02 - m20) * s;
            myZ = (m10 - m01) * s;
        } else if (m00 > m11 && m00 > m22) {
            double s = CCMath.sqrt(1.0f + m00 - m11 - m22); // |s|>=1
            myX = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            myY = (m10 + m01) * s;
            myZ = (m02 + m20) * s;
            myW = (m21 - m12) * s;
        } else if (m11 > m22) {
            double s = CCMath.sqrt(1.0f + m11 - m00 - m22); // |s|>=1
            myY = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            myX = (m10 + m01) * s;
            myZ = (m21 + m12) * s;
            myW = (m02 - m20) * s;
        } else {
            double s = CCMath.sqrt(1.0f + m22 - m00 - m11); // |s|>=1
            myZ = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            myX = (m02 + m20) * s;
            myY = (m21 + m12) * s;
            myW = (m10 - m01) * s;
        }

        return set(myX, myY, myZ, myW);
    }

    /**
     * @return the rotation matrix representation of this quaternion (normalized)
     * 
     *         if store is not null and is read only.
     */
    public CCMatrix3x3 toRotationMatrix(CCMatrix3x3 theStore) {
        if(theStore == null)theStore = new CCMatrix3x3();

        final double norm = magnitudeSquared();
        final double s = norm > 0.0f ? 2.0f / norm : 0.0f;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        final double xs = x * s;
        final double ys = y * s;
        final double zs = z * s;
        final double xx = x * xs;
        final double xy = x * ys;
        final double xz = x * zs;
        final double xw = w * xs;
        final double yy = y * ys;
        final double yz = y * zs;
        final double yw = w * ys;
        final double zz = z * zs;
        final double zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        theStore._m00 = 1.0f - (yy + zz);
        theStore._m01 = xy - zw;
        theStore._m02 = xz + yw;
        theStore._m10 = xy + zw;
        theStore._m11 = 1.0f - (xx + zz);
        theStore._m12 = yz - xw;
        theStore._m20 = xz - yw;
        theStore._m21 = yz + xw;
        theStore._m22 = 1.0f - (xx + yy);

        return theStore;
    }
    
    public CCMatrix3x3 toRotationMatrix(){
    	return toRotationMatrix(new CCMatrix3x3());
    }

    /**
     * @param store
     *            the matrix to store our result in. If null, a new matrix is created.
     * @return the rotation matrix representation of this quaternion (normalized)
     */
    public CCMatrix4x4 toRotationMatrix(final CCMatrix4x4 store) {
        CCMatrix4x4 result = store;
        if (result == null) {
            result = new CCMatrix4x4();
        }

        final double norm = magnitudeSquared();
        final double s = norm == 1.0f ? 2.0f : norm > 0.0f ? 2.0f / norm : 0f;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        final double xs = x * s;
        final double ys = y * s;
        final double zs = z * s;
        final double xx = x * xs;
        final double xy = x * ys;
        final double xz = x * zs;
        final double xw = w * xs;
        final double yy = y * ys;
        final double yz = y * zs;
        final double yw = w * ys;
        final double zz = z * zs;
        final double zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        result.m00 = 1.0f - (yy + zz);
        result.m01 = xy - zw;
        result.m02 = xz + yw;
        result.m10 = xy + zw;
        result.m11 = 1.0f - (xx + zz);
        result.m12 = yz - xw;
        result.m20 = xz - yw;
        result.m21 = yz + xw;
        result.m22 = 1.0f - (xx + yy);

        return result;
    }

    /**
     * @param index
     *            the 3x3 rotation matrix column to retrieve from this quaternion (normalized). Must be between 0 and 2.
     * @param store
     *            the vector object to store the result in. if null, a new one is created.
     * @return the column specified by the index.
     */
    public CCVector3 getRotationColumn(final int index, final CCVector3 store) {
        CCVector3 result = store;
        if (result == null) {
            result = new CCVector3();
        }

        final double norm = magnitudeSquared();
        final double s = norm == 1.0f ? 2.0f : norm > 0.0f ? 2.0f / norm : 0f;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        final double xs = x * s;
        final double ys = y * s;
        final double zs = z * s;
        final double xx = x * xs;
        final double xy = x * ys;
        final double xz = x * zs;
        final double xw = w * xs;
        final double yy = y * ys;
        final double yz = y * zs;
        final double yw = w * ys;
        final double zz = z * zs;
        final double zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 3 multiplications by 2 here
        double myX, myY, myZ;
        switch (index) {
            case 0:
                myX = 1.0f - (yy + zz);
                myY = xy + zw;
                myZ = xz - yw;
                break;
            case 1:
                myX = xy - zw;
                myY = 1.0f - (xx + zz);
                myZ = yz + xw;
                break;
            case 2:
                myX = xz + yw;
                myY = yz - xw;
                myZ = 1.0f - (xx + yy);
                break;
            default:
                throw new IllegalArgumentException("Invalid column index. " + index);
        }

        return result.set(myX, myY, myZ);
    }
    
    public CCVector3 getRotationColumn(final int index){
    	return getRotationColumn(index, new CCVector3());
    }

    /**
     * Sets the values of this quaternion to the values represented by a given angle and axis of rotation. Note that
     * this method creates an object, so use fromAngleNormalAxis if your axis is already normalized. If axis == 0,0,0
     * the quaternion is set to identity.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation.
     * @return this quaternion for chaining
     * @throws NullPointerException
     *             if axis is null
     */
    public CCQuaternion fromAngleAxis(final double angle, final CCVector3 axis) {
        final CCQuaternion quat = fromAngleNormalAxis(angle, axis.normalize());
        return quat;
    }

    /**
     * Sets the values of this quaternion to the values represented by a given angle and unit length axis of rotation.
     * If axis == 0,0,0 the quaternion is set to identity.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized - unit length).
     * @throws NullPointerException
     *             if axis is null
     */
    public CCQuaternion fromAngleNormalAxis(final double angle, final CCVector3 axis) {
        if (axis.equals(CCVector3.ZERO)) {
            return setIdentity();
        }

        final double halfAngle = 0.5f * angle;
        final double sin = CCMath.sin(halfAngle);
        final double myW = CCMath.cos(halfAngle);
        final double myX = sin * axis.x;
        final double myY = sin * axis.y;
        final double myZ = sin * axis.z;
        return set(myX, myY, myZ, myW);
    }

    /**
     * Returns the rotation angle represented by this quaternion. If a non-null vector is provided, the axis of rotation
     * is stored in that vector as well.
     * 
     * @param axisStore
     *            the object we'll store the computed axis in. If null, no computations are done to determine axis.
     * @return the angle of rotation in radians.
     */
    public double toAngleAxis(final CCVector3 axisStore) {
        final double sqrLength = x * x + y * y + z * z;
        double angle;
        if (CCMath.abs(sqrLength) <= CCMath.FLT_EPSILON) { // length is ~0
            angle = 0.0f;
            if (axisStore != null) {
                axisStore.x = 1.0f;
                axisStore.y = 0.0f;
                axisStore.z = 0.0f;
            }
        } else {
            angle = 2.0f * CCMath.acos(w);
            if (axisStore != null) {
                final double invLength = 1.0f / CCMath.sqrt(sqrLength);
                axisStore.x = x * invLength;
                axisStore.y = y * invLength;
                axisStore.z = z * invLength;
            }
        }

        return angle;
    }

    /**
     * Sets this quaternion to that which will rotate vector "from" into vector "to". from and to do not have to be the
     * same length.
     * 
     * @param from
     *            the source vector to rotate
     * @param to
     *            the destination vector into which to rotate the source vector
     * @return this quaternion for chaining
     */
    public CCQuaternion fromVectorToVector(final CCVector3 from, final CCVector3 to) {
        final CCVector3 a = from;
        final CCVector3 b = to;
        final double factor = a.length() * b.length();
        if (CCMath.abs(factor) > CCMath.FLT_EPSILON) {
            // Vectors have length > 0
            try {
                final double dot = a.dot(b) / factor;
                final double theta = CCMath.acos(CCMath.max(-1.0f, CCMath.min(dot, 1.0f)));
                final CCVector3 pivotVector = a.cross(b);
                if (dot < 0.0 && pivotVector.length() < CCMath.FLT_EPSILON) {
                    // Vectors parallel and opposite direction, therefore a rotation of 180 degrees about any vector
                    // perpendicular to this vector will rotate vector a onto vector b.
                    //
                    // The following guarantees the dot-product will be 0.0.
                    int dominantIndex;
                    if (CCMath.abs(a.x) > CCMath.abs(a.y)) {
                        if (CCMath.abs(a.x) > CCMath.abs(a.z)) {
                            dominantIndex = 0;
                        } else {
                            dominantIndex = 2;
                        }
                    } else {
                        if (CCMath.abs(a.y) > CCMath.abs(a.z)) {
                            dominantIndex = 1;
                        } else {
                            dominantIndex = 2;
                        }
                    }
                    pivotVector.setValue(dominantIndex, -a.getValue((dominantIndex + 1) % 3));
                    pivotVector.setValue((dominantIndex + 1) % 3, a.getValue(dominantIndex));
                    pivotVector.setValue((dominantIndex + 2) % 3, 0f);
                }
                return fromAngleAxis(theta, pivotVector);
            } finally {
            }
        } else {
            return setIdentity();
        }
    }

    /**
     * @return a new quaternion that represents a unit length version of this Quaternion.
     */
    public CCQuaternion normalize(CCQuaternion theStore) {
        if(theStore == null)theStore = new CCQuaternion();

        final double n = 1.0f / magnitude();
        final double myX = x * n;
        final double myY = y * n;
        final double myZ = z * n;
        final double myW = w * n;
        return theStore.set(myX, myY, myZ, myW);
    }
    
    public CCQuaternion normalize(){
    	return normalize(null);
    }

    /**
     * @return this quaternion, modified to be unit length, for chaining.
     */
    public CCQuaternion normalizeLocal() {
    	return normalize(this);
    }

    /**
     * Calculates the <i>multiplicative inverse</i> <code>Q<sup>-1</sup></code> of this quaternion <code>Q</code> such
     * that <code>QQ<sup>-1</sup> = [0,0,0,1]</code> (the identity quaternion). Note that for unit quaternions, a
     * quaternion's inverse is equal to its (far easier to calculate) conjugate.
     * 
     * @see #conjugate(CCQuaternion)
     * @return the multiplicative inverse of this quaternion.
     */
    public CCQuaternion invert() {
        final double magnitudeSQ = magnitudeSquared();
        
        if (CCMath.abs(1.0 - magnitudeSQ) <= CCMath.FLT_EPSILON) {
            return conjugate();
        } else {
            return conjugate().multiplyLocal(1.0f / magnitudeSQ);
        }
    }

    /**
     * Locally sets this quaternion <code>Q</code> to its <i>multiplicative inverse</i> <code>Q<sup>-1</sup></code> such
     * that <code>QQ<sup>-1</sup> = [0,0,0,1]</code> (the identity quaternion). Note that for unit quaternions, a
     * quaternion's inverse is equal to its (far easier to calculate) conjugate.
     * 
     * @see #conjugate(CCQuaternion)
     * 
     * @return this <code>Quaternion</code> for chaining.
     */
    public CCQuaternion invertLocal() {
        final double magnitudeSQ = magnitudeSquared();
        conjugateLocal();
        if (CCMath.abs(1.0 - magnitudeSQ) <= CCMath.FLT_EPSILON) {
            return this;
        } else {
            return multiplyLocal(1.0f / magnitudeSquared());
        }
    }

    /**
     * Creates a new quaternion that is the conjugate <code>[-x, -y, -z, w]</code> of this quaternion.
     * 
     * @param store
     *            the <code>Quaternion</code> to store the result in. If <code>null</code>, a new one is created.
     * @return the conjugate to this quaternion.
     */
    public CCQuaternion conjugate() {
        return new CCQuaternion(-x, -y, -z, w);
    }

    /**
     * Internally sets this quaternion to its conjugate <code>[-x, -y, -z, w]</code>.
     * 
     * @return this <code>Quaternion</code> for chaining.
     */
    public CCQuaternion conjugateLocal() {
        return set(-x, -y, -z, w);
    }

    /**
     * Adds this quaternion to another and places the result in the given store.
     * 
     * @param quat
     * @param store
     *            the Quaternion to store the result in. if null, a new one is created.
     * @return a quaternion representing the fields of this quaternion added to those of the given quaternion.
     */
    public CCQuaternion add(final CCQuaternion quat) {
        return new CCQuaternion(x + quat.x, y + quat.y, z + quat.z, w + quat.w);
    }

    /**
     * Internally increments the fields of this quaternion with the field values of the given quaternion.
     * 
     * @param quat
     * @return this quaternion for chaining
     */
    public CCQuaternion addLocal(final CCQuaternion quat) {
        x = x + quat.x;
        y = y + quat.y;
        z = z + quat.z;
        w = w + quat.w;
        return this;
    }

    /**
     * @param quat
     * @return a quaternion representing the fields of this quaternion subtracted from those of the given quaternion.
     */
    public CCQuaternion subtract(final CCQuaternion quat) {
        return new CCQuaternion(x - quat.x, y - quat.y, z - quat.z, w - quat.w);
    }

    /**
     * Internally decrements the fields of this quaternion by the field values of the given quaternion.
     * 
     * @param quat
     * @return this quaternion for chaining.
     */
    public CCQuaternion subtractLocal(final CCQuaternion quat) {
        x = x - quat.x;
        y = y - quat.y;
        z = z - quat.z;
        w = w - quat.w;
        return this;
    }

    /**
     * Multiplies each value of this quaternion by the given scalar value.
     * 
     * @param scalar
     *            the quaternion to multiply this quaternion by.
     * @return the resulting quaternion.
     */
    public CCQuaternion multiply(final double scalar) {
        return new CCQuaternion(scalar * x, scalar * y, scalar * z, scalar * w);
    }

    /**
     * Multiplies each value of this quaternion by the given scalar value. The result is stored in this quaternion.
     * 
     * @param scalar
     *            the quaternion to multiply this quaternion by.
     * @return this quaternion for chaining.
     */
    public CCQuaternion multiplyLocal(final double scalar) {
        x = x * scalar;
        y = y * scalar;
        z = z * scalar;
        w = w * scalar;
        return this;
    }

    /**
     * Multiplies this quaternion by the supplied quaternion. The result is stored in the given store quaternion or a
     * new quaternion if store is null.
     * 
     * It IS safe for quat and store to be the same object.
     * 
     * @param quat
     *            the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     * 
     *         if the given store is read only.
     */
    public CCQuaternion multiply(final CCQuaternion quat) {
      
        final double myX =  x * quat.w + y * quat.z - z * quat.y + w * quat.x;
        final double myY = -x * quat.z + y * quat.w + z * quat.x + w * quat.y;
        final double myZ =  x * quat.y - y * quat.x + z * quat.w + w * quat.z;
        final double myW = -x * quat.x - y * quat.y - z * quat.z + w * quat.w;
        return new CCQuaternion(myX, myY, myZ, myW);
    }

    /**
     * Multiplies this quaternion by the supplied quaternion. The result is stored locally.
     * 
     * @param quat
     *            The Quaternion to multiply this one by.
     * @return this quaternion for chaining
     * @throws NullPointerException
     *             if quat is null.
     */
    public CCQuaternion multiplyLocal(final CCQuaternion quat) {
        return multiplyLocal(quat.x, quat.y, quat.z, quat.w);
    }

    /**
     * Multiplies this quaternion by the supplied matrix. The result is stored locally.
     * 
     * @param matrix
     *            the matrix to apply to this quaternion.
     * @return this quaternion for chaining
     * @throws NullPointerException
     *             if matrix is null.
     */
    public CCQuaternion multiplyLocal(final CCMatrix3x3 matrix) {
        final double oldX = x, oldY = y, oldZ = z, oldW = w;
        fromRotationMatrix(matrix);
        final double tempX = x, tempY = y, tempZ = z, tempW = w;

        final double myX = oldX * tempW + oldY * tempZ - oldZ * tempY + oldW * tempX;
        final double myY = -oldX * tempZ + oldY * tempW + oldZ * tempX + oldW * tempY;
        final double myZ = oldX * tempY - oldY * tempX + oldZ * tempW + oldW * tempZ;
        final double myW = -oldX * tempX - oldY * tempY - oldZ * tempZ + oldW * tempW;
        return set(myX, myY, myZ, myW);
    }

    /**
     * Multiplies this quaternion by the supplied quaternion values. The result is stored locally.
     * 
     * @param qx
     * @param qy
     * @param qz
     * @param qw
     * @return this quaternion for chaining
     */
    public CCQuaternion multiplyLocal(final double qx, final double qy, final double qz, final double qw) {
        final double myX = x * qw + y * qz - z * qy + w * qx;
        final double myY = -x * qz + y * qw + z * qx + w * qy;
        final double myZ = x * qy - y * qx + z * qw + w * qz;
        final double myW = -x * qx - y * qy - z * qz + w * qw;
        return set(myX, myY, myZ, myW);
    }

    /**
     * Multiply this quaternion by a rotational quaternion made from the given angle and axis. The axis must be a
     * normalized vector.
     * 
     * @param angle
     *            in radians
     * @param theX
     *            x coord of rotation axis
     * @param theY
     *            y coord of rotation axis
     * @param theZ
     *            z coord of rotation axis
     * @return this quaternion for chaining.
     */
    public CCQuaternion applyRotation(final double angle, final double theX, final double theY, final double theZ) {
        if (theX == 0 && theY == 0 && theZ == 0) {
            // no change
            return this;
        }

        final double halfAngle = 0.5f * angle;
        final double sin = CCMath.sin(halfAngle);
        final double qw = CCMath.cos(halfAngle);
        final double qx = sin * theX;
        final double qy = sin * theY;
        final double qz = sin * theZ;

        final double newX = x * qw + y * qz - z * qy + w * qx;
        final double newY = -x * qz + y * qw + z * qx + w * qy;
        final double newZ = x * qy - y * qx + z * qw + w * qz;
        final double newW = -x * qx - y * qy - z * qz + w * qw;

        return set(newX, newY, newZ, newW);
    }

    /**
     * Apply rotation around X
     * 
     * @param angle
     *            in radians
     * @return this quaternion for chaining.
     */
    public CCQuaternion applyRotationX(final double angle) {
        final double halfAngle = 0.5f * angle;
        final double sin = CCMath.sin(halfAngle);
        final double cos = CCMath.cos(halfAngle);

        final double newX = x * cos + w * sin;
        final double newY = y * cos + z * sin;
        final double newZ = -y * sin + z * cos;
        final double newW = -x * sin + w * cos;

        return set(newX, newY, newZ, newW);
    }

    /**
     * Apply rotation around Y
     * 
     * @param angle
     *            in radians
     * @return this quaternion for chaining.
     */
    public CCQuaternion applyRotationY(final double angle) {
        final double halfAngle = 0.5f * angle;
        final double sin = CCMath.sin(halfAngle);
        final double cos = CCMath.cos(halfAngle);

        final double newX = x * cos - z * sin;
        final double newY = y * cos + w * sin;
        final double newZ = x * sin + z * cos;
        final double newW = -y * sin + w * cos;

        return set(newX, newY, newZ, newW);
    }

    /**
     * Apply rotation around Z
     * 
     * @param angle
     *            in radians
     * @return this quaternion for chaining.
     */
    public CCQuaternion applyRotationZ(final double angle) {
        final double halfAngle = 0.5f * angle;
        final double sin = CCMath.sin(halfAngle);
        final double cos = CCMath.cos(halfAngle);

        final double newX = x * cos + y * sin;
        final double newY = -x * sin + y * cos;
        final double newZ = z * cos + w * sin;
        final double newW = -z * sin + w * cos;

        return set(newX, newY, newZ, newW);
    }

    /**
     * Rotates the given vector by this quaternion. If supplied, the result is stored into the supplied "store" vector.
     * 
     * @param vec
     *            the vector to multiply this quaternion by.
     * @param store
     *            the vector to store the result in. If store is null, a new vector is created. Note that it IS safe for
     *            vec and store to be the same object.
     * @return the store vector, or a new vector if store is null.
     * @throws NullPointerException
     *             if vec is null
     * 
     *             if the given store is read only.
     */
    public CCVector3 apply(final CCVector3 vec, CCVector3 theStore) {
        
    	if(theStore == null) theStore = new CCVector3();
       
        if (vec.equals(CCVector3.ZERO)) {
        	theStore.set(0, 0, 0);
        } else {
            final double myX = 
            	w * w * vec.x + 2 * y * w * vec.z - 2 * z * w * vec.y + 
            	x * x * vec.x + 2 * y * x * vec.y + 2 * z * x * vec.z - 
            	z * z * vec.x - y * y * vec.x;
            final double myY = 
            	2 * x * y * vec.x + y * y * vec.y + 2 * z * y * vec.z + 
            	2 * w * z * vec.x - z * z * vec.y + w * w * vec.y - 
            	2 * x * w * vec.z - x * x * vec.y;
            final double myZ = 
            	2 * x * z * vec.x + 2 * y * z * vec.y + z * z * vec.z - 
            	2 * w * y * vec.x - y * y * vec.z + 2 * w * x * vec.y - 
            	x * x * vec.z + w * w * vec.z;
            theStore.set(myX, myY, myZ);
        }
        return theStore;
    }
    
    public CCVector3 apply(final CCVector3 theVector){
    	return apply(theVector, null);
    }
    
    /**
	 * Apply the inverse of the rotation to a vector.
	 * 
	 * @param u
	 *            vector to apply the inverse of the rotation to
	 * @return a new vector which such that u is its image by the rotation
	 */
	public CCVector3 applyInverse(final CCVector3 u) {

		final double myX = u.x;
		final double myY = u.y;
		final double myZ = u.z;

		final double s = x * myX + y * myY + z * myZ;
		final double m0 = -w;

		return new CCVector3(2 * (m0 * (myX * m0 - (y * myZ - z * myY)) + s * x) - myX, 2
				* (m0 * (myY * m0 - (z * myX - x * myZ)) + s * y) - myY, 2
				* (m0 * (myZ * m0 - (x * myY - y * myX)) + s * z) - myZ);

	}

    /**
     * Updates this quaternion to represent a rotation formed by the given three axes. These axes are assumed to be
     * orthogonal and no error checking is applied. It is the user's job to insure that the three axes being provided
     * indeed represent a proper right handed coordinate system.
     * 
     * @param xAxis
     *            vector representing the x-axis of the coordinate system.
     * @param yAxis
     *            vector representing the y-axis of the coordinate system.
     * @param zAxis
     *            vector representing the z-axis of the coordinate system.
     * @return this quaternion for chaining
     */
    public CCQuaternion fromAxes(final CCVector3 xAxis, final CCVector3 yAxis, final CCVector3 zAxis) {
        return fromRotationMatrix(
        	xAxis.x, yAxis.x, zAxis.x, 
        	xAxis.y, yAxis.y, zAxis.y,
            xAxis.z, yAxis.z, zAxis.z
        );
    }

    /**
     * Converts this quaternion to a rotation matrix and then extracts rotation axes.
     * 
     * @param axes
     *            the array of vectors to be filled.
     * @throws ArrayIndexOutOfBoundsException
     *             if the given axes array is smaller than 3 elements.
     * @return the axes
     */
    public CCVector3[] toAxes(final CCVector3[] axes) {
        final CCMatrix3x3 tempMat = toRotationMatrix();
        axes[2] = tempMat.column(2);
        axes[1] = tempMat.column(1);
        axes[0] = tempMat.column(0);
        return axes;
    }

    /**
     * Does a spherical linear interpolation between this quaternion and the given end quaternion by the given change
     * amount.
     * 
     * @param endQuat
     * @param changeAmnt
     * @param store
     *            the quaternion to store the result in for return. If null, a new quaternion object is created and
     *            returned.
     * @return a new quaternion containing the result.
     */
    public CCQuaternion slerp(final CCQuaternion endQuat, final double changeAmnt) {
        return CCQuaternion.slerp(this, endQuat, changeAmnt);
    }

    /**
     * Does a spherical linear interpolation between this quaternion and the given end quaternion by the given change
     * amount. Stores the results locally in this quaternion.
     * 
     * @param endQuat
     * @param changeAmnt
     * @return this quaternion for chaining.
     */
    public CCQuaternion slerpLocal(final CCQuaternion endQuat, final double changeAmnt) {
        return slerpLocal(this, endQuat, changeAmnt);
    }

    /**
     * Does a spherical linear interpolation between the given start and end quaternions by the given change amount.
     * Returns the result as a new quaternion.
     * 
     * @param startQuat
     * @param endQuat
     * @param changeAmnt
     * @param store
     *            the quaternion to store the result in for return. If null, a new quaternion object is created and
     *            returned.
     * @return the new quaternion
     */
    public static CCQuaternion slerp(final CCQuaternion startQuat, final CCQuaternion endQuat, final double changeAmnt) {
        CCQuaternion result = new CCQuaternion();

        // check for weighting at either extreme
        if (changeAmnt == 0.0) {
            return result.set(startQuat);
        } else if (changeAmnt == 1.0) {
            return result.set(endQuat);
        }

        result.set(endQuat);
        // Check for equality and skip operation.
        if (startQuat.equals(result)) {
            return result;
        }

        double dotP = startQuat.dot(result);

        if (dotP < 0.0) {
            // Negate the second quaternion and the result of the dot product
            result.multiplyLocal(-1);
            dotP = -dotP;
        }

        // Set the first and second scale for the interpolation
        double scale0 = 1 - changeAmnt;
        double scale1 = changeAmnt;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if (1 - dotP > 0.1) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final double theta = CCMath.acos(dotP);
            final double invSinTheta = 1f / CCMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = CCMath.sin((1 - changeAmnt) * theta) * invSinTheta;
            scale1 = CCMath.sin(changeAmnt * theta) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special form of linear interpolation for quaternions.
        final double myX = scale0 * startQuat.x + scale1 * result.x;
        final double myY = scale0 * startQuat.y + scale1 * result.y;
        final double myZ = scale0 * startQuat.z + scale1 * result.z;
        final double myW = scale0 * startQuat.w + scale1 * result.w;

        // Return the interpolated quaternion
        return result.set(myX, myY, myZ, myW);
    }

    /**
     * Does a spherical linear interpolation between the given start and end quaternions by the given change amount.
     * Stores the result locally.
     * 
     * @param startQuat
     * @param endQuat
     * @param changeAmnt
     * @param workQuat
     *            a Quaternion to use as scratchpad during calculation
     * @return this quaternion for chaining.
     * @throws NullPointerException
     *             if startQuat, endQuat or workQuat are null.
     */
    public CCQuaternion slerpLocal(final CCQuaternion startQuat, final CCQuaternion endQuat, final double changeAmnt) {

        // check for weighting at either extreme
        if (changeAmnt == 0.0) {
            return set(startQuat);
        } else if (changeAmnt == 1.0) {
            return set(endQuat);
        }

        // Check for equality and skip operation.
        if (startQuat.equals(endQuat)) {
            return set(startQuat);
        }

        double result = startQuat.dot(endQuat);
        set(endQuat);

        if (result < 0.0) {
            // Negate the second quaternion and the result of the dot product
            multiplyLocal(-1);
            result = -result;
        }

        // Set the first and second scale for the interpolation
        double scale0 = 1 - changeAmnt;
        double scale1 = changeAmnt;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if (1 - result > 0.1) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final double theta = CCMath.acos(result);
            final double invSinTheta = 1f / CCMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = CCMath.sin((1 - changeAmnt) * theta) * invSinTheta;
            scale1 = CCMath.sin(changeAmnt * theta) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special form of linear interpolation for quaternions.
        x = scale0 * startQuat.x + scale1 * x;
        y = scale0 * startQuat.y + scale1 * y;
        z = scale0 * startQuat.z + scale1 * z;
        w = scale0 * startQuat.w + scale1 * w;

        // Return the interpolated quaternion
        return this;
    }

    /**
     * Modifies this quaternion to equal the rotation required to point the z-axis at 'direction' and the y-axis to
     * 'up'.
     * 
     * @param direction
     *            where to 'look' at
     * @param up
     *            a vector indicating the local up direction.
     * @return this quaternion for chaining.
     */
    public CCQuaternion lookAt(final CCVector3 direction, final CCVector3 up) {
        final CCVector3 zAxis = direction.normalize();
        final CCVector3 xAxis = up.normalize().crossLocal(zAxis);
        final CCVector3 yAxis = zAxis.cross(xAxis);
        
        fromAxes(xAxis, yAxis, zAxis);
        normalizeLocal();
        return this;
    }

    /**
     * @return the squared magnitude of this quaternion.
     */
    public double magnitudeSquared() {
        return w * w + x * x + y * y + z * z;
    }

    /**
     * @return the magnitude of this quaternion. basically sqrt({@link #magnitude()})
     */
   public double magnitude() {
        final double magnitudeSQ = magnitudeSquared();
        if (magnitudeSQ == 1.0) {
            return 1.0f;
        }

        return CCMath.sqrt(magnitudeSQ);
    }

    /**
     * @param theX
     * @param theY
     * @param theZ
     * @param theW
     * @return the dot product of this quaternion with the given x,y,z and w values.
     */
    public double dot(final double theX, final double theY, final double theZ, final double theW) {
        return x * theX + y * theY + z * theZ + w * theW;
    }

    /**
     * @param quat
     * @return the dot product of this quaternion with the given quaternion.
     */
    public double dot(final CCQuaternion quat) {
        return dot(quat.x, quat.y, quat.z, quat.w);
    }

    /**
     * Sets the value of this quaternion to (0, 0, 0, 1). Equivalent to calling set(0, 0, 0, 1)
     * 
     * @return this quaternion for chaining
     */
    public CCQuaternion setIdentity() {
        return set(0, 0, 0, 1);
    }

    /**
     * @return true if this quaternion is (0, 0, 0, 1)
     */
    public boolean isIdentity() {
        return strictEquals(CCQuaternion.IDENTITY);
    }

    /**
     * Check a quaternion... if it is null or its doubles are NaN or infinite, return false. Else return true.
     * 
     * @param quat
     *            the quaternion to check
     * @return true or false as stated above.
     */
    public static boolean isValid(final CCQuaternion quat) {
        if (quat == null) {
            return false;
        }
        if (Double.isNaN(quat.x) || Double.isInfinite(quat.x)) {
            return false;
        }
        if (Double.isNaN(quat.y) || Double.isInfinite(quat.y)) {
            return false;
        }
        if (Double.isNaN(quat.z) || Double.isInfinite(quat.z)) {
            return false;
        }
        if (Double.isNaN(quat.w) || Double.isInfinite(quat.w)) {
            return false;
        }
        return true;
    }

    /**
     * @return the string representation of this quaternion.
     */
    @Override
    public String toString() {
        return getClass().getName() + " [X=" + x + ", Y=" + y + ", Z=" + z + ", W=" + w + "]";
    }

    /**
     * @return returns a unique code for this quaternion object based on its values. If two quaternions are numerically
     *         equal, they will return the same hash code value.
     */
    @Override
    public int hashCode() {
        int result = 17;

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
     * @param o
     *            the object to compare for equality
     * @return true if this quaternion and the provided quaternion have roughly the same x, y, z and w values.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CCQuaternion)) {
            return false;
        }
        final CCQuaternion comp = (CCQuaternion) o;
        return 
        	CCMath.abs(x - comp.x) < CCQuaternion.ALLOWED_DEVIANCE && 
        	CCMath.abs(y - comp.y) < CCQuaternion.ALLOWED_DEVIANCE && 
        	CCMath.abs(z - comp.z) < CCQuaternion.ALLOWED_DEVIANCE && 
        	CCMath.abs(w - comp.w) < CCQuaternion.ALLOWED_DEVIANCE;
    }

    /**
     * @param o
     *            the object to compare for equality
     * @return true if this quaternion and the provided quaternion have the exact same double values.
     */
    public boolean strictEquals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CCQuaternion)) {
            return false;
        }
        final CCQuaternion comp = (CCQuaternion) o;
        return x == comp.x && y == comp.y && z == comp.z && w == comp.w;
    }

    // /////////////////
    // Method for Cloneable
    // /////////////////

    @Override
    public CCQuaternion clone() {
        return new CCQuaternion(this);
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
        x = in.readDouble();
        y = in.readDouble();
        z = in.readDouble();
        w = in.readDouble();
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
        out.writeDouble(z);
        out.writeDouble(w);
    }
}
