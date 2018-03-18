package cc.creativecomputing.demo.kle.svgled;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGElement;
import cc.creativecomputing.model.svg.CCSVGGroup;
import cc.creativecomputing.model.svg.CCSVGIO;

public class CCSVGLedDemo extends CCGL2Adapter {
	
	private List<CCLedGroup> _myLedGroups = new ArrayList<>();
	
	@CCProperty(name = "scale", min = 0, max = 1)
	private double _cScale = 0.1;
	
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("demo/leds.svg"));
		
		for(CCSVGElement myElement:myDocument){
			_myLedGroups.add(new CCLedGroup((CCSVGGroup)myElement));
		}
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		g.pushMatrix();
		g.scale(_cScale);
		for(CCLedGroup myGroup:_myLedGroups){
			g.color(1d);
			for(CCLed myLed:myGroup){
				g.ellipse(myLed.center.x,  myLed.center.y, myLed.diameter);
			}
		}
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		
//		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("demo/leds.svg"));
//		
//		for(CCSVGElement myElement:myDocument){
//			new CCLedGroup((CCSVGGroup)myElement);
//		}

		CCSVGLedDemo demo = new CCSVGLedDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

