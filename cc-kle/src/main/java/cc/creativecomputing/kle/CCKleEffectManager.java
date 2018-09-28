package cc.creativecomputing.kle;

import java.util.List;

import cc.creativecomputing.core.CCTimer;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.gl.app.CCGLTimer;

public class CCKleEffectManager extends CCEffectManager<CCKleEffectable>{

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
	
	public boolean isInRecord = false;
	
	@Override
	public void update(CCTimer theAnimator) {
		if(isInRecord)return;
		super.update(theAnimator);
	}
	
	public void updateRecord(CCGLTimer theAnimator) {
		super.update(theAnimator);
	}
	
	private CCSequenceElementSetupValues[] _mySetups;
	
	public CCKleEffectManager(List<CCKleEffectable> theElements, CCKleChannelType theChannelType, String...theValueNames){
		super(theElements, theValueNames);
		_mySetups = new CCSequenceElementSetupValues[]{new CCSequenceElementSetupValues(theChannelType, 0, theValueNames.length)};
	}
	
	public CCKleEffectManager(List<CCKleEffectable> theElements, CCSequenceElementSetupValues[] theSetups, String...theValueNames){
		super(theElements, theValueNames);
		_mySetups = theSetups;
	}
	
	@Override
	public void apply(CCKleEffectable theEffectable, double[] theValues) {
		for(CCSequenceElementSetupValues mySetup:_mySetups){
			if(theEffectable._cActive){
				theEffectable.setup(mySetup._myChannelType).setByRelativePosition(CCArrayUtil.subset(theValues, mySetup._myStart, mySetup._myValues));
			}else{
				theEffectable.setup(mySetup._myChannelType).setByRelativePosition(CCArrayUtil.subset(_myDefaultValues, mySetup._myStart, mySetup._myValues));
//				CCLog.info(_myDefaultValues[0] + " : " + _myDefaultValues[1] + " : " + theEffectable.setup(mySetup._myChannelType).position());
			}
		}
	}

}
