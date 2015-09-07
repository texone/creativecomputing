

package cc.creativecomputing.math;

import static org.junit.Assert.*;

import org.junit.Test;

public class CCVector4Test {

    @Test
    public void testAdd() {
        final CCVector4 vec1 = new CCVector4();
        final CCVector4 vec2 = new CCVector4(CCVector4.ONE);

        vec1.addLocal(1, 2, 3, 4);
        assertEquals(new CCVector4(1, 2, 3, 4), vec1);
        vec1.addLocal(-1, -2, -3, -4);
        assertEquals(CCVector4.ZERO, vec1);

        vec1.zero();
        vec1.addLocal(vec2);
        assertEquals(CCVector4.ONE, vec1);

        vec1.zero();
        final CCVector4 vec3 = vec1.add(vec2);
        assertEquals(CCVector4.ZERO, vec1);
        assertEquals(CCVector4.ONE, vec3);
    }

    @Test
    public void testSubtract() {
        final CCVector4 vec1 = new CCVector4();
        final CCVector4 vec2 = new CCVector4(CCVector4.ONE);

        vec1.subtractLocal(1, 2, 3, 4);
        assertEquals(new CCVector4(-1, -2, -3, -4), vec1);
        vec1.subtractLocal(-1, -2, -3, -4);
        assertEquals(CCVector4.ZERO, vec1);

        vec1.zero();
        vec1.subtractLocal(vec2);
        assertEquals(CCVector4.NEG_ONE, vec1);

        vec1.zero();
        final CCVector4 vec3 = vec1.subtract(vec2);
        assertEquals(CCVector4.ZERO, vec1);
        assertEquals(CCVector4.NEG_ONE, vec3);
    }

    @Test
    public void testGetSet() {
        final CCVector4 vec1 = new CCVector4();
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

        vec1.w = 0;
        assertTrue(vec1.w == 0.0);
        vec1.w = Float.POSITIVE_INFINITY;
        assertTrue(vec1.w == Float.POSITIVE_INFINITY);
        vec1.w = Float.NEGATIVE_INFINITY;
        assertTrue(vec1.w == Float.NEGATIVE_INFINITY);
        assertTrue(vec1.getValue(3) == Float.NEGATIVE_INFINITY);

        vec1.set(CCMath.PI, CCMath.PI, CCMath.PI, CCMath.PI);
        assertTrue(vec1.x ==  CCMath.PI);
        assertTrue(vec1.y ==  CCMath.PI);
        assertTrue(vec1.z ==  CCMath.PI);
        assertTrue(vec1.w ==  CCMath.PI);

        final CCVector4 vec2 = new CCVector4();
        vec2.set(vec1);
        assertEquals(vec1, vec2);

        vec1.setValue(0, 0);
        vec1.setValue(1, 0);
        vec1.setValue(2, 0);
        vec1.setValue(3, 0);
        assertEquals(CCVector4.ZERO, vec1);

        // catch a few expected exceptions
        try {
            vec2.getValue(4);
            fail("getValue(4) should have thrown IllegalArgumentException.");
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
            vec2.setValue(4, 0);
            fail("setValue(4, 0) should have thrown IllegalArgumentException.");
        } catch (final IllegalArgumentException e) {
        }
        // above exceptions shouldn't have altered vec2
        assertEquals(new CCVector4(CCMath.PI, CCMath.PI, CCMath.PI, CCMath.PI), vec2);
    }

