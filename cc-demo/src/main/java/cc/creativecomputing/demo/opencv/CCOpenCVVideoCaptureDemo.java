package cc.creativecomputing.demo.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_videoio.*;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;

public class CCOpenCVVideoCaptureDemo extends CCGL2Adapter {
	
	
	
	private boolean USE_CAPTURE = true;
	
	
	
	@CCProperty(name = "player")
	private CCCVVideoPlayer _myPlayer;
	
	private CCCVVideoIn _myVideoIn;
	
	private CCCVTexture _myTexture;

	private void initCam() {
		CCCVVideoCapture _myCapture = new CCCVVideoCapture(1);
//		_myCapture.exposure(-8);
		//8.44715353E8 microsoft color
		//-4.66162819E8 vision black
		//-4.66162819E8
		_myCapture.frameWidth(1440);
		_myCapture.frameHeight(1080);
//		_myCapture.format(CV_8UC3); // trying to set RGB3 requesting format
//		_myCapture.mode(CV_CAP_MODE_RGB);
//		_myCapture.convertRGB(true);
		_myCapture.frameRate(30);
		CCLog.info(_myCapture.frameRate());
		_myVideoIn = _myCapture;
		CCLog.info(_myCapture.fourcc());
		//_myCapture.start();
	}
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		if(USE_CAPTURE) {
			initCam();
		}else {
			_myPlayer = new CCCVVideoPlayer(CCNIOUtil.dataPath("videos/hand01.mp4").toAbsolutePath().toString());
			_myVideoIn = _myPlayer;
		}
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(true);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		Mat myOrigin = _myVideoIn.read();
		if(myOrigin.arrayWidth() == 0) {
			CCLog.info("Lost cam");
			initCam();
		}
		CCLog.info(myOrigin);
		_myTexture.image(myOrigin);
		g.ortho2D();
		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCOpenCVVideoCaptureDemo demo = new CCOpenCVVideoCaptureDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 960);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
