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

import static org.junit.Assert.*;

import org.junit.Test;

public class CCRay3Test {
    @Test
    public void testData() {
        final CCRay3 ray = new CCRay3();
        assertEquals(CCVector3.UNIT_Z, ray.getDirection());
        assertEquals(CCVector3.ZERO, ray.getOrigin());

        ray.setDirection(CCVector3.NEG_UNIT_X);
        assertEquals(CCVector3.NEG_UNIT_X, ray.getDirection());
        ray.setOrigin(CCVector3.ONE);
        assertEquals(CCVector3.ONE, ray.getOrigin());

        final CCRay3 ray2 = new CCRay3(ray);
        assertEquals(CCVector3.NEG_UNIT_X, ray2.getDirection());
        assertEquals(CCVector3.ONE, ray2.getOrigin());

        ray.set(new CCRay3());
        assertEquals(CCVector3.UNIT_Z, ray.getDirection());
        assertEquals(CCVector3.ZERO, ray.getOrigin());
    }

    @Test
    public void testValid() {
        final CCRay3 ray1 = new CCRay3(new CCVector3(0, 0, 0), new CCVector3(0, 0, 1));
        final CCRay3 ray2 = new CCRay3(new CCVector3(Float.POSITIVE_INFINITY, 0, 0), new CCVector3(0, 0, 1));
        final CCRay3 ray3 = new CCRay3(new CCVector3(0, 0, 0), new CCVector3(Float.POSITIVE_INFINITY, 0, 1));

        assertTrue(CCRay3.isValid(ray1));
        assertFalse(CCRay3.isValid(ray2));
        assertFalse(CCRay3.isValid(ray3));

        assertFalse(CCRay3.isValid(null));

        // couple if equals validity tests
        assertEquals(ray1, ray1);
        assertFalse(ray1.equals(null));
        assertFalse(ray1.equals(new CCVector3()));

        // cover more of equals
        assertFalse(ray1.equals(new CCRay3(CCVector3.ZERO, CCVector3.NEG_UNIT_X)));
    }

    @Test
    public void testClone() {
        final CCRay3 ray1 = new CCRay3();
        final CCRay3 ray2 = ray1.clone();
        assertEquals(ray1, ray2);
        assertNotSame(ray1, ray2);
    }

    @Test
    public void testDistance() {
        final CCRay3 ray1 = new CCRay3();
        assertTrue(25.0 == ray1.distanceSquared(new CCVector3(0, 5, 3), null));

        final CCVector3 store = new CCVector3();
        assertTrue(9.0 == ray1.distanceSquared(new CCVector3(0, 3, 3), store));
        assertEquals(new CCVector3(0, 0, 3), store);
        assertTrue(18.0 == ray1.distanceSquared(new CCVector3(0, 3, -3), store));
        assertEquals(new CCVector3(0, 0, 0), store);
    }

