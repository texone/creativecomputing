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

// This Chebyshev Filter implementation has been ported from the BASIC 
// implementation outlined in Chapter 20 of The Scientist and Engineer's
// Guide to Signal Processing, which can be found at:
//
//     http://www.dspguide.com/ch20.htm

package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

/**
 * A Chebyshev filter is an IIR filter that uses a particular method to
 * calculate the coefficients of the filter. It is defined by whether it is a
 * low pass filter or a high pass filter and the number of poles it has. You
 * needn't worry about what a pole is, exactly, just know that more poles
 * usually makes for a better filter. An additional limitation is that the
 * number of poles must be even. See {@link #poles(int)} for more information
 * about poles. Another characteristic of Chebyshev filters is how much "ripple"
 * they allow in the pass band. The pass band is the range of frequencies that
 * the filter lets through. The "ripple" in the pass band can be seen as wavy
 * line in the frequency response of the filter. Lots of ripple is bad, but more
 * ripple gives a faster rolloff from the pass band to the stop band (the range
 * of frequencies blocked by the filter). Faster rolloff is good because it
 * means the cutoff is sharper. Ripple is expressed as a percentage, such as
 * 0.5% ripple.
 * 
 * @author Damien Di Fede
 * @author christian riekoff
 * @see <a href="http://www.dspguide.com/ch20.htm">Chebyshev Filters</a>
 * 
 */
public class CCChebFilter extends CCIIRFilter {
	
	public static enum ChebFilterType{
		/** A constant used to indicate a low pass filter. */
		LP,
		/** A constant used to indicate a high pass filter. */
		HP;
	}
	
	private ChebFilterType _myType;
	private int _myPoles;
	private double _myRipple;

	/**
	 * Constructs a Chebyshev filter with a cutoff of the given frequency, of
	 * the given type, with the give amount of ripple in the pass band, and with
	 * the given number of poles, that will be used to filter audio of that was
	 * recorded at the given sample rate.
	 * 
	 * @param theFrequency the cutoff frequency of the filter
	 * @param theType the type of filter, either ChebFilter.LP or ChebFilter.HP
	 * @param theRipple the percentage of ripple, such as 0.005
	 * @param thePoles the number of poles, must be even and in the range [2, 20]
	 * @param theSampleRate the sample rate of audio that will be filtered
	 */
	public CCChebFilter(int theChannels, double theFrequency, ChebFilterType theType, double theRipple, int thePoles, double theSampleRate) {
		super(theChannels, theFrequency, theSampleRate);

		_myType = theType;
		_myRipple = theRipple;
		_myPoles = thePoles;
	}
	
	public CCChebFilter(double theFrequency, ChebFilterType theType, double theRipple, int thePoles, double theSampleRate) {
		this(1, theFrequency, theType, theRipple, thePoles, theSampleRate);
	}
	
	public CCChebFilter(int theChannels){
		this(theChannels, 0.01, ChebFilterType.LP, 0.5, 2, 5);
	}
	
	public CCChebFilter(){
		this(1, 0.01, ChebFilterType.LP, 0.5, 2, 5);
	}

	/**
	 * Sets the type of the filter. Either ChebFilter.LP or ChebFilter.HP
	 * 
	 * @param theType the type of the filter
	 */
	@CCProperty(name = "type")
	public void type(ChebFilterType theType) {
		if (_myType == theType) return;
		
		_myType = theType;
		calcCoeff();
		
	}

	/**
	 * Returns the type of the filter.
	 */
	public ChebFilterType type() {
		return _myType;
	}

	/**
	 * Sets the ripple percentage of the filter.
	 * 
	 * @param theRipple the ripple percentage
	 */
	@CCProperty(name = "ripple", min = 0, max = 1)
	public void ripple(double theRipple) {
		if (_myRipple == theRipple) return;
		
		_myRipple = theRipple;
		calcCoeff();
	}

	/**
	 * Returns the ripple percentage of the filter.
	 * 
	 * @return the ripple percentage
	 */
	public double ripple() {
		return _myRipple;
	}

