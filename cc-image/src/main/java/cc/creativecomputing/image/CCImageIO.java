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

import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.image.format.CCDDSFormat;
import cc.creativecomputing.image.format.CCImageFormat;
import cc.creativecomputing.image.format.CCImageIOFormat;
import cc.creativecomputing.image.format.CCKTXFormat;
import cc.creativecomputing.image.format.CCSGIFormat;
import cc.creativecomputing.image.format.CCTGAFormat;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.CCWriteMode;

/**
 * <p>
 * Provides input and output facilities for both loading OpenGL textures from disk and streams as well as writing
 * textures already in memory back to disk.
 * </p>
 * 
 * <p>
 * The CCImageIO class supports an arbitrary number of plug-in formats via CCImageFormats.
 * CCImageFormats know how to produce Image objects from files, InputStreams and URLs.
 * and to write Image objects to disk in various file formats. The CCImage class
 * represents the raw data of the texture before it has been converted to an CCImage object. 
 * The CCImage class represents the OpenGL texture object and provides easy facilities for 
 * using the texture.
 * </p>
 * <p>
 * There are several built-in CCImageFormats supplied with the CCImageIO implementation. 
 * The most basic format uses the platform's Image I/O facilities to read in a BufferedImage 
 * and convert it to a texture. This is the baseline format and is registered so that it is the 
 * last one consulted. All others are asked first to open a given file.
 * </p>
 * <p>
 * There are three other formats registered by default. One handles SGI RGB (".sgi",".rgb") images 
 * from both files and streams. One handles DirectDraw Surface (".dds") images read from files, 
 * though can not read these images from streams. One handles Targa (".tga") images read from 
 * both files and streams. These formats are executed in an arbitrary order. Some of these 
 * formats require the file's suffix to either be specified via the newImage methods or 
 * for the file to be named with the appropriate suffix. In general a file suffix should be 
 * provided to the newImage and newImage methods if at all possible.
 * </p>
 * <p>
 * Note that additional CCImageFormats, if reading images from InputStreams, must use the 
 * mark()/reset() methods on InputStream when probing for e.g. magic numbers at the head of the 
 * file to make sure not to disturb the state of the InputStream for downstream CCImageFormats.
 * </p>
 * <p>
 * The CCImageFormats can also be used for writing textures back to disk if desired. 
 * Some of formats have certain limitations such as only being able to write out textures 
 * stored in RGB or RGBA format. The DDS writer supports fetching and writing to disk of 
 * texture data in DXTn compressed format. Whether this will occur is dependent on
 * whether the texture's internal format is one of the DXTn compressed formats and whether 
 * the target file is .dds format.
 * </p>
 * 
 * @author Christian Riekoff
 */
public class CCImageIO {
	
	static public enum CCImageFormats{
		DDS("dds"),
		KTX("ktx"),
		SGI("sgi"),
		SGI_RGB("rgb"),
		GIF("gif"),
		JPG("jpg"),
		JPEG("jpg"),
		PNG("png"),
		TGA("tga"),
		TIFF("tiff");
		
		public final String fileExtension;
		
		private CCImageFormats(final String theFileExtension){
			fileExtension = theFileExtension;
		}
	}
	
	public static boolean DEBUG = false;
	
	// HELPERS
	
	private static String toLowerCase(final String theString) {
		if (theString == null) {
			return null;
		}

		return theString.toLowerCase();
	}
	
	/**
	 * Use this function to get a list of textures inside a folder, that can be
	 * loaded be creative computing. Supported texture formats are dds, gif,
	 * jpg, png, sgi, sgi_rgb, tga and tiff. You can also pass the file type of
	 * the textures you want to be listed.
	 * 
	 * @param theFolder
	 * @return
	 */
	public static List<Path> listImages(final Path theFolder){
		return listImages(
			theFolder, 
			CCImageFormats.DDS, 
			CCImageFormats.GIF, 
			CCImageFormats.JPG, 
			CCImageFormats.JPEG, 
			CCImageFormats.PNG, 
			CCImageFormats.SGI, 
			CCImageFormats.SGI_RGB,
			CCImageFormats.TGA,
			CCImageFormats.TIFF
		);
	}
	
