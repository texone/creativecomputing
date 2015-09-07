/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.gl.data;

import java.nio.FloatBuffer;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.data.CCBufferUtils;
import cc.creativecomputing.data.CCCombinedBufferData;
import cc.creativecomputing.data.CCFloatBufferData;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCTransform;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * MeshData contains all the commonly used buffers for rendering a mesh.
 */
public class CCGeometryData extends CCCombinedBufferData{
	
	private static int index = 0;

	public static final int VERTEX_LAYOUT_INDEX = index++;
	public static final int NORMAL_LAYOUT_INDEX = index++;
	public static final int COLOR_LAYOUT_INDEX = index++;
	public static final int FOG_LAYOUT_INDEX = index++;
	public static final int TANGENT_LAYOUT_INDEX = index++;

	public static final int MAX_TEXTURE_UNITS = 16;

	public static final int TEXTURE_0_LAYOUT_INDEX = index++;
	public static final int TEXTURE_1_LAYOUT_INDEX = index++;
	public static final int TEXTURE_2_LAYOUT_INDEX = index++;
	public static final int TEXTURE_3_LAYOUT_INDEX = index++;
	public static final int TEXTURE_4_LAYOUT_INDEX = index++;
	public static final int TEXTURE_5_LAYOUT_INDEX = index++;
	public static final int TEXTURE_6_LAYOUT_INDEX = index++;
	public static final int TEXTURE_7_LAYOUT_INDEX = index++;
	public static final int TEXTURE_8_LAYOUT_INDEX = index++;
	public static final int TEXTURE_9_LAYOUT_INDEX = index++;
	public static final int TEXTURE_10_LAYOUT_INDEX = index++;
	public static final int TEXTURE_11_LAYOUT_INDEX = index++;
	public static final int TEXTURE_12_LAYOUT_INDEX = index++;
	public static final int TEXTURE_13_LAYOUT_INDEX = index++;
	public static final int TEXTURE_14_LAYOUT_INDEX = index++;
	public static final int TEXTURE_15_LAYOUT_INDEX = index++;

	public static final String VERTEX_LAYOUT_NAME = "vertex";
	public static final String NORMAL_LAYOUT_NAME = "normal";
	public static final String COLOR_LAYOUT_NAME = "color";
	public static final String FOG_LAYOUT_NAME = "fogCoord";
	public static final String TANGENT_LAYOUT_NAME = "tangent";

	private static final String TEXTURE_LAYOUT_NAME = "textureCoord_";
	public static final String TEXTURE_0_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "0";
	public static final String TEXTURE_1_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "1";
	public static final String TEXTURE_2_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "2";
	public static final String TEXTURE_3_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "3";
	public static final String TEXTURE_4_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "4";
	public static final String TEXTURE_5_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "5";
	public static final String TEXTURE_6_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "6";
	public static final String TEXTURE_7_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "7";
	public static final String TEXTURE_8_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "8";
	public static final String TEXTURE_9_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "9";
	public static final String TEXTURE_10_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "10";
	public static final String TEXTURE_11_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "11";
	public static final String TEXTURE_12_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "12";
	public static final String TEXTURE_13_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "13";
	public static final String TEXTURE_14_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "14";
	public static final String TEXTURE_15_LAYOUT_NAME = TEXTURE_LAYOUT_NAME + "15";

    /** Number of primitives represented by this data. */
    protected transient int _myPrimitiveCount;

    /** Interleaved data (for VBO id use). */
    protected CCFloatBufferData _myInterleavedData;

    protected GLDrawMode _myDrawMode;
    
    public CCGeometryData(GLDrawMode theDrawMode, int theVertexCount){
		super(theVertexCount);
    	_myDrawMode = theDrawMode;
    }

    /**
     * Gets the vertex buffer.
     * 
     * @return the vertex buffer
     */
    public FloatBuffer vertexBuffer() {
    	return buffer(VERTEX_LAYOUT_INDEX);
    }

