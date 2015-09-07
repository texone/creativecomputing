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

import java.nio.DoubleBuffer;

import org.junit.Test;

public class CCMatrix3Test {

	@Test
	public void testGetSet() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		assertEquals(CCMatrix3x3.IDENTITY, mat3A);

		CCMatrix3x3 mat3C = new CCMatrix3x3();
		mat3C.setIdentity();
		assertTrue(mat3C.isIdentity());

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				final double value = (10 * x + y) / 10.f;
				mat3C.setValue(x, y, value);
				assertTrue(value == mat3C.getValue(x, y));
			}
		}

		mat3C.setIdentity();
		mat3C.set(0.0f, 0.1f, 0.2f, 2.0f, 2.1f, 2.2f, 4.0f, 4.1f, 4.2f);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				assertTrue((20 * x + y) / 10.f == mat3C.getValue(x, y));
			}
		}

		final CCMatrix3x3 store = new CCMatrix3x3(mat3C);
		// catch a few expected exceptions
		try {
			mat3C.getValue(-1, 0);
			fail("getValue(-1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.getValue(0, 3);
			fail("getValue(0, 3) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.getValue(1, -1);
			fail("getValue(1, -1) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.getValue(2, 3);
			fail("getValue(2, 3) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.getValue(3, 0);
			fail("getValue(3, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}

		try {
			mat3C.setValue(-1, 0, 0);
			fail("setValue(-1, 0, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.setValue(0, -1, 0);
			fail("setValue(0, -1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.setValue(1, 3, 0);
			fail("setValue(1, 3, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.setValue(2, -1, 0);
			fail("setValue(2, -1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3C.setValue(3, 0, 0);
			fail("setValue(3, 0, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		// above exceptions shouldn't have altered mat3C
		assertEquals(store, mat3C);
	}

	@Test
	public void testColumns() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		mat3A.setColumn(0, new CCVector3(0, 3, 6));
		mat3A.setColumn(1, new CCVector3(1, 4, 7));
		mat3A.setColumn(2, new CCVector3(2, 5, 8));
		assertEquals(new CCVector3(0, 3, 6), mat3A.column(0));
		assertEquals(new CCVector3(1, 4, 7), mat3A.column(1));
		assertEquals(new CCVector3(2, 5, 8), mat3A.column(2));
		try {
			mat3A.column(-1);
			fail("getColumn(-1, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.column(3);
			fail("getColumn(3, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.setColumn(-1, new CCVector3());
			fail("setColumn(-1, Vector3) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.setColumn(4, new CCVector3());
			fail("setColumn(4, Vector3) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}

		mat3A.fromAxes(new CCVector3(1, 2, 3), new CCVector3(4, 5, 6),
				new CCVector3(7, 8, 9));
		mat3A.setColumn(0, new CCVector3(1, 2, 3));
		mat3A.setColumn(1, new CCVector3(4, 5, 6));
		mat3A.setColumn(2, new CCVector3(7, 8, 9));
	}

	@Test
	public void testRows() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		mat3A.setRow(0, new CCVector3(0, 1, 2));
		mat3A.setRow(1, new CCVector3(3, 4, 5));
		mat3A.setRow(2, new CCVector3(6, 7, 8));
		assertEquals(new CCVector3(0, 1, 2), mat3A.getRow(0));
		assertEquals(new CCVector3(3, 4, 5), mat3A.getRow(1));
		assertEquals(new CCVector3(6, 7, 8), mat3A.getRow(2));
		try {
			mat3A.getRow(-1);
			fail("getRow(-1, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.getRow(3);
			fail("getRow(3, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.setRow(-1, new CCVector3());
			fail("setRow(-1, Vector3) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat3A.setRow(3, new CCVector3());
			fail("setRow(3, Vector3]) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
	}

	@Test
	public void testSetRotation() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		// rotate identity 90 degrees around Y
		final double a = CCMath.HALF_PI;
		final CCQuaternion quaternion = new CCQuaternion();
		quaternion.fromAngleAxis(a, CCVector3.UNIT_Y);
		mat3A.set(quaternion);

		assertEquals(new CCMatrix3x3( //
				CCMath.cos(a), 0, CCMath.sin(a), //
				0, 1, 0, //
				-CCMath.sin(a), 0, CCMath.cos(a)), mat3A);
	}

	@Test
	public void testFromBuffer() {
		final DoubleBuffer fb = DoubleBuffer.allocate(9);
		fb.put(new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
		fb.flip();
		// row major
		final CCMatrix3x3 mat3A = new CCMatrix3x3().fromDoubleBuffer(fb);
		assertTrue(0 == mat3A._m00);
		assertTrue(1 == mat3A._m01);
		assertTrue(2 == mat3A._m02);
		assertTrue(3 == mat3A._m10);
		assertTrue(4 == mat3A._m11);
		assertTrue(5 == mat3A._m12);
		assertTrue(6 == mat3A._m20);
		assertTrue(7 == mat3A._m21);
		assertTrue(8 == mat3A._m22);

		// column major
		fb.rewind();
		mat3A.setIdentity();
		mat3A.fromDoubleBuffer(fb, false);
		assertTrue(0 == mat3A._m00);
		assertTrue(3 == mat3A._m01);
		assertTrue(6 == mat3A._m02);
		assertTrue(1 == mat3A._m10);
		assertTrue(4 == mat3A._m11);
		assertTrue(7 == mat3A._m12);
		assertTrue(2 == mat3A._m20);
		assertTrue(5 == mat3A._m21);
		assertTrue(8 == mat3A._m22);
	}

	@Test
	public void testToBuffer() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final double[] colmajor = { 0, 3, 6, 1, 4, 7, 2, 5, 8 };

		final CCMatrix3x3 mat3A = new CCMatrix3x3().fromArray(values);

		// row major
		final DoubleBuffer fb = mat3A.toBuffer(DoubleBuffer.allocate(9));
		fb.flip();
		for (int i = 0; i < 9; i++) {
			assertTrue(values[i] == fb.get());
		}

		// column major
		fb.rewind();
		mat3A.toBuffer(fb, false);
		fb.flip();
		for (int i = 0; i < 9; i++) {
			assertTrue(colmajor[i] == fb.get());
		}
	}

	@Test
	public void testFromArray() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final CCMatrix3x3 mat3A = new CCMatrix3x3();

		// row major
		mat3A.fromArray(values);
		assertTrue(0 == mat3A._m00);
		assertTrue(1 == mat3A._m01);
		assertTrue(2 == mat3A._m02);
		assertTrue(3 == mat3A._m10);
		assertTrue(4 == mat3A._m11);
		assertTrue(5 == mat3A._m12);
		assertTrue(6 == mat3A._m20);
		assertTrue(7 == mat3A._m21);
		assertTrue(8 == mat3A._m22);

		// column major
		mat3A.setIdentity();
		mat3A.fromArray(values, false);
		assertTrue(0 == mat3A._m00);
		assertTrue(3 == mat3A._m01);
		assertTrue(6 == mat3A._m02);
		assertTrue(1 == mat3A._m10);
		assertTrue(4 == mat3A._m11);
		assertTrue(7 == mat3A._m12);
		assertTrue(2 == mat3A._m20);
		assertTrue(5 == mat3A._m21);
		assertTrue(8 == mat3A._m22);
	}

	@Test
	public void testToArray() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		final CCMatrix3x3 mat3A = new CCMatrix3x3().fromArray(values);

		// row major
		final double[] dbls1 = mat3A.toArray(new double[9]);
		for (int i = 0; i < 9; i++) {
			assertTrue(values[i] == dbls1[i]);
		}

		// column major
		final double[] colmajor = { 0, 3, 6, 1, 4, 7, 2, 5, 8 };
		mat3A.toArray(dbls1, false);
		for (int i = 0; i < 9; i++) {
			assertTrue(colmajor[i] == dbls1[i]);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadArray() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		mat3A.toArray(new double[4]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadAnglesArray() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		mat3A.toAngles(new double[2]);
	}

	@Test
	public void testAngleAxis() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		// rotate identity 90 degrees around X
		final double angle = CCMath.HALF_PI;
		mat3A.fromAngleAxis(CCMath.HALF_PI, CCVector3.UNIT_X);
		assertEquals(new CCMatrix3x3( //
				1, 0, 0, //
				0, CCMath.cos(angle), -CCMath.sin(angle), //
				0, CCMath.sin(angle), CCMath.cos(angle)), mat3A);
	}

	@Test
	public void testRotations() {
		CCVector3 rotated = new CCVector3(1, 1, 1);
		CCVector3 expected = new CCVector3(1, 1, 1);

		// rotated
		final CCMatrix3x3 mat3A = new CCMatrix3x3().fromAngles(CCMath.HALF_PI,
				CCMath.QUARTER_PI, CCMath.PI);
		rotated = mat3A.applyPost(rotated);

		// expected - post
		final CCMatrix3x3 worker = new CCMatrix3x3().fromAngleAxis(CCMath.HALF_PI,
				CCVector3.UNIT_X);
		expected = worker.applyPost(expected);
		worker.fromAngleAxis(CCMath.PI, CCVector3.UNIT_Z);
		expected = worker.applyPost(expected);
		worker.fromAngleAxis(CCMath.QUARTER_PI, CCVector3.UNIT_Y);
		expected = worker.applyPost(expected);

		// test how close it came out
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Try a new way with new angles...
		final CCMatrix3x3 mat3B = new CCMatrix3x3().fromAngles(CCMath.QUARTER_PI,
				CCMath.PI, CCMath.HALF_PI);
		rotated.set(1, 1, 1);
		rotated = mat3B.applyPost(rotated);

		// expected
		expected.set(1, 1, 1);
		worker.setIdentity();
		// put together matrix, then apply to vector, so YZX
		worker.applyRotationY(CCMath.PI);
		worker.applyRotationZ(CCMath.HALF_PI);
		worker.applyRotationX(CCMath.QUARTER_PI);
		expected = worker.applyPost(expected);

		// test how close it came out
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// test axis rotation methods against general purpose
		// X AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationX(CCMath.QUARTER_PI)
				.applyPost(expected);
		rotated = worker.setIdentity()
				.applyRotation(CCMath.QUARTER_PI, 1, 0, 0).applyPost(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Y AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationY(CCMath.QUARTER_PI)
				.applyPost(expected);
		rotated = worker.setIdentity()
				.applyRotation(CCMath.QUARTER_PI, 0, 1, 0).applyPost(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Z AXIS
		expected.set(1, 1, 1);
		rotated.set(1, 1, 1);
		expected = worker.setIdentity().applyRotationZ(CCMath.QUARTER_PI)
				.applyPost(expected);
		rotated = worker.setIdentity()
				.applyRotation(CCMath.QUARTER_PI, 0, 0, 1).applyPost(rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// test toAngles - not necessarily the same values as fromAngles, but
		// should be same resulting Matrix.
		mat3A.fromAngles(CCMath.HALF_PI, CCMath.QUARTER_PI, CCMath.PI);
		final double[] angles = mat3A.toAngles(null);
		worker.fromAngles(angles[0], angles[1], angles[2]);
		assertEquals(mat3A, worker);

		mat3A.fromAngles(CCMath.HALF_PI, CCMath.QUARTER_PI, CCMath.HALF_PI);
		mat3A.toAngles(angles);
		worker.fromAngles(angles[0], angles[1], angles[2]);
		assertEquals(mat3A, worker);

		mat3A.fromAngles(CCMath.HALF_PI, CCMath.QUARTER_PI, -CCMath.HALF_PI);
		mat3A.toAngles(angles);
		worker.fromAngles(angles[0], angles[1], angles[2]);
		assertEquals(mat3A, worker);
	}

	@Test
	public void testMultiplyDiagonal() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3();
		CCMatrix3x3 result = mat3A.multiplyDiagonalPost(new CCVector3(2, 4, 6));
		assertEquals(new CCMatrix3x3( //
				2, 0, 0, //
				0, 4, 0, //
				0, 0, 6), result);
		result = mat3A.multiplyDiagonalPre(new CCVector3(-2, -4, -6));
		assertEquals(new CCMatrix3x3( //
				-2, 0, 0, //
				0, -4, 0, //
				0, 0, -6), result);

		final double a = CCMath.HALF_PI;
		mat3A.applyRotationY(a);
		result = mat3A.multiplyDiagonalPost(new CCVector3(2, 4, 6));
		assertEquals(new CCMatrix3x3( //
				2 * CCMath.cos(a), 4 * 0, 6 * CCMath.sin(a), //
				2 * 0, 4 * 1, 6 * 0, //
				2 * -CCMath.sin(a), 4 * 0, 6 * CCMath.cos(a)), result);
		result = mat3A.multiplyDiagonalPre(new CCVector3(-2, -4, -6));
		assertEquals(new CCMatrix3x3( //
				-2 * CCMath.cos(a), -2 * 0, -2 * CCMath.sin(a), //
				-4 * 0, -4 * 1, -4 * 0, //
				-6 * -CCMath.sin(a), -6 * 0, -6 * CCMath.cos(a)), result);
	}

	@Test
	public void testMultiply() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3( //
				0.01f, 0.1f, 0.2f, //
				1.0f, 1.1f, 1.2f, //
				2.0f, 2.1f, 2.2f);
		mat3A.multiplyLocal(2);
		assertEquals(new CCMatrix3x3( //
				0.02f, 0.2f, 0.4f, //
				2.0f, 2.2f, 2.4f, //
				4.0f, 4.2f, 4.4f), mat3A);

		final CCMatrix3x3 mat3B = new CCMatrix3x3( //
				0.5, 1, 2, //
				4, 5, 6, //
				8, 9, 10);
		final CCMatrix3x3 result = mat3A.multiply(mat3B);
		System.out.println(0.02 * 0.5 + 0.2 * 4 + 0.4 * 8 +"=="+ result._m00);
		assertTrue(CCMath.abs(0.02 * 0.5   + 0.2 * 4 + 0.4 * 8 - result._m00) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 1 	+ 0.2 * 5 + 0.4 * 9 - result._m01) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 2 	+ 0.2 * 6 + 0.4 * 10 - result._m02) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 0.5   + 2.2 * 4 + 2.4 * 8 - result._m10) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 1 	+ 2.2 * 5 + 2.4 * 9 - result._m11) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 2 	+ 2.2 * 6 + 2.4 * 10 - result._m12) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 0.5   + 4.2 * 4 + 4.4 * 8 - result._m20) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 1 	+ 4.2 * 5 + 4.4 * 9 - result._m21) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 2 	+ 4.2 * 6 + 4.4 * 10 - result._m22) < 0.00001);
		mat3A.multiplyLocal(mat3B);
		assertTrue(CCMath.abs(0.02 * 0.5   + 0.2 * 4 + 0.4 * 8 - mat3A._m00) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 1 	+ 0.2 * 5 + 0.4 * 9 - mat3A._m01) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 2 	+ 0.2 * 6 + 0.4 * 10 - mat3A._m02) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 0.5   + 2.2 * 4 + 2.4 * 8 - mat3A._m10) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 1 	+ 2.2 * 5 + 2.4 * 9 - mat3A._m11) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 2 	+ 2.2 * 6 + 2.4 * 10 - mat3A._m12) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 0.5   + 4.2 * 4 + 4.4 * 8 - mat3A._m20) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 1 	+ 4.2 * 5 + 4.4 * 9 - mat3A._m21) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 2 	+ 4.2 * 6 + 4.4 * 10 - mat3A._m22) < 0.00001);
	}

	@Test
	public void testAddSubtract() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3( //
				0.0f, 0.1f, 0.2f, //
				1.0f, 1.1f, 1.2f, //
				2.0f, 2.1f, 2.2f);

		final CCMatrix3x3 result1 = mat3A.add(new CCMatrix3x3(//
				1, 2, 3,//
				5, 6, 7, //
				9, 10, 11));
		assertEquals(new CCMatrix3x3( //
				1.0f, 2.1f, 3.2f, //
				6.0f, 7.1f, 8.2f, //
				11.0f, 12.1f, 13.2f), result1);

		final CCMatrix3x3 result2 = result1.subtract(new CCMatrix3x3(//
				1, 2, 3, //
				5, 6, 7, //
				9, 10, 11));
		assertEquals(mat3A, result2);
		result2.addLocal(CCMatrix3x3.IDENTITY);
		assertEquals(new CCMatrix3x3( //
				1.0f, 0.1f, 0.2f, //
				1.0f, 2.1f, 1.2f, //
				2.0f, 2.1f, 3.2f), result2);

		result1.subtractLocal(CCMatrix3x3.IDENTITY);
		assertEquals(new CCMatrix3x3( //
				0.0f, 2.1f, 3.2f, //
				6.0f, 6.1f, 8.2f, //
				11.0f, 12.1f, 12.2f), result1);
	}

	@Test
	public void testScale() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3( //
				0.01f, 0.1f, 0.2f, //
				1.0f, 1.1f, 1.2f, //
				2.0f, 2.1f, 2.2f);
		final CCMatrix3x3 result = mat3A.scale(new CCVector3(-1, 2, 4));
		assertEquals(new CCMatrix3x3( //
				0.01f * -1f, 0.1f * 2, 0.2f * 4, //
				1.0f * -1f, 1.1f * 2f, 1.2f * 4, //
				2.0f * -1f, 2.1f * 2f, 2.2f * 4), result);

		result.scaleLocal(new CCVector3(-1f, 0.5f, 1 / 4.f));
		assertEquals(mat3A, result);
	}

	@Test
	public void testTranspose() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3( //
				0.01f, 0.1f, 0.2f, //
				1.0f, 1.1f, 1.2f, //
				2.0f, 2.1f, 2.2f);
		final CCMatrix3x3 result = mat3A.transpose();
		assertEquals(new CCMatrix3x3( //
				0.01f, 1.0f, 2.0f, //
				0.1f, 1.1f, 2.1f, //
				0.2f, 1.2f, 2.2f), result);
		assertEquals(new CCMatrix3x3( //
				0.01f, 0.1f, 0.2f, //
				1.0f, 1.1f, 1.2f, //
				2.0f, 2.1f, 2.2f), result.transposeLocal());
		// coverage
		final CCMatrix3x3 result2 = result.transposeLocal().transpose();
		assertEquals(mat3A, result2);
	}

	@Test
	public void testInvert() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3()
				.applyRotationX(CCMath.QUARTER_PI);
		final CCMatrix3x3 inverted = mat3A.invert();
		assertEquals(CCMatrix3x3.IDENTITY, mat3A.multiply(inverted));
		assertEquals(mat3A, inverted.invertLocal());
	}

	@Test(expected = ArithmeticException.class)
	public void testBadInvert() {
		final CCMatrix3x3 mat3A = new CCMatrix3x3(0, 0, 0, 0, 0, 0, 0, 0, 0);
		mat3A.invertLocal();
	}

	@Test
	public void testAdjugate() {
		final double //
		a = -3, b = 2, c = -5, //
		d = -1, e = 0, f = -2, //
		g = 3, h = -4, i = 1;

		final CCMatrix3x3 mat3A = new CCMatrix3x3( //
				a, b, c, //
				d, e, f, //
				g, h, i);

		final CCMatrix3x3 testValue = new CCMatrix3x3( //
				e * i - h * f, -(b * i - h * c), b * f - e * c, //
				-(d * i - g * f), a * i - g * c, -(a * f - d * c),//
				d * h - g * e, -(a * h - g * b), a * e - d * b);

		assertEquals(testValue, mat3A.adjugate());
		assertEquals(testValue, mat3A.adjugateLocal());
	}

	@Test
	public void testDeterminant() {
		{
			final double //
			a = -3, b = 2, c = -5, //
			d = -1, e = 0, f = -2, //
			g = 3, h = -4, i = 1;

			final CCMatrix3x3 mat3A = new CCMatrix3x3( //
					a, b, c, //
					d, e, f, //
					g, h, i);
			final double determinant = a * e * i + b * f * g + c * d * h - c * e
					* g - b * d * i - a * f * h;
			assertTrue(determinant == mat3A.determinant());
		}

		{
			final double //
			a = -1, b = 2, c = -3, //
			d = 4, e = -5, f = 6, //
			g = -7, h = 8, i = -9;

			final CCMatrix3x3 mat3A = new CCMatrix3x3( //
					a, b, c, //
					d, e, f, //
					g, h, i);
			final double determinant = a * e * i + b * f * g + c * d * h - c * e
					* g - b * d * i - a * f * h;
			assertTrue(determinant == mat3A.determinant());
		}
	}

	@Test
	public void testClone() {
		final CCMatrix3x3 mat1 = new CCMatrix3x3();
		final CCMatrix3x3 mat2 = mat1.clone();
		assertEquals(mat1, mat2);
		assertNotSame(mat1, mat2);
	}

	@Test
	public void testValid() {
		final CCMatrix3x3 mat3 = new CCMatrix3x3();
		assertTrue(CCMatrix3x3.isValid(mat3));
		for (int i = 0; i < 9; i++) {
			mat3.setIdentity();
			mat3.setValue(i / 3, i % 3, Double.NaN);
			assertFalse(CCMatrix3x3.isValid(mat3));
			mat3.setIdentity();
			mat3.setValue(i / 3, i % 3, Double.POSITIVE_INFINITY);
			assertFalse(CCMatrix3x3.isValid(mat3));
		}

		mat3.setIdentity();
		assertTrue(CCMatrix3x3.isValid(mat3));

		assertFalse(CCMatrix3x3.isValid(null));

		// couple of equals validity tests
		assertEquals(mat3, mat3);
		assertTrue(mat3.strictEquals(mat3));
		assertFalse(mat3.equals(null));
		assertFalse(mat3.strictEquals(null));
		assertFalse(mat3.equals(new CCVector3()));
		assertFalse(mat3.strictEquals(new CCVector3()));

		// cover more of equals
		mat3.set(0, 1, 2, 3, 4, 5, 6, 7, 8);
		final CCMatrix3x3 comp = new CCMatrix3x3(-1, -1, -1, -1, -1, -1, -1, -1, -1);
		assertFalse(mat3.equals(comp));
		assertFalse(mat3.strictEquals(comp));
		for (int i = 0; i < 8; i++) {
			comp.setValue(i / 3, i % 3, i);
			assertFalse(mat3.equals(comp));
			assertFalse(mat3.strictEquals(comp));
		}
	}

	@Test
	public void testSimpleHash() {
		// Just a simple sanity check.
		final CCMatrix3x3 mat1 = new CCMatrix3x3(1, 2, 3, 4, 5, 6, 7, 8, 9);
		final CCMatrix3x3 mat2 = new CCMatrix3x3(1, 2, 3, 4, 5, 6, 7, 8, 9);
		final CCMatrix3x3 mat3 = new CCMatrix3x3(1, 2, 3, 4, 5, 6, 7, 8, 0);

		assertTrue(mat1.hashCode() == mat2.hashCode());
		assertTrue(mat1.hashCode() != mat3.hashCode());
	}

	@Test
	public void testOrthonormal() {
		final CCMatrix3x3 mat3 = new CCMatrix3x3();
		assertTrue(mat3.isOrthonormal());
		// just rotation
		mat3.applyRotationX(CCMath.QUARTER_PI);
		assertTrue(mat3.isOrthonormal());
		// non-uniform scale
		mat3.setIdentity();
		mat3.scaleLocal(new CCVector3(1, 2, 3));
		assertFalse(mat3.isOrthonormal());
		// non-uniform scale + rotation
		mat3.setIdentity();
		mat3.scaleLocal(new CCVector3(1, 2, 3));
		mat3.applyRotationX(CCMath.QUARTER_PI);
		assertFalse(mat3.isOrthonormal());
	}

	@Test
	public void testApplyVector3() {
		final CCMatrix3x3 mat3 = new CCMatrix3x3().applyRotationX(CCMath.HALF_PI);
		final CCVector3 vec3 = new CCVector3(0, 1, 0);
		CCVector3 result = mat3.applyPost(vec3);
		assertTrue(CCMath.abs(new CCVector3(0, 0, 1).distance(result)) <= CCMath.FLT_EPSILON);
		vec3.set(0, 1, 1);
		result = mat3.applyPost(vec3);
		assertTrue(CCMath.abs(new CCVector3(0, -1, 1).distance(result)) <= CCMath.FLT_EPSILON);

		vec3.set(0, 1, 1);
		result = mat3.applyPre(vec3);
		assertTrue(CCMath.abs(new CCVector3(0, 1, -1).distance(result)) <= CCMath.FLT_EPSILON);

		vec3.set(1, 1, 1);
		assertTrue(CCMath.abs(new CCVector3(1, 1, -1).distance(mat3.applyPre(vec3))) <= CCMath.FLT_EPSILON);
	}

	@Test
	public void testStartEnd() {
		final CCMatrix3x3 mat3 = new CCMatrix3x3();
		mat3.fromStartEndLocal(CCVector3.UNIT_X, CCVector3.UNIT_Y); // should be
																	// a 90
																	// degree
																	// turn
																	// around Z
		assertEquals(new CCVector3(-1, 1, 1),mat3.applyPost(new CCVector3(1, 1, 1)));

		// coverage
		mat3.fromStartEndLocal(new CCVector3(1, 0, 0), new CCVector3(1 + Double.MIN_VALUE, 0, 0));
		assertTrue(mat3.applyPost(CCVector3.ONE).distance(CCVector3.ONE) < CCMath.ZERO_TOLERANCE);
		mat3.fromStartEndLocal(new CCVector3(0, 1, 0), new CCVector3(0,1 + Double.MIN_VALUE, 0));
		assertTrue(mat3.applyPost(CCVector3.ONE).distance(CCVector3.ONE) < CCMath.ZERO_TOLERANCE);
		mat3.fromStartEndLocal(new CCVector3(0, 0, 1), new CCVector3(0, 0,1 + Double.MIN_VALUE));
		assertTrue(mat3.applyPost(CCVector3.ONE).distance(CCVector3.ONE) < CCMath.ZERO_TOLERANCE);
	}

	@Test
	public void testLookAt() {
		final CCVector3 direction = new CCVector3(-1, 0, 0);
		final CCMatrix3x3 mat3 = new CCMatrix3x3().lookAt(direction, CCVector3.UNIT_Y);
		assertEquals(direction, mat3.applyPost(CCVector3.UNIT_Z));

		direction.set(1, 1, 1).normalizeLocal();
		mat3.lookAt(direction, CCVector3.UNIT_Y);
		
		assertEquals(direction, mat3.applyPost(CCVector3.UNIT_Z));

		direction.set(-1, 2, -1).normalizeLocal();
		mat3.lookAt(direction, CCVector3.UNIT_Y);
		assertEquals(direction, mat3.applyPost(CCVector3.UNIT_Z));
	}
}
