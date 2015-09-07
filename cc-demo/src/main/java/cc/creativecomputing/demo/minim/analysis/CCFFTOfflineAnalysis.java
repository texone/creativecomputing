package cc.creativecomputing.demo.minim.analysis;

import java.util.Arrays;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioPlayer;
import cc.creativecomputing.sound.CCAudioRecordingStream;
import cc.creativecomputing.sound.CCAudioSample;
import cc.creativecomputing.sound.CCFFT;
import cc.creativecomputing.sound.CCMultiChannelBuffer;
import cc.creativecomputing.sound.CCSoundIO;
import ddf.minim.analysis.FFT;

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

	double[][] spectra;
	double[][] averages;
	
	@CCProperty(name = "fft")
	private CCFFT _myFFT = new CCFFT();

	void analyzeUsingAudioSample() {
		CCAudioSample jingle = CCSoundIO.loadSample(CCNIOUtil.dataPath("sound/jingle.mp3"), 2048);

		// get the left channel of the audio as a float array
		// getChannel is defined in the interface BuffereAudio,
		// which also defines two constants to use as an argument
		// BufferedAudio.LEFT and BufferedAudio.RIGHT
		float[] leftChannel = jingle.getChannel(CCAudioSample.LEFT);

		// then we create an array we'll copy sample data into for the FFT
		// object
		// this should be as large as you want your FFT to be. generally
		// speaking, 1024 is probably fine.
		int fftSize = 1024;
		float[] fftSamples = new float[fftSize];
		_myFFT.setup(fftSize, jingle.sampleRate());

		// now we'll analyze the samples in chunks
		int totalChunks = (leftChannel.length / fftSize) + 1;

		// allocate a 2-dimentional array that will hold all of the spectrum
		// data for all of the chunks.
		// the second dimension if fftSize/2 because the spectrum size is always
		// half the number of samples analyzed.
		spectra = new double [totalChunks][fftSize / 2];

		for (int chunkIdx = 0; chunkIdx < totalChunks; ++chunkIdx) {
			int chunkStartIndex = chunkIdx * fftSize;

			// the chunk size will always be fftSize, except for the
			// last chunk, which will be however many samples are left in source
			int chunkSize = CCMath.min(leftChannel.length - chunkStartIndex, fftSize);

			// copy first chunk into our analysis array
			CCArrayUtil.arraycopy(leftChannel, // source of the copy
					chunkStartIndex, // index to start in the source
					fftSamples, // destination of the copy
					0, // index to copy to
					chunkSize // how many samples to copy
					);

			// if the chunk was smaller than the fftSize, we need to pad the
			// analysis buffer with zeroes
			if (chunkSize < fftSize) {
				// we use a system call for this
				Arrays.fill(fftSamples, chunkSize, fftSamples.length - 1, 0.0f);
			}

			// now analyze this buffer
			_myFFT.forward(fftSamples);

			// and copy the resulting spectrum into our spectra array
			for (int i = 0; i < 512; ++i) {
				spectra[chunkIdx][i] = _myFFT.spectrum()[i];
			}
		}

		jingle.close();
	}

	@CCProperty(name = "analyze stream")
	void analyzeUsingAudioRecordingStream() {
		int fftSize = 512;
		CCAudioRecordingStream stream = CCSoundIO.loadFileStream(CCNIOUtil.dataPath("sound/fair1939.wav"), fftSize,//01 Verses.wav
				false);

		// tell it to "play" so we can read from it.
		stream.play();

		// create the fft we'll use for analysis
		
		
		

		// create the buffer we use for reading from the stream
		CCMultiChannelBuffer buffer = new CCMultiChannelBuffer(fftSize, stream.getFormat().getChannels());

		// figure out how many samples are in the stream so we can allocate the
		// correct number of spectra
		int totalSamples = (int) ((stream.getMillisecondLength() / 1000.0) * stream.getFormat().getSampleRate());

		// now we'll analyze the samples in chunks
		int totalChunks = (totalSamples / fftSize) + 1;
		CCLog.info("Analyzing " + totalSamples + " samples for total of " + totalChunks + " chunks.");

		_myFFT.setup(fftSize, stream.getFormat().getSampleRate());
		// allocate a 2-dimentional array that will hold all of the spectrum
		// data for all of the chunks.
		// the second dimension if fftSize/2 because the spectrum size is always
		// half the number of samples analyzed.
		spectra = new double[totalChunks][fftSize / 2];
		if(_myFFT.averages() != null)averages = new double[totalChunks][_myFFT.averages().length];
		

		for (int chunkIdx = 0; chunkIdx < totalChunks; ++chunkIdx) {
			CCLog.info("Chunk " + chunkIdx);
			CCLog.info("  Reading...");
			stream.read(buffer);
			CCLog.info("  Analyzing...");

			// now analyze the left channel
			_myFFT.forward(buffer.getChannel(0));

			// and copy the resulting spectrum into our spectra array
			CCLog.info("  Copying...");
			for (int i = 0; i < 256; ++i) {
				spectra[chunkIdx][i] = _myFFT.spectrum()[i];
			}
			if(_myFFT.averages() != null && averages != null){
				for (int i = 0; i < _myFFT.averagesSize(); ++i) {
					averages[chunkIdx][i] = _myFFT.averages()[i];
				}
			}
		}
	}

	@CCProperty(name = "amp", min = 0, max = 3)
	private double _cAmp = 1;
	
	@CCProperty(name = "averages")
	private boolean _cAverages = false;

	@Override
	public void start(CCAnimator theAnimator) {
		// There are two ways you can do offline analysis:
		// 1. Loading audio data fully into memory using an AudioSample and then
		// analyzing a channel
		// analyzeUsingAudioSample();

		// 2. Loading an AudioRecordingStream and reading in a buffer at a time.
		// This second option is available starting with Minim Beta 2.1.0
		analyzeUsingAudioRecordingStream();
	}

	@Override
	public void init(CCGraphics g) {
	}

	// how many units to step per second
	float cameraStep = 100;
	// our current z position for the camera
	float cameraPos = 0;
	// how far apart the spectra are so we can loop the camera back
	float spectraSpacing = 50;

	@Override
	public void update(CCAnimator theAnimator) {
		double dt = 1.0f / theAnimator.frameRate();

		cameraPos += cameraStep * dt;

		// jump back to start position when we get to the end
		if (cameraPos > spectra.length * spectraSpacing) {
			cameraPos = 0;
		}
	}

	double myMax = 0;

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width / 2, -g.height / 2);
		float centerFrequency = 0;
		g.beginShape(CCDrawMode.POINTS);
		// render the spectra going back into the screen
		
		if(_cAverages){
			
			for (int s = 0; averages != null && s < averages.length; s++) {
				// don't draw spectra that are behind the camera or too far away

				for (int i = 0; i < averages[s].length - 1; ++i) {
					myMax = CCMath.max(averages[s][i], myMax);
					g.color(averages[s][i] * _cAmp);
					g.vertex(s, i);

				}
			}
		}else{
			for (int s = 0; s < spectra.length; s++) {
				// don't draw spectra that are behind the camera or too far away

				for (int i = 0; i < spectra[s].length - 1; ++i) {
					myMax = CCMath.max(spectra[s][i], myMax);
					g.color(spectra[s][i] * _cAmp);
					g.vertex(s, i);

				}
			}
		}
		
		
		g.endShape();

		g.color(255);
		g.text("Spectrum Center Frequency: " + centerFrequency, 5, 15);

		g.popMatrix();
	}

	public static void main(String[] args) {
		CCFFTOfflineAnalysis demo = new CCFFTOfflineAnalysis();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
