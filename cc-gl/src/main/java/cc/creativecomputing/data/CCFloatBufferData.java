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

import java.nio.FloatBuffer;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

/**
 * Simple data class storing a buffer of floats and a number that indicates how
 * many floats to group together to make up a "tuple"
 */
public class CCFloatBufferData extends CCAbstractBufferData<FloatBuffer> {
	
	public static CCFloatBufferData makeNew(final CCVector2[] coords) {
        if (coords == null) {
            return null;
        }

        return new CCFloatBufferData(CCBufferUtils.createFloatBuffer(coords), 2);
    }

    public static CCFloatBufferData makeNew(final CCVector3[] coords) {
        if (coords == null) {
            return null;
        }

        return new CCFloatBufferData(CCBufferUtils.createFloatBuffer(coords), 3);
    }

    public static CCFloatBufferData makeNew(final float[] coords) {
        if (coords == null) {
            return null;
        }

        return new CCFloatBufferData(CCBufferUtils.createFloatBuffer(coords), 1);
    }

    /**
     * Check an incoming TexCoords object for null and correct size.
     * 
     * @param tc
     * @param vertexCount
     * @param perVert
     * @return tc if it is not null and the right size, otherwise it will be a new TexCoords object.
     */
    public static CCFloatBufferData ensureSize(final CCFloatBufferData tc, final int vertexCount, final int coordsPerVertex) {
        if (tc == null) {
            return new CCFloatBufferData(CCBufferUtils.createFloatBuffer(vertexCount * coordsPerVertex), coordsPerVertex);
        }

        if (tc.buffer().limit() == coordsPerVertex * vertexCount && tc.valuesPerTuple() == coordsPerVertex) {
            tc.buffer().rewind();
            return tc;
        } else if (tc.buffer().limit() == coordsPerVertex * vertexCount) {
            tc.valuesPerTuple(coordsPerVertex);
        } else {
            return new CCFloatBufferData(CCBufferUtils.createFloatBuffer(vertexCount * coordsPerVertex), coordsPerVertex);
        }

        return tc;
    }

	/** Specifies the number of coordinates per vertex. Must be 1 - 4. */
	private int _myValuesPerTuple;

	/**
	 * Instantiates a new FloatBufferData.
	 */
	public CCFloatBufferData() {
	}

	/**
	 * Instantiates a new FloatBufferData with a buffer of the given size.
	 */
	public CCFloatBufferData(final int theSize, final int theValuesPerTuple) {
		this(CCBufferUtils.createFloatBuffer(theSize), theValuesPerTuple);
	}

	/**
	 * Creates a new FloatBufferData.
	 * 
	 * @param theBuffer
	 *            Buffer holding the data. Must not be null.
	 * @param theValuesPerTuple
	 *            Specifies the number of values per tuple. Can not be < 1.
	 */
	public CCFloatBufferData(final FloatBuffer theBuffer, final int theValuesPerTuple) {
		if (theBuffer == null) {
			throw new IllegalArgumentException("Buffer can not be null!");
		}

		if (theValuesPerTuple < 1) {
			throw new IllegalArgumentException("valuesPerTuple must be greater than 1.");
		}

		_myBuffer = theBuffer;
		_myValuesPerTuple = theValuesPerTuple;
	}

	public CCFloatBufferData add(final float theValue0, final float theValue1, final float theValue2) {
		if ( valuesPerTuple() != 3) {
			throw new IllegalArgumentException("Invalid number of values passed to float buffer: " + 3 + " valuesPerTuple " + valuesPerTuple());
		}
		_myBuffer.put(theValue0);
		_myBuffer.put(theValue1);
		_myBuffer.put(theValue2);
		return this;
	}
	
	public CCFloatBufferData add(final float theValue0, final float theValue1) {
		if ( valuesPerTuple() != 2) {
			throw new IllegalArgumentException("Invalid number of values passed to float buffer: " + 2 + " valuesPerTuple " + valuesPerTuple());
		}
		_myBuffer.put(theValue0);
		_myBuffer.put(theValue1);
		return this;
	}
	
	public CCFloatBufferData add(final double theValue0, double theValue1, double theValue2) {
		return add((float)theValue0, (float)theValue1, (float)theValue2);
	}
	
