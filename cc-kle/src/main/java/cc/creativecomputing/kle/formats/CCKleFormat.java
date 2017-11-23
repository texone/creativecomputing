package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;

public interface CCKleFormat {
	
	public void save(Path theFile, CCKleMapping<?> theMapping, CCSequence theSequence);
	
	public void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave);

	public CCSequence load(Path theFile, CCKleMapping<?> theMapping);
	
	public String extension();
}
