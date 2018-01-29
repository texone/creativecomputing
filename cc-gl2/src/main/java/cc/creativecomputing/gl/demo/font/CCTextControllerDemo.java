package cc.creativecomputing.gl.demo.font;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextAlign;
import cc.creativecomputing.graphics.font.CCTextArea;
import cc.creativecomputing.graphics.font.CCTextFieldController;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;

public class CCTextControllerDemo extends CCGLApp {

	private static String TEXT = "Bitte informieren Sie sich kurz vor Ihrer Reise über möglicheÄnderungenIhrerReisedaten unter www.bahn.de/reiseplan oder mobil über die App DB Navigator. Achten Sie auch auf Informationen und Ansagen im Zug und am Bahnhof. Wir danken Ihnen für Ihre Buchung und wünschen Ihnen eine angenehme Reise!.";

	private CCTextArea _myTextArea;
	private CCTextureMapFont _myVectorFont;

	private double _myScale;
	
	private CCTextFieldController _myController;

	@Override
	public void setup(CCGraphics g, CCGLTimer theTimer) {
		_myVectorFont = new CCTextureMapFont(CCCharSet.EXTENDED, CCNIOUtil.dataPath("fonts/Lato/Lato-Regular.ttf"), 20);
		_myTextArea = new CCTextArea(_myVectorFont, TEXT);
		_myTextArea.position(-400, 400);
		_myTextArea.dimension(400, 400);
		_myTextArea.align(CCTextAlign.LEFT);
		_myTextArea.fontSize(50);
		_myController = new CCTextFieldController(_myTextArea, _myMainWindow);
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
		g.clearColor(0f, 0.0f, 0.0f, 0.0f);
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

		_myController.draw(g);
		_myTextArea.draw(g);
		g.popMatrix();
	}

	public static void main(String[] args) {
		CCTextControllerDemo myDemo = new CCTextControllerDemo();
		myDemo.width = 1024;
		myDemo.height = 768;
		myDemo.run();
		
	}
}
