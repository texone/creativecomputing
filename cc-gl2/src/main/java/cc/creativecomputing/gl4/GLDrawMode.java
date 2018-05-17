package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL32.GL_LINES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_LINE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import static org.lwjgl.opengl.GL32.GL_TRIANGLE_STRIP_ADJACENCY;
import static org.lwjgl.opengl.GL40.GL_PATCHES;


/**
 * TODO docs
 * @author christianr
 *
 */
public enum GLDrawMode {
	
	
	
	POINTS(GL_POINTS),
	
	LINES(GL_LINES),
	LINE_LOOP(GL_LINE_LOOP),
	LINE_STRIP(GL_LINE_STRIP),
	LINE_STRIP_ADJACENCY(GL_LINE_STRIP_ADJACENCY),
	LINES_ADJACENCY(GL_LINES_ADJACENCY),
	
	TRIANGLES(GL_TRIANGLES),
	TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL_TRIANGLE_FAN),
	TRIANGLE_STRIP_ADJACENCY(GL_TRIANGLE_STRIP_ADJACENCY),
	TRIANGLES_ADJACENCY(GL_TRIANGLES_ADJACENCY),
	PATCHES(GL_PATCHES);

	private int glID;
	
	GLDrawMode(int theGLID){
		glID = theGLID;
	}
	
	public int glID(){
		return glID;
	}
	
	/**
     * @param indexMode
     * @param theSize
     * @return the number of primitives you would have if you connected an array of points of the given size using the
     *         given index mode.
     */
    public static int getPrimitiveCount(final GLDrawMode theDrawMode, final int theSize) {
        switch (theDrawMode) {
            case TRIANGLES:
                return theSize / 3;
            case TRIANGLE_FAN:
            case TRIANGLE_STRIP:
                return theSize - 2;
            case LINES:
                return theSize / 2;
            case LINE_STRIP:
                return theSize - 1;
            case LINE_LOOP:
                return theSize;
            case POINTS:
                return theSize;
            default:
                	throw new IllegalArgumentException("unimplemented draw mode: " + theDrawMode);
        }

        
    }
    
    public int getVertexCount() {
        switch (this) {
            case TRIANGLES:
            case TRIANGLE_STRIP:
            case TRIANGLE_FAN:
                return 3;
            case LINES:
            case LINE_STRIP:
            case LINE_LOOP:
                return 2;
            case POINTS:
                return 1;
                default:
                	throw new IllegalArgumentException("Unhandled type: " + this);
        }
        
    }
}
