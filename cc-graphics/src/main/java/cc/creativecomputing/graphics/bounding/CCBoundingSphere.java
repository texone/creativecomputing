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
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;

/**
 * <code>BoundingSphere</code> defines a sphere that defines a container for a
 * group of vertices of a particular piece of geometry. This sphere defines a
 * radius and a center. <br>
 * <br>
 * A typical usage is to allow the class define the center and radius by calling
 * either <code>containAABB</code> or <code>averagePoints</code>. A call to
 * <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
 */
public class CCBoundingSphere extends CCBoundingVolume {

	private static final long serialVersionUID = 1L;

	private double _radius;

	static final private double radiusEpsilon = 1 + 0.00001f;

	protected final CCVector3 _compVect3 = new CCVector3();
	protected final CCVector3 _compVect4 = new CCVector3();

	/**
	 * Default constructor instantiates a new <code>BoundingSphere</code>
	 * object.
	 */
	public CCBoundingSphere() {
	}

	/**
	 * Constructor instantiates a new <code>BoundingSphere</code> object.
	 * 
	 * @param r
	 *            the radius of the sphere.
	 * @param c
	 *            the center of the sphere.
	 */
	public CCBoundingSphere(final double r, final CCVector3 c) {
		_myCenter.set(c);
		setRadius(r);
	}

	@Override
	public Type type() {
		return Type.Sphere;
	}

	@Override
	public CCBoundingVolume transform(final CCTransform transform, final CCBoundingVolume store) {
		CCBoundingSphere sphere;
		if (store == null || store.type() != CCBoundingVolume.Type.Sphere) {
			sphere = new CCBoundingSphere(1, new CCVector3(0, 0, 0));
		} else {
			sphere = (CCBoundingSphere) store;
		}

		transform.applyForward(_myCenter, sphere._myCenter);

		if (!transform.isRotationMatrix()) {
			final CCVector3 scale = _compVect3.set(1, 1, 1);
			transform.applyForwardVector(scale);
			sphere.setRadius(CCMath.abs(maxAxis(scale) * radius()) + radiusEpsilon - 1);
		} else {
			final CCVector3 scale = transform.scale();
			sphere.setRadius(CCMath.abs(maxAxis(scale) * radius()) + radiusEpsilon - 1);
		}

		return sphere;
	}

	private double maxAxis(final CCVector3 scale) {
		return CCMath.max(CCMath.abs(scale.x), CCMath.max(CCMath.abs(scale.y), CCMath.abs(scale.z)));
	}

	/**
	 * <code>getRadius</code> returns the radius of the bounding sphere.
	 * 
	 * @return the radius of the bounding sphere.
	 */
	@Override
	public double radius() {
		return _radius;
	}

	/**
	 * <code>setRadius</code> sets the radius of this bounding sphere.
	 * 
	 * @param radius
	 *            the new radius of the bounding sphere.
	 */
	public void setRadius(final double radius) {
		_radius = radius;
	}

	/**
	 * <code>computeFromPoints</code> creates a new Bounding Sphere from a given
	 * set of points. It uses the <code>calcWelzl</code> method as default.
	 * 
	 * @param points
	 *            the points to contain.
	 */
	@Override
	public void computeFromPoints(final FloatBuffer points) {
		calcWelzl(points);
	}

	@Override
	public void computeFromPrimitives(final CCGeometryData data) {
		averagePoints(CCBufferUtils.getVector3Array(data.vertices(), new CCVector3()));
	}

	/**
	 * Calculates a minimum bounding sphere for the set of points. The algorithm
	 * was originally found at
	 * http://www.flipcode.com/cgi-bin/msg.cgi?showThread
	 * =COTD-SmallestEnclosingSpheres&forum=cotd&id=-1 in C++ and translated to
	 * java by Cep21
	 * 
	 * @param points
	 *            The points to calculate the minimum bounds from.
	 */
	public void calcWelzl(final FloatBuffer points) {
		final double[] buf = new double[points.limit()];
		points.rewind();
		for(int i = 0; i < buf.length;i++){
			buf[i] = points.get();
		}
		recurseMini(buf, buf.length / 3, 0, 0);
	}

