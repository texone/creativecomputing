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
 * Used to set pixel storage modes
 * @author christianriekoff
 *
 */
public class CCPixelStorageModes {

	/**
	 * If true, byte ordering for multibyte color components, depth 
	 * components, color indices, or stencil indices is reversed. 
	 */
	private boolean _mySwapBytes;
	
	/**
	 * If true, bits are ordered within a byte from least significant to most significant.
	 */
	private boolean _myLSBFirst;
	
	/**
	 * If greater than 0, defines the number of pixels in a row.
	 */
	private int _myRowLength;
	
	/**
	 * Specifies the alignment requirements for the start of each pixel row in memory.
	 */
	private int _myAlignment;
	
	/**
	 * defines the number of pixels in an image three- dimensional texture volume
	 */
	private int _myImageHeight;
	
	/**
	 *  equivalent to incrementing the pointer by in components or indices, 
	 *  where n is the number of components or indices in each pixel
	 */
	private int _mySkipPixels;
	
	/**
	 * equivalent to incrementing the pointer by jm components or indices, 
	 * where m is the number of components or indices per row, as just 
	 * computed in the rowLength section
	 */
	private int _mySkipRows;
	
	/**
	 * equivalent to incrementing the pointer by kp, where p is the number of 
	 * components or indices per image, as computed in the iageHeight section.
	 */
	private int _mySkipImages;
	
	public CCPixelStorageModes() {
		_mySwapBytes = false;
		_myLSBFirst = false;
		
		_myRowLength = 0;
		_myAlignment = 4;
		_myImageHeight = 0;
		
		_mySkipPixels = 0;
		_mySkipRows = 0;
		_mySkipImages = 0;
	}
	
	/**
	 * If <code>true</code>, byte ordering for multibyte color components, depth components, 
	 * color indices, or stencil indices is reversed. That is, if a four-byte 
	 * component consists of bytes b0, b1, b2, b3, it is stored in or read from 
	 * memory as b3, b2, b1, b0 if swapBytes is true. swapBytes has no effect on 
	 * the memory order of components within a pixel, only on the order of bytes 
	 * within components or indices. For example, the three components of a RGB 
	 * format pixel are always stored with red first, green second, and blue third, 
	 * regardless of the value of swapBytes. The default value is <code>false</code>.
	 * @return true if use reversed byte ordering otherwise false
	 */
	public boolean swapBytes() {
		return _mySwapBytes;
	}
	
	/**
	 * If true, byte ordering for multibyte color components, depth components, 
	 * color indices, or stencil indices is reversed.
	 * @see #swapBytes()
	 * @param theSwapBytes true if use reversed byte ordering otherwise false
	 */
	public void swapBytes(final boolean theSwapBytes) {
		_mySwapBytes = theSwapBytes;
	}
	
	/**
	 * If <code>true</code>, bits are ordered within a byte from least significant to most 
	 * significant; otherwise, the first bit in each byte is the most significant one. 
	 * This parameter is significant for bitmap data only. The default value
	 * is <code>false</code>
	 * @return <code>true</code> if bits are ordered within a byte from least significant to most 
	 * significant otherwise <code>false</code>
	 */
	public boolean isLSBFirst() {
		return _myLSBFirst;
	}
	
	/**
	 * If true, bits are ordered within a byte from least significant to most 
	 * significant.
	 * @see #isLSBFirst()
	 * @param theLSBFirst true if bits are ordered within a byte from least significant to most 
	 * significant otherwise false
	 */
	public void isLSBFirst(final boolean theLSBFirst) {
		_myLSBFirst = theLSBFirst;
	}
	
	/**
	 * <p>
	 * If greater than 0, rowLength defines the number of pixels in a row. 
	 * If the first pixel of a row is placed at location p in memory, then 
	 * the location of the first pixel of the next row is obtained by skipping:
	 * <pre>
	 * 	k = n * l 			// if s >= a
	 * 	k = (a / s) * [s * n * l / a] 	// if s < a
	 * </pre>
	 * components or indices, where n is the number of components or indices 
	 * in a pixel, l is the number of pixels in a row (rowLength if it is greater 
	 * than 0, the width argument to the pixel routine otherwise), a is the value 
	 * of alignment, and s is the size, in bytes, of a single component 
	 * (if a < s, then it is as if a = s). 
	 * </p>
	 * <p>
	 * In the case of 1-bit values, the location of the next row is obtained by skipping:
	 * <pre>
	 * 	k = 8a * [n * l / 8a] 
	 * </pre>
	 * components or indices.The word component in this description refers to the 
	 * nonindex values red, green, blue, alpha, and depth. Storage format RGB, 
	 * for example, has three components per pixel: first red, then green, and 
	 * finally blue. The default value is 0.
	 * </p>
	 * @return the number of pixels in a row
	 */
	public int rowLength() {
		return _myRowLength;
	}
	
	/**
	 * If greater than 0, rowLength defines the number of pixels in a row. 
	 * @see #rowLength()
	 * @param theRowLength
	 */
	public void rowLength(final int theRowLength) {
		_myRowLength = theRowLength;
	}
	
	/**
	 * Specifies the alignment requirements for the start of each pixel row in memory. 
	 * The allowable values are:
	 * <ul>
	 * <li>1 (byte-alignment)</li> 
	 * <li>2 (rows aligned to even-numbered bytes)</li>
	 * <li>4 (word-alignment)</li>
	 * <li>8 (rows start on double-word boundaries)</li>
	 * </ul>
	 * The default value is 4.
	 * @return alignment requirements for the start of each pixel row in memory
	 */
	public int alignment() {
		return _myAlignment;
	}
	
