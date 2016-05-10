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

public class CCVector2Test {

    @Test
    public void testAdd() {
        final CCVector2 vec1 = new CCVector2();
        final CCVector2 vec2 = new CCVector2(CCVector2.ONE);

        vec1.addLocal(1, 2);
        assertEquals(new CCVector2(1, 2), vec1);
        vec1.addLocal(-1, -2);
        assertEquals(CCVector2.ZERO, vec1);

        vec1.zero();
        vec1.addLocal(vec2);
        assertEquals(CCVector2.ONE, vec1);

        vec1.zero();
        final CCVector2 vec3 = vec1.add(vec2, new CCVector2());
        assertEquals(CCVector2.ZERO, vec1);
        assertEquals(CCVector2.ONE, vec3);

        final CCVector2 vec4 = vec1.add(1, 0, null);
        assertEquals(CCVector2.ZERO, vec1);
        assertEquals(CCVector2.UNIT_X, vec4);
    }

    @Test
    public void testSubtract() {
        final CCVector2 vec1 = new CCVector2();
        final CCVector2 vec2 = new CCVector2(CCVector2.ONE);

        vec1.subtractLocal(1, 2);
        assertEquals(new CCVector2(-1, -2), vec1);
        vec1.subtractLocal(-1, -2);
        assertEquals(CCVector2.ZERO, vec1);

        vec1.zero();
        vec1.subtractLocal(vec2);
        assertEquals(CCVector2.NEG_ONE, vec1);

        vec1.zero();
        final CCVector2 vec3 = vec1.subtract(vec2, new CCVector2());
        assertEquals(CCVector2.ZERO, vec1);
        assertEquals(CCVector2.NEG_ONE, vec3);

        final CCVector2 vec4 = vec1.subtract(1, 0, null);
        assertEquals(CCVector2.ZERO, vec1);
        assertEquals(CCVector2.NEG_UNIT_X, vec4);
    }

