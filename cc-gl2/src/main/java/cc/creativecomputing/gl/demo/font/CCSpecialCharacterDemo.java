package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSpecialCharacterDemo extends CCGLApp{
	
	private CCTextField _myTextFieldLeft;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer t) {
		
		_myTextFieldLeft = new CCTextField(new CCTextureMapFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50, 2, 2), "ÄäÜüÖöß")
				.position(0, -100)
				.align(CCTextAlign.LEFT)
				.fontSize(150);
	}
	
	private double _myRotation = 0;
	
	@Override
	public void update(CCGLTimer theTimer) {
//		double myFontSize = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, 50, 150);
//		_myRotation = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, -15, 15);
//		_myTextFieldLeft.fontSize(myFontSize);
//		_myTextFieldCenter.fontSize(myFontSize);
//		_myTextFieldRight.fontSize(myFontSize);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.pushMatrix();
		g.rotate(_myRotation);
		g.translate(0,0);
		g.color(1d);
		g.line(-g.width()/2, -100, g.width()/2, -100);
		g.line(-g.width()/2, 0, g.width()/2, 0);
		g.line(-g.width()/2, 100, g.width()/2, 100);
		
		_myTextFieldLeft.draw(g);
		g.line(0, -50, _myTextFieldLeft.width(), -50);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCSpecialCharacterDemo myDemo = new CCSpecialCharacterDemo();
		myDemo.run();
	}
}
