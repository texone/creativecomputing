package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;

public class CCEnumDemo extends CCGL2Adapter{
	
	
	@CCProperty(name = "draw mode")
	private CCDrawMode _myDrawMode = CCDrawMode.POINTS;
	
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g) {
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(0);
		g.clear();
		
		CCMath.randomSeed(0);
		g.color(255);
		g.beginShape(_myDrawMode);
		for(int i = 0; i < 1000;i++){
			g.vertex(
				CCMath.random(-g.width()/2, g.width()/2),
				CCMath.random(-g.height()/2, g.height()/2)
			);
		}
		g.endShape();
		
	
	}
	
	public static void main(String[] args) {
		
		
		CCEnumDemo demo = new CCEnumDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
