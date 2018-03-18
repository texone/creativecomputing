package cc.creativecomputing.kle.formats;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.CCKleSegment;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.kle.sequence.CCSequenceRecorder.CCSequenceChannelRecording;

@SuppressWarnings("rawtypes")
public class CCKleV2Container extends CCKleContainerFormat{
	private CCKlePNGFormat _myPNGFormat;
	private CCKleBinFormat _myBINFormat;
	private CCKleCSVFormat _myCSVFormat;
	
	public CCKleV2Container(){
		_myPNGFormat = new CCKlePNGFormat();
		_myBINFormat = new CCKleBinFormat();
		_myCSVFormat = new CCKleCSVFormat();
	}
	
	/*
	 * <segments>
	<segment id="segment0" desc="stroke sequence" start="0" end="600000"/>
	<segment id="segment1" desc="stroke morph" start="600000" end="1200000"/>
	<segment id="segment3" desc="waves sequence" start="120000" end="1800000"/>
	<segment id="segment4" desc="center low" start="200000" end="200000"/>
</ segments >

	 */
	private void saveSegments(List<CCKleSegment> theSegments, Path thePath){
		if(theSegments == null || theSegments.size() <= 0)return;
		
		CCDataElement mySegmentsXML = new CCDataElement("segments");
		int id = 0;
		for(CCKleSegment mySegment:theSegments){
			CCDataElement mySegmentXML = mySegmentsXML.createChild("segment");
			mySegmentXML.addAttribute("id", id);
			mySegmentXML.addAttribute("desc", mySegment.name);
			mySegmentXML.addAttribute("start", (int)(mySegment.startTime * 1000));
			mySegmentXML.addAttribute("end", (int)(mySegment.endTime * 1000));
			id++;
		}
		CCXMLIO.saveXMLElement(mySegmentsXML, thePath);
	}
	
	public static void main(String[] args) {
		Path myPath = Paths.get("/Users/christianr/Dropbox/mkt tests/circles_new.kle");
		System.out.println(myPath.toUri());
		System.out.println(myPath.toUri().getPath());
	}
	
	public void save(Path thePath, CCKleEffectables theElements, Map<CCKleChannelType, CCSequence> theSequences, List<CCKleSegment> theSegments) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:" + thePath.toUri());

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes)) {
        	Path myFramesFolder = fs.getPath("frames");
        	Path myCsvFolder = fs.getPath("csv");
			CCNIOUtil.createDirectories(myFramesFolder);
			
        	for(CCKleChannelType myKey:theElements.mappings().keySet()){
	        	
	        	CCKleMapping myMapping = theElements.mappings().get(myKey);
	        	CCSequence mySequence = theSequences.get(myKey);
	        	if(mySequence instanceof CCSequenceChannelRecording){
	        		if(!((CCSequenceChannelRecording)mySequence).export)continue;
	        	}
		        
				_myPNGFormat.save(myFramesFolder, myMapping, mySequence);
				_myCSVFormat.save(myCsvFolder, myMapping, mySequence);
				_myBINFormat.save(CCNIOUtil.addExtension(myFramesFolder.resolve(myMapping.type().id()), "bin"), myMapping, mySequence);
	        }
	
	        Path myMetaInfFolder = fs.getPath("META-INF");
			CCNIOUtil.createDirectories(myMetaInfFolder);
			CCXMLIO.saveXMLElement(CCKleEffectables.mapping(theElements), myMetaInfFolder.resolve("mapping.xml"));
			CCXMLIO.saveXMLElement(CCKleEffectables.sculpture(theElements), myMetaInfFolder.resolve("sculpture.xml"));
			saveSegments(theSegments, myMetaInfFolder.resolve("segments.xml"));
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	@Override
	public void save(Path thePath, CCKleEffectables theElements, Map<CCKleChannelType, CCSequence> theSequences) {
		save(thePath, theElements, theSequences, null);
	}

	@Override
	public Map<CCKleChannelType,CCSequence> load(Path thePath, Map<CCKleChannelType, CCKleMapping<?>> theMappings) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());
        
        Map<CCKleChannelType,CCSequence> myResult = new HashMap<CCKleChannelType, CCSequence>();

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes)) {
        	Path myFramesFolder = fs.getPath("frames");
	        
	        for(CCKleChannelType myKey:theMappings.keySet()){
	        	
	        	CCKleMapping myMapping = theMappings.get(myKey);
				
	        	CCSequence mySequence = _myBINFormat.load(myFramesFolder.resolve(myMapping.type().id()+".bin"), myMapping);
				myResult.put(myKey, mySequence);
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
		
		return myResult;
	}
	
}
