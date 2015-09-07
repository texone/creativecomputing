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
import java.nio.DoubleBuffer;

/**
 * Transform models a transformation in 3d space as: Y = M*X+T, with M being a
 * Matrix3 and T is a Vector3. Generally M will be a rotation only matrix in
 * which case it is represented by the matrix and scale fields as R*S, where S
 * is a positive scale vector. For non-uniform scales and reflections, use
 * setMatrix, which will consider M as being a general 3x3 matrix and disregard
 * anything set in scale.
 */
public class CCTransform implements Cloneable, Externalizable {
	/**
	 * Used with equals method to determine if two Transforms are close enough
	 * to be considered equal.
	 */
	public static final double ALLOWED_DEVIANCE = 0.00000001f;

	private static final long serialVersionUID = 1L;

	/**
	 * Identity transform.
	 */
	public static final CCTransform IDENTITY = new CCTransform(CCMatrix3x3.IDENTITY, CCVector3.ONE, CCVector3.ZERO, true, true, true);

	protected final CCMatrix3x3 _myMatrix = new CCMatrix3x3(CCMatrix3x3.IDENTITY);
	protected final CCVector3 _myTranslation = new CCVector3(CCVector3.ZERO);
	protected final CCVector3 _myScale = new CCVector3(CCVector3.ONE);

	/**
	 * true if this transform is guaranteed to be the identity matrix.
	 */
	protected boolean _myIsIdentity;

	/**
	 * true if the matrix portion of this transform is only rotation.
	 */
	protected boolean _myIsRotationMatrix;

	/**
	 * true if scale is used and scale is guaranteed to be uniform.
	 */
	protected boolean _myIsUniformScale;

	/**
	 * Constructs a new Transform object.
	 */
	public CCTransform() {
		_myIsIdentity = true;
		_myIsRotationMatrix = true;
		_myIsUniformScale = true;
	}

	/**
	 * Constructs a new Transform object from the information stored in the
	 * given source Transform.
	 * 
	 * @param theSource
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCTransform(final CCTransform theSource) {
		_myMatrix.set(theSource.getMatrix());
		_myScale.set(theSource.scale());
		_myTranslation.set(theSource.translation());

		_myIsIdentity = theSource.isIdentity();
		_myIsRotationMatrix = theSource.isRotationMatrix();
		_myIsUniformScale = theSource.isUniformScale();

	}

	/**
	 * Internal only constructor, generally used for making an immutable
	 * transform.
	 * 
	 * @param theMatrix
	 * @param theScale
	 * @param theTranslation
	 * @param theIsIdentity
	 * @param theIsRotationMatrix
	 * @param theIsUniformScale
	 * @throws NullPointerException
	 *             if a param is null.
	 */
	protected CCTransform(
		final CCMatrix3x3 theMatrix, 
		final CCVector3 theScale, 
		final CCVector3 theTranslation, 
		final boolean theIsIdentity,
		final boolean theIsRotationMatrix, 
		final boolean theIsUniformScale
	) {
		_myMatrix.set(theMatrix);
		_myScale.set(theScale);
		_myTranslation.set(theTranslation);

		_myIsIdentity = theIsIdentity;
		_myIsRotationMatrix = theIsRotationMatrix;
		_myIsUniformScale = theIsUniformScale;
	}

	public CCMatrix3x3 getMatrix() {
		return _myMatrix;
	}
	
	public CCMatrix3x3 rotation(){
		return _myMatrix;
	}

	/**
	 * @return true if this transform is guaranteed to be the identity matrix.
	 */
	public boolean isIdentity() {
		return _myIsIdentity;
	}

	/**
	 * @return true if the matrix portion of this transform is only rotation.
	 */
	public boolean isRotationMatrix() {
		return _myIsRotationMatrix;
	}

	/**
	 * @return true if scale is used and scale is guaranteed to be uniform.
	 */
	public boolean isUniformScale() {
		return _myIsUniformScale;
	}

	/**
	 * Resets this transform to identity and resets all flags.
	 * 
	 * @return this Transform for chaining.
	 */
	public CCTransform setIdentity() {
		_myMatrix.set(CCMatrix3x3.IDENTITY);
		_myScale.set(CCVector3.ONE);
		_myTranslation.set(CCVector3.ZERO);
		_myIsIdentity = true;
		_myIsRotationMatrix = true;
		_myIsUniformScale = true;
		return this;
	}

