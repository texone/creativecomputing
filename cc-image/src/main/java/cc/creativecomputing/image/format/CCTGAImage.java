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
package cc.creativecomputing.image.format;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;

/**
 * <p>
 * Targa image reader and writer adapted from sources of the 
 * <a href = "http://java.sun.com/products/jimi/">Jimi</a> image 
 * I/O class library.
 * </p>
 * <p>
 * Image decoder for image data stored in TGA file format. Currently 
 * only the original TGA file format is supported. This is because the 
 * new TGA format has data at the end of the file, getting to the end of 
 * a file in an InputStream orient environment presents several difficulties 
 * which are avoided at the moment.
 * </p>
 * <p>
 * This is a simple decoder and is only setup to load a single image from the input stream
 * </p>
 * 
 * @author Robin Luiten
 * @author Kenneth Russell
 * @version $Revision: 1.1 $
 */

public class CCTGAImage {
	/**
	 * Little Endian Data Input Stream.
	 * <p>
	 * This class implements an input stream filter to allow reading of java native datatypes from an input stream which
	 * has those native datatypes stored in a little endian byte order.
	 * </p>
	 * <p>
	 * This is the sister class of the DataInputStream which allows for reading of java native datatypes from an input
	 * stream with the datatypes stored in big endian byte order.
	 * </p>
	 * <p>
	 * This class implements the minimum required and calls DataInputStream for some of the required methods for
	 * DataInput.
	 * </p>
	 * 
	 * Not all methods are implemented due to lack of immediate requirement for that functionality. It is not clear if
	 * it is ever going to be functionally required to be able to read UTF data in a LittleEndianManner
	 * <p>
	 * 
	 * @author Robin Luiten
	 * @version 1.1 15/Dec/1997
	 */
	private static class LEDataInputStream extends FilterInputStream implements DataInput {
		/**
		 * To reuse some of the non endian dependent methods from DataInputStreams methods.
		 */
		DataInputStream dataIn;

		public LEDataInputStream(InputStream in) {
			super(in);
			dataIn = new DataInputStream(in);
		}

		public void close() throws IOException {
			dataIn.close(); // better close as we create it.
			// this will close underlying as well.
		}

		public synchronized final int read(byte b[]) throws IOException {
			return dataIn.read(b, 0, b.length);
		}

		public synchronized final int read(byte b[], int off, int len) throws IOException {
			int rl = dataIn.read(b, off, len);
			return rl;
		}

		public final void readFully(byte b[]) throws IOException {
			dataIn.readFully(b, 0, b.length);
		}

		public final void readFully(byte b[], int off, int len) throws IOException {
			dataIn.readFully(b, off, len);
		}

		public final int skipBytes(int n) throws IOException {
			return dataIn.skipBytes(n);
		}

		public final boolean readBoolean() throws IOException {
			int ch = dataIn.read();
			if (ch < 0)
				throw new EOFException();
			return (ch != 0);
		}

		public final byte readByte() throws IOException {
			int ch = dataIn.read();
			if (ch < 0)
				throw new EOFException();
			return (byte) (ch);
		}

		public final int readUnsignedByte() throws IOException {
			int ch = dataIn.read();
			if (ch < 0)
				throw new EOFException();
			return ch;
		}

		public final short readShort() throws IOException {
			int ch1 = dataIn.read();
			int ch2 = dataIn.read();
			if ((ch1 | ch2) < 0)
				throw new EOFException();
			return (short) ((ch1 << 0) + (ch2 << 8));
		}

		public final int readUnsignedShort() throws IOException {
			int ch1 = dataIn.read();
			int ch2 = dataIn.read();
			if ((ch1 | ch2) < 0)
				throw new EOFException();
			return (ch1 << 0) + (ch2 << 8);
		}

		public final char readChar() throws IOException {
			int ch1 = dataIn.read();
			int ch2 = dataIn.read();
			if ((ch1 | ch2) < 0)
				throw new EOFException();
			return (char) ((ch1 << 0) + (ch2 << 8));
		}

		public final int readInt() throws IOException {
			int ch1 = dataIn.read();
			int ch2 = dataIn.read();
			int ch3 = dataIn.read();
			int ch4 = dataIn.read();
			if ((ch1 | ch2 | ch3 | ch4) < 0)
				throw new EOFException();
			return ((ch1 << 0) + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
		}

		public final long readLong() throws IOException {
			int i1 = readInt();
			int i2 = readInt();
			return ((long) (i1) & 0xFFFFFFFFL) + (i2 << 32);
		}

		public final float readFloat() throws IOException {
			return Float.intBitsToFloat(readInt());
		}

		public final double readDouble() throws IOException {
			return Double.longBitsToDouble(readLong());
		}

		/**
		 * dont call this it is not implemented.
		 * 
		 * @return empty new string
		 **/
		public final String readLine() throws IOException {
			return new String();
		}

		/**
		 * dont call this it is not implemented
		 * 
		 * @return empty new string
		 **/
		public final String readUTF() throws IOException {
			return new String();
		}
	}

