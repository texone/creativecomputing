package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCOutlineFont;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextFieldDemo extends CCGLApp{
	
	private CCTextField _myTextFieldLeft;
	private CCTextField _myTextFieldCenter;
	private CCTextField _myTextFieldRight;
	
	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		
		_myTextFieldLeft = new CCTextField(new CCTextureMapFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50, 2, 2), "Align Left")
				.position(0, -100)
				.align(CCTextAlign.LEFT)
				.fontSize(150);
		_myTextFieldCenter = new CCTextField(new CCVectorFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50), "Align Center")
				.position(0,    0)
				.align(CCTextAlign.CENTER)
				.fontSize(150);
		_myTextFieldRight = new CCTextField(new CCOutlineFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50), "Align Right")
				.position(0,  100)
				.align(CCTextAlign.RIGHT)
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
		_myTextFieldCenter.draw(g);
		g.line(-_myTextFieldCenter.width()/2, 50, _myTextFieldCenter.width() / 2, 50);
		_myTextFieldRight.draw(g);
		g.line(-_myTextFieldRight.width(), 150, 0, 150);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCTextFieldDemo myDemo = new CCTextFieldDemo();
		myDemo.run();
	}
}
