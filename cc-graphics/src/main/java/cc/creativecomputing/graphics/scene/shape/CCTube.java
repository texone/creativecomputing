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

public class CCTube extends CCPrimitive {

	private int _myAxisSamples;
	private int _myRadialSamples;

	private double _myOuterRadius;
	private double _myInnerRadius;
	private double _myHeight;

	protected boolean _myViewInside;

	public CCTube(
		final int theAxisSamples, 
		final int theRadialSamples, 
		final double theOuterRadius, 
		final double theInnerRadius, 
		final double theHeight
	) {
		_myOuterRadius = theOuterRadius;
		_myInnerRadius = theInnerRadius;
		_myHeight = theHeight;
		_myAxisSamples = theAxisSamples;
		_myRadialSamples = theRadialSamples;

		final int myNumberOfVertices = (2 * (_myAxisSamples + 1) * (_myRadialSamples + 1) + _myRadialSamples * 4);
		final int myNumberOfIndices = (4 * _myRadialSamples * (1 + _myAxisSamples)) * 3;
		allocate(myNumberOfVertices, myNumberOfIndices);
		createGeometry();
	}

	public CCTube(final double theOuterRadius, final double theInnerRadius, final double theHeight) {
		this(2, 20, theOuterRadius, theInnerRadius, theHeight);
	}

	public int axisSamples() {
		return _myAxisSamples;
	}

	public int radialSamples() {
		return _myRadialSamples;
	}

	public double outerRadius() {
		return _myOuterRadius;
	}

	public double innerRadius() {
		return _myInnerRadius;
	}

	public double height() {
		return _myHeight;
	}

	@Override
	protected void setGeometryData() {

		final double inverseRadial = 1.0f / _myRadialSamples;
		final double axisStep = _myHeight / _myAxisSamples;
		final double axisTextureStep = 1.0f / _myAxisSamples;
		final double halfHeight = 0.5f * _myHeight;
		final double innerOuterRatio = _myInnerRadius / _myOuterRadius;
		final double[] sin = new double[_myRadialSamples];
		final double[] cos = new double[_myRadialSamples];

		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			final double angle = CCMath.TWO_PI * inverseRadial * radialCount;
			cos[radialCount] = CCMath.cos(angle);
			sin[radialCount] = CCMath.sin(angle);
		}

