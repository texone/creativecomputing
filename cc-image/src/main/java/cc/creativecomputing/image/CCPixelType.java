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
package cc.creativecomputing.image;

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
	UNSIGNED_BYTE(1),
	/**
	 * Signed 8-bit integer
	 */
	BYTE(1),
	/**
	 * Single bits, no color data; same as glBitmap
	 */
	BITMAP(),
	/**
	 * Unsigned 16-bit integer
	 */
	UNSIGNED_SHORT(2),
	/**
	 * Signed 16-bit integer
	 */
	SHORT(2),
	/**
	 * Unsigned 32-bit integer
	 */
	UNSIGNED_INT(4),
	/**
	 * Signed 32-bit integer
	 */
	INT(4),
	/**
	 * Signed 32-bit integer
	 */
	FIXED(),
	/**
	 * Single-precision float
	 */
	FLOAT(4),
	/**
	 * Single-precision float
	 */
	HALF_FLOAT(2),
	/**
	 * Single-precision float
	 */
	DOUBLE(8),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_BYTE_2_3_3_REV(),

	UNSIGNED_BYTE_3_3_2,
	/**
	 * Packed RGB values
	 */
	UNSIGNED_SHORT_5_6_5(),
	/**
	 * Packed RGB values
	 */
	UNSIGNED_SHORT_5_6_5_REV(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_4_4_4_4(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_4_4_4_4_REV(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_5_5_5_1(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_SHORT_1_5_5_5_REV(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_8_8_8_8(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_8_8_8_8_REV(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_10_10_10_2(),
	/**
	 * Packed RGBA values
	 */
	UNSIGNED_INT_2_10_10_10_REV(),
	UNSIGNED_INT_24_8,
	UNSIGNED_INT_10F_11F_11F_REV,
	UNSIGNED_INT_5_9_9_9_REV,
	FLOAT_32_UNSIGNED_INT_24_8_REV;
	
	public final int bytesPerChannel;
	
	CCPixelType(final int theBytesPerChannel){
		bytesPerChannel = theBytesPerChannel;
	}
	
	CCPixelType(){
		this(-1);
	}
}
