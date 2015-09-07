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

import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.math.CCMath;


/**
 * <code>Quad</code> defines a four sided, two dimensional shape. The local
 * height of the <code>Quad</code> defines it's size about the y-axis, while the
 * width defines the x-axis. The z-axis will always be 0.
 */
public class CCPointGrid extends CCPrimitive {

	
	private final float _myX;
	private final float _myY;
	private final float _myWidth;
	private final float _myHeight;
	
	protected int _myXRes;
	protected int _myYRes;
	


	/**
	 * Constructor creates a new <code>PointGrid</code> object with the provided
	 * parameters.
	 * 
	 * @param theXRes
	 *            the width of the <code>Quad</code>.
	 * @param theYRes
	 *            the height of the <code>Quad</code>.
	 */
	public CCPointGrid(final float theX, final float theY, final float theWidth, final float theHeight,final int theXRes, final int theYRes) {
		super();
		
		_myX = theX;
		_myY = theY;
		
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myXRes = theXRes;
		_myYRes = theYRes;
		
		final int verts = _myXRes * _myYRes;
		final int myNumberOfIndices = _myXRes * _myYRes;
		allocate(GLDrawMode.POINTS,verts, myNumberOfIndices);
		createGeometry();
	}

	/**
	 * Constructor creates a new <code>Quad</code> object.
	 * 
	 * @param name
	 *            the name of this <code>Quad</code>.
	 */
	public CCPointGrid(int theXRes, int theYRes) {
		this(0,0,1, 1, theXRes, theYRes);
	}
	
	@Override
	protected void setGeometryData() {
		float myX0 = _myX;
		float myX1 = _myX + _myWidth;

		float myY0 = _myY;
		float myY1 = _myY + _myHeight;
		
		for(int x = 0; x < _myXRes;x++){
			float myX = CCMath.map(x, 0, _myXRes - 1, myX0, myX1);
			float myTexX = (x + 0.5f) / _myXRes;
			for(int y = 0; y < _myYRes;y++){
				float myY = CCMath.map(y, 0, _myYRes - 1, myY0, myY1);
				float myTexY = (y + 0.5f) / _myYRes;
				_myGeometryData.vertices().add(myX,myY, 0);
				_myGeometryData.normals().add(0, 0, 1);
				_myGeometryData.textureCoords(0).add(myTexX, myTexY);
			}
		}
	}
	
	@Override
	protected void setIndexData() {
		int i = 0;
		for(int x = 0; x < _myXRes;x++){
			for(int y = 0; y < _myYRes;y++){
				_myGeometryData.indices().add(i++);
			}
		}
	}

	public float width() {
		return _myXRes;
	}

	public float height() {
		return _myYRes;
	}
}