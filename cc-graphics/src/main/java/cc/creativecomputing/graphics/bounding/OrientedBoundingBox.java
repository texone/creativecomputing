/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.bounding;

import java.nio.FloatBuffer;

import cc.creativecomputing.data.CCBufferUtils;
import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.graphics.intersection.IntersectionRecord;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector3;

public class OrientedBoundingBox extends CCBoundingVolume {

	private static final long serialVersionUID = 1L;

	/** X axis of the Oriented Box. */
	protected final CCVector3 _xAxis = new CCVector3(1, 0, 0);

	/** Y axis of the Oriented Box. */
	protected final CCVector3 _yAxis = new CCVector3(0, 1, 0);

	/** Z axis of the Oriented Box. */
	protected final CCVector3 _zAxis = new CCVector3(0, 0, 1);

	/** Extents of the box along the x,y,z axis. */
	protected final CCVector3 _extent = new CCVector3(0, 0, 0);

	/** Vector array used to store the array of 8 corners the box has. */
	protected final CCVector3[] _vectorStore = new CCVector3[8];

	/**
	 * If true, the box's vectorStore array correctly represents the box's
	 * corners.
	 */
	public boolean correctCorners = false;

	protected final CCVector3 _compVect3 = new CCVector3();

	public OrientedBoundingBox() {
		for (int x = 0; x < 8; x++) {
			_vectorStore[x] = new CCVector3();
		}
	}

	@Override
	public Type type() {
		return Type.OBB;
	}

	@Override
	// XXX: HACK, revisit.
	public CCBoundingVolume transform(final CCTransform transform, CCBoundingVolume store) {
		if (store == null || store.type() != Type.OBB) {
			store = new OrientedBoundingBox();
		}
		final OrientedBoundingBox toReturn = (OrientedBoundingBox) store;
		final CCVector3 helper = new CCVector3();
		helper.set(1, 0, 0);
		final double scaleX = transform.applyForwardVector(helper).length();
		helper.set(0, 1, 0);
		final double scaleY = transform.applyForwardVector(helper).length();
		helper.set(0, 0, 1);
		final double scaleZ = transform.applyForwardVector(helper).length();
		toReturn._extent.set(CCMath.abs(_extent.x * scaleX), CCMath.abs(_extent.y * scaleY), CCMath.abs(_extent.z * scaleZ));

		transform.getMatrix().applyPost(_xAxis, toReturn._xAxis);
		transform.getMatrix().applyPost(_yAxis, toReturn._yAxis);
		transform.getMatrix().applyPost(_zAxis, toReturn._zAxis);
		if (!transform.isRotationMatrix()) {
			toReturn._xAxis.normalizeLocal();
			toReturn._yAxis.normalizeLocal();
			toReturn._zAxis.normalizeLocal();
		}

		transform.applyForward(_myCenter, toReturn._myCenter);
		toReturn.correctCorners = false;
		toReturn.computeCorners();
		return toReturn;
	}

	@Override
	public CCPlane.Side whichSide(final CCPlane plane) {
		final CCVector3 planeNormal = plane.normal();
		final double fRadius = 
			CCMath.abs(_extent.x * (planeNormal.dot(_xAxis))) + 
			CCMath.abs(_extent.y * (planeNormal.dot(_yAxis))) + 
			CCMath.abs(_extent.z * (planeNormal.dot(_zAxis)));
		final double fDistance = plane.pseudoDistance(_myCenter);
		if (fDistance <= -fRadius) {
			return CCPlane.Side.Inside;
		} else if (fDistance >= fRadius) {
			return CCPlane.Side.Outside;
		} else {
			return CCPlane.Side.Neither;
		}
	}

	@Override
	public void computeFromPoints(final FloatBuffer points) {
		containAABB(points);
	}

	/**
	 * Calculates an AABB of the given point values for this OBB.
	 * 
	 * @param points
	 *            The points this OBB should contain.
	 */
	private void containAABB(final FloatBuffer points) {
		if (points == null || points.limit() <= 2) { // we need at least a 3
			// double vector
			return;
		}

		CCBufferUtils.populateFromBuffer(_myCompVect1, points, 0);
		double minX = _myCompVect1.x, minY = _myCompVect1.y, minZ = _myCompVect1.z;
		double maxX = _myCompVect1.x, maxY = _myCompVect1.y, maxZ = _myCompVect1.z;

		for (int i = 1, len = points.limit() / 3; i < len; i++) {
			CCBufferUtils.populateFromBuffer(_myCompVect1, points, i);

			minX = CCMath.min(_myCompVect1.x, minX);
			maxX = CCMath.max(_myCompVect1.x, maxX);

			minY = CCMath.min(_myCompVect1.y, minY);
			maxY = CCMath.max(_myCompVect1.y, maxY);

			minZ = CCMath.min(_myCompVect1.z, minZ);
			maxZ = CCMath.max(_myCompVect1.z, maxZ);
		}

		_myCenter.set(minX + maxX, minY + maxY, minZ + maxZ);
		_myCenter.multiplyLocal(0.5f);

		_extent.set(maxX - _myCenter.x, maxY - _myCenter.y, maxZ - _myCenter.z);

		_xAxis.set(1, 0, 0);
		_yAxis.set(0, 1, 0);
		_zAxis.set(0, 0, 1);

		correctCorners = false;
	}

	@Override
	public CCBoundingVolume merge(final CCBoundingVolume volume) {
		// clone ourselves into a new bounding volume, then merge.
		return clone(new OrientedBoundingBox()).mergeLocal(volume);
	}

	@Override
	public CCBoundingVolume mergeLocal(final CCBoundingVolume volume) {
		if (volume == null) {
			return this;
		}

		switch (volume.type()) {

		case OBB: {
			return mergeOBB((OrientedBoundingBox) volume);
		}

		case AABB: {
			return mergeAABB((CCBoundingBox) volume);
		}

		case Sphere: {
			return mergeSphere((CCBoundingSphere) volume);
		}

		default:
			return null;

		}
	}

