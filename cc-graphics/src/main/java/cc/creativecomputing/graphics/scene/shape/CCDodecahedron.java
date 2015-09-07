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

import cc.creativecomputing.data.CCIndexBufferData;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCDodecahedron extends CCPrimitive {

    private static final int NUMBER_OF_VERTICES = 20;
    private static final int NUM_TRIS = 36;

    private float _mySideLength;

    /**
     * Creates an Dodecahedron (think of 12-sided dice) with center at the origin. The length of the sides will be as
     * specified in sideLength.
     * 
     * @param name
     *            The name of the octahedron.
     * @param sideLength
     *            The length of each side of the octahedron.
     */
    public CCDodecahedron(final float sideLength) {
        _mySideLength = sideLength;
        
        allocate(NUMBER_OF_VERTICES, 3 * NUM_TRIS);
        createGeometry();

    }

    @Override
    protected void setIndexData() {
        final CCIndexBufferData<?> indices = _myGeometryData.indices();
        indices.rewind();
        indices.add(0).add(8).add(9);
        indices.add(0).add(9).add(4);
        indices.add(0).add(4).add(16);
        indices.add(0).add(12).add(13);
        indices.add(0).add(13).add(1);
        indices.add(0).add(1).add(8);
        indices.add(0).add(16).add(17);
        indices.add(0).add(17).add(2);
        indices.add(0).add(2).add(12);
        indices.add(8).add(1).add(18);
        indices.add(8).add(18).add(5);
        indices.add(8).add(5).add(9);
        indices.add(12).add(2).add(10);
        indices.add(12).add(10).add(3);
        indices.add(12).add(3).add(13);
        indices.add(16).add(4).add(14);
        indices.add(16).add(14).add(6);
        indices.add(16).add(6).add(17);
        indices.add(9).add(5).add(15);
        indices.add(9).add(15).add(14);
        indices.add(9).add(14).add(4);
        indices.add(6).add(11).add(10);
        indices.add(6).add(10).add(2);
        indices.add(6).add(2).add(17);
        indices.add(3).add(19).add(18);
        indices.add(3).add(18).add(1);
        indices.add(3).add(1).add(13);
        indices.add(7).add(15).add(5);
        indices.add(7).add(5).add(18);
        indices.add(7).add(18).add(19);
        indices.add(7).add(11).add(6);
        indices.add(7).add(6).add(14);
        indices.add(7).add(14).add(15);
        indices.add(7).add(19).add(3);
        indices.add(7).add(3).add(10);
        indices.add(7).add(10).add(11);
    }
    
    @Override
    protected void setGeometryData() {
    	float fA = 1.0f / CCMath.sqrt(3.0f);
        float fB = CCMath.sqrt((3.0f - CCMath.sqrt(5.0f)) / 6.0f);
        float fC = CCMath.sqrt((3.0f + CCMath.sqrt(5.0f)) / 6.0f);
        fA *= _mySideLength;
        fB *= _mySideLength;
        fC *= _mySideLength;
        
    	CCVector3[] myVertices = new CCVector3[]{
    		new CCVector3(  fA,  fA,   fA),
    		new CCVector3(  fA,  fA,  -fA),
    		new CCVector3(  fA, -fA,   fA),
    		new CCVector3(  fA, -fA,  -fA),
    		new CCVector3( -fA,  fA,   fA),
    		new CCVector3( -fA,  fA,  -fA),
    		new CCVector3( -fA, -fA,   fA),
    		new CCVector3( -fA, -fA,  -fA),
    		new CCVector3(  fB,  fC, 0.0f),
    		new CCVector3( -fB,  fC, 0.0f),
    		new CCVector3(  fB, -fC, 0.0f),
    		new CCVector3( -fB, -fC, 0.0f),
    		new CCVector3(  fC, 0.0f,  fB),
    		new CCVector3(  fC, 0.0f, -fB),
    		new CCVector3( -fC, 0.0f,  fB),
    		new CCVector3( -fC, 0.0f, -fB),
    		new CCVector3( 0.0f,  fB,  fC),
    		new CCVector3( 0.0f, -fB,  fC),
    		new CCVector3( 0.0f,  fB, -fC),
    		new CCVector3( 0.0f, -fB, -fC)	
    	};
    	
        final CCVector2 tex = new CCVector2();
    	
    	for(CCVector3 myVertex:myVertices){
    		_myGeometryData.vertices().add(myVertex);
    		_myGeometryData.normals().add(myVertex.normalize());
    		
    		if (CCMath.abs(myVertex.z) < _mySideLength) {
                tex.x = 0.5f * (1.0f + CCMath.atan2(myVertex.y, myVertex.x) / CCMath.PI);
            } else {
                tex.x = 0.5f;
            }
            tex.y = CCMath.acos(myVertex.z / _mySideLength) / CCMath.PI;
            _myGeometryData.textureCoords(0).add(tex.x, tex.y);
    	}
    }

}
