package cc.creativecomputing.effects;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCSignalEffect extends CCEffect{

	@CCProperty(name = "signal")
	private CCMixSignal _mySignal = new CCMixSignal();
	
	@CCProperty(name = "phase speed", min = 0, max = 50)
	private double _cSpeed = 0;
	@CCProperty(name = "phase scale", min = 1, max = 10)
	private double _cPhaseScale = 1;

	private double _myPhase = 0;
	
	@CCProperty(name = "gradient")
	private CCGradient _cGradient = new CCGradient();
	@CCProperty(name = "use gradient")
	private boolean _cUseGradient = false;
	@CCProperty(name = "gradient channel")
	private int _cGradientChannel = 0;
	@CCProperty(name = "print")
	private boolean _cPrint = false;
	
	@Override
	public String[] modulationSources(String[]theValueNames) {
		String[] myResult = new String[theValueNames.length * 3];
		int i = 0;
		for(String myModulation:new String[] {"phase", "amount", "frequency"}){
			for(String myValueName:_myValueNames){
				myResult[i++] = myValueName + " " + myModulation;
			}
		}
		return myResult;
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
		if(_cUseGradient) {
			double[] myResult = new double[_myValueNames.length];
			double myPhase = _myPhase + modulation( _myValueNames[_cGradientChannel] + " phase").modulation(theEffectable, -1, 1);
			myPhase *= modulation( _myValueNames[_cGradientChannel] + " frequency").modulation(theEffectable, -1, 1);
			double mySignal = _mySignal.value(myPhase);
			mySignal = mySignal * 2 - 1;
			double myAmount = modulation(_myValueNames[_cGradientChannel] + " amount").modulation(theEffectable, -1, 1) * myBlend;
			double myGrad = CCMath.abs(mySignal * myAmount) % 1;
			if(_cPrint)CCLog.info(myGrad);
			CCColor myColor = _cGradient.color(myGrad);
				
			for(int i = 0; i < _myValueNames.length;i++){
				switch(i % 4){
				case 0:
					myResult[i] = CCMath.saturate(myColor.r * myBlend);
					break;
				case 1:
					myResult[i] = CCMath.saturate(myColor.g * myBlend);
					break;
				case 2:
					myResult[i] = CCMath.saturate(myColor.b * myBlend);
					break;
				case 3:
					myResult[i] = CCMath.saturate(myColor.a * myBlend);
					break;
				}
			}
				
			return myResult;
		}else {
			double[] myResult = new double[_myValueNames.length];
			for(int i = 0; i < _myValueNames.length;i++){
				double myPhase = _myPhase + modulation( _myValueNames[i] + " phase").modulation(theEffectable, -1, 1);
				myPhase *= modulation(_myValueNames[i] + " frequency").modulation(theEffectable, -1, 1);
				double mySignal = _mySignal.value(myPhase);
				mySignal = mySignal * 2 - 1;
				double myAmount = modulation(_myValueNames[i] + " amount").modulation(theEffectable, -1, 1) * myBlend;
				myResult[i] = mySignal * myAmount;
			}
			return myResult;
		}
	}

}
