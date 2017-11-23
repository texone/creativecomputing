package cc.creativecomputing.effects;

import cc.creativecomputing.math.CCMath;

public class CCEffectBlendCombiner implements CCEffectCombiner{

	@Override
	public double[][] combine(double[] theBlends, double[][] theValuesA, double[][] theValuesB) {
		double[][] myResult = new double[theValuesA.length][theBlends.length];
		for(int i = 0; i < theBlends.length;i++) {
			myResult[i] = CCMath.blend(theValuesA[i], theValuesB[i], theBlends[i]);
		}
		return myResult;
	}

}
