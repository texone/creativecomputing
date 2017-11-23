package cc.creativecomputing.kle.formats;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceElementRecording;
import cc.creativecomputing.math.CCMatrix2;

@SuppressWarnings("rawtypes")
public class CCKleTXTFormat implements CCKleFormat{
	
	public CCKleTXTFormat(){
	}

	@Override
	public void save(Path thePath, CCKleMapping theMapping, CCSequence theSequence) {
		
		CCNIOUtil.createDirectories(thePath);
		
		try{
			for (int c = 0; c < theSequence.columns(); c++) {
					
				BufferedWriter myWriter = Files.newBufferedWriter(thePath.resolve("e" + CCFormatUtil.nf(c, 3) + ".txt"));	
				for (CCMatrix2 frame : theSequence) {
					myWriter.write(
						frame.data()[c][0][0] + " " + 
						frame.data()[c][0][1] + " " + 
						frame.data()[c][0][2] + " " + 
						frame.data()[c][0][3] + " " + 
						frame.data()[c][0][4] + "\n"
					);
				}
				myWriter.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void savePosition(Path theFile, CCSequenceElementRecording theRecording, boolean[] theSave) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CCSequence load(Path thePath, CCKleMapping theMapping) {
		
//		nElements = theNElements;
//		dataEF = new CCVector2[nElements][];
//		
//		for (int i=0; i<nElements; i++) {
//			String frames[] = CCIOUtil.loadStrings(root+"/e"+CCFormatUtil.nf(i, 3)+".txt");
//			
//			nFrames = frames.length;
//			dataEF[i] = new CCVector2 [nFrames];
//			for (int j=0; j<nFrames; j++) {
//				String[] frame = frames[j].split(" ");
//				dataEF[i][j] = new CCVector2 (Float.parseFloat(frame[0]), Float.parseFloat(frame[1])*-1);
//			}
//		}
//		dataFE = new CCVector2 [nFrames][nElements];
//		
//		for (int e=0; e<nElements; e++) {
//			for (int f=0; f<nFrames; f++) {
//				dataFE[f][e] = dataEF[e][f];
//			}
//		}
		
		Path myImportPath = thePath.resolve(theMapping.type().id());
		CCSequence result = new CCSequence (theMapping.columns(), theMapping.rows(), theMapping.depth());
		
		for(Path myPath:CCNIOUtil.list(myImportPath, "csv")){
			String myFileName = myPath.getFileName().toString();
			int myColumn = Integer.parseInt(myFileName.substring(1, 4));
			int myRow = Integer.parseInt(myFileName.substring(6, 9));
			int myDepth = Integer.parseInt(myFileName.substring(11, 14));
			String[] arr = CCNIOUtil.loadString(myImportPath.resolve(myPath)).split("\\n");
					    
			for (int i = 0; i < arr.length;i++) {
				if(i >= result.size()){
					result.addEmptyFrame();
				}
				double a = Double.parseDouble(arr[i].split(",")[1]);
				result.frame(i).data()[myColumn][myRow][myDepth] = a;
			}
		}
			    
		return result;
	}
	
	@Override
	public String extension() {
		return null;
	}
	
	public static void main(String[] args) {
		Path myExportPath = CCNIOUtil.dataPath("export/csv2/motors");
		
		CCNIOUtil.createDirectories(myExportPath);
		
		try{
			for (int c = 0; c < 95; c++) {
				for (int r = 0; r < 2; r++) {
					for (int d = 0; d < 1; d++) {
					
						PrintWriter writer = new PrintWriter(
							myExportPath.resolve(
								"c" + CCFormatUtil.nf(c, 3) + 
								"_r" + CCFormatUtil.nf(r, 3) + 
								"_d" + CCFormatUtil.nf(d, 3) + ".csv"
							).toFile()
						);
						
						
						for (int i = 0; i < 1000; i++) {
							double data = (Math.sin(i / 500.0) + 1) * 5000 + 2000;
							writer.write(i + "," + data + "\n");
							i += 1;
						}
						writer.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