	public static List<Path> listImages(final Path theFolder, final CCImageFormats...theFormat){
		String[] myExtensions = new String[theFormat.length];
		for(int i = 0; i < theFormat.length;i++){
			myExtensions[i] = theFormat[i].fileExtension;
		}
		return CCNIOUtil.list(theFolder, myExtensions);
	}
	
	/**
	 * Use this function to load more than one texture at once, this makes sense
	 * if you want to load all textures of a folder for example. Be aware that
	 * the paths are ordered alphabetically. Files that can not be read, will be
	 * ignored and do not throw an exception.
	 * @shortdesc Loads multiple textures and returns them as list.
	 * @param theImagePaths paths of the textures you want to load
	 * @return a list with all the loaded textures
	 */
	public static List<CCImage> newImages(final Path...theImagePaths){
		List<CCImage> myResult = new ArrayList<CCImage>();
		Arrays.sort(theImagePaths);
		
		for(Path myImage:theImagePaths){
			try {
				myResult.add(newImage(myImage));
			} catch (CCImageException e) {
				// just catch if single files can not be loaded
			}
		}
		
		return myResult;
	}
	
	/**
	 * @param theImagePaths theImagePaths paths of the textures you want to load
	 */
	public static List<CCImage> newImages(final List<Path> theImagePaths){
		List<CCImage> myResult = new ArrayList<CCImage>();
		Collections.sort(theImagePaths);
		
		for(Path myImage:theImagePaths){
			try {
				myResult.add(newImage(myImage));
			} catch (CCImageException e) {
				// just catch if single files can not be loaded
			}
		}
		return myResult;
	}

	/**
	 * @param theFolder folder containing all textures you want to load
	 */
	public static List<CCImage> newImages(final Path theFolder){
		List<Path> myPaths = new ArrayList<Path>();
		for(Path myPath:listImages(theFolder)) {
			myPaths.add(theFolder.resolve(myPath));
		}
		return newImages(myPaths);
	}

	/**
	 * @param theFolder folder containing all textures you want to load
	 * @param theFormat format of the textures you want to load
	 */
	public static List<CCImage> newImages(final Path theFolder, final CCImageFormats...theFormat){
		List<Path> myPaths = new ArrayList<Path>();
		for(Path myPath:listImages(theFolder,theFormat)) {
			myPaths.add(theFolder.resolve(myPath));
		}
		return newImages(myPaths);
	}
	
	////////////////////////////////////////////////////
	//
	// LOAD TEXTURE DATA
	//
	////////////////////////////////////////////////////
	
//	/**
//	 * Creates a CCImage from the given resource.
//	 * @param theImagePath
//	 * 			the resource from which to read the texture data
//	 * @throws CCImageException
//	 * @return the texture data from the resource, or null if none of the registered texture providers could read the file
//	 */
//	public static CCImage newImage(final String theImagePath) {
//		String myLowerFileTyp = theImagePath.toLowerCase();
//		final int myIndex = theImagePath.lastIndexOf('.') + 1;
//		myLowerFileTyp = myLowerFileTyp.substring(myIndex, myLowerFileTyp.length());
//
//		final InputStream myInputStream = CCIOUtil.openStream(theImagePath);
//		if (myInputStream == null) {
//			throw new CCImageException("The Image you tried to load is not availabe!:" + CCIOUtil.dataPath(theImagePath));
//		}
//		
//		return newImage(CCIOUtil.openStream(theImagePath), myLowerFileTyp);
//	}
	
	/**
	 * Creates a CCImage from the given resource.
	 * @param thePath
	 * 			the file from which to read the texture data
	 * @param theFileSuffix 
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the resource, or null if none of the registered texture providers could read the file
	 */
	public static CCImage newImage(final Path thePath, final String theFileSuffix){
		return newImageImpl(thePath, null, null, theFileSuffix);
	}
	
	public static CCImage newImage(final Path thePath){
		return newImageImpl(thePath, null, null, null);
	}
	
	/**
	 * Creates a CCImage from the given resource.
	 * @param theStream
	 * 			the stream from which to read the texture data
	 * @param theFileSuffix
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the stream, or null if none of the registered texture providers could read the file
	 */
	public static CCImage newImage(final InputStream theStream, final String theFileSuffix){
		return newImageImpl(theStream, null, null, theFileSuffix);
	}
	
