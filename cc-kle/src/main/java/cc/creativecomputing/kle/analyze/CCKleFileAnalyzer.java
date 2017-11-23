package cc.creativecomputing.kle.analyze;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset.CCAssetListener;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.kle.postprocess.CCSequenceProcessor;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceAsset;
import cc.creativecomputing.math.CCMatrix2;

public class CCKleFileAnalyzer extends CCKleAnalyzer {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequence;
	@CCProperty(name = "position", min = 0, max = 1)
	private double _cPosition = 0;
	
	private class CCFileProcessor{
		@CCProperty(name = "processor", hide = true)
		private CCSequenceProcessor _myProcessor;
		
		private CCFileProcessor(CCSequenceProcessor theProcessor){
			_myProcessor = theProcessor;
		}
		
		@CCProperty(name = "apply")
		private void apply(CCTriggerProgress theProgress){
			theProgress.start();
			applyProcessor(_myProcessor, theProgress);
			theProgress.end();
		}
	}

	@CCProperty(name = "processors", hide = true)
	private Map<String, CCFileProcessor> _myProcessorMap = new LinkedHashMap<>();
	
	private CCKleMapping<?> _myMapping;
	
	public CCKleFileAnalyzer(CCKleEffectables theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myMapping = theElements.mappings().get(CCKleChannelType.MOTORS);
		_myUseHistorySize = false;
		_mySequence = new CCSequenceAsset(_myMapping, "kle", "bin", "xml");
		_mySequence.normalize(false);
		_mySequence.events().add(new CCAssetListener<CCSequence>() {
			@Override
			public void onChange(CCSequence theAsset) {
				CCLog.info(_mySequence.value() == null);
				CCLog.info(theAsset.length());
				reset1();
			}
		});
	}
	
	public void  addProcessors(String theName, CCSequenceProcessor theProcessor){
		_myProcessorMap.put(theName, new CCFileProcessor(theProcessor));
	}
	
	private void resetAnalyzerData(){
		float myUpdateTime = 1f / _mySequence.rate();
		reset();
		for(int i = 0; i < _mySequence.value().size();i++){
			_mySequence.frame(i);
			CCMatrix2 myFrame = _mySequence.frame();
			for(CCKleEffectable myElement:_myElements){
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
	
	@CCProperty(name = "reset")
	public void reset1(){
		if(_mySequence.value() == null)return;
		
		resetAnalyzerData();
	}
	
	@CCProperty(name = "undo")
	public void undo(){
		if(_myLast == null)return;
		
		for(int i = 0; i < _myLast.size(); i++){
			_mySequence.value().set(i, _myLast.get(i));
		}

		resetAnalyzerData();
	}
	
//	private CCSequence _myInterpolatedSequence;
	
	public CCSequence interpolation(){
		return _mySequence.value();
	}
	
	private CCSequence _myLast;
	
	public void applyProcessor(CCSequenceProcessor theProcessor, CCTriggerProgress theProgress){
		
		_myLast = _mySequence.value().clone();
		
		theProcessor.process(_mySequence.value(), _myMapping, theProgress);
		
		resetAnalyzerData();
	}

	public void update(CCAnimator theAnimator){
		if(_mySequence == null)return;
		_mySequence.time(0, _cPosition * _mySequence.length(), 0);
		CCMatrix2 myFrame = _mySequence.frame();
		if(myFrame == null)return;
		for(CCKleEffectable myElement:_myElements){
			double[] myLength = new double[myElement.motorSetup().channels().size()];
			int j = 0;
			for(CCMotorChannel myChannel:myElement.motorSetup().channels()){
				myLength[j] = myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()];
				j++;
			}
			myElement.motorSetup().setByRopeLength(myLength);
		}
	}

}
