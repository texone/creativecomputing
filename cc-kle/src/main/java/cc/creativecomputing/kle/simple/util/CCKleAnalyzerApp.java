package cc.creativecomputing.kle.simple.util;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.analyze.CCFileMotionAnalyzer;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionBounds;

/**
* @author christian riekoff
*/
public class CCKleAnalyzerApp extends CCGL2Adapter{
	
	private CCSequenceElements _myElements;

	
	
	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "bounds")
	private CC2Motor1ConnectionBounds _cBounds = new CC2Motor1ConnectionBounds();
	
	@CCProperty(name = "analyzer")
	private CCFileMotionAnalyzer _myAnalyzer;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		_myElements = new CCSequenceElements(
			CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/mapping.xml")), 
			CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/sculpture.xml")), _cBounds, 1);
		
		_myAnalyzer = new CCFileMotionAnalyzer(_myElements, theAnimator, CCKleChannelType.MOTORS);
		
		_myCameraController = new CCCameraController(this, g, 100);

		_myFont = CCFontIO.createTextureMapFont("arial", 20);
	}
	
	
	@Override
	public void update(final CCAnimator theAnimator){
	}
	
	private int _myFrame = 0;
	private int _myMovie = 0;
	private boolean _myIsRecording = false;
	

	private CCTextureMapFont _myFont;

	@Override
	public void display(CCGraphics g) {
		g.clearColor(125, 255);
		g.clear();
		
		_myCameraController.camera().draw(g);
		
		if(_myIsRecording){
			CCScreenCapture.capture(CCNIOUtil.dataPath("export_" + _myMovie + "/capure_" + CCFormatUtil.nf(_myFrame++, 5) + ".png"), g.width(), g.height());
			g.color(255,0,0);
			g.rect(-g.width()/2 + 20, -g.height()/2 + 20, 100, 100);
		}

		
		_myAnalyzer.draw3D(g);
		
		g.camera().draw(g);
		_myAnalyzer.draw2D(g);

//		CCMotorSetup mySetup = _myElements.elements().get(0).element().motorSetup();
//		float height0 = mySetup.channels().get(0).connectionPosition().y - mySetup.channels().get(0).position().y;
//		float height1 = mySetup.channels().get(1).connectionPosition().y - mySetup.channels().get(1).position().y;
//		g.textFont(_myFont);
//		g.text(CCFormatUtil.nf(mySetup.channels().get(0).value(), 4, 4) + " : " + CCFormatUtil.nf(mySetup.channels().get(1).value(), 4, 4),0,0);
//		g.text(CCFormatUtil.nf(height0, 4, 4) + " : " + CCFormatUtil.nf(height1, 4, 4),0,20);
//		g.text(g.height + " : " + g.width,0,40);
	}
	
//	@Override
//	public void keyPressed(CCKeyEvent theKeyEvent) {
//		switch(theKeyEvent.keyCode()){
//		case VK_R:
//			_myRecorder.recordFrame(CCIOUtil.selectOutput("save output"));
//			break;
//		case VK_C:
//			CCScreenCapture.capture("export/capure_" + frameCount + ".png", width, height);
//			break;
//		case VK_M:
//			_myIsRecording = !_myIsRecording;
//			if(_myIsRecording){
//				_myMovie++;
//				_myFrame = 0;
//				fixUpdateTime(1f/30);
//				
//			}else{
//				freeUpdateTime();
//			}
//			break;
//		default:
//			break;
//		}
//	}
	
	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCKleAnalyzerApp());
		myAppManager.glcontext().size(1800, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.glcontext().inVsync = false;
		myAppManager.start();
	}
	
}