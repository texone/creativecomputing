package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCPositionSource extends CCModulationSource {

	@CCProperty(name = "x", min = 0, max = 1)
	private double _cX;
	@CCProperty(name = "y", min = 0, max = 1)
	private double _cY;
	@CCProperty(name = "z", min = 0, max = 1)
	private double _cZ;

	public CCPositionSource(String theName) {
		super(theName, null);
		
		
		_myModulationImplementation = (effectManager, effectable) -> {
			return CCMath.saturate(
				effectable.normedPosition().x * _cX +
				effectable.normedPosition().y * _cY +
				effectable.normedPosition().z * _cZ
			);
		};
	}
	
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
