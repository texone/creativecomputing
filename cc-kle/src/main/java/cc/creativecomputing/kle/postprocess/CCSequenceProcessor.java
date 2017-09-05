package cc.creativecomputing.kle.postprocess;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public interface CCSequenceProcessor {

	public void process(CCSequence theInput, CCSequenceMapping<?> theMapping, CCTriggerProgress theProcess);
}