	/**
	 * Sets the matrix portion of this transform to the given value.
	 * 
	 * NB: Calling this with a matrix that is not purely rotational
	 * (orthonormal) will result in a Transform whose scale comes from its
	 * matrix. Further attempts to set scale directly will throw an error.
	 * 
	 * @param theRotation
	 *            our new matrix.
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if rotation is null.
	 * @see CCMatrix3x3#isOrthonormal()
	 */
	public CCTransform rotation(final CCMatrix3x3 theRotation) {
		_myMatrix.set(theRotation);
		updateFlags(false);
		return this;
	}

	/**
	 * Sets the matrix portion of this transform to the rotational value of the
	 * given Quaternion. Calling this allows scale to be set and used.
	 * 
	 * @param theRotation
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if rotation is null.
	 */
	public CCTransform rotation(final CCQuaternion theRotation) {
		_myMatrix.set(theRotation);
		updateFlags(true);
		return this;
	}
	
	public void rotation(final double theAngle, final double theX, final double theY, final double theZ){
		rotation(new CCQuaternion().applyRotation(theAngle, theX, theY, theZ));
	}
	
	public void rotation(final double theAngle, final CCVector3 theAxis){
		rotation(new CCQuaternion().applyRotation(theAngle, theAxis.x, theAxis.y, theAxis.z));
	}

	public CCVector3 translation() {
		return _myTranslation;
	}

	/**
	 * Sets the translation portion of this transform to the given values.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this transform for chaining.
	 */
	public CCTransform translation(final double x, final double y, final double z) {
		_myTranslation.set(x, y, z);
		_myIsIdentity = false;
		return this;
	}

	/**
	 * Sets the translation portion of this transform to the given value.
	 * 
	 * @param theTranslation
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if translation is null.
	 */
	public CCTransform translation(final CCVector3 theTranslation) {
		return translation(theTranslation.x, theTranslation.y, theTranslation.z);
	}

	public CCVector3 scale() {
		return _myScale;
	}

	/**
	 * Sets the scale portion of this transform to the given values.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if scale is null.
	 * @throws CCTransformException
	 *             if this transform has a generic 3x3 matrix set.
	 * @throws IllegalArgumentException
	 *             if scale is (0,0,0)
	 */
	public CCTransform scale(final double x, final double y, final double z) {
		if (!_myIsRotationMatrix) {
			throw new CCTransformException("Scale is already provided by 3x3 matrix.  If this is a mistake, consider using setRotation instead of setMatrix.");
		}
		if (x == 0.0 && y == 0.0 && z == 0.0) {
			throw new IllegalArgumentException("scale may not be ZERO.");
		}

		_myScale.set(x, y, z);
		_myIsIdentity =  _myIsIdentity && x == 1.0 && y == 1.0 && z == 1.0;
		_myIsUniformScale = x == y && y == z;
		return this;
	}

	/**
	 * Sets the scale portion of this transform to the given value as a vector
	 * (u, u, u)
	 * 
	 * @param theScale
	 * @return this transform for chaining.
	 * @throws CCTransformException
	 *             if this transform has a generic 3x3 matrix set.
	 * @throws IllegalArgumentException
	 *             if uniformScale is 0
	 */
	public CCTransform scale(final double theScale) {
		return scale(theScale, theScale, theScale);
	}

	/**
	 * Sets the scale portion of this transform to the given value.
	 * 
	 * @param theScale
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if scale is null.
	 * @throws CCTransformException
	 *             if this transform has a generic 3x3 matrix set.
	 * @throws IllegalArgumentException
	 *             if scale is (0,0,0)
	 */
	public CCTransform scale(final CCVector3 theScale) {
		return scale(theScale.x, theScale.y, theScale.z);
	}

	/**
	 * Copies the given transform values into this transform object.
	 * 
	 * @param source
	 * @return this transform for chaining.
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCTransform set(final CCTransform source) {
		if (source.isIdentity()) {
			setIdentity();
		} else {
			_myMatrix.set(source.getMatrix());
			_myScale.set(source.scale());
			_myTranslation.set(source.translation());

			_myIsIdentity = false;
			_myIsRotationMatrix = source.isRotationMatrix();
			_myIsUniformScale = source.isUniformScale();
		}
		return this;
	}

	/**
	 * Locally adds to the translation of this transform.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return this transform for chaining.
	 */
	public CCTransform translate(final double x, final double y, final double z) {
		_myTranslation.addLocal(x, y, z);
		_myIsIdentity = _myIsIdentity && _myTranslation.equals(CCVector3.ZERO);
		return this;
	}

