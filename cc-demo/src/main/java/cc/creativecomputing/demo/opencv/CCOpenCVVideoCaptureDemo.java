package cc.creativecomputing.demo.opencv;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;
import cc.creativecomputing.opencv.CCHandTracker;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;

public class CCOpenCVVideoCaptureDemo extends CCGL2Adapter {
	
	@CCProperty(name = "color")
	private CCColor _cContourColor = new CCColor();
	@CCProperty(name = "hull color")
	private CCColor _cHullColor = new CCColor();
	
	private boolean USE_CAPTURE = false;
	
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
		if(USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(1);
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
		g.clear();
		
		
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
		g.image(_myTexture, 0,0);
		
		g.color(_cContourColor);
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		_cHandTracker.handContour().forEach(v -> g.vertex(v));
		g.endShape();
		

		g.color(_cHullColor);
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		_cHandTracker.handHull().forEach(v -> g.vertex(v));
		g.endShape();
		
		double myMaxDist = 10;
		
		List<CCVector2> myHullTipPoints = new ArrayList<>();
		CCVector2 myLast = null;
		CCVector2 myAverage = new CCVector2();
		int myCount = 0;
		for(int i = 0; i < _cHandTracker.handHull().size();i++) {
			CCVector2 myCurrent = _cHandTracker.handHull().get(i);
			if(myLast != null) {
				double myDist = myCurrent.distance(myLast);
				myAverage.addLocal(myLast);
				if(myDist > myMaxDist) {
					myHullTipPoints.add(myAverage.divide(myCount + 1));
					myAverage.set(0,0);
					myCount = 0;
				}
				myCount++;
			}
			myLast = myCurrent;
		}
		
		g.pointSize(10);
		g.beginShape(CCDrawMode.POINTS);
		_cHandTracker.handHull().forEach(v -> g.vertex(v));
		g.endShape();
		
		g.color(1d);
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
