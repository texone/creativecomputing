package cc.creativecomputing.kle.formats;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public class CCSequenceIO {	
	
	public static CCSequence load(Path thePath, CCSequenceMapping<?> theMapping){
		String myExtension = CCNIOUtil.fileExtension(thePath);
		
		if(myExtension != null){
			switch(myExtension){
			case "bin":
				return new CCSequenceBinFormat().load(thePath, theMapping);
			case "cca":
				return new CCSequenceCCAFormat().load(thePath, theMapping);
			case "xml":
				return new CCSequenceXMLFormat().load(thePath, theMapping);
			case "kle":
				try{
					CCSequenceKLE2Container myContainer = new CCSequenceKLE2Container();
					Map<CCKleChannelType,CCSequenceMapping<?>> myMappings = new HashMap<CCKleChannelType, CCSequenceMapping<?>>();
					myMappings.put(theMapping.type(), theMapping);
					Map<CCKleChannelType, CCSequence> mySequenceMap = myContainer.load(thePath, myMappings);
					CCSequence mySequence = mySequenceMap.get(theMapping.type());
					if(mySequence != null)return mySequence;
				}catch(Exception e){
					
				}
			}
		}
		
		for(CCSequenceFormats myFormatEnum:CCSequenceFormats.values()){
			try{
				CCSequence mySequence = null;
				switch(myFormatEnum){
				case ANIM:
					mySequence = new CCSequenceAnimFormat().load(thePath, theMapping);
					break;
				case CSV:
					mySequence = new CCSequenceCSVFormat().load(thePath, theMapping);
					break;
				case PNG:
					mySequence = new CCSequencePNGFormat().load(thePath, theMapping);
					break;
				default:
				}
				if(mySequence != null && mySequence.size() > 0){
					return mySequence;
				}
			}catch(Exception e){
//				e.printStackTrace();
			}
		}
//		try{
//			CCSequenceKLE1Container myContainer = new CCSequenceKLE1Container();
//			Map<CCKleChannelType,CCSequenceMapping<?>> myMappings = new HashMap<CCKleChannelType, CCSequenceMapping<?>>();
//			myMappings.put(theMapping.type(), theMapping);
//			Map<CCKleChannelType, CCSequence> mySequenceMap = myContainer.load(thePath, myMappings);
//			CCSequence mySequence = mySequenceMap.get(theMapping.type());
//			if(mySequence != null)return mySequence;
//		}catch(Exception e){
//			
//		}
		
		return null;
	}
}
