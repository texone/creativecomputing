package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextFieldMetricsDemo extends CCGLApp{
	
	private CCTextField _myTextField;
	private CCVectorFont _myVectorFont;
	
	@Override
	public void setup() {
		
		_myVectorFont = new CCVectorFont(null,CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50);
		_myTextField = new CCTextField(_myVectorFont, "Font Metrics")
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

		g.line(-g.width()/2, _myTextField.verticalAdvance(), g.width()/2, _myTextField.verticalAdvance());
		g.line(-g.width()/2, _myTextField.ascent(), g.width()/2, _myTextField.ascent());
		g.line(-g.width()/2, 0, g.width()/2, 0);
		g.line(-g.width()/2, _myTextField.descent(), g.width()/2, _myTextField.descent());
		g.line(-g.width()/2, -_myTextField.verticalAdvance(), g.width()/2, -_myTextField.verticalAdvance());
		
		_myTextField.draw(g);
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		CCTextFieldMetricsDemo myDemo = new CCTextFieldMetricsDemo();

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
