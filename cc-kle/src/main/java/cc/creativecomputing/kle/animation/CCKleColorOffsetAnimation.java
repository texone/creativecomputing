package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;

public class CCKleColorOffsetAnimation extends CCKleAnimation<CCColor>{
	
	@CCProperty(name = "hue modulation")
	private CCKleModulation _cHueModulation = new CCKleModulation();
	@CCProperty(name = "saturation modulation")
	private CCKleModulation _cSaturationModulation = new CCKleModulation();
	@CCProperty(name = "brightness modulation")
	private CCKleModulation _cBrightnessModulation = new CCKleModulation();

	@Override
	public CCColor animate(CCSequenceElement theElement) {
		
		CCColor myResult = CCColor.createFromHSB(
			_cHueModulation.modulation(theElement) * 2, 
			_cSaturationModulation.modulation(theElement) * 2, 
			_cBrightnessModulation.modulation(theElement) * 2
		);

		double myBlend = elementBlend(theElement);
		myResult.r *= myBlend;
		myResult.g *= myBlend;
		myResult.b *= myBlend;
			
		return myResult;
	}

}
