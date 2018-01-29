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
package cc.creativecomputing.graphics.shader.util;


public class CCStatistics {
	float sum   = 0;
	float mean  = 0;
	float max   = 0;
	float min   = 0;
	float sigma = 0;
	
	public void sum (float s) {
		sum = s;
	}
	public float sum() {
		return sum;
	}
	public void mean (float m) {
		mean = m;
	}
	public float mean() {
		return mean;
	}
	public void max (float m) {
		max = m;
	}
	public float max() {
		return max;
	}
}
