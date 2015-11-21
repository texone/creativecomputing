package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioAsset;
import cc.creativecomputing.sound.CCFFT;

public class CCKleColorFFTAnimation extends CCKleAnimation<CCColor>{
	
	
	
	@CCProperty(name = "fft")
	private CCFFT _myFFT;
	@CCProperty(name = "averages")
	private boolean _cUseAverages = true;
	
	@CCProperty(name = "brightness amp", min = 0, max = 10)
	private double _cBrightnessAmp = 1f;
	
	@CCProperty(name = "brightness band")
	private CCKleModulation _myBrightnessModulation = new CCKleModulation();
	
	private CCAudioAsset _myAudioAsset;
	
	public CCKleColorFFTAnimation(CCAudioAsset theAudioAsset){
		_myFFT = new CCFFT();
		_myAudioAsset = theAudioAsset;
	}
	
	public CCFFT fft(){
		return _myFFT;
	}
	
	public void update(final double theDeltaTime){
		
		if(_myAudioAsset.value() == null)return;
		_myFFT.forward(_myAudioAsset.value().player);
	}
	
	public CCColor animate(CCSequenceElement theElement){
		double myBrightness = 0;
		if(_cUseAverages){
			if(_myFFT.averages() != null && _myFFT.averages().length >= 1){
				int myBand = (int)CCMath.blend(0, _myFFT.averages().length - 1, _myBrightnessModulation.modulation(theElement));
				if(myBand < _myFFT.averages().length){
					myBrightness = _myFFT.averages()[myBand] * _cBrightnessAmp;
				}
			}
		}else{
			int myBand = (int)CCMath.blend(0, _myFFT.spectrum().length, _myBrightnessModulation.modulation(theElement));
			if(myBand < _myFFT.spectrum().length){
				myBrightness = _myFFT.spectrum()[myBand] * _cBrightnessAmp;
			}
		}
		CCColor myResult = CCColor.createFromHSB(
			1, 
			0, 
			myBrightness
		);
		double myBlend = elementBlend(theElement);
		myResult.r *= myBlend;
		myResult.g *= myBlend;
		myResult.b *= myBlend;
		
		return myResult;
	}
}
