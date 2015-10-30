package cc.creativecomputing.kle.analyze;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;

public class CCRealtimeSequenceAnalyzer extends CCSequenceAnalyzer implements CCAnimatorListener{

	public CCRealtimeSequenceAnalyzer(CCSequenceElements theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myUseHistorySize = true;
		theAnimator.listener().add(this);
	}

	@Override
	public void start(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_cAnalyzeMode == CCAnalyzeMode.OFF)return;
		if(!_cUpdate)return;
		
		for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
			myAnalyzer.update(theAnimator.deltaTime());
		}
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

}
