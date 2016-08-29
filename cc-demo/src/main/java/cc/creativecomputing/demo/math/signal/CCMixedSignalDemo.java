package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCMixedSignalDemo extends CCGL2Adapter{
	
	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	@CCProperty(name = "height", min = 0, max = 500)
	private double _cHeight = 100;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	private int _myIndex = 0;
	
	@Override
	public void init(CCGraphics g) {
		
//		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
//			CCLog.info(myGraph);
//			if(myGraph == null)return;
//			myGraph.draw(g);
//		}
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
		
		_myData = new double[g.width()];
		
	}
	
	double[] _myData;
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		double mySignalValue = _mySignal.value(theAnimator.frames());
		int myIndex = _myIndex % _myData.length;
		_myData[myIndex] = mySignalValue;
		
		
		
		_myIndex++;
	}
	
	
	
	private void drawGraph(CCGraphics g, double[] theData, double theOffset, double theScale){
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int x = 0; x < g.width(); x++){
			double y = (_mySignal.value(x - g.width()/2) - theOffset) * theScale;
			g.vertex(x - g.width()/2, y);
		}
		g.endShape();
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		g.color(255);
		drawGraph(g, _myData, 0.5,_cHeight);

	}
	
	public static void main(String[] args) {
		
		
		CCMixedSignalDemo demo = new CCMixedSignalDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
