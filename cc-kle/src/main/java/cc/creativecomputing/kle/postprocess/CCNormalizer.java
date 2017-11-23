package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCNormalizer implements CCSequenceProcessor{

	@Override
	public void process(CCSequence theInput, CCKleMapping<?> theMapping, CCTriggerProgress theProcess) {
		if (theInput == null)
			return;

		int i = 0;
		for (CCMatrix2 myFrame : theInput) {
			for (int c = 0; c < theMapping.columns(); c++) {
				for (int r = 0; r < theMapping.rows(); r++) {
					for (int d = 0; d < theMapping.depth(); d++) {
						myFrame.data()[c][r][d] = CCMath.norm(myFrame.data()[c][r][d], 0, theMapping.max(c, r, d) - theMapping.min(c, r, d));
					}
				}
			}
			i++;
			theProcess.progress(i / (double) theInput.length());
		}
	}

}
