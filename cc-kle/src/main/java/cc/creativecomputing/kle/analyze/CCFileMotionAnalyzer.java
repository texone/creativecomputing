package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCAsset.CCAssetListener;
import cc.creativecomputing.control.timeline.util.CubicSolver;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.CCSequenceAsset;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCFileMotionAnalyzer extends CCSequenceAnalyzer {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequence;
	@CCProperty(name = "position", min = 0, max = 1)
	private double _cPosition = 0;

	public CCFileMotionAnalyzer(CCSequenceElements theElements, CCAnimator theAnimator, CCKleChannelType theType) {
		super(theElements, theType);
		_myUseHistorySize = false;
		_mySequence = new CCSequenceAsset(theElements.mappings().get(CCKleChannelType.MOTORS), "kle", "bin", "xml");
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
	
	private class CCValueIndex{
		private double value;
		private int index;
		
		private CCValueIndex(double theValue, int theIndex){
			value = theValue;
			index = theIndex;
		}
	}
	
	/**
	 * Returns the bezier blend between 0 and 1 that would result in the given x
	 * @param theTime0 time of the first key
	 * @param theTime1 time of the first control point
	 * @param theTime2 time of the second control point
	 * @param theTime3 time of the second key
	 * @param theTime 
	 * @return bezier blend for the given time
	 */
	private double bezierBlend(double theTime0, double theTime1, double theTime2, double theTime3, double theTime) {
		double a = -theTime0 + 3 * theTime1 - 3 * theTime2 + theTime3;
		double b = 3 * theTime0 - 6 * theTime1 + 3 * theTime2;
		double c = -3 * theTime0 + 3 * theTime1;
		double d = theTime0 - theTime;

		double[] myResult = CubicSolver.solveCubic(a, b, c, d);
		int i = 0;
		while(i < myResult.length - 1 && (myResult[i] < 0 || myResult[i] > 1)) {
			i++;
		}
		return myResult[i];
	}
	
	private double bezierValue(double theValue0, double theValue1, double theValue2, double theValue3, double theBlend) {
		double a = -theValue0 + 3 * theValue1 - 3 * theValue2 + theValue3;
		double b = 3 * theValue0 - 6 * theValue1 + 3 * theValue2;
		double c = -3 * theValue0 + 3 * theValue1;
		double d = theValue0;
		
		return 
		a * theBlend * theBlend * theBlend + 
		b * theBlend * theBlend	+ 
		c * theBlend + 
		d;
	}

	public double sampleBezierSegment(CCValueIndex p0, CCValueIndex p1, CCValueIndex p2, CCValueIndex p3, double theTime) {
		double myBezierBlend = bezierBlend(p0.index, p1.index, p2.index, p3.index, theTime);
		return bezierValue(p0.value, p1.value, p2.value, p3.value, myBezierBlend);
	}
	
	@CCProperty(name = "reset")
	public void reset1(){
		reset();
		if(_mySequence.value() == null)return;
		float myUpdateTime = 1f / _mySequence.rate();
		
		for(int i = 0; i < _mySequence.value().size();i++){
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
	
	private CCSequence _myInterpolatedSequence;
	
	public CCSequence interpolation(){
		return _myInterpolatedSequence;
	}
	
	@CCProperty(name = "interpolate")
	public void applyInterpolation(){
		reset();
		if(_mySequence.value() == null)return;
		float myUpdateTime = 1f / _mySequence.rate();
		
		CCSequenceMapping<?> myMapping = _myElements.mappings().get(CCKleChannelType.MOTORS);
		_myInterpolatedSequence = _mySequence.value().clone();
		for(int c = 0; c < myMapping.columns();c++){
			for(int r = 0; r < myMapping.rows();r++){
				CCLog.info(c + " : " + r);
				for(int d = 0; d < myMapping.depth();d++){
				
					List<CCValueIndex> myValues = new ArrayList<>();
					myValues.add(new CCValueIndex(_myInterpolatedSequence.frame(0).data()[c][r][d],0));
					for(int i = 1; i < _myInterpolatedSequence.length() - 1;i++){
						double myData0 = _myInterpolatedSequence.frame(i - 1).data()[c][r][d];
						double myData1 = _myInterpolatedSequence.frame(i).data()[c][r][d];
						double myData2 = _myInterpolatedSequence.frame(i + 1).data()[c][r][d];
						
						double myDif0 = myData1 - myData0;
						double myDif1 = myData2 - myData1;
						if(CCMath.sign(myDif0) != CCMath.sign(myDif1)){
							if(c == 0 && r == 0)CCLog.info(myDif0 + " " + myDif1);
							myValues.add(new CCValueIndex(_myInterpolatedSequence.frame(i).data()[c][r][d],i));
						}
					}
					if(c == 0 && r == 0)CCLog.info(myValues.size());
					for(int i = 1; i < myValues.size(); i++){
						
						CCValueIndex myVal0 = i > 1 ? myValues.get(i - 2) : myValues.get(i - 1);
						CCValueIndex myVal1 = myValues.get(i - 1);
						CCValueIndex myVal2 = myValues.get(i);
						CCValueIndex myVal3 = i < myValues.size() - 1 ? myValues.get(i + 1) : myValues.get(i);
						
						int range0 = CCMath.min(myVal1.index - myVal0.index, myVal2.index - myVal1.index);
						int range1 = CCMath.min(myVal2.index - myVal1.index, myVal3.index - myVal2.index);
						
						CCLog.info(i + " : " +  myVal0.index + " : " +  myVal1.index + " : " +  myVal2.index + " : " +  myVal3.index + " : " +  range0 + " : " + range1);
						
						CCValueIndex mySVal0 = new CCValueIndex(myVal1.value, myVal1.index);
						CCValueIndex mySVal1 = new CCValueIndex(myVal1.value, myVal1.index + range0);
						CCValueIndex mySVal2 = new CCValueIndex(myVal2.value, myVal2.index - range1);
						CCValueIndex mySVal3 = new CCValueIndex(myVal2.value, myVal2.index);
						
						for(int j = mySVal0.index; j < mySVal3.index; j++){
//							double myBlend = CCMath.norm(j, myVal1.index, myVal2.index);
//							mySequence.frame(j).data()[c][r][d] = CCMath.blend(myVal1.value, myVal2.value, CCMath.smoothStep(0, 1, myBlend));
							//
							_myInterpolatedSequence.frame(j).data()[c][r][d] = sampleBezierSegment(mySVal0, mySVal1, mySVal2, mySVal3, j);

//							if(c == 0 && r == 0)CCLog.info(myBlend);
						}
					}
				}
			}
		}
		
		for(int i = 0; i < _myInterpolatedSequence.size();i++){
			CCMatrix2 myFrame = _myInterpolatedSequence.frame(i);
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
		
		CCLog.info("interpolated");
	}
	
	@CCProperty(name = "interpolate2")
	public void applyInterpolation2(){
		reset();
		if(_mySequence.value() == null)return;
		float myUpdateTime = 1f / _mySequence.rate();
		
		CCSequenceMapping<?> myMapping = _myElements.mappings().get(CCKleChannelType.MOTORS);
		_myInterpolatedSequence = _mySequence.value().clone();
	
		for(int c = 0; c < myMapping.columns();c++){
			for(int r = 0; r < myMapping.rows();r++){
				for(int d = 0; d < myMapping.depth();d++){
				
					List<CCValueIndex> myValues = new ArrayList<>();
					myValues.add(new CCValueIndex(_myInterpolatedSequence.frame(0).data()[c][r][d],0));
					for(int i = 1; i < _myInterpolatedSequence.length() - 1;i++){
						double myData0 = _myInterpolatedSequence.frame(i - 1).data()[c][r][d];
						double myData1 = _myInterpolatedSequence.frame(i).data()[c][r][d];
						double myData2 = _myInterpolatedSequence.frame(i + 1).data()[c][r][d];
						
						double myDif0 = myData1 - myData0;
						double myDif1 = myData2 - myData1;
						if(CCMath.sign(myDif0) != CCMath.sign(myDif1)){
							if(c == 0 && r == 0)CCLog.info(myDif0 + " " + myDif1);
							myValues.add(new CCValueIndex(_myInterpolatedSequence.frame(i).data()[c][r][d],i));
						}
					}
					if(c == 0 && r == 0)CCLog.info(myValues.size());
					for(int i = 1; i < myValues.size(); i++){
						
						CCValueIndex myVal1 = myValues.get(i - 1);
						CCValueIndex myVal2 = myValues.get(i);
						
						for(int j = myVal1.index; j < myVal2.index; j++){
							double myBlend = CCMath.norm(j, myVal1.index, myVal2.index);
							_myInterpolatedSequence.frame(j).data()[c][r][d] = CCMath.blend(myVal1.value, myVal2.value, CCMath.smoothStep(0, 1, myBlend));
							//

//							if(c == 0 && r == 0)CCLog.info(myBlend);
						}
					}
				}
			}
		}
		
		for(int i = 0; i < _myInterpolatedSequence.size();i++){
			CCMatrix2 myFrame = _myInterpolatedSequence.frame(i);
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
		
		CCLog.info("interpolated");
	}

	public void update(CCAnimator theAnimator){
		if(_mySequence == null)return;
		_mySequence.time(0, _cPosition * _mySequence.length(), 0);
		CCMatrix2 myFrame = _mySequence.frame();
		if(myFrame == null)return;
		for(CCSequenceElement myElement:_myElements){
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
