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
package cc.creativecomputing.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * <p>
 * The CCMesh class maps the OPENGL vertex arrays that can make drawing much
 * faster than using beginShape and endShape. Note that there are different ways
 * to work with the meshes. You can setup the mesh once with all the data and
 * than just repeatedly draw it. Also look at the VBOMesh that is even much
 * faster for drawing static content.
 * </p>
 * <p>
 * To update the mesh content simply provide the data as a list of vertices or a
 * doublebuffer. Be aware that using a doublebuffer should be preferred for
 * better performance. You can also initialize a mesh with a certain size and
 * than add all vertices one by one.
 * </p>
 * <p>
 * You can also pass indices to draw an indexed array. This can be used to
 * reduce the mesh size.
 * </p>
 * 
 * @author info
 * @see #CCVBOMesh
 */
public class CCMesh {
	// Mesh Data
	protected int _myNumberOfVertices = 0;
	protected int _myVertexSize;
	protected int[] _myTextureCoordSize = new int[8];

	protected int _myNumberOfIndices = 0;

	protected FloatBuffer _myVertices;
	protected FloatBuffer _myNormals;
	protected FloatBuffer[] _myTextureCoords = new FloatBuffer[8];
	protected FloatBuffer _myColors;

	protected IntBuffer _myIndices;

	protected CCDrawMode _myDrawMode = CCDrawMode.TRIANGLES;

	public CCMesh() {
		_myVertexSize = 3;
	}

	public CCMesh(final int theNumberOfVertices) {
		_myNumberOfVertices = theNumberOfVertices;
		_myVertexSize = 3;
	}

	public CCMesh(final CCDrawMode theDrawMode) {
		_myDrawMode = theDrawMode;
		_myVertexSize = 3;
	}

	public CCMesh(final CCDrawMode theDrawMode, final int theNumberOfVertices) {
		_myDrawMode = theDrawMode;
		_myNumberOfVertices = theNumberOfVertices;
		_myVertexSize = 3;
	}

	public CCMesh(final List<CCVector3> theVertices, final List<CCVector2> theTextureCoords,
			final List<CCColor> theColors) {
		this(CCDrawMode.TRIANGLES, theVertices, theTextureCoords, theColors);
	}

	public CCMesh(final CCDrawMode theDrawMode, final List<CCVector3> theVertices, final List<CCVector2> theTextureCoords, final List<CCColor> theColors) {
		_myDrawMode = theDrawMode;
		_myVertexSize = 3;
		if (theVertices != null)
			vertices(theVertices, false);
		if (theTextureCoords != null)
			textureCoords(theTextureCoords);
		if (theColors != null)
			colors(theColors);
	}

	//////////////////////////////////////////////////////
	//
	// METHODS TO ADD VERTEX DATA
	//
	//////////////////////////////////////////////////////

