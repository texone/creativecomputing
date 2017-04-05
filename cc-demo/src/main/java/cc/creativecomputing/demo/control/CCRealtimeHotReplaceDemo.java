package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCRealtimeHotReplaceDemo extends CCGL2Adapter{
	
	public static interface CCRealtimeGraph extends CCCompileObject{
		public void draw(CCGraphics g);

//		@Override
//		public void onRecompile() {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public Object[] parameters() {
//			// TODO Auto-generated method stub
//			return null;
//		}
	}
	
	
	private CCRealtimeCompile<CCRealtimeHotReplaceClass> _myRealTimeCompile;
	
	private CCRealtimeHotReplaceClass _myRealtimeObject;
	
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g,CCAnimator theAnimator) {
		_myRealTimeCompile = new CCRealtimeCompile<>(CCRealtimeHotReplaceClass.class.getName(),CCRealtimeHotReplaceClass.class);
		_myRealTimeCompile.recompile();
		_myRealtimeObject = _myRealTimeCompile.createObject();
		_myRealtimeObject = _myRealTimeCompile.instance();
		CCLog.info(_myRealtimeObject);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		if(_myRealTimeCompile.hasCodeUpdate()){
			CCLog.info("has update");
			_myRealTimeCompile.recompile();
			_myRealtimeObject = null;
			
			System.gc();
			
			_myRealtimeObject = _myRealTimeCompile.createObject();
			_myRealtimeObject.doSomthing();
			CCLog.info(_myRealtimeObject);
		}
		
//			CCLog.info(myGraph);
			if(_myRealtimeObject == null)return;
		
	}
	
	public static void main(String[] args) {
		
		
		CCRealtimeHotReplaceDemo demo = new CCRealtimeHotReplaceDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.start();
	}
}