	/**
	 * Used from calcWelzl. This function recurses to calculate a minimum
	 * bounding sphere a few points at a time.
	 * 
	 * @param points
	 *            The array of points to look through.
	 * @param p
	 *            The size of the list to be used.
	 * @param pnts
	 *            The number of points currently considering to include with the
	 *            sphere.
	 * @param ap
	 *            A variable simulating pointer arithmetic from C++, and offset
	 *            in <code>points</code>.
	 */
	private void recurseMini(final double[] points, final int p, final int pnts, final int ap) {
		switch (pnts) {
		case 0:
			setRadius(0);
			_myCenter.set(0, 0, 0);
			break;
		case 1:
			setRadius(1f - radiusEpsilon);
			populateFromBuffer(_myCenter, points, ap - 1);
			break;
		case 2:
			populateFromBuffer(_myCompVect1, points, ap - 1);
			populateFromBuffer(_myCompVect2, points, ap - 2);
			setSphere(_myCompVect1, _myCompVect2);
			break;
		case 3:
			populateFromBuffer(_myCompVect1, points, ap - 1);
			populateFromBuffer(_myCompVect2, points, ap - 2);
			populateFromBuffer(_compVect3, points, ap - 3);
			setSphere(_myCompVect1, _myCompVect2, _compVect3);
			break;
		case 4:
			populateFromBuffer(_myCompVect1, points, ap - 1);
			populateFromBuffer(_myCompVect2, points, ap - 2);
			populateFromBuffer(_compVect3, points, ap - 3);
			populateFromBuffer(_compVect4, points, ap - 4);
			setSphere(_myCompVect1, _myCompVect2, _compVect3, _compVect4);
			return;
		}
		for (int i = 0; i < p; i++) {
			populateFromBuffer(_myCompVect1, points, i + ap);
			if (_myCompVect1.distanceSquared(_myCenter) - (radius() * radius()) > radiusEpsilon - 1f) {
				for (int j = i; j > 0; j--) {
					populateFromBuffer(_myCompVect2, points, j + ap);
					populateFromBuffer(_compVect3, points, j - 1 + ap);
					setInBuffer(_compVect3, points, j + ap);
					setInBuffer(_myCompVect2, points, j - 1 + ap);
				}
				recurseMini(points, i, pnts + 1, ap + 1);
			}
		}
	}

	public static void populateFromBuffer(final CCVector3 vector, final double[] buf, final int index) {
		vector.x = buf[index * 3];
		vector.y = buf[index * 3 + 1];
		vector.z = buf[index * 3 + 2];
	}

