package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.math.CCMath;

public class CCRandomSource extends CCModulationSource {

	public CCRandomSource() {
		super(
			"random", 
			(effectManager, theEffectable) -> {
				return CCMath.random();
			}
		);
	}

}