    @Test
    public void testToArray() {
        final CCVector4 vec1 = new CCVector4();
        vec1.set(CCMath.PI, Float.MAX_VALUE, 42, -1);
        final double[] array = vec1.toArray(null);
        final double[] array2 = vec1.toArray(new double[4]);
        assertNotNull(array);
        assertTrue(array.length == 4);
        assertTrue(array[0] == CCMath.PI);
        assertTrue(array[1] == Float.MAX_VALUE);
        assertTrue(array[2] == 42);
        assertTrue(array[3] == -1);
        assertNotNull(array2);
        assertTrue(array2.length == 4);
        assertTrue(array2[0] == CCMath.PI);
        assertTrue(array2[1] == Float.MAX_VALUE);
        assertTrue(array2[2] == 42);
        assertTrue(array2[3] == -1);

        try {
            vec1.toArray(new double[1]);
            fail("toArray(d[1]) should have thrown ArrayIndexOutOfBoundsException.");
        } catch (final ArrayIndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testMultiply() {
        final CCVector4 vec1 = new CCVector4(1, -1, 2, -2);
        final CCVector4 vec2 = vec1.multiply(2.0f);
        assertEquals(new CCVector4(2.0f, -2.0f, 4.0f, -4.0f), vec2);

        vec2.multiplyLocal(0.5f);
        assertEquals(new CCVector4(1.0f, -1.0f, 2.0f, -2.0f), vec2);

        final CCVector4 vec3 = vec1.multiply(vec2);
        assertEquals(new CCVector4(1, 1, 4, 4), vec3);

        final CCVector4 vec4 = vec1.multiply(2, 3, 2, 3);
        assertEquals(new CCVector4(2, -3, 4, -6), vec4);

        vec1.multiplyLocal(0.5f, 0.5f, 0.5f, 0.5f);
        assertEquals(new CCVector4(0.5f, -0.5f, 1.0f, -1.0f), vec1);

        vec1.multiplyLocal(vec2);
        assertEquals(new CCVector4(0.5f, 0.5f, 2.0f, 2.0f), vec1);
    }

    @Test
    public void testDivide() {
        final CCVector4 vec1 = new CCVector4(1, -1, 2, -2);
        final CCVector4 vec2 = vec1.divide(2.0f);
        assertEquals(new CCVector4(0.5f, -0.5f, 1.0f, -1.0f), vec2);

        vec2.divideLocal(0.5f);
        assertEquals(new CCVector4(1.0f, -1.0f, 2.0f, -2.0f), vec2);

        final CCVector4 vec3 = vec1.divide(vec2);
        assertEquals(CCVector4.ONE, vec3);

        final CCVector4 vec4 = vec1.divide(2, 3, 4, 5);
//        assertEquals(new CCVector4(0.5f, -1 / 3.f, 0.5f, -0.4f), vec4);

        vec1.divideLocal(0.5f, 0.5f, 0.5f, 0.5f);
        assertEquals(new CCVector4(2, -2, 4, -4), vec1);

        vec1.divideLocal(vec2);
        assertEquals(new CCVector4(2, 2, 2, 2), vec1);
    }

    @Test
    public void testScaleAdd() {
        final CCVector4 vec1 = new CCVector4(1, 1, 1, 1);
        final CCVector4 vec2 = vec1.scaleAdd(2.0f, new CCVector4(1, 2, 3, 4));
        assertEquals(new CCVector4(3.0f, 4.0f, 5.0f, 6.0f), vec2);

        vec1.scaleAddLocal(2.0f, new CCVector4(1, 2, 3, 4));
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNegate() {
        final CCVector4 vec1 = new CCVector4(3, 2, -1, 1);
        final CCVector4 vec2 = vec1.negate();
        assertEquals(new CCVector4(-3, -2, 1, -1), vec2);

        vec1.negateLocal();
        assertEquals(vec2, vec1);
    }

    @Test
    public void testNormalize() {
        final CCVector4 vec1 = new CCVector4(2, 1, 3, -1);
//        assertTrue(vec1.length() == CCMath.sqrt(15));

        final CCVector4 vec2 = vec1.normalize();
        final double invLength = 1f / CCMath.sqrt(2 * 2 + 1 * 1 + 3 * 3 + -1 * -1);
//        assertEquals(new CCVector4(2 * invLength, 1 * invLength, 3 * invLength, -1 * invLength), vec2);

        vec1.normalizeLocal();
//        assertEquals(new CCVector4(2 * invLength, 1 * invLength, 3 * invLength, -1 * invLength), vec1);


        // ensure no exception thrown
        vec1.normalizeLocal();
        vec1.normalize();
    }

    @Test
    public void testDistance() {
        final CCVector4 vec1 = new CCVector4(0, 0, 0, 0);
        assertTrue(4.0 == vec1.distance(4, 0, 0, 0));
        assertTrue(3.0 == vec1.distance(0, 3, 0, 0));
        assertTrue(2.0 == vec1.distance(0, 0, 2, 0));
        assertTrue(1.0 == vec1.distance(0, 0, 0, 1));

        final CCVector4 vec2 = new CCVector4(1, 1, 1, 1);
        assertTrue(CCMath.sqrt(4) == vec1.distance(vec2));
    }

    @Test
    public void testLerp() {
        final CCVector4 vec1 = new CCVector4(8, 3, -2, 2);
        final CCVector4 vec2 = new CCVector4(2, 1, 0, -2);
        assertEquals(new CCVector4(5, 2, -1, 0), vec1.lerp(vec2, 0.5f));
        assertEquals(new CCVector4(5, 2, -1, 0), CCVector4.lerp(vec1, vec2, 0.5f));

        vec1.set(14, 5, 4, 2);
        vec1.lerpLocal(vec2, 0.25f);
        assertEquals(new CCVector4(11, 4, 3, 1), vec1);

        vec1.set(15, 7, 6, 8);
        final CCVector4 vec3 = new CCVector4(-1, -1, -1, -1);
        vec3.lerpLocal(vec1, vec2, 0.5f);
        assertEquals(new CCVector4(8.5f, 4.0f, 3.0f, 3.0f), vec3);

        // coverage
        assertEquals(vec1.lerp(vec1, .25f), vec1);
        assertEquals(vec2.lerpLocal(vec2, .25f), vec2);
        assertEquals(vec2.lerpLocal(vec2, vec2, .25f), vec2);
        assertEquals(CCVector4.lerp(vec1, vec1, .25f), vec1);
    }

    @Test
    public void testDot() {
        final CCVector4 vec1 = new CCVector4(7, 2, 5, -1);
        assertTrue(35.0 == vec1.dot(3, 1, 2, -2));

        assertTrue(-11.0 == vec1.dot(new CCVector4(-1, 1, -1, 1)));
    }

    @Test
    public void testClone() {
        final CCVector4 vec1 = new CCVector4(0, 0, 0, 0);
        final CCVector4 vec2 = vec1.clone();
        assertEquals(vec1, vec2);
        assertNotSame(vec1, vec2);
    }

    @Test
    public void testValid() {
        final CCVector4 vec1 = new CCVector4(0, 0, 0, 0);
        final CCVector4 vec2A = new CCVector4(Float.POSITIVE_INFINITY, 0, 0, 0);
        final CCVector4 vec2B = new CCVector4(0, Float.NEGATIVE_INFINITY, 0, 0);
        final CCVector4 vec2C = new CCVector4(0, 0, Float.POSITIVE_INFINITY, 0);
        final CCVector4 vec2D = new CCVector4(0, 0, 0, Float.POSITIVE_INFINITY);
        final CCVector4 vec3A = new CCVector4(Float.NaN, 0, 0, 0);
        final CCVector4 vec3B = new CCVector4(0, Float.NaN, 0, 0);
        final CCVector4 vec3C = new CCVector4(0, 0, Float.NaN, 0);
        final CCVector4 vec3D = new CCVector4(0, 0, 0, Float.NaN);

        assertTrue(CCVector4.isValid(vec1));
        assertFalse(CCVector4.isValid(vec2A));
        assertFalse(CCVector4.isValid(vec2B));
        assertFalse(CCVector4.isValid(vec2C));
        assertFalse(CCVector4.isValid(vec2D));
        assertFalse(CCVector4.isValid(vec3A));
        assertFalse(CCVector4.isValid(vec3B));
        assertFalse(CCVector4.isValid(vec3C));
        assertFalse(CCVector4.isValid(vec3D));

        vec3C.zero();
        assertTrue(CCVector4.isValid(vec3C));

        assertFalse(CCVector4.isValid(null));

        // couple of equals validity tests
        assertEquals(vec1, vec1);
        assertFalse(vec1.equals(null));
        assertFalse(vec1.equals(new CCVector3()));

        // cover more of equals
        vec1.set(0, 1, 2, 3);
        assertFalse(vec1.equals(new CCVector4(0, 4, 4, 4)));
        assertFalse(vec1.equals(new CCVector4(0, 1, 4, 4)));
        assertFalse(vec1.equals(new CCVector4(0, 1, 2, 4)));
    }

    @Test
    public void testSimpleHash() {
        // Just a simple sanity check.
        final CCVector4 vec1 = new CCVector4(1, 2, 3, 4);
        final CCVector4 vec2 = new CCVector4(1, 2, 3, 4);
        final CCVector4 vec3 = new CCVector4(2, 2, 2, 2);

        assertTrue(vec1.hashCode() == vec2.hashCode());
        assertTrue(vec1.hashCode() != vec3.hashCode());
    }
}
