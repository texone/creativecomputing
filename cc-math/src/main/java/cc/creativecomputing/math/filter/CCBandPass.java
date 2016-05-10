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

/**
 * A band pass filter is a filter that filters out all frequencies except for
 * those in a band centered on the current frequency of the filter.
 * 
 * @author Damien Di Fede
 * @author christianr
 */
public class CCBandPass extends CCIIRFilter {
	private double _myBandWidth;

	/**
	 * Constructs a band pass filter with the requested center frequency,
	 * bandwidth and sample rate.
	 * 
	 * @param theFrequency the center frequency of the band to pass (in Hz)
	 * @param theBandWidth the width of the band to pass (in Hz)
	 * @param theSampleRate the sample rate of audio that will be filtered by this
	 *            filter
	 */
	public CCBandPass(double theFrequency, double theBandWidth, double theSampleRate) {
		super(theFrequency, theSampleRate);
		bandWidth(theBandWidth);
	}

	/**
	 * Sets the band width of the filter. Doing this will cause the coefficients
	 * to be recalculated.
	 * 
	 * @param theBandWidth the band width (in Hz)
	 */
	public void bandWidth(double theBandWidth) {
		_myBandWidth = theBandWidth / _mySampleRate;
		calcCoeff();
	}

	/**
	 * Returns the band width of this filter.
	 * 
	 * @return the band width (in Hz)
	 */
	public double bandWidth() {
		return _myBandWidth * _mySampleRate;
	}

	@Override
	protected void calcCoeff() {
		double R = 1 - 3 * _myBandWidth;
		double fracFreq = frequency() / _mySampleRate;
		double T = 2 * (double) Math.cos(2 * Math.PI * fracFreq);
		double K = (1 - R * T + R * R) / (2 - T);
		a = new double[] { 1 - K, (K - R) * T, R * R - K };
		b = new double[] { R * T, -R * R };
	}
}
