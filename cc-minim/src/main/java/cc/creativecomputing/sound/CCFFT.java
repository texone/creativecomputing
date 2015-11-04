package cc.creativecomputing.sound;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.WindowFunction;

public class CCFFT {
	
	private static enum CCFFTWindowType{
		BARTLETT(FFT.BARTLETT),
		BARTLETTHANN(FFT.BARTLETTHANN),
		BLACKMAN(FFT.BLACKMAN),
		COSINE(FFT.COSINE),
		GAUSS(FFT.GAUSS),
		HAMMING(FFT.HAMMING),
		HANN(FFT.LANCZOS),
		LANCZOS(FFT.LANCZOS),
		NONE(FFT.NONE),
		TRIANGULAR(FFT.TRIANGULAR);
		
		private WindowFunction _myWindowFunction;
		
		private CCFFTWindowType(WindowFunction theWindowFunction){
			_myWindowFunction = theWindowFunction;
		}
		
	}
	
	private static enum CCFFTAveraging{
		NONE,
		LINEAR,
		LOGARITHMIC
	}

	private FFT _myFFT;
	
	@CCProperty(name = "window type")
	private CCFFTWindowType _cWindowType = CCFFTWindowType.NONE;
	@CCProperty(name = "averaging")
	private CCFFTAveraging _cAveraging = CCFFTAveraging.NONE;
	
	@CCProperty(name = "linear averages", min = 2, max = 512)
	private int _cLinearAverages = 64;
	@CCProperty(name = "log bandwidth", min = 2, max = 220)
	private int _cLogBandwidth = 22;
	@CCProperty(name = "log bands per octave", min = 1, max = 10)
	private int _cLogBandsPerOctave = 3;
	@CCProperty(name = "smoothing", min = 0, max = 1)
	private float _cSmoothing = 0;
	@CCProperty(name = "average smoothing", min = 0, max = 1)
	private float _cAverageSmoothing = 0;
	@CCProperty(name = "normalize")
	private boolean _cNormalize = false;
	@CCProperty(name = "max reduction", min = 0, max = 1)
	private double _cMaxReduction = 0;
	

	private double[] _mySpectrum;
	private double[] _mySpectrumMax;
	
	private double[] _myAverages;
	private double[] _myAveragesMax;
	
	public CCFFT(){
		
	}
	
	private double _mySampleRate = 0;
	
	public void setup(int theBufferSize, double theSampleRate){
		if(_myFFT == null || theBufferSize != _myFFT.timeSize() || theSampleRate != _mySampleRate){
			_myFFT = new FFT(theBufferSize, (float)theSampleRate);
			_mySampleRate = theSampleRate;
			_mySpectrum = null;
			_mySpectrumMax = null;
		}
	}
	
	public void forward(float[] theBuffer){
		switch(_cAveraging){
		case NONE:
			_myFFT.noAverages();
			break;
		case LINEAR:
			_myFFT.linAverages(CCMath.min(_myFFT.specSize(), _cLinearAverages));
			break;
		case LOGARITHMIC:
			_myFFT.logAverages(_cLogBandwidth, _cLogBandsPerOctave);
			break;
		}
		_myFFT.window(_cWindowType._myWindowFunction);
		_myFFT.forward(theBuffer);
		
		if(_mySpectrum == null){
			_mySpectrum = new double[_myFFT.specSize()];
			_mySpectrumMax = new double[_myFFT.specSize()];
			for(int i = 0; i < _myFFT.specSize();i++){
				_mySpectrumMax[i] = _myFFT.getBand(i);
				_mySpectrum[i] = _myFFT.getBand(i);
			}
		}else{
			for(int i = 0; i < _myFFT.specSize();i++){
				_mySpectrumMax[i] = CCMath.max(_myFFT.getBand(i), _mySpectrumMax[i] - _cMaxReduction, 0);
				if(_cNormalize){
					_mySpectrum[i] = CCMath.blend(_myFFT.getBand(i) / _mySpectrumMax[i], _mySpectrum[i], _cSmoothing);
				}else{
					_mySpectrum[i] = CCMath.blend(_myFFT.getBand(i), _mySpectrum[i], _cSmoothing);
				}
				if(Double.isNaN(_mySpectrumMax[i]))_mySpectrumMax[i] = 0;
				if(Double.isNaN(_mySpectrum[i]))_mySpectrum[i] = 0;
			}
		}
		if(_cAveraging == CCFFTAveraging.NONE)return;
		
		if(_myAverages == null || _myAverages.length != _myFFT.avgSize()){
			_myAverages = new double[_myFFT.avgSize()];
			_myAveragesMax = new double[_myFFT.avgSize()];
			for(int i = 0; i < _myFFT.avgSize();i++){
				_myAveragesMax[i] = _myFFT.getAvg(i);
				_myAverages[i] = _myFFT.getAvg(i);
			}
		}else{
			for(int i = 0; i < _myFFT.avgSize();i++){
				_myAveragesMax[i] = CCMath.max(_myFFT.getAvg(i), _myAveragesMax[i] - _cMaxReduction , 0);
				if(_cNormalize){
					_myAverages[i] = CCMath.blend(_myFFT.getAvg(i) / _myAveragesMax[i], _myAverages[i], _cAverageSmoothing);
				}else{
					_myAverages[i] = CCMath.blend(_myFFT.getAvg(i), _myAverages[i], _cAverageSmoothing);
				}
				if(Double.isNaN(_myAveragesMax[i]))_myAveragesMax[i] = 0;
				if(Double.isNaN(_myAverages[i]))_myAverages[i] = 0;
			}
		}
	}
	
	public void forward(CCAudioBuffer theBuffer){
		forward(theBuffer.toArray());
	}
	
	public void forward(CCAudioPlayer thePlayer){
		setup(thePlayer.bufferSize(), thePlayer.sampleRate());
		forward(thePlayer.mix);
	}
	
	public boolean isNormalized(){
		return _cNormalize;
	}
	
	public double[] spectrum(){
		if(_myFFT == null)return null;
		return _mySpectrum;
	}
	
	public int averagesSize(){
		return _myFFT.avgSize();
	}
	
	public double[] averages(){
		if(_myFFT == null)return null;
		if(_cAveraging == CCFFTAveraging.NONE)return null;
		return _myAverages;
	}
}
