package cc.creativecomputing.kle.formats;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.CCFileOutputChannel;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.math.CCMatrix2;

public class CCKleCSVBinFormat implements CCKleFormat{
	
	public CCKleCSVBinFormat(){
	}

	@Override
	public void save(Path thePath, CCKleMapping<?> theMapping, CCSequence theSequence) {
		Path myExportPath = thePath.resolve(theMapping.type().id());
		
		CCNIOUtil.createDirectories(myExportPath);
		
		try{
			int fileC = 0;
			for (int c = 0; c < theSequence.columns(); c++) {
				for (int r = 0; r < theSequence.rows(); r++) {
					for (int d = 0; d < theSequence.depth(); d++) {
						CCFileOutputChannel fileChannel = new CCFileOutputChannel(
//							myExportPath.resolve(
//								"c" + CCFormatUtil.nf(c, 3) + 
//								"_r" + CCFormatUtil.nf(r, 3) + 
//								"_d" + CCFormatUtil.nf(d, 3) + ".bcsv"
//							)
							myExportPath.resolve(CCFormatUtil.nf(fileC++, 3) + ".bcsv")
						);
						
						CCLog.info(c,r,d,fileC);
						
						int myFrame = 0;
						
						for (CCMatrix2 frame : theSequence) {
							double myValue = frame.data()[c][r][d];
							fileChannel.write((byte)0x22);
							byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myFrame).array();
							for(int i = 0; i < bytes.length;i++) {
								if(bytes[i] == 0x22) {
									fileChannel.write((byte)0x22);
								}
								fileChannel.write(bytes[i]);
							}
							fileChannel.write((byte)0x22);
							fileChannel.write((byte)0x3B);
							fileChannel.write((byte)0x22);
							bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(myValue).array();
							for(int i = 0; i < bytes.length;i++) {
								if(bytes[i] == 0x22) {
									fileChannel.write((byte)0x22);
								}
								fileChannel.write(bytes[i]);
							}
							fileChannel.write((byte)0x22);
							fileChannel.write((byte)0x0D);
							fileChannel.write((byte)0x0A);
							myFrame += 1;
						}
						fileChannel.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void savePosition(Path thePath, CCSequenceElementRecording theRecording, boolean[] theSave) {
		Path myExportPath = thePath.resolve("positions");
		
		CCNIOUtil.createDirectories(myExportPath);
		try{
			for (int c = 0; c < theRecording.columns(); c++) {
				BufferedWriter myWriter = Files.newBufferedWriter(myExportPath.resolve(
					"element" + CCFormatUtil.nf(c, 3) + ".csv"
				));
				
				int id = 0;
				
				for (CCMatrix2 frame : theRecording) {
					myWriter.write(id++ + "");
					for(int i = 0; i < theSave.length;i++){
						if(theSave[i]){
							myWriter.write("," + frame.data()[c][0][i]);
						}
					}
					myWriter.write("\n");
				}

				myWriter.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public CCSequence load(Path thePath, CCKleMapping<?> theMapping) {
		
		Path myImportPath = thePath.resolve(theMapping.type().id());
		CCSequence result = new CCSequence (theMapping.columns(), theMapping.rows(), theMapping.depth());
		
		for(Path myPath:CCNIOUtil.list(myImportPath, "bcsv")){
			String myFileName = myPath.getFileName().toString();
			int myColumn = Integer.parseInt(myFileName.substring(1, 4));
			int myRow = Integer.parseInt(myFileName.substring(6, 9));
			int myDepth = Integer.parseInt(myFileName.substring(11, 14));
			
			ByteBuffer myBuffer = CCNIOUtil.loadBytes(myPath);
			myBuffer.order(ByteOrder.LITTLE_ENDIAN);
			byte[] myBytes = new byte[10];	
			int myCounter = 0;
			int myFrame = 0;
			boolean myFoundMark = false;
			while(myBuffer.hasRemaining()) {
				byte myByte = myBuffer.get();
				
				if(myByte == 0x22) {
					if(myFoundMark) {
						myBytes[myCounter] = myByte;
						myCounter++;
						myFoundMark = false;
						continue;
					}
					myFoundMark = true;
					
				}else {
					if(myFoundMark) {
						myFoundMark = false;
						if(myCounter == 1) {
						}else if(myCounter == 2) {
						}else if(myCounter == 4) {
							myFrame = ByteBuffer.wrap(myBytes,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
							CCLog.info(myFrame);
						}else if(myCounter == 8) {
							double myValue = ByteBuffer.wrap(myBytes,0,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
							result.frame(myFrame).data()[myColumn][myRow][myDepth] = myValue;
						}
						myCounter = 0;
					}
					myBytes[myCounter] = myByte;
					myCounter++;
				}
			}
		}
			    
		return result;
	}
	
	@Override
	public String extension() {
		return null;
	}
	
	public static void main(String[] args) {
//		CCFileOutputChannel fileChannel = new CCFileOutputChannel(CCNIOUtil.dataPath("format_test/bin_csv/001.csv"));
//		fileChannel.order(ByteOrder.LITTLE_ENDIAN);
//		for(String myString:CCNIOUtil.loadStrings(CCNIOUtil.dataPath("format_test/bin_csv/Motor_1.csv"))) {
//			String[] myData = myString.split(Pattern.quote(","));
//			int myFrame = Integer.parseInt(myData[0]);
//			double myValue = Double.parseDouble(myData[1]);
//			fileChannel.write((byte)0x22);
//			byte[] bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(myFrame).array();
//			for(int i = 0; i < bytes.length;i++) {
//				if(bytes[i] == 0x22) {
//					fileChannel.write((byte)0x22);
//				}
//				fileChannel.write(bytes[i]);
//			}
//			fileChannel.write((byte)0x22);
//			fileChannel.write((byte)0x3B);
//			fileChannel.write((byte)0x22);
//			bytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(myValue).array();
//			for(int i = 0; i < bytes.length;i++) {
//				if(bytes[i] == 0x22) {
//					fileChannel.write((byte)0x22);
//				}
//				fileChannel.write(bytes[i]);
//			}
//			fileChannel.write((byte)0x22);
//			fileChannel.write((byte)0x0D);
//			fileChannel.write((byte)0x0A);
//			CCLog.info(myFrame,myValue);
//		}
//		
//		
		ByteBuffer myBuffer = CCNIOUtil.loadBytes(CCNIOUtil.dataPath("format_test/bin_csv/000.csv"));
		myBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byte[] myBytes = new byte[10];	
		int myCounter = 0;
		boolean myFoundMark = false;
		StringBuffer myBuffer2 = new StringBuffer();
		while(myBuffer.hasRemaining()) {
			byte myByte = myBuffer.get();
			
			if(myByte == 0x22) {
				if(myFoundMark) {
					myBytes[myCounter] = myByte;
					myCounter++;
					myFoundMark = false;
					continue;
				}
				myFoundMark = true;
				
			}else {
				if(myFoundMark) {
					myFoundMark = false;
					if(myCounter == 1) {
//						CCLog.info(myBytes[0], ';' + 0);
					}else if(myCounter == 2) {
//						CCLog.info(Integer.toHexString(ByteBuffer.wrap(myBytes, 0, 2).getShort()));
						CCLog.info(myBuffer2.toString());
						myBuffer2 = new StringBuffer();
					}else if(myCounter == 4) {
						int myFrame = ByteBuffer.wrap(myBytes,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
						myBuffer2.append(myFrame);
						myBuffer2.append(";");
					}else if(myCounter == 8) {
						double myValue = ByteBuffer.wrap(myBytes,0,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
						myBuffer2.append(myValue);
					}
					myCounter = 0;
				}
				myBytes[myCounter] = myByte;
				myCounter++;
			}
		}
		CCLog.info(myBuffer2.toString());
//		CCLog.info(0x122);
	
	}
}
