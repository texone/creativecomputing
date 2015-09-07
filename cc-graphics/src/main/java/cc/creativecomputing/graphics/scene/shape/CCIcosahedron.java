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
 * TODO fix indices
 */
public class CCIcosahedron extends CCPrimitive {

	private static final int NUM_POINTS = 12;
	
	private final static int[] indices = {
		0,  8,  4, 
		0,  5, 10, 
		2,  4,  9, 
		2, 11,  5, 
		1,  6,  8, 
		1, 10,  7, 
		3,  9,  6, 
		3,  7, 11, 
		0, 10,  8, 
		1,  8, 10, 
		2,  9, 11,
		1,	3, 11, 
		9,  4,  2, 
		0,  5,  0, 
		2,  6,  1, 
		3,  7,  3, 
		1,  8,  6, 
		4,  9,  4, 
		6, 10,  5, 
		7, 11,  5
	};

	private float _mySideLength;

	/**
	 * Creates an Icosahedron (think of 20-sided dice) with center at the
	 * origin. The length of the sides will be as specified in sideLength.
	 * 
	 * @param theSideLength
	 *            The length of each side of the Icosahedron.
	 */
	public CCIcosahedron(final float theSideLength) {
		_mySideLength = theSideLength;
		
		allocate(NUM_POINTS, indices.length);
		createGeometry();
		
	}
	
	@Override
	protected void setGeometryData() {
		final float dGoldenRatio = 0.5f * (1.0f + CCMath.sqrt(5.0f));
		final float dInvRoot = 1.0f / CCMath.sqrt(1.0f + dGoldenRatio * dGoldenRatio);
		final float dU = (dGoldenRatio * dInvRoot * _mySideLength);
		final float dV = (dInvRoot * _mySideLength);
		
		CCVector3[] myVertices = new CCVector3[]{
			new CCVector3( dU,  dV, 0.0f),
			new CCVector3(-dU,  dV, 0.0f),
			new CCVector3( dU, -dV, 0.0f),
			new CCVector3(-dU, -dV, 0.0f),
			new CCVector3( dV, 0.0f,  dU),
			new CCVector3( dV, 0.0f, -dU),
			new CCVector3(-dV, 0.0f,  dU),
			new CCVector3(-dV, 0.0f, -dU),
			new CCVector3(0.0f,  dU,  dV),
			new CCVector3(0.0f, -dU,  dV),
			new CCVector3(0.0f,  dU, -dV),
			new CCVector3(0.0f, -dU, -dV)
		};

		final CCVector2 tex = new CCVector2();
		
		for(CCVector3 myVertex:myVertices){
			_myGeometryData.vertices().add(myVertex);
			
			if (CCMath.abs(myVertex.z) < _mySideLength) {
				tex.x = 0.5f * (1.0f + CCMath.atan2(myVertex.y, myVertex.x) / CCMath.PI);
			} else {
				tex.x = 0.5f;
			}
			tex.y = (CCMath.acos(myVertex.z / _mySideLength) / CCMath.PI);

			_myGeometryData.textureCoords(0).add(tex);
			_myGeometryData.normals().add(myVertex.normalize());
		}
	}

	@Override
	protected void setIndexData() {
		_myGeometryData.indices().add(indices);
	}
}
