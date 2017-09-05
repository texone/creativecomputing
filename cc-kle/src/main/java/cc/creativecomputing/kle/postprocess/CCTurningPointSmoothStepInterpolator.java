package cc.creativecomputing.kle.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMath;

public class CCTurningPointSmoothStepInterpolator implements CCSequenceProcessor{

	@Override
	public void process(CCSequence theInput, CCSequenceMapping<?> theMapping, CCTriggerProgress theProgress) {
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
						
						CCValueIndex myVal1 = myValues.get(i - 1);
						CCValueIndex myVal2 = myValues.get(i);
						
						for(int j = myVal1.index; j < myVal2.index; j++){
							double myBlend = CCMath.norm(j, myVal1.index, myVal2.index);
							theInput.frame(j).data()[c][r][d] = CCMath.blend(myVal1.value, myVal2.value, CCMath.smoothStep(0, 1, myBlend));
							//

//							if(c == 0 && r == 0)CCLog.info(myBlend);
						}
					}
					theProgress.progress(counter / (double)size);
					counter++;
				}
			}
		}
	}

}
