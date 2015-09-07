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
 * <code>Cylinder</code> provides an extension of <code>Mesh</code>. A
 * <code>Cylinder</code> is defined by a height and radius. The center of the
 * Cylinder is the origin.
 */
public class CCCylinder extends CCPrimitive {

	private int _myAxisSamples;
	private int _myRadialSamples;

	private double _myRadius;
	private double _myRadius2;

	private double _myHeight;
	private boolean _myIsClosed;
	private boolean _myInvertNormals;
	
	
	protected CCCylinder(
		final int theAxisSamples, 
		final int theRadialSamples, 
		final double theRadius, 
		final double theRadius2, 
		final double theHeight, 
		final boolean theIsClosed,
		final boolean theIsInverted
	) {
		_myAxisSamples = theAxisSamples + (theIsClosed ? 2 : 0);
		_myRadialSamples = theRadialSamples;
		_myRadius = theRadius;
		_myRadius2 = theRadius2;
		_myHeight = theHeight;
		_myIsClosed = theIsClosed;
		_myInvertNormals = theIsInverted;

		final int myNumberOfVertices = _myAxisSamples * (_myRadialSamples + 1) + (_myIsClosed ? 2 : 0);
		final int myNumberOfIndices = ((_myIsClosed ? 2 : 0) + 2 * (_myAxisSamples - 1)) * _myRadialSamples * 3;
		
		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}
	
	/**
	 * Creates a new Cylinder. By default its center is the origin. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information. <br>
	 * If the cylinder is closed the texture is split into axisSamples parts:
	 * top most and bottom most part is used for top and bottom of the cylinder,
	 * rest of the texture for the cylinder wall. The middle of the top is
	 * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
	 * a suited distorted texture.
	 * 
	 * @param theAxisSamples
	 *            Number of triangle samples along the axis.
	 * @param theRadialSamples
	 *            Number of triangle samples along the radial.
	 * @param theRadius
	 *            The radius of the cylinder.
	 * @param theHeight
	 *            The cylinder's height.
	 * @param theIsClosed
	 *            true to create a cylinder with top and bottom surface
	 * @param theIsInverted
	 *            true to create a cylinder that is meant to be viewed from the
	 *            interior.
	 */
	public CCCylinder(
		final int theAxisSamples, 
		final int theRadialSamples, 
		final double theRadius, 
		final double theHeight, 
		final boolean theIsClosed,
		final boolean theIsInverted
	) {
		this(theAxisSamples, theRadialSamples, theRadius, theRadius, theHeight, theIsClosed, theIsInverted);
	}

	/**
	 * Creates a new Cylinder. By default its center is the origin. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information.
	 * 
	 * @param axisSamples
	 *            Number of triangle samples along the axis.
	 * @param theRadialSamples
	 *            Number of triangle samples along the radial.
	 * @param radius
	 *            The radius of the cylinder.
	 * @param height
	 *            The cylinder's height.
	 */
	public CCCylinder(final int theAxisSamples, final int theRadialSamples, final double radius, final double height) {
		this(theAxisSamples, theRadialSamples, radius, height, false);
	}

