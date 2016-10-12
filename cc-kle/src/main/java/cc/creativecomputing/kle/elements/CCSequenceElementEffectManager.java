package cc.creativecomputing.kle.elements;

import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.effects.CCEffectManager;

public class CCSequenceElementEffectManager extends CCEffectManager<CCSequenceElement> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	
	
	public static class CCSequenceElementSetupValues{
		protected final CCKleChannelType _myChannelType;
		protected int _myStart;
		protected int _myValues;
		
		public CCSequenceElementSetupValues(CCKleChannelType theChannelType, int theStart, int theValues){
			_myChannelType = theChannelType;
			_myStart = theStart;
			_myValues = theValues;
		}
	}
	
	private CCSequenceElementSetupValues[] _mySetups;
	
	public CCSequenceElementEffectManager(CCSequenceElements theElements, CCKleChannelType theChannelType, String...theValueNames){
		super(theElements, theValueNames);
		_mySetups = new CCSequenceElementSetupValues[]{new CCSequenceElementSetupValues(theChannelType, 0, theValueNames.length)};
	}
	
	public CCSequenceElementEffectManager(CCSequenceElements theElements, CCSequenceElementSetupValues[] theSetups, String...theValueNames){
		super(theElements, theValueNames);
		_mySetups = theSetups;
	}
	
	@Override
	public void apply(CCSequenceElement theEffectable, double[] theValues) {
		for(CCSequenceElementSetupValues mySetup:_mySetups){
			theEffectable.setup(mySetup._myChannelType).setByRelativePosition(CCArrayUtil.subset(theValues, mySetup._myStart, mySetup._myValues));
		}
	}

}
