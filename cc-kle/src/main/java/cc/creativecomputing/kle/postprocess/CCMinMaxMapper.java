package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCMinMaxMapper implements CCSequenceProcessor{
	
	@CCProperty(name = "invert")
	private boolean _cInvert = false;

	@Override
	public void process(CCSequence theInput, CCSequenceMapping<?> theMapping, CCTriggerProgress theProcess) {
		if(theInput == null)return ;
		
	
		int i = 0;
		for(CCMatrix2 myFrame:theInput){
			for(int c = 0; c < theMapping.columns();c++){
				for(int r = 0; r < theMapping.rows();r++){
					for(int d = 0; d < theMapping.depth();d++){
						if(_cInvert){
							myFrame.data()[c][r][d]= CCMath.blend(theMapping.max(c, r, d), theMapping.min(c, r, d), myFrame.data()[c][r][d]);
						}else{
							myFrame.data()[c][r][d]= CCMath.blend(theMapping.min(c, r, d), theMapping.max(c, r, d), myFrame.data()[c][r][d]);
						}
					}
				}
			}
			i++;
			theProcess.progress(i / (double)theInput.length());
		}
	}

}
