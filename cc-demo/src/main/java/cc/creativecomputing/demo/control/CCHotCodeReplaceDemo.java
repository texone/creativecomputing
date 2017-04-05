package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCHotCodeReplaceDemo extends CCGL2Adapter {
	
	@CCProperty(name = "realtime compile")
	private CCRealtimeHotReplaceInterface _myObject = new CCRealtimeHotReplaceClass();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}
	
	private CCRealtimeHotReplaceInterface _myLastObject;

	@Override
	public void update(CCAnimator theAnimator) {
		_myObject.doSomthing();
		
		if(_myObject != _myLastObject){
			_myLastObject = _myObject;
			CCLog.info(_myObject);
		}
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCHotCodeReplaceDemo demo = new CCHotCodeReplaceDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
