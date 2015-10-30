package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCKleSignal{
	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	
	@CCProperty(name = "amount", min = 0, max = 1) double _cAmount = 0;
	
	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();
	@CCProperty(name = "amount modulation")
	private CCKleModulation _cAmountModulation = new CCKleModulation();

	@CCProperty(name = "use global phase")
	private boolean _cUseGlobalPhase = false;
	@CCProperty(name = "phase speed", min = 0, max = 0.2)
	private double _cSpeed = 0;
	
	private double _myPhase = 0;
	
	@CCProperty(name = "factor", min = 1, max = 10)
	private double _cFactor = 1;
	
	public void update(final double theDeltaTime){
		_myPhase += theDeltaTime * _cSpeed;
	}
	
	public double value(CCSequenceElement theElement, double theGlobalPhase){
		double myPhase = _cUseGlobalPhase ? theGlobalPhase : _myPhase;
		double myResult = _mySignal.value(
			(myPhase + _cModulation.modulation(theElement)) * _cFactor
		);
//		myResult *= _cAmountModulation.modulation(theElement, -1, 1);
		return myResult;
	}
	
	public double value(CCSequenceElement theElement, double theGlobalPhase, double theMin, double theMax){
		double myPhase = _cUseGlobalPhase ? theGlobalPhase : _myPhase;
		double myResult = _mySignal.value(
			(myPhase + _cModulation.modulation(theElement)) * _cFactor
		);
//		myResult *= _cAmountModulation.modulation(theElement, -1, 1);
		return CCMath.blend(theMin, theMax, myResult);
	}
}