package cc.creativecomputing.demo.math.sun;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.util.CCSunCalc;

public class CCSunLightDemo extends CCGL2Adapter{
	
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
		
		for(double x = 0; x < 365;x+=1){
			_myDate.fromDoubleDay(x);
			for(int y = -g.height()/2; y < g.height()/2;y+=myres){
				_myDate.fromDoubleTime(CCMath.map(y, -g.height()/2, g.height()/2, 0, 24));
				double myLight = CCSunCalc.lightBlend(_myDate, _myLatitude, _myLongitude);
			
				g.color(myLight);
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
		CCSunLightDemo demo = new CCSunLightDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
