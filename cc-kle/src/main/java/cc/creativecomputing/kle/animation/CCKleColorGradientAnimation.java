package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.image.CCImageAsset;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCKleColorGradientAnimation extends CCKleAnimation<CCColor>{
	
	@CCProperty(name = "offset 1")
	private CCKleSignal _myOffset1Signal = new CCKleSignal();
	@CCProperty(name = "ofset2")
	private CCKleSignal _myOffset2Signal = new CCKleSignal();
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
		_myOffset1Signal.update(theDeltaTime);
		_myOffset2Signal.update(theDeltaTime);
	}
	
	public CCColor animate(CCSequenceElement theElement){
		
		if(_myImage.value() == null)return new CCColor();
		CCColor myResult = _myImage.value().getPixel(
			(
				_myOffset1Signal.value(theElement, _myGlobalPhase) * _cOffset1Amp + 
				_myOffset2Signal.value(theElement, _myGlobalPhase) * _cOffset2Amp +
				_cOffset1Add
			) * _myImage.value().width() % _myImage.value().width(), 
			0
		);

		double myBlend = elementBlend(theElement);
		myResult.r = CCMath.saturate(myResult.r * myBlend * _cAmp);
		myResult.g = CCMath.saturate(myResult.g * myBlend * _cAmp);
		myResult.b = CCMath.saturate(myResult.b * myBlend * _cAmp);
		
		
		
		double[] hsb = myResult.hsb();
		myResult.setHSB(
			(hsb[0] + _cHShift) % 1, 
			CCMath.saturate(hsb[1] + _cSShift),
			CCMath.saturate(hsb[2] + _cBShift)
		);
		
		return myResult;
	}
}
