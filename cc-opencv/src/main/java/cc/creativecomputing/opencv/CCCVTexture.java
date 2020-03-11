package cc.creativecomputing.opencv;

import java.nio.Buffer;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;

public class CCCVTexture extends CCTexture2D{

	public CCCVTexture() {
		super(new CCTextureAttributes().format(CCPixelFormat.BGR).pixelType(CCPixelType.UNSIGNED_BYTE));
		mustFlipVertically(true);
	}
	
	public void image(Mat theMat) {
		if(theMat.empty())return;
		boolean myAllocateData = theMat.cols() != width() || theMat.rows() != height();
		if(myAllocateData) {
			allocateData(theMat.cols(), theMat.rows(), null);
		}
		
		CCLog.info();
		if(theMat.channels() == 1) {
			theMat = CCCVUtil.grayToRGB(theMat);
		}
		
		Buffer myBuffer = theMat.createBuffer();
		CCLog.info(myBuffer.capacity(), myBuffer.position(), myBuffer.limit());
		texImage2D(myBuffer);
	}
}
