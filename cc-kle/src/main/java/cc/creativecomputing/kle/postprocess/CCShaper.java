package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCShaper implements CCSequenceProcessor {
	
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();

	@Override
	public void process(CCSequence theInput, CCSequenceMapping<?> theMapping, CCTriggerProgress theProcess) {
		if (theInput == null)
			return;


		int i = 0;
		for (CCMatrix2 myFrame : theInput) {
			double myScale = _cEnvelope.value(i / (double)theInput.length()) * 2;
			for (int c = 0; c < theMapping.columns(); c++) {
				for (int r = 0; r < theMapping.rows(); r++) {
					for (int d = 0; d < theMapping.depth(); d++) {
						myFrame.data()[c][r][d] = -(myFrame.data()[c][r][d]) *  myScale;
						
					}
				}
			}
			i++;
			theProcess.progress(i / (double)theInput.length());
		}
	}

}
