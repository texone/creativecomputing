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

import cc.creativecomputing.core.logging.CCLog;

/**
 * <code>Convolver</code> is an effect that convolves a signal with a kernel.
 * The kernel can be thought of as the impulse response of an audio filter, or
 * simply as a set of weighting coefficients. <code>Convolver</code> performs
 * brute-force convolution, meaning that it is slow, relatively speaking.
 * However, the algorithm is very straighforward. Each output sample
 * <code>i</code> is calculated by multiplying each kernel value <code>j</code>
 * with the input sample <code>i - j</code> and then summing the resulting
 * values. The output will be <code>kernel.length + signal.length - 1</code>
 * samples long, so the extra samples are stored in an overlap array. The
 * overlap array from the previous signal convolution is added into the
 * beginning of the output array, which results in a output signal without pops.
 * 
 * @author Damien Di Fede
 * @see <a href="http://www.dspguide.com/ch6.htm">Convolution</a>
 * 
 */
public class CCConvolver extends CCFilter {
	protected double[] kernel;
	protected double[] outputL;
	protected double[] overlapL;
	protected int sigLen;

	/**
	 * Constructs a Convolver with the kernel <code>k</code> that expects buffer
	 * of length <code>sigLength</code>.
	 * 
	 * @param k the kernel of the filter
	 * @param sigLength the length of the buffer that will be convolved with the
	 *            kernel
	 */
	public CCConvolver(double[] k, int sigLength) {
		sigLen = sigLength;
		kernel(k);
	}

	/**
	 * Sets the kernel to <code>k</code>. The values in <code>k</code> are
	 * copied so it is not possible to alter the kernel after it has been set
	 * except by setting it again.
	 * 
	 * @param k the kernel to use
	 */
	public void kernel(double[] k) {
		kernel = new double[k.length];
		System.arraycopy(k, 0, kernel, 0, k.length);
		outputL = new double[sigLen + kernel.length - 1];
		overlapL = new double[outputL.length - sigLen];
	}

	@Override
	public void process(int theChannel, double[] theData, double theTime) {
		if(_myBypass)return;
		if (theData.length != sigLen) {
			CCLog.error("Convolver.process: signal.length does not equal sigLen, no processing will occurr.");
			return;
		}
		// store the overlap from the previous convolution
		System.arraycopy(outputL, theData.length, overlapL, 0, overlapL.length);
		// convolve kernel with signal and put the result in outputL
		for (int i = 0; i < outputL.length; i++) {
			outputL[i] = 0;
			for (int j = 0; j < kernel.length; j++) {
				if (i - j < 0 || i - j > theData.length)
					continue;
				outputL[i] += kernel[j] * theData[i - j];
			}
		}
		// copy the result into signal
		System.arraycopy(outputL, 0, theData, 0, theData.length);
		// add the overlap from the previous convolution to the beginning of
		// signal
		for (int i = 0; i < overlapL.length; i++) {
			theData[i] += overlapL[i];
		}
	}
}
