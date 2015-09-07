package cc.creativecomputing.kle.animation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;

public class CCKleDoubleAnimator extends CCKleAnimator<Double>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "scale", min = 0, max = 3)
	private double _cScale = 1;
	
	public CCKleDoubleAnimator(CCSequenceElements theElements, CCKleChannelType theChannelType){
		super(theElements, theChannelType, new CCKleDoubleAnimationBlender());
	}

	public void update(CCAnimator theAnimator){
		for(CCKleAnimation<Double> myAnimation:values()){
			myAnimation.update(theAnimator.deltaTime());
		}
//		double myCenter = 0;
		for(CCSequenceElement myElement:_myElements){
			double myValueA = 0;
			double myValueB = 0;
			for(CCKleAnimation<Double> myAnimation:values()){
				double myValue = myAnimation.animate(myElement);
				if(Double.isNaN(myValue)){
					continue;
				}
				myValueA += myValue * (1 - myAnimation.channelBlend());
				myValueB += myValue * (myAnimation.channelBlend());
			}
			double myValue = _myAnimationBlender.blend(myElement, myValueA, myValueB);
//			myElement.motorSetup().rotateZ(CCMath.sign(myTranslation.x) * CCMath.pow(CCMath.abs(myTranslation.x), _cRotationPow) * _cRotationAngle);
			myValue *= _cScale * 0.5f;
			myValue += 0.5f;
			myElement.setup(_myChannelType).setByRelativePosition(myValue);
		}
	}
}
