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

public class CCRay3 extends CCLine3Base {

	private static final long serialVersionUID = 1L;
	
	public static CCRay3 createFromLine(CCVector3 theStart, CCVector3 theEnd){
		return new CCRay3(theStart.clone(), theStart.subtract(theEnd).normalize());
	}

	/**
	 * Constructs a new ray with an origin at (0,0,0) and a direction of
	 * (0,0,1).
	 */
	public CCRay3() {
		super(CCVector3.ZERO, CCVector3.UNIT_Z);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param theSource
	 *            the ray to copy from.
	 */
	public CCRay3(final CCRay3 theSource) {
		this(theSource.getOrigin(), theSource.getDirection());
	}

	/**
	 * Constructs a new ray using the supplied origin point and unit length
	 * direction vector
	 * 
	 * @param theOrigin
	 * @param theDirection
	 *            - unit length
	 */
	public CCRay3(final CCVector3 theOrigin, final CCVector3 theDirection) {
		super(theOrigin, theDirection);
	}

	/**
	 * Copies the values of the given source ray into this ray.
	 * 
	 * @param theSource
	 * @return this ray for chaining
	 * @throws NullPointerException
	 *             if source is null.
	 */
	public CCRay3 set(final CCRay3 theSource) {
		_myOrigin.set(theSource.getOrigin());
		_myDirection.set(theSource.getDirection());
		return this;
	}

	/**
	 * @param theWorldVertices
	 *            an array of vectors describing a polygon
	 * @return the distance from our origin to the primitive or
	 *         POSITIVE_INFINITY if we do not intersect.
	 */
	public double getDistanceToPrimitive(final CCVector3[] theWorldVertices) {
		// Intersection test
		final CCVector3 intersect = new CCVector3();
		try {
			if (intersects(theWorldVertices, intersect)) {
				return getOrigin().distance(intersect);
			}
		} finally {

		}
		return Float.POSITIVE_INFINITY;
	}

	/**
	 * @param thePolygonVertices
	 * @param theLocationStore
	 * @return true if this ray intersects a polygon described by the given
	 *         vertices.
	 */
	public boolean intersects(final CCVector3[] thePolygonVertices, final CCVector3 theLocationStore) {
		if (thePolygonVertices.length == 3) {
			// TRIANGLE
			return intersectsTriangle(thePolygonVertices[0], thePolygonVertices[1], thePolygonVertices[2], theLocationStore);
		} else if (thePolygonVertices.length == 4) {
			// QUAD
			return intersectsQuad(thePolygonVertices[0], thePolygonVertices[1], thePolygonVertices[2], thePolygonVertices[3], theLocationStore);
		}
		// TODO: Add support for line and point
		return false;
	}

	/**
	 * @param theA
	 * @param theB
	 * @param theC
	 * @param theLocationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this CCVector3
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points.
	 * @throws NullPointerException
	 *             if any of the points are null.
	 */
	public boolean intersectsTriangle(
		final CCVector3 theA, 
		final CCVector3 theB, 
		final CCVector3 theC, 
		final CCVector3 theLocationStore
	) {
		return intersects(theA, theB, theC, theLocationStore, false);
	}

	/**
	 * @param pointA
	 * @param pointB
	 * @param pointC
	 * @param locationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this CCVector3 as (t,
	 *            u, v) where t is the distance from the _origin to the point of
	 *            intersection and (u, v) is the intersection point on the
	 *            triangle plane.
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points.
	 * @throws NullPointerException
	 *             if any of the points are null.
	 */
	public boolean intersectsTrianglePlanar(
		final CCVector3 pointA, 
		final CCVector3 pointB, 
		final CCVector3 pointC,
		final CCVector3 locationStore
	) {
		return intersects(pointA, pointB, pointC, locationStore, true);
	}

	/**
	 * @param theA
	 * @param theB
	 * @param theC
	 * @param theD
	 * @param theLocationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this CCVector3
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points. The points are assumed to be coplanar.
	 * @throws NullPointerException
	 *             if any of the points are null.
	 */
	public boolean intersectsQuad(
		final CCVector3 theA, 
		final CCVector3 theB, 
		final CCVector3 theC, 
		final CCVector3 theD,
		final CCVector3 theLocationStore
	) {
		return intersects(theA, theB, theC, theLocationStore, false) || intersects(theA, theC, theD, theLocationStore, false);
	}

	/**
	 * @param theA
	 * @param theB
	 * @param theC
	 * @param theD
	 * @param theLocationStore
	 *            if not null, and this ray intersects, the point of
	 *            intersection is calculated and stored in this CCVector3 as (t,
	 *            u, v) where t is the distance from the _origin to the point of
	 *            intersection and (u, v) is the intersection point on the
	 *            triangle plane.
	 * @return true if this ray intersects a quad formed by the given four
	 *         points. The points are assumed to be coplanar.
	 * @throws NullPointerException
	 *             if any of the points are null.
	 */
	public boolean intersectsQuadPlanar(
		final CCVector3 theA, 
		final CCVector3 theB, 
		final CCVector3 theC, 
		final CCVector3 theD,
		final CCVector3 theLocationStore
	) {
		return intersects(theA, theB, theC, theLocationStore, true) || intersects(theA, theC, theD, theLocationStore, true);
	}

	/**
	 * Ray vs triangle implementation.
	 * 
	 * @param theA
	 * @param theB
	 * @param theC
	 * @param theLocationStore
	 * @param theDoPlanar
	 * @return true if this ray intersects a triangle formed by the given three
	 *         points.
	 * @throws NullPointerException
	 *             if any of the points are null.
	 */
	protected boolean intersects(
		final CCVector3 theA, 
		final CCVector3 theB, 
		final CCVector3 theC, 
		final CCVector3 theLocationStore,
		final boolean theDoPlanar
	) {
		final CCVector3 diff = _myOrigin.subtract(theA);
		final CCVector3 edge1 = theB.subtract(theA);
		final CCVector3 edge2 = theC.subtract(theA);
		final CCVector3 norm = edge1.cross(edge2);

		double dirDotNorm = _myDirection.dot(norm);
		double sign;
		if (dirDotNorm > CCMath.FLT_EPSILON) {
			sign = 1.0f;
		} else if (dirDotNorm < -CCMath.FLT_EPSILON) {
			sign = -1.0f;
			dirDotNorm = -dirDotNorm;
		} else {
			// ray and triangle/quad are parallel
			return false;
		}

		final double dirDotDiffxEdge2 = sign * _myDirection.dot(diff.cross(edge2, edge2));
		boolean result = false;
		if (dirDotDiffxEdge2 >= 0.0) {
			final double dirDotEdge1xDiff = sign * _myDirection.dot(edge1.crossLocal(diff));
			if (dirDotEdge1xDiff >= 0.0) {
				if (dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm) {
					final double diffDotNorm = -sign * diff.dot(norm);
					if (diffDotNorm >= 0.0) {
						// ray intersects triangle
						// if storage vector is null, just return true,
						if (theLocationStore == null) {
							return true;
						}
						// else fill in.
						final double inv = 1f / dirDotNorm;
						final double t = diffDotNorm * inv;
						if (!theDoPlanar) {
							theLocationStore.set(_myOrigin).addLocal(_myDirection.x * t, _myDirection.y * t, _myDirection.z * t);
						} else {
							// these weights can be used to determine
							// interpolated values, such as texture coord.
							// eg. texcoord s,t at intersection point:
							// s = w0*s0 + w1*s1 + w2*s2;
							// t = w0*t0 + w1*t1 + w2*t2;
							final double w1 = dirDotDiffxEdge2 * inv;
							final double w2 = dirDotEdge1xDiff * inv;
							// double w0 = 1.0 - w1 - w2;
							theLocationStore.set(t, w1, w2);
						}
						result = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param thePlane
	 * @param theLocationStore
	 *            if not null, and this ray intersects the plane, the world
	 *            location of the point of intersection is stored in this
	 *            vector.
	 * @return true if the ray collides with the given Plane
	 * @throws NullPointerException
	 *             if the plane is null.
	 */
	public CCVector3 intersectsPlane(final CCPlane thePlane, CCVector3 theLocationStore) {
		final CCVector3 normal = thePlane.normal();
		final double denominator = normal.dot(_myDirection);

		if (denominator > -CCMath.FLT_EPSILON && denominator < CCMath.FLT_EPSILON) {
			return null; // coplanar
		}

		final double numerator = -normal.dot(_myOrigin) + thePlane.constant();
		final double ratio = numerator / denominator;

		if (ratio < CCMath.FLT_EPSILON) {
			return null; // intersects behind _origin
		}

		if (theLocationStore == null) {
			theLocationStore = new CCVector3();
		}

		theLocationStore.set(_myDirection).multiplyLocal(ratio).addLocal(_myOrigin);
		return theLocationStore;
	}
	
	public CCVector3 intersectsPlane(final CCPlane thePlane){
		return intersectsPlane(thePlane, new CCVector3());
	}

	/**
	 * @param thePoint
	 * @param theStore
	 *            if not null, the closest point is stored in this param
	 * @return the squared distance from this ray to the given point.
	 * @throws NullPointerException
	 *             if the point is null.
	 */
	public double distanceSquared(final CCVector3 thePoint, final CCVector3 theStore) {
		final CCVector3 vectorA = new CCVector3();
		vectorA.set(thePoint).subtractLocal(_myOrigin);
		final double t0 = _myDirection.dot(vectorA);
		if (t0 > 0) {
			// d = |P - (O + t*D)|
			vectorA.set(_myDirection).multiplyLocal(t0);
			vectorA.addLocal(_myOrigin);
		} else {
			// ray is closest to origin point
			vectorA.set(_myOrigin);
		}

		// Save away the closest point if requested.
		if (theStore != null) {
			theStore.set(vectorA);
		}

		thePoint.subtract(vectorA, vectorA);
		final double lSQ = vectorA.lengthSquared();
		return lSQ;
	}

	/**
	 * Check a ray... if it is null or the values of its origin or direction are
	 * NaN or infinite, return false. Else return true.
	 * 
	 * @param theRay
	 *            the ray to check
	 * @return true or false as stated above.
	 */
	public static boolean isValid(final CCRay3 theRay) {
		if (theRay == null) {
			return false;
		}

		return CCVector3.isValid(theRay.getDirection()) && CCVector3.isValid(theRay.getOrigin());
	}

	/**
	 * @return the string representation of this ray.
	 */
	@Override
	public String toString() {
		return "com.ardor3d.math.Ray [Origin: " + _myOrigin + " - Direction: " + _myDirection + "]";
	}

	/**
	 * @param theObject
	 *            the object to compare for equality
	 * @return true if this ray and the provided ray have the same constant and
	 *         normal values.
	 */
	@Override
	public boolean equals(final Object theObject) {
		if (this == theObject) {
			return true;
		}
		if (!(theObject instanceof CCRay3)) {
			return false;
		}
		final CCRay3 comp = (CCRay3) theObject;
		return _myOrigin.equals(comp.getOrigin()) && _myDirection.equals(comp.getDirection());
	}

	// /////////////////
	// Method for Cloneable
	// /////////////////

	@Override
	public CCRay3 clone() {
		return new CCRay3(this);
	}
}
