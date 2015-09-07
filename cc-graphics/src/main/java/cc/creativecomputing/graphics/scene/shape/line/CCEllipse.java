/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.scene.shape.line;

import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.graphics.scene.shape.CCPrimitive;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * An approximations of a flat circle. It is simply defined with a radius. It
 * starts out flat along the Z, with center at the origin.
 */
public class CCEllipse extends CCPrimitive {

	private int _myRadialSamples;

	private double _myXRadius;
	private double _myYRadius;

	/**
	 * Creates a circle at the origin flat along the Z. Usually, a
	 * higher sample number creates a better looking circle, but at the cost
	 * of more vertex information.
	 * 
	 * @param theRadialSamples
	 *            The number of radial samples.
	 * @param theRadius
	 *            The radius of the disk.
	 */
	public CCEllipse(final int theRadialSamples, final double theRadiusX, final double theRadiusY) {

		_myRadialSamples = theRadialSamples;
		_myXRadius = theRadiusX;
		_myYRadius = theRadiusY;
		
		// allocate vertices
		final int myNumberOfVertices = _myRadialSamples;
		final int myNumberOfInidices = _myRadialSamples;
		allocate(GLDrawMode.LINE_LOOP, myNumberOfVertices, myNumberOfInidices);
		createGeometry();
	}
	
	public CCEllipse(final int theRadialSamples, final double theRadius){
		this(theRadialSamples, theRadius, theRadius);
	}

	@Override
	protected void setGeometryData() {
		
		for (int i = 0; i < _myRadialSamples; i++) {
			final double angle = CCMath.map(i, 0, _myRadialSamples, 0, CCMath.TWO_PI);
			final double cos = CCMath.cos(angle);
			final double sin = CCMath.sin(angle);
			final CCVector3 radial = new CCVector3(cos, sin, 0);

			_myGeometryData.textureCoords(0).add(0.5f * (1.0f + radial.x), 0.5f * (1.0f + radial.y));
			_myGeometryData.vertices().add(radial.x * _myXRadius, radial.y * _myYRadius, 0);
			_myGeometryData.normals().add(radial.x, radial.y, 0);
		}
	}

	@Override
	protected void setIndexData() {
		for (int i = 0; i < _myRadialSamples; i++) {
			_myGeometryData.indices().add(i);
		}
	}
}