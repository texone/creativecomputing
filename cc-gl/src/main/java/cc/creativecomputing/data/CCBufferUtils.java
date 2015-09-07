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
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

/**
 * <code>BufferUtils</code> is a helper class for generating nio buffers from data classes such as Vectors and
 * CCColor.
 */
public final class CCBufferUtils {

    // // -- COLORRGBA METHODS -- ////

    /**
     * Generate a new FloatBuffer using the given array of CCColor objects. The FloatBuffer will be 4 * data.length
     * long and contain the color data as data[0].r, data[0].g, data[0].b, data[0].a, data[1].r... etc.
     * 
     * @param data
     *            array of CCColor objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final CCColor... theData) {
        if (theData == null) {
            return null;
        }
        return createFloatBuffer(0, theData.length, theData);
    }

    /**
     * Generate a new FloatBuffer using the given array of CCColor objects. The FloatBuffer will be 4 * data.length
     * long and contain the color data as data[0].r, data[0].g, data[0].b, data[0].a, data[1].r... etc.
     * 
     * @param theOffset
     *            the starting index to read from in our data array
     * @param length
     *            the number of colors to read
     * @param theData
     *            array of CCColor objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final int theOffset, final int theLength, final CCColor... theData) {
        if (theData == null) {
            return null;
        }
        final FloatBuffer buff = createFloatBuffer(4 * theLength);
        for (int x = theOffset; x < theLength; x++) {
            if (theData[x] != null) {
                buff.put((float)theData[x].r).put((float)theData[x].g).put((float)theData[x].b).put((float)theData[x].a);
            } else {
                buff.put(0).put(0).put(0).put(0);
            }
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCColor object data.
     * 
     * @param theColors
     *            number of colors that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createColorBuffer(final int theColors) {
        final FloatBuffer colorBuff = createFloatBuffer(4 * theColors);
        return colorBuff;
    }

    /**
     * Updates the values of the given color from the specified buffer at the index provided.
     * 
     * @param store
     *            the color to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of colors, not floats) to read from the buf
     */
    public static void populateFromBuffer(final CCColor store, final FloatBuffer buf, final int index) {
        store.r = buf.get(index * 4);
        store.g = buf.get(index * 4 + 1);
        store.b = buf.get(index * 4 + 2);
        store.a = buf.get(index * 4 + 3);
    }

    /**
     * Generates a CCColor array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of CCColor objects
     */
    public static CCColor[] getColorArray(final FloatBuffer buff) {
        buff.rewind();
        final CCColor[] colors = new CCColor[buff.limit() >> 2];
        for (int x = 0; x < colors.length; x++) {
            final CCColor c = new CCColor(buff.get(), buff.get(), buff.get(), buff.get());
            colors[x] = c;
        }
        return colors;
    }

    /**
     * Generates a CCColor array from the given FloatBufferData.
     * 
     * @param buff
     *            the FloatBufferData to read from
     * @param defaults
     *            a default value to set each color to, used when the tuple size of the given {@link CCFloatBufferData} is
     *            smaller than 4.
     * @return a newly generated array of CCColor objects
     */
    public static CCColor[] getColorArray(final CCFloatBufferData data, final CCColor defaults) {
        final FloatBuffer buff = data.buffer();
        buff.clear();
        final CCColor[] colors = new CCColor[data.tupleCount()];
        final int tupleSize = data.valuesPerTuple();
        for (int x = 0; x < colors.length; x++) {
            final CCColor c = new CCColor(defaults);
            c.r = buff.get();
            if (tupleSize > 1) {
                c.g = buff.get();
            }
            if (tupleSize > 2) {
                c.b = buff.get();
            }
            if (tupleSize > 3) {
                c.a = buff.get();
            }
            if (tupleSize > 4) {
                buff.position(buff.position() + tupleSize - 4);
            }
            colors[x] = c;
        }
        return colors;
    }

    /**
     * Copies a CCColor from one position in the buffer to another. The index values are in terms of color number (eg,
     * color number 0 is positions 0-3 in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the color to copy
     * @param toPos
     *            the index to copy the color to
     */
    public static void copyInternalColor(final FloatBuffer buf, final int fromPos, final int toPos) {
        copyInternal(buf, fromPos * 4, toPos * 4, 4);
    }

    /**
     * Checks to see if the given CCColor is equals to the data stored in the buffer at the given data index.
     * 
     * @param check
     *            the color to check against - null will return false.
     * @param buf
     *            the buffer to compare data with
     * @param index
     *            the position (in terms of colors, not floats) of the color in the buffer to check against
     * @return
     */
    public static boolean equals(final CCColor check, final FloatBuffer buf, final int index) {
        final CCColor temp = new CCColor();
        populateFromBuffer(temp, buf, index);
        return temp.equals(check);
    }

    // // -- CCVector4 METHODS -- ////

