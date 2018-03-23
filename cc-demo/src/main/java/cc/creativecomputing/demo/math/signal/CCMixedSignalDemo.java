package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCMixedSignalDemo extends CCGL2Adapter{
	
	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	@CCProperty(name = "height", min = 0, max = 500)
	private double _cHeight = 100;

	private void drawGraph(CCGraphics g, double theScale){
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < g.width(); x++){
			double y = (_mySignal.value(x - g.width()/2)) * theScale;
			g.vertex(x - g.width()/2, y);
		}
		g.endShape();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		g.color(255);
		drawGraph(g,_cHeight);

	}
	
	public static void main(String[] args) {
		
		
		CCMixedSignalDemo demo = new CCMixedSignalDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
