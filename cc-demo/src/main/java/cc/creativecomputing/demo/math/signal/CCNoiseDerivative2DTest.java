package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCSimplexNoise;

public class CCNoiseDerivative2DTest extends CCGL2Adapter {
	
	@CCProperty(name = "space", min = 1, max = 10f)
	private float _cSpace = 1;
	
	@CCProperty(name = "length", min = 1, max = 200f)
	private float _cLength = 1;
	
	@CCProperty(name = "alpha", min = 0, max = 1f)
	private float _cAlpha = 1;
	
	@CCProperty(name = "noise")
	private CCSimplexNoise _myNoise = new CCSimplexNoise();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}
	
	double _myTime = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myTime = theAnimator.time();
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255);
		g.clear();
		g.smooth();
		g.blend(CCBlendMode.BLEND);
		g.color(0f,_cAlpha);
		g.beginShape(CCDrawMode.LINES);
		for(float x = -g.width()/2; x <= g.width()/2;x+=_cSpace){
			for(float y = -g.height()/2; y <= g.height()/2;y+=_cSpace) {
				g.vertex(x,y);
				double[] myDNoise = _myNoise.values(x + g.width()/2,y + g.height()/2,_myTime * 100);
				g.vertex(x + (myDNoise[1] - 0.5f) * _cLength,y + (myDNoise[2] - 0.5f) * _cLength);
			}
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCNoiseDerivative2DTest demo = new CCNoiseDerivative2DTest();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

