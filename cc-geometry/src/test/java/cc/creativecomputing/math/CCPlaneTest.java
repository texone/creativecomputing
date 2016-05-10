/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cc.creativecomputing.math.CCPlane.Side;

public class CCPlaneTest {

    @Test
    public void testGetSet() {
        final CCPlane plane = new CCPlane();
        assertEquals(CCVector3.UNIT_Y, plane.normal());
        assertTrue(plane.constant() == 0.0);

        plane.setNormal(CCVector3.UNIT_X);
        plane.setConstant(1.0f);
        assertEquals(CCVector3.UNIT_X, plane.normal());
        assertTrue(plane.constant() == 1.0);

        final CCPlane plane2 = new CCPlane(plane);
        assertEquals(CCVector3.UNIT_X, plane2.normal());
        assertTrue(plane.constant() == 1.0);

        final CCPlane plane3 = new CCPlane(CCVector3.NEG_UNIT_Z, 2.5f);
        assertEquals(CCVector3.NEG_UNIT_Z, plane3.normal());
        assertTrue(plane3.constant() == 2.5);

        final CCPlane plane4 = new CCPlane().setPlanePoints(new CCVector3(1, 1, 1), new CCVector3(2, 1, 1),
                new CCVector3(2, 2, 1));
        assertEquals(CCVector3.UNIT_Z, plane4.normal());
        assertTrue(plane4.constant() == 1.0);
    }

    @Test
    public void testEquals() {
        // couple of equals validity tests
        final CCPlane plane1 = new CCPlane();
        assertEquals(plane1, plane1);
        assertFalse(plane1.equals(null));
        assertFalse(plane1.equals(new CCVector2()));

        // cover more of equals
        assertFalse(plane1.equals(new CCPlane(CCVector3.UNIT_X, 0)));
    }

    @Test
    public void testSimpleHash() {
        // Just a simple sanity check.
        final CCPlane plane1 = new CCPlane(CCVector3.UNIT_Y, 2);
        final CCPlane plane2 = new CCPlane(CCVector3.UNIT_Y, 2);
        final CCPlane plane3 = new CCPlane(CCVector3.UNIT_Z, 2);

//        assertTrue(plane1.hashCode() == plane2.hashCode());
//        assertTrue(plane1.hashCode() != plane3.hashCode());
    }

    @Test
    public void testClone() {
        final CCPlane plane1 = new CCPlane();
        final CCPlane plane2 = plane1.clone();
        assertEquals(plane1, plane2);
        assertNotSame(plane1, plane2);
    }

    @Test
    public void testValid() {
        final CCPlane plane1 = new CCPlane();
        final CCPlane plane2 = new CCPlane(new CCVector3(Float.NaN, 0, 0), 0.5f);
        final CCPlane plane3 = new CCPlane(CCVector3.UNIT_X, Float.NaN);
        final CCPlane plane4 = new CCPlane(CCVector3.UNIT_X, Float.POSITIVE_INFINITY);

        assertTrue(CCPlane.isValid(plane1));
        assertFalse(CCPlane.isValid(plane2));
        assertFalse(CCPlane.isValid(plane3));
        assertFalse(CCPlane.isValid(plane4));

        plane4.setConstant(1);
        assertTrue(CCPlane.isValid(plane4));

        assertFalse(CCPlane.isValid(null));
    }

    @Test
    public void testDistance() {
        final CCPlane plane1 = new CCPlane(CCVector3.UNIT_Y, 1.0f);
        final CCVector3 point = new CCVector3(0, 5, 0);
        assertTrue(4.0 == plane1.pseudoDistance(point));
        assertEquals(Side.Outside, plane1.whichSide(point));

        point.set(0, -4, 0);
        assertTrue(-5.0 == plane1.pseudoDistance(point));
        assertEquals(Side.Inside, plane1.whichSide(point));

        point.set(1, 1, 1);
        assertTrue(0.0 == plane1.pseudoDistance(point));
        assertEquals(Side.Neither, plane1.whichSide(point));
    }

    @Test
    public void testReflect() {
        final CCPlane plane1 = new CCPlane(CCVector3.UNIT_X, 5.0f);
        assertEquals(new CCVector3(), plane1.reflectVector(new CCVector3(), new CCVector3()));
        assertEquals(new CCVector3(-1, 0, 0), plane1.reflectVector(new CCVector3(1, 0, 0), null));
        assertEquals(new CCVector3(-1, 1, 1).normalizeLocal(),
                plane1.reflectVector(new CCVector3(1, 1, 1).normalizeLocal(), null));
        assertEquals(new CCVector3(-3, 2, -1).normalizeLocal(),
                plane1.reflectVector(new CCVector3(3, 2, -1).normalizeLocal(), null));

        final CCPlane plane2 = new CCPlane(CCVector3.UNIT_Z, 1.0f);
        assertEquals(new CCVector3(), plane2.reflectVector(new CCVector3(), new CCVector3()));
        assertEquals(new CCVector3(0, 0, -1), plane2.reflectVector(new CCVector3(0, 0, 1), null));
        assertEquals(new CCVector3(1, 1, -1).normalizeLocal(),
                plane2.reflectVector(new CCVector3(1, 1, 1).normalizeLocal(), null));
        assertEquals(new CCVector3(3, 2, 1).normalizeLocal(),
                plane2.reflectVector(new CCVector3(3, 2, -1).normalizeLocal(), null));
    }
}
