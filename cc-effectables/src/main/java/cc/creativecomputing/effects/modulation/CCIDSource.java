package cc.creativecomputing.effects.modulation;

public class CCIDSource extends CCModulationSource {

	public CCIDSource(String theName) {
		super(
			theName, 
			(effectManager, effectable) -> {
				return effectable.idSource(theName) / (double)effectManager.idMax(theName);
			}
		);
	}

}
