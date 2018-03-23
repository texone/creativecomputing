package cc.creativecomputing.demo.control;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCMapDoubleMinMaxDemo extends CCGL2Adapter{
	
	
	
	
	@CCProperty(name = "check", min = -0.5, max = 0.5)
	private Map<String, Double> _myValueMap = new LinkedHashMap<>();
	
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myValueMap.put("x", 0d);
		_myValueMap.put("y", 0d);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.ellipse(_myValueMap.get("x") * g.width(), _myValueMap.get("y") * g.height(), 20);
	}
	
	public static void main(String[] args) {
		
		
		CCMapDoubleMinMaxDemo demo = new CCMapDoubleMinMaxDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
