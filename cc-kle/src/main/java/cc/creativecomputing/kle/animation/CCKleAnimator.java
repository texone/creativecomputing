package cc.creativecomputing.kle.animation;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;

public class CCKleAnimator extends LinkedHashMap<String, CCKleAnimation> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "animation blender")
	protected final CCKleAnimationBlender _myAnimationBlender;
	
	protected final CCSequenceElements _myElements;
	
	protected final CCKleChannelType _myChannelType;
	
	private final String[] _myValueNames;
	
	@CCProperty(name = "scales", min = 0, max = 3, defaultValue = 1)
	private final Map<String, Double> _cScales = new LinkedHashMap<>();
	
	@CCProperty(name = "normalize")
	private boolean _cNormalize = false;
	
	public CCKleAnimator(CCSequenceElements theElements, CCKleChannelType theChannelType, String...theValueNames){
		_myElements = theElements;
		_myChannelType = theChannelType;
		_myAnimationBlender = new CCKleAnimationBlender();
		_myValueNames = theValueNames;
		for(String myValueName:_myValueNames){
			_cScales.put(myValueName + " scale", 1.0);
		}
	}
	
	@Override
	public CCKleAnimation put(String theKey, CCKleAnimation theAnimation) {
		theAnimation.addGroupBlends(_myElements.groups());
		theAnimation.valueNames(_myValueNames);
		return super.put(theKey, theAnimation);
	}

	@Override
	public void update(CCAnimator theAnimator){
		double myBlendSumA = 0;
		double myBlendSumB = 0;
		
		for(CCKleAnimation myAnimation:values()){
			myAnimation.update(theAnimator.deltaTime());
			myBlendSumA += myAnimation.blend() * (1 - myAnimation.channelBlend());
			myBlendSumB += myAnimation.blend() * myAnimation.channelBlend();
		}
//		double myCenter = 0;
		for(CCSequenceElement myElement:_myElements){
			double[] myValueA = new double[_myValueNames.length];
			double[] myValueB = new double[_myValueNames.length];
			for(CCKleAnimation myAnimation:values()){
				double[] myValues = myAnimation.animate(myElement);
				
				for(int i = 0; i < myValues.length;i++){
					double myValue = myValues[i];
					if(Double.isNaN(myValue))continue;
					
					myValueA[i] += myValue * (1 - myAnimation.channelBlend());
					myValueB[i] += myValue * (myAnimation.channelBlend());
				}
				
			}
			
			if(_cNormalize){
				for(int i = 0; i < myValueA.length;i++){
					myValueA[i] = myBlendSumA == 0 ? 1 : myValueA[i] / myBlendSumA;
					myValueB[i] = myBlendSumB == 0 ? 1 : myValueB[i] / myBlendSumB;
				}
			}
			double[] myValues = _myAnimationBlender.blend(myElement, myValueA, myValueB);
//			myElement.motorSetup().rotateZ(CCMath.sign(myTranslation.x) * CCMath.pow(CCMath.abs(myTranslation.x), _cRotationPow) * _cRotationAngle);
			for(int i = 0; i < myValues.length;i++){
				String myValueName = _myValueNames[i];
				myValues[i] = myValues[i] * _cScales.get(myValueName + " scale") * 0.5 + 0.5;
			}
			myElement.setup(_myChannelType).setByRelativePosition(myValues);
		}
	}
	
	@Override
	public void start(CCAnimator theAnimator) {}
	
	@Override
	public void stop(CCAnimator theAnimator) {}
}
