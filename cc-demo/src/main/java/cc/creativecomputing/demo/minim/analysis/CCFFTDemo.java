package cc.creativecomputing.demo.minim.analysis;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.gl.app.events.CCMouseAdapter;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.sound.CCAudioPlayer;
import cc.creativecomputing.sound.CCFFT;
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
public class CCFFTDemo extends CCGL2Adapter {

	CCSoundIO minim;
	CCAudioPlayer jingle;
	
	@CCProperty(name = "fft")
	private CCFFT _myFFT;
	
	@CCProperty(name = "spectrum scale", min = 0, max = 10)
	private double _cSpectrumScale = 10;
	
	@CCProperty(name = "average scale", min = 0, max = 10)
	private double _cAverageScale = 10;
	
	int mouseX, mouseY;
	
	@Override
	public void start(CCAnimator theAnimator) {
		minim = new CCSoundIO();
		
		jingle = CCSoundIO.loadFile(CCNIOUtil.dataPath("sound/fair1939.wav"), 1024);
		
		// loop the file
//		jingle.loop();
		
		_myFFT = new CCFFT();
		
		CCLog.info("start");
	}

	@Override
	public void init(CCGraphics g) {
		
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
	public void update(CCAnimator theAnimator) {
		// perform a forward FFT on the samples in jingle's mix buffer
		// note that if jingle were a MONO file, this would be the same as using
		// jingle.left or jingle.right
		jingle.cue((int)CCMath.random(jingle.length()));
		_myFFT.forward(jingle);
		
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(255);

		// use the mix buffer to draw the waveforms.
		g.pushMatrix();
		g.translate(-g.width() / 2, -g.height() / 2);
		float centerFrequency = 0;

		double[] mySpectrum = _myFFT.spectrum();
		
		// draw the full spectrum
		for (int i = 0; i < mySpectrum.length; i++) {
			float myX = CCMath.map(i, 0, mySpectrum.length - 1, 0, g.width());
			g.line(myX, 0, myX, mySpectrum[i] * (_myFFT.isNormalized() ? _cSpectrumScale * 20 : _cSpectrumScale));
		}

		double[] myAverages = _myFFT.averages();
		if(myAverages != null){
			double myWidth = g.width() / (double)myAverages.length;
			for (int i = 0; i < myAverages.length; i++) {
				float myX = CCMath.map(i, 0, myAverages.length - 1, 0, g.width());
				g.rect(myX, g.height()/2, myWidth, myAverages[i] * (_myFFT.isNormalized() ? _cAverageScale * 20 : _cAverageScale));
			}
		}

		g.color(255);
		g.text("Spectrum Center Frequency: " + centerFrequency, 5, 15);

		g.popMatrix();
	}

	public static void main(String[] args) {
		CCFFTDemo demo = new CCFFTDemo();
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
