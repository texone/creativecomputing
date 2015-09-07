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
import java.nio.ShortBuffer;

/**
 * Simple data class storing a buffer of shorts
 */
public class CCShortBufferData extends CCIndexBufferData<ShortBuffer> {

    /**
     * Instantiates a new ShortBufferData.
     */
    public CCShortBufferData() {}

    /**
     * Instantiates a new ShortBufferData with a buffer of the given size.
     */
    public CCShortBufferData(final int theSize) {
        this(CCBufferUtils.createShortBuffer(theSize));
    }

    /**
     * Creates a new ShortBufferData.
     * 
     * @param theBuffer
     *            Buffer holding the data. Must not be null.
     */
    public CCShortBufferData(final ShortBuffer theBuffer) {
        if (theBuffer == null) {
            throw new IllegalArgumentException("Buffer can not be null!");
        }

        _myBuffer = theBuffer;
    }

    public Class<? extends CCShortBufferData> getClassTag() {
        return getClass();
    }

    @Override
    public int get() {
        return _myBuffer.get() & 0xFFFF;
    }

    @Override
    public int get(final int theIndex) {
        return _myBuffer.get(theIndex) & 0xFFFF;
    }

    @Override
    public CCShortBufferData add(final int theValue) {
        if (theValue < 0 || theValue >= 65536) {
            throw new IllegalArgumentException("Invalid value passed to short buffer: " + theValue);
        }
        _myBuffer.put((short) theValue);
        return this;
    }

    @Override
    public CCShortBufferData add(final int theIndex, final int theValue) {
        if (theValue < 0 || theValue >= 65536) {
            throw new IllegalArgumentException("Invalid value passed to short buffer: " + theValue);
        }
        _myBuffer.put(theIndex, (short) theValue);
        return this;
    }

    @Override
    public void add(final CCIndexBufferData<?> theValues) {
        if (theValues instanceof CCShortBufferData) {
            _myBuffer.put((ShortBuffer) theValues.buffer());
        } else {
            while (theValues.buffer().hasRemaining()) {
                add(theValues.get());
            }
        }
    }

    @Override
    public void add(final int[] theValues) {
        for (int i = 0; i < theValues.length; i++) {
            add(theValues[i]);
        }
    }

    @Override
    public void add(final int[] theValues, final int theOffset, final int theLength) {
        for (int i = theOffset, max = theOffset + theLength; i < max; i++) {
            add(theValues[i]);
        }
    }

    @Override
    public int byteCount() {
        return 2;
    }

    @Override
    public ShortBuffer buffer() {
        return _myBuffer;
    }

    @Override
    public IntBuffer asIntBuffer() {
        final ShortBuffer source = buffer().duplicate();
        source.rewind();
        final IntBuffer buff = CCBufferUtils.createIntBufferOnHeap(source.limit());
        for (int i = 0, max = source.limit(); i < max; i++) {
            buff.put(source.get() & 0xFFFF);
        }
        buff.flip();
        return buff;
    }

    @Override
    public CCShortBufferData clone() {
        final CCShortBufferData copy = new CCShortBufferData();
        copy._myBuffer = CCBufferUtils.clone(_myBuffer);
        return copy;
    }
}