    /**
     * Sets the vertex buffer.
     * 
     * @param vertexBuffer
     *            the new vertex buffer
     */
    public void vertices(final FloatBuffer vertexBuffer) {
    	buffer(VERTEX_LAYOUT_INDEX, vertexBuffer, 3);
		buffer(VERTEX_LAYOUT_NAME, vertexBuffer, 3);
		refreshInterleaved();
    }
    
    public void allocateVertices(){
		allocateData(VERTEX_LAYOUT_INDEX, 3);
		buffer(VERTEX_LAYOUT_NAME, vertexBuffer(), 3);
    }

    /**
     * Gets the vertex coords.
     * 
     * @return the vertex coords
     */
    public CCFloatBufferData vertices() {
		return bufferData(VERTEX_LAYOUT_INDEX);
    }

    private void refreshInterleaved() {
        if (_myInterleavedData != null) {
            _myInterleavedData.needsRefresh(true);
        }
    }

    /**
     * Sets the vertex coords.
     * 
     * @param theBufferData
     *            the new vertex coords
     */
    public void vertices(final CCFloatBufferData theBufferData) {
		bufferData(VERTEX_LAYOUT_INDEX, theBufferData);
		bufferData(VERTEX_LAYOUT_NAME, theBufferData);
		updateVertexCount();
		refreshInterleaved();
    }

    /**
     * Gets the normal buffer.
     * 
     * @return the normal buffer
     */
    public FloatBuffer normalBuffer() {
    	return buffer(NORMAL_LAYOUT_INDEX);
    }

    /**
     * Sets the normal buffer.
     * @param theNormalBuffer  the new normal buffer
     */
    public void normals(final FloatBuffer theNormalBuffer) {
    	buffer(NORMAL_LAYOUT_INDEX, theNormalBuffer, 3);
		buffer(NORMAL_LAYOUT_NAME, theNormalBuffer, 3);
		refreshInterleaved();
    }
    
    public void allocateNormals(){
		allocateData(NORMAL_LAYOUT_INDEX, 3);
		buffer(NORMAL_LAYOUT_NAME, normalBuffer(), 3);
    }

    /**
     * Gets the normal coords.
     * @return the normal coords
     */
    public CCFloatBufferData normals() {
		return bufferData(NORMAL_LAYOUT_INDEX);
    }

    /**
     * Sets the normal coords.
     * @param theBufferData the new normal coords
     */
    public void normals(final CCFloatBufferData theBufferData) {
		bufferData(NORMAL_LAYOUT_INDEX, theBufferData);
		bufferData(NORMAL_LAYOUT_NAME, theBufferData);
		refreshInterleaved();
    }

    /**
     * Gets the color buffer.
     * 
     * @return the color buffer
     */
    public FloatBuffer colorBuffer() {
    	return buffer(COLOR_LAYOUT_INDEX);
    }

    /**
     * Sets the color buffer.
     * 
     * @param theColorBuffer
     *            the new color buffer
     */
    public void colors(final FloatBuffer theColorBuffer) {
		buffer(COLOR_LAYOUT_INDEX, theColorBuffer, 4);
		buffer(COLOR_LAYOUT_NAME, theColorBuffer, 4);
		refreshInterleaved();
    }

    /**
     * Gets the color coords.
     * 
     * @return the color coords
     */
    public CCFloatBufferData colors() {
		return bufferData(COLOR_LAYOUT_INDEX);
    }

    /**
     * Sets the color coords.
     * 
     * @param theBufferData the new color coords
     */
    public void colors(final CCFloatBufferData theBufferData) {
    	bufferData(COLOR_LAYOUT_INDEX, theBufferData);
		refreshInterleaved();
    }

    /**
     * Gets the fog buffer.
     * 
     * @return the fog buffer
     */
    public FloatBuffer fogCoordBuffer() {
		return buffer(FOG_LAYOUT_INDEX);
    }

