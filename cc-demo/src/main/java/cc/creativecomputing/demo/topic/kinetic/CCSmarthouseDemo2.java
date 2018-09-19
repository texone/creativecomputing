package cc.creativecomputing.demo.topic.kinetic;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.demo.protocol.serial.CCSerialDemo;
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
import cc.creativecomputing.protocol.serial.CCSerialInput;
import cc.creativecomputing.protocol.serial.CCSerialModule;

public class CCSmarthouseDemo2 extends CCGL2Adapter {
	static double MAX_ANGLE = 175 / 2;

	/**
	 * animation class
	 */
	abstract class  Animation{
		float minDuration = 10;
	    float maxDuration = 10;
	    /**
	     * propability to pic this animation
	     */
		 double propability = 1;
	    double myTime = 0;
	    double duration = 0;
	    
	    void init(){
	      myTime = 0;
	    }

	    void update(double theDeltaTime){
	      myTime += theDeltaTime;
	    }
	    
	    abstract double value();

	    boolean isFinished(){
	    		return myTime > duration;
	    }
	};

	/**
	 * Animation to hold at base position 6h
	 */
	class BaseStillAnimation extends Animation{
	    double value (){
	      return 0;
	    }
	};

	/**
	 * Animation hold at random position
	 */
	 class RandomStillAnimation extends Animation{
	  
	    /**
	     * duration of the animation
	     */
		 double minDuration = 10;
		 double maxDuration = 10;
	    double position = 0;

	    void init(){
	      super.init();
	      position = CCMath.random(-1,1) * MAX_ANGLE;
	    }
	    
	    double value(){
	      return position;
	    }

		@Override
		boolean isFinished() {
			return false;
		}
	};

	/**
	 * jitter at a random position
	 */
	class JitterAnimation extends Animation{
	  
	    double position = 0;

	    @CCProperty(name = "jitter amplitude", min = 0, max = 1)
	    double _cJitterAmplitude = 0.01;
	    @CCProperty(name = "jitter frequency", min = 0, max = 1)
	    double _cJitterFrequency = 1;
	    
	    double myRandom = CCMath.random(-1,1);

	    void init(){
	      super.init();
	    }
	    
	    @Override
	    void update(double theDeltaTime) {
	    		super.update(theDeltaTime);
	    		position = myRandom - _cJitterAmplitude * CCMath.sign(myRandom);
	    }
	    
	    double value(){
	      return (position + CCMath.sin(myTime * CCMath.TWO_PI * _cJitterFrequency) * _cJitterAmplitude) * MAX_ANGLE;
	    }

		@Override
		boolean isFinished() {
			// TODO Auto-generated method stub
			return false;
		}
	};

	/**
	 * Langsames zittriges Bewegen auf beliebige Position; 
	 * kurzes Innehalten; 
	 * langsames zittriges Weiterbewegen auf nächste beliebige Position; 
	 * kurzes Innehalten, usw. (n-Schritte), dann b.);
	 * 
	 * Parameter:  
	          Geschwindigkeit
	          Pausenzeit
	          anzahl
	          zeit stillstand 
	          */
	class JitterMoveAnimation extends Animation{
	 
	    double position = 0;

	    @CCProperty(name = "jitter amplitude", min = 0, max = 1)
	    double _cJitterAmplitude = 0.01;
	    @CCProperty(name = "jitter frequency", min = 0, max = 1)
	    double _cJitterFrequency = 1;
	    @CCProperty(name = "move time", min = 0, max = 20)
	    double _cMoveTime = 1;
	    @CCProperty(name = "break time", min = 0, max = 20)
	    double _cBreakTime = 1;
	    
	    double myRandom0 = CCMath.random(-1,1);
	    double myRandom1 = CCMath.random(-1,1);

	    void init(){
	      super.init();
	    }
	    