	public CCFloatBufferData add(final double theValue0, double theValue1) {
		return add((float)theValue0, (float)theValue1);
	}

	public CCFloatBufferData add(CCVector3 theVector) {
		return add(theVector.x, theVector.y, theVector.z);
	}

	public CCFloatBufferData add(CCVector2 theVector) {
		return add((float)theVector.x, (float)theVector.y);
	}

	public CCFloatBufferData put(final int theIndex, final float... theValues) {
		if (theValues == null || theValues.length != valuesPerTuple()) {
			throw new IllegalArgumentException("Invalid number of values passed to float buffer: " + theValues.length + " valuesPerTuple "
					+ valuesPerTuple());
		}
		int i = 0;
		for (float myValue : theValues) {
			_myBuffer.put(theIndex * valuesPerTuple() + i, myValue);
			i++;
		}
		return this;
	}

	public CCFloatBufferData put(final int theIndex, final CCVector4 theVector) {
		return put(theIndex, (float)theVector.x, (float)theVector.y, (float)theVector.z, (float)theVector.w);
	}

	public CCFloatBufferData put(final int theIndex, final CCVector3 theVector) {
		return put(theIndex, (float)theVector.x, (float)theVector.y, (float)theVector.z);
	}

	public CCFloatBufferData put(final int theIndex, final CCVector2 theVector) {
		return put(theIndex, (float)theVector.x, (float)theVector.y);
	}
	
	public CCFloatBufferData put(final int theIndex, final CCColor theColor) {
		return put(theIndex, (float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a);
	}
	


    /**
     * Updates the values of the given vector from the specified buffer at the index provided.
     * 
     * @param theVector
     *            the vector to set data on
     * @param buf
     *            the buffer to read from
     * @param theIndex
     *            the position (in terms of vectors, not floats) to read from the buffer
     */
    public void get(final CCVector4 theVector, final int theIndex) {
        theVector.x = _myBuffer.get(theIndex * 4);
        theVector.y = _myBuffer.get(theIndex * 4 + 1);
        theVector.z = _myBuffer.get(theIndex * 4 + 2);
        theVector.w = _myBuffer.get(theIndex * 4 + 3);
    }

	public void put(final CCFloatBufferData buf) {
		_myBuffer.put(buf.buffer());
	}

	@Override
	public int byteCount() {
		return 4;
	}

	public int tupleCount() {
		return bufferLimit() / _myValuesPerTuple;
	}

	/**
	 * @return number of values per tuple
	 */
	public int valuesPerTuple() {
		return _myValuesPerTuple;
	}

	/**
	 * Set number of values per tuple. This method should only be used
	 * internally.
	 * 
	 * @param theValuesPerTuple
	 *            number of values per tuple
	 */
	void valuesPerTuple(final int theValuesPerTuple) {
		_myValuesPerTuple = theValuesPerTuple;
	}

	/**
	 * Scale the data in this buffer by the given value(s)
	 * 
	 * @param theScales
	 *            the scale values to use. The Nth buffer element is scaled by
	 *            the (N % scales.length) scales element.
	 */
	public void scaleData(final float... theScales) {
		_myBuffer.rewind();
		for (int i = 0; i < _myBuffer.limit();) {
			_myBuffer.put(_myBuffer.get(i) * theScales[i % theScales.length]);
			i++;
		}
		_myBuffer.rewind();
	}

	/**
	 * Translate the data in this buffer by the given value(s)
	 * 
	 * @param theTranslationValues
	 *            the translation values to use. The Nth buffer element is
	 *            translated by the (N % translates.length) translates element.
	 */
	public void translateData(final float... theTranslationValues) {
		_myBuffer.rewind();
		for (int i = 0; i < _myBuffer.limit();) {
			_myBuffer.put(_myBuffer.get(i) + theTranslationValues[i % theTranslationValues.length]);
			i++;
		}
		_myBuffer.rewind();
	}

	@Override
	public CCFloatBufferData clone() {
		final CCFloatBufferData myClone = new CCFloatBufferData();
		myClone._myBuffer = CCBufferUtils.clone(_myBuffer);
		myClone._myValuesPerTuple = _myValuesPerTuple;
		return myClone;
	}

	public Class<? extends CCFloatBufferData> getClassTag() {
		return getClass();
	}

}
