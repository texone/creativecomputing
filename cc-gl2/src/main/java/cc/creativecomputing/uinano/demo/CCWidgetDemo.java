package cc.creativecomputing.uinano.demo;


import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.uinano.CCUILabel;
import cc.creativecomputing.uinano.CCWidget;
import cc.creativecomputing.uinano.Screen;

public class CCWidgetDemo extends CCGLApp{

	private double _myRotation;
	
	private CCGLWindow _myWindow2;
	
	private CCWidget _myWidget;
	
	private CCTextureMapFont _myFont;
	
	private Screen _myScreen;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myWindow2 = createWindow(400, 400, "bla");
//		_myWindow2.drawEvents.add(gr -> {
//			g.pushMatrix();
//			g.ortho();
//			g.clear();
//			_myWidget.draw(g);
//			g.popMatrix();
//		});
		_myWindow2.show();
		
		_myScreen = new Screen();
		_myScreen.initialize(_myWindow2, false);
		
		_myWidget = new CCWidget(_myScreen).position(100, 100).size(200,200);
		
//		try {
			_myFont = new CCTextureMapFont(CCNIOUtil.dataPath("Roboto-Regular.ttf"), 12);
			new CCUILabel(_myWidget, "label", _myFont);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
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
		CCWidgetDemo myDemo = new CCWidgetDemo();
		myDemo.run();
	}
}
