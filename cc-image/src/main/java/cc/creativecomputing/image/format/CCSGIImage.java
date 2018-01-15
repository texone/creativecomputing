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
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;

/**
 * <p>
 * Reads and writes SGI RGB/RGBA images.
 * </p>
 * 
 * <p>
 * Written from <a href = "http://astronomy.swin.edu.au/~pbourke/dataformats/sgirgb/">Paul Bourke's adaptation</a> of
 * the <a href = "http://astronomy.swin.edu.au/~pbourke/dataformats/sgirgb/sgiversion.html">SGI specification</a>.
 * </p>
 */

public class CCSGIImage {
	private CCSGIHeader _myHeader;
	
	private CCPixelFormat _myFormat;
	private CCPixelInternalFormat _myInternalFormat;
	
	private byte[] _myData;
	// Used for decoding RLE-compressed images
	private int[] _myRowStart;
	private int[] _myRowSize;
	private int _myRLEEnd;
	
	private byte[] _myTmpData;
	private byte[] _myTmpRead;

	private static final int MAGIC = 474;

	static class CCSGIHeader {
		/**
		 * IRIS image file magic number
		 */
		private short _myMagicNumber;
		
		/**
		 * Storage format, This should be decimal 474
		 * 0 for uncompressed
		 * 1 for RLE compression
		 */
		private byte _myStorage;
		
		/**
		 * Number of bytes per pixel channel
		 */
		private byte _myBytesPerPixelChannel;
		
		/**
		 * Number of dimensions Legally 1, 2, or 3
		 * 1 means a single row, XSIZE long
		 * 2 means a single 2D image
		 * 3 means multiple 2D images
		 */
		private short _myDimensions;
		
		/**
		 * X size in pixels
		 */
		private short _myXsize;
		
		/**
		 * Y size in pixels
		 */
		private short _myYsize;
		
		/**
		 * Number of channels1 indicates greyscale
		 * 3 indicates RGB
		 * 4 indicates RGB and Alpha
		 */
		private short _myNumberOfChannels;
		
		/**
		 * This is the lowest pixel value in the image
		 */
		private int _myMinimumPixelValue;
		
		/**
		 * This is the highest pixel value in the image
		 */
		private int _myMaximumPixelValue;
		
		/**
		 * Image name; 80 bytes long
		 */
		private String _myImagename;
		
		/**
		 * Colormap ID
		 * 0 - normal mode
		 * 1 - dithered, 3 mits for red and green, 2 for blue, obsolete
		 * 2 - index colour, obsolete
		 * 3 - not an image but a colourmap
		 * 404 bytes char DUMMY Ignored
		 * Should be set to 0, makes the header 512 bytes.
		 */
		private int _myColormap;

		CCSGIHeader() {
			_myMagicNumber = MAGIC;
		}

		CCSGIHeader(final DataInputStream theInputStream) throws IOException {
			_myMagicNumber = theInputStream.readShort();
			_myStorage = theInputStream.readByte();
			_myBytesPerPixelChannel = theInputStream.readByte();
			_myDimensions = theInputStream.readShort();
			
			_myXsize = theInputStream.readShort();
			_myYsize = theInputStream.readShort();
			
			_myNumberOfChannels = theInputStream.readShort();
			
			_myMinimumPixelValue = theInputStream.readInt();
			_myMaximumPixelValue = theInputStream.readInt();
			
			theInputStream.readInt();
			
			byte[] tmpname = new byte[80];
			theInputStream.read(tmpname);
			
			int numChars = 0;
			while (tmpname[numChars++] != 0);
			_myImagename = new String(tmpname, 0, numChars);
			
			_myColormap = theInputStream.readInt();
			
			byte[] tmp = new byte[404];
			theInputStream.read(tmp);
		}

		public String toString() {
			return (
				"magic: " + _myMagicNumber + 
				" storage: " + (int) _myStorage + 
				" bpc: " + (int) _myBytesPerPixelChannel + 
				" dimension: " + _myDimensions + 
				" xsize: " + _myXsize + 
				" ysize: " + _myYsize + 
				" zsize: " + _myNumberOfChannels + 
				" pixmin: " + _myMinimumPixelValue + 
				" pixmax: " + _myMaximumPixelValue + 
				" imagename: " + _myImagename + 
				" colormap: " + _myColormap
			);
		}
	}

	private CCSGIImage(CCSGIHeader header) {
		this._myHeader = header;
	}

