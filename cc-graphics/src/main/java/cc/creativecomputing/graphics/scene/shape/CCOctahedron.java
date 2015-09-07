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
 * An eight faced polyhedron. It looks somewhat like two pyramids placed bottom
 * to bottom.
 */
public class CCOctahedron extends CCPrimitive {

	private static final int NUM_POINTS = 6;

	private static final int NUM_TRIS = 8;

	private float _mySideLength;

	

	/**
	 * Creates an octahedron with center at the origin. The length sides are
	 * given.
	 * 
	 * @param theSideLength
	 *            The length of each side of the octahedron.
	 */
	public CCOctahedron(final float theSideLength) {
		_mySideLength = theSideLength;

		allocate(NUM_POINTS, 3 * NUM_TRIS);
		createGeometry();

	}

	@Override
	protected void setIndexData() {
		_myGeometryData.indices().add(4).add(0).add(2);
		_myGeometryData.indices().add(4).add(2).add(1);
		_myGeometryData.indices().add(4).add(1).add(3);
		_myGeometryData.indices().add(4).add(3).add(0);
		_myGeometryData.indices().add(5).add(2).add(0);
		_myGeometryData.indices().add(5).add(1).add(2);
		_myGeometryData.indices().add(5).add(3).add(1);
		_myGeometryData.indices().add(5).add(0).add(3);
	}
	
	@Override
	protected void setGeometryData() {

		CCVector3[] myVertices = new CCVector3[]{
			new CCVector3(_mySideLength, 0.0f, 0.0f),
			new CCVector3(-_mySideLength, 0.0f, 0.0f),
			new CCVector3(0.0f, _mySideLength, 0.0f),
			new CCVector3(0.0f, -_mySideLength, 0.0f),
			new CCVector3(0.0f, 0.0f, _mySideLength),
			new CCVector3(0.0f, 0.0f, -_mySideLength)
		};

		final CCVector2 tex = new CCVector2();
		for(CCVector3 myVertex:myVertices){
			_myGeometryData.vertices().add(myVertex);
			
			if (CCMath.abs(myVertex.z) < _mySideLength) {
				tex.x = 0.5f * (1.0f + CCMath.atan2(myVertex.y, myVertex.x) / CCMath.PI);
			} else {
				tex.x = 0.5f;
			}
			tex.y = CCMath.acos(myVertex.z / _mySideLength) / CCMath.PI;
			_myGeometryData.textureCoords(0).add(tex);
			
			_myGeometryData.normals().add(myVertex.normalize());
		}	
	}
}