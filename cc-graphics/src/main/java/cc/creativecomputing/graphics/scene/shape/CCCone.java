/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.scene.shape;

import cc.creativecomputing.math.CCMath;

public class CCCone extends CCCylinder {

	public CCCone(final int axisSamples, final int radialSamples, final float radius, final float height) {
		this(axisSamples, radialSamples, radius, height, true);
	}

	public CCCone(final int axisSamples, final int radialSamples, final float radius, final float height, final boolean closed) {
		super(axisSamples, radialSamples, radius, 0, height, closed, false);
	}

	public void setHalfAngle(final float radians) {
		radii(0,CCMath.tan(radians));
	}
}
