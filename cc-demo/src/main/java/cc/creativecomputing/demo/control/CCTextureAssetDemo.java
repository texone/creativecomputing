package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;

public class CCTextureAssetDemo extends CCGL2Adapter{
	
	@CCProperty(name = "texture asset")
	private CCTexture2DAsset _myAsset;
	
	@CCProperty(name = "value")
	private double _myValue;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myAsset = new CCTexture2DAsset(glContext());
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		if(_myAsset.value() == null)return;
		g.image(_myAsset.value(), -_myAsset.value().width() / 2, -_myAsset.value().height() / 2);
	}
	
	public static void main(String[] args) {
		CCTextureAssetDemo demo = new CCTextureAssetDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
