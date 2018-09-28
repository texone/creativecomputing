package cc.creativecomputing.sound;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.control.CCAsset;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;

public class CCAudioAsset extends CCAsset<CCAudioAssetData>{
	
	private class CCAssetFFT{

		@CCProperty(name = "fft")
		private CCFFT _myFFT = new CCFFT();
		
		@CCProperty(name = "fft amp", min = 0, max = 10)
		private double _cAmp = 1;
		
		@CCProperty(name = "fft pow", min = 0, max = 10)
		private double _cPow = 1;
		
		@CCProperty(name = "normalize")
		private boolean _cNormalize = false;
		
		@CCProperty(name = "fft max reduction", min = 0, max = 1)
		private double _cMaxReduction = 1;
		@CCProperty(name = "min max", min = 0, max = 1)
		private double _cMinMax = 1;
		
		@CCProperty(name = "use fft")
		private boolean _cUseFFT = false;
		
		@CCProperty(name = "spec min", min = 0, max = 1)
		private double _cSpecMin = 0;
		@CCProperty(name = "spec max", min = 0, max = 1)
		private double _cSpecMax = 1;
		
		private void updateSpectrum(CCTriggerProgress theProcess, long theChunks, long theTotalChunks, CCAudioAssetData theData){
			_myFFT.setup(fftSize, theData.sampleRate());
			int totalSamples = theData.samples();
			int totalChunks = (totalSamples / fftSize);
			
			CCMatrix2 mySpectrum = null;
			
			double[] mySpectraMax = null;

			for (int chunkIdx = 0; chunkIdx < totalChunks; ++chunkIdx) {
				// now analyze the left channel
				_myFFT.forward(theData.data(chunkIdx * fftSize, fftSize));
				
				if(mySpectrum == null){
					if(_myFFT.averages() != null){
						mySpectrum = new CCMatrix2(totalChunks, _myFFT.averagesSize());
						mySpectraMax = new double[_myFFT.averagesSize()];
					}else{
						mySpectrum = new CCMatrix2(totalChunks,fftSize / 2);
						mySpectraMax = new double[fftSize / 2];
					}
				}
				
				// and copy the resulting spectrum into our spectra array
				if(_myFFT.averages() != null){
					for (int i = 0; i < _myFFT.averagesSize(); ++i) {
						mySpectrum.set(chunkIdx,i,_myFFT.averages()[i]);
						if(!_cNormalize)continue;
						mySpectraMax[i] = CCMath.max(mySpectraMax[i] - _cMaxReduction, _cMinMax, _myFFT.averages()[i]);
						mySpectrum.data()[chunkIdx][i][0] /= mySpectraMax[i];
					}
				}else{
					for (int i = 0; i < 256; ++i) {
						mySpectrum.set(chunkIdx,i,_myFFT.spectrum()[i]);
						if(!_cNormalize)continue;
						mySpectraMax[i] = CCMath.max(mySpectraMax[i] - _cMaxReduction, _cMinMax, _myFFT.spectrum()[i]);
						mySpectrum.data()[chunkIdx][i][0] /= mySpectraMax[i];
					}
				}
				if(theProcess != null)theProcess.progress(CCMath.map(chunkIdx + theChunks, 0, theTotalChunks, 0, 1));
			}
			
			theData.spectrum(mySpectrum);
			
		}
		
		@CCProperty(name = "update spectrum")
		public void updateSpectrum(CCTriggerProgress theProcess){
			if(!_cUseFFT)return;
			
			int totalChunks = 0;
			for(CCAudioAssetData myData:_myAssetMap.values()){
				totalChunks += (myData.samples() / fftSize) + 1;
			}
			int chunks = 0;
			theProcess.start();
			for(CCAudioAssetData myData:_myAssetMap.values()){
				updateSpectrum(theProcess, chunks, totalChunks, myData);
				chunks += (myData.samples() / fftSize) + 1;
			}
			theProcess.end();
		}
		
		public double spectrum(double theOffset){
			if(!_cUseFFT)return 0;
			if(_myAsset == null)return 0;
			return CCMath.pow(_myAsset.spectrum(CCMath.blend(_cSpecMin, _cSpecMax, theOffset), _myPlayTimeMillis), _cPow) * _cAmp;
		}

		public CCAudioAssetData checkSpectrumData(CCEventPoint theEvent) {
			CCAudioAssetData myAudioData = null;
			Path myFilePath = Paths.get(theEvent.content().value().toString());
			
			if(_myAssetMap.containsKey(myFilePath)){
				myAudioData = _myAssetMap.get(myFilePath);
				if(_cUseFFT && !myAudioData.hasSpectrum())updateSpectrum(null,0,0,myAudioData);
			}else{
				try{
					myAudioData = new CCAudioAssetData(CCSoundIO.loadFile(myFilePath, 2048), CCSoundIO.loadSample(myFilePath).getChannel(0)) ;
					if(_cUseFFT)updateSpectrum(null,0,0,myAudioData);
					_myAssetMap.put(myFilePath, myAudioData);
				}catch(Exception e){
					
				}
			}
			
			return myAudioData;
		}
	}
	
	@CCProperty(name = "fft options")
	private CCAssetFFT _myAssetFFT = new CCAssetFFT();
	
