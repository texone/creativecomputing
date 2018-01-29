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

import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallback;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * To keep OpenGL as fast as possible, all geometric primitives must be convex.
 * However, many times we have vertex data for a concave or more complex shape 
 * that we want to render with OpenGL. These shapes fall into two basic categories:
 * concave polygons and a more complex polygons with holes in it. To break down
 * more complex shapes into multiple triangles you can use the tessellator.
 * @author christian riekoff
 *
 */
public class CCTesselator implements GLUtessellatorCallback{
	
	protected final GLUtessellator _myTesselator;
	
	protected double[] _myVertexData = new double[14];
	
	protected final static int VERTEX_X = 0;
	protected final static int VERTEX_Y = 1;
	protected final static int VERTEX_Z = 2;

	protected final static int NORMAL_X = 3;
	protected final static int NORMAL_Y = 4;
	protected final static int NORMAL_Z = 5;

	protected final static int COLOR_R = 6;
	protected final static int COLOR_G = 7;
	protected final static int COLOR_B = 8;
	protected final static int COLOR_A = 9;

	protected final static int TEXTURE_S = 10;
	protected final static int TEXTURE_T = 11;
	protected final static int TEXTURE_R = 12;
	protected final static int TEXTURE_Q = 13;
	
	protected boolean _myHasColorData = false;
	protected boolean _myHasNormalData = false;
	protected boolean _myHasTextureData = false;
	
