package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class CCRealtimeCodingDemo extends CCGL2Adapter{
	
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
	
	@CCProperty(name = "real time visual")
	private CCRealtimeCompile<CCRealtimeGraph> _myRealTimeGraph;
	
	private class Container{
		@CCProperty(name = "real time object", readBack = true)
		private CCRealtimeGraph _myObject;
	}
	
	@CCProperty(name = "yo", readBack = true)
	private Container yo = new Container();
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g,CCAnimator theAnimator) {
		_myRealTimeGraph = new CCRealtimeCompile<CCRealtimeGraph>(CCRealtimeGraph.class);
		yo._myObject = _myRealTimeGraph.createObject();
		CCLog.info(yo._myObject);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		yo._myObject = _myRealTimeGraph.instance();

		CCLog.info(yo._myObject);
//			CCLog.info(myGraph);
			if(_myRealTimeGraph.instance() == null)return;
			_myRealTimeGraph.instance().draw(g);
		
		
	}
	
	public static void main(String[] args) {
		
		
		CCRealtimeCodingDemo demo = new CCRealtimeCodingDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.start();
	}
}
