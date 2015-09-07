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

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import cc.creativecomputing.data.CCBufferUtils;
import cc.creativecomputing.gl.data.CCGeometryData;
import cc.creativecomputing.graphics.intersection.IntersectionRecord;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCRay3;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector3;

/**
 * <code>BoundingBox</code> defines an axis-aligned cube that defines a container for a group of vertices of a
 * particular piece of geometry. This box defines a center and extents from that center along the x, y and z axis. <br>
 * <br>
 * A typical usage is to allow the class define the center and radius by calling either <code>containAABB</code> or
 * <code>averagePoints</code>. A call to <code>computeFramePoint</code> in turn calls <code>containAABB</code>.
 */
public class CCBoundingBox extends CCBoundingVolume {

    private static final long serialVersionUID = 1L;

    private double _xExtent, _yExtent, _zExtent;

    /**
     * Default constructor instantiates a new <code>BoundingBox</code> object.
     */
    public CCBoundingBox() {}

    /**
     * Constructor instantiates a new <code>BoundingBox</code> object with given values.
     */
    public CCBoundingBox(final CCBoundingBox other) {
        this(other.center(), other.getXExtent(), other.getYExtent(), other.getZExtent());
    }

    /**
     * Constructor instantiates a new <code>BoundingBox</code> object with given values.
     */
    public CCBoundingBox(final CCVector3 c, final double x, final double y, final double z) {
        _myCenter.set(c);
        setXExtent(x);
        setYExtent(y);
        setZExtent(z);
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CCBoundingBox)) {
            return false;
        }
        final CCBoundingBox b = (CCBoundingBox) other;
        return _myCenter.equals(b._myCenter) && _xExtent == b._xExtent && _yExtent == b._yExtent && _zExtent == b._zExtent;
    }

    @Override
    public Type type() {
        return Type.AABB;
    }

    public void setXExtent(final double xExtent) {
        _xExtent = xExtent;
    }

    public double getXExtent() {
        return _xExtent;
    }

    public void setYExtent(final double yExtent) {
        _yExtent = yExtent;
    }

    public double getYExtent() {
        return _yExtent;
    }

    public void setZExtent(final double zExtent) {
        _zExtent = zExtent;
    }

    public double getZExtent() {
        return _zExtent;
    }

    @Override
    public double radius() {
        return CCMath.sqrt(_xExtent * _xExtent + _yExtent * _yExtent + _zExtent * _zExtent);
    }

    // Some transform matrices are not in decomposed form and in this
    // situation we need to use a different, more robust, algorithm
    // for computing the new bounding box.
    @Override
    public CCBoundingVolume transform(final CCTransform transform, final CCBoundingVolume store) {

        if (transform.isRotationMatrix()) {
            return transformRotational(transform, store);
        }

        CCBoundingBox box;
        if (store == null || store.type() != Type.AABB) {
            box = new CCBoundingBox();
        } else {
            box = (CCBoundingBox) store;
        }

        final CCVector3[] corners = new CCVector3[8];
        for (int i = 0; i < corners.length; i++) {
            corners[i] = new CCVector3();
        }
        getCorners(corners);

        // Transform all of these points by the transform
        for (int i = 0; i < corners.length; i++) {
            transform.applyForward(corners[i]);
        }
        // Now compute based on these transformed points
        double minX = corners[0].x;
        double minY = corners[0].y;
        double minZ = corners[0].z;
        double maxX = minX;
        double maxY = minY;
        double maxZ = minZ;
        for (int i = 1; i < corners.length; i++) {
            final double curX = corners[i].x;
            final double curY = corners[i].y;
            final double curZ = corners[i].z;
            minX = CCMath.min(minX, curX);
            minY = CCMath.min(minY, curY);
            minZ = CCMath.min(minZ, curZ);
            maxX = CCMath.max(maxX, curX);
            maxY = CCMath.max(maxY, curY);
            maxZ = CCMath.max(maxZ, curZ);
        }

        final double ctrX = (maxX + minX) * 0.5f;
        final double ctrY = (maxY + minY) * 0.5f;
        final double ctrZ = (maxZ + minZ) * 0.5f;

        box._myCenter.set(ctrX, ctrY, ctrZ);
        box._xExtent = maxX - ctrX;
        box._yExtent = maxY - ctrY;
        box._zExtent = maxZ - ctrZ;

        return box;
    }

    public CCBoundingVolume transformRotational(final CCTransform transform, final CCBoundingVolume store) {

        final CCMatrix3x3 rotate = transform.getMatrix();
        final CCVector3 scale = transform.scale();
        final CCVector3 translate = transform.translation();

        CCBoundingBox box;
        if (store == null || store.type() != Type.AABB) {
            box = new CCBoundingBox();
        } else {
            box = (CCBoundingBox) store;
        }

        _myCenter.multiply(scale, box._myCenter);
        rotate.applyPost(box._myCenter, box._myCenter);
        box._myCenter.addLocal(translate);

        final CCMatrix3x3 transMatrix = new CCMatrix3x3();
        transMatrix.set(rotate);
        // Make the rotation matrix all positive to get the maximum x/y/z extent
        transMatrix.setValue(0, 0, CCMath.abs(transMatrix._m00));
        transMatrix.setValue(0, 1, CCMath.abs(transMatrix._m01));
        transMatrix.setValue(0, 2, CCMath.abs(transMatrix._m02));
        transMatrix.setValue(1, 0, CCMath.abs(transMatrix._m10));
        transMatrix.setValue(1, 1, CCMath.abs(transMatrix._m11));
        transMatrix.setValue(1, 2, CCMath.abs(transMatrix._m12));
        transMatrix.setValue(2, 0, CCMath.abs(transMatrix._m20));
        transMatrix.setValue(2, 1, CCMath.abs(transMatrix._m21));
        transMatrix.setValue(2, 2, CCMath.abs(transMatrix._m22));

        _myCompVect1.set(getXExtent() * scale.x, getYExtent() * scale.y, getZExtent() * scale.z);
        transMatrix.applyPost(_myCompVect1, _myCompVect1);
        // Assign the biggest rotations after scales.
        box.setXExtent(CCMath.abs(_myCompVect1.x));
        box.setYExtent(CCMath.abs(_myCompVect1.y));
        box.setZExtent(CCMath.abs(_myCompVect1.z));

        return box;
    }

    private void checkMinMax(final CCVector3 min, final CCVector3 max, final CCVector3 point) {
        if (point.x < min.x) {
            min.x = point.x;
        }
        if (point.x > max.x) {
            max.x = point.x;
        }

        if (point.y < min.y) {
            min.y = point.y;
        }
        if (point.y > max.y) {
            max.y = point.y;
        }

        if (point.z < min.z) {
            min.z = point.z;
        }
        if (point.z > max.z) {
            max.z = point.z;
        }
    }

    /**
     * <code>computeFromPoints</code> creates a new Bounding Box from a given set of points. It uses the
     * <code>containAABB</code> method as default.
     * 
     * @param points
     *            the points to contain.
     */
    @Override
    public void computeFromPoints(final FloatBuffer points) {
    	 if (points == null) {
             return;
         }

         points.rewind();
         if (points.remaining() <= 2) {
             return;
         }

         CCBufferUtils.populateFromBuffer(_myCompVect1, points, 0);
         double minX = _myCompVect1.x, minY = _myCompVect1.y, minZ = _myCompVect1.z;
         double maxX = _myCompVect1.x, maxY = _myCompVect1.y, maxZ = _myCompVect1.z;

         for (int i = 1, len = points.remaining() / 3; i < len; i++) {
             CCBufferUtils.populateFromBuffer(_myCompVect1, points, i);

             if (_myCompVect1.x < minX) {
                 minX = _myCompVect1.x;
             } else if (_myCompVect1.x > maxX) {
                 maxX = _myCompVect1.x;
             }

             if (_myCompVect1.y < minY) {
                 minY = _myCompVect1.y;
             } else if (_myCompVect1.y > maxY) {
                 maxY = _myCompVect1.y;
             }

             if (_myCompVect1.z < minZ) {
                 minZ = _myCompVect1.z;
             } else if (_myCompVect1.z > maxZ) {
                 maxZ = _myCompVect1.z;
             }
         }

         _myCenter.set(minX + maxX, minY + maxY, minZ + maxZ);
         _myCenter.multiplyLocal(0.5f);

         setXExtent(maxX - _myCenter.x);
         setYExtent(maxY - _myCenter.y);
         setZExtent(maxZ - _myCenter.z);
    }

    @Override
    public void computeFromPrimitives(final CCGeometryData data) {

        final CCVector3 min = _myCompVect1.set(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        final CCVector3 max = _myCompVect2.set(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);

		for (CCVector3 theVertex : CCBufferUtils.getVector3Array(data.vertices(), new CCVector3())) {
			checkMinMax(min, max, theVertex);
		}

        _myCenter.set(min.addLocal(max));
        _myCenter.multiplyLocal(0.5f);

        setXExtent(max.x - _myCenter.x);
        setYExtent(max.y - _myCenter.y);
        setZExtent(max.z - _myCenter.z);
    }

    /**
     * <code>whichSide</code> takes a plane (typically provided by a view frustum) to determine which side this bound is
     * on.
     * 
     * @param plane
     *            the plane to check against.
     */
    @Override
    public CCPlane.Side whichSide(final CCPlane plane) {
        final CCVector3 normal = plane.getNormal();
        final double radius = CCMath.abs(getXExtent() * normal.x) + CCMath.abs(getYExtent() * normal.y)
                + CCMath.abs(getZExtent() * normal.z);

        final double distance = plane.pseudoDistance(_myCenter);

        if (distance < -radius) {
            return CCPlane.Side.Inside;
        } else if (distance > radius) {
            return CCPlane.Side.Outside;
        } else {
            return CCPlane.Side.Neither;
        }
    }

    /**
     * <code>merge</code> combines this sphere with a second bounding sphere. This new sphere contains both bounding
     * spheres and is returned.
     * 
     * @param volume
     *            the sphere to combine with this sphere.
     * @return the new sphere
     */
    @Override
    public CCBoundingVolume merge(final CCBoundingVolume volume) {
        if (volume == null) {
            return this;
        }

        switch (volume.type()) {
            case AABB: {
                final CCBoundingBox vBox = (CCBoundingBox) volume;
                return merge(vBox._myCenter, vBox.getXExtent(), vBox.getYExtent(), vBox.getZExtent(), new CCBoundingBox(
                        new CCVector3(0, 0, 0), 0, 0, 0));
            }

            case Sphere: {
                final CCBoundingSphere vSphere = (CCBoundingSphere) volume;
                return merge(vSphere._myCenter, vSphere.radius(), vSphere.radius(), vSphere.radius(),
                        new CCBoundingBox(new CCVector3(0, 0, 0), 0, 0, 0));
            }

            case OBB: {
                final OrientedBoundingBox box = (OrientedBoundingBox) volume;
                final CCBoundingBox rVal = (CCBoundingBox) this.clone(null);
                return rVal.mergeOBB(box);
            }

            default:
                return null;
        }
    }

    /**
     * <code>mergeLocal</code> combines this sphere with a second bounding sphere locally. Altering this sphere to
     * contain both the original and the additional sphere volumes;
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
            case AABB: {
                final CCBoundingBox vBox = (CCBoundingBox) volume;
                return merge(vBox._myCenter, vBox.getXExtent(), vBox.getYExtent(), vBox.getZExtent(), this);
            }

            case Sphere: {
                final CCBoundingSphere vSphere = (CCBoundingSphere) volume;
                return merge(vSphere._myCenter, vSphere.radius(), vSphere.radius(), vSphere.radius(), this);
            }

            case OBB: {
                return mergeOBB((OrientedBoundingBox) volume);
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
                return this.clone(null);
            }

            case Sphere: {
                final CCBoundingSphere sphere = new CCBoundingSphere(0, _myCenter);
                return sphere.merge(this);
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
     * Merges this AABB with the given OBB.
     * 
     * @param volume
     *            the OBB to merge this AABB with.
     * @return This AABB extended to fit the given OBB.
     */
    private CCBoundingBox mergeOBB(final OrientedBoundingBox volume) {
        // check for infinite bounds to prevent NaN values
        if (Double.isInfinite(getXExtent()) || Double.isInfinite(getYExtent()) || Double.isInfinite(getZExtent()) || CCVector3.isInfinite(volume.getExtent())) {
            center(CCVector3.ZERO);
            setXExtent(Double.POSITIVE_INFINITY);
            setYExtent(Double.POSITIVE_INFINITY);
            setZExtent(Double.POSITIVE_INFINITY);
            return this;
        }

        if (!volume.correctCorners) {
            volume.computeCorners();
        }

        double minX, minY, minZ;
        double maxX, maxY, maxZ;

        minX = _myCenter.x - getXExtent();
        minY = _myCenter.y - getYExtent();
        minZ = _myCenter.z - getZExtent();

        maxX = _myCenter.x + getXExtent();
        maxY = _myCenter.y + getYExtent();
        maxZ = _myCenter.z + getZExtent();

        for (int i = 1; i < volume._vectorStore.length; i++) {
            final CCVector3 temp = volume._vectorStore[i];
            if (temp.x < minX) {
                minX = temp.x;
            } else if (temp.x > maxX) {
                maxX = temp.x;
            }

            if (temp.y < minY) {
                minY = temp.y;
            } else if (temp.y > maxY) {
                maxY = temp.y;
            }

            if (temp.z < minZ) {
                minZ = temp.z;
            } else if (temp.z > maxZ) {
                maxZ = temp.z;
            }
        }

        _myCenter.set(minX + maxX, minY + maxY, minZ + maxZ);
        _myCenter.multiplyLocal(0.5f);

        setXExtent(maxX - _myCenter.x);
        setYExtent(maxY - _myCenter.y);
        setZExtent(maxZ - _myCenter.z);
        return this;
    }

    /**
     * <code>merge</code> combines this bounding box with another box which is defined by the center, x, y, z extents.
     * 
     * @param boxCenter
     *            the center of the box to merge with
     * @param boxX
     *            the x extent of the box to merge with.
     * @param boxY
     *            the y extent of the box to merge with.
     * @param boxZ
     *            the z extent of the box to merge with.
     * @param store
     *            the box to store our results in.
     * @return the resulting merged box.
     */
    private CCBoundingBox merge(final CCVector3 boxCenter, final double boxX, final double boxY, final double boxZ,
            final CCBoundingBox store) {
        // check for infinite bounds to prevent NaN values
        if (Double.isInfinite(getXExtent()) || Double.isInfinite(getYExtent()) || Double.isInfinite(getZExtent())
                || Double.isInfinite(boxX) || Double.isInfinite(boxY) || Double.isInfinite(boxZ)) {
            store.center(CCVector3.ZERO);
            store.setXExtent(Double.POSITIVE_INFINITY);
            store.setYExtent(Double.POSITIVE_INFINITY);
            store.setZExtent(Double.POSITIVE_INFINITY);
            return store;
        }

        _myCompVect1.x = _myCenter.x - getXExtent();
        if (_myCompVect1.x > boxCenter.x - boxX) {
            _myCompVect1.x = boxCenter.x - boxX;
        }
        _myCompVect1.y = _myCenter.y - getYExtent();
        if (_myCompVect1.y > boxCenter.y - boxY) {
            _myCompVect1.y = boxCenter.y - boxY;
        }
        _myCompVect1.z = _myCenter.z - getZExtent();
        if (_myCompVect1.z > boxCenter.z - boxZ) {
            _myCompVect1.z = boxCenter.z - boxZ;
        }

        _myCompVect2.x = _myCenter.x + getXExtent();
        if (_myCompVect2.x < boxCenter.x + boxX) {
            _myCompVect2.x = boxCenter.x + boxX;
        }
        _myCompVect2.y = _myCenter.y + getYExtent();
        if (_myCompVect2.y < boxCenter.y + boxY) {
            _myCompVect2.y = boxCenter.y + boxY;
        }
        _myCompVect2.z = _myCenter.z + getZExtent();
        if (_myCompVect2.z < boxCenter.z + boxZ) {
            _myCompVect2.z = boxCenter.z + boxZ;
        }

        store._myCenter.set(_myCompVect2).addLocal(_myCompVect1).multiplyLocal(0.5f);

        store.setXExtent(_myCompVect2.x - store._myCenter.x);
        store.setYExtent(_myCompVect2.y - store._myCenter.y);
        store.setZExtent(_myCompVect2.z - store._myCenter.z);

        return store;
    }

    /**
     * <code>clone</code> creates a new BoundingBox object containing the same data as this one.
     * 
     * @param store
     *            where to store the cloned information. if null or wrong class, a new store is created.
     * @return the new BoundingBox
     */
    @Override
    public CCBoundingVolume clone(final CCBoundingVolume store) {
        if (store != null && store.type() == Type.AABB) {
            final CCBoundingBox rVal = (CCBoundingBox) store;
            rVal._myCenter.set(_myCenter);
            rVal.setXExtent(_xExtent);
            rVal.setYExtent(_yExtent);
            rVal.setZExtent(_zExtent);
            rVal._checkPlane = _checkPlane;
            return rVal;
        }

        final CCBoundingBox rVal = new CCBoundingBox(_myCenter, getXExtent(), getYExtent(), getZExtent());
        return rVal;
    }

    /**
     * <code>toString</code> returns the string representation of this object. The form is:
     * "Radius: RRR.SSSS Center: <Vector>".
     * 
     * @return the string representation of this.
     */
    @Override
    public String toString() {
        return "com.ardor3d.scene.BoundingBox [Center: " + _myCenter + "  xExtent: " + getXExtent() + "  yExtent: "
                + getYExtent() + "  zExtent: " + getZExtent() + "]";
    }

    @Override
    public boolean intersects(final CCBoundingVolume bv) {
        if (bv == null) {
            return false;
        }

        return bv.intersectsBoundingBox(this);
    }

    @Override
    public boolean intersectsSphere(final CCBoundingSphere bs) {
        if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bs._myCenter)) {
            return false;
        }

        if (CCMath.abs(_myCenter.x - bs.center().x) < bs.radius() + getXExtent()
                && CCMath.abs(_myCenter.y - bs.center().y) < bs.radius() + getYExtent()
                && CCMath.abs(_myCenter.z - bs.center().z) < bs.radius() + getZExtent()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean intersectsBoundingBox(final CCBoundingBox bb) {
        if (!CCVector3.isValid(_myCenter) || !CCVector3.isValid(bb._myCenter)) {
            return false;
        }

        if (_myCenter.x + getXExtent() < bb._myCenter.x - bb.getXExtent()
                || _myCenter.x - getXExtent() > bb._myCenter.x + bb.getXExtent()) {
            return false;
        } else if (_myCenter.y + getYExtent() < bb._myCenter.y - bb.getYExtent()
                || _myCenter.y - getYExtent() > bb._myCenter.y + bb.getYExtent()) {
            return false;
        } else if (_myCenter.z + getZExtent() < bb._myCenter.z - bb.getZExtent()
                || _myCenter.z - getZExtent() > bb._myCenter.z + bb.getZExtent()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean intersectsOrientedBoundingBox(final OrientedBoundingBox obb) {
        return obb.intersectsBoundingBox(this);
    }

    @Override
    public boolean intersects(final CCRay3 ray) {
        if (!CCVector3.isValid(_myCenter)) {
            return false;
        }

        final CCVector3 diff = ray.getOrigin().subtract(_myCenter, _myCompVect1);

        final CCVector3 direction = ray.getDirection();

        final double[] t = { 0.0f, Double.POSITIVE_INFINITY };

        // Check for degenerate cases and pad using zero tolerance. Should give close enough result.
        double x = getXExtent();
        if (x < CCMath.ZERO_TOLERANCE && x >= 0) {
            x = CCMath.ZERO_TOLERANCE;
        }
        double y = getYExtent();
        if (y < CCMath.ZERO_TOLERANCE && y >= 0) {
            y = CCMath.ZERO_TOLERANCE;
        }
        double z = getZExtent();
        if (z < CCMath.ZERO_TOLERANCE && z >= 0) {
            z = CCMath.ZERO_TOLERANCE;
        }

        // Special case.
        if (Double.isInfinite(x) && Double.isInfinite(y) && Double.isInfinite(z)) {
            return true;
        }

        final boolean notEntirelyClipped = clip(direction.x, -diff.x - x, t)
                && clip(-direction.x, diff.x - x, t) && clip(direction.y, -diff.y - y, t)
                && clip(-direction.y, diff.y - y, t) && clip(direction.z, -diff.z - z, t)
                && clip(-direction.z, diff.z - z, t);

        return (notEntirelyClipped && (t[0] != 0.0 || t[1] != Double.POSITIVE_INFINITY));
    }

    @Override
    public IntersectionRecord intersectsWhere(final CCRay3 ray) {
        if (!CCVector3.isValid(_myCenter)) {
            return null;
        }

        final CCVector3 diff = ray.getOrigin().subtract(_myCenter, _myCompVect1);

        final CCVector3 direction = ray.getDirection();

        final double[] t = { 0.0f, Double.POSITIVE_INFINITY };

        // Check for degenerate cases and pad using zero tolerance. Should give close enough result.
        double x = getXExtent();
        if (x < CCMath.ZERO_TOLERANCE && x >= 0) {
            x = CCMath.ZERO_TOLERANCE;
        }
        double y = getYExtent();
        if (y < CCMath.ZERO_TOLERANCE && y >= 0) {
            y = CCMath.ZERO_TOLERANCE;
        }
        double z = getZExtent();
        if (z < CCMath.ZERO_TOLERANCE && z >= 0) {
            z = CCMath.ZERO_TOLERANCE;
        }

        final boolean notEntirelyClipped = clip(direction.x, -diff.x - x, t)
                && clip(-direction.x, diff.x - x, t) && clip(direction.y, -diff.y - y, t)
                && clip(-direction.y, diff.y - y, t) && clip(direction.z, -diff.z - z, t)
                && clip(-direction.z, diff.z - z, t);

        if (notEntirelyClipped && (t[0] != 0.0 || t[1] != Double.POSITIVE_INFINITY)) {
            if (t[1] > t[0]) {
                final double[] distances = t;
                final CCVector3[] points = new CCVector3[] {
                        new CCVector3(ray.getDirection()).multiplyLocal(distances[0]).addLocal(ray.getOrigin()),
                        new CCVector3(ray.getDirection()).multiplyLocal(distances[1]).addLocal(ray.getOrigin()) };
                return new IntersectionRecord(distances, points);
            }

            final double[] distances = new double[] { t[0] };
            final CCVector3[] points = new CCVector3[] { new CCVector3(ray.getDirection()).multiplyLocal(distances[0])
                    .addLocal(ray.getOrigin()), };
            return new IntersectionRecord(distances, points);
        }

        return null;

    }

    @Override
    public boolean contains(final CCVector3 point) {
        return CCMath.abs(_myCenter.x - point.x) < getXExtent()
                && CCMath.abs(_myCenter.y - point.y) < getYExtent()
                && CCMath.abs(_myCenter.z - point.z) < getZExtent();
    }

    @Override
    public double distanceToEdge(final CCVector3 point) {
        // compute coordinates of point in box coordinate system
        final CCVector3 closest = point.subtract(_myCenter, _myCompVect1);

        // project test point onto box
        double sqrDistance = 0.0f;
        double delta;

        if (closest.x < -getXExtent()) {
            delta = closest.x + getXExtent();
            sqrDistance += delta * delta;
            closest.x = -getXExtent();
        } else if (closest.x > getXExtent()) {
            delta = closest.x - getXExtent();
            sqrDistance += delta * delta;
            closest.x = getXExtent();
        }

        if (closest.y < -getYExtent()) {
            delta = closest.y + getYExtent();
            sqrDistance += delta * delta;
            closest.y = -getYExtent();
        } else if (closest.y > getYExtent()) {
            delta = closest.y - getYExtent();
            sqrDistance += delta * delta;
            closest.y = getYExtent();
        }

        if (closest.z < -getZExtent()) {
            delta = closest.z + getZExtent();
            sqrDistance += delta * delta;
            closest.z = -getZExtent();
        } else if (closest.z > getZExtent()) {
            delta = closest.z - getZExtent();
            sqrDistance += delta * delta;
            closest.z = getZExtent();
        }

        return CCMath.sqrt(sqrDistance);
    }

    /**
     * Get our corners using the bounding center and extents.
     * 
     * @param store
     *            An optional store. Must be at least length of 8. If null, one will be created for you.
     * @return array filled with our corners.
     * @throws ArrayIndexOutOfBoundsException
     *             if our store is length < 8.
     */
    public CCVector3[] getCorners(CCVector3[] store) {
        if (store == null) {
            store = new CCVector3[8];
            for (int i = 0; i < store.length; i++) {
                store[i] = new CCVector3();
            }
        }
        store[0].set(_myCenter.x + _xExtent, _myCenter.y + _yExtent, _myCenter.z + _zExtent);
        store[1].set(_myCenter.x + _xExtent, _myCenter.y + _yExtent, _myCenter.z - _zExtent);
        store[2].set(_myCenter.x + _xExtent, _myCenter.y - _yExtent, _myCenter.z + _zExtent);
        store[3].set(_myCenter.x + _xExtent, _myCenter.y - _yExtent, _myCenter.z - _zExtent);
        store[4].set(_myCenter.x - _xExtent, _myCenter.y + _yExtent, _myCenter.z + _zExtent);
        store[5].set(_myCenter.x - _xExtent, _myCenter.y + _yExtent, _myCenter.z - _zExtent);
        store[6].set(_myCenter.x - _xExtent, _myCenter.y - _yExtent, _myCenter.z + _zExtent);
        store[7].set(_myCenter.x - _xExtent, _myCenter.y - _yExtent, _myCenter.z - _zExtent);
        return store;
    }

    /**
     * <code>clip</code> determines if a line segment intersects the current test plane.
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

    /**
     * Query extent.
     * 
     * @param store
     *            where extent gets stored - null to return a new vector
     * @return store / new vector
     */
    public CCVector3 getExtent(CCVector3 store) {
        if (store == null) {
            store = new CCVector3();
        }
        store.set(getXExtent(), getYExtent(), getZExtent());
        return store;
    }

    @Override
    public double getVolume() {
        return (8 * getXExtent() * getYExtent() * getZExtent());
    }
}
