package cc.creativecomputing.kle.elements;

import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.effects.CCEffectManager;

public class CCSequenceElementEffectManager extends CCEffectManager<CCSequenceElement> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	protected final CCKleChannelType _myChannelType;
	
	public CCSequenceElementEffectManager(CCSequenceElements theElements, CCKleChannelType theChannelType, String...theValueNames){
		super(theElements, theValueNames);
		_myChannelType = theChannelType;
	}
	
	@Override
	public void apply(CCSequenceElement theEffectable, double[] theValues) {
		theEffectable.setup(_myChannelType).setByRelativePosition(theValues);
	}

}
