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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * The point x, y, z is the center of a normal probability density of standard 
 * deviation stdev. The density is radially symmetrical. The blob domain allows 
 * for some very natural-looking effects because there is no sharp, artificial-looking 
 * boundary at the edge of the domain.
 * Generate returns a point with normal probability density. 
 * Within has a probability of returning true equal to the probability density at the specified point.
 * @author christianr
 *
 */
public class CCBlob extends CCDomain{

	public CCVector3 center;
	double stdev, scale1, scale2;

	public CCBlob(final CCVector3 i_center, final double i_stdev){
		center = i_center;
		stdev = i_stdev;
		
		double oneOverSigma = 1.0f/(stdev+0.000000000001f);
		
		scale1 = -0.5f*CCMath.sq(oneOverSigma);
		scale2 = ONEOVERSQRT2PI * oneOverSigma;
	}

	/**
	 *  IsWithin has a probability of returning true equal to the probability density at the specified point.
	 */
	public boolean isWithin(final CCVector3 i_vector){
		CCVector3 x = i_vector.clone();
		x.subtractLocal(center);

		double gX = Math.exp(x.lengthSquared() * scale1) * scale2;
		return (CCMath.random() < gX);
	}
	
	static private double ONE_OVER_SIGMA_EXP = 1.0f / 0.7975f;
	
	private double randf(final double i_sigma){
		if(i_sigma == 0) return 0;

	    double y;
	    do {
	        y = -Math.log(CCMath.random());
	    }while(CCMath.random() > Math.exp(-CCMath.sq(y - 1.0f)*0.5f));

	    if(CCMath.random() > 0.5f)
	        return y * i_sigma * ONE_OVER_SIGMA_EXP;
	    else
	        return -y * i_sigma * ONE_OVER_SIGMA_EXP;
	}

	/**
	 * Generate returns a point with normal probability density.
	 */
	public CCVector3 generate(){
		final CCVector3 result = new CCVector3(randf(stdev),randf(stdev),randf(stdev));
		result.addLocal(center);
		return result;
	}
}
