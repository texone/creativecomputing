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
package cc.creativecomputing.math.random;

import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class CCFastRandom {
	// seed this if one wishes
	private int _mySeed;
	
	public CCFastRandom(int theSeed){
		_mySeed = theSeed;
	}
	
	public CCFastRandom(){
		this((int)System.nanoTime());
	}

	/**
	 * Returns a pseudo-random number.
	 */
	private final int randomImp() {
		// this makes a 'nod' to being potentially called from multiple threads
		int seed = _mySeed;

		seed *= 1103515245;
		seed += 12345;
		_mySeed = seed;

		// NOTE: hi bits have better properties
		return seed;
	}

	/**
	 * Returns a random number on [0, range)
	 */
	public final int randomInt(int range) {
		return ((randomImp() >>> 15) * range) >>> 17;
	}

	public final boolean nextBoolean() {
		// hi-bit is the most random
		return randomImp() > 0;
	}

	public final float nextFloat() {
		return (randomImp() >>> 8) * (1.f / (1 << 24));
	}
	
	
	/**
	 * True if the next nextGaussian is available.  This is used by
	 * nextGaussian, which generates two gaussian numbers by one call,
	 * and returns the second on the second call.
	 *
	 * @serial whether nextNextGaussian is available
	 * @see #nextGaussian()
	 * @see #nextNextGaussian
	 */
	private boolean haveNextNextGaussian;
	  
	/**
	 * The next nextGaussian, when available.  This is used by nextGaussian,
	 * which generates two gaussian numbers by one call, and returns the
	 * second on the second call.
	 *
	 * @serial the second gaussian of a pair
	 * @see #nextGaussian()
	 * @see #haveNextNextGaussian
	 */
	private float nextNextGaussian;
	
	/**
	 * Generates the next pseudorandom, Gaussian (normally) distributed
	 * double value, with mean 0.0 and standard deviation 1.0.
	 * <p>This is described in section 3.4.1 of <em>The Art of Computer
	 * Programming, Volume 2</em> by Donald Knuth.
	 *
	 * @return the next pseudorandom Gaussian distributed double
	 */
	public synchronized float nextGaussian(){
		if (haveNextNextGaussian){
			haveNextNextGaussian = false;
			return nextNextGaussian;
		}
		float v1, v2, s;
		do{
			v1 = 2 * nextFloat() - 1; // Between -1.0 and 1.0.
			v2 = 2 * nextFloat() - 1; // Between -1.0 and 1.0.
			s = v1 * v1 + v2 * v2;
		}
		while (s >= 1);
		float norm = (float)CCMath.sqrt(-2 * CCMath.log(s) / s);
		nextNextGaussian = v2 * norm;
		haveNextNextGaussian = true;
		return v1 * norm;
	}
	
	/**
	 * @shortdesc Generates random numbers. 
	 * Generates random numbers. Each time the random() function is called, it returns an 
	 * unexpected value within the specified range. If one parameter is passed to the function 
	 * it will return a float between zero and the value of the high parameter. The function 
	 * call random(5) returns values between 0 and 5. If two parameters are passed, 
	 * it will return a float with a value between the the parameters. The function call 
	 * random(-5, 10.2) returns values between -5 and 10.2. 
	 * @param howsmall
	 * @param howbig
	 * @return random value
	 */
	public float random(){
		return nextFloat();
	}
	
	/**
	 * 
	 * @param theMax
	 */
	public float random(float theMax){
		if (theMax == 0)
			return 0;
		return nextFloat() * theMax;
	}
	
	
	/**
	 * 
	 * @param theMin minimum value for the random to generate
	 * @param theMax maximum value for the random to generate
	 * @return random value
	 */
	public float random(float theMin, float theMax){
		if (theMin >= theMax){
			float tmp = theMin;
			theMin = theMax;
			theMax = tmp;
		}
			
		float diff = theMax - theMin;
		return random(diff) + theMin;
	}
	
	/**
	 * @shortdesc Returns the next pseudorandom, Gaussian ("normally") distributed double value
	 * Returns the next pseudorandom, Gaussian ("normally") distributed double value with mean 
	 * 0.0 and standard deviation 1.0 from this random number generator's sequence. The general 
	 * contract of nextGaussian is that one double value, chosen from (approximately) the usual 
	 * normal distribution with mean 0.0 and standard deviation 1.0, is pseudo randomly generated 
	 * and returned. 
	 * 
	 * This method ensures that the result is mapped between 0 and 1.
	 * @return gaussian random
	 */
	public float gaussianRandom() {
		return (CCMath.constrain((float)nextGaussian() / 4,-1 ,1 ) + 1) / 2;
	}
	
	/**
	 * 
	 * @param theMax
	 * @return
	 */
	public float gaussianRandom(final float theMax) {
		  return gaussianRandom() * theMax;
	}
	
	/**
	 * 
	 * @param theMin minimum value for the random to generate
	 * @param theMax maximum value for the random to generate
	 * @return
	 */
	public float gaussianRandom(final float theMin, final float theMax) {
		  return gaussianRandom() * (theMax - theMin) + theMin;
	}

	/**
	 * Sets the seed of this random number generator using a single long seed. By default, random() produces different results 
	 * each time the program is run. Set the value parameter to a constant to return the 
	 * same pseudo-random numbers each time the software is run.
	 * 
	 *  The general contract of setSeed is that it alters the state of this random number 
	 *  generator object so as to be in exactly the same state as if it had just been created 
	 *  with the argument seed as a seed.The implementation of setSeed by class Random happens 
	 *  to use only 48 bits of the given seed. In general, however, an overriding method may 
	 *  use all 64 bits of the long argument as a seed value. 
	 *  Note: Although the seed value is an AtomicLong, this method must still be 
	 *  synchronized to ensure correct semantics of haveNextNextGaussian.
	 * @param what
	 */
	public void randomSeed(int what){
		_mySeed = what;
		random();
	}
}