	/**
	 * Reads an SGI image from the specified file.
	 * @param theFileName
	 * @return
	 */
	public static CCSGIImage read(final String theFileName){
		try {
			return read(new FileInputStream(theFileName));
		} catch (FileNotFoundException e) {
			throw new CCImageException("Could not read SGI Image: " + theFileName,e);
		}
	}

	/**
	 * Reads an SGI image from the specified InputStream.
	 * @param theInputStream
	 * @return read image
	 */
	public static CCSGIImage read(final InputStream theInputStream){
		DataInputStream dIn = new DataInputStream(new BufferedInputStream(theInputStream));

		try {
			CCSGIHeader header = new CCSGIHeader(dIn);
			CCSGIImage res = new CCSGIImage(header);
			res.decodeImage(dIn);
			return res;
		}catch(IOException e) {
			throw new CCImageException("Could not read the image:",e);
		}
	}

	/**
	 * Writes this SGIImage to the specified file. If flipVertically is set, 
	 * outputs the scanlines from top to bottom rather than the default bottom to top order.
	 *
	 * @param theFile
	 * @param theFlipVertically
	 */
	public void write(final Path theFile, final boolean theFlipVertically){
		try {
			writeImage(theFile, _myData, _myHeader, theFlipVertically);
		} catch (IOException e) {
			throw new CCImageException("",e);
		}
	}

	/**
	 * Creates an SGIImage from the specified data in either RGB or RGBA format.
	 * @param theWidth
	 * @param theHeight
	 * @param theHasAlpha
	 * @param theData
	 * @return
	 */
	public static CCSGIImage createFromData(
		final int theWidth, final int theHeight, 
		final boolean theHasAlpha, final byte[] theData
	) {
		CCSGIHeader myHeader = new CCSGIHeader();
		myHeader._myXsize = (short) theWidth;
		myHeader._myYsize = (short) theHeight;
		myHeader._myNumberOfChannels = (short) (theHasAlpha ? 4 : 3);
		
		CCSGIImage myImage = new CCSGIImage(myHeader);
		myImage._myData = theData;
		
		return myImage;
	}
	
	/**
	 * Determines from the magic number whether the given InputStream 
	 * points to an SGI RGB image. The given InputStream must return true 
	 * from markSupported() and support a minimum of two bytes of read-ahead.
	 *
	 * @param theInputStrea
	 * @return
	 * @throws IOException
	 */
	public static boolean isSGIImage(InputStream theInputStrea){
		if (!(theInputStrea instanceof BufferedInputStream)) {
			theInputStrea = new BufferedInputStream(theInputStrea);
		}
		if (!theInputStrea.markSupported()) {
			throw new CCImageException("Can not test non-destructively whether given InputStream is an SGI RGB image");
		}
		try {
			DataInputStream dIn = new DataInputStream(theInputStrea);
			dIn.mark(4);
			short magic = dIn.readShort();
			dIn.reset();
			return (magic == MAGIC);
		} catch (IOException e) {
			throw new CCImageException("Can not test non-destructively whether given InputStream is an SGI RGB image",e);
		}
	}

	/** 
	 * Returns the width of the image. 
	 */
	public int width() {
		return _myHeader._myXsize;
	}

	/** 
	 * Returns the height of the image. 
	 */
	public int getHeight() {
		return _myHeader._myYsize;
	}

	/** 
	 * Returns the pixel internal format for this texture; e.g. RGB or RGBA. 
	 */
	public CCPixelInternalFormat getInternalFormat() {
		return _myInternalFormat;
	}

	/** 
	 * Returns the pixel format for this texture; e.g. RGB or RGBA.
	 */
	public CCPixelFormat getFormat() {
		return _myFormat;
	}

	/**
	 * Returns the raw data for this texture in the correct (bottom-to-top) order for calls to glTexImage2D.
	 */
	public byte[] getData() {
		return _myData;
	}

	public String toString() {
		return _myHeader.toString();
	}

	// ----------------------------------------------------------------------
	// Internals only below this point
	//

