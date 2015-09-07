/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.scene.shape;

import cc.creativecomputing.data.CCBufferUtils;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * Sphere represents a 3D object with all points equi-distance from a center
 * point.
 */
public class CCSphere extends CCPrimitive {

	public enum CCSphereTextureMode {
		LINEAR, PROJECTED, POLAR;
	}

	private int _myZSamples;

	private int _myRadialSamples;

	/** the distance from the center point each point falls on */
	private double _myRadius;
	/** the center of the sphere */
	private final CCVector3 _myCenter = new CCVector3();

	protected CCSphereTextureMode _myTextureMode = CCSphereTextureMode.LINEAR;

	protected boolean _myViewInside = false;

	/**
	 * Constructs a sphere. By default the Sphere has not geometry data or
	 * center.
	 */
	public CCSphere() {
	}

	/**
	 * Constructs a sphere with center at the origin. For details, see the other
	 * constructor.
	 * 
	 * @param theZSamples
	 *            The samples along the Z.
	 * @param theRadialSamples
	 *            The samples along the radial.
	 * @param radius
	 *            Radius of the sphere.
	 * @see #Sphere(java.lang.String, com.ardor3d.math.CCVector3, int, int,
	 *      double)
	 */
	public CCSphere(final int theZSamples, final int theRadialSamples, final double theRadius) {
		this(new CCVector3(0, 0, 0), theZSamples, theRadialSamples, theRadius);
	}

	/**
	 * Constructs a sphere. All geometry data buffers are updated automatically.
	 * Both zSamples and theRadialSamples increase the quality of the generated
	 * sphere.
	 * 
	 * @param theCenter
	 *            Center of the sphere.
	 * @param theZSamples
	 *            The number of samples along the Z.
	 * @param theRadialSamples
	 *            The number of samples along the radial.
	 * @param theRadius
	 *            The radius of the sphere.
	 */
	public CCSphere(final CCVector3 theCenter, final int theZSamples, final int theRadialSamples, final double theRadius) {
		setData(theCenter, theZSamples, theRadialSamples, theRadius);
	}

	/**
	 * Constructs a sphere. All geometry data buffers are updated automatically.
	 * Both zSamples and theRadialSamples increase the quality of the generated
	 * sphere.
	 * 
	 * @param theCenter
	 *            Center of the sphere.
	 * @param theZSamples
	 *            The number of samples along the Z.
	 * @param theRadialSamples
	 *            The number of samples along the radial.
	 * @param theRadius
	 *            The theRadius of the sphere.
	 * @param theTextureMode
	 *            the mode to use when setting uv coordinates for this Sphere.
	 */
	public CCSphere(final CCVector3 theCenter, final int theZSamples, final int theRadialSamples, final double theRadius, final CCSphereTextureMode theTextureMode) {
		_myTextureMode = theTextureMode;
		setData(theCenter, theZSamples, theRadialSamples, theRadius);
	}

	/**
	 * Changes the information of the sphere into the given values.
	 * 
	 * @param theCenter
	 *            The new center of the sphere.
	 * @param theZSamples
	 *            The new number of zSamples of the sphere.
	 * @param theRadialSamples
	 *            The new number of radial samples of the sphere.
	 * @param theRadius
	 *            The new radius of the sphere.
	 */
	public void setData(final CCVector3 theCenter, final int theZSamples, final int theRadialSamples, final double theRadius) {
		_myCenter.set(theCenter);
		_myZSamples = theZSamples;
		_myRadialSamples = theRadialSamples;
		_myRadius = theRadius;

		final int myNumberOfVertices = (_myZSamples - 2) * (_myRadialSamples + 1) + 2;
		final int myNumberOfIndices = 2 * (_myZSamples - 2) * _myRadialSamples * 3;
		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}

