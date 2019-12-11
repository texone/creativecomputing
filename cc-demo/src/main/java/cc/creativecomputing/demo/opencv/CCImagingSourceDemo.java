package cc.creativecomputing.demo.opencv;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.video.CCImagingSourceDevice;
import cc.creativecomputing.video.CCVideoTexture;

public class CCImagingSourceDemo extends CCGL2Adapter {
	
	
	

	@CCProperty(name = "capture")
	private CCImagingSourceDevice _mySource;
	private CCVideoTexture _myTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySource = new CCImagingSourceDevice(theAnimator);
		_mySource.startLive(false);
		_myTexture = new CCVideoTexture(this,_mySource);
		_myTexture.mustFlipVertically(true);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho2D();
		g.image(_myTexture, 0,0);
	}

	public static void main(String[] args) {
		CCImagingSourceDemo demo = new CCImagingSourceDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 960);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
