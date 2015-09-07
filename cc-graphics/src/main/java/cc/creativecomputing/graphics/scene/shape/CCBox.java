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
 * <code>Box</code> is an axis-aligned rectangular prism defined by a center
 * point and x, y, and z extents from that center (essentially radii.)
 */
public class CCBox extends CCPrimitive {

	private final CCVector3 _myExtent = new CCVector3(0, 0, 0);
	private final CCVector3 _myCenter = new CCVector3(0, 0, 0);

	/**
	 * Constructs a new <code>Box</code> object using the given center and
	 * extents. Since the extents represent the distance from the center of the
	 * box to the edge, the full length of a side is actually 2 * extent.
	 * 
	 * @param theCenter
	 *            Center of the box.
	 * @param xExtent
	 *            x extent of the box
	 * @param yExtent
	 *            y extent of the box
	 * @param zExtent
	 *            z extent of the box
	 */
	public CCBox(final CCVector3 theCenter, final double xExtent, final double yExtent, final double zExtent) {
		_myCenter.set(theCenter);

		_myExtent.x = xExtent;
		_myExtent.y = yExtent;
		_myExtent.z = zExtent;
		
		allocate(24, 36);
		createGeometry();
	}
	
	/**
	 * Constructs a new <code>Box</code> object using the given 
	 * extents. Since the extents represent the distance from the center of the
	 * box to the edge, the full length of a side is actually 2 * extent.
	 * 
	 * 
	 * @param xExtent
	 *            x extent of the box
	 * @param yExtent
	 *            y extent of the box
	 * @param zExtent
	 *            z extent of the box
	 */
	public CCBox(final double xExtent, final double yExtent, final double zExtent) {
		this(CCVector3.ZERO, xExtent, yExtent, zExtent);
	}
	
	public CCBox(final double theExtent) {
		this(theExtent,theExtent,theExtent);
	}
	
	/**
	 * Constructs a new 1x1x1 <code>Box</code> .
	 */
	public CCBox() {
		this(0.5f, 0.5f, 0.5f);
	}

	/**
	 * Constructs a new <code>Box</code> object using the given two points as
	 * opposite corners of the box. These two points may be in any order.
	 * 
	 * @param thePointA
	 *            the first point
	 * @param thePointB
	 *            the second point.
	 */
	public CCBox(final CCVector3 thePointA, final CCVector3 thePointB) {
		_myCenter.set(thePointB).addLocal(thePointA).multiplyLocal(0.5f);
		_myExtent.set(
			CCMath.abs(thePointB.x - _myCenter.x),
			CCMath.abs(thePointB.y - _myCenter.y),
			CCMath.abs(thePointB.z - _myCenter.z)
		);
	}

	/**
	 * @return the current center of this box.
	 */
	public CCVector3 center() {
		return _myCenter;
	}

	/**
	 * @return the current X extent of this box.
	 */
	public CCVector3 extent() {
		return _myExtent;
	}

	/**
	 * <code>setVertexData</code> sets the vertex positions that define the box
	 * using the center point and defined extents.
	 */
	@Override
	protected void setGeometryData() {

		final CCVector3[] vert = new CCVector3[]{
			_myCenter.add(-_myExtent.x, -_myExtent.y, -_myExtent.z),
			_myCenter.add( _myExtent.x, -_myExtent.y, -_myExtent.z),
			_myCenter.add( _myExtent.x,  _myExtent.y, -_myExtent.z),
			_myCenter.add(-_myExtent.x,  _myExtent.y, -_myExtent.z),
			_myCenter.add( _myExtent.x, -_myExtent.y,  _myExtent.z),
			_myCenter.add(-_myExtent.x, -_myExtent.y,  _myExtent.z),
			_myCenter.add( _myExtent.x,  _myExtent.y,  _myExtent.z),
			_myCenter.add(-_myExtent.x,  _myExtent.y,  _myExtent.z)
		};

		// Back
		_myGeometryData.vertices().add(vert[0]);
		_myGeometryData.vertices().add(vert[1]);
		_myGeometryData.vertices().add(vert[2]);
		_myGeometryData.vertices().add(vert[3]);
		
		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(0, 0, -1);
		}

		// Right
		_myGeometryData.vertices().add(vert[1]);
		_myGeometryData.vertices().add(vert[4]);
		_myGeometryData.vertices().add(vert[6]);
		_myGeometryData.vertices().add(vert[2]);

		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(1, 0, 0);
		}

		// Front
		_myGeometryData.vertices().add(vert[4]);
		_myGeometryData.vertices().add(vert[5]);
		_myGeometryData.vertices().add(vert[7]);
		_myGeometryData.vertices().add(vert[6]);
		
		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(0, 0, 1);
		}

		// Left
		_myGeometryData.vertices().add(vert[5]);
		_myGeometryData.vertices().add(vert[0]);
		_myGeometryData.vertices().add(vert[3]);
		_myGeometryData.vertices().add(vert[7]);

		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(-1, 0, 0);
		}

		// Top
		_myGeometryData.vertices().add(vert[2]);
		_myGeometryData.vertices().add(vert[6]);
		_myGeometryData.vertices().add(vert[7]);
		_myGeometryData.vertices().add(vert[3]);

		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(0, 1, 0);
		}

		// Bottom
		_myGeometryData.vertices().add(vert[0]);
		_myGeometryData.vertices().add(vert[5]);
		_myGeometryData.vertices().add(vert[4]);
		_myGeometryData.vertices().add(vert[1]);

		for (int i = 0; i < 4; i++) {
			_myGeometryData.normals().add(0, -1, 0);
		}
		
		for (int i = 0; i < 6; i++) {
			_myGeometryData.textureCoords(0).add(1, 0);
			_myGeometryData.textureCoords(0).add(0, 0);
			_myGeometryData.textureCoords(0).add(0, 1);
			_myGeometryData.textureCoords(0).add(1, 1);
		}
	}

	/**
	 * <code>setIndexData</code> sets the indices into the list of vertices,
	 * defining all triangles that constitute the box.
	 */
	protected void setIndexData() {
		final int[] indices = {
			 2,  1,  0,  3, 
			 2,  0,  6,  5, 
			 4,  7,  6,  4, 
			10,  9,  8, 11, 
			10,  8, 14, 13, 
			12, 15, 14, 12, 
			18, 17, 16, 19, 
			18, 16, 22, 21, 
			20, 23, 22, 20 
		};
		for(int index:indices){
			_myGeometryData.indices().add(index);
		}
	}

	/**
	 * <code>clone</code> creates a new Box object containing the same data as
	 * this one.
	 * 
	 * @return the new Box
	 */
	@Override
	public CCBox clone() {
		return new CCBox(_myCenter.clone(), _myExtent.x, _myExtent.y, _myExtent.z);
	}
}