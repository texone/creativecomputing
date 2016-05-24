package cc.creativecomputing.image.format;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.image.format.ktx.GLConstants;
import cc.creativecomputing.image.format.ktx.CCKTXImage;
import cc.creativecomputing.io.CCNIOUtil;

public class CCKTXFormat implements CCImageFormat{
	
	@Override
	public CCImage createImage(
		final Path theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if (
			CCImageFormats.KTX.fileExtension.equals(theFileSuffix) || 
			CCImageFormats.KTX.fileExtension.equals(CCNIOUtil.fileExtension(theFile))
		) {
			CCKTXImage image = new CCKTXImage();
			try {
				image.read(theFile);
			} catch (Exception e) {
				throw new CCImageException(e);
			}
			return createImage(image, theInternalFormat, thePixelFormat);
		}

		return null;
	}

	@Override
	public CCImage createImage(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if (!CCImageFormats.KTX.fileExtension.equals(theFileSuffix)) return null;
			
		CCKTXImage image = new CCKTXImage();
		try {
			image.read(theStream);
			theStream.close();
		} catch (Exception e) {
			throw new CCImageException(e);
		}
			
		return createImage(image, theInternalFormat, thePixelFormat);
		
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
		final CCKTXImage theImage, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat
	) {
		
		boolean myIsCompressed = false;
		
		CCLog.info("thePixelFormat:" + thePixelFormat + " : " + Integer.toHexString(theImage.getHeader().glInternalFormat()));
		
		if (thePixelFormat == null) {
			
			switch (theImage.getHeader().glInternalFormat()) {
			case GLConstants.GL_RED:
				thePixelFormat = CCPixelFormat.RED;
				theInternalFormat = CCPixelInternalFormat.RED;
				break;
			case GLConstants.GL_RG:
				thePixelFormat = CCPixelFormat.RG;
				theInternalFormat = CCPixelInternalFormat.RG;
				break;
			case GLConstants.GL_RGB:
				thePixelFormat = CCPixelFormat.RGB;
				theInternalFormat = CCPixelInternalFormat.RGB;
				break;
			case GLConstants.GL_RGBA:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.RGBA;
				break;
			case GLConstants.GL_RGB8:
				thePixelFormat = CCPixelFormat.RGB;
				theInternalFormat = CCPixelInternalFormat.RGB8;
				break;
			case GLConstants.GL_RGBA8:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.RGBA8;
				break;
			case GLConstants.GL_RGBA32F:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.RGBA32F;
				break;
			case GLConstants.GL_COMPRESSED_RGB_S3TC_DXT1_EXT:
				thePixelFormat = CCPixelFormat.RGB;
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
				myIsCompressed = true;
				break;
			case GLConstants.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT1_EXT;
				myIsCompressed = true;
				break;
			case GLConstants.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
				myIsCompressed = true;
				break;
			case GLConstants.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
				myIsCompressed = true;
				break;
			case GLConstants.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.ATC_RGBA_EXPLICIT_ALPHA_AMD;
				myIsCompressed = true;
				break;
			case GLConstants.GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.ATC_RGBA_INTERPOLATED_ALPHA_AMD;
				myIsCompressed = true;
				break;
			case GLConstants.GL_ETC1_RGB8_OES:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.ETC1_RGB8_OES;
				myIsCompressed = true;
				break;
			}
		}
		
		if (theInternalFormat == null) {
			thePixelFormat = CCPixelFormat.RGBA;
			theInternalFormat = CCPixelInternalFormat.RGBA;
		}
		
		
		CCImage data;
		int myNumberOfMipMaps = theImage.getTextureData().getNumberOfMipmapLevels();
		int myImageWidth = theImage.getHeader().pixelWidth();
		int myImageHeight = theImage.getHeader().pixelHeight();
		if (theImage.getTextureData().getNumberOfMipmapLevels() > 1) {
			CCLog.info("MIP MAP");
			Buffer[] mipmapData = new Buffer[myNumberOfMipMaps];
			for (int i = 0; i < myNumberOfMipMaps; i++) {
				mipmapData[i] = theImage.getTextureData().getFace(i, 0);
				CCLog.info("MIP MAP:" + mipmapData[i].limit());
			}
			data = new CCImage(
				myImageWidth, myImageHeight, 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				myIsCompressed, true, mipmapData, null
			);
		} else {
			CCLog.info("NO MIP MAP");
			CCLog.info(theImage.getTextureData().getFace(0, 0));
			data = new CCImage(
				myImageWidth, myImageHeight, 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				myIsCompressed, true, theImage.getTextureData().getFace(0, 0), null
			);
		}
		return data;
	}
	
	@Override
	public boolean write(Path file, CCImage data, final double theQuality){
//		if (CCFileFormat.KTX.fileExtension.equals(CCIOUtil.fileExtension(file))) {
//			// See whether the DDS writer can handle this TextureData
//			CCPixelFormat pixelFormat = data.pixelFormat();
//			CCPixelType pixelType = data.pixelType();
//			if (pixelType != CCPixelType.BYTE && pixelType != CCPixelType.UNSIGNED_BYTE) {
//				throw new CCImageException("DDS writer only supports byte / unsigned byte textures");
//			}
//
//			int d3dFormat = 0;
//			
//			switch (pixelFormat) {
//			case RGB:
//				d3dFormat = CCDDSImage.D3DFMT_R8G8B8;
//				break;
//			case BGR:
//				d3dFormat = CCDDSImage.D3DFMT_B8G8R8;
//				break;
//			case RGBA:
//				d3dFormat = CCDDSImage.D3DFMT_A8R8G8B8;
//				break;
//			case COMPRESSED_RGB_S3TC_DXT1_EXT:
//				d3dFormat = CCDDSImage.D3DFMT_DXT1;
//				break;
//			case COMPRESSED_RGBA_S3TC_DXT1_EXT:
//				throw new CCImageException("RGBA DXT1 not yet supported");
//			case COMPRESSED_RGBA_S3TC_DXT3_EXT:
//				d3dFormat = CCDDSImage.D3DFMT_DXT3;
//				break;
//			case COMPRESSED_RGBA_S3TC_DXT5_EXT:
//				d3dFormat = CCDDSImage.D3DFMT_DXT5;
//				break;
//			default:
//				throw new CCImageException("Unsupported pixel format " + pixelFormat + " by DDS writer");
//			}
//
//			ByteBuffer[] mipmaps = null;
//			if (data.mipmapData() != null) {
//				CCLog.info("MIP MAP");
//				mipmaps = new ByteBuffer[data.mipmapData().length];
//				for (int i = 0; i < mipmaps.length; i++) {
//					mipmaps[i] = (ByteBuffer) data.mipmapData()[i];
//				}
//			} else {
//				CCLog.info("NOT MIP MAP");
//				mipmaps = new ByteBuffer[] { (ByteBuffer) data.buffer() };
//			}
//
//			CCDDSImage image = CCDDSImage.createFromData(d3dFormat, data.width(), data.height(), mipmaps);
//			image.write(file);
//			return true;
//		}

		return false;
	}

}
