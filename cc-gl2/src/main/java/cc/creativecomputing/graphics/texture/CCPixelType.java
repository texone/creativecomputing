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
package cc.creativecomputing.graphics.texture;

import static org.lwjgl.opengl.GL11.GL_BITMAP;
import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_BYTE_2_3_3_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_BYTE_3_3_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_10_10_10_2;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_5_5_1;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_6_5;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_SHORT_5_6_5_REV;
import static org.lwjgl.opengl.GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_10F_11F_11F_REV;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_5_9_9_9_REV;
import static org.lwjgl.opengl.GL41.GL_FIXED;

/**
 * The type parameter interprets the data pointed to by the *pixels parameter. 
 * It tells cc what data type within the buffer is used to store the color components.
 * @author christian riekoff
 *
 */
public enum CCPixelType{
	/**
	 * Each color component is an 8-bit unsigned integer
	 */
	UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1),
	/**
	 * Signed 8-bit integer
	 */
	BYTE(GL_BYTE, 1),
	/**
	 * Single bits, no color data; same as glBitmap
	 */
	BITMAP(GL_BITMAP),
	/**
	 * Unsigned 16-bit integer
	 */
	UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2),
	/**
	 * Signed 16-bit integer
	 */
	SHORT(GL_SHORT, 2),
	/**
	 * Unsigned 32-bit integer
	 */
	UNSIGNED_INT(GL_UNSIGNED_INT, 4),
	/**
	 * Signed 32-bit integer
	 */
	INT(GL_INT, 4),
	/**
	 * Signed 32-bit integer
	 */
	FIXED(GL_FIXED),
	/**
	 * Single-precision float
	 */
	FLOAT(GL_FLOAT, 4),
	/**
	 * Single-precision float
	 */
	HALF_FLOAT(GL_HALF_FLOAT, 4),
	/**
	 * Single-precision float
	 */
	DOUBLE(GL_DOUBLE, 8),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_BYTE_3_2_2(GL_UNSIGNED_BYTE_3_3_2),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_BYTE_2_3_3_REV(GL_UNSIGNED_BYTE_2_3_3_REV),
	
	UNSIGNED_BYTE_3_3_2(GL_UNSIGNED_BYTE_3_3_2),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_SHORT_5_6_5(GL_UNSIGNED_SHORT_5_6_5),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_SHORT_5_6_5_REV(GL_UNSIGNED_SHORT_5_6_5_REV),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_4_4_4_4(GL_UNSIGNED_SHORT_4_4_4_4),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_4_4_4_4_REV(GL_UNSIGNED_SHORT_4_4_4_4_REV),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_5_5_5_1(GL_UNSIGNED_SHORT_5_5_5_1),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_1_5_5_5_REV(GL_UNSIGNED_SHORT_1_5_5_5_REV),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_8_8_8_8(GL_UNSIGNED_INT_8_8_8_8),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_8_8_8_8_REV(GL_UNSIGNED_INT_8_8_8_8_REV),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_10_10_10_2(GL_UNSIGNED_INT_10_10_10_2),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_2_10_10_10_REV(GL_UNSIGNED_INT_2_10_10_10_REV),
	
	UNSIGNED_INT_10F_11F_11F_REV(GL_UNSIGNED_INT_10F_11F_11F_REV),
	
	UNSIGNED_INT_5_9_9_9_REV(GL_UNSIGNED_INT_5_9_9_9_REV),
	
	FLOAT_32_UNSIGNED_INT_24_8_REV(GL_FLOAT_32_UNSIGNED_INT_24_8_REV),
	
	UNSIGNED_INT_24_8(GL_UNSIGNED_INT_24_8);
	
	public final int glID;
	public final int bytesPerChannel;
	
	CCPixelType(final int theGLid, final int theBytesPerChannel){
		glID = theGLid;
		bytesPerChannel = theBytesPerChannel;
	}
	
	CCPixelType(final int theGLid){
		this(theGLid, -1);
	}
}
