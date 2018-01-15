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
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;
import cc.creativecomputing.math.CCVector3;

@SuppressWarnings("rawtypes")
public class CCKleV1Container extends CCKleContainerFormat{
	private CCKlePNGFormat _myPNGFormat;
	private CCKleBinFormat _myBINFormat;
	
	private boolean _myUseStartEndChannel = false;
	private int _myStartChannel = 0;
	private int _myEndChannel = 0;
	
	public CCKleV1Container(){
		_myPNGFormat = new CCKlePNGFormat(false);
		_myBINFormat = new CCKleBinFormat();
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
	public void save(Path thePath, CCKleEffectables theElements, Map<CCKleChannelType, CCSequence> theSequences) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes)) {
	        
	        Path myMetaInfFolder = fs.getPath("META-INF");
			CCNIOUtil.createDirectories(myMetaInfFolder);
			CCDataElement myMappingXML = new CCDataElement("mapping");
			for(CCKleChannelType myKey:theElements.mappings().keySet()){
				if(myKey != CCKleChannelType.MOTORS)continue;
				CCKleMapping<?> myMapping = theElements.mappings().get(myKey);
				for(CCKleChannel myChannel:myMapping){
					if(_myUseStartEndChannel && (myChannel.id() < _myStartChannel || myChannel.id() > _myEndChannel))continue;
					myMappingXML.addChild(myChannel.mappingXML());
				}
			}
			CCLog.info(myMetaInfFolder.resolve("setup.xml"));
			
			CCXMLIO.saveXMLElement(myMappingXML, myMetaInfFolder.resolve("setup.xml"));
			
			CCDataElement mySculptureXML = new CCDataElement("sculpture");
			CCDataElement myElementsXML = mySculptureXML.createChild("elements");
			
			for(CCKleEffectable myElement:theElements){
				CCDataElement myElementXML = myElementsXML.createChild("element");
				myElementXML.addAttribute("id", myElement.id());
				CCDataElement myMotorsXML = myElementXML.createChild("motors");
				for(CCMotorChannel myChannel:myElement.motorSetup().channels()){
					if(_myUseStartEndChannel && (myChannel.id() < _myStartChannel || myChannel.id() > _myEndChannel))continue;
					CCDataElement myMotorXML = myMotorsXML.createChild("motor");
					myMotorXML.addAttribute("id", myChannel.id());
					myMotorXML.addAttribute("x", myChannel.position().x);
					myMotorXML.addAttribute("y", myChannel.position().y);
					myMotorXML.addAttribute("z", myChannel.position().z);
				}
				CCDataElement myBoundsXML = myElementXML.createChild("bounds");
				for(CCVector3 myPoint:myElement.motorSetup().bounds()){
					addPoint(myBoundsXML, myPoint);
				}
			}
			
			CCXMLIO.saveXMLElement(mySculptureXML, myMetaInfFolder.resolve("sculpture.xml"));
			
			Path myFramesFolder = fs.getPath("frames");
			
			CCLog.info(myFramesFolder.toAbsolutePath());
	        
	        for(CCKleChannelType myKey:theElements.mappings().keySet()){
	        	if(myKey != CCKleChannelType.MOTORS)continue;
	        	
	        	CCKleMapping myMapping = theElements.mappings().get(myKey);
	        	CCSequence mySequence = theSequences.get(myKey);

				CCNIOUtil.createDirectories(myFramesFolder);
				
				_myPNGFormat.save(myFramesFolder, myMapping, mySequence);
				_myBINFormat.save(myFramesFolder.resolve(myMapping.type().id() + ".bin"), myMapping, mySequence);
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	private void addPoint(CCDataElement theParentXML, CCVector3 thePoint){
		CCDataElement myPointXML = theParentXML.createChild("point");
		myPointXML.addAttribute("x", thePoint.x);
		myPointXML.addAttribute("y", thePoint.y);
		myPointXML.addAttribute("z", thePoint.z);
	}

	@Override
	public Map<CCKleChannelType,CCSequence> load(Path thePath, Map<CCKleChannelType, CCKleMapping<?>> theMappings) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());
        
        CCLog.info(zipFile);
        
        Map<CCKleChannelType,CCSequence> myResult = new HashMap<CCKleChannelType, CCSequence>();

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes)) {
        	Path myFramesFolder = fs.getPath("frames");
        	
        	CCLog.info(myFramesFolder);
	        
	        for(CCKleChannelType myKey:theMappings.keySet()){
	        	if(myKey != CCKleChannelType.MOTORS)continue;
	        	
	        	CCKleMapping myMapping = theMappings.get(myKey);
				
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
		CCKleV1Container myContainer = new CCKleV1Container();
		CCKleEffectables myElements = new CCKleEffectables(
				CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/mapping.xml")), 
				CCXMLIO.createXMLElement(CCNIOUtil.dataPath("manila_kle2/sculpture.xml")), new CC2Motor1ConnectionBounds(), 1);
		
		Map<CCKleChannelType, CCSequence> mySequenceMap = myContainer.load(CCNIOUtil.dataPath("141128_CoD_Synchronicity_Choreography.kle"), myElements.mappings());
		
		CCKleCSVFormat myCsvFormat = new CCKleCSVFormat();
		myCsvFormat.save(CCNIOUtil.dataPath("manila_csv"), myElements.mappings().get(CCKleChannelType.MOTORS), mySequenceMap.get(CCKleChannelType.MOTORS));
	}
	
	public static void main2(String[] args) throws IOException {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + CCNIOUtil.dataPath("bla4.zip").toUri().getPath());
        
        try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes)) {
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
	    try (FileSystem zipFileSys = FileSystems.newFileSystem(zipFile, attributes)) {
	      Path path = zipFileSys.getPath("docs2");
	      Files.createDirectory(path);
	      try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(zipFileSys.getPath("/"))) {
	        for (Path file : directoryStream) {
	          System.out.println(file.getFileName());
	        }
	      }
	    }
	  }
}
