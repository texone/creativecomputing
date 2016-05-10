package cc.creativecomputing.kle.formats;

import java.nio.file.Path;
import java.util.Map;

import cc.creativecomputing.kle.CCSequence;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;

public abstract class CCSequencesContainer  {
	
	public abstract void save(Path theFile, CCSequenceElements theElements, Map<CCKleChannelType,CCSequence> theSequences);

	public abstract Map<CCKleChannelType,CCSequence> load(Path theFile, Map<CCKleChannelType,CCSequenceMapping<?>> theMappings);
	
	public Map<CCKleChannelType,CCSequence> load(Path theFile, CCSequenceElements theElements){
		return load(theFile, theElements.mappings());
	}
	

}
