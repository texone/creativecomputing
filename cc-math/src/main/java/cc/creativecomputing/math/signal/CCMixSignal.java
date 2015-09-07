package cc.creativecomputing.math.signal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;

public class CCMixSignal extends CCSignal{
	
	@CCProperty(name = "saw", min = 0, max = 1)
	private double _cSaw = 0;
	@CCProperty(name = "simplex", min = 0, max = 1)
	private double _cSimplex = 0;
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
	private CCSinSignal _mySine;
	private CCSquareSignal _mySquare;
	private CCTriSignal _myTri;
	private CCSlopedTriSignal _mySlopedTri;
	
	private List<CCSignal> _mySignals = new ArrayList<>();
	
	public CCMixSignal(){
		_mySignals.add(_mySaw = new CCSawSignal());
		_mySignals.add(_mySimplex = new CCSimplexNoise());
		_mySignals.add(_mySine = new CCSinSignal());
		_mySignals.add(_mySquare = new CCSquareSignal());
		_mySignals.add(_myTri = new CCTriSignal());
		_mySignals.add(_mySlopedTri = new CCSlopedTriSignal());
	}
	
	@Override
	protected void scaleImplementation(double theNoiseScale) {
		super.scaleImplementation(theNoiseScale);
		for(CCSignal mySignal:_mySignals){
			mySignal.scale(theNoiseScale);
		}
	}
	
	@Override
	protected void bandsImplementation(double theBands) {
		super.bandsImplementation(theBands);
		for(CCSignal mySignal:_mySignals){
			mySignal.bands(theBands);
		}
	}
	
	@Override
	protected void gainImplementation(double theGain) {
		super.gainImplementation(theGain);
		for(CCSignal mySignal:_mySignals){
			mySignal.gain(theGain);
		}
	}
	
	@Override
	protected void lacunarityImplementation(double theLacunarity) {
		super.lacunarityImplementation(theLacunarity);
		for(CCSignal mySignal:_mySignals){
			mySignal.lacunarity(theLacunarity);
		}
	}
	
	private double mixSignal(double[] theSaw, double[] theSimplex, double[] theSine, double[] theSquare, double[] theTri, double[] theSlopedTri){
		double myMaxAmount = _cSaw + _cSimplex + _cSine + _cSquare + _cTri + _cSlopedTri;
		if(myMaxAmount == 0)return 0;
		return (
			theSaw[0] * _cSaw + 
			theSimplex[0] * _cSimplex + 
			theSine[0] * _cSine +
			theSquare[0] * _cSquare + 
			theTri[0] * _cTri + 
			theSlopedTri[0] * _cSlopedTri
		) / myMaxAmount * _cAmp;
	}

	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		return new double[]{mixSignal(
			_mySaw.signalImpl(theX, theY, theZ),
			_mySimplex.signalImpl(theX, theY, theZ),
			_mySine.signalImpl(theX, theY, theZ),
			_mySquare.signalImpl(theX, theY, theZ),
			_myTri.signalImpl(theX, theY, theZ),
			_mySlopedTri.signalImpl(theX, theY, theZ)
		)};
	}
	
	@Override
	public double[] signalImpl(double theX, double theY) {
		return new double[]{mixSignal(
			_mySaw.signalImpl(theX, theY),
			_mySimplex.signalImpl(theX, theY),
			_mySine.signalImpl(theX, theY),
			_mySquare.signalImpl(theX, theY),
			_myTri.signalImpl(theX, theY),
			_mySlopedTri.signalImpl(theX, theY)
		)};
	}
	
	@Override
	public double[] signalImpl(double theX) {
		return new double[]{mixSignal(
			_mySaw.signalImpl(theX),
			_mySimplex.signalImpl(theX),
			_mySine.signalImpl(theX),
			_mySquare.signalImpl(theX),
			_myTri.signalImpl(theX),
			_mySlopedTri.signalImpl(theX)
		)};
	}

}
