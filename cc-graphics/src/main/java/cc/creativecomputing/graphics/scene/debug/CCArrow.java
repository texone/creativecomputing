/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.graphics.scene.debug;

import cc.creativecomputing.graphics.scene.CCGeometry;
import cc.creativecomputing.graphics.scene.CCNode;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.shape.CCCylinder;
import cc.creativecomputing.graphics.scene.shape.CCPyramid;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCQuaternion;

/**
 * <code>Arrow</code> is basically a cylinder with a pyramid on top.
 * 
 * TODO fix cylinder
 */
public class CCArrow extends CCNode {

	protected float _myLength;
	protected float _myWidth;

	protected static final CCQuaternion rotator = new CCQuaternion().applyRotationX(CCMath.HALF_PI);

	public CCArrow() {
		this(1, 0.25f);
	}

	public CCArrow(final float theLength, final float theWidth) {
		_myLength = theLength;
		_myWidth = theWidth;

		buildArrow();
	}

	public void buildArrow() {
		// Start with cylinders:
		final CCCylinder base = new CCCylinder(4, 16, _myWidth * .75f, _myLength, true);
		base.meshData().rotatePoints(rotator);
		base.meshData().rotateNormals(rotator);
		attachChild(base);
		base.updateBoundState();

		final CCPyramid tip = new CCPyramid(2 * _myWidth, _myLength / 2f);
		tip.meshData().translatePoints(0, _myLength * .75f, 0);
		attachChild(tip);
		tip.updateBoundState();
	}

	public float length() {
		return _myLength;
	}

	public void length(final float theLength) {
		_myLength = theLength;
	}

	public float width() {
		return _myWidth;
	}

	public void width(final float theWidth) {
		_myWidth = theWidth;
	}

	public void solidColor(final CCColor theColor) {
		for (CCSpatial myChild : _myChildren) {
			if (myChild instanceof CCGeometry) {
				// ((CCGeometry) myChild).setSolidColor(color);
			}
		}
	}

	public void defaultColor(final CCColor theColor) {
		for (CCSpatial myChild : _myChildren) {
			if (myChild instanceof CCGeometry) {
				// ((CCGeometry) myChild).setDefaultColor(color);
			}
		}
	}
}
