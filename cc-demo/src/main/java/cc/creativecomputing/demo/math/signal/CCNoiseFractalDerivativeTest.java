package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCNoiseFractalDerivativeTest extends CCGL2Adapter {
	
	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	private float fractalSum(float theX, float theY) {
		float f = 0.0f;
		float w = 0.5f;
		float dx = 0.0f;
		float dz = 0.0f;
		
		for (int i = 0; i < 4; i++) {
			double[] myNoiseV = _myNoise.values(theX, theY);
			dx += myNoiseV[1];
			dz += myNoiseV[2];
			f += w * myNoiseV[0] / (1.0f + dx * dx + dz * dz); // replace with "w * n[0]" for a classic fbm()
			w *= 0.5f;
			theX *= 2.0f;
			theY *= 2.0f;
		}
		return f;
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.color(255,0,0);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float x = -g.width()/2; x <= g.width()/2;x++){
			float y = fractalSum((x + g.width()/2),0) * g.height() - g.height()/2;
			g.vertex(x,y);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCNoiseFractalDerivativeTest demo = new CCNoiseFractalDerivativeTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
