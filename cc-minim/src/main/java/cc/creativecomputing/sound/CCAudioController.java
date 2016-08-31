/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cc.creativecomputing.sound;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

import cc.creativecomputing.math.CCMath;

/**
 * <code>{@linkplain CCAudioController}</code> is the base class of all Minim
 * classes that deal with audio I/O. It provides control over the underlying
 * <code>{@linkplain DataLine}</code>, which is a low-level JavaSound class that
 * talks directly to the audio hardware of the computer. This means that you can
 * make changes to the audio without having to manipulate the samples directly.
 * The downside to this is that when outputting sound to the system (such as
 * with an <code>{@linkplain AudioOutput}</code>), these changes will not be
 * present in the samples made available to your program.
 * <p>
 * The {@link #volume()}, {@link #gain()}, {@link #pan()}, and
 * {@link #balance()} methods return objects of type
 * <code>{@linkplain FloatControl}</code>, which is a class defined by the
 * JavaSound API. A <code>{@linkplain FloatControl}</code> represents a control
 * of a line that holds a <code>float</code> value. This value has an associated
 * maximum and minimum value (such as between -1 and 1 for pan), and also a unit
 * type (such as dB for gain). You should refer to the {@link FloatControl}
 * Javadoc for the full description of the methods available.
 * <p>
 * Not all controls are available on all objects. Before calling the methods
 * mentioned above, you should call
 * {@link #hasControl(javax.sound.sampled.Control.Type)} with the control type
 * you want to use. Alternatively, you can use the <code>get</code> and
 * <code>set</code> methods, which will simply do nothing if the control you are
 * trying to manipulate is not available.
 * 
 * @author Damien Di Fede
 * 
 */
public class CCAudioController {
	/**
	 * The volume control type.
	 */
	protected static FloatControl.Type VOLUME = FloatControl.Type.VOLUME;

	/**
	 * The gain control type.
	 */
	protected static FloatControl.Type GAIN = FloatControl.Type.MASTER_GAIN;

	/**
	 * The balance control type.
	 */
	private static FloatControl.Type BALANCE = FloatControl.Type.BALANCE;

	/**
	 * The pan control type.
	 */
	private static FloatControl.Type PAN = FloatControl.Type.PAN;

	/**
	 * The mute control type.
	 */
	private static BooleanControl.Type MUTE = BooleanControl.Type.MUTE;

	private Map<Control.Type, Control> _myControlMap = new HashMap<>();
	// the starting value for shifting
	private ValueShifter vshifter, gshifter, bshifter, pshifter;
	private boolean vshift, gshift, bshift, pshift;

	/**
	 * Constructs a <code>Controller</code> for the given <code>Line</code>.
	 * 
	 * @param cntrls an array of Controls that this Controller will manipulate
	 */
	public CCAudioController(Control[] theControls) {
		for (Control myControl : theControls) {
			_myControlMap.put(myControl.getType(), myControl);
		}
		vshift = gshift = bshift = pshift = false;
	}

	// for line reading/writing classes to alert the controller
	// that a new buffer has been read/written
	void update() {
		if (vshift) {
			volume(vshifter.value());
			if (vshifter.done())
				vshift = false;
		}

		if (gshift) {
			gain(gshifter.value());
			if (gshifter.done())
				gshift = false;
		}

		if (bshift) {
			balance(bshifter.value());
			if (bshifter.done())
				bshift = false;
		}

		if (pshift) {
			pan(pshifter.value());
			if (pshifter.done())
				pshift = false;
		}
	}

	// a small class to interpolate a value over time
	class ValueShifter {
		private float tstart, tend, vstart, vend;

		public ValueShifter(float vs, float ve, int t) {
			tstart = (int) System.currentTimeMillis();
			tend = tstart + t;
			vstart = vs;
			vend = ve;
		}

		public float value() {
			int millis = (int) System.currentTimeMillis();
			float norm = (float) (millis - tstart) / (tend - tstart);
			float range = (float) (vend - vstart);
			return vstart + range * norm;
		}

		public boolean done() {
			return (int) System.currentTimeMillis() > tend;
		}
	}