	/**
	 * Creates a new Cylinder. By default its center is the origin. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information. <br>
	 * If the cylinder is closed the texture is split into axisSamples parts:
	 * top most and bottom most part is used for top and bottom of the cylinder,
	 * rest of the texture for the cylinder wall. The middle of the top is
	 * mapped to texture coordinates (0.5, 1), bottom to (0.5, 0). Thus you need
	 * a suited distorted texture.
	 * 
	 * @param name
	 *            The name of this Cylinder.
	 * @param theAxisSamples
	 *            Number of triangle samples along the axis.
	 * @param theRadialSamples
	 *            Number of triangle samples along the radial.
	 * @param radius
	 *            The radius of the cylinder.
	 * @param height
	 *            The cylinder's height.
	 * @param closed
	 *            true to create a cylinder with top and bottom surface
	 */
	public CCCylinder(final int theAxisSamples, final int theRadialSamples, final double radius, final double height, final boolean closed) {
		this(theAxisSamples, theRadialSamples, radius, height, closed, false);
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
	 * Change the radius of this cylinder. This resets any second radius.
	 * 
	 * @param theRadius
	 *            The radius to set.
	 */
	public void radius(final double theRadius) {
		_myRadius = theRadius;
		_myRadius2 = theRadius;
		createGeometry();
	}

	/**
	 * Set the top and bottom radius of the 'cylinder'
	 * 
	 * @param theTopRadius
	 *            The top radius to set.
	 * @param theBottomRadius
	 *            The bottom radius to set.
	 * @see com.CCCone.extension.shape.Cone
	 */
	public void radii(final double theTopRadius, final double theBottomRadius) {
		_myRadius = theTopRadius;
		_myRadius2 = theBottomRadius;
		createGeometry();
	}

	/**
	 * @return the number of samples along the cylinder axis
	 */
	public int axisSamples() {
		return _myAxisSamples;
	}

	/**
	 * @return number of samples around cylinder
	 */
	public int radialSamples() {
		return _myRadialSamples;
	}

	/**
	 * @return true if end caps are used.
	 */
	public boolean isClosed() {
		return _myIsClosed;
	}

	/**
	 * @return true if normals and uvs are created for interior use
	 */
	public boolean isInverted() {
		return _myInvertNormals;
	}

	@Override
	protected void setGeometryData() {
		// generate geometry
		final double inverseRadial = 1.0f / _myRadialSamples;
		final double inverseAxisLess = 1.0f / (_myIsClosed ? _myAxisSamples - 3 : _myAxisSamples - 1);
		final double inverseAxisLessTexture = 1.0f / (_myAxisSamples - 1);
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

		// generate the cylinder itself
		final CCVector3 tempNormal = new CCVector3();
		for (int axisCount = 0; axisCount < _myAxisSamples; axisCount++) {
			double axisFraction;
			double axisFractionTexture;
			int topBottom = 0;
			if (!_myIsClosed) {
				axisFraction = axisCount * inverseAxisLess; // in [0,1]
				axisFractionTexture = axisFraction;
			} else {
				if (axisCount == 0) {
					topBottom = -1; // bottom
					axisFraction = 0;
					axisFractionTexture = inverseAxisLessTexture;
				} else if (axisCount == _myAxisSamples - 1) {
					topBottom = 1; // top
					axisFraction = 1;
					axisFractionTexture = 1 - inverseAxisLessTexture;
				} else {
					axisFraction = (axisCount - 1) * inverseAxisLess;
					axisFractionTexture = axisCount * inverseAxisLessTexture;
				}
			}
			final double z = -halfHeight + _myHeight * axisFraction;

			// compute center of slice
			final CCVector3 sliceCenter = new CCVector3(0, 0, z);

			// compute slice vertices with duplication at end point
			for (int radialCount = 0; radialCount <= _myRadialSamples; radialCount++) {
				final double radialFraction = radialCount * inverseRadial; // in
																			// [0,1)
				tempNormal.set(cos[radialCount], sin[radialCount], 0);
				if (topBottom == 0) {
					if (!_myInvertNormals) {
						_myGeometryData.normals().add(tempNormal);
					} else {
						_myGeometryData.normals().add(-tempNormal.x, -tempNormal.y, -tempNormal.z);
					}
				} else {
					_myGeometryData.normals().add(0,0,topBottom * (_myInvertNormals ? -1 : 1));
				}

				tempNormal.multiplyLocal((_myRadius - _myRadius2) * axisFraction + _myRadius2).addLocal(sliceCenter);
				_myGeometryData.vertices().add(tempNormal);

				_myGeometryData.textureCoords(0).add(_myInvertNormals ? 1 - radialFraction : radialFraction, axisFractionTexture);
			}
		}

		if (_myIsClosed) {
			_myGeometryData.vertices().add(0,0,-halfHeight); // bottom
			_myGeometryData.normalBuffer().put(0).put(0).put(-1 * (_myInvertNormals ? -1 : 1));
			_myGeometryData.textureCoords(0).add(0.5f,0);
			
			_myGeometryData.vertices().add(0,0, halfHeight); // top
			_myGeometryData.normals().add(0,0,(_myInvertNormals ? -1 : 1));
			_myGeometryData.textureCoords(0).add(0.5f,1);
		}
	}

	@Override
	protected void setIndexData() {

		// generate connectivity
		for (int axisCount = 0, axisStart = 0; axisCount < _myAxisSamples - 1; axisCount++) {
			int i0 = axisStart;
			int i1 = i0 + 1;
			axisStart += _myRadialSamples + 1;
			int i2 = axisStart;
			int i3 = i2 + 1;
			for (int i = 0; i < _myRadialSamples; i++) {
				if (_myIsClosed && axisCount == 0) {
					if (!_myInvertNormals) {
						_myGeometryData.indices().add(i0++);
						_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 2);
						_myGeometryData.indices().add(i1++);
					} else {
						_myGeometryData.indices().add(i0++);
						_myGeometryData.indices().add(i1++);
						_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 2);
					}
				} else if (_myIsClosed && axisCount == _myAxisSamples - 2) {
					if (!_myInvertNormals) {
						_myGeometryData.indices().add(i2++);
						_myGeometryData.indices().add(i3++);
						_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 1);
					} else {
						_myGeometryData.indices().add(i2++);
						_myGeometryData.indices().add(_myGeometryData.numberOfVertices() - 1);
						_myGeometryData.indices().add(i3++);
					}
				} else {
					if (!_myInvertNormals) {
						_myGeometryData.indices().add(i0++);
						_myGeometryData.indices().add(i1);
						_myGeometryData.indices().add(i2);
						_myGeometryData.indices().add(i1++);
						_myGeometryData.indices().add(i3++);
						_myGeometryData.indices().add(i2++);
					} else {
						_myGeometryData.indices().add(i0++);
						_myGeometryData.indices().add(i2);
						_myGeometryData.indices().add(i1);
						_myGeometryData.indices().add(i1++);
						_myGeometryData.indices().add(i2++);
						_myGeometryData.indices().add(i3++);
					}
				}
			}
		}
	}
}
