package cc.creativecomputing.demo.simulation.particles.realsense;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.realsense.CCRealSenseTextures;

public class CCRealSenseCameraDemo extends CCGL2Adapter {
	
	@CCProperty(name = "force field")
	private CCRealSenseTextures _cForceField;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cForceField = new CCRealSenseTextures();
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		_cForceField.preDisplay(g);
		g.clear();
		
		_cCameraController.camera().draw(g);
		g.image(_cForceField.forceField(), 0,0);
		g.image(_cForceField.depthMap(), 640,0);
		
//		g.image(_myTexture0, 0,0);
//		g.image(_myTexture1, 640,0);
	}

	public static void main(String[] args) {

		CCRealSenseCameraDemo demo = new CCRealSenseCameraDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
