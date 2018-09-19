package cc.creativecomputing.gl4;


import cc.creativecomputing.image.CCPixelType;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL46.*;

public enum GLPixelDataType {
	UNSIGNED_BYTE(GL_UNSIGNED_BYTE),
	BYTE(GL_BYTE),
	BITMAP(GL_BITMAP),
	UNSIGNED_SHORT(GL_UNSIGNED_SHORT),
	SHORT(GL_SHORT),
	UNSIGNED_INT(GL_UNSIGNED_INT),
	INT(GL_INT),
	FIXED(GL_FIXED),
	HALF_FLOAT(GL_HALF_FLOAT),
	FLOAT(GL_FLOAT),
	DOUBLE(GL_DOUBLE),
	UNSIGNED_BYTE_3_3_2(GL_UNSIGNED_BYTE_3_3_2),
	UNSIGNED_BYTE_2_3_3_REV(GL_UNSIGNED_BYTE_2_3_3_REV),
	UNSIGNED_SHORT_5_6_5(GL_UNSIGNED_SHORT_5_6_5),
	UNSIGNED_SHORT_5_6_5_REV(GL_UNSIGNED_SHORT_5_6_5_REV),
	UNSIGNED_SHORT_4_4_4_4(GL_UNSIGNED_SHORT_4_4_4_4),
	UNSIGNED_SHORT_4_4_4_4_REV(GL_UNSIGNED_SHORT_4_4_4_4_REV),
	UNSIGNED_SHORT_5_5_5_1(GL_UNSIGNED_SHORT_5_5_5_1),
	UNSIGNED_SHORT_1_5_5_5_REV(GL_UNSIGNED_SHORT_1_5_5_5_REV),
	UNSIGNED_INT_8_8_8_8(GL_UNSIGNED_INT_8_8_8_8),
	UNSIGNED_INT_8_8_8_8_REV(GL_UNSIGNED_INT_8_8_8_8_REV),
	UNSIGNED_INT_10_10_10_2(GL_UNSIGNED_INT_10_10_10_2),
	UNSIGNED_INT_2_10_10_10_REV(GL_UNSIGNED_INT_2_10_10_10_REV),
	UNSIGNED_INT_24_8(GL_UNSIGNED_INT_24_8),
	UNSIGNED_INT_10F_11F_11F_REV(GL_UNSIGNED_INT_10F_11F_11F_REV),
	UNSIGNED_INT_5_9_9_9_REV(GL_UNSIGNED_INT_5_9_9_9_REV),
	FLOAT_32_UNSIGNED_INT_24_8_REV(GL_FLOAT_32_UNSIGNED_INT_24_8_REV);
	
	public final int glID;
	
	GLPixelDataType(int theGLID){
		glID = theGLID;
	}
	
	public static GLPixelDataType fromCC(CCPixelType theType){
		switch(theType){
		case UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case BYTE:return BYTE;
		case UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case SHORT:return SHORT;
		case UNSIGNED_INT:return UNSIGNED_INT;
		case INT:return INT;
		case HALF_FLOAT:return HALF_FLOAT;
		case FLOAT:return FLOAT;
		case UNSIGNED_BYTE_3_3_2:return UNSIGNED_BYTE_3_3_2;
		case UNSIGNED_BYTE_2_3_3_REV:return UNSIGNED_BYTE_2_3_3_REV;
		case UNSIGNED_SHORT_5_6_5:return UNSIGNED_SHORT_5_6_5;
		case UNSIGNED_SHORT_5_6_5_REV:return UNSIGNED_SHORT_5_6_5_REV;
		case UNSIGNED_SHORT_4_4_4_4:return UNSIGNED_SHORT_4_4_4_4;
		case UNSIGNED_SHORT_4_4_4_4_REV:return UNSIGNED_SHORT_4_4_4_4_REV;
		case UNSIGNED_SHORT_5_5_5_1:return UNSIGNED_SHORT_5_5_5_1;
		case UNSIGNED_SHORT_1_5_5_5_REV:return UNSIGNED_SHORT_1_5_5_5_REV;
		case UNSIGNED_INT_8_8_8_8:return UNSIGNED_INT_8_8_8_8;
		case UNSIGNED_INT_8_8_8_8_REV:return UNSIGNED_INT_8_8_8_8_REV;
		case UNSIGNED_INT_10_10_10_2:return UNSIGNED_INT_10_10_10_2;
		case UNSIGNED_INT_2_10_10_10_REV:return UNSIGNED_INT_2_10_10_10_REV;
		case UNSIGNED_INT_24_8:return UNSIGNED_INT_24_8;
		case UNSIGNED_INT_10F_11F_11F_REV:return UNSIGNED_INT_10F_11F_11F_REV;
		case UNSIGNED_INT_5_9_9_9_REV:return UNSIGNED_INT_5_9_9_9_REV;
		case FLOAT_32_UNSIGNED_INT_24_8_REV:return FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		return null;
	}
	
	public static GLPixelDataType fromGLID(int theGLID){
		switch(theGLID){
		case GL_UNSIGNED_BYTE:return UNSIGNED_BYTE;
		case GL_BYTE:return BYTE;
		case GL_UNSIGNED_SHORT:return UNSIGNED_SHORT;
		case GL_SHORT:return SHORT;
		case GL_UNSIGNED_INT:return UNSIGNED_INT;
		case GL_INT:return INT;
		case GL_HALF_FLOAT:return HALF_FLOAT;
		case GL_FLOAT:return FLOAT;
		case GL_UNSIGNED_BYTE_3_3_2:return UNSIGNED_BYTE_3_3_2;
		case GL_UNSIGNED_BYTE_2_3_3_REV:return UNSIGNED_BYTE_2_3_3_REV;
		case GL_UNSIGNED_SHORT_5_6_5:return UNSIGNED_SHORT_5_6_5;
		case GL_UNSIGNED_SHORT_5_6_5_REV:return UNSIGNED_SHORT_5_6_5_REV;
		case GL_UNSIGNED_SHORT_4_4_4_4:return UNSIGNED_SHORT_4_4_4_4;
		case GL_UNSIGNED_SHORT_4_4_4_4_REV:return UNSIGNED_SHORT_4_4_4_4_REV;
		case GL_UNSIGNED_SHORT_5_5_5_1:return UNSIGNED_SHORT_5_5_5_1;
		case GL_UNSIGNED_SHORT_1_5_5_5_REV:return UNSIGNED_SHORT_1_5_5_5_REV;
		case GL_UNSIGNED_INT_8_8_8_8:return UNSIGNED_INT_8_8_8_8;
		case GL_UNSIGNED_INT_8_8_8_8_REV:return UNSIGNED_INT_8_8_8_8_REV;
		case GL_UNSIGNED_INT_10_10_10_2:return UNSIGNED_INT_10_10_10_2;
		case GL_UNSIGNED_INT_2_10_10_10_REV:return UNSIGNED_INT_2_10_10_10_REV;
		case GL_UNSIGNED_INT_24_8:return UNSIGNED_INT_24_8;
		case GL_UNSIGNED_INT_10F_11F_11F_REV:return UNSIGNED_INT_10F_11F_11F_REV;
		case GL_UNSIGNED_INT_5_9_9_9_REV:return UNSIGNED_INT_5_9_9_9_REV;
		case GL_FLOAT_32_UNSIGNED_INT_24_8_REV:return FLOAT_32_UNSIGNED_INT_24_8_REV;
		}
		return null;
	}
}

