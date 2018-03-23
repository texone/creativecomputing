package cc.creativecomputing.demo.minim.analysis;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.sound.CCAudioPlayer;
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
public class BandCenters extends CCGL2Adapter {

	CCSoundIO minim;
	CCAudioPlayer jingle;
	FFT fftLin;
	FFT fftLog;
	float height3;
	float height23;
	float spectrumScale = 2;
	
	int mouseX, mouseY;

	@Override
	public void init(CCGraphics g) {
		minim = new CCSoundIO();
		CCSoundIO.debugOn();

		height3 = g.height() / 3;
		height23 = 2 * g.height() / 3;

		jingle = CCSoundIO.loadFile(CCNIOUtil.dataPath("sound/jingle.mp3"), 1024);

		// loop the file
		jingle.loop();

		// create an FFT object that has a time-domain buffer the same size as
		// jingle's sample buffer
		// note that this needs to be a power of two
		// and that it means the size of the spectrum will be 1024.
		// see the online tutorial for more info.
		fftLin = new FFT(jingle.bufferSize(), jingle.sampleRate());

		// calculate the averages by grouping frequency bands linearly. use 30
		// averages.
		fftLin.linAverages(30);
		fftLog = new FFT(jingle.bufferSize(), jingle.sampleRate());

		// calculate averages based on a miminum octave width of 22 Hz
		// split each octave into three bands
		// this should result in 30 averages
		fftLog.logAverages(22, 3);

		g.rectMode(CCShapeMode.CORNERS);
		
		mouseListener().add(new CCMouseAdapter() {
			@Override
			public void mouseMoved(CCMouseEvent theMouseEvent) {
				mouseX = theMouseEvent.x();
				mouseY = theMouseEvent.y();
				
				CCLog.info(mouseX + " : " + mouseY);
			}
		});
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		float centerFrequency = 0;

		// perform a forward FFT on the samples in jingle's mix buffer
		// note that if jingle were a MONO file, this would be the same as using
		// jingle.left or jingle.right
		fftLin.forward(jingle.mix);

		// draw the full spectrum
		for (int i = 0; i < fftLin.specSize(); i++) {
			if (i == mouseX) {
				centerFrequency = fftLin.indexToFreq(i);
				g.color(255, 0, 0);
			} else {
				g.color(255);
			}
			g.line(i, height3, i, height3 - fftLin.getBand(i) * spectrumScale);
		}

		g.color(255);
		g.text("Spectrum Center Frequency: " + centerFrequency, 5, 15);

		// draw the linear averages
		int w = g.width() / fftLin.avgSize();
		for (int i = 0; i < fftLin.avgSize(); i++) {
			if (mouseX >= i * w && mouseX <= i * w + w) {
				centerFrequency = fftLin.getAverageCenterFrequency(i);
				g.color(255);
				g.text("Linear Average Center Frequency: " + centerFrequency, 5, 15 + height3);
				g.color(255, 0, 0);
			} else {
				g.color(255);
			}
			// draw a rectangle for each average, multiply the value by
			// spectrumScale so we can see it better
			g.rect(i * w, height23, i * w + w, height23 - fftLin.getAvg(i) * spectrumScale);
		}

		// draw the logarithmic averages
		fftLog.forward(jingle.mix);
		w = g.width() / fftLog.avgSize();
		for (int i = 0; i < fftLog.avgSize(); i++) {
			if (mouseX >= i * w && mouseX <= i * w + w) {
				centerFrequency = fftLog.getAverageCenterFrequency(i);
				g.color(255);
				g.text("Logarithmic Average Center Frequency: " + centerFrequency, 5, 15 + height23);
				g.color(255, 0, 0);
			} else {
				g.color(255);
			}
			// draw a rectangle for each average, multiply the value by
			// spectrumScale so we can see it better
			g.rect(i * w, g.height(), i * w + w, g.height() - fftLog.getAvg(i) * spectrumScale);
		}
		g.popMatrix();
	}

	public static void main(String[] args) {
		BandCenters demo = new BandCenters();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
