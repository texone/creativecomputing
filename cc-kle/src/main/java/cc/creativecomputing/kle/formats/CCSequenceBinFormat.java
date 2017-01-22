package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.io.CCFileInputChannel;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCSequenceBinFormat implements CCSequenceFormat {
	
	public CCSequenceBinFormat(){
	}

	@Override
	public void save(Path thePath, CCSequenceMapping<?> theMapping, CCSequence theSequence) {
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(thePath);

		for (CCMatrix2 frame : theSequence) {
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					for (int d = 0; d < frame.depth(); d++) {
						fileChannel.write(frame.data()[c][r][d]);
					}
				}
			}
		}
		
		fileChannel.close();
	}
	
	@Override
	public void savePosition(Path thePath, CCSequenceElementRecording theRecording, boolean[] theSave) {
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(thePath);

		for (CCMatrix2 frame : theRecording) {
			for (int c = 0; c < theRecording.columns(); c++) {
				for(int i = 0; i < theSave.length;i++){
					if(theSave[i])fileChannel.write(frame.data()[c][0][i]);
				}
			}
		}
		
		fileChannel.close();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public CCSequence load(Path thePath, CCSequenceMapping theMapping) {
		CCFileInputChannel fileChannel = new CCFileInputChannel(thePath);
		
		float numberOfFrames = (float)fileChannel.size() / theMapping.columns() / theMapping.rows() / theMapping.depth() / 8;
				
		CCSequence result = new CCSequence(theMapping.columns(), theMapping.rows(), theMapping.depth());
	
		if(numberOfFrames - (int)numberOfFrames > 0)return result;
				
		for (int i = 0; i < numberOfFrames; i++) {
			CCMatrix2 frame = new CCMatrix2(theMapping.columns(), theMapping.rows(), theMapping.depth());
			for (int c = 0; c < theMapping.columns(); c++) {
				for (int r = 0; r < theMapping.rows(); r++) {
					for (int d = 0; d < theMapping.depth(); d++) {
						frame.data()[c][r][d] = fileChannel.readDouble();
					}
				}
			}
			result.add(frame);
		}
				
		return result;
	}
	
	@Override
	public String extension() {
		return "bin";
	}

	public static void main(String[] args) {
		CCFileInputChannel fileChannel = new CCFileInputChannel(CCNIOUtil.dataPath("lights7.bin"));
		CCFileInputChannel fileChannel2 = new CCFileInputChannel(CCNIOUtil.dataPath("lights7.bin"));
		
		int columns = 96;
		int rows = 1;
		int depth = 4;
		float numberOfFrames = (float)fileChannel.size() / columns / rows / depth / 8;
				
		CCSequence result = new CCSequence(columns, rows, depth);
//	
//		if(numberOfFrames - (int)numberOfFrames > 0)return result;
				
		for (int i = 0; i < numberOfFrames; i++) {
			System.out.println(i);
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < columns; c++) {
				for (int r = 0; r < rows; r++) {
					long[] myVals = new long[4];
					for (int d = 0; d < depth; d++) {
						frame.data()[c][r][d] = fileChannel.readDouble();
						myVals[d] = fileChannel2.readLong();
					}
					if(c == 10)
						System.out.println(
							Long.toHexString(myVals[0]) + " : "+frame.data()[c][r][0] + " : "+
									Long.toHexString(myVals[1]) + " : "+frame.data()[c][r][1] + " : "+
									Long.toHexString(myVals[2]) + " : "+frame.data()[c][r][2] + " : "+
									Long.toHexString(myVals[3]) + " : "+frame.data()[c][r][3]);
				}
			}
			result.add(frame);
		}
				
	}
}