	/**
	 * Specifies the alignment requirements for the start of each pixel row in memory. 
	 * @see #alignment()
	 * @param theAlignment alignment requirements for the start of each pixel row in memory
	 */
	public void alignment(final int theAlignment) {
		_myAlignment = theAlignment;
	}
	
	/**
	 * <p>
	 * If greater than 0, imageHeight defines the number of pixels in an image three-
	 * dimensional texture volume, where 'image' is defined by all pixels sharing the 
	 * same third dimension index. If the first pixel of a row is placed at location 
	 * p in memory, then the location of the first pixel of the next row is obtained 
	 * by skipping
	 *  <pre>
	 * 	k = n * l * h 				// if s >= a
	 * 	k = (a / s) * [s * n * l *h/ a] 	// if s < a
	 * </pre>
	 * components or indices, where n is the number of components or indices in a pixel, 
	 * l is the number of pixels in a row (rowLength if it is greater than 0, the width 
	 * argument to glTexImage3d otherwise), h is the number of rows in a pixel image 
	 * (imageHeight if it is greater than 0, the height argument to the glTexImage3D 
	 * routine otherwise), a is the value of alignment, and s is the size, in bytes, of 
	 * a single component (if a < s, then it is as if a = s).
	 * </p>
	 * <p>
	 * The word component in this description refers to the nonindex values red, green, 
	 * blue, alpha, and depth. Storage format RGB, for example, has three components 
	 * per pixel: first red, then green, and finally blue. The default value is 0.
	 * </p>
	 * @return the number of pixels in an image three-dimensional texture volume
	 */
	public int imageHeight() {
		return _myImageHeight;
	}
	
	/**
	 * If greater than 0, imageHeight defines the number of pixels in an image three-
	 * dimensional texture volume, where 'image' is defined by all pixels sharing the 
	 * same third dimension index. 
	 * @see #imageHeight()
	 * @param theImageHeight the number of pixels in an image three-dimensional texture volume
	 */
	public void imageHeight(final int theImageHeight) {
		_myImageHeight = theImageHeight;
	}

	/**
	 * This method is provided as a convenience to the programmer; it provides no 
	 * functionality that cannot be duplicated simply by incrementing the pointer 
	 * passed to glReadPixels. Setting skipPixels to <code>i</code> is equivalent to incrementing 
	 * the pointer by <code>i * n</code> components or indices, where <code>n</code> 
	 * is the number of components or indices in each pixel. The default value is 0.
	 * @return the skipPixels
	 */
	public int skipPixels() {
		return _mySkipPixels;
	}

	/**
	 * Setting skipPixels to <code>i</code> is equivalent to incrementing 
	 * the pointer by <code>i * n</code> components or indices, where <code>n</code> 
	 * is the number of components or indices in each pixel. The default value is 0.
	 * @see #skipPixels()
	 * @param skipPixels the skipPixels to set
	 */
	public void skipPixels(int theSkipPixels) {
		_mySkipPixels = theSkipPixels;
	}

	/**
	 * This method is provided as a convenience to the programmer; it provides no 
	 * functionality that cannot be duplicated simply by incrementing the pointer 
	 * passed to glReadPixels. Setting skipRows to <code>j</code> is equivalent to 
	 * incrementing the pointer by <code>j * m</code> components or indices, where 
	 * <code>m</code> is the number of components or indices per row, as just computed 
	 * in the rowLength section. The default value is 0.
	 * @return the skipRows
	 */
	public int skipRows() {
		return _mySkipRows;
	}

	/**
	 * Setting skipRows to <code>j</code> is equivalent to 
	 * incrementing the pointer by <code>j * m</code> components or indices, where 
	 * <code>m</code> is the number of components or indices per row, as just computed 
	 * in the rowLength section. 
	 * @see #skipRows()
	 * @param theSkipRows the skipRows to set
	 */
	public void skipRows(int theSkipRows) {
		_mySkipRows = theSkipRows;
	}

	/**
	 *  This method is provided as a convenience to the programmer; it provides no 
	 * functionality that cannot be duplicated simply by incrementing the pointer 
	 * passed to glReadPixels. Setting skipImages to <code>k</code> is equivalent 
	 * to incrementing the pointer by <code>k * p</code>, where <code>p</code> is 
	 * the number of components or indices per image, as computed in the 
	 * imageHeight section. The default value is 0.
	 * @return the skipImages
	 */
	public int skipImages() {
		return _mySkipImages;
	}

	/**
	 * Setting skipImages to <code>k</code> is equivalent 
	 * to incrementing the pointer by <code>k * p</code>, where <code>p</code> is 
	 * the number of components or indices per image, as computed in the 
	 * imageHeight section. The default value is 0.
	 * @see #skipImages()
	 * @param theSkipImages the skipImages to set
	 */
	public void skipImages(int theSkipImages) {
		_mySkipImages = theSkipImages;
	}
	
	public CCPixelStorageModes clone() {
		CCPixelStorageModes myResult = new CCPixelStorageModes();
		myResult._mySwapBytes = _mySwapBytes;
		myResult._myLSBFirst = _myLSBFirst;
		
		myResult._myRowLength = _myRowLength;
		myResult._myAlignment = _myAlignment;
		myResult._myImageHeight = _myImageHeight;
		
		myResult._mySkipPixels = _mySkipPixels;
		myResult._mySkipRows = _mySkipRows;
		myResult._mySkipImages = _mySkipImages;
		return myResult;
	}
	
	
}
