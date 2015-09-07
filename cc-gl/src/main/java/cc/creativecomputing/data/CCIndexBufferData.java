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

import java.nio.Buffer;
import java.nio.IntBuffer;

public abstract class CCIndexBufferData<T extends Buffer> extends CCAbstractBufferData<T> {

	/**
	 * @return the next value from this object, as an int, incrementing our
	 *         position by 1 entry. Buffer types smaller than an int should
	 *         return unsigned values.
	 */
	public abstract int get();

	/**
	 * @param theIndex
	 *            the absolute position to get our value from. This is in
	 *            entries, not bytes, and is 0 based. So for a ShortBuffer, 2
	 *            would be the 3rd short from the beginning, etc.
	 * @return the value from this object, as an int, at the given absolute
	 *         entry position. Buffer types smaller than an int should return
	 *         unsigned values.
	 */
	public abstract int get(int theIndex);

	/**
	 * @return a new, non-direct IntBuffer containing a snapshot of the contents
	 *         of this buffer.
	 */
	public abstract IntBuffer asIntBuffer();

	/**
	 * Sets the value of this buffer at the current position, incrementing our
	 * position by 1 entry.
	 * 
	 * @param theValue
	 *            the value to place into this object at the current position.
	 * @return this object, for chaining.
	 */
	public abstract CCIndexBufferData<T> add(int theValue);

	/**
	 * Sets the value of this buffer at the given index.
	 * 
	 * @param theIndex
	 *            the absolute position to put our value at. This is in entries,
	 *            not bytes, and is 0 based. So for a ShortBuffer, 2 would be
	 *            the 3rd short from the beginning, etc.
	 * @param theValue
	 *            the value to place into this object
	 * @return
	 */
	public abstract CCIndexBufferData<T> add(int theIndex, int theValue);

	/**
	 * Write the contents of the given IndexBufferData into this one. Note that
	 * data conversion is handled using the get/put methods in IndexBufferData.
	 * 
	 * @param theBuffer
	 *            the source buffer object.
	 */
	public abstract void add(CCIndexBufferData<?> theBuffer);

	/**
	 * Write the contents of the given int array into this IndexBufferData. Note
	 * that data conversion is handled using the get/put methods in
	 * IndexBufferData.
	 * 
	 * @param theValues
	 *            the source int array.
	 */
	public abstract void add(int[] theValues);

	/**
	 * Write the contents of the given int array into this IndexBufferData. Note
	 * that data conversion is handled using the get/put methods in
	 * IndexBufferData.
	 * 
	 * @param theValues
	 *            the source int array.
	 * @param theOffset
	 * @param theLength
	 */
	public abstract void add(int[] theValues, int theOffset, int theLength);

	/**
	 * Get the underlying nio buffer.
	 */
	@Override
	public abstract T buffer();

	/**
	 * @see Buffer#remaining();
	 */
	public int remaining() {
		return buffer().remaining();
	}

	/**
	 * @see Buffer#position();
	 */
	public int position() {
		return buffer().position();
	}

	/**
	 * @see Buffer#position(int);
	 */
	public void position(final int position) {
		buffer().position(position);
	}

	/**
	 * @see Buffer#limit();
	 */
	public int limit() {
		return buffer().limit();
	}

	/**
	 * @see Buffer#limit(int);
	 */
	public void limit(final int theLimit) {
		buffer().limit(theLimit);
	}

	/**
	 * @see Buffer#capacity();
	 */
	public int capacity() {
		return buffer().capacity();
	}

	/**
	 * @see Buffer#rewind();
	 */
	public void rewind() {
		buffer().rewind();
	}

	/**
	 * @see Buffer#flip();
	 */
	public void flip() {
		buffer().flip();
	}

	/**
	 * @see Buffer#clear();
	 */
	public void clear() {
		buffer().clear();
	}

	/**
	 * @see Buffer#reset();
	 */
	public void reset() {
		buffer().reset();
	}

	@Override
	public abstract CCIndexBufferData<T> clone();
}