    /**
     * Generate a new FloatBuffer using the given array of CCVector4 objects. The FloatBuffer will be 4 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[0].z, data[0].w, data[1].x... etc.
     * 
     * @param data
     *            array of CCVector4 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final CCVector4... data) {
        if (data == null) {
            return null;
        }
        return createFloatBuffer(0, data.length, data);
    }

    /**
     * Generate a new FloatBuffer using the given array of CCVector4 objects. The FloatBuffer will be 4 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[0].z, data[0].w, data[1].x... etc.
     * 
     * @param data
     *            array of CCVector4 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final int theOffset, final int theLength, final CCVector4... data) {
        if (data == null) {
            return null;
        }
        final FloatBuffer buff = createFloatBuffer(4 * theLength);
        for (int x = theOffset; x < theLength; x++) {
            if (data[x] != null) {
                buff.put((float)data[x].x).put((float)data[x].y).put((float)data[x].z).put((float)data[x].w);
            } else {
                buff.put(0).put(0).put(0);
            }
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector4 object data.
     * 
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector4Buffer(final int vertices) {
        final FloatBuffer vBuff = createFloatBuffer(4 * vertices);
        return vBuff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector4 object data only if the
     * given buffer if not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector4Buffer(final FloatBuffer buf, final int vertices) {
        if (buf != null && buf.limit() == 4 * vertices) {
            buf.rewind();
            return buf;
        }

        return createFloatBuffer(4 * vertices);
    }

    /**
     * Sets the data contained in the given CCVector4 into the FloatBuffer at the specified index.
     * 
     * @param vector
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the position to place the data; in terms of vectors not floats
     */
    public static void setInBuffer(final CCVector4 vector, final FloatBuffer buf, final int index) {
        if (buf == null) {
            return;
        }
        if (vector == null) {
            buf.put(index * 4, 0);
            buf.put((index * 4) + 1, 0);
            buf.put((index * 4) + 2, 0);
            buf.put((index * 4) + 3, 0);
        } else {
            buf.put(index * 4, (float)vector.x);
            buf.put((index * 4) + 1, (float)vector.y);
            buf.put((index * 4) + 2, (float)vector.z);
            buf.put((index * 4) + 3, (float)vector.w);
        }
    }

    /**
     * Updates the values of the given vector from the specified buffer at the index provided.
     * 
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from the buffer
     */
    public static void populateFromBuffer(final CCVector4 vector, final FloatBuffer buf, final int index) {
        vector.x = buf.get(index * 4);
        vector.y = buf.get(index * 4 + 1);
        vector.z = buf.get(index * 4 + 2);
        vector.w = buf.get(index * 4 + 3);
    }