	@Override
	public CCBoundingVolume asType(final Type newType) {
		if (newType == null) {
			return null;
		}

		switch (newType) {
		case AABB: {
			final CCBoundingBox box = new CCBoundingBox(_myCenter, 0, 0, 0);
			return box.merge(this);
		}

		case Sphere: {
			final CCBoundingSphere sphere = new CCBoundingSphere(0, _myCenter);
			return sphere.merge(this);
		}

		case OBB: {
			return this.clone(null);
		}

		default:
			return null;
		}
	}

	private CCBoundingVolume mergeSphere(final CCBoundingSphere volume) {
		// check for infinite bounds to prevent NaN values
		if (CCVector3.isInfinite(getExtent()) || Double.isInfinite(volume.radius())) {
			center(CCVector3.ZERO);
			_extent.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			return this;
		}

		final CCBoundingSphere mergeSphere = volume;
		if (!correctCorners) {
			computeCorners();
		}

		final FloatBuffer mergeBuf = CCBufferUtils.createFloatBufferOnHeap(16 * 3);

		mergeBuf.rewind();
		for (int i = 0; i < 8; i++) {
			mergeBuf.put((float) _vectorStore[i].x);
			mergeBuf.put((float) _vectorStore[i].y);
			mergeBuf.put((float) _vectorStore[i].z);
		}
		mergeBuf.put((float) (mergeSphere._myCenter.x + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z + mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z + mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z + mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z - mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z + mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z - mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x + mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z - mergeSphere.radius()));
		mergeBuf.put((float) (mergeSphere._myCenter.x - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.y - mergeSphere.radius()))
				.put((float) (mergeSphere._myCenter.z - mergeSphere.radius()));
		containAABB(mergeBuf);
		correctCorners = false;
		return this;
	}

	private CCBoundingVolume mergeAABB(final CCBoundingBox volume) {
		// check for infinite bounds to prevent NaN values
		if (CCVector3.isInfinite(getExtent()) || Double.isInfinite(volume.getXExtent()) || Double.isInfinite(volume.getYExtent())
				|| Double.isInfinite(volume.getZExtent())) {
			center(CCVector3.ZERO);
			_extent.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			return this;
		}

		final CCBoundingBox mergeBox = volume;
		if (!correctCorners) {
			computeCorners();
		}

		final FloatBuffer mergeBuf = CCBufferUtils.createFloatBufferOnHeap(16 * 3);

		mergeBuf.rewind();
		for (int i = 0; i < 8; i++) {
			mergeBuf.put((float) _vectorStore[i].x);
			mergeBuf.put((float) _vectorStore[i].y);
			mergeBuf.put((float) _vectorStore[i].z);
		}
		mergeBuf
			.put((float) (mergeBox._myCenter.x + mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y + mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z + mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x - mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y + mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z + mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x + mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y - mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z + mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x + mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y + mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z - mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x - mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y - mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z + mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x - mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y + mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z - mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x + mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y - mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z - mergeBox.getZExtent()));
		mergeBuf
			.put((float) (mergeBox._myCenter.x - mergeBox.getXExtent()))
			.put((float) (mergeBox._myCenter.y - mergeBox.getYExtent()))
			.put((float) (mergeBox._myCenter.z - mergeBox.getZExtent()));
		containAABB(mergeBuf);
		correctCorners = false;
		return this;
	}

