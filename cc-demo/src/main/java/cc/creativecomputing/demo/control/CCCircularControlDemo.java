package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCVector2;

public class CCCircularControlDemo extends CCGL2Adapter {
	
	private CCVector2 _myStart = null;
	private CCVector2 _myEnd = null;
	
	private double _myValue = 0;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		mousePressed().add(e -> {
			_myStart = new CCVector2(e.x(), e.y());
			_myEnd = new CCVector2(e.x(), e.y());
		});
		
		mouseReleased().add(e -> {
			_myStart = null;
			_myEnd = null;
		});
		
		mouseDragged().add(e ->{
			_myEnd = new CCVector2(e.x(), e.y());
		});
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_myStart != null){
			_myValue = new CCVector2(0,10).dot(_myEnd.subtract(_myStart));
		}
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		
		if(_myStart != null){
			g.ellipse(_myStart, 20);
		}
		
		g.text(_myValue, 200,200);
	}

	public static void main(String[] args) {

		CCCircularControlDemo demo = new CCCircularControlDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

