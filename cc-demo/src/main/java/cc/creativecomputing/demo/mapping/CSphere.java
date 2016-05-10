package cc.creativecomputing.demo.mapping;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CSphere extends CCGL2Adapter{
	
	
	@CCProperty(name = "draw mode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		
	}
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this, g, 100);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@CCProperty(name = "signal")
	private CCMixSignal _myMixSignal = new CCMixSignal();
	
	@CCProperty(name = "res")
	private int _cRes = 20;
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();

		_myCameraController.camera().draw(g);
		g.noDepthTest();
		g.blend(CCBlendMode.ADD);
		
		for(int i = 0; i < 30;i++){
			double angle = CCMath.map(i, 0, 30, 0, CCMath.TWO_PI);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(int j = 0; j <= 30;j++){
				double angle0 = CCMath.map(j, 0, 30, 0, CCMath.PI);
				double angle1 = angle + _myMixSignal.value(CCMath.norm(j, 0, 30));
				double x = 400 * CCMath.sin(angle0) * CCMath.cos(angle1);
				double y = 400 * CCMath.sin(angle0) * CCMath.sin(angle1);
				double z = 400 * CCMath.cos(angle0);
				g.vertex(x,y,z);
			}
			g.endShape();
		}
		
	
	}
	
	public static void main(String[] args) {
		
		
		
		CCGL2Application myAppManager = new CCGL2Application(new CSphere());
		myAppManager.glcontext().size(1800, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.glcontext().inVsync = false;
//		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
//		myAppManager.glcontext().deviceSetup().display(1);
		myAppManager.start();
	}
}
