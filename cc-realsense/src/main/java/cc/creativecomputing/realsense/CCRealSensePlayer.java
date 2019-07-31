package cc.creativecomputing.realsense;

import java.nio.ByteBuffer;
import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.io.CCFileInputChannel;

public class CCRealSensePlayer {
	
	private CCFileInputChannel _myInputChannel;
	

	private int width;
	private int height;
	
	private long _myFrameSize;
	private long _myFrame;
	private long _myNumberOfFrames;
	
	private CCImage depthImage;
	private CCImage lastdepthImage;
	
	private ByteBuffer _myReadBuffer;

	public CCRealSensePlayer(Path thePath, int theWidth, int theHeight) {
		_myInputChannel = new CCFileInputChannel(thePath);
		this.width = theWidth;
		this.height = theHeight;

		// create images
		depthImage = new CCImage(this.width, this.height, CCPixelInternalFormat.LUMINANCE16, CCPixelFormat.LUMINANCE, CCPixelType.UNSIGNED_SHORT);
		lastdepthImage = new CCImage(this.width, this.height, CCPixelInternalFormat.LUMINANCE16, CCPixelFormat.LUMINANCE, CCPixelType.UNSIGNED_SHORT);
		
		_myFrameSize = width * height * 2;
		
		_myFrame = 0;
		_myNumberOfFrames = _myInputChannel.size() / _myFrameSize;
	}

	public CCImage depthImage() {
		return depthImage;
	}
	public CCImage lastDepthImage() {
		return lastdepthImage;
	}
	
	public void update(CCAnimator theAnimator) {
		_myReadBuffer = ByteBuffer.allocateDirect((int)_myFrameSize);
		_myInputChannel.read(_myFrame * _myFrameSize, _myReadBuffer);
		_myReadBuffer.rewind();
		
		CCImage tmp = depthImage;
		depthImage = lastdepthImage;
		lastdepthImage = tmp;
		depthImage.buffer(_myReadBuffer.asShortBuffer());
		
		_myFrame++;
		_myFrame %= _myNumberOfFrames;
	}
}