    /**
     * Sets the fog buffer.
     * 
     * @param theBuffer
     *            the new fog buffer
     */
    public void fogCoordBuffer(final FloatBuffer theBuffer) {
		buffer(FOG_LAYOUT_INDEX, theBuffer, 3);
		buffer(FOG_LAYOUT_NAME, theBuffer, 3);
    }

    /**
     * Gets the fog coords.
     * 
     * @return the fog coords
     */
    public CCFloatBufferData fogCoords() {
		return bufferData(FOG_LAYOUT_INDEX);
    }

    /**
     * Sets the fog coords.
     * 
     * @param theBufferData the new fog coords
     */
    public void fogCoords(final CCFloatBufferData theBufferData) {
		bufferData(FOG_LAYOUT_INDEX, theBufferData);
		bufferData(FOG_LAYOUT_NAME, theBufferData);
    }

    /**
     * Gets the tangent buffer.
     * 
     * @return the tangent buffer
     */
    public FloatBuffer tangentBuffer() {
		return buffer(TANGENT_LAYOUT_INDEX);
    }

    /**
     * Sets the tangent buffer.
     * 
     * @param theTangentBuffer the new tangent buffer
     */
    public void tangents(final FloatBuffer theTangentBuffer) {
		buffer(TANGENT_LAYOUT_INDEX, theTangentBuffer, 3);
		buffer(TANGENT_LAYOUT_NAME, theTangentBuffer, 3);
    }

    /**
     * Gets the tangent coords.
     * 
     * @return the tangent coords
     */
    public CCFloatBufferData tangents() {
		return bufferData(TANGENT_LAYOUT_INDEX);
    }

    /**
     * Sets the tangent coords.
     * 
     * @param bufferData
     *            the new tangent coords
     */
    public void tangents(final CCFloatBufferData bufferData) {
		bufferData(TANGENT_LAYOUT_INDEX, bufferData);
		bufferData(TANGENT_LAYOUT_NAME, bufferData);
    }
    
    private boolean validTexUnit(final int theTextureUnit) {
		return theTextureUnit >= 0 && theTextureUnit < MAX_TEXTURE_UNITS;
	}

    /**
     * Gets the FloatBuffer of the FloatBufferData set on a given texture unit.
     * 
     * @param theTextureUnit the unit index
     * 
     * @return the texture buffer for the given index, or null if none was set.
     */
    public FloatBuffer textureBuffer(final int theTextureUnit) {
		if (!validTexUnit(theTextureUnit))
			return null;
		return buffer(theTextureUnit + TEXTURE_0_LAYOUT_INDEX);
    }

    /**
	 * Sets the texture buffer for a given texture unit index. Interprets it as
	 * a 2 component float buffer data. If you need other sizes, use
	 * setTextureCoords instead.
	 * 
	 * @param theTextureBuffer
	 *            the texture buffer
	 * @param theTextureUnit
	 *            the unit index
	 * @see #textureCoords(CCFloatBufferData, int)
	 */
	public void textureCoords(final FloatBuffer theTextureBuffer, final int theTextureUnit, final int theVertexSize) {
		if (!validTexUnit(theTextureUnit))
			return;

		buffer(TEXTURE_0_LAYOUT_INDEX + theTextureUnit, theTextureBuffer, theVertexSize);
		buffer(TEXTURE_LAYOUT_NAME + theTextureUnit, theTextureBuffer, theVertexSize);

		refreshInterleaved();
	}
    
    public void allocateTextureCoords(int theTextureUnit, int theVertexSize){
		if (theTextureUnit < 0 || theTextureUnit >= MAX_TEXTURE_UNITS) {
			return;
		}
		textureCoords(new CCFloatBufferData(FloatBuffer.allocate(_myNumberOfVertices * theVertexSize), theVertexSize), theTextureUnit);
    }