	/**
	 * builds the vertices based on the radius, center and radial and zSamples.
	 */
	@Override
	protected void setGeometryData() {
		// allocate vertices

		// generate geometry
		final double fInvRS = 1.0f / _myRadialSamples;
		final double fZFactor = 2.0f / (_myZSamples - 1);

		// Generate points on the unit circle to be used in computing the mesh
		// points on a sphere slice.
		final double[] afSin = new double[_myRadialSamples + 1];
		final double[] afCos = new double[_myRadialSamples + 1];
		for (int iR = 0; iR < _myRadialSamples; iR++) {
			final double fAngle = CCMath.TWO_PI * fInvRS * iR;
			afCos[iR] = CCMath.cos(fAngle);
			afSin[iR] = CCMath.sin(fAngle);
		}
		afSin[_myRadialSamples] = afSin[0];
		afCos[_myRadialSamples] = afCos[0];

		// generate the sphere itself
		int i = 0;
		final CCVector3 tempVa = new CCVector3();
		final CCVector3 tempVb = new CCVector3();
		final CCVector3 tempVc = new CCVector3();
		for (int iZ = 1; iZ < (_myZSamples - 1); iZ++) {
			final double fAFraction = CCMath.HALF_PI * (-1.0f + fZFactor * iZ); // in
																				// (-pi/2,
																				// pi/2)
			final double fZFraction = CCMath.sin(fAFraction); // in (-1,1)
			final double fZ = _myRadius * fZFraction;

			// compute center of slice
			final CCVector3 kSliceCenter = tempVb.set(_myCenter);
			kSliceCenter.z = kSliceCenter.z + fZ;

			// compute radius of slice
			final double fSliceRadius = CCMath.sqrt(CCMath.abs(_myRadius * _myRadius - fZ * fZ));

			// compute slice vertices with duplication at end point
			CCVector3 kNormal;
			final int iSave = i;
			for (int iR = 0; iR < _myRadialSamples; iR++) {
				final double fRadialFraction = iR * fInvRS; // in [0,1)
				final CCVector3 kRadial = tempVc.set(afCos[iR], afSin[iR], 0);
				kRadial.multiply(fSliceRadius, tempVa);
				_myGeometryData.vertices().add(
					kSliceCenter.x + tempVa.x,
					kSliceCenter.y + tempVa.y,
					kSliceCenter.z + tempVa.z
				);

				CCBufferUtils.populateFromBuffer(tempVa, _myGeometryData.vertexBuffer(), i);
				kNormal = tempVa.subtractLocal(_myCenter);
				kNormal.normalizeLocal();
				if (!_myViewInside) {
					_myGeometryData.normals().add(kNormal.x, kNormal.y, kNormal.z);
				} else {
					_myGeometryData.normals().add(-kNormal.x, -kNormal.y, -kNormal.z);
				}

				if (_myTextureMode == CCSphereTextureMode.LINEAR) {
					_myGeometryData.textureCoords(0).add(fRadialFraction,0.5f * (fZFraction + 1.0f));
				} else if (_myTextureMode == CCSphereTextureMode.PROJECTED) {
					_myGeometryData.textureCoords(0).add(fRadialFraction,(CCMath.HALF_PI + CCMath.asin(fZFraction)) / CCMath.PI);
				} else if (_myTextureMode == CCSphereTextureMode.POLAR) {
					final double r = (CCMath.HALF_PI - CCMath.abs(fAFraction)) / CCMath.PI;
					final double u = r * afCos[iR] + 0.5f;
					final double v = r * afSin[iR] + 0.5f;
					_myGeometryData.textureCoords(0).add(u,v);
				}

				i++;
			}

			CCBufferUtils.copyInternalVector3(_myGeometryData.vertexBuffer(), iSave, i);
			CCBufferUtils.copyInternalVector3(_myGeometryData.normalBuffer(), iSave, i);

			if (_myTextureMode == CCSphereTextureMode.LINEAR) {
				_myGeometryData.textureCoords(0).add(1.0f,0.5f * (fZFraction + 1.0f));
			} else if (_myTextureMode == CCSphereTextureMode.PROJECTED) {
				_myGeometryData.textureCoords(0).add(1.0f,(CCMath.HALF_PI + CCMath.asin(fZFraction)) / CCMath.PI);
			} else if (_myTextureMode == CCSphereTextureMode.POLAR) {
				final double r = ((CCMath.HALF_PI - CCMath.abs(fAFraction)) / CCMath.PI);
				_myGeometryData.textureCoords(0).add(r + 0.5f,0.5f);
			}

			i++;
		}

		// south pole
		_myGeometryData.vertices().position(i * 3);
		_myGeometryData.vertices().add(_myCenter.x,_myCenter.y,_myCenter.z - _myRadius);

		_myGeometryData.normals().position(i * 3);
		if (!_myViewInside) {
			// TODO: allow for inner texture orientation later.
			_myGeometryData.normals().add(0f, 0f, -1f);
		} else {
			_myGeometryData.normals().add(0, 0, 1);
		}

		_myGeometryData.textureCoords(0).buffer().position(i * 2);
		if (_myTextureMode == CCSphereTextureMode.POLAR) {
			_myGeometryData.textureCoords(0).add(0.5f,0.5f);
		} else {
			_myGeometryData.textureCoords(0).add(0.5f,0.0f);
		}

		i++;

		// north pole
		_myGeometryData.vertices().add(_myCenter.x,_myCenter.y,_myCenter.z + _myRadius);

		if (!_myViewInside) {
			_myGeometryData.normals().add(0, 0, 1);
		} else {
			_myGeometryData.normals().add(0f, 0f, -1f);
		}

		if (_myTextureMode == CCSphereTextureMode.POLAR) {
			_myGeometryData.textureCoords(0).add(0.5f,0.5f);
		} else {
			_myGeometryData.textureCoords(0).add(0.5f,1.0f);
		}
	}

