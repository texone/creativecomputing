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
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;

public enum CCDrawMode{
	/**
	 * Treats each vertex as a single point. Vertex n defines point n. N points are drawn.
	 */
	POINTS(GL.GL_POINTS),
	/**
	 * Treats each pair of vertices as an independent line segment. 
	 * Vertices 2n-1 and 2n define line n. N/2 lines are drawn.
	 */
	LINES(GL.GL_LINES),
	LINES_ADJACENCY(GL2GL3.GL_LINES_ADJACENCY_EXT),
	/**
	 * Draws a connected group of line segments from the first vertex to the last. 
	 * Vertices n and n+1 define line n. N-1 lines drawn.
	 */
	LINE_STRIP(GL.GL_LINE_STRIP),
	LINE_STRIP_ADJACENCY(GL2GL3.GL_LINE_STRIP_ADJACENCY_EXT),
	/**
	 * Draws a connected group of line segments from the first vertex to the last, 
	 * then back to the first. Vertices n and n+1 define line n. 
	 * The last line, however, is defined by vertices N and 1. N lines are drawn.
	 */
	LINE_LOOP(GL.GL_LINE_LOOP),
	/**
	 * Treates each triplet of vertices as an independent triangle. 
	 * Vertices 3n-2, 3n-1, and 3n define triangle n. N/3 triangles are drawn.
	 */
	TRIANGLES(GL.GL_TRIANGLES),
	TRIANGLES_ADJACENCY(GL2GL3.GL_TRIANGLES_ADJACENCY_EXT),
	/**
	 * Draws a connected group of triangles. One triangle is defined for each 
	 * vertex presented after the first two vertices. 
	 * For odd n, vertices n, n+1, and n+2 define triangle n. 
	 * For even n, vertices n+1, n, and n+2 define triangle n. N-2 triangles are drawn.
	 */
	TRIANGLE_STRIP(GL.GL_TRIANGLE_STRIP),
	TRIANGLE_STRIP_ADJACENCY(GL2GL3.GL_TRIANGLE_STRIP_ADJACENCY_EXT),
	/**
	 * Draws a connected group of triangles. One triangle is defined for each 
	 * vertex presented after the first two vertices. 
	 * For odd n, vertices n, n+1, and n+2 define triangle n. 
	 * For even n, vertices n+1, n, and n+2 define triangle n. N-2 triangles are drawn.
	 */
	TRIANGLE_FAN(GL2ES2.GL_TRIANGLE_FAN),
	/**
	 * Treats each group of four vertices as an independent quadrilateral. 
	 * Vertices 4n-3, 4n-2, 4n-1, and 4n define quadrilateral n. N/4 quadrilaterals are drawn.
	 */
	QUADS(GL2.GL_QUADS),
	/**
	 * Draws a connected group of quadrilaterals. One quadrilateral is defined for 
	 * each pair of vertices presented after the first pair. Vertices 2n-1, 2n, 2n+2, 
	 * and 2n+1 define quadrilateral n. N/2-1 quadrilaterals are drawn. 
	 * Note that the order in which vertices are used to construct a quadrilateral 
	 * from strip data is different from that used with independent data.
	 */
	QUAD_STRIP(GL2.GL_QUAD_STRIP),
	/**
	 * Draws a single, convex polygon. Vertices 1 through N define this polygon.
	 */
	POLYGON(GL2.GL_POLYGON);
	
	public int glID;
	
	CCDrawMode(final int theGlID){
		glID = theGlID;
	}
}
