package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;

public class CCEnvelopeDemo2 extends CCGL2Adapter {

	@CCProperty(name = "envelope")
	private CCEnvelope _myEnvelope = new CCEnvelope();
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < g.width();x++){
			g.vertex(x, _myEnvelope.value(x/(double)g.width()) * 200 + 200);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCEnvelopeDemo2 demo = new CCEnvelopeDemo2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
