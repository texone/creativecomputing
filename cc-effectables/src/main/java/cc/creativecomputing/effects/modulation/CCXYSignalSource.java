package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCXYSignalSource extends CCModulationSource {

	@CCProperty(name = "signal")
	private CCMixSignal _cSignal = new CCMixSignal();

	public CCXYSignalSource(String theName) {
		super(theName, null);
		
		_myModulationImplementation = (effectManager, effectable) -> {
			return _cSignal.value(effectable.position().xy());
		};
	}

	@Override
	public boolean isUpdated() {
		return false;
	}

}