	public void prepareVertexData(int theNumberOfVertices, int theVertexSize) {
		_myNumberOfVertices = theNumberOfVertices;
		_myVertexSize = theVertexSize;

		if (_myVertices == null || _myVertices.limit() / _myVertexSize != _myNumberOfVertices) {
			_myNumberOfVertices = theNumberOfVertices;
			_myVertexSize = theVertexSize;
			_myVertices = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * _myVertexSize);
		}
	}

	public void prepareVertexData(int theVertexSize) {
		prepareVertexData(_myNumberOfVertices, theVertexSize);
	}

	/**
	 * Adds the given vertex to the Mesh.
	 */
	public void addVertex(final double theX, final double theY, final double theZ) {
		prepareVertexData(_myNumberOfVertices, 3);
		_myVertices.put((float) theX);
		_myVertices.put((float) theY);
		_myVertices.put((float) theZ);
	}

	public void addVertex(final double theX, final double theY) {
		addVertex(theX, theY, 0);
	}

	public void addVertex(final double theX, final double theY, final double theZ, final double theW) {
		prepareVertexData(_myNumberOfVertices, 4);

		_myVertices.put((float) theX);
		_myVertices.put((float) theY);
		_myVertices.put((float) theZ);
		_myVertices.put((float) theW);
	}

	public void addVertex(final CCVector2 theVertex) {
		addVertex(theVertex.x, theVertex.y, 0);
	}

	public void addVertex(final CCVector3 theVertex) {
		addVertex(theVertex.x, theVertex.y, theVertex.z);
	}

	public void addVertex(final CCVector4 theVertex) {
		addVertex(theVertex.x, theVertex.y, theVertex.z, theVertex.w);
	}

	/**
	 * Fills the mesh with the data from the given double buffer. This means the
	 * double buffer needs to contain all vertex data for the mesh. This method
	 * assumes you pass three coords for every vertex.
	 * 
	 * @param theVertices vertex data for the mesh
	 */
	public void vertices(final FloatBuffer theVertices) {
		vertices(theVertices, 3);
	}

	/**
	 * Fills the mesh with the data from the given double buffer. This means the
	 * double buffer needs to contain all vertex data for the mesh.
	 * 
	 * @param theVertices vertex data for the mesh
	 * @param theVertexSize
	 */
	public void vertices(final FloatBuffer theVertices, int theVertexSize) {
		theVertices.rewind();
		prepareVertexData(theVertices.limit() / theVertexSize, theVertexSize);
		_myNumberOfVertices = theVertices.limit() / theVertexSize;
		_myVertexSize = theVertexSize;

		_myVertices.rewind();
		_myVertices.put(theVertices);
		_myVertices.rewind();
	}

	public FloatBuffer vertices() {
		return _myVertices;
	}

	public void vertices(final List<CCVector3> theVertices) {
		vertices(theVertices, false);
	}

	protected FloatBuffer[] createVertexBufferFromList4(final List<CCVector4> theVertices, final boolean theGenerateNormals) {
		FloatBuffer[] myResult = new FloatBuffer[2];

		if (theVertices.size() == 0) {
			return null;
		}

		_myVertexSize = 4;
		_myNumberOfVertices = theVertices.size();
		myResult[0] = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * _myVertexSize);

		myResult[0].rewind();
		for (CCVector4 myVertex : theVertices) {
			myResult[0].put((float) myVertex.x);
			myResult[0].put((float) myVertex.y);
			myResult[0].put((float) myVertex.z);
			myResult[0].put((float) myVertex.w);
		}
		myResult[0].rewind();

		return myResult;
	}

	protected FloatBuffer[] createVertexBufferFromList(final List<CCVector3> theVertices, final boolean theGenerateNormals) {
		FloatBuffer[] myResult = new FloatBuffer[2];

		if (theVertices.size() == 0) {
			return null;
		}

		_myVertexSize = 3;
		_myNumberOfVertices = theVertices.size();
		myResult[0] = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * _myVertexSize);

		myResult[0].rewind();
		for (CCVector3 myVertex : theVertices) {
			myResult[0].put((float) myVertex.x);
			myResult[0].put((float) myVertex.y);
			myResult[0].put((float) myVertex.z);
		}
		myResult[0].rewind();

		if (!theGenerateNormals)
			return myResult;

		myResult[1] = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * _myVertexSize);

		CCVector3 v1, v2, v3, v21, v31, normal = new CCVector3(1, 0, 0);

		int myPointsPerPolygon = -1;

		switch (_myDrawMode) {
		case QUADS:
			myPointsPerPolygon = 4;
			break;
		case TRIANGLES:
			myPointsPerPolygon = 3;
			break;
		default:
			return myResult;
		}

		for (int i = 0; i < theVertices.size(); i += myPointsPerPolygon) {
			v1 = theVertices.get(i);
			v2 = theVertices.get(i + 1);
			v3 = theVertices.get(i + 2);

			v21 = v2.subtract(v1);
			v31 = v3.subtract(v1);

			normal = v21.cross(v31);
			normal.normalize();

			for (int j = 0; j < myPointsPerPolygon; j++) {
				myResult[1].put((float) normal.x);
				myResult[1].put((float) normal.y);
				myResult[1].put((float) normal.z);
			}
		}

		return myResult;
	}

	public void vertices(final List<CCVector3> theVertices, final boolean theGenerateNormals) {
		if (theVertices.size() == 0) {
			return;
		}

		FloatBuffer[] myVertexBuffer = createVertexBufferFromList(theVertices, theGenerateNormals);
		if (myVertexBuffer[0] != null)
			vertices(myVertexBuffer[0]);
		if (myVertexBuffer[1] != null)
			normals(myVertexBuffer[1]);

	}

	public void vertices4(final List<CCVector4> theVertices, final boolean theGenerateNormals) {
		if (theVertices.size() == 0) {
			return;
		}

		FloatBuffer[] myVertexBuffer = createVertexBufferFromList4(theVertices, theGenerateNormals);
		if (myVertexBuffer[0] != null)
			vertices(myVertexBuffer[0], 4);
		if (myVertexBuffer[1] != null)
			normals(myVertexBuffer[1]);

	}

	//////////////////////////////////////////////////////
	//
	// METHODS TO ADD NORMAL DATA
	//
	//////////////////////////////////////////////////////

	public void prepareNormalData(int theNumberOfVertices) {
		_myNumberOfVertices = theNumberOfVertices;

		if (_myNormals == null || _myNormals.limit() / 3 != _myNumberOfVertices) {
			_myNumberOfVertices = theNumberOfVertices;
			_myNormals = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * 3);
		}
	}

	public void prepareNormalData() {
		prepareNormalData(_myNumberOfVertices);
	}

	public void addNormal(final double theX, final double theY, final double theZ) {
		prepareNormalData(_myNumberOfVertices);

		_myNormals.put((float) theX);
		_myNormals.put((float) theY);
		_myNormals.put((float) theZ);
	}

	public void addNormal(final CCVector3 theNormal) {
		addNormal(theNormal.x, theNormal.y, theNormal.z);
	}

	public void normals(final double[] theNormalData) {
		prepareNormalData(theNormalData.length / 3);

		_myNormals.rewind();
		_myNormals.put((float) theNormalData[0]);
		_myNormals.put((float) theNormalData[1]);
		_myNormals.put((float) theNormalData[2]);
		_myNormals.rewind();
	}

	public void normals(final FloatBuffer theNormalData) {
		prepareNormalData(theNormalData.limit() / 3);

		_myNormals.rewind();
		_myNormals.put(theNormalData);
		_myNormals.rewind();
	}

	public FloatBuffer normals() {
		return _myNormals;
	}

	public void normals(final List<CCVector3> theNormals) {
		prepareNormalData(theNormals.size());

		_myNormals.rewind();
		for (CCVector3 myNormal : theNormals) {
			_myNormals.put((float) myNormal.x);
			_myNormals.put((float) myNormal.y);
			_myNormals.put((float) myNormal.z);
		}
		_myNormals.rewind();
	}

	//////////////////////////////////////////////////////
	//
	// METHODS TO ADD TEXTURE COORD DATA
	//
	//////////////////////////////////////////////////////

	public void prepareTextureCoordData(int theNumberOfVertices, int theLevel, int theTextureCoordSize) {
		_myNumberOfVertices = theNumberOfVertices;
		_myTextureCoordSize[theLevel] = theTextureCoordSize;

		if (_myTextureCoords[theLevel] == null
				|| _myNumberOfVertices != _myTextureCoords[theLevel].limit() / theTextureCoordSize) {
			_myTextureCoords[theLevel] = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * theTextureCoordSize);
		}
	}

	public void prepareTextureCoordData(int theLevel, int theTextureCoordSize) {
		prepareTextureCoordData(_myNumberOfVertices, theLevel, theTextureCoordSize);
	}

	public void addTextureCoords(final int theLevel, final double theX, final double theY) {
		prepareTextureCoordData(_myNumberOfVertices, theLevel, 2);

		_myTextureCoords[theLevel].put((float) theX);
		_myTextureCoords[theLevel].put((float) theY);
	}

	public void addTextureCoords(final double theX, final double theY) {
		addTextureCoords(0, theX, theY);
	}

	public void addTextureCoords(final int theLevel, final CCVector2 theTextureCoords) {
		addTextureCoords(1, theTextureCoords.x, theTextureCoords.y);
	}

	public void addTextureCoords(final int theLevel, final double theX, final double theY, final double theZ) {
		prepareTextureCoordData(_myNumberOfVertices, theLevel, 3);

		_myTextureCoords[theLevel].put((float) theX);
		_myTextureCoords[theLevel].put((float) theY);
		_myTextureCoords[theLevel].put((float) theZ);
	}

	public void addTextureCoords(final int theLevel, final CCVector3 theTextureCoords) {
		addTextureCoords(theLevel, theTextureCoords.x, theTextureCoords.y, theTextureCoords.z);
	}

	public void addTextureCoords(final int theLevel, final double theX, final double theY, final double theZ,
			final double theW) {
		prepareTextureCoordData(_myNumberOfVertices, theLevel, 4);

		_myTextureCoords[theLevel].put((float) theX);
		_myTextureCoords[theLevel].put((float) theY);
		_myTextureCoords[theLevel].put((float) theZ);
		_myTextureCoords[theLevel].put((float) theW);
	}

	public void addTextureCoords(final int theLevel, final CCVector4 theTextureCoords) {
		addTextureCoords(theLevel, theTextureCoords.x, theTextureCoords.y, theTextureCoords.z, theTextureCoords.w);
	}

	public void textureCoords(final int theLevel, final FloatBuffer theTextureCoords, final int theTextureCoordSize) {
		theTextureCoords.rewind();
		_myNumberOfVertices = theTextureCoords.limit() / theTextureCoordSize;
		_myTextureCoordSize[theLevel] = theTextureCoordSize;
		prepareTextureCoordData(theTextureCoords.limit() / theTextureCoordSize, theLevel, theTextureCoordSize);
		//
		_myTextureCoords[theLevel].rewind();
		_myTextureCoords[theLevel].put(theTextureCoords);
		_myTextureCoords[theLevel].rewind();
	}

	public void textureCoords(final int theLevel, final FloatBuffer theTextureCoords) {
		textureCoords(theLevel, theTextureCoords, 2);
	}

	public void textureCoords(final FloatBuffer theTextureCoords) {
		textureCoords(0, theTextureCoords);
	}

	public void textureCoords(final int theLevel, final List<?> theTextureCoords) {
		if (theTextureCoords.get(0) instanceof CCVector2) {
			prepareTextureCoordData(theTextureCoords.size(), theLevel, 2);
			_myTextureCoords[theLevel].rewind();
			for (Object myObject : theTextureCoords) {
				CCVector2 myTextureCoords = (CCVector2) myObject;
				_myTextureCoords[theLevel].put((float) myTextureCoords.x);
				_myTextureCoords[theLevel].put((float) myTextureCoords.y);
			}
		} else if (theTextureCoords.get(0) instanceof CCVector4) {
			prepareTextureCoordData(theTextureCoords.size(), theLevel, 4);
			_myTextureCoords[theLevel].rewind();
			for (Object myObject : theTextureCoords) {
				CCVector4 myTextureCoords = (CCVector4) myObject;
				_myTextureCoords[theLevel].put((float) myTextureCoords.x);
				_myTextureCoords[theLevel].put((float) myTextureCoords.y);
				_myTextureCoords[theLevel].put((float) myTextureCoords.z);
				_myTextureCoords[theLevel].put((float) myTextureCoords.w);
			}
		} else if (theTextureCoords.get(0) instanceof CCVector3) {
			prepareTextureCoordData(theTextureCoords.size(), theLevel, 3);
			_myTextureCoords[theLevel].rewind();
			for (Object myObject : theTextureCoords) {
				CCVector3 myTextureCoords = (CCVector3) myObject;
				_myTextureCoords[theLevel].put((float) myTextureCoords.x);
				_myTextureCoords[theLevel].put((float) myTextureCoords.y);
				_myTextureCoords[theLevel].put((float) myTextureCoords.z);
			}
		}

		_myTextureCoords[theLevel].rewind();
	}

	public void textureCoords(final List<?> theTextureCoords) {
		textureCoords(0, theTextureCoords);
	}

	public FloatBuffer textureCoords(int theLevel) {
		return _myTextureCoords[theLevel];
	}

	//////////////////////////////////////////////////////
	//
	// METHODS TO ADD COLOR DATA
	//
	//////////////////////////////////////////////////////

	public void prepareColorData(int theNumberOfVertices) {
		_myNumberOfVertices = theNumberOfVertices;
		if (_myColors == null || _myColors.limit() / 4 != _myNumberOfVertices) {
			_myNumberOfVertices = theNumberOfVertices;
			_myColors = CCBufferUtil.newDirectFloatBuffer(_myNumberOfVertices * 4);
		}
	}

	public void addColor(final double theRed, final double theGreen, final double theBlue, final double theAlpha) {
		prepareColorData(_myNumberOfVertices);
		_myColors.put((float) theRed);
		_myColors.put((float) theGreen);
		_myColors.put((float) theBlue);
		_myColors.put((float) theAlpha);
	}

	public void addColor(final CCColor theColor) {
		addColor(theColor.r, theColor.g, theColor.b, theColor.a);
	}

	public void addColor(final double theRed, final double theGreen, final double theBlue) {
		addColor(theRed, theGreen, theBlue, 1f);
	}

	public void addColor(final double theGray, final double theAlpha) {
		addColor(theGray, theGray, theGray, theAlpha);
	}

	public void addColor(final double theGray) {
		addColor(theGray, theGray, theGray, 1f);
	}

	public void colors(final List<CCColor> theColors) {
		prepareColorData(theColors.size());
		_myColors.rewind();

		for (CCColor myColor : theColors) {
			_myColors.put((float) myColor.r);
			_myColors.put((float) myColor.g);
			_myColors.put((float) myColor.b);
			_myColors.put((float) myColor.a);
		}
		_myColors.rewind();
	}

	public void colors(final FloatBuffer theColors) {
		prepareColorData(theColors.limit() / 4);
		_myColors.rewind();
		_myColors.put(theColors);
		_myColors.rewind();
	}

	public void indices(final List<Integer> theIndices) {
		if (theIndices.size() == 0)
			return;
		_myNumberOfIndices = theIndices.size();
		_myIndices = CCBufferUtil.newIntBuffer(theIndices.size());
		for (int myIndex : theIndices) {
			_myIndices.put(myIndex);
		}
		_myIndices.rewind();
	}

	public void indices(final int[] theIndices) {
		indices(IntBuffer.wrap(theIndices));
	}

	public void indices(final IntBuffer theIndices) {
		_myNumberOfIndices = theIndices.capacity();
		if (theIndices.hasArray()) {
			_myIndices = theIndices;
		} else {
			_myIndices = CCBufferUtil.newIntBuffer(theIndices.capacity());
			theIndices.rewind();
			_myIndices.put(theIndices);
		}
		_myIndices.rewind();
	}

	public void noIndices() {
		_myIndices = null;
	}

	public IntBuffer indices() {
		return _myIndices;
	}

	public int numberOfVertices() {
		return _myNumberOfVertices;
	}

	//////////////////////////////////////////////////////
	//
	// METHODS TO RESET THE MESH
	//
	//////////////////////////////////////////////////////

	public void clearVertices() {
		_myVertices = null;
	}

	public void clearTextureCoords() {
		for (int i = 0; i < _myTextureCoords.length; i++) {
			_myTextureCoords[i] = null;
		}
	}

	public void clearNormals() {
		_myNormals = null;
	}

	public void clearColors() {
		_myColors = null;
	}

	public void clearIndices() {
		_myIndices = null;
	}

	public void clearAll() {
		clearVertices();
		clearTextureCoords();
		clearNormals();
		clearColors();
		clearIndices();
	}

	public void drawMode(CCDrawMode theDrawMode) {
		_myDrawMode = theDrawMode;
	}

	public void enable(CCGraphics g) {
		// Enable Pointers
		if (_myVertices != null) {
			_myVertices.rewind();
			g.gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			g.gl.glVertexPointer(_myVertexSize, GL2.GL_FLOAT, 0, _myVertices);
		}
		if (_myNormals != null) {
			_myNormals.rewind();
			g.gl.getGL2GL3().glEnableClientState(GL2.GL_NORMAL_ARRAY);
			g.gl.glNormalPointer(GL2.GL_FLOAT, 0, _myNormals);
		}
		for (int i = 0; i < _myTextureCoords.length; i++) {
			if (_myTextureCoords[i] != null) {
				_myTextureCoords[i].rewind();
				g.gl.glClientActiveTexture(GL.GL_TEXTURE0 + i);
				g.gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
				g.gl.glTexCoordPointer(_myTextureCoordSize[i], GL2.GL_FLOAT, 0, _myTextureCoords[i]);
			}
		}
		if (_myColors != null) {
			_myColors.rewind();
			g.gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			g.gl.glColorPointer(4, GL2.GL_FLOAT, 0, _myColors);
		}

		// if(_myDrawMode == CCGraphics.POINTS && g._myDrawTexture &&
		// _myTextureCoords != null){
		// g.gl.glEnable(GL.GL_POINT_SPRITE);
		// g.gl.glTexEnvi(GL.GL_POINT_SPRITE, GL.GL_COORD_REPLACE, GL.GL_TRUE);
		// }
	}

	public void disable(CCGraphics g) {
		// if(_myDrawMode == CCGraphics.POINTS && g._myDrawTexture){
		// g.gl.glDisable(GL.GL_POINT_SPRITE);
		// }

		// Disable Pointers
		if (_myVertices != null) {
			g.gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		}
		if (_myNormals != null) {
			g.gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
		}
		for (int i = 0; i < _myTextureCoords.length; i++) {
			if (_myTextureCoords[i] != null) {
				_myTextureCoords[i].rewind();
				g.gl.glClientActiveTexture(GL.GL_TEXTURE0 + i);
				g.gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
			}
		}
		g.gl.glClientActiveTexture(GL.GL_TEXTURE0);
		if (_myColors != null) {
			g.gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
		}
	}

	public void drawArray(CCGraphics g) {
		// Draw All Of The Triangles At Once
		if (_myIndices == null) {
			g.gl.getGL2GL3().glDrawArrays(_myDrawMode.glID, 0, _myNumberOfVertices);
		} else {
			g.gl.glDrawElements(_myDrawMode.glID, _myNumberOfIndices, GL.GL_UNSIGNED_INT, _myIndices);
		}
	}

	public void draw(CCGraphics g) {
		enable(g);
		drawArray(g);
		disable(g);
	}

	private CCVector4 tangent(CCVector3 normal, CCVector3 tangent, CCVector3 bitangent) {
		double myDot = normal.dot(tangent);
		// Gram-Schmidt orthogonalize
		// tangent[a] = (t - n * Dot(n, t)).Normalize();
		CCVector3 myTangent = tangent.subtract(normal.multiply(myDot)).normalizeLocal();

		// Calculate handedness
		// tangent[a].w = (Dot(Cross(n, t), tan2[a]) < 0.0F) ? -1.0F : 1.0F;
		CCVector3 myCross = normal.cross(tangent);
		double myHandedness = myCross.dot(bitangent) < 0 ? -1 : 1;

		return new CCVector4(myTangent, myHandedness);
	}

	private CCVector3[] calculateTangents(int theID0, int theID1, int theID2, int theTextureSource) {
		int myVertexIndex0 = theID0 * _myVertexSize;
		CCVector3 v1 = new CCVector3(_myVertices.get(myVertexIndex0 + 0), _myVertices.get(myVertexIndex0 + 1), _myVertices.get(myVertexIndex0 + 2));
		int myVertexIndex1 = theID1 * _myVertexSize;
		CCVector3 v2 = new CCVector3(_myVertices.get(myVertexIndex1 + 0), _myVertices.get(myVertexIndex1 + 1), _myVertices.get(myVertexIndex1 + 2));
		int myVertexIndex2 = theID2 * _myVertexSize;
		CCVector3 v3 = new CCVector3(_myVertices.get(myVertexIndex2 + 0), _myVertices.get(myVertexIndex2 + 1), _myVertices.get(myVertexIndex2 + 2));

		int myTexIndex0 = theID0 * _myTextureCoordSize[theTextureSource];
		CCVector2 w1 = new CCVector2(_myTextureCoords[theTextureSource].get(myTexIndex0 + 0), _myTextureCoords[theTextureSource].get(myTexIndex0 + 1));
		int myTexIndex1 = theID1 * _myTextureCoordSize[theTextureSource];
		CCVector2 w2 = new CCVector2(_myTextureCoords[theTextureSource].get(myTexIndex1 + 0), _myTextureCoords[theTextureSource].get(myTexIndex1 + 1));
		int myTexIndex2 = theID2 * _myTextureCoordSize[theTextureSource];
		CCVector2 w3 = new CCVector2(_myTextureCoords[theTextureSource].get(myTexIndex2 + 0), _myTextureCoords[theTextureSource].get(myTexIndex2 + 1));

		double x1 = v2.x - v1.x;
		double y1 = v2.y - v1.y;
		double z1 = v2.z - v1.z;

		double x2 = v3.x - v1.x;
		double y2 = v3.y - v1.y;
		double z2 = v3.z - v1.z;

		double s1 = w2.x - w1.x;
		double t1 = w2.y - w1.y;

		double s2 = w3.x - w1.x;
		double t2 = w3.y - w1.y;

		double r = 1.0F / (s1 * t2 - s2 * t1);

		return new CCVector3[] {
				new CCVector3((t2 * x1 - t1 * x2) * r, (t2 * y1 - t1 * y2) * r, (t2 * z1 - t1 * z2) * r),
				new CCVector3((s1 * x2 - s2 * x1) * r, (s1 * y2 - s2 * y1) * r, (s1 * z2 - s2 * z1) * r) };
	}

	public void generateTangents(int theTextureSource, int theTextureTarget) {

		if (_myDrawMode != CCDrawMode.TRIANGLES)
			return;
		if (_myVertexSize != 3)
			return;

		prepareTextureCoordData(theTextureTarget, 4);

		if (_myIndices == null) {
			for (int i = 0; i < _myNumberOfVertices; i += 3) {

				int myNormalIndex0 = i * 3;
				CCVector3 n1 = new CCVector3(_myNormals.get(myNormalIndex0 + 0), _myNormals.get(myNormalIndex0 + 1),
						_myNormals.get(myNormalIndex0 + 2));
				int myNormalIndex1 = (i + 1) * 3;
				CCVector3 n2 = new CCVector3(_myNormals.get(myNormalIndex1 + 0), _myNormals.get(myNormalIndex1 + 1),
						_myNormals.get(myNormalIndex1 + 2));
				int myNormalIndex2 = (i + 2) * 3;
				CCVector3 n3 = new CCVector3(_myNormals.get(myNormalIndex2 + 0), _myNormals.get(myNormalIndex2 + 1),
						_myNormals.get(myNormalIndex2 + 2));

				CCVector3[] myTangents = calculateTangents(i, i + 1, i + 2, theTextureSource);
				CCVector3 myTangent = myTangents[0];
				CCVector3 myBiTangent = myTangents[1];

				CCVector4 myTangent1 = tangent(n1, myTangent, myBiTangent);
				CCVector4 myTangent2 = tangent(n2, myTangent, myBiTangent);
				CCVector4 myTangent3 = tangent(n3, myTangent, myBiTangent);

				addTextureCoords(theTextureTarget, myTangent1);
				addTextureCoords(theTextureTarget, myTangent2);
				addTextureCoords(theTextureTarget, myTangent3);
			}
		} else {
			CCVector3[] myTangents = new CCVector3[_myNumberOfVertices];
			CCVector3[] myBiTangents = new CCVector3[_myNumberOfVertices];
			for (int i = 0; i < _myNumberOfIndices; i += 3) {
				int i1 = _myIndices.get(i);
				int i2 = _myIndices.get(i + 1);
				int i3 = _myIndices.get(i + 2);

				CCVector3[] myTriTangents = calculateTangents(i1, i2, i3, theTextureSource);
				myTangents[i1] = myTriTangents[0];
				myTangents[i2] = myTriTangents[0];
				myTangents[i3] = myTriTangents[0];

				myBiTangents[i1] = myTriTangents[1];
				myBiTangents[i2] = myTriTangents[1];
				myBiTangents[i3] = myTriTangents[1];
			}

			for (int i = 0; i < _myNumberOfVertices; i++) {
				CCVector3 myNormal = new CCVector3(_myNormals.get(i * 3 + 0), _myNormals.get(i * 3 + 1),
						_myNormals.get(i * 3 + 2));
				addTextureCoords(theTextureTarget, tangent(myNormal, myTangents[i], myBiTangents[i]));
			}
		}
	}
}
