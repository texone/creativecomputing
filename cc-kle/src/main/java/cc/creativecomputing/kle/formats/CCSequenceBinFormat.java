package cc.creativecomputing.kle.formats;

import java.nio.file.Path;

import cc.creativecomputing.io.CCFileInputChannel;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.kle.CCSequence;
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
	}

	@Override
	public CCSequence load(Path thePath, CCSequenceMapping<?> theMapping) {
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
}
