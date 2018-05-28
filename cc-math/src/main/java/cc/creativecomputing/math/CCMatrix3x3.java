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
import java.nio.BufferOverflowException;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

/**
 * Matrix3 represents a double precision 3x3 matrix.
 * 
 * Note: some algorithms in this class were ported from Eberly, Wolfram, Game Gems and others to Java.
 */
public class CCMatrix3x3 implements Cloneable, Externalizable{
	
	/**
     * 
     * @param thePosition
     * @param theTarget
     * @param up
     * @param theStore
     */
	public static CCMatrix3x3 createLookAt(
		final CCVector3 thePosition, 
		final CCVector3 theTarget, 
		final CCVector3 theWorldUp, 
		CCMatrix3x3 theStore
	) {
		if (theStore == null)
			theStore = new CCMatrix3x3();

		final CCVector3 direction = new CCVector3();
		final CCVector3 side = new CCVector3();
		final CCVector3 up = new CCVector3();

		direction.set(theTarget).subtractLocal(thePosition).normalizeLocal();
		direction.cross(theWorldUp, side).normalizeLocal();
		side.cross(direction, up);

		return theStore.set(
			side.x, up.x, -direction.x, 
			side.y, up.y, -direction.y, 
			side.z, up.z, -direction.z
		);

	}
	
	public static CCMatrix3x3 createLookAt(
		final CCVector3 thePosition, 
		final CCVector3 theTarget, 
		final CCVector3 theWorldUp
	) {
		return createLookAt(thePosition, theTarget, theWorldUp, null);
	}

    /** Used with equals method to determine if two Matrix3 objects are close enough to be considered equal. */
    public static final double ALLOWED_DEVIANCE = 0.000001f;

    private static final long serialVersionUID = 1L;

    /**
     * <pre>
     * 1, 0, 0
     * 0, 1, 0
     * 0, 0, 1
     * </pre>
     */
    public final static CCMatrix3x3 IDENTITY = new CCMatrix3x3(1, 0, 0, 0, 1, 0, 0, 0, 1);

    public double _m00, _m01, _m02;
    public double _m10, _m11, _m12;
    public double _m20, _m21, _m22;

    /**
     * Constructs a new, mutable matrix set to identity.
     */
    public CCMatrix3x3() {
        this(CCMatrix3x3.IDENTITY);
    }

    /**
     * Constructs a new, mutable matrix using the given matrix values (names are mRC = m[ROW][COL])
     * 
     * @param theM00
     * @param theM01
     * @param theM02
     * @param theM10
     * @param theM11
     * @param theM12
     * @param theM20
     * @param theM21
     * @param theM22
     */
    public CCMatrix3x3(
    	final double theM00, final double theM01, final double theM02, 
    	final double theM10, final double theM11, final double theM12, 
    	final double theM20, final double theM21, final double theM22
    ) {

        _m00 = theM00;
        _m01 = theM01;
        _m02 = theM02;
        _m10 = theM10;
        _m11 = theM11;
        _m12 = theM12;
        _m20 = theM20;
        _m21 = theM21;
        _m22 = theM22;
    }

    /**
     * Constructs a new, mutable matrix using the values from the given matrix
     * 
     * @param theSource
     */
    public CCMatrix3x3(final CCMatrix3x3 theSource) {
        set(theSource);
    }

    /**
     * @param theRow
     * @param theColumn
     * @return the value stored in this matrix at row, column.
     * @throws IllegalArgumentException
     *             if row and column are not in bounds [0, 2]
     */
    public double getValue(final int theRow, final int theColumn) {
        switch (theRow) {
            case 0:
                switch (theColumn) {
                    case 0:
                        return _m00;
                    case 1:
                        return _m01;
                    case 2:
                        return _m02;
                }
                break;
            case 1:
                switch (theColumn) {
                    case 0:
                        return _m10;
                    case 1:
                        return _m11;
                    case 2:
                        return _m12;
                }
                break;

            case 2:
                switch (theColumn) {
                    case 0:
                        return _m20;
                    case 1:
                        return _m21;
                    case 2:
                        return _m22;
                }
                break;
        }
        throw new IllegalArgumentException();
    }

    

    /**
     * Same as set(IDENTITY)
     * 
     * @return this matrix for chaining
     */
    public CCMatrix3x3 setIdentity() {
        return set(CCMatrix3x3.IDENTITY);
    }

    /**
     * @return true if this matrix equals the 3x3 identity matrix
     */
    public boolean isIdentity() {
        return strictEquals(CCMatrix3x3.IDENTITY);
    }

