package cc.creativecomputing.gl2;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.camera.CCCameraController;

public class CCCameraDemo extends CCGLApp {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void setup() {
		_cCameraController = new CCCameraController(this, g, 100);	
	}

	@Override
	public void update(CCGLTimer theTimer) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		g.box(50);
	}

	public static void main(String[] args) {

		CCCameraDemo demo = new CCCameraDemo();

		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		CCControlApp _myControls = new CCControlApp(myAppManager, demo);
		myAppManager.run();

	}
}