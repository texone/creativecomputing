/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryUtil;

/**
 * This class contains several utility function for java nio buffer objects.
 * 
 * @author christianriekoff
 * 
 */
public class CCBufferUtil {
	public static final int SIZE_OF_FLOAT = 4;
	public static final int SIZE_OF_DOUBLE = 8;

	public static final int SIZE_OF_BYTE = 1;
	public static final int SIZE_OF_SHORT = 2;
	public static final int SIZE_OF_INT = 4;
	public static final int SIZE_OF_LONG = 8;

	public static DoubleBuffer newDirectDoubleBuffer(final int theSize) {
		return MemoryUtil.memAllocDouble(theSize);
	}

	public static DoubleBuffer newDirectDoubleBuffer(final double[] theData) {
		DoubleBuffer myResult = newDirectDoubleBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given FloatBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(DoubleBuffer theBuffer, double theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}

	public static FloatBuffer newDirectFloatBuffer(final int theSize) {
		return MemoryUtil.memAllocFloat(theSize);
	}

	public static FloatBuffer newDirectFloatBuffer(final float[] theData) {
		FloatBuffer myResult = newDirectFloatBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given FloatBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(FloatBuffer theBuffer, float theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}

	public static ByteBuffer newDirectByteBuffer(final int theSize) {
		return MemoryUtil.memAlloc(theSize);
	}

	public static ByteBuffer newDirectByteBuffer(final byte[] theData) {
		ByteBuffer myResult = newDirectByteBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given ByteBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(ByteBuffer theBuffer, byte theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}

	/**
	 * Creates a ShortBuffer of the given size
	 * 
	 * @param theSize
	 *            size for the buffer
	 * @return ShortBuffer of the given size
	 */
	public static ShortBuffer newDirectShortBuffer(final int theSize) {
		return MemoryUtil.memAllocShort(theSize);
	}

	public static ShortBuffer newDirectShortBuffer(final short[] theData) {
		ShortBuffer myResult = newDirectShortBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given ShortBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(ShortBuffer theBuffer, short theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}

	/**
	 * @param theI
	 * @return
	 */
	public static IntBuffer newDirectIntBuffer(final int theSize) {
		return MemoryUtil.memAllocInt(theSize);
	}

	public static IntBuffer newDirectIntBuffer(final int[] theData) {
		IntBuffer myResult = newDirectIntBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given IntBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(IntBuffer theBuffer, int theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}
	
	/**
	 * @param theI
	 * @return
	 */
	public static LongBuffer newDirectLongBuffer(final int theSize) {
		return MemoryUtil.memAllocLong(theSize);
	}

	public static LongBuffer newDirectLongBuffer(final long[] theData) {
		LongBuffer myResult = newDirectLongBuffer(theData.length);
		myResult.put(theData, 0, theData.length);
		return myResult;
	}

	/**
	 * Fills the given IntBuffer with the given default value
	 * 
	 * @param theBuffer
	 *            buffer to be filled
	 * @param theValue
	 *            value for the buffer
	 */
	public static void fill(LongBuffer theBuffer, long theValue) {
		for (int i = 0; i < theBuffer.limit(); i++) {
			theBuffer.put(i, theValue);
		}
	}

	private static class BytesRead {
		BytesRead(int payloadLen, byte[] data) {
			this.payloadLen = payloadLen;
			this.data = data;
		}

		int payloadLen;
		byte[] data;
	}

	private static BytesRead readAllImpl(InputStream stream) throws IOException {
		if (!(stream instanceof BufferedInputStream)) {
			stream = new BufferedInputStream(stream);
		}
		int avail = stream.available();
		byte[] data = new byte[avail];
		int numRead = 0;
		int pos = 0;
		do {
			if (pos + avail > data.length) {
				byte[] newData = new byte[pos + avail];
				System.arraycopy(data, 0, newData, 0, pos);
				data = newData;
			}
			numRead = stream.read(data, pos, avail);
			if (numRead >= 0) {
				pos += numRead;
			}
			avail = stream.available();
		} while (avail > 0 && numRead >= 0);

		return new BytesRead(pos, data);
	}

	public static ByteBuffer readAll2Buffer(InputStream stream) throws IOException {
		BytesRead bytesRead = readAllImpl(stream);
		return ByteBuffer.wrap(bytesRead.data, 0, bytesRead.payloadLen);
	}

	// ----------------------------------------------------------------------
	// Conversion routines
	//
	public final static float[] getFloatArray(double[] source) {
		int i = source.length;
		float[] dest = new float[i--];
		while (i >= 0) {
			dest[i] = (float) source[i];
			i--;
		}
		return dest;
	}
}