    /**
     * Gets the texture coords assigned to a specific texture unit index of this MeshData.
     * 
     * @param theTextureUnit
     *            the texture unit index
     * 
     * @return the texture coords
     */
    public CCFloatBufferData textureCoords(final int theTextureUnit) {
		if (!validTexUnit(theTextureUnit))
			return null;
		return bufferData(theTextureUnit + TEXTURE_0_LAYOUT_INDEX);
    }

    /**
     * Sets all texture coords on this MeshData.
     * 
     * @param textureCoords
     *            the new texture coords
     */
    public void textureCoords(final CCFloatBufferData[] textureCoords) {
		for (int i = 0; i < textureCoords.length; i++) {
			textureCoords(textureCoords[i], i);
		}
		refreshInterleaved();
    }

    /**
     * Sets the texture coords of a specific texture unit index to the given FloatBufferData.
     * 
     * @param textureCoords
     *            the texture coords
     * @param theTextureUnit
     *            the unit index
     */
    public void textureCoords(final CCFloatBufferData textureCoords, final int theTextureUnit) {
		if (!validTexUnit(theTextureUnit))
			return;

		bufferData(TEXTURE_0_LAYOUT_INDEX + theTextureUnit, textureCoords);
		bufferData(TEXTURE_LAYOUT_NAME + theTextureUnit, textureCoords);

		refreshInterleaved();
    }

    /**
     * <code>copyTextureCoords</code> copies the texture coordinates of a given texture unit to another location. If the
     * texture unit is not valid, then the coordinates are ignored. Coords are multiplied by the given S and T factors.
     * 
     * @param theSourceUnit the coordinates to copy.
     * @param theDstUnit the texture unit to set them to. Must not be the same as the fromIndex.
     * @param factorS a multiple to apply to the S channel when copying
     * @param factorT a multiple to apply to the T channel when copying
     */
    public void copyTextureCoordinates(final int theSourceUnit, final int theDstUnit, final float factorS, final float factorT) {
    	if (theSourceUnit < 0 || theSourceUnit >= MAX_TEXTURE_UNITS || textureCoords(theSourceUnit) == null) {
			return;
		}

		if (theDstUnit < 0 || theDstUnit == theSourceUnit) {
			return;
		}

		CCFloatBufferData dest = textureCoords(theDstUnit);
		final CCFloatBufferData src = textureCoords(theSourceUnit);
		if (dest == null || dest.buffer().capacity() != src.buffer().limit()) {
			dest = new CCFloatBufferData(CCBufferUtils.createFloatBuffer(src.buffer().capacity()), src.valuesPerTuple());
			textureCoords(dest, theDstUnit);
		}
		dest.buffer().clear();
		final int oldLimit = src.buffer().limit();
		src.buffer().clear();
		for (int i = 0, len = dest.buffer().capacity(); i < len; i++) {
			if (i % 2 == 0) {
				dest.buffer().put(factorS * src.buffer().get());
			} else {
				dest.buffer().put(factorT * src.buffer().get());
			}
		}
		src.buffer().limit(oldLimit);
		dest.buffer().limit(oldLimit);
    }
    
    /**
     * <code>copyTextureCoords</code> copies the texture coordinates of a given texture unit to another location. If the
     * texture unit is not valid, then the coordinates are ignored. Coords are multiplied by the given factor.
     * 
     * @param theSourceUnit the coordinates to copy.
     * @param theDstUnit the texture unit to set them to. Must not be the same as the fromIndex.
     * @param theFactor a multiple to apply when copying
     */
    public void copyTextureCoordinates(final int theSourceUnit, final int theDstUnit, final float theFactor) {
    	copyTextureCoordinates(theSourceUnit, theDstUnit, theFactor, theFactor);
    }

    /**
     * Returns the number of texture units this geometry is currently using.
     * 
     * @return the number of texture units in use.
     */
    public int numberOfTextureUnits() {
        return MAX_TEXTURE_UNITS;
    }

