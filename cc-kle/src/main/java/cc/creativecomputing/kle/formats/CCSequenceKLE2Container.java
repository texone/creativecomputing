package cc.creativecomputing.kle.formats;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public class CCSequenceKLE2Container extends CCSequencesContainer{
	private CCSequencePNGFormat _myPNGFormat;
	private CCSequenceBinFormat _myBINFormat;
	
	public CCSequenceKLE2Container(){
		_myPNGFormat = new CCSequencePNGFormat();
		_myBINFormat = new CCSequenceBinFormat();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void save(Path thePath, CCSequenceElements theElements, Map<String, CCSequence> theSequences) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes);) {
        	Path myFramesFolder = fs.getPath("frames");
	        
        	for(CCKleChannelType myKey:theElements.mappings().keySet()){
	        	
	        	CCSequenceMapping myMapping = theElements.mappings().get(myKey);
	        	CCSequence mySequence = theSequences.get(myKey);
		        
				CCNIOUtil.createDirectories(myFramesFolder);
				
				_myPNGFormat.save(myFramesFolder.resolve(myMapping.type().id()), myMapping, mySequence);
				_myBINFormat.save(myFramesFolder.resolve(myMapping.type().id() + ".bin"), myMapping, mySequence);
	        }
	
	        Path myMetaInfFolder = fs.getPath("META-INF");
			CCNIOUtil.createDirectories(myMetaInfFolder);
			CCXMLIO.saveXMLElement(CCSequenceElements.mapping(theElements), myMetaInfFolder.resolve("mapping.xml"));
			CCXMLIO.saveXMLElement(CCSequenceElements.sculpture(theElements), myMetaInfFolder.resolve("sculpture.xml"));
        }catch(Exception e){
        	
        }
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<CCKleChannelType,CCSequence> load(Path thePath, Map<CCKleChannelType, CCSequenceMapping<?>> theMappings) {
		Map<String, String> attributes = new HashMap<>();
	    attributes.put("create", "true");
        
        final URI zipFile = URI.create("jar:file:" + thePath.toUri().getPath());
        
        Map<CCKleChannelType,CCSequence> myResult = new HashMap<CCKleChannelType, CCSequence>();

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, attributes);) {
        	Path myFramesFolder = fs.getPath("frames");
	        
	        for(CCKleChannelType myKey:theMappings.keySet()){
	        	
	        	CCSequenceMapping myMapping = theMappings.get(myKey);
				
				CCSequence mySequence = _myPNGFormat.load(myFramesFolder.resolve(myMapping.type().id()), myMapping);
				myResult.put(myKey, mySequence);
	        }
        }catch(Exception e){
        	
        }
		
		return myResult;
	}
	
}
