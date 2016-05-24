package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTextureAttributes;
import cc.creativecomputing.graphics.texture.CCVideoTexture;
import cc.creativecomputing.video.CCImageSequenceAsset;

public class CCImageSequenceAssetDemo extends CCGL2Adapter{
	
	@CCProperty(name = "texture asset")
	private CCImageSequenceAsset _myAsset;
	private CCVideoTexture _myVideoTexture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

		CCTextureAttributes myAttributes = new CCTextureAttributes();
		myAttributes.generateMipmaps(true);
		_myVideoTexture = new CCVideoTexture(this, CCTextureTarget.TEXTURE_2D, myAttributes);
		
		_myAsset = new CCImageSequenceAsset(theAnimator);
		_myAsset.setListener(_myVideoTexture);
		
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
		g.clearColor(1d,0,0);
		g.clear();
		g.image(_myVideoTexture,0,0);
		g.rect(0,0,100,100);
	}
	
	public static void main(String[] args) {
		
		
		CCImageSequenceAssetDemo demo = new CCImageSequenceAssetDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
