package cc.creativecomputing.kle.animation;

import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2AnimationBlender extends CCKleAnimationBlender<CCVector2> {

	@Override
	public CCVector2 blend(CCSequenceElement theElement, CCVector2 theTranslationA, CCVector2 theTranslationB){
		return CCVector2.lerp(theTranslationA, theTranslationB, blend(theElement));
	}
}
