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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallback;



public class CCTesselatorMesh implements GLUtessellatorCallback{
	
	protected final GLUtessellator _myTesselator;
	
	protected final GL gl;
	
	protected List<CCVector3> _myVertices = new ArrayList<CCVector3>();
	protected List<CCVector2> _myTextureCoords = new ArrayList<CCVector2>();
	protected List<CCVector3> _myNormals = new ArrayList<CCVector3>();
	
	protected CCMesh _myMesh;
	
	public CCTesselatorMesh(){
		gl = CCGraphics.currentGL();
		_myTesselator = GLU.gluNewTess();
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_BEGIN, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_COMBINE, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_EDGE_FLAG, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_END, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_ERROR, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_VERTEX, this);
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
		GLU.gluDeleteTess(_myTesselator);
		super.finalize();
	}
	
	/**
	 * Specifies a function that is called to begin a GL_TRIANGLES, GL_TRIANGLE_STRIP, or GL_TRIANGLE_FAN primitive. 
	 * The function must accept a single GLenum parameter that specifies the primitive to be rendered and is usually set to glBegin.
	 */
	public void begin(final int theMode) {
	}

	/**
	 * Like GLU_TESS_BEGIN, specifies a function that is called to begin a GL_TRIANGLES, GL_TRIANGLE_STRIP, or GL_TRIANGLE_FAN primitive. 
	 * The function must accept a Glenum parameter that specifies the primitive to be rendered and a reference
	 * from the call to gluTessBeginPolygon.
	 */
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
     * @param theWeight is an array of weights, one for each element of "data"
     * that should be linearly combined for new values.
     * @param theOutputData is the set of new values of "data" after being
     * put back together based on the weights. it's passed back as a
     * single element Object[] array because that's the closest
     * that Java gets to a pointer.
     */
	public void combine(
		final double[] theCoords, final Object[] theInputData,
		final float[] theWeight, final Object[] theOutputData
	) {
		
		double[] vertex = new double[theCoords.length];
		vertex[0] = theCoords[0];
		vertex[1] = theCoords[1];
		vertex[2] = theCoords[2];
		
		// this is just 3, so nothing interesting to bother combining
		
		// not gonna bother doing any combining,
		// since no user data is being passed in.
		/*
		for (int i = 3; i < 6; i++) {
		vertex[i] =
		weight[0] * ((double[]) data[0])[i] +
		weight[1] * ((double[]) data[1])[i] +
		weight[2] * ((double[]) data[2])[i] +
		weight[3] * ((double[]) data[3])[i];
		}
		*/
		theOutputData[0] = vertex;
	}

	/**
	 * Like GLU_TESS_COMBINE, specifies a function [GLU 1.2] that is called when vertices in the polygon are coincident. 
	 * The function also receives a pointer to the user data from gluTessBeginPolygon.
	 */
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
	public void edgeFlag(final boolean theArg0) {
	}

	/**
	 * Specifies a function similar to the GLU_TESS_EDGE_FLAG, with the exception that a void pointer to user data is also accepted.
	 */
	public void edgeFlagData(boolean theArg0, final Object theData) {
	}

	/**
	 * Specifies a function that marks the end of a drawing primitive, usually glEnd. It takes no arguments.
	 */
	public void end() {
	}

	/**
	 * Specifies a function similar to GLU_TESS_END, with the addition of a void pointer to user data.
	 */
	public void endData(final Object theData) {
	}

	/**
	 * Specifies a function that is called when an error occurs. It must take a single argument of type GLenum.
	 */
	public void error(final int theErrorNumber) {
//	      String estring = GLU.gluErrorString(theErrorNumber);
	      //throw new RuntimeException();
//	      throw new RuntimeException("Tessellation Error: " + estring);
	}

	public void errorData(final int theErrorNumber, final Object theUserData) {
	}

	/**
	 * Specifies a function that is called before every vertex is sent, usually with glVertex3dv. 
	 * The function receives a copy of the third argument to gluTessVertex.
	 */
	public void vertex(final Object theVertexData) {
		if (theVertexData instanceof double[]) {
			double[] d = (double[]) theVertexData;
			if (d.length >= 3) {
				_myVertices.add(new CCVector3((float)d[0],(float)d[1],(float)d[2]));
			}
			if (d.length == 5) {
				_myTextureCoords.add(new CCVector2((float)d[3],(float)d[4]));
			}
			if (d.length == 6) {
				_myNormals.add(new CCVector3((float)d[3],(float)d[4],(float)d[5]));
			}
			if (d.length == 8) {
				_myNormals.add(new CCVector3((float)d[3],(float)d[4],(float)d[5]));
				_myTextureCoords.add(new CCVector2((float)d[6],(float)d[7]));
			}
		} else {
			throw new RuntimeException("TessCallback vertex() data not understood");
		}
	}

	/**
	 * Like GLU_TESS_VERTEX, specifies a function [GLU 1.2] that is called
	 * before every vertex is sent. The function also receives a copy of the
	 * second argument to gluTessBeginPolygon.
	 */
	public void vertexData(final Object theVertexData, final Object theUserData) {
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//
	// DRAWING METHODS OF THE TESSELATOR
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	public void beginRecord(){
		
	}
	
	public void endRecord(){
		_myMesh = new CCMesh(CCDrawMode.TRIANGLES);
		_myMesh.vertices(_myVertices);
	}
	
	public CCMesh mesh(){
		return _myMesh;
	}
	
	public List<CCVector3> vertices(){
		return _myVertices;
	}
	
	/**
	 * Starts tessellation of a complex polygon.
	 */
	public void beginPolygon(){
		GLU.gluTessBeginPolygon(_myTesselator, null);
	}
	
	/**
	 * Ends tessellation of a complex polygon and renders it.
	 */
	public void endPolygon(){
		GLU.gluEndPolygon(_myTesselator);
	}
	
	/**
	 * Specifies a new contour or hole in a complex polygon.
	 */
	public void beginContour(){
		GLU.gluTessBeginContour(_myTesselator);
	}
	
	/**
	 * Ends a contour in a complex polygon.
	 */
	public void endContour(){
		GLU.gluTessEndContour(_myTesselator);
	}
	
	public void vertex(final double theX, final double theY){
		vertex(theX, theY,0);
	}
	
	/**
	 * Adds a vertex to the current polygon path.
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void vertex(final double theX, final double theY, final double theZ){
		double[] myVertex = new double[3];
		myVertex[0] = theX;
		myVertex[1] = theY;
		myVertex[2] = theZ;
		
		GLU.gluTessVertex(_myTesselator, myVertex, 0, myVertex);
	}
	
	public void vertex(
		final double theX, final double theY, 
		final double theTX, final double theTY	
	){
		vertex(theX, theY, 0, theTX, theTY);
	}
	
	public void vertex(
		final double theX, final double theY, final double theZ,
		final double theTX, final double theTY	
	){
		double[] myVertex = new double[5];
		myVertex[0] = theX;
		myVertex[1] = theY;
		myVertex[2] = theZ;
		myVertex[3] = theTX;
		myVertex[4] = theTY;
		
		GLU.gluTessVertex(_myTesselator, myVertex, 0, myVertex);
	}
	
	public void vertex(
		final double theX, final double theY, final double theZ,
		final double theNX, final double theNY, final double theNZ	
	){
		double[] myVertex = new double[6];
		myVertex[0] = theX;
		myVertex[1] = theY;
		myVertex[2] = theZ;
		myVertex[3] = theNX;
		myVertex[4] = theNY;
		myVertex[5] = theNZ;
		
		GLU.gluTessVertex(_myTesselator, myVertex, 0, myVertex);
	}
	
	public void vertex(
		final double theX, final double theY, final double theZ,
		final double theNX, final double theNY, final double theNZ,	
		final double theTX, final double theTY	
	){
		double[] myVertex = new double[8];
		myVertex[0] = theX;
		myVertex[1] = theY;
		myVertex[2] = theZ;
		myVertex[3] = theNX;
		myVertex[4] = theNY;
		myVertex[5] = theNZ;
		myVertex[6] = theTX;
		myVertex[7] = theTY;
		
		GLU.gluTessVertex(_myTesselator, myVertex, 0, myVertex);
	}
	
	public void vertex(final CCVector2 theVertex){
		vertex(theVertex.x, theVertex.y);
	}
	
	public void vertex(final CCVector3 theVertex){
		vertex(theVertex.x, theVertex.y, theVertex.z);
	}
	
	public void vertex(final CCVector2 theVertex,final CCVector2 theTextureCoords){
		vertex(
			theVertex.x, theVertex.y, 
			theTextureCoords.x, theTextureCoords.y
		);
	}
	
	public void vertex(final CCVector3 theVertex,final CCVector2 theTextureCoords){
		vertex(
			theVertex.x, theVertex.y, theVertex.z, 
			theTextureCoords.x, theTextureCoords.y
		);
	}
	
	public void vertex(final CCVector3 theVertex,final CCVector3 theNormal){
		vertex(
			theVertex.x, theVertex.y, theVertex.z, 
			theNormal.x, theNormal.y,theNormal.z
		);
	}
	
	public void vertex(final CCVector3 theVertex,final CCVector3 theNormal,final CCVector2 theTextureCoords){
		vertex(
			theVertex.x, theVertex.y, theVertex.z, 
			theNormal.x, theNormal.y,theNormal.z, 
			theTextureCoords.x, theTextureCoords.y
		);
	}
	
	/**
	 * For boundaryOnly, the value can be true or false. If true, only the boundary of the polygon is displayed (no holes).
	 * @param theBoundaryOnly
	 */
	public void boundaryOnly(final boolean theBoundaryOnly){
		if(theBoundaryOnly){
			GLU.gluTessProperty(_myTesselator, GLU.GLU_TESS_BOUNDARY_ONLY, GL.GL_TRUE);
		}else{
			GLU.gluTessProperty(_myTesselator, GLU.GLU_TESS_BOUNDARY_ONLY, GL.GL_FALSE);
		}
	}
	
	/**
	 * Sets the coordinate tolerance for vertices in the polygon.
	 * @param theTolerance
	 */
	public void tolerance(final float theTolerance){
		GLU.gluTessProperty(_myTesselator, GLU.GLU_TESS_TOLERANCE, theTolerance);
	}
	
	public void draw(CCGraphics g){
		_myMesh.draw(g);
	}
	
}
