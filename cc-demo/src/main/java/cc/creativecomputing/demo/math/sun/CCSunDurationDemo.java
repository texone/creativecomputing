package cc.creativecomputing.demo.math.sun;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.util.CCSunCalc;
import cc.creativecomputing.math.util.CCSunCalc.CCSunInfo;

public class CCSunDurationDemo extends CCGL2Adapter{
	
	
	
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

	@CCProperty(name = "mid night")
	private CCColor _myMidNightColor = new CCColor();
	@CCProperty(name = "night")
	private CCColor _myNightColor = new CCColor();
	@CCProperty(name = "day")
	private CCColor _myDayColor = new CCColor();
	@CCProperty(name = "mid day")
	private CCColor _myMidDayColor = new CCColor();
	
	
	private void draw(CCGraphics g) {
		
		g.clear();
		
		double myres = 1;
		
		for(double x = 0; x < 365;x+=1){
			_myDate.fromDoubleDay(x);
			CCSunInfo mySunInfo = CCSunCalc.sunInfo(_myDate, _myLatitude, _myLongitude, false);
			double mySunRiseStart = mySunInfo.dawn.dayProgress();
			double mySunRiseEnd = mySunInfo.sunrise.end.dayProgress();
			double mySunSetStart = mySunInfo.sunset.start.dayProgress();
			double mySunSetEnd = mySunInfo.dusk.dayProgress();
			double myTransit =  mySunInfo.transit.dayProgress();
			CCGradient myGradient = new CCGradient();
			myGradient.add(0, _myMidNightColor);
			myGradient.add(mySunRiseStart, _myNightColor);
			myGradient.add(mySunRiseEnd, _myDayColor);
			myGradient.add(myTransit, _myMidDayColor);
			myGradient.add(mySunSetStart, _myDayColor);
			myGradient.add(mySunSetEnd, _myNightColor);
			myGradient.add(1, _myMidNightColor);
			for(int y = -g.height()/2; y < g.height()/2;y+=myres){
				double myBlend = CCMath.norm(y, -g.height()/2, g.height()/2);
				g.color(myGradient.color(myBlend));
				g.rect(x,y,myres,myres);
			}
		}
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(1d,0.25d);
		draw(g);
	}
	
	public static void main(String[] args) {
		
		
		CCSunDurationDemo demo = new CCSunDurationDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
