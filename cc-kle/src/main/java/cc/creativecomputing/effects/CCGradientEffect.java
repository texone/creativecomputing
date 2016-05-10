package cc.creativecomputing.effects;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.image.CCImageAsset;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCGradientEffect extends CCEffect{
	
	@CCProperty(name = "offset 1")
	private CCMixSignal _myOffset1Signal = new CCMixSignal();
	@CCProperty(name = "ofset2")
	private CCMixSignal _myOffset2Signal = new CCMixSignal();
	
	@CCProperty(name = "offset 1 modulation")
	private CCEffectModulation _cOffset1Modulation = new CCEffectModulation();
	@CCProperty(name = "offset 2 modulation")
	private CCEffectModulation _cOffset2Modulation = new CCEffectModulation();
	
	@CCProperty(name = "phase speed", min = 0, max = 10)
	private double _cSpeed = 0;
	
	@CCProperty(name = "offset 1 amp", min = 0, max = 1)
	private double _cOffset1Amp = 1;
	@CCProperty(name = "offset 2 amp", min = 0, max = 1)
	private double _cOffset2Amp = 1;
	@CCProperty(name = "offset add", min = 0, max = 1)
	private double _cOffset1Add = 1;
	

	@CCProperty(name = "gradient")
	private CCImageAsset _myImage = new CCImageAsset();
	private int _myResultLength = 0;
	
	private double _myGlobalPhase = 0;
	
	@CCProperty(name = "amp", min = 0, max = 10)
	private double _cAmp = 1;
	
	@CCProperty(name = "h shift", min = -1, max = 1)
	private double _cHShift = 0;
	@CCProperty(name = "s shift", min = -1, max = 1)
	private double _cSShift = 0;
	@CCProperty(name = "b shift", min = -1, max = 1)
	private double _cBShift = 0;
	
	public void update(final double theDeltaTime){
		_myGlobalPhase += theDeltaTime * _cSpeed;
	}
	
	public double[] applyTo(CCEffectable theEffectable){
		double[] myResult = new double[_myResultLength];
		if(_myImage.value() == null)return myResult;
		CCColor myColor = _myImage.value().getPixel(
			(
				_myOffset1Signal.value(_myGlobalPhase + _cOffset1Modulation.modulation(theEffectable)) * _cOffset1Amp + 
				_myOffset2Signal.value(_myGlobalPhase + _cOffset2Modulation.modulation(theEffectable)) * _cOffset2Amp +
				_cOffset1Add
			) * _myImage.value().width() % _myImage.value().width(), 
			0
		);
		double[] hsb = myColor.hsb();
		myColor.setHSB(
			(hsb[0] + _cHShift) % 1, 
			CCMath.saturate(hsb[1] + _cSShift),
			CCMath.saturate(hsb[2] + _cBShift)
		);

		double myBlend = elementBlend(theEffectable);
		for(int i = 0; i < _myResultLength;i++){
			switch(_myResultLength % 3){
			case 0:
				myResult[i] = CCMath.saturate(myColor.r * myBlend * _cAmp);
				break;
			case 1:
				myResult[i] = CCMath.saturate(myColor.g * myBlend * _cAmp);
				break;
			case 2:
				myResult[i] = CCMath.saturate(myColor.b * myBlend * _cAmp);
				break;
			}
		}
		
		return myResult;
	}
	
	@Override
	public void valueNames(String... theValueNames) {
		_myResultLength = theValueNames.length;
	}
}