    /**
     * Sets the value of this matrix at row, column to the given value.
     * 
     * @param theRow
     * @param theColumn
     * @param theValue
     * @return this matrix for chaining
     * @throws IllegalArgumentException
     *             if row and column are not in bounds [0, 2]
     */
    public CCMatrix3x3 setValue(final int theRow, final int theColumn, final double theValue) {
        switch (theRow) {
            case 0:
                switch (theColumn) {
                    case 0:
                        _m00 = theValue;
                        break;
                    case 1:
                        _m01 = theValue;
                        break;
                    case 2:
                        _m02 = theValue;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;

            case 1:
                switch (theColumn) {
                    case 0:
                        _m10 = theValue;
                        break;
                    case 1:
                        _m11 = theValue;
                        break;
                    case 2:
                        _m12 = theValue;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;

            case 2:
                switch (theColumn) {
                    case 0:
                        _m20 = theValue;
                        break;
                    case 1:
                        _m21 = theValue;
                        break;
                    case 2:
                        _m22 = theValue;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;

            default:
                throw new IllegalArgumentException();
        }

        return this;
    }

    /**
     * Sets the values of this matrix to the values given.
     * 
     * @param theM00
     * @param theM01
     * @param theM02
     * @param theM10
     * @param theM11
     * @param theM12
     * @param theM20
     * @param theM21
     * @param theM22
     * @return this matrix for chaining
     */
    public CCMatrix3x3 set(
    	final double theM00, final double theM01, final double theM02, 
    	final double theM10, final double theM11, final double theM12, 
    	final double theM20, final double theM21, final double theM22
    ) {
    	_m00 = theM00;
    	_m01 = theM01;
    	_m02 = theM02;
    	_m10 = theM10;
    	_m11 = theM11;
    	_m12 = theM12;
    	_m20 = theM20;
    	_m21 = theM21;
    	_m22 = theM22;
        
    	return this;
    }

    /**
     * Sets the values of this matrix to the values of the provided source matrix.
     * 
     * @param theSource
     * @return this matrix for chaining
     * @throws NullPointerException if source is null.
     */
    public CCMatrix3x3 set(final CCMatrix3x3 theSource) {
        _m00 = theSource._m00;
        _m10 = theSource._m10;
        _m20 = theSource._m20;

        _m01 = theSource._m01;
        _m11 = theSource._m11;
        _m21 = theSource._m21;

        _m02 = theSource._m02;
        _m12 = theSource._m12;
        _m22 = theSource._m22;

        return this;
    }

    /**
     * Sets the values of this matrix to the rotational value of the given quaternion.
     * 
     * @param theQuaternion
     * @return this matrix for chaining
     */
    public CCMatrix3x3 set(final CCQuaternion theQuaternion) {
        return theQuaternion.toRotationMatrix(this);
    }

    /**
     * @param theSource
     *            the buffer to read our matrix data from.
     * @return this matrix for chaining.
     */
    public CCMatrix3x3 fromDoubleBuffer(final DoubleBuffer theSource) {
        return fromDoubleBuffer(theSource, true);
    }

    /**
     * @param theSource
     *            the buffer to read our matrix data from.
     * @param theRowMajor
     *            if true, data is stored row by row. Otherwise it is stored column by column.
     * @return this matrix for chaining.
     */
    public CCMatrix3x3 fromDoubleBuffer(final DoubleBuffer theSource, final boolean theRowMajor) {
        if (theRowMajor) {
            _m00 = theSource.get();
            _m01 = theSource.get();
            _m02 = theSource.get();
            _m10 = theSource.get();
            _m11 = theSource.get();
            _m12 = theSource.get();
            _m20 = theSource.get();
            _m21 = theSource.get();
            _m22 = theSource.get();
        } else {
            _m00 = theSource.get();
            _m10 = theSource.get();
            _m20 = theSource.get();
            _m01 = theSource.get();
            _m11 = theSource.get();
            _m21 = theSource.get();
            _m02 = theSource.get();
            _m12 = theSource.get();
            _m22 = theSource.get();
        }

        return this;
    }

    /**
     * Sets the values of this matrix to the values of the provided double array.
     * 
     * @param theSource
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if source is null.
     * @throws ArrayIndexOutOfBoundsException
     *             if source array has a length less than 9.
     */
    public CCMatrix3x3 fromArray(final double[] theSource) {
        return fromArray(theSource, true);
    }

    /**
     * Sets the values of this matrix to the values of the provided double array.
     * 
     * @param theSource
     * @param theRowMajor
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if source is null.
     * @throws ArrayIndexOutOfBoundsException
     *             if source array has a length less than 9.
     */
    public CCMatrix3x3 fromArray(final double[] theSource, final boolean theRowMajor) {
        if (theRowMajor) {
            _m00 = theSource[0];
            _m01 = theSource[1];
            _m02 = theSource[2];
            _m10 = theSource[3];
            _m11 = theSource[4];
            _m12 = theSource[5];
            _m20 = theSource[6];
            _m21 = theSource[7];
            _m22 = theSource[8];
        } else {
            _m00 = theSource[0];
            _m10 = theSource[1];
            _m20 = theSource[2];
            _m01 = theSource[3];
            _m11 = theSource[4];
            _m21 = theSource[5];
            _m02 = theSource[6];
            _m12 = theSource[7];
            _m22 = theSource[8];
        }
        return this;
    }

    /**
     * Replaces a column in this matrix with the values of the given vector.
     * 
     * @param theColumnIndex
     * @param theColumnData
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if columnData is null.
     * @throws IllegalArgumentException
     *             if columnIndex is not in [0, 2]
     */
    public CCMatrix3x3 setColumn(final int theColumnIndex, final CCVector3 theColumnData) {
        switch (theColumnIndex) {
            case 0:
                _m00 = theColumnData.x;
                _m10 = theColumnData.y;
                _m20 = theColumnData.z;
                break;
            case 1:
                _m01 = theColumnData.x;
                _m11 = theColumnData.y;
                _m21 = theColumnData.z;
                break;
            case 2:
                _m02 = theColumnData.x;
                _m12 = theColumnData.y;
                _m22 = theColumnData.z;
                break;
            default:
                throw new IllegalArgumentException("Bad columnIndex: " + theColumnIndex);
        }
        return this;
    }

    /**
     * Replaces a row in this matrix with the values of the given vector.
     * 
     * @param theRowIndex
     * @param theRowData
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if rowData is null.
     * @throws IllegalArgumentException
     *             if rowIndex is not in [0, 2]
     */
    public CCMatrix3x3 setRow(final int theRowIndex, final CCVector3 theRowData) {
        switch (theRowIndex) {
            case 0:
                _m00 = theRowData.x;
                _m01 = theRowData.y;
                _m02 = theRowData.z;
                break;
            case 1:
                _m10 = theRowData.x;
                _m11 = theRowData.y;
                _m12 = theRowData.z;
                break;
            case 2:
                _m20 = theRowData.x;
                _m21 = theRowData.y;
                _m22 = theRowData.z;
                break;
            default:
                throw new IllegalArgumentException("Bad rowIndex: " + theRowIndex);
        }
        return this;
    }

    /**
     * Set the values of this matrix from the axes (columns) provided.
     * 
     * @param uAxis
     * @param vAxis
     * @param wAxis
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if any of the axes are null.
     */
    public CCMatrix3x3 fromAxes(final CCVector3 uAxis, final CCVector3 vAxis, final CCVector3 wAxis) {
        setColumn(0, uAxis);
        setColumn(1, vAxis);
        setColumn(2, wAxis);
        return this;
    }

    /**
     * Sets this matrix to the rotation indicated by the given angle and axis of rotation. Note: This method creates an
     * object, so use fromAngleNormalAxis when possible, particularly if your axis is already normalized.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation.
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if axis is null.
     */
    public CCMatrix3x3 fromAngleAxis(final double angle, final CCVector3 axis) {
        final CCVector3 normAxis = axis.normalize();
        fromAngleNormalAxis(angle, normAxis);
        return this;
    }

    /**
     * Sets this matrix to the rotation indicated by the given angle and a unit-length axis of rotation.
     * @param angle  the angle to rotate (in radians).
     * @param theX the axis of rotation (already normalized).
     * @param theY the axis of rotation (already normalized).
     * @param theZ the axis of rotation (already normalized).
     * @return this matrix for chaining
     */
    public CCMatrix3x3 fromAngleNormalAxis(final double angle, final double theX, final double theY, final double theZ) {
        final double fCos = CCMath.cos(angle);
        final double fSin = CCMath.sin(angle);
        final double fOneMinusCos = 1.0f - fCos;
        final double fX2 = theX * theX;
        final double fY2 = theY * theY;
        final double fZ2 = theZ * theZ;
        final double fXYM = theX * theY * fOneMinusCos;
        final double fXZM = theX * theZ * fOneMinusCos;
        final double fYZM = theY * theZ * fOneMinusCos;
        final double fXSin = theX * fSin;
        final double fYSin = theY * fSin;
        final double fZSin = theZ * fSin;

        _m00 = fX2 * fOneMinusCos + fCos;
        _m01 = fXYM - fZSin;
        _m02 = fXZM + fYSin;
        _m10 = fXYM + fZSin;
        _m11 = fY2 * fOneMinusCos + fCos;
        _m12 = fYZM - fXSin;
        _m20 = fXZM - fYSin;
        _m21 = fYZM + fXSin;
        _m22 = fZ2 * fOneMinusCos + fCos;

        return this;
    }
    
    /**
     * Sets this matrix to the rotation indicated by the given angle and a unit-length axis of rotation.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized).
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if axis is null.
     */
    public CCMatrix3x3 fromAngleNormalAxis(final double angle, final CCVector3 axis) {
    	return fromAngleNormalAxis(angle, axis.x, axis.y, axis.z);
    }

    /**
     * XXX: Need to redo this again... or at least correct the terms. YRP are arbitrary terms, based on a specific frame
     * of axis.
     * 
     * Updates this matrix from the given Euler rotation angles (y,r,p). Note that we are applying in order: roll,
     * pitch, yaw but we've ordered them in x, y, and z for convenience. See:
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToMatrix/index.htm
     * 
     * @param yaw
     *            the Euler yaw of rotation (in radians). (aka Bank, often rot around x)
     * @param roll
     *            the Euler roll of rotation (in radians). (aka Heading, often rot around y)
     * @param pitch
     *            the Euler pitch of rotation (in radians). (aka Attitude, often rot around z)
     * @return this matrix for chaining
     */
    public CCMatrix3x3 fromAngles(final double yaw, final double roll, final double pitch) {
        final double ch = CCMath.cos(roll);
        final double sh = CCMath.sin(roll);
        final double cp = CCMath.cos(pitch);
        final double sp = CCMath.sin(pitch);
        final double cy = CCMath.cos(yaw);
        final double sy = CCMath.sin(yaw);

        _m00 = ch * cp;
        _m01 = sh * sy - ch * sp * cy;
        _m02 = ch * sp * sy + sh * cy;
        _m10 = sp;
        _m11 = cp * cy;
        _m12 = -cp * sy;
        _m20 = -sh * cp;
        _m21 = sh * sp * cy + ch * sy;
        _m22 = -sh * sp * sy + ch * cy;
        return this;
    }

    public CCMatrix3x3 applyRotation(final double angle, final double x, final double y, final double z) {
        final double m00 = _m00, m01 = _m01, m02 = _m02, //
        m10 = _m10, m11 = _m11, m12 = _m12, //
        m20 = _m20, m21 = _m21, m22 = _m22;

        final double cosAngle = CCMath.cos(angle);
        final double sinAngle = CCMath.sin(angle);
        final double oneMinusCos = 1.0f - cosAngle;
        final double xyOneMinusCos = x * y * oneMinusCos;
        final double xzOneMinusCos = x * z * oneMinusCos;
        final double yzOneMinusCos = y * z * oneMinusCos;
        final double xSin = x * sinAngle;
        final double ySin = y * sinAngle;
        final double zSin = z * sinAngle;

        final double r00 = x * x * oneMinusCos + cosAngle;
        final double r01 = xyOneMinusCos - zSin;
        final double r02 = xzOneMinusCos + ySin;
        final double r10 = xyOneMinusCos + zSin;
        final double r11 = y * y * oneMinusCos + cosAngle;
        final double r12 = yzOneMinusCos - xSin;
        final double r20 = xzOneMinusCos - ySin;
        final double r21 = yzOneMinusCos + xSin;
        final double r22 = z * z * oneMinusCos + cosAngle;

        _m00 = m00 * r00 + m01 * r10 + m02 * r20;
        _m01 = m00 * r01 + m01 * r11 + m02 * r21;
        _m02 = m00 * r02 + m01 * r12 + m02 * r22;

        _m10 = m10 * r00 + m11 * r10 + m12 * r20;
        _m11 = m10 * r01 + m11 * r11 + m12 * r21;
        _m12 = m10 * r02 + m11 * r12 + m12 * r22;

        _m20 = m20 * r00 + m21 * r10 + m22 * r20;
        _m21 = m20 * r01 + m21 * r11 + m22 * r21;
        _m22 = m20 * r02 + m21 * r12 + m22 * r22;

        return this;
    }

    /**
     * Apply rotation around X (Mrx * this)
     * 
     * @param angle
     * @return
     */
    public CCMatrix3x3 applyRotationX(final double angle) {
        final double m01 = _m01, m02 = _m02, //
        m11 = _m11, m12 = _m12, //
        m21 = _m21, m22 = _m22;

        final double cosAngle = CCMath.cos(angle);
        final double sinAngle = CCMath.sin(angle);

        _m01 = m01 * cosAngle + m02 * sinAngle;
        _m02 = m02 * cosAngle - m01 * sinAngle;

        _m11 = m11 * cosAngle + m12 * sinAngle;
        _m12 = m12 * cosAngle - m11 * sinAngle;

        _m21 = m21 * cosAngle + m22 * sinAngle;
        _m22 = m22 * cosAngle - m21 * sinAngle;

        return this;
    }

    /**
     * Apply rotation around Y (Mry * this)
     * 
     * @param angle
     * @return
     */
    public CCMatrix3x3 applyRotationY(final double angle) {
        final double m00 = _m00, m02 = _m02, //
        m10 = _m10, m12 = _m12, //
        m20 = _m20, m22 = _m22;

        final double cosAngle = CCMath.cos(angle);
        final double sinAngle = CCMath.sin(angle);

        _m00 = m00 * cosAngle - m02 * sinAngle;
        _m02 = m00 * sinAngle + m02 * cosAngle;

        _m10 = m10 * cosAngle - m12 * sinAngle;
        _m12 = m10 * sinAngle + m12 * cosAngle;

        _m20 = m20 * cosAngle - m22 * sinAngle;
        _m22 = m20 * sinAngle + m22 * cosAngle;

        return this;
    }

    /**
     * Apply rotation around Z (Mrz * this)
     * 
     * @param angle
     * @return
     */
    public CCMatrix3x3 applyRotationZ(final double angle) {
        final double m00 = _m00, m01 = _m01, //
        m10 = _m10, m11 = _m11, //
        m20 = _m20, m21 = _m21;

        final double cosAngle = CCMath.cos(angle);
        final double sinAngle = CCMath.sin(angle);

        _m00 = m00 * cosAngle + m01 * sinAngle;
        _m01 = m01 * cosAngle - m00 * sinAngle;

        _m10 = m10 * cosAngle + m11 * sinAngle;
        _m11 = m11 * cosAngle - m10 * sinAngle;

        _m20 = m20 * cosAngle + m21 * sinAngle;
        _m21 = m21 * cosAngle - m20 * sinAngle;

        return this;
    }

    /**
     * @param theIndex
     * @return the column specified by the index.
     * @throws IllegalArgumentException
     *             if index is not in bounds [0, 2]
     */
    public CCVector3 getColumn(final int theIndex, CCVector3 theStore) {
        if(theStore == null) theStore = new CCVector3();

        switch (theIndex) {
            case 0:
            	theStore.x = _m00;
            	theStore.y = _m10;
            	theStore.z = _m20;
                break;
            case 1:
            	theStore.x = _m01;
            	theStore.y = _m11;
            	theStore.z = _m21;
                break;
            case 2:
            	theStore.x = _m02;
            	theStore.y = _m12;
            	theStore.z = _m22;
                break;
            default:
                throw new IllegalArgumentException("invalid column index: " + theIndex);
        }

        return theStore;
    }
    
    public CCVector3 column(final int theIndex){
    	return getColumn(theIndex, null);
    }

    /**
     * @param index
     * @return the row specified by the index.
     * @throws IllegalArgumentException
     *             if index is not in bounds [0, 2]
     */
    public CCVector3 getRow(final int index) {
        CCVector3 result = new CCVector3();
        
        switch (index) {
            case 0:
                result.x = _m00;
                result.y = _m01;
                result.z = _m02;
                break;
            case 1:
                result.x = _m10;
                result.y = _m11;
                result.z = _m12;
                break;
            case 2:
                result.x = _m20;
                result.y = _m21;
                result.z = _m22;
                break;
            default:
                throw new IllegalArgumentException("invalid row index: " + index);
        }
        return result;
    }

    

    /**
     * @param store
     *            the buffer to store our matrix data in. Must not be null. Data is entered starting at current buffer
     *            position.
     * @return matrix data as a DoubleBuffer in row major order. The position is at the end of the inserted data.
     * @throws NullPointerException
     *             if store is null.
     * @throws BufferOverflowException
     *             if there is not enough room left in the buffer to write all 9 values.
     */
    public FloatBuffer toBuffer(final FloatBuffer store) {
        return toBuffer(store, true);
    }

    /**
     * @param store
     *            the buffer to store our matrix data in. Must not be null. Data is entered starting at current buffer
     *            position.
     * @param rowMajor
     *            if true, data is stored row by row. Otherwise it is stored column by column.
     * @return matrix data as a DoubleBuffer in the specified order. The position is at the end of the inserted data.
     * @throws NullPointerException
     *             if store is null.
     * @throws BufferOverflowException
     *             if there is not enough room left in the buffer to write all 9 values.
     */
    public FloatBuffer toBuffer(final FloatBuffer store, final boolean rowMajor) {
        if (rowMajor) {
            store.put((float)_m00);
            store.put((float)_m01);
            store.put((float)_m02);
            store.put((float)_m10);
            store.put((float)_m11);
            store.put((float)_m12);
            store.put((float)_m20);
            store.put((float)_m21);
            store.put((float)_m22);
        } else {
            store.put((float)_m00);
            store.put((float)_m10);
            store.put((float)_m20);
            store.put((float)_m01);
            store.put((float)_m11);
            store.put((float)_m21);
            store.put((float)_m02);
            store.put((float)_m12);
            store.put((float)_m22);
        }

        return store;
    }

    /**
     * @param store
     *            the double array to store our matrix data in. If null, a new array is created.
     * @return matrix data as a double array in row major order.
     * @throws IllegalArgumentException
     *             if the store is non-null and has a length < 9
     */
    public float[] toArray(final float[] store) {
        return toArray(store, true);
    }

    /**
     * @param store
     *            the double array to store our matrix data in. If null, a new array is created.
     * @param rowMajor
     *            if true, data is stored row by row. Otherwise it is stored column by column.
     * @return matrix data as a double array in the specified order.
     * @throws IllegalArgumentException
     *             if the store is non-null and has a length < 9
     */
    public float[] toArray(final float[] store, final boolean rowMajor) {
    	float[] result = store;
        if (result == null) {
            result = new float[9];
        } else if (result.length < 9) {
            throw new IllegalArgumentException("store must be at least length 9.");
        }

        if (rowMajor) {
            result[0] = (float)_m00;
            result[1] = (float)_m01;
            result[2] = (float)_m02;
            result[3] = (float)_m10;
            result[4] = (float)_m11;
            result[5] = (float)_m12;
            result[6] = (float)_m20;
            result[7] = (float)_m21;
            result[8] = (float)_m22;
        } else {
            result[0] = (float)_m00;
            result[1] = (float)_m10;
            result[2] = (float)_m20;
            result[3] = (float)_m01;
            result[4] = (float)_m11;
            result[5] = (float)_m21;
            result[6] = (float)_m02;
            result[7] = (float)_m12;
            result[8] = (float)_m22;
        }

        return result;
    }

    

    /**
     * @param store
     *            the buffer to store our matrix data in. Must not be null. Data is entered starting at current buffer
     *            position.
     * @return matrix data as a DoubleBuffer in row major order. The position is at the end of the inserted data.
     * @throws NullPointerException
     *             if store is null.
     * @throws BufferOverflowException
     *             if there is not enough room left in the buffer to write all 9 values.
     */
    public DoubleBuffer toBuffer(final DoubleBuffer store) {
        return toBuffer(store, true);
    }

    /**
     * @param store
     *            the buffer to store our matrix data in. Must not be null. Data is entered starting at current buffer
     *            position.
     * @param rowMajor
     *            if true, data is stored row by row. Otherwise it is stored column by column.
     * @return matrix data as a DoubleBuffer in the specified order. The position is at the end of the inserted data.
     * @throws NullPointerException
     *             if store is null.
     * @throws BufferOverflowException
     *             if there is not enough room left in the buffer to write all 9 values.
     */
    public DoubleBuffer toBuffer(final DoubleBuffer store, final boolean rowMajor) {
        if (rowMajor) {
            store.put(_m00);
            store.put(_m01);
            store.put(_m02);
            store.put(_m10);
            store.put(_m11);
            store.put(_m12);
            store.put(_m20);
            store.put(_m21);
            store.put(_m22);
        } else {
            store.put(_m00);
            store.put(_m10);
            store.put(_m20);
            store.put(_m01);
            store.put(_m11);
            store.put(_m21);
            store.put(_m02);
            store.put(_m12);
            store.put(_m22);
        }

        return store;
    }

    /**
     * @param store
     *            the double array to store our matrix data in. If null, a new array is created.
     * @return matrix data as a double array in row major order.
     * @throws IllegalArgumentException
     *             if the store is non-null and has a length < 9
     */
    public double[] toArray(final double[] store) {
        return toArray(store, true);
    }

    /**
     * @param store
     *            the double array to store our matrix data in. If null, a new array is created.
     * @param rowMajor
     *            if true, data is stored row by row. Otherwise it is stored column by column.
     * @return matrix data as a double array in the specified order.
     * @throws IllegalArgumentException
     *             if the store is non-null and has a length < 9
     */
    public double[] toArray(final double[] store, final boolean rowMajor) {
        double[] result = store;
        if (result == null) {
            result = new double[9];
        } else if (result.length < 9) {
            throw new IllegalArgumentException("store must be at least length 9.");
        }

        if (rowMajor) {
            result[0] = _m00;
            result[1] = _m01;
            result[2] = _m02;
            result[3] = _m10;
            result[4] = _m11;
            result[5] = _m12;
            result[6] = _m20;
            result[7] = _m21;
            result[8] = _m22;
        } else {
            result[0] = _m00;
            result[1] = _m10;
            result[2] = _m20;
            result[3] = _m01;
            result[4] = _m11;
            result[5] = _m21;
            result[6] = _m02;
            result[7] = _m12;
            result[8] = _m22;
        }

        return result;
    }

    /**
     * converts this matrix to Euler rotation angles (yaw, roll, pitch). See
     * http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToEuler/index.htm
     * 
     * @param store
     *            the double[] array to store the computed angles in. If null, a new double[] will be created
     * @return the double[] array.
     * @throws IllegalArgumentException
     *             if non-null store is not at least length 3
     * @see #fromAngles(double, double, double)
     */
    public double[] toAngles(final double[] store) {
        double[] result = store;
        if (result == null) {
            result = new double[3];
        } else if (result.length < 3) {
            throw new IllegalArgumentException("store array must have at least three elements");
        }

        double heading, attitude, bank;
        if (_m10 > 1 - CCMath.ZERO_TOLERANCE) { // singularity at north pole
            heading = CCMath.atan2(_m02, _m22);
            attitude = CCMath.PI / 2;
            bank = 0;
        } else if (_m10 < -1 + CCMath.ZERO_TOLERANCE) { // singularity at south pole
            heading = CCMath.atan2(_m02, _m22);
            attitude = -CCMath.PI / 2;
            bank = 0;
        } else {
            heading = CCMath.atan2(-_m20, _m00);
            bank = CCMath.atan2(-_m12, _m11);
            attitude = CCMath.asin(_m10);
        }
        result[0] = bank;
        result[1] = heading;
        result[2] = attitude;

        return result;
    }

    /**
     * @param matrix
     * @return This matrix for chaining, modified internally to reflect multiplication against the given matrix
     * @throws NullPointerException
     *             if matrix is null
     */
    public CCMatrix3x3 multiplyLocal(final CCMatrix3x3 matrix) {
    	final double temp00 = _m00 * matrix._m00 + _m01 * matrix._m10 + _m02 * matrix._m20;
        final double temp01 = _m00 * matrix._m01 + _m01 * matrix._m11 + _m02 * matrix._m21;
        final double temp02 = _m00 * matrix._m02 + _m01 * matrix._m12 + _m02 * matrix._m22;
        final double temp10 = _m10 * matrix._m00 + _m11 * matrix._m10 + _m12 * matrix._m20;
        final double temp11 = _m10 * matrix._m01 + _m11 * matrix._m11 + _m12 * matrix._m21;
        final double temp12 = _m10 * matrix._m02 + _m11 * matrix._m12 + _m12 * matrix._m22;
        final double temp20 = _m20 * matrix._m00 + _m21 * matrix._m10 + _m22 * matrix._m20;
        final double temp21 = _m20 * matrix._m01 + _m21 * matrix._m11 + _m22 * matrix._m21;
        final double temp22 = _m20 * matrix._m02 + _m21 * matrix._m12 + _m22 * matrix._m22;

        set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);

        return this;
    }

    /**
     * @param matrix
     * @return this matrix multiplied by the given matrix.
     * @throws NullPointerException
     *             if matrix is null.
     */
    public CCMatrix3x3 multiply(final CCMatrix3x3 matrix) {
        CCMatrix3x3 result = new CCMatrix3x3();
        
        final double temp00 = _m00 * matrix._m00 + _m01 * matrix._m10 + _m02 * matrix._m20;
        final double temp01 = _m00 * matrix._m01 + _m01 * matrix._m11 + _m02 * matrix._m21;
        final double temp02 = _m00 * matrix._m02 + _m01 * matrix._m12 + _m02 * matrix._m22;
        final double temp10 = _m10 * matrix._m00 + _m11 * matrix._m10 + _m12 * matrix._m20;
        final double temp11 = _m10 * matrix._m01 + _m11 * matrix._m11 + _m12 * matrix._m21;
        final double temp12 = _m10 * matrix._m02 + _m11 * matrix._m12 + _m12 * matrix._m22;
        final double temp20 = _m20 * matrix._m00 + _m21 * matrix._m10 + _m22 * matrix._m20;
        final double temp21 = _m20 * matrix._m01 + _m21 * matrix._m11 + _m22 * matrix._m21;
        final double temp22 = _m20 * matrix._m02 + _m21 * matrix._m12 + _m22 * matrix._m22;

        result.set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);

        return result;
    }

    /**
     * Multiplies this matrix by the diagonal matrix formed by the given vector (v^D * M). If supplied, the result is
     * stored into the supplied "store" matrix.
     * 
     * @param vec
     * @return the store matrix, or a new matrix if store is null.
     * @throws NullPointerException
     *             if vec is null
     */
    public CCMatrix3x3 multiplyDiagonalPre(final CCVector3 vec) {
        CCMatrix3x3 result = new CCMatrix3x3();
        

        result.set( //
                vec.x * _m00, vec.x * _m01, vec.x * _m02, //
                vec.y * _m10, vec.y * _m11, vec.y * _m12, //
                vec.z * _m20, vec.z * _m21, vec.z * _m22);

        return result;
    }

    /**
     * Multiplies this matrix by the diagonal matrix formed by the given vector (M * v^D). If supplied, the result is
     * stored into the supplied "store" matrix.
     * 
     * @param vec
     * @return the store matrix, or a new matrix if store is null.
     * @throws NullPointerException
     *             if vec is null
     */
    public CCMatrix3x3 multiplyDiagonalPost(final CCVector3 vec) {
        CCMatrix3x3 result = new CCMatrix3x3();

        result.set( //
                vec.x * _m00, vec.y * _m01, vec.z * _m02, //
                vec.x * _m10, vec.y * _m11, vec.z * _m12, //
                vec.x * _m20, vec.y * _m21, vec.z * _m22);

        return result;
    }

    /**
     * Internally scales all values of this matrix by the given scalar.
     * 
     * @param scalar
     * @return this matrix for chaining.
     */
    public CCMatrix3x3 multiplyLocal(final double scalar) {
        _m00 *= scalar;
        _m01 *= scalar;
        _m02 *= scalar;
        _m10 *= scalar;
        _m11 *= scalar;
        _m12 *= scalar;
        _m20 *= scalar;
        _m21 *= scalar;
        _m22 *= scalar;
        return this;
    }

    /**
     * @param matrix
     *            the matrix to add to this.
     * @param store
     *            a matrix to store the result in. If store is null, a new matrix is created. Note that it IS safe for
     *            matrix and store to be the same object.
     * @return the result.
     * @throws NullPointerException
     *             if matrix is null
     */
   public CCMatrix3x3 add(final CCMatrix3x3 matrix) {
        CCMatrix3x3 result = new CCMatrix3x3();

        result._m00 = _m00 + matrix._m00;
        result._m01 = _m01 + matrix._m01;
        result._m02 = _m02 + matrix._m02;
        result._m10 = _m10 + matrix._m10;
        result._m11 = _m11 + matrix._m11;
        result._m12 = _m12 + matrix._m12;
        result._m20 = _m20 + matrix._m20;
        result._m21 = _m21 + matrix._m21;
        result._m22 = _m22 + matrix._m22;

        return result;
    }

    /**
     * Internally adds the values of the given matrix to this matrix.
     * 
     * @param matrix
     *            the matrix to add to this.
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if matrix is null
     */
    public CCMatrix3x3 addLocal(final CCMatrix3x3 matrix) {
    	_m00 = _m00 + matrix._m00;
        _m01 = _m01 + matrix._m01;
        _m02 = _m02 + matrix._m02;
        _m10 = _m10 + matrix._m10;
        _m11 = _m11 + matrix._m11;
        _m12 = _m12 + matrix._m12;
        _m20 = _m20 + matrix._m20;
        _m21 = _m21 + matrix._m21;
        _m22 = _m22 + matrix._m22;

        return this;
    }

    /**
     * @param matrix
     *            the matrix to subtract from this.
     * @return the result.
     * @throws NullPointerException
     *             if matrix is null
     */
    public CCMatrix3x3 subtract(final CCMatrix3x3 matrix) {
        CCMatrix3x3 result = new CCMatrix3x3();

        result._m00 = _m00 - matrix._m00;
        result._m01 = _m01 - matrix._m01;
        result._m02 = _m02 - matrix._m02;
        result._m10 = _m10 - matrix._m10;
        result._m11 = _m11 - matrix._m11;
        result._m12 = _m12 - matrix._m12;
        result._m20 = _m20 - matrix._m20;
        result._m21 = _m21 - matrix._m21;
        result._m22 = _m22 - matrix._m22;

        return result;
    }

    /**
     * Internally subtracts the values of the given matrix from this matrix.
     * 
     * @param matrix
     *            the matrix to subtract from this.
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if matrix is null
     */
    public CCMatrix3x3 subtractLocal(final CCMatrix3x3 matrix) {
    	_m00 = _m00 - matrix._m00;
        _m01 = _m01 - matrix._m01;
        _m02 = _m02 - matrix._m02;
        _m10 = _m10 - matrix._m10;
        _m11 = _m11 - matrix._m11;
        _m12 = _m12 - matrix._m12;
        _m20 = _m20 - matrix._m20;
        _m21 = _m21 - matrix._m21;
        _m22 = _m22 - matrix._m22;

        return this;
    }

    /**
     * Applies the given scale to this matrix and returns the result as a new matrix
     * 
     * @param scale
     * @return the new matrix
     * @throws NullPointerException
     *             if scale is null.
     */
    public CCMatrix3x3 scale(final CCVector3 scale) {
        CCMatrix3x3 result = new CCMatrix3x3();

        return result.set( //
                _m00 * scale.x, _m01 * scale.y, _m02 * scale.z, //
                _m10 * scale.x, _m11 * scale.y, _m12 * scale.z, //
                _m20 * scale.x, _m21 * scale.y, _m22 * scale.z);
    }

    /**
     * Applies the given scale to this matrix values internally
     * 
     * @param scale
     * @return this matrix for chaining.
     * @throws NullPointerException
     *             if scale is null.
     */
    public CCMatrix3x3 scaleLocal(final CCVector3 scale) {
    	return set( //
                _m00 * scale.x, _m01 * scale.y, _m02 * scale.z, //
                _m10 * scale.x, _m11 * scale.y, _m12 * scale.z, //
                _m20 * scale.x, _m21 * scale.y, _m22 * scale.z);
    }

    /**
     * transposes this matrix as a new matrix, basically flipping it across the diagonal
     * 
     * @return this matrix for chaining.
     * @see <a href="http://en.wikipedia.org/wiki/Transpose">wikipedia.org-Transpose</a>
     */
    public CCMatrix3x3 transpose() {
        CCMatrix3x3 result = new CCMatrix3x3();
        
        result._m00 = _m00;
        result._m01 = _m10;
        result._m02 = _m20;
        result._m10 = _m01;
        result._m11 = _m11;
        result._m12 = _m21;
        result._m20 = _m02;
        result._m21 = _m12;
        result._m22 = _m22;

        return result;
    }

    /**
     * transposes this matrix in place
     * 
     * @return this matrix for chaining.
     * @see <a href="http://en.wikipedia.org/wiki/Transpose">wikipedia.org-Transpose</a>
     */
    public CCMatrix3x3 transposeLocal() {
        final double m01 = _m01;
        final double m02 = _m02;
        final double m12 = _m12;
        _m01 = _m10;
        _m02 = _m20;
        _m12 = _m21;
        _m10 = m01;
        _m20 = m02;
        _m21 = m12;
        return this;
    }

    /**
     * @return a matrix that represents this matrix, inverted.
     * 
     *         if store is not null and is read only
     * @throws ArithmeticException
     *             if this matrix can not be inverted.
     */
    public CCMatrix3x3 invert() {
        CCMatrix3x3 result = new CCMatrix3x3();

        final double det = determinant();
        if (CCMath.abs(det) <= CCMath.FLT_EPSILON) {
            throw new ArithmeticException("This matrix cannot be inverted.");
        }

        final double temp00 = _m11 * _m22 - _m12 * _m21;
        final double temp01 = _m02 * _m21 - _m01 * _m22;
        final double temp02 = _m01 * _m12 - _m02 * _m11;
        final double temp10 = _m12 * _m20 - _m10 * _m22;
        final double temp11 = _m00 * _m22 - _m02 * _m20;
        final double temp12 = _m02 * _m10 - _m00 * _m12;
        final double temp20 = _m10 * _m21 - _m11 * _m20;
        final double temp21 = _m01 * _m20 - _m00 * _m21;
        final double temp22 = _m00 * _m11 - _m01 * _m10;
        result.set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);
        result.multiplyLocal(1.0f / det);
        return result;
    }

