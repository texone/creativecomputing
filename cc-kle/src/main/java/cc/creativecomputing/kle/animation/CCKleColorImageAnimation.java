package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.image.CCImageAsset;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCKleColorImageAnimation extends CCKleAnimation<CCColor>{
	
	@CCProperty(name = "x modulation")
	private CCKleModulation _cXModulation = new CCKleModulation();
	@CCProperty(name = "y modulation")
	private CCKleModulation _cYModulation = new CCKleModulation();
	@CCProperty(name = "amp", min = 1, max = 2)
	private double _cAmp = 1;
	@CCProperty(name = "h shift", min = -1, max = 1)
	private double _cHShift = 0;
	@CCProperty(name = "s shift", min = -1, max = 1)
	private double _cSShift = 0;
	@CCProperty(name = "b shift", min = -1, max = 1)
	private double _cBShift = 0;
	
	@CCProperty(name = "image")
	private CCImageAsset _myImage = new CCImageAsset();

	@Override
	public CCColor animate(CCSequenceElement theElement) {
		if(_myImage.value() == null)return new CCColor();
		CCColor myResult = _myImage.value().getPixel(
			_cXModulation.modulation(theElement) * _myImage.value().width(), 
			_cYModulation.modulation(theElement) * _myImage.value().height()
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
