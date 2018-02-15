package cc.creativecomputing.demo.topic.kinetic;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCSmarthouseDemo extends CCGL2Adapter {
	
	@CCProperty(name = "speed", min = 0, max = 1)
	private double _cSpeed = 1;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}
	
	private CCVector2 _myPosition = new CCVector2();
	private CCVector2 _myPosition2 = new CCVector2();
	private double _myLastSteps = 0;
	
	private double _myProgress = 3;
	private double _myTime = 2;
	private double _myPause = 1;
	
	private double myTarget = 0;
	
	private double _myXStart = 0;
	private double _myYStart = 0;
	
	private double _myXEnd = 0;
	private double _myYEnd = 0;
	
	private double x(double theAngle, double theRadius){
		return CCMath.cos( theAngle ) * theRadius;
	}
	
	private double y(double theAngle, double theRadius){
		return CCMath.sin( theAngle ) * theRadius;
	}
	
	private double angle(double theX, double theY){
		return CCMath.atan2(theY, theX);
	}
	
	private double radius(double theX, double theY){
		return CCMath.sqrt(theX * theX + theY * theY);
	}
	
	public double easeInOut(final double theBlend) {
		return (CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2;
	}
	
	public double blend(double theStart, double theEnd, double theBlend){
		return theStart * (1 - theBlend) + theEnd * theBlend;
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(_myProgress > _myTime){
			_myXStart = _myXEnd;
			_myYStart = _myYEnd;
			
			double myAngle = CCMath.random(-CCMath.radians(175 / 2), CCMath.radians(175 / 2));
			double myRadius = CCMath.random(0.66,1);
			
			_myXEnd = x(myAngle, myRadius);
			_myYEnd = y(myAngle, myRadius);
			
			_myProgress = 0;
		}
	
//		double myAngle = CCMath.cos(theAnimator.time() * _cSpeed * CCMath.TWO_PI) ;
//		double mySteps = myAngle * 63400;
//		double mySpeed = (mySteps - _myLastSteps);
//		_myLastSteps = mySteps;
//		double myStepTime = theAnimator.deltaTime() / mySpeed;
		//CCLog.info(myStepTime * 1000 * 100);
		double myBlend = _myProgress / _myTime;
		double myEasedBlend = easeInOut(myBlend);
		
		double myBlendX = blend(_myXStart, _myXEnd, myEasedBlend);
		double myBlendY = blend(_myYStart, _myYEnd, myEasedBlend);
		
		double myBlendAngle = angle(myBlendX, myBlendY);
		double myRadius = radius(myBlendX, myBlendY);
		
		CCLog.info(CCMath.degrees(myTarget), CCMath.degrees(myBlendAngle), myRadius);
		_myPosition.set(x(myBlendAngle, myRadius * 300), y(myBlendAngle, myRadius * 300));
		_myProgress += theAnimator.deltaTime();
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.color(255);
		g.ellipse(_myPosition, 30);
		g.color(255,0,0);
		g.ellipse(_myPosition2, 20);
	}

	public static void main(String[] args) {

		CCSmarthouseDemo demo = new CCSmarthouseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 800);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
