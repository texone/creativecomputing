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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;

public class CCTGAFormat extends CCStreamBasedTextureFormat {

	@Override
	public CCImage createImage(final InputStream theStream) throws CCImageException {
		CCTGAImage image = CCTGAImage.read(theStream);
		
		return new CCImage(
			image.getWidth(), image.getHeight(), 0, 
			CCPixelInternalFormat.RGBA8, image.pixelFormat(), CCPixelType.UNSIGNED_BYTE, 
			false, false, 
			image.getData(), null
		);
	}

	@Override
	public boolean write(final Path theFile, final CCImage theData, final double theQuality) throws CCImageException {
		// See whether the TGA writer can handle this TextureData
		CCPixelFormat pixelFormat = theData.pixelFormat();
		CCPixelType pixelType = theData.pixelType();
		
		if ((pixelFormat != CCPixelFormat.RGB && pixelFormat != CCPixelFormat.RGBA) || (pixelType != CCPixelType.BYTE && pixelType != CCPixelType.UNSIGNED_BYTE)){
			throw new CCImageException("TGA writer doesn't support this pixel format / type (only RGB/A + bytes)");
		}
		
		ByteBuffer buf = ((theData.buffer() != null) ? (ByteBuffer) theData.buffer() : (ByteBuffer) theData.mipmapData()[0]);
		// Must reverse order of red and blue channels to get correct results
		int skip = ((pixelFormat == CCPixelFormat.RGB) ? 3 : 4);
		
		for (int i = 0; i < buf.remaining(); i += skip) {
			byte red = buf.get(i + 0);
			byte blue = buf.get(i + 2);
			buf.put(i + 0, blue);
			buf.put(i + 2, red);
		}

		CCTGAImage image = CCTGAImage.createFromData(
			theData.width(), theData.height(), (pixelFormat == CCPixelFormat.RGBA), 
			false,
			(ByteBuffer) theData.buffer()
		);
		try {
			image.write(theFile);
		} catch (IOException e) {
			throw new CCImageException(e);
		}
		return true;
	}
}
