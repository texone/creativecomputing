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

public class CCRoundedBox extends CCPrimitive {

	private final CCVector3 _myExtent;
	private final CCVector3 _myBorder;
	private final CCVector3 _mySlope;



	public CCRoundedBox(final CCVector3 extent, final CCVector3 border, final CCVector3 slope) {
		_myBorder = border;
		_mySlope = slope;
		_myExtent = extent;
		extent.subtractLocal(_mySlope);
		
		allocate(48, 180);
		createGeometry();
	}

	public CCRoundedBox(final CCVector3 extent) {
		this(extent, new CCVector3(0.05f, 0.05f, 0.05f),new CCVector3(0.02f, 0.02f, 0.02f));
	}
	
	public CCRoundedBox(double theExtent){
		this(new CCVector3(theExtent, theExtent, theExtent));
	}
	
	/** Creates a new instance of RoundedBox */
	public CCRoundedBox() {
		this(0.5f);
	}
	
	private void addVertexNormal(final CCVector3 vec) {
		_myGeometryData.vertices().add(vec);
		_myGeometryData.normals().add(vec.normalize());
	}

	@Override
	protected void setGeometryData() {
		final CCVector3[] vert = computeVertices(); // returns 32

		// bottom
		addVertexNormal(vert[0]);
		addVertexNormal(vert[1]);
		addVertexNormal(vert[2]);
		addVertexNormal(vert[3]);
		addVertexNormal(vert[8]);
		addVertexNormal(vert[9]);
		addVertexNormal(vert[10]);
		addVertexNormal(vert[11]);

		// front
		addVertexNormal(vert[1]);
		addVertexNormal(vert[0]);
		addVertexNormal(vert[5]);
		addVertexNormal(vert[4]);
		addVertexNormal(vert[13]);
		addVertexNormal(vert[12]);
		addVertexNormal(vert[15]);
		addVertexNormal(vert[14]);

		// right
		addVertexNormal(vert[3]);
		addVertexNormal(vert[1]);
		addVertexNormal(vert[7]);
		addVertexNormal(vert[5]);
		addVertexNormal(vert[17]);
		addVertexNormal(vert[16]);
		addVertexNormal(vert[19]);
		addVertexNormal(vert[18]);

		// back
		addVertexNormal(vert[2]);
		addVertexNormal(vert[3]);
		addVertexNormal(vert[6]);
		addVertexNormal(vert[7]);
		addVertexNormal(vert[20]);
		addVertexNormal(vert[21]);
		addVertexNormal(vert[22]);
		addVertexNormal(vert[23]);

		// left
		addVertexNormal(vert[0]);
		addVertexNormal(vert[2]);
		addVertexNormal(vert[4]);
		addVertexNormal(vert[6]);
		addVertexNormal(vert[24]);
		addVertexNormal(vert[25]);
		addVertexNormal(vert[26]);
		addVertexNormal(vert[27]);

		// top
		addVertexNormal(vert[5]);
		addVertexNormal(vert[4]);
		addVertexNormal(vert[7]);
		addVertexNormal(vert[6]);
		addVertexNormal(vert[29]);
		addVertexNormal(vert[28]);
		addVertexNormal(vert[31]);
		addVertexNormal(vert[30]);
		
		final double[][] ratio = new double[][] {
			{ 0.5f * _myBorder.x / (_myExtent.x + _mySlope.x), 0.5f * _myBorder.z / (_myExtent.z + _mySlope.z) },
			{ 0.5f * _myBorder.x / (_myExtent.x + _mySlope.x), 0.5f * _myBorder.y / (_myExtent.y + _mySlope.y) },
			{ 0.5f * _myBorder.z / (_myExtent.z + _mySlope.z), 0.5f * _myBorder.y / (_myExtent.y + _mySlope.y) },
			{ 0.5f * _myBorder.x / (_myExtent.x + _mySlope.x), 0.5f * _myBorder.y / (_myExtent.y + _mySlope.y) },
			{ 0.5f * _myBorder.z / (_myExtent.z + _mySlope.z), 0.5f * _myBorder.y / (_myExtent.y + _mySlope.y) },
			{ 0.5f * _myBorder.x / (_myExtent.x + _mySlope.x), 0.5f * _myBorder.z / (_myExtent.z + _mySlope.z) }
		};

		for (int i = 0; i < 6; i++) {
			_myGeometryData.textureCoords(0).add(1, 0);
			_myGeometryData.textureCoords(0).add(0, 0);
			_myGeometryData.textureCoords(0).add(1, 1);
			_myGeometryData.textureCoords(0).add(0, 1);
			_myGeometryData.textureCoords(0).add(1 - ratio[i][0], 0 + ratio[i][1]);
			_myGeometryData.textureCoords(0).add(0 + ratio[i][0], 0 + ratio[i][1]);
			_myGeometryData.textureCoords(0).add(1 - ratio[i][0], 1 - ratio[i][1]);
			_myGeometryData.textureCoords(0).add(0 + ratio[i][0], 1 - ratio[i][1]);
		}
	}

	@Override
	protected void setIndexData() {
		final int[] data = new int[] { 0, 4, 1, 1, 4, 5, 1, 5, 3, 3, 5, 7, 3, 7, 2, 2, 7, 6, 2, 6, 0, 0, 6, 4, 4, 6, 5, 5, 6, 7 };
		for (int i = 0; i < 6; i++) {
			for (int n = 0; n < 30; n++) {
				_myGeometryData.indices().add(30 * i + n, 8 * i + data[n]);
			}
		}
	}

