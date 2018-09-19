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

import cc.creativecomputing.core.CCProperty;

/**
 * An Infinite Impulse Response, or IIR, filter is a filter that uses a set of
 * coefficients and previous filtered values to filter a stream of audio. It is
 * an efficient way to do digital filtering. IIRFilter is a general IIRFilter
 * that simply applies the filter designated by the filter coefficients so that
 * sub-classes only have to dictate what the values of those coefficients are by
 * defining the <code>{@linkplain #calcCoeff()}</code> function. When filling the coefficient
 * arrays, be aware that <code>b[0]</code> corresponds to
 * <code>b<sub>1</sub></code>.
 * 
 * @author Damien Di Fede
 * 
 */
public abstract class CCIIRFilter extends CCFilter{
	public double _myFrequency;

	/** The a coefficients. */
	protected double[] a;
	/** The b coefficients. */
	protected double[] b;

	/**
	 * The input values to the left of the output value currently being
	 * calculated.
	 */
	private double[][] in;
	
	/** The previous output values. */
	private double[][] out;

	private double _myPreviousFrequency;

	/**
	 * Constructs an IIRFilter with the given cutoff frequency that will be used
	 * to filter audio recorded at <code>sampleRate</code>.
	 * 
	 * @param freq the cutoff frequency
	 * @param sampleRate the sample rate of audio to be filtered
	 */
	public CCIIRFilter(int theChannels, double freq, double sampleRate) {
		super();
		_myChannels = theChannels;
		_mySampleRate = sampleRate;

		_myFrequency = freq;

		// set our center frequency

		// force use to calculate coefficients the first time we generate
		_myPreviousFrequency = -1.f;
	}
	
	public CCIIRFilter(double freq, double sampleRate) {
		this(1, freq, sampleRate);
	}

	/**
	 * Initializes the in and out arrays based on the number of coefficients
	 * being used.
	 * 
	 */
	private final void initArrays() {
		int memSize = (a.length >= b.length) ? a.length : b.length;
		in = new double[_myChannels][memSize];
		out = new double[_myChannels][memSize];
	}

	@Override
	public synchronized double process(int theChannel, double signal, double theTime) {
		
		// make sure our coefficients are up-to-date
		if (_myFrequency != _myPreviousFrequency) {
			calcCoeff();
			initArrays();
			_myPreviousFrequency = _myFrequency;
		}

		// make sure we have enough filter buffers
		if (in == null || in.length != _myChannels || (in[theChannel].length < a.length && in[theChannel].length < b.length)) {
			initArrays();
		}
		
		// apply the filter to the sample value in each channel
		System.arraycopy(in[theChannel], 0, in[theChannel], 1, in[theChannel].length - 1);
		in[theChannel][0] = signal;
		double y = 0;
		for (int ci = 0; ci < a.length; ci++) {
			y += a[ci] * in[theChannel][ci];
		}
		for (int ci = 0; ci < b.length; ci++) {
			y += b[ci] * out[theChannel][ci];
		}
		System.arraycopy(out[theChannel], 0, out[theChannel], 1, out[theChannel].length - 1);
			
		if(Double.isNaN(y)){
			y = 0;
		}
		out[theChannel][0] = y;
		
		if(_myBypass)return signal;
		return y;
		
	}

	/**
	 * Sets the cutoff/center frequency of the filter. Doing this causes the
	 * coefficients to be recalculated.
	 * 
	 * @param theFrequency the new cutoff/center frequency (in Hz).
	 */
	@CCProperty(name = "freqency", min = 0, max = 1)
	public final void frequency(double theFrequency) {
		// no need to recalc if the cutoff isn't actually changing
		if (validFreq(theFrequency) && theFrequency != _myFrequency) {
			_myPreviousFrequency = theFrequency;
			_myFrequency = theFrequency;
			calcCoeff();
		}
	}
	
	@Override
	public void sampleRate(double theSampleRate){
		super.sampleRate(theSampleRate);
		calcCoeff();
	}

	/**
	 * Returns true if the frequency is valid for this filter. Subclasses can
	 * override this method if they want to limit center frequencies to certain
	 * ranges to avoid becoming unstable. The default implementation simply
	 * makes sure that <code>f</code> is positive.
	 * 
	 * @param theFrequency the frequency (in Hz) to validate
	 * @return true if <code>f</code> is a valid frequency for this filter
	 */
	public boolean validFreq(double theFrequency) {
		return theFrequency > 0;
	}

	/**
	 * Returns the cutoff frequency (in Hz).
	 * 
	 * @return the current cutoff frequency (in Hz).
	 */
	@CCProperty(name = "freqency")
	public final double frequency() {
		return _myFrequency;
	}

	/**
	 * Calculates the coefficients of the filter using the current cutoff
	 * frequency. To make your own IIRFilters, you must extend IIRFilter and
	 * implement this function. The frequency is expressed as a fraction of the
	 * sample rate. When filling the coefficient arrays, be aware that
	 * <code>b[0]</code> corresponds to the coefficient
	 * <code>b<sub>1</sub></code>.
	 * 
	 */
	protected abstract void calcCoeff();

	/**
	 * Prints the current values of the coefficients to the console.
	 * 
	 */
	public final void printCoeff() {
		System.out.println("Filter coefficients: ");
		if (a != null) {
			for (int i = 0; i < a.length; i++) {
				System.out.print("  A" + i + ": " + a[i]);
			}
		}
		System.out.println();
		if (b != null) {
			for (int i = 0; i < b.length; i++) {
				System.out.print("  B" + (i + 1) + ": " + b[i]);
			}
			System.out.println();
		}
	}
}
