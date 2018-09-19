package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextField;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextFieldMetricsDemo extends CCGLApp{
	
	private CCTextField _myTextField;
	private CCTextureMapFont _myVectorFont;
	
	@Override
	public void setup() {
		
		_myVectorFont = new CCTextureMapFont(CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 50);
		_myTextField = new CCTextField(_myVectorFont, "Font\nMetrics")
			.position(0,    0)
			.align(CCTextAlign.LEFT)
			.fontSize(150);
		
		g.textFont(new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 20,2,2));
	}
	
	@Override
	public void update(CCGLTimer theTimer) {
	}
	
	private void drawMetricLine(CCGraphics g, String theMetric, double theValue) {
		g.line(-g.width()/2, theValue, g.width()/2, theValue);
		g.text(theMetric + " " + theValue, -g.width()/2, theValue);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();

		drawMetricLine(g, "vertical advance", _myTextField.verticalAdvance());
		drawMetricLine(g, "vertical advance", -_myTextField.verticalAdvance());
		drawMetricLine(g, "base line", 0);
		drawMetricLine(g, "ascent", -_myTextField.ascent());
		drawMetricLine(g, "descent", -_myTextField.descent());
		
		g.rect(_myTextField.position().x,  _myTextField.position().y-_myTextField.ascent(), _myTextField.width(), _myTextField.height(), true);
//		g.line(-g.width()/2, _myTextField.ascent(), g.width()/2, _myTextField.ascent());
//		g.line(-g.width()/2, 0, g.width()/2, 0);
//		g.line(-g.width()/2, _myTextField.descent(), g.width()/2, _myTextField.descent());
//		g.line(-g.width()/2, -_myTextField.verticalAdvance(), g.width()/2, -_myTextField.verticalAdvance());
		
		_myTextField.draw(g);
	}
	
	public static void main(String[] args) {
		CCTextFieldMetricsDemo myDemo = new CCTextFieldMetricsDemo();

		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
