package cc.creativecomputing.demo.kle.motorrotationdemo;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCTrikiAnimationBlender {

	@CCProperty(name = "modulation")
	private CCTriKiModulation _cModulation = new CCTriKiModulation();
	
	@CCProperty(name = "")
	private double _cBlend = 0;
	@CCProperty(name = "apply blend", min = 0, max = 1)
	private double _cApplyBlendRange = 0f;
	@CCProperty(name = "blend range", min = 0, max = 1)
	private double _cBlendRange = 0f;
	
	@CCProperty(name = "reverse")
	private boolean _cReverse = true;
	
	protected double blend(CCTriangleElement myElement){
		double myOffsetSum = _cModulation.offsetSum();
		double myBlendRadius = (myOffsetSum + _cBlendRange) * ((!_cReverse) ? 1 -_cBlend : _cBlend);
		
		double myModulation = myOffsetSum == 0 ? 0 : _cModulation.modulation(myElement);
		
		double myBlend = CCMath.saturate((myBlendRadius - myModulation)  / (_cBlendRange == 0 ? 1 : _cBlendRange));
		
		if(!_cReverse) {
			myBlend = 1 - myBlend;
		}
		
		
		return myBlend = CCEaseFormular.SINE.easing().easeInOut(CCMath.blend(_cBlend, myBlend, _cApplyBlendRange));
	}
	
	public double blend(CCTriangleElement theElement, double theA, double theB){
		return CCMath.blend(theA, theB, blend(theElement));
	}
}
