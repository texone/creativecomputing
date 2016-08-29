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
package cc.creativecomputing.graphics.export;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.format.CCImageIOFormat;
import cc.creativecomputing.io.CCNIOUtil;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLException;

/** Utilities for taking screenshots of OpenGL applications. */

public class CCScreenCapture {
	
	/**
	 * Utility class which helps take fast screenshots of OpenGL rendering results
	 * into Targa-format files. Used by the {@link com.sun.opengl.util.Screenshot
	 * Screenshot} class; can also be used in conjunction with the
	 * {@link com.sun.opengl.util.TileRenderer TileRenderer} class.
	 * <P>
	 */

	private static class TGAWriter {
		private static final int TARGA_HEADER_SIZE = 18;

		private FileChannel _myOutputChannel;
		private ByteBuffer buf;

		/** Constructor for the TGAWriter. */
		public TGAWriter() {
		}

		/**
		 * Opens the specified Targa file for writing, overwriting any existing
		 * file, and sets up the header of the file expecting the data to be filled
		 * in before closing it.
		 * 
		 * @param file the file to write containing the screenshot
		 * @param width the width of the current drawable
		 * @param height the height of the current drawable
		 * @param alpha whether the alpha channel should be saved. If true, requires GL_EXT_abgr extension to be present.
		 * 
		 * @throws IOException if an I/O error occurred while writing the file
		 */
		public boolean open(File file, int width, int height, boolean alpha) {
			try {
				RandomAccessFile myOutputFile = new RandomAccessFile(file, "rw");
				_myOutputChannel = myOutputFile.getChannel();
				int pixelSize = (alpha ? 32 : 24);
				int numChannels = (alpha ? 4 : 3);
				int fileLength = TARGA_HEADER_SIZE + width * height * numChannels;
				myOutputFile.setLength(fileLength);
				MappedByteBuffer image = _myOutputChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileLength);
				myOutputFile.close();
				// write the TARGA header
				image.put(0, (byte) 0).put(1, (byte) 0);
				image.put(2, (byte) 2); // uncompressed type
				image.put(12, (byte) (width & 0xFF)); // width
				image.put(13, (byte) (width >> 8)); // width
				image.put(14, (byte) (height & 0xFF)); // height
				image.put(15, (byte) (height >> 8)); // height
				image.put(16, (byte) pixelSize); // pixel size
				// go to image data position
				image.position(TARGA_HEADER_SIZE);
				// jogl needs a sliced buffer
				buf = image.slice();
				return true;
			} catch (Exception e) {
				CCLog.error("COULD NOT WRITE TGA FILE:" + file.getName());
				return false;
			}
		}

		/**
		 * Returns the ByteBuffer corresponding to the data for the image. This must
		 * be filled in with data in either BGR or BGRA format depending on whether
		 * an alpha channel was specified during open().
		 */
		public ByteBuffer getImageData() {
			return buf;
		}

