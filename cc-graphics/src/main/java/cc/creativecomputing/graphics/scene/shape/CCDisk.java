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
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * An approximations of a flat circle. It is simply defined with a radius. It
 * starts out flat along the Z, with center at the origin.
 */
public class CCDisk extends CCPrimitive {

	private int _myShellSamples;
	private int _myRadialSamples;

	private int _myRadialless;
	private int _myShellLess;

	private double _myRadius;

	/**
	 * Creates a flat disk (circle) at the origin flat along the Z. Usually, a
	 * higher sample number creates a better looking cylinder, but at the cost
	 * of more vertex information.
	 * 
	 * @param theShellSamples
	 *            The number of shell samples.
	 * @param theRadialSamples
	 *            The number of radial samples.
	 * @param theRadius
	 *            The radius of the disk.
	 */
	public CCDisk(final int theShellSamples, final int theRadialSamples, final double theRadius) {

		_myShellSamples = theShellSamples;
		_myRadialSamples = theRadialSamples;
		_myRadius = theRadius;

		_myRadialless = theRadialSamples - 1;
		_myShellLess = theShellSamples - 1;
		// allocate vertices
		final int myNumberOfVertices = 1 + theRadialSamples * _myShellLess;
		final int myNumberOfInidices = theRadialSamples * (2 * _myShellLess - 1) * 3;
		allocate(myNumberOfVertices, myNumberOfInidices);
		createGeometry();
	}

	@Override
	protected void setGeometryData() {
		// generate geometry
		// center of disk
		_myGeometryData.vertices().add(0,0,0);

		for (int x = 0; x < _myGeometryData.numberOfVertices(); x++) {
			_myGeometryData.normals().add(0,0,1);
		}

		_myGeometryData.textureCoords(0).add(.5f,.5f);

		final double inverseShellLess = 1.0f / _myShellLess;
		final double inverseRadial = 1.0f / _myRadialSamples;
		final CCVector3 radialFraction = new CCVector3();
		final CCVector2 texCoord = new CCVector2();
		for (int radialCount = 0; radialCount < _myRadialSamples; radialCount++) {
			final double angle = CCMath.TWO_PI * inverseRadial * radialCount;
			final double cos = CCMath.cos(angle);
			final double sin = CCMath.sin(angle);
			final CCVector3 radial = new CCVector3(cos, sin, 0);

			for (int shellCount = 1; shellCount < _myShellSamples; shellCount++) {
				final double fraction = inverseShellLess * shellCount; // in
																		// (0,R]
				radialFraction.set(radial).multiplyLocal(fraction);
				final int i = shellCount + _myShellLess * radialCount;
				texCoord.set(0.5f * (1.0f + radialFraction.x), 0.5f * (1.0f + radialFraction.y));
				_myGeometryData.textureCoords(0).put(i, texCoord);
				
				radialFraction.multiplyLocal(_myRadius);
				_myGeometryData.vertices().put(i, radialFraction);
			}
		}
	}

	@Override
	protected void setIndexData() {
		// generate connectivity
		for (int radialCount0 = _myRadialless, radialCount1 = 0; radialCount1 < _myRadialSamples; radialCount0 = radialCount1++) {
			_myGeometryData.indices().add(0);
			_myGeometryData.indices().add(1 + _myShellLess * radialCount0);
			_myGeometryData.indices().add(1 + _myShellLess * radialCount1);
			for (int iS = 1; iS < _myShellLess; iS++) {
				final int i00 = iS + _myShellLess * radialCount0;
				final int i01 = iS + _myShellLess * radialCount1;
				final int i10 = i00 + 1;
				final int i11 = i01 + 1;
				_myGeometryData.indices().add(i00);
				_myGeometryData.indices().add(i10);
				_myGeometryData.indices().add(i11);
				_myGeometryData.indices().add(i00);
				_myGeometryData.indices().add(i11);
				_myGeometryData.indices().add(i01);
			}
		}
	}
}