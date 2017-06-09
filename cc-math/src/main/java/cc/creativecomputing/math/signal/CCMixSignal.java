package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;

public class CCMixSignal extends CCSignal{
	
	@CCProperty(name = "saw", min = 0, max = 1)
	private double _cSaw = 0;
	@CCProperty(name = "simplex", min = 0, max = 1)
	private double _cSimplex = 0;
	@CCProperty(name = "worley", min = 0, max = 1)
	private double _cWorley = 0;
	@CCProperty(name = "sine", min = 0, max = 1)
	private double _cSine = 0;
	@CCProperty(name = "square", min = 0, max = 1)
	private double _cSquare = 0;
	@CCProperty(name = "tri", min = 0, max = 1)
	private double _cTri = 0;
	@CCProperty(name = "sloped tri", min = 0, max = 1)
	private double _cSlopedTri = 0;
	@CCProperty(name = "amp", min = 0, max = 1)
	private double _cAmp = 0;
	
	
	private CCSawSignal _mySaw;
	private CCSimplexNoise _mySimplex;
	@CCProperty(name = "worley setup")
	private CCWorleyNoise _myWorley;
	private CCSinSignal _mySine;
	@CCProperty(name = "square setup")
	private CCSquareSignal _mySquare;
	@CCProperty(name = "tri setup")
	private CCTriSignal _myTri;
	private CCSlopedTriSignal _mySlopedTri;
	
	public CCMixSignal(){
		_mySaw = new CCSawSignal(this);
		_mySimplex = new CCSimplexNoise(this);
		_myWorley = new CCWorleyNoise(this);
		_mySine = new CCSinSignal(this);
		_mySquare = new CCSquareSignal(this);
		_myTri = new CCTriSignal(this);
		_mySlopedTri = new CCSlopedTriSignal(this);
	}
	
	private double mixSignal(
		double[] theSaw, 
		double[] theSimplex, 
		double[] theWorley, 
		double[] theSine, 
		double[] theSquare, 
		double[] theTri, 
		double[] theSlopedTri
	){
		double myMaxAmount = _cSaw + _cSimplex + _cWorley + _cSine + _cSquare + _cTri + _cSlopedTri;
		if(myMaxAmount == 0)return 0;
		return (
			theSaw[0] * _cSaw + 
			theSimplex[0] * _cSimplex + 
			theWorley[0] * _cWorley + 
			theSine[0] * _cSine +
			theSquare[0] * _cSquare + 
			theTri[0] * _cTri + 
			theSlopedTri[0] * _cSlopedTri
		) / myMaxAmount * _cAmp;
	}

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{mixSignal(
			_cSaw == 0 ? new double[]{0} : _mySaw.signalImpl(theX, theY, theZ),
			_cSimplex == 0 ? new double[]{0} : _mySimplex.signalImpl(theX, theY, theZ),
			_cWorley == 0 ? new double[]{0} : _myWorley.signalImpl(theX, theY, theZ),
			_cSine == 0 ? new double[]{0} : _mySine.signalImpl(theX, theY, theZ),
			_cSquare == 0 ? new double[]{0} : _mySquare.signalImpl(theX, theY, theZ),
			_cTri == 0 ? new double[]{0} : _myTri.signalImpl(theX, theY, theZ),
			_cSlopedTri == 0 ? new double[]{0} : _mySlopedTri.signalImpl(theX, theY, theZ)
		)};
	}
	
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{mixSignal(
			_cSaw == 0 ? new double[]{0} : _mySaw.signalImpl(theX, theY),
			_cSimplex == 0 ? new double[]{0} : _mySimplex.signalImpl(theX, theY),
			_cWorley == 0 ? new double[]{0} : _myWorley.signalImpl(theX, theY),
			_cSine == 0 ? new double[]{0} : _mySine.signalImpl(theX, theY),
			_cSquare == 0 ? new double[]{0} : _mySquare.signalImpl(theX, theY),
			_cTri == 0 ? new double[]{0} : _myTri.signalImpl(theX, theY),
			_cSlopedTri == 0 ? new double[]{0} : _mySlopedTri.signalImpl(theX, theY)
		)};
	}
	
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{mixSignal(
			_cSaw == 0 ? new double[]{0} : _mySaw.signalImpl(theX),
			_cSimplex == 0 ? new double[]{0} : _mySimplex.signalImpl(theX),
			_cWorley == 0 ? new double[]{0} : _myWorley.signalImpl(theX),
			_cSine == 0 ? new double[]{0} : _mySine.signalImpl(theX),
			_cSquare == 0 ? new double[]{0} : _mySquare.signalImpl(theX),
			_cTri == 0 ? new double[]{0} : _myTri.signalImpl(theX),
			_cSlopedTri == 0 ? new double[]{0} : _mySlopedTri.signalImpl(theX)
		)};
	}

}
