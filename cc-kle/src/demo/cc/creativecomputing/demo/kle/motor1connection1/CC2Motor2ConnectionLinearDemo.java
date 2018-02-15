package cc.creativecomputing.demo.kle.motor1connection1;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.effects.CCSimpleGradientEffect;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.analyze.CCKleRealtimeAnalyzer;
import cc.creativecomputing.kle.config.CC1Motor1ConnectionConfigCreator;
import cc.creativecomputing.kle.config.CC2Motor2ConnectionLinearConfigCreator;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionLinearBounds;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder;

public class CC2Motor2ConnectionLinearDemo extends CCGL2Adapter {
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	

	@CCProperty(name = "bounds")
	private CC2Motor2ConnectionLinearBounds _cBounds = new CC2Motor2ConnectionLinearBounds();

	private CCKleEffectables _mySequenceElements;
	
	@CCProperty(name = "effects")
	private CCKleEffectManager _myEffectManager;
	

	@CCProperty(name = "lights")
	private CCKleEffectManager _myLightAnimator;

	@CCProperty(name = "record")
	protected CCSequenceRecorder _myRecorder;

	@CCProperty(name = "analyzer")
	private CCKleRealtimeAnalyzer _myAnalyzer;
	
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();

	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		new CC2Motor2ConnectionLinearConfigCreator(70, 50, 20, 0, 30, 5).saveXML("linear/config");
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		_mySequenceElements = new CCKleEffectables(
			CCNIOUtil.dataPath("linear/config"), 
			_cBounds,
			1
		);
//		for(CCKleEffectable myElement:_mySequenceElements){
//			myElement.column(myElement.motorSetup().channels().get(0).column());
//			myElement.row(myElement.motorSetup().channels().get(0).row());
//		}
		
		_myEffectManager = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.MOTORS, "rotation","lift");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		
		_myLightAnimator = new CCKleEffectManager(_mySequenceElements, CCKleChannelType.LIGHTS, "r", "g", "b");
		_myLightAnimator.put("offset", new CCOffsetEffect());
		_myLightAnimator.put("signal", new CCSignalEffect());
		_myLightAnimator.put("gradient1", new CCSimpleGradientEffect());

		_myRecorder = new CCSequenceRecorder(this, _mySequenceElements, theAnimator);

		_myAnalyzer = new CCKleRealtimeAnalyzer(_mySequenceElements, theAnimator, CCKleChannelType.MOTORS);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
		_myLightAnimator.update(theAnimator);
	}
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();
	

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myCameraController.camera().draw(g);
		
		g.color(255);
		g.pointSize(2);
		_cAttributes.start(g);
		for(CCKleEffectable mySphere:_mySequenceElements){
			g.pushMatrix();
			g.applyMatrix(mySphere.matrix());
//			g.point(mySphere.motorSetup().channels().get(0).connectionPosition());
//			g.point(mySphere.motorSetup().channels().get(1).connectionPosition());
//			g.line(mySphere.motorSetup().channels().get(0).position(),mySphere.motorSetup().channels().get(0).connectionPosition());
//			g.line(mySphere.motorSetup().channels().get(1).position(),mySphere.motorSetup().channels().get(1).connectionPosition());
//
//			g.line(mySphere.motorSetup().channels().get(0).connectionPosition(),mySphere.motorSetup().channels().get(1).connectionPosition());
			
//			CCLog.info(mySphere.lightSetup().color(), mySphere.lightSetup().channels().get(0).value(), mySphere.lightSetup().channels().get(1).value(), mySphere.lightSetup().channels().get(2).value());
			g.color(mySphere.lightSetup().color());
			g.beginShape(CCDrawMode.QUADS);
			g.vertex(mySphere.motorSetup().channels().get(0).position());
			g.vertex(mySphere.motorSetup().channels().get(0).connectionPosition());
			g.vertex(mySphere.motorSetup().channels().get(1).connectionPosition());
			g.vertex(mySphere.motorSetup().channels().get(1).position());
			g.endShape();
			g.popMatrix();
		}
		_cAttributes.end(g);
		
		_myAnalyzer.draw3D(g);
		
		g.camera().draw(g);
		_myAnalyzer.draw2D(g);
	}

	public static void main(String[] args) {

		CC2Motor2ConnectionLinearDemo demo = new CC2Motor2ConnectionLinearDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo, true);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