	private void decodeImage(DataInputStream in) throws IOException {
		if (_myHeader._myStorage == 1) {
			// Read RLE compression data; row starts and sizes
			int x = _myHeader._myYsize * _myHeader._myNumberOfChannels;
			_myRowStart = new int[x];
			_myRowSize = new int[x];
			_myRLEEnd = 4 * 2 * x + 512;
			for (int i = 0; i < x; i++) {
				_myRowStart[i] = in.readInt();
			}
			for (int i = 0; i < x; i++) {
				_myRowSize[i] = in.readInt();
			}
			_myTmpRead = new byte[_myHeader._myXsize * 256];
		}
		_myTmpData = readAll(in);

		int xsize = _myHeader._myXsize;
		int ysize = _myHeader._myYsize;
		int zsize = _myHeader._myNumberOfChannels;
		int lptr = 0;

		_myData = new byte[xsize * ysize * 4];
		byte[] rbuf = new byte[xsize];
		byte[] gbuf = new byte[xsize];
		byte[] bbuf = new byte[xsize];
		byte[] abuf = new byte[xsize];
		for (int y = 0; y < ysize; y++) {
			if (zsize >= 4) {
				getRow(rbuf, y, 0);
				getRow(gbuf, y, 1);
				getRow(bbuf, y, 2);
				getRow(abuf, y, 3);
				rgbatorgba(rbuf, gbuf, bbuf, abuf, _myData, lptr);
			} else if (zsize == 3) {
				getRow(rbuf, y, 0);
				getRow(gbuf, y, 1);
				getRow(bbuf, y, 2);
				rgbtorgba(rbuf, gbuf, bbuf, _myData, lptr);
			} else if (zsize == 2) {
				getRow(rbuf, y, 0);
				getRow(abuf, y, 1);
				latorgba(rbuf, abuf, _myData, lptr);
			} else {
				getRow(rbuf, y, 0);
				bwtorgba(rbuf, _myData, lptr);
			}
			lptr += 4 * xsize;
		}
		_myRowStart = null;
		_myRowSize = null;
		_myTmpData = null;
		_myTmpRead = null;
		_myFormat = CCPixelFormat.RGBA;
		_myInternalFormat = CCPixelInternalFormat.RGBA;
		_myHeader._myNumberOfChannels = 4;
	}

	private void getRow(byte[] buf, int y, int z) {
		if (_myHeader._myStorage == 1) {
			int offs = _myRowStart[y + z * _myHeader._myYsize] - _myRLEEnd;
			System.arraycopy(_myTmpData, offs, _myTmpRead, 0, _myRowSize[y + z * _myHeader._myYsize]);
			int iPtr = 0;
			int oPtr = 0;
			for (;;) {
				byte pixel = _myTmpRead[iPtr++];
				int count = pixel & 0x7F;
				if (count == 0) {
					return;
				}
				if ((pixel & 0x80) != 0) {
					while ((count--) > 0) {
						buf[oPtr++] = _myTmpRead[iPtr++];
					}
				} else {
					pixel = _myTmpRead[iPtr++];
					while ((count--) > 0) {
						buf[oPtr++] = pixel;
					}
				}
			}
		} else {
			int offs = (y * _myHeader._myXsize) + (z * _myHeader._myXsize * _myHeader._myYsize);
			System.arraycopy(_myTmpData, offs, buf, 0, _myHeader._myXsize);
		}
	}

	private void bwtorgba(byte[] b, byte[] dest, int lptr) {
		for (int i = 0; i < b.length; i++) {
			dest[4 * i + lptr + 0] = b[i];
			dest[4 * i + lptr + 1] = b[i];
			dest[4 * i + lptr + 2] = b[i];
			dest[4 * i + lptr + 3] = (byte) 0xFF;
		}
	}

	private void latorgba(byte[] b, byte[] a, byte[] dest, int lptr) {
		for (int i = 0; i < b.length; i++) {
			dest[4 * i + lptr + 0] = b[i];
			dest[4 * i + lptr + 1] = b[i];
			dest[4 * i + lptr + 2] = b[i];
			dest[4 * i + lptr + 3] = a[i];
		}
	}

	private void rgbtorgba(byte[] r, byte[] g, byte[] b, byte[] dest, int lptr) {
		for (int i = 0; i < b.length; i++) {
			dest[4 * i + lptr + 0] = r[i];
			dest[4 * i + lptr + 1] = g[i];
			dest[4 * i + lptr + 2] = b[i];
			dest[4 * i + lptr + 3] = (byte) 0xFF;
		}
	}

	private void rgbatorgba(byte[] r, byte[] g, byte[] b, byte[] a, byte[] dest, int lptr) {
		for (int i = 0; i < b.length; i++) {
			dest[4 * i + lptr + 0] = r[i];
			dest[4 * i + lptr + 1] = g[i];
			dest[4 * i + lptr + 2] = b[i];
			dest[4 * i + lptr + 3] = a[i];
		}
	}