	/**
	 * 
	 * Prints the available controls and their ranges to the console. Not all
	 * lines have all of the controls available on them so this is a way to find
	 * out what is available.
	 * 
	 */
	public void printControls() {
		if (_myControlMap.size() <= 0) {
			System.out.println("There are no controls available for this line.");
			return;
		}

		System.out.println("Available controls are:");
		for (Control.Type type : _myControlMap.keySet()) {
			System.out.print("  " + type.toString());
			if (type == VOLUME || type == GAIN || type == BALANCE || type == PAN) {
				FloatControl fc = (FloatControl) _myControlMap.get(type);
				String shiftSupported = "does";
				if (fc.getUpdatePeriod() == -1) {
					shiftSupported = "doesn't";
				}
				System.out.println(", which has a range of " + fc.getMaximum() + " to " + fc.getMinimum() + " and "
						+ shiftSupported + " support shifting.");
			} else {
				System.out.println("");
			}
		}
	}

	/**
	 * Returns whether or not the particular control type is supported by the
	 * Line being controlled.
	 * 
	 * @see #VOLUME
	 * @see #GAIN
	 * @see #BALANCE
	 * @see #PAN
	 * @see #SAMPLE_RATE
	 * @see #MUTE
	 * 
	 * @return true if the control is available
	 */
	public boolean hasControl(Control.Type type) {
		return _myControlMap.containsKey(type);
	}

	/**
	 * Returns true if the sound is muted.
	 * 
	 * @return the current mute state
	 * 
	 * @see #isMuted(boolean)
	 */
	public boolean isMuted() {
		return value(MUTE);
	}

	public void isMuted(boolean theIsMuted) {
		value(MUTE, theIsMuted);
	}

	private boolean value(BooleanControl.Type type) {
		boolean v = false;
		if (hasControl(type)) {
			BooleanControl c = (BooleanControl) _myControlMap.get(type);
			v = c.getValue();
		} else {
			throw new CCSoundException(type.toString() + " is not supported.");
		}
		return v;
	}

	private void value(BooleanControl.Type type, boolean v) {
		if (hasControl(type)) {
			BooleanControl c = (BooleanControl) _myControlMap.get(type);
			c.setValue(v);
		} else {
			throw new CCSoundException(type.toString() + " is not supported.");
		}
	}

	private float value(FloatControl.Type type) {
		float v = 0;
		if (hasControl(type)) {
			FloatControl c = (FloatControl) _myControlMap.get(type);
			v = c.getValue();
		} else {
			throw new CCSoundException(type.toString() + " is not supported.");
		}
		return v;
	}

	private void value(FloatControl.Type type, float v) {
		if (!hasControl(type))
			return;

		FloatControl c = (FloatControl) _myControlMap.get(type);
		c.setValue(CCMath.constrain(v, c.getMinimum(), c.getMaximum()));
	}

	/**
	 * Returns the current volume. If a volume control is not available, this
	 * returns 0. Note that the volume is not the same thing as the
	 * <code>level()</code> of an AudioBuffer!
	 * 
	 * @return the current volume or zero if a volume control is unavailable
	 * 
	 * @see #volume(float)
	 * @see #shiftVolume(float, float, int)
	 */
	public float volume() {
		return value(VOLUME);
	}

	/**
	 * Sets the volume. If a volume control is not available, this does nothing.
	 * 
	 * @param value float: the new value for the volume, usually in the range
	 *            [0,1].
	 * 
	 * @see #volume()
	 * @see #shiftVolume(float, float, int)
	 */
	public void volume(float value) {
		value(VOLUME, value);
	}

	/**
	 * Transitions the volume from one value to another.
	 * 
	 * @param from float: the starting volume
	 * @param to float: the ending volume
	 * @param millis int: the length of the transition in milliseconds
	 * 
	 * @see #volume()
	 * @see #volume(float)
	 */
	public void shiftVolume(float from, float to, int millis) {
		if (hasControl(VOLUME)) {
			volume(from);
			vshifter = new ValueShifter(from, to, millis);
			vshift = true;
		}
	}

