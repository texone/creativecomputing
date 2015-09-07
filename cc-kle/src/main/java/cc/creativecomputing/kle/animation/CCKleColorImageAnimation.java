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
	
	@CCProperty(name = "image")
	private CCImageAsset _myImage = new CCImageAsset();

	@Override
	public CCColor animate(CCSequenceElement theElement) {
		if(_myImage.value() == null)return new CCColor();
		CCColor myResult = _myImage.value().getPixel(
			_cXModulation.modulation(theElement) * _myImage.value().width(), 
			_cYModulation.modulation(theElement) * _myImage.value().width()
		);
			
		myResult.r = CCMath.saturate(myResult.r * _cBlend * _cAmp);
		myResult.g = CCMath.saturate(myResult.g * _cBlend * _cAmp);
		myResult.b = CCMath.saturate(myResult.b * _cBlend * _cAmp);
			
		return myResult;
	}

}
