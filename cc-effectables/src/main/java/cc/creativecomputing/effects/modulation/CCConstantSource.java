package cc.creativecomputing.effects.modulation;

public class CCConstantSource extends CCModulationSource {

	public CCConstantSource() {
		super(
			"constant", 
			(effectManager, effectable) -> {
				return 1d;
			}
		);
	}

}
