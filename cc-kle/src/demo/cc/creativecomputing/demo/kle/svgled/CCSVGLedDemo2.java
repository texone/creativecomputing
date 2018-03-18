package cc.creativecomputing.demo.kle.svgled;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.config.CC1Motor1ConnectionConfigCreator;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGElement;
import cc.creativecomputing.model.svg.CCSVGGroup;
import cc.creativecomputing.model.svg.CCSVGIO;

public class CCSVGLedDemo2 extends CCGL2Adapter {
	
	private List<CCLedGroup> _myLedGroups = new ArrayList<>();
	
	@CCProperty(name = "scale", min = 0, max = 1)
	private double _cScale = 0.1;
	
	private CCKleEffectables _mySequenceElements;
	
	@CCProperty(name = "effects")
	private CCKleEffectManager _myEffectManager;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		new CCBrightnessLightConfigCreator().saveXML("config_leds");
		
		_mySequenceElements = new CCKleEffectables(
			CCNIOUtil.dataPath("config_leds"), 
			null,
			1
		);
			
		_myEffectManager = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.LIGHTS, "brightness");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
		g.pushMatrix();
		g.scale(_cScale);
		g.color(255);
		g.pointSize(10);
		for(CCKleEffectable myElement:_mySequenceElements){
			g.pushMatrix();
			g.applyMatrix(myElement.matrix());
			g.color(myElement.lightSetup().color());
			g.ellipse(0,0, 20);
			g.popMatrix();
		}
		g.popMatrix();
	}
	
	public static void main(String[] args) {
		
//		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("demo/leds.svg"));
//		
//		for(CCSVGElement myElement:myDocument){
//			new CCLedGroup((CCSVGGroup)myElement);
//		}

		CCSVGLedDemo2 demo = new CCSVGLedDemo2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

