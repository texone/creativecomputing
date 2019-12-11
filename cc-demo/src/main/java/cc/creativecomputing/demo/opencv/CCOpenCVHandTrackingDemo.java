package cc.creativecomputing.demo.opencv;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;
import cc.creativecomputing.opencv.CCHandTracker;

public class CCOpenCVHandTrackingDemo extends CCGL2Adapter {
	
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	@CCProperty(name = "hull color")
	private CCColor _cHullColor = new CCColor();
	

	@CCProperty(name = "back color")
	private CCColor _cBackColor = new CCColor();
	
	private boolean USE_CAPTURE = true;
	
	@CCProperty(name = "capture")
	private CCCVVideoCapture _myCapture;
	
	@CCProperty(name = "player")
	private CCCVVideoPlayer _myPlayer;
	
	private CCCVVideoIn _myVideoIn;
	
	
	@CCProperty(name = "hand tracker")
	private CCHandTracker _cHandTracker;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		if(USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(1);
			CCLog.info(_myCapture.frameWidth(), _myCapture.frameHeight());
//			_myCapture.exposure(-8);
			_myCapture.frameWidth(1280);
			_myCapture.frameHeight(960);
			_myCapture.frameRate(30);
			_myVideoIn = _myCapture;
		}else {
			_myPlayer = new CCCVVideoPlayer(CCNIOUtil.dataPath("videos/hand01.mp4").toAbsolutePath().toString());
			_myVideoIn = _myPlayer;
		}
		
		_cHandTracker = new CCHandTracker(null, null);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		_cHandTracker.preDisplay(g);
		
		g.clearColor(_cBackColor);
		g.clear();
		g.clearColor(1d);
		
		
		
			
		g.ortho2D();
		_cHandTracker.drawDebug(g);
		
	}

	public static void main(String[] args) {
		CCOpenCVHandTrackingDemo demo = new CCOpenCVHandTrackingDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 960);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