	private CCBoundingVolume mergeOBB(final OrientedBoundingBox volume) {
		// check for infinite bounds to prevent NaN values
		if (CCVector3.isInfinite(getExtent()) || CCVector3.isInfinite(volume.getExtent())) {
			center(CCVector3.ZERO);
			_extent.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
			return this;
		}

		// OrientedBoundingBox mergeBox=(OrientedBoundingBox) volume;
		// if (!correctCorners) this.computeCorners();
		// if (!mergeBox.correctCorners) mergeBox.computeCorners();
		// CCVector3[] mergeArray=new CCVector3[16];
		// for (int i=0;i<vectorStore.length;i++){
		// mergeArray[i*2+0]=this .vectorStore[i];
		// mergeArray[i*2+1]=mergeBox.vectorStore[i];
		// }
		// containAABB(mergeArray);
		// correctCorners=false;
		// return this;
		// construct a box that contains the input boxes
		// Box3<Real> kBox;
		final OrientedBoundingBox rkBox0 = this;
		final OrientedBoundingBox rkBox1 = volume;

		// The first guess at the box center. This value will be updated later
		// after the input box vertices are projected onto axes determined by an
		// average of box axes.
		final CCVector3 kBoxCenter = (rkBox0._myCenter.add(rkBox1._myCenter, new CCVector3())).multiplyLocal(.5f);

		// A box's axes, when viewed as the columns of a matrix, form a rotation
		// matrix. The input box axes are converted to quaternions. The average
		// quaternion is computed, then normalized to unit length. The result is
		// the slerp of the two input quaternions with t-value of 1/2. The
		// result is converted back to a rotation matrix and its columns are
		// selected as the merged box axes.
		final CCQuaternion kQ0 = new CCQuaternion(), kQ1 = new CCQuaternion();
		kQ0.fromAxes(rkBox0._xAxis, rkBox0._yAxis, rkBox0._zAxis);
		kQ1.fromAxes(rkBox1._xAxis, rkBox1._yAxis, rkBox1._zAxis);

		if (kQ0.dot(kQ1) < 0.0) {
			kQ1.multiplyLocal(-1.0f);
		}

		final CCQuaternion kQ = kQ0.addLocal(kQ1);
		kQ.normalizeLocal();

		final CCMatrix3x3 kBoxaxis = kQ.toRotationMatrix();
		final CCVector3 newXaxis = kBoxaxis.getColumn(0, new CCVector3());
		final CCVector3 newYaxis = kBoxaxis.getColumn(1, new CCVector3());
		final CCVector3 newZaxis = kBoxaxis.getColumn(2, new CCVector3());

		// Project the input box vertices onto the merged-box axes. Each axis
		// D[i] containing the current center C has a minimum projected value
		// pmin[i] and a maximum projected value pmax[i]. The corresponding end
		// points on the axes are C+pmin[i]*D[i] and C+pmax[i]*D[i]. The point C
		// is not necessarily the midpoint for any of the intervals. The actual
		// box center will be adjusted from C to a point C' that is the midpoint
		// of each interval,
		// C' = C + sum_{i=0}^1 0.5*(pmin[i]+pmax[i])*D[i]
		// The box extents are
		// e[i] = 0.5*(pmax[i]-pmin[i])

		int i;
		double fDot;
		final CCVector3 kDiff = new CCVector3();
		final CCVector3 kMin = new CCVector3();
		final CCVector3 kMax = new CCVector3();

		if (!rkBox0.correctCorners) {
			rkBox0.computeCorners();
		}
		for (i = 0; i < 8; i++) {
			rkBox0._vectorStore[i].subtract(kBoxCenter, kDiff);

			fDot = kDiff.dot(newXaxis);
			if (fDot > kMax.x) {
				kMax.x = fDot;
			} else if (fDot < kMin.x) {
				kMin.x = fDot;
			}

			fDot = kDiff.dot(newYaxis);
			if (fDot > kMax.y) {
				kMax.y = fDot;
			} else if (fDot < kMin.y) {
				kMin.y = fDot;
			}

			fDot = kDiff.dot(newZaxis);
			if (fDot > kMax.z) {
				kMax.z = fDot;
			} else if (fDot < kMin.z) {
				kMin.z = fDot;
			}

		}

		if (!rkBox1.correctCorners) {
			rkBox1.computeCorners();
		}
		for (i = 0; i < 8; i++) {
			rkBox1._vectorStore[i].subtract(kBoxCenter, kDiff);

			fDot = kDiff.dot(newXaxis);
			if (fDot > kMax.x) {
				kMax.x = fDot;
			} else if (fDot < kMin.x) {
				kMin.x = fDot;
			}

			fDot = kDiff.dot(newYaxis);
			if (fDot > kMax.y) {
				kMax.y = fDot;
			} else if (fDot < kMin.y) {
				kMin.y = fDot;
			}

			fDot = kDiff.dot(newZaxis);
			if (fDot > kMax.z) {
				kMax.z = fDot;
			} else if (fDot < kMin.z) {
				kMin.z = fDot;
			}
		}

		_xAxis.set(newXaxis);
		_yAxis.set(newYaxis);
		_zAxis.set(newZaxis);

		final CCVector3 tempVec = new CCVector3();
		_extent.x = .5f * (kMax.x - kMin.x);
		kBoxCenter.addLocal(_xAxis.multiply(.5f * (kMax.x + kMin.x), tempVec));

		_extent.y = .5f * (kMax.y - kMin.y);
		kBoxCenter.addLocal(_yAxis.multiply(.5f * (kMax.y + kMin.y), tempVec));

		_extent.z = .5f * (kMax.z - kMin.z);
		kBoxCenter.addLocal(_zAxis.multiply(.5f * (kMax.z + kMin.z), tempVec));

		_myCenter.set(kBoxCenter);

		correctCorners = false;

		return this;
	}

	@Override
	public CCBoundingVolume clone(final CCBoundingVolume store) {
		OrientedBoundingBox toReturn;
		if (store instanceof OrientedBoundingBox) {
			toReturn = (OrientedBoundingBox) store;
		} else {
			toReturn = new OrientedBoundingBox();
		}
		toReturn._extent.set(_extent);
		toReturn._xAxis.set(_xAxis);
		toReturn._yAxis.set(_yAxis);
		toReturn._zAxis.set(_zAxis);
		toReturn._myCenter.set(_myCenter);
		toReturn._checkPlane = _checkPlane;
		for (int x = _vectorStore.length; --x >= 0;) {
			toReturn._vectorStore[x].set(_vectorStore[x]);
		}
		toReturn.correctCorners = correctCorners;
		return toReturn;
	}

	@Override
	public double radius() {
		double radius = 0.0f;
		radius = CCMath.max(radius, _xAxis.multiply(_extent.x, _myCompVect1).length());
		radius = CCMath.max(radius, _yAxis.multiply(_extent.y, _myCompVect1).length());
		radius = CCMath.max(radius, _zAxis.multiply(_extent.z, _myCompVect1).length());

		return radius;
	}

	/**
	 * Sets the vectorStore information to the 8 corners of the box.
	 */
	public void computeCorners() {
		final CCVector3 tempAxis0 = _xAxis.multiply(_extent.x, _myCompVect1);
		final CCVector3 tempAxis1 = _yAxis.multiply(_extent.y, _myCompVect2);
		final CCVector3 tempAxis2 = _zAxis.multiply(_extent.z, _compVect3);

		_vectorStore[0].set(_myCenter).subtractLocal(tempAxis0).subtractLocal(tempAxis1).subtractLocal(tempAxis2);
		_vectorStore[1].set(_myCenter).addLocal(tempAxis0).subtractLocal(tempAxis1).subtractLocal(tempAxis2);
		_vectorStore[2].set(_myCenter).addLocal(tempAxis0).addLocal(tempAxis1).subtractLocal(tempAxis2);
		_vectorStore[3].set(_myCenter).subtractLocal(tempAxis0).addLocal(tempAxis1).subtractLocal(tempAxis2);
		_vectorStore[4].set(_myCenter).subtractLocal(tempAxis0).subtractLocal(tempAxis1).addLocal(tempAxis2);
		_vectorStore[5].set(_myCenter).addLocal(tempAxis0).subtractLocal(tempAxis1).addLocal(tempAxis2);
		_vectorStore[6].set(_myCenter).addLocal(tempAxis0).addLocal(tempAxis1).addLocal(tempAxis2);
		_vectorStore[7].set(_myCenter).subtractLocal(tempAxis0).addLocal(tempAxis1).addLocal(tempAxis2);

		correctCorners = true;
	}

