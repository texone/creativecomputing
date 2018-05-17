package cc.creativecomputing.demo.gl2.draw;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;

public class CCMaskDemo extends CCGLApp {

	@Override
	public void setup() {
	}

	@Override
	public void update(CCGLTimer theTimer) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.beginMask();
		g.rect(0,0,200,200);
		g.endMask();
		g.color(1d);
		g.ellipse(0,0, 200);
		g.noMask();
		g.color(1d,0d,0d);
		g.rect(-200,-200,200,200);
	}

	public static void main(String[] args) {

		CCMaskDemo demo = new CCMaskDemo();

		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		myAppManager.run();
	}
}
