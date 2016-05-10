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

public class TestQuaternion {

	@Test
	public void testGetSet() {
		final CCQuaternion quat1 = new CCQuaternion();
		assertEquals(CCQuaternion.IDENTITY, quat1);
		assertTrue(quat1.isIdentity());

		quat1.x = 1;
		assertTrue(quat1.x == 1.0);
		quat1.x = Float.POSITIVE_INFINITY;
		assertTrue(quat1.x == Float.POSITIVE_INFINITY);
		quat1.x = Float.NEGATIVE_INFINITY;
		assertTrue(quat1.x == Float.NEGATIVE_INFINITY);

		quat1.y = 1;
		assertTrue(quat1.y == 1.0);
		quat1.y = Float.POSITIVE_INFINITY;
		assertTrue(quat1.y == Float.POSITIVE_INFINITY);
		quat1.y = Float.NEGATIVE_INFINITY;
		assertTrue(quat1.y == Float.NEGATIVE_INFINITY);

		quat1.z = 1;
		assertTrue(quat1.z == 1.0);
		quat1.z = Float.POSITIVE_INFINITY;
		assertTrue(quat1.z == Float.POSITIVE_INFINITY);
		quat1.z = Float.NEGATIVE_INFINITY;
		assertTrue(quat1.z == Float.NEGATIVE_INFINITY);

		quat1.w = 1;
		assertTrue(quat1.w == 1.0);
		quat1.w = Float.POSITIVE_INFINITY;
		assertTrue(quat1.w == Float.POSITIVE_INFINITY);
		quat1.w = Float.NEGATIVE_INFINITY;
		assertTrue(quat1.w == Float.NEGATIVE_INFINITY);

		quat1.set(CCMath.PI, CCMath.PI, CCMath.PI, CCMath.PI);
		assertTrue(quat1.x == CCMath.PI);
		assertTrue(quat1.y == CCMath.PI);
		assertTrue(quat1.z == CCMath.PI);
		assertTrue(quat1.w == CCMath.PI);

		final CCQuaternion quat2 = new CCQuaternion();
		quat2.set(quat1);
		assertEquals(quat1, quat2);
	}

