package cc.creativecomputing.demo.simulation.fluid;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.simulation.fluid.CCFluidDisplay;
import cc.creativecomputing.simulation.fluid.CCFluidGrid;
import cc.creativecomputing.simulation.fluid.CCFluidSolver;
import cc.creativecomputing.simulation.fluid.CCFluidTime;

public class CCFluidDemo extends CCGL2Adapter {
	
	
	
	private CCFluidGrid _myGrid = new CCFluidGrid();
	
	@CCProperty(name = "time")
	private CCFluidTime _myTime = new CCFluidTime();
	
	
	@CCProperty(name = "solver")
	private CCFluidSolver _mySolver;
	
	@CCProperty(name = "update")
	private boolean _cUpdate = true;
	
	@CCProperty(name = "speed x", min = -1, max = 1)
	private double _cSpeedX = 0;
	@CCProperty(name = "speed y", min = -1, max = 1)
	private double _cSpeedY = 0;
	@CCProperty(name = "speed z", min = -1, max = 1)
	private double _cSpeedZ = 0;
	

	@CCProperty(name = "mouse impulse", min = 0, max = 1)
	private double _cMouseImpulse = 0;
	
	private CCMouseSimpleInfo _myMouse = new CCMouseSimpleInfo();
	

	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _myScreenCapture;
	
	private CCText _myText;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myGrid.size.set(g.width()/2, g.height()/2);
		_mySolver = new CCFluidSolver(_myGrid, _myTime, new CCVector2(g.width(), g.height()));
		
		
		mouseListener().add(_myMouse);
		mouseMotionListener().add(_myMouse);
		
		_myText = new CCText(CCFontIO.createTextureMapFont("Helvetica-Bold", 40));
		_myText.text(
				"We can modify CHO cell lines\n to provide viral resistance to \nMVM. Learn more at booth\n 608 at #BPSMT");
		_myText.position(20, 200);
		
		_myScreenCapture = new CCScreenCaptureController(this);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_mySolver.noiseOffset().x = theAnimator.time() * _cSpeedX;
		_mySolver.noiseOffset().y = theAnimator.time() * _cSpeedY;
		_mySolver.noiseOffset().z = theAnimator.time() * _cSpeedZ;
	}
	
	public void addForces(CCGraphics g) {
		_mySolver.addColor(g, _myMouse.position, CCColor.createFromHSB(animator().time(), 1d, 1d));
		_mySolver.addForce(g, _myMouse.position, new CCVector2( _myMouse.motion.x, - _myMouse.motion.y).multiplyLocal(_cMouseImpulse));
	}
	
	

	@Override
	public void display(CCGraphics g) {
		
		if(_cUpdate){
			addForces(g);
//			_mySolver.density.beginDraw();
//			g.color(CCColor.createFromHSB(animator().time() * 0.1, 1d, 1d));
//			g.rect(animator().time() * 100 % _myGrid.size.x,100, 10, 10);
//			_mySolver.density.endDraw();
//			_mySolver.velocity.beginDraw();
//			g.color(255,0,0);
//			g.rect(animator().time() * 100 % _myGrid.size.x,100, 10, 10);
//			_mySolver.velocity.endDraw();
			_mySolver.step(g);
		}else{
			_mySolver.density.beginDraw();
//			g.color(255,0,0);
//			g.rect(100,100, 100, 100);
//			g.color(0,255,0);
//			g.rect(200,100, 100, 100);
//			g.color(0,0,255);
//			g.rect(300,100, 100, 100);
			g.clear();
			g.color(1d);
			_myText.draw(g);
			_mySolver.density.endDraw();
		}

		 g.clear();
		 
		 _mySolver.display(g);
		 
//		 g.image(_mySolver.density.attachment(0), -g.width()/2, -g.height()/2, g.width(), g.height());
	}

	public static void main(String[] args) {

		CCFluidDemo demo = new CCFluidDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
