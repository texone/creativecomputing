package cc.creativecomputing.kle.formats;

import java.nio.file.Files;
import java.nio.file.Path;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

@SuppressWarnings("rawtypes")
public class CCFormatConverter {

	public final static void convert(
		CCSequenceMapping theMapping,
		Path theInputPath, CCSequenceFormat theInputFormat,
		Path theOutputPath, CCSequenceFormat theOutputFormat
	){
		CCSequence mySequence = theInputFormat.load(theInputPath, theMapping);
		theOutputFormat.save(theOutputPath, theMapping, mySequence);
	}
	
	public final static void convertFolder(
		CCSequenceMapping theMapping,
		Path theInputFolder, CCSequenceFormat theInputFormat,
		Path theOutputFolder, CCSequenceFormat theOutputFormat
	){
		
		CCNIOUtil.createDirectories(theOutputFolder);
		for(Path myPathString:CCNIOUtil.list(theInputFolder)){
			try{
			Path myPath = theInputFolder.resolve(myPathString);
			if(Files.isDirectory(myPath)){
				convertFolder(
					theMapping,
					theInputFolder.resolve(myPathString), theInputFormat, 
					theOutputFolder.resolve(myPathString), theOutputFormat
				);
			}else{
				Path myOutPath = myPathString;
				if(myOutPath.endsWith(".bin") || myOutPath.endsWith(".kle"))myOutPath = CCNIOUtil.filePath(myOutPath);
				convert(
					theMapping,
					theInputFolder.resolve(myPathString), theInputFormat, 
					theOutputFolder.resolve(myOutPath), theOutputFormat
				);
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
//		CCSequenceFormat myInFormat = new CCSequenceBinFormat(175,2);
//		CCSequenceFormat myOutFormat = new CCSequenceKLEFormat(
//			CCNIOUtil.dataPath("manila_setup_V2.xml"),
//			CCNIOUtil.dataPath("manila_sculpture_V2.xml")
//		);
//		convertFolder(
//			CCNIOUtil.dataPath("max"), myInFormat,
//			CCNIOUtil.dataPath("max_kle"), myOutFormat
//		);
//		for(String mySequenceFolder:CCIOUtil.list("pngs")){
//			convert(
//				CCIOUtil.dataPath("pngs" + Path.separator + mySequenceFolder + Path.separator + "frames"), new CCSequencePNGFormat(CCIOUtil.dataPath("manila_setup_V2.xml")),
//				CCIOUtil.dataPath("bins" + Path.separator + mySequenceFolder + ".bin"), new CCSequenceBinFormat(175,2)
//			);
//		}
	}
	
}