	@Override
	public void computeFromPrimitives(final CCGeometryData data) {

		final CCVector3 min = _myCompVect1.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		final CCVector3 max = _myCompVect2.set(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		for (CCVector3 theVertex : CCBufferUtils.getVector3Array(data.vertices(), new CCVector3())) {
			if (theVertex.x < min.x) {
				min.x = theVertex.x;
			} else if (theVertex.x > max.x) {
				max.x = theVertex.x;
			}
			if (theVertex.y < min.y) {
				min.y = theVertex.y;
			} else if (theVertex.y > max.y) {
				max.y = theVertex.y;
			}
			if (theVertex.z < min.z) {
				min.z = theVertex.z;
			} else if (theVertex.z > max.z) {
				max.z = theVertex.z;
			}
		}

		_myCenter.set(min.addLocal(max));
		_myCenter.multiplyLocal(0.5f);

		_extent.set(max.x - _myCenter.x, max.y - _myCenter.y, max.z - _myCenter.z);

		_xAxis.set(1, 0, 0);
		_yAxis.set(0, 1, 0);
		_zAxis.set(0, 0, 1);

		correctCorners = false;
	}

	public boolean intersection(final OrientedBoundingBox box1) {
		// Cutoff for cosine of angles between box axes. This is used to catch
		// the cases when at least one pair of axes
		// are parallel. If this happens, there is no need to test for
		// separation along the Cross(A[i],B[j]) directions.
		final OrientedBoundingBox box0 = this;
		final double cutoff = 0.999999f;
		boolean parallelPairExists = false;
		int i;

		// convenience variables
		final CCVector3 akA[] = new CCVector3[] { box0.getXAxis(), box0.getYAxis(), box0.getZAxis() };
		final CCVector3[] akB = new CCVector3[] { box1.getXAxis(), box1.getYAxis(), box1.getZAxis() };
		final CCVector3 afEA = box0._extent;
		final CCVector3 afEB = box1._extent;

		// compute difference of box centers, D = C1-C0
		final CCVector3 kD = box1._myCenter.subtract(box0._myCenter, _myCompVect1);

		final double[][] aafC = { new double[3], new double[3], new double[3] };

		final double[][] aafAbsC = { new double[3], new double[3], new double[3] };

		final double[] afAD = new double[3];
		double fR0, fR1, fR; // interval radii and distance between centers
		double fR01; // = R0 + R1

		// axis C0+t*A0
		for (i = 0; i < 3; i++) {
			aafC[0][i] = akA[0].dot(akB[i]);
			aafAbsC[0][i] = CCMath.abs(aafC[0][i]);
			if (aafAbsC[0][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[0] = akA[0].dot(kD);
		fR = CCMath.abs(afAD[0]);
		fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z * aafAbsC[0][2];
		fR01 = afEA.x + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1
		for (i = 0; i < 3; i++) {
			aafC[1][i] = akA[1].dot(akB[i]);
			aafAbsC[1][i] = CCMath.abs(aafC[1][i]);
			if (aafAbsC[1][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[1] = akA[1].dot(kD);
		fR = CCMath.abs(afAD[1]);
		fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z * aafAbsC[1][2];
		fR01 = afEA.y + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2
		for (i = 0; i < 3; i++) {
			aafC[2][i] = akA[2].dot(akB[i]);
			aafAbsC[2][i] = CCMath.abs(aafC[2][i]);
			if (aafAbsC[2][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[2] = akA[2].dot(kD);
		fR = CCMath.abs(afAD[2]);
		fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z * aafAbsC[2][2];
		fR01 = afEA.z + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B0
		fR = CCMath.abs(akB[0].dot(kD));
		fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z * aafAbsC[2][0];
		fR01 = fR0 + afEB.x;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B1
		fR = CCMath.abs(akB[1].dot(kD));
		fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z * aafAbsC[2][1];
		fR01 = fR0 + afEB.y;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B2
		fR = CCMath.abs(akB[2].dot(kD));
		fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z * aafAbsC[2][2];
		fR01 = fR0 + afEB.z;
		if (fR > fR01) {
			return false;
		}

		// At least one pair of box axes was parallel, so the separation is
		// effectively in 2D where checking the "edge" normals is sufficient for
		// the separation of the boxes.
		if (parallelPairExists) {
			return true;
		}

		// axis C0+t*A0xB0
		fR = CCMath.abs(afAD[2] * aafC[1][0] - afAD[1] * aafC[2][0]);
		fR0 = afEA.y * aafAbsC[2][0] + afEA.z * aafAbsC[1][0];
		fR1 = afEB.y * aafAbsC[0][2] + afEB.z * aafAbsC[0][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A0xB1
		fR = CCMath.abs(afAD[2] * aafC[1][1] - afAD[1] * aafC[2][1]);
		fR0 = afEA.y * aafAbsC[2][1] + afEA.z * aafAbsC[1][1];
		fR1 = afEB.x * aafAbsC[0][2] + afEB.z * aafAbsC[0][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A0xB2
		fR = CCMath.abs(afAD[2] * aafC[1][2] - afAD[1] * aafC[2][2]);
		fR0 = afEA.y * aafAbsC[2][2] + afEA.z * aafAbsC[1][2];
		fR1 = afEB.x * aafAbsC[0][1] + afEB.y * aafAbsC[0][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB0
		fR = CCMath.abs(afAD[0] * aafC[2][0] - afAD[2] * aafC[0][0]);
		fR0 = afEA.x * aafAbsC[2][0] + afEA.z * aafAbsC[0][0];
		fR1 = afEB.y * aafAbsC[1][2] + afEB.z * aafAbsC[1][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB1
		fR = CCMath.abs(afAD[0] * aafC[2][1] - afAD[2] * aafC[0][1]);
		fR0 = afEA.x * aafAbsC[2][1] + afEA.z * aafAbsC[0][1];
		fR1 = afEB.x * aafAbsC[1][2] + afEB.z * aafAbsC[1][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB2
		fR = CCMath.abs(afAD[0] * aafC[2][2] - afAD[2] * aafC[0][2]);
		fR0 = afEA.x * aafAbsC[2][2] + afEA.z * aafAbsC[0][2];
		fR1 = afEB.x * aafAbsC[1][1] + afEB.y * aafAbsC[1][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB0
		fR = CCMath.abs(afAD[1] * aafC[0][0] - afAD[0] * aafC[1][0]);
		fR0 = afEA.x * aafAbsC[1][0] + afEA.y * aafAbsC[0][0];
		fR1 = afEB.y * aafAbsC[2][2] + afEB.z * aafAbsC[2][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB1
		fR = CCMath.abs(afAD[1] * aafC[0][1] - afAD[0] * aafC[1][1]);
		fR0 = afEA.x * aafAbsC[1][1] + afEA.y * aafAbsC[0][1];
		fR1 = afEB.x * aafAbsC[2][2] + afEB.z * aafAbsC[2][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB2
		fR = CCMath.abs(afAD[1] * aafC[0][2] - afAD[0] * aafC[1][2]);
		fR0 = afEA.x * aafAbsC[1][2] + afEA.y * aafAbsC[0][2];
		fR1 = afEB.x * aafAbsC[2][1] + afEB.y * aafAbsC[2][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		return true;
	}

	@Override
	public boolean intersects(final CCBoundingVolume bv) {
		if (bv == null) {
			return false;
		}

		return bv.intersectsOrientedBoundingBox(this);
	}

	@Override
	public boolean intersectsSphere(final CCBoundingSphere bs) {
		if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bs._myCenter)) {
			return false;
		}

		_myCompVect1.set(bs.center()).subtractLocal(_myCenter);
		final CCMatrix3x3 tempMa = new CCMatrix3x3().fromAxes(_xAxis, _yAxis, _zAxis);

		tempMa.applyPost(_myCompVect1, _myCompVect1);

		boolean result = false;
		if (CCMath.abs(_myCompVect1.x) < bs.radius() + _extent.x && CCMath.abs(_myCompVect1.y) < bs.radius() + _extent.y
				&& CCMath.abs(_myCompVect1.z) < bs.radius() + _extent.z) {
			result = true;
		}

		return result;
	}

	@Override
	public boolean intersectsBoundingBox(final CCBoundingBox bb) {
		if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bb._myCenter)) {
			return false;
		}

		// Cutoff for cosine of angles between box axes. This is used to catch
		// the cases when at least one pair of axes are parallel. If this
		// happens,
		// there is no need to test for separation along the Cross(A[i],B[j])
		// directions.
		final double cutoff = 0.999999f;
		boolean parallelPairExists = false;
		int i;

		// convenience variables
		final CCVector3 akA[] = new CCVector3[] { _xAxis, _yAxis, _zAxis };
		final CCVector3[] akB = new CCVector3[] { new CCVector3(), new CCVector3(), new CCVector3() };
		final CCVector3 afEA = _extent;
		final CCVector3 afEB = new CCVector3().set(bb.getXExtent(), bb.getYExtent(), bb.getZExtent());

		// compute difference of box centers, D = C1-C0
		final CCVector3 kD = bb.center().subtract(_myCenter);

		final double[][] aafC = { new double[3], new double[3], new double[3] };

		final double[][] aafAbsC = { new double[3], new double[3], new double[3] };

		final double[] afAD = new double[3];
		double fR0, fR1, fR; // interval radii and distance between centers
		double fR01; // = R0 + R1

		try {

			// axis C0+t*A0
			for (i = 0; i < 3; i++) {
				aafC[0][i] = akA[0].dot(akB[i]);
				aafAbsC[0][i] = CCMath.abs(aafC[0][i]);
				if (aafAbsC[0][i] > cutoff) {
					parallelPairExists = true;
				}
			}
			afAD[0] = akA[0].dot(kD);
			fR = CCMath.abs(afAD[0]);
			fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z * aafAbsC[0][2];
			fR01 = afEA.x + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A1
			for (i = 0; i < 3; i++) {
				aafC[1][i] = akA[1].dot(akB[i]);
				aafAbsC[1][i] = CCMath.abs(aafC[1][i]);
				if (aafAbsC[1][i] > cutoff) {
					parallelPairExists = true;
				}
			}
			afAD[1] = akA[1].dot(kD);
			fR = CCMath.abs(afAD[1]);
			fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z * aafAbsC[1][2];
			fR01 = afEA.y + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A2
			for (i = 0; i < 3; i++) {
				aafC[2][i] = akA[2].dot(akB[i]);
				aafAbsC[2][i] = CCMath.abs(aafC[2][i]);
				if (aafAbsC[2][i] > cutoff) {
					parallelPairExists = true;
				}
			}
			afAD[2] = akA[2].dot(kD);
			fR = CCMath.abs(afAD[2]);
			fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z * aafAbsC[2][2];
			fR01 = afEA.z + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*B0
			fR = CCMath.abs(akB[0].dot(kD));
			fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z * aafAbsC[2][0];
			fR01 = fR0 + afEB.x;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*B1
			fR = CCMath.abs(akB[1].dot(kD));
			fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z * aafAbsC[2][1];
			fR01 = fR0 + afEB.y;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*B2
			fR = CCMath.abs(akB[2].dot(kD));
			fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z * aafAbsC[2][2];
			fR01 = fR0 + afEB.z;
			if (fR > fR01) {
				return false;
			}

			// At least one pair of box axes was parallel, so the separation is
			// effectively in 2D where checking the "edge" normals is sufficient
			// for
			// the separation of the boxes.
			if (parallelPairExists) {
				return true;
			}

			// axis C0+t*A0xB0
			fR = CCMath.abs(afAD[2] * aafC[1][0] - afAD[1] * aafC[2][0]);
			fR0 = afEA.y * aafAbsC[2][0] + afEA.z * aafAbsC[1][0];
			fR1 = afEB.y * aafAbsC[0][2] + afEB.z * aafAbsC[0][1];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A0xB1
			fR = CCMath.abs(afAD[2] * aafC[1][1] - afAD[1] * aafC[2][1]);
			fR0 = afEA.y * aafAbsC[2][1] + afEA.z * aafAbsC[1][1];
			fR1 = afEB.x * aafAbsC[0][2] + afEB.z * aafAbsC[0][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A0xB2
			fR = CCMath.abs(afAD[2] * aafC[1][2] - afAD[1] * aafC[2][2]);
			fR0 = afEA.y * aafAbsC[2][2] + afEA.z * aafAbsC[1][2];
			fR1 = afEB.x * aafAbsC[0][1] + afEB.y * aafAbsC[0][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A1xB0
			fR = CCMath.abs(afAD[0] * aafC[2][0] - afAD[2] * aafC[0][0]);
			fR0 = afEA.x * aafAbsC[2][0] + afEA.z * aafAbsC[0][0];
			fR1 = afEB.y * aafAbsC[1][2] + afEB.z * aafAbsC[1][1];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A1xB1
			fR = CCMath.abs(afAD[0] * aafC[2][1] - afAD[2] * aafC[0][1]);
			fR0 = afEA.x * aafAbsC[2][1] + afEA.z * aafAbsC[0][1];
			fR1 = afEB.x * aafAbsC[1][2] + afEB.z * aafAbsC[1][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A1xB2
			fR = CCMath.abs(afAD[0] * aafC[2][2] - afAD[2] * aafC[0][2]);
			fR0 = afEA.x * aafAbsC[2][2] + afEA.z * aafAbsC[0][2];
			fR1 = afEB.x * aafAbsC[1][1] + afEB.y * aafAbsC[1][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A2xB0
			fR = CCMath.abs(afAD[1] * aafC[0][0] - afAD[0] * aafC[1][0]);
			fR0 = afEA.x * aafAbsC[1][0] + afEA.y * aafAbsC[0][0];
			fR1 = afEB.y * aafAbsC[2][2] + afEB.z * aafAbsC[2][1];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A2xB1
			fR = CCMath.abs(afAD[1] * aafC[0][1] - afAD[0] * aafC[1][1]);
			fR0 = afEA.x * aafAbsC[1][1] + afEA.y * aafAbsC[0][1];
			fR1 = afEB.x * aafAbsC[2][2] + afEB.z * aafAbsC[2][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			// axis C0+t*A2xB2
			fR = CCMath.abs(afAD[1] * aafC[0][2] - afAD[0] * aafC[1][2]);
			fR0 = afEA.x * aafAbsC[1][2] + afEA.y * aafAbsC[0][2];
			fR1 = afEB.x * aafAbsC[2][1] + afEB.y * aafAbsC[2][0];
			fR01 = fR0 + fR1;
			if (fR > fR01) {
				return false;
			}

			return true;
		} finally {

		}
	}

	@Override
	public boolean intersectsOrientedBoundingBox(final OrientedBoundingBox obb) {
		if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(obb._myCenter)) {
			return false;
		}

		// Cutoff for cosine of angles between box axes. This is used to catch
		// the cases when at least one pair of axes are parallel. If this
		// happens,
		// there is no need to test for separation along the Cross(A[i],B[j])
		// directions.
		final double cutoff = 0.999999f;
		boolean parallelPairExists = false;
		int i;

		// convenience variables
		final CCVector3 akA[] = new CCVector3[] { _xAxis, _yAxis, _zAxis };
		final CCVector3[] akB = new CCVector3[] { obb._xAxis, obb._yAxis, obb._zAxis };
		final CCVector3 afEA = _extent;
		final CCVector3 afEB = obb._extent;

		// compute difference of box centers, D = C1-C0
		final CCVector3 kD = obb._myCenter.subtract(_myCenter, _myCompVect1);

		final double[][] aafC = { new double[3], new double[3], new double[3] };

		final double[][] aafAbsC = { new double[3], new double[3], new double[3] };

		final double[] afAD = new double[3];
		double fR0, fR1, fR; // interval radii and distance between centers
		double fR01; // = R0 + R1

		// axis C0+t*A0
		for (i = 0; i < 3; i++) {
			aafC[0][i] = akA[0].dot(akB[i]);
			aafAbsC[0][i] = CCMath.abs(aafC[0][i]);
			if (aafAbsC[0][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[0] = akA[0].dot(kD);
		fR = CCMath.abs(afAD[0]);
		fR1 = afEB.x * aafAbsC[0][0] + afEB.y * aafAbsC[0][1] + afEB.z * aafAbsC[0][2];
		fR01 = afEA.x + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1
		for (i = 0; i < 3; i++) {
			aafC[1][i] = akA[1].dot(akB[i]);
			aafAbsC[1][i] = CCMath.abs(aafC[1][i]);
			if (aafAbsC[1][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[1] = akA[1].dot(kD);
		fR = CCMath.abs(afAD[1]);
		fR1 = afEB.x * aafAbsC[1][0] + afEB.y * aafAbsC[1][1] + afEB.z * aafAbsC[1][2];
		fR01 = afEA.y + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2
		for (i = 0; i < 3; i++) {
			aafC[2][i] = akA[2].dot(akB[i]);
			aafAbsC[2][i] = CCMath.abs(aafC[2][i]);
			if (aafAbsC[2][i] > cutoff) {
				parallelPairExists = true;
			}
		}
		afAD[2] = akA[2].dot(kD);
		fR = CCMath.abs(afAD[2]);
		fR1 = afEB.x * aafAbsC[2][0] + afEB.y * aafAbsC[2][1] + afEB.z * aafAbsC[2][2];
		fR01 = afEA.z + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B0
		fR = CCMath.abs(akB[0].dot(kD));
		fR0 = afEA.x * aafAbsC[0][0] + afEA.y * aafAbsC[1][0] + afEA.z * aafAbsC[2][0];
		fR01 = fR0 + afEB.x;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B1
		fR = CCMath.abs(akB[1].dot(kD));
		fR0 = afEA.x * aafAbsC[0][1] + afEA.y * aafAbsC[1][1] + afEA.z * aafAbsC[2][1];
		fR01 = fR0 + afEB.y;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*B2
		fR = CCMath.abs(akB[2].dot(kD));
		fR0 = afEA.x * aafAbsC[0][2] + afEA.y * aafAbsC[1][2] + afEA.z * aafAbsC[2][2];
		fR01 = fR0 + afEB.z;
		if (fR > fR01) {
			return false;
		}

		// At least one pair of box axes was parallel, so the separation is
		// effectively in 2D where checking the "edge" normals is sufficient for
		// the separation of the boxes.
		if (parallelPairExists) {
			return true;
		}

		// axis C0+t*A0xB0
		fR = CCMath.abs(afAD[2] * aafC[1][0] - afAD[1] * aafC[2][0]);
		fR0 = afEA.y * aafAbsC[2][0] + afEA.z * aafAbsC[1][0];
		fR1 = afEB.y * aafAbsC[0][2] + afEB.z * aafAbsC[0][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A0xB1
		fR = CCMath.abs(afAD[2] * aafC[1][1] - afAD[1] * aafC[2][1]);
		fR0 = afEA.y * aafAbsC[2][1] + afEA.z * aafAbsC[1][1];
		fR1 = afEB.x * aafAbsC[0][2] + afEB.z * aafAbsC[0][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A0xB2
		fR = CCMath.abs(afAD[2] * aafC[1][2] - afAD[1] * aafC[2][2]);
		fR0 = afEA.y * aafAbsC[2][2] + afEA.z * aafAbsC[1][2];
		fR1 = afEB.x * aafAbsC[0][1] + afEB.y * aafAbsC[0][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB0
		fR = CCMath.abs(afAD[0] * aafC[2][0] - afAD[2] * aafC[0][0]);
		fR0 = afEA.x * aafAbsC[2][0] + afEA.z * aafAbsC[0][0];
		fR1 = afEB.y * aafAbsC[1][2] + afEB.z * aafAbsC[1][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB1
		fR = CCMath.abs(afAD[0] * aafC[2][1] - afAD[2] * aafC[0][1]);
		fR0 = afEA.x * aafAbsC[2][1] + afEA.z * aafAbsC[0][1];
		fR1 = afEB.x * aafAbsC[1][2] + afEB.z * aafAbsC[1][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A1xB2
		fR = CCMath.abs(afAD[0] * aafC[2][2] - afAD[2] * aafC[0][2]);
		fR0 = afEA.x * aafAbsC[2][2] + afEA.z * aafAbsC[0][2];
		fR1 = afEB.x * aafAbsC[1][1] + afEB.y * aafAbsC[1][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB0
		fR = CCMath.abs(afAD[1] * aafC[0][0] - afAD[0] * aafC[1][0]);
		fR0 = afEA.x * aafAbsC[1][0] + afEA.y * aafAbsC[0][0];
		fR1 = afEB.y * aafAbsC[2][2] + afEB.z * aafAbsC[2][1];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB1
		fR = CCMath.abs(afAD[1] * aafC[0][1] - afAD[0] * aafC[1][1]);
		fR0 = afEA.x * aafAbsC[1][1] + afEA.y * aafAbsC[0][1];
		fR1 = afEB.x * aafAbsC[2][2] + afEB.z * aafAbsC[2][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		// axis C0+t*A2xB2
		fR = CCMath.abs(afAD[1] * aafC[0][2] - afAD[0] * aafC[1][2]);
		fR0 = afEA.x * aafAbsC[1][2] + afEA.y * aafAbsC[0][2];
		fR1 = afEB.x * aafAbsC[2][1] + afEB.y * aafAbsC[2][0];
		fR01 = fR0 + fR1;
		if (fR > fR01) {
			return false;
		}

		return true;
	}

	@Override
	public boolean intersects(final CCRay3 ray) {
		if (!CCVector3.isValid(_myCenter)) {
			return false;
		}

		double rhs;
		final CCVector3 rayDir = ray.getDirection();
		final CCVector3 diff = _myCompVect1.set(ray.getOrigin()).subtractLocal(_myCenter);
		final CCVector3 wCrossD = _myCompVect2;

		final double[] fWdU = new double[3];
		final double[] fAWdU = new double[3];
		final double[] fDdU = new double[3];
		final double[] fADdU = new double[3];
		final double[] fAWxDdU = new double[3];

		fWdU[0] = rayDir.dot(_xAxis);
		fAWdU[0] = CCMath.abs(fWdU[0]);
		fDdU[0] = diff.dot(_xAxis);
		fADdU[0] = CCMath.abs(fDdU[0]);
		if (fADdU[0] > _extent.x && fDdU[0] * fWdU[0] >= 0.0) {
			return false;
		}

		fWdU[1] = rayDir.dot(_yAxis);
		fAWdU[1] = CCMath.abs(fWdU[1]);
		fDdU[1] = diff.dot(_yAxis);
		fADdU[1] = CCMath.abs(fDdU[1]);
		if (fADdU[1] > _extent.y && fDdU[1] * fWdU[1] >= 0.0) {
			return false;
		}

		fWdU[2] = rayDir.dot(_zAxis);
		fAWdU[2] = CCMath.abs(fWdU[2]);
		fDdU[2] = diff.dot(_zAxis);
		fADdU[2] = CCMath.abs(fDdU[2]);
		if (fADdU[2] > _extent.z && fDdU[2] * fWdU[2] >= 0.0) {
			return false;
		}

		rayDir.cross(diff, wCrossD);

		fAWxDdU[0] = CCMath.abs(wCrossD.dot(_xAxis));
		rhs = _extent.y * fAWdU[2] + _extent.z * fAWdU[1];
		if (fAWxDdU[0] > rhs) {
			return false;
		}

		fAWxDdU[1] = CCMath.abs(wCrossD.dot(_yAxis));
		rhs = _extent.x * fAWdU[2] + _extent.z * fAWdU[0];
		if (fAWxDdU[1] > rhs) {
			return false;
		}

		fAWxDdU[2] = CCMath.abs(wCrossD.dot(_zAxis));
		rhs = _extent.x * fAWdU[1] + _extent.y * fAWdU[0];
		if (fAWxDdU[2] > rhs) {
			return false;

		}

		return true;
	}

	@Override
	public IntersectionRecord intersectsWhere(final CCRay3 ray) {
		final CCVector3 rayDir = ray.getDirection();
		final CCVector3 rayOrigin = ray.getOrigin();

		// convert ray to box coordinates
		final CCVector3 diff = rayOrigin.subtract(center(), _myCompVect1);
		diff.set(_xAxis.dot(diff), _yAxis.dot(diff), _zAxis.dot(diff));
		final CCVector3 direction = _myCompVect2.set(_xAxis.dot(rayDir), _yAxis.dot(rayDir), _zAxis.dot(rayDir));

		final double[] t = { 0, Double.POSITIVE_INFINITY };

		final double saveT0 = t[0], saveT1 = t[1];
		final boolean notEntirelyClipped = clip(+direction.x, -diff.x - _extent.x, t) && clip(-direction.x, +diff.x - _extent.x, t)
				&& clip(+direction.y, -diff.y - _extent.y, t) && clip(-direction.y, +diff.y - _extent.y, t)
				&& clip(+direction.z, -diff.z - _extent.z, t) && clip(-direction.z, +diff.z - _extent.z, t);

		if (notEntirelyClipped && (t[0] != saveT0 || t[1] != saveT1)) {
			if (t[1] > t[0]) {
				final double[] distances = t;
				final CCVector3[] points = new CCVector3[] { rayDir.multiply(distances[0], new CCVector3()).addLocal(rayOrigin),
						rayDir.multiply(distances[1], new CCVector3()).addLocal(rayOrigin) };
				final IntersectionRecord record = new IntersectionRecord(distances, points);
				return record;
			}

			final double[] distances = new double[] { t[0] };
			final CCVector3[] points = new CCVector3[] { rayDir.multiply(distances[0], new CCVector3()).addLocal(rayOrigin) };
			final IntersectionRecord record = new IntersectionRecord(distances, points);
			return record;
		}

		return null;

	}

	/**
	 * <code>clip</code> determines if a line segment intersects the current
	 * test plane.
	 * 
	 * @param denom
	 *            the denominator of the line segment.
	 * @param numer
	 *            the numerator of the line segment.
	 * @param t
	 *            test values of the plane.
	 * @return true if the line segment intersects the plane, false otherwise.
	 */
	private boolean clip(final double denom, final double numer, final double[] t) {
		// Return value is 'true' if line segment intersects the current test
		// plane. Otherwise 'false' is returned in which case the line segment
		// is entirely clipped.
		if (denom > 0.0) {
			if (numer > denom * t[1]) {
				return false;
			}
			if (numer > denom * t[0]) {
				t[0] = numer / denom;
			}
			return true;
		} else if (denom < 0.0) {
			if (numer > denom * t[0]) {
				return false;
			}
			if (numer > denom * t[1]) {
				t[1] = numer / denom;
			}
			return true;
		} else {
			return numer <= 0.0;
		}
	}

	public void setXAxis(final CCVector3 axis) {
		_xAxis.set(axis);
		correctCorners = false;
	}

	public void setYAxis(final CCVector3 axis) {
		_yAxis.set(axis);
		correctCorners = false;
	}

	public void setZAxis(final CCVector3 axis) {
		_zAxis.set(axis);
		correctCorners = false;
	}

	public void setExtent(final CCVector3 ext) {
		_extent.set(ext);
		correctCorners = false;
	}

	public CCVector3 getXAxis() {
		return _xAxis;
	}

	public CCVector3 getYAxis() {
		return _yAxis;
	}

	public CCVector3 getZAxis() {
		return _zAxis;
	}

	public CCVector3 getExtent() {
		return _extent;
	}

	@Override
	public boolean contains(final CCVector3 point) {
		_myCompVect1.set(point).subtractLocal(_myCenter);
		double coeff = _myCompVect1.dot(_xAxis);
		if (CCMath.abs(coeff) > _extent.x) {
			return false;
		}

		coeff = _myCompVect1.dot(_yAxis);
		if (CCMath.abs(coeff) > _extent.y) {
			return false;
		}

		coeff = _myCompVect1.dot(_zAxis);
		if (CCMath.abs(coeff) > _extent.z) {
			return false;
		}

		return true;
	}

	@Override
	public double distanceToEdge(final CCVector3 point) {
		// compute coordinates of point in box coordinate system
		final CCVector3 diff = point.subtract(_myCenter, _myCompVect1);
		final CCVector3 closest = _myCompVect2.set(diff.dot(_xAxis), diff.dot(_yAxis), diff.dot(_zAxis));

		// project test point onto box
		double sqrDistance = 0.0f;
		double delta;

		if (closest.x < -_extent.x) {
			delta = closest.x + _extent.x;
			sqrDistance += delta * delta;
			closest.x = -_extent.x;
		} else if (closest.x > _extent.x) {
			delta = closest.x - _extent.x;
			sqrDistance += delta * delta;
			closest.x = _extent.x;
		}

		if (closest.y < -_extent.y) {
			delta = closest.y + _extent.y;
			sqrDistance += delta * delta;
			closest.y = -_extent.y;
		} else if (closest.y > _extent.y) {
			delta = closest.y - _extent.y;
			sqrDistance += delta * delta;
			closest.y = _extent.y;
		}

		if (closest.z < -_extent.z) {
			delta = closest.z + _extent.z;
			sqrDistance += delta * delta;
			closest.z = -_extent.z;
		} else if (closest.z > _extent.z) {
			delta = closest.z - _extent.z;
			sqrDistance += delta * delta;
			closest.z = _extent.z;
		}

		return CCMath.sqrt(sqrDistance);
	}

	@Override
	public double getVolume() {
		return (8 * _extent.x * _extent.y * _extent.z);
	}
}