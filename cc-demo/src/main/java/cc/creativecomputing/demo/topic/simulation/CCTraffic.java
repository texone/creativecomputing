package cc.creativecomputing.demo.topic.simulation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCTraffic extends CCGL2Adapter {
	
	
	private class SlotPath{
		
		
		
		@CCProperty(name = "space", min = 0, max = 200)
		private double _cSpace = 20;
		
		@CCProperty(name = "speed", min = 0, max = 200)
		private double _cSpeed = 10;
		
		private double _myPosition = 0;

		@CCProperty(name = "spline")
		private CCLinearSpline _mySpline = new CCLinearSpline();
		private CCLinearSpline _myPath = new CCLinearSpline();
		
		public SlotPath() {
		}
		
		private void updatePathFromSpline(CCGraphics g, CCLinearSpline theSpline, CCLinearSpline thePath) {
			
		}
		
		public void update(CCAnimator theAnimator) {
			_myPosition += theAnimator.deltaTime();
		}
		
		public void draw(CCGraphics g) {
			_myPath.clear();
			for(CCVector3 myPoint:_mySpline) {
				_mySpline.addPoint(new CCVector3(
					CCMath.blend(-g.width() / 2, g.width()/ 2, myPoint.x),
					CCMath.blend(-g.height() / 2, g.height()/ 2, myPoint.y)
				));
			}
			_myPath.endEditSpline();
			
			g.color(1d,0.5d);
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCVector3 myPoint:_myPath) {
				g.vertex(myPoint);
			}
			g.endShape();
			
			
		}
	}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCTraffic demo = new CCTraffic();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
