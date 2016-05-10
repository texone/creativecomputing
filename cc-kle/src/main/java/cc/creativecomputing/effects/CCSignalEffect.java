package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCSignalEffect extends CCEffect{

	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	
	@CCProperty(name = "phase speed", min = 0, max = 50)
	private double _cSpeed = 0;
	@CCProperty(name = "phase scale", min = 1, max = 10)
	private double _cPhaseScale = 1;
	
	@CCProperty(name = "amount modulations")
	private Map<String, CCEffectModulation> _cAmountModulations = null;

	private String[] _myAmountNames = new String[0];

	private double _myPhase = 0;

	
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
			double mySignal = _mySignal.value(_myPhase + _cModulations.get(_myValueNames[i]).modulation(theEffectable, -1, 1));
			mySignal = mySignal * 2 - 1;
			double myAmount = _cAmountModulations.get(_myAmountNames[i]).modulation(theEffectable, -1, 1) * myBlend;
			myResult[i] = mySignal * myAmount;
		}
		return myResult;
	}

	@Override
	public void valueNames(String... theValueNames) {
		super.valueNames(theValueNames);
		_cAmountModulations = new LinkedHashMap<>();
		_myAmountNames = new String[theValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			_myAmountNames[i] = theValueNames[i] + " amount modulation";
			_cAmountModulations.put(_myAmountNames[i], new CCEffectModulation());
		}
	}

}
