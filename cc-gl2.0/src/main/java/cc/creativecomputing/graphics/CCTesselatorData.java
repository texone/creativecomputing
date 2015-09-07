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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;


public class CCTesselatorData<ObjectType> extends CCTesselator{
	
	public CCTesselatorData(final CCGraphics theGraphics){
		super();
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	//
	// IMPLEMENTATION OF THE TESSELATOR FUNCTIONS
	//
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////

	/**
	 * Like GLU_TESS_BEGIN, specifies a function that is called to begin a GL_TRIANGLES, GL_TRIANGLE_STRIP, or GL_TRIANGLE_FAN primitive. 
	 * The function must accept a Glenum parameter that specifies the primitive to be rendered and a reference
	 * from the call to gluTessBeginPolygon.
	 */
	@SuppressWarnings("unchecked")
	public void beginData(final int theMode, final Object theUserData) {
		begin(theMode,(ObjectType)theUserData);
	}
	
	public void begin(final int theMode, final ObjectType theUserData){
		
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
		combine(theCoords, theInputData, theWeight, theOutputData);
		
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
		gl.glEnd();
	}

	/**
	 * Specifies a function similar to GLU_TESS_END, with the addition of a void pointer to user data.
	 */
	public void endData(final Object theData) {
		end();
	}

	/**
	 * Specifies a function that is called when an error occurs. It must take a single argument of type GLenum.
	 */
	public void error(final int theErrorNumber) {
//	      String estring = glu.gluErrorString(theErrorNumber);
	      //throw new RuntimeException();
//	      throw new RuntimeException("Tessellation Error: " + estring);
	}

	public void errorData(final int theErrorNumber, final Object theUserData) {
		error(theErrorNumber);
	}

	/**
	 * Specifies a function that is called before every vertex is sent, usually with glVertex3dv. 
	 * The function receives a copy of the third argument to gluTessVertex.
	 */
	public void vertex(final Object theVertexData) {
	      if (theVertexData instanceof double[]) {
	        double[] d = (double[]) theVertexData;
	        if (d.length != 3) {
	          throw new RuntimeException("TessCallback vertex() data " +
	                                     "isn't length 3");
	        }
	        gl.glVertex3dv(d, 0);
	      } else {
	        throw new RuntimeException("TessCallback vertex() data not understood");
	      }
	    }

	/**
	 * Like GLU_TESS_VERTEX, specifies a function [GLU 1.2] that is called before every vertex is sent. 
	 * The function also receives a copy of the second argument to gluTessBeginPolygon.
	 */
	public void vertexData(final Object theVertexData, final Object theUserData) {
		vertex(theVertexData);
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
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_COMBINE, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_EDGE_FLAG, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_END, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_ERROR, this);
	    GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_VERTEX, this);
	    GLU.gluTessBeginPolygon(_myTesselator, null);
	}
	
	/**
	 * Ends tessellation of a complex polygon and renders it.
	 */
	public void endPolygon(){
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_BEGIN_DATA, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_COMBINE_DATA, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_EDGE_FLAG_DATA, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_END_DATA, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_ERROR_DATA, this);
		GLU.gluTessCallback(_myTesselator, GLU.GLU_TESS_VERTEX_DATA, this);
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
	
	/**
	 * Adds a vertex to the current polygon path.
	 * @param theX
	 * @param theY
	 * @param theZ
	 */
	public void vertex(final float theX, final float theY, final float theZ){
		double[] myVertex = new double[3];
		myVertex[0] = theX;
		myVertex[1] = theY;
		myVertex[2] = theZ;
		
		GLU.gluTessVertex(_myTesselator, myVertex, 0, myVertex);
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
	
	
}
