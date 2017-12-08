package cc.creativecomputing.demo.topic.kinetic;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCSmarthouseDemo extends CCGL2Adapter {
	
	@CCProperty(name = "speed", min = 0, max = 1)
	private double _cSpeed = 1;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}
	
	private CCVector2 _myPosition = new CCVector2();
	private double _myLastSteps = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		double myAngle = CCMath.cos(theAnimator.time() * _cSpeed * CCMath.TWO_PI) ;
		double mySteps = myAngle * 63400;
		double mySpeed = (mySteps - _myLastSteps);
		_myLastSteps = mySteps;
		double myStepTime = theAnimator.deltaTime() / mySpeed;
		CCLog.info(myStepTime * 1000 * 100);
		_myPosition = CCVector2.circlePoint(myAngle * CCMath.radians(175 / 2) - CCMath.radians(90), 300, 0, 0);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.ellipse(_myPosition, 30);
	}

	public static void main(String[] args) {

		CCSmarthouseDemo demo = new CCSmarthouseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 800);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
