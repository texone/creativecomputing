package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.math.CCMath;

public class CCPositionXSource extends CCModulationSource{

	public CCPositionXSource(double theMin, double theMax) {
		super("x", (effectManager, theEffectable) -> {return CCMath.norm(theEffectable.position().x, theMin, theMax);});
	}

	
}
