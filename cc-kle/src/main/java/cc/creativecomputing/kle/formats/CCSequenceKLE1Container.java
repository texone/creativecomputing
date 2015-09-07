package cc.creativecomputing.kle.formats;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCVector3;

public class CCSequenceKLE1Container extends CCSequencesContainer{
	private CCSequencePNGFormat _myPNGFormat;
	private CCSequenceBinFormat _myBINFormat;
	
	private boolean _myUseStartEndChannel = false;
	private int _myStartChannel = 0;
	private int _myEndChannel = 0;
	
	public CCSequenceKLE1Container(){
		_myPNGFormat = new CCSequencePNGFormat(false);
		_myBINFormat = new CCSequenceBinFormat();
	}
	
	public void useStartEndChannels(boolean theUseStartEndChannel){
		_myUseStartEndChannel = theUseStartEndChannel;
	}
	
	public void startChannel(int theStartChannel){
		_myStartChannel = theStartChannel;
	}
	
	public void endChannel(int theEndChannel){
		_myEndChannel = theEndChannel;
	}
	

	@Override
	public void save(Path thePath, CCSequenceElements theElements, Map<String, CCSequence> theSequences) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes);) {
	        Path myFramesFolder = fs.getPath("frames");
	        
	        for(CCKleChannelType myKey:theElements.mappings().keySet()){
	        	if(myKey != CCKleChannelType.MOTORS)continue;
	        	
	        	CCSequenceMapping myMapping = theElements.mappings().get(myKey);
	        	CCSequence mySequence = theSequences.get(myKey);

				CCNIOUtil.createDirectories(myFramesFolder);
				
				_myPNGFormat.save(myFramesFolder, myMapping, mySequence);
				_myBINFormat.save(myFramesFolder.resolve(myMapping.type().id() + ".bin"), myMapping, mySequence);
	        }
	
	        Path myMetaInfFolder = fs.getPath("META-INF");
			CCNIOUtil.createDirectories(myMetaInfFolder);
			CCXMLElement myMappingXML = new CCXMLElement("mapping");
			for(CCKleChannelType myKey:theElements.mappings().keySet()){
				if(myKey != CCKleChannelType.MOTORS)continue;
				CCSequenceMapping<?> myMapping = theElements.mappings().get(myKey);
				for(CCSequenceChannel myChannel:myMapping){
					if(_myUseStartEndChannel && (myChannel.id() < _myStartChannel || myChannel.id() > _myEndChannel))continue;
					myMappingXML.addChild(myChannel.mappingXML());
				}
			}
			CCLog.info(myMetaInfFolder.resolve("setup.xml"));
			
			CCXMLIO.saveXMLElement(myMappingXML, myMetaInfFolder.resolve("setup.xml"));
			
			CCXMLElement mySculptureXML = new CCXMLElement("sculpture");
			CCXMLElement myElementsXML = mySculptureXML.createChild("elements");
			
			for(CCSequenceElement myElement:theElements){
				CCXMLElement myElementXML = myElementsXML.createChild("element");
				myElementXML.addAttribute("id", myElement.id());
				CCXMLElement myMotorsXML = myElementXML.createChild("motors");
				for(CCMotorChannel myChannel:myElement.motorSetup().channels()){
					if(_myUseStartEndChannel && (myChannel.id() < _myStartChannel || myChannel.id() > _myEndChannel))continue;
					CCXMLElement myMotorXML = myMotorsXML.createChild("motor");
					myMotorXML.addAttribute("id", myChannel.id());
					myMotorXML.addAttribute("x", myChannel.position().x);
					myMotorXML.addAttribute("y", myChannel.position().y);
					myMotorXML.addAttribute("z", myChannel.position().z);
				}
				CCXMLElement myBoundsXML = myElementXML.createChild("bounds");
				for(CCVector3 myPoint:myElement.motorSetup().bounds()){
					addPoint(myBoundsXML, myPoint);
				}
			}
			
			CCXMLIO.saveXMLElement(mySculptureXML, myMetaInfFolder.resolve("sculpture.xml"));
        }catch(Exception e){
        	
        }
	}
	
	private void addPoint(CCXMLElement theParentXML, CCVector3 thePoint){
		CCXMLElement myPointXML = theParentXML.createChild("point");
		myPointXML.addAttribute("x", thePoint.x);
		myPointXML.addAttribute("y", thePoint.y);
		myPointXML.addAttribute("z", thePoint.z);
	}

	@Override
	public Map<CCKleChannelType,CCSequence> load(Path thePath, Map<CCKleChannelType, CCSequenceMapping<?>> theMappings) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());
        
        CCLog.info(zipFile);
        
        Map<CCKleChannelType,CCSequence> myResult = new HashMap<CCKleChannelType, CCSequence>();

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes);) {
        	Path myFramesFolder = fs.getPath("frames");
        	
        	CCLog.info(myFramesFolder);
	        
	        for(CCKleChannelType myKey:theMappings.keySet()){
	        	if(myKey != CCKleChannelType.MOTORS)continue;
	        	
	        	CCSequenceMapping myMapping = theMappings.get(myKey);
				
				CCSequence mySequence = _myPNGFormat.load(myFramesFolder, myMapping);
	        	CCLog.info("LOAD:" + mySequence.columns() + ":" +mySequence.rows() + ":" +mySequence.depth() + ":" +mySequence.size());
				myResult.put(myKey, mySequence);
				
				for(int i = 0; i < mySequence.size();i++){
//					CCLog.info(mySequence.get(i).data()[0][0][0]);
				}
	        }
        }catch(Exception e){
        	
        }
		
		return myResult;
	}
	
	public static void main(String[] args) {
		CCSequenceKLE1Container myContainer = new CCSequenceKLE1Container();
		CCSequenceElements myElements = new CCSequenceElements(
				CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/mapping.xml")), 
				CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/sculpture.xml")), new CC2Motor1ConnectionBounds(), 1);
		
		Map<CCKleChannelType, CCSequence> mySequenceMap = myContainer.load(CCNIOUtil.dataPath("141128_CoD_Synchronicity_Choreography.kle"), myElements.mappings());
		
		CCSequenceCSVFormat myCsvFormat = new CCSequenceCSVFormat();
		myCsvFormat.save(CCNIOUtil.dataPath("manila_csv"), myElements.mappings().get(CCKleChannelType.MOTORS), mySequenceMap.get(CCKleChannelType.MOTORS));
	}
	
	public static void main2(String[] args) throws IOException {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + CCNIOUtil.dataPath("bla4.zip").toUri().getPath());
       
        FileSystem fs;
        
        try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes);) {
        	Path path = zipFileSys.getPath("docer");
        	Files.write( zipFileSys.getPath( "/j1.txt" ), "The truth is out there. Anybody got the URL?".getBytes() );
        	Files.write( zipFileSys.getPath( "/j2.txt" ), "The more I C, the less I see.".getBytes() );
        	CCNIOUtil.createDirectories(path);
        }
	}
	public static void main1(String[] args) throws Exception {
	    Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
	    
	    URI zipFile = URI.create("jar:file:" + CCNIOUtil.dataPath("bla.zip").toUri().getPath());
	    try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes);) {
	      Path path = zipFileSys.getPath("docs2");
	      Files.createDirectory(path);
	      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(zipFileSys.getPath("/"));) {
	        for (Path file : directoryStream) {
	          System.out.println(file.getFileName());
	        }
	      }
	    }
	  }
}
