package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.io.CCFileInputChannel;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

@SuppressWarnings("rawtypes")
public class CCSequenceCCAFormat implements CCSequenceFormat{
	
	public CCSequenceCCAFormat(){
	}

	@Override
	public void save(Path thePath, CCSequenceMapping theMapping, CCSequence theSequence) {
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(thePath);
		fileChannel.write(theSequence.columns());

		for (CCMatrix2 frame : theSequence) {
			for (int c = 0; c < theSequence.columns(); c++) {
				fileChannel.write(frame.data()[c][0][5]);
				fileChannel.write(frame.data()[c][0][6]);
				fileChannel.write(frame.data()[c][0][7]);
			}
		}
		
		fileChannel.close();
	}
	
	@Override
	public void savePosition(Path thePath, CCSequenceElementRecording theRecording, boolean[] theSave) {
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(thePath);
		fileChannel.write(theRecording.columns());
		int channels = 0;
		for(boolean mySave:theSave){
			if(mySave) channels++;
		}
		fileChannel.write(channels);

		for (CCMatrix2 frame : theRecording) {
			for (int c = 0; c < theRecording.columns(); c++) {
				for(int i = 0; i < theSave.length;i++){
					if(theSave[i])fileChannel.write(frame.data()[c][0][i]);
				}
			}
		}
		
		fileChannel.close();
	}

	@Override
	public CCSequence load(Path thePath, CCSequenceMapping theMapping) {
		
		CCFileInputChannel fileChannel = new CCFileInputChannel(thePath);
		
		int mySequenceColumns = fileChannel.readInt();
				
		CCSequence result = new CCSequence(mySequenceColumns, 1, 3);
	
		while(!fileChannel.isFinished()){
			CCMatrix2 frame = new CCMatrix2(mySequenceColumns, 1, 3);
			for (int c = 0; c < mySequenceColumns; c++) {

				frame.data()[c][0][0] = fileChannel.readDouble();
				frame.data()[c][0][1] = fileChannel.readDouble();
				frame.data()[c][0][2] = fileChannel.readDouble();
			}
			result.add(frame);
		}
				
		return result;
	}
	
	@Override
	public String extension() {
		return "cca";
	}
}
