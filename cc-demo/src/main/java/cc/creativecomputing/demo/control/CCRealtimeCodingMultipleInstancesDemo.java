package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;

public class CCRealtimeCodingMultipleInstancesDemo extends CCGL2Adapter{
	
	public static class CCRealtimeParticleSettings{
		
		@CCProperty(name = "random", min = 0, max = 1)
		public double _cRandom = 0;
		
		@CCProperty(name = "color")
		public CCColor _cColor = new CCColor();
	}
	
	public static interface CCRealtimeParticle extends CCCompileObject{
		
		public void update(CCAnimator theAnimator);
		
		public void draw(CCGraphics g);
	}
	
	@CCProperty(name = "real time visual")
	private CCRealtimeCompile<CCRealtimeParticle> _myRealTimeGraph;
	
	@CCProperty(name = "settings")
	private CCRealtimeParticleSettings _cSettings = new CCRealtimeParticleSettings();
	
	@Override
	public void init(CCGraphics g) {

		_myRealTimeGraph = new CCRealtimeCompile<CCRealtimeParticle>("cc.creativecomputing.control.CCRealtimeParticleImp", CCRealtimeParticle.class);
		for(int i = 0; i < 100;i++){
			_myRealTimeGraph.createObject(_cSettings);
		}
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		for(CCRealtimeParticle myParticle:_myRealTimeGraph.instances()){
			if(myParticle == null)continue;
			myParticle.update(theAnimator);
		}
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		for(CCRealtimeParticle myParticle:_myRealTimeGraph.instances()){
//			CCLog.info(myGraph);
			if(myParticle == null)continue;
			myParticle.draw(g);
		}
		
	}
	
	public static void main(String[] args) {
		CCRealtimeCodingMultipleInstancesDemo demo = new CCRealtimeCodingMultipleInstancesDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