	public CCVector3[] computeVertices() {
		return new CCVector3[] {
			// Cube
			new CCVector3(-_myExtent.x, -_myExtent.y,  _myExtent.z), // 0
			new CCVector3( _myExtent.x, -_myExtent.y,  _myExtent.z), // 1
			new CCVector3(-_myExtent.x, -_myExtent.y, -_myExtent.z), // 2
			new CCVector3( _myExtent.x, -_myExtent.y, -_myExtent.z), // 3
			new CCVector3(-_myExtent.x,  _myExtent.y,  _myExtent.z), // 4
			new CCVector3( _myExtent.x,  _myExtent.y,  _myExtent.z), // 5
			new CCVector3(-_myExtent.x,  _myExtent.y, -_myExtent.z), // 6
			new CCVector3( _myExtent.x,  _myExtent.y, -_myExtent.z), // 7
			
			// bottom
			new CCVector3(-_myExtent.x + _myBorder.x, -_myExtent.y - _mySlope.y, _myExtent.z - _myBorder.z), // 8
																										// (0)
			new CCVector3(_myExtent.x - _myBorder.x, -_myExtent.y - _mySlope.y, _myExtent.z - _myBorder.z), // 9
			// (
			// 1
			// )
			new CCVector3(-_myExtent.x + _myBorder.x, -_myExtent.y - _mySlope.y, -_myExtent.z + _myBorder.z), // 10
																										// (2)
			new CCVector3(_myExtent.x - _myBorder.x, -_myExtent.y - _mySlope.y, -_myExtent.z + _myBorder.z), // 11
																										// (3)
			// front
			new CCVector3(-_myExtent.x + _myBorder.x, -_myExtent.y + _myBorder.y, _myExtent.z + _mySlope.z), // 12
																										// (0)
			new CCVector3(_myExtent.x - _myBorder.x, -_myExtent.y + _myBorder.y, _myExtent.z + _mySlope.z), // 13
			// (
			// 1
			// )
			new CCVector3(-_myExtent.x + _myBorder.x, _myExtent.y - _myBorder.y, _myExtent.z + _mySlope.z), // 14
			// (
			// 4
			// )
			new CCVector3(_myExtent.x - _myBorder.x, _myExtent.y - _myBorder.y, _myExtent.z + _mySlope.z), // 15
			// (
			// 5
			// )
			
			// right
			new CCVector3(_myExtent.x + _mySlope.x, -_myExtent.y + _myBorder.y, _myExtent.z - _myBorder.z), // 16
			// (
			// 1
			// )
			new CCVector3(_myExtent.x + _mySlope.x, -_myExtent.y + _myBorder.y, -_myExtent.z + _myBorder.z), // 17
																										// (3)
			new CCVector3(_myExtent.x + _mySlope.x, _myExtent.y - _myBorder.y, _myExtent.z - _myBorder.z), // 18
			// (
			// 5
			// )
			new CCVector3(_myExtent.x + _mySlope.x, _myExtent.y - _myBorder.y, -_myExtent.z + _myBorder.z), // 19
			// (
			// 7
			// )
			
			// back
			new CCVector3(-_myExtent.x + _myBorder.x, -_myExtent.y + _myBorder.y, -_myExtent.z - _mySlope.z), // 20
																										// (2)
			new CCVector3(_myExtent.x - _myBorder.x, -_myExtent.y + _myBorder.y, -_myExtent.z - _mySlope.z), // 21
																										// (3)
			new CCVector3(-_myExtent.x + _myBorder.x, _myExtent.y - _myBorder.y, -_myExtent.z - _mySlope.z), // 22
																										// (6)
			new CCVector3(_myExtent.x - _myBorder.x, _myExtent.y - _myBorder.y, -_myExtent.z - _mySlope.z), // 23
			// (
			// 7
			// )
			// left
			new CCVector3(-_myExtent.x - _mySlope.x, -_myExtent.y + _myBorder.y, _myExtent.z - _myBorder.z), // 24
																										// (0)
			new CCVector3(-_myExtent.x - _mySlope.x, -_myExtent.y + _myBorder.y, -_myExtent.z + _myBorder.z), // 25
																										// (2)
			new CCVector3(-_myExtent.x - _mySlope.x, _myExtent.y - _myBorder.y, _myExtent.z - _myBorder.z), // 26
			// (
			// 4
			// )
			new CCVector3(-_myExtent.x - _mySlope.x, _myExtent.y - _myBorder.y, -_myExtent.z + _myBorder.z), // 27
																										// (6)
			// top
			new CCVector3(-_myExtent.x + _myBorder.x, _myExtent.y + _mySlope.y, _myExtent.z - _myBorder.z), // 28
			// (
			// 4
			// )
			new CCVector3(_myExtent.x - _myBorder.x, _myExtent.y + _mySlope.y, _myExtent.z - _myBorder.z), // 29
			// (
			// 5
			// )
			new CCVector3(-_myExtent.x + _myBorder.x, _myExtent.y + _mySlope.y, -_myExtent.z + _myBorder.z), // 30
																										// (6)
			new CCVector3(_myExtent.x - _myBorder.x, _myExtent.y + _mySlope.y, -_myExtent.z + _myBorder.z), // 31
			// (
			// 7
			// )
		};
	}

	/**
	 * <code>clone</code> creates a new RoundedBox object containing the same
	 * data as this one.
	 * 
	 * @return the new Box
	 */
	@Override
	public CCRoundedBox clone() {
		return new CCRoundedBox(_myExtent.clone(), _myBorder.clone(), _mySlope.clone());
	}
}