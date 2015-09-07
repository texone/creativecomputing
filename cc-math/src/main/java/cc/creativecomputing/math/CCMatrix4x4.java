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
 * CCMatrix4 represents a double precision 4x4 matrix and contains a flag, set at
 * object creation, indicating if the given CCMatrix4 object is mutable.
 * 
 * Note: some algorithms in this class were ported from Eberly, Wolfram, Game
 * Gems and others to Java.
 */
public class CCMatrix4x4 implements Cloneable, Externalizable {
	
	 /**
     * 
     * @param theLeft
     * @param theRight
     * @param theBottom
     * @param theTop
     * @param theNearZ
     * @param theFarZ
     * @param theStore
     */
	public static CCMatrix4x4 createFrustum(
		final double theLeft, final double theRight, 
		final double theBottom, final double theTop, 
		final double theNearZ, final double theFarZ, 
		CCMatrix4x4 theStore
	) {
		if(theStore == null)theStore = new CCMatrix4x4();
		
		final double x = 2.0f * theNearZ / (theRight - theLeft);
		final double y = 2.0f * theNearZ / (theTop - theBottom);
		final double a = (theRight + theLeft) / (theRight - theLeft);
		final double b = (theTop + theBottom) / (theTop - theBottom);
		final double c = -(theFarZ + theNearZ) / (theFarZ - theNearZ);
		final double d = -(2.0f * theFarZ * theNearZ) / (theFarZ - theNearZ);

		return theStore.set(
			x, 0, a,  0f, 
			0, y, b,  0, 
			0, 0, c, d, 
			0,0, -1,  0
		);
	}
	
	/**
     * 
     * @param theLeft
     * @param theRight
     * @param theBottom
     * @param theTop
     * @param theNearZ
     * @param theFarZ
     */
	public static CCMatrix4x4 createFrustum(
		final double theLeft, final double theRight, 
		final double theBottom, final double theTop, 
		final double theNearZ, final double theFarZ
	){
		return createFrustum(theLeft, theRight, theBottom, theTop, theNearZ, theFarZ, null);
	}

    /**
     * 
     * @param left
     * @param theRight
     * @param theBottom
     * @param theTop
     * @param theNearZ
     * @param theFarZ
     * @param theStore
     */
    public static CCMatrix4x4 createOrtho(
    	final double theLeft, final double theRight, 
    	final double theBottom, final double theTop,
    	final double theNearZ, final double theFarZ, 
    	CCMatrix4x4 theStore
    ) {
		if(theStore == null)theStore = new CCMatrix4x4();
    	
    	final double x = 2f / (theRight - theLeft);
    	final double y = 2f / (theTop - theBottom);
    	final double z = -2f / (theFarZ - theNearZ);
    	final double a = -(theRight + theLeft) / (theRight - theLeft);
    	final double b = -(theTop + theBottom) / (theTop - theBottom);
    	final double c = -(theFarZ + theNearZ) / (theFarZ - theNearZ);
    	
        return theStore.set(
        	x, 0, 0, a, 
        	0, y, 0, b, 
        	0, 0, z, c, 
        	0, 0, 0, 1
        );
    }
    
    /**
     * 
     * @param left
     * @param theRight
     * @param theBottom
     * @param theTop
     * @param theNearZ
     * @param theFarZ
     * @param theStore
     */
    public static CCMatrix4x4 createOrtho(
    	final double theLeft, final double theRight, 
    	final double theBottom, final double theTop,
    	final double theNearZ, final double theFarZ
    ) {
    	return createOrtho(theLeft, theRight, theBottom, theTop, theNearZ, theFarZ, null);
    }

    /**
     * 
     * @param theFovY
     * @param theAspect
     * @param theZNear
     * @param theZFar
     * @param theStore
     */
    public static CCMatrix4x4 createPerspective(
    	final double theFovY, 
    	final double theAspect, 
    	final double theZNear, 
    	final double theZFar,
    	CCMatrix4x4 theStore
    ) {
		if(theStore == null)theStore = new CCMatrix4x4();
		
		final double halfFovyRadians =  CCMath.radians( (theFovY / 2.0f) );
		final double range =  CCMath.tan(halfFovyRadians) * theZNear;
		final double left = -range * theAspect;
		final double right = range * theAspect;
		final double bottom = -range;
		final double top = range;
		
		return createFrustum(left, right, bottom, top, theZNear, theZFar);
    }
    
    /**
     * 
     * @param theFovY
     * @param theAspect
     * @param theZNear
     * @param theZFar
     * @param theStore
     */
    public static CCMatrix4x4 createPerspective(
    	final double theFovY, 
    	final double theAspect, 
    	final double theZNear, 
    	final double theZFar
    ) {
    	return createPerspective(theFovY, theAspect, theZNear, theZFar, null);
    }
    
    /**
	 * Creates a view matrix based on the given camera frame
	 * @param thePosition
	 * @param theDirection
	 * @param theUp
	 * @param theRight
	 */
	public static CCMatrix4x4 createFrame(
		final CCVector3 thePosition, 
		final CCVector3 theDirection, 
		final CCVector3 theUp, 
		final CCVector3 theRight,
		CCMatrix4x4 theStore
	) {
		if(theStore == null)theStore = new CCMatrix4x4();
		
		theStore.set(
			theRight.x, theRight.y, theRight.z, -theRight.dot(thePosition),
			theUp.x, theUp.y, theUp.z, -theUp.dot(thePosition),
			-theDirection.x, -theDirection.y, -theDirection.z, theDirection.dot(thePosition),
			0f, 0f, 0f, 1f
		);
		
		return theStore;
	}
	
	public static CCMatrix4x4 createFrame(
		final CCVector3 thePosition, 
		final CCVector3 theDirection, 
		final CCVector3 theUp, 
		final CCVector3 theRight
	) {
		return createFrame(thePosition, theDirection, theUp, theRight, null);
	}

    /**
     * 
     * @param position
     * @param theTarget
     * @param up
     * @param theStore
     */
	public static CCMatrix4x4 createLookAt(
		final CCVector3 thePosition, 
		final CCVector3 theTarget, 
		final CCVector3 theUp, 
		CCMatrix4x4 theStore
	) {
		final CCVector3 myDirection = theTarget.clone().subtract(thePosition).normalize();
		CCVector3 myUp = theUp.clone().normalize();
		final CCVector3 myRight = myDirection.cross(myUp).normalize();
		myUp = myRight.cross(myDirection);
		
		return createFrame(thePosition, myDirection, myUp, myRight, theStore);
	}
	
	public static CCMatrix4x4 createLookAt(
		final CCVector3 thePosition, 
		final CCVector3 theTarget, 
		final CCVector3 theWorldUp
	) {
		return createLookAt(thePosition, theTarget, theWorldUp, null);
	}
    
	/**
	 * Used with equals method to determine if two CCMatrix4 objects are close
	 * enough to be considered equal.
	 */
	

	private static final long serialVersionUID = 1L;

