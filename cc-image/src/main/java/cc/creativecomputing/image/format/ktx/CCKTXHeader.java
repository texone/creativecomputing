/* JKTX
 * 
 * Copyright (c) 2011 Timon Bijlsma
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
   
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cc.creativecomputing.image.format.ktx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class CCKTXHeader {

	public static final int HEADER_LENGTH = 64;

	/**
	 * The file identifier is a unique set of bytes that will differentiate the file from other types of files. It consists of 12 bytes.
	 * <p>
	 * The rationale behind the choice values in the identifier is based on the rationale for the identifier in the PNG specification. 
	 * This identifier both identifies the file as a KTX file and provides for immediate detection of common file-transfer problems.
	 */
	public static final byte[] FILE_IDENTIFIER = new byte[] {
		(byte) 0xAB, 0x4B, 0x54, 0x58, 0x20, 0x31, 0x31, 
		(byte) 0xBB, 0x0D, 0x0A, 0x1A, 0x0A 
	};

	private ByteOrder _myByteOrder;
	private boolean _myByteOrderNative;
	private int _myGLType;
	private int _myGLTypeSize;
	private int _myGLFormat;
	private int _myGLInternalFormat;
	private int _myGLBaseInternalFormat;
	private int _myPixelWidth;
	private int _myPixelHeight;
	private int _myPixelDepth;
	private int _myNumberOfArrayElements;
	private int _myNumberOfFaces;
	private int _mynumberOfMipmapLevels;
	private int _myBytesOfKeyValueData;

	public CCKTXHeader() {
		_myByteOrder = ByteOrder.nativeOrder();
		_myByteOrderNative = true;
		_myGLType = GLConstants.GL_UNSIGNED_INT_8_8_8_8_REV;
		_myGLTypeSize = 4;
		_myGLFormat = GLConstants.GL_BGRA;
		_myGLInternalFormat = GLConstants.GL_RGBA8;
		_myGLBaseInternalFormat = GLConstants.GL_RGBA;
		_myNumberOfFaces = 1;
		_mynumberOfMipmapLevels = 1;
	}

	// Functions
	public void read(InputStream in) throws KTXFormatException, IOException {
		read(in, false);
	}

	public void read(InputStream in, boolean strict) throws KTXFormatException, IOException {
		ByteBuffer buf = ByteBuffer.allocate(HEADER_LENGTH);
		KTXUtil.readFully(in, buf);
		read(buf, strict);
	}

	public void read(ByteBuffer buf) throws KTXFormatException {
		read(buf, false);
	}

	public void read(ByteBuffer buf, boolean strict) throws KTXFormatException {
		ByteOrder oldOrder = buf.order();
		try {
			read0(buf, strict);
		} catch (BufferUnderflowException bue) {
			throw new KTXFormatException("Unexpected end of input", bue);
		} finally {
			buf.order(oldOrder);
		}
	}

	private void read0(ByteBuffer buf, boolean strict) throws KTXFormatException {
		buf.order(ByteOrder.nativeOrder());

		// Check file identifier
		byte[] magic = new byte[FILE_IDENTIFIER.length];
		buf.get(magic);
		if (!Arrays.equals(magic, FILE_IDENTIFIER)) {
			throw new KTXFormatException("Input doesn't start with KTX file identifier");
		}

		// Check endianness and, if necessary, flip the buffer's endianness
		int endianness = buf.getInt();
		if (endianness == 0x04030201) {
			// Endianness OK
			_myByteOrderNative = true;
		} else if (endianness == 0x01020304) {
			// Endianness Reversed
			_myByteOrderNative = false;
		} else {
			throw new KTXFormatException(String.format("Endianness field has an unexpected value: %08x", endianness));
		}

		_myByteOrder = buf.order();
		if (!_myByteOrderNative) {
			if (_myByteOrder == ByteOrder.BIG_ENDIAN) {
				_myByteOrder = ByteOrder.LITTLE_ENDIAN;
			} else {
				_myByteOrder = ByteOrder.BIG_ENDIAN;
			}
			buf.order(_myByteOrder);
		}

		_myGLType = buf.getInt();
		_myGLTypeSize = buf.getInt();
		if (_myGLTypeSize != 1 && _myGLTypeSize != 2 && _myGLTypeSize != 4) {
			throw new KTXFormatException("glTypeSize not supported: " + _myGLTypeSize);
		}

		_myGLFormat = buf.getInt();
		_myGLInternalFormat = buf.getInt();
		_myGLBaseInternalFormat = buf.getInt();
		_myPixelWidth = buf.getInt();
		_myPixelHeight = buf.getInt();
		_myPixelDepth = buf.getInt();
		if (_myPixelWidth < 0 || _myPixelHeight < 0 || _myPixelDepth < 0) {
			throw new KTXFormatException(String.format("Invalid number of pixel dimensions: %dx%dx%d", _myPixelWidth, _myPixelHeight, _myPixelDepth));
		}
		_myNumberOfArrayElements = buf.getInt();
		if (_myNumberOfArrayElements < 0) {
			throw new KTXFormatException(String.format("Invalid number of array elements: %d", _myNumberOfArrayElements));
		}
		_myNumberOfFaces = buf.getInt();
		if (_myNumberOfFaces != 1 && _myNumberOfFaces != 6) {
			if (strict) {
				throw new KTXFormatException(String.format("Invalid number of faces: %d", _myNumberOfFaces));
			} else if (_myNumberOfFaces <= 0) {
				_myNumberOfFaces = 1;
			}
		}
		_mynumberOfMipmapLevels = buf.getInt();
		if (_mynumberOfMipmapLevels < 0) {
			throw new KTXFormatException(String.format("Invalid number of mipmap levels: %d", _mynumberOfMipmapLevels));
		}
		_myBytesOfKeyValueData = buf.getInt();
		if (_myBytesOfKeyValueData < 0) {
			throw new KTXFormatException(String.format("Invalid key/value byte size: %d", _myBytesOfKeyValueData));
		}
	}

	public void write(OutputStream out) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(HEADER_LENGTH);
		write(buf);
		out.write(buf.array());
	}

	public void write(ByteBuffer buf) {
		ByteOrder oldOrder = buf.order();
		try {
			write0(buf);
		} finally {
			buf.order(oldOrder);
		}
	}

	private void write0(ByteBuffer buf) {
		buf.order(_myByteOrder);

		buf.put(FILE_IDENTIFIER);
		buf.putInt(0x04030201);

		buf.putInt(_myGLType);
		buf.putInt(_myGLTypeSize);
		buf.putInt(_myGLFormat);
		buf.putInt(_myGLInternalFormat);
		buf.putInt(_myGLBaseInternalFormat);
		buf.putInt(_myPixelWidth);
		buf.putInt(_myPixelHeight);
		buf.putInt(_myPixelDepth);
		buf.putInt(_myNumberOfArrayElements);
		buf.putInt(_myNumberOfFaces);
		buf.putInt(_mynumberOfMipmapLevels);
		buf.putInt(_myBytesOfKeyValueData);
	}

	@Override
	public String toString() {
		return String.format(
			"%s[glType=%d, "
			+ "glTypeSize=%d, "
			+ "glFormat=%d, "
			+ "glInternalFormat=%d, "
			+ "glBaseInternalFormat=%d, "
			+ "pixelWidth=%d, "
			+ "pixelHeight=%d, "
			+ "pixelDepth=%d, "
			+ "numberOfArrayElements=%d, "
			+ "numberOfFaces=%d, "
			+ "numberOfMipmapLevels=%d, "
			+ "bytesOfKeyValueData=%d]", 
			getClass().getSimpleName(), 
			_myGLType, 
			_myGLTypeSize, 
			_myGLFormat, 
			_myGLInternalFormat, 
			_myGLBaseInternalFormat, 
			_myPixelWidth, 
			_myPixelHeight,
			_myPixelDepth, 
			_myNumberOfArrayElements, 
			_myNumberOfFaces, 
			_mynumberOfMipmapLevels, 
			_myBytesOfKeyValueData
		);
	}

	/**
	 * endianness contains the number 0x04030201 written as a 32 bit integer. 
	 * If the file is little endian then this is represented as the bytes 0x01 0x02 0x03 0x04. 
	 * If the file is big endian then this is represented as the bytes 0x04 0x03 0x02 0x01. 
	 * When reading endianness as a 32 bit integer produces the value 0x04030201 then the 
	 * endianness of the file matches the the endianness of the program that is reading the 
	 * file and no conversion is necessary. When reading endianness as a 32 bit integer 
	 * produces the value 0x01020304 then the endianness of the file is opposite the 
	 * endianness of the program that is reading the file, and in that case the program 
	 * reading the file must endian convert all header bytes to the endianness of the program 
	 * (i.e. a little endian program must convert from big endian, and a big endian program 
	 * must convert to little endian).
	 * @return
	 */
	public ByteOrder byteOrder() {
		return _myByteOrder;
	}

	public boolean isByteOrderNative() {
		return _myByteOrderNative;
	}

	/**
	 * For compressed textures, glType must equal 0. For uncompressed textures, glType 
	 * specifies the type parameter passed to glTex{,Sub}Image*D, usually one of the 
	 * values from table 8.2 of the OpenGL 4.4 specification
	 * @return
	 */
	public int glType() {
		return _myGLType;
	}

	/**
	 * glTypeSize specifies the data type size that should be used when endianness 
	 * conversion is required for the texture data stored in the file. If glType is 
	 * not 0, this should be the size in bytes corresponding to glType. For texture 
	 * data which does not depend on platform endianness, including compressed 
	 * texture data, glTypeSize must equal 1.
	 * @return
	 */
	public int glTypeSize() {
		return _myGLTypeSize;
	}

	/**
	 * For compressed textures, glFormat must equal 0. For uncompressed textures, 
	 * glFormat specifies the format parameter passed to glTex{,Sub}Image*D, usually 
	 * one of the values from table 8.3 of the OpenGL 4.4 specification 
	 * [OPENGL44] (RGB, RGBA, BGRA, etc.)
	 * @return
	 */
	public int glFormat() {
		return _myGLFormat;
	}

	/**
	 * For compressed textures, glInternalFormat must equal the compressed internal format, 
	 * usually one of the values from table 8.14 of the OpenGL 4.4 specification [OPENGL44]. 
	 * For uncompressed textures, glInternalFormat specifies the internalformat parameter 
	 * passed to glTexStorage*D or glTexImage*D, usually one of the sized internal formats 
	 * from tables 8.12 & 8.13 of the OpenGL 4.4 specification [OPENGL44]. The sized format 
	 * should be chosen to match the bit depth of the data provided. glInternalFormat is 
	 * used when loading both compressed and uncompressed textures, except when loading 
	 * into a context that does not support sized formats, such as an unextended OpenGL 
	 * ES 2.0 context where the internalformat parameter is required to have the same 
	 * value as the format parameter.
	 * @return
	 */
	public int glInternalFormat() {
		return _myGLInternalFormat;
	}

	/**
	 * For both compressed and uncompressed textures, glBaseInternalFormat specifies the base 
	 * internal format of the texture, usually one of the values from table 8.11 of the 
	 * OpenGL 4.4 specification [OPENGL44] (RGB, RGBA, ALPHA, etc.). For uncompressed 
	 * textures, this value will be the same as glFormat and is used as the internalformat 
	 * parameter when loading into a context that does not support sized formats, such as 
	 * an unextended OpenGL ES 2.0 context.
	 * @return
	 */
	public int glBaseInternalFormat() {
		return _myGLBaseInternalFormat;
	}

	/**
	 * The width of the texture image for level 0, in pixels.
	 * @return
	 */
	public int pixelWidth() {
		return _myPixelWidth;
	}

	/**
	 * The size of the texture image for level 0, in pixels.
	 * @param i
	 * @return
	 */
	public int pixelWidth(int i) {
		return Math.max(1, pixelWidth() >> i);
	}

	/**
	 * The height of the texture image for level 0, in pixels.
	 * @return
	 */
	public int pixelHeight() {
		return _myPixelHeight;
	}

	public int pixelHeight(int i) {
		return Math.max(1, pixelHeight() >> i);
	}

	/**
	 * The depth of the texture image for level 0, in pixels.
	 * @return
	 */
	public int pixelDepth() {
		return _myPixelDepth;
	}

	public int pixelDepth(int i) {
		return Math.max(1, pixelDepth() >> i);
	}

	/**
	 * numberOfArrayElements specifies the number of array elements. 
	 * If the texture is not an array texture, numberOfArrayElements must equal 0.
	 * @return
	 */
	public int numberOfArrayElements() {
		return _myNumberOfArrayElements;
	}

	/**
	 * numberOfFaces specifies the number of cubemap faces. For cubemaps and cubemap 
	 * arrays this should be 6. For non cubemaps this should be 1. Cube map faces 
	 * are stored in the order: +X, -X, +Y, -Y, +Z, -Z.
	 * @return
	 */
	public int numberOfFaces() {
		return _myNumberOfFaces;
	}

	/**
	 * numberOfMipmapLevels must equal 1 for non-mipmapped textures. For mipmapped 
	 * textures, it equals the number of mipmaps. Mipmaps are stored in order from 
	 * largest size to smallest size. The first mipmap level is always level 0. A KTX 
	 * file does not need to contain a complete mipmap pyramid. If numberOfMipmapLevels 
	 * equals 0, it indicates that a full mipmap pyramid should be generated from level 
	 * 0 at load time (this is usually not allowed for compressed formats).
	 * @return
	 */
	public int numberOfMipmapLevels() {
		return _mynumberOfMipmapLevels;
	}

	public boolean autoGenerateMipmap() {
		return _mynumberOfMipmapLevels == 0;
	}

	/**
	 * An arbitrary number of key/value pairs may follow the header. This can be used to 
	 * encode any arbitrary data. The bytesOfKeyValueData field indicates the total number 
	 * of bytes of key/value data including all keyAndValueByteSize fields, all keyAndValue 
	 * fields, and all valuePadding fields. The file offset of the first imageSize field is 
	 * located at the file offset of the bytesOfKeyValueData field plus the value of the 
	 * bytesOfKeyValueData field plus 4.
	 * @return
	 */
	public int bytesOfKeyValueData() {
		return _myBytesOfKeyValueData;
	}

	public void byteOrder(ByteOrder order) {
		_myByteOrder = order;
		_myByteOrderNative = (order == ByteOrder.nativeOrder());
	}

	public void glFormat(int glInternalFormat, int glBaseInternalFormat, int glFormat, int glType, int glTypeSize) {
		_myGLInternalFormat = glInternalFormat;
		_myGLBaseInternalFormat = glBaseInternalFormat;
		_myGLFormat = glFormat;
		_myGLType = glType;
		_myGLTypeSize = glTypeSize;
	}

	public void compressedGLFormat(int glInternalFormat, int glBaseInternalFormat) {
		glFormat(glInternalFormat, glBaseInternalFormat, 0, 0, 1);
	}

	public void dimensions(int w, int h, int d) {
		_myPixelWidth = w;
		_myPixelHeight = h;
		_myPixelDepth = d;
	}

	public void numberOfArrayElements(int numberOfArrayElements) {
		_myNumberOfArrayElements = numberOfArrayElements;
	}

	public void numberOfFaces(int numberOfFaces) {
		_myNumberOfFaces = numberOfFaces;
	}

	public void numberOfMipmapLevels(int numberOfMipmapLevels) {
		_mynumberOfMipmapLevels = numberOfMipmapLevels;
	}

	public void bytesOfKeyValueData(int bytesOfKeyValueData) {
		_myBytesOfKeyValueData = bytesOfKeyValueData;
	}

}