package cc.creativecomputing.gl4;
/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */


import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BACK_LEFT;
import static org.lwjgl.opengl.GL11.GL_BACK_RIGHT;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_COLOR;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DECR;
import static org.lwjgl.opengl.GL11.GL_DEPTH;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT_LEFT;
import static org.lwjgl.opengl.GL11.GL_FRONT_RIGHT;
import static org.lwjgl.opengl.GL11.GL_GEQUAL;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_INCR;
import static org.lwjgl.opengl.GL11.GL_INVERT;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEFT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_NEVER;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_NOTEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_LINE;
import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_POINT;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_RIGHT;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA_SATURATE;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glClearStencil;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glIsTexture;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glPolygonOffset;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_CONSTANT_ALPHA;
import static org.lwjgl.opengl.GL14.GL_CONSTANT_COLOR;
import static org.lwjgl.opengl.GL14.GL_DECR_WRAP;
import static org.lwjgl.opengl.GL14.GL_INCR_WRAP;
import static org.lwjgl.opengl.GL14.GL_ONE_MINUS_CONSTANT_ALPHA;
import static org.lwjgl.opengl.GL14.GL_ONE_MINUS_CONSTANT_COLOR;
import static org.lwjgl.opengl.GL14.glBlendColor;
import static org.lwjgl.opengl.GL15.GL_SRC1_ALPHA;
import static org.lwjgl.opengl.GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.glVertexAttrib4f;
import static org.lwjgl.opengl.GL30.glClearBufferfv;
import static org.lwjgl.opengl.GL30.glClearBufferiv;
import static org.lwjgl.opengl.GL31.GL_PRIMITIVE_RESTART;
import static org.lwjgl.opengl.GL31.glPrimitiveRestartIndex;
import static org.lwjgl.opengl.GL32.GL_FIRST_VERTEX_CONVENTION;
import static org.lwjgl.opengl.GL32.GL_LAST_VERTEX_CONVENTION;
import static org.lwjgl.opengl.GL32.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.opengl.GL32.glProvokingVertex;
import static org.lwjgl.opengl.GL33.GL_ONE_MINUS_SRC1_ALPHA;
import static org.lwjgl.opengl.GL33.GL_ONE_MINUS_SRC1_COLOR;
import static org.lwjgl.opengl.GL33.GL_SRC1_COLOR;
import static org.lwjgl.opengl.GL42.glBindImageTexture;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
public class GLGraphics {
	
	
	