    /**
     * Generates a CCVector4 array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of CCVector3 objects
     */
    public static CCVector4[] getVector4Array(final FloatBuffer buff) {
        buff.clear();
        final CCVector4[] verts = new CCVector4[buff.limit() / 4];
        for (int x = 0; x < verts.length; x++) {
            final CCVector4 v = new CCVector4(buff.get(), buff.get(), buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Generates a CCVector4 array from the given FloatBufferData.
     * 
     * @param buff
     *            the FloatBufferData to read from
     * @param defaults
     *            a default value to set each color to, used when the tuple size of the given {@link CCFloatBufferData} is
     *            smaller than 4.
     * @return a newly generated array of CCVector4 objects
     */
    public static CCVector4[] getVector4Array(final CCFloatBufferData data, final CCVector4 defaults) {
        final FloatBuffer buff = data.buffer();
        buff.clear();
        final CCVector4[] verts = new CCVector4[data.tupleCount()];
        final int tupleSize = data.valuesPerTuple();
        for (int x = 0; x < verts.length; x++) {
            final CCVector4 v = new CCVector4(defaults);
            v.x = buff.get();
            if (tupleSize > 1) {
                v.y = buff.get();
            }
            if (tupleSize > 2) {
                v.z = buff.get();
            }
            if (tupleSize > 3) {
                v.w = buff.get();
            }
            if (tupleSize > 4) {
                buff.position(buff.position() + tupleSize - 4);
            }
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Copies a CCVector3 from one position in the buffer to another. The index values are in terms of vector number (eg,
     * vector number 0 is positions 0-2 in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the vector to copy
     * @param toPos
     *            the index to copy the vector to
     */
    public static void copyInternalVector4(final FloatBuffer buf, final int fromPos, final int toPos) {
        copyInternal(buf, fromPos * 4, toPos * 4, 4);
    }

    /**
     * Normalize a CCVector4 in-buffer.
     * 
     * @param buf
     *            the buffer to find the CCVector4 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to normalize
     */
    public static void normalizeVector4(final FloatBuffer buf, final int index) {
        final CCVector4 temp = new CCVector4();
        populateFromBuffer(temp, buf, index);
        temp.normalizeLocal();
        setInBuffer(temp, buf, index);
    }

    /**
     * Add to a CCVector4 in-buffer.
     * 
     * @param toAdd
     *            the vector to add from
     * @param buf
     *            the buffer to find the CCVector4 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to add to
     */
    public static void addInBuffer(final CCVector4 toAdd, final FloatBuffer buf, final int index) {
        final CCVector4 temp = new CCVector4();
        populateFromBuffer(temp, buf, index);
        temp.addLocal(toAdd);
        setInBuffer(temp, buf, index);
    }

    /**
     * Multiply and store a CCVector3 in-buffer.
     * 
     * @param toMult
     *            the vector to multiply against
     * @param buf
     *            the buffer to find the CCVector3 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to multiply
     */
    public static void multInBuffer(final CCVector4 toMult, final FloatBuffer buf, final int index) {
        final CCVector4 temp = new CCVector4();
        populateFromBuffer(temp, buf, index);
        temp.multiplyLocal(toMult);
        setInBuffer(temp, buf, index);
    }

    /**
     * Checks to see if the given CCVector3 is equals to the data stored in the buffer at the given data index.
     * 
     * @param check
     *            the vector to check against - null will return false.
     * @param buf
     *            the buffer to compare data with
     * @param index
     *            the position (in terms of vectors, not floats) of the vector in the buffer to check against
     * @return
     */
    public static boolean equals(final CCVector4 check, final FloatBuffer buf, final int index) {
        final CCVector4 temp = new CCVector4();
        populateFromBuffer(temp, buf, index);
        final boolean equals = temp.equals(check);
        return equals;
    }

    // // -- CCVector3 METHODS -- ////

    /**
     * Generate a new FloatBuffer using the given array of CCVector3 objects. The FloatBuffer will be 3 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[0].z, data[1].x... etc.
     * 
     * @param data
     *            array of CCVector3 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final CCVector3... data) {
        if (data == null) {
            return null;
        }
        return createFloatBuffer(0, data.length, data);
    }

    /**
     * Generate a new FloatBuffer using the given array of CCVector3 objects. The FloatBuffer will be 3 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[0].z, data[1].x... etc.
     * 
     * @param theOffset
     *            the starting index to read from in our data array
     * @param theLength
     *            the number of vectors to read
     * @param data
     *            array of CCVector3 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final int theOffset, final int theLength, final CCVector3... data) {
        if (data == null) {
            return null;
        }
        final FloatBuffer buff = createFloatBuffer(3 * theLength);
        for (int x = theOffset; x < theLength; x++) {
            if (data[x] != null) {
                buff.put((float)data[x].x).put((float)data[x].y).put((float)data[x].z);
            } else {
                buff.put(0).put(0).put(0);
            }
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector3 object data.
     * 
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector3Buffer(final int vertices) {
        final FloatBuffer vBuff = createFloatBuffer(3 * vertices);
        return vBuff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector3 object data only if the
     * given buffer is not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector3Buffer(final FloatBuffer buf, final int vertices) {
        if (buf != null && buf.limit() == 3 * vertices) {
            buf.rewind();
            return buf;
        }

        return createFloatBuffer(3 * vertices);
    }

    /**
     * Sets the data contained in the given CCVector3 into the FloatBuffer at the specified index.
     * 
     * @param vector
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the position to place the data; in terms of vectors not floats
     */
    public static void setInBuffer(final CCVector3 vector, final FloatBuffer buf, final int index) {
        if (buf == null) {
            return;
        }
        if (vector == null) {
            buf.put(index * 3, 0);
            buf.put((index * 3) + 1, 0);
            buf.put((index * 3) + 2, 0);
        } else {
            buf.put(index * 3, (float)vector.x);
            buf.put((index * 3) + 1, (float)vector.y);
            buf.put((index * 3) + 2, (float)vector.z);
        }
    }

    /**
     * Updates the values of the given vector from the specified buffer at the index provided.
     * 
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from the buf
     */
    public static void populateFromBuffer(final CCVector3 vector, final FloatBuffer buf, final int index) {
        vector.x = buf.get(index * 3);
        vector.y = buf.get(index * 3 + 1);
        vector.z = buf.get(index * 3 + 2);
    }

    /**
     * Generates a CCVector3 array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of CCVector3 objects
     */
    public static CCVector3[] getVector3Array(final FloatBuffer buff) {
        buff.clear();
        final CCVector3[] verts = new CCVector3[buff.limit() / 3];
        for (int x = 0; x < verts.length; x++) {
            final CCVector3 v = new CCVector3(buff.get(), buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Generates a CCVector3 array from the given FloatBufferData.
     * 
     * @param buff
     *            the FloatBufferData to read from
     * @param defaults
     *            a default value to set each color to, used when the tuple size of the given {@link CCFloatBufferData} is
     *            smaller than 3.
     * @return a newly generated array of CCVector3 objects
     */
    public static CCVector3[] getVector3Array(final CCFloatBufferData data, final CCVector3 defaults) {
        final FloatBuffer buff = data.buffer();
        buff.rewind();
        final CCVector3[] verts = new CCVector3[data.tupleCount()];
        final int tupleSize = data.valuesPerTuple();
        for (int x = 0; x < verts.length; x++) {
            final CCVector3 v = new CCVector3(defaults);
            v.x = buff.get();
            if (tupleSize > 1) {
                v.y = buff.get();
            }
            if (tupleSize > 2) {
                v.z = buff.get();
            }
            if (tupleSize > 3) {
                buff.position(buff.position() + tupleSize - 3);
            }
            verts[x] = v;
        }
        buff.rewind();
        return verts;
    }

    /**
     * Copies a CCVector3 from one position in the buffer to another. The index values are in terms of vector number (eg,
     * vector number 0 is positions 0-2 in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the vector to copy
     * @param toPos
     *            the index to copy the vector to
     */
    public static void copyInternalVector3(final FloatBuffer buf, final int fromPos, final int toPos) {
        copyInternal(buf, fromPos * 3, toPos * 3, 3);
    }

    /**
     * Normalize a CCVector3 in-buffer.
     * 
     * @param buf
     *            the buffer to find the CCVector3 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to normalize
     */
    public static void normalizeVector3(final FloatBuffer buf, final int index) {
        final CCVector3 temp = new CCVector3();
        populateFromBuffer(temp, buf, index);
        temp.normalizeLocal();
        setInBuffer(temp, buf, index);
    }

    /**
     * Add to a CCVector3 in-buffer.
     * 
     * @param toAdd
     *            the vector to add from
     * @param buf
     *            the buffer to find the CCVector3 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to add to
     */
    public static void addInBuffer(final CCVector3 toAdd, final FloatBuffer buf, final int index) {
        final CCVector3 temp = new CCVector3();
        populateFromBuffer(temp, buf, index);
        temp.addLocal(toAdd);
        setInBuffer(temp, buf, index);
    }

    /**
     * Multiply and store a CCVector3 in-buffer.
     * 
     * @param toMult
     *            the vector to multiply against
     * @param buf
     *            the buffer to find the CCVector3 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to multiply
     */
    public static void multInBuffer(final CCVector3 toMult, final FloatBuffer buf, final int index) {
        final CCVector3 temp = new CCVector3();
        populateFromBuffer(temp, buf, index);
        temp.multiplyLocal(toMult);
        setInBuffer(temp, buf, index);
    }

    /**
     * Checks to see if the given CCVector3 is equals to the data stored in the buffer at the given data index.
     * 
     * @param check
     *            the vector to check against - null will return false.
     * @param buf
     *            the buffer to compare data with
     * @param index
     *            the position (in terms of vectors, not floats) of the vector in the buffer to check against
     * @return
     */
    public static boolean equals(final CCVector3 check, final FloatBuffer buf, final int index) {
        final CCVector3 temp = new CCVector3();
        populateFromBuffer(temp, buf, index);
        final boolean equals = temp.equals(check);
        return equals;
    }

    // // -- CCVector2 METHODS -- ////

    /**
     * Generate a new FloatBuffer using the given array of CCVector2 objects. The FloatBuffer will be 2 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[1].x... etc.
     * 
     * @param data
     *            array of CCVector2 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final CCVector2... data) {
        if (data == null) {
            return null;
        }
        return createFloatBuffer(0, data.length, data);
    }

    /**
     * Generate a new FloatBuffer using the given array of CCVector2 objects. The FloatBuffer will be 2 * data.length long
     * and contain the vector data as data[0].x, data[0].y, data[1].x... etc.
     * 
     * @param theOffset
     *            the starting index to read from in our data array
     * @param theLength
     *            the number of vectors to read
     * @param data
     *            array of CCVector2 objects to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final int theOffset, final int theLength, final CCVector2... data) {
        if (data == null) {
            return null;
        }
        final FloatBuffer buff = createFloatBuffer(2 * theLength);
        for (int x = theOffset; x < theLength; x++) {
            if (data[x] != null) {
                buff.put((float)data[x].x).put((float)data[x].y);
            } else {
                buff.put(0).put(0);
            }
        }
        buff.flip();
        return buff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector2 object data.
     * 
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector2Buffer(final int vertices) {
        final FloatBuffer vBuff = createFloatBuffer(2 * vertices);
        return vBuff;
    }

    /**
     * Create a new FloatBuffer of an appropriate size to hold the specified number of CCVector2 object data only if the
     * given buffer if not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param vertices
     *            number of vertices that need to be held by the newly created buffer
     * @return the requested new FloatBuffer
     */
    public static FloatBuffer createVector2Buffer(final FloatBuffer buf, final int vertices) {
        if (buf != null && buf.limit() == 2 * vertices) {
            buf.rewind();
            return buf;
        }

        return createFloatBuffer(2 * vertices);
    }

    /**
     * Sets the data contained in the given CCVector2 into the FloatBuffer at the specified index.
     * 
     * @param vector
     *            the data to insert
     * @param buf
     *            the buffer to insert into
     * @param index
     *            the position to place the data; in terms of vectors not floats
     */
    public static void setInBuffer(final CCVector2 vector, final FloatBuffer buf, final int index) {
        buf.put(index * 2, (float)vector.x);
        buf.put((index * 2) + 1, (float)vector.y);
    }

    /**
     * Updates the values of the given vector from the specified buffer at the index provided.
     * 
     * @param vector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param index
     *            the position (in terms of vectors, not floats) to read from the buf
     */
    public static void populateFromBuffer(final CCVector2 vector, final FloatBuffer buf, final int index) {
        vector.x = buf.get(index * 2);
        vector.y = buf.get(index * 2 + 1);
    }

    /**
     * Generates a CCVector2 array from the given FloatBuffer.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a newly generated array of CCVector2 objects
     */
    public static CCVector2[] getVector2Array(final FloatBuffer buff) {
        buff.clear();
        final CCVector2[] verts = new CCVector2[buff.limit() / 2];
        for (int x = 0; x < verts.length; x++) {
            final CCVector2 v = new CCVector2(buff.get(), buff.get());
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Generates a CCVector2 array from the given FloatBufferData.
     * 
     * @param buff
     *            the FloatBufferData to read from
     * @param defaults
     *            a default value to set each color to, used when the tuple size of the given {@link CCFloatBufferData} is
     *            smaller than 2.
     * @return a newly generated array of CCVector2 objects
     */
    public static CCVector2[] getVector2Array(final CCFloatBufferData data, final CCVector2 defaults) {
        final FloatBuffer buff = data.buffer();
        buff.clear();
        final CCVector2[] verts = new CCVector2[data.tupleCount()];
        final int tupleSize = data.valuesPerTuple();
        for (int x = 0; x < verts.length; x++) {
            final CCVector2 v = new CCVector2(defaults);
            v.x = buff.get();
            if (tupleSize > 1) {
                v.y = buff.get();
            }
            if (tupleSize > 2) {
                buff.position(buff.position() + tupleSize - 2);
            }
            verts[x] = v;
        }
        return verts;
    }

    /**
     * Copies a CCVector2 from one position in the buffer to another. The index values are in terms of vector number (eg,
     * vector number 0 is positions 0-1 in the FloatBuffer.)
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the index of the vector to copy
     * @param toPos
     *            the index to copy the vector to
     */
    public static void copyInternalVector2(final FloatBuffer buf, final int fromPos, final int toPos) {
        copyInternal(buf, fromPos * 2, toPos * 2, 2);
    }

    /**
     * Normalize a CCVector2 in-buffer.
     * 
     * @param buf
     *            the buffer to find the CCVector2 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to normalize
     */
    public static void normalizeVector2(final FloatBuffer buf, final int index) {
        final CCVector2 temp = new CCVector2();
        populateFromBuffer(temp, buf, index);
        temp.normalizeLocal();
        setInBuffer(temp, buf, index);
    }

    /**
     * Add to a CCVector2 in-buffer.
     * 
     * @param toAdd
     *            the vector to add from
     * @param buf
     *            the buffer to find the CCVector2 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to add to
     */
    public static void addInBuffer(final CCVector2 toAdd, final FloatBuffer buf, final int index) {
        final CCVector2 temp = new CCVector2();
        populateFromBuffer(temp, buf, index);
        temp.addLocal(toAdd);
        setInBuffer(temp, buf, index);
    }

    /**
     * Multiply and store a CCVector2 in-buffer.
     * 
     * @param toMult
     *            the vector to multiply against
     * @param buf
     *            the buffer to find the CCVector2 within
     * @param index
     *            the position (in terms of vectors, not floats) of the vector to multiply
     */
    public static void multInBuffer(final CCVector2 toMult, final FloatBuffer buf, final int index) {
        final CCVector2 temp = new CCVector2();
        populateFromBuffer(temp, buf, index);
        temp.multiplyLocal(toMult);
        setInBuffer(temp, buf, index);
    }

    /**
     * Checks to see if the given CCVector2 is equals to the data stored in the buffer at the given data index.
     * 
     * @param check
     *            the vector to check against - null will return false.
     * @param buf
     *            the buffer to compare data with
     * @param index
     *            the position (in terms of vectors, not floats) of the vector in the buffer to check against
     * @return
     */
    public static boolean equals(final CCVector2 check, final FloatBuffer buf, final int index) {
        final CCVector2 temp = new CCVector2();
        populateFromBuffer(temp, buf, index);
        final boolean equals = temp.equals(check);
        return equals;
    }

    // // -- INT METHODS -- ////

    /**
     * Generate a new IntBuffer using the given array of ints. The IntBuffer will be data.length long and contain the
     * int data as data[0], data[1]... etc.
     * 
     * @param data
     *            array of ints to place into a new IntBuffer
     */
    public static IntBuffer createIntBuffer(final int... data) {
        if (data == null) {
            return null;
        }
        final IntBuffer buff = createIntBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }

    /**
     * Create a new int[] array and populate it with the given IntBuffer's contents.
     * 
     * @param buff
     *            the IntBuffer to read from
     * @return a new int array populated from the IntBuffer
     */
    public static int[] getIntArray(final IntBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.rewind();
        final int[] inds = new int[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    /**
     * Create a new int[] array and populate it with the given IndexBufferData's contents.
     * 
     * @param buff
     *            the IndexBufferData to read from
     * @return a new int array populated from the IndexBufferData
     */
    public static int[] getIntArray(final CCIndexBufferData<?> buff) {
        if (buff == null || buff.bufferLimit() == 0) {
            return null;
        }
        buff.buffer().rewind();
        final int[] inds = new int[buff.bufferLimit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    /**
     * Create a new float[] array and populate it with the given FloatBuffer's contents.
     * 
     * @param buff
     *            the FloatBuffer to read from
     * @return a new float array populated from the FloatBuffer
     */
    public static float[] getFloatArray(final FloatBuffer buff) {
        if (buff == null) {
            return null;
        }
        buff.clear();
        final float[] inds = new float[buff.limit()];
        for (int x = 0; x < inds.length; x++) {
            inds[x] = buff.get();
        }
        return inds;
    }

    // // -- GENERAL DOUBLE ROUTINES -- ////

    /**
     * Create a new DoubleBuffer of the specified size.
     * 
     * @param size
     *            required number of double to store.
     * @return the new DoubleBuffer
     */
    public static DoubleBuffer createDoubleBufferOnHeap(final int size) {
        final DoubleBuffer buf = ByteBuffer.allocate(8 * size).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new DoubleBuffer of the specified size.
     * 
     * @param size
     *            required number of double to store.
     * @return the new DoubleBuffer
     */
    public static DoubleBuffer createDoubleBuffer(final int size) {
        final DoubleBuffer buf = ByteBuffer.allocateDirect(8 * size).order(ByteOrder.nativeOrder()).asDoubleBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new DoubleBuffer of an appropriate size to hold the specified number of doubles only if the given buffer
     * if not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of doubles that need to be held by the newly created buffer
     * @return the requested new DoubleBuffer
     */
    public static DoubleBuffer createDoubleBuffer(DoubleBuffer buf, final int size) {
        if (buf != null && buf.limit() == size) {
            buf.rewind();
            return buf;
        }

        buf = createDoubleBuffer(size);
        return buf;
    }

    /**
     * Creates a new DoubleBuffer with the same contents as the given DoubleBuffer. The new DoubleBuffer is seperate
     * from the old one and changes are not reflected across. If you want to reflect changes, consider using
     * Buffer.duplicate().
     * 
     * @param buf
     *            the DoubleBuffer to copy
     * @return the copy
     */
    public static DoubleBuffer clone(final DoubleBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final DoubleBuffer copy;
        if (buf.isDirect()) {
            copy = createDoubleBuffer(buf.limit());
        } else {
            copy = createDoubleBufferOnHeap(buf.limit());
        }
        copy.put(buf);

        return copy;
    }

    // // -- GENERAL FLOAT ROUTINES -- ////

    /**
     * Create a new FloatBuffer of the specified size.
     * 
     * @param size
     *            required number of floats to store.
     * @return the new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final int size) {
        final FloatBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new FloatBuffer of the specified size.
     * 
     * @param size
     *            required number of floats to store.
     * @return the new FloatBuffer
     */
    public static FloatBuffer createFloatBufferOnHeap(final int size) {
        final FloatBuffer buf = ByteBuffer.allocate(4 * size).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Generate a new FloatBuffer using the given array of float primitives.
     * 
     * @param data
     *            array of float primitives to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final float... data) {
        return createFloatBuffer(null, data);
    }

    /**
     * Generate a new FloatBuffer using the given array of float primitives.
     * 
     * @param data
     *            array of float primitives to place into a new FloatBuffer
     */
    public static FloatBuffer createFloatBuffer(final FloatBuffer reuseStore, final float... data) {
        if (data == null) {
            return null;
        }
        final FloatBuffer buff;
        if (reuseStore == null || reuseStore.capacity() != data.length) {
            buff = createFloatBuffer(data.length);
        } else {
            buff = reuseStore;
            buff.clear();
        }
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }

    public static IntBuffer createIntBuffer(final IntBuffer reuseStore, final int... data) {
        if (data == null) {
            return null;
        }
        final IntBuffer buff;
        if (reuseStore == null || reuseStore.capacity() != data.length) {
            buff = createIntBuffer(data.length);
        } else {
            buff = reuseStore;
            buff.clear();
        }
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }

    /**
     * Copies floats from one buffer to another.
     * 
     * @param source
     *            the buffer to copy from
     * @param fromPos
     *            the starting point to copy from
     * @param destination
     *            the buffer to copy to
     * @param toPos
     *            the starting point to copy to
     * @param theLength
     *            the number of floats to copy
     */
    public static void copy(final FloatBuffer source, final int fromPos, final FloatBuffer destination,
            final int toPos, final int theLength) {
        final int oldLimit = source.limit();
        source.position(fromPos);
        source.limit(fromPos + theLength);
        destination.position(toPos);
        destination.put(source);
        source.limit(oldLimit);
    }

    /**
     * Copies floats from one position in the buffer to another.
     * 
     * @param buf
     *            the buffer to copy from/to
     * @param fromPos
     *            the starting point to copy from
     * @param toPos
     *            the starting point to copy to
     * @param theLength
     *            the number of floats to copy
     */
    public static void copyInternal(final FloatBuffer buf, final int fromPos, final int toPos, final int theLength) {
        final float[] data = new float[theLength];
        buf.position(fromPos);
        buf.get(data);
        buf.position(toPos);
        buf.put(data);
    }

    /**
     * Creates a new FloatBuffer with the same contents as the given FloatBuffer. The new FloatBuffer is seperate from
     * the old one and changes are not reflected across. If you want to reflect changes, consider using
     * Buffer.duplicate().
     * 
     * @param buf
     *            the FloatBuffer to copy
     * @return the copy
     */
    public static FloatBuffer clone(final FloatBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final FloatBuffer copy;
        if (buf.isDirect()) {
            copy = createFloatBuffer(buf.limit());
        } else {
            copy = createFloatBufferOnHeap(buf.limit());
        }
        copy.put(buf);

        return copy;
    }

    // // -- GENERAL INT ROUTINES -- ////

    /**
     * Create a new IntBuffer of the specified size.
     * 
     * @param size
     *            required number of ints to store.
     * @return the new IntBuffer
     */
    public static IntBuffer createIntBufferOnHeap(final int size) {
        final IntBuffer buf = ByteBuffer.allocate(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new IntBuffer of the specified size.
     * 
     * @param size
     *            required number of ints to store.
     * @return the new IntBuffer
     */
    public static IntBuffer createIntBuffer(final int size) {
        final IntBuffer buf = ByteBuffer.allocateDirect(4 * size).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new IntBuffer of an appropriate size to hold the specified number of ints only if the given buffer if
     * not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of ints that need to be held by the newly created buffer
     * @return the requested new IntBuffer
     */
    public static IntBuffer createIntBuffer(IntBuffer buf, final int size) {
        if (buf != null && buf.limit() == size) {
            buf.rewind();
            return buf;
        }

        buf = createIntBuffer(size);
        return buf;
    }

    /**
     * Creates a new IntBuffer with the same contents as the given IntBuffer. The new IntBuffer is seperate from the old
     * one and changes are not reflected across. If you want to reflect changes, consider using Buffer.duplicate().
     * 
     * @param buf
     *            the IntBuffer to copy
     * @return the copy
     */
    public static IntBuffer clone(final IntBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final IntBuffer copy;
        if (buf.isDirect()) {
            copy = createIntBuffer(buf.limit());
        } else {
            copy = createIntBufferOnHeap(buf.limit());
        }
        copy.put(buf);

        return copy;
    }

    // // -- GENERAL BYTE ROUTINES -- ////

    /**
     * Create a new ByteBuffer of the specified size.
     * 
     * @param size
     *            required number of ints to store.
     * @return the new IntBuffer
     */
    public static ByteBuffer createByteBuffer(final int size) {
        final ByteBuffer buf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
        buf.clear();
        return buf;
    }

    /**
     * Create a new ByteBuffer of an appropriate size to hold the specified number of ints only if the given buffer if
     * not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of bytes that need to be held by the newly created buffer
     * @return the requested new IntBuffer
     */
    public static ByteBuffer createByteBuffer(ByteBuffer buf, final int size) {
        if (buf != null && buf.limit() == size) {
            buf.rewind();
            return buf;
        }

        buf = createByteBuffer(size);
        return buf;
    }

    /**
     * Creates a new ByteBuffer with the same contents as the given ByteBuffer. The new ByteBuffer is seperate from the
     * old one and changes are not reflected across. If you want to reflect changes, consider using Buffer.duplicate().
     * 
     * @param buf
     *            the ByteBuffer to copy
     * @return the copy
     */
    public static ByteBuffer clone(final ByteBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final ByteBuffer copy;
        if (buf.isDirect()) {
            copy = createByteBuffer(buf.limit());
        } else {
            copy = createByteBufferOnHeap(buf.limit());
        }
        copy.put(buf);

        return copy;
    }

    // // -- GENERAL SHORT ROUTINES -- ////

    /**
     * Create a new ShortBuffer of the specified size.
     * 
     * @param size
     *            required number of shorts to store.
     * @return the new ShortBuffer
     */
    public static ShortBuffer createShortBufferOnHeap(final int size) {
        final ShortBuffer buf = ByteBuffer.allocate(2 * size).order(ByteOrder.nativeOrder()).asShortBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Create a new ShortBuffer of the specified size.
     * 
     * @param size
     *            required number of shorts to store.
     * @return the new ShortBuffer
     */
    public static ShortBuffer createShortBuffer(final int size) {
        final ShortBuffer buf = ByteBuffer.allocateDirect(2 * size).order(ByteOrder.nativeOrder()).asShortBuffer();
        buf.clear();
        return buf;
    }

    /**
     * Generate a new ShortBuffer using the given array of short primitives.
     * 
     * @param data
     *            array of short primitives to place into a new ShortBuffer
     */
    public static ShortBuffer createShortBuffer(final short... data) {
        if (data == null) {
            return null;
        }
        final ShortBuffer buff = createShortBuffer(data.length);
        buff.clear();
        buff.put(data);
        buff.flip();
        return buff;
    }

    /**
     * Create a new ShortBuffer of an appropriate size to hold the specified number of shorts only if the given buffer
     * if not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of shorts that need to be held by the newly created buffer
     * @return the requested new ShortBuffer
     */
    public static ShortBuffer createShortBuffer(ShortBuffer buf, final int size) {
        if (buf != null && buf.limit() == size) {
            buf.rewind();
            return buf;
        }

        buf = createShortBuffer(size);
        return buf;
    }

    /**
     * Creates a new ShortBuffer with the same contents as the given ShortBuffer. The new ShortBuffer is seperate from
     * the old one and changes are not reflected across. If you want to reflect changes, consider using
     * Buffer.duplicate().
     * 
     * @param buf
     *            the ShortBuffer to copy
     * @return the copy
     */
    public static ShortBuffer clone(final ShortBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final ShortBuffer copy;
        if (buf.isDirect()) {
            copy = createShortBuffer(buf.limit());
        } else {
            copy = createShortBufferOnHeap(buf.limit());
        }
        copy.put(buf);

        return copy;
    }

    /**
     * Ensures there is at least the <code>required</code> number of entries left after the current position of the
     * buffer. If the buffer is too small a larger one is created and the old one copied to the new buffer.
     * 
     * @param buffer
     *            buffer that should be checked/copied (may be null)
     * @param required
     *            minimum number of elements that should be remaining in the returned buffer
     * @return a buffer large enough to receive at least the <code>required</code> number of entries, same position as
     *         the input buffer, not null
     */
    public static FloatBuffer ensureLargeEnough(FloatBuffer buffer, final int required) {
        if (buffer == null || (buffer.remaining() < required)) {
            final int position = (buffer != null ? buffer.position() : 0);
            final FloatBuffer newVerts = createFloatBuffer(position + required);
            if (buffer != null) {
                buffer.rewind();
                newVerts.put(buffer);
                newVerts.position(position);
            }
            buffer = newVerts;
        }
        return buffer;
    }

    // // -- GENERAL INDEXBUFFERDATA ROUTINES -- ////

    /**
     * Create a new IndexBufferData of the specified size. The specific implementation will be chosen based on the max
     * value you need to store in your buffer. If that value is less than 2^8, a ByteBufferData is used. If it is less
     * than 2^16, a ShortBufferData is used. Otherwise an IntBufferData is used.
     * 
     * @param size
     *            required number of values to store.
     * @param maxValue
     *            the largest value you will need to store in your buffer. Often this is equal to
     *            ("size of vertex buffer" - 1).
     * @return the new IndexBufferData
     */
    public static CCIndexBufferData<?> createIndexBufferData(final int size, final int maxValue) {
        if (maxValue < 256) { // 2^8
            return createIndexBufferData(size, CCByteBufferData.class);
        } else if (maxValue < 65536) { // 2^16
            return createIndexBufferData(size, CCShortBufferData.class);
        } else {
            return createIndexBufferData(size, CCIntBufferData.class);
        }
    }

    /**
     * Create a new IndexBufferData large enough to fit the contents of the given array. The specific implementation
     * will be chosen based on the max value you need to store in your buffer. If that value is less than 2^8, a
     * ByteBufferData is used. If it is less than 2^16, a ShortBufferData is used. Otherwise an IntBufferData is used.
     * 
     * @param contents
     *            an array of index values to store in your newly created IndexBufferData.
     * @param maxValue
     *            the largest value you will need to store in your buffer. Often this is equal to
     *            ("size of vertex buffer" - 1).
     * @return the new IndexBufferData
     */
    public static CCIndexBufferData<?> createIndexBufferData(final int[] contents, final int maxValue) {
        final CCIndexBufferData<?> buffer;
        if (maxValue < 256) { // 2^8
            buffer = createIndexBufferData(contents.length, CCByteBufferData.class);
        } else if (maxValue < 65536) { // 2^16
            buffer = createIndexBufferData(contents.length, CCShortBufferData.class);
        } else {
            buffer = createIndexBufferData(contents.length, CCIntBufferData.class);
        }
        buffer.add(contents);
        return buffer;
    }

    /**
     * Create a new IndexBufferData of the specified size and class.
     * 
     * @param size
     *            required number of values to store.
     * @param clazz
     *            The class type to instantiate.
     * @return the new IndexBufferData
     */
    public static CCIndexBufferData<?> createIndexBufferData(final int size,
            final Class<? extends CCIndexBufferData<?>> clazz) {
        try {
            return clazz.getConstructor(int.class).newInstance(size);
        } catch (final Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Creates a new IndexBufferData with the same contents as the given IndexBufferData. The new IndexBufferData is
     * separate from the old one and changes are not reflected across.
     * 
     * @param buf
     *            the IndexBufferData to copy
     * @return the copy
     */
    @SuppressWarnings("unchecked")
    public static CCIndexBufferData<?> clone(final CCIndexBufferData<?> buf) {
        if (buf == null) {
            return null;
        }

        final CCIndexBufferData<?> copy = createIndexBufferData(buf.bufferLimit(),
                (Class<? extends CCIndexBufferData<?>>) buf.getClass());
        if (buf.buffer() == null) {
            copy.buffer(null);
        } else {
            buf.buffer().rewind();
            copy.add(buf);
        }

        return copy;
    }

    // // -- GENERAL HEAP BYTE ROUTINES -- ////

    /**
     * Create a new ByteBuffer of the specified size.
     * 
     * @param size
     *            required number of ints to store.
     * @return the new IntBuffer
     */
    public static ByteBuffer createByteBufferOnHeap(final int size) {
        final ByteBuffer buf = ByteBuffer.allocate(size).order(ByteOrder.nativeOrder());
        buf.clear();
        return buf;
    }

    /**
     * Create a new ByteBuffer of an appropriate size to hold the specified number of ints only if the given buffer if
     * not already the right size.
     * 
     * @param buf
     *            the buffer to first check and rewind
     * @param size
     *            number of bytes that need to be held by the newly created buffer
     * @return the requested new IntBuffer
     */
    public static ByteBuffer createByteBufferOnHeap(ByteBuffer buf, final int size) {
        if (buf != null && buf.limit() == size) {
            buf.rewind();
            return buf;
        }

        buf = createByteBufferOnHeap(size);
        return buf;
    }

    /**
     * Creates a new ByteBuffer with the same contents as the given ByteBuffer. The new ByteBuffer is seperate from the
     * old one and changes are not reflected across. If you want to reflect changes, consider using Buffer.duplicate().
     * 
     * @param buf
     *            the ByteBuffer to copy
     * @return the copy
     */
    public static ByteBuffer cloneOnHeap(final ByteBuffer buf) {
        if (buf == null) {
            return null;
        }
        buf.rewind();

        final ByteBuffer copy = createByteBufferOnHeap(buf.limit());
        copy.put(buf);

        return copy;
    }
}
