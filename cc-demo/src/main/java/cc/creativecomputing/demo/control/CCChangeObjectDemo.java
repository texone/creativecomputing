package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCChangeObjectDemo extends CCGL2Adapter {
	
	private class CCObject0{
		@CCProperty(name = "control 0", min = 0, max = 1)
		private double _myControl0 = 0;
		@CCProperty(name = "control 1", min = 0, max = 1)
		private double _myControl1 = 0;
		@CCProperty(name = "control 2", min = 0, max = 1)
		private double _myControl2 = 0;
	}
	
	private class CCObject1{
		@CCProperty(name = "control 3", min = 0, max = 1)
		private double _myControl3 = 0;
		@CCProperty(name = "control 4", min = 0, max = 1)
		private double _myControl4 = 0;
		@CCProperty(name = "control 5", min = 0, max = 1)
		private double _myControl5 = 0;
	}

	@CCProperty(name = "object", readBack = true)
	private Object _myObject = new CCObject0();
	
	@CCProperty(name = "change object")
	private void changeObject(){
		if(_myObject instanceof CCObject0){
			_myObject = new CCObject1();
		}else{
			_myObject = new CCObject0();
		}
	}
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		
	}

	public static void main(String[] args) {

		CCChangeObjectDemo demo = new CCChangeObjectDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
