package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public abstract class CCKleAnimationBlender<BlendType> {

	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();
	
	@CCProperty(name = "")
	private double _cBlend = 0;
	@CCProperty(name = "apply blend", min = 0, max = 1)
	private double _cApplyBlendRange = 0f;
	@CCProperty(name = "blend range", min = 0, max = 1)
	private double _cBlendRange = 0f;
	
	@CCProperty(name = "reverse")
	private boolean _cReverse = true;
	
	protected double blend(CCSequenceElement myElement){
		double myOffsetSum = _cModulation.offsetSum();
		double myBlendRadius = (myOffsetSum + _cBlendRange) * ((!_cReverse) ? 1 -_cBlend : _cBlend);
		
		double myModulation = myOffsetSum == 0 ? 0 : _cModulation.modulation(myElement);
		
		double myBlend = CCMath.saturate((myBlendRadius - myModulation)  / (_cBlendRange == 0 ? 1 : _cBlendRange));
		
		if(!_cReverse) {
			myBlend = 1 - myBlend;
		}
		
		
		return myBlend = CCEaseFormular.SINE.easing().easeInOut(CCMath.blend(_cBlend, myBlend, _cApplyBlendRange));
	}
	
	public abstract BlendType blend(CCSequenceElement theElement, BlendType theA, BlendType theB);
}
