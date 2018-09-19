package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCLines extends CCGL2Adapter{
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	
	@Override
	public void init(CCGraphics g) {
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	
	
	@Override
	public void display(CCGraphics g) {
		_cCameraController.camera().draw(g);
		g.clear();
		g.beginShape(CCDrawMode.LINES);
		for(int i = 0; i < 25; i++){
			g.color(255);
			g.vertex(i - 200 + i * 16,200,200);
			g.vertex(200,-200, i - 200 + i * 16);

			g.vertex(i - 200 + i * 16,200,-200);
			g.vertex(-200,-200, i - 200 + i * 16);
			

			g.vertex(i - 200 + i * 16,-200,200);
			g.vertex(200,200, i - 200 + i * 16);

			g.vertex(i - 200 + i * 16,-200,-200);
			g.vertex(-200,200, i - 200 + i * 16);
			
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCLines demo = new CCLines();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
