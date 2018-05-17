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

public class CCUIImageWidgetDemo extends CCGLApp {
	
	private CCUIContext _myContext;
	@Override
	public void setup() {
		
		CCUIHorizontalFlowPane myHorizontalPane = new CCUIHorizontalFlowPane();
		myHorizontalPane.translation().set(-framebufferSize().x / 2, framebufferSize().y / 2);
		_myContext = new CCUIContext(this, myHorizontalPane);		
		_myContext.widget().addChild(new CCUIImageWidget(new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("waltz.jpg")))));
		
	

		scrollEvents.add(pos -> {CCLog.info(pos.x,pos.y);});
		
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
		g.line(-g.width()/2,100,g.width()/2,100);
		g.line(0,-g.height()/2,0,g.height()/2);
		g.pushAttribute();
		_myContext.widget().draw(g);
		g.popAttribute();
	}

	public static void main(String[] args) {
		CCGLApplicationManager myApplicationManager = new CCGLApplicationManager(new CCUIImageWidgetDemo());
		myApplicationManager.run();
	}
}
