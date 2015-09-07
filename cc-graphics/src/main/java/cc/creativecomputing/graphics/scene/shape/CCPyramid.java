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

import cc.creativecomputing.math.CCVector3;

/**
 * <code>Pyramid</code> provides an extension of <code>Mesh</code>. A pyramid is
 * defined by a width at the base and a height. The pyramid is a four sided
 * pyramid with the center at (0,0). The pyramid will be axis aligned with the
 * peak being on the positive y axis and the base being in the x-z plane.
 */
public class CCPyramid extends CCPrimitive {

	private float _myHeight;
	private float _myWidth;

	/**
	 * Constructor instantiates a new <code>Pyramid</code> object. The base
	 * width and the height are provided.
	 * 
	 * @param theWidth
	 *            the base width of the pyramid.
	 * @param theHeight
	 *            the height of the pyramid from the base to the peak.
	 */
	public CCPyramid(final float theWidth, final float theHeight) {
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		allocate(16, 18);
		createGeometry();
	}
	
	@Override
	protected void setGeometryData() {
		final CCVector3 peak = new CCVector3(0, _myHeight / 2, 0);
		final CCVector3 vert0 = new CCVector3(-_myWidth / 2, -_myHeight / 2, -_myWidth / 2);
		final CCVector3 vert1 = new CCVector3( _myWidth / 2, -_myHeight / 2, -_myWidth / 2);
		final CCVector3 vert2 = new CCVector3( _myWidth / 2, -_myHeight / 2,  _myWidth / 2);
		final CCVector3 vert3 = new CCVector3(-_myWidth / 2, -_myHeight / 2,  _myWidth / 2);
		
		// base
		_myGeometryData.vertices().add(vert3);
		_myGeometryData.vertices().add(vert2);
		_myGeometryData.vertices().add(vert1);
		_myGeometryData.vertices().add(vert0);

		_myGeometryData.normals().add(0, -1, 0);
		_myGeometryData.normals().add(0, -1, 0);
		_myGeometryData.normals().add(0, -1, 0);
		_myGeometryData.normals().add(0, -1, 0);

		_myGeometryData.textureCoords(0).add(1, 0);
		_myGeometryData.textureCoords(0).add(0, 0);
		_myGeometryData.textureCoords(0).add(0, 1);
		_myGeometryData.textureCoords(0).add(1, 1);

		// side 1
		_myGeometryData.vertices().add(vert0);
		_myGeometryData.vertices().add(vert1);
		_myGeometryData.vertices().add(peak);

		_myGeometryData.normals().add(0, 0.70710677f, -0.70710677f);
		_myGeometryData.normals().add(0, 0.70710677f, -0.70710677f);
		_myGeometryData.normals().add(0, 0.70710677f, -0.70710677f);

		_myGeometryData.textureCoords(0).add(1, 0);
		_myGeometryData.textureCoords(0).add(0.75f, 0);
		_myGeometryData.textureCoords(0).add(0.5f, 1);

		// side 2
		_myGeometryData.vertices().add(vert1);
		_myGeometryData.vertices().add(vert2);
		_myGeometryData.vertices().add(peak);

		_myGeometryData.normals().add(0.70710677f, 0.70710677f, 0);
		_myGeometryData.normals().add(0.70710677f, 0.70710677f, 0);
		_myGeometryData.normals().add(0.70710677f, 0.70710677f, 0);

		_myGeometryData.textureCoords(0).add(0.75f, 0);
		_myGeometryData.textureCoords(0).add(0.5f, 0);
		_myGeometryData.textureCoords(0).add(0.5f, 1);
		
		// side 3
		_myGeometryData.vertices().add(vert2);
		_myGeometryData.vertices().add(vert3);
		_myGeometryData.vertices().add(peak);
		
		_myGeometryData.normals().add(0, 0.70710677f, 0.70710677f);
		_myGeometryData.normals().add(0, 0.70710677f, 0.70710677f);
		_myGeometryData.normals().add(0, 0.70710677f, 0.70710677f);

		_myGeometryData.textureCoords(0).add(0.5f, 0);
		_myGeometryData.textureCoords(0).add(0.25f, 0);
		_myGeometryData.textureCoords(0).add(0.5f, 1);

		// side 4
		_myGeometryData.vertices().add(vert3);
		_myGeometryData.vertices().add(vert0);
		_myGeometryData.vertices().add(peak);

		_myGeometryData.normals().add(-0.70710677f, 0.70710677f, 0);
		_myGeometryData.normals().add(-0.70710677f, 0.70710677f, 0);
		_myGeometryData.normals().add(-0.70710677f, 0.70710677f, 0);

		_myGeometryData.textureCoords(0).add(0.25f, 0);
		_myGeometryData.textureCoords(0).add(0, 0);
		_myGeometryData.textureCoords(0).add(0.5f, 1);
	}

	/**
	 * <code>setIndexData</code> sets the indices into the list of vertices,
	 * defining all triangles that constitute the pyramid.
	 */
	@Override
	protected void setIndexData() {
		_myGeometryData.indices().add(3);
		_myGeometryData.indices().add(2);
		_myGeometryData.indices().add(1);
		
		_myGeometryData.indices().add(3);
		_myGeometryData.indices().add(1);
		_myGeometryData.indices().add(0);
		
		_myGeometryData.indices().add(6);
		_myGeometryData.indices().add(5);
		_myGeometryData.indices().add(4);
		
		_myGeometryData.indices().add(9);
		_myGeometryData.indices().add(8);
		_myGeometryData.indices().add(7);
		
		_myGeometryData.indices().add(12);
		_myGeometryData.indices().add(11);
		_myGeometryData.indices().add(10);
		
		_myGeometryData.indices().add(15);
		_myGeometryData.indices().add(14);
		_myGeometryData.indices().add(13);
	}
}