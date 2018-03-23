package cc.creativecomputing.demo.minim.analysis;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioRecordingStream;
import cc.creativecomputing.sound.CCFFT;
import cc.creativecomputing.sound.CCMultiChannelBuffer;
import cc.creativecomputing.sound.CCSoundIO;

/**
 * This sketch demonstrates the difference between linearly spaced averages and
 * logarithmically spaced averages.
 * <p>
 * From top to bottom:<br/>
 * - The full spectrum.<br/>
 * - The spectrum grouped into 30 linearly spaced averages.<br/>
 * - The spectrum grouped logarithmically into 10 octaves, each split into 3
 * bands.
 * <p>
 * For more information about Minim and additional features, visit
 * http://code.compartmental.net/minim/
 */
public class CCFFTOfflineAnalysis extends CCGL2Adapter {
	
	private static class CCSpectrogram{
		
		private CCImage _mySpectrumImage;
		private CCImage _mySpectrumAveragesImage;
		
		private CCTexture2D _mySpectrumTexture;
		private CCTexture2D _mySpectrumAveragesTexture;
		
		private double[][] _mySpectrumValues;
		private double[][] _mySpectrumAverageValues;
		
		private final CCFFT _myFFT;
		

		@CCProperty(name = "amp", min = 0, max = 10)
		private double _cAmp = 1;
		
		@CCProperty(name = "pow", min = 0, max = 1)
		private double _cPow = 1;
		
		@CCProperty(name = "averages")
		private boolean _cAverages = false;
		
		@CCProperty(name = "curves")
		private boolean _cDrawCurves = false;

		@CCProperty(name = "curve scale", min = 0, max = 1000)
		private double _cCurveScale = 1;
		
		@CCProperty(name = "curve step", min = 1, max = 30)
		private int _cCurveStep = 1;
		
		public CCSpectrogram(CCFFT theFFT) {
			_myFFT = theFFT;
		}
		
		private boolean _myUpdateTexture = false;
		
		private double _myChuncksPerSecond = 1;
		@CCProperty(name = "seconds grid", min = 1, max = 30)
		private int _cSecondsGrid = 1;
		
		@CCProperty(name = "analyze stream")
		void analyzeUsingAudioRecordingStream() {
			int fftSize = 512;
			CCAudioRecordingStream stream = CCSoundIO.loadFileStream(
				CCNIOUtil.dataPath("sound/01 Verses.wav"), //fair1939.wav
				fftSize,//
				false
			);

			stream.play();

			CCMultiChannelBuffer buffer = new CCMultiChannelBuffer(fftSize, stream.getFormat().getChannels());

			int totalSamples = (int) ((stream.getMillisecondLength() / 1000.0) * stream.getFormat().getSampleRate());

			int totalChunks = (totalSamples / fftSize) + 1;
			CCLog.info("Analyzing " + totalSamples + " samples for total of " + totalChunks + " chunks." + stream.getMillisecondLength() / 1000.0);

			_myChuncksPerSecond = totalChunks / (stream.getMillisecondLength() / 1000.0);
			_myFFT.setup(fftSize, stream.getFormat().getSampleRate());
			_mySpectrumValues = new double[totalChunks][fftSize / 2];
			
			double mySpectraMax = 0;
			double myAvarageMax = 0;

			for (int chunkIdx = 0; chunkIdx < totalChunks; ++chunkIdx) {
				stream.read(buffer);

				// now analyze the left channel
				_myFFT.forward(buffer.getChannel(0));
				if(_mySpectrumAverageValues == null || _mySpectrumAverageValues[0].length != _myFFT.averagesSize()){
					_mySpectrumAverageValues = new double[totalChunks][_myFFT.averagesSize()];
				}

				// and copy the resulting spectrum into our spectra array
				
				for (int i = 0; i < 256; ++i) {
					mySpectraMax = CCMath.max(mySpectraMax, _myFFT.spectrum()[i]);
					_mySpectrumValues[chunkIdx][i] = _myFFT.spectrum()[i];
				}
				if(_myFFT.averages() != null && _mySpectrumAverageValues != null){
					for (int i = 0; i < _myFFT.averagesSize(); ++i) {
						myAvarageMax = CCMath.max(myAvarageMax, _myFFT.averages()[i]);
						_mySpectrumAverageValues[chunkIdx][i] = _myFFT.averages()[i];
					}
				}
			}
			_mySpectrumImage = new CCImage(totalChunks,fftSize / 2);
			if(_mySpectrumAverageValues != null)_mySpectrumAveragesImage = new CCImage(totalChunks,_myFFT.averagesSize());
			for (int chunkIdx = 0; chunkIdx < totalChunks; ++chunkIdx) {
				for (int i = 0; i < 256; ++i) {
					_mySpectrumValues[chunkIdx][i] /= mySpectraMax;
//					_mySpectrumImage.setPixel(chunkIdx, i, CCColor.createFromHSB(CCMath.pow(_mySpectrumValues[chunkIdx][i], _cPow) * _cAmp, 1, 1));
					_mySpectrumImage.setPixel(chunkIdx, i, CCColor.createFromHSB(_mySpectrumValues[chunkIdx][i], 1, 1));
				}
				if(_myFFT.averages() != null && _mySpectrumAverageValues != null){
					for (int i = 0; i < _myFFT.averagesSize(); ++i) {
						_mySpectrumAverageValues[chunkIdx][i] /= myAvarageMax;
						_mySpectrumAveragesImage.setPixel(chunkIdx, i,  CCColor.createFromHSB(CCMath.pow(_mySpectrumAverageValues[chunkIdx][i], _cPow) * _cAmp, 1, 1));
					}
				}
			}
			_myUpdateTexture = true;
			
//			CCImageIO.write(_mySpectrumAveragesImage, CCNIOUtil.dataPath("check01.png"));
		}
		
