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

public class CCMatrix4Test {

	@Test
	public void testGetSet() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		assertEquals(CCMatrix4x4.IDENTITY, mat4A);

		mat4A.m00 = 0.0f;
		mat4A.m01 = 0.1f;
		mat4A.m02 = 0.2f;
		mat4A.m03 = 0.3f;
		mat4A.m10 = 1.0f;
		mat4A.m11 = 1.1f;
		mat4A.m12 = 1.2f;
		mat4A.m13 = 1.3f;
		mat4A.m20 = 2.0f;
		mat4A.m21 = 2.1f;
		mat4A.m22 = 2.2f;
		mat4A.m23 = 2.3f;
		mat4A.m30 = 3.0f;
		mat4A.m31 = 3.1f;
		mat4A.m32 = 3.2f;
		mat4A.m33 = 3.3f;

		assertTrue(0.0f == mat4A.m00);
		assertTrue(0.1f == mat4A.m01);
		assertTrue(0.2f == mat4A.m02);
		assertTrue(0.3f == mat4A.m03);
		assertTrue(1.0f == mat4A.m10);
		assertTrue(1.1f == mat4A.m11);
		assertTrue(1.2f == mat4A.m12);
		assertTrue(1.3f == mat4A.m13);
		assertTrue(2.0f == mat4A.m20);
		assertTrue(2.1f == mat4A.m21);
		assertTrue(2.2f == mat4A.m22);
		assertTrue(2.3f == mat4A.m23);
		assertTrue(3.0f == mat4A.m30);
		assertTrue(3.1f == mat4A.m31);
		assertTrue(3.2f == mat4A.m32);
		assertTrue(3.3f == mat4A.m33);

		final CCMatrix4x4 mat4B = new CCMatrix4x4(mat4A);
		assertTrue(0.0f == mat4B.m00);
		assertTrue(0.1f == mat4B.m01);
		assertTrue(0.2f == mat4B.m02);
		assertTrue(0.3f == mat4B.m03);
		assertTrue(1.0f == mat4B.m10);
		assertTrue(1.1f == mat4B.m11);
		assertTrue(1.2f == mat4B.m12);
		assertTrue(1.3f == mat4B.m13);
		assertTrue(2.0f == mat4B.m20);
		assertTrue(2.1f == mat4B.m21);
		assertTrue(2.2f == mat4B.m22);
		assertTrue(2.3f == mat4B.m23);
		assertTrue(3.0f == mat4B.m30);
		assertTrue(3.1f == mat4B.m31);
		assertTrue(3.2f == mat4B.m32);
		assertTrue(3.3f == mat4B.m33);

		final CCMatrix4x4 mat4C = new CCMatrix4x4(
			0.0f, 1.0f, 2.0f, 3.0f, 
			0.1f, 1.1f, 2.1f, 3.1f, 
			0.2f, 1.2f, 2.2f, 3.2f, 
			0.3f, 1.3f, 2.3f, 3.3f
		);
		assertTrue(0.0f == mat4C.m00);
		assertTrue(0.1f == mat4C.m01);
		assertTrue(0.2f == mat4C.m02);
		assertTrue(0.3f == mat4C.m03);
		assertTrue(1.0f == mat4C.m10);
		assertTrue(1.1f == mat4C.m11);
		assertTrue(1.2f == mat4C.m12);
		assertTrue(1.3f == mat4C.m13);
		assertTrue(2.0f == mat4C.m20);
		assertTrue(2.1f == mat4C.m21);
		assertTrue(2.2f == mat4C.m22);
		assertTrue(2.3f == mat4C.m23);
		assertTrue(3.0f == mat4C.m30);
		assertTrue(3.1f == mat4C.m31);
		assertTrue(3.2f == mat4C.m32);
		assertTrue(3.3f == mat4C.m33);

