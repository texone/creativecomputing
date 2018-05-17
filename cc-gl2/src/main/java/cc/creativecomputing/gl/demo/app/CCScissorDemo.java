package cc.creativecomputing.gl.demo.app;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public class CCScissorDemo extends CCGLApp{

	
	
	
	@Override
	public void setup() {
		
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		g.color(1d);
		g.pushMatrix();
		g.translate(100,100);
		g.scissor(100, 100, 100, 100);
		g.rect(10,10,g.width() - 20, g.height() - 20 );
		g.popMatrix();
		
	}
	
	public static void main(String[] args) {
		CCScissorDemo myDemo = new CCScissorDemo();

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
