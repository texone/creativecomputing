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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * <code>Capsule</code> provides an extension of <code>Mesh</code>. A
 * <code>Capsule</code> is defined by a height and a radius. The center of the
 * Capsule is the origin.
 */
public class CCCapsule extends CCPrimitive {

	private int _myAxisSamples;
	private int _myRadialSamples;
	private int _mySphereSamples;

	private double _myRadius;
	private double _myHeight;

	/**
	 * Creates a new Capsule. By default its center is the origin. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information. <br>
	 * If the cylinder is closed the texture is split into axisSamples parts:
	 * top most and bottom most part is used for top and bottom of the cylinder,
	 * rest of the texture for the cylinder wall. The middle of the top is
	 * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
	 * a suited distorted texture.
	 * 
	 * @param axisSamples
	 *            Number of triangle samples along the axis.
	 * @param radialSamples
	 *            Number of triangle samples along the radial.
	 * @param radius
	 *            The radius of the cylinder.
	 * @param height
	 *            The cylinder's height.
	 */
	public CCCapsule(final int axisSamples, final int radialSamples, final int sphereSamples, final double radius, final double height) {

		_myAxisSamples = axisSamples;
		_mySphereSamples = sphereSamples;
		_myRadialSamples = radialSamples;
		_myRadius = radius;
		_myHeight = height;

		// determine vert quantity - first the sphere caps
		final int sampleLines = (2 * _mySphereSamples - 1 + _myAxisSamples);
		final int myNumberOfVertices = (_myRadialSamples + 1) * sampleLines + 2;
		final int myNumberOfIndices = 2 * _myRadialSamples * sampleLines * 3;

		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}

	/**
	 * @return Returns the height.
	 */
	public double height() {
		return _myHeight;
	}

	/**
	 * @param theHeight
	 *            The height to set.
	 */
	public void height(final double theHeight) {
		_myHeight = theHeight;
		createGeometry();
	}

	/**
	 * @return Returns the radius.
	 */
	public double radius() {
		return _myRadius;
	}

	/**
	 * Change the radius of this cylinder.
	 * 
	 * @param theRadius
	 *            The radius to set.
	 */
	public void radius(final double theRadius) {
		_myRadius = theRadius;
		createGeometry();
	}

	@Override
	protected void setGeometryData() {

		// generate geometry
		final double inverseRadial = 1.0f / _myRadialSamples;
		final double inverseSphere = 1.0f / _mySphereSamples;
		final double halfHeight = 0.5f * _myHeight;

		// Generate points on the unit circle to be used in computing the mesh
		// points on a cylinder slice.
		final double[] sin = new double[_myRadialSamples + 1];
		final double[] cos = new double[_myRadialSamples + 1];

		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			final double angle = CCMath.TWO_PI * inverseRadial * radialCount;
			cos[radialCount] = CCMath.cos(angle);
			sin[radialCount] = CCMath.sin(angle);
		}
		sin[_myRadialSamples] = sin[0];
		cos[_myRadialSamples] = cos[0];

		final CCVector3 tempA = new CCVector3();

		// top point.
		_myGeometryData.vertices().add(0, _myRadius + halfHeight, 0);
		_myGeometryData.normals().add(0,1,0);
		_myGeometryData.textureCoords(0).add(1,1);

		// generating the top dome.
		for (int i = 0; i < _mySphereSamples; i++) {
			final double center = _myRadius * (1 - (i + 1) * (inverseSphere));
			final double lengthFraction = (center + _myHeight + _myRadius) / (_myHeight + 2 * _myRadius);

			// compute radius of slice
			final double fSliceRadius = CCMath.sqrt(CCMath.abs(_myRadius * _myRadius - center * center));

			for (int j = 0; j <= _myRadialSamples; j++) {
				final CCVector3 kRadial = tempA.set(cos[j], 0, sin[j]);
				kRadial.multiplyLocal(fSliceRadius);
				_myGeometryData.vertices().add(kRadial.x, (center + halfHeight),kRadial.z);
				kRadial.y = center;
				kRadial.normalizeLocal();
				_myGeometryData.normals().add(kRadial.x,kRadial.y,kRadial.z);
				final double radialFraction = 1 - (j * inverseRadial); // in
																		// [0,1)
				_myGeometryData.textureCoords(0).add( radialFraction, lengthFraction);
			}
		}