	/**
	 * Locally adds to the translation of this transform.
	 * 
	 * @param vec
	 * @return this transform for chaining.
	 */
	public CCTransform translate(final CCVector3 vec) {
		_myTranslation.addLocal(vec);
		_myIsIdentity = _myIsIdentity && _myTranslation.equals(CCVector3.ZERO);
		return this;
	}

	/**
	 * Locally applies this transform to the given point: P' = M*P+T
	 * 
	 * @param point
	 * @return the transformed point.
	 * @throws NullPointerException
	 *             if point is null.
	 */
	public CCVector3 applyForward(CCVector3 point, CCVector3 theStore) {
		if (point == null) {
			throw new NullPointerException();
		}
		if(theStore == null)theStore = new CCVector3();
		

		if (_myIsIdentity) {
			// No need to make changes
			// Y = X
			theStore.set(point);
			return theStore;
		}

		if (_myIsRotationMatrix) {
			// Scale is separate from matrix
			// Y = R*S*X + T
			theStore.set(point.x * _myScale.x, point.y * _myScale.y, point.z * _myScale.z);
			_myMatrix.applyPost(theStore, theStore);
			theStore.addLocal(_myTranslation);
			return theStore;
		}

		// scale is part of matrix.
		// Y = M*X + T
		_myMatrix.applyPost(point, theStore);
		theStore.addLocal(_myTranslation);
		return theStore;

	}
	
	public CCVector3 applyForward(CCVector3 point){
		return applyForward(point,null);
	}

	/**
	 * Locally applies the inverse of this transform to the given point: P' =
	 * M^{-1}*(P-T)
	 * 
	 * @param thePoint
	 * @return the transformed point.
	 * @throws NullPointerException
	 *             if point is null.
	 */
	public CCVector3 applyInverse(final CCVector3 thePoint, CCVector3 theStore) {
		if (thePoint == null) {
			throw new NullPointerException();
		}
		if(theStore == null)theStore = new CCVector3(thePoint);

		if (_myIsIdentity) {
			// No need to make changes
			// P' = P
			return theStore;
		}

		// Back track translation
		theStore.subtractLocal(_myTranslation);

		if (_myIsRotationMatrix) {
			// Scale is separate from matrix so...
			// P' = S^{-1}*R^t*(P - T)
			theStore = _myMatrix.applyPre(theStore);
			if (_myIsUniformScale) {
				theStore.divideLocal(_myScale.x);
			} else {
				theStore.x = theStore.x / _myScale.x;
				theStore.y = theStore.y / _myScale.y;
				theStore.z = theStore.z / _myScale.z;
			}
		} else {
			// P' = M^{-1}*(P - T)
			final CCMatrix3x3 invertedMatrix = _myMatrix.invert();
			theStore = invertedMatrix.applyPost(theStore);
		}

		return theStore;
	}
	
	public CCVector3 applyInverse(final CCVector3 thePoint){
		return applyInverse(thePoint, null);
	}

	/**
	 * Locally applies this transform to the given vector: V' = M*V
	 * 
	 * @param vector
	 * @return the transformed vector.
	 * @throws NullPointerException
	 *             if vector is null.
	 */
	public CCVector3 applyForwardVector(CCVector3 theVector, CCVector3 theStore) {
		if (theVector == null) {
			throw new NullPointerException();
		}
		if(theStore == null){
			theStore = new CCVector3();
		}

		if (_myIsIdentity) {
			// No need to make changes
			// V' = V
			theStore.set(theVector);
			return theStore;
		}

		if (_myIsRotationMatrix) {
			// Scale is separate from matrix
			// V' = R*S*V
			theStore.set(theVector.x * _myScale.x, theVector.y * _myScale.y, theVector.z * _myScale.z);
			theStore = _myMatrix.applyPost(theStore);
			return theStore;
		}

		// scale is part of matrix.
		// V' = M*V
		theStore = _myMatrix.applyPost(theVector);
		return theStore;

	}
	
