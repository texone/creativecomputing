package cc.creativecomputing.kle.animation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.math.CCColor;

public class CCKleColorAnimator extends CCKleAnimator<CCColor>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2930876449361910983L;

	
	public CCKleColorAnimator(CCSequenceElements theElements){
		super(theElements, CCKleChannelType.LIGHTS, new CCKleColorAnimationBlender());
	}

	@Override
	public void update(CCAnimator theAnimator){
		double myLightBlendsA = 0;
		double myLightBlendsB = 0;
		
		for(CCKleAnimation<CCColor> myLightAnimation:values()){
			myLightAnimation.update(theAnimator.deltaTime());
			myLightBlendsA += myLightAnimation.blend() * (1 - myLightAnimation.channelBlend());
			myLightBlendsB += myLightAnimation.blend() * myLightAnimation.channelBlend();
		}
		
		for(CCSequenceElement myElement:_myElements){
			CCColor myColorA = new CCColor();
			CCColor myColorB = new CCColor();
			
			for(CCKleAnimation<CCColor> myAnimation:values()){
				if(myAnimation.blend() == 0)continue;
				
				CCColor myAnimationColor = myAnimation.animate(myElement);
				myColorA.r += myAnimationColor.r * (1 - myAnimation.channelBlend());
				myColorA.g += myAnimationColor.g * (1 - myAnimation.channelBlend());
				myColorA.b += myAnimationColor.b * (1 - myAnimation.channelBlend());

				myColorB.r += myAnimationColor.r * (myAnimation.channelBlend());
				myColorB.g += myAnimationColor.g * (myAnimation.channelBlend());
				myColorB.b += myAnimationColor.b * (myAnimation.channelBlend());
			}
			
			if(myLightBlendsA == 0){
				myColorA.set(1f);
			}else{
				myColorA.r /= myLightBlendsA;
				myColorA.g /= myLightBlendsA;
				myColorA.b /= myLightBlendsA;
			}
			
			if(myLightBlendsB == 0){
				myColorB.set(1f);
			}else{
				myColorB.r /= myLightBlendsB;
				myColorB.g /= myLightBlendsB;
				myColorB.b /= myLightBlendsB;
			}
			
			CCColor myOutput = _myAnimationBlender.blend(myElement, myColorA, myColorB);
			myElement.lightSetup().setByRelativePosition(myOutput.r, myOutput.g, myOutput.b);
		}
	}
}