	public int getInteger(int theGLParameter){
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
		    IntBuffer ip = stack.mallocInt(1); 
		    glGetIntegerv(theGLParameter, ip);
		    return ip.get();
		}
	}

	/**
	 * TODO docs
	 * @param thePointSize
	 */
	public void pointSize(float thePointSize){
		glPointSize(thePointSize);
	}
	
	/**
	 * TODO docs
	 */
	public void programPointSize(){
		glEnable(GL_PROGRAM_POINT_SIZE);
	}
	
	/**
	 * TODO docs
	 */
	public void noProgramSize(){
		glDisable(GL_PROGRAM_POINT_SIZE);
	}
	
	/**
	 * TODO docs
	 * @param theLideWidth
	 */
	public void lineWidth(float theLideWidth){
		glLineWidth(theLideWidth);
	}
	
	public enum GLPolygonMode {
		POINT(GL_POINT),
		LINE(GL_LINE),
		FILL(GL_FILL);
		
		private int _myGLID;
		
		GLPolygonMode(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLPolygonMode fromGLID(int theGLID){
			switch(theGLID){
			case GL_POINT:return POINT;
			case GL_LINE:return LINE;
			case GL_FILL:return FILL;
			}
			return null;
		}
	}
	
	public void polygonMode(GLPolygonMode thePolygonMode){
		glPolygonMode(GL_FRONT_AND_BACK, thePolygonMode.glID());
	}
	
	/**
	 * If enabled, and if the polygon is rendered in {@linkplain GLPolygonMode#FILL} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 * @see #polygonOffset(float, float)
	 */
	public void polygonOffsetFill(){
		glEnable(GL_POLYGON_OFFSET_FILL);
	}
	
	/**
	 * @see #polygonOffsetFill()
	 */
	public void noPolygonOffsetFill(){
		glDisable(GL_POLYGON_OFFSET_FILL);
	}
	
	/**
	 * If enabled, and if the polygon is rendered in {@linkplain GLPolygonMode#LINE} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 * @see #polygonOffset(float, float)
	 */
	public void polygonOffsetLine(){
		glEnable(GL_POLYGON_OFFSET_LINE);
	}
	
	/**
	 * @see #polygonOffsetLine()
	 */
	public void noPolygonOffsetLINE(){
		glDisable(GL_POLYGON_OFFSET_LINE);
	}
	
	/**
	 * If enabled, and if the polygon is rendered in {@linkplain GLPolygonMode#POINT} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 * @see #polygonOffset(float, float)
	 */
	public void polygonOffsetPoint(){
		glEnable(GL_POLYGON_OFFSET_POINT);
	}
	
	/**
	 * @see #polygonOffsetPoint()
	 */
	public void noPolygonOffsetPoint(){
		glDisable(GL_POLYGON_OFFSET_POINT);
	}
	
	/**
	 * Set the scale and units used to calculate depth values
	 * <p>
	 * When {@linkplain #polygonOffsetFill()}, {@linkplain #polygonOffsetLine()}, or {@linkplain #polygonOffsetPoint()} is enabled, 
	 * each fragment's depth value will be offset after it is interpolated from the depth values of 
	 * the appropriate vertices. The value of the offset is <code>factor * DZ + r * units</code>, where 
	 * DZ is a measurement of the change in depth relative to the screen area of the polygon, and r is 
	 * the smallest value that is guaranteed to produce a resolvable offset for a given implementation. 
	 * The offset is added before the depth test is performed and before the value is written into the depth buffer.
	 * <p>
	 * polygonOffset is useful for rendering hidden-line images, for applying decals to surfaces, 
	 * and for rendering solids with highlighted edges.
	 * @param theFactor Specifies a scale factor that is used to create a variable depth offset for each polygon. The initial value is 0.
	 * @param theUnits Is multiplied by an implementation-specific value to create a constant depth offset. The initial value is 0.
	 */
	public void polygonOffset(float theFactor, float theUnits){
		glPolygonOffset(theFactor, theUnits);
	}
	
	/**
	 * 
	 * @author christianr
	 *
	 */
	public enum GLFrontFace {
		/**
		 * selects clockwise polygons as front-facing.
		 */
		CLOCK_WISE(GL_CW),
		/**
		 * Passing {@linkplain GLFrontFace#COUNTER_CLOCK_WISE} to mode selects counterclockwise 
		 * polygons as front-facing; 
		 */
		COUNTER_CLOCK_WISE(GL_CCW);

		private int glID;
		
		GLFrontFace(int theGLID){
			glID = theGLID;
		}
		
		public int glID(){
			return glID;
		}
		
		public static GLFrontFace fromGLID(int theGLID){
			switch(theGLID){
			case GL_CW:return CLOCK_WISE;
			case GL_CCW:return COUNTER_CLOCK_WISE;
			}
			return null;
		}
	}
	
	/**
	 * define front- and back-facing polygons. In a scene composed entirely of opaque closed surfaces, 
	 * back-facing polygons are never visible. Eliminating these invisible polygons has the obvious benefit 
	 * of speeding up the rendering of the image. To enable and disable elimination of back-facing polygons, 
	 * call {@linkplain #cullFace(GLCullFace)} and {@linkplain #noCullFace()}.
	 * <p>
	 * The projection of a polygon to window coordinates is said to have clockwise winding if an imaginary 
	 * object following the path from its first vertex, its second vertex, and so on, to its last vertex, 
	 * and finally back to its first vertex, moves in a clockwise direction about the interior of the polygon. 
	 * The polygon's winding is said to be counterclockwise if the imaginary object following the same path 
	 * moves in a counterclockwise direction about the interior of the polygon. frontFace specifies whether 
	 * polygons with clockwise winding in window coordinates, or counterclockwise winding in window coordinates, 
	 * are taken to be front-facing. Passing {@linkplain GLFrontFace#COUNTER_CLOCK_WISE} to mode selects counterclockwise 
	 * polygons as front-facing; {@linkplain GLFrontFace#CLOCK_WISE} selects clockwise polygons as front-facing. 
	 * By default, counterclockwise polygons are taken to be front-facing.
	 * @param theFrontFace
	 */
	public void frontFace(GLFrontFace theFrontFace){
		glFrontFace(theFrontFace.glID());
	}
	
	public enum GLCullFace {
		FRONT(GL_FRONT),
		BACK(GL_BACK),
		FRONT_AND_BACK(GL_FRONT_AND_BACK);
		
		private int _myGLID;
		
		GLCullFace(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLCullFace fromGLID(int theGLID){
			switch(theGLID){
			case GL_FRONT:return FRONT;
			case GL_BACK:return BACK;
			case GL_FRONT_AND_BACK:return FRONT_AND_BACK;
			}
			return null;
		}
	}
	
	public void cullFace(){
		glEnable(GL_CULL_FACE);
	}
	
	public void cullFace(GLCullFace theFace){
		glEnable(GL_CULL_FACE);
		glCullFace(theFace.glID());
	}
	
	public void noCullFace(){
		glDisable(GL_CULL_FACE);
	}
	
	public enum GLProvokingIndex{
		FIRST_VERTEX_CONVENTION(GL_FIRST_VERTEX_CONVENTION),
		LAST_VERTEX_CONVENTION(GL_LAST_VERTEX_CONVENTION);
		
		private int _myGLID;
		
		GLProvokingIndex(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLProvokingIndex fromGLID(int theGLID){
			switch(theGLID){
			case GL_FIRST_VERTEX_CONVENTION:return FIRST_VERTEX_CONVENTION;
			case GL_LAST_VERTEX_CONVENTION:return LAST_VERTEX_CONVENTION;
			}
			return null;
		}
	}
	
	/**
	 * Flatshading a vertex shader varying output means to assign all vertices of the primitive
	 * the same value for that output. The vertex from which these values is derived is known as 
	 * the provoking vertex and <code>provokingVertex</code> specifies which vertex is to be used 
	 * as the source of data for flat shaded varyings.
	 * <p>
	 * provokeMode must be either {@linkplain GLProvokingIndex#FIRST_VERTEX_CONVENTION} or {@linkplain GLProvokingIndex#LAST_VERTEX_CONVENTION}, and 
	 * controls the selection of the vertex whose values are assigned to flatshaded varying outputs.
	 * If a vertex or geometry shader is active, user-defined varying outputs may be flatshaded by 
	 * using the flat qualifier when declaring the output.
	 * @param theProvokingIndex Specifies the vertex to be used as the source of data for flat shaded varyings.
	 */
	public void provokingVertex(GLProvokingIndex theProvokingIndex){
		glProvokingVertex(theProvokingIndex.glID());
	}
	
	// TODO add documentation
	public void primitiveRestart(int thePrimitiveRestart){
		glEnable(GL_PRIMITIVE_RESTART);
		glPrimitiveRestartIndex(thePrimitiveRestart);
	}
	
	// TODO add documentation
	public void primitiveRestart(){
		glEnable(GL_PRIMITIVE_RESTART);
	}
	
	// TODO add documentation
	public void noPrimitiveRestart(){
		glDisable(GL_PRIMITIVE_RESTART);
	}
	
	public enum GLColorBuffer {
		NONE(GL_NONE),
		COLOR(GL_COLOR),
		FRONT_LEFT(GL_FRONT_LEFT),
		FRONT_RIGHT(GL_FRONT_RIGHT),
		BACK_LEFT(GL_BACK_LEFT),
		BACK_RIGHT(GL_BACK_RIGHT),
		FRONT(GL_FRONT),
		BACK(GL_BACK),
		LEFT(GL_LEFT),
		RIGHT(GL_RIGHT),
		FRONT_AND_BACK(GL_FRONT_AND_BACK),
		DEPTH(GL_DEPTH);
		
		private int _myGLID;
		
		GLColorBuffer(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLColorBuffer fromGLID(int theGLID){
			switch(theGLID){
			case GL_NONE:return NONE;
			case GL_COLOR:return COLOR;
			case GL_FRONT_LEFT:return FRONT_LEFT;
			case GL_FRONT_RIGHT:return FRONT_RIGHT;
			case GL_BACK_LEFT:return BACK_LEFT;
			case GL_BACK_RIGHT:return BACK_RIGHT;
			case GL_FRONT:return FRONT;
			case GL_BACK:return BACK;
			case GL_LEFT:return LEFT;
			case GL_RIGHT:return RIGHT;
			case GL_FRONT_AND_BACK:return FRONT_AND_BACK;
			}
			return null;
		}
	}
	
	/**
	 * clearBuffer clears the specified buffer to the specified value(s). If buffer is {@linkplain GLColorBuffer#COLOR}, 
	 * a particular draw buffer GL_DRAW_BUFFERi is specified by passing i as drawBuffer. In this case, value points to a 
	 * four-element vector specifying the R, G, B and A color to clear that draw buffer to. If buffer is one of GL_FRONT, 
	 * GL_BACK, GL_LEFT, GL_RIGHT, or GL_FRONT_AND_BACK, identifying multiple buffers, each selected buffer is cleared 
	 * to the same value. Clamping and conversion for fixed-point color buffers are performed in the same fashion as 
	 * {@linkplain #clearColor(cc.creativecomputing.graphics.CCColor)}.
	 * @param theBuffer
	 * @param theDrawBuffer
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void clearBufferfv(GLColorBuffer theBuffer, int theDrawBuffer, float r, float g, float b, float a){
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			FloatBuffer fp = stack.mallocFloat(4); 
			fp.put(0, r);
			fp.put(1, g);
			fp.put(2, b);
			fp.put(3, a);
			glClearBufferfv(theBuffer.glID(), theDrawBuffer, fp);
		}
	}
	
	public void clearBuffer(GLColorBuffer theBuffer, int theDrawBuffer, CCColor theColor){
		clearBufferfv(theBuffer, theDrawBuffer, (float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a);
	}
	
	public void clearBufferfv(GLColorBuffer theBuffer, float r, float g, float b, float a){
		clearBufferfv(theBuffer, 0, r, g, b, a);
	}

	public void clearBufferiv(GLColorBuffer theBuffer, int theDrawBuffer, int r, int g, int b, int a){
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			IntBuffer fp = stack.mallocInt(4); 
			fp.put(0, r);
			fp.put(1, g);
			fp.put(2, b);
			fp.put(3, a);
			glClearBufferiv(theBuffer.glID(), theDrawBuffer, fp);
		}
	}
	
	public void clearBufferiv(GLColorBuffer theBuffer, int r, int g, int b, int a){
		clearBufferfv(theBuffer, 0, r, g, b, a);
	}
	
	public void clearDepthBuffer(float theDepth){
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			FloatBuffer fp = stack.mallocFloat(1); 
			fp.put(0, theDepth);
			glClearBufferfv(GLColorBuffer.DEPTH.glID(), 0, fp);
		}
	}
	
	/**
	 * colorMask specifies whether the individual color components in the frame buffer 
	 * can or cannot be written. If theMaskRed is false, for example, no change is made to the red
	 * component of any pixel in any of the color buffers, regardless of the drawing operation attempted.
	 * @param theMaskRed
	 * @param theMaskGreen
	 * @param theMaskBlue
	 * @param theMaskAlpha
	 */
	public void colorMask(final boolean theMaskRed, final boolean theMaskGreen, final boolean theMaskBlue, final boolean theMaskAlpha) {
		glColorMask(theMaskRed, theMaskGreen, theMaskBlue, theMaskAlpha);
	}
	
	/**
	 * Disables a previous color mask.
	 */
	public void noColorMask() {
		colorMask(true, true, true, true);
	}

	
	
	public void scissor(final int theX, final int theY, final int theWidth, final int theHeight) {
		glEnable(GL_SCISSOR_TEST);
		glScissor(theX, theY, theWidth, theHeight);
	}
	
	public void noScissor() {
		glDisable(GL_SCISSOR_TEST);
	}
	
	/**
	 * Specifies compare functions.
	 * @author christianriekoff
	 *
	 */
	public enum GLCompareFunction{
		/**
		 * Always fails.
		 */
		NEVER(GL_NEVER),
		/**
		 * Passes if ( ref & mask ) < ( value & mask ).
		 */
		LESS(GL_LESS),
		
		/**
		 * Passes if ( ref & mask ) <= ( value & mask ).
		 */
		LESS_EQUAL(GL_LEQUAL),
		/**
		 * Passes if ( ref & mask ) > ( value & mask ).
		 */
		GREATER(GL_GREATER),
		/**
		 * Passes if ( ref & mask ) >= ( value & mask ).
		 */
		GREATER_EQUAL(GL_GEQUAL),
		/**
		 * Passes if ( ref & mask ) = ( value & mask ).
		 */
		EQUAL(GL_EQUAL),
		/**
		 * Passes if ( ref & mask ) != ( value & mask ).
		 */
		NOT_EQUAL(GL_NOTEQUAL),
		/**
		 * Always passes.
		 */
		ALWAYS(GL_ALWAYS);
		
		
		private final int glID;
		
		GLCompareFunction(final int theGlID){
			glID = theGlID;
		}
		
		public static GLCompareFunction fromGLID(int theGLID){
			switch(theGLID){
			case GL_NEVER:return NEVER;
			case GL_LESS:return LESS;
			case GL_LEQUAL:return LESS_EQUAL;
			case GL_GREATER:return GREATER;
			case GL_GEQUAL:return GREATER_EQUAL;
			case GL_EQUAL:return EQUAL;
			case GL_NOTEQUAL:return NOT_EQUAL;
			case GL_ALWAYS:return ALWAYS;
			}
			return null;
		}
	}
	
	/**
	 * <p>
	 * Specifies the function used to compare each incoming pixel depth 
	 * value with the depth value present in the depth buffer. The comparison 
	 * is performed only if depth testing is enabled.
	 * </p>
	 * <p>
	 * The initial value of func is LESS_EQUAL. Initially, depth testing is disabled. 
	 * If depth testing is disabled or if no depth buffer exists, it is as if the depth test always passes.
	 * @param theCompare Specifies the depth comparison function.
	 */
	public void depthFunc(final GLCompareFunction theCompare){
		glDepthFunc(theCompare.glID);
	}
	
	public void depthTest(){
		glEnable(GL_DEPTH_TEST);
	}
	
	public void noDepthTest(){
		glDisable(GL_DEPTH_TEST);
	}
	
	public void depthMask(){
		glDepthMask(true);
	}

	public void noDepthMask(){
		glDepthMask(false);
	}
	
	public void clearColor(float theR, float theG, float theB, float theA){
		glClearColor(theR, theG, theB, theA);
	}
	
	public void clear(){
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * Clears the depth buffer
	 */
	public void clearDepthBuffer(){
		glClear(GL_DEPTH_BUFFER_BIT);
	}
	
	public void clearDepth(final float theDefaultDepth){
		glClearDepth(theDefaultDepth);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  BLENDING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	public enum GLSourceBlendFunction {
		ZERO(GL_ZERO),
		ONE(GL_ONE),
		DST_COLOR(GL_DST_COLOR),
		ONE_MINUS_DST_COLOR(GL_ONE_MINUS_DST_COLOR),
		SRC_ALPHA(GL_SRC_ALPHA),
		ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA(GL_DST_ALPHA),
		ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
		SRC_ALPHA_SATURATE(GL_SRC_ALPHA_SATURATE),
		CONSTANT_COLOR(GL_CONSTANT_COLOR),
		ONE_MINUS_CONSTANT_COLOR(GL_ONE_MINUS_CONSTANT_COLOR),
		CONSTANT_ALPHA(GL_CONSTANT_ALPHA),
		ONE_MINUS_CONSTANT_ALPHA(GL_ONE_MINUS_CONSTANT_ALPHA);
		
		private int _myGLID;
		
		GLSourceBlendFunction(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLSourceBlendFunction fromGLID(int theGLID){
			switch(theGLID){
			case GL_ZERO:return ZERO;
			case GL_ONE:return ONE;
			case GL_DST_COLOR:return DST_COLOR;
			case GL_ONE_MINUS_DST_COLOR:return ONE_MINUS_DST_COLOR;
			case GL_SRC_ALPHA:return SRC_ALPHA;
			case GL_ONE_MINUS_SRC_ALPHA:return ONE_MINUS_SRC_ALPHA;
			case GL_DST_ALPHA:return DST_ALPHA;
			case GL_ONE_MINUS_DST_ALPHA:return ONE_MINUS_DST_ALPHA;
			case GL_CONSTANT_COLOR:return CONSTANT_COLOR;
			case GL_ONE_MINUS_CONSTANT_COLOR:return ONE_MINUS_CONSTANT_COLOR;
			case GL_CONSTANT_ALPHA:return CONSTANT_ALPHA;
			case GL_ONE_MINUS_CONSTANT_ALPHA:return ONE_MINUS_CONSTANT_ALPHA;
			case GL_SRC_ALPHA_SATURATE:return SRC_ALPHA_SATURATE;
			}
			return null;
		}
	}
	
	public enum GLDestinationBlendFunction {
		ZERO(GL_ZERO),
		ONE(GL_ONE),
		SRC_COLOR(GL_SRC_COLOR),
		SRC1_COLOR(GL_SRC1_COLOR),
		ONE_MINUS_SRC_COLOR(GL_ONE_MINUS_SRC_COLOR),
		SRC_ALPHA(GL_SRC_ALPHA),
		SRC1_ALPHA(GL_SRC1_ALPHA),
		ONE_MINUS_SRC_ALPHA(GL_ONE_MINUS_SRC_ALPHA),
		DST_ALPHA(GL_DST_ALPHA),
		ONE_MINUS_DST_ALPHA(GL_ONE_MINUS_DST_ALPHA),
		CONSTANT_COLOR(GL_CONSTANT_COLOR),
		ONE_MINUS_CONSTANT_COLOR(GL_ONE_MINUS_CONSTANT_COLOR),
		CONSTANT_ALPHA(GL_CONSTANT_ALPHA),
		ONE_MINUS_CONSTANT_ALPHA(GL_ONE_MINUS_CONSTANT_ALPHA),
		ONE_MINUS_SRC1_COLOR(GL_ONE_MINUS_SRC1_COLOR),
		ONE_MINUS_SRC1_ALPHA(GL_ONE_MINUS_SRC1_ALPHA);
		
		private int _myGLID;
		
		GLDestinationBlendFunction(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLDestinationBlendFunction fromGLID(int theGLID){
			switch(theGLID){
			case GL_ZERO:return ZERO;
			case GL_ONE:return ONE;
			case GL_SRC_COLOR:return SRC_COLOR;
			case GL_ONE_MINUS_SRC_COLOR:return ONE_MINUS_SRC_COLOR;
			case GL_SRC_ALPHA:return SRC_ALPHA;
			case GL_ONE_MINUS_SRC_ALPHA:return ONE_MINUS_SRC_ALPHA;
			case GL_DST_ALPHA:return DST_ALPHA;
			case GL_ONE_MINUS_DST_ALPHA:return ONE_MINUS_DST_ALPHA;
			case GL_CONSTANT_COLOR:return CONSTANT_COLOR;
			case GL_ONE_MINUS_CONSTANT_COLOR:return ONE_MINUS_CONSTANT_COLOR;
			case GL_CONSTANT_ALPHA:return CONSTANT_ALPHA;
			case GL_ONE_MINUS_CONSTANT_ALPHA:return ONE_MINUS_CONSTANT_ALPHA;
			case GL_SRC1_COLOR:return SRC1_COLOR;
			case GL_ONE_MINUS_SRC1_COLOR:return ONE_MINUS_SRC1_COLOR;
			case GL_SRC1_ALPHA:return SRC1_ALPHA;
			case GL_ONE_MINUS_SRC1_ALPHA:return ONE_MINUS_SRC1_ALPHA;
			}
			return null;
		}
	}
	
	public void blend(){
		glEnable(GL_BLEND);
	}
	
	public void noBlend(){
		glDisable(GL_BLEND);
	}
	
	public void blendFunc(GLSourceBlendFunction theSourceFunction, GLDestinationBlendFunction theDestinationFunction){
		glBlendFunc(theSourceFunction.glID(), theDestinationFunction.glID());
	}
	
	public void blendColor(CCColor theColor){
		glBlendColor((float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	//  STENCIL OPERATIONS
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * specifies the index used by clearStencil() to clear the stencil buffer. 
	 * s is masked with 2 m - 1 , where m is the number of bits in the stencil buffer.
	 * @param theS Specifies the index used when the stencil buffer is cleared. The initial value is 0.
	 */
	public void clearStencil(int theS){
		glClearStencil(theS);
	}
	
	/**
	 * Clears the stencil buffer.
	 */
	public void clearStencilBuffer(){
		glClear(GL_STENCIL_BUFFER_BIT);
	}
	
	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void stencilTest(){
		glEnable(GL_STENCIL_TEST);
	}

	/**
	 * If enabled, do stencil testing and update the stencil buffer. 
	 */
	public void noStencilTest(){
		glDisable(GL_STENCIL_TEST);
	}
	
	
	
	/**
	 * <p>
	 * Stenciling, like depth-buffering, enables and disables drawing on a per-pixel basis. 
	 * Stencil planes are first drawn into using GL drawing primitives, then geometry and 
	 * images are rendered using the stencil planes to mask out portions of the screen. 
	 * Stenciling is typically used in multipass rendering algorithms to achieve special 
	 * effects, such as decals, outlining, and constructive solid geometry rendering.
	 * </p>
	 * <p>
	 * The stencil test conditionally eliminates a pixel based on the outcome of a comparison 
	 * between the reference value and the value in the stencil buffer. To enable and disable 
	 * the test, call {@linkplain #stencilTest()} and {@linkplain #noStencilTest()}. To specify 
	 * actions based on the outcome of the stencil test, call glStencilOp or glStencilOpSeparate.
	 * </p>
	 * <p>
	 * There can be two separate sets of func, ref, and mask parameters; one affects back-facing 
	 * polygons, and the other affects front-facing polygons as well as other non-polygon primitives. 
	 * glStencilFunc sets both front and back stencil state to the same values. Use glStencilFuncSeparate 
	 * to set front and back stencil state to different values.
	 * </p>
	 * <p>
	 * func is a symbolic constant that determines the stencil comparison function. It accepts 
	 * one of eight values, shown in the following list. ref is an integer reference value that 
	 * is used in the stencil comparison. It is clamped to the range 0 2 n - 1 , where n is the 
	 * number of bitplanes in the stencil buffer. mask is bitwise ANDed with both the reference 
	 * value and the stored stencil value, with the ANDed values participating in the comparison.
	 * </p>
	 * <p>
	 * If stencil represents the value stored in the corresponding stencil buffer location, the 
	 * following list shows the effect of each comparison function that can be specified by func. 
	 * Only if the comparison succeeds is the pixel passed through to the next stage in the 
	 * rasterization process (see glStencilOp). All tests treat stencil values as unsigned 
	 * integers in the range 0 2 n - 1 , where n is the number of bitplanes in the stencil buffer.
	 * </p>
	 * @param theFunc Specifies the test function. Eight symbolic constants are valid: 
	 * @param theRef 
	 * 		Specifies the reference value for the stencil test. ref is clamped to the range 
	 * 		0 2 n - 1 , where n is the number of bitplanes in the stencil buffer. The initial value is 0.
	 * @param theMask
	 * 		Specifies a mask that is ANDed with both the reference value and the stored stencil value when 
	 * 		the test is done. The initial value is all 1's.
	 */
	public void stencilFunc(GLCompareFunction theFunc, int theRef, int theMask){
		glStencilFunc(theFunc.glID, theRef, theMask);
	}
	
	/**
	 * control the front and back writing of individual bits in the stencil planes
	 * <p>
	 * stencilMask controls the writing of individual bits in the stencil planes. The least significant n bits 
	 * of mask, where n is the number of bits in the stencil buffer, specify a mask. Where a 1 appears in the mask, 
	 * it's possible to write to the corresponding bit in the stencil buffer. Where a 0 appears, the corresponding bit 
	 * is write-protected. Initially, all bits are enabled for writing.
	 * <p>
	 * There can be two separate mask writemasks; one affects back-facing polygons, and the other affects front-facing 
	 * polygons as well as other non-polygon primitives. glStencilMask sets both front and back stencil writemasks to 
	 * the same values. Use glStencilMaskSeparate to set front and back stencil writemasks to different values.
	 * @param theMask Specifies a bit mask to enable and disable writing of individual bits in the stencil planes. Initially, the mask is all 1's
	 */
	public void stencilMask(int theMask){
		glStencilMask(theMask);
	}
	
	/**
	 * set front and back stencil test actions
	 * <p>
	 * Stenciling, like depth-buffering, enables and disables drawing on a per-pixel basis. You draw into the stencil 
	 * planes using GL drawing primitives, then render geometry and images, using the stencil planes to mask out portions 
	 * of the screen. Stenciling is typically used in multipass rendering algorithms to achieve special effects, such as decals,
	 * outlining, and constructive solid geometry rendering.
	 * <p>
	 * The stencil test conditionally eliminates a pixel based on the outcome of a comparison between the value in the 
	 * stencil buffer and a reference value. To enable and disable the test, call {@linkplain #stencilTest()} and 
	 * {@linkplain #noStencilTest()}; to control it, call {@linkplain #stencilFunc(GLCompareFunction, int, int)()}.
	 * <p>
	 * There can be two separate sets of sfail, dpfail, and dppass parameters; one affects back-facing polygons, and 
	 * the other affects front-facing polygons as well as other non-polygon primitives. stencilOp sets both front and back 
	 * stencil state to the same values. Use glStencilOpSeparate to set front and back stencil state to different values.
	 * <p>
	 * tencilOp takes three arguments that indicate what happens to the stored stencil value while stenciling is enabled. 
	 * If the stencil test fails, no change is made to the pixel's color or depth buffers, and sfail specifies what 
	 * happens to the stencil buffer contents. The following eight actions are possible.
	 * <ul>
	 * <li>{@linkplain GLStencilOperation#KEEP} Keeps the current value.</li>
	 * <li>{@linkplain GLStencilOperation#ZERO} Sets the stencil buffer value to 0.</li>
	 * <li>{@linkplain GLStencilOperation#REPLACE} Sets the stencil buffer value to ref, as specified by {@linkplain #stencilFunc(GLCompareFunction, int, int)}.</li>
	 * <li>{@linkplain GLStencilOperation#INCR} Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.</li>
	 * <li>{@linkplain GLStencilOperation#INCR_WRAP} Increments the current stencil buffer value. Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.</li>
	 * <li>{@linkplain GLStencilOperation#DECR} Decrements the current stencil buffer value. Clamps to 0.</li>
	 * <li>{@linkplain GLStencilOperation#DECR_WRAP} Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.</li>
	 * <li>{@linkplain GLStencilOperation#INVERT} Bitwise inverts the current stencil buffer value.</li>
	 * </ul>
	 * Stencil buffer values are treated as unsigned integers. When incremented and decremented, values are clamped to 0 and 2n-1, 
	 * where n is the value returned by querying GL_STENCIL_BITS.
	 * <p>
	 * The other two arguments to stencilOp specify stencil buffer actions that depend on whether subsequent depth buffer tests succeed 
	 * (dppass) or fail (dpfail) (see glDepthFunc). The actions are specified using the same eight symbolic constants as sfail. 
	 * Note that dpfail is ignored when there is no depth buffer, or when the depth buffer is not enabled. In these cases, sfail and 
	 * dppass specify stencil action when the stencil test fails and passes, respectively.
	 * <p>
	 * Notes
	 * <p>
	 * Initially the stencil test is disabled. If there is no stencil buffer, no stencil modification can occur and it is as 
	 * if the stencil tests always pass, regardless of any call to glStencilOp.
	 * 
	 * @param theFail Specifies the action to take when the stencil test fails. The initial value is {@linkplain GLStencilOperation#KEEP}.
	 * @param theZFail Specifies the stencil action when the stencil test passes, but the depth test fails. The initial value is {@linkplain GLStencilOperation#KEEP}.
	 * @param theZPass Specifies the stencil action when both the stencil test and the depth test pass, or when the stencil test passes and either 
	 * there is no depth buffer or depth testing is not enabled. The initial value is {@linkplain GLStencilOperation#KEEP}.
	 */
	public void stencilOp(GLStencilOperation theFail, GLStencilOperation theZFail, GLStencilOperation theZPass){
		glStencilOp(theFail.glID, theZFail.glID, theZPass.glID);
	}
	
	/**
	 * Specifies thestencil  test function.
	 * @author christianriekoff
	 *
	 */
	public enum GLStencilOperation{
		/**
		 * Keeps the current value.
		 */
		KEEP(GL_KEEP),
		/**
		 * Sets the stencil buffer value to 0.
		 */
		ZERO(GL_ZERO),
		
		/**
		 * Sets the stencil buffer value to ref, as specified by stencilFunction.
		 */
		REPLACE(GL_REPLACE),
		/**
		 * Increments the current stencil buffer value. Clamps to the maximum representable unsigned value.
		 */
		INCREMENT(GL_INCR),
		/**
		 * Increments the current stencil buffer value. Wraps stencil buffer value to zero when incrementing the maximum representable unsigned value.
		 */
		INCREMENT_WRAP(GL_INCR_WRAP),
		/**
		 * Decrements the current stencil buffer value. Clamps to 0.
		 */
		DECREMENT(GL_DECR),
		/**
		 * Decrements the current stencil buffer value. Wraps stencil buffer value to the maximum representable unsigned value when decrementing a stencil buffer value of zero.
		 */
		DECREMENT_WRAP(GL_DECR_WRAP),
		/**
		 * Bitwise inverts the current stencil buffer value.
		 */
		INVERT(GL_INVERT);
		
		
		private final int glID;
		
		GLStencilOperation(final int theGlID){
			glID = theGlID;
		}
		
		public static GLStencilOperation fromGLID(int theGLID){
			switch(theGLID){
			case GL_KEEP:return KEEP;
			case GL_ZERO:return ZERO;
			case GL_REPLACE:return REPLACE;
			case GL_INCR:return INCREMENT;
			case GL_INCR_WRAP:return INCREMENT_WRAP;
			case GL_DECR:return DECREMENT;
			case GL_DECR_WRAP:return DECREMENT_WRAP;
			case GL_INVERT:return INVERT;
			}
			return null;
		}
	}
	
	public void stencilOperation(
		GLStencilOperation theStencilTestFailOp,
		GLStencilOperation theDepthTestFailOp,
		GLStencilOperation ThePassOp
	){
		glStencilOp(theStencilTestFailOp.glID, theDepthTestFailOp.glID, ThePassOp.glID);
	}
	
	public void stencilOperation(GLStencilOperation theOperation){
		stencilOperation(theOperation,theOperation,theOperation);
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	// 
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	
	
	public void vertexAttrib4f(int theIndex, float theX, float theY, float theZ, float theW){
		glVertexAttrib4f(theIndex, theX, theY, theZ, theW);
	}
	

	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	//
	// TEXTURING
	//
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the maximum number of texture units that might be accessible to all shader stages at anyone time.
	 * @return the maximum number of texture units
	 */
	public int maxCombinedTextureImageUnits(){
		return getInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
	}
	
	/**
	 * Selects which texture unit subsequent texture state calls will affect. 
	 * The number of texture units an implementation supports is implementation dependent, but must be at least 80.
	 * @param theTextureUnit select active texture unit
	 */
	public void activeTexture(int theTextureUnit) {
		glActiveTexture(GL_TEXTURE0 + theTextureUnit);
	}
	
	public boolean isTexture(int theTextureUnit){
		return glIsTexture(GL_TEXTURE0 + theTextureUnit);
	}
	
	public void bindImageTexture(int theUnit, CCTexture2D theTexture, int theLevel, boolean theIsLayered, int theLayer, GLAccesMode theAccesMode, GLPixelDataInternalFormat theFormat){
		glBindImageTexture(theUnit, theTexture.id(), theLevel, theIsLayered, theLayer, theAccesMode.glID(), theFormat.glID);
	}

	public void beginDraw() {
		// TODO Auto-generated method stub
		
	}

	public void endDraw() {
		// TODO Auto-generated method stub
		
	}
}