		// outer cylinder
		for (int radialCount = 0; radialCount < _myRadialSamples + 1; radialCount++) {
			for (int axisCount = 0; axisCount < _myAxisSamples + 1; axisCount++) {
				_myGeometryData.vertices().add(
					cos[radialCount % _myRadialSamples] * _myOuterRadius,
					axisStep * axisCount - halfHeight,
					sin[radialCount % _myRadialSamples] * _myOuterRadius
				);
				if (_myViewInside) {
					_myGeometryData.normals().add(
						cos[radialCount % _myRadialSamples],
						0,
						sin[radialCount % _myRadialSamples]
					);
				} else {
					_myGeometryData.normals().add(
						-cos[radialCount % _myRadialSamples],
						0,
						-sin[radialCount % _myRadialSamples]
					);
				}
				_myGeometryData.textureCoords(0).add(
					radialCount * inverseRadial,
					axisTextureStep * axisCount
				);
			}
		}
		// inner cylinder
		for (int radialCount = 0; radialCount < _myRadialSamples + 1; radialCount++) {
			for (int axisCount = 0; axisCount < _myAxisSamples + 1; axisCount++) {
				_myGeometryData.vertices().add(
					cos[radialCount % _myRadialSamples] * _myInnerRadius,
					axisStep * axisCount - halfHeight,
					sin[radialCount % _myRadialSamples] * _myInnerRadius
				);
				if (_myViewInside) {
					_myGeometryData.normals().add(
						-cos[radialCount % _myRadialSamples],
						0,
						-sin[radialCount % _myRadialSamples]
					);
				} else {
					_myGeometryData.normals().add(
						cos[radialCount % _myRadialSamples],
						0,
						sin[radialCount % _myRadialSamples]
					);
				}
				_myGeometryData.textureCoords(0).add(
					radialCount * inverseRadial,
					axisTextureStep * axisCount
				);
			}
		}
		// bottom edge
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			_myGeometryData.vertices().add(
				cos[radialCount] * _myOuterRadius,
				-halfHeight,
				sin[radialCount] * _myOuterRadius
			);
			_myGeometryData.vertices().add(
				cos[radialCount] * _myInnerRadius,
				-halfHeight,
				sin[radialCount] * _myInnerRadius
			);
			if (_myViewInside) {
				_myGeometryData.normals().add(0,1,0);
				_myGeometryData.normals().add(0,1,0);
			} else {
				_myGeometryData.normals().add(0,-1,0);
				_myGeometryData.normals().add(0,-1,0);
			}
			_myGeometryData.textureCoords(0).add(
				0.5f + 0.5f * cos[radialCount], 
				0.5f + 0.5f * sin[radialCount]
			);
			_myGeometryData.textureCoords(0).add(
				0.5f + innerOuterRatio * 0.5f * cos[radialCount],
				0.5f + innerOuterRatio * 0.5f * sin[radialCount]
			);
		}
		// top edge
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			_myGeometryData.vertices().add(
				cos[radialCount] * _myOuterRadius,
				halfHeight,
				sin[radialCount] * _myOuterRadius
			);
			_myGeometryData.vertices().add(
				cos[radialCount] * _myInnerRadius,
				halfHeight,
				sin[radialCount] * _myInnerRadius
			);
			if (_myViewInside) {
				_myGeometryData.normals().add(0,-1,0);
				_myGeometryData.normals().add(0,-1,0);
			} else {
				_myGeometryData.normals().add(0,1,0);
				_myGeometryData.normals().add(0,1,0);
			}
			_myGeometryData.textureCoords(0).add(
				0.5f + 0.5f * cos[radialCount], 
				0.5f + 0.5f * sin[radialCount]
			);
			_myGeometryData.textureCoords(0).add(
				0.5f + innerOuterRatio * 0.5f * cos[radialCount], 
				0.5f + innerOuterRatio * 0.5f * sin[radialCount]
			);
		}

	}

	@Override
	protected void setIndexData() {
		_myGeometryData.indices().rewind();

		final int outerCylinder = (_myAxisSamples + 1) * (_myRadialSamples + 1);
		final int bottomEdge = 2 * outerCylinder;
		final int topEdge = bottomEdge + 2 * _myRadialSamples;
		// inner cylinder
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			for (int axisCount = 0; axisCount < _myAxisSamples; axisCount++) {
				final int index0 = axisCount + (_myAxisSamples + 1) * radialCount;
				final int index1 = index0 + 1;
				final int index2 = index0 + (_myAxisSamples + 1);
				final int index3 = index2 + 1;
				if (_myViewInside) {
					_myGeometryData.indices().add(index0).add(index1).add(index2);
					_myGeometryData.indices().add(index1).add(index3).add(index2);
				} else {
					_myGeometryData.indices().add(index0).add(index2).add(index1);
					_myGeometryData.indices().add(index1).add(index2).add(index3);
				}
			}
		}

		// outer cylinder
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			for (int axisCount = 0; axisCount < _myAxisSamples; axisCount++) {
				final int index0 = outerCylinder + axisCount + (_myAxisSamples + 1) * radialCount;
				final int index1 = index0 + 1;
				final int index2 = index0 + (_myAxisSamples + 1);
				final int index3 = index2 + 1;
				if (_myViewInside) {
					_myGeometryData.indices().add(index0).add(index2).add(index1);
					_myGeometryData.indices().add(index1).add(index2).add(index3);
				} else {
					_myGeometryData.indices().add(index0).add(index1).add(index2);
					_myGeometryData.indices().add(index1).add(index3).add(index2);
				}
			}
		}

		// bottom edge
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			final int index0 = bottomEdge + 2 * radialCount;
			final int index1 = index0 + 1;
			final int index2 = bottomEdge + 2 * ((radialCount + 1) % _myRadialSamples);
			final int index3 = index2 + 1;
			if (_myViewInside) {
				_myGeometryData.indices().add(index0).add(index2).add(index1);
				_myGeometryData.indices().add(index1).add(index2).add(index3);
			} else {
				_myGeometryData.indices().add(index0).add(index1).add(index2);
				_myGeometryData.indices().add(index1).add(index3).add(index2);
			}
		}

		// top edge
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			final int index0 = topEdge + 2 * radialCount;
			final int index1 = index0 + 1;
			final int index2 = topEdge + 2 * ((radialCount + 1) % _myRadialSamples);
			final int index3 = index2 + 1;
			if (_myViewInside) {
				_myGeometryData.indices().add(index0).add(index1).add(index2);
				_myGeometryData.indices().add(index1).add(index3).add(index2);
			} else {
				_myGeometryData.indices().add(index0).add(index2).add(index1);
				_myGeometryData.indices().add(index1).add(index2).add(index3);
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
}