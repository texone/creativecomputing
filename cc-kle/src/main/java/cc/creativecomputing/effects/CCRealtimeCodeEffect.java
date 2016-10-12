package cc.creativecomputing.effects;

import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;

public class CCRealtimeCodeEffect extends CCEffect {
	
	public static interface CCRealtimeAnimation extends CCCompileObject{
		
		public void update(final double theDeltaTime);
		
		public double animate(CCEffectable theEffectable, CCEffectModulation theModulation);
	}
	
	private String[] _myValueNames = new String[0];
	
	@CCProperty(name = "real time animation")
	private CCRealtimeCompile<CCRealtimeAnimation> _myRealTimeAnimation;
	
	public CCRealtimeCodeEffect(){
		_myRealTimeAnimation = new CCRealtimeCompile<CCRealtimeAnimation>("cc.creativecomputing.control.CCRealtimeGraphImp", CCRealtimeAnimation.class);
		_myRealTimeAnimation.createObject();
	}

	@Override
	public void update(final double theDeltaTime) {
		if(_myRealTimeAnimation.instance() == null)return;
		
		_myRealTimeAnimation.instance().update(theDeltaTime);
	}

	public double[] applyTo(CCEffectable theEffectable) {
		if(_myRealTimeAnimation.instance() == null)return new double[_myValueNames.length];
		
		double myBlend = elementBlend(theEffectable);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			try{
				myResult[i] = _myRealTimeAnimation.instance().animate(theEffectable,modulation(_myValueNames[i])) * myBlend;
			}catch(Exception e){
				
			}
		}
		return myResult;
	}
}