    /**
     * Retrieves the interleaved buffer, if set or created through packInterleaved.
     * 
     * @return the interleaved buffer
     */
    public FloatBuffer interleavedDataBuffer() {
        if (_myInterleavedData == null) {
            return null;
        }
        return _myInterleavedData.buffer();
    }

    /**
     * Gets the interleaved data.
     * 
     * @return the interleaved data
     */
    public CCFloatBufferData interleavedData() {
        return _myInterleavedData;
    }

    /**
     * Sets the interleaved data.
     * 
     * @param theBufferData the interleaved data
     */
    public void interleavedData(final CCFloatBufferData theBufferData) {
        _myInterleavedData = theBufferData;
        refreshInterleaved();
    }

    /**
     * Update the vertex count based on the current limit of the vertex buffer.
     */
    public void updateVertexCount() {
        if (vertices() == null) {
            _myNumberOfVertices = 0;
        } else {
            _myNumberOfVertices = vertices().tupleCount();
        }
        // update primitive count if we are using arrays
        if (_myIndices == null) {
            updatePrimitiveCounts();
        }
    }

    /**
     * Sets the index mode.
     * 
     * @param indexMode
     *            the new GLDrawMode to use for the first section of this MeshData.
     */
    public void drawMode(final GLDrawMode theDrawMode) {
        _myDrawMode = theDrawMode;
        updatePrimitiveCounts();
        refreshInterleaved();
    }

    /**
     * Gets the index lengths.
     * 
     * @return the index lengths
     */
    public int numberOfIndices() {
        return _myNumberOfIndices;
    }

    /**
     * Gets the index modes.
     * 
     * @return the index modes
     */
    public GLDrawMode drawMode() {
        return _myDrawMode;
    }

    /**
     * Gets the primitive count.
     * 
     * @param section
     *            the section
     * 
     * @return the number of primitives (triangles, quads, lines, points, etc.) on a given section of this mesh data.
     */
    public int getPrimitiveCount() {
        return _myPrimitiveCount;
    }

    /**
     * Returns the vertex indices of a specified primitive.
     * 
     * @param primitiveIndex
     *            which triangle, quad, etc
     * @param store
     *            an int array to store the results in. if null, or the length < the size of the primitive, a new array
     *            is created and returned.
     * 
     * @return the primitive's vertex indices as an array
     * 
     * @throws IndexOutOfBoundsException
     *             if primitiveIndex is outside of range [0, count-1] where count is the number of primitives in the
     *             given section.
     * @throws ArrayIndexOutOfBoundsException
     *             if section is out of range [0, N-1] where N is the number of sections in this MeshData object.
     */
    public int[] getPrimitiveIndices(final int primitiveIndex, final int[] store) {
        final int count = getPrimitiveCount();
        if (primitiveIndex >= count || primitiveIndex < 0) {
            throw new IndexOutOfBoundsException("Invalid primitiveIndex '" + primitiveIndex + "'.  Count is " + count);
        }

        final int rSize = _myDrawMode.getVertexCount();

        int[] result = store;
        if (result == null || result.length < rSize) {
            result = new int[rSize];
        }

        for (int i = 0; i < rSize; i++) {
            if (indices() != null) {
                result[i] = indices().get(getVertexIndex(primitiveIndex, i));
            } else {
                result[i] = getVertexIndex(primitiveIndex, i);
            }
        }

        return result;
    }

