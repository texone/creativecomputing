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

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.image.CCImageUtil;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.io.CCNIOUtil;

public class CCImageIOFormat implements CCImageFormat {
	
	private CCImage createImage(
		BufferedImage theImage, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat,
		final String theFileSuffix	
	) {
		if (theImage == null) {
			return null;
		}
		
		if(theImage.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
			theImage = CCImageUtil.convert(theImage, BufferedImage.TYPE_4BYTE_ABGR);
		}
		
		if (CCImageIO.DEBUG) {
			CCLog.info("TextureIO.newImage(): BufferedImage type for stream = " + theImage.getType());
		}
		
		CCImage myImage = new CCImage();
		if (theInternalFormat == null) {
			myImage.internalFormat(theImage.getColorModel().hasAlpha() ? CCPixelInternalFormat.RGBA : CCPixelInternalFormat.RGB);
		} else {
			myImage.internalFormat(theInternalFormat);
		}
		myImage.pixelFormat(thePixelFormat);
		
		return CCImageUtil.toImage(theImage, myImage);
	}
	
	@Override
	public CCImage createImage(
		final Path theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			return createImage(ImageIO.read(Files.newInputStream(theFile)), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}

	@Override
	public CCImage createImage(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){

		try {
			return createImage(ImageIO.read(theStream), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}

	@Override
	public CCImage createImage(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		
		try {
			final InputStream myStream = theUrl.openStream();
			try {
				return createImage(myStream, theInternalFormat, thePixelFormat, theFileSuffix);
			} finally {
				myStream.close();
			}
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}
	
	@Override
	public boolean write(final Path thePath, final CCImage theData) throws CCImageException {
			
		// Convert Image to appropriate BufferedImage
		BufferedImage myImage = CCImageUtil.toBufferedImage(theData);
			
		// Happened to notice that writing RGBA images to JPEGS is broken
		if (CCImageFormats.JPG.fileExtension.equals(CCNIOUtil.fileExtension(thePath)) && myImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			BufferedImage tmpImage = new BufferedImage(myImage.getWidth(), myImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = tmpImage.getGraphics();
			g.drawImage(myImage, 0, 0, null);
			g.dispose();
			myImage = tmpImage;
		}
		try {
			return ImageIO.write(myImage, CCNIOUtil.fileExtension(thePath), Files.newOutputStream(thePath));
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}
}
