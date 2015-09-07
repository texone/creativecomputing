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

import cc.creativecomputing.graphics.scene.CCNode;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix3x3;

/**
 * <code>AxisRods</code> is a convenience shape representing three axes in space.
 * 
 * TODO fix this
 */
public class CCAxisRods extends CCNode {

    protected static final CCColor xAxisColor = new CCColor(1, 0, 0, .4f);
    protected static final CCColor yAxisColor = new CCColor(0, 1, 0, .25f);
    protected static final CCColor zAxisColor = new CCColor(0, 0, 1, .4f);

    protected float _myLength;
    protected float _myWidth;
    protected boolean _myIsRightHanded;

    protected CCArrow _myXAxis;
    protected CCArrow _myYAxis;
    protected CCArrow _myZAxis;

    public CCAxisRods() {
        this( true, 1);
    }

    public CCAxisRods(final boolean theIsRightHanded, final float theBaseScale) {
        this(theIsRightHanded, theBaseScale, theBaseScale * 0.125f);
    }

    public CCAxisRods(final boolean theIsRightHanded, final float theLength, final float theWidth) {
        _myLength = theLength;
        _myWidth = theWidth;
        _myIsRightHanded = theIsRightHanded;

        buildAxis();
    }

    protected void buildAxis() {
        _myXAxis = new CCArrow( _myLength, _myWidth);
        _myXAxis.defaultColor(xAxisColor);
        _myXAxis.localTransform().rotation(new CCMatrix3x3().fromAngles(0, 0, -90 * CCMath.DEG_TO_RAD));
        _myXAxis.localTransform().translation(_myLength * .5f, 0, 0);
        attachChild(_myXAxis);

        _myYAxis = new CCArrow( _myLength, _myWidth);
        _myYAxis.defaultColor(yAxisColor);
        _myYAxis.localTransform().translation(0, _myLength * .5f, 0);
        attachChild(_myYAxis);

        _myZAxis = new CCArrow(_myLength, _myWidth);
        _myZAxis.defaultColor(zAxisColor);
        if (_myIsRightHanded) {
            _myZAxis.localTransform().rotation(new CCMatrix3x3().fromAngles(90 * CCMath.DEG_TO_RAD, 0, 0));
            _myZAxis.localTransform().translation(0, 0, _myLength * .5f);
        } else {
            _myZAxis.localTransform().rotation(new CCMatrix3x3().fromAngles(-90 * CCMath.DEG_TO_RAD, 0, 0));
            _myZAxis.localTransform().translation(0, 0, -_myLength * .5f);
        }
        attachChild(_myZAxis);
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

    public CCArrow xAxis() {
        return _myXAxis;
    }

    public CCArrow yAxis() {
        return _myYAxis;
    }

    public CCArrow zAxis() {
        return _myZAxis;
    }
}
