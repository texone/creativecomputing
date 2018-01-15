package cc.creativecomputing.math.signal;

public enum CCSignalType {
	SIMPLEX(new CCSimplexNoise()), 
	LINEAR_RANDOM(new CCLinearRandom()),
	WORLEY(new CCWorleyNoise()), 
	SINUS(new CCSinSignal()), 
	SQUARE(new CCSquareSignal()), 
	TRI(new CCTriSignal()),
	SLOPED_TRI(new CCSlopedTriSignal()),
	SAW(new CCSawSignal());

	private CCSignal _mySignal;

	CCSignalType(CCSignal theSignal) {
		_mySignal = theSignal;
	}

	public CCSignal signal() {
		return _mySignal;
	}

}
