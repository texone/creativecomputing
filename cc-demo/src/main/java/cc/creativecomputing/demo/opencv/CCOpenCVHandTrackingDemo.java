package cc.creativecomputing.demo.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;
import cc.creativecomputing.opencv.CCHandTracker;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;

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
	
	private CCCVTexture _myTexture;
	
	@CCProperty(name = "shader filter")
	private CCCVShaderFilter _cFilter;
	
	@CCProperty(name = "morphology")
	private CCMorphologyFilter _cMorphology;

	@CCProperty(name = "blur")
	private CCBlur _cBlur = new CCBlur();

	@CCProperty(name = "threshold")
	private CCThreshold _cThreshold = new CCThreshold();
	
	@CCProperty(name = "hand tracker")
	private CCHandTracker _cHandTracker;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private static enum CCDrawMat{
		ORIGIN,
		SHADER,
		MORPHOLOGY,
		BLUR,
		THRESHOLD
	}
	
	@CCProperty(name = "draw mat")
	private CCDrawMat _cDrawMat = CCDrawMat.THRESHOLD;
	@CCProperty(name = "draw contour")
	private boolean _cDrawContour = true;
	


	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		if(USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(1);
			CCLog.info(_myCapture.frameWidth(), _myCapture.frameHeight());
//			_myCapture.exposure(-8);
//			_myCapture.frameWidth(1280);
//			_myCapture.frameHeight(960);
			_myVideoIn = _myCapture;
		}else {
			_myPlayer = new CCCVVideoPlayer(CCNIOUtil.dataPath("videos/hand01.mp4").toAbsolutePath().toString());
			_myVideoIn = _myPlayer;
		}
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(true);
		
		_cFilter = new CCCVShaderFilter(
			CCNIOUtil.classPath(this, "cv_shader_vertex.glsl"),
			CCNIOUtil.classPath(this, "cv_shader_fragment.glsl")
		);
		_cMorphology = new CCMorphologyFilter();
		
		_cHandTracker = new CCHandTracker();
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		
		g.clearColor(_cBackColor);
		g.clear();
		g.clearColor(1d);
		
		Mat myOrigin = _myVideoIn.read();
		Mat myDrawMat = new Mat();
		
		if(_cDrawMat == CCDrawMat.ORIGIN)myDrawMat = myOrigin.clone();
		_cFilter.process(myOrigin);
		_cFilter.preDisplay(g);
		if(_cDrawMat == CCDrawMat.SHADER)myDrawMat = myOrigin.clone();
		
		_cMorphology.process(myOrigin);
		if(_cDrawMat == CCDrawMat.MORPHOLOGY)myDrawMat = myOrigin.clone();
		_cBlur.process(myOrigin);
		if(_cDrawMat == CCDrawMat.BLUR)myDrawMat = myOrigin.clone();
		_cThreshold.process(myOrigin);
		if(_cDrawMat == CCDrawMat.THRESHOLD)myDrawMat = myOrigin.clone();
		
		_cHandTracker.trackHands(myOrigin);
		if(_cDrawContour)_cHandTracker.drawContour(myDrawMat);
		_myTexture.image(myDrawMat);
		
			
		g.ortho2D();
		
//		_cCameraController.camera().draw(g);
		g.color(1d);
		g.image(_myTexture, 0,0);
		g.color(_cContourColor);
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		_cHandTracker.handContour().forEach(v -> g.vertex(v));
		g.endShape();
		
		g.strokeWeight(5);
		g.color(_cHullColor);
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		_cHandTracker.hullWithDefects().forEach(v -> g.vertex(v.vector));
		g.endShape();
		
		g.pointSize(10);
		g.pointSmooth();
		g.strokeWeight(2);
		
		for(int i = 0; i < _cHandTracker.hullWithDefects().size();i++) {
			if(i % 2 == 0)g.color(1d,0,0);
			else g.color(0d,1,0);
			
			if(i == 0)g.color(CCColor.CYAN);
			
			double myRadius = 2;
			
			CCVector2 myCurrent = _cHandTracker.hullWithDefects().get(i).vector;
			g.ellipse(myCurrent,myRadius,myRadius, false);
		}
		
		for(int i = 0; i < _cHandTracker.fingerTips().size();i++) {
			
			if(i == 0)g.color(CCColor.CYAN, 0.5);
			else g.color(CCColor.MAGENTA, 0.5);
			
			CCVector3 myTip = _cHandTracker.fingerTips().get(i);
			double myRadius = myTip.z / 5;
			
			g.ellipse(myTip.xy(),myRadius,myRadius, false);
		}
		
		g.color(1d);
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
