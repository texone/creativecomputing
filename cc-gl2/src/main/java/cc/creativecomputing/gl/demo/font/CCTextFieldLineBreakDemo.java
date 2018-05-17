package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextFieldLineBreakDemo extends CCGLApp{
	
	private CCTextField _myTextField;
	private CCVectorFont _myVectorFont;
	
	private double _myScale;
	
	@Override
	public void setup() {
		_myVectorFont = new CCVectorFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50);
		_myTextField = new CCTextField(_myVectorFont, "Line 1 \nLine2 Line2\nLine3 Line3 Line3")
			.position(0,    0)
			.align(CCTextAlign.CENTER)
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
		double mysecondLine = -_myVectorFont.verticalAdvance() * _myVectorFont.scaleForPixelHeight(150);
		g.line(-g.width()/2, 0, g.width()/2, 0);
		g.line(-g.width()/2, mysecondLine, g.width()/2, mysecondLine);
		
		_myTextField.draw(g);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCTextFieldLineBreakDemo myDemo = new CCTextFieldLineBreakDemo();
		
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
