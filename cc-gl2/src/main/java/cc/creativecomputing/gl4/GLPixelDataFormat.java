package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_BLUE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_GREEN;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_INDEX;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL12.GL_BGRA;
import static org.lwjgl.opengl.GL30.GL_BGRA_INTEGER;
import static org.lwjgl.opengl.GL30.GL_BGR_INTEGER;
import static org.lwjgl.opengl.GL30.GL_BLUE_INTEGER;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_GREEN_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RGBA_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RGB_INTEGER;
import static org.lwjgl.opengl.GL30.GL_RG_INTEGER;

import cc.creativecomputing.image.CCPixelFormat;

public enum GLPixelDataFormat {
	DEPTH_COMPONENT(GL_DEPTH_COMPONENT),
	DEPTH_STENCIL(GL_DEPTH_STENCIL),
	STENCIL_INDEX(GL_STENCIL_INDEX),
	RED(GL_RED),
	GREEN(GL_GREEN),
	BLUE(GL_BLUE),
	RG(GL_RG),
	RGB(GL_RGB),
	RGBA(GL_RGBA),
	BGR(GL_BGR),
	BGRA(GL_BGRA),
	RED_INTEGER(GL_RED_INTEGER),
	GREEN_INTEGER(GL_GREEN_INTEGER),
	BLUE_INTEGER(GL_BLUE_INTEGER),
	RG_INTEGER(GL_RG_INTEGER),
	RGB_INTEGER(GL_RGB_INTEGER),
	RGBA_INTEGER(GL_RGBA_INTEGER),
	BGR_INTEGER(GL_BGR_INTEGER),
	BGRA_INTEGER(GL_BGRA_INTEGER);
	
	private int _myGLID;
	
	GLPixelDataFormat(int theGLID){
		_myGLID = theGLID;
	}
	
	public int glID(){
		return _myGLID;
	}
	
	public static GLPixelDataFormat fromCC(CCPixelFormat theFormat){
		switch(theFormat){
		case DEPTH_COMPONENT:return DEPTH_COMPONENT;
		case DEPTH_STENCIL:return DEPTH_STENCIL;
		case STENCIL_INDEX:return STENCIL_INDEX;
		case RED:return RED;
		case RED_INTEGER:return RED_INTEGER;
		case GREEN:return GREEN;
		case GREEN_INTEGER:return GREEN_INTEGER;
		case BLUE:return BLUE;
		case BLUE_INTEGER:return BLUE_INTEGER;
		case RG:return RG;
		case RG_INTEGER:return RG_INTEGER;
		case RGB:return RGB;
		case RGBA:return RGBA;
		case BGR:return BGR;
		case BGR_INTEGER:return BGR_INTEGER;
		case BGRA:return BGRA;
		case BGRA_INTEGER:return BGRA_INTEGER;
		case RGB_INTEGER:return RGB_INTEGER;
		case RGBA_INTEGER:return RGBA_INTEGER;
		}
		return null;
	}
	
	public static GLPixelDataFormat fromGLID(int theGLID){
		switch(theGLID){
		case GL_DEPTH_COMPONENT:return DEPTH_COMPONENT;
		case GL_DEPTH_STENCIL:return DEPTH_STENCIL;
		case GL_STENCIL_INDEX:return STENCIL_INDEX;
		case GL_RED:return RED;
		case GL_GREEN:return GREEN;
		case GL_BLUE:return BLUE;
		case GL_RG:return RG;
		case GL_RGB:return RGB;
		case GL_RGBA:return RGBA;
		case GL_BGR:return BGR;
		case GL_BGRA:return BGRA;
		case GL_RED_INTEGER:return RED_INTEGER;
		case GL_GREEN_INTEGER:return GREEN_INTEGER;
		case GL_BLUE_INTEGER:return BLUE_INTEGER;
		case GL_RG_INTEGER:return RG_INTEGER;
		case GL_RGB_INTEGER:return RGB_INTEGER;
		case GL_RGBA_INTEGER:return RGBA_INTEGER;
		case GL_BGR_INTEGER:return BGR_INTEGER;
		case GL_BGRA_INTEGER:return BGRA_INTEGER;
		}
		return null;
	}
}