	private Header header;
	private CCPixelFormat format;
	private ByteBuffer data;

	private CCTGAImage(Header header) {
		this.header = header;
	}

	/**
	 * This class reads in all of the TGA image header in addition it also reads in the imageID field as it is
	 * convenient to handle that here.
	 * 
	 * @author Robin Luiten
	 * @version 1.1
	 */
	public static class Header {
		/** Set of possible file format TGA types */
		public final static int TYPE_NEW = 0;
		public final static int TYPE_OLD = 1;
		public final static int TYPE_UNK = 2; // cant rewind stream so unknown for now.

		/** Set of possible image types in TGA file */
		public final static int NO_IMAGE = 0; // no image data
		public final static int UCOLORMAPPED = 1; // uncompressed color mapped image
		public final static int UTRUECOLOR = 2; // uncompressed true color image
		public final static int UBLACKWHITE = 3; // uncompressed black and white image
		public final static int COLORMAPPED = 9; // compressed color mapped image
		public final static int TRUECOLOR = 10; // compressed true color image
		public final static int BLACKWHITE = 11; // compressed black and white image

		/** Field image descriptor bitfield values definitions */
		public final static int ID_ATTRIBPERPIXEL = 0xF;
		public final static int ID_RIGHTTOLEFT = 0x10;
		public final static int ID_TOPTOBOTTOM = 0x20;
		public final static int ID_INTERLEAVE = 0xC0;

		/** Field image descriptor / interleave values */
		public final static int I_NOTINTERLEAVED = 0;
		public final static int I_TWOWAY = 1;
		public final static int I_FOURWAY = 2;

		/** Type of this TGA file format */
		private int tgaType;

		/** initial TGA image data fields */
		private int idLength; // byte value
		private int colorMapType; // byte value
		private int imageType; // byte value

		/** TGA image colour map fields */
		private int firstEntryIndex;
		private int colorMapLength;
		private byte colorMapEntrySize;

		/** TGA image specification fields */
		private int xOrigin;
		private int yOrigin;
		private int width;
		private int height;
		private byte pixelDepth;
		private byte imageDescriptor;

		private byte[] imageIDbuf;
		private String imageID;

		// For construction from user data
		Header() {
			tgaType = TYPE_OLD; // dont try and get footer.
		}

		Header(LEDataInputStream in) throws IOException {
			tgaType = TYPE_OLD; // dont try and get footer.

			// initial header fields
			idLength = in.readUnsignedByte();
			colorMapType = in.readUnsignedByte();
			imageType = in.readUnsignedByte();

			// color map header fields
			firstEntryIndex = in.readUnsignedShort();
			colorMapLength = in.readUnsignedShort();
			colorMapEntrySize = in.readByte();

			// TGA image specification fields
			xOrigin = in.readUnsignedShort();
			yOrigin = in.readUnsignedShort();
			width = in.readUnsignedShort();
			height = in.readUnsignedShort();
			pixelDepth = in.readByte();
			imageDescriptor = in.readByte();

			if (idLength > 0) {
				imageIDbuf = new byte[idLength];
				in.read(imageIDbuf, 0, idLength);
				imageID = new String(imageIDbuf, "US-ASCII");
			}
		}

		public int tgaType() {
			return tgaType;
		}

		/** initial TGA image data fields */
		public int idLength() {
			return idLength;
		}

		public int colorMapType() {
			return colorMapType;
		}

		public int imageType() {
			return imageType;
		}

		/** TGA image colour map fields */
		public int firstEntryIndex() {
			return firstEntryIndex;
		}

		public int colorMapLength() {
			return colorMapLength;
		}

		public byte colorMapEntrySize() {
			return colorMapEntrySize;
		}

		/** TGA image specification fields */
		public int xOrigin() {
			return xOrigin;
		}

		public int yOrigin() {
			return yOrigin;
		}

		public int width() {
			return width;
		}

		public int height() {
			return height;
		}

		public byte pixelDepth() {
			return pixelDepth;
		}

		public byte imageDescriptor() {
			return imageDescriptor;
		}

		/** bitfields in imageDescriptor */
		public byte attribPerPixel() {
			return (byte) (imageDescriptor & ID_ATTRIBPERPIXEL);
		}

		public boolean rightToLeft() {
			return ((imageDescriptor & ID_RIGHTTOLEFT) != 0);
		}

		public boolean topToBottom() {
			return ((imageDescriptor & ID_TOPTOBOTTOM) != 0);
		}

		public byte interleave() {
			return (byte) ((imageDescriptor & ID_INTERLEAVE) >> 6);
		}

