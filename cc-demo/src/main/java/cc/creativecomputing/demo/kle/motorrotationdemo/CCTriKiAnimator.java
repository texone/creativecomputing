package cc.creativecomputing.demo.kle.motorrotationdemo;

import java.util.LinkedHashMap;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;
import cc.creativecomputing.math.CCMath;

public class CCTriKiAnimator extends LinkedHashMap<String, CCTriKiAnimation<?>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "animation blender")
	private final CCTrikiAnimationBlender _myAnimationBlender;
	@CCProperty(name = "rotation pow", min = 1, max = 10)
	private double _cRotationPow = 1;
	@CCProperty(name = "rotation angle", min = 0, max = 45)
	private double _cRotationAngle = 1;
	
	private final List<CCTriangleElement> _myElements;
	
	public CCTriKiAnimator(List<CCTriangleElement> theElements){
		_myElements = theElements;
		_myAnimationBlender = new CCTrikiAnimationBlender();
	}

	public void update(CCAnimator theAnimator){
		for(CCTriKiAnimation<?> myAnimation:values()){
			myAnimation.update(theAnimator.deltaTime());
		}
		for(CCTriangleElement myElement:_myElements){
			double myTranslationA = 0;
			double myTranslationB = 0;
			for(CCTriKiAnimation<?> myAnimation:values()){
				double myTranslation = myAnimation.animate(myElement);
				if(Double.isNaN(myTranslation)){
					continue;
				}
				myTranslationA += (myTranslation * (1 - myAnimation.channelBlend()));
				myTranslationB += (myTranslation * (myAnimation.channelBlend()));
			}
			double myTranslation = _myAnimationBlender.blend(myElement, myTranslationA, myTranslationB);

			myElement._myAngle = myTranslation * CCMath.TWO_PI;
		}

	}
}