	private static byte imgref(byte[] i, int x, int y, int z, int xs, int ys, int zs) {
		return i[(xs * ys * z) + (xs * y) + x];
	}

	private void writeHeader(DataOutputStream stream, int xsize, int ysize, int zsize, boolean rle) throws IOException {
		// effects: outputs the 512-byte IRIS RGB header to STREAM, using xsize,
		// ysize, and depth as the dimensions of the image. NOTE that
		// the following defaults are used:
		// STORAGE = 1 (storage format = RLE)
		// BPC = 1 (# bytes/channel)
		// DIMENSION = 3
		// PIXMIN = 0
		// PIXMAX = 255
		// IMAGENAME = <80 nulls>
		// COLORMAP = 0
		// See ftp://ftp.sgi.com/pub/sgi/SGIIMAGESPEC for more details.

		// write out MAGIC, STORAGE, BPC
		stream.writeShort(474);
		stream.write((rle ? 1 : 0));
		stream.write(1);

		// write out DIMENSION
		stream.writeShort(3);

		// write XSIZE, YSIZE, ZSIZE
		stream.writeShort(xsize);
		stream.writeShort(ysize);
		stream.writeShort(zsize);

		// write PIXMIN, PIXMAX
		stream.writeInt(0);
		stream.writeInt(255);

		// write DUMMY
		stream.writeInt(0);

		// write IMAGENAME
		for (int i = 0; i < 80; i++)
			stream.write(0);

		// write COLORMAP
		stream.writeInt(0);

		// write DUMMY (404 bytes)
		for (int i = 0; i < 404; i++)
			stream.write(0);
	}