    @Test
    public void testGetSet() {
        final CCVector2 vec1 = new CCVector2();
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

        vec1.set(CCMath.PI, CCMath.PI);
        assertTrue(vec1.x == CCMath.PI);
        assertTrue(vec1.y == CCMath.PI);

        final CCVector2 vec2 = new CCVector2();
        vec2.set(vec1);
        assertEquals(vec1, vec2);

        vec1.setValue(0, 0);
        vec1.setValue(1, 0);
        assertEquals(CCVector2.ZERO, vec1);

        // catch a few expected exceptions
        try {
            vec2.getValue(2);
            fail("getValue(2) should have thrown IllegalArgumentException.");
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
            vec2.setValue(2, 0);
            fail("setValue(2, 0) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        // above exceptions shouldn't have altered vec2
        assertEquals(new CCVector2(CCMath.PI, CCMath.PI), vec2);
    }

    @Test
    public void testPolarAngle() {
        final CCVector2 vec1 = new CCVector2();
        assertTrue(0.0 == vec1.getPolarAngle());

        vec1.set(1.0f, 0.0f); // 0
        assertTrue(CCMath.abs(0 - vec1.getPolarAngle()) <= CCMath.FLT_EPSILON);

        vec1.set(0.0f, 1.0f); // -HALF_PI
        assertTrue(CCMath.abs(-CCMath.HALF_PI - vec1.getPolarAngle()) <= CCMath.FLT_EPSILON);

        vec1.set(-1.0f, 0.0f); // -PI
        assertTrue(CCMath.abs(-CCMath.PI - vec1.getPolarAngle()) <= CCMath.FLT_EPSILON);

        vec1.set(0f, -1.0f); // HALF_PI
        assertTrue(CCMath.abs(CCMath.HALF_PI - vec1.getPolarAngle()) <= CCMath.FLT_EPSILON);
    }

    @Test
    public void testToArray() {
        final CCVector2 vec1 = new CCVector2();
        vec1.set(CCMath.PI, Float.MAX_VALUE);
        final double[] array = vec1.toArray(null);
        final double[] array2 = vec1.toArray(new double[2]);
        assertNotNull(array);
        assertTrue(array.length == 2);
        assertTrue(array[0] == CCMath.PI);
        assertTrue(array[1] == Float.MAX_VALUE);
        assertNotNull(array2);
        assertTrue(array2.length == 2);
        assertTrue(array2[0] == CCMath.PI);
        assertTrue(array2[1] == Float.MAX_VALUE);

        try {
            vec1.toArray(new double[1]);
            fail("toArray(d[1]) should have thrown ArrayIndexOutOfBoundsException.");
        } catch (final ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testMultiply() {
        final CCVector2 vec1 = new CCVector2(1, -1);
        final CCVector2 vec2 = vec1.multiply(2.0f);
        final CCVector2 vec2B = vec1.multiply(2.0f, new CCVector2());
        assertEquals(new CCVector2(2.0f, -2.0f), vec2);
        assertEquals(new CCVector2(2.0f, -2.0f), vec2B);

        vec2.multiplyLocal(0.5f);
        assertEquals(new CCVector2(1.0f, -1.0f), vec2);

        final CCVector2 vec3 = vec1.multiply(vec2, null);
        final CCVector2 vec3B = vec1.multiply(vec2, new CCVector2());
        assertEquals(CCVector2.ONE, vec3);
        assertEquals(CCVector2.ONE, vec3B);

        final CCVector2 vec4 = vec1.multiply(2, 3, null);
        final CCVector2 vec4B = vec1.multiply(2, 3, new CCVector2());
        assertEquals(new CCVector2(2, -3), vec4);
        assertEquals(new CCVector2(2, -3), vec4B);

        vec1.multiplyLocal(0.5f, 0.5f);
        assertEquals(new CCVector2(0.5f, -0.5f), vec1);

        vec1.multiplyLocal(vec2);
        assertEquals(new CCVector2(0.5f, 0.5f), vec1);
    }

    @Test
    public void testDivide() {
        final CCVector2 vec1 = new CCVector2(1, -1);
        final CCVector2 vec2 = vec1.divide(2.0f);
        final CCVector2 vec2B = vec1.divide(2.0f, new CCVector2());
        assertEquals(new CCVector2(0.5f, -0.5f), vec2);
        assertEquals(new CCVector2(0.5f, -0.5f), vec2B);

        vec2.divideLocal(0.5f);
        assertEquals(new CCVector2(1.0f, -1.0f), vec2);

        final CCVector2 vec3 = vec1.divide(vec2);
        final CCVector2 vec3B = vec1.divide(vec2, new CCVector2());
        assertEquals(CCVector2.ONE, vec3);
        assertEquals(CCVector2.ONE, vec3B);

        final CCVector2 vec4 = vec1.divide(2, 3, null);
        final CCVector2 vec4B = vec1.divide(2, 3, new CCVector2());
        assertEquals(new CCVector2(1 / 2.f, -1 / 3.f), vec4);
        assertEquals(new CCVector2(1 / 2.f, -1 / 3.f), vec4B);

        vec1.divideLocal(0.5f, 0.5f);
        assertEquals(new CCVector2(2, -2), vec1);

        vec1.divideLocal(vec2);
        assertEquals(new CCVector2(2, 2), vec1);
    }

    @Test
    public void testScaleAdd() {
        final CCVector2 vec1 = new CCVector2(1, 1);
        final CCVector2 vec2 = vec1.scaleAdd(2.0f, new CCVector2(1, 2));
        final CCVector2 vec2B = vec1.scaleAdd(2.0f, new CCVector2(1, 2), new CCVector2());
        assertEquals(new CCVector2(3.0f, 4.0f), vec2);
        assertEquals(new CCVector2(3.0f, 4.0f), vec2B);

        vec1.scaleAddLocal(2.0f, new CCVector2(1, 2));
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNegate() {
        final CCVector2 vec1 = new CCVector2(2, 1);
        final CCVector2 vec2 = vec1.negate(null);
        assertEquals(new CCVector2(-2, -1), vec2);

        vec1.negateLocal();
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNormalize() {
        final CCVector2 vec1 = new CCVector2(2, 1);
//        assertTrue(vec1.length() == CCMath.sqrt(5));

        final CCVector2 vec2 = vec1.normalize();
        final double invLength = 1f / CCMath.sqrt(2 * 2 + 1 * 1);
        assertEquals(new CCVector2(2 * invLength, 1 * invLength), vec2);

        vec1.normalizeLocal();
        assertEquals(new CCVector2(2 * invLength, 1 * invLength), vec1);

        vec1.zero();
        vec1.normalize(vec2);
        assertEquals(vec1, vec2);

        // ensure no exception thrown
        vec1.normalizeLocal();
        vec1.normalize(null);
    }

    @Test
    public void testDistance() {
        final CCVector2 vec1 = new CCVector2(0, 0);
        assertTrue(3.0 == vec1.distance(0, 3));
        assertTrue(4.0 == vec1.distance(4, 0));

        final CCVector2 vec2 = new CCVector2(1, 1);
//        assertTrue(CCMath.sqrt(2) == vec1.distance(vec2));
    }

    @Test
    public void testLerp() {
        final CCVector2 vec1 = new CCVector2(8, 3);
        final CCVector2 vec2 = new CCVector2(2, 1);
        assertEquals(new CCVector2(5, 2), vec1.lerp(vec2, 0.5f));
        assertEquals(new CCVector2(5, 2), vec1.lerp(vec2, 0.5f, new CCVector2()));
        assertEquals(new CCVector2(5, 2), CCVector2.lerp(vec1, vec2, 0.5f));
        assertEquals(new CCVector2(5, 2), CCVector2.lerp(vec1, vec2, 0.5f, new CCVector2()));

        vec1.set(14, 5);
        vec1.lerpLocal(vec2, 0.25f);
        assertEquals(new CCVector2(11, 4), vec1);

        vec1.set(15, 7);
        final CCVector2 vec3 = new CCVector2(-1, -1);
        vec3.lerpLocal(vec1, vec2, 0.5f);
        assertEquals(new CCVector2(8.5f, 4.0f), vec3);
    }

    @Test
    public void testRotate() {
        final CCVector2 vec1 = new CCVector2(1, 0);
        final CCVector2 vec2 = vec1.rotateAroundOrigin(CCMath.HALF_PI, true, null);
        final CCVector2 vec2B = vec1.rotateAroundOrigin(CCMath.HALF_PI, false, new CCVector2());
        assertEquals(new CCVector2(0, -1), vec2);
        assertEquals(new CCVector2(0, 1), vec2B);
        vec2.rotateAroundOriginLocal(CCMath.HALF_PI, false);
        assertEquals(new CCVector2(1, 0), vec2);
        vec2.rotateAroundOriginLocal(CCMath.PI, true);
        assertTrue(CCMath.abs(vec2.x - -1) <= CCMath.FLT_EPSILON);
        assertTrue(CCMath.abs(vec2.y - 0) <= CCMath.FLT_EPSILON);
    }

    @Test
    public void testAngle() {
        final CCVector2 vec1 = new CCVector2(1, 0);
        assertTrue(CCMath.HALF_PI == vec1.angleBetween(new CCVector2(0, 1)));
        assertTrue(-CCMath.HALF_PI == vec1.angleBetween(new CCVector2(0, -1)));

        assertTrue(CCMath.HALF_PI == vec1.smallestAngleBetween(new CCVector2(0, -1)));
    }

    @Test
    public void testDot() {
        final CCVector2 vec1 = new CCVector2(7, 2);
        assertTrue(23.0 == vec1.dot(3, 1));

        assertTrue(-5.0 == vec1.dot(new CCVector2(-1, 1)));
    }

    @Test
    public void testClone() {
        final CCVector2 vec1 = new CCVector2(0, 0);
        final CCVector2 vec2 = vec1.clone();
        assertEquals(vec1, vec2);
        assertNotSame(vec1, vec2);
    }

    @Test
    public void testValid() {
        final CCVector2 vec1 = new CCVector2(0, 0);
        final CCVector2 vec2 = new CCVector2(Float.POSITIVE_INFINITY, 0);
        final CCVector2 vec3 = new CCVector2(0, Float.NEGATIVE_INFINITY);
        final CCVector2 vec4 = new CCVector2(Float.NaN, 0);
        final CCVector2 vec5 = new CCVector2(0, Float.NaN);

        assertTrue(CCVector2.isValid(vec1));
        assertFalse(CCVector2.isValid(vec2));
        assertFalse(CCVector2.isValid(vec3));
        assertFalse(CCVector2.isValid(vec4));
        assertFalse(CCVector2.isValid(vec5));

        vec5.zero();
        assertTrue(CCVector2.isValid(vec5));

        assertFalse(CCVector2.isValid(null));

        // couple of equals validity tests
        assertEquals(vec1, vec1);
        assertFalse(vec1.equals(null));
        assertFalse(vec1.equals(new CCVector3()));

        // cover more of equals
        vec1.set(0, 1);
        assertFalse(vec1.equals(new CCVector2(0, 2)));
    }

    @Test
    public void testSimpleHash() {
        // Just a simple sanity check.
        final CCVector2 vec1 = new CCVector2(1, 2);
        final CCVector2 vec2 = new CCVector2(1, 2);
        final CCVector2 vec3 = new CCVector2(2, 2);

//        assertTrue(vec1.hashCode() == vec2.hashCode());
//        assertTrue(vec1.hashCode() != vec3.hashCode());
    }
}
