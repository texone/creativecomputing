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
 * A regular hexagon with each triangle having side length that is given in the constructor.
 */
public class CCHexagon extends CCPrimitive {

    private static final int NUM_POINTS = 7;

    private static final int NUM_TRIS = 6;

    private float _mySideLength;

    /**
     * Hexagon Constructor instantiates a new Hexagon. This element is center on 0,0,0 with all normals pointing up. The
     * user must move and rotate for positioning.
     * 
     * @param theSideLength
     *            The length of all the sides of the triangles
     */
    public CCHexagon(final float theSideLength) {
        _mySideLength = theSideLength;
        
        allocate(NUM_POINTS, 3 * NUM_TRIS);
        createGeometry();
    }

    /**
     * Vertices are set up like this: 0__1 / \ / \ 5/__\6/__\2 \ / \ / \ /___\ / 4 3 All lines on this diagram are
     * sideLength long. Therefore, the width of the hexagon is sideLength * 2, and the height is 2 * the height of one
     * equilateral triangle with all side = sideLength which is .866
     */
    @Override
    protected void setGeometryData() {
        _myGeometryData.vertices().add(-_mySideLength / 2, _mySideLength * 0.866f, 0.0f);
        _myGeometryData.vertices().add(_mySideLength / 2, _mySideLength * 0.866f, 0.0f);
        _myGeometryData.vertices().add(_mySideLength, 0.0f, 0.0f);
        _myGeometryData.vertices().add(_mySideLength / 2, -_mySideLength * 0.866f, 0.0f);
        _myGeometryData.vertices().add(-_mySideLength / 2, -_mySideLength * 0.866f, 0.0f);
        _myGeometryData.vertices().add(-_mySideLength, 0.0f, 0.0f);
        _myGeometryData.vertices().add(0.0f, 0.0f, 0.0f);
        
        _myGeometryData.textureCoords(0).add(0.25f, 0);
        _myGeometryData.textureCoords(0).add(0.75f, 0);
        _myGeometryData.textureCoords(0).add(1.0f, 0.5f);
        _myGeometryData.textureCoords(0).add(0.75f, 1.0f);
        _myGeometryData.textureCoords(0).add(0.25f, 1.0f);
        _myGeometryData.textureCoords(0).add(0.0f, 0.5f);
        _myGeometryData.textureCoords(0).add(0.5f, 0.5f);
        
        final CCVector3 zAxis = new CCVector3(0, 0, 1);
        for (int i = 0; i < NUM_POINTS; i++) {
        	_myGeometryData.normals().add(zAxis);
        }
    }

    /**
     * Sets up the indexes of the mesh. These go in a clockwise fashion and thus only the 'up' side of the hex is lit
     * properly. If you wish to have to either set two sided lighting or create two hexes back-to-back
     */
    @Override
    protected void setIndexData() {
        // tri 1
        _myGeometryData.indices().add(0);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(1);
        // tri 2
        _myGeometryData.indices().add(1);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(2);
        // tri 3
        _myGeometryData.indices().add(2);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(3);
        // tri 4
        _myGeometryData.indices().add(3);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(4);
        // tri 5
        _myGeometryData.indices().add(4);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(5);
        // tri 6
        _myGeometryData.indices().add(5);
        _myGeometryData.indices().add(6);
        _myGeometryData.indices().add(0);
    }
}
