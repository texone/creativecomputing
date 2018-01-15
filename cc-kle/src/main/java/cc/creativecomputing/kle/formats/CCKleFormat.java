package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;

public interface CCKleFormat {
	
	void save(Path theFile, CCKleMapping<?> theMapping, CCSequence theSequence);
	
	void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave);

	CCSequence load(Path theFile, CCKleMapping<?> theMapping);
	
	String extension();
}
