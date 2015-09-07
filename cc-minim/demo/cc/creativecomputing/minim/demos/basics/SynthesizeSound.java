package cc.creativecomputing.minim.demos.basics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.minim.CCMinimConnector;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;

/**
 * This sketch demonstrates how to create synthesized sound with Minim 
 * using an AudioOutput and an Oscil. An Oscil is a UGen object, 
 * one of many different types included with Minim. By using 
 * the numbers 1 thru 5, you can change the waveform being used
 * by the Oscil to make sound. These basic waveforms are the 
 * basis of much audio synthesis. 
 * 
 * For many more examples of UGens included with Minim, 
 * have a look in the Synthesis folder of the Minim examples.
 * <p>
 * For more information about Minim and additional features, 
 * visit http://code.compartmental.net/minim/
 */
public class SynthesizeSound extends CCApp {

	Minim minim;
	AudioOutput out;
	Oscil wave;

	@Override
	public void setup() {
		minim = new Minim(new CCMinimConnector());

		// use the getLineOut method of the Minim object to get an AudioOutput
		// object
		out = minim.getLineOut();

		// create a sine wave Oscil, set to 440 Hz, at 0.5 amplitude
		wave = new Oscil(440, 0.5f, Waves.SINE);
		// patch the Oscil to the output
		wave.patch(out);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
		g.color(255);
		g.strokeWeight(1);

		g.pushMatrix();
		g.translate(-width/2, -height/2);
		// draw the waveform of the output
		for (int i = 0; i < out.bufferSize() - 1; i++) {
			g.line(i, 50 - out.left.get(i) * 50, i + 1, 50 - out.left.get(i + 1) * 50);
			g.line(i, 150 - out.right.get(i) * 50, i + 1, 150 - out.right.get(i + 1) * 50);
		}

		// draw the waveform we are using in the oscillator
		g.color(128, 0, 0);
		g.strokeWeight(4);
		for (int i = 0; i < width - 1; ++i) {
			g.point(i, height / 2 - (height * 0.49f) * wave.getWaveform().value((float) i / width));
		}
		g.popMatrix();
	}
	@Override
	public void mouseMoved(CCMouseEvent theMouseEvent) {
		// usually when setting the amplitude and frequency of an Oscil
		// you will want to patch something to the amplitude and frequency
		// inputs
		// but this is a quick and easy way to turn the screen into
		// an x-y control for them.

		float amp = CCMath.map(mouseY, 0, height, 1, 0);
		wave.setAmplitude(amp);

		float freq = CCMath.map(mouseX, 0, width, 110, 880);
		wave.setFrequency(freq);
	}
	
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		switch (theKeyEvent.keyCode()) {
		case VK_1:
			wave.setWaveform(Waves.SINE);
			break;

		case VK_2:
			wave.setWaveform(Waves.TRIANGLE);
			break;

		case VK_3:
			wave.setWaveform(Waves.SAW);
			break;

		case VK_4:
			wave.setWaveform(Waves.SQUARE);
			break;

		case VK_5:
			wave.setWaveform(Waves.QUARTERPULSE);
			break;

		default:
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(SynthesizeSound.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