	private void writeImage(Path file, byte[] data, final CCSGIHeader theHeader, boolean yflip) throws IOException{
		int xsize = theHeader._myXsize;
		int ysize = theHeader._myYsize;
		int zsize = theHeader._myNumberOfChannels;
		
		// Input data is in RGBRGBRGB or RGBARGBARGBA format; first unswizzle it
		byte[] tmpData = new byte[xsize * ysize * zsize];
		int dest = 0;
		for (int i = 0; i < zsize; i++) {
			for (int j = i; j < (xsize * ysize * zsize); j += zsize) {
				tmpData[dest++] = data[j];
			}
		}
		data = tmpData;

		// requires: DATA must be an array of size XSIZE * YSIZE * ZSIZE,
		// indexed in the following manner:
		// data[0] ...data[xsize-1] == first row of first channel
		// data[xsize]...data[2*xsize-1] == second row of first channel
		// ... data[(ysize - 1) * xsize]...data[(ysize * xsize) - 1] ==
		// last row of first channel
		// Later channels follow the same format.
		// *** NOTE that "first row" is defined by the BOTTOM ROW of
		// the image. That is, the origin is in the lower left corner.
		// effects: writes out an SGI image to FILE, RLE-compressed, INCLUDING
		// header, of dimensions (xsize, ysize, zsize), and containing
		// the data in DATA. If YFLIP is set, outputs the data in DATA
		// in reverse order vertically (equivalent to a flip about the
		// x axis).

		// Build the offset tables
		int[] starttab = new int[ysize * zsize];
		int[] lengthtab = new int[ysize * zsize];

		// Temporary buffer for holding RLE data.
		// Note that this makes the assumption that RLE-compressed data will
		// never exceed twice the size of the input data.
		// There are surely formal proofs about how big the RLE buffer should
		// be, as well as what the optimal look-ahead size is (i.e. don't switch
		// copy/repeat modes for less than N repeats). However, I'm going from
		// empirical evidence here; the break-even point seems to be a look-
		// ahead of 3. (That is, if the three values following this one are all
		// the same as the current value, switch to repeat mode.)
		int lookahead = 3;
		byte[] rlebuf = new byte[2 * xsize * ysize * zsize];

		int cur_loc = 0; // current offset location.
		int ptr = 0;
		int total_size = 0;
		int ystart = 0;
		int yincr = 1;
		int yend = ysize;

		if (yflip) {
			ystart = ysize - 1;
			yend = -1;
			yincr = -1;
		}

		boolean DEBUG = false;

		for (int z = 0; z < zsize; z++) {
			for (int y = ystart; y != yend; y += yincr) {
				// RLE-compress each row.

				int x = 0;
				byte count = 0;
				boolean repeat_mode = false;
				boolean should_switch = false;
				int start_ptr = ptr;
				int num_ptr = ptr++;
				byte repeat_val = 0;

				while (x < xsize) {
					// see if we should switch modes
					should_switch = false;
					if (repeat_mode) {
						if (imgref(data, x, y, z, xsize, ysize, zsize) != repeat_val) {
							should_switch = true;
						}
					} else {
						// look ahead to see if we should switch to repeat mode.
						// stay within the scanline for the lookahead
						if ((x + lookahead) < xsize) {
							should_switch = true;
							for (int i = 1; i <= lookahead; i++) {
								if (DEBUG)
									CCLog.info("left side was " + ((int) imgref(data, x, y, z, xsize, ysize, zsize)) + ", right side was "
											+ (int) imgref(data, x + i, y, z, xsize, ysize, zsize));

								if (imgref(data, x, y, z, xsize, ysize, zsize) != imgref(data, x + i, y, z, xsize, ysize, zsize))
									should_switch = false;
							}
						}
					}

					if (should_switch || (count == 127)) {
						// update the number of elements we repeated/copied
						if (x > 0) {
							if (repeat_mode)
								rlebuf[num_ptr] = count;
							else
								rlebuf[num_ptr] = (byte) (count | 0x80);
						}
						// perform mode switch if necessary; output repeat_val if
						// switching FROM repeat mode, and set it if switching
						// TO repeat mode.
						if (repeat_mode) {
							if (should_switch)
								repeat_mode = false;
							rlebuf[ptr++] = repeat_val;
						} else {
							if (should_switch)
								repeat_mode = true;
							repeat_val = imgref(data, x, y, z, xsize, ysize, zsize);
						}

						if (x > 0) {
							// reset the number pointer
							num_ptr = ptr++;
							// reset number of bytes copied
							count = 0;
						}
					}

					// if not in repeat mode, copy element to ptr
					if (!repeat_mode) {
						rlebuf[ptr++] = imgref(data, x, y, z, xsize, ysize, zsize);
					}
					count++;

					if (x == xsize - 1) {
						// Need to store the number of pixels we copied/repeated.
						if (repeat_mode) {
							rlebuf[num_ptr] = count;
							// If we ended the row in repeat mode, store the
							// repeated value
							rlebuf[ptr++] = repeat_val;
						} else
							rlebuf[num_ptr] = (byte) (count | 0x80);

						// output zero counter for the last value in the row
						rlebuf[ptr++] = 0;
					}

					x++;
				}
				// output this row's length into the length table
				int rowlen = ptr - start_ptr;
				if (yflip)
					lengthtab[ysize * z + (ysize - y - 1)] = rowlen;
				else
					lengthtab[ysize * z + y] = rowlen;
				// add to the start table, and update the current offset
				if (yflip)
					starttab[ysize * z + (ysize - y - 1)] = cur_loc;
				else
					starttab[ysize * z + y] = cur_loc;
				cur_loc += rowlen;
			}
		}

		// Now we have the offset tables computed, as well as the RLE data.
		// Output this information to the file.
		total_size = ptr;

		if (DEBUG)
			CCLog.info("total_size was " + total_size);

		DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file.toFile())));

		writeHeader(stream, xsize, ysize, zsize, true);

		int SIZEOF_INT = 4;
		for (int i = 0; i < (ysize * zsize); i++)
			stream.writeInt(starttab[i] + 512 + (2 * ysize * zsize * SIZEOF_INT));
		for (int i = 0; i < (ysize * zsize); i++)
			stream.writeInt(lengthtab[i]);
		for (int i = 0; i < total_size; i++)
			stream.write(rlebuf[i]);

		stream.close();
	}

	private byte[] readAll(DataInputStream in) throws IOException {
		byte[] dest = new byte[16384];
		int pos = 0;
		int numRead = 0;

		boolean done = false;

		do {
			numRead = in.read(dest, pos, dest.length - pos);
			if (pos == dest.length) {
				// Resize destination buffer
				byte[] newDest = new byte[2 * dest.length];
				System.arraycopy(dest, 0, newDest, 0, pos);
				dest = newDest;
			}
			if (numRead > 0) {
				pos += numRead;
			}

			done = ((numRead == -1) || (in.available() == 0));
		} while (!done);

		// Trim destination buffer
		if (pos != dest.length) {
			byte[] finalDest = new byte[pos];
			System.arraycopy(dest, 0, finalDest, 0, pos);
			dest = finalDest;
		}

		return dest;
	}
}
