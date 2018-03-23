package cc.creativecomputing.demo.context;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCMouseMotionDemo extends CCGL2Adapter{
	
	
	private float _myMouseX;
	private float _myMouseY;
	
	@Override
	public void init(CCGraphics g) {
		mouseMotionListener().add(new CCMouseAdapter() {
			
			
			@Override
			public void mouseMoved(CCMouseEvent theMouseEvent) {
				_myMouseX = theMouseEvent.x();
				_myMouseY = theMouseEvent.y();
				
				CCLog.info(g.width() + ":" + g.height() + ":" + _myMouseX + ":" + _myMouseY);
			}
		});
		
		g.camera(new CCCamera(g.width(), g.height()));
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.color(255,0,0);
		g.rect(-g.width()/4, - g.height()/4, g.width()/2, g.height()/2);
		g.color(255);
		g.ellipse(_myMouseX,  _myMouseY, 20);

//		CCLog.info(g.width + ":" + g.height);
	}
	
	public static void main(String[] args) {
		
		
		CCMouseMotionDemo demo = new CCMouseMotionDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
