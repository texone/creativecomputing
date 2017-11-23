package cc.creativecomputing.kle.formats;

import java.nio.file.Path;
import java.util.Map;

import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.sequence.CCSequence;

public abstract class CCKleContainerFormat  {
	
	public abstract void save(Path theFile, CCKleEffectables theElements, Map<CCKleChannelType,CCSequence> theSequences);

	public abstract Map<CCKleChannelType,CCSequence> load(Path theFile, Map<CCKleChannelType,CCKleMapping<?>> theMappings);
	
	public Map<CCKleChannelType,CCSequence> load(Path theFile, CCKleEffectables theElements){
		return load(theFile, theElements.mappings());
	}
	

}