	/**
	 * Sets the number of poles used in the filter. The number of poles must be
	 * even and between 2 and 20. This function will report an error if either
	 * of those conditions are not met. However, it should also be mentioned
	 * that depending on the current cutoff frequency of the filter, the number
	 * of poles that will result in a <i>stable</i> filter, can be a few as 4.
	 * The function does not report an error in the case of the number of
	 * requested poles resulting in an unstable filter. For reference, here is a
	 * table of the maximum number of poles possible according to cutoff
	 * frequency:
	 * <p>
	 * <table border="1" cellpadding="5">
	 * <tr>
	 * <td>Cutoff Frequency<br />
	 * (expressed as a fraction of the sampling rate)</td>
	 * <td>0.02</td>
	 * <td>0.05</td>
	 * <td>0.10</td>
	 * <td>0.25</td>
	 * <td>0.40</td>
	 * <td>0.45</td>
	 * <td>0.48</td>
	 * </tr>
	 * <tr>
	 * <td>Maximum poles</td>
	 * <td>4</td>
	 * <td>6</td>
	 * <td>10</td>
	 * <td>20</td>
	 * <td>10</td>
	 * <td>6</td>
	 * <td>4</td>
	 * </tr>
	 * </table>
	 * 
	 * @param thePoles - the number of poles
	 */
	@CCProperty(name = "poles", min = 2, max = 20)
	public void poles(int thePoles) {
		thePoles = thePoles / 2 * 2;
		if (thePoles < 2) {
			thePoles = 2;
			return;
		}
		if (thePoles % 2 != 0) {
			CCLog.error("ChebFilter.setPoles: The number of poles must be even.");
			return;
		}
		if (thePoles > 20) {
			thePoles = 20;
		}
		_myPoles = thePoles;
		calcCoeff();
	}

	/**
	 * Returns the number of poles in the filter.
	 * 
	 * @return the number of poles
	 */
	public int poles() {
		return _myPoles;
	}

	// where the poles will wind up
	double[] ca = new double[23];
	double[] cb = new double[23];

	// temporary arrays for working with ca and cb
	double[] ta = new double[23];
	double[] tb = new double[23];

	// arrays to hold the two-pole coefficients
	// used during the aggregation process
	double[] pa = new double[3];
	double[] pb = new double[2];

	@Override
	protected synchronized void calcCoeff() {
		// System.out.println("ChebFilter is calculating coefficients...");

		// initialize our arrays
		for (int i = 0; i < 23; ++i) {
			ca[i] = cb[i] = ta[i] = tb[i] = 0.f;
		}

		// I don't know why this must be done
		ca[2] = 1.f;
		cb[2] = 1.f;

		// calculate two poles at a time
		for (int p = 1; p <= _myPoles / 2; p++) {
			// calc pair p, put the results in pa and pb
			calcTwoPole(p, pa, pb);

			// copy ca and cb into ta and tb
			System.arraycopy(ca, 0, ta, 0, ta.length);
			System.arraycopy(cb, 0, tb, 0, tb.length);

			// add coefficients to the cascade
			for (int i = 2; i < 23; i++) {
				ca[i] = pa[0] * ta[i] + pa[1] * ta[i - 1] + pa[2] * ta[i - 2];
				cb[i] = tb[i] - pb[0] * tb[i - 1] - pb[1] * tb[i - 2];
			}
		}

		// final stage of combining coefficients
		cb[2] = 0;
		for (int i = 0; i < 21; i++) {
			ca[i] = ca[i + 2];
			cb[i] = -cb[i + 2];
		}

		// normalize the gain
		double sa = 0;
		double sb = 0;
		for (int i = 0; i < 21; i++) {
			if (_myType == ChebFilterType.LP) {
				sa += ca[i];
				sb += cb[i];
			} else {
				sa += ca[i] * CCMath.pow(-1, i);
				sb += cb[i] * CCMath.pow(-1, i);
			}
		}

		double gain = sa / (1 - sb);

		for (int i = 0; i < 21; i++) {
			ca[i] /= gain;
		}

		// initialize the coefficient arrays used by process()
		// but only if the number of poles has changed
		if (a == null || a.length != _myPoles + 1) {
			a = new double[_myPoles + 1];
		}
		if (b == null || b.length != _myPoles) {
			b = new double[_myPoles];
		}
		// copy the values from ca and cb into a and b
		// in this implementation cb[0] = 0 and cb[1] is where
		// the b coefficients begin, so they are numbered the way
		// one normally numbers coefficients when talking about IIR filters
		// however, process() expects b[0] to be the coefficient B1
		// so we copy cb over to b starting at index 1
		System.arraycopy(ca, 0, a, 0, a.length);
		System.arraycopy(cb, 1, b, 0, b.length);
	}