    /**
     * Gets the vertices that make up the given primitive.
     * 
     * @param primitiveIndex
     *            the primitive index
     * @param section
     *            the section
     * @param store
     *            the store. If null or the wrong size, we'll make a new array and return that instead.
     * 
     * @return the primitive
     */
    public CCVector3[] getPrimitiveVertices(final int primitiveIndex, final CCVector3[] store) {
        final int count = getPrimitiveCount();
        if (primitiveIndex >= count || primitiveIndex < 0) {
            throw new IndexOutOfBoundsException("Invalid primitiveIndex '" + primitiveIndex + "'.  Count is " + count);
        }

        final int rSize = _myDrawMode.getVertexCount();
        CCVector3[] result = store;
        if (result == null || result.length < rSize) {
            result = new CCVector3[rSize];
        }

        for (int i = 0; i < rSize; i++) {
            if (result[i] == null) {
                result[i] = new CCVector3();
            }
            if (indices() != null) {
                // indexed geometry
                CCBufferUtils.populateFromBuffer(result[i], vertexBuffer(),
                        indices().get(getVertexIndex(primitiveIndex, i)));
            } else {
                // non-indexed geometry
                CCBufferUtils
                        .populateFromBuffer(result[i], vertexBuffer(), getVertexIndex(primitiveIndex, i));
            }
        }

        return result;
    }

    /**
     * Gets the texture coordinates of the primitive.
     * 
     * @param primitiveIndex
     *            the primitive index
     * @param section
     *            the section
     * @param textureIndex
     *            the texture index
     * @param store
     *            the store
     * 
     * @return the texture coordinates of the primitive
     */
    public CCVector2[] getPrimitiveTextureCoords(final int primitiveIndex, final int textureIndex,
            final CCVector2[] store) {
        CCVector2[] result = null;
        if (textureBuffer(textureIndex) != null) {
            final int count = getPrimitiveCount();
            if (primitiveIndex >= count || primitiveIndex < 0) {
                throw new IndexOutOfBoundsException("Invalid primitiveIndex '" + primitiveIndex + "'.  Count is "
                        + count);
            }
            final int rSize = _myDrawMode.getVertexCount();
            result = store;
            if (result == null || result.length < rSize) {
                result = new CCVector2[rSize];
            }
            for (int i = 0; i < rSize; i++) {
                if (result[i] == null) {
                    result[i] = new CCVector2();
                }
                if (indexBuffer() != null) {// indexed geometry
                    CCBufferUtils.populateFromBuffer(result[i], textureBuffer(textureIndex),
                            indices().get(getVertexIndex(primitiveIndex, i)));
                } else {// non-indexed geometry
                    CCBufferUtils.populateFromBuffer(result[i], textureBuffer(textureIndex),
                            getVertexIndex(primitiveIndex, i));
                }
            }
        }
        return result;
    }

    /**
     * Gets the vertex index.
     * 
     * @param primitiveIndex
     *            which triangle, quad, etc.
     * @param point
     *            which point on the triangle, quad, etc. (triangle has three points, so this would be 0-2, etc.)
     * @param section
     *            which section to pull from (corresponds to array position in indexmodes and lengths)
     * 
     * @return the position you would expect to find the given point in the index buffer
     */
    public int getVertexIndex(final int primitiveIndex, final int point) {
        int index = 0;

        // Ok, now pull primitive index based on indexmode.
        switch (_myDrawMode) {
            case TRIANGLES:
                index += (primitiveIndex * 3) + point;
                break;
            case TRIANGLE_STRIP:
                // XXX: we need to flip point 0 and 1 on odd primitiveIndex values
                if (point < 2 && primitiveIndex % 2 == 1) {
                    index += primitiveIndex + (point == 0 ? 1 : 0);
                } else {
                    index += primitiveIndex + point;
                }
                break;
            case TRIANGLE_FAN:
                if (point == 0) {
                    index += 0;
                } else {
                    index += primitiveIndex + point;
                }
                break;
//            case Quads:
//                index += (primitiveIndex * 4) + point;
//                break;
//            case QuadStrip:
//                index += (primitiveIndex * 2) + point;
//                break;
            case POINTS:
                index += primitiveIndex;
                break;
            case LINES:
                index += (primitiveIndex * 2) + point;
                break;
            case LINE_STRIP:
            case LINE_LOOP:
                index += primitiveIndex + point;
                break;
            default:
                CCLog.warn("unimplemented index mode: " + _myDrawMode);
                return -1;
        }
        return index;
    }

