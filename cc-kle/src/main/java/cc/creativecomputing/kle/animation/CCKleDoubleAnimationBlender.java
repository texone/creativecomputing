package cc.creativecomputing.kle.animation;

import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;

public class CCKleDoubleAnimationBlender extends CCKleAnimationBlender<Double> {

	@Override
	public Double blend(CCSequenceElement theElement, Double theValueA, Double theValueB){
		return CCMath.blend(theValueA, theValueB, blend(theElement));
	}
}
