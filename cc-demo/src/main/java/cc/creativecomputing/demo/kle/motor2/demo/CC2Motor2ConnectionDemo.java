package cc.creativecomputing.demo.kle.motor2.demo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectableRenderer;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionCalculations;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionDemo extends CCGL2Adapter{
	
	@CCProperty(name = "bounds")
	private CC2Motor2ConnectionCalculations _cBounds = new CC2Motor2ConnectionCalculations();
	
	private CCKleEffectables _mySequenceElements;
	
	@CCProperty(name = "renderer")
	private CCKleEffectableRenderer _myRenderer;
	
	@CCProperty(name = "camera controller")
	private CCCameraController _cCameraController;
	

	@CCProperty(name = "effects")
	private CCKleEffectManager _myAnimator;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySequenceElements = new CCKleEffectables();
		for(int i = 0; i < 20;i++){
			List<CCMotorChannel> myMotorChannels = new ArrayList<>();
			myMotorChannels.add(new CCMotorChannel(i * 2, new CCVector3(-200, 0, i * 20), new CCVector3(-100, 400, i * 20)));
			myMotorChannels.add(new CCMotorChannel(i * 2 + 1, new CCVector3( 200, 0, i * 20), new CCVector3( 100, 400, i * 20)));
			
			
			_mySequenceElements.add(CCKleEffectable.createMotorElement(i, myMotorChannels,_cBounds, null, new CCVector3(0, 500, i * 20), 100));
		}
		_cBounds.setElements(_mySequenceElements);
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		_myRenderer = new CCKleEffectableRenderer(_mySequenceElements);
		
		_myAnimator = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.MOTORS, "x", "y");
		_myAnimator.put("offset animation", new CCOffsetEffect());
	}
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		
	}
	
	@Override
	public void init(CCGraphics g) {
		
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_cBounds.update(theAnimator);
		_myAnimator.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		_cCameraController.camera().draw(g);
		_myRenderer.draw(g);
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
