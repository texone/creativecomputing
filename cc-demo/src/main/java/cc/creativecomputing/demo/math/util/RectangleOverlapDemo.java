package cc.creativecomputing.demo.math.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCAvoidRectangleOverlap;

public class RectangleOverlapDemo extends CCGL2Adapter {
	
	private List<Rectangle> rects;

	private static int WIDTH = 400;
	private static int HEIGHT = 400;
	private static int BUFFER = 50;
			
	private Rectangle randomRectangle(boolean uniform) {
		    double left = CCMath.random(BUFFER, WIDTH);
		    double top = CCMath.random(BUFFER, HEIGHT);

		    double width = 10, height = 10, ratio = 0;

		   
		    while (ratio < 0.1 || ratio > 10) {
			     width = CCMath.random(10, CCMath.max(CCMath.min(WIDTH - left, 75), 10));
			       double max_height =  uniform ? 75 :width;
			        height = CCMath.random(10,  CCMath.max( CCMath.min(HEIGHT - top, max_height), 10));
			        ratio = width / height;
		    }

		    return new Rectangle(left, top, width, height);
	}
	
	public RectangleStrategy strategy;
	
	private void resetRects() {
		rects = new ArrayList<>();
		for(int i = 0; i < 100;i++) {
			rects.add(randomRectangle(true));
		}
		strategy = new RectangleSeparationStrategy(rects);
		
	}
	
	
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		resetRects();
		
		mousePressed().add(l ->resetRects());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		strategy.step();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.pushMatrix();
		g.ortho2D();
		double i = 0;
		for(Rectangle r:rects) {
			
			double blend = i / rects.size();
			i++;
			g.color(blend,0.25);
			g.rect(r.left, r.top, r.width, r.height);
			g.color(blend,0.5);
			g.rect(r.left, r.top, r.width, r.height, true);
		}
		g.popMatrix();
	}

	public static void main(String[] args) {

		RectangleOverlapDemo demo = new RectangleOverlapDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(WIDTH, HEIGHT);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
