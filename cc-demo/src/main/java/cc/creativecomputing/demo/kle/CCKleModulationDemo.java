package cc.creativecomputing.demo.kle;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectModulation;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;

public class CCKleModulationDemo extends CCGL2Adapter{
	
	
	private CCSequenceElements _mySequenceElements;
	
	@CCProperty(name = "modulation")
	private CCEffectModulation _cModulation;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		_mySequenceElements = new CCSequenceElements();
		for(int i = 0; i < 20;i++){
			CCSequenceElement myElement = new CCSequenceElement(i);
			myElement.idBlend(i / 20f);
			myElement.groupIDBlend(i / 20f);
			_mySequenceElements.add(myElement);
		}
		
		_cModulation = new CCEffectModulation();
	}
	
	@Override
	public void init(CCGraphics g) {
		
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		for(CCSequenceElement myElement:_mySequenceElements){
			double myMod = _cModulation.modulation(myElement, -1, 1);
			
			g.line(myElement.id() * 20, myMod * 200,myElement.id() * 20 + 20,myMod * 200);
		}
	}
	
	public static void main(String[] args) {
		
		
		CCKleModulationDemo demo = new CCKleModulationDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
