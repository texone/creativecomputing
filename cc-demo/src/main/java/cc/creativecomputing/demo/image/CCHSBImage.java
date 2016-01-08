package cc.creativecomputing.demo.image;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCHSBImage extends CCGL2Adapter{
	
	
	@CCProperty(name = "color")
	private CCColor _myColor = new CCColor();
	
	CCImage myHSBImage = new CCImage(300,300);
	CCTexture2D myTexture;
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	@Override
	public void init(CCGraphics g) {
		CCVector2 myCenter = new CCVector2(myHSBImage.width() / 2, myHSBImage.height() / 2);
		for(int x = 0; x < myHSBImage.width(); x++){
			for(int y = 0; y < myHSBImage.height(); y++){
				double myRadius = myCenter.distance(new CCVector2(x,y));
				double myAngle = CCVector2.angle(myCenter, new CCVector2(x,y));
				CCColor myColor = CCColor.createFromHSB(myAngle / CCMath.TWO_PI, myRadius / myHSBImage.width(), 1);
				myHSBImage.setPixel(x, y, myColor);
			}
		}
		
		myTexture = new CCTexture2D(myHSBImage);
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(_myColor);
		g.clear();
		
		g.image(myTexture,0,0);
		
	
	}
	
	public static void main(String[] args) {
		
		
		CCHSBImage demo = new CCHSBImage();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
