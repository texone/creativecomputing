package cc.creativecomputing.image.format;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngWriter;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.math.CCMath;

public class CCPNGFormat implements CCImageFormat{

	@Override
	public CCImage createImage(Path theFile, CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, String theFileSuffix) throws CCImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CCImage createImage(InputStream theStream, CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, String theFileSuffix) throws CCImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CCImage createImage(URL theUrl, CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat,
			String theFileSuffix) throws CCImageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean write(Path theFile, CCImage theData) throws CCImageException {
		return false;
		
	}

}
