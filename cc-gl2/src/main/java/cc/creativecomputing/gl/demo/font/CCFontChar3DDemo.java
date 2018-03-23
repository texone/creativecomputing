package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CC3DChar;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCFontChar3DDemo extends CCGLApp{

	private CCFont<?> _myFont;
	private CC3DChar _myChar;
	
	private double _myScale;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer t) {
		_myFont = new CCFont(null,CCNIOUtil.dataPath("fonts/Raleway/Raleway-Regular.ttf"));
		_myChar = _myFont.char3D('t', 20);
		_myScale = _myFont.scaleForPixelHeight(240);
		
		_myMainWindow.keyCharEvents.add((c) -> {
			_myChar = _myFont.char3D(c, 20);
		});
	}
	
	private double _myRotation = 0;
	
	@Override
	public void update(CCGLTimer theTimer) {
		_myRotation += theTimer.deltaTime() * 90;
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.translate(0,0);
		g.color(1d);
		
		g.pushMatrix();
		g.rotateY(_myRotation);
		_myChar.draw(g, 0, 0, 0, _myScale);
		g.popMatrix();
		
	}
	
	public static void main(String[] args) {
		CCFontChar3DDemo myDemo = new CCFontChar3DDemo();
		myDemo.run();
	}
}