	public CCVector3 applyForwardVector(CCVector3 theVector){
		return applyForwardVector(theVector, null);
	}

	/**
	 * Locally applies the inverse of this transform to the given vector: V' =
	 * M^{-1}*V
	 * 
	 * @param theVector
	 * @return the transformed theVector.
	 * @throws NullPointerException
	 *             if vector is null.
	 */
	public CCVector3 applyInverseVector(CCVector3 theVector) {
		if (theVector == null) {
			throw new NullPointerException();
		}

		theVector = theVector.clone();

		if (_myIsIdentity) {
			// No need to make changes
			// V' = V
			return theVector;
		}

		if (_myIsRotationMatrix) {
			// Scale is separate from matrix so...
			// V' = S^{-1}*R^t*V
			theVector = _myMatrix.applyPre(theVector);
			if (_myIsUniformScale) {
				theVector.divideLocal(_myScale.x);
			} else {
				theVector.x = theVector.x / _myScale.x;
				theVector.y = theVector.y / _myScale.y;
				theVector.z = theVector.z / _myScale.z;
			}
		} else {
			// V' = M^{-1}*V
			final CCMatrix3x3 invertedMatrix = _myMatrix.invert();
			theVector = invertedMatrix.applyPost(theVector);
		}

		return theVector;
	}

	/**
	 * Calculates the product of this transform with the given "transformBy"
	 * transform (P = this * T) and stores this in store.
	 * 
	 * @param transformBy
	 * @return the product
	 * @throws NullPointerException
	 *             if transformBy is null.
	 */
	public CCTransform multiply(final CCTransform transformBy, CCTransform theStore) {
		if(theStore == null)theStore = new CCTransform();

		if (_myIsIdentity) {
			return theStore.set(transformBy);
		}

		if (transformBy.isIdentity()) {
			return theStore.set(this);
		}

		if (_myIsRotationMatrix && transformBy.isRotationMatrix() && _myIsUniformScale) {
			theStore._myIsRotationMatrix = true;
			theStore._myMatrix.set(_myMatrix).multiplyLocal(transformBy.getMatrix());

			theStore._myTranslation.set(transformBy.translation());
			theStore._myTranslation.set(_myMatrix.applyPost(theStore._myTranslation));
			// uniform scale, so just use X.
			theStore._myTranslation.multiplyLocal(_myScale.x);
			theStore._myTranslation.addLocal(_myTranslation);

			if (transformBy.isUniformScale()) {
				theStore.scale(_myScale.x * transformBy.scale().x);
			} else {
				final CCVector3 scale = theStore._myScale.set(transformBy.scale());
				scale.multiplyLocal(_myScale.x);
			}

			// update our flags in one place.
			theStore.updateFlags(true);

			return theStore;
		}

		// In all remaining cases, the matrix cannot be written as R*S*X+T.
		final CCMatrix3x3 matrixA = isRotationMatrix() ? _myMatrix.multiplyDiagonalPost(_myScale) : _myMatrix;

		final CCMatrix3x3 matrixB = transformBy.isRotationMatrix() ? transformBy.getMatrix().multiplyDiagonalPost(transformBy.scale())
				: transformBy.getMatrix();

		final CCMatrix3x3 newMatrix = theStore._myMatrix;
		newMatrix.set(matrixA).multiplyLocal(matrixB);

		theStore._myTranslation.set(matrixA.applyPost(transformBy.translation()).addLocal(translation()));

		// prevent scale bleeding since we don't set it.
		theStore._myScale.set(1.0f, 1.0f, 1.0f);

		// update our flags in one place.
		theStore.updateFlags(false);

		return theStore;
	}
	
	public CCTransform multiply(final CCTransform transformBy){
		return multiply(transformBy, null);
	}

	/**
	 * Updates _rotationMatrix, _uniformScale and _identity based on the current
	 * contents of this Transform.
	 * 
	 * @param rotationMatrixGuaranteed
	 *            true if we know for sure that the _matrix component is
	 *            rotational.
	 */
	protected void updateFlags(final boolean rotationMatrixGuaranteed) {
		_myIsIdentity = _myTranslation.equals(CCVector3.ZERO) && _myMatrix.isIdentity() && _myScale.equals(CCVector3.ONE);
		if (_myIsIdentity) {
			_myIsRotationMatrix = true;
			_myIsUniformScale = true;
		} else {
			_myIsRotationMatrix = rotationMatrixGuaranteed ? true : _myMatrix.isOrthonormal();
			_myIsUniformScale = _myIsRotationMatrix && _myScale.x == _myScale.y && _myScale.y == _myScale.z;
		}
	}