	public CCTesselator(){
		_myTesselator = GLU.gluNewTess();
		_myTesselator.gluTessCallback(GLU.GLU_TESS_BEGIN, this);
		_myTesselator.gluTessCallback(GLU.GLU_TESS_COMBINE, this);
		_myTesselator.gluTessCallback(GLU.GLU_TESS_EDGE_FLAG, this);
		_myTesselator.gluTessCallback(GLU.GLU_TESS_END, this);
		_myTesselator.gluTessCallback(GLU.GLU_TESS_ERROR, this);
		_myTesselator.gluTessCallback(GLU.GLU_TESS_VERTEX, this);
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//
	// IMPLEMENTATION OF THE TESSELATOR FUNCTIONS
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	@Override
	protected void finalize() throws Throwable {
		_myTesselator.gluDeleteTess();
		super.finalize();
	}



	/**
	 * Specifies a function that is called to begin a GL_TRIANGLES, GL_TRIANGLE_STRIP, or GL_TRIANGLE_FAN primitive. 
	 * The function must accept a single GLenum parameter that specifies the primitive to be rendered and is usually set to glBegin.
	 */
	@Override
	public void begin(final int theMode) {
		GL11.glBegin(theMode);
	}

	/**
	 * Like GLU_TESS_BEGIN, specifies a function that is called to begin a GL_TRIANGLES, GL_TRIANGLE_STRIP, or GL_TRIANGLE_FAN primitive. 
	 * The function must accept a Glenum parameter that specifies the primitive to be rendered and a reference
	 * from the call to gluTessBeginPolygon.
	 */
	@Override
	public void beginData(final int theMode, final Object theUserData) {
	}

	/**
	 * Specifies a function that is called when [GLU 1.2] vertices in the polygon are coincident; that is, they are equal.
	 */
	/**
     * Implementation of the GLU_TESS_COMBINE callback.
     * @param theCoords is the 3-vector of the new vertex
     * @param theInputData is the vertex data to be combined, up to four elements.
     * This is useful when mixing colors together or any other
     * user data that was passed in to gluTessVertex.
     * @param theWeights is an array of weights, one for each element of "data"
     * that should be linearly combined for new values.
     * @param theOutputData is the set of new values of "data" after being
     * put back together based on the weights. it's passed back as a
     * single element Object[] array because that's the closest
     * that Java gets to a pointer.
     */
	@Override
	public void combine(
		final double[] theCoords, final Object[] theInputData,
		final float[] theWeights, final Object[] theOutputData
	) {
		double[] myVertexData = new double[_myVertexData.length];
		myVertexData[0] = theCoords[0];
		myVertexData[1] = theCoords[1];
		myVertexData[2] = theCoords[2];
		
		// not gonna bother doing any combining,
		// since no user data is being passed in.
		
		// combine the normal data
		if(_myHasNormalData) {
			for(int i = NORMAL_X; i < NORMAL_X + 3;i++) {
				myVertexData[i] =
					theWeights[0] * ((double[]) theInputData[0])[i] +
					theWeights[1] * ((double[]) theInputData[1])[i] +
					(theInputData[2] == null ? 0 : theWeights[2] * ((double[]) theInputData[2])[i])+
					(theInputData[3] == null ? 0 : theWeights[3] * ((double[]) theInputData[3])[i]);
			}
		}
		
		// combine the color data
		if(_myHasColorData) {
			for(int i = COLOR_R; i < COLOR_R + 4;i++) {
				myVertexData[i] =1;
//					theWeights[0] * ((double[]) theInputData[0])[i] +
//					theWeights[1] * ((double[]) theInputData[1])[i] +
//					(theInputData[2] == null ? 0 : theWeights[2] * ((double[]) theInputData[2])[i]) +
//					(theInputData[3] == null ? 0 : theWeights[3] * ((double[]) theInputData[3])[i]);
			}
		}
		
		// combine the texture data
		if(_myHasTextureData) {
			for(int i = TEXTURE_S; i < TEXTURE_S + 4;i++) {
				myVertexData[i] =
					theWeights[0] * ((double[]) theInputData[0])[i] +
					theWeights[1] * ((double[]) theInputData[1])[i] +
					(theInputData[2] == null ? 0 : theWeights[2] * ((double[]) theInputData[2])[i]) +
					(theInputData[3] == null ? 0 : theWeights[3] * ((double[]) theInputData[3])[i]);
			}
		}
		
		theOutputData[0] = myVertexData;
	}

	/**
	 * Like GLU_TESS_COMBINE, specifies a function [GLU 1.2] that is called when vertices in the polygon are coincident. 
	 * The function also receives a pointer to the user data from gluTessBeginPolygon.
	 */
	@Override
	public void combineData(
		final double[] theCoords, final Object[] theInputData,
		final float[] theWeight, final Object[] theOutputData, final Object theUserData
	) {
	}

	/**
	 * Specifies a function that marks whether succeeding GLU_TESS_VERTEX callbacks refer to 
	 * original or generated vertices. The function must accept a single boolean argument that 
	 * is true for original and false for generated vertices.
	 */
	@Override
	public void edgeFlag(final boolean theArg0) {
	}

	/**
	 * Specifies a function similar to the GLU_TESS_EDGE_FLAG, with the exception that a void pointer to user data is also accepted.
	 */
	@Override
	public void edgeFlagData(boolean theArg0, final Object theData) {
	}

	/**
	 * Specifies a function that marks the end of a drawing primitive, usually glEnd. It takes no arguments.
	 */
	@Override
	public void end() {
		GL11.glEnd();
	}

	/**
	 * Specifies a function similar to GLU_TESS_END, with the addition of a void pointer to user data.
	 */
	@Override
	public void endData(final Object theData) {
	}

	/**
	 * Specifies a function that is called when an error occurs. It must take a single argument of type GLenum.
	 */
	@Override
	public void error(final int theErrorNumber) {
//	      String estring = glu.gluErrorString(theErrorNumber);
//	      throw new RuntimeException("ERROR:"+theErrorNumber+":"+estring);
	}

	@Override
	public void errorData(final int theErrorNumber, final Object theUserData) {
	}

	/**
	 * Specifies a function that is called before every vertex is sent, usually with glVertex3dv. 
	 * The function receives a copy of the third argument to gluTessVertex.
	 */
	@Override
	public void vertex(final Object theVertexData) {
		if (theVertexData instanceof double[]) {
			double[] d = (double[]) theVertexData;
			if(_myHasNormalData)GL11.glNormal3d(d[NORMAL_X],d[NORMAL_Y],d[NORMAL_Z]);
			if(_myHasColorData) {
				GL11.glColor4d(d[COLOR_R],d[COLOR_G],d[COLOR_B],d[COLOR_A]);
			}
			if(_myHasTextureData)GL11.glTexCoord4d(d[TEXTURE_S],d[TEXTURE_T],d[TEXTURE_R],d[TEXTURE_Q]);
			GL11.glVertex3d(d[VERTEX_X], d[VERTEX_Y], d[VERTEX_Z]);
			

		} else {
			throw new RuntimeException("TessCallback vertex() data not understood");
		}
	}

	/**
	 * Like GLU_TESS_VERTEX, specifies a function [GLU 1.2] that is called
	 * before every vertex is sent. The function also receives a copy of the
	 * second argument to gluTessBeginPolygon.
	 */
	@Override
	public void vertexData(final Object theVertexData, final Object theUserData) {
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//
	// DRAWING METHODS OF THE TESSELATOR
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Starts tessellation of a complex polygon.
	 */
	public void beginPolygon(){
		_myTesselator.gluTessBeginPolygon(null);
	}
	
	/**
	 * Ends tessellation of a complex polygon and renders it.
	 */
	public void endPolygon(){
		_myTesselator.gluEndPolygon();
	}
	
	/**
	 * Specifies a new contour or hole in a complex polygon.
	 */
	public void beginContour(){
		_myTesselator.gluTessBeginContour();
	}
	
	/**
	 * Ends a contour in a complex polygon.
	 */
	public void endContour(){
		_myTesselator.gluTessEndContour();
	}
	
	/**
	 * Adds a vertex to the current polygon path.
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void vertex(final double theX, final double theY, final double theZ){
		_myVertexData[VERTEX_X] = theX;
		_myVertexData[VERTEX_Y] = theY;
		_myVertexData[VERTEX_Z] = theZ;
		
		double[] myCopy = Arrays.copyOf(_myVertexData, _myVertexData.length);
		
		_myTesselator.gluTessVertex(myCopy, 0, myCopy);
	}
	
	public void vertex(final double theX, final double theY){
		vertex(theX, theY,0);
	}
	
	public void vertex(final CCVector2 theVertex){
		vertex(theVertex.x, theVertex.y);
	}
	
	public void vertex(final CCVector3 theVertex){
		vertex(theVertex.x, theVertex.y, theVertex.z);
	}
	
	public void normal(final double theX, final double theY, final double theZ) {
		_myHasNormalData = true;
		_myVertexData[NORMAL_X] = theX;
		_myVertexData[NORMAL_Y] = theY;
		_myVertexData[NORMAL_Z] = theZ;
	}
	
	public void normal(final CCVector3 theNormal) {
		_myHasNormalData = true;
		_myVertexData[NORMAL_X] = theNormal.x;
		_myVertexData[NORMAL_Y] = theNormal.y;
		_myVertexData[NORMAL_Z] = theNormal.z;
	}
	
	public void textureCoords(final double theS, final double theT, final double theR, final double theQ) {
		_myHasTextureData = true;
		_myVertexData[TEXTURE_S] = theS;
		_myVertexData[TEXTURE_T] = theT;
		_myVertexData[TEXTURE_R] = theR;
		_myVertexData[TEXTURE_Q] = theQ;
	}
	
	public void textureCoords(final double theS, final double theT, final double theR) {
		textureCoords(theS, theT, theR, 0.0);
	}
	
	public void textureCoords(final double theS, final double theT) {
		textureCoords(theS, theT, 0.0, 0.0);
	}
	
	public void textureCoords(final CCVector2 theCoords) {
		textureCoords(theCoords.x, theCoords.y, 0.0, 0.0);
	}
	
	public void color(final float theRed, final float theGreen, final float theBlue, final float theAlpha) {
		_myHasColorData = true;
		_myVertexData[COLOR_R] = theRed;
		_myVertexData[COLOR_G] = theGreen;
		_myVertexData[COLOR_B] = theBlue;
		_myVertexData[COLOR_A] = theAlpha;
	}
	
	/**
	 * For boundaryOnly, the value can be true or false. If true, only the boundary of the polygon is displayed (no holes).
	 * @param theBoundaryOnly
	 */
	public void boundaryOnly(final boolean theBoundaryOnly){
		if(theBoundaryOnly){
			_myTesselator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, GL11.GL_TRUE);
		}else{
			_myTesselator.gluTessProperty(GLU.GLU_TESS_BOUNDARY_ONLY, GL11.GL_FALSE);
		}
	}
	
	/**
	 * Sets the coordinate tolerance for vertices in the polygon.
	 * @param theTolerance
	 */
	public void tolerance(final float theTolerance){
		_myTesselator.gluTessProperty(GLU.GLU_TESS_TOLERANCE, theTolerance);
	}
	
	public void reset() {
		_myHasColorData = false;
		_myHasNormalData = false;
		_myHasTextureData = false;
	}
}