    @Test
    public void testIntersectsTriangle() {
        final CCVector3 v0 = new CCVector3(-1, -1, -1);
        final CCVector3 v1 = new CCVector3(+1, -1, -1);
        final CCVector3 v2 = new CCVector3(+1, +1, -1);

        final CCVector3 intersectionPoint = new CCVector3();

        // inside triangle
        CCRay3 pickRay = new CCRay3(new CCVector3(0.5f, -0.5f, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // horizontal edge
        pickRay = new CCRay3(new CCVector3(0, -1, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // diagonal edge
        pickRay = new CCRay3(new CCVector3(0, 0, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // vertical edge
        pickRay = new CCRay3(new CCVector3(+1, 0, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // v0
        pickRay = new CCRay3(new CCVector3(-1, -1, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // v1
        pickRay = new CCRay3(new CCVector3(+1, -1, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // v2
        pickRay = new CCRay3(new CCVector3(1, 1, 3), new CCVector3(0, 0, -1));
        assertTrue(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // outside horizontal edge
        pickRay = new CCRay3(new CCVector3(0f, -1.1f, 3), new CCVector3(0, 0, -1));
        assertFalse(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // outside diagonal edge
        pickRay = new CCRay3(new CCVector3(-0.1f, 0.1f, 3), new CCVector3(0, 0, -1));
        assertFalse(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // outside vertical edge
        pickRay = new CCRay3(new CCVector3(+1.1f, 0, 3), new CCVector3(0, 0, -1));
        assertFalse(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // inside triangle but ray pointing other way
        pickRay = new CCRay3(new CCVector3(-0.5f, -0.5f, 3), new CCVector3(0, 0, +1));
        assertFalse(pickRay.intersectsTriangle(v0, v1, v2, intersectionPoint));

        // test distance
        pickRay = new CCRay3(new CCVector3(0.5f, -0.5f, 3), new CCVector3(0, 0, -1));
        assertTrue(4.0 == pickRay.getDistanceToPrimitive(new CCVector3[] { v0, v1, v2 }));

        // test intersect planar
        assertTrue(pickRay.intersectsTrianglePlanar(v0, v1, v2, intersectionPoint));

    }

    @Test
    public void testIntersectsPlane() {
        final CCVector3 intersectionPoint = new CCVector3();

        CCPlane plane = new CCPlane(new CCVector3(0, 1, 0), 2);

        CCRay3 pickRay = new CCRay3(new CCVector3(0, 3, 0), new CCVector3(0, 0, 1));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, 3, 0), new CCVector3(0, 1, 0));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, 2, 0), new CCVector3(0, 1, 0));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, 1, 0), new CCVector3(0, 1, 0));
        assertNotNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, 0, 0), new CCVector3(1, 0, 0));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -3, 0), new CCVector3(0, 0, 1));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, 3, 0), new CCVector3(0, -1, 0));
        assertNotNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -3, 0), new CCVector3(1, 1, 1));
        assertNotNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -3, 0), new CCVector3(-1, -1, -1));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        plane = new CCPlane(new CCVector3(1, 1, 1), -2);

        pickRay = new CCRay3(new CCVector3(0, 0, 0), new CCVector3(1, -1, 1));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -1, 0), new CCVector3(0, 1, 0));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -2, 0), new CCVector3(0, 1, 0));
        assertNull(pickRay.intersectsPlane(plane, intersectionPoint));

        pickRay = new CCRay3(new CCVector3(0, -3, 0), new CCVector3(0, 1, 0));
        assertNotNull(pickRay.intersectsPlane(plane, null));
    }

    @Test
    public void testIntersectsQuad() {
        final CCVector3 v0 = new CCVector3(0, 0, 0);
        final CCVector3 v1 = new CCVector3(5, 0, 0);
        final CCVector3 v2 = new CCVector3(5, 5, 0);
        final CCVector3 v3 = new CCVector3(0, 5, 0);

        CCVector3 intersectionPoint = null;

        // inside quad
        final CCRay3 pickRayA = new CCRay3(new CCVector3(2, 2, 10), new CCVector3(0, 0, -1));
        final CCRay3 pickRayB = new CCRay3(new CCVector3(2, 4, 10), new CCVector3(0, 0, -1));
        assertTrue(pickRayA.intersectsQuad(v0, v1, v2, v3, intersectionPoint));
        assertTrue(pickRayB.intersectsQuad(v0, v1, v2, v3, intersectionPoint));

        // inside quad
        final CCRay3 pickRay2 = new CCRay3(new CCVector3(-1, 0, 10), new CCVector3(0, 0, -1));
        assertFalse(pickRay2.intersectsQuad(v0, v1, v2, v3, intersectionPoint));

        // test distance
        assertTrue(10.0 == pickRayA.getDistanceToPrimitive(new CCVector3[] { v0, v1, v2, v3 }));
        assertTrue(Float.POSITIVE_INFINITY == pickRay2.getDistanceToPrimitive(new CCVector3[] { v0, v1, v2, v3 }));

        // test unsupported pick
        assertFalse(pickRay2.intersects(new CCVector3[] { v0, v1 }, null));

        // test intersect planar
        assertFalse(new CCRay3(new CCVector3(0, 0, -1), CCVector3.UNIT_Y).intersectsQuadPlanar(v0, v1, v2, v3,
                intersectionPoint));
        intersectionPoint = new CCVector3();
        assertTrue(pickRayA.intersectsQuadPlanar(v0, v1, v2, v3, intersectionPoint));
        assertTrue(pickRayB.intersectsQuadPlanar(v0, v1, v2, v3, intersectionPoint));
    }
}
