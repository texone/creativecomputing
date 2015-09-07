/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.control.timeline.util;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 * 
 */
public class CubicSolver {
	// cubic equation solver example using Cardano's method

	private static double THIRD = 0.333333333333333;
	private static double ROOTTHREE = 1.73205080756888;

	// this function returns the cube root if x were a negative number as well
	
	/**
	 * Returns the cube root of the given value, also for negative x values
	 */
	public static double cubeRoot(double theX) {
		if (theX < 0)
			return -CCMath.pow(-theX, THIRD);
		else
			return CCMath.pow(theX, THIRD);
	}

	public static double[] solveCubic(double a, double b, double c, double d) {
		double[] myResult = new double[3];
		
		// find the discriminant
		double f = (3 * c / a - CCMath.pow(b, 2) / CCMath.pow(a, 2)) / 3;
		double g = (2 * CCMath.pow(b, 3) / CCMath.pow(a, 3) - 9 * b * c / CCMath.pow(a, 2) + 27 * d / a) / 27;
		double h = CCMath.pow(g, 2) / 4 + CCMath.pow(f, 3) / 27;

		// evaluate discriminant
		if (f == 0 && g == 0 && h == 0) {
			// 3 equal roots
			// when f, g, and h all equal 0 the roots can be found by the following line
			double x = -cubeRoot(d / a);
			myResult[0] = x;
			myResult[1] = x;
			myResult[2] = x;
			return myResult;
		} 
		
		if (h <= 0) {
			// 3 real roots
			// complicated math making use of the method
			double i = CCMath.pow(CCMath.pow(g, 2) / 4 - h, 0.5);
			double j = cubeRoot(i);
			double k = CCMath.acos(-(g / (2 * i)));
			double m = CCMath.cos(k / 3);
			double n = ROOTTHREE * CCMath.sin(k / 3);
			double p = -(b / (3 * a));
			
			myResult[0] = 2 * j * m + p;
			myResult[1] = -j * (m + n) + p;
			myResult[2] = -j * (m - n) + p;
			return myResult;
		}
		
		if (h > 0) {
			// 1 real root and 2 complex roots
			double r, s, t, u, p;
			// complicated maths making use of the method
			r = -(g / 2) + CCMath.pow(h, 0.5);
			s = cubeRoot(r);
			t = -(g / 2) - CCMath.pow(h, 0.5);
			u = cubeRoot(t);
			p = -(b / (3 * a));
			// print solutions
//			("x = ");
//			(" " + (s + u + p));
//			(" " + (-(s + u) / 2 + p) + " +" + (s - u) * ROOTTHREE / 2 + "i");
//			(" " + (-(s + u) / 2 + p) + " " + -(s - u) * ROOTTHREE / 2 + "i");
			
			myResult[0] = s + u + p;
			myResult[1] = -(s + u) / 2 + p;
			myResult[2] = -(s + u) / 2 + p;
		}
		
		return myResult;
	}

	public static void main(String[] args) {
		// introduction

		double[] myResult = solveCubic(45, 24, -7, -2);
		// print solutions
		CCLog.info("x = ");
		CCLog.info(" " + myResult[0]);
		CCLog.info(" " + myResult[1]);
		CCLog.info(" " + myResult[2]);
	}

}
