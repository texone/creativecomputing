package cc.creativecomputing.effects.modulation;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCTimedMotionData;

public class CCMotionSource extends CCModulationSource {

	@CCProperty(name = "velocity", min = 0, max = 1)
	private double _cVelocity;
	@CCProperty(name = "acceleration", min = 0, max = 1)
	private double _cAcceleration;
	@CCProperty(name = "jerk", min = 0, max = 1)
	private double _cJerk;
	
	@CCProperty(name = "velocity reduction")
	private double _cVelocityReduction = 1;
	@CCProperty(name = "acceleration reduction")
	private double _cAccelerationReduction = 1;
	@CCProperty(name = "jerk reduction")
	private double _cJerkReduction = 1;
	
	@CCProperty(name = "print")
	private boolean _cPrint = false;
	
	private class CCMotionSourceHistoryPoint extends CCTimedMotionData{
		
		public double maxVelocity = 0;
		public double maxAcceleration = 0;
		public double maxJerk = 0;

		public CCMotionSourceHistoryPoint(CCVector3 thePosition, double theLength, double theVelocitiy, double theAcceleration, double theJerk, double theTimeStep) {
			super(thePosition, theLength, theVelocitiy, theAcceleration, theJerk, theTimeStep);
		}
		
		public void update(CCAnimator theAnimator) {
			maxVelocity -= _cVelocityReduction * theAnimator.deltaTime();
			maxAcceleration -= _cAccelerationReduction * theAnimator.deltaTime();
			maxJerk -= _cJerkReduction * theAnimator.deltaTime();
			
			if(_cPrint)CCLog.info(maxVelocity);
			
			if(Double.isNaN(maxVelocity))maxVelocity = velocity;
			if(Double.isNaN(maxVelocity))maxAcceleration = acceleration;
			if(Double.isNaN(maxVelocity))maxJerk = jerk;
			
			maxVelocity = CCMath.max(maxVelocity, CCMath.abs(velocity), 0);
			maxAcceleration = CCMath.max(maxAcceleration, 0, CCMath.abs(acceleration));
			maxJerk = CCMath.max(maxJerk, 0, CCMath.abs(jerk));
		}
		
		public double eval() {
			return 
			CCMath.abs(velocity) / maxVelocity * _cVelocity + 
			CCMath.abs(acceleration) / maxAcceleration * _cAcceleration + 
			CCMath.abs(jerk) / maxJerk * _cJerk;
		}

		public void reset() {
			maxVelocity = 0;
			maxAcceleration = 0;
			maxJerk = 0;
		}
	}

	private Map<CCEffectable, CCMotionSourceHistoryPoint> _myMap = new HashMap<>();

	public CCMotionSource(String theName) {
		super(theName, null);
		
		
		_myModulationImplementation = (effectManager, effectable) -> {
			if(!_myMap.containsKey(effectable)){
				return 0;
			}
			
			return _myMap.get(effectable).eval();
		};
	}
	
	@Override
	public void update(CCAnimator theAnimator, CCEffectManager<?> theManager) {
		for(CCEffectable myEffectable:theManager.effectables()){
			if(!_myMap.containsKey(myEffectable)){
				_myMap.put(myEffectable, new CCMotionSourceHistoryPoint(myEffectable.position(), 0, 0, 0, 0, theAnimator.deltaTime()));
				continue;
			}
			CCMotionSourceHistoryPoint myLastData = _myMap.get(myEffectable);
			
			double myVelocity = myLastData.position.distance(myEffectable.position()) / theAnimator.deltaTime();
			double myAcceleration = (myVelocity - myLastData.velocity) / theAnimator.deltaTime();
			double myJerk = (myAcceleration - myLastData.acceleration) / theAnimator.deltaTime();

			myLastData.position.set(myEffectable.position());
			myLastData.velocity = myVelocity;
			myLastData.acceleration = myAcceleration;
			myLastData.jerk = myJerk;
			
			myLastData.update(theAnimator);
		}
	}
	
	@CCProperty(name = "reset")
	public void reset() {
		for(CCMotionSourceHistoryPoint myPoint:_myMap.values()) {
			myPoint.reset();
		}
	}
	
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
