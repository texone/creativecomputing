/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.math.signal;

import java.util.Random;

import cc.creativecomputing.math.CCMath;

public class CCFarbrauschNoise extends CCSignal {

	private static final int PERLIN_Y_WRAP_B = 4;

	private static final int PERLIN_Y_WRAP = 1 << PERLIN_Y_WRAP_B;

	private static final int PERLIN_Z_WRAP_B = 8;

	private static final int PERLIN_Z_WRAP = 1 << PERLIN_Z_WRAP_B;

	private static final int PERLIN_SIZE = 4095;

	private double[] _myRandomTable;

	private Random _myPerlinRandom;

	/**
	 * Computes the Perlin noise function value at x, y, z.
	 */
	public double[] signalImpl(double theX, double theY, double theZ) {
		if (_myRandomTable == null) {
			if (_myPerlinRandom == null) {
				_myPerlinRandom = new Random();
			}
			_myRandomTable = new double[PERLIN_SIZE + 1];
			for (int i = 0; i < PERLIN_SIZE + 1; i++) {
				_myRandomTable[i] = _myPerlinRandom.nextFloat();
			}
		}

		theX += _myOffsetX;
		theY += _myOffsetY;
		theZ += _myOffsetZ;

		theX *= _myScale;
		theY *= _myScale;
		theZ *= _myScale;

		if (theX < 0)
			theX = -theX;
		if (theY < 0)
			theY = -theY;
		if (theZ < 0)
			theZ = -theZ;

		int xi = (int) theX;
		int yi = (int) theY;
		int zi = (int) theZ;

		double xf = theX - xi;
		double yf = theY - yi;
		double zf = theZ - zi;
		double rxf, ryf;

		double r = 0;

		double n1, n2, n3;

		int of = xi + (yi << PERLIN_Y_WRAP_B) + (zi << PERLIN_Z_WRAP_B);

		rxf = noise_fsc(xf);
		ryf = noise_fsc(yf);

		n1 = _myRandomTable[of & PERLIN_SIZE];
		n1 += rxf * (_myRandomTable[(of + 1) & PERLIN_SIZE] - n1);
		n2 = _myRandomTable[(of + PERLIN_Y_WRAP) & PERLIN_SIZE];
		n2 += rxf * (_myRandomTable[(of + PERLIN_Y_WRAP + 1) & PERLIN_SIZE] - n2);
		n1 += ryf * (n2 - n1);

		of += PERLIN_Z_WRAP;
		n2 = _myRandomTable[of & PERLIN_SIZE];
		n2 += rxf * (_myRandomTable[(of + 1) & PERLIN_SIZE] - n2);
		n3 = _myRandomTable[(of + PERLIN_Y_WRAP) & PERLIN_SIZE];
		n3 += rxf * (_myRandomTable[(of + PERLIN_Y_WRAP + 1) & PERLIN_SIZE] - n3);
		n2 += ryf * (n3 - n2);

		n1 += noise_fsc(zf) * (n2 - n1);

		r += n1;
		xi <<= 1;
		xf *= 2;
		yi <<= 1;
		yf *= 2;
		zi <<= 1;
		zf *= 2;

		if (xf >= 1.0f) {
			xi++;
			xf--;
		}
		if (yf >= 1.0f) {
			yi++;
			yf--;
		}
		if (zf >= 1.0f) {
			zi++;
			zf--;
		}

		return new double[] {r};
	}

	private double noise_fsc(double i) {
		return 0.5f * (1.0f - CCMath.cos(i * CCMath.PI));
	}

	public void noiseSeed(long what) {
		if (_myPerlinRandom == null)
			_myPerlinRandom = new Random();
		_myPerlinRandom.setSeed(what);
	}
}