	/**
	 * sets the indices for rendering the sphere.
	 */
	@Override
	protected void setIndexData() {

		// generate connectivity
		for (int iZ = 0, iZStart = 0; iZ < (_myZSamples - 3); iZ++) {
			int i0 = iZStart;
			int i1 = i0 + 1;
			iZStart += (_myRadialSamples + 1);
			int i2 = iZStart;
			int i3 = i2 + 1;
			for (int i = 0; i < _myRadialSamples; i++) {
				if (!_myViewInside) {
					_myGeometryData.indices().add(i0++);
					_myGeometryData.indices().add(i1);
					_myGeometryData.indices().add(i2);
					_myGeometryData.indices().add(i1++);
					_myGeometryData.indices().add(i3++);
					_myGeometryData.indices().add(i2++);
				} else // inside view
				{
					_myGeometryData.indices().add(i0++);
					_myGeometryData.indices().add(i2);
					_myGeometryData.indices().add(i1);
					_myGeometryData.indices().add(i1++);
					_myGeometryData.indices().add(i2++);
					_myGeometryData.indices().add(i3++);
				}
			}
		}

		// south pole triangles
		for (int i = 0; i < _myRadialSamples; i++) {
			if (!_myViewInside) {
				_myGeometryData.indices().add(i);
				_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 2);
				_myGeometryData.indices().add(i + 1);
			} else // inside view
			{
				_myGeometryData.indices().add(i);
				_myGeometryData.indices().add(i + 1);
				_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 2);
			}
		}

		// north pole triangles
		final int iOffset = (_myZSamples - 3) * (_myRadialSamples + 1);
		for (int i = 0; i < _myRadialSamples; i++) {
			if (!_myViewInside) {
				_myGeometryData.indices().add(i + iOffset);
				_myGeometryData.indices().add(i + 1 + iOffset);
				_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 1);
			} else // inside view
			{
				_myGeometryData.indices().add(i + iOffset);
				_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 1);
				_myGeometryData.indices().add(i + 1 + iOffset);
			}
		}
	}

	/**
	 * Returns the center of this sphere.
	 * 
	 * @return The sphere's center.
	 */
	public CCVector3 cnter() {
		return _myCenter;
	}

	/**
	 * 
	 * @return true if the normals are inverted to point into the sphere so that
	 *         the face is oriented for a viewer inside the sphere. false (the
	 *         default) for exterior viewing.
	 */
	public boolean isViewFromInside() {
		return _myViewInside;
	}

	/**
	 * 
	 * @param viewInside
	 *            if true, the normals are inverted to point into the sphere so
	 *            that the face is oriented for a viewer inside the sphere.
	 *            Default is false (for outside viewing)
	 */
	public void setViewFromInside(final boolean viewInside) {
		if (viewInside != _myViewInside) {
			_myViewInside = viewInside;
			setGeometryData();
			setIndexData();
		}
	}

	/**
	 * @return Returns the textureMode.
	 */
	public CCSphereTextureMode textureMode() {
		return _myTextureMode;
	}

	/**
	 * @param textureMode
	 *            The textureMode to set.
	 */
	public void textureMode(final CCSphereTextureMode textureMode) {
		_myTextureMode = textureMode;
		setGeometryData();
		setIndexData();
	}

	public double radius() {
		return _myRadius;
	}
}