package cc.creativecomputing.demo.realtime;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCInMemoryCompileModeDemo extends CCGL2Adapter {
	
	@CCProperty(name = "width", min = 0, max = 100) 
	private double _cWidth = 100;
	@CCProperty(name = "height", min = 0, max = 100)
	private double _cHeight = 100;
	@CCProperty(name = "x", min = -1000, max = 1000)
	private double _cX = 100;
	@CCProperty(name = "y", min = -1000, max = 1000)
	private double _cY = 100;
	@CCProperty(name = "pointsize", min = 1, max = 10)
	private double _cPointSize = 10;
	
	@CCProperty(name = "rectCol")
	private CCColor _crectCol = new CCColor();
	
	@CCProperty(name = "enveloper")
	private CCEnvelope _cenveloper = new CCEnvelope();
  
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

	}

	@Override
	public void update(CCAnimator theAnimator) { 
	}
	

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(_crectCol);
		g.rect(_cX,_cY,_cWidth,_cHeight);
		g.pointSize(_cPointSize);
		g.beginShape(CCDrawMode.POINTS);
		for (int i = -g.width()/2; i < g.width(); i+= 10) {
			g.vertex(i, _cenveloper.value(CCMath.map(i,-g.width()/2,g.width(), 0,1)) * 200 - 100);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCInMemoryCompileModeDemo demo = new CCInMemoryCompileModeDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