		public void close() throws IOException {
			// close the file channel
			_myOutputChannel.close();
			buf = null;
		}
	}
	
	static class PixelStorageModes {
		int packAlignment;
		int packRowLength;
		int packSkipRows;
		int packSkipPixels;
		int packSwapBytes;
		int[] tmp = new int[1];

		void save(GL gl) {
			packAlignment = glGetInteger(gl, GL.GL_PACK_ALIGNMENT, tmp);
			packRowLength = glGetInteger(gl, GL2.GL_PACK_ROW_LENGTH, tmp);
			packSkipRows = glGetInteger(gl, GL2.GL_PACK_SKIP_ROWS, tmp);
			packSkipPixels = glGetInteger(gl, GL2.GL_PACK_SKIP_PIXELS, tmp);
			packSwapBytes = glGetInteger(gl, GL2.GL_PACK_SWAP_BYTES, tmp);

			gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
			gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, 0);
			gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, 0);
			gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, 0);
			gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, 0);
		}

		void restore(GL gl) {
			gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, packAlignment);
			gl.glPixelStorei(GL2.GL_PACK_ROW_LENGTH, packRowLength);
			gl.glPixelStorei(GL2.GL_PACK_SKIP_ROWS, packSkipRows);
			gl.glPixelStorei(GL2.GL_PACK_SKIP_PIXELS, packSkipPixels);
			gl.glPixelStorei(GL2.GL_PACK_SWAP_BYTES, packSwapBytes);
		}
	}

	/**
	 * Takes a fast screenshot of the current OpenGL drawable to a Targa file.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). This is the fastest mechanism for taking a screenshot of
	 * an application. Contributed by Carsten Weisse of Bytonic Software
	 * (http://bytonic.de/).
	 * <p>
	 * 
	 * No alpha channel is written with this variant.
	 * 
	 * @param file the file to write containing the screenshot
	 * @param width the width of the desired capture area
	 * @param height the height of the desired capture area
	 */
	public static void writeToTargaFile(final Path thePath, int width, int height){
		writeToTargaFile(thePath, width, height, false);
	}

	/**
	 * Takes a fast screenshot of the current OpenGL drawable to a Targa file.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). This is the fastest mechanism for taking a screenshot of
	 * an application. Contributed by Carsten Weisse of Bytonic Software
	 * (http://bytonic.de/).
	 * 
	 * @param file
	 *            the file to write containing the screenshot
	 * @param width
	 *            the width of the current drawable
	 * @param height
	 *            the height of the current drawable
	 * @param alpha
	 *            whether the alpha channel should be saved. If true, requires
	 *            GL_EXT_abgr extension to be present.
	 */
	public static void writeToTargaFile(final Path thePath, int width, int height, boolean alpha){
		writeToTargaFile(thePath, 0, 0, width, height, alpha);
	}

	/**
	 * Takes a fast screenshot of the current OpenGL drawable to a Targa file.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). This is the fastest mechanism for taking a screenshot of
	 * an application. Contributed by Carsten Weisse of Bytonic Software
	 * (http://bytonic.de/).
	 * 
	 * @param file the file to write containing the screenshot
	 * @param x the starting x coordinate of the screenshot, measured from the lower-left
	 * @param y the starting y coordinate of the screenshot, measured from the lower-left
	 * @param width the width of the desired screenshot area
	 * @param height the height of the desired screenshot area
	 * @param alpha whether the alpha channel should be saved. If true, requires GL_EXT_abgr extension to be present.
	 * 
	 * @throws GLException
	 *             if an OpenGL context was not current or another
	 *             OpenGL-related error occurred
	 * @throws IOException
	 *             if an I/O error occurred while writing the file
	 */
	public static void writeToTargaFile(
		final Path thePath, 
		int x, int y, int width, int height, 
		boolean alpha
	){
		if (alpha) {
			checkExtABGR();
		}

		try{
			TGAWriter myWriter = new TGAWriter();
			
			boolean myHasOpen = false;
			
			while(!myHasOpen){
				myHasOpen = myWriter.open(thePath.toFile(), width, height, alpha);
			}
			ByteBuffer bgr = myWriter.getImageData();
	
			GL gl = CCGraphics.currentGL();
	
			// Set up pixel storage modes
			PixelStorageModes psm = new PixelStorageModes();
			psm.save(gl);
	
			int readbackType = (alpha ? GL2.GL_ABGR_EXT : GL2.GL_BGR);
	
			// read the BGR values into the image buffer
			gl.glReadPixels(x, y, width, height, readbackType, GL.GL_UNSIGNED_BYTE, bgr);
	
			// Restore pixel storage modes
			psm.restore(gl);
	
			// close the file
			myWriter.close();
		}catch(Exception e){
			throw new CCScreenCaptureException("",e);
		}
	}

	/**
	 * Takes a screenshot of the current OpenGL drawable to a BufferedImage.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). Note that the scanlines of the resulting image are
	 * flipped vertically in order to correctly match the OpenGL contents, which
	 * takes time and is therefore not as fast as the Targa screenshot function.
	 * <P>
	 * 
	 * No alpha channel is read back with this variant.
	 * 
	 * @param width
	 *            the width of the current drawable
	 * @param height
	 *            the height of the current drawable
	 * 
	 * @throws GLException
	 *             if an OpenGL context was not current or another
	 *             OpenGL-related error occurred
	 */
	public static BufferedImage readToBufferedImage(int width, int height)throws GLException  {
		return capture(width, height, false);
	}

	/**
	 * Takes a screenshot of the current OpenGL drawable to a BufferedImage.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). Note that the scanlines of the resulting image are
	 * flipped vertically in order to correctly match the OpenGL contents, which
	 * takes time and is therefore not as fast as the Targa screenshot function.
	 * 
	 * @param width
	 *            the width of the current drawable
	 * @param height
	 *            the height of the current drawable
	 * @param alpha
	 *            whether the alpha channel should be read back. If true,
	 *            requires GL_EXT_abgr extension to be present.
	 */
	public static BufferedImage capture(int width, int height, boolean alpha){
		return readToBufferedImage(0, 0, width, height, alpha);
	}
	
	/** 
	 * Flips the supplied BufferedImage vertically. This is often a
	 * necessary conversion step to display a Java2D image correctly
	 * with OpenGL and vice versa. 
	 **/
	private static void flipImageVertically(BufferedImage image) {
		WritableRaster raster = image.getRaster();

		Object scanline1 = null;
		Object scanline2 = null;

		for (int i = 0; i < image.getHeight() / 2; i++) {
			scanline1 = raster.getDataElements(0, i, image.getWidth(), 1, scanline1);
			scanline2 = raster.getDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline2);
			raster.setDataElements(0, i, image.getWidth(), 1, scanline2);
			raster.setDataElements(0, image.getHeight() - i - 1, image.getWidth(), 1, scanline1);
		}
	}

	/**
	 * Takes a screenshot of the current OpenGL drawable to a BufferedImage.
	 * Requires the OpenGL context for the desired drawable to be current. Takes
	 * the screenshot from the last assigned read buffer, or the OpenGL default
	 * read buffer if none has been specified by the user (GL_FRONT for
	 * single-buffered configurations and GL_BACK for double-buffered
	 * configurations). Note that the scanlines of the resulting image are
	 * flipped vertically in order to correctly match the OpenGL contents, which
	 * takes time and is therefore not as fast as the Targa screenshot function.
	 * 
	 * @param x
	 *            the starting x coordinate of the screenshot, measured from the
	 *            lower-left
	 * @param y
	 *            the starting y coordinate of the screenshot, measured from the
	 *            lower-left
	 * @param width
	 *            the width of the desired screenshot area
	 * @param height
	 *            the height of the desired screenshot area
	 * @param alpha
	 *            whether the alpha channel should be read back. If true,
	 *            requires GL_EXT_abgr extension to be present.
	 * 
	 */
	public static BufferedImage readToBufferedImage(int x, int y, int width, int height, boolean alpha){
		try{
		int bufImgType = (alpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
		int readbackType = (alpha ? GL2.GL_ABGR_EXT : GL2.GL_BGR);

		if (alpha) {
			checkExtABGR();
		}

		// Allocate necessary storage
		BufferedImage image = new BufferedImage(width, height, bufImgType);

		GL gl = CCGraphics.currentGL();

		// Set up pixel storage modes
		PixelStorageModes psm = new PixelStorageModes();
		psm.save(gl);

		// read the BGR values into the image
		gl.glReadPixels(x, y, width, height, readbackType, GL.GL_UNSIGNED_BYTE,ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()));

		// Restore pixel storage modes
		psm.restore(gl);

		// Must flip BufferedImage vertically for correct results
		flipImageVertically(image);
		return image;
		}catch(Exception e){
			throw new CCScreenCaptureException("",e);
		}
	}

	/**
	 * Takes a screenshot of the current OpenGL drawable to the specified file
	 * on disk using the ImageIO package. Requires the OpenGL context for the
	 * desired drawable to be current. Takes the screenshot from the last
	 * assigned read buffer, or the OpenGL default read buffer if none has been
	 * specified by the user (GL_FRONT for single-buffered configurations and
	 * GL_BACK for double-buffered configurations). This is not the fastest
	 * mechanism for taking a screenshot but may be more convenient than others
	 * for getting images for consumption by other packages. The file format is
	 * inferred from the suffix of the given file.
	 * <P>
	 * 
	 * No alpha channel is saved with this variant.
	 * 
	 * @param thePath
	 *            the file to write containing the screenshot
	 * @param theWidth
	 *            the width of the current drawable
	 * @param theHeight
	 *            the height of the current drawable
	 */
	public static void capture(final Path thePath, final int theWidth, final int theHeight) {
		capture(thePath, theWidth, theHeight, false);
	}

	/**
	 * Takes a screenshot of the current OpenGL drawable to the specified file
	 * on disk using the ImageIO package. Requires the OpenGL context for the
	 * desired drawable to be current. Takes the screenshot from the last
	 * assigned read buffer, or the OpenGL default read buffer if none has been
	 * specified by the user (GL_FRONT for single-buffered configurations and
	 * GL_BACK for double-buffered configurations). This is not the fastest
	 * mechanism for taking a screenshot but may be more convenient than others
	 * for getting images for consumption by other packages. The file format is
	 * inferred from the suffix of the given file.
	 * <P>
	 * 
	 * Note that some file formats, in particular JPEG, can not handle an alpha
	 * channel properly. If the "alpha" argument is specified as true for such a
	 * file format it will be silently ignored.
	 * 
	 * @param file
	 *            the file to write containing the screenshot
	 * @param width
	 *            the width of the current drawable
	 * @param height
	 *            the height of the current drawable
	 * @param alpha
	 *            whether an alpha channel should be saved. If true, requires
	 *            GL_EXT_abgr extension to be present.
	 */
	public static void capture(Path file, int width, int height, boolean alpha){
		capture(file, 0, 0, width, height, alpha, 1);
	}
	
	private static CCImageIOFormat _myFormat = new CCImageIOFormat();

	/**
	 * Takes a screenshot of the current OpenGL drawable to the specified file
	 * on disk using the ImageIO package. Requires the OpenGL context for the
	 * desired drawable to be current. Takes the screenshot from the last
	 * assigned read buffer, or the OpenGL default read buffer if none has been
	 * specified by the user (GL_FRONT for single-buffered configurations and
	 * GL_BACK for double-buffered configurations). This is not the fastest
	 * mechanism for taking a screenshot but may be more convenient than others
	 * for getting images for consumption by other packages. The file format is
	 * inferred from the suffix of the given file.
	 * <P>
	 * 
	 * Note that some file formats, in particular JPEG, can not handle an alpha
	 * channel properly. If the "alpha" argument is specified as true for such a
	 * file format it will be silently ignored.
	 * 
	 * @param file the file to write containing the screenshot
	 * @param x the starting x coordinate of the screenshot, measured from the lower-left
	 * @param y the starting y coordinate of the screenshot, measured from the lower-left
	 * @param width the width of the current drawable
	 * @param height the height of the current drawable
	 * @param alpha whether an alpha channel should be saved
	 * 
	 */
	public static void capture(final Path thePath, int x, int y, int width, int height, boolean alpha, double theQuality){
		String fileSuffix = CCNIOUtil.fileExtension(thePath);
		
		if(fileSuffix == null){
			throw new CCScreenCaptureException("Not able to perform screen capture because of missing file extension.");
		}
		
		if(fileSuffix.equals("tga")){
			writeToTargaFile(thePath, x,y,width, height,alpha);
			return;
		}
		try{
			
			if (alpha && (fileSuffix.equals("jpg") || fileSuffix.equals("jpeg"))) {
				// JPEGs can't deal properly with alpha channels
				alpha = false;
			}
	
			BufferedImage image = readToBufferedImage(x, y, width, height, alpha);
			_myFormat.write(thePath, image, theQuality);
			if (!_myFormat.write(thePath, image, theQuality)) {
				throw new CCScreenCaptureException("Unsupported file format " + fileSuffix);
			}
		}catch(Exception e){
			throw new CCScreenCaptureException(e);
		}
	}

	private static int glGetInteger(GL gl, int pname, int[] tmp) {
		gl.glGetIntegerv(pname, tmp, 0);
		return tmp[0];
	}

	private static void checkExtABGR() {
		GL gl = CCGraphics.currentGL();
		if (!gl.isExtensionAvailable("GL_EXT_abgr")) {
			throw new IllegalArgumentException(
					"Saving alpha channel requires GL_EXT_abgr");
		}
	}

	
}
