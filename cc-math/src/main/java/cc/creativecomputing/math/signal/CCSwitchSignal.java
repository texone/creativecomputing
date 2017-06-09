package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;

public class CCSwitchSignal extends CCSignal{
	
	@CCProperty(name = "signal")
	private CCSignalType _cSignal = CCSignalType.SIMPLEX;

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		_cSignal.signal().settings(this);
		return _cSignal.signal().signalImpl(theX, theY, theZ);
	}
	
	@Override
	public double[] signalImpl(double theX, double theY) {
		_cSignal.signal().settings(this);
		return _cSignal.signal().signalImpl(theX, theY);
	}
	
	@Override
	public double[] signalImpl(double theX) {
		_cSignal.signal().settings(this);
		return _cSignal.signal().signalImpl(theX);
	}

}
