package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

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
		
		g.pushMatrix();
		g.translate(-250,-250);
		g.color(255,0,0);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(int x = 0; x <= 500;x+= 2){
			double myVal = _myEnvelope.value(x/500d);
//			CCLog.info(x/(double)g.width() + ":" + myVal);
			g.vertex(x, myVal * 500);
			g.vertex(x, 0);
		}
		g.endShape();
		g.color(0,0,255);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(int x = 0; x <= 500;x+= 2){
			double myVal = _myEnvelope.value(x/500d);
//			CCLog.info(x/(double)g.width() + ":" + myVal);
			g.vertex(x, myVal * 500);
			g.vertex(x, 500);
		}
		g.endShape();
		
		g.popMatrix();
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
