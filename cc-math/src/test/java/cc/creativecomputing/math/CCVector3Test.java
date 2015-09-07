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

public class CCVector3Test {

    @Test
    public void testAdd() {
        final CCVector3 vec1 = new CCVector3();
        final CCVector3 vec2 = new CCVector3(CCVector3.ONE);

        vec1.addLocal(1, 2, 3);
        assertEquals(new CCVector3(1, 2, 3), vec1);
        vec1.addLocal(-1, -2, -3);
        assertEquals(CCVector3.ZERO, vec1);

        vec1.zero();
        vec1.addLocal(vec2);
        assertEquals(CCVector3.ONE, vec1);

        vec1.zero();
        final CCVector3 vec3 = vec1.add(vec2);
        assertEquals(CCVector3.ZERO, vec1);
        assertEquals(CCVector3.ONE, vec3);

        final CCVector3 vec4 = vec1.add(1, 0, 0);
        assertEquals(CCVector3.ZERO, vec1);
        assertEquals(CCVector3.UNIT_X, vec4);
    }

    @Test
    public void testSubtract() {
        final CCVector3 vec1 = new CCVector3();
        final CCVector3 vec2 = new CCVector3(CCVector3.ONE);

        vec1.subtractLocal(1, 2, 3);
        assertEquals(new CCVector3(-1, -2, -3), vec1);
        vec1.subtractLocal(-1, -2, -3);
        assertEquals(CCVector3.ZERO, vec1);

        vec1.zero();
        vec1.subtractLocal(vec2);
        assertEquals(CCVector3.NEG_ONE, vec1);

        vec1.zero();
        final CCVector3 vec3 = vec1.subtract(vec2);
        assertEquals(CCVector3.ZERO, vec1);
        assertEquals(CCVector3.NEG_ONE, vec3);

        final CCVector3 vec4 = vec1.subtract(1, 0, 0);
        assertEquals(CCVector3.ZERO, vec1);
        assertEquals(CCVector3.NEG_UNIT_X, vec4);
    }

