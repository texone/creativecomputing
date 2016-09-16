package cc.creativecomputing.kle.formats;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.math.CCMatrix2;

public class CCSequenceCSVFormat implements CCSequenceFormat{
	
	public CCSequenceCSVFormat(){
	}

	@Override
	public void save(Path thePath, CCSequenceMapping<?> theMapping, CCSequence theSequence) {
		Path myExportPath = thePath.resolve(theMapping.type().id());
		
		CCNIOUtil.createDirectories(myExportPath);
		
		try{
			for (int c = 0; c < theSequence.columns(); c++) {
				for (int r = 0; r < theSequence.rows(); r++) {
					for (int d = 0; d < theSequence.depth(); d++) {
					
						BufferedWriter myWriter = Files.newBufferedWriter(myExportPath.resolve(
								"c" + CCFormatUtil.nf(c, 3) + 
								"_r" + CCFormatUtil.nf(r, 3) + 
								"_d" + CCFormatUtil.nf(d, 3) + ".csv"
							));
						
						
						int i = 0;
						
						for (CCMatrix2 frame : theSequence) {
							double data = frame.data()[c][r][d];
							myWriter.write(i + "," + data + "\n");
							i += 1;
						}
						myWriter.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public CCSequence load(Path thePath, CCSequenceMapping<?> theMapping) {
		
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
