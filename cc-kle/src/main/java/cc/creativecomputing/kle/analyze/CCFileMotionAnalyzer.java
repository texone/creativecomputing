package cc.creativecomputing.kle.analyze;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset.CCAssetListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.CCSequenceAsset;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMatrix2;

public class CCFileMotionAnalyzer extends CCSequenceAnalyzer {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequence;

	public CCFileMotionAnalyzer(CCSequenceElements theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myUseHistorySize = false;
		_mySequence = new CCSequenceAsset(theElements.mappings().get(CCKleChannelType.MOTORS));
		_mySequence.events().add(new CCAssetListener<CCSequence>() {
			@Override
			public void onChange(CCSequence theAsset) {
				reset();
				if(theAsset == null)return;
				float myUpdateTime = 1f / _mySequence.rate();
				
				for(int i = 0; i < theAsset.size();i++){
					_mySequence.frame(i);
					CCMatrix2 myFrame = _mySequence.frame();
					for(CCSequenceElement myElement:_myElements){
						double[] myLength = new double[myElement.motorSetup().channels().size()];
						int j = 0;
						for(CCMotorChannel myChannel:myElement.motorSetup().channels()){
							myLength[j] = myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()];
							j++;
						}
						myElement.motorSetup().setByRopeLength(myLength);
					}
					for(CCElementAnalyzer myAnalyzer:_myElementAnalyzers){
						myAnalyzer.update(myUpdateTime);
					}
				}
			}
		});
	}

	

}
