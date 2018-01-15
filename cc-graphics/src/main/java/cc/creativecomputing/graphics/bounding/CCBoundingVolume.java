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

import java.io.Serializable;
import java.nio.FloatBuffer;

import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.graphics.intersection.IntersectionRecord;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;

public abstract class CCBoundingVolume implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        Sphere, AABB, OBB
    }

    protected int _checkPlane = 0;

    protected final CCVector3 _myCenter = new CCVector3();

    protected final CCVector3 _myCompVect1 = new CCVector3();
    protected final CCVector3 _myCompVect2 = new CCVector3();

    public CCBoundingVolume() {}

    public CCBoundingVolume(final CCVector3 center) {
        _myCenter.set(center);
    }

    /**
     * Grabs the checkplane we should check first.
     * 
     */
    public int checkPlane() {
        return _checkPlane;
    }

    /**
     * Sets the index of the plane that should be first checked during rendering.
     * 
     * @param theValue
     */
    public final void checkPlane(final int theValue) {
        _checkPlane = theValue;
    }

    /**
     * getType returns the type of bounding volume this is.
     */
    public abstract Type type();

    /**
     * 
     * <code>transform</code> alters the location of the bounding volume by a transform.
     * 
     * @param theTransform
     * @param theStore
     * @return
     */
    public abstract CCBoundingVolume transform(final CCTransform theTransform, final CCBoundingVolume theStore);

    /**
     * 
     * <code>whichSide</code> returns the side on which the bounding volume lies on a plane. Possible values are
     * POSITIVE_SIDE, NEGATIVE_SIDE, and NO_SIDE.
     * 
     * @param thePlane
     *            the plane to check against this bounding volume.
     * @return the side on which this bounding volume lies.
     */
    public abstract CCPlane.Side whichSide(CCPlane thePlane);

    /**
     * 
     * <code>computeFromPoints</code> generates a bounding volume that encompasses a collection of points.
     * 
     * @param thePoints
     *            the points to contain.
     */
    public abstract void computeFromPoints(FloatBuffer thePoints);

    /**
     * <code>merge</code> combines two bounding volumes into a single bounding volume that contains both this bounding
     * volume and the parameter volume.
     * 
     * @param theVolume
     *            the volume to combine.
     * @return the new merged bounding volume.
     */
    public abstract CCBoundingVolume merge(CCBoundingVolume theVolume);

    /**
     * <code>mergeLocal</code> combines two bounding volumes into a single bounding volume that contains both this
     * bounding volume and the parameter volume. The result is stored locally.
     * 
     * @param theVolume
     *            the volume to combine.
     * @return this
     */
    public abstract CCBoundingVolume mergeLocal(CCBoundingVolume theVolume);

    /**
     * <code>clone</code> creates a new BoundingVolume object containing the same data as this one.
     * 
     * @param theStore
     *            where to store the cloned information. if null or wrong class, a new store is created.
     * @return the new BoundingVolume
     */
    public abstract CCBoundingVolume clone(CCBoundingVolume theStore);

    /**
     * @return the distance from the center of this bounding volume to its further edge/corner. Similar to converting
     *         this BoundingVolume to a sphere and asking for radius.
     */
    public abstract double radius();

    public final CCVector3 center() {
        return _myCenter;
    }

    public final void center(final CCVector3 newCenter) {
        _myCenter.set(newCenter);
    }

    public void center(final double x, final double y, final double z) {
        _myCenter.set(x, y, z);
    }

    /**
     * Find the distance from the center of this Bounding Volume to the given point.
     * 
     * @param thePoint
     *            The point to get the distance to
     * @return distance
     */
    public final double distanceTo(final CCVector3 thePoint) {
        return _myCenter.distance(thePoint);
    }

    /**
     * Find the squared distance from the center of this Bounding Volume to the given point.
     * 
     * @param thePoint
     *            The point to get the distance to
     * @return distance
     */
    public final double distanceSquaredTo(final CCVector3 thePoint) {
        return _myCenter.distanceSquared(thePoint);
    }

    /**
     * Find the distance from the nearest edge of this Bounding Volume to the given point.
     * 
     * @param thePoint
     *            The point to get the distance to
     * @return distance
     */
    public abstract double distanceToEdge(CCVector3 thePoint);

    /**
     * determines if this bounding volume and a second given volume are intersecting. Intersecting being: one volume
     * contains another, one volume overlaps another or one volume touches another.
     * 
     * @param bv
     *            the second volume to test against.
     * @return true if this volume intersects the given volume.
     */
    public abstract boolean intersects(CCBoundingVolume bv);

    /**
     * determines if a ray intersects this bounding volume.
     * 
     * @param ray
     *            the ray to test.
     * @return true if this volume is intersected by a given ray.
     */
    public abstract boolean intersects(CCRay3 ray);

    /**
     * determines if a ray intersects this bounding volume and if so, where.
     * 
     * @param ray
     *            the ray to test.
     * @return an IntersectionRecord containing information about any intersections made by the given Ray with this
     *         bounding
     */
    public abstract IntersectionRecord intersectsWhere(CCRay3 ray);

    /**
     * determines if this bounding volume and a given bounding sphere are intersecting.
     * 
     * @param bs
     *            the bounding sphere to test against.
     * @return true if this volume intersects the given bounding sphere.
     */
    public abstract boolean intersectsSphere(CCBoundingSphere bs);

    /**
     * determines if this bounding volume and a given bounding box are intersecting.
     * 
     * @param bb
     *            the bounding box to test against.
     * @return true if this volume intersects the given bounding box.
     */
    public abstract boolean intersectsBoundingBox(CCBoundingBox bb);

    /**
     * determines if this bounding volume and a given bounding box are intersecting.
     * 
     * @param bb
     *            the bounding box to test against.
     * @return true if this volume intersects the given bounding box.
     */
    public abstract boolean intersectsOrientedBoundingBox(OrientedBoundingBox bb);

    /**
     * 
     * determines if a given point is contained within this bounding volume.
     * 
     * @param thePoint
     *            the point to check
     * @return true if the point lies within this bounding volume.
     */
    public abstract boolean contains(CCVector3 thePoint);

    /**
     * Convert this bounding volume to another, given bounding type.
     * 
     * @param newType
     *            the type of bounding volume to convert to.
     * @return a new bounding volume of the given type, containing this bounding volume.
     */
    public abstract CCBoundingVolume asType(Type newType);

    public Class<? extends CCBoundingVolume> getClassTag() {
        return this.getClass();
    }

    public abstract void computeFromPrimitives(CCGeometryData data);

    public abstract double getVolume();
}