		public byte[] imageIDbuf() {
			return imageIDbuf;
		}

		public String imageID() {
			return imageID;
		}

		public String toString() {
			return "TGA Header " + " id length: " + idLength + " color map type: " + colorMapType + " image type: " + imageType + " first entry index: " + firstEntryIndex
					+ " color map length: " + colorMapLength + " color map entry size: " + colorMapEntrySize + " x Origin: " + xOrigin + " y Origin: " + yOrigin + " width: "
					+ width + " height: " + height + " pixel depth: " + pixelDepth + " image descriptor: " + imageDescriptor
					+ (imageIDbuf == null ? "" : (" ID String: " + imageID));
		}

		public int size() {
			return 18 + idLength;
		}

		// buf must be in little-endian byte order
		private void write(ByteBuffer buf) {
			buf.put((byte) idLength);
			buf.put((byte) colorMapType);
			buf.put((byte) imageType);
			buf.putShort((short) firstEntryIndex);
			buf.putShort((short) colorMapLength);
			buf.put((byte) colorMapEntrySize);
			buf.putShort((short) xOrigin);
			buf.putShort((short) yOrigin);
			buf.putShort((short) width);
			buf.putShort((short) height);
			buf.put((byte) pixelDepth);
			buf.put((byte) imageDescriptor);
			if (idLength > 0) {
				try {
					byte[] chars = imageID.getBytes("US-ASCII");
					buf.put(chars);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Identifies the image type of the tga image data and loads it into the JimiImage structure. This was taken from
	 * the prototype and modified for the new Jimi structure
	 */
	private void decodeImage(LEDataInputStream dIn) throws IOException {
		switch (header.imageType()) {
		case Header.UCOLORMAPPED:
			throw new IOException("TGADecoder Uncompressed Colormapped images not supported");

		case Header.UTRUECOLOR: // pixelDepth 15, 16, 24 and 32
			switch (header.pixelDepth) {
			case 16:
				throw new IOException("TGADecoder Uncompressed 16-bit True Color images not supported");

			case 24:
			case 32:
				decodeRGBImageU24_32(dIn);
				break;
			}
			break;

		case Header.UBLACKWHITE:
			throw new IOException("TGADecoder Uncompressed Grayscale images not supported");

		case Header.COLORMAPPED:
			throw new IOException("TGADecoder Compressed Colormapped images not supported");

		case Header.TRUECOLOR:
			switch (header.pixelDepth) {
			case 16:
				throw new IOException("TGADecoder Compressed 16-bit True Color images not supported");

			case 24:
			case 32:
				decodeRGBImageC24_32(dIn);
				break;
			}
			break;

		case Header.BLACKWHITE:
			throw new IOException("TGADecoder Compressed Grayscale images not supported");
		}
	}
	
	/**
	 * This assumes that the body is for a 24 bit or 32 bit for a RGB or ARGB image respectively.
	 * @param theInputStream
	 * @throws IOException
	 */
	private void decodeRGBImageU24_32(LEDataInputStream theInputStream) throws IOException {
		int i; // row index
		int y; // output row index
		int rawWidth = header.width() * (header.pixelDepth() / 8);
		byte[] rawBuf = new byte[rawWidth];
		byte[] tmpData = new byte[rawWidth * header.height()];

		if (header.pixelDepth() == 24) {
			format = CCPixelFormat.BGR;
		} else {
			assert header.pixelDepth() == 32;
			format = CCPixelFormat.BGRA;
		}

		for (i = 0; i < header.height(); ++i) {
			theInputStream.readFully(rawBuf, 0, rawWidth);

			if (header.topToBottom())
				y = header.height - i - 1; // range 0 to (header.height - 1)
			else
				y = i;

			System.arraycopy(rawBuf, 0, tmpData, y * rawWidth, rawBuf.length);
		}

		data = ByteBuffer.wrap(tmpData);
	}

	private void decodeRGBImageC24_32(LEDataInputStream theInputStream) throws IOException {// Load COMPRESSED TGAs{
		if (header.pixelDepth() == 24) {
			format = CCPixelFormat.RGB;
		} else {
			assert header.pixelDepth() == 32;
			format = CCPixelFormat.RGBA;
		}

		int bytesPerPixel = (header.pixelDepth() / 8); // Compute BYTES per pixel

		// Nuber of pixels in the image
		int pixelcount = header.width() * header.height(); 
		
		// Compute amout of memory needed to store image
		int imageSize = (bytesPerPixel * header.width() * header.height());
		// Allocate that much memory
		byte[] imageData = new byte[imageSize];

		int currentpixel = 0; // Current pixel being read
		int currentbyte = 0; // Current byte
		// Storage for 1 pixel
		byte[] colorbuffer = new byte[bytesPerPixel];

		do {
			int chunkheader = theInputStream.read();

			// If the ehader is < 128, it means the that is the number of
			// RAW color packets minus 1
			if (chunkheader < 128) { // that follow the header
				chunkheader++; // add 1 to get number of following color values
				// Read RAW color values
				for (short counter = 0; counter < chunkheader; counter++) {
					theInputStream.readFully(colorbuffer);
					// write to memory
					// Flip R and B vcolor values around in the process
					imageData[currentbyte] = colorbuffer[2];
					imageData[currentbyte + 1] = colorbuffer[1];
					imageData[currentbyte + 2] = colorbuffer[0];

					if (bytesPerPixel == 4) // if its a 32 bpp image
					{
						// copy the 4th byte
						imageData[currentbyte + 3] = colorbuffer[3];
					}

					// Increase thecurrent byte by the number of bytes per pixel
					currentbyte += bytesPerPixel;
					currentpixel++; // Increase current pixel by 1

					// Make sure we havent read too many pixels
					if (currentpixel > pixelcount) {
						// if there is too many... Display an error!
						throw new IOException("Too many pixels read");
					}
				}
			} else {
				// chunkheader > 128 RLE data, next color reapeated chunkheader - 127 times
				chunkheader -= 127; // Subteact 127 to get rid of the ID bit
				theInputStream.readFully(colorbuffer);

				// copy the color into the image data as many times as dictated
				for (short counter = 0; counter < chunkheader; counter++) { // by the header
					imageData[currentbyte] = colorbuffer[2];
					imageData[currentbyte + 1] = colorbuffer[1];
					imageData[currentbyte + 2] = colorbuffer[0];

					if (bytesPerPixel == 4) // if its a 32 bpp image
					{
						// copy the 4th byte
						imageData[currentbyte + 3] = colorbuffer[3];
					}

					// Increase current byte by the number of bytes per pixel
					currentbyte += bytesPerPixel;
					currentpixel++; // Increase pixel count by 1

					// Make sure we havent written too many pixels
					if (currentpixel > pixelcount) {
						// if there is too many... Display an error!
						throw new IOException("Too many pixels read");
					}
				}
			}
		} while (currentpixel < pixelcount); // Loop while there are still pixels left
		data = ByteBuffer.wrap(imageData);
	}

	/** Returns the width of the image. */
	public int getWidth() {
		return header.width();
	}

	/** Returns the height of the image. */
	public int getHeight() {
		return header.height();
	}

	/** Returns the OpenGL format for this texture; e.g. GL.GL_BGR or GL.GL_BGRA. */
	public CCPixelFormat pixelFormat() {
		return format;
	}

	/**
	 * Returns the raw data for this texture in the correct (bottom-to-top) order for calls to glTexImage2D.
	 */
	public ByteBuffer getData() {
		return data;
	}

	/** Reads a Targa image from the specified file. */
	public static CCTGAImage read(String filename) throws IOException {
		return read(new FileInputStream(filename));
	}

	/** 
	 * Reads a Targa image from the specified InputStream. 
	 **/
	public static CCTGAImage read(InputStream in) throws CCImageException {
		LEDataInputStream dIn = new LEDataInputStream(new BufferedInputStream(in));

		CCTGAImage res;
		try {
			Header header = new Header(dIn);
			res = new CCTGAImage(header);
			res.decodeImage(dIn);
		} catch (IOException e) {
			throw new CCImageException(e);
		}
		return res;
	}

	/** Writes the image in Targa format to the specified file. */
	public void write(Path file) throws IOException {
		FileChannel chan = FileChannel.open(file);
		ByteBuffer buf = ByteBuffer.allocate(header.size());
		buf.order(ByteOrder.LITTLE_ENDIAN);
		header.write(buf);
		buf.rewind();
		chan.write(buf);
		chan.write(data);
		data.rewind();
		chan.force(true);
		chan.close();
	}

	/**
	 * Creates a TGAImage from data supplied by the end user. Shares data with 
	 * the passed ByteBuffer. Assumes the data is already in the correct byte 
	 * order for writing to disk, i.e., BGR or BGRA.
	 *
	 * @param width
	 * @param height
	 * @param hasAlpha
	 * @param topToBottom
	 * @param data
	 * @return
	 */
	public static CCTGAImage createFromData(int width, int height, boolean hasAlpha, boolean topToBottom, ByteBuffer data) {
		Header myHeader = new Header();
		myHeader.imageType = Header.UTRUECOLOR;
		myHeader.width = width;
		myHeader.height = height;
		myHeader.pixelDepth = (byte) (hasAlpha ? 32 : 24);
		myHeader.imageDescriptor = (byte) (topToBottom ? Header.ID_TOPTOBOTTOM : 0);
		
		// Note ID not supported
		CCTGAImage ret = new CCTGAImage(myHeader);
		ret.data = data;
		return ret;
	}
}
