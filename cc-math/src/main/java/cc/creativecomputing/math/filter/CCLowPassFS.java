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

package cc.creativecomputing.math.filter;

import cc.creativecomputing.math.CCMath;


/**
 * LowPassFS is a four stage low pass filter. It becomes unstable if the cutoff
 * frequency is set below 60 Hz, so it will report and error and set the cutoff
 * to 60 Hz if you try to set it lower.
 * 
 * @author Damien Di Fede
 * @author christianr
 * 
 */
public class CCLowPassFS extends CCIIRFilter {
	/**
	 * Constructs a low pass filter with a cutoff frequency of <code>freq</code>
	 * that will be used to filter audio recorded at <code>sampleRate</code>.
	 * 
	 * @param theFrequency the cutoff frequency
	 * @param theSampleRate the sample rate of the audio that will be filtered
	 */
	public CCLowPassFS(double theFrequency, double theSampleRate) {
		super(theFrequency, theSampleRate);
	}

	@Override
	public boolean validFreq(double f) {
//		if (f < 60) {
//			Minim.error("This filter quickly becomes unstable below 60 Hz, setting frequency to 60 Hz.");
//			return false;
//		}
		return true;
	}

	@Override
	protected void calcCoeff() {
		double freqFrac = _myFrequency / _mySampleRate;
		double x = Math.exp(-14.445 * freqFrac);
		a = new double[] { CCMath.pow(1 - x, 4) };
		b = new double[] { 4 * x, -6 * x * x, 4 * x * x * x, -x * x * x * x };
	}
}
