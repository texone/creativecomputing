package cc.creativecomputing.demo.realtime.motion;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCMotionRealtime extends CCGL2Adapter {
	@CCProperty(name = "mix signal")
	private CCMixSignal _cMixSignal = new CCMixSignal();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
		CCLog.info("bla:", theAnimator.frames());
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho2D();
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(double x = 0;x < g.width();x++){
			g.vertex(x, _cMixSignal.value(x) * 100);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCMotionRealtime demo = new CCMotionRealtime();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