	/**
	 * <pre>
	 * 1, 0, 0, 0
	 * 0, 1, 0, 0
	 * 0, 0, 1, 0
	 * 0, 0, 0, 1
	 * </pre>
	 */
	public final static CCMatrix4x4 IDENTITY = new CCMatrix4x4(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

	public double m00, m01, m02, m03, //
			m10, m11, m12, m13, //
			m20, m21, m22, m23, //
			m30, m31, m32, m33;

	/**
	 * Constructs a new matrix set to identity.
	 */
	public CCMatrix4x4() {
		this(CCMatrix4x4.IDENTITY);
	}

	/**
	 * Constructs a new matrix set to the given matrix values. (names are mRC =
	 * m[ROW][COL])
	 * 
	 * @param the00
	 * @param the10
	 * @param the20
	 * @param the30
	 * @param the01
	 * @param the11
	 * @param the21
	 * @param the31
	 * @param the02
	 * @param the12
	 * @param the22
	 * @param the32
	 * @param the03
	 * @param the13
	 * @param the23
	 * @param the33
	 */
	public CCMatrix4x4(
		final double the00, final double the10, final double the20, final double the30, 
		final double the01, final double the11, final double the21, final double the31, 
		final double the02, final double the12, final double the22, final double the32,
		final double the03, final double the13, final double the23, final double the33
	) {
		m00 = the00; m10 = the10; m20 = the20; m30 = the30;
		m01 = the01; m11 = the11; m21 = the21; m31 = the31;
		m02 = the02; m12 = the12; m22 = the22; m23 = the23;
		m03 = the03; m13 = the13; m32 = the32; m33 = the33;
	}

	/**
	 * Constructs a new matrix set to the values of the given matrix.
	 * 
	 * @param theSource
	 */
	public CCMatrix4x4(final CCMatrix4x4 theSource) {
		set(theSource);
	}

	/**
	 * @param theRow
	 * @param theColumn
	 * @return the value stored in this matrix at row, column.
	 * @throws IllegalArgumentException
	 *             if row and column are not in bounds [0, 3]
	 */
	public double getValue(final int theRow, final int theColumn) {
		switch (theRow) {
		case 0:
			switch (theColumn) {
			case 0:
				return m00;
			case 1:
				return m10;
			case 2:
				return m20;
			case 3:
				return m30;
			}
			break;
		case 1:
			switch (theColumn) {
			case 0:
				return m01;
			case 1:
				return m11;
			case 2:
				return m21;
			case 3:
				return m31;
			}
			break;

		case 2:
			switch (theColumn) {
			case 0:
				return m02;
			case 1:
				return m12;
			case 2:
				return m22;
			case 3:
				return m32;
			}
			break;

		case 3:
			switch (theColumn) {
			case 0:
				return m03;
			case 1:
				return m13;
			case 2:
				return m23;
			case 3:
				return m33;
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
	public CCMatrix4x4 setIdentity() {
		return set(CCMatrix4x4.IDENTITY);
	}

	/**
	 * @return true if this matrix equals the 4x4 identity matrix
	 */
	public boolean isIdentity() {
		return strictEquals(CCMatrix4x4.IDENTITY);
	}

	/**
	 * Sets the value of this matrix at row, column to the given value.
	 * 
	 * @param theRow
	 * @param theColumn
	 * @param theValue
	 * @return this matrix for chaining
	 * @throws IllegalArgumentException
	 *             if row and column are not in bounds [0, 3]
	 */
	public CCMatrix4x4 setValue(final int theRow, final int theColumn, final double theValue) {
		switch (theRow) {
		case 0:
			switch (theColumn) {
			case 0:
				m00 = theValue;
				break;
			case 1:
				m10 = theValue;
				break;
			case 2:
				m20 = theValue;
				break;
			case 3:
				m30 = theValue;
				break;
			default:
				throw new IllegalArgumentException();
			}
			break;

		case 1:
			switch (theColumn) {
			case 0:
				m01 = theValue;
				break;
			case 1:
				m11 = theValue;
				break;
			case 2:
				m21 = theValue;
				break;
			case 3:
				m31 = theValue;
				break;
			default:
				throw new IllegalArgumentException();
			}
			break;

		case 2:
			switch (theColumn) {
			case 0:
				m02 = theValue;
				break;
			case 1:
				m12 = theValue;
				break;
			case 2:
				m22 = theValue;
				break;
			case 3:
				m32 = theValue;
				break;
			default:
				throw new IllegalArgumentException();
			}
			break;

		case 3:
			switch (theColumn) {
			case 0:
				m03 = theValue;
				break;
			case 1:
				m13 = theValue;
				break;
			case 2:
				m23 = theValue;
				break;
			case 3:
				m33 = theValue;
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
	 * @param the00
	 * @param the10
	 * @param the20
	 * @param the30
	 * @param the01
	 * @param the11
	 * @param the21
	 * @param the31
	 * @param the02
	 * @param the12
	 * @param the22
	 * @param the32
	 * @param the03
	 * @param the13
	 * @param the23
	 * @param the33
	 * @return this matrix for chaining
	 */
	public CCMatrix4x4 set(
		final double the00, final double the10, final double the20, final double the30, 
		final double the01, final double the11, final double the21, final double the31, 
		final double the02, final double the12, final double the22, final double the32,
		final double the03, final double the13, final double the23, final double the33
	) {
		m00 = the00; m10 = the10; m20 = the20; m30 = the30; 
		m01 = the01; m11 = the11; m21 = the21; m31 = the31; 
		m02 = the02; m12 = the12; m22 = the22; m32 = the32; 
		m03 = the03; m13 = the13; m23 = the23; m33 = the33;

		return this;
	}

	/**
	 * Sets the values of this matrix to the values of the provided source
	 * matrix.
	 * 
	 * @param theSource
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCMatrix4x4 set(final CCMatrix4x4 theSource) {
		m00 = theSource.m00;
		m01 = theSource.m01;
		m02 = theSource.m02;
		m03 = theSource.m03;

		m10 = theSource.m10;
		m11 = theSource.m11;
		m12 = theSource.m12;
		m13 = theSource.m13;

		m20 = theSource.m20;
		m21 = theSource.m21;
		m22 = theSource.m22;
		m23 = theSource.m23;

		m30 = theSource.m30;
		m31 = theSource.m31;
		m32 = theSource.m32;
		m33 = theSource.m33;

		return this;
	}

	/**
	 * Sets the 3x3 rotation part of this matrix to the values of the provided
	 * source matrix.
	 * 
	 * @param theSource
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCMatrix4x4 set(final CCMatrix3x3 theSource) {
		m00 = theSource._m00;
		m01 = theSource._m01;
		m02 = theSource._m02;
		m10 = theSource._m10;
		m11 = theSource._m11;
		m12 = theSource._m12;
		m20 = theSource._m20;
		m21 = theSource._m21;
		m22 = theSource._m22;
		return this;
	}

	/**
	 * Sets the values of this matrix to the rotational value of the given
	 * quaternion. Only modifies the 3x3 rotation part of this matrix.
	 * 
	 * @param theQuaternion
	 * @return this matrix for chaining
	 */
	public CCMatrix4x4 set(final CCQuaternion theQuaternion) {
		return theQuaternion.toRotationMatrix(this);
	}

	/**
	 * Note: data is cast to floats.
	 * 
	 * @param store
	 *            the buffer to read our matrix data from.
	 * @return this matrix for chaining.
	 */
	public CCMatrix4x4 fromDoubleBuffer(final DoubleBuffer theSource) {
		return fromDoubleBuffer(theSource, true);
	}

	/**
	 * Note: data is cast to floats.
	 * 
	 * @param store
	 *            the buffer to read our matrix data from.
	 * @param theRowMajor
	 *            if true, data is stored row by row. Otherwise it is stored
	 *            column by column.
	 * @return this matrix for chaining.
	 */
	public CCMatrix4x4 fromDoubleBuffer(final DoubleBuffer theSource, final boolean theRowMajor) {
		if (theRowMajor) {
			m00 = theSource.get();
			m01 = theSource.get();
			m02 = theSource.get();
			m03 = theSource.get();
			m10 = theSource.get();
			m11 = theSource.get();
			m12 = theSource.get();
			m13 = theSource.get();
			m20 = theSource.get();
			m21 = theSource.get();
			m22 = theSource.get();
			m23 = theSource.get();
			m30 = theSource.get();
			m31 = theSource.get();
			m32 = theSource.get();
			m33 = theSource.get();
		} else {
			m00 = theSource.get();
			m10 = theSource.get();
			m20 = theSource.get();
			m30 = theSource.get();
			m01 = theSource.get();
			m11 = theSource.get();
			m21 = theSource.get();
			m31 = theSource.get();
			m02 = theSource.get();
			m12 = theSource.get();
			m22 = theSource.get();
			m32 = theSource.get();
			m03 = theSource.get();
			m13 = theSource.get();
			m23 = theSource.get();
			m33 = theSource.get();
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
	 *             if source array has a length less than 16.
	 */
	public CCMatrix4x4 fromArray(final double[] theSource) {
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
	 *             if source array has a length less than 16.
	 */
	public CCMatrix4x4 fromArray(final double[] theSource, final boolean theRowMajor) {
		if (theRowMajor) {
			m00 = theSource[0];
			m01 = theSource[1];
			m02 = theSource[2];
			m03 = theSource[3];
			m10 = theSource[4];
			m11 = theSource[5];
			m12 = theSource[6];
			m13 = theSource[7];
			m20 = theSource[8];
			m21 = theSource[9];
			m22 = theSource[10];
			m23 = theSource[11];
			m30 = theSource[12];
			m31 = theSource[13];
			m32 = theSource[14];
			m33 = theSource[15];
		} else {
			m00 = theSource[0];
			m10 = theSource[1];
			m20 = theSource[2];
			m30 = theSource[3];
			m01 = theSource[4];
			m11 = theSource[5];
			m21 = theSource[6];
			m31 = theSource[7];
			m02 = theSource[8];
			m12 = theSource[9];
			m22 = theSource[10];
			m32 = theSource[11];
			m03 = theSource[12];
			m13 = theSource[13];
			m23 = theSource[14];
			m33 = theSource[15];
		}
		return this;
	}

	/**
	 * Replaces a column in this matrix with the values of the given array.
	 * 
	 * @param theIndex
	 * @param theData
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if columnData is null.
	 * @throws IllegalArgumentException
	 *             if columnData has a length < 4
	 * @throws IllegalArgumentException
	 *             if columnIndex is not in [0, 3]
	 */
	public CCMatrix4x4 setColumn(final int theIndex, final CCVector4 theData) {
		switch (theIndex) {
		case 0:
			m00 = theData.x;
			m01 = theData.y;
			m02 = theData.z;
			m03 = theData.w;
			break;
		case 1:
			m10 = theData.x;
			m11 = theData.y;
			m12 = theData.z;
			m13 = theData.w;
			break;
		case 2:
			m20 = theData.x;
			m21 = theData.y;
			m22 = theData.z;
			m23 = theData.w;
			break;
		case 3:
			m30 = theData.x;
			m31 = theData.y;
			m32 = theData.z;
			m33 = theData.w;
			break;
		default:
			throw new IllegalArgumentException("Bad columnIndex: " + theIndex);
		}
		return this;
	}

	/**
	 * Replaces a row in this matrix with the values of the given array.
	 * 
	 * @param theIndex
	 * @param theData
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if rowData is null.
	 * @throws IllegalArgumentException
	 *             if rowData has a length < 4
	 * @throws IllegalArgumentException
	 *             if rowIndex is not in [0, 3]
	 */
	public CCMatrix4x4 setRow(final int theIndex, final CCVector4 theData) {
		switch (theIndex) {
		case 0:
			m00 = theData.x;
			m10 = theData.y;
			m20 = theData.z;
			m30 = theData.w;
			break;
		case 1:
			m01 = theData.x;
			m11 = theData.y;
			m21 = theData.z;
			m31 = theData.w;
			break;
		case 2:
			m02 = theData.x;
			m12 = theData.y;
			m22 = theData.z;
			m32 = theData.w;
			break;
		case 3:
			m03 = theData.x;
			m13 = theData.y;
			m23 = theData.z;
			m33 = theData.w;
			break;
		default:
			throw new IllegalArgumentException("Bad rowIndex: " + theIndex);
		}
		return this;
	}

	/**
	 * Sets the 3x3 rotation portion of this matrix to the rotation indicated by
	 * the given angle and axis of rotation. Note: This method creates an
	 * object, so use fromAngleNormalAxis when possible, particularly if your
	 * axis is already normalized.
	 * 
	 * @param theAngle
	 *            the angle to rotate (in radians).
	 * @param theAxis
	 *            the axis of rotation.
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if axis is null.
	 */
	public CCMatrix4x4 fromAngleAxis(final double theAngle, final CCVector3 theAxis) {
		final CCVector3 normAxis = theAxis.normalize();
		fromAngleNormalAxis(theAngle, normAxis);
		return this;
	}

	/**
	 * Sets the 3x3 rotation portion of this matrix to the rotation indicated by
	 * the given angle and a unit-length axis of rotation.
	 * 
	 * @param theAngle
	 *            the angle to rotate (in radians).
	 * @param theAxis
	 *            the axis of rotation (already normalized).
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if axis is null.
	 */
	public CCMatrix4x4 fromAngleNormalAxis(final double theAngle, final CCVector3 theAxis) {
		final double fCos = CCMath.cos(theAngle);
		final double fSin = CCMath.sin(theAngle);
		final double fOneMinusCos = 1.0f - fCos;
		final double fX2 = theAxis.x * theAxis.x;
		final double fY2 = theAxis.y * theAxis.y;
		final double fZ2 = theAxis.z * theAxis.z;
		final double fXYM = theAxis.x * theAxis.y * fOneMinusCos;
		final double fXZM = theAxis.x * theAxis.z * fOneMinusCos;
		final double fYZM = theAxis.y * theAxis.z * fOneMinusCos;
		final double fXSin = theAxis.x * fSin;
		final double fYSin = theAxis.y * fSin;
		final double fZSin = theAxis.z * fSin;

		m00 = fX2 * fOneMinusCos + fCos;
		m01 = fXYM - fZSin;
		m02 = fXZM + fYSin;
		m10 = fXYM + fZSin;
		m11 = fY2 * fOneMinusCos + fCos;
		m12 = fYZM - fXSin;
		m20 = fXZM - fYSin;
		m21 = fYZM + fXSin;
		m22 = fZ2 * fOneMinusCos + fCos;

		return this;
	}

	public CCMatrix4x4 applyRotation(final double theAngle, final double theX, final double theY, final double theZ) {
		final double my00 = m00, my10 = m10, my20 = m20; //
		final double my01 = m01, my11 = m11, my21 = m21; //
		final double my02 = m02, my12 = m12, my22 = m22; //
		final double my03 = m03, my13 = m13, my23 = m23;

		final double cosAngle = CCMath.cos(theAngle);
		final double sinAngle = CCMath.sin(theAngle);
		final double oneMinusCos = 1.0f - cosAngle;
		final double xyOneMinusCos = theX * theY * oneMinusCos;
		final double xzOneMinusCos = theX * theZ * oneMinusCos;
		final double yzOneMinusCos = theY * theZ * oneMinusCos;
		final double xSin = theX * sinAngle;
		final double ySin = theY * sinAngle;
		final double zSin = theZ * sinAngle;

		final double r00 = theX * theX * oneMinusCos + cosAngle;
		final double r01 = xyOneMinusCos - zSin;
		final double r02 = xzOneMinusCos + ySin;
		final double r10 = xyOneMinusCos + zSin;
		final double r11 = theY * theY * oneMinusCos + cosAngle;
		final double r12 = yzOneMinusCos - xSin;
		final double r20 = xzOneMinusCos - ySin;
		final double r21 = yzOneMinusCos + xSin;
		final double r22 = theZ * theZ * oneMinusCos + cosAngle;

		m00 = my00 * r00 + my10 * r10 + my20 * r20;
		m10 = my00 * r01 + my10 * r11 + my20 * r21;
		m20 = my00 * r02 + my10 * r12 + my20 * r22;
		// _m03 is unchanged

		m01 = my01 * r00 + my11 * r10 + my21 * r20;
		m11 = my01 * r01 + my11 * r11 + my21 * r21;
		m21 = my01 * r02 + my11 * r12 + my21 * r22;
		// _m13 is unchanged

		m02 = my02 * r00 + my12 * r10 + my22 * r20;
		m12 = my02 * r01 + my12 * r11 + my22 * r21;
		m22 = my02 * r02 + my12 * r12 + my22 * r22;
		// _m23 is unchanged

		m03 = my03 * r00 + my13 * r10 + my23 * r20;
		m13 = my03 * r01 + my13 * r11 + my23 * r21;
		m23 = my03 * r02 + my13 * r12 + my23 * r22;
		// _m33 is unchanged

		return this;
	}

	public CCMatrix4x4 applyRotationX(final double theAngle) {
		final double my10 = m10, my20 = m20; //
		final double my11 = m11, my21 = m21; //
		final double my12 = m12, my22 = m22; //
		final double my13 = m13, my23 = m23;

		final double cosAngle = CCMath.cos(theAngle);
		final double sinAngle = CCMath.sin(theAngle);

		m10 = my10 * cosAngle + my20 * sinAngle;
		m20 = my20 * cosAngle - my10 * sinAngle;

		m11 = my11 * cosAngle + my21 * sinAngle;
		m21 = my21 * cosAngle - my11 * sinAngle;

		m12 = my12 * cosAngle + my22 * sinAngle;
		m22 = my22 * cosAngle - my12 * sinAngle;

		m13 = my13 * cosAngle + my23 * sinAngle;
		m23 = my23 * cosAngle - my13 * sinAngle;

		return this;
	}

	public CCMatrix4x4 applyRotationY(final double theAngle) {
		final double my00 = m00, my20 = m20; //
		final double my01 = m01, my21 = m21; //
		final double my02 = m02, my22 = m22; //
		final double my03 = m03, my23 = m23;

		final double cosAngle = CCMath.cos(theAngle);
		final double sinAngle = CCMath.sin(theAngle);

		m00 = my00 * cosAngle - my20 * sinAngle;
		m20 = my00 * sinAngle + my20 * cosAngle;

		m01 = my01 * cosAngle - my21 * sinAngle;
		m21 = my01 * sinAngle + my21 * cosAngle;

		m02 = my02 * cosAngle - my22 * sinAngle;
		m22 = my02 * sinAngle + my22 * cosAngle;

		m03 = my03 * cosAngle - my23 * sinAngle;
		m23 = my03 * sinAngle + my23 * cosAngle;

		return this;
	}

	public CCMatrix4x4 applyRotationZ(final double theAngle) {
		final double my00 = m00, my10 = m10; //
		final double my01 = m01, my11 = m11; //
		final double my02 = m02, my12 = m12; //
		final double my03 = m03, my13 = m13;

		final double cosAngle = CCMath.cos(theAngle);
		final double sinAngle = CCMath.sin(theAngle);

		m00 = my00 * cosAngle + my10 * sinAngle;
		m10 = my10 * cosAngle - my00 * sinAngle;

		m01 = my01 * cosAngle + my11 * sinAngle;
		m11 = my11 * cosAngle - my01 * sinAngle;

		m02 = my02 * cosAngle + my12 * sinAngle;
		m12 = my12 * cosAngle - my02 * sinAngle;

		m03 = my03 * cosAngle + my13 * sinAngle;
		m13 = my13 * cosAngle - my03 * sinAngle;

		return this;
	}

	/**
	 * M*T
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public CCMatrix4x4 applyTranslationPost(final double theX, final double theY, final double theZ) {
		m30 = m00 * theX + m10 * theY + m20 * theZ + m30;
		m31 = m01 * theX + m11 * theY + m21 * theZ + m31;
		m32 = m02 * theX + m12 * theY + m22 * theZ + m32;
		m33 = m03 * theX + m13 * theY + m23 * theZ + m33;

		return this;
	}

	/**
	 * T*M
	 * 
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public CCMatrix4x4 applyTranslationPre(final double theX, final double theY, final double theZ) {
		m30 = theX;
		m31 = theY;
		m32 = theZ;

		return this;
	}

	/**
	 * @param theIndex
	 * @return the column specified by the index.
	 * @throws IllegalArgumentException
	 *             if index is not in bounds [0, 3]
	 */
	public CCVector4 getColumn(final int theIndex, CCVector4 theStore) {
		if (theStore == null)
			theStore = new CCVector4();

		switch (theIndex) {
		case 0:
			theStore.x = m00;
			theStore.y = m01;
			theStore.z = m02;
			theStore.w = m03;
			break;
		case 1:
			theStore.x = m10;
			theStore.y = m11;
			theStore.z = m12;
			theStore.w = m13;
			break;
		case 2:
			theStore.x = m20;
			theStore.y = m21;
			theStore.z = m22;
			theStore.w = m23;
			break;
		case 3:
			theStore.x = m30;
			theStore.y = m31;
			theStore.z = m32;
			theStore.w = m33;
			break;
		default:
			throw new IllegalArgumentException("invalid column index: " + theIndex);
		}

		return theStore;
	}

	public CCVector4 getColumn(final int theIndex) {
		return getColumn(theIndex, null);
	}

	/**
	 * @param theIndex
	 * @return the row specified by the index.
	 * @throws IllegalArgumentException
	 *             if index is not in bounds [0, 3]
	 */
	public CCVector4 getRow(final int theIndex, CCVector4 theStore) {
		if (theStore == null)
			theStore = new CCVector4();
		switch (theIndex) {
		case 0:
			theStore.x = m00;
			theStore.y = m10;
			theStore.z = m20;
			theStore.w = m30;
			break;
		case 1:
			theStore.x = m01;
			theStore.y = m11;
			theStore.z = m21;
			theStore.w = m31;
			break;
		case 2:
			theStore.x = m02;
			theStore.y = m12;
			theStore.z = m22;
			theStore.w = m32;
			break;
		case 3:
			theStore.x = m03;
			theStore.y = m13;
			theStore.z = m23;
			theStore.w = m33;
			break;
		default:
			throw new IllegalArgumentException("invalid row index: " + theIndex);
		}
		return theStore;
	}

	public CCVector4 getRow(final int theIndex) {
		return getRow(theIndex, null);
	}
	
	public CCMatrix3x3 matrix3(CCMatrix3x3 theMatrix){
		theMatrix.set(
			m00, m10, m20, 
			m01, m11, m21, 
			m02, m12, m22
		);
		return theMatrix;
	}
		
	public CCMatrix3x3 matrix3(){
		return matrix3(new CCMatrix3x3());
	}
	
	public FloatBuffer toFloatBuffer(){
		return toFloatBuffer(FloatBuffer.allocate(16));
	}

	/**
	 * Note: data is cast to floats.
	 * 
	 * @param theStore
	 *            the buffer to store our matrix data in. Must not be null. Data
	 *            is entered starting at current buffer
	 * @return matrix data as a DoubleBuffer in row major order. The position is
	 *         at the end of the inserted data.
	 * @throws NullPointerException
	 *             if store is null.
	 * @throws BufferOverflowException
	 *             if there is not enough room left in the buffer to write all
	 *             16 values.
	 */
	public FloatBuffer toFloatBuffer(final FloatBuffer theStore) {
		return toFloatBuffer(theStore, true);
	}
	
	private float[] _myFloatBufferContainer = new float[16];

	/**
	 * 
	 * @param theStore
	 *            the buffer to store our matrix data in. Must not be null. Data
	 *            is entered starting at current buffer
	 * @param theRowMajor
	 *            if true, data is stored row by row. Otherwise it is stored
	 *            column by column.
	 * @return matrix data as a DoubleBuffer in the specified order. The position
	 *         is at the end of the inserted data.
	 * @throws NullPointerException
	 *             if store is null.
	 * @throws BufferOverflowException
	 *             if there is not enough room left in the buffer to write all
	 *             16 values.
	 */
	public FloatBuffer toFloatBuffer(final FloatBuffer theStore, final boolean theRowMajor) {
		theStore.put(toArray(_myFloatBufferContainer, theRowMajor));
		theStore.rewind();
		return theStore;
	}

	/**
	 * @param theStore
	 *            the double array to store our matrix data in. If null, a new
	 *            array is created.
	 * @return matrix data as a double array in row major order.
	 * @throws IllegalArgumentException
	 *             if the store is non-null and has a length < 16
	 */
	public float[] toArray(final float[] theStore) {
		return toArray(theStore, true);
	}

	/**
	 * @param theStore
	 *            the double array to store our matrix data in. If null, a new
	 *            array is created.
	 * @param theRowMajor
	 *            if true, data is stored row by row. Otherwise it is stored
	 *            column by column.
	 * @return matrix data as a double array in the specified order.
	 * @throws IllegalArgumentException
	 *             if the store is non-null and has a length < 16
	 */
	public float[] toArray(final float[] theStore, final boolean theRowMajor) {
		float[] result = theStore;
		if (result == null) {
			result = new float[16];
		} else if (result.length < 16) {
			throw new IllegalArgumentException("store must be at least length 16.");
		}

		if (theRowMajor) {
			result[0] = (float)m00;
			result[1] = (float)m01;
			result[2] = (float)m02;
			result[3] = (float)m03;
			result[4] = (float)m10;
			result[5] = (float)m11;
			result[6] = (float)m12;
			result[7] = (float)m13;
			result[8] = (float)m20;
			result[9] = (float)m21;
			result[10] = (float)m22;
			result[11] = (float)m23;
			result[12] = (float)m30;
			result[13] = (float)m31;
			result[14] = (float)m32;
			result[15] = (float)m33;
		} else {
			result[0] = (float)m00;
			result[1] = (float)m10;
			result[2] = (float)m20;
			result[3] = (float)m30;
			result[4] = (float)m01;
			result[5] = (float)m11;
			result[6] = (float)m21;
			result[7] = (float)m31;
			result[8] = (float)m02;
			result[9] = (float)m12;
			result[10] = (float)m22;
			result[11] = (float)m32;
			result[12] = (float)m03;
			result[13] = (float)m13;
			result[14] = (float)m23;
			result[15] = (float)m33;
		}

		return result;
	}
	
	public DoubleBuffer toDoubleBuffer(){
		return toDoubleBuffer(DoubleBuffer.allocate(16));
	}

	/**
	 * Note: data is cast to floats.
	 * 
	 * @param theStore
	 *            the buffer to store our matrix data in. Must not be null. Data
	 *            is entered starting at current buffer
	 * @return matrix data as a DoubleBuffer in row major order. The position is
	 *         at the end of the inserted data.
	 * @throws NullPointerException
	 *             if store is null.
	 * @throws BufferOverflowException
	 *             if there is not enough room left in the buffer to write all
	 *             16 values.
	 */
	public DoubleBuffer toDoubleBuffer(final DoubleBuffer theStore) {
		return toDoubleBuffer(theStore, true);
	}
	
	private double[] _mybufferContainer = new double[16];

	/**
	 * 
	 * @param theStore
	 *            the buffer to store our matrix data in. Must not be null. Data
	 *            is entered starting at current buffer
	 * @param theRowMajor
	 *            if true, data is stored row by row. Otherwise it is stored
	 *            column by column.
	 * @return matrix data as a DoubleBuffer in the specified order. The position
	 *         is at the end of the inserted data.
	 * @throws NullPointerException
	 *             if store is null.
	 * @throws BufferOverflowException
	 *             if there is not enough room left in the buffer to write all
	 *             16 values.
	 */
	public DoubleBuffer toDoubleBuffer(final DoubleBuffer theStore, final boolean theRowMajor) {
		theStore.put(toArray(_mybufferContainer, theRowMajor));
		theStore.rewind();
		return theStore;
	}

	/**
	 * @param theStore
	 *            the double array to store our matrix data in. If null, a new
	 *            array is created.
	 * @return matrix data as a double array in row major order.
	 * @throws IllegalArgumentException
	 *             if the store is non-null and has a length < 16
	 */
	public double[] toArray(final double[] theStore) {
		return toArray(theStore, true);
	}

	/**
	 * @param theStore
	 *            the double array to store our matrix data in. If null, a new
	 *            array is created.
	 * @param theRowMajor
	 *            if true, data is stored row by row. Otherwise it is stored
	 *            column by column.
	 * @return matrix data as a double array in the specified order.
	 * @throws IllegalArgumentException
	 *             if the store is non-null and has a length < 16
	 */
	public double[] toArray(final double[] theStore, final boolean theRowMajor) {
		double[] result = theStore;
		if (result == null) {
			result = new double[16];
		} else if (result.length < 16) {
			throw new IllegalArgumentException("store must be at least length 16.");
		}

		if (theRowMajor) {
			result[0] = m00;
			result[1] = m01;
			result[2] = m02;
			result[3] = m03;
			result[4] = m10;
			result[5] = m11;
			result[6] = m12;
			result[7] = m13;
			result[8] = m20;
			result[9] = m21;
			result[10] = m22;
			result[11] = m23;
			result[12] = m30;
			result[13] = m31;
			result[14] = m32;
			result[15] = m33;
		} else {
			result[0] = m00;
			result[1] = m10;
			result[2] = m20;
			result[3] = m30;
			result[4] = m01;
			result[5] = m11;
			result[6] = m21;
			result[7] = m31;
			result[8] = m02;
			result[9] = m12;
			result[10] = m22;
			result[11] = m32;
			result[12] = m03;
			result[13] = m13;
			result[14] = m23;
			result[15] = m33;
		}

		return result;
	}

	/**
	 * Multiplies this matrix by the diagonal matrix formed by the given vector
	 * (v^D * M). If supplied, the result is stored into the supplied "store"
	 * matrix.
	 * 
	 * @param theVector
	 * @return the store matrix, or a new matrix if store is null.
	 * @throws NullPointerException
	 *             if vec is null
	 */
	public CCMatrix4x4 multiplyDiagonalPre(final CCVector4 theVector, CCMatrix4x4 theStore) {
		if (theStore == null)
			theStore = new CCMatrix4x4();

		return theStore.set( //
			theVector.x * m00, theVector.y * m01, theVector.z * m02, theVector.w * m03, //
			theVector.x * m10, theVector.y * m11, theVector.z * m12, theVector.w * m13, //
			theVector.x * m20, theVector.y * m21, theVector.z * m22, theVector.w * m23, //
			theVector.x * m30, theVector.y * m31, theVector.z * m32, theVector.w * m33
		);
	}
	
	public CCMatrix4x4 multiplyDiagonalPre(final CCVector4 theVector){
		return multiplyDiagonalPre(theVector, null);
	}

	/**
	 * Multiplies this matrix by the diagonal matrix formed by the given vector
	 * (M * v^D). If supplied, the result is stored into the supplied "store"
	 * matrix.
	 * 
	 * @param theVector
	 * @return the store matrix, or a new matrix if store is null.
	 * @throws NullPointerException
	 *             if vec is null
	 */
	public CCMatrix4x4 multiplyDiagonalPost(final CCVector4 theVector, CCMatrix4x4 theStore) {
		if (theStore == null)
			theStore = new CCMatrix4x4();

		return theStore.set( //
			theVector.x * m00, theVector.x * m01, theVector.x * m02, theVector.x * m03, //
			theVector.y * m10, theVector.y * m11, theVector.y * m12, theVector.y * m13, //
			theVector.z * m20, theVector.z * m21, theVector.z * m22, theVector.z * m23, //
			theVector.w * m30, theVector.w * m31, theVector.w * m32, theVector.w * m33
		);
	}

	public CCMatrix4x4 multiplyDiagonalPost(final CCVector4 theVector) {
		return multiplyDiagonalPost(theVector, null);
	}

	/**
	 * @param theMatrix
	 * @return This matrix for chaining, modified internally to reflect
	 *         multiplication against the given matrix
	 * @throws NullPointerException
	 *             if matrix is null
	 */
	public CCMatrix4x4 multiply(final CCMatrix4x4 theMatrix, CCMatrix4x4 theStore) {
		if(theStore == null)theStore = new CCMatrix4x4();
		
		final double temp00 = m00 * theMatrix.m00 + m01 * theMatrix.m10 + m02 * theMatrix.m20 + m03 * theMatrix.m30;
		final double temp01 = m00 * theMatrix.m01 + m01 * theMatrix.m11 + m02 * theMatrix.m21 + m03 * theMatrix.m31;
		final double temp02 = m00 * theMatrix.m02 + m01 * theMatrix.m12 + m02 * theMatrix.m22 + m03 * theMatrix.m32;
		final double temp03 = m00 * theMatrix.m03 + m01 * theMatrix.m13 + m02 * theMatrix.m23 + m03 * theMatrix.m33;

		final double temp10 = m10 * theMatrix.m00 + m11 * theMatrix.m10 + m12 * theMatrix.m20 + m13 * theMatrix.m30;
		final double temp11 = m10 * theMatrix.m01 + m11 * theMatrix.m11 + m12 * theMatrix.m21 + m13 * theMatrix.m31;
		final double temp12 = m10 * theMatrix.m02 + m11 * theMatrix.m12 + m12 * theMatrix.m22 + m13 * theMatrix.m32;
		final double temp13 = m10 * theMatrix.m03 + m11 * theMatrix.m13 + m12 * theMatrix.m23 + m13 * theMatrix.m33;

		final double temp20 = m20 * theMatrix.m00 + m21 * theMatrix.m10 + m22 * theMatrix.m20 + m23 * theMatrix.m30;
		final double temp21 = m20 * theMatrix.m01 + m21 * theMatrix.m11 + m22 * theMatrix.m21 + m23 * theMatrix.m31;
		final double temp22 = m20 * theMatrix.m02 + m21 * theMatrix.m12 + m22 * theMatrix.m22 + m23 * theMatrix.m32;
		final double temp23 = m20 * theMatrix.m03 + m21 * theMatrix.m13 + m22 * theMatrix.m23 + m23 * theMatrix.m33;

		final double temp30 = m30 * theMatrix.m00 + m31 * theMatrix.m10 + m32 * theMatrix.m20 + m33 * theMatrix.m30;
		final double temp31 = m30 * theMatrix.m01 + m31 * theMatrix.m11 + m32 * theMatrix.m21 + m33 * theMatrix.m31;
		final double temp32 = m30 * theMatrix.m02 + m31 * theMatrix.m12 + m32 * theMatrix.m22 + m33 * theMatrix.m32;
		final double temp33 = m30 * theMatrix.m03 + m31 * theMatrix.m13 + m32 * theMatrix.m23 + m33 * theMatrix.m33;

		return theStore.set(
			temp00, temp10, temp20, temp30, 
			temp01, temp11, temp21, temp31, 
			temp02, temp12, temp22, temp32, 
			temp03, temp13, temp23, temp33
		);
	}

	/**
	 * @param theMatrix
	 * @return this matrix multiplied by the given matrix.
	 * @throws NullPointerException
	 *             if matrix is null.
	 */
	public CCMatrix4x4 multiply(final CCMatrix4x4 theMatrix) {
		return multiply(theMatrix, null);
	}

	public CCMatrix4x4 multiplyLocal(final CCMatrix4x4 theMatrix) {
		return multiply(theMatrix, this);
	}

	/**
	 * Internally scales all values of this matrix by the given scalar.
	 * 
	 * @param scalar
	 * @return this matrix for chaining.
	 */
	public CCMatrix4x4 multiplyLocal(final double scalar) {
		m00 *= scalar;
		m01 *= scalar;
		m02 *= scalar;
		m03 *= scalar;
		m10 *= scalar;
		m11 *= scalar;
		m12 *= scalar;
		m13 *= scalar;
		m20 *= scalar;
		m21 *= scalar;
		m22 *= scalar;
		m23 *= scalar;
		m30 *= scalar;
		m31 *= scalar;
		m32 *= scalar;
		m33 *= scalar;

		return this;
	}

	/**
	 * @param matrix
	 *            the matrix to add to this.
	 * @return the result.
	 * @throws NullPointerException
	 *             if matrix is null
	 */
	public CCMatrix4x4 add(final CCMatrix4x4 matrix) {
		return add(matrix, null);
	}

	public CCMatrix4x4 add(final CCMatrix4x4 theMatrix, CCMatrix4x4 theStore) {
		if (theStore == null)
			theStore = new CCMatrix4x4();
		theStore.m00 = m00 + theMatrix.m00;
		theStore.m01 = m01 + theMatrix.m01;
		theStore.m02 = m02 + theMatrix.m02;
		theStore.m03 = m03 + theMatrix.m03;
		theStore.m10 = m10 + theMatrix.m10;
		theStore.m11 = m11 + theMatrix.m11;
		theStore.m12 = m12 + theMatrix.m12;
		theStore.m13 = m13 + theMatrix.m13;
		theStore.m20 = m20 + theMatrix.m20;
		theStore.m21 = m21 + theMatrix.m21;
		theStore.m22 = m22 + theMatrix.m22;
		theStore.m23 = m23 + theMatrix.m23;
		theStore.m30 = m30 + theMatrix.m30;
		theStore.m31 = m31 + theMatrix.m31;
		theStore.m32 = m32 + theMatrix.m32;
		theStore.m33 = m33 + theMatrix.m33;
		return theStore;
	}

	/**
	 * Internally adds the values of the given matrix to this matrix.
	 * 
	 * @param theMatrix
	 *            the matrix to add to this.
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if matrix is null
	 */
	public CCMatrix4x4 addLocal(final CCMatrix4x4 theMatrix) {
		return add(theMatrix, this);
	}

	/**
	 * @param theMatrix
	 *            the matrix to subtract from this.
	 * @return the result.
	 * @throws NullPointerException
	 *             if matrix is null
	 */
	public CCMatrix4x4 subtract(final CCMatrix4x4 theMatrix, CCMatrix4x4 theStore) {
		if(theStore == null)theStore = new CCMatrix4x4();
		return theStore.set(
			m00 - theMatrix.m00, m10 - theMatrix.m10, m20 - theMatrix.m20, m30 - theMatrix.m30,
			m01 - theMatrix.m01, m11 - theMatrix.m11, m21 - theMatrix.m21, m31 - theMatrix.m31,
			m02 - theMatrix.m02, m12 - theMatrix.m12, m22 - theMatrix.m22, m32 - theMatrix.m32,
			m03 - theMatrix.m03, m13 - theMatrix.m13, m23 - theMatrix.m23, m33 - theMatrix.m33
		);
	}
	
	public CCMatrix4x4 subtract(final CCMatrix4x4 theMatrix) {
		return subtract(theMatrix, null);
	}

	/**
	 * Internally subtracts the values of the given matrix from this matrix.
	 * 
	 * @param theMatrix
	 *            the matrix to subtract from this.
	 * @return this matrix for chaining
	 * @throws NullPointerException
	 *             if matrix is null
	 */
	public CCMatrix4x4 subtractLocal(final CCMatrix4x4 theMatrix) {
		return subtract(theMatrix, this);
	}

	/**
	 * Applies the given scale to this matrix and returns the result as a new
	 * matrix
	 * 
	 * @param theScale
	 * @return the new matrix
	 * @throws NullPointerException
	 *             if scale is null.
	 */
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ, 
		final double theScaleW, 
		CCMatrix4x4 theStore
	) {
		if(theStore == null)theStore = new CCMatrix4x4();
		return theStore.set( //
			m00 * theScaleX, m10 * theScaleX, m20 * theScaleX, m30 * theScaleX, 
			m01 * theScaleY, m11 * theScaleY, m21 * theScaleY, m31 * theScaleY, 
			m02 * theScaleZ, m12 * theScaleZ, m22 * theScaleZ, m32 * theScaleZ, 
			m03 * theScaleW, m13 * theScaleW, m23 * theScaleW, m33 * theScaleW
		);
	}
	
	public CCMatrix4x4 scale(CCVector4 theScale, CCMatrix4x4 theStore){
		return scale(theScale.x, theScale.y, theScale.z, theScale.w, theStore);
	}
	
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ, 
		final double theScaleW
	){
		return scale(theScaleX, theScaleY, theScaleZ, theScaleW, null);
	}
	
	public CCMatrix4x4 scale(CCVector4 theScale){
		return scale(theScale.x, theScale.y, theScale.z, theScale.w);
	}

	/**
	 * Applies the given scale to this matrix values internally
	 * 
	 * @param scale
	 * @return this matrix for chaining.
	 * @throws NullPointerException
	 *             if scale is null.
	 */
	public CCMatrix4x4 scaleLocal(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ, 
		final double theScaleW
	) {
		return scale(theScaleX, theScaleY, theScaleZ, theScaleW, this);
	}
	
	public CCMatrix4x4 scaleLocal(CCVector4 theScale){
		return scale(theScale.x, theScale.y, theScale.z, theScale.w, this);
	}
	
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ,
		final CCMatrix4x4 theStore
	){
		return scale(theScaleX, theScaleY, theScaleZ, 1, theStore);
	}
	
	public CCMatrix4x4 scale(CCVector3 theScale, final CCMatrix4x4 theStore){
		return scale(theScale.x, theScale.y, theScale.z, theStore);
	}
	
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ
	){
		return scale(theScaleX, theScaleY, theScaleZ, 1, null);
	}
	
	public CCMatrix4x4 scale(CCVector3 theScale){
		return scale(theScale.x, theScale.y, theScale.z);
	}
	
	public CCMatrix4x4 scaleLocal(
		final double theScaleX, 
		final double theScaleY, 
		final double theScaleZ
	){
		return scale(theScaleX, theScaleY, theScaleZ, 1, this);
	}
		
	public CCMatrix4x4 scaleLocal(CCVector3 theScale){
		return scale(theScale.x, theScale.y, theScale.z);
	}
	
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY,
		final CCMatrix4x4 theStore
	){
		return scale(theScaleX, theScaleY, 1, 1, theStore);
	}
	
	public CCMatrix4x4 scale(CCVector2 theScale, final CCMatrix4x4 theStore){
		return scale(theScale.x, theScale.y, theStore);
	}
	
	public CCMatrix4x4 scale(
		final double theScaleX, 
		final double theScaleY
	){
		return scale(theScaleX, theScaleY, null);
	}
	
	public CCMatrix4x4 scale(CCVector2 theScale){
		return scale(theScale.x, theScale.y);
	}
	
	public CCMatrix4x4 scaleLocal(
		final double theScaleX, 
		final double theScaleY
	){
		return scale(theScaleX, theScaleY, this);
	}
		
	public CCMatrix4x4 scaleLocal(CCVector2 theScale){
		return scale(theScale.x, theScale.y);
	}
	
	public CCMatrix4x4 scale(final double theScale, CCMatrix4x4 theStore){
		return scale(theScale, theScale, theScale, 1, theStore);
	}
	
	public CCMatrix4x4 scale(final double theScale){
		return scale(theScale, null);
	}
	
	public CCMatrix4x4 scaleLocal(final double theScale){
		return scale(theScale, this);
	}

	/**
	 * transposes this matrix as a new matrix, basically flipping it across the
	 * diagonal
	 * 
	 * @return this matrix for chaining.
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Transpose">wikipedia.org-Transpose</a>
	 */
	public CCMatrix4x4 transpose(CCMatrix4x4 theStore) {
		if(theStore == null)theStore = new CCMatrix4x4();
		
		return theStore.set(
			m00, m01, m02, m03, 
			m10, m11, m12, m13, 
			m20, m21, m22, m23, 
			m30, m31, m32, m33
		);
	}
	
	public CCMatrix4x4 transpose(){
		return transpose(null);
	}

	/**
	 * transposes this matrix in place
	 * 
	 * @return this matrix for chaining.
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Transpose">wikipedia.org-Transpose</a>
	 */
	public CCMatrix4x4 transposeLocal() {
		return transpose(this);
	}

	/**
	 * @return a matrix that represents this matrix, inverted.
	 * @throws ArithmeticException
	 *             if this matrix can not be inverted.
	 */
	public CCMatrix4x4 invert(CCMatrix4x4 theStore) {
		if(theStore == null) theStore = new CCMatrix4x4();
		final double dA0 = m00 * m11 - m01 * m10;
		final double dA1 = m00 * m12 - m02 * m10;
		final double dA2 = m00 * m13 - m03 * m10;
		final double dA3 = m01 * m12 - m02 * m11;
		final double dA4 = m01 * m13 - m03 * m11;
		final double dA5 = m02 * m13 - m03 * m12;
		final double dB0 = m20 * m31 - m21 * m30;
		final double dB1 = m20 * m32 - m22 * m30;
		final double dB2 = m20 * m33 - m23 * m30;
		final double dB3 = m21 * m32 - m22 * m31;
		final double dB4 = m21 * m33 - m23 * m31;
		final double dB5 = m22 * m33 - m23 * m32;
		final double det = dA0 * dB5 - dA1 * dB4 + dA2 * dB3 + dA3 * dB2 - dA4 * dB1 + dA5 * dB0;

		if (CCMath.abs(det) <= CCMath.FLT_EPSILON) {
			throw new ArithmeticException("This matrix cannot be inverted");
		}

		final double temp00 = +m11 * dB5 - m12 * dB4 + m13 * dB3;
		final double temp10 = -m10 * dB5 + m12 * dB2 - m13 * dB1;
		final double temp20 = +m10 * dB4 - m11 * dB2 + m13 * dB0;
		final double temp30 = -m10 * dB3 + m11 * dB1 - m12 * dB0;
		final double temp01 = -m01 * dB5 + m02 * dB4 - m03 * dB3;
		final double temp11 = +m00 * dB5 - m02 * dB2 + m03 * dB1;
		final double temp21 = -m00 * dB4 + m01 * dB2 - m03 * dB0;
		final double temp31 = +m00 * dB3 - m01 * dB1 + m02 * dB0;
		final double temp02 = +m31 * dA5 - m32 * dA4 + m33 * dA3;
		final double temp12 = -m30 * dA5 + m32 * dA2 - m33 * dA1;
		final double temp22 = +m30 * dA4 - m31 * dA2 + m33 * dA0;
		final double temp32 = -m30 * dA3 + m31 * dA1 - m32 * dA0;
		final double temp03 = -m21 * dA5 + m22 * dA4 - m23 * dA3;
		final double temp13 = +m20 * dA5 - m22 * dA2 + m23 * dA1;
		final double temp23 = -m20 * dA4 + m21 * dA2 - m23 * dA0;
		final double temp33 = +m20 * dA3 - m21 * dA1 + m22 * dA0;

		theStore.set(temp00, temp10, temp20, temp30, temp01, temp11, temp21, temp31, temp02, temp12, temp22, temp32, temp03, temp13, temp23, temp33);
		theStore.multiplyLocal(1.0f / det);

		return theStore;
	}
	
	public CCMatrix4x4 invert() {
		return invert(null);
	}

	/**
	 * inverts this matrix locally.
	 * 
	 * @return this matrix inverted internally.
	 * @throws ArithmeticException
	 *             if this matrix can not be inverted.
	 */
	public CCMatrix4x4 invertLocal() {
		return invert(this);
	}

	/**
	 * @return The adjugate, or classical adjoint, of this matrix
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Adjugate_matrix">wikipedia.org-Adjugate_matrix</a>
	 */
	public CCMatrix4x4 adjugate(CCMatrix4x4 theStore) {
		if(theStore == null)theStore = new CCMatrix4x4();
		final double dA0 = m00 * m11 - m01 * m10;
		final double dA1 = m00 * m12 - m02 * m10;
		final double dA2 = m00 * m13 - m03 * m10;
		final double dA3 = m01 * m12 - m02 * m11;
		final double dA4 = m01 * m13 - m03 * m11;
		final double dA5 = m02 * m13 - m03 * m12;
		final double dB0 = m20 * m31 - m21 * m30;
		final double dB1 = m20 * m32 - m22 * m30;
		final double dB2 = m20 * m33 - m23 * m30;
		final double dB3 = m21 * m32 - m22 * m31;
		final double dB4 = m21 * m33 - m23 * m31;
		final double dB5 = m22 * m33 - m23 * m32;

		final double temp00 = +m11 * dB5 - m12 * dB4 + m13 * dB3;
		final double temp10 = -m10 * dB5 + m12 * dB2 - m13 * dB1;
		final double temp20 = +m10 * dB4 - m11 * dB2 + m13 * dB0;
		final double temp30 = -m10 * dB3 + m11 * dB1 - m12 * dB0;
		final double temp01 = -m01 * dB5 + m02 * dB4 - m03 * dB3;
		final double temp11 = +m00 * dB5 - m02 * dB2 + m03 * dB1;
		final double temp21 = -m00 * dB4 + m01 * dB2 - m03 * dB0;
		final double temp31 = +m00 * dB3 - m01 * dB1 + m02 * dB0;
		final double temp02 = +m31 * dA5 - m32 * dA4 + m33 * dA3;
		final double temp12 = -m30 * dA5 + m32 * dA2 - m33 * dA1;
		final double temp22 = +m30 * dA4 - m31 * dA2 + m33 * dA0;
		final double temp32 = -m30 * dA3 + m31 * dA1 - m32 * dA0;
		final double temp03 = -m21 * dA5 + m22 * dA4 - m23 * dA3;
		final double temp13 = +m20 * dA5 - m22 * dA2 + m23 * dA1;
		final double temp23 = -m20 * dA4 + m21 * dA2 - m23 * dA0;
		final double temp33 = +m20 * dA3 - m21 * dA1 + m22 * dA0;

		return theStore.set(
			temp00, temp10, temp20, temp30, 
			temp01, temp11, temp21, temp31, 
			temp02, temp12, temp22, temp32, 
			temp03, temp13, temp23, temp33
		);
	}
	
	public CCMatrix4x4 adjugate() {
		return adjugate(null);
	}

	/**
	 * @return this matrix, modified to represent its adjugate, or classical
	 *         adjoint
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Adjugate_matrix">wikipedia.org-Adjugate_matrix</a>
	 */
	public CCMatrix4x4 adjugateLocal() {
		return adjugate(this);
	}

	/**
	 * @return the determinate of this matrix
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Determinant">wikipedia.org-Determinant</a>
	 */
	public double determinant() {
		final double val1 = 
			m11 * m22 * m33 + 
			m12 * m23 * m31 + 
			m13 * m21 * m32 - //
			m13 * m22 * m31 - 
			m12 * m21 * m33 - 
			m11 * m23 * m32;
		final double val2 = 
			m10 * m22 * m33 + 
			m12 * m23 * m30 + 
			m13 * m20 * m32 - //
			m13 * m22 * m30 - 
			m12 * m20 * m33 - 
			m10 * m23 * m32;
		final double val3 = 
			m10 * m21 * m33 + 
			m11 * m23 * m30 + 
			m13 * m20 * m31 - //
			m13 * m21 * m30 - 
			m11 * m20 * m33 - 
			m10 * m23 * m31;
		final double val4 = 
			m10 * m21 * m32 +
			m11 * m22 * m30 + 
			m12 * m20 * m31 - //
			m12 * m21 * m30 - 
			m11 * m20 * m32 - 
			m10 * m22 * m31;

		return m00 * val1 - m01 * val2 + m02 * val3 - m03 * val4;
	}

	/**
	 * Multiplies the given vector by this matrix (v * M). If supplied, the
	 * result is stored into the supplied "store" vector.
	 * 
	 * @param vector
	 *            the vector to multiply this matrix by.
	 * @return the store vector, or a new vector if store is null.
	 * @throws NullPointerException
	 *             if vector is null
	 */
	public CCVector4 applyPre(final CCVector4 vector, CCVector4 theStore) {
		if(theStore == null) theStore = new CCVector4();

		final double x = vector.x;
		final double y = vector.y;
		final double z = vector.z;
		final double w = vector.w;

		theStore.x = m00 * x + m01 * y + m02 * z + m03 * w;
		theStore.y = m10 * x + m11 * y + m12 * z + m13 * w;
		theStore.z = m20 * x + m21 * y + m22 * z + m23 * w;
		theStore.w = m30 * x + m31 * y + m32 * z + m33 * w;

		return theStore;
	}
	
	public CCVector4 applyPre(final CCVector4 theVector){
		return applyPre(theVector, null);
	}

	/**
	 * Multiplies the given vector by this matrix (M * v). If supplied, the
	 * result is stored into the supplied "store" vector.
	 * 
	 * @param theVector
	 *            the vector to multiply this matrix by.
	 * @return the store vector, or a new vector if store is null.
	 * @throws NullPointerException
	 *             if vector is null
	 */
	public CCVector4 applyPost(final CCVector4 theVector, CCVector4 theStore) {
		if (theStore == null)
			theStore = new CCVector4();

		final double x = theVector.x;
		final double y = theVector.y;
		final double z = theVector.z;
		final double w = theVector.w;

		theStore.x = m00 * x + m10 * y + m20 * z + m30 * w;
		theStore.y = m01 * x + m11 * y + m21 * z + m31 * w;
		theStore.z = m02 * x + m12 * y + m22 * z + m32 * w;
		theStore.w = m03 * x + m13 * y + m23 * z + m33 * w;

		return theStore;
	}

	public CCVector4 applyPost(final CCVector4 theVector) {
		return applyPost(theVector, null);
	}

	/**
	 * Multiplies the given point by this matrix (M * p). If supplied, the
	 * result is stored into the supplied "store" vector.
	 * 
	 * @param thePoint
	 *            the point to multiply against this matrix.
	 * @return the store object, or a new CCVector3 if store is null.
	 * @throws NullPointerException
	 *             if point is null
	 */
	public CCVector3 applyPostPoint(final CCVector3 thePoint, CCVector3 theStore) {
		if(theStore == null) theStore = new CCVector3();

		final double x = thePoint.x;
		final double y = thePoint.y;
		final double z = thePoint.z;

		theStore.x = m00 * x + m10 * y + m20 * z + m30;
		theStore.y = m01 * x + m11 * y + m21 * z + m31;
		theStore.z = m02 * x + m12 * y + m22 * z + m32;

		return theStore;
	}
	
	public CCVector3 applyPostPoint(final CCVector3 thePoint){
		return applyPostPoint(thePoint, null);
	}

	/**
	 * Multiplies the given vector by this matrix (M * v). If supplied, the
	 * result is stored into the supplied "store" vector.
	 * 
	 * @param theVector
	 *            the vector to multiply this matrix by.
	 * @return the store vector, or a new vector if store is null.
	 * @throws NullPointerException
	 *             if vector is null
	 */
	public CCVector3 applyPostVector(final CCVector3 theVector, CCVector3 theStore) {
		if(theStore == null)theStore = new CCVector3();

		final double x = theVector.x;
		final double y = theVector.y;
		final double z = theVector.z;

		theStore.x = m00 * x + m10 * y + m20 * z;
		theStore.y = m01 * x + m11 * y + m21 * z;
		theStore.z = m02 * x + m12 * y + m22 * z;

		return theStore;
	}
	
	public CCVector3 applyPostVector(final CCVector3 theVector){
		return applyPostVector(theVector, null);
	}

	/**
	 * Check a matrix... if it is null or its doubles are NaN or infinite,
	 * return false. Else return true.
	 * 
	 * @param matrix
	 *            the vector to check
	 * @return true or false as stated above.
	 */
	public static boolean isValid(final CCMatrix4x4 matrix) {
		if (matrix == null) {
			return false;
		}

		if (Double.isNaN(matrix.m00) || Double.isInfinite(matrix.m00)) {
			return false;
		} else if (Double.isNaN(matrix.m01) || Double.isInfinite(matrix.m01)) {
			return false;
		} else if (Double.isNaN(matrix.m02) || Double.isInfinite(matrix.m02)) {
			return false;
		} else if (Double.isNaN(matrix.m03) || Double.isInfinite(matrix.m03)) {
			return false;
		} else if (Double.isNaN(matrix.m10) || Double.isInfinite(matrix.m10)) {
			return false;
		} else if (Double.isNaN(matrix.m11) || Double.isInfinite(matrix.m11)) {
			return false;
		} else if (Double.isNaN(matrix.m12) || Double.isInfinite(matrix.m12)) {
			return false;
		} else if (Double.isNaN(matrix.m13) || Double.isInfinite(matrix.m13)) {
			return false;
		} else if (Double.isNaN(matrix.m20) || Double.isInfinite(matrix.m20)) {
			return false;
		} else if (Double.isNaN(matrix.m21) || Double.isInfinite(matrix.m21)) {
			return false;
		} else if (Double.isNaN(matrix.m22) || Double.isInfinite(matrix.m22)) {
			return false;
		} else if (Double.isNaN(matrix.m23) || Double.isInfinite(matrix.m23)) {
			return false;
		} else if (Double.isNaN(matrix.m30) || Double.isInfinite(matrix.m30)) {
			return false;
		} else if (Double.isNaN(matrix.m31) || Double.isInfinite(matrix.m31)) {
			return false;
		} else if (Double.isNaN(matrix.m32) || Double.isInfinite(matrix.m32)) {
			return false;
		} else if (Double.isNaN(matrix.m33) || Double.isInfinite(matrix.m33)) {
			return false;
		}

		return true;
	}

	/**
	 * @return true if this Matrix is orthonormal
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Orthogonal_matrix">wikipedia.org-Orthogonal
	 *      matrix</a>
	 */
	public boolean isOrthonormal() {
		final double myM00 = m00;
		final double my01 = m01;
		final double my02 = m02;
		final double my03 = m03;
		final double my10 = m10;
		final double my11 = m11;
		final double my12 = m12;
		final double my13 = m13;
		final double my20 = m20;
		final double my21 = m21;
		final double my22 = m22;
		final double my23 = m23;
		final double my30 = m30;
		final double my31 = m31;
		final double my32 = m32;
		final double my33 = m33;

		if (CCMath.abs(myM00 * myM00 + my01 * my01 + my02 * my02 + my03 * my03 - 1.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(myM00 * my10 + my01 * my11 + my02 * my12 + my03 * my13 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(myM00 * my20 + my01 * my21 + my02 * my22 + my03 * my23 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(myM00 * my30 + my01 * my31 + my02 * my32 + my03 * my33 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}

		if (CCMath.abs(my10 * myM00 + my11 * my01 + my12 * my02 + my13 * my03 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my10 * my10 + my11 * my11 + my12 * my12 + my13 * my13 - 1.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my10 * my20 + my11 * my21 + my12 * my22 + my13 * my23 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my10 * my30 + my11 * my31 + my12 * my32 + my13 * my33 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}

		if (CCMath.abs(my20 * myM00 + my21 * my01 + my22 * my02 + my23 * my03 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my20 * my10 + my21 * my11 + my22 * my12 + my23 * my13 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my20 * my20 + my21 * my21 + my22 * my22 + my23 * my23 - 1.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my20 * my30 + my21 * my31 + my22 * my32 + my23 * my33 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}

		if (CCMath.abs(my30 * myM00 + my31 * my01 + my32 * my02 + my33 * my03 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my30 * my10 + my31 * my11 + my32 * my12 + my33 * my13 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my30 * my20 + my31 * my21 + my32 * my22 + my33 * my23 - 0.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}
		if (CCMath.abs(my30 * my30 + my31 * my31 + my32 * my32 + my33 * my33 - 1.0) > CCMath.ZERO_TOLERANCE) {
			return false;
		}

		return true;
	}

	/**
	 * @return the string representation of this matrix.
	 */
	@Override
	public String toString() {
		final StringBuffer result = new StringBuffer(getClass().getName() + "\n[\n");
		result.append(' ');
		result.append(m00);
		result.append(' ');
		result.append(m10);
		result.append(' ');
		result.append(m20);
		result.append(' ');
		result.append(m30);
		result.append(" \n");

		result.append(' ');
		result.append(m01);
		result.append(' ');
		result.append(m11);
		result.append(' ');
		result.append(m21);
		result.append(' ');
		result.append(m31);
		result.append(" \n");

		result.append(' ');
		result.append(m02);
		result.append(' ');
		result.append(m12);
		result.append(' ');
		result.append(m22);
		result.append(' ');
		result.append(m32);
		result.append(" \n");

		result.append(' ');
		result.append(m03);
		result.append(' ');
		result.append(m13);
		result.append(' ');
		result.append(m23);
		result.append(' ');
		result.append(m33);
		result.append(" \n");

		result.append(']');
		return result.toString();
	}

	/**
	 * @return returns a unique code for this matrix object based on its values.
	 *         If two matrices are numerically equal, they will return the same
	 *         hash code value.
	 */
	@Override
	public int hashCode() {
		int result = 17;

		long val = Double.doubleToLongBits(m00);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m01);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m02);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m03);
		result += 31 * result + (int) (val ^ val >>> 32);

		val = Double.doubleToLongBits(m10);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m11);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m12);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m13);
		result += 31 * result + (int) (val ^ val >>> 32);

		val = Double.doubleToLongBits(m20);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m21);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m22);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m23);
		result += 31 * result + (int) (val ^ val >>> 32);

		val = Double.doubleToLongBits(m30);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m31);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m32);
		result += 31 * result + (int) (val ^ val >>> 32);
		val = Double.doubleToLongBits(m33);
		result += 31 * result + (int) (val ^ val >>> 32);

		return result;
	}

	/**
	 * @param theObject
	 *            the object to compare for equality
	 * @return true if this matrix and the provided matrix have the double values
	 *         that are within the CCMath.ZERO_TOLERANCE.
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}
		if (!(theObject instanceof CCMatrix4x4)) {
			return false;
		}
		final CCMatrix4x4 comp = (CCMatrix4x4) theObject;
		if (CCMath.abs(m00 - comp.m00) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m01 - comp.m01) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m02 - comp.m02) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m03 - comp.m03) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m10 - comp.m10) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m11 - comp.m11) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m12 - comp.m12) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m13 - comp.m13) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m20 - comp.m20) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m21 - comp.m21) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m22 - comp.m22) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m23 - comp.m23) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m30 - comp.m30) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m31 - comp.m31) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m32 - comp.m32) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		} else if (CCMath.abs(m33 - comp.m33) > CCMath.ALLOWED_DEVIANCE) {
			return false;
		}

		return true;
	}

	/**
	 * @param theObject
	 *            the object to compare for equality
	 * @return true if this matrix and the provided matrix have the exact same
	 *         double values.
	 */
	public boolean strictEquals(final Object theObject) {
		if (this == theObject) {
			return true;
		}
		if (!(theObject instanceof CCMatrix4x4)) {
			return false;
		}
		final CCMatrix4x4 comp = (CCMatrix4x4) theObject;
		if (m00 != comp.m00) {
			return false;
		} else if (m01 != comp.m01) {
			return false;
		} else if (m02 != comp.m02) {
			return false;
		} else if (m03 != comp.m03) {
			return false;
		} else if (m10 != comp.m10) {
			return false;
		} else if (m11 != comp.m11) {
			return false;
		} else if (m12 != comp.m12) {
			return false;
		} else if (m13 != comp.m13) {
			return false;
		} else if (m20 != comp.m20) {
			return false;
		} else if (m21 != comp.m21) {
			return false;
		} else if (m22 != comp.m22) {
			return false;
		} else if (m23 != comp.m23) {
			return false;
		} else if (m30 != comp.m30) {
			return false;
		} else if (m31 != comp.m31) {
			return false;
		} else if (m32 != comp.m32) {
			return false;
		} else if (m33 != comp.m33) {
			return false;
		}

		return true;
	}

	// /////////////////
	// Method for Cloneable
	// /////////////////

	@Override
	public CCMatrix4x4 clone() {
		return new CCMatrix4x4(this);
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
		m00 = theInput.readDouble();
		m01 = theInput.readDouble();
		m02 = theInput.readDouble();
		m03 = theInput.readDouble();
		m10 = theInput.readDouble();
		m11 = theInput.readDouble();
		m12 = theInput.readDouble();
		m13 = theInput.readDouble();
		m20 = theInput.readDouble();
		m21 = theInput.readDouble();
		m22 = theInput.readDouble();
		m23 = theInput.readDouble();
		m30 = theInput.readDouble();
		m31 = theInput.readDouble();
		m32 = theInput.readDouble();
		m33 = theInput.readDouble();
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
		theOutput.writeDouble(m00);
		theOutput.writeDouble(m01);
		theOutput.writeDouble(m02);
		theOutput.writeDouble(m03);
		theOutput.writeDouble(m10);
		theOutput.writeDouble(m11);
		theOutput.writeDouble(m12);
		theOutput.writeDouble(m13);
		theOutput.writeDouble(m20);
		theOutput.writeDouble(m21);
		theOutput.writeDouble(m22);
		theOutput.writeDouble(m23);
		theOutput.writeDouble(m30);
		theOutput.writeDouble(m31);
		theOutput.writeDouble(m32);
		theOutput.writeDouble(m33);
	}
}