		// generate cylinder... but no need to add points for first and last
		// samples as they are already part of domes.
		for (int i = 1; i < _myAxisSamples; i++) {
			final double center = halfHeight - (i * _myHeight / _myAxisSamples);
			final double lengthFraction = (center + halfHeight + _myRadius) / (_myHeight + 2 * _myRadius);

			for (int j = 0; j <= _myRadialSamples; j++) {
				
				final CCVector3 kRadial = tempA.set(cos[j], 0, sin[j]);
				kRadial.multiplyLocal(_myRadius);
				_myGeometryData.vertices().add(kRadial.x, center,kRadial.z);
				
				kRadial.normalizeLocal();
				_myGeometryData.normals().add(kRadial.x,kRadial.y,kRadial.z);
				
				final double radialFraction = 1 - (j * inverseRadial); // in														// [0,1)
				_myGeometryData.textureCoords(0).add( radialFraction, lengthFraction);
			}
		}

		// generating the bottom dome.
		for (int i = 0; i < _mySphereSamples; i++) {
			final double center = i * (_myRadius / _mySphereSamples);
			final double lengthFraction = (_myRadius - center) / (_myHeight + 2 * _myRadius);

			// compute radius of slice
			final double fSliceRadius = CCMath.sqrt(CCMath.abs(_myRadius * _myRadius - center * center));

			for (int j = 0; j <= _myRadialSamples; j++) {
				final CCVector3 kRadial = tempA.set(cos[j], 0, sin[j]);
				kRadial.multiplyLocal(fSliceRadius);
				_myGeometryData.vertices().add(kRadial.x, (-center - halfHeight),kRadial.z);
				kRadial.y = -center;
				kRadial.normalizeLocal();
				_myGeometryData.normals().add(kRadial.x,kRadial.y,kRadial.z);
				final double radialFraction = 1 - (j * inverseRadial); // in
																		// [0,1)
				_myGeometryData.textureCoords(0).add( radialFraction, lengthFraction);
			}
		}

		// bottom point.
		_myGeometryData.vertices().add(0, (-_myRadius - halfHeight),0);
		_myGeometryData.normals().add(0,-1,0);
		_myGeometryData.textureCoords(0).add(0,0);

	}

	@Override
	protected void setIndexData() {
		_myGeometryData.indices().rewind();

		// start with top of top dome.
		for (int samples = 1; samples <= _myRadialSamples; samples++) {
			_myGeometryData.indices().add(samples + 1);
			_myGeometryData.indices().add(samples);
			_myGeometryData.indices().add(0);
		}

		for (int plane = 1; plane < (_mySphereSamples); plane++) {
			final int topPlaneStart = plane * (_myRadialSamples + 1);
			final int bottomPlaneStart = (plane - 1) * (_myRadialSamples + 1);
			for (int sample = 1; sample <= _myRadialSamples; sample++) {
				_myGeometryData.indices().add(bottomPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
			}
		}

		int start = _mySphereSamples * (_myRadialSamples + 1);

		// add cylinder
		for (int plane = 0; plane < (_myAxisSamples); plane++) {
			final int topPlaneStart = start + plane * (_myRadialSamples + 1);
			final int bottomPlaneStart = start + (plane - 1) * (_myRadialSamples + 1);
			for (int sample = 1; sample <= _myRadialSamples; sample++) {
				_myGeometryData.indices().add(bottomPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
			}
		}

		start += ((_myAxisSamples - 1) * (_myRadialSamples + 1));

		// Add most of the bottom dome triangles.
		for (int plane = 1; plane < (_mySphereSamples); plane++) {
			final int topPlaneStart = start + plane * (_myRadialSamples + 1);
			final int bottomPlaneStart = start + (plane - 1) * (_myRadialSamples + 1);
			for (int sample = 1; sample <= _myRadialSamples; sample++) {
				_myGeometryData.indices().add(bottomPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
				_myGeometryData.indices().add(bottomPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample + 1);
				_myGeometryData.indices().add(topPlaneStart + sample);
			}
		}

		start += ((_mySphereSamples - 1) * (_myRadialSamples + 1));
		// Finally the bottom of bottom dome.
		for (int samples = 1; samples <= _myRadialSamples; samples++) {
			_myGeometryData.indices().add(start + samples);
			_myGeometryData.indices().add(start + samples + 1);
			_myGeometryData.indices().add(start + _myRadialSamples + 2);
		}
	}

}
