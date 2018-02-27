package cc.creativecomputing.effects.modulation;

public class CCRelativeSource extends CCModulationSource {

	public CCRelativeSource(String theID) {
		super(
			theID, 
			(effectManager, theEffectable) -> {
				return theEffectable.relativeSource(theID);
			}
		);
	}

}
