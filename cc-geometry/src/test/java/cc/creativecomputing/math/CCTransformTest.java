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

import java.nio.FloatBuffer;

import org.junit.Test;

public class CCTransformTest {
//
//	@Test
//	public void testGetSet() {
//		final CCTransform trans = new CCTransform();
//		assertEquals(CCTransform.IDENTITY, trans);
//
//		final CCTransform immutable = new CCTransform(
//			new CCMatrix3().applyRotationX(CCMath.QUARTER_PI),
//			new CCVector3(0, -1, -2), 
//			new CCVector3(1, 2, 3), 
//			true, true, true
//		);
//		assertTrue(true == immutable.isIdentity());
//		assertTrue(true == immutable.isRotationMatrix());
//		assertTrue(true == immutable.isUniformScale());
//		assertEquals(new CCMatrix3().applyRotationX(CCMath.QUARTER_PI),
//				immutable.getMatrix());
//		assertEquals(new CCVector3(0, -1, -2), immutable.scale());
//		assertEquals(new CCVector3(1, 2, 3), immutable.translation());
//
//		final CCTransform trans2 = new CCTransform(immutable);
//		assertEquals(immutable, trans2);
//		trans2.updateFlags(false);
//
//		trans.set(immutable);
//		assertEquals(CCTransform.IDENTITY, trans); // because of shortcut flags.
//
//		trans.set(trans2);
//		assertEquals(trans2, trans);
//
//		trans.setIdentity();
//		assertEquals(CCTransform.IDENTITY, trans);
//
//		final float a = CCMath.QUARTER_PI;
//		trans.rotation(new CCQuaternion().fromAngleAxis(a, CCVector3.UNIT_Y));
//
//		assertEquals(new CCMatrix3( //
//				CCMath.cos(a), 0, CCMath.sin(a), //
//				0, 1, 0, //
//				-CCMath.sin(a), 0, CCMath.cos(a)), trans.getMatrix());
//
//		trans2.rotation(new CCMatrix3().fromAngleAxis(a, CCVector3.UNIT_Y));
//		assertEquals(trans.getMatrix(), trans2.getMatrix());
//
//		trans.scale(1.0f);
//		assertEquals(CCVector3.ONE, trans.scale());
//
//		trans.scale(new CCVector3(1, 2, 3));
//		assertEquals(new CCVector3(1, 2, 3), trans.scale());
//
//		trans.scale(-1, 5, -3);
//		assertEquals(new CCVector3(-1, 5, -3), trans.scale());
//
//		trans.translation(new CCVector3(10, 20, 30));
//		assertEquals(new CCVector3(10, 20, 30), trans.translation());
//
//		trans.translation(-10, 50, -30);
//		assertEquals(new CCVector3(-10, 50, -30), trans.translation());
//
//		trans.setIdentity();
//		trans.rotation(new CCMatrix3().fromAngleAxis(a, CCVector3.UNIT_Y));
//		trans.scale(2, 3, 4);
//		trans.translation(5, 10, 15);
//
//		final CCMatrix4 mat4 = trans.getHomogeneousMatrix(null);
//		assertEquals(new CCMatrix4( //
//				2 * CCMath.cos(a), 3 * 0, 4 * -CCMath.sin(a), 0, //
//				2 * 0, 3 * 1, 4 * 0, 0, //
//				2 * CCMath.sin(a), 3 * 0, 4 * CCMath.cos(a), 0, //
//				5, 10, 15, 1), mat4);
//
//		trans2.fromHomogeneousMatrix(mat4);
//		trans2.getHomogeneousMatrix(mat4);
//		assertEquals(new CCMatrix4( //
//				2 * CCMath.cos(a), 3 * 0, 4 * -CCMath.sin(a), 0, //
//				2 * 0, 3 * 1, 4 * 0, 0, //
//				2 * CCMath.sin(a), 3 * 0, 4 * CCMath.cos(a), 0, //
//				5, 10, 15, 1), mat4);
//
//		trans.setIdentity();
//		trans.rotation(new CCMatrix3(0, 1, 2, 3, 4, 5, 6, 7, 8));
//		trans.translation(10, 11, 12);
//		trans.getHomogeneousMatrix(mat4);
//		assertEquals(new CCMatrix4( //
//				0, 3, 6, 0, //
//				1, 4, 7, 0, //
//				2, 5, 8, 0, //
//				10, 11, 12, 1), mat4);
//
//	}
//
//	@Test(expected = CCTransformException.class)
//	public void testFailScale1A() {
//		final CCTransform trans = new CCTransform(new CCMatrix3(),
//				new CCVector3(), new CCVector3(), false, false, false);
//		trans.scale(CCVector3.ONE);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testFailScale1B() {
//		final CCTransform trans = new CCTransform();
//		trans.scale(CCVector3.ZERO);
//	}
//
//	@Test(expected = CCTransformException.class)
//	public void testFailScale2A() {
//		final CCTransform trans = new CCTransform(new CCMatrix3(),
//				new CCVector3(), new CCVector3(), false, false, false);
//		trans.scale(1, 1, 1);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testFailScale2B() {
//		final CCTransform trans = new CCTransform();
//		trans.scale(0, 0, 0);
//	}
//
//	@Test(expected = CCTransformException.class)
//	public void testFailScale3A() {
//		final CCTransform trans = new CCTransform(new CCMatrix3(),
//				new CCVector3(), new CCVector3(), false, false, false);
//		trans.scale(1);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testFailScale3B() {
//		final CCTransform trans = new CCTransform();
//		trans.scale(0);
//	}
//
//	@Test
//	public void testTranslate() {
//		final CCTransform trans = new CCTransform();
//		trans.translate(1, 3, 5);
//		assertEquals(new CCVector3(1, 3, 5), trans.translation());
//		trans.translate(trans.translation().negate());
//		assertEquals(CCVector3.ZERO, trans.translation());
//
//		trans.translate(new CCVector3(1, 3, 5));
//		assertEquals(new CCVector3(1, 3, 5), trans.translation());
//		trans.translate(-1, -3, -5);
//		assertEquals(CCVector3.ZERO, trans.translation());
//	}
//
//	@Test
//	public void testApplyVector3() {
//		final CCTransform trans = new CCTransform().rotation(
//			new CCMatrix3().applyRotationX(CCMath.HALF_PI)
//		).translate(1, 2, 3);
//		CCVector3 vec3 = new CCVector3(0, 1, 0);
//
//		CCVector3 result = trans.applyForward(vec3);
//		assertEquals(new CCVector3(1, 2, 4), result);
//		result = trans.applyForward(vec3);
//		assertEquals(new CCVector3(1, 2, 4), result);
//		vec3 = trans.applyForward(vec3);
//		assertEquals(new CCVector3(1, 2, 4),vec3);
//
//		vec3.set(0, 1, 1);
//		CCVector3 result2 = trans.applyForwardVector(vec3);
//		assertEquals(new CCVector3(0, -1, 1), result2);
//		result2 = trans.applyForwardVector(vec3);
//		assertEquals(new CCVector3(0, -1, 1), result2);
//		vec3 = trans.applyForwardVector(vec3);
//		assertEquals(new CCVector3(0, -1, 1), vec3);
//
//		vec3.set(0, 1, 0);
//		CCVector3 result3 = trans.applyInverse(vec3);
//		assertEquals(new CCVector3(-1, -3, 1), result3);
//		result3 = trans.applyInverse(vec3);
//		assertEquals(new CCVector3(-1, -3, 1), result3);
//		vec3 = trans.applyInverse(vec3);
//		assertEquals(new CCVector3(-1, -3, 1), vec3);
//
//		vec3.set(0, 1, 1);
//		CCVector3 result4 = trans.applyInverseVector(vec3);
//		assertEquals(new CCVector3(0, 1, -1), result4);
//		
//		result4 = trans.applyInverseVector(vec3);
//		assertEquals(new CCVector3(0, 1, -1), result4);
//		
//		vec3 = trans.applyInverseVector(vec3);
//		assertEquals(new CCVector3(0, 1, -1), vec3);
//
//		trans.rotation(new CCMatrix3().applyRotationY(CCMath.PI)).translate(2, 3, -1);
//
//		vec3.set(1, 2, 3).normalizeLocal();
//		final CCVector3 orig = new CCVector3(vec3);
//		trans.applyForward(vec3);
//		trans.applyInverse(vec3);
//		assertEquals(orig, vec3); // accumulated
//																			// error
//
//		vec3.set(orig);
//		trans.applyForwardVector(vec3);
//		trans.applyInverseVector(vec3);
//		assertEquals(orig, vec3); // accumulated
//																			// error
//
//		vec3.set(orig);
//		trans.setIdentity();
//		trans.applyForward(vec3);
//		assertEquals(orig, vec3);
//		trans.applyForwardVector(vec3);
//		assertEquals(orig, vec3);
//		trans.applyInverse(vec3);
//		assertEquals(orig, vec3);
//		trans.applyInverseVector(vec3);
//		assertEquals(orig, vec3);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testApplyFail1() {
//		final CCTransform trans = new CCTransform();
//		trans.applyForward(null);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testApplyFail2() {
//		final CCTransform trans = new CCTransform();
//		trans.applyForwardVector(null);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testApplyFail3() {
//		final CCTransform trans = new CCTransform();
//		trans.applyInverse(null);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void testApplyFail4() {
//		final CCTransform trans = new CCTransform();
//		trans.applyInverseVector(null);
//	}
//
//	@Test
//	public void testMultiply() {
//		final CCTransform trans1 = new CCTransform();
//		final CCTransform trans2 = new CCTransform();
//		assertEquals(CCTransform.IDENTITY, trans1.multiply(trans2));
//
//		trans1.translation(1, 2, 3);
//		CCTransform trans3 = trans1.multiply(trans2);
//		assertEquals(trans1, trans3);
//
//		trans2.translation(-1, -2, -3);
//		trans3 = trans1.multiply(trans2);
//		assertEquals(CCTransform.IDENTITY, trans3);
//		assertTrue(trans3.isRotationMatrix());
//		assertTrue(trans3.isIdentity());
//		assertTrue(trans3.isUniformScale());
//
//		trans2.scale(1, 2, 1);
//		trans3 = trans1.multiply(trans2);
//		assertEquals(new CCTransform().scale(1, 2, 1), trans3);
//		assertTrue(trans3.isRotationMatrix());
//		assertFalse(trans3.isIdentity());
//		assertFalse(trans3.isUniformScale());
//
//		trans1.scale(1, 2, 1);
//		trans3 = trans1.multiply(trans2);
//		assertEquals(new CCTransform().rotation(new CCMatrix3(1, 0, 0, 0, 4, 0, 0, 0, 1)).translation(0, -2, 0), trans3);
//		assertFalse(trans3.isRotationMatrix());
//		assertFalse(trans3.isIdentity());
//		assertFalse(trans3.isUniformScale());
//	}
//
//	@Test
//	public void testInvert() {
//		CCTransform trans1 = new CCTransform();
//		trans1.rotation(new CCMatrix3().applyRotationZ(3 * CCMath.QUARTER_PI));
//		final CCTransform trans2 = trans1.invert();
//		assertEquals(CCTransform.IDENTITY, trans1.multiply(trans2));
//
//		trans1 = trans1.setIdentity().invert();
//		assertEquals(CCTransform.IDENTITY, trans1);
//	}
//
//	@Test
//	public void testClone() {
//		final CCTransform trans1 = new CCTransform();
//		final CCTransform trans2 = trans1.clone();
//		assertEquals(trans1, trans2);
//		assertNotSame(trans1, trans2);
//	}
//
//	@Test
//	public void testValid() {
//		final CCTransform trans = new CCTransform();
//		assertTrue(CCTransform.isValid(trans));
//		trans.setIdentity();
//		trans.rotation(new CCMatrix3(Float.NaN, 0, 0, 0, 0, 0, 0, 0, 0));
//		assertFalse(CCTransform.isValid(trans));
//		trans.setIdentity();
//		trans.scale(Float.NaN, 0, 0);
//		assertFalse(CCTransform.isValid(trans));
//		trans.scale(Float.NaN);
//		assertFalse(CCTransform.isValid(trans));
//		trans.setIdentity();
//		trans.translation(Float.NaN, 0, 0);
//		assertFalse(CCTransform.isValid(trans));
//
//		trans.setIdentity();
//		assertTrue(CCTransform.isValid(trans));
//
//		assertFalse(CCTransform.isValid(null));
//
//		// couple of equals validity tests
//		assertEquals(trans, trans);
//		assertTrue(trans.strictEquals(trans));
//		assertFalse(trans.equals(null));
//		assertFalse(trans.strictEquals(null));
//		assertFalse(trans.equals(new CCVector3()));
//		assertFalse(trans.strictEquals(new CCVector3()));
//
//		// cover more of equals
//		trans.scale(1, 2, 3);
//		trans.rotation(new CCMatrix3(0, 1, 2, 3, 4, 5, 6, 7, 8));
//		trans.translation(1, 2, 3);
//		final CCTransform comp = new CCTransform();
//		final CCMatrix3 mat3 = new CCMatrix3(-1, -1, -1, -1, -1, -1, -1, -1, -1);
//		comp.scale(-1, -1, -1);
//		comp.rotation(mat3);
//		comp.translation(-1, -1, -1);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//		for (int i = 0; i < 8; i++) {
//			mat3.setValue(i / 3, i % 3, i);
//			comp.rotation(mat3);
//			assertFalse(trans.equals(comp));
//			assertFalse(trans.strictEquals(comp));
//		}
//		// test translation
//		trans.rotation(CCMatrix3.IDENTITY);
//		comp.rotation(CCMatrix3.IDENTITY);
//		comp.translation(1, -1, -1);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//		comp.translation(1, 2, -1);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//		comp.translation(1, 2, 3);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//
//		// test scale
//		comp.scale(1, -1, -1);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//		comp.scale(1, 2, -1);
//		assertFalse(trans.equals(comp));
//		assertFalse(trans.strictEquals(comp));
//		comp.scale(1, 2, 3);
//		assertTrue(trans.equals(comp));
//		assertTrue(trans.strictEquals(comp));
//	}
//
//	@Test
//	public void testSimpleHash() {
//		// Just a simple sanity check.
//		final CCTransform trans1 = new CCTransform().translation(1, 2, 3);
//		final CCTransform trans2 = new CCTransform().translation(1, 2, 3);
//		final CCTransform trans3 = new CCTransform().translation(1, 2, 0);
//
//		assertTrue(trans1.hashCode() == trans2.hashCode());
//		assertTrue(trans1.hashCode() != trans3.hashCode());
//	}
//
//	@Test
//	public void testGLApplyMatrix() {
//		final CCTransform trans = new CCTransform();
//
//		// non-rotational
//		trans.rotation(new CCMatrix3(0, 1, 2, 3, 4, 5, 6, 7, 8));
//		trans.translation(10, 11, 12);
//		final FloatBuffer db = FloatBuffer.allocate(16);
//		trans.getGLApplyMatrix(db);
//		for (final float val : new float[] { 0, 3, 6, 0, 1, 4, 7, 0, 2, 5, 8,
//				0, 10, 11, 12, 1 }) {
//			assertTrue(val == db.get());
//		}
//		final FloatBuffer fb = FloatBuffer.allocate(16);
//		trans.getGLApplyMatrix(fb);
//		for (final float val : new float[] { 0, 3, 6, 0, 1, 4, 7, 0, 2, 5, 8,
//				0, 10, 11, 12, 1 }) {
//			assertTrue(val == fb.get());
//		}
//
//		// rotational
//		final float a = CCMath.QUARTER_PI;
//		trans.rotation(new CCMatrix3().applyRotationY(a));
//		trans.translation(10, 11, 12);
//		trans.scale(2, 3, 4);
//		db.rewind();
//		trans.getGLApplyMatrix(db);
//		for (final float val : new float[] { 2 * CCMath.cos(a), 2 * 0,
//				2 * -CCMath.sin(a), 0, //
//				3 * 0, 3 * 1, 3 * 0, 0, //
//				4 * CCMath.sin(a), 4 * 0, 4 * CCMath.cos(a), 0, //
//				10, 11, 12, 1 }) {
//			assertTrue(val == db.get());
//		}
//		fb.rewind();
//		trans.getGLApplyMatrix(fb);
//		for (final float val : new float[] { (float) (2 * CCMath.cos(a)), 2 * 0,
//				(float) (2 * -CCMath.sin(a)), 0, //
//				3 * 0, 3 * 1, 3 * 0, 0, //
//				(float) (4 * CCMath.sin(a)), 4 * 0, (float) (4 * CCMath.cos(a)), 0, //
//				10, 11, 12, 1 }) {
//			assertTrue(val == fb.get());
//		}
//	}
}