	/**
	 * Returns the current gain. If a gain control is not available, this
	 * returns 0. Note that the gain is not the same thing as the
	 * <code>{@linkplain CCAudioBuffer#level()}</code> of an AudioBuffer! Gain describes the current volume
	 * of the sound in decibels, which is a logarithmic, rather than linear,
	 * scale. A gain of 0dB means the sound is not being amplified or
	 * attenuated. Negative gain values will reduce the volume of the sound, and
	 * positive values will increase it.
	 * <p>
	 * See: <a
	 * href="http://wikipedia.org/wiki/Decibel">http://wikipedia.org/wiki
	 * /Decibel</a>
	 * 
	 * @return float: the current gain or zero if a gain control is unavailable.
	 *         the gain is expressed in decibels.
	 * 
	 * @see #gain(float)
	 * @see #shiftGain(float, float, int)
	 */
	public float gain() {
		return value(GAIN);
	}

	/**
	 * Sets the gain. If a gain control is not available, this does nothing.
	 * 
	 * @param value float: the new value for the gain, expressed in decibels.
	 * 
	 * @see #gain()
	 * @see #shiftGain ()
	 */
	public void gain(float value) {
		value(GAIN, value);
	}

	/**
	 * Transitions the gain from one value to another.
	 * 
	 * @param from float: the starting gain
	 * @param to float: the ending gain
	 * @param millis int: the length of the transition in milliseconds
	 * 
	 * @see #gain()
	 * @see #gain(float)
	 */
	public void shiftGain(float from, float to, int millis) {
		if (hasControl(GAIN)) {
			gain(from);
			gshifter = new ValueShifter(from, to, millis);
			gshift = true;
		}
	}

	/**
	 * Returns the current balance. This will be in the range [-1, 1]. Usually
	 * balance will only be available for stereo audio sources, because it
	 * describes how much attenuation should be applied to the left and right
	 * channels. If a balance control is not available, this will do nothing.
	 * 
	 * @return float: the current balance or zero if a balance control is
	 *         unavailable
	 * 
	 * @see #balance(float)
	 * @see #shiftBalance(float, float, int)
	 */
	public float balance() {
		return value(BALANCE);
	}

	/**
	 * Sets the balance. The value should be in the range [-1, 1]. If a balance
	 * control is not available, this will do nothing.
	 * 
	 * @param value float: the new value for the balance
	 * 
	 * @see #balance()
	 * @see #shiftBalance(float, float, int)
	 */
	public void balance(float value) {
		value(BALANCE, value);
	}

	/**
	 * Transitions the balance from one value to another.
	 * 
	 * @param from float: the starting balance
	 * @param to float: the ending balance
	 * @param millis int: the length of the transition in milliseconds
	 * 
	 * @see #balance()
	 * @see #balance(float)
	 */
	public void shiftBalance(float from, float to, int millis) {
		if (hasControl(BALANCE)) {
			balance(from);
			bshifter = new ValueShifter(from, to, millis);
			bshift = true;
		}
	}

	/**
	 * Returns the current pan. Usually pan will be only be available on mono
	 * audio sources because it describes a mono signal's position in a stereo
	 * field. This will be in the range [-1, 1], where -1 will place the sound
	 * only in the left speaker and 1 will place the sound only in the right
	 * speaker.
	 * 
	 * @return float: the current pan or zero if a pan control is unavailable
	 * 
	 * @see #pan(float)
	 * @see #shiftPan(float, float, int)
	 */
	public float pan() {
		return value(PAN);
	}

	/**
	 * Sets the pan. The provided value should be in the range [-1, 1]. If a pan
	 * control is not present, this does nothing.
	 * 
	 * @param value float: the new value for the pan
	 * 
	 * @see #pan()
	 * @see #shiftPan(float, float, int)
	 */
	public void pan(float value) {
		value(PAN, value);
	}

	/**
	 * Transitions the pan from one value to another.
	 * 
	 * @param from float: the starting pan
	 * @param to float: the ending pan
	 * @param millis int: the length of the transition in milliseconds
	 * 
	 * @see #pan()
	 * @see #pan(float)
	 */
	public void shiftPan(float from, float to, int millis) {
		if (hasControl(PAN)) {
			pan(from);
			pshifter = new ValueShifter(from, to, millis);
			pshift = true;
		}
	}
}
