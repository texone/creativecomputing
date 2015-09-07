/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.intersection;

import cc.creativecomputing.math.CCRay3;

/**
 * An interface describing objects that can be used with our PickingUtil class.
 */
public interface Pickable {

	/**
	 * @return true if this pickable supports intersectsWorldBoundsWhere. False
	 *         if this method will always return null.
	 */
	boolean supportsBoundsIntersectionRecord();

	/**
	 * @return true if this pickable supports intersectsPrimitivesWhere. False
	 *         if this method will always return null.
	 */
	boolean supportsPrimitivesIntersectionRecord();

	/**
	 * @param theRay
	 *            a theRay, in world coordinates.
	 * @return true if the given theRay intersects our world bounding volume. false
	 *         if it does not.
	 * @throws NullPointerException
	 *             if there is no bound to check.
	 */
	boolean intersectsWorldBound(CCRay3 theRay);

	/**
	 * @param theRay
	 *            a theRay, in world coordinates.
	 * @return an intersection record containing information about where the theRay
	 *         intersected our bounding volume, or null if it does not.
	 * @throws NullPointerException
	 *             if there is no bound to check.
	 */
	IntersectionRecord intersectsWorldBoundsWhere(CCRay3 theRay);

	/**
	 * @param theRay
	 *            a theRay, in world coordinates.
	 * @return an intersection record containing information about where the ray
	 *         intersected our primitives, or null if it does not.
	 */
	IntersectionRecord intersectsPrimitivesWhere(CCRay3 theRay);

}
