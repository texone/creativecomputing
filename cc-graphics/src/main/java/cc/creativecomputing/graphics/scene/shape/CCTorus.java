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

public class CCTorus extends CCPrimitive {

	protected int _myCircleSamples;
	protected int _myRadialSamples;

	protected double _myTubeRadius;
	protected double _myCenterRadius;

	protected boolean _myViewInside;

	/**
	 * Constructs a new Torus. Center is the origin, but the Torus may be
	 * transformed.
	 * 
	 * @param circleSamples
	 *            The number of samples along the circles.
	 * @param radialSamples
	 *            The number of samples along the radial.
	 * @param tubeRadius
	 *            the radius of the torus tube.
	 * @param centerRadius
	 *            The distance from the center of the torus hole to the center
	 *            of the torus tube.
	 */
	public CCTorus(final int circleSamples, final int radialSamples, final double tubeRadius, final double centerRadius) {
		_myCircleSamples = circleSamples;
		_myRadialSamples = radialSamples;
		_myTubeRadius = tubeRadius;
		_myCenterRadius = centerRadius;

		final int myNumberOfVertices = (_myCircleSamples + 1) * (_myRadialSamples + 1);
		final int myNumberOfIndices = 2 * _myCircleSamples * _myRadialSamples * 3;
		
		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}

	@Override
	protected void setGeometryData() {

		// generate geometry
		final double inverseCircleSamples = 1.0f / _myCircleSamples;
		final double inverseRadialSamples = 1.0f / _myRadialSamples;
		
		// generate the cylinder itself
		final CCVector3 radialAxis = new CCVector3();
		final CCVector3 torusMiddle = new CCVector3();
		final CCVector3 tempNormal = new CCVector3();
		
		for (int circleCount = 0; circleCount <= _myCircleSamples; circleCount++) {
			// compute center point on torus circle at specified angle
			final double circleFraction = circleCount * inverseCircleSamples;
			final double theta = CCMath.TWO_PI * circleFraction;
			final double cosTheta = CCMath.cos(theta);
			final double sinTheta = CCMath.sin(theta);
			radialAxis.set(cosTheta, sinTheta, 0);
			radialAxis.multiply(_myCenterRadius, torusMiddle);

			// compute slice vertices with duplication at end point
			for (int radialCount = 0; radialCount <= _myRadialSamples; radialCount++) {
				final double radialFraction = radialCount * inverseRadialSamples;
				// in [0,1)
				final double phi = CCMath.TWO_PI * radialFraction;
				final double cosPhi = CCMath.cos(phi);
				final double sinPhi = CCMath.sin(phi);
				
				tempNormal.set(radialAxis).multiplyLocal(cosPhi);
				tempNormal.z = (tempNormal.z + sinPhi);
				tempNormal.normalizeLocal();
				
				if (!_myViewInside) {
					_myGeometryData.normals().add(tempNormal);
				} else {
					_myGeometryData.normals().add(-tempNormal.x, -tempNormal.y, -tempNormal.z);
				}

				tempNormal.multiplyLocal(_myTubeRadius).addLocal(torusMiddle);
				_myGeometryData.vertices().add(tempNormal);
				_myGeometryData.textureCoords(0).add(radialFraction,circleFraction);
			}
		}
	}

	@Override
	protected void setIndexData() {
		// generate connectivity
		int connectionStart = 0;
		for (int circleCount = 0; circleCount < _myCircleSamples; circleCount++) {
			int i0 = connectionStart;
			int i1 = i0 + 1;
			connectionStart += _myRadialSamples + 1;
			int i2 = connectionStart;
			int i3 = i2 + 1;
			for (int i = 0; i < _myRadialSamples; i++) {
				if (!_myViewInside) {
					_myGeometryData.indices().add(i0++);
					_myGeometryData.indices().add(i2);
					_myGeometryData.indices().add(i1);
					_myGeometryData.indices().add(i1++);
					_myGeometryData.indices().add(i2++);
					_myGeometryData.indices().add(i3++);
				} else {
					_myGeometryData.indices().add(i0++);
					_myGeometryData.indices().add(i1);
					_myGeometryData.indices().add(i2);
					_myGeometryData.indices().add(i1++);
					_myGeometryData.indices().add(i3++);
					_myGeometryData.indices().add(i2++);
				}
			}
		}
	}

	/**
	 * 
	 * @return true if the normals are inverted to point into the torus so that
	 *         the face is oriented for a viewer inside the torus. false (the
	 *         default) for exterior viewing.
	 */
	public boolean isViewFromInside() {
		return _myViewInside;
	}

	/**
	 * 
	 * @param viewInside
	 *            if true, the normals are inverted to point into the torus so
	 *            that the face is oriented for a viewer inside the torus.
	 *            Default is false (for outside viewing)
	 */
	public void setViewFromInside(final boolean viewInside) {
		if (viewInside != _myViewInside) {
			_myViewInside = viewInside;
			setGeometryData();
			setIndexData();
		}
	}

}