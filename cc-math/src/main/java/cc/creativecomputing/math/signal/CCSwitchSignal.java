package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;

public class CCSwitchSignal extends CCSignal{
	
	@CCProperty(name = "signal")
	private CCSignalType _cSignal = CCSignalType.SIMPLEX;

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return _cSignal.signal().signalImpl(theX, theY, theZ);
	}
	
	@Override
	public double[] signalImpl(double theX, double theY) {
		return _cSignal.signal().signalImpl(theX, theY);
	}
	
	@Override
	public double[] signalImpl(double theX) {
		return _cSignal.signal().signalImpl(theX);
	}

}
