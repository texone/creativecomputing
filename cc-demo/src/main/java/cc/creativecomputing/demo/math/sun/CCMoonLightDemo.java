package cc.creativecomputing.demo.math.sun;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.util.CCMoonCalc;
import cc.creativecomputing.math.util.CCMoonCalc.CCMoonInfo;

public class CCMoonLightDemo extends CCGL2Adapter{
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	private CCDate _myDate;
	@CCProperty(name = "latitude")
	private double _myLatitude = 47.4988;
	@CCProperty(name = "longitude")
	private double _myLongitude = 8.7237;
	
	
			
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myDate = new CCDate();
	}
	
	
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	private void draw(CCGraphics g) {
		
		g.clear();
		
		double myres = 1;
		
		for(int x = -g.width()/2; x < g.width()/2;x+=myres){
			_myDate = new CCDate();
			_myDate.fromDoubleDay(CCMath.map(x, -g.width()/2, g.width()/2, 0, 365));
			CCMoonInfo myInfo = CCMoonCalc.moonInfo(_myDate, _myLatitude, _myLongitude);
			double myMoonRise = myInfo.rise.dayProgress();
			double myMoonSet = myInfo.set.dayProgress();
			for(int y = -g.height()/2; y < g.height()/2;y+=myres){
				double myBlend = (CCMath.norm(y, -g.height()/2, g.height()/2));
				double myLight = 0;
				if(myBlend > myMoonRise && myBlend < myMoonSet)myLight = 1;
				if(myMoonRise > myMoonSet){
					if(myBlend > myMoonRise)myLight = 1;
					if(myBlend < myMoonSet)myLight = 1;
				}
				g.color(myLight * myInfo.fraction);
				g.rect(x,y,myres,myres);
			}
		}
		CCLog.info(_myDate);
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(1d,0.25d);
		draw(g);
	}
	
	public static void main(String[] args) {
		CCMoonLightDemo demo = new CCMoonLightDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
