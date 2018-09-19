package cc.creativecomputing.image.format;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.IImageLineSet;
import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineInt;
import ar.com.hjg.pngj.PngReader;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.io.CCBufferUtil;

public class CCPNGFormat extends CCStreamBasedTextureFormat{
	
	@Override
	public CCImage createImage(InputStream theStream) throws CCImageException {
		PngReader myPngReader = new PngReader(theStream);
		ImageInfo myInfo = myPngReader.imgInfo;
		
		
		CCImage myImage = new CCImage();
		
		switch(myInfo.channels) {
		case 1:
			myImage.pixelFormat(CCPixelFormat.LUMINANCE);
			myImage.internalFormat(CCPixelInternalFormat.LUMINANCE);
			break;
		case 2:
			myImage.pixelFormat(CCPixelFormat.LUMINANCE_ALPHA);
			myImage.internalFormat(CCPixelInternalFormat.LUMINANCE_ALPHA);
			break;
		case 3:
			myImage.pixelFormat(CCPixelFormat.RGB);
			myImage.internalFormat(CCPixelInternalFormat.RGB);
			break;
		case 4:
			myImage.pixelFormat(CCPixelFormat.RGBA);
			myImage.internalFormat(CCPixelInternalFormat.RGBA);
			break;
		}
		
		IImageLineSet<? extends IImageLine> myLineSet = myPngReader.readRows();
		
		if(myLineSet.size() == 0)return null;
		
		IImageLine myCheckLine = myLineSet.getImageLine(0);
		
		
		
		if(myCheckLine instanceof ImageLineInt){
			ImageLineInt myImageLineInt = (ImageLineInt)myCheckLine;
			
			
			int _myHeight = myLineSet.size();
			int _myWidth = myImageLineInt.getSize() / myInfo.channels;
			myImage.width(_myWidth);
			myImage.height(_myHeight);
			
			switch(myInfo.bitDepth) {
			case 8:
				myImage.pixelType(CCPixelType.UNSIGNED_BYTE);
				ByteBuffer myBuffer = CCBufferUtil.newDirectByteBuffer(myLineSet.size() * myImageLineInt.getSize());
				for(int y = 0; y < myLineSet.size(); y++){
					ImageLineInt myImageLine = (ImageLineInt)myLineSet.getImageLine(y);
					for(int x = 0; x < myImageLine.getSize(); x++){
						myBuffer.put((byte)myImageLine.getElem(x));
					}
					
				}
				myBuffer.rewind();
				myImage.buffer(myBuffer);
				break;
			}	
		}
		
		return myImage;
	}

	@Override
	public CCImage createImage(URL theUrl) throws CCImageException {
		throw new CCImageException("png from url ist not supported");
	}

	@Override
	public boolean write(Path theFile, CCImage theData, final double theQuality) throws CCImageException {
		CCPNGImage myImage = new CCPNGImage(theData.width(), theData.height(), theData.pixelFormat(), theData.pixelType());
		return false;
		
	}

}
