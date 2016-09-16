package cc.creativecomputing.demo.io;

import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCFileInputChannel;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCFileChannelDemo {
	
	public static void main(String[] args) {
		CCFileOutputChannel myOutChannel = new CCFileOutputChannel(CCNIOUtil.dataPath("doubleout.raw"));
		for(double i = 0; i < 100;i++){
			myOutChannel.write(i * 0.0001);
			myOutChannel.write(0.0);
		}
		myOutChannel.close();
		
		CCFileInputChannel myInChannel = new CCFileInputChannel(CCNIOUtil.dataPath("doubleout.raw"));
		CCFileInputChannel myInChannel2 = new CCFileInputChannel(CCNIOUtil.dataPath("doubleout.raw"));
		for(double i = 0; i < 100;i++){
			System.out.println(Long.toHexString(myInChannel.readLong()) + " : "+myInChannel2.readDouble() + " : ");
		}
		
		int columns = 96;
		int rows = 1;
		int depth = 4;
		
		CCSequence result = new CCSequence(columns, rows, depth);
		
		
		for(double i = 0; i < 150;i++){
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					frame.data()[c][r][0] = i / 150;
					frame.data()[c][r][1] = 0;
					frame.data()[c][r][2] = 0;
					frame.data()[c][r][3] = 0;
				}
			}
			result.add(frame);
		}
		
		for(double i = 0; i < 150;i++){
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					frame.data()[c][r][0] = 0;
					frame.data()[c][r][1] = i / 150;
					frame.data()[c][r][2] = 0;
					frame.data()[c][r][3] = 0;
				}
			}
			result.add(frame);
		}
		
		for(double i = 0; i < 150;i++){
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					frame.data()[c][r][0] = 0;
					frame.data()[c][r][1] = 0;
					frame.data()[c][r][2] = i / 150;
					frame.data()[c][r][3] = 0;
				}
			}
			result.add(frame);
		}
		
		for(double i = 0; i < 150;i++){
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					frame.data()[c][r][0] = 0;
					frame.data()[c][r][1] = 0;
					frame.data()[c][r][2] = 0;
					frame.data()[c][r][3] = i / 150;
				}
			}
			result.add(frame);
		}
		
		CCFileOutputChannel fileChannel = new CCFileOutputChannel(CCNIOUtil.dataPath("matrix.raw"));

		for (CCMatrix2 frame : result) {
			for (int c = 0; c < frame.columns(); c++) {
				for (int r = 0; r < frame.rows(); r++) {
					for (int d = 0; d < frame.depth(); d++) {
						if(c == 0 && d == 0)CCLog.info(frame.data()[c][r][d]);
						fileChannel.write(frame.data()[c][r][d]);
					}
				}
			}
		}
			
		fileChannel.close();
		
		CCFileInputChannel fileInChannel = new CCFileInputChannel(CCNIOUtil.dataPath("matrix.raw"));
		CCFileInputChannel fileInChannel2 = new CCFileInputChannel(CCNIOUtil.dataPath("matrix.raw"));
			
		float numberOfFrames = (float)fileInChannel.size() / columns / rows / depth / 8;
		long[] myVals = new long[4];
		for (int i = 0; i < numberOfFrames; i++) {
			CCMatrix2 frame = new CCMatrix2(columns, rows, depth);
			for (int c = 0; c < columns; c++) {
				for (int r = 0; r < rows; r++) {
					for (int d = 0; d < depth; d++) {
						frame.data()[c][r][d] = fileInChannel.readDouble();
						myVals[d] = fileInChannel2.readLong();
					}
					
					if(c == 0)System.out.println(
						Long.toHexString(myVals[0]) + " : "+frame.data()[c][r][0] + " : "+
						Long.toHexString(myVals[1]) + " : "+frame.data()[c][r][1] + " : "+
						Long.toHexString(myVals[2]) + " : "+frame.data()[c][r][2] + " : "+
						Long.toHexString(myVals[3]) + " : "+frame.data()[c][r][3]
					);
				}
			}
			result.add(frame);
		}
	}
}
