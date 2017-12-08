package cc.creativecomputing.demo.protocol.serial.hpgl;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL;
import cc.creativecomputing.protocol.serial.hpgl.CCHPGL.CCHPPaperFormat;

public class CCHPGLDemo extends CCGL2Adapter {
	
	@CCProperty(name = "HP")
	private CCHPGL _cHPGL;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cHPGL = new CCHPGL(CCHPPaperFormat.A4);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		//,200,,7400
		int myLastX = 0;
		int myLastY = 0;
		if(_cHPGL.isConnected()){
//			_cHPGL.lineTo((int)CCMath.random(430,10430), (int)CCMath.random(200,7400));

			_cHPGL.line(CCMath.random(430,10430), CCMath.random(200,7400), CCMath.random(430,10430), CCMath.random(200,7400));
			
//			int radius = (int)CCMath.random(1000);
//			int x = (int)CCMath.random(430 + radius,10430 - radius);
//			int y = (int)CCMath.random(200 + radius,7400 - radius);
//			_cHPGL.circle(x, y, radius);
		}
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCHPGLDemo demo = new CCHPGLDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

