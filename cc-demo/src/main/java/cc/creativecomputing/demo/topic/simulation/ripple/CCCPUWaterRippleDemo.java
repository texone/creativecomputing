package cc.creativecomputing.demo.topic.simulation.ripple;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCPUWaterRippleDemo extends CCGL2Adapter {
	
	private CCVector2[][] _myData;
	private CCVector2[][] _myData1;
	int size = 100;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_myData = new CCVector2[size][size];
		_myData1 = new CCVector2[size][size];
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				_myData[x][y] = new CCVector2();
				_myData1[x][y] = new CCVector2();
			}
		}
	}
	
	double radius = 5;
	double strength = 0.1;
	CCVector2 center = new CCVector2(size / 2, size/ 2);
	
	private void emit() {
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				CCVector2 info = _myData[x][y];

				double drop = CCMath.max(0.0, 1 - center.distance(x,y) / radius);
				drop = 0.5 - CCMath.cos(drop * CCMath.PI) * 0.5;
				info.x += drop * strength;
			}
		}
	}
	
	private void ripple() {
		for(int x = 1; x < size - 1; x++) {
			for(int y = 1; y < size - 1; y++) {
				CCVector2 info = _myData[x][y];
				CCVector2 info1 = _myData1[x][y];

				// calculate average neighbor height
				double average = (
					_myData[x - 1][y].x +
					_myData[x + 1][y].x +
					_myData[x][y - 1].x +
					_myData[x][y + 1].x
				) * 0.25;

				// change the velocity to move toward the average
				info1.y +=  (average - info.x) * 2;
				// attenuate the velocity a little so waves do not last forever
				info1.y *= 0.995;
				// move the vertex along the velocity 
				info1.x += info1.y; 
			}
		}
		
		 CCVector2[][] tmp = _myData;
		 _myData = _myData1;
		 _myData1 = tmp;
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(theAnimator.frames() % 30 == 0)emit();
		ripple();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		
		g.polygonMode(CCPolygonMode.FILL);
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				g.color(_myData[x][y].x);
				g.vertex(x * 10, y * 10);
				g.vertex((x + 1) * 10, y * 10);
				g.vertex((x + 1) * 10, (y + 1) * 10);
				g.vertex(x * 10, (y + 1) * 10);
			}
		}
		g.endShape();
		
		g.color(255);
		g.polygonMode(CCPolygonMode.LINE);
		g.beginShape(CCDrawMode.QUADS);
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				g.vertex(x * 10, y * 10);
				g.vertex((x + 1) * 10, y * 10);
				g.vertex((x + 1) * 10, (y + 1) * 10);
				g.vertex(x * 10, (y + 1) * 10);
			}
		}
		g.endShape();
		
	}

	public static void main(String[] args) {

		CCCPUWaterRippleDemo demo = new CCCPUWaterRippleDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
