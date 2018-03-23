package cc.creativecomputing.demo;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;

public class CCEmptyApp2 extends CCGL2Adapter{
	
	@CCProperty(name = "blend", min = 0, max = 1)
	private double _cBlend = 0;
	@CCProperty(name = "steep", min = 1, max = 10)
	private double _cSteep = 4;
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	private double blend(double theBlend, double theValue) {
		double myControlBlend = 1 - _cBlend;
		if(myControlBlend >= 0.5) {
			return CCMath.pow(theValue, CCMath.pow(10, (myControlBlend - 0.5) * _cSteep));
		}else {
			myControlBlend = 0.5 - myControlBlend;
			return 1 - CCMath.pow(1 - theValue, CCMath.pow(10, (myControlBlend) * _cSteep));
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		
		g.clear();
		g.ortho2D();
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < g.width();i++) {
			double myBlend = (i * 1d) / g.width();
			
			g.color(blend(_cBlend, myBlend));
			g.vertex(i,0);
			g.vertex(i,g.height());
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCEmptyApp2 demo = new CCEmptyApp2();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
