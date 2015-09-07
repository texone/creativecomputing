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
 * A half sphere.
 */
public class CCDome extends CCPrimitive {

	private int _myPlanes;
	private int _myRadialSamples;
	
	private CCVector3 _myCenter;
	
	private boolean _myIsOutSideView;

	/** The radius of the dome */
	private double _myRadius;
	


	/**
	 * Changes the information of the dome into the given values. The boolean at
	 * the end signals if buffer data should be updated as well. If the dome is
	 * to be rendered, then that value should be true.
	 * @param theRadialSamples
	 *            The new number of radial samples of the dome.
	 * @param theRadius
	 *            The new theRadius of the dome.
	 * @param theIsOutsideView
	 *            If true, the triangles will be connected for a view outside of
	 *            the dome.
	 * @param center
	 *            The new center of the dome.
	 * @param planes
	 *            The number of planes along the Z-axis.
	 */
	public CCDome(final CCVector3 theCenter, final int thePlanes, final int theRadialSamples, final double theRadius, final boolean theIsOutsideView) {
		_myCenter = theCenter;
		_myPlanes = thePlanes;
		_myRadialSamples = theRadialSamples;
		_myRadius = theRadius;
		_myIsOutSideView =theIsOutsideView;


		final int myNumberOfVertices = ((_myPlanes - 1) * (_myRadialSamples + 1)) + 1;
		final int myNumberOfIndices = ((_myPlanes - 2) * _myRadialSamples * 2 + _myRadialSamples) * 3;
		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}

	/**
	 * Constructs a dome. All geometry data buffers are updated automatically.
	 * Both planes and radialSamples increase the quality of the generated dome.
	 * 
	 * @param theCenter
	 *            Center of the dome.
	 * @param thePlanes
	 *            The number of planes along the Z-axis.
	 * @param theRadialSamples
	 *            The number of samples along the radial.
	 * @param theRadius
	 *            The theRadius of the dome.
	 */
	public CCDome(final CCVector3 theCenter, final int thePlanes, final int theRadialSamples, final double theRadius) {
		this(theCenter, thePlanes, theRadialSamples, theRadius, true);
	}

	/**
	 * Constructs a dome with theCenter at the origin. For details, see the other
	 * constructor.
	 * 
	 * @param thePlanes
	 *            The number of thePlanes along the Z-axis.
	 * @param theRadialSamples
	 *            The samples along the radial.
	 * @param theRadius
	 *            Radius of the dome.
	 * @see #Dome(java.lang.String, com.ardor3d.math.CCVector3, int, int, double)
	 */
	public CCDome(final int thePlanes, final int theRadialSamples, final double theRadius) {
		this(new CCVector3(0, 0, 0), thePlanes, theRadialSamples, theRadius);
	}

	/**
	 * Generates the vertices of the dome
	 * 
	 * @param outsideView
	 *            If the dome should be viewed from the outside (if not zbuffer
	 *            is used)
	 * @param theCenter
	 */
	@Override
	protected void setGeometryData() {
		final CCVector3 tempVa = new CCVector3();
		final CCVector3 tempVb = new CCVector3();
		final CCVector3 tempVc = new CCVector3();

		// generate geometry
		final double fInvRS = 1.0f / _myRadialSamples;
		final double fYFactor = 1.0f / (_myPlanes - 1);

		// Generate points on the unit circle to be used in computing the mesh
		// points on a dome slice.
		final double[] afSin = new double[(_myRadialSamples)];
		final double[] afCos = new double[(_myRadialSamples)];
		for (int iR = 0; iR < _myRadialSamples; iR++) {
			final double fAngle = CCMath.TWO_PI * fInvRS * iR;
			afCos[iR] = CCMath.cos(fAngle);
			afSin[iR] = CCMath.sin(fAngle);
		}

		// generate the dome itself
		int i = 0;
		for (int iY = 0; iY < (_myPlanes - 1); iY++) {
			final double fYFraction = fYFactor * iY; // in (0,1)
			final double fY = _myRadius * fYFraction;
			// compute center of slice
			final CCVector3 kSliceCenter = tempVb.set(_myCenter);
			kSliceCenter.addLocal(0, fY, 0);

			// compute radius of slice
			final double fSliceRadius = CCMath.sqrt(CCMath.abs(_myRadius * _myRadius - fY * fY));

			// compute slice vertices
			CCVector3 kNormal;
			final int iSave = i;
			for (int iR = 0; iR < _myRadialSamples; iR++) {
				final double fRadialFraction = iR * fInvRS; // in [0,1)
				final CCVector3 kRadial = tempVc.set(afCos[iR], 0, afSin[iR]);
				kRadial.multiply(fSliceRadius, tempVa);
				_myGeometryData.vertices().add(
					kSliceCenter.x + tempVa.x,
					kSliceCenter.y + tempVa.y,
					kSliceCenter.z + tempVa.z
				);

				CCBufferUtils.populateFromBuffer(tempVa, _myGeometryData.vertexBuffer(), i);
				kNormal = tempVa.subtractLocal(_myCenter);
				kNormal.normalizeLocal();
				
				if (_myIsOutSideView) {
					_myGeometryData.normals().add(kNormal.x, kNormal.y, kNormal.z);
				} else {
					_myGeometryData.normals().add(-kNormal.x, -kNormal.y, -kNormal.z);
				}

				_myGeometryData.textureCoords(0).add(fRadialFraction, fYFraction);

				i++;
			}

			CCBufferUtils.copyInternalVector3(_myGeometryData.vertexBuffer(), iSave, i);
			CCBufferUtils.copyInternalVector3(_myGeometryData.normalBuffer(), iSave, i);

			_myGeometryData.textureCoords(0).add(1.0f, fYFraction);

			i++;
		}

		// pole
		_myGeometryData.vertices().add(_myCenter.x, (_myCenter.y + _myRadius), _myCenter.z);

		if (_myIsOutSideView) {
			_myGeometryData.normals().add(0, 1, 0);
		} else {
			_myGeometryData.normals().add(0, -1, 0);
		}

		_myGeometryData.textureCoords(0).add(0.5f, 1.0f);
	}

	/**
	 * Generates the connections
	 */
	@Override
	protected void setIndexData() {

		// generate connectivity
		// Generate only for middle planes
		for (int plane = 1; plane < (_myPlanes - 1); plane++) {
			final int bottomPlaneStart = (plane - 1) * (_myRadialSamples + 1);
			final int topPlaneStart = plane * (_myRadialSamples + 1);
			for (int sample = 0; sample < _myRadialSamples; sample++) {
				_myGeometryData.indices().add(bottomPlaneStart + sample);
				_myGeometryData.indices().add(topPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
				_myGeometryData.indices().add(topPlaneStart + sample + 1);
			}
		}

		// pole triangles
		final int bottomPlaneStart = (_myPlanes - 2) * (_myRadialSamples + 1);
		for (int samples = 0; samples < _myRadialSamples; samples++) {
			_myGeometryData.indices().add(bottomPlaneStart + samples);
			_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 1);
			_myGeometryData.indices().add(bottomPlaneStart + samples + 1);
		}
	}

	public int planes() {
		return _myPlanes;
	}

	public int radialSamples() {
		return _myRadialSamples;
	}

	public double radius() {
		return _myRadius;
	}
}