	/**
	 * Creates a CCImage from the given resource.
	 * @param theUrl
	 * 			the url from which to read the texture data
	 * @param theFileSuffix
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the url, or null if none of the registered texture providers could read the file
	 */
	public static CCImage newImage(final URL theUrl, String theFileSuffix){
		if (theFileSuffix == null) {
			theFileSuffix = CCIOUtil.fileExtension(theUrl.getPath());
		}
		return newImageImpl(theUrl, null, null, theFileSuffix);
	}
	
	/**
	 * Creates a CCImage from the given buffered image.
	 * @param theImage
	 * 			the image from which to read the texture data
	 * @return the texture data from the buffered image, or null if none of the registered texture providers could read the file
	 */
	public static CCImage newImage(final Image theImage) {
		return newImageImpl(theImage, null, null);
	}

	//----------------------------------------------------------------------
	// These methods make no assumption about the OpenGL internal format
	// or pixel format of the texture; they must be specified by the
	// user. It is not allowed to supply 0 (indicating no preference)
	// for either the internalFormat or the pixelFormat;
	// IllegalArgumentException will be thrown in this case.

	/**
	 * Creates a Image from the given file, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param thePath the file from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the Image
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the Image
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the file, or null if none of the registered texture providers could read the file
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the file
	 * @invisible
	 */
	public static CCImage newImage(
		final Path thePath, 
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new CCImageException("internalFormat and pixelFormat must be non-zero");
		}