	@Test
	public void testToArray() {
		final CCQuaternion quat1 = new CCQuaternion();
		quat1.set(CCMath.PI, Float.MAX_VALUE, 42, -1);
		final double[] array = quat1.toArray(null);
		final double[] array2 = quat1.toArray(new double[4]);
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
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadArray() {
		final CCQuaternion quat = new CCQuaternion();
		quat.toArray(new double[2]);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadAxesArray() {
		final CCQuaternion quat = new CCQuaternion();
		quat.toAxes(new CCVector3[2]);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadEuler1() {
		new CCQuaternion().fromEulerAngles(new double[2]);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testBadEuler2() {
		final CCQuaternion quat = new CCQuaternion();
		quat.toEulerAngles(new double[2]);
	}

	@Test
	public void testEulerAngles() {
		final CCQuaternion quat = new CCQuaternion().fromEulerAngles(new double[] { CCMath.HALF_PI, 0, 0 });
		assertTrue(1.0 == quat.magnitude());
		assertTrue(CCMath.abs(CCVector3.NEG_UNIT_Z.distance(quat.apply(CCVector3.UNIT_X))) <= CCMath.FLT_EPSILON);

		quat.fromEulerAngles(0, -CCMath.HALF_PI, 0);
		assertTrue(1.0 == quat.magnitude());
		assertTrue(CCMath.abs(CCVector3.NEG_UNIT_Y.distance(quat.apply(CCVector3.UNIT_X))) <= CCMath.FLT_EPSILON);

		quat.fromEulerAngles(0, 0, CCMath.HALF_PI);
		assertTrue(1.0 == quat.magnitude());
		assertTrue(CCMath.abs(CCVector3.UNIT_Z.distance(quat.apply(CCVector3.UNIT_Y))) <= CCMath.FLT_EPSILON);

		quat.fromEulerAngles(0, CCMath.HALF_PI, 0);
		double[] angles = quat.toEulerAngles(null);
		final CCQuaternion quat2 = new CCQuaternion().fromEulerAngles(angles);
		assertEquals(quat, quat2);
		quat.fromEulerAngles(0, -CCMath.HALF_PI, 0);
		angles = quat.toEulerAngles(null);
		quat2.fromEulerAngles(angles);
		assertEquals(quat, quat2);
		quat.fromEulerAngles(0, 0, CCMath.HALF_PI);
		angles = quat.toEulerAngles(null);
		quat2.fromEulerAngles(angles);
		assertEquals(quat, quat2);
	}

	@Test
	public void testMatrix3() {
		double a = CCMath.HALF_PI;
		final CCQuaternion quat = new CCQuaternion();
		quat.fromRotationMatrix( //
			1, 0, 0, //
			0, CCMath.cos(a), -CCMath.sin(a), //
			0, CCMath.sin(a), CCMath.cos(a)
		);

		assertTrue(CCMath.abs(CCVector3.UNIT_Z.distance(quat.apply(CCVector3.UNIT_Y))) <= CCMath.FLT_EPSILON);
		CCMatrix3x3 mat = quat.toRotationMatrix();
		assertTrue(CCMath.abs(quat.apply(CCVector3.NEG_ONE).distance(mat.applyPost(CCVector3.NEG_ONE))) <= CCMath.FLT_EPSILON);

		a = CCMath.PI;
		quat.fromRotationMatrix( //
			1, 0, 0, //
			0, CCMath.cos(a), -CCMath.sin(a), //
			0, CCMath.sin(a), CCMath.cos(a)
		);

		assertTrue(CCMath.abs(CCVector3.NEG_UNIT_Y.distance(quat.apply(CCVector3.UNIT_Y))) <= CCMath.FLT_EPSILON);
		mat = quat.toRotationMatrix();
		assertTrue(CCMath.abs(quat.apply(CCVector3.ONE).distance(mat.applyPost(CCVector3.ONE))) <= CCMath.FLT_EPSILON);

		quat.set(0, 0, 0, 0);
		assertEquals(CCMatrix3x3.IDENTITY, quat.toRotationMatrix());

		a = CCMath.PI;
		quat.fromRotationMatrix( //
			CCMath.cos(a), 0, CCMath.sin(a), //
			0, 1, 0, //
			-CCMath.sin(a), 0, CCMath.cos(a)
		);

		assertTrue(CCMath.abs(CCVector3.NEG_UNIT_X.distance(quat.apply(CCVector3.UNIT_X))) <= CCMath.FLT_EPSILON);
		final CCMatrix4x4 mat4 = quat.toRotationMatrix((CCMatrix4x4) null);
		assertTrue(CCMath.abs(quat.apply(CCVector3.NEG_ONE).distance(mat4.applyPostVector(CCVector3.NEG_ONE))) <= CCMath.FLT_EPSILON);

		a = CCMath.PI;
		quat.fromRotationMatrix(new CCMatrix3x3(//
			CCMath.cos(a), -CCMath.sin(a), 0, //
			CCMath.sin(a), CCMath.cos(a), 0, //
			0, 0, 1)
		);

		assertTrue(CCMath.abs(CCVector3.NEG_UNIT_X.distance(quat.apply(CCVector3.UNIT_X))) <= CCMath.FLT_EPSILON);
		quat.toRotationMatrix(mat4);
		assertTrue(CCMath.abs(quat.apply(CCVector3.ONE).distance(mat4.applyPostVector(CCVector3.ONE))) <= CCMath.FLT_EPSILON);

		quat.set(0, 0, 0, 0);
		assertEquals(CCMatrix4x4.IDENTITY, quat.toRotationMatrix((CCMatrix4x4) null));
	}

	@Test
	public void testRotations() {
		final double a = CCMath.QUARTER_PI;
		final CCQuaternion quat = new CCQuaternion().fromRotationMatrix(new CCMatrix3x3(//
			CCMath.cos(a), -CCMath.sin(a), 0, //
			CCMath.sin(a), CCMath.cos(a), 0, //
			0, 0, 1)
		);
		final CCVector3 column = quat.getRotationColumn(0);
		assertTrue(CCMath.abs(new CCVector3(CCMath.cos(a), CCMath.sin(a), 0).distance(column)) <= CCMath.FLT_EPSILON);
		quat.getRotationColumn(1, column);
		assertTrue(CCMath.abs(new CCVector3(-CCMath.sin(a), CCMath.sin(a), 0).distance(column)) <= CCMath.FLT_EPSILON);
		quat.getRotationColumn(2, column);
		assertTrue(CCMath.abs(new CCVector3(0, 0, 1).distance(column)) <= CCMath.FLT_EPSILON);

		quat.set(0, 0, 0, 0);
		assertEquals(CCVector3.UNIT_X, quat.getRotationColumn(0));

		// Try a new way with new angles...
		quat.fromEulerAngles(CCMath.QUARTER_PI, CCMath.PI, CCMath.HALF_PI);
		CCVector3 rotated = new CCVector3(1, 1, 1);
		rotated = quat.apply(rotated);

		// expected
		CCVector3 expected = new CCVector3(1, 1, 1);
		final CCQuaternion worker = new CCQuaternion();
		// put together matrix, then apply to vector, so YZX
		worker.applyRotationY(CCMath.QUARTER_PI);
		worker.applyRotationZ(CCMath.PI);
		worker.applyRotationX(CCMath.HALF_PI);
		expected = worker.apply(expected);

		// test how close it came out
		assertTrue(rotated.distance(expected) <= CCQuaternion.ALLOWED_DEVIANCE);

		// test axis rotation methods against general purpose
		// X AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationX(CCMath.QUARTER_PI).apply(expected);
		rotated = worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 1, 0, 0).apply(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Y AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationY(CCMath.QUARTER_PI).apply(expected);
		rotated = worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 0, 1, 0).apply(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Z AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationZ(CCMath.QUARTER_PI).apply(expected);
		rotated = worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 0, 0, 1).apply(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		quat.set(worker);
		worker.applyRotation(0, 0, 0, 0);
		assertEquals(quat, worker);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadRotationColumn1() {
		new CCQuaternion().getRotationColumn(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadRotationColumn2() {
		new CCQuaternion().getRotationColumn(4);
	}

	@Test
	public void testAngleAxis() {
		final CCQuaternion quat = new CCQuaternion().fromAngleAxis(CCMath.HALF_PI, new CCVector3(2, 0, 0));
		final CCQuaternion quat2 = new CCQuaternion().fromAngleNormalAxis(CCMath.HALF_PI, new CCVector3(1, 0, 0));

		assertEquals(quat2, quat);
		assertTrue(1 - quat.magnitude() <= CCMath.FLT_EPSILON);

		assertEquals(quat.apply(CCVector3.ONE), quat2.apply(CCVector3.ONE));
		assertTrue(CCMath.abs(new CCVector3(0, -1, 0).distance(quat.apply(new CCVector3(0, 0, 1)))) <= CCMath.FLT_EPSILON);

		assertEquals(CCQuaternion.IDENTITY,new CCQuaternion(1, 2, 3, 4).fromAngleAxis(CCMath.HALF_PI,new CCVector3(0, 0, 0)));

		final CCVector3 axisStore = new CCVector3();
		double angle = quat.toAngleAxis(axisStore);
		assertEquals(quat, new CCQuaternion().fromAngleAxis(angle, axisStore));

		quat.set(0, 0, 0, 0);
		angle = quat.toAngleAxis(axisStore);
		assertTrue(0.0 == angle);
		assertEquals(CCVector3.UNIT_X, axisStore);
	}

	@Test
	public void testFromVectorToVector() {
		final CCQuaternion quat = new CCQuaternion().fromVectorToVector(CCVector3.UNIT_Z, CCVector3.UNIT_X);
		assertEquals(new CCQuaternion().fromAngleAxis(CCMath.HALF_PI,CCVector3.UNIT_Y), quat);

		quat.fromVectorToVector(CCVector3.UNIT_Z, CCVector3.NEG_UNIT_Z);
		assertTrue(CCMath.abs(new CCVector3(0, 0, -1).distance(quat.apply(new CCVector3(0, 0, 1)))) <= CCQuaternion.ALLOWED_DEVIANCE);

		quat.fromVectorToVector(CCVector3.UNIT_X, CCVector3.NEG_UNIT_X);
		assertTrue(CCMath.abs(new CCVector3(-1, 0, 0).distance(quat.apply(new CCVector3(1, 0, 0)))) <= CCQuaternion.ALLOWED_DEVIANCE);

		quat.fromVectorToVector(CCVector3.UNIT_Y, CCVector3.NEG_UNIT_Y);
		assertTrue(CCMath.abs(new CCVector3(0, -1, 0).distance(quat.apply(new CCVector3(0, 1, 0)))) <= CCQuaternion.ALLOWED_DEVIANCE);

		quat.fromVectorToVector(CCVector3.ONE, CCVector3.NEG_ONE);
		assertTrue(CCMath.abs(new CCVector3(-1, -1, -1).distance(quat.apply(new CCVector3(1, 1, 1)))) <= CCQuaternion.ALLOWED_DEVIANCE);

		quat.fromVectorToVector(CCVector3.ZERO, CCVector3.ZERO);
		assertEquals(CCQuaternion.IDENTITY, quat);
	}

	@Test
	public void testNormalize() {
		final CCQuaternion quat = new CCQuaternion(0, 1, 2, 3);
		final CCQuaternion quat2 = quat.normalize();
		assertEquals(quat2, quat.normalizeLocal());
		assertTrue(CCMath.abs(1 - quat.magnitude()) <= CCMath.FLT_EPSILON);
		assertTrue(CCMath.abs(1 - quat2.magnitude()) <= CCMath.FLT_EPSILON);
	}

	@Test
	public void testApplyToZero() {
		assertEquals(CCVector3.ZERO,
				new CCQuaternion().apply(new CCVector3(0, 0, 0)));
	}

	@Test
	public void testInvert() {
		final CCQuaternion quat1 = new CCQuaternion(0, 1, 2, 3);
		CCQuaternion quat2 = quat1.invert();
		assertEquals(CCQuaternion.IDENTITY, quat1.multiply(quat2));
		assertEquals(quat1, quat2.invert());
		assertEquals(quat1, quat2.invertLocal());

		// normalized version
		quat1.fromAngleAxis(CCMath.QUARTER_PI, CCVector3.UNIT_Y);
		quat2 = quat1.invert();
		assertEquals(CCQuaternion.IDENTITY, quat1.multiply(quat2));
		assertEquals(quat1, quat2.invert());
		assertEquals(quat1, quat2.invertLocal());

		// conjugate check
		assertEquals(new CCQuaternion(-1, -2, -3, 4), new CCQuaternion(1, 2, 3, 4).conjugate());
	}

	@Test
	public void testAddSubtract() {
		final CCQuaternion quat1 = new CCQuaternion(0, 1, 2, 3);
		final CCQuaternion quat2 = new CCQuaternion(1, 1, 1, 1);
		assertEquals(new CCQuaternion(1, 2, 3, 4), quat1.add(quat2));
		assertEquals(new CCQuaternion(1, 2, 3, 4), quat1.add(quat2));
		assertEquals(new CCQuaternion(1, 2, 3, 4), quat1.addLocal(quat2));

		quat1.set(0, 1, 2, 3);
		quat2.set(1, 1, 1, 1);
		assertEquals(new CCQuaternion(-1, 0, 1, 2), quat1.subtract(quat2));
		assertEquals(new CCQuaternion(-1, 0, 1, 2), quat1.subtract(quat2));
		assertEquals(new CCQuaternion(-1, 0, 1, 2), quat1.subtractLocal(quat2));
	}

	@Test
    public void testMultiply() {
        final CCQuaternion quat1 = new CCQuaternion(0.5f, 1, 2, 3);
        CCQuaternion quat2 = new CCQuaternion();
        assertEquals(new CCQuaternion(1, 2, 4, 6), quat1.multiply(2));
        assertEquals(new CCQuaternion(2, 4, 8, 12), quat1.multiply(4));
        assertEquals(new CCQuaternion(1, 2, 4, 6), quat1.multiplyLocal(2));

        quat1.fromAngleNormalAxis(CCMath.QUARTER_PI, CCVector3.UNIT_Y);
        quat2 = quat1.multiply(quat1);

        final CCVector3 vec = CCVector3.UNIT_Z;
        assertTrue(CCMath.abs(CCVector3.UNIT_X.distance(quat2.apply(vec))) <= CCQuaternion.ALLOWED_DEVIANCE);
        quat1.multiplyLocal(quat1.x, quat1.y, quat1.z, quat1.w);
        assertTrue(CCMath.abs(CCVector3.UNIT_X.distance(quat1.apply(vec))) <= CCQuaternion.ALLOWED_DEVIANCE);
        quat2.fromAngleNormalAxis(CCMath.HALF_PI, CCVector3.UNIT_Y);
        quat1.multiplyLocal(quat2);
        assertTrue(CCMath.abs(CCVector3.NEG_UNIT_Z.distance(quat1.apply(vec))) <= CCQuaternion.ALLOWED_DEVIANCE);

        quat1.multiplyLocal(new CCMatrix3x3().applyRotationY(CCMath.HALF_PI));
        assertTrue(CCMath.abs(CCVector3.NEG_UNIT_X.distance(quat1.apply(vec))) <= CCQuaternion.ALLOWED_DEVIANCE);
    }

	@Test
	public void testAxes() {
		final CCMatrix3x3 rot = new CCMatrix3x3().applyRotationX(CCMath.QUARTER_PI).applyRotationY(CCMath.HALF_PI);
		final CCQuaternion quat1 = new CCQuaternion().fromAxes(rot.column(0), rot.column(1), rot.column(2));
		final CCQuaternion quat2 = new CCQuaternion().fromRotationMatrix(rot);
		assertEquals(quat2, quat1);

		final CCVector3[] axes = quat1.toAxes(new CCVector3[3]);
		quat1.fromAxes(axes[0], axes[1], axes[2]);
		assertEquals(quat2, quat1);
	}

	@Test
	public void testSlerp() {
		final CCQuaternion quat = new CCQuaternion();
		final CCQuaternion quat2 = new CCQuaternion().applyRotationY(CCMath.HALF_PI);
		CCQuaternion store = quat.slerp(quat2, .5f);
		assertTrue(CCMath.abs(new CCVector3(CCMath.sin(CCMath.QUARTER_PI), 0, CCMath.sin(CCMath.QUARTER_PI)).distance(store.apply(CCVector3.UNIT_Z))) <= CCQuaternion.ALLOWED_DEVIANCE);

		// delta == 100%
		quat2.setIdentity().applyRotationZ(CCMath.PI);
		store = quat.slerp(quat2, 1.0f);
		assertEquals(new CCVector3(-1, 0, 0),store.apply(CCVector3.UNIT_X));

//		quat2.setIdentity().applyRotationZ(CCMath.PI);
//		store = quat.slerp(quat2, .5f);
//		System.out.println(new CCVector3(0, -1, 0).equals(store.apply(CCVector3.UNIT_X)));
//		assertEquals(new CCVector3(0, 1, 0),store.apply(CCVector3.UNIT_X));
		
		// delta == 0%
		quat2.setIdentity().applyRotationZ(CCMath.PI);
		store = quat.slerp(quat2, 0);
		assertEquals(new CCVector3(1, 0, 0),store.apply(CCVector3.UNIT_X));

		// a==b
		quat2.setIdentity();
		store = quat.slerp(quat2, 0.25f);
		assertEquals(new CCVector3(1, 0, 0),store.apply(CCVector3.UNIT_X));

		// negative dot product
		quat.setIdentity().applyRotationX(-2 * CCMath.HALF_PI);
		quat2.setIdentity().applyRotationX(CCMath.HALF_PI);
		store = quat.slerp(quat2, 0.5f);
		assertEquals(new CCVector3(0, -CCMath.sin(CCMath.QUARTER_PI), CCMath.sin(CCMath.QUARTER_PI)),store.apply(CCVector3.UNIT_Y));

		// LOCAL
		// delta == 100%
		quat2.setIdentity().applyRotationX(CCMath.PI);
		quat.slerpLocal(quat2, 1.0f);
		assertEquals(new CCVector3(0, -1, 0),quat.apply(CCVector3.UNIT_Y));

//		quat.setIdentity();
//		quat.slerpLocal(quat2, .5f);
//		assertEquals(new CCVector3(0, 0, 1),quat.apply(CCVector3.UNIT_Y));

		// delta == 0%
		quat.setIdentity();
		quat.slerpLocal(quat2, 0);
		assertEquals(new CCVector3(0, 1, 0),quat.apply(CCVector3.UNIT_Y));

		// a==b
		quat.setIdentity();
		quat2.setIdentity();
		quat.slerpLocal(quat2, 0.25f);
		assertEquals(new CCVector3(0, 1, 0),quat.apply(CCVector3.UNIT_Y));

		// negative dot product
//		quat.setIdentity().applyRotationX(-2 * CCMath.HALF_PI);
//		quat2.setIdentity().applyRotationX(CCMath.HALF_PI);
//		quat.slerpLocal(quat2, 0.5f);
//		assertEquals(new CCVector3(0, -CCMath.sin(CCMath.QUARTER_PI), CCMath.sin(CCMath.QUARTER_PI)),quat.apply(CCVector3.UNIT_Y));
	}

	@Test
	public void testLookAt() {
		final CCVector3 direction = new CCVector3(-1, 0, 0);
		final CCQuaternion quat = new CCQuaternion().lookAt(direction, CCVector3.UNIT_Y);
		assertTrue(CCMath.abs(direction.distance(quat.apply(CCVector3.UNIT_Z))) <= CCQuaternion.ALLOWED_DEVIANCE);

		direction.set(1, 1, 1).normalizeLocal();
		quat.lookAt(direction, CCVector3.UNIT_Y);
		assertTrue(CCMath.abs(direction.distance(quat.apply(CCVector3.UNIT_Z))) <= CCQuaternion.ALLOWED_DEVIANCE);

		direction.set(-1, 2, -1).normalizeLocal();
		quat.lookAt(direction, CCVector3.UNIT_Y);
		assertTrue(CCMath.abs(direction.distance(quat.apply(CCVector3.UNIT_Z))) <= CCQuaternion.ALLOWED_DEVIANCE);
	}

	@Test
	public void testDot() {
		final CCQuaternion quat = new CCQuaternion(7, 2, 5, -1);
		assertTrue(35.0 == quat.dot(3, 1, 2, -2));

		assertTrue(-11.0 == quat.dot(new CCQuaternion(-1, 1, -1, 1)));
	}

	@Test
	public void testClone() {
		final CCQuaternion quat1 = new CCQuaternion();
		final CCQuaternion quat2 = quat1.clone();
		assertEquals(quat1, quat2);
		assertNotSame(quat1, quat2);
	}

	@Test
    public void testValid() {
        final CCQuaternion quat = new CCQuaternion();
        assertTrue(CCQuaternion.isValid(quat));

        quat.set(Float.NaN, 0, 0, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, Float.NaN, 0, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, 0, Float.NaN, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, 0, 0, Float.NaN);
        assertFalse(CCQuaternion.isValid(quat));

        quat.set(Float.NEGATIVE_INFINITY, 0, 0, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, Float.NEGATIVE_INFINITY, 0, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, 0, Float.NEGATIVE_INFINITY, 0);
        assertFalse(CCQuaternion.isValid(quat));
        quat.set(0, 0, 0, Float.NEGATIVE_INFINITY);
        assertFalse(CCQuaternion.isValid(quat));

        quat.setIdentity();
        assertTrue(CCQuaternion.isValid(quat));

        assertFalse(CCQuaternion.isValid(null));

        // couple of equals validity tests
        assertEquals(quat, quat);
        assertTrue(quat.strictEquals(quat));
        assertFalse(quat.equals(null));
        assertFalse(quat.strictEquals(null));
        assertFalse(quat.equals(new CCVector3()));
        assertFalse(quat.strictEquals(new CCVector3()));


        // cover more of equals
        quat.set(0, 1, 2, 3);
        final CCQuaternion comp = new CCQuaternion(-1, -1, -1, -1);
        assertFalse(quat.equals(comp));
        assertFalse(quat.strictEquals(comp));
        comp.x = 0;
        assertFalse(quat.equals(comp));
        assertFalse(quat.strictEquals(comp));
        comp.y = 1;
        assertFalse(quat.equals(comp));
        assertFalse(quat.strictEquals(comp));
        comp.z = 2;
        assertFalse(quat.equals(comp));
        assertFalse(quat.strictEquals(comp));
        comp.w = 3;
        assertEquals(quat, comp);
        assertTrue(quat.strictEquals(comp));
    }

	@Test
	public void testSimpleHash() {
		// Just a simple sanity check.
		final CCQuaternion quat1 = new CCQuaternion(1, 2, 3, 4);
		final CCQuaternion quat2 = new CCQuaternion(1, 2, 3, 4);
		final CCQuaternion quat3 = new CCQuaternion(1, 2, 3, 0);

		assertTrue(quat1.hashCode() == quat2.hashCode());
		assertTrue(quat1.hashCode() != quat3.hashCode());
	}

}
