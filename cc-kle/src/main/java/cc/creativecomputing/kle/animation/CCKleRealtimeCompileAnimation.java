package cc.creativecomputing.kle.animation;

import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCVector2;

public abstract class CCKleRealtimeCompileAnimation<Type> extends CCKleAnimation<Type> {
	
	public static interface CCRealtimeAnimation<Type> extends CCCompileObject{
		
		public void update(final float theDeltaTime);
		
		public Type animate(CCSequenceElement theElement, CCKleModulation theModulation);
	}

	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();
	
	@CCProperty(name = "real time visual")
	private CCRealtimeCompile<CCRealtimeAnimation> _myRealTimeAnimation;
	
	public CCKleRealtimeCompileAnimation(){
		_myRealTimeAnimation = new CCRealtimeCompile<CCRealtimeAnimation>("cc.creativecomputing.control.CCRealtimeGraphImp", CCRealtimeAnimation.class);
		_myRealTimeAnimation.createObject();
	}

	public void update(final float theDeltaTime) {
		if(_myRealTimeAnimation.instance() == null)return;
		
		_myRealTimeAnimation.instance().update(theDeltaTime);
	}

	public Type animate(CCSequenceElement theElement) {
		if(_myRealTimeAnimation.instance() == null)return null;
		Type myResult = (Type)_myRealTimeAnimation.instance().animate(theElement,_cModulation);
		if(myResult == null)myResult = null;
		return myResult;
	}

}

