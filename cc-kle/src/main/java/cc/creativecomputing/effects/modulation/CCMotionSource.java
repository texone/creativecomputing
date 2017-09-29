package cc.creativecomputing.effects.modulation;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.kle.analyze.CCMotionHistoryDataPoint;
import cc.creativecomputing.math.CCMath;

public class CCMotionSource extends CCModulationSource {

	@CCProperty(name = "velocity", min = 0, max = 1)
	private double _cVelocity;
	@CCProperty(name = "acceleration", min = 0, max = 1)
	private double _cAcceleration;
	@CCProperty(name = "jerk", min = 0, max = 1)
	private double _cJerk;
	
	@CCProperty(name = "max velocity")
	private double _cMaxVelocity = 1;
	@CCProperty(name = "max acceleration")
	private double _cMaxAcceleration = 1;
	@CCProperty(name = "max jerk")
	private double _cMaxJerk = 1;

	private Map<CCEffectable, CCMotionHistoryDataPoint> _myMap = new HashMap<>();

	public CCMotionSource(String theName) {
		super(theName, null);
		
		
		_myModulationImplementation = (effectManager, effectable) -> {
			if(!_myMap.containsKey(effectable)){
				return 0;
			}
			
			CCMotionHistoryDataPoint myLastData = _myMap.get(effectable);
			return CCMath.saturate(
				CCMath.abs(myLastData.velocity) / _cMaxVelocity * _cVelocity + 
				CCMath.abs(myLastData.acceleration) / _cMaxAcceleration * _cAcceleration + 
				CCMath.abs(myLastData.jerk) / _cMaxJerk * _cJerk
			);
		};
	}
	
	@Override
	public void update(CCAnimator theAnimator, CCEffectManager<?> theManager) {
		for(CCEffectable myEffectable:theManager.effectables()){
			if(!_myMap.containsKey(myEffectable)){
				_myMap.put(myEffectable, new CCMotionHistoryDataPoint(myEffectable.position(), 0, 0, 0, 0, theAnimator.deltaTime()));
				continue;
			}
			CCMotionHistoryDataPoint myLastData = _myMap.get(myEffectable);
			
			double myVelocity = myLastData.position.distance(myEffectable.position()) / theAnimator.deltaTime();
			double myAcceleration = (myVelocity - myLastData.velocity) / theAnimator.deltaTime();
			double myJerk = (myAcceleration - myLastData.acceleration) / theAnimator.deltaTime();

			myLastData.position.set(myEffectable.position());
			myLastData.velocity = myVelocity;
			myLastData.acceleration = myAcceleration;
			myLastData.jerk = myJerk;
			
		}
	}
	
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
