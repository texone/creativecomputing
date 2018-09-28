package cc.creativecomputing.demo.ui;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.widget.CCUIDialog;
import cc.creativecomputing.ui.widget.CCUIImageWidget;
import cc.creativecomputing.yoga.CCYogaNode;

public class CCUIDialogDemo extends CCGLApp {
	@Override
	public void setup() {

		CCUIDialog.createTextInput(appManager, "type text", "here eins zwei trei", 100, 100);
	}

	@Override
	public void update(final CCGLTimer theTimer) {
		
	}

	@Override
	public void display(CCGraphics g) {
	}
	

	public static CCGLApplicationManager appManager;

	public static void main(String[] args) {
		appManager = new CCGLApplicationManager(new CCUIDialogDemo());
		appManager.run();
	}
}