    /**
     * Random vertex.
     * 
     * @param store
     *            the vector object to store the result in. if null, a new one is created.
     * 
     * @return a random vertex from the vertices stored in this MeshData. null is returned if there are no vertices.
     */
    public CCVector3 randomVertex(final CCVector3 store) {
        if (vertices() == null) {
            return null;
        }

        CCVector3 result = store;
        if (result == null) {
            result = new CCVector3();
        }

        final int i = CCMath.RANDOM.nextInt(_myNumberOfVertices - 1);
        CCBufferUtils.populateFromBuffer(result, vertices().buffer(), i);

        return result;
    }

    /**
     * Random point on primitives.
     * 
     * @param store
     *            the vector object to store the result in. if null, a new one is created.
     * 
     * @return a random point from the surface of a primitive stored in this MeshData. null is returned if there are no
     *         vertices or indices.
     */
    public CCVector3 randomPointOnPrimitives(final CCVector3 store) {
        if (vertices() == null || _myIndices == null) {
            return null;
        }

        CCVector3 result = store;
        if (result == null) {
            result = new CCVector3();
        }


        // randomly pick a primitive in that section
        final int primitiveIndex = CCMath.RANDOM.nextInt(getPrimitiveCount() - 1);

        // Now, based on GLDrawMode, pick a point on that primitive
        
        final boolean hasIndices = indices() != null;
        switch (_myDrawMode) {
            case TRIANGLES:
            case TRIANGLE_FAN:
            case TRIANGLE_STRIP:
//            case Quads:
//            case QuadStrip: 
            {
                int pntA = getVertexIndex(primitiveIndex, 0);
                int pntB = getVertexIndex(primitiveIndex, 1);
                int pntC = getVertexIndex(primitiveIndex, 2);

                if (hasIndices) {
                    pntA = indices().get(pntA);
                    pntB = indices().get(pntB);
                    pntC = indices().get(pntC);
                }

                float b = CCMath.RANDOM.nextFloat();
                float c = CCMath.RANDOM.nextFloat();

//                if (mode != GLDrawMode.Quads && mode != GLDrawMode.QuadStrip) {
//                    // keep it in the triangle by reflecting it across the center diagonal BC
//                    if (b + c > 1) {
//                        b = 1 - b;
//                        c = 1 - c;
//                    }
//                }

                final float a = 1 - b - c;

                final CCVector3 work = new CCVector3();
                CCBufferUtils.populateFromBuffer(work, vertexBuffer(), pntA);
                work.multiplyLocal(a);
                result.set(work);

                CCBufferUtils.populateFromBuffer(work, vertexBuffer(), pntB);
                work.multiplyLocal(b);
                result.addLocal(work);

                CCBufferUtils.populateFromBuffer(work, vertexBuffer(), pntC);
                work.multiplyLocal(c);
                result.addLocal(work);
                break;
            }
            case POINTS: {
                int pnt = getVertexIndex(primitiveIndex, 0);
                if (hasIndices) {
                    pnt = indices().get(pnt);
                }
                CCBufferUtils.populateFromBuffer(result, vertexBuffer(), pnt);
                break;
            }
            case LINES:
            case LINE_LOOP:
            case LINE_STRIP: {
                int pntA = getVertexIndex(primitiveIndex, 0);
                int pntB = getVertexIndex(primitiveIndex, 1);
                if (hasIndices) {
                    pntA = indices().get(pntA);
                    pntB = indices().get(pntB);
                }

                final CCVector3 work = new CCVector3();
                CCBufferUtils.populateFromBuffer(result, vertexBuffer(), pntA);
                CCBufferUtils.populateFromBuffer(work, vertexBuffer(), pntB);
                CCVector3.lerp(result, work, CCMath.RANDOM.nextFloat(), result);
                break;
            }
		default:
			break;
        }

        return result;
    }

