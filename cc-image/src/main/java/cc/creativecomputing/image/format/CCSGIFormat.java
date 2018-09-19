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
import java.nio.ByteBuffer;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSGIFormat extends CCStreamBasedTextureFormat {
	
	@Override
	public CCImage createImage(final InputStream theStream) throws CCImageException {
		
		if (!CCSGIImage.isSGIImage(theStream)) throw new CCImageException("NO SGI image");
		
		CCSGIImage image = CCSGIImage.read(theStream);

		CCPixelInternalFormat theInternalFormat = image.getInternalFormat();
		CCPixelFormat thePixelFormat  = image.getFormat();

		return new CCImage(
			image.width(), image.getHeight(), 0,
			theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
			false, false, 
			ByteBuffer.wrap(image.getData()), null
		);
	}
	
	@Override
	public boolean write(final Path theFile, final CCImage theData, final double theQuality) throws CCImageException {
		String fileSuffix = CCNIOUtil.fileExtension(theFile);
		if (
			CCImageFormats.SGI.fileExtension.equals(fileSuffix) || 
			CCImageFormats.SGI_RGB.fileExtension.equals(fileSuffix)
		) {
			// See whether the SGI writer can handle this TextureData
			CCPixelFormat pixelFormat = theData.pixelFormat();
			CCPixelType pixelType = theData.pixelType();
			if (
				(pixelFormat == CCPixelFormat.RGB || pixelFormat == CCPixelFormat.RGBA) && 
				(pixelType == CCPixelType.BYTE || pixelType == CCPixelType.UNSIGNED_BYTE)
			) {
				ByteBuffer buf = ((theData.buffer() != null) ? (ByteBuffer) theData.buffer() : (ByteBuffer) theData.mipmapData()[0]);
				byte[] bytes;
				if (buf.hasArray()) {
					bytes = buf.array();
				} else {
					buf.rewind();
					bytes = new byte[buf.remaining()];
					buf.get(bytes);
					buf.rewind();
				}

				CCSGIImage image = CCSGIImage.createFromData(
					theData.width(), theData.height(), 
					(pixelFormat == CCPixelFormat.RGBA),
					bytes
				);
				image.write(theFile, false);
				return true;
			}

			throw new CCImageException("SGI writer doesn't support this pixel format / type (only RGB/A + bytes)");
		}

		return false;
	}
}
