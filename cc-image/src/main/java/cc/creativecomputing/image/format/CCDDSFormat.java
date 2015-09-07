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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.io.CCBufferUtil;



public class CCDDSFormat implements CCImageFormat {
	
	@Override
	public CCImage createImage(
		final Path theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		
		CCDDSImage image = CCDDSImage.read(theFile);
		return createImage(image, theInternalFormat, thePixelFormat);
	}

	@Override
	public CCImage createImage(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if (
			CCImageFormats.DDS.fileExtension.equals(theFileSuffix) || 
			CCDDSImage.isDDSImage(theStream)
		) {
			
			CCDDSImage image;
			try {
				ByteBuffer buf = CCBufferUtil.readAll2Buffer(theStream);
				image = CCDDSImage.read(buf);
				theStream.close();
			} catch (IOException e) {
				throw new CCImageException(e);
			}
			
			return createImage(image, theInternalFormat, thePixelFormat);
		}

		return null;
	}

	@Override
	public CCImage createImage(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			final InputStream theStream = new BufferedInputStream(theUrl.openStream());
			
			return createImage(theStream, theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCImageException(e);
		}
	}

	private CCImage createImage(
		final CCDDSImage theImage, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat
	) {
		CCDDSImage.ImageInfo info = theImage.getMipMap(0);
		
		if (thePixelFormat == null) {
			switch (theImage.getPixelFormat()) {
			case CCDDSImage.D3DFMT_R8G8B8:
				thePixelFormat = CCPixelFormat.RGB;
				theInternalFormat = CCPixelInternalFormat.RGB8;
				break;
			case CCDDSImage.D3DFMT_B8G8R8:
				thePixelFormat = CCPixelFormat.BGR;
				theInternalFormat = CCPixelInternalFormat.RGB8;
				break;
			default:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.RGBA8;
				break;
			}
		}

		if (info.isCompressed()) {
			switch (info.getCompressionFormat()) {
			case CCDDSImage.D3DFMT_DXT1:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
				break;
			case CCDDSImage.D3DFMT_DXT3:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
				break;
			case CCDDSImage.D3DFMT_DXT5:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
				break;
			default:
				throw new CCImageException("Unsupported DDS compression format \"" + CCDDSImage.getCompressionFormatName(info.getCompressionFormat()) + "\"");
			}
		}
		
		if (theInternalFormat == null) {
			switch (theImage.getPixelFormat()) {
			case CCDDSImage.D3DFMT_R8G8B8:
				thePixelFormat = CCPixelFormat.RGB;
				break;
			default:
				thePixelFormat = CCPixelFormat.RGBA;
				break;
			}
		}
		
		CCImage.Flusher flusher = new CCImage.Flusher() {
			public void flush() {
				theImage.close();
			}
		};
		
		CCImage data;
		if (theImage.getNumMipMaps() > 0) {
			Buffer[] mipmapData = new Buffer[theImage.getNumMipMaps()];
			for (int i = 0; i < theImage.getNumMipMaps(); i++) {
				mipmapData[i] = theImage.getMipMap(i).getData();
			}
			data = new CCImage(
				info.getWidth(), info.getHeight(), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				info.isCompressed(), true, mipmapData, flusher
			);
		} else {
			data = new CCImage(
				info.getWidth(), info.getHeight(), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				info.isCompressed(), true, info.getData(), flusher
			);
		}
		return data;
	}
	
	@Override
	public boolean write(Path file, CCImage data){
			// See whether the DDS writer can handle this TextureData
			CCPixelFormat pixelFormat = data.pixelFormat();
			CCPixelType pixelType = data.pixelType();
			if (pixelType != CCPixelType.BYTE && pixelType != CCPixelType.UNSIGNED_BYTE) {
				throw new CCImageException("DDS writer only supports byte / unsigned byte textures");
			}

			int d3dFormat = 0;
			
			switch (pixelFormat) {
			case RGB:
				d3dFormat = CCDDSImage.D3DFMT_R8G8B8;
				break;
			case BGR:
				d3dFormat = CCDDSImage.D3DFMT_B8G8R8;
				break;
			case RGBA:
				d3dFormat = CCDDSImage.D3DFMT_A8R8G8B8;
				break;
			case COMPRESSED_RGB_S3TC_DXT1_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT1;
				break;
			case COMPRESSED_RGBA_S3TC_DXT1_EXT:
				throw new CCImageException("RGBA DXT1 not yet supported");
			case COMPRESSED_RGBA_S3TC_DXT3_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT3;
				break;
			case COMPRESSED_RGBA_S3TC_DXT5_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT5;
				break;
			default:
				throw new CCImageException("Unsupported pixel format " + pixelFormat + " by DDS writer");
			}

			ByteBuffer[] mipmaps = null;
			if (data.mipmapData() != null) {
				mipmaps = new ByteBuffer[data.mipmapData().length];
				for (int i = 0; i < mipmaps.length; i++) {
					mipmaps[i] = (ByteBuffer) data.mipmapData()[i];
				}
			} else {
				mipmaps = new ByteBuffer[] { (ByteBuffer) data.buffer() };
			}

			CCDDSImage image = CCDDSImage.createFromData(d3dFormat, data.width(), data.height(), mipmaps);
			image.write(file);
			return true;
	}
}
