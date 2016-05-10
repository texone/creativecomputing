package cc.creativecomputing.demo.mapping;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;

public class CRingMapping extends CCGL2Adapter{
	
	
	@CCProperty(name = "draw mode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "ring mapping")
	private CCRingMapping _myMapping = new CCRingMapping();
	
	@Override
	public void start(CCAnimator theAnimator) {
		
	}
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this, g, 100);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myMapping.update(theAnimator);
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();

		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		g.color(1d);
		g.box(15, 15, 6);
		
		_myMapping.display(g);
	
	}
	
	public static void main(String[] args) {
		
		
		
		CCGL2Application myAppManager = new CCGL2Application(new CRingMapping());
		myAppManager.glcontext().size(1800, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.glcontext().inVsync = false;
//		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
//		myAppManager.glcontext().deviceSetup().display(1);
		myAppManager.start();
	}
}