	public static void setInBuffer(final CCVector3 vector, final double[] buf, final int index) {
		if (buf == null) {
			return;
		}
		if (vector == null) {
			buf[index * 3] = 0;
			buf[(index * 3) + 1] = 0;
			buf[(index * 3) + 2] = 0;
		} else {
			buf[index * 3] = vector.x;
			buf[(index * 3) + 1] = vector.y;
			buf[(index * 3) + 2] = vector.z;
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 4 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @param B
	 *            The 3rd point inside the sphere.
	 * @param C
	 *            The 4th point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(final CCVector3 O, final CCVector3 A, final CCVector3 B, final CCVector3 C) {
		final CCVector3 a = A.subtract(O, null);
		final CCVector3 b = B.subtract(O, null);
		final CCVector3 c = C.subtract(O, null);

		final double Denominator = 2.0f * (a.x * (b.y * c.z - c.y * b.z) - b.x
				* (a.y * c.z - c.y * a.z) + c.x * (a.y * b.z - b.y * a.z));
		if (Denominator == 0) {
			_myCenter.set(0, 0, 0);
			setRadius(0);
		} else {
			final CCVector3 o = a.cross(b, null).multiplyLocal(c.lengthSquared())
					.addLocal(c.cross(a, null).multiplyLocal(b.lengthSquared()))
					.addLocal(b.cross(c, null).multiplyLocal(a.lengthSquared())).divideLocal(Denominator);

			setRadius(o.length() * radiusEpsilon);
			O.add(o, _myCenter);
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 3 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @param B
	 *            The 3rd point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(final CCVector3 O, final CCVector3 A, final CCVector3 B) {
		final CCVector3 a = A.subtract(O, null);
		final CCVector3 b = B.subtract(O, null);
		final CCVector3 acrossB = a.cross(b, null);

		final double Denominator = 2.0f * acrossB.dot(acrossB);

		if (Denominator == 0) {
			_myCenter.set(0, 0, 0);
			setRadius(0);
		} else {

			final CCVector3 o = acrossB.cross(a, null).multiplyLocal(b.lengthSquared())
					.addLocal(b.cross(acrossB, null).multiplyLocal(a.lengthSquared())).divideLocal(Denominator);
			setRadius(o.length() * radiusEpsilon);
			O.add(o, _myCenter);
		}
	}

	/**
	 * Calculates the minimum bounding sphere of 2 points. Used in welzl's
	 * algorithm.
	 * 
	 * @param O
	 *            The 1st point inside the sphere.
	 * @param A
	 *            The 2nd point inside the sphere.
	 * @see #calcWelzl(java.nio.FloatBuffer)
	 */
	private void setSphere(final CCVector3 O, final CCVector3 A) {
		setRadius(CCMath.sqrt(((A.x - O.x) * (A.x - O.x) + (A.y - O.y) * (A.y - O.y) + (A.z - O
				.z) * (A.z - O.z)) / 4f)
				+ radiusEpsilon - 1);
		CCVector3.lerp(O, A, .5f, _myCenter);
	}

	/**
	 * <code>averagePoints</code> selects the sphere center to be the average of
	 * the points and the sphere radius to be the smallest value to enclose all
	 * points.
	 * 
	 * @param points
	 *            the list of points to contain.
	 */
	public void averagePoints(final CCVector3[] points) {
		_myCenter.set(points[0]);

		for (int i = 1; i < points.length; i++) {
			_myCenter.addLocal(points[i]);
		}

		final double quantity = 1.0f / points.length;
		_myCenter.multiplyLocal(quantity);

		double maxRadiusSqr = 0;
		for (int i = 0; i < points.length; i++) {
			final CCVector3 diff = points[i].subtract(_myCenter, _myCompVect1);
			final double radiusSqr = diff.lengthSquared();
			if (radiusSqr > maxRadiusSqr) {
				maxRadiusSqr = radiusSqr;
			}
		}

		setRadius(CCMath.sqrt(maxRadiusSqr) + radiusEpsilon - 1f);

	}

	/**
	 * <code>whichSide</code> takes a plane (typically provided by a view
	 * frustum) to determine which side this bound is on.
	 * 
	 * @param plane
	 *            the plane to check against.
	 * @return side
	 */
	@Override
	public CCPlane.Side whichSide(final CCPlane plane) {
		final double distance = plane.pseudoDistance(_myCenter);

		if (distance <= -radius()) {
			return CCPlane.Side.Inside;
		} else if (distance >= radius()) {
			return CCPlane.Side.Outside;
		} else {
			return CCPlane.Side.Neither;
		}
	}

	/**
	 * <code>merge</code> combines this sphere with a second bounding sphere.
	 * This new sphere contains both bounding spheres and is returned.
	 * 
	 * @param volume
	 *            the sphere to combine with this sphere.
	 * @return a new sphere
	 */
	@Override
	public CCBoundingVolume merge(final CCBoundingVolume volume) {
		if (volume == null) {
			return this;
		}

		switch (volume.type()) {

		case Sphere: {
			final CCBoundingSphere sphere = (CCBoundingSphere) volume;
			final double temp_radius = sphere.radius();
			final CCVector3 tempCenter = sphere.center();
			final CCBoundingSphere rVal = new CCBoundingSphere();
			return merge(temp_radius, tempCenter, rVal);
		}

		case AABB: {
			final CCBoundingBox box = (CCBoundingBox) volume;
			final CCVector3 radVect = new CCVector3(box.getXExtent(), box.getYExtent(), box.getZExtent());
			final CCVector3 tempCenter = box._myCenter;
			final CCBoundingSphere rVal = new CCBoundingSphere();
			return merge(radVect.length(), tempCenter, rVal);
		}

		case OBB: {
			final OrientedBoundingBox box = (OrientedBoundingBox) volume;
			final CCBoundingSphere rVal = (CCBoundingSphere) this.clone(null);
			return rVal.mergeLocalOBB(box);
		}

		default:
			return null;

		}
	}

	/**
	 * <code>mergeLocal</code> combines this sphere with a second bounding
	 * sphere locally. Altering this sphere to contain both the original and the
	 * additional sphere volumes;
	 * 
	 * @param volume
	 *            the sphere to combine with this sphere.
	 * @return this
	 */
	@Override
	public CCBoundingVolume mergeLocal(final CCBoundingVolume volume) {
		if (volume == null) {
			return this;
		}

		switch (volume.type()) {

		case Sphere: {
			final CCBoundingSphere sphere = (CCBoundingSphere) volume;
			final double temp_radius = sphere.radius();
			final CCVector3 temp_center = sphere.center();
			return merge(temp_radius, temp_center, this);
		}

		case AABB: {
			final CCBoundingBox box = (CCBoundingBox) volume;
			final CCVector3 temp_center = box._myCenter;
			_myCompVect1.set(box.getXExtent(), box.getYExtent(), box.getZExtent());
			final double radius = _myCompVect1.length();
			return merge(radius, temp_center, this);
		}

		case OBB: {
			return mergeLocalOBB((OrientedBoundingBox) volume);
		}

		default:
			return null;
		}
	}

	/**
	 * Merges this sphere with the given OBB.
	 * 
	 * @param volume
	 *            The OBB to merge.
	 * @return This sphere, after merging.
	 */
	private CCBoundingSphere mergeLocalOBB(final OrientedBoundingBox volume) {
		// check for infinite bounds to prevent NaN values... is so, return
		// infinite bounds with center at origin
		if (Double.isInfinite(radius()) || CCVector3.isInfinite(volume.getExtent())) {
			center(CCVector3.ZERO);
			setRadius(Double.POSITIVE_INFINITY);
			return this;
		}

		// compute edge points from the obb
		if (!volume.correctCorners) {
			volume.computeCorners();
		}

		final FloatBuffer mergeBuf = CCBufferUtils.createFloatBufferOnHeap(8 * 3);

		for (int i = 0; i < 8; i++) {
			mergeBuf.put((float) volume._vectorStore[i].x);
			mergeBuf.put((float) volume._vectorStore[i].y);
			mergeBuf.put((float) volume._vectorStore[i].z);
		}

		// remember old radius and center
		final double oldRadius = radius();
		final double oldCenterX = _myCenter.x;
		final double oldCenterY = _myCenter.y;
		final double oldCenterZ = _myCenter.z;

		// compute new radius and center from obb points
		computeFromPoints(mergeBuf);

		final double newCenterX = _myCenter.x;
		final double newCenterY = _myCenter.y;
		final double newCenterZ = _myCenter.z;
		final double newRadius = radius();

		// restore old center and radius
		_myCenter.set(oldCenterX, oldCenterY, oldCenterZ);
		setRadius(oldRadius);

		// merge obb points result
		merge(newRadius, _compVect4.set(newCenterX, newCenterY, newCenterZ), this);

		return this;
	}

	private CCBoundingVolume merge(final double otherRadius, final CCVector3 otherCenter, final CCBoundingSphere store) {
		// check for infinite bounds... is so, return infinite bounds with
		// center at origin
		if (Double.isInfinite(otherRadius) || Double.isInfinite(radius())) {
			store.center(CCVector3.ZERO);
			store.setRadius(Double.POSITIVE_INFINITY);
			return store;
		}

		final CCVector3 diff = otherCenter.subtract(_myCenter, _myCompVect1);
		final double lengthSquared = diff.lengthSquared();
		final double radiusDiff = otherRadius - radius();
		final double radiusDiffSqr = radiusDiff * radiusDiff;

		// if one sphere wholly contains the other
		if (radiusDiffSqr >= lengthSquared) {
			// if we contain the other
			if (radiusDiff <= 0.0) {
				store.center(_myCenter);
				store.setRadius(_radius);
				return store;
			}
			// else the other contains us
			else {
				store.center(otherCenter);
				store.setRadius(otherRadius);
				return store;
			}
		}

		// distance between sphere centers
		final double length = CCMath.sqrt(lengthSquared);

		// init a center var using our center
		final CCVector3 rCenter = _myCompVect2;
		rCenter.set(_myCenter);

		// if our centers are at least a tiny amount apart from each other...
		if (length > CCMath.FLT_EPSILON) {
			// place us between the two centers, weighted by radii
			final double coeff = (length + radiusDiff) / (2.0f * length);
			rCenter.addLocal(diff.multiplyLocal(coeff));
		}

		// set center on our resulting bounds
		store.center(rCenter);

		// Set radius
		store.setRadius(0.5f * (length + radius() + otherRadius));
		return store;
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
			return this.clone(null);
		}

		case OBB: {
			final OrientedBoundingBox obb = new OrientedBoundingBox();
			obb.center(_myCenter);
			return obb.merge(this);
		}

		default:
			return null;
		}
	}

	/**
	 * <code>clone</code> creates a new BoundingSphere object containing the
	 * same data as this one.
	 * 
	 * @param store
	 *            where to store the cloned information. if null or wrong class,
	 *            a new store is created.
	 * @return the new BoundingSphere
	 */
	@Override
	public CCBoundingVolume clone(final CCBoundingVolume store) {
		if (store != null && store.type() == Type.Sphere) {
			final CCBoundingSphere rVal = (CCBoundingSphere) store;
			rVal._myCenter.set(_myCenter);
			rVal.setRadius(_radius);
			rVal._checkPlane = _checkPlane;
			return rVal;
		}

		return new CCBoundingSphere(radius(), _myCenter);
	}

	@Override
	public String toString() {
		return "com.ardor3d.scene.BoundingSphere [Radius: " + radius() + " Center: " + _myCenter + "]";
	}

	@Override
	public boolean intersects(final CCBoundingVolume bv) {
		if (bv == null) {
			return false;
		}

		return bv.intersectsSphere(this);
	}

	@Override
	public boolean intersectsSphere(final CCBoundingSphere bs) {
		if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bs._myCenter)) {
			return false;
		}

		final CCVector3 diff = _myCompVect1.set(center()).subtractLocal(bs.center());
		final double rsum = radius() + bs.radius();
		return (diff.dot(diff) <= rsum * rsum);
	}

