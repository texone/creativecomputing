package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2SignalAnimation extends CCKleAnimation<CCVector2>{
	
	@CCProperty(name = "x signal")
	private CCKleSignal _myXSignal = new CCKleSignal();
	@CCProperty(name = "y signal")
	private CCKleSignal _myYSignal = new CCKleSignal();
	@CCProperty(name = "phase speed", min = 0, max = 0.1)
	private double _cSpeed = 0;
	
	private double _myGlobalPhase = 0;
	
	
	public void update(final double theDeltaTime){
		_myGlobalPhase += theDeltaTime * _cSpeed;
		_myXSignal.update(theDeltaTime);
		_myYSignal.update(theDeltaTime);
	}
	
	public CCVector2 animate(CCSequenceElement theElement){
		return new CCVector2(
			_myXSignal.value(theElement, _myGlobalPhase, -1, 1) * _cBlend * _myXSignal._cAmount, 
			_myYSignal.value(theElement, _myGlobalPhase, -1, 1) * _cBlend * _myYSignal._cAmount
		);
	}

}
