package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCKleColorRangeAnimation extends CCKleAnimation<CCColor>{
	
	@CCProperty(name = "hue")
	private CCColorRange _cHueModulation = new CCColorRange();
	@CCProperty(name = "saturation")
	private CCColorRange _cSaturationModulation = new CCColorRange();
	@CCProperty(name = "brightness")
	private CCColorRange _cBrightnessModulation = new CCColorRange();
	
	private class CCColorRange{
		@CCProperty(name = "modulation")
		private CCKleModulation _cModulation = new CCKleModulation();
		@CCProperty(name = "position", min = 0, max = 1)
		private double _cPosition = 0;
		@CCProperty(name = "range", min = 0, max = 1)
		private double _cRange = 0;
		@CCProperty(name = "amount", min = 0, max = 1)
		private double _cAmount = 1;
		@CCProperty(name = "constant", min = 0, max = 1)
		private double _cConstant = 1;
		
		
		private double value(CCSequenceElement theElement){
			double myMod = _cModulation.modulation(theElement);
			double myValue =  CCMath.saturate(1 - CCMath.abs(myMod - _cPosition) / _cRange);
			return CCMath.blend(_cConstant, myValue, _cAmount);
		}
	}

	@Override
	public CCColor animate(CCSequenceElement theElement) {
		
		CCColor myResult = CCColor.createFromHSB(
			_cHueModulation.value(theElement), 
			_cSaturationModulation.value(theElement), 
			_cBrightnessModulation.value(theElement)
		);

		double myBlend = elementBlend(theElement);
		myResult.r *= myBlend;
		myResult.g *= myBlend;
		myResult.b *= myBlend;
			
		return myResult;
	}

}
