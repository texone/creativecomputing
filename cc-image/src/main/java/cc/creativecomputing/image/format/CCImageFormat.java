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

/**
 * @author christian riekoff
 *
 */
public interface CCImageFormat {

	/**
	 * Produces an image object from a file, or throws an exception the file format was
	 * not supported by this ImageProvider.
	 * 
	 * @param theFile
	 *            the file from which to read the texture data
	 * @param theFlipVertically TODO
	 * 
	 * @throws CCImageException
	 *             if an error occurred while reading the file
	 */
	CCImage createImage(Path theFile, boolean theFlipVertically) throws CCImageException;

	/**
	 * Produces an image object from a stream.
	 * 
	 * @param theStream
	 *            the stream from which to read the texture data
	 * 
	 * @throws CCImageException
	 *             if an error occurred while reading the stream
	 */
	CCImage createImage(InputStream theStream) throws CCImageException;

	/**
	 * Produces an image object from a URL.
	 * 
	 * @param theUrl
	 *            the URL from which to read the texture data
	 * 
	 * @throws CCImageException
	 *             if an error occurred while reading the URL
	 */
	CCImage createImage(URL theUrl) throws CCImageException;

	/**
	 * Writes the given TextureData to the passed file. Returns true if this
	 * TextureWriter successfully handled the writing of the file, otherwise false.
	 * May throw IOException if either this writer did not support certain
	 * parameters of the TextureData or if an I/O error occurred.
	 */
	boolean write(Path theFile, CCImage theData, double theQuality) throws CCImageException;
}
