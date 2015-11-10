package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;

public class CCKleColorSignalAnimation extends CCKleAnimation<CCColor>{
	
	@CCProperty(name = "hue")
	private CCKleSignal _myHueSignal = new CCKleSignal();
	@CCProperty(name = "saturation")
	private CCKleSignal _mySaturationSignal = new CCKleSignal();
	@CCProperty(name = "brightness")
	private CCKleSignal _myBrightnessSignal = new CCKleSignal();
	@CCProperty(name = "phase speed", min = 0, max = 10)
	private double _cSpeed = 0;
	
	private double _myGlobalPhase = 0;
	
	@CCProperty(name = "amp", min = 0, max = 10)
	private double _cAmp = 1;
	
	@CCProperty(name = "hue offset", min = 0, max = 1)
	private double _cHueOffset = 0;
	
	public void update(final double theDeltaTime){
		_myGlobalPhase += theDeltaTime * _cSpeed;
		_myHueSignal.update(theDeltaTime);
		_mySaturationSignal.update(theDeltaTime);
		_myBrightnessSignal.update(theDeltaTime);
	}
	
	public CCColor animate(CCSequenceElement theElement){
		CCColor myResult = CCColor.createFromHSB(
			_myHueSignal.value(theElement, _myGlobalPhase) * _myHueSignal._cAmount + _cHueOffset, 
			_mySaturationSignal.value(theElement, _myGlobalPhase) * _mySaturationSignal._cAmount, 
			1 - (_myBrightnessSignal.value(theElement, _myGlobalPhase) * (_myBrightnessSignal._cAmount))
		);
		double myBlend = elementBlend(theElement);
		myResult.r *= myBlend * _cAmp;
		myResult.g *= myBlend * _cAmp;
		myResult.b *= myBlend * _cAmp;
		
		return myResult;
	}
}
