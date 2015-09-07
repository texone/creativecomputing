package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;

public class CCKleDoubleSignalAnimation extends CCKleAnimation<Double>{
	
	@CCProperty(name = "signal")
	private CCKleSignal _mySignal = new CCKleSignal();
	@CCProperty(name = "phase speed", min = 0, max = 50)
	private double _cSpeed = 0;
	
	private double _myGlobalPhase = 0;
	
	
	public void update(final double theDeltaTime){
		_myGlobalPhase += theDeltaTime * _cSpeed;
		_mySignal.update(theDeltaTime);
	}
	
	@Override
	public Double animate(CCSequenceElement theElement){
		return _mySignal.value(theElement, _myGlobalPhase, -1, 1) * _cBlend * _mySignal._cAmount;
	}

}