    /**
     * Translate points.
     * 
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     */
    public void translatePoints(final float x, final float y, final float z) {
        translatePoints(new CCVector3(x, y, z));
    }

    /**
     * Translate points.
     * 
     * @param amount
     *            the amount
     */
    public void translatePoints(final CCVector3 amount) {
        for (int x = 0; x < _myNumberOfVertices; x++) {
            CCBufferUtils.addInBuffer(amount, vertices().buffer(), x);
        }
    }

    public void transformVertices(final CCTransform transform) {
        final CCVector3 store = new CCVector3();
        for (int x = 0; x < _myNumberOfVertices; x++) {
            CCBufferUtils.populateFromBuffer(store, vertices().buffer(), x);
            transform.applyForward(store, store);
            CCBufferUtils.setInBuffer(store, vertices().buffer(), x);
        }
    }

    public void transformNormals(final CCTransform transform, final boolean normalize) {
        final CCVector3 store = new CCVector3();
        for (int x = 0; x < _myNumberOfVertices; x++) {
            CCBufferUtils.populateFromBuffer(store, normals().buffer(), x);
            transform.applyForwardVector(store, store);
            if (normalize) {
                store.normalizeLocal();
            }
            CCBufferUtils.setInBuffer(store, normals().buffer(), x);
        }
    }

    /**
     * Rotate points.
     * 
     * @param rotate
     *            the rotate
     */
    public void rotatePoints(final CCQuaternion rotate) {
        final CCVector3 store = new CCVector3();
        for (int x = 0; x < _myNumberOfVertices; x++) {
            CCBufferUtils.populateFromBuffer(store, vertices().buffer(), x);
            rotate.apply(store, store);
            CCBufferUtils.setInBuffer(store, vertices().buffer(), x);
        }
    }

    /**
     * Rotate normals.
     * 
     * @param rotate
     *            the rotate
     */
    public void rotateNormals(final CCQuaternion rotate) {
        final CCVector3 store = new CCVector3();
        for (int x = 0; x < _myNumberOfVertices; x++) {
            CCBufferUtils.populateFromBuffer(store, normals().buffer(), x);
            rotate.apply(store, store);
            CCBufferUtils.setInBuffer(store, normals().buffer(), x);
        }
    }

    /**
     * Update primitive counts.
     */
    private void updatePrimitiveCounts() {
        final int size = _myIndices != null ? _myIndices.bufferLimit() : _myNumberOfVertices;
            _myPrimitiveCount = GLDrawMode.getPrimitiveCount(_myDrawMode, size);

    }

	public void rewindBuffer() {
		if(vertices() != null)vertices().buffer().rewind();
		if(normals() != null)normals().buffer().rewind();
		if(_myIndices != null)_myIndices.rewind();
		for(int texUnit = 0; texUnit < MAX_TEXTURE_UNITS; texUnit++){
			CCFloatBufferData myData = textureCoords(texUnit);
			if(myData != null)myData.buffer().rewind();
		}
	}

    public CCGeometryData makeCopy() {
        final CCGeometryData data = new CCGeometryData(_myDrawMode, _myNumberOfVertices);
        data._myPrimitiveCount = _myPrimitiveCount;

        if (vertices() != null) {
            data.vertices(vertices().clone());
        }
        if (normals() != null) {
            data.normals(normals().clone());
        }
        if (colors() != null) {
            data.colors(colors().clone());
        }
        if (fogCoords() != null) {
            data.fogCoords(fogCoords().clone());
        }
        if (tangents() != null) {
            data.tangents(tangents().clone());
        }

//        for (final FloatBufferData tCoord : _textureCoords) {
//            if (tCoord != null) {
//                data._textureCoords.add(tCoord.makeCopy());
//            } else {
//                data._textureCoords.add(null);
//            }
//        }

        if (_myIndices != null) {
            data._myIndices = _myIndices.clone();
        }

        return data;
    }

}
