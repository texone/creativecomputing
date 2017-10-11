package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.math.CCMath;

public class CCEnvelopeSource extends CCModulationSource {

	@CCProperty(name = "scale", min = 0, max = 1)
	private double _cScale;
	
	@CCProperty(name = "offset", min = 0, max = 2)
	private double _cOffset = 1;
	
	@CCProperty(name = "envelope")
	private CCEnvelope _cEnvelope = new CCEnvelope();

	public CCEnvelopeSource(String theName) {
		super(theName, null);
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myID = effectable.relativeSource(CCEffectable.ID_SOURCE);
			return CCMath.saturate(
				_cEnvelope.value(myID * _cScale + _cOffset) % 1
			);
		};
	}
	
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
