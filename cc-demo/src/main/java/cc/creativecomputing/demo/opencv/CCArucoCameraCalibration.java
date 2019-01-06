package cc.creativecomputing.demo.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;

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

public class CCArucoCameraCalibration extends CCGL2Adapter {
	
	
	
	private boolean USE_CAPTURE = true;
	
	@CCProperty(name = "capture")
	private CCCVVideoCapture _myCapture;
	
	@CCProperty(name = "player")
	private CCCVVideoPlayer _myPlayer;
	
	private CCCVVideoIn _myVideoIn;
	
	private CCCVTexture _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		if(USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(0);
			_myCapture.exposure(-8);
			_myCapture.frameWidth(1280);
			_myCapture.frameHeight(960);
			_myVideoIn = _myCapture;
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
		_myTexture.image(myOrigin);
		g.ortho2D();
		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCArucoCameraCalibration demo = new CCArucoCameraCalibration();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 960);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