	@CCProperty(name = "min time offset", min = 0.01, max = 1)
	private float _cMaxTimeOffset = 0.05f;
	
	@CCProperty(name = "volume", min = 0, max = 1)
	private float _cVolume = 1;

	@CCProperty(name = "pan", min = -1, max = 1)
	private float _cPan = 0;
	
	public CCAudioAsset(){
		_myAsset = null;
	}
	
	@Override
	public CCAudioAssetData loadAsset(Path thePath) {
		return new CCAudioAssetData(
			CCSoundIO.loadFile(thePath, 2048), 
			CCSoundIO.loadSample(thePath).getChannel(0)
		) ;
	}
	

	private static final int fftSize = 512;
	
	
	
	
	
	private boolean _myIsPlaying = false;
	
	private int _myPlayTimeMillis = 0;
	
	
	@Override
	public void mute(boolean theMute) {
		
	}
	
	
	@Override
	public void time(double theGlobalTime, double theEventTime, double theContentOffset) {
		_myPlayTimeMillis = (int)((theEventTime - theContentOffset) * 1000);
		if(_myAsset == null)return;
		if(!_myIsPlaying)return;
		
		if(_myPlayTimeMillis < 0 || _myPlayTimeMillis > _myAsset.player.length()){
			if(_myAsset.player.isPlaying()){
				_myAsset.player.pause();
			}
			return;
		}
		if(!_myAsset.player.isPlaying()){
			_myAsset.player.play(_myPlayTimeMillis);
		}
//		_myAsset.setGain(_cVolume);
		_myAsset.player.balance(_cPan);
		int myOffset = CCMath.abs(_myAsset.player.position() - _myPlayTimeMillis);
		if(myOffset > _cMaxTimeOffset * 1000){
			_myAsset.player.skip(-(_myAsset.player.position() - _myPlayTimeMillis));
		}
	}
	
	public double spectrum(double theOffset){
		return _myAssetFFT.spectrum(theOffset);
	}
	
	
//	@Override
//	public void renderTimedEvent(CCTimedEventPoint theEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
//		if(theEvent.content() == null || theEvent.content().value() == null)return;
//		
//		CCAudioAssetData myAudioData = _myAssetFFT.checkSpectrumData(theEvent);
//		
//		if(myAudioData == null)return;
//		if(myAudioData.data == null)return;
//		
//		
//		double myWidth = theUpper.getX() - theLower.getX();
//		double myHeight = theUpper.getY() - theLower.getY();
//		
//		if(!_myAssetFFT._cUseFFT || myAudioData.image() == null){
//			GeneralPath myPath = new GeneralPath();
//			myPath.moveTo(theLower.getX(), theLower.getY());
//			for (int x = 0; x < myWidth - 1; x++) {
//				double myTime1 = CCMath.map(x, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset();
//				int mySample1 = (int)CCMath.map(myTime1, 0, myAudioData.player.length() / 1000d, 0 ,myAudioData.data.length);
//				double myTime2 = CCMath.map(x + 1, 0, myWidth, lowerTime, UpperTime) - theEvent.time() - theEvent.contentOffset();
//				int mySample2 = (int)CCMath.map(myTime2, 0, myAudioData.player.length() / 1000d, 0 ,myAudioData.data.length);
//				
//				float value0 = 0;
//				float value1 = 0;
//				for(int j = mySample1;j<mySample2;j++){
//					if(j >= myAudioData.data.length || j < 0)continue;
//					 value0 = CCMath.max(myAudioData.data[j], value0);
//					 value1 = CCMath.min(myAudioData.data[j], value1);
//				}
//				
//	//			g.line(i / _cScale, 50 - song.getChannel(0)[i] * 50, i / _cScale + 1, 50 - song.getChannel(0)[i + _cScale] * 10);
//				myPath.moveTo(x + theLower.getX(),myHeight / 2 +  value0 * myHeight / 2);
//				myPath.lineTo(x + theLower.getX(),myHeight / 2 +  value1 * myHeight / 2);
//			}
//	        theG2d.draw(myPath);
//		}else{
//			double myTime1 = lowerTime - theEvent.time() - theEvent.contentOffset();
//			double myTime2 = UpperTime - theEvent.time() - theEvent.contentOffset();
//			int sx1 = (int)CCMath.map(myTime1 * 1000, 0, myAudioData.player.length(), 0, myAudioData.image().getWidth());
//			int sx2 = (int)CCMath.map(myTime2 * 1000, 0, myAudioData.player.length(), 0, myAudioData.image().getWidth());
//			theG2d.drawImage(
//				myAudioData.image(), 
//				(int)theLower.getX(), 
//				0, 
//				(int)(theLower.getX() + myWidth), 
//				(int)myHeight, 
//				sx1, 
//				(int)(myAudioData.image().getHeight() * _myAssetFFT._cSpecMin), 
//				sx2, 
//				(int)(myAudioData.image().getHeight() * _myAssetFFT._cSpecMax), 
//				null
//			);
//		}
//		
//	}
	
	@Override
	public void out() {
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}

	@Override
	public void play() {
		_myIsPlaying = true;
	}

	@Override
	public void stop() {
		_myIsPlaying = false;
		if(_myAsset == null)return;
		_myAsset.player.pause();
	}
}
