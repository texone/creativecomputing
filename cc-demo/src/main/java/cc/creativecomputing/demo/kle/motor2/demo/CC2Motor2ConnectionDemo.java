package cc.creativecomputing.demo.kle.motor2.demo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionDemo extends CCGL2Adapter{
	
	@CCProperty(name = "bounds")
	private CC2Motor2ConnectionBounds _cBounds = new CC2Motor2ConnectionBounds();
	
	private CCSequenceElements _mySequenceElements;
	
	private class CCMotorArea{
		
		
	}
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		_mySequenceElements = new CCSequenceElements();
		for(int i = 0; i < 20;i++){
			List<CCMotorChannel> myMotorChannels = new ArrayList<>();
			CCMotorChannel myMotor0 = new CCMotorChannel(i * 2, new CCVector3(-200, 0, i * 20), new CCVector3(-200, 400, i * 20));
			CCMotorChannel myMotor1 = new CCMotorChannel(i * 2 + 1, new CCVector3( 200, 0, i * 20), new CCVector3( 200, 400, i * 20));
			myMotorChannels.add(myMotor0);
			myMotorChannels.add(myMotor1);
//			CC2Motor2ConnectionSetup mySetup = new CC2Motor2ConnectionSetup(myMotorChannels, _cBounds, 200);
//			_mySequenceElements.add(new CCSequenceElement(i, mySetup));
		}
		_cBounds.setElements(_mySequenceElements);
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
			myElement.motorSetup().drawRopes(g);
			myElement.motorSetup().drawElementBounds(g);
		}
	}
	
	public static void main(String[] args) {
		
		
		CC2Motor2ConnectionDemo demo = new CC2Motor2ConnectionDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