	    @Override
	    void update(double theDeltaTime) {
	    		super.update(theDeltaTime);
	    		double myRange = (1. - _cJitterAmplitude);
	    		
	    		if(myTime < _cMoveTime) {
	    			double myRandom = CCMath.blend(myRandom0, myRandom1, myTime / _cMoveTime);
		    		position = myRandom - _cJitterAmplitude * CCMath.sign(myRandom);
	    		}
	    		if(myTime > _cMoveTime + _cBreakTime) {
	    			myTime = 0;
	    			myRandom0 = myRandom1;
	    		    myRandom1 = CCMath.random(-1,1);
	    		}
	    }
	    
	    double value(){
	      return (position + CCMath.sin(myTime * CCMath.TWO_PI * _cJitterFrequency) * _cJitterAmplitude) * MAX_ANGLE;
	    }

		@Override
		boolean isFinished() {
			// TODO Auto-generated method stub
			return false;
		}
	};
	/*
	  Schnelles Bewegen auf beliebige Position; kurzes Innehalten; schnelles Weiterbewegen auf nächste beliebige Position; kurzes Innehalten, usw. (n-Schritte), dann b.);


	    Parameter:  Wahrscheinlichkeit
	          Zittern Frequenz
	          Zittern amplitude
	          Geschwindigkeit
	          Pausenzeit
	          anzahl
	          zeit stillstand 
	 */
	 class RandomMoveAnimation extends  Animation{
		 double position = 0;

		    @CCProperty(name = "move time", min = 0, max = 20)
		    double _cMoveTime = 1;
		    @CCProperty(name = "break time", min = 0, max = 20)
		    double _cBreakTime = 1;
		    
		    double myRandom0 = CCMath.random(-1,1);
		    double myRandom1 = CCMath.random(-1,1);

		    void init(){
		      super.init();
		    }
		    
		    @Override
		    void update(double theDeltaTime) {
		    		super.update(theDeltaTime);
		    		
		    		if(myTime < _cMoveTime) {
			    		position = CCMath.blend(myRandom0, myRandom1, CCMath.smoothStep(0, 1, myTime / _cMoveTime));
		    		}
		    		if(myTime > _cMoveTime + _cBreakTime) {
		    			myTime = 0;
		    			myRandom0 = myRandom1;
		    		    myRandom1 = CCMath.random(-1,1);
		    		}
		    }
		    
		    double value(){
		      return position * MAX_ANGLE;
		    }

			@Override
			boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}
	}