	@Override
	public boolean intersectsBoundingBox(final CCBoundingBox bb) {
		if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bb._myCenter)) {
			return false;
		}

		if (CCMath.abs(bb._myCenter.x - center().x) < radius() + bb.getXExtent()
				&& CCMath.abs(bb._myCenter.y - center().y) < radius() + bb.getYExtent()
				&& CCMath.abs(bb._myCenter.z - center().z) < radius() + bb.getZExtent()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean intersectsOrientedBoundingBox(final OrientedBoundingBox obb) {
		return obb.intersectsSphere(this);
	}

	@Override
	public boolean intersects(final CCRay3 ray) {
		if (!CCVector3.isValid(_myCenter)) {
			return false;
		}

		final CCVector3 diff = ray.getOrigin().subtract(center(), _myCompVect1);
		final double radiusSquared = radius() * radius();
		final double a = diff.dot(diff) - radiusSquared;
		if (a <= 0.0) {
			// in sphere
			return true;
		}

		// outside sphere
		final CCVector3 dir = _myCompVect2.set(ray.getDirection());
		final double b = dir.dot(diff);
		if (b >= 0.0) {
			return false;
		}
		return b * b >= a;
	}

	@Override
	public IntersectionRecord intersectsWhere(final CCRay3 ray) {

		final CCVector3 diff = ray.getOrigin().subtract(center(), _myCompVect1);
		final double a = diff.dot(diff) - (radius() * radius());
		double a1, discr, root;
		if (a <= 0.0) {
			// inside sphere
			a1 = ray.getDirection().dot(diff);
			discr = (a1 * a1) - a;
			root = CCMath.sqrt(discr);
			final double[] distances = new double[] { root - a1 };
			final CCVector3[] points = new CCVector3[] { ray.getDirection().multiply(distances[0], new CCVector3())
					.addLocal(ray.getOrigin()) };
			return new IntersectionRecord(distances, points);
		}

		a1 = ray.getDirection().dot(diff);
		if (a1 >= 0.0) {
			// No intersection
			return null;
		}

		discr = a1 * a1 - a;
		if (discr < 0.0) {
			return null;
		} else if (discr >= CCMath.ZERO_TOLERANCE) {
			root = CCMath.sqrt(discr);
			final double[] distances = new double[] { -a1 - root, -a1 + root };
			final CCVector3[] points = new CCVector3[] {
					ray.getDirection().multiply(distances[0], new CCVector3()).addLocal(ray.getOrigin()),
					ray.getDirection().multiply(distances[1], new CCVector3()).addLocal(ray.getOrigin()) };
			final IntersectionRecord record = new IntersectionRecord(distances, points);
			return record;
		}

		final double[] distances = new double[] { -a1 };
		final CCVector3[] points = new CCVector3[] { ray.getDirection().multiply(distances[0], new CCVector3()).addLocal(ray.getOrigin()) };
		return new IntersectionRecord(distances, points);
	}

	@Override
	public boolean contains(final CCVector3 point) {
		return center().distanceSquared(point) < (radius() * radius());
	}

	@Override
	public double distanceToEdge(final CCVector3 point) {
		return _myCenter.distance(point) - radius();
	}

	@Override
	public double getVolume() {
		return 4 * CCMath.ONE_THIRD * CCMath.PI * radius() * radius() * radius();
	}
}