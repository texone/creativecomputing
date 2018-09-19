package cc.creativecomputing.gl.demo.font;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextArea;
import cc.creativecomputing.graphics.font.CCVectorFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextAreaDemo extends CCGLApp {

	private static String TEXT = "Bitte informieren Sie sich kurz vor Ihrer Reise über mögliche ÄnderungenIhrerReisedaten unter www.bahn.de/reiseplan oder mobil über die App DB Navigator. Achten Sie auch auf Informationen und Ansagen im Zug und am Bahnhof. Wir danken Ihnen für Ihre Buchung und wünschen Ihnen eine angenehme Reise!.";

	private CCTextArea _myTextArea;
	private CCVectorFont _myVectorFont;

	private double _myScale;

	@Override
	public void setup() {
		_myVectorFont = new CCVectorFont(CCCharSet.EXTENDED, CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 20);
		_myTextArea = new CCTextArea(_myVectorFont, TEXT);
		_myTextArea.position(-400, 400);
		_myTextArea.dimension(400, 400);
		_myTextArea.align(CCTextAlign.LEFT);
		_myTextArea.fontSize(50);

	}

	private double _myRotation = 0;

	@Override
	public void update(CCGLTimer theTimer) {
		// double myFontSize = CCMath.map(CCMath.sin(theTimer.time()), -1, 1,
		// 50, 150);
		// _myRotation = CCMath.map(CCMath.sin(theTimer.time()), -1, 1, -15,
		// 15);
		// _myTextFieldLeft.fontSize(myFontSize);
		// _myTextFieldCenter.fontSize(myFontSize);
		// _myTextFieldRight.fontSize(myFontSize);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(1.0f, 0.0f, 0.0f, 0.0f);
		g.clear();
		g.pushMatrix();
		g.rotate(_myRotation);
		g.translate(0, 0);
		g.color(0d);
		g.rect(-400, 0, 400, 400);
		g.color(1d);
		double mysecondLine = -_myVectorFont.verticalAdvance() * _myVectorFont.scaleForPixelHeight(150);
		g.line(-g.width() / 2, 0, g.width() / 2, 0);
		g.line(-g.width() / 2, mysecondLine, g.width() / 2, mysecondLine);

		_myTextArea.draw(g);
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCTextAreaDemo myDemo = new CCTextAreaDemo();
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(myDemo);
		myApplicationManager.run();
	}
}
