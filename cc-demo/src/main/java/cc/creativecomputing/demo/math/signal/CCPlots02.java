package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCPlots02 extends CCGL2Adapter{
	
	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	@CCProperty(name = "height", min = 0, max = 5000)
	private double _cHeight = 100;
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha = 1;
	@CCProperty(name = "drawmode")
	private CCDrawMode _cDrawMode = CCDrawMode.LINE_STRIP;
	@CCProperty(name = "offset", min = 0, max = 1)
	private double _cSignalOffset = 0;
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		g.color(1d, _cAlpha);
		g.beginShape(_cDrawMode);
		for(int a = 0; a < 20000; a++){
			double x = (_mySignal.value(a / 10000d)) * _cHeight;
			double y = (_mySignal.value(a / 10000d + 0.25)) * _cHeight;
			g.vertex(x,y);
			x = (_mySignal.value(a / 10000d + _cSignalOffset)) * _cHeight;
			y = (_mySignal.value(a / 10000d + 0.25 + _cSignalOffset)) * _cHeight;
			g.vertex(x,y);
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCPlots02 demo = new CCPlots02();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
