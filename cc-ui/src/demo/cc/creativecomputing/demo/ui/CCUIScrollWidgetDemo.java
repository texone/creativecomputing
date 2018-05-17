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
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;
import cc.creativecomputing.ui.widget.CCUIImageWidget;
import cc.creativecomputing.ui.widget.CCUIScrollWidget;

public class CCUIScrollWidgetDemo extends CCGLApp {
	
	private CCUIContext _myContext;
	@Override
	public void setup() {
		
		CCUIHorizontalFlowPane myHorizontalPane = new CCUIHorizontalFlowPane();
//		myHorizontalPane.translation().set(-framebufferSize().x / 2, framebufferSize().y / 2);
		_myContext = new CCUIContext(this, myHorizontalPane);
		CCUIImageWidget myImage = new CCUIImageWidget(new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("waltz.jpg"))));
		myImage.mouseDragged.add(e -> {CCLog.info(e);});
		CCUIScrollWidget myScrollWidget = new CCUIScrollWidget(myImage, 300, 300);
		_myContext.widget().addChild(myScrollWidget);
		
//		_myMainWindow.mouseMoveEvents.add(pos -> {
//			if(_myWidget.isInside(pos.x - g.width()/2, g.height()/2 - pos.y)){
//				_myWidget.border(new CCUILineBorderDecorator(CCColor.RED.clone(), 2, 30));
//			}else{
//				_myWidget.border(new CCUILineBorderDecorator(CCColor.WHITE.clone(), 2, 30));
//			}
//		});
	}

	@Override
	public void update(final CCGLTimer theTimer) {
		_myContext.widget().update(theTimer);
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.pushAttribute();
		_myContext.widget().draw(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(new CCUIScrollWidgetDemo());
		myApplicationManager.run();
	}
}
