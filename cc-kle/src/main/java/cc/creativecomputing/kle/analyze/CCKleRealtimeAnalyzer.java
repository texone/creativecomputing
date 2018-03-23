package cc.creativecomputing.kle.analyze;

import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectable;

public class CCKleRealtimeAnalyzer extends CCKleAnalyzer{

	public CCKleRealtimeAnalyzer(List<CCKleEffectable> theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myUseHistorySize = true;
		theAnimator.updateEvents.add(e -> {
			if(_cAnalyzeMode == CCAnalyzeMode.OFF)return;
			if(!_cUpdate)return;
			
			for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
				myAnalyzer.update(e.deltaTime());
			}
		});
	}

}
