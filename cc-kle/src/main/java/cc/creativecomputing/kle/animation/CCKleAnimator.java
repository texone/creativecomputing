package cc.creativecomputing.kle.animation;

import java.util.LinkedHashMap;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;

public abstract class CCKleAnimator<Type> extends LinkedHashMap<String, CCKleAnimation<Type>> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	@CCProperty(name = "animation blender")
	protected final CCKleAnimationBlender<Type> _myAnimationBlender;
	
	protected final CCSequenceElements _myElements;
	
	protected final CCKleChannelType _myChannelType;
	
	public CCKleAnimator(CCSequenceElements theElements, CCKleChannelType theChannelType, CCKleAnimationBlender<Type> theAnimationBlender){
		_myElements = theElements;
		_myChannelType = theChannelType;
		_myAnimationBlender = theAnimationBlender;
	}
	
	@Override
	public CCKleAnimation<Type> put(String theKey, CCKleAnimation<Type> theAnimation) {
		theAnimation.addGroupBlends(_myElements.groups());
		return super.put(theKey, theAnimation);
	}

	@Override
	public void update(CCAnimator theAnimator){}
	
	@Override
	public void start(CCAnimator theAnimator) {}
	
	@Override
	public void stop(CCAnimator theAnimator) {}
}
