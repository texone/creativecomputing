package cc.creativecomputing.kle.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCCubicSolver;

public class CCTurningPointBezierInterpolator implements CCSequenceProcessor{
	
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

		double[] myResult = CCCubicSolver.solveCubic(a, b, c, d);
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
	
	@Override
	public void process(CCSequence theInput, CCKleMapping<?> theMapping, CCTriggerProgress theProcess) {
		if(theInput == null)return;
		
		int size = theMapping.columns() * theMapping.rows() * theMapping.depth();
		int counter = 0;
		for(int c = 0; c < theMapping.columns();c++){
			for(int r = 0; r < theMapping.rows();r++){
				for(int d = 0; d < theMapping.depth();d++){
				
					List<CCValueIndex> myValues = new ArrayList<>();
					myValues.add(new CCValueIndex(theInput.frame(0).data()[c][r][d],0));
					for(int i = 1; i < theInput.length() - 1;i++){
						double myData0 = theInput.frame(i - 1).data()[c][r][d];
						double myData1 = theInput.frame(i).data()[c][r][d];
						double myData2 = theInput.frame(i + 1).data()[c][r][d];
						
						double myDif0 = myData1 - myData0;
						double myDif1 = myData2 - myData1;
						if(CCMath.sign(myDif0) != CCMath.sign(myDif1)){
							if(c == 0 && r == 0)CCLog.info(myDif0 + " " + myDif1);
							myValues.add(new CCValueIndex(theInput.frame(i).data()[c][r][d],i));
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
							theInput.frame(j).data()[c][r][d] = sampleBezierSegment(mySVal0, mySVal1, mySVal2, mySVal3, j);

//							if(c == 0 && r == 0)CCLog.info(myBlend);
						}
					}
					theProcess.progress(counter / (double)size);
					counter++;
				}
			}
		}
	}

}
