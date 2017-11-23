package cc.creativecomputing.kle.analyze;

import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectable;

public class CCKleRealtimeAnalyzer extends CCKleAnalyzer implements CCAnimatorListener{

	public CCKleRealtimeAnalyzer(List<CCKleEffectable> theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myUseHistorySize = true;
		theAnimator.listener().add(this);
	}

	@Override
	public void start(CCAnimator theAnimator) {}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_cAnalyzeMode == CCAnalyzeMode.OFF)return;
		if(!_cUpdate)return;
		
		for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
			myAnalyzer.update(theAnimator.deltaTime());
		}
	}

	@Override
	public void stop(CCAnimator theAnimator) {}

}
