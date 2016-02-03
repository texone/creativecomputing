package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioAsset;
import cc.creativecomputing.sound.CCFFT;

public class CCKleFFTAnimation extends CCKleAnimation{
	
	
	
	@CCProperty(name = "fft")
	private CCFFT _myFFT;
	@CCProperty(name = "averages")
	private boolean _cUseAverages = true;
	
	private CCAudioAsset _myAudioAsset;
	
	public CCKleFFTAnimation(CCAudioAsset theAudioAsset){
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
	
	public double[] animate(CCSequenceElement theElement){
		double myBlend = elementBlend(theElement);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			if(_cUseAverages){
				if(_myFFT.averages() != null && _myFFT.averages().length >= 1){
					int myBand = (int)CCMath.blend(0, _myFFT.averages().length - 1, _cModulations.get(_myValueNames[i]).modulation(theElement));
					if(myBand < _myFFT.averages().length){
						myResult[i] = _myFFT.averages()[myBand] * myBlend;
					}
				}
			}else{
				int myBand = (int)CCMath.blend(0, _myFFT.spectrum().length, _cModulations.get(_myValueNames[i]).modulation(theElement));
				if(myBand < _myFFT.spectrum().length){
					myResult[i] = _myFFT.spectrum()[myBand] * myBlend;
				}
			}
		}
		return myResult;
	}
}
