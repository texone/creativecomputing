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

public abstract class CCAbstractBufferData<T extends Buffer> {

	/** Buffer holding the data. */
	protected T _myBuffer;

	/** Flag for notifying the renderer that the VBO buffer needs to be updated. */
	protected boolean _myNeedsRefresh = false;

	CCAbstractBufferData() {
	}

	/**
	 * @return the number of bytes per entry in the buffer. For example, an
	 *         IntBuffer would return 4.
	 */
	public abstract int byteCount();

	public void position(int thePosition) {
		_myBuffer.position(thePosition);
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int bufferLimit() {
		if (_myBuffer != null) {
			return _myBuffer.limit();
		}

		return 0;
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public int bufferCapacity() {
		if (_myBuffer != null) {
			return _myBuffer.capacity();
		}

		return 0;
	}

	/**
	 * Get the buffer holding the data.
	 * 
	 * @return the buffer
	 */
	public T buffer() {
		return _myBuffer;
	}

	/**
	 * Set the buffer holding the data.
	 * 
	 * @param buffer
	 *            the buffer to set
	 */
	public void buffer(final T buffer) {
		_myBuffer = buffer;
	}

	public boolean needsRefresh() {
		return _myNeedsRefresh;
	}

	public void needsRefresh(final boolean needsRefresh) {
		_myNeedsRefresh = needsRefresh;
	}

	/**
	 * @return a deep copy of this buffer data object
	 */
	public abstract CCAbstractBufferData<T> clone();
}
