package cc.creativecomputing.effects;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCSignalEffect extends CCEffect{

	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	
	@CCProperty(name = "phase speed", min = 0, max = 50)
	private double _cSpeed = 0;
	@CCProperty(name = "phase scale", min = 1, max = 10)
	private double _cPhaseScale = 1;

	private double _myPhase = 0;
	
	public CCSignalEffect(){
		super("phase", "amount", "frequency");
	}

	
	@CCProperty(name = "reset phase")
	public void resetPhase(){
		_myPhase = 0;
	}
	
	public void update(final double theDeltaTime){
		_myPhase += theDeltaTime * _cSpeed;
	}
	
	@Override
	public double[] applyTo(CCEffectable theEffectable){
		double myBlend = elementBlend(theEffectable);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			double myPhase = _myPhase + modulation("phase", _myValueNames[i]).modulation(theEffectable, -1, 1);
			myPhase *= modulation("frequency", _myValueNames[i]).modulation(theEffectable, -1, 1);
			double mySignal = _mySignal.value(myPhase);
			mySignal = mySignal * 2 - 1;
			double myAmount = modulation("amount", _myValueNames[i]).modulation(theEffectable, -1, 1) * myBlend;
			myResult[i] = mySignal * myAmount;
		}
		return myResult;
	}

}
