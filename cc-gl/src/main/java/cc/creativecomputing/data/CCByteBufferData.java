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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;


/**
 * Simple data class storing a buffer of bytes
 */
public class CCByteBufferData extends CCIndexBufferData<ByteBuffer> {

    /**
     * Instantiates a new ByteBufferData.
     */
    public CCByteBufferData() {}

    /**
     * Instantiates a new ByteBufferData with a buffer of the given size.
     */
    public CCByteBufferData(final int theSize) {
        this(CCBufferUtils.createByteBuffer(theSize));
    }

    /**
     * Creates a new ByteBufferData.
     * 
     * @param theBuffer
     *            Buffer holding the data. Must not be null.
     */
    public CCByteBufferData(final ByteBuffer theBuffer) {
        if (theBuffer == null) {
            throw new IllegalArgumentException("Buffer can not be null!");
        }

        _myBuffer = theBuffer;
    }

    public Class<? extends CCByteBufferData> getClassTag() {
        return getClass();
    }

    @Override
    public int get() {
        return _myBuffer.get() & 0xFF;
    }

    @Override
    public int get(final int theIndex) {
        return _myBuffer.get(theIndex) & 0xFF;
    }

    @Override
    public CCByteBufferData add(final int theValue) {
        if (theValue < 0 || theValue >= 256) {
            throw new IllegalArgumentException("Invalid value passed to byte buffer: " + theValue);
        }
        _myBuffer.put((byte) theValue);
        return this;
    }

    @Override
    public CCByteBufferData add(final int theIndex, final int theValue) {
        if (theValue < 0 || theValue >= 256) {
            throw new IllegalArgumentException("Invalid value passed to byte buffer: " + theValue);
        }
        _myBuffer.put(theIndex, (byte) theValue);
        return this;
    }

    @Override
    public void add(final CCIndexBufferData<?> theBuffer) {
        if (theBuffer instanceof CCByteBufferData) {
            _myBuffer.put((ByteBuffer) theBuffer.buffer());
        } else {
            while (theBuffer.buffer().hasRemaining()) {
                add(theBuffer.get());
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
        return 1;
    }

    @Override
    public ByteBuffer buffer() {
        return _myBuffer;
    }

    @Override
    public IntBuffer asIntBuffer() {
        final ByteBuffer source = buffer().duplicate();
        source.rewind();
        final IntBuffer buff = CCBufferUtils.createIntBufferOnHeap(source.limit());
        for (int i = 0, max = source.limit(); i < max; i++) {
            buff.put(source.get() & 0xFF);
        }
        buff.flip();
        return buff;
    }

    @Override
    public CCByteBufferData clone() {
        final CCByteBufferData copy = new CCByteBufferData();
        copy._myBuffer = CCBufferUtils.clone(_myBuffer);
        return copy;
    }
}