    @Test
    public void testGetSet() {
        final CCVector3 vec1 = new CCVector3();
        vec1.x = 0;
        assertTrue(vec1.x == 0.0);
        vec1.x = Float.POSITIVE_INFINITY;
        assertTrue(vec1.x == Float.POSITIVE_INFINITY);
        vec1.x = Float.NEGATIVE_INFINITY;
        assertTrue(vec1.x == Float.NEGATIVE_INFINITY);
        assertTrue(vec1.getValue(0) == Float.NEGATIVE_INFINITY);

        vec1.y = 0;
        assertTrue(vec1.y == 0.0);
        vec1.y = Float.POSITIVE_INFINITY;
        assertTrue(vec1.y == Float.POSITIVE_INFINITY);
        vec1.y = Float.NEGATIVE_INFINITY;
        assertTrue(vec1.y == Float.NEGATIVE_INFINITY);
        assertTrue(vec1.getValue(1) == Float.NEGATIVE_INFINITY);

        vec1.z = 0;
        assertTrue(vec1.z == 0.0);
        vec1.z = Float.POSITIVE_INFINITY;
        assertTrue(vec1.z == Float.POSITIVE_INFINITY);
        vec1.z = Float.NEGATIVE_INFINITY;
        assertTrue(vec1.z == Float.NEGATIVE_INFINITY);
        assertTrue(vec1.getValue(2) == Float.NEGATIVE_INFINITY);

        vec1.set(CCMath.PI, CCMath.PI, CCMath.PI);
        assertTrue(vec1.x == CCMath.PI);
        assertTrue(vec1.y == CCMath.PI);
        assertTrue(vec1.z == CCMath.PI);

        final CCVector3 vec2 = new CCVector3();
        vec2.set(vec1);
        assertEquals(vec1, vec2);

        vec1.setValue(0, 0);
        vec1.setValue(1, 0);
        vec1.setValue(2, 0);
        assertEquals(CCVector3.ZERO, vec1);

        // catch a few expected exceptions
        try {
            vec2.getValue(3);
            fail("getValue(3) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        try {
            vec2.getValue(-1);
            fail("getValue(-1) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        try {
            vec2.setValue(-1, 0);
            fail("setValue(-1, 0) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        try {
            vec2.setValue(3, 0);
            fail("setValue(3, 0) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        // above exceptions shouldn't have altered vec2
        assertEquals(new CCVector3(CCMath.PI, CCMath.PI, CCMath.PI), vec2);
    }

    @Test
    public void testToArray() {
        final CCVector3 vec1 = new CCVector3();
        vec1.set(CCMath.PI, Float.MAX_VALUE, 42);
        final double[] array = vec1.toArray(null);
        final double[] array2 = vec1.toArray(new double[3]);
        assertNotNull(array);
        assertTrue(array.length == 3);
        assertTrue(array[0] == CCMath.PI);
        assertTrue(array[1] == Float.MAX_VALUE);
        assertTrue(array[2] == 42);
        assertNotNull(array2);
        assertTrue(array2.length == 3);
        assertTrue(array2[0] == CCMath.PI);
        assertTrue(array2[1] == Float.MAX_VALUE);
        assertTrue(array2[2] == 42);

        try {
            vec1.toArray(new double[1]);
            fail("toArray(d[1]) should have thrown ArrayIndexOutOfBoundsException.");
        } catch (final ArrayIndexOutOfBoundsException e) {
        }

        final double[] farray = vec1.toArray();
        final double[] farray2 = vec1.toArray(new double[3]);
        assertNotNull(farray);
        assertTrue(farray.length == 3);
        assertTrue(farray[0] == CCMath.PI);
        assertTrue(farray[1] == Float.MAX_VALUE);
        assertTrue(farray[2] == 42f);
        assertNotNull(farray2);
        assertTrue(farray2.length == 3);
        assertTrue(farray2[0] == CCMath.PI);
        assertTrue(farray2[1] == Float.MAX_VALUE);
        assertTrue(farray2[2] == 42f);

        try {
            vec1.toArray(new double[1]);
            fail("toFloatArray(d[1]) should have thrown ArrayIndexOutOfBoundsException.");
        } catch (final ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testMultiply() {
        final CCVector3 vec1 = new CCVector3(1, -1, 2);
        final CCVector3 vec2 = vec1.multiply(2.0f);
        assertEquals(new CCVector3(2.0f, -2.0f, 4.0f), vec2);

        vec2.multiplyLocal(0.5f);
        assertEquals(new CCVector3(1.0f, -1.0f, 2.0f), vec2);

        final CCVector3 vec3 = vec1.multiply(vec2);
        assertEquals(new CCVector3(1, 1, 4), vec3);

        final CCVector3 vec4 = vec1.multiply(2, 3, 2);
        assertEquals(new CCVector3(2, -3, 4), vec4);

        vec1.multiplyLocal(0.5f, 0.5f, 0.5f);
        assertEquals(new CCVector3(0.5f, -0.5f, 1.0f), vec1);

        vec1.multiplyLocal(vec2);
        assertEquals(new CCVector3(0.5f, 0.5f, 2.0f), vec1);
    }

    @Test
    public void testDivide() {
        final CCVector3 vec1 = new CCVector3(1, -1, 2);
        final CCVector3 vec2 = vec1.divide(2.0f);
        assertEquals(new CCVector3(0.5f, -0.5f, 1.0f), vec2);

        vec2.divideLocal(0.5f);
        assertEquals(new CCVector3(1.0f, -1.0f, 2.0f), vec2);

        final CCVector3 vec3 = vec1.divide(vec2);
        assertEquals(CCVector3.ONE, vec3);

        final CCVector3 vec4 = vec1.divide(2, 3, 4);
        assertEquals(new CCVector3(0.5f, -1 / 3f, 0.5f), vec4);

        vec1.divideLocal(0.5f, 0.5f, 0.5f);
        assertEquals(new CCVector3(2, -2, 4), vec1);

        vec1.divideLocal(vec2);
        assertEquals(new CCVector3(2, 2, 2), vec1);
    }

    @Test
    public void testScaleAdd() {
        final CCVector3 vec1 = new CCVector3(1, 1, 1);
        final CCVector3 vec2 = vec1.scaleAdd(2.0f, new CCVector3(1, 2, 3));
        assertEquals(new CCVector3(3.0f, 4.0f, 5.0f), vec2);

        vec1.scaleAddLocal(2.0f, new CCVector3(1, 2, 3));
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNegate() {
        final CCVector3 vec1 = new CCVector3(3, 2, -1);
        final CCVector3 vec2 = vec1.negate();
        assertEquals(new CCVector3(-3, -2, 1), vec2);

        vec1.negateLocal();
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNormalize() {
        final CCVector3 vec1 = new CCVector3(2, 1, 3);
//        assertTrue(vec1.length() == CCMath.sqrt(14));

        final CCVector3 vec2 = vec1.normalize();
        final double invLength = 1 / CCMath.sqrt(2 * 2 + 1 * 1 + 3 * 3);
        assertEquals(new CCVector3(2 * invLength, 1 * invLength, 3 * invLength), vec2);

        vec1.normalizeLocal();
        assertEquals(new CCVector3(2 * invLength, 1 * invLength, 3 * invLength), vec1);

        // ensure no exception thrown
        vec1.normalizeLocal();
        vec1.normalize();
    }

    @Test
    public void testDistance() {
        final CCVector3 vec1 = new CCVector3(0, 0, 0);
        assertTrue(4.0 == vec1.distance(4, 0, 0));
        assertTrue(3.0 == vec1.distance(0, 3, 0));
        assertTrue(2.0 == vec1.distance(0, 0, 2));

        final CCVector3 vec2 = new CCVector3(1, 1, 1);
//        assertTrue(CCMath.sqrt(3) == vec1.distance(vec2));
    }

    @Test
    public void testLerp() {
        final CCVector3 vec1 = new CCVector3(8, 3, -2);
        final CCVector3 vec2 = new CCVector3(2, 1, 0);
        assertEquals(new CCVector3(5, 2, -1), vec1.lerp(vec2, 0.5f));
        assertEquals(new CCVector3(5, 2, -1), CCVector3.lerp(vec1, vec2, 0.5f));

        vec1.set(14, 5, 4);
        vec1.lerpLocal(vec2, 0.25f);
        assertEquals(new CCVector3(11, 4, 3), vec1);

        vec1.set(15, 7, 6);
        final CCVector3 vec3 = new CCVector3(-1, -1, -1);
        vec3.lerpLocal(vec1, vec2, 0.5f);
        assertEquals(new CCVector3(8.5f, 4.0f, 3.0f), vec3);

        // coverage
        assertEquals(vec1.lerp(vec1, .25f), vec1);
        assertEquals(vec2.lerpLocal(vec2, .25f), vec2);
        assertEquals(vec2.lerpLocal(vec2, vec2, .25f), vec2);
        assertEquals(CCVector3.lerp(vec1, vec1, .25f), vec1);
    }

    @Test
    public void testCross() {
        final CCVector3 vec1 = new CCVector3(1, 0, 0);
        final CCVector3 vec2 = new CCVector3(0, 1, 0);
        assertEquals(CCVector3.UNIT_Z, vec1.cross(vec2));

        assertEquals(CCVector3.UNIT_Z, vec1.cross(0, 1, 0));

        vec1.crossLocal(vec2);
        assertEquals(CCVector3.UNIT_Z, vec1);
        vec2.crossLocal(1, 0, 0);
        assertEquals(CCVector3.NEG_UNIT_Z, vec2);
    }

    @Test
    public void testAngle() {
        final CCVector3 vec1 = new CCVector3(1, 0, 0);

        assertTrue(CCMath.HALF_PI == vec1.smallestAngleBetween(new CCVector3(0, -1, 0)));
    }

    @Test
    public void testDot() {
        final CCVector3 vec1 = new CCVector3(7, 2, 5);
        assertTrue(33.0 == vec1.dot(3, 1, 2));

        assertTrue(-10.0 == vec1.dot(new CCVector3(-1, 1, -1)));
    }

    @Test
    public void testClone() {
        final CCVector3 vec1 = new CCVector3(0, 0, 0);
        final CCVector3 vec2 = vec1.clone();
        assertEquals(vec1, vec2);
        assertNotSame(vec1, vec2);
    }

    @Test
    public void testValid() {
        final CCVector3 vec1 = new CCVector3(0, 0, 0);
        final CCVector3 vec2A = new CCVector3(Float.POSITIVE_INFINITY, 0, 0);
        final CCVector3 vec2B = new CCVector3(0, Float.NEGATIVE_INFINITY, 0);
        final CCVector3 vec2C = new CCVector3(0, 0, Float.POSITIVE_INFINITY);
        final CCVector3 vec3A = new CCVector3(Float.NaN, 0, 0);
        final CCVector3 vec3B = new CCVector3(0, Float.NaN, 0);
        final CCVector3 vec3C = new CCVector3(0, 0, Float.NaN);

        assertTrue(CCVector3.isValid(vec1));
        assertFalse(CCVector3.isValid(vec2A));
        assertFalse(CCVector3.isValid(vec2B));
        assertFalse(CCVector3.isValid(vec2C));
        assertFalse(CCVector3.isValid(vec3A));
        assertFalse(CCVector3.isValid(vec3B));
        assertFalse(CCVector3.isValid(vec3C));

        assertFalse(CCVector3.isInfinite(vec1));
        assertTrue(CCVector3.isInfinite(vec2A));

        vec3C.zero();
        assertTrue(CCVector3.isValid(vec3C));

        assertFalse(CCVector3.isValid(null));
        assertFalse(CCVector3.isInfinite(null));

        // couple of equals validity tests
        assertEquals(vec1, vec1);
        assertFalse(vec1.equals(null));
        assertFalse(vec1.equals(new CCVector4()));

        // cover more of equals
        vec1.set(0, 1, 2);
        assertFalse(vec1.equals(new CCVector3(0, 2, 3)));
        assertFalse(vec1.equals(new CCVector3(0, 1, 3)));
    }

    @Test
    public void testSimpleHash() {
        // Just a simple sanity check.
        final CCVector3 vec1 = new CCVector3(1, 2, 3);
        final CCVector3 vec2 = new CCVector3(1, 2, 3);
        final CCVector3 vec3 = new CCVector3(2, 2, 2);

        assertTrue(vec1.hashCode() == vec2.hashCode());
//        assertTrue(vec1.hashCode() != vec3.hashCode());
    }
}
