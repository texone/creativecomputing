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
 * LowPassSP is a single pole low pass filter. It is not super great, but it
 * gets the job done.
 * 
 * @author Damien Di Fede
 * @author christianr
 *
 */
public class CCLowPassSP extends CCIIRFilter {
	/**
	 * Constructs a low pass filter with a cutoff frequency of <code>freq</code>
	 * that will be used to filter audio recorded at <code>sampleRate</code>.
	 * 
	 * @param theFrequency the cutoff frequency
	 * @param theSampleRate the sample rate of the audio that will be filtered
	 */
	public CCLowPassSP(double theFrequency, double theSampleRate) {
		super(theFrequency, theSampleRate);
	}

	@Override
	protected void calcCoeff() {
		double fracFreq = frequency() / _mySampleRate;
		double x = CCMath.exp(-2 * CCMath.PI * fracFreq);
		a = new double[] { 1 - x };
		b = new double[] { x };
	}
}
