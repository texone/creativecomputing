package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;

public interface CCSequenceProcessor {

	public void process(CCSequence theInput, CCKleMapping<?> theMapping, CCTriggerProgress theProcess);
}