	/**
	 * Calculates the inverse of this transform.
	 * 
	 * @return the inverted transform
	 */
	public CCTransform invert() {
		CCTransform result = new CCTransform();

		if (_myIsIdentity) {
			result.setIdentity();
			return result;
		}

		result._myMatrix.set(_myMatrix);
		if (_myIsRotationMatrix) {
			if (_myIsUniformScale) {
				final double sx = _myScale.x;
				result._myMatrix.transposeLocal();
				if (sx != 1.0) {
					result._myMatrix.multiplyLocal(1.0f / sx);
				}
			} else {
				result._myMatrix.set(result._myMatrix.multiplyDiagonalPost(_myScale).invertLocal());
			}
		} else {
			result._myMatrix.invertLocal();
		}

		result._myTranslation.set(result._myMatrix.applyPost(_myTranslation).negateLocal());
		result.updateFlags(_myIsRotationMatrix);

		return result;
	}

	/**
	 * @param store
	 *            the matrix to store the result in for return. If null, a new
	 *            matrix object is created and returned.
	 * @return this transform represented as a 4x4 matrix:
	 * 
	 *         <pre>
	 * R R R Tx
	 * R R R Ty
	 * R R R Tz
	 * 0 0 0 1
	 * </pre>
	 */
	public CCMatrix4x4 getHomogeneousMatrix(final CCMatrix4x4 store) {
		CCMatrix4x4 result = store;
		if (result == null) {
			result = new CCMatrix4x4();
		}

		if (_myIsRotationMatrix) {
			result.m00 = _myScale.x * _myMatrix._m00;
			result.m01 = _myScale.x * _myMatrix._m10;
			result.m02 = _myScale.x * _myMatrix._m20;
			result.m10 = _myScale.y * _myMatrix._m01;
			result.m11 = _myScale.y * _myMatrix._m11;
			result.m12 = _myScale.y * _myMatrix._m21;
			result.m20 = _myScale.z * _myMatrix._m02;
			result.m21 = _myScale.z * _myMatrix._m12;
			result.m22 = _myScale.z * _myMatrix._m22;
		} else {
			result.m00 = _myMatrix._m00;
			result.m01 = _myMatrix._m10;
			result.m02 = _myMatrix._m20;
			result.m10 = _myMatrix._m01;
			result.m11 = _myMatrix._m11;
			result.m12 = _myMatrix._m21;
			result.m20 = _myMatrix._m02;
			result.m21 = _myMatrix._m12;
			result.m22 = _myMatrix._m22;
		}

		result.m03 = 0.0f;
		result.m13 = 0.0f;
		result.m23 = 0.0f;

		result.m30 = _myTranslation.x;
		result.m31 = _myTranslation.y;
		result.m32 = _myTranslation.z;
		result.m33 = 1.0f;

		return result;
	}

	public void getGLApplyMatrix(final DoubleBuffer store) {
		if (_myIsRotationMatrix) {
			store.put(0, _myScale.x * _myMatrix._m00);
			store.put(1, _myScale.x * _myMatrix._m10);
			store.put(2, _myScale.x * _myMatrix._m20);
			store.put(4, _myScale.y * _myMatrix._m01);
			store.put(5, _myScale.y * _myMatrix._m11);
			store.put(6, _myScale.y * _myMatrix._m21);
			store.put(8, _myScale.z * _myMatrix._m02);
			store.put(9, _myScale.z * _myMatrix._m12);
			store.put(10, _myScale.z * _myMatrix._m22);
		} else {
			store.put(0, _myMatrix._m00);
			store.put(1, _myMatrix._m10);
			store.put(2, _myMatrix._m20);
			store.put(4, _myMatrix._m01);
			store.put(5, _myMatrix._m11);
			store.put(6, _myMatrix._m21);
			store.put(8, _myMatrix._m02);
			store.put(9, _myMatrix._m12);
			store.put(10, _myMatrix._m22);
		}

		store.put(12, _myTranslation.x);
		store.put(13, _myTranslation.y);
		store.put(14, _myTranslation.z);
		store.put(15, 1.0f);
	}

