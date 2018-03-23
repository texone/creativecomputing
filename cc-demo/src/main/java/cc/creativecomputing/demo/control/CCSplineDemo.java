package cc.creativecomputing.demo.control;

import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineDemo extends CCGL2Adapter{
	
	public interface CCRealtimeGraph extends CCCompileObject{
		void draw(CCGraphics g);
	}
	
	@CCProperty(name = "linear spline")
	private CCSpline _myLinearSpline = new CCLinearSpline(false);
	@CCProperty(name = "bezier spline")
	private CCSpline _myBezierSpline = new CCBezierSpline(false);
	@CCProperty(name = "catmulrom spline")
	private CCSpline _myCatmulromSpline = new CCCatmulRomSpline(1.0, false);
	
	@CCProperty(name = "drawmode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	private void drawSpline(CCGraphics g, CCColor theColor, CCSpline theSpline){
		g.color(theColor);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < 1000;i++){
			CCVector3 myVertex = theSpline.interpolate(i / 1000f);
			if(myVertex == null)break;
			g.vertex(myVertex);
		}
		g.endShape();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		

		g.color(255);
		
		g.ortho();
		g.pushMatrix();
		g.scale(g.width(), g.height());
		drawSpline(g, CCColor.CYAN, _myLinearSpline);
		drawSpline(g, CCColor.MAGENTA, _myBezierSpline);
		drawSpline(g, CCColor.YELLOW, _myCatmulromSpline);
		g.popMatrix();
		
		

	}
	
	public static void main(String[] args) {
		
		
		CCSplineDemo demo = new CCSplineDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