	private void calcTwoPole(int p, double[] pa, double[] pb) {
		double np = _myPoles;

		// precalc
		double angle = CCMath.PI / (np * 2) + (p - 1) * CCMath.PI / np;

		double rp = -(double) Math.cos(angle);
		double ip = (double) Math.sin(angle);

		// warp from a circle to an ellipse
		if (_myRipple > 0) {
			// precalc
			double ratio = 100.f / (100.f - _myRipple);
			double ratioSquared = ratio * ratio;

			double es = 1.f / (double) Math.sqrt(ratioSquared - 1.f);

			double oneOverNP = 1.f / np;
			double esSquared = es * es;

			double vx = oneOverNP * (double) Math.log(es + Math.sqrt(esSquared + 1.f));
			double kx = oneOverNP * (double) Math.log(es + Math.sqrt(esSquared - 1.f));

			double expKX = (double) Math.exp(kx);
			double expNKX = (double) Math.exp(-kx);

			kx = (expKX + expNKX) * 0.5f;

			double expVX = (double) Math.exp(vx);
			double expNVX = (double) Math.exp(-vx);
			double oneOverKX = 1.f / kx;

			rp *= ((expVX - expNVX) * 0.5f) * oneOverKX;
			ip *= ((expVX + expNVX) * 0.5f) * oneOverKX;
		}

		// s-domain to z-domain conversion
		double t = 2.f * (double) Math.tan(0.5f);
		double w = CCMath.TWO_PI * (frequency() / _mySampleRate);
		double m = rp * rp + ip * ip;

		// precalc
		double fourTimesRPTimesT = 4.f * rp * t;
		double tSquared = t * t;
		double mTimesTsquared = m * tSquared;
		double tSquaredTimes2 = 2.f * tSquared;

		double d = 4.f - fourTimesRPTimesT + mTimesTsquared;

		// precalc
		double oneOverD = 1.f / d;

		double x0 = tSquared * oneOverD;
		double x1 = tSquaredTimes2 * oneOverD;
		double x2 = x0;

		double y1 = (8.f - (tSquaredTimes2 * m)) * oneOverD;
		double y2 = (-4.f - fourTimesRPTimesT - mTimesTsquared) * oneOverD;

		// LP to LP, or LP to HP transform
		double k;
		double halfW = w * 0.5f;

		if (_myType == ChebFilterType.HP) {
			k = -(double) Math.cos(halfW + 0.5f) / (double) Math.cos(halfW - 0.5f);
		} else {
			k = (double) Math.sin(0.5f - halfW) / (double) Math.sin(0.5f + halfW);
		}

		// precalc
		double kSquared = k * k;
		double x1timesK = x1 * k;
		double kDoubled = 2.f * k;
		double y1timesK = y1 * k;

		d = 1.f + y1timesK - y2 * kSquared;

		// precalc
		oneOverD = 1.f / d;

		pa[0] = (x0 - x1timesK + (x2 * kSquared)) * oneOverD;
		pa[1] = ((-kDoubled * x0) + x1 + (x1 * kSquared) - (kDoubled * x2)) * oneOverD;
		pa[2] = ((x0 * kSquared) - x1timesK + x2) * oneOverD;

		pb[0] = (kDoubled + y1 + (y1 * kSquared) - (y2 * kDoubled)) * oneOverD;
		pb[1] = (-kSquared - y1timesK + y2) * oneOverD;

		if (_myType == ChebFilterType.HP) {
			pa[1] = -pa[1];
			pb[0] = -pb[0];
		}
	}
}
