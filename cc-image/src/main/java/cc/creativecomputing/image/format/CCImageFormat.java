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

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;

/**
 * @author christian riekoff
 *
 */
public interface CCImageFormat {
	
	/**
	 * Produces an image object from a file, or returns null if the file format was not supported by this
	 * ImageProvider.
	 * 
	 * @param theFile the file from which to read the texture data
	 * 
	 * @param theInternalFormat the internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCImageException if an error occurred while reading the file
	 */
    CCImage createImage(
            Path theFile,
            CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat,
            String theFileSuffix
    ) throws CCImageException;

	/**
	 * Produces an image object from a stream, or returns null if the file format was not supported by this
	 * TextureProvider. 
	 * 
	 * @param theStream the stream from which to read the texture data
	 * 
	 * @param theInternalFormat the internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCImageException if an error occurred while reading the stream
	 */
    CCImage createImage(
            InputStream theStream,
            CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat,
            String theFileSuffix
    ) throws CCImageException;

	/**
	 * Produces an image object from a URL, or returns null if the file format was not supported by this
	 * TextureProvider.
	 * 
	 * @param theUrl the URL from which to read the texture data
	 * 
	 * @param theInternalFormat the internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCImageException if an error occurred while reading the URL
	 */
    CCImage createImage(
            URL theUrl,
            CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat,
            String theFileSuffix
    ) throws CCImageException;

	/**
	 * Writes the given TextureData to the passed file. Returns true if this TextureWriter successfully handled the
	 * writing of the file, otherwise false. May throw IOException if either this writer did not support certain
	 * parameters of the TextureData or if an I/O error occurred.
	 */
    boolean write(Path theFile, CCImage theData, double theQuality) throws CCImageException;
}
