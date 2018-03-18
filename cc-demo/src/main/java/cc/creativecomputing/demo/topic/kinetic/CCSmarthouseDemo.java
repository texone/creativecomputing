package cc.creativecomputing.demo.topic.kinetic;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.signal.CCSinSignal;

public class CCSmarthouseDemo extends CCGL2Adapter {

	@CCProperty(name = "screen capure controller")
	private CCScreenCaptureController _myScreenCapture;
	
	@CCProperty(name = "speed", min = 0, max = 1)
	private double _cSpeed = 1;
	
	@CCProperty(name = "offset angle", min = -180, max = 180)
	private double _cOffset = 0;
	@CCProperty(name = "max radius", min = 0, max = 400)
	private double _cMaxRadius = 0;
	@CCProperty(name = "eye radius", min = 0, max = 100)
	private double _cEyeRadius = 0;
	
	private static enum SHModes{
		QUICK_GAZE,
		TATI,
		TATI_FULL,
		HAUNTED_HOUSE,
		FOLLOW
	}
	
	@CCProperty(name = "mode")
	private static SHModes _cMode = SHModes.TATI;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		g.ellipseMode(CCShapeMode.RADIUS);

		_myScreenCapture = new CCScreenCaptureController(this, theAnimator);
	}
	
	private CCVector2 _myPosition = new CCVector2();
	
	
	
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
	
	private double _myProgress = 3;
	private double _myTime = 2;
	
	private double _myXStart = 0;
	private double _myYStart = 0;
	
	private double _myXEnd = 0;
	private double _myYEnd = 0;
	
	private void quickGaze(CCAnimator theAnimator) {
		if(_myProgress > _myTime){
			_myXStart = _myXEnd;
			_myYStart = _myYEnd;
			
			double myAngle = CCMath.random(-CCMath.radians(175 / 2), CCMath.radians(175 / 2)) + CCMath.radians(_cOffset);
			double myRadius = CCMath.random(0.66,1) * _cMaxRadius;
			
			_myXEnd = x(myAngle, myRadius);
			_myYEnd = y(myAngle, myRadius);
			
			_myProgress = 0;
		}
		double myBlend = _myProgress / _myTime;
		double myEasedBlend = easeInOut(myBlend);
		
		double myBlendX = blend(_myXStart, _myXEnd, myEasedBlend);
		double myBlendY = blend(_myYStart, _myYEnd, myEasedBlend);
		
		double myBlendAngle = angle(myBlendX, myBlendY);
		double myRadius = radius(myBlendX, myBlendY);
		
		_myPosition.set(x(myBlendAngle, myRadius), y(myBlendAngle, myRadius));
		_myProgress += theAnimator.deltaTime();
	}
	
	@CCProperty(name = "tati noise")
	private CCSimplexNoise _cNoise = new CCSimplexNoise();
	
	public void tati(CCAnimator theAnimator) {
		_myPosition.set(CCVector2.circlePoint((_cNoise.value(theAnimator.time()) * 2 - 1) * CCMath.radians(150 / 2) - CCMath.HALF_PI, _cMaxRadius, 0, 0));
	}
	
	@CCProperty(name = "tati sine")
	private CCSinSignal _cSine = new CCSinSignal();
	
	public void tatiFull(CCAnimator theAnimator) {
		_myPosition.set(CCVector2.circlePoint((_cSine.value(theAnimator.time()) * 2 - 1) * CCMath.radians(150 / 2) - CCMath.HALF_PI, _cMaxRadius, 0, 0));
	}
	
	@CCProperty(name = "haunted y", min = 0, max = 1)
	private double _cY = 0;
	
	@CCProperty(name = "haunted sine")
	private CCSinSignal _cHSine = new CCSinSignal();
	@CCProperty(name = "haunted noise")
	private CCSimplexNoise _cHNoise = new CCSimplexNoise();
	@CCProperty(name = "haunted full")
	private boolean _cHFull = true;
	
	private void hauntedHouse(CCAnimator theAnimator) {
		double myAngle = CCMath.asin(_cY);
		double myX = CCMath.cos(myAngle) * _cMaxRadius;

		double myBlendX = myX * (_cHSine.value(theAnimator.time()) * 2 - 1);
		if(!_cHFull) {
			myBlendX = myX * (_cHNoise.value(theAnimator.time()) * 2 - 1);
		}
		double myBlendY = -_cY * _cMaxRadius;
		
		double myBlendAngle = angle(myBlendX, myBlendY);
		double myRadius = radius(myBlendX, myBlendY);
		
		_myPosition.set(x(myBlendAngle, myRadius), y(myBlendAngle, myRadius));
		_myProgress += theAnimator.deltaTime();
	}
	
	@CCProperty(name = "follow time", min = 0, max = 5)
	private double _cFollowTime = 2; 
	@CCProperty(name = "follow break", min = 0, max = 5)
	private double _cFollowBreak = 2; 
	
	private int dir = 1;
	
	private double _myFolloTime = 2;
	private double _myBreakTime = 2;
	
	private void follow(CCAnimator theAnimator) {
		if(_myProgress > _myFolloTime + _myBreakTime){
			_myXStart = _myXEnd;
			_myYStart = _myYEnd;
			
			double myAngle = CCMath.random(CCMath.radians(175 / 2)) * dir + CCMath.radians(_cOffset);
			double myRadius = _cMaxRadius;
			
			_myXEnd = x(myAngle, myRadius);
			_myYEnd = y(myAngle, myRadius);
			
			_myProgress = 0;
			dir *= -1;
			
			_myBreakTime = CCMath.random(_cFollowBreak / 2, _cFollowBreak);
			_myFolloTime = CCMath.random(_cFollowTime / 2, _cFollowTime);
		}
		double myBlend = CCMath.saturate(_myProgress / _myFolloTime);
		double myEasedBlend = easeInOut(myBlend);
		
		double myBlendX = blend(_myXStart, _myXEnd, myEasedBlend);
		double myBlendY = blend(_myYStart, _myYEnd, myEasedBlend);
		
		double myBlendAngle = angle(myBlendX, myBlendY);
		double myRadius = radius(myBlendX, myBlendY);
		
		_myPosition.set(x(myBlendAngle, myRadius), y(myBlendAngle, myRadius));
		_myProgress += theAnimator.deltaTime();
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
//		double myAngle = CCMath.cos(theAnimator.time() * _cSpeed * CCMath.TWO_PI) ;
//		double mySteps = myAngle * 63400;
//		double mySpeed = (mySteps - _myLastSteps);
//		_myLastSteps = mySteps;
//		double myStepTime = theAnimator.deltaTime() / mySpeed;
		//CCLog.info(myStepTime * 1000 * 100);
		
		switch(_cMode) {
		case TATI:
			tati(theAnimator);
			break;
		case TATI_FULL:
			tatiFull(theAnimator);
			break;
		case QUICK_GAZE:
			quickGaze(theAnimator);
			break;
		case HAUNTED_HOUSE:
			hauntedHouse(theAnimator);
			break;
		case FOLLOW:
			follow(theAnimator);
			break;
		}
	

		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();

		g.color(CCColor.WHITE);
		g.ellipse(0,0, _cMaxRadius + _cEyeRadius);
		g.color(CCColor.BLACK);
		g.ellipse(_myPosition, _cEyeRadius);
		
		g.color(CCColor.RED);
//		g.line(_myXStart, _myYStart, _myXEnd, _myYEnd);
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