		public void saveImage(){
			
		}
		
		public void draw(CCGraphics g){
			if(_mySpectrumValues == null)return;
			
			
			if(_cDrawCurves){
				for(int x = 0; x < g.width(); x += _myChuncksPerSecond * _cSecondsGrid / _cCurveStep){
					g.line(x, 0, x, g.height());
				}
				if(_cAverages){

					for (int i = 0; i < _mySpectrumAverageValues[0].length - 1; i++) {
						g.beginShape(CCDrawMode.LINE_STRIP);
						for (int s = 0; _mySpectrumAverageValues != null && s < _mySpectrumAverageValues.length; s+= _cCurveStep) {
						// don't draw spectra that are behind the camera or too far away
	
							g.color(CCColor.createFromHSB(CCMath.pow(_mySpectrumAverageValues[s][i], _cPow) * _cAmp, 1d, 1d));
							double y = CCMath.pow(_mySpectrumAverageValues[s][i], _cPow) * _cCurveScale;
							g.vertex(s / _cCurveStep, y + i * 5);
	
						}
						g.endShape();
					}
				}else{

					for (int i = 0; i < _mySpectrumValues[0].length - 1; i++) {
						g.beginShape(CCDrawMode.LINE_STRIP);
						for (int s = 0; s < _mySpectrumValues.length & s < g.width(); s+= _cCurveStep) {
						// don't draw spectra that are behind the camera or too far away
	
							g.color(CCColor.createFromHSB(CCMath.pow(_mySpectrumValues[s][i], _cPow) * _cAmp, 1d, 1d));
							double y = CCMath.pow(_mySpectrumValues[s][i], _cPow) * _cCurveScale;
						
							g.vertex(s / _cCurveStep, i * 2 + y);
	
						}
						g.endShape();
					}
				}
			}else{
				g.beginShape(CCDrawMode.POINTS);
				
				if(_cAverages){
					
					for (int s = 0; _mySpectrumAverageValues != null && s < _mySpectrumAverageValues.length; s++) {
						// don't draw spectra that are behind the camera or too far away
	
						for (int i = 0; i < _mySpectrumAverageValues[s].length - 1; i++) {
							g.color(CCColor.createFromHSB(CCMath.pow(_mySpectrumAverageValues[s][i], _cPow) * _cAmp, 1d, 1d));
							g.vertex(s, i);
	
						}
					}
				}else{
					for (int s = 0; s < _mySpectrumValues.length; s++) {
						// don't draw spectra that are behind the camera or too far away
	
						for (int i = 0; i < _mySpectrumValues[s].length - 1; ++i) {
							g.color(CCColor.createFromHSB(CCMath.pow(_mySpectrumValues[s][i], _cPow) * _cAmp, 1d, 1d));
							g.vertex(s, i);
	
						}
					}
				}
				g.endShape();
			}
			
			
		}
	}

	

	

	
	@CCProperty(name = "fft")
	private CCFFT _myFFT = new CCFFT();
	@CCProperty(name = "spectogram")
	private CCSpectrogram _mySpectogram = new CCSpectrogram(_myFFT);

	@Override
	public void start(CCAnimator theAnimator) {
	}

	@Override
	public void init(CCGraphics g) {
	}


	@Override
	public void update(CCAnimator theAnimator) {
	}

	double myMax = 0;

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		
		_mySpectogram.draw(g);

		g.popMatrix();
	}

	public static void main(String[] args) {
		CCFFTOfflineAnalysis demo = new CCFFTOfflineAnalysis();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
