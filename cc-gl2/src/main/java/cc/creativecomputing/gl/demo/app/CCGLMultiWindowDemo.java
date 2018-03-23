package cc.creativecomputing.gl.demo.app;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;

public class CCGLMultiWindowDemo extends CCGLApp{

	private double _myRotation;
	
	private CCGLWindow _myWindow2;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer t) {
		_myWindow2 = createWindow(400, 400, "bla");
		_myWindow2.drawEvents.add(gr -> {display(gr);});
		_myWindow2.show();
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
		_myRotation = theTimer.time() * 180;
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.rotate(_myRotation);
		g.rect(0, 0, 100, 100);
	}
	
	public static void main(String[] args) {
		CCGLMultiWindowDemo myDemo = new CCGLMultiWindowDemo();
		myDemo.run();
	}
}