	/**
	 * Reads in a 4x4 matrix as a 3x3 matrix and translation.
	 * 
	 * @param matrix
	 * @return this matrix for chaining.
	 * @throws NullPointerException
	 *             if matrix is null.
	 */
	public CCTransform fromHomogeneousMatrix(final CCMatrix4x4 matrix) {
		_myMatrix.set(
			matrix.m00, matrix.m10, matrix.m20, 
			matrix.m01, matrix.m11, matrix.m21, 
			matrix.m02, matrix.m12, matrix.m22
		);
		_myTranslation.set(matrix.m03, matrix.m13, matrix.m23);

		updateFlags(false);
		return this;
	}

	/**
	 * Check a transform... if it is null or one of its members are invalid,
	 * return false. Else return true.
	 * 
	 * @param transform
	 *            the transform to check
	 * 
	 * @return true or false as stated above.
	 */
	public static boolean isValid(final CCTransform transform) {
		if (transform == null) {
			return false;
		}
		if (!CCVector3.isValid(transform.scale()) || !CCVector3.isValid(transform.translation())
				|| !CCMatrix3x3.isValid(transform.getMatrix())) {
			return false;
		}

		return true;
	}

	/**
	 * @return the string representation of this triangle.
	 */
	@Override
	public String toString() {
		return "com.ardor3d.math.Transform [\n M: " + _myMatrix + "\n S: " + _myScale + "\n T: " + _myTranslation + "\n]";
	}

	/**
	 * @return returns a unique code for this transform object based on its
	 *         values.
	 */
	@Override
	public int hashCode() {
		int result = 17;

		result += 31 * result + _myMatrix.hashCode();
		result += 31 * result + _myScale.hashCode();
		result += 31 * result + _myTranslation.hashCode();

		return result;
	}

	/**
	 * @param o
	 *            the object to compare for equality
	 * @return true if this transform and the provided transform have the same
	 *         values.
	 */
	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CCTransform)) {
			return false;
		}
		final CCTransform comp = (CCTransform) o;
		return _myMatrix.equals(comp.getMatrix()) && Math.abs(_myTranslation.x - comp.translation().x) < CCTransform.ALLOWED_DEVIANCE
				&& Math.abs(_myTranslation.y - comp.translation().y) < CCTransform.ALLOWED_DEVIANCE
				&& Math.abs(_myTranslation.z - comp.translation().z) < CCTransform.ALLOWED_DEVIANCE
				&& Math.abs(_myScale.x - comp.scale().x) < CCTransform.ALLOWED_DEVIANCE
				&& Math.abs(_myScale.y - comp.scale().y) < CCTransform.ALLOWED_DEVIANCE
				&& Math.abs(_myScale.z - comp.scale().z) < CCTransform.ALLOWED_DEVIANCE;
	}

	/**
	 * @param o
	 *            the object to compare for equality
	 * @return true if this transform and the provided transform have the exact
	 *         same double values.
	 */
	public boolean strictEquals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof CCTransform)) {
			return false;
		}
		final CCTransform comp = (CCTransform) o;
		return _myMatrix.strictEquals(comp.getMatrix()) && _myScale.equals(comp.scale()) && _myTranslation.equals(comp.translation());
	}

	// /////////////////
	// Method for Cloneable
	// /////////////////

	@Override
	public CCTransform clone() {
		return new CCTransform(this);
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
		_myMatrix.set((CCMatrix3x3) in.readObject());
		_myScale.set((CCVector3) in.readObject());
		_myTranslation.set((CCVector3) in.readObject());
		_myIsIdentity = in.readBoolean();
		_myIsRotationMatrix = in.readBoolean();
		_myIsUniformScale = in.readBoolean();
	}

	/*
	 * Used with serialization. Not to be called manually.
	 * 
	 * @param out ObjectOutput
	 * 
	 * @throws IOException
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeObject(_myMatrix);
		out.writeObject(_myScale);
		out.writeObject(_myTranslation);
		out.writeBoolean(_myIsIdentity);
		out.writeBoolean(_myIsRotationMatrix);
		out.writeBoolean(_myIsUniformScale);
	}
}
