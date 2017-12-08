package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.math.CCMath;

public class CCObama extends CCGL2Adapter {
	
	@CCProperty(name = "texture;")
	private CCTexture2DAsset _cTexture;
	@CCProperty(name = "alpha", min = 0, max = 1)
	private double _cAlpha;
	@CCProperty(name = "number of points")
	private int _NumberOfPoints = 0;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cTexture = new CCTexture2DAsset(_myContext);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255);
		g.clear();
		
		g.ortho2D();
		g.color(1d, _cAlpha);
		
		if(_cTexture.value() == null)return;
		
		g.image(_cTexture.value(),0,0);
		
		g.color(0);
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 0; i < _NumberOfPoints;i++){
			int x = (int)CCMath.random(_cTexture.value().width());
			int y = (int)CCMath.random(_cTexture.value().height());
			
			if(CCMath.random() > _cTexture.image().getPixel(x, y).brightness()){
				g.vertex(x,y);
			}
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCObama demo = new CCObama();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
