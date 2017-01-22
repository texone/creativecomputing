package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public interface CCSequenceFormat {
	
	public void save(Path theFile, CCSequenceMapping<?> theMapping, CCSequence theSequence);
	
	public void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave);

	public CCSequence load(Path theFile, CCSequenceMapping<?> theMapping);
	
	public String extension();
}
