package cc.creativecomputing.demo.control;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioAsset;

public class CCAudioAssetDemo extends CCGL2Adapter{
	
	@CCProperty(name = "asset")
	private CCAudioAsset _myAsset = new CCAudioAsset();
	@CCProperty(name = "asset1")
	private CCAudioAsset _myAsset1 = new CCAudioAsset();
	@CCProperty(name = "asset2")
	private CCAudioAsset _myAsset2 = new CCAudioAsset();
	@CCProperty(name = "asset3")
	private CCAudioAsset _myAsset3 = new CCAudioAsset();
	
	@Override
	public void init(CCGraphics g) {
		
		
//		for(CCRealtimeGraph myGraph:_myRealTimeGraph.instances()){
//			CCLog.info(myGraph);
//			if(myGraph == null)return;
//			myGraph.draw(g);
//		}
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		if(_myAsset.value() == null)return;
		g.color(1d);
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int i = 0; i < 200;i++){
			double mySpec = CCMath.map(i, 0, 199, 0, 1);
			g.vertex(i,_myAsset.spectrum(mySpec) * 100);
		}
		g.endShape();
		
		for(int i = 0; i < 16;i++){
			double mySpec = CCMath.map(i, 0, 15, 0, 1);
			double mySpecAmp = _myAsset.spectrum(mySpec);
			g.color(mySpecAmp);
			g.rect(i * 100 - g.width()/2, -200, 100, 50);
		}
	}
	
	public static void main(String[] args) {
		
		
		CCAudioAssetDemo demo = new CCAudioAssetDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