		mat4C.setIdentity();
		assertTrue(mat4C.isIdentity());

		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				final double value = (10 * x + y) / 10.f;
				mat4C.setValue(x, y, value);
				assertTrue(value == mat4C.getValue(x, y));
			}
		}

		mat4C.setIdentity();
		mat4C.set(
			0.0f, 0.1f, 0.2f, 0.3f, 
			2.0f, 2.1f, 2.2f, 2.3f, 
			4.0f, 4.1f, 4.2f, 4.3f, 
			6.0f, 6.1f, 6.2f, 6.3f
		);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				assertTrue((20 * x + y) / 10.f == mat4C.getValue(x, y));
			}
		}

		final CCMatrix4x4 store = new CCMatrix4x4(mat4C);
		// catch a few expected exceptions
		try {
			mat4C.getValue(-1, 0);
			fail("getValue(-1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.getValue(0, 4);
			fail("getValue(0, 4) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.getValue(1, -1);
			fail("getValue(1, -1) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.getValue(2, 4);
			fail("getValue(2, 4) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.getValue(3, -1);
			fail("getValue(3, -1) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.getValue(4, 0);
			fail("getValue(4, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}

		try {
			mat4C.setValue(-1, 0, 0);
			fail("setValue(-1, 0, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.setValue(0, -1, 0);
			fail("setValue(0, -1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.setValue(1, 4, 0);
			fail("setValue(1, 4, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.setValue(2, -1, 0);
			fail("setValue(2, -1, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.setValue(3, 4, 0);
			fail("setValue(3, 4, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4C.setValue(4, 0, 0);
			fail("setValue(4, 0, 0) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		// above exceptions shouldn't have altered mat4C
		assertEquals(store, mat4C);
	}

	@Test
	public void testColumns() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		mat4A.setColumn(0, new CCVector4(0, 4, 8, 12));
		mat4A.setColumn(1, new CCVector4(1, 5, 9, 13));
		mat4A.setColumn(2, new CCVector4(2, 6, 10, 14));
		mat4A.setColumn(3, new CCVector4(3, 7, 11, 15));
		assertEquals(new CCVector4(0, 4, 8, 12), mat4A.getColumn(0, new CCVector4()));
		assertEquals(new CCVector4(1, 5, 9, 13), mat4A.getColumn(1));
		assertEquals(new CCVector4(2, 6, 10, 14), mat4A.getColumn(2));
		assertEquals(new CCVector4(3, 7, 11, 15), mat4A.getColumn(3));
		try {
			mat4A.getColumn(-1, null);
			fail("getColumn(-1, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.getColumn(4, null);
			fail("getColumn(4, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.setColumn(-1, new CCVector4());
			fail("setColumn(-1, double[]) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.setColumn(4, new CCVector4());
			fail("setColumn(4, double[]) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
	}

	@Test
	public void testRows() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		mat4A.setRow(0, new CCVector4(0, 1, 2, 3));
		mat4A.setRow(1, new CCVector4(4, 5, 6, 7));
		mat4A.setRow(2, new CCVector4(8, 9, 10, 11));
		mat4A.setRow(3, new CCVector4(12, 13, 14, 15));
		assertEquals(new CCVector4(0, 1, 2, 3), mat4A.getRow(0, new CCVector4()));
		assertEquals(new CCVector4(4, 5, 6, 7), mat4A.getRow(1));
		assertEquals(new CCVector4(8, 9, 10, 11), mat4A.getRow(2));
		assertEquals(new CCVector4(12, 13, 14, 15), mat4A.getRow(3));
		try {
			mat4A.getRow(-1, null);
			fail("getRow(-1, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.getRow(4, null);
			fail("getRow(4, null) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.setRow(-1, new CCVector4());
			fail("setRow(-1, double[]) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
		try {
			mat4A.setRow(4, new CCVector4());
			fail("setRow(4, double[]) should have thrown IllegalArgumentException.");
		} catch (final IllegalArgumentException e) {
		}
	}

	@Test
	public void testSetRotation() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4(
			0.0f, 1.0f, 2.0f, 3.0f, 
			0.1f, 1.1f, 2.1f, 3.1f, 
			0.2f, 1.2f, 2.2f, 3.2f, 
			0.3f, 1.3f, 2.3f,3.3f
		);
		mat4A.set(CCMatrix3x3.IDENTITY);
		assertTrue(1.0f == mat4A.m00);
		assertTrue(0.0f == mat4A.m01);
		assertTrue(0.0f == mat4A.m02);
		assertTrue(0.3f == mat4A.m03);
		assertTrue(0.0f == mat4A.m10);
		assertTrue(1.0f == mat4A.m11);
		assertTrue(0.0f == mat4A.m12);
		assertTrue(1.3f == mat4A.m13);
		assertTrue(0.0f == mat4A.m20);
		assertTrue(0.0f == mat4A.m21);
		assertTrue(1.0f == mat4A.m22);
		assertTrue(2.3f == mat4A.m23);
		assertTrue(3.0f == mat4A.m30);
		assertTrue(3.1f == mat4A.m31);
		assertTrue(3.2f == mat4A.m32);
		assertTrue(3.3f == mat4A.m33);

		mat4A.setIdentity();
		// rotate identity 90 degrees around Y
		final double a = CCMath.HALF_PI;
		final CCQuaternion quaternion = new CCQuaternion();
		quaternion.fromAngleAxis(a, CCVector3.UNIT_Y);
		mat4A.set(quaternion);

		assertEquals(new CCMatrix4x4( //
				CCMath.cos(a), 0, -CCMath.sin(a), 0, //
				0, 1, 0, 0, //
				CCMath.sin(a), 0, CCMath.cos(a), 0, //
				0, 0, 0, 1), mat4A);
	}

	@Test
	public void testFromBuffer() {
		final DoubleBuffer fb = DoubleBuffer.allocate(16);
		fb.put(new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 });
		fb.flip();
		// row major
		final CCMatrix4x4 mat4A = new CCMatrix4x4().fromDoubleBuffer(fb);
		assertTrue(0 == mat4A.m00);
		assertTrue(1 == mat4A.m01);
		assertTrue(2 == mat4A.m02);
		assertTrue(3 == mat4A.m03);
		assertTrue(4 == mat4A.m10);
		assertTrue(5 == mat4A.m11);
		assertTrue(6 == mat4A.m12);
		assertTrue(7 == mat4A.m13);
		assertTrue(8 == mat4A.m20);
		assertTrue(9 == mat4A.m21);
		assertTrue(10 == mat4A.m22);
		assertTrue(11 == mat4A.m23);
		assertTrue(12 == mat4A.m30);
		assertTrue(13 == mat4A.m31);
		assertTrue(14 == mat4A.m32);
		assertTrue(15 == mat4A.m33);

		// column major
		fb.rewind();
		mat4A.setIdentity();
		mat4A.fromDoubleBuffer(fb, false);
		assertTrue(0 == mat4A.m00);
		assertTrue(4 == mat4A.m01);
		assertTrue(8 == mat4A.m02);
		assertTrue(12 == mat4A.m03);
		assertTrue(1 == mat4A.m10);
		assertTrue(5 == mat4A.m11);
		assertTrue(9 == mat4A.m12);
		assertTrue(13 == mat4A.m13);
		assertTrue(2 == mat4A.m20);
		assertTrue(6 == mat4A.m21);
		assertTrue(10 == mat4A.m22);
		assertTrue(14 == mat4A.m23);
		assertTrue(3 == mat4A.m30);
		assertTrue(7 == mat4A.m31);
		assertTrue(11 == mat4A.m32);
		assertTrue(15 == mat4A.m33);
	}

	@Test
	public void testToBuffer() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		final double[] colmajor = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

		final CCMatrix4x4 mat4A = new CCMatrix4x4().fromArray(values);

		// row major
		final DoubleBuffer fb = mat4A.toDoubleBuffer(DoubleBuffer.allocate(16));
		fb.rewind();
		for (int i = 0; i < 16; i++) {
			assertTrue(values[i] == fb.get());
		}

		// column major
		fb.rewind();
		mat4A.toDoubleBuffer(fb, false);
		fb.rewind();
		for (int i = 0; i < 16; i++) {
			assertTrue(colmajor[i] == fb.get());
		}
	}

	@Test
	public void testFromArray() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		final CCMatrix4x4 mat4A = new CCMatrix4x4();

		// row major
		mat4A.setIdentity();
		mat4A.fromArray(values);
		assertTrue(0 == mat4A.m00);
		assertTrue(1 == mat4A.m01);
		assertTrue(2 == mat4A.m02);
		assertTrue(3 == mat4A.m03);
		assertTrue(4 == mat4A.m10);
		assertTrue(5 == mat4A.m11);
		assertTrue(6 == mat4A.m12);
		assertTrue(7 == mat4A.m13);
		assertTrue(8 == mat4A.m20);
		assertTrue(9 == mat4A.m21);
		assertTrue(10 == mat4A.m22);
		assertTrue(11 == mat4A.m23);
		assertTrue(12 == mat4A.m30);
		assertTrue(13 == mat4A.m31);
		assertTrue(14 == mat4A.m32);
		assertTrue(15 == mat4A.m33);

		// column major
		mat4A.setIdentity();
		mat4A.fromArray(values, false);
		assertTrue(0 == mat4A.m00);
		assertTrue(4 == mat4A.m01);
		assertTrue(8 == mat4A.m02);
		assertTrue(12 == mat4A.m03);
		assertTrue(1 == mat4A.m10);
		assertTrue(5 == mat4A.m11);
		assertTrue(9 == mat4A.m12);
		assertTrue(13 == mat4A.m13);
		assertTrue(2 == mat4A.m20);
		assertTrue(6 == mat4A.m21);
		assertTrue(10 == mat4A.m22);
		assertTrue(14 == mat4A.m23);
		assertTrue(3 == mat4A.m30);
		assertTrue(7 == mat4A.m31);
		assertTrue(11 == mat4A.m32);
		assertTrue(15 == mat4A.m33);
	}

	@Test
	public void testToArray() {
		final double[] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
		final CCMatrix4x4 mat4A = new CCMatrix4x4().fromArray(values);

		// row major
		final double[] dbls1 = mat4A.toArray(new double[16]);
		for (int i = 0; i < 16; i++) {
			assertTrue(values[i] == dbls1[i]);
		}

		// column major
		final double[] colmajor = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };
		mat4A.toArray(dbls1, false);
		for (int i = 0; i < 16; i++) {
			assertTrue(colmajor[i] == dbls1[i]);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadArray() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		mat4A.toArray(new double[9]);
	}

	@Test
	public void testAngleAxis() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		// rotate identity 90 degrees around X
		final double angle = CCMath.HALF_PI;
		mat4A.fromAngleAxis(CCMath.HALF_PI, CCVector3.UNIT_X);
		assertEquals(new CCMatrix4x4( //
				1, 0, 0, 0, //
				0, CCMath.cos(angle), CCMath.sin(angle), 0, //
				0, -CCMath.sin(angle), CCMath.cos(angle), 0, //
				0, 0, 0, 1), mat4A);
	}

	@Test
	public void testRotations() {
		final CCVector4 rotated = new CCVector4();
		final CCVector4 expected = new CCVector4();
		final CCMatrix4x4 worker = new CCMatrix4x4();

		// test axis rotation methods against general purpose
		// X AXIS
		expected.set(1, 1, 1, 1);
		rotated.set(1, 1, 1, 1);
		worker.setIdentity().applyRotationX(CCMath.QUARTER_PI).applyPost(expected, expected);
		worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 1, 0, 0).applyPost(rotated, rotated);
		System.out.println(rotated + " " + expected);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Y AXIS
		expected.set(1, 1, 1, 1);
		rotated.set(1, 1, 1, 1);
		worker.setIdentity().applyRotationY(CCMath.QUARTER_PI).applyPost(expected, expected);
		worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 0, 1, 0).applyPost(rotated, rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);

		// Z AXIS
		expected.set(1, 1, 1, 1);
		rotated.set(1, 1, 1, 1);
		worker.setIdentity().applyRotationZ(CCMath.QUARTER_PI).applyPost(expected, expected);
		worker.setIdentity().applyRotation(CCMath.QUARTER_PI, 0, 0, 1).applyPost(rotated, rotated);
		assertTrue(rotated.distance(expected) <= CCMath.FLT_EPSILON);
	}

	@Test
	public void testTranslation() {
		final CCMatrix4x4 src = new CCMatrix4x4();
		src.applyRotation(CCMath.QUARTER_PI, 1, 0, 0);

		final CCMatrix4x4 trans = new CCMatrix4x4();
		trans.setColumn(3, new CCVector4(1, 2, 3, 1));
		final CCMatrix4x4 transThenRotate = src.multiply(trans, null);
		final CCMatrix4x4 rotateThenTrans = trans.multiply(src, null);

		final CCMatrix4x4 pre1 = new CCMatrix4x4(src).applyTranslationPre(1, 2, 3);
		final CCMatrix4x4 post1 = new CCMatrix4x4(src).applyTranslationPost(1, 2, 3);

		assertEquals(transThenRotate, pre1);
		assertEquals(rotateThenTrans, post1);
	}

	@Test
	public void testMultiplyDiagonal() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4();
		CCMatrix4x4 result = mat4A.multiplyDiagonalPost(new CCVector4(2, 4, 6, 8));
		assertEquals(new CCMatrix4x4( //
				2, 0, 0, 0, //
				0, 4, 0, 0, //
				0, 0, 6, 0, //
				0, 0, 0, 8), result);
		mat4A.multiplyDiagonalPre(new CCVector4(-2, -4, -6, -8), result);
		assertEquals(new CCMatrix4x4( //
				-2, 0, 0, 0, //
				0, -4, 0, 0, //
				0, 0, -6, 0, //
				0, 0, 0, -8), result);

		final double a = CCMath.HALF_PI;
		mat4A.applyRotationY(a);
		mat4A.multiplyDiagonalPost(new CCVector4(2, 4, 6, 8), result);
		assertEquals(new CCMatrix4x4( //
				2 * CCMath.cos(a), 2 * 0, 2 * -CCMath.sin(a), 2 * 0, //
				4 * 0, 4 * 1, 4 * 0, 4 * 0, //
				6 * CCMath.sin(a), 6 * 0, 6 * CCMath.cos(a), 6 * 0, //
				8 * 0, 8 * 0, 8 * 0, 8 * 1), result);
		result = mat4A.multiplyDiagonalPre(new CCVector4(-2, -4, -6, -8), null);
		assertEquals(new CCMatrix4x4( //
				-2 * CCMath.cos(a), -4 * 0, -6 * -CCMath.sin(a), -8 * 0, //
				-2 * 0, -4 * 1, -6 * 0, -8 * 0, //
				-2 * CCMath.sin(a), -4 * 0, -6 * CCMath.cos(a), -8 * 0, //
				-2 * 0, -4 * 0, -6 * 0, -8 * 1), result);
	}

	@Test
	public void testMultiply() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4( //
				0.01f, 1.0f, 2.0f, 3.0f, //
				0.1f, 1.1f, 2.1f, 3.1f, //
				0.2f, 1.2f, 2.2f, 3.2f, //
				0.3f, 1.3f, 2.3f, 3.3f);
		mat4A.multiplyLocal(2);
		assertEquals(new CCMatrix4x4( //
				0.02f, 2.0f, 4.0f, 6.0f, //
				0.2f, 2.2f, 4.2f, 6.2f, //
				0.4f, 2.4f, 4.4f, 6.4f, //
				0.6f, 2.6f, 4.6f, 6.6f), mat4A);

		final CCMatrix4x4 mat4B = new CCMatrix4x4( //
			0.5f, 	4f,  8f,  12f, //
			1f, 	5f,  9f,  13f, //
			2f, 	6f, 10f, 14f, //
			3f,   7f, 11f, 15f
		);
		final CCMatrix4x4 result = mat4A.multiply(mat4B, null);
		assertTrue(CCMath.abs(0.02 * 0.5f + 0.2f * 4 + 0.4f *  8 + 0.6f * 12 - result.m00) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 1    + 0.2f * 5 + 0.4f *  9 + 0.6f * 13 - result.m01) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 2    + 0.2f * 6 + 0.4f * 10 + 0.6f * 14 - result.m02) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 3    + 0.2f * 7 + 0.4f * 11 + 0.6f * 15 - result.m03) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 0.5f + 2.2f * 4 + 2.4f *  8 + 2.6f * 12 - result.m10) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 1    + 2.2f * 5 + 2.4f *  9 + 2.6f * 13 - result.m11) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 2    + 2.2f * 6 + 2.4f * 10 + 2.6f * 14 - result.m12) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 3    + 2.2f * 7 + 2.4f * 11 + 2.6f * 15 - result.m13) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 0.5f + 4.2f * 4 + 4.4f *  8 + 4.6f * 12 - result.m20) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 1    + 4.2f * 5 + 4.4f *  9 + 4.6f * 13 - result.m21) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 2    + 4.2f * 6 + 4.4f * 10 + 4.6f * 14 - result.m22) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 3    + 4.2f * 7 + 4.4f * 11 + 4.6f * 15 - result.m23) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 0.5f + 6.2f * 4 + 6.4f *  8 + 6.6f * 12 - result.m30) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 1    + 6.2f * 5 + 6.4f *  9 + 6.6f * 13 - result.m31) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 2    + 6.2f * 6 + 6.4f * 10 + 6.6f * 14 - result.m32) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 3    + 6.2f * 7 + 6.4f * 11 + 6.6f * 15 - result.m33) < 0.00001);
		mat4A.multiplyLocal(mat4B);
		assertTrue(CCMath.abs(0.02 * 0.5f + 0.2f * 4 + 0.4f *  8 + 0.6f * 12 - mat4A.m00) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 1    + 0.2f * 5 + 0.4f *  9 + 0.6f * 13 - mat4A.m01) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 2    + 0.2f * 6 + 0.4f * 10 + 0.6f * 14 - mat4A.m02) < 0.00001);
		assertTrue(CCMath.abs(0.02 * 3    + 0.2f * 7 + 0.4f * 11 + 0.6f * 15 - mat4A.m03) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 0.5f + 2.2f * 4 + 2.4f *  8 + 2.6f * 12 - mat4A.m10) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 1    + 2.2f * 5 + 2.4f *  9 + 2.6f * 13 - mat4A.m11) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 2    + 2.2f * 6 + 2.4f * 10 + 2.6f * 14 - mat4A.m12) < 0.00001);
		assertTrue(CCMath.abs(2.0  * 3    + 2.2f * 7 + 2.4f * 11 + 2.6f * 15 - mat4A.m13) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 0.5f + 4.2f * 4 + 4.4f *  8 + 4.6f * 12 - mat4A.m20) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 1    + 4.2f * 5 + 4.4f *  9 + 4.6f * 13 - mat4A.m21) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 2    + 4.2f * 6 + 4.4f * 10 + 4.6f * 14 - mat4A.m22) < 0.00001);
		assertTrue(CCMath.abs(4.0  * 3    + 4.2f * 7 + 4.4f * 11 + 4.6f * 15 - mat4A.m23) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 0.5f + 6.2f * 4 + 6.4f *  8 + 6.6f * 12 - mat4A.m30) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 1    + 6.2f * 5 + 6.4f *  9 + 6.6f * 13 - mat4A.m31) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 2    + 6.2f * 6 + 6.4f * 10 + 6.6f * 14 - mat4A.m32) < 0.00001);
		assertTrue(CCMath.abs(6.0  * 3    + 6.2f * 7 + 6.4f * 11 + 6.6f * 15 - mat4A.m33) < 0.00001);
	}

	@Test
	public void testAddSubtract() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4( //
			0.0f, 1.0f, 2.0f, 3.0f, //
			0.1f, 1.1f, 2.1f, 3.1f, //
			0.2f, 1.2f, 2.2f, 3.2f, //
			0.3f, 1.3f, 2.3f, 3.3f
		);

		final CCMatrix4x4 result1 = mat4A.add(new CCMatrix4x4(//
			1, 5, 9, 13,//
			2, 6, 10, 14,//
			3, 7, 11, 15,//
			4, 8, 12, 16
		), null);
		assertEquals(new CCMatrix4x4( //
			1.0f, 6.0f, 11.0f, 16.0f, //
			2.1f, 7.1f, 12.1f, 17.1f, //
			3.2f, 8.2f, 13.2f, 18.2f, //
			4.3f, 9.3f, 14.3f, 19.3f), result1);

		final CCMatrix4x4 result2 = result1.subtract(new CCMatrix4x4(//
				1, 5, 9, 13,//
				2, 6, 10, 14,//
				3, 7, 11, 15,//
				4, 8, 12, 16));
		assertEquals(mat4A, result2);
		result2.addLocal(CCMatrix4x4.IDENTITY);
		assertEquals(new CCMatrix4x4( //
				1.0f, 1.0f, 2.0f, 3.0f, //
				0.1f, 2.1f, 2.1f, 3.1f, //
				0.2f, 1.2f, 3.2f, 3.2f, //
				0.3f, 1.3f, 2.3f, 4.3f), result2);

		result1.subtractLocal(CCMatrix4x4.IDENTITY);
		assertEquals(new CCMatrix4x4( //
				0.0f, 6.0f, 11.0f, 16.0f, //
				2.1f, 6.1f, 12.1f, 17.1f, //
				3.2f, 8.2f, 12.2f, 18.2f, //
				4.3f, 9.3f, 14.3f, 18.3f), result1);
	}

	@Test
	public void testScale() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4( //
			0.01f, 1.0f, 2.0f, 3.0f, //
			0.1f, 1.1f, 2.1f, 3.1f, //
			0.2f, 1.2f, 2.2f, 3.2f, //
			0.3f, 1.3f, 2.3f, 3.3f
		);
		final CCMatrix4x4 result = mat4A.scale(new CCVector4(-1, 2, 3, 4));
		assertEquals(new CCMatrix4x4( //
			0.01f * -1, 1.0f * - 1, 2.0f * - 1, 3.0f * - 1, //
			0.1f * 2, 1.1f * 2, 2.1f * 2, 3.1f * 2, //
			0.2f * 3, 1.2f * 3, 2.2f * 3, 3.2f * 3, //
			0.3f * 4, 1.3f * 4, 2.3f * 4, 3.3f * 4
		), result);

		result.scaleLocal(new CCVector4(-1, 0.5f, 1 / 3.f, .25f));
		assertEquals(mat4A, result);
	}

	@Test
	public void testTranspose() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4( //
			0.01f, 1.0f, 2.0f, 3.0f, //
			0.1f, 1.1f, 2.1f, 3.1f, //
			0.2f, 1.2f, 2.2f, 3.2f, //
			0.3f, 1.3f, 2.3f, 3.3f
		);
		final CCMatrix4x4 result = mat4A.transpose();
		assertEquals(new CCMatrix4x4( //
			0.01f, 0.1f, 0.2f, 0.3f, //
			1.0f, 1.1f, 1.2f, 1.3f, //
			2.0f, 2.1f, 2.2f, 2.3f, //
			3.0f, 3.1f, 3.2f, 3.3f
		), result);
		assertEquals(new CCMatrix4x4( //
			0.01f, 1.0f, 2.0f, 3.0f, //
			0.1f, 1.1f, 2.1f, 3.1f, //
			0.2f, 1.2f, 2.2f, 3.2f, //
			0.3f, 1.3f, 2.3f, 3.3f
		), result.transposeLocal());
		// coverage
		final CCMatrix4x4 result2 = result.transposeLocal().transpose(new CCMatrix4x4());
		assertEquals(mat4A, result2);
	}

	@Test
	public void testInvert() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4().applyRotationX(CCMath.QUARTER_PI).applyTranslationPost(1, 2, 3);
		final CCMatrix4x4 inverted = mat4A.invert(null);
		assertEquals(CCMatrix4x4.IDENTITY, mat4A.multiply(inverted, null));
		assertEquals(mat4A, inverted.invertLocal());
	}

	@Test(expected = ArithmeticException.class)
	public void testBadInvert() {
		final CCMatrix4x4 mat4A = new CCMatrix4x4(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		mat4A.invertLocal();
	}

	@Test
	public void testAdjugate() {
		final double //
		a = -3, b = 2, c = -5, d = 2, //
		e = -1, f = 0, g = -2, h = 3, //
		i = 1, j = -3, k = -4, l = 0, //
		m = 4, n = 2, o = 3, p = 1;

		final CCMatrix4x4 mat4A = new CCMatrix4x4( //
				a, e, i, m,//
				b, f, j, n, //
				c, g, k, o, //
				d, h, l, p);

		// prepare sections
		final CCMatrix3x3 m00 = new CCMatrix3x3(f, g, h, j, k, l, n, o, p);
		final CCMatrix3x3 m01 = new CCMatrix3x3(b, c, d, j, k, l, n, o, p);
		final CCMatrix3x3 m02 = new CCMatrix3x3(b, c, d, f, g, h, n, o, p);
		final CCMatrix3x3 m03 = new CCMatrix3x3(b, c, d, f, g, h, j, k, l);
		final CCMatrix3x3 m10 = new CCMatrix3x3(e, g, h, i, k, l, m, o, p);
		final CCMatrix3x3 m11 = new CCMatrix3x3(a, c, d, i, k, l, m, o, p);
		final CCMatrix3x3 m12 = new CCMatrix3x3(a, c, d, e, g, h, m, o, p);
		final CCMatrix3x3 m13 = new CCMatrix3x3(a, c, d, e, g, h, i, k, l);
		final CCMatrix3x3 m20 = new CCMatrix3x3(e, f, h, i, j, l, m, n, p);
		final CCMatrix3x3 m21 = new CCMatrix3x3(a, b, d, i, j, l, m, n, p);
		final CCMatrix3x3 m22 = new CCMatrix3x3(a, b, d, e, f, h, m, n, p);
		final CCMatrix3x3 m23 = new CCMatrix3x3(a, b, d, e, f, h, i, j, l);
		final CCMatrix3x3 m30 = new CCMatrix3x3(e, f, g, i, j, k, m, n, o);
		final CCMatrix3x3 m31 = new CCMatrix3x3(a, b, c, i, j, k, m, n, o);
		final CCMatrix3x3 m32 = new CCMatrix3x3(a, b, c, e, f, g, m, n, o);
		final CCMatrix3x3 m33 = new CCMatrix3x3(a, b, c, e, f, g, i, j, k);

		// generate adjugate
		final CCMatrix4x4 testValue = new CCMatrix4x4(
			m00.determinant(), -m10.determinant(), m20.determinant(), -m30.determinant(), //
			-m01.determinant(), m11.determinant(), -m21.determinant(), m31.determinant(), //
			m02.determinant(), -m12.determinant(), m22.determinant(), -m32.determinant(), //
			-m03.determinant(), m13.determinant(), -m23.determinant(), m33.determinant());

		assertEquals(testValue, mat4A.adjugate(null));
		assertEquals(testValue, mat4A.adjugateLocal());
	}

	@Test
	public void testDeterminant() {
		{
			final double //
			a = -3, b = 2, c = -5, d = 2, //
			e = -1, f = 0, g = -2, h = 3, //
			i = 1, j = -3, k = -4, l = 0, //
			m = 4, n = 2, o = 3, p = 1;

			final CCMatrix4x4 mat4A = new CCMatrix4x4( //
					a, e, i, m,//
					b, f, j, n, //
					c, g, k, o, //
					d, h, l, p);

			// prepare sections
			final double m00 = new CCMatrix3x3(f, g, h, j, k, l, n, o, p).determinant();
			final double m01 = new CCMatrix3x3(e, g, h, i, k, l, m, o, p).determinant();
			final double m02 = new CCMatrix3x3(e, f, h, i, j, l, m, n, p).determinant();
			final double m03 = new CCMatrix3x3(e, f, g, i, j, k, m, n, o).determinant();
			final double determinant = a * m00 - b * m01 + c * m02 - d * m03;

			assertTrue(CCMath.abs(determinant - mat4A.determinant()) <= CCMath.FLT_EPSILON);
		}

		{
			final double //
			a = -1.2f, b = 4, c = -2.5f, d = 1.7f, //
			e = 2, f = -3, g = -2, h = 3.2f, //
			i = 3.1f, j = -1, k = 2, l = 1.15f, //
			m = 1, n = 2, o = 3.14f, p = 1.4f;

			final CCMatrix4x4 mat4A = new CCMatrix4x4( //
					a, e, i, m,//
					b, f, j, n, //
					c, g, k, o, //
					d, h, l, p);

			// prepare sections
			final double m00 = new CCMatrix3x3(f, g, h, j, k, l, n, o, p).determinant();
			final double m01 = new CCMatrix3x3(e, g, h, i, k, l, m, o, p).determinant();
			final double m02 = new CCMatrix3x3(e, f, h, i, j, l, m, n, p).determinant();
			final double m03 = new CCMatrix3x3(e, f, g, i, j, k, m, n, o).determinant();
			final double determinant = a * m00 - b * m01 + c * m02 - d * m03;

			assertTrue(CCMath.abs(determinant - mat4A.determinant()) <= CCMath.FLT_EPSILON);
		}
	}

	@Test
	public void testClone() {
		final CCMatrix4x4 mat1 = new CCMatrix4x4();
		final CCMatrix4x4 mat2 = mat1.clone();
		assertEquals(mat1, mat2);
		assertNotSame(mat1, mat2);
	}

	@Test
	public void testValid() {
		final CCMatrix4x4 mat4 = new CCMatrix4x4();
		assertTrue(CCMatrix4x4.isValid(mat4));
		for (int i = 0; i < 16; i++) {
			mat4.setIdentity();
			mat4.setValue(i / 4, i % 4, Double.NaN);
			assertFalse(CCMatrix4x4.isValid(mat4));
			mat4.setIdentity();
			mat4.setValue(i / 4, i % 4, Double.POSITIVE_INFINITY);
			assertFalse(CCMatrix4x4.isValid(mat4));
		}

		mat4.setIdentity();
		assertTrue(CCMatrix4x4.isValid(mat4));

		assertFalse(CCMatrix4x4.isValid(null));

		// couple of equals validity tests
		assertEquals(mat4, mat4);
		assertTrue(mat4.strictEquals(mat4));
		assertFalse(mat4.equals(null));
		assertFalse(mat4.strictEquals(null));
		assertFalse(mat4.equals(new CCVector2()));
		assertFalse(mat4.strictEquals(new CCVector2()));

		// cover more of equals
		mat4.set(0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15);
		final CCMatrix4x4 comp = new CCMatrix4x4(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1);
		assertFalse(mat4.equals(comp));
		assertFalse(mat4.strictEquals(comp));
		for (int i = 0; i < 15; i++) {
			comp.setValue(i / 4, i % 4, i);
			assertFalse(mat4.equals(comp));
			assertFalse(mat4.strictEquals(comp));
		}
	}

	@Test
	public void testSimpleHash() {
		// Just a simple sanity check.
		final CCMatrix4x4 mat1 = new CCMatrix4x4(1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 16);
		final CCMatrix4x4 mat2 = new CCMatrix4x4(1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 16);
		final CCMatrix4x4 mat3 = new CCMatrix4x4(1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 0);

		assertTrue(mat1.hashCode() == mat2.hashCode());
		assertTrue(mat1.hashCode() != mat3.hashCode());
	}

	@Test
	public void testOrthonormal() {
		final CCMatrix4x4 mat4 = new CCMatrix4x4();
		assertTrue(mat4.isOrthonormal());
		// just rotation
		mat4.set(new CCMatrix3x3().applyRotationX(CCMath.QUARTER_PI));
		assertTrue(mat4.isOrthonormal());
		// non-uniform scale
		mat4.set(new CCMatrix3x3().scaleLocal(new CCVector3(1, 2, 3)).applyRotationX(CCMath.QUARTER_PI));
		assertFalse(mat4.isOrthonormal());
		// uniform scale
		mat4.set(new CCMatrix3x3().scaleLocal(new CCVector3(2, 2, 2)).applyRotationX(CCMath.QUARTER_PI));
		assertFalse(mat4.isOrthonormal());
		// uniform scale 1
		mat4.set(new CCMatrix3x3().scaleLocal(new CCVector3(1, 1, 1)).applyRotationX(CCMath.QUARTER_PI));
		assertTrue(mat4.isOrthonormal());
	}

	@Test
	public void testApplyVector4() {
		final CCMatrix4x4 mat4 = new CCMatrix4x4().applyRotationX(CCMath.HALF_PI);
		final CCVector4 vec4 = new CCVector4(0, 1, 0, 1);
		final CCVector4 result = mat4.applyPost(vec4);
		assertTrue(CCMath.abs(new CCVector4(0, 0, 1, 1).distance(result)) <= CCMath.FLT_EPSILON);
		vec4.set(0, 1, 1, 1);
		mat4.applyPost(vec4, result);
		assertTrue(CCMath.abs(new CCVector4(0, -1, 1, 1).distance(result)) <= CCMath.FLT_EPSILON);

		vec4.set(0, 1, 1, 1);
		mat4.applyPre(vec4, result);
		assertTrue(CCMath.abs(new CCVector4(0, 1, -1, 1).distance(result)) <= CCMath.FLT_EPSILON);

		vec4.set(1, 1, 1, 1);
		assertTrue(CCMath.abs(new CCVector4(1, 1, -1, 1).distance(mat4.applyPre(vec4))) <= CCMath.FLT_EPSILON);
	}

	@Test
	public void testApplyVector3() {
//		final CCMatrix4 mat4 = new CCMatrix4().applyRotationX(CCMath.HALF_PI).applyTranslationPre(1, 2, 3);
//		final CCVector3 vec3 = new CCVector3(0, 1, 0);
//		final CCVector3 result = mat4.applyPostPoint(vec3);
//		assertTrue(CCMath.abs(new CCVector3(1, 2, 4).distance(result)) <= CCMath.FLT_EPSILON);
//		vec3.set(0, 1, 1);
//		mat4.applyPostPoint(vec3, result);
//		assertTrue(CCMath.abs(new CCVector3(1, 1, 4).distance(result)) <= CCMath.FLT_EPSILON);
//
//		vec3.set(0, 1, 1);
//		mat4.applyPostVector(vec3, result);
//		assertTrue(CCMath.abs(new CCVector3(0, -1, 1).distance(result)) <= CCMath.FLT_EPSILON);
//
//		vec3.set(1, 1, 1);
//		assertTrue(CCMath.abs(new CCVector3(1, -1, 1).distance(mat4.applyPostVector(vec3, null))) <= CCMath.FLT_EPSILON);
	}
}
