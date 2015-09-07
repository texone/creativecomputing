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


/**
 * <code>Quad</code> defines a four sided, two dimensional shape. The local
 * height of the <code>Quad</code> defines it's size about the y-axis, while the
 * width defines the x-axis. The z-axis will always be 0.
 */
public class CCQuad extends CCPrimitive {

	protected float _myWidth = 0;
	protected float _myHeight = 0;
	


	/**
	 * Constructor creates a new <code>Quade</code> object with the provided
	 * width and height.
	 * 
	 * @param theWidth
	 *            the width of the <code>Quad</code>.
	 * @param theHeight
	 *            the height of the <code>Quad</code>.
	 */
	public CCQuad(final float theWidth, final float theHeight) {
		super();
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		final int verts = 4;
		final int myNumberOfIndices = 6;
		allocate(verts, myNumberOfIndices);
		createGeometry();
	}

	/**
	 * Constructor creates a new <code>Quad</code> object.
	 * 
	 * @param name
	 *            the name of this <code>Quad</code>.
	 */
	public CCQuad() {
		this(1, 1);
	}
	
	@Override
	protected void setGeometryData() {
		_myGeometryData.vertices().add(-_myWidth / 2, _myHeight / 2, 0);
		_myGeometryData.vertices().add(-_myWidth / 2, -_myHeight / 2, 0);
		_myGeometryData.vertices().add(_myWidth / 2, -_myHeight / 2, 0);
		_myGeometryData.vertices().add(_myWidth / 2, _myHeight / 2, 0);
		
		_myGeometryData.normals().add(0, 0, 1);
		_myGeometryData.normals().add(0, 0, 1);
		_myGeometryData.normals().add(0, 0, 1);
		_myGeometryData.normals().add(0, 0, 1);

		_myGeometryData.textureCoords(0).add(0, 1);
		_myGeometryData.textureCoords(0).add(0, 0);
		_myGeometryData.textureCoords(0).add(1, 0);
		_myGeometryData.textureCoords(0).add(1, 1);
	}
	
	@Override
	protected void setIndexData() {
		_myGeometryData.indices().add(0);
		_myGeometryData.indices().add(1);
		_myGeometryData.indices().add(2);
		_myGeometryData.indices().add(0);
		_myGeometryData.indices().add(2);
		_myGeometryData.indices().add(3);
	}

	public float width() {
		return _myWidth;
	}

	public float height() {
		return _myHeight;
	}
}