    /**
     * Inverts this matrix locally.
     * 
     * @return this matrix inverted internally.
     * @throws ArithmeticException
     *             if this matrix can not be inverted.
     */
    public CCMatrix3x3 invertLocal() {
    	final double det = determinant();
        if (CCMath.abs(det) <= CCMath.FLT_EPSILON) {
            throw new ArithmeticException("This matrix cannot be inverted.");
        }

        final double temp00 = _m11 * _m22 - _m12 * _m21;
        final double temp01 = _m02 * _m21 - _m01 * _m22;
        final double temp02 = _m01 * _m12 - _m02 * _m11;
        final double temp10 = _m12 * _m20 - _m10 * _m22;
        final double temp11 = _m00 * _m22 - _m02 * _m20;
        final double temp12 = _m02 * _m10 - _m00 * _m12;
        final double temp20 = _m10 * _m21 - _m11 * _m20;
        final double temp21 = _m01 * _m20 - _m00 * _m21;
        final double temp22 = _m00 * _m11 - _m01 * _m10;
        this.set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);
        this.multiplyLocal(1.0f / det);
        return  this;
    }

    /**
     * @return The adjugate, or classical adjoint, of this matrix
     * @see <a href="http://en.wikipedia.org/wiki/Adjugate_matrix">wikipedia.org-Adjugate_matrix</a>
     */
    public CCMatrix3x3 adjugate() {
        return clone().adjugateLocal();
    }

    /**
     * @return this matrix, modified to represent its adjugate, or classical adjoint
     * @see <a href="http://en.wikipedia.org/wiki/Adjugate_matrix">wikipedia.org-Adjugate_matrix</a>
     */
    public CCMatrix3x3 adjugateLocal() {
    	final double temp00 = _m11 * _m22 - _m12 * _m21;
        final double temp01 = _m02 * _m21 - _m01 * _m22;
        final double temp02 = _m01 * _m12 - _m02 * _m11;
        final double temp10 = _m12 * _m20 - _m10 * _m22;
        final double temp11 = _m00 * _m22 - _m02 * _m20;
        final double temp12 = _m02 * _m10 - _m00 * _m12;
        final double temp20 = _m10 * _m21 - _m11 * _m20;
        final double temp21 = _m01 * _m20 - _m00 * _m21;
        final double temp22 = _m00 * _m11 - _m01 * _m10;
        return set(temp00, temp01, temp02, temp10, temp11, temp12, temp20, temp21, temp22);
    }

    /**
     * @return the determinate of this 3x3 matrix (aei+bfg+cdh-ceg-bdi-afh)
     * @see <a href="http://en.wikipedia.org/wiki/Determinant">wikipedia.org-Determinant</a>
     */
    public double determinant() {
        return _m00 * _m11 * _m22 + _m01 * _m12 * _m20 + _m02 * _m10 * _m21 - //
                _m02 * _m11 * _m20 - _m01 * _m10 * _m22 - _m00 * _m12 * _m21;
    }

    /**
     * A function for creating a rotation matrix that rotates a vector called "start" into another vector called "end".
     * 
     * @param start
     *            normalized non-zero starting vector
     * @param end
     *            normalized non-zero ending vector
     * @return this matrix, for chaining
     * @see "Tomas Möller, John Hughes 'Efficiently Building a Matrix to Rotate One Vector to Another' Journal of Graphics Tools, 4(4):1-4, 1999"
     */
    public CCMatrix3x3 fromStartEndLocal(final CCVector3 start, final CCVector3 end) {
        final CCVector3 v = start.cross(end);
        double h;

        double e = start.dot(end);
        double f = e < 0 ? -e : e;

        // if "from" and "to" vectors are nearly parallel
        if (f > 1.0 - CCMath.ZERO_TOLERANCE) {
            final CCVector3 u = new CCVector3();
            final CCVector3 x = new CCVector3();
            double c1, c2, c3; /* coefficients for later use */

            x.x = start.x > 0.0 ? start.x : -start.x;
            x.y = start.y > 0.0 ? start.y : -start.y;
            x.z = start.z > 0.0 ? start.z : -start.z;

            if (x.x < x.y) {
                if (x.x < x.z) {
                    x.set(1.0f, 0.0f, 0.0f);
                } else {
                    x.set(0.0f, 0.0f, 1.0f);
                }
            } else {
                if (x.y < x.z) {
                    x.set(0.0f, 1.0f, 0.0f);
                } else {
                    x.set(0.0f, 0.0f, 1.0f);
                }
            }

            u.set(x).subtractLocal(start);
            v.set(x).subtractLocal(end);

            c1 = 2.0f / u.dot(u);
            c2 = 2.0f / v.dot(v);
            c3 = c1 * c2 * u.dot(v);

            _m00 = -c1 * u.x * u.x - c2 * v.x * v.x + c3 * v.x * u.x + 1.0f;
            _m01 = -c1 * u.x * u.y - c2 * v.x * v.y + c3 * v.x * u.y;
            _m02 = -c1 * u.x * u.z - c2 * v.x * v.z + c3 * v.x * u.z;
            _m10 = -c1 * u.y * u.x - c2 * v.y * v.x + c3 * v.y * u.x;
            _m11 = -c1 * u.y * u.y - c2 * v.y * v.y + c3 * v.y * u.y + 1.0f;
            _m12 = -c1 * u.y * u.z - c2 * v.y * v.z + c3 * v.y * u.z;
            _m20 = -c1 * u.z * u.x - c2 * v.z * v.x + c3 * v.z * u.x;
            _m21 = -c1 * u.z * u.y - c2 * v.z * v.y + c3 * v.z * u.y;
            _m22 = -c1 * u.z * u.z - c2 * v.z * v.z + c3 * v.z * u.z + 1.0f;
        } else {
            // the most common case, unless "start"="end", or "start"=-"end"
            double hvx, hvz, hvxy, hvxz, hvyz;
            h = 1.0f / (1.0f + e);
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            _m00 = e + hvx * v.x;
            _m01 = hvxy - v.z;
            _m02 = hvxz + v.y;

            _m10 = hvxy + v.z;
            _m11 = e + h * v.y * v.y;
            _m12 = hvyz - v.x;

            _m20 = hvxz - v.y;
            _m21 = hvyz + v.x;
            _m22 = e + hvz * v.z;
        }
        return this;
    }

    /**
     * Multiplies the given vector by this matrix (v * M). If supplied, the result is stored into the supplied "store"
     * vector.
     * 
     * @param vec
     *            the vector to multiply this matrix by.
     * @param store
     *            a vector to store the result in. If store is null, a new vector is created. Note that it IS safe for
     *            vec and store to be the same object.
     * @return the store vector, or a new vector if store is null.
     * @throws NullPointerException
     *             if vec is null
     */
    public CCVector3 applyPre(final CCVector3 vec) {
        CCVector3 result = new CCVector3();

        final double x = vec.x;
        final double y = vec.y;
        final double z = vec.z;

        result.x = _m00 * x + _m10 * y + _m20 * z;
        result.y = _m01 * x + _m11 * y + _m21 * z;
        result.z = _m02 * x + _m12 * y + _m22 * z;
        return result;
    }

    /**
     * Multiplies the given vector by this matrix (M * v). If supplied, the result is stored into the supplied "store"
     * vector.
     * 
     * @param vec
     *            the vector to multiply this matrix by.
     * @return the store vector, or a new vector if store is null.
     * @throws NullPointerException
     *             if vec is null
     */
    public CCVector3 applyPost(final CCVector3 vec, CCVector3 theStore) {
        if(theStore == null)theStore = new CCVector3();

        final double x = vec.x;
        final double y = vec.y;
        final double z = vec.z;

        theStore.x = _m00 * x + _m01 * y + _m02 * z;
        theStore.y = _m10 * x + _m11 * y + _m12 * z;
        theStore.z = _m20 * x + _m21 * y + _m22 * z;
        return theStore;
    }
    
    public CCVector3 applyPost(final CCVector3 vec){
    		return applyPost(vec, null);
    }

    /**
     * Modifies this matrix to equal the rotation required to point the z-axis at 'direction' and the y-axis to 'up'.
     * 
     * @param direction
     *            where to 'look' at
     * @param up
     *            a vector indicating the local up direction.
     * @return this matrix for chaining
     */
    public CCMatrix3x3 lookAt(final CCVector3 direction, final CCVector3 up) {
        final CCVector3 zAxis = direction.normalize();
        final CCVector3 xAxis = up.normalize().crossLocal(zAxis).normalizeLocal();
        final CCVector3 yAxis = zAxis.cross(xAxis);
       
        fromAxes(xAxis, yAxis, zAxis);

        return this;
    }

    /**
     * Check a matrix... if it is null or its doubles are NaN or infinite, return false. Else return true.
     * 
     * @param matrix
     *            the vector to check
     * @return true or false as stated above.
     */
    public static boolean isValid(final CCMatrix3x3 matrix) {
        if (matrix == null) {
            return false;
        }

        if (Double.isNaN(matrix._m00) || Double.isInfinite(matrix._m00)) {
            return false;
        } else if (Double.isNaN(matrix._m01) || Double.isInfinite(matrix._m01)) {
            return false;
        } else if (Double.isNaN(matrix._m02) || Double.isInfinite(matrix._m02)) {
            return false;
        } else if (Double.isNaN(matrix._m10) || Double.isInfinite(matrix._m10)) {
            return false;
        } else if (Double.isNaN(matrix._m11) || Double.isInfinite(matrix._m11)) {
            return false;
        } else if (Double.isNaN(matrix._m12) || Double.isInfinite(matrix._m12)) {
            return false;
        } else if (Double.isNaN(matrix._m20) || Double.isInfinite(matrix._m20)) {
            return false;
        } else if (Double.isNaN(matrix._m21) || Double.isInfinite(matrix._m21)) {
            return false;
        } else return !Double.isNaN(matrix._m22) && !Double.isInfinite(matrix._m22);

    }

    /**
     * @return true if this Matrix is orthonormal - its rows are orthogonal, unit vectors.
     */
    public boolean isOrthonormal() {
        if (CCMath.abs(_m00 * _m00 + _m01 * _m01 + _m02 * _m02 - 1.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m00 * _m10 + _m01 * _m11 + _m02 * _m12 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m00 * _m20 + _m01 * _m21 + _m02 * _m22 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m10 * _m00 + _m11 * _m01 + _m12 * _m02 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m10 * _m10 + _m11 * _m11 + _m12 * _m12 - 1.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m10 * _m20 + _m11 * _m21 + _m12 * _m22 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m20 * _m00 + _m21 * _m01 + _m22 * _m02 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else if (CCMath.abs(_m20 * _m10 + _m21 * _m11 + _m22 * _m12 - 0.0) > CCMath.ZERO_TOLERANCE) {
            return false;
        } else return !(CCMath.abs(_m20 * _m20 + _m21 * _m21 + _m22 * _m22 - 1.0) > CCMath.ZERO_TOLERANCE);

    }

    /**
     * @return the string representation of this matrix.
     */
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer("com.ardor3d.math.Matrix3\n[\n");
        result.append(' ');
        result.append(_m00);
        result.append(' ');
        result.append(_m01);
        result.append(' ');
        result.append(_m02);
        result.append(" \n");

        result.append(' ');
        result.append(_m10);
        result.append(' ');
        result.append(_m11);
        result.append(' ');
        result.append(_m12);
        result.append(" \n");

        result.append(' ');
        result.append(_m20);
        result.append(' ');
        result.append(_m21);
        result.append(' ');
        result.append(_m22);
        result.append(" \n");

        result.append(']');
        return result.toString();
    }

    /**
     * @return returns a unique code for this matrix object based on its values. If two matrices are numerically equal,
     *         they will return the same hash code value.
     */
    @Override
    public int hashCode() {
        int result = 17;

        long val = Double.doubleToLongBits(_m00);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m01);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m02);
        result += 31 * result + (int) (val ^ val >>> 32);

        val = Double.doubleToLongBits(_m10);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m11);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m12);
        result += 31 * result + (int) (val ^ val >>> 32);

        val = Double.doubleToLongBits(_m20);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m21);
        result += 31 * result + (int) (val ^ val >>> 32);
        val = Double.doubleToLongBits(_m22);
        result += 31 * result + (int) (val ^ val >>> 32);

        return result;
    }

    /**
     * @param o
     *            the object to compare for equality
     * @return true if this matrix and the provided matrix have the double values that are within the
     *         CCMath.ZERO_TOLERANCE.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CCMatrix3x3)) {
            return false;
        }
        final CCMatrix3x3 comp = (CCMatrix3x3) o;
        return 
        	CCMath.abs(_m00 - comp._m00) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m01 - comp._m01) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m02 - comp._m02) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m10 - comp._m10) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m11 - comp._m11) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m12 - comp._m12) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m20 - comp._m20) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m21 - comp._m21) < CCMatrix3x3.ALLOWED_DEVIANCE &&
            CCMath.abs(_m22 - comp._m22) < CCMatrix3x3.ALLOWED_DEVIANCE;
    }

    /**
     * @param o
     *            the object to compare for equality
     * @return true if this matrix and the provided matrix have the exact same double values.
     */
    public boolean strictEquals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CCMatrix3x3)) {
            return false;
        }
        final CCMatrix3x3 comp = (CCMatrix3x3) o;
        if (_m00 != comp._m00) {
            return false;
        } else if (_m01 != comp._m01) {
            return false;
        } else if (_m02 != comp._m02) {
            return false;
        } else if (_m10 != comp._m10) {
            return false;
        } else if (_m11 != comp._m11) {
            return false;
        } else if (_m12 != comp._m12) {
            return false;
        } else if (_m20 != comp._m20) {
            return false;
        } else if (_m21 != comp._m21) {
            return false;
        } else return !(_m22 != comp._m22);

    }

    // /////////////////
    // Method for Cloneable
    // /////////////////

    @Override
    public CCMatrix3x3 clone() {
        return new CCMatrix3x3(this);
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
        _m00 = in.readDouble();
        _m01 = in.readDouble();
        _m02 = in.readDouble();
        _m10 = in.readDouble();
        _m11 = in.readDouble();
        _m12 = in.readDouble();
        _m20 = in.readDouble();
        _m21 = in.readDouble();
        _m22 = in.readDouble();
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
        out.writeDouble(_m00);
        out.writeDouble(_m01);
        out.writeDouble(_m02);
        out.writeDouble(_m10);
        out.writeDouble(_m11);
        out.writeDouble(_m12);
        out.writeDouble(_m20);
        out.writeDouble(_m21);
        out.writeDouble(_m22);
    }
}
