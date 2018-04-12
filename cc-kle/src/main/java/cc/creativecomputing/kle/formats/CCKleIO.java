package cc.creativecomputing.kle.formats;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;

public class CCKleIO {	
	
	public static CCSequence load(Path thePath, CCKleMapping<?> theMapping){
		String myExtension = CCNIOUtil.fileExtension(thePath);
		
		if(myExtension != null){
			switch(myExtension){
			case "bin":
				return new CCKleBinFormat().load(thePath, theMapping);
			case "cca":
				return new CCKleCCAFormat().load(thePath, theMapping);
			case "xml":
				return new CCKleXMLFormat().load(thePath, theMapping);
			case "kle":
				try{
					CCKleV2Container myContainer = new CCKleV2Container();
					Map<CCKleChannelType,CCKleMapping<?>> myMappings = new HashMap<CCKleChannelType, CCKleMapping<?>>();
					myMappings.put(theMapping.type(), theMapping);
					Map<CCKleChannelType, CCSequence> mySequenceMap = myContainer.load(thePath, myMappings);
					CCSequence mySequence = mySequenceMap.get(theMapping.type());
					if(mySequence != null)return mySequence;
				}catch(Exception e){
					
				}
			}
		}
		
		for(CCKleFormats myFormatEnum:CCKleFormats.values()){
			try{
				CCSequence mySequence = null;
				switch(myFormatEnum){
				case ANIM:
					mySequence = new CCKleAnimFormat().load(thePath, theMapping);
					break;
				case CSV:
					mySequence = new CCKleCSVFormat().load(thePath, theMapping);
					break;
				case BCSV:
					mySequence = new CCKleCSVBinFormat().load(thePath, theMapping);
					break;
				case PNG:
					mySequence = new CCKlePNGFormat().load(thePath, theMapping);
					break;
				default:
				}
				if(mySequence != null && mySequence.size() > 0){
					return mySequence;
				}
			}catch(Exception e){
				e.printStackTrace();
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
