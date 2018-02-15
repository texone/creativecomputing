package cc.creativecomputing.demo.kle.motor1connection1;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.CCKleEffectableRenderer;
import cc.creativecomputing.kle.analyze.CCKleRealtimeAnalyzer;
import cc.creativecomputing.kle.config.CC1Motor1ConnectionConfigCreator;
import cc.creativecomputing.kle.config.CC2MotorRotationAxisConfigCreator;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CC2MotorRotationAxisBounds;
import cc.creativecomputing.kle.sequence.CCKleSequenceAnimation;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.filter.CCChebFilter;
import cc.creativecomputing.math.filter.CCExponentialSmoothingFilter;
import cc.creativecomputing.math.filter.CCFIRFilter;

public class CC2MotorRotationAxisDemo extends CCGL2Adapter {
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	

	@CCProperty(name = "bounds")
	private CC2MotorRotationAxisBounds _cBounds = new CC2MotorRotationAxisBounds();

	private CCKleEffectables _mySequenceElements;
	
	@CCProperty(name = "renderer")
	private CCKleEffectableRenderer _myRenderer;
	
	@CCProperty(name = "effects")
	private CCKleEffectManager _myEffectManager;

	@CCProperty(name = "record")
	protected CCSequenceRecorder _myRecorder;

	@CCProperty(name = "analyzer")
	private CCKleRealtimeAnalyzer _myAnalyzer;
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		CC2MotorRotationAxisConfigCreator myConfigCreator = new CC2MotorRotationAxisConfigCreator(1, 18, 0, 90, 200, 5);
		myConfigCreator.saveXML("config_sh");
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		_mySequenceElements = new CCKleEffectables(
			CCNIOUtil.dataPath("config_sh"), 
			_cBounds,
			1
		);
		for(CCKleEffectable myElement:_mySequenceElements){
			
		}
		
		_myRenderer = new CCKleEffectableRenderer(_mySequenceElements){
			@Override
			public void drawElement(CCGraphics g, CCKleEffectable theElement) {
				CCLog.info(theElement.motorSetup().elementOffset());
				g.ellipse(theElement.motorSetup().elementOffset(), 50);
			}
		};
		
		_myEffectManager = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.MOTORS, "angle", "radius");
		_myEffectManager.addFilter("lowpass", new CCChebFilter());
		_myEffectManager.addFilter("ma", new CCExponentialSmoothingFilter());
		_myEffectManager.addFilter("fir", new CCFIRFilter());
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		_myEffectManager.put("sequence", new CCKleSequenceAnimation(_mySequenceElements.mappings().get(CCKleChannelType.MOTORS)));

		_myRecorder = new CCSequenceRecorder(this, _mySequenceElements, theAnimator);

		_myAnalyzer = new CCKleRealtimeAnalyzer(_mySequenceElements, theAnimator, CCKleChannelType.MOTORS);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(50);
		g.clear();
		_myCameraController.camera().draw(g);
		
		g.color(255);
		g.pointSize(10);
		g.pushMatrix();
//		g.scale(0.3);
		
		g.color(255);
		g.ellipse(0, 0, 260, 260);
		for(CCKleEffectable theElement:_mySequenceElements){
//			g.pushMatrix();
//			g.applyMatrix(theElement.matrix());
			g.color(0);
			g.ellipse(theElement.motorSetup().elementOffset(), 50);
//			g.popMatrix();
		}
		
		_myAnalyzer.draw3D(g);
		g.popMatrix();
		
		g.noDepthTest();
		g.camera().draw(g);
		_myAnalyzer.draw2D(g);
	}

	public static void main(String[] args) {

		CC2MotorRotationAxisDemo demo = new CC2MotorRotationAxisDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo, true);
		myAppManager.glcontext().size(600, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

