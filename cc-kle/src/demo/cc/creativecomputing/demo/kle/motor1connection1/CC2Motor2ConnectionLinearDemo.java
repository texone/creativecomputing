package cc.creativecomputing.demo.kle.motor1connection1;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.effects.CCSimpleGradientEffect;
import cc.creativecomputing.effects.modulation.CCTimeSource;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCViewport;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.analyze.CCKleRealtimeAnalyzer;
import cc.creativecomputing.kle.config.CC2Motor2ConnectionLinearConfigCreator;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionLinearBounds;
import cc.creativecomputing.kle.sequence.CCKleSequenceAnimation;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder;
import cc.creativecomputing.math.filter.CCChebFilter;

public class CC2Motor2ConnectionLinearDemo extends CCGL2Adapter {
	

	@CCProperty(name = "multi camera controller")
	private Map<String, CCCameraController> _myMultiControllerMap = new HashMap<>();
	
	@CCProperty(name = "bounds")
	private CC2Motor2ConnectionLinearBounds _cBounds = new CC2Motor2ConnectionLinearBounds();

	@CCProperty(name = "elements")
	private CCKleEffectables _myEffectables;
	
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
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		new CC2Motor2ConnectionLinearConfigCreator(60, 317, 200, 162.5, 0, 30, 10).saveXML("linear/config");
		
		_myMultiControllerMap.put("controller 0", new CCCameraController(this, g, 0.0, 0.0, 0.5, 0.5, 0, 0, 0, 100));
		_myMultiControllerMap.put("controller 1", new CCCameraController(this, g, 0.5, 0.0, 0.5, 0.5, 0, 0, 0, 100));
		_myMultiControllerMap.put("controller 2", new CCCameraController(this, g, 0.0, 0.5, 0.5, 0.5, 0, 0, 0, 100));
		_myMultiControllerMap.put("controller 3", new CCCameraController(this, g, 0.5, 0.5, 0.5, 0.5, 0, 0, 0, 100));
		
		_myEffectables = new CCKleEffectables(
			CCNIOUtil.dataPath("linear/config"), 
			_cBounds,
			1
		);
//		for(CCKleEffectable myElement:_mySequenceElements){
//			myElement.column(myElement.motorSetup().channels().get(0).column());
//			myElement.row(myElement.motorSetup().channels().get(0).row());
//		}
		
		_myEffectManager = new CCKleEffectManager(_myEffectables, CCKleChannelType.MOTORS, "rotation","lift");
		_myEffectManager.addFilter("lowpass", new CCChebFilter());
		_myEffectManager.addRelativeSources(new CCTimeSource("seconds"));
		_myEffectManager.addRelativeSources(new CCTimeSource("minutes"));
		_myEffectManager.addRelativeSources(new CCTimeSource("hours"));
		_myEffectManager.addRelativeSources(new CCTimeSource("day"));
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		_myEffectManager.put("sequence", new CCKleSequenceAnimation(_myEffectables.mappings().get(CCKleChannelType.MOTORS)));
		
		_myLightAnimator = new CCKleEffectManager(_myEffectables, CCKleChannelType.LIGHTS, "r", "g", "b");
		_myEffectManager.addRelativeSources(new CCTimeSource("seconds"));
		_myEffectManager.addRelativeSources(new CCTimeSource("minutes"));
		_myEffectManager.addRelativeSources(new CCTimeSource("hours"));
		_myEffectManager.addRelativeSources(new CCTimeSource("day"));
		_myLightAnimator.put("offset", new CCOffsetEffect());
		_myLightAnimator.put("signal", new CCSignalEffect());
		_myLightAnimator.put("gradient1", new CCSimpleGradientEffect());

		_myRecorder = new CCSequenceRecorder(this, _myEffectables, theAnimator);
		_myRecorder.addEffectManager(_myEffectManager, CCKleChannelType.MOTORS);
		_myRecorder.addEffectManager(_myLightAnimator, CCKleChannelType.LIGHTS);

		_myAnalyzer = new CCKleRealtimeAnalyzer(_myEffectables, theAnimator, CCKleChannelType.MOTORS);

		_cScreenCapture = new CCScreenCaptureController(this, theAnimator);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
		_myLightAnimator.update(theAnimator);
		_myRecorder.update(theAnimator);
	}
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();
	
	private void drawModel(CCGraphics g) {
		_cAttributes.start(g);
		for(CCKleEffectable myEffectable:_myEffectables){
			g.pushMatrix();
			g.applyMatrix(myEffectable.matrix());
			g.pointSize(3);
			g.color(0);
			g.beginShape(CCDrawMode.POINTS);
			g.vertex(myEffectable.motorSetup().channels().get(0).connectionPosition());
			g.vertex(myEffectable.motorSetup().channels().get(1).connectionPosition());
			g.vertex(myEffectable.motorSetup().channels().get(0).position());
			g.vertex(myEffectable.motorSetup().channels().get(1).position());
			g.vertex(myEffectable.motorSetup().centroid());
			g.endShape();

			g.beginShape(CCDrawMode.LINE_STRIP);
			g.vertex(myEffectable.motorSetup().channels().get(0).position());
			g.vertex(myEffectable.motorSetup().channels().get(0).connectionPosition());
			g.vertex(myEffectable.motorSetup().channels().get(1).connectionPosition());
			g.vertex(myEffectable.motorSetup().channels().get(1).position());
			g.vertex(myEffectable.motorSetup().centroid());
			g.endShape();
			
			g.color(255,100);
			g.beginShape(CCDrawMode.LINES);
			g.vertex(-100,0,0);
			g.vertex(100,0,0);
			g.endShape();
			
			g.pushMatrix();
			g.translate(myEffectable.motorSetup().channels().get(0).connectionPosition());
			g.rotate(myEffectable.motorSetup().rotateZ());
			g.color(myEffectable.lightSetup().color());
			g.rect(-110,-10,220,160);
			g.popMatrix();
			
			g.popMatrix();
		}
		_cAttributes.end(g);
		
		_myAnalyzer.draw3D(g);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(100);
		g.clear();
		for(CCCameraController myController:_myMultiControllerMap.values()){
			g.pushAttribute();
			g.pushMatrix();
			myController.camera().draw(g);
			
			drawModel(g);
			
			g.popMatrix();
			g.color(255);
			CCViewport vp = myController.camera().viewport();
			g.camera().draw(g);
			g.beginShape(CCDrawMode.LINE_LOOP);
			g.vertex(-g.width() / 2 + vp.x(), -g.height() / 2 + vp.y());
			g.vertex(-g.width() / 2 + vp.x() + vp.width(), -g.height() / 2 + vp.y());
			g.vertex(-g.width() / 2 + vp.x() + vp.width(), -g.height() / 2 + vp.y() + vp.height());
			g.vertex(-g.width() / 2 + vp.x(), -g.height() / 2 + vp.y() + vp.height());
			g.endShape();
			g.popAttribute();
		}
		
		
		
		
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

