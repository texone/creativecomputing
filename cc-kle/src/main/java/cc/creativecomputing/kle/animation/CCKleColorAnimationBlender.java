package cc.creativecomputing.kle.animation;

import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;

public class CCKleColorAnimationBlender extends CCKleAnimationBlender<CCColor>{

	@Override
	public CCColor blend(CCSequenceElement myElement, CCColor theColorA, CCColor theColorB){
		return CCColor.blend(theColorA, theColorB, (float)blend(myElement));
	}
	
}
