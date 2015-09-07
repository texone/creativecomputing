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

public class CCPerlinNoise extends CCSignal{

	private static double fade(double t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}
	
	private static double dfade(double t) {
		return 30.0f * t * t * (t * (t - 2.0f) + 1.0f );
	}

	private static double grad(int hash, double x, double y) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
		v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private static double grad(int hash, double x, double y, double z) {
		int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
		double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
		v = h < 4 ? y : h == 12 || h == 14 ? x : z;
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	private int _mySeed = 0x214;

	private int[] mPerms = new int[512];

	public CCPerlinNoise() {
		initPermutationTable();
	}

	public CCPerlinNoise(int aSeed) {
		_mySeed = aSeed;
		initPermutationTable();
	}

	private void initPermutationTable() {
		Random rand = new Random(_mySeed);
		for (int t = 0; t < 256; ++t) {
			mPerms[t] = mPerms[t + 256] = rand.nextInt() & 255;
		}
	}

	public void seed(int theSeed) {
		_mySeed = theSeed;
		initPermutationTable();
	}

	// / Calculates a single octave of noise
//	public double noiseImpl(double x) {
//		int X = CCMath.floor(x) & 255;
//
//		x -= CCMath.floor(x);
//
//		double u = fade(x);
//
//		int A = mPerms[X];
//		int AA = mPerms[A];
//		int B = mPerms[X + 1];
//		int BA = mPerms[B];
//
//		return (lerp(u, grad(mPerms[AA], x), grad(mPerms[BA], x - 1)) + 1) / 2;
//	}

	@Override
	public double[] signalImpl( double x, double y ) {
		int X = CCMath.floor(x) & 255;
		int Y = CCMath.floor(y) & 255;
		x -= CCMath.floor(x); 
		y -= CCMath.floor(y);
		
		double u = fade(x);
		double v = fade(y);
		
		double du = dfade(x);
		double dv = dfade(y);
		
		int A = mPerms[X  ]+Y, AA = mPerms[A]+0, AB = mPerms[A+1]+0,
			B = mPerms[X+1]+Y, BA = mPerms[B]+0, BB = mPerms[B+1]+0;

		if( du < 0.000001f ) du = 1.0f;
		if( dv < 0.000001f ) dv = 1.0f;

		double a = grad( mPerms[AA], x  , y);
		double b = grad( mPerms[BA], x-1, y);
		double c = grad( mPerms[AB], x  , y - 1);
		double d = grad( mPerms[BB], x-1, y - 1);

		double k0 = a;
	    double k1 = b - a;
	    double k2 = c - a;
	    double k3 = d - c + b - a;
	    double k4 = a - b - c + d;
 
	    return new double[] {
			(k0 + k1 * u + k2 * v + k3 * v * u) * 0.5f + 0.5f,
			du * ( k1 + k4 * v ) * 0.5f + 0.5f, 
			dv * ( k2 + k4 * u ) * 0.5f + 0.5f
	    };
	}

	@Override
	/**
	 * Implementation that returns the noise and its three derivatives in one go based on inigo quilez
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return
	 */
	public double[] signalImpl(double theX, double theY, double theZ) {
		int X = CCMath.floor(theX) & 255;
		int Y = CCMath.floor(theY) & 255;
		int Z = CCMath.floor(theZ) & 255;
		
		theX -= CCMath.floor(theX); 
		theY -= CCMath.floor(theY); 
		theZ -= CCMath.floor(theZ);
		
		double u = fade( theX );
		double v = fade( theY );
		double w = fade( theZ );
		
		double du = dfade( theX );
		double dv = dfade( theY );
		double dw = dfade( theZ );
		
		int A = mPerms[X] + Y;
		int AA = mPerms[A] + Z;
		int AB = mPerms[A+1] + Z;
		
		int B = mPerms[X+1] + Y;
		int BA = mPerms[B] + Z;
		int BB = mPerms[B+1] + Z;

		if( du < 0.000001f ) du = 1.0f;
		if( dv < 0.000001f ) dv = 1.0f;
		if( dw < 0.000001f ) dw = 1.0f;	

		double a = grad( mPerms[AA  ], theX  , theY  , theZ  );
		double b = grad( mPerms[BA  ], theX-1, theY  , theZ  );
		double c = grad( mPerms[AB  ], theX  , theY-1, theZ  );
		double d = grad( mPerms[BB  ], theX-1, theY-1, theZ  );
		double e = grad( mPerms[AA+1], theX  , theY  , theZ-1);
		double f = grad( mPerms[BA+1], theX-1, theY  , theZ-1);
		double g = grad( mPerms[AB+1], theX  , theY-1, theZ-1);
		double h = grad( mPerms[BB+1], theX-1, theY-1, theZ-1);

		double k0 =  a;
	    double k1 =  b - a;
	    double k2 =  c - a;
	    double k3 =  e - a;
	    double k4 =  a - b - c + d;
	    double k5 =  a - c - e + g;
	    double k6 =  a - b - e + f;
	    double k7 = -a + b + c - d + e - f - g + h;

		return new double[] {
			(k0 + k1 * u + k2 * v + k3 * w + k4 * u * v + k5 * v * w + k6 * w * u + k7 * u * v * w) * 0.5f + 0.5f,
			du * (k1 + k4 * v + k6 * w + k7 * v * w) * 0.5f + 0.5f,
			dv * (k2 + k5 * w + k4 * u + k7 * w * u) * 0.5f + 0.5f,
			dw * (k3 + k6 * u + k5 * v + k7 * u * v) * 0.5f + 0.5f
		};
	}

}
