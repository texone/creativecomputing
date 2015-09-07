package cc.creativecomputing.kle.animation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2Animator extends CCKleAnimator<CCVector2>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "x scale", min = 0, max = 3)
	private double _cXScale = 1;
	@CCProperty(name = "y scale", min = 0, max = 2)
	private double _cYScale = 1;
	
	public CCKleVector2Animator(CCSequenceElements theElements){
		super(theElements, CCKleChannelType.MOTORS, new CCKleVector2AnimationBlender());
	}

	public void update(CCAnimator theAnimator){
		for(CCKleAnimation<CCVector2> myAnimation:values()){
			myAnimation.update(theAnimator.deltaTime());
		}
		CCVector2 myCenter = new CCVector2();
		for(CCSequenceElement myElement:_myElements){
			CCVector2 myTranslationA = new CCVector2();
			CCVector2 myTranslationB = new CCVector2();
			for(CCKleAnimation<CCVector2> myAnimation:values()){
				CCVector2 myTranslation = myAnimation.animate(myElement);
				if(myTranslation.isNaN()){
					continue;
				}
				myTranslationA.addLocal(myTranslation.multiply(1 - myAnimation.channelBlend()));
				myTranslationB.addLocal(myTranslation.multiply(myAnimation.channelBlend()));
			}
			CCVector2 myTranslation = _myAnimationBlender.blend(myElement, myTranslationA, myTranslationB);
//			myElement.motorSetup().rotateZ(CCMath.sign(myTranslation.x) * CCMath.pow(CCMath.abs(myTranslation.x), _cRotationPow) * _cRotationAngle);
			myTranslation.multiplyLocal(_cXScale, _cYScale).multiplyLocal(0.5f).addLocal(0.5f, 0.5f);
			myElement.setup(_myChannelType).setByRelativePosition(myTranslation.x, myTranslation.y);
			myCenter.addLocal(myTranslation);
		}
		myCenter.multiplyLocal(1f / _myElements.size());

	}
}
