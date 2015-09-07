/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.data;

import java.nio.IntBuffer;


/**
 * Simple data class storing a buffer of ints
 */
public class CCIntBufferData extends CCIndexBufferData<IntBuffer> {

    /**
     * Instantiates a new IntBufferData.
     */
    public CCIntBufferData() {}

    /**
     * Instantiates a new IntBufferData with a buffer of the given size.
     */
    public CCIntBufferData(final int theSize) {
        this(CCBufferUtils.createIntBuffer(theSize));
    }

    /**
     * Creates a new IntBufferData.
     * 
     * @param theBuffer
     *            Buffer holding the data. Must not be null.
     */
    public CCIntBufferData(final IntBuffer theBuffer) {
        if (theBuffer == null) {
            throw new IllegalArgumentException("Buffer can not be null!");
        }

        _myBuffer = theBuffer;
    }

    public Class<? extends CCIntBufferData> getClassTag() {
        return getClass();
    }

    @Override
    public int get() {
        return _myBuffer.get();
    }

    @Override
    public int get(final int theIndex) {
        return _myBuffer.get(theIndex);
    }

    @Override
    public CCIntBufferData add(final int theValue) {
        if (theValue < 0) {
            throw new IllegalArgumentException("Invalid value passed to int buffer: " + theValue);
        }
        _myBuffer.put(theValue);
        return this;
    }

    @Override
    public CCIntBufferData add(final int theIndex, final int theValue) {
        if (theValue < 0) {
            throw new IllegalArgumentException("Invalid value passed to int buffer: " + theValue);
        }
        _myBuffer.put(theIndex, theValue);
        return this;
    }

    @Override
    public void add(final CCIndexBufferData<?> theData) {
        if (theData instanceof CCIntBufferData) {
            _myBuffer.put((IntBuffer) theData.buffer());
        } else {
            while (theData.buffer().hasRemaining()) {
                add(theData.get());
            }
        }
    }

    @Override
    public void add(final int[] theValues) {
        _myBuffer.put(theValues);
    }

    @Override
    public void add(final int[] theValues, final int theOffset, final int theLength) {
        _myBuffer.put(theValues, theOffset, theLength);
    }

    @Override
    public int byteCount() {
        return 4;
    }

    @Override
    public IntBuffer buffer() {
        return _myBuffer;
    }

    @Override
    public IntBuffer asIntBuffer() {
        final IntBuffer source = buffer().duplicate();
        source.rewind();
        final IntBuffer buff = CCBufferUtils.createIntBufferOnHeap(source.limit());
        buff.put(source);
        buff.flip();
        return buff;
    }

    @Override
    public CCIntBufferData clone() {
        final CCIntBufferData copy = new CCIntBufferData();
        copy._myBuffer = CCBufferUtils.clone(_myBuffer);
        return copy;
    }
}
