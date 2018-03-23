package cc.creativecomputing.kle.sequence;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.sequence.CCSequenceViewer.CCSequenceDrawMode;
import cc.creativecomputing.kle.sequence.CCSequenceViewer.CCSequenceDrawStyle;
import cc.creativecomputing.math.CCMath;

public class CCSequenceViewerApp extends CCGL2Adapter{
	
	private CCSequenceViewer _myViewer;
	
	@CCProperty(name = "max speed", min = 0, max = 300)
	private float _cMaxSpeed = 150;
	@CCProperty(name = "max acc", min = 0, max = 300)
	private float _cMaxAcc = 150;
	@CCProperty(name = "max jerk", min = 0, max = 300)
	private float _cMaxJerk = 150;
	@CCProperty(name = "line", min = 0, max = 350)
	private int _cLine = 0;
	@CCProperty(name = "line scale", min = 0, max = 1000)
	private int _cLineScale = 1000;
	@CCProperty(name = "draw mode")
	private CCSequenceDrawMode _cDrawMode = CCSequenceDrawMode.POSITION;
	@CCProperty(name = "draw style")
	private CCSequenceDrawStyle _cDrawStyle = CCSequenceDrawStyle.LINES;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g) {
		_myViewer = new CCSequenceViewer(g, CCNIOUtil.dataPath("manila_setup_V2.xml"));
		_myViewer.load(CCNIOUtil.dataPath("dragon.bin"));

		
//		addControls("app", "app", this);
	}
	
	@Override
	public void display(CCGraphics g) {

		_myViewer.maxSpeed(_cMaxSpeed);
		_myViewer.maxAcceleration(_cMaxAcc);
		_myViewer.maxJerk(_cMaxJerk);
		_myViewer.line(_cLine);
		_myViewer.lineScale(_cLineScale);
		_myViewer.drawMode(_cDrawMode);
		_myViewer.drawStyle(_cDrawStyle);
		
		g.camera().set(1800, 900, CCMath.radians(60));
		g.clearColor(255);
		g.clear();
		g.pushMatrix();
		g.scale(1f / 900);
		_myViewer.draw(g);
		g.popMatrix();
		
//		g.rect(0, 0, 0.1f,0.1f);
	}
	
//	@Override
//	public void keyPressed(CCKeyEvent theKeyEvent) {
//		switch(theKeyEvent.keyCode()){
//		case VK_L:
//			String myFile = CCIOUtil.selectInput("choose_bin_file", CCIOUtil.appPath(""));
//			if(myFile == null)return;
//			_myViewer.load(myFile);
//			break;
//		}
//	}

	public static void main(String[] args) {
		CCGL2Application myAppManager = new CCGL2Application(new CCSequenceViewerApp());
		myAppManager.glcontext().size(1800, 900);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
	
}