		return newImageImpl(thePath, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a Image from the given stream, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param theStream the stream from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the Image
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the Image
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the stream, or null if none of the registered texture providers could read the
	 *         stream
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the stream
	 */
	public static CCImage newImage(
		final InputStream theStream,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		final String theFileSuffix
	){
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new CCImageException("internalFormat and pixelFormat must be non-zero");
		}

		return newImageImpl(theStream, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a Image from the given URL, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param theUrl the URL from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the Image
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the Image
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the URL, or null if none of the registered texture providers could read the URL
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the URL
	 */
	public static CCImage newImage(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	)throws IOException, IllegalArgumentException {
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new IllegalArgumentException("internalFormat and pixelFormat must be non-zero");
		}

		return newImageImpl(theUrl, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a Image from the given BufferedImage, using the specified OpenGL internal format and pixel format
	 * for the texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be
	 * zero; to use default values, use the variant of this method which does not take these arguments. Does no OpenGL
	 * work.
	 * 
	 * @param theImage the BufferedImage containing the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the Image
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the Image
	 * @return the texture data from the image
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 */
	public static CCImage newImage(
		final Image theImage,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat
	)throws IllegalArgumentException {
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new IllegalArgumentException("internalFormat and pixelFormat must be non-zero");
		}

		return newImageImpl(theImage, theInternalFormat, thePixelFormat);
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theImage the data to be written to disk
	 * @param thePath file to save to
	 * @param theWriteMode
	 */
	public static void write(final CCImage theImage, final Path thePath, final CCWriteMode theWriteMode, double theQuality){
		switch(theWriteMode){
		case KEEP:
			if(CCNIOUtil.exists(thePath))return;
		default:
		}
		String myExtension = CCNIOUtil.fileExtension(thePath);
		
		CCImageFormat myFormat = textureFormats.get(myExtension);
		
		if(myFormat == null)throw new CCImageException("The Image format:" + myExtension + " is not supported.");
		
		if (myFormat.write(thePath, theImage, theQuality)) {
			return;
		}
		
		throw new CCImageException("The given image could not be written.");
	}
	
	public static void write(final CCImage theImage, final Path thePath, double theQuality){
		write(theImage, thePath, CCWriteMode.OVERWRITE, theQuality);
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theImage the data to be written to disk
	 * @param thePath file to save to
	 */
	public static void write(final CCImage theImage, final String thePath, double theQuality){
		try {
			write(theImage,thePath,CCWriteMode.OVERWRITE, theQuality);
		} catch (Exception e) {
			throw new RuntimeException("Problems writing file "+thePath,e);
		}
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theImage the data to be written to disk
	 * @param thePath file to save to
	 * @param theWriteMode
	 */
	public static void write(final CCImage theImage, final String thePath, final CCWriteMode theWriteMode, double theQuality){
		try {
			write(theImage,CCNIOUtil.dataPath(thePath),theWriteMode, theQuality);
		} catch (Exception e) {
			throw new RuntimeException("Problems writing file "+thePath,e);
		}
	}
	
	private static Map<String, CCImageFormat> textureFormats = new HashMap<String, CCImageFormat>();
	public static boolean QTinitialized;
	
	/** 
	 * Adds a ImageProvider to support reading of a new file format. 
	 * @param theFormat the format to add
	 **/
	public static void addImageFormat(String theExtension, final CCImageFormat theFormat) {
		// Must always add at the front so the ImageIO provider is last,
		// so we don't accidentally use it instead of a user's possibly
		// more optimal provider
		textureFormats.put(theExtension, theFormat);
	}
	
	public static CCImageFormat IMAGE_IO_FORMAT = new CCImageIOFormat();
	public static CCImageFormat DDS_FORMAT = new CCDDSFormat();
	public static CCImageFormat SGI_FORMAT = new CCSGIFormat();
	public static CCImageFormat TGA_FORMAT = new CCTGAFormat();
	public static CCImageFormat KTX_FORMAT = new CCKTXFormat();

	static {
		
		// ImageIO provider, the fall-back, must be the first one added
		addImageFormat("png", IMAGE_IO_FORMAT);
		addImageFormat("jpg", IMAGE_IO_FORMAT);
		addImageFormat("jpeg", IMAGE_IO_FORMAT);
		addImageFormat("gif", IMAGE_IO_FORMAT);

		// Other special-case providers
		addImageFormat("dds", DDS_FORMAT);
		addImageFormat("sgi", SGI_FORMAT);
		addImageFormat("tga", TGA_FORMAT);
		addImageFormat("ktx", KTX_FORMAT);
	}

	private static CCImage newImageImpl(
		final Path thePath,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	) {
		if (thePath == null) {
			throw new CCImageException("File was null");
		}

		if (theFileSuffix == null) {
			theFileSuffix = CCNIOUtil.fileExtension(thePath);
		}
		
		theFileSuffix = toLowerCase(theFileSuffix);
		
		CCImageFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCImageException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCImage data = myFormat.createImage(thePath, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCImageException("The given image could not be loaded.");
	}

	private static CCImage newImageImpl(
		InputStream theInputStream,
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	){
		if (theInputStream == null) {
			throw new CCImageException("Stream was null");
		}

		theFileSuffix = toLowerCase(theFileSuffix);

		// Note: use of BufferedInputStream works around 4764639/4892246
		if (!(theInputStream instanceof BufferedInputStream)) {
			theInputStream = new BufferedInputStream(theInputStream);
		}
		
		CCImageFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCImageException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCImage data = myFormat.createImage(theInputStream, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCImageException("The given image could not be loaded.");
	}

	private static CCImage newImageImpl(
		final URL theURL,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	){
		if (theURL == null) {
			throw new CCImageException("URL was null");
		}
		
		if (theFileSuffix == null) {
			theFileSuffix = CCIOUtil.fileExtension(theURL.getPath());
		}

		theFileSuffix = toLowerCase(theFileSuffix);

		CCImageFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCImageException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCImage data = myFormat.createImage(theURL, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCImageException("The given image could not be loaded.");
	}

	/**
	 * 
	 * @param theImage
	 * @param theInternalFormat
	 * @param thePixelFormat
	 * @return
	 */
	private static CCImage newImageImpl(
		final Image theImage,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat
	) {
		CCImage myImage = new CCImage();
		myImage.internalFormat(theInternalFormat);
		myImage.pixelFormat(thePixelFormat);
		
		return CCImageUtil.toImage(theImage, myImage);
	}
	
	public static CCImage loadCubeMapData(
		final Path thePositiveX, final Path theNegativeX,
		final Path thePositiveY, final Path theNegativeY,
		final Path thePositiveZ, final Path theNegativeZ
	) {
		Buffer[] myBuffers = new Buffer[6];
		CCImage myImage = newImage(thePositiveX);
		myBuffers[0] = myImage.buffer();
		myBuffers[1] = newImage(theNegativeX).buffer();
		myBuffers[2] = newImage(thePositiveY).buffer();
		myBuffers[3] = newImage(theNegativeY).buffer();
		myBuffers[4] = newImage(thePositiveZ).buffer();
		myBuffers[5] = newImage(theNegativeZ).buffer();
		myImage.buffers(myBuffers);
		
		return myImage;
	}
}