//	/*
//	  Langsames 'Augenrollen' (n-fach vor- und zurück) über die gesamte Reichweite (170°), dann b.);
//
//	    Parameter:  Wahrscheinlichkeit
//	          Geschwindigkeit
//	          anzahl
//	          zeit stillstand 
//	          */
	 class FullRollAnimation extends  Animation{
		 double position = 0;

		    @CCProperty(name = "move time", min = 0, max = 20)
		    double _cMoveTime = 1;

		    void init(){
		      super.init();
		    }
		    
		    @Override
		    void update(double theDeltaTime) {
		    		super.update(theDeltaTime);	
		    		position = CCMath.cos(myTime / _cMoveTime * CCMath.TWO_PI);
		    }
		    
		    double value(){
		      return position * MAX_ANGLE;
		    }

			@Override
			boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}
	}
	/*
	  Langsames 'Augenrollen' (n-fach vor- und zurück) über einen beliebigen Teilabschnitt

	    Parameter:  Wahrscheinlichkeit
	          Geschwindigkeit
	          anzahl
	          maximaler abschnitt
	          zeit stillstand 
	          */
	 class RandomRollAnimation extends  Animation{
		 double position = 0;

		    @CCProperty(name = "move time", min = 0, max = 20)
		    double _cMoveTime = 1;

		    void init(){
		      super.init();
		    }
		    
		    @Override
		    void update(double theDeltaTime) {
		    		super.update(theDeltaTime);	
		    		position = CCMath.cos(myTime / _cMoveTime * CCMath.TWO_PI);
		    }
		    
		    double value(){
		      return position * MAX_ANGLE;
		    }

			@Override
			boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}
	}

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

	@CCProperty()
	private CCSerialModule _cSerial;

	@CCProperty(name = "jitter still")
	private JitterAnimation _myJitterAnimation = new JitterAnimation();
	@CCProperty(name = "jitter move")
	private JitterMoveAnimation _myJitterMoveAnimation = new JitterMoveAnimation();
	@CCProperty(name = "random move")
	private RandomMoveAnimation _myRandomMove = new RandomMoveAnimation();
	@CCProperty(name = "full roll animation")
	private FullRollAnimation _myFullRoll = new FullRollAnimation();
	
	
	private Animation _myAnimation = _myFullRoll;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		g.ellipseMode(CCShapeMode.RADIUS);

		_myScreenCapture = new CCScreenCaptureController(this, theAnimator);
		
		_cSerial = new CCSerialModule("serial");
		_cSerial.listener().add(input -> {
			String myInput = input.readString();
			handleInput(myInput);

		});
	}
	
	StringBuffer _myReadBuffer = new StringBuffer();
	
	private double  _mySerialAngle;
	private double _mySerialRadius;
	
	double MAX_STEPS_A = 60000; 
	
	private void handleInput(String theInput) {
		_myReadBuffer.indexOf("\n");
		
		if(_myReadBuffer.toString().contains("\n")){
			try {
				CCLog.info(_myReadBuffer);
//				_myReadBuffer.deleteCharAt(_myReadBuffer.length() - 1);
				String[] myData = _myReadBuffer.toString().split(",");
				_myReadBuffer = new StringBuffer();
				_myReadBuffer.append(theInput);
				
				double myProgress = Double.parseDouble(myData[0]);
				int myStepsA = Integer.parseInt(myData[5]);
				_mySerialAngle = myStepsA / MAX_STEPS_A * CCMath.radians(175.0 / 2.) ;
				double myX = x(_mySerialAngle, _mySerialRadius);
				double myY = y(_mySerialAngle, _mySerialRadius);
				CCLog.info(
					myProgress,
					CCFormatUtil.nd(myX, 2),
					CCFormatUtil.nd(myY, 2),
					CCFormatUtil.nd(_mySerialAngle, 2) +","+
					CCFormatUtil.nd(_mySerialRadius, 2) +","+
					myStepsA );
//				CCLog.info(_mySerialAngle, _mySerialRadius);
//				CCLog.info(myStepA, myStepZ);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}else{
			_myReadBuffer.append(theInput);
		}
	}
	
	private CCVector2 _myPosition = new CCVector2();
	
	
	
	private double x(double theAngle, double theRadius){
		return CCMath.cos( theAngle ) * theRadius;
	}
	
	private double y(double theAngle, double theRadius){
		return CCMath.sin( theAngle ) * theRadius;
	}
	
	public double easeInOut(final double theBlend) {
		return (CCMath.cos(CCMath.PI+CCMath.PI * theBlend) + 1) / 2;
	}
	
	public double blend(double theStart, double theEnd, double theBlend){
		return theStart * (1 - theBlend) + theEnd * theBlend;
	}
	
	
	
	private void serial(CCAnimator theAnimator) {
		double myAngle = _mySerialAngle;
		_myPosition.set(x(myAngle, _mySerialRadius * _cMaxRadius), y(myAngle, _mySerialRadius * _cMaxRadius));
	}
	
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myAnimation.update(theAnimator.deltaTime());
		double myAngle = _myAnimation.value() * CCMath.DEG_TO_RAD;
		_myPosition.set(x(myAngle, _cMaxRadius), y(myAngle, _cMaxRadius));
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.pushMatrix();
		g.rotate(-90);
		g.color(CCColor.WHITE);
		g.ellipse(0,0, _cMaxRadius + _cEyeRadius);
		g.color(CCColor.BLACK);
		g.ellipse(_myPosition, _cEyeRadius);
		
		g.color(CCColor.RED);
		g.popMatrix();
//		g.line(_myXStart, _myYStart, _myXEnd, _myYEnd);
	}

	public static void main(String[] args) {

		CCSmarthouseDemo2 demo = new CCSmarthouseDemo2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 800);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
