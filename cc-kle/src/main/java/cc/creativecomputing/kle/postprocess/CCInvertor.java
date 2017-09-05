package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCInvertor implements CCSequenceProcessor {

	@Override
	public void process(CCSequence theInput, CCSequenceMapping<?> theMapping, CCTriggerProgress theProcess) {
		if (theInput == null)
			return;


		int i = 0;
		for (CCMatrix2 myFrame : theInput) {
			for (int c = 0; c < theMapping.columns(); c++) {
				for (int r = 0; r < theMapping.rows(); r++) {
					for (int d = 0; d < theMapping.depth(); d++) {
						if(c == 0 && r == 0 && d == 0){
							CCLog.info(myFrame.data()[c][r][d]);
						}
						double myCenter = (theMapping.max(c, r, d) + theMapping.min(c, r, d)) / 2;
						myFrame.data()[c][r][d] = -(myFrame.data()[c][r][d] - myCenter) + myCenter;
						if(c == 0 && r == 0 && d == 0){
							CCLog.info("-> " +myFrame.data()[c][r][d]);
						}
					}
				}
			}
			i++;
			theProcess.progress(i / (double)theInput.length());
		}
	}